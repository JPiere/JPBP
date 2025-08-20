/******************************************************************************
 * Product: JPiere                                                            *
 * Copyright (C) Hideaki Hagiwara (h.hagiwara@oss-erp.co.jp)                  *
 *                                                                            *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY.                          *
 * See the GNU General Public License for more details.                       *
 *                                                                            *
 * JPiere is maintained by OSS ERP Solutions Co., Ltd.                        *
 * (http://www.oss-erp.co.jp)                                                 *
 *****************************************************************************/


package jpiere.base.plugin.org.compiere.acct;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.compiere.acct.DocLine;
import org.compiere.acct.DocLine_InOut;
import org.compiere.acct.Doc_InOut;
import org.compiere.acct.Doc_Order;
import org.compiere.acct.Fact;
import org.compiere.acct.FactLine;
import org.compiere.model.MAccount;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MCostDetail;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInOutLineMA;
import org.compiere.model.MProduct;
import org.compiere.model.MRMA;
import org.compiere.model.MSysConfig;
import org.compiere.model.ProductCost;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MContractAcct;
import jpiere.base.plugin.org.adempiere.model.MContractChargeAcct;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractProductAcct;
import jpiere.base.plugin.org.adempiere.model.MRecognitionLine;

/**
*  JPIERE-0363: Contract Management
*
* @author Hideaki Hagiwara
*
*/
public class Doc_InOutJP extends Doc_InOut {

	public Doc_InOutJP(MAcctSchema as, ResultSet rs, String trxName)
	{
		super(as, rs, trxName);
	}

	private int				m_Reversal_ID = 0;
	@SuppressWarnings("unused")
	private String			m_DocStatus = "";
//	private boolean 			m_deferPosting = false; //JPIERE comment out

	/**
	 *  Load Document Details
	 *  @return error message or null
	 */
	@Override
	protected String loadDocumentDetails()
	{
		MInOut inout = (MInOut)getPO();
		m_Reversal_ID = inout.getReversal_ID();//store original (voided/reversed) document
		return super.loadDocumentDetails();
	}

	ArrayList<Fact> facts = new ArrayList<Fact>();

	@Override
	public ArrayList<Fact> createFacts(MAcctSchema as)
	{
		if (!as.isAccrual())
			return super.createFacts(as);

		MInOut inOut = (MInOut)getPO();

		/**iDempiere Standard Posting*/
		int JP_ContractContent_ID = inOut.get_ValueAsInt("JP_ContractContent_ID");
		if(JP_ContractContent_ID == 0)
		{
			return super.createFacts(as);
		}

		MContractContent contractContent = MContractContent.get(getCtx(), JP_ContractContent_ID);
		if(contractContent.getJP_Contract_Acct_ID() == 0)
		{
			return super.createFacts(as);
		}

		MContractAcct contractAcct = MContractAcct.get(Env.getCtx(),contractContent.getJP_Contract_Acct_ID());
		if(!contractAcct.isPostingContractAcctJP())
		{
			return super.createFacts(as);
		}

		/**JPiere Posting Logic*/
//		ArrayList<Fact> facts = new ArrayList<Fact>();
		//  create Fact Header
		Fact fact = new Fact(this, as, Fact.POST_Actual);

		if(!contractAcct.isPostingRecognitionDocJP())
		{
			if (getDocumentType().equals(DOCTYPE_MatShipment) && isSOTrx()) //Sales - Shipment
			{
				postSalesShipment(as, contractAcct, fact);

			}else if ( getDocumentType().equals(DOCTYPE_MatReceipt) && isSOTrx() ){//Sales - Return

				postSalesReturn(as, contractAcct, fact);

			}else if (getDocumentType().equals(DOCTYPE_MatReceipt) && !isSOTrx()){//Purchasing - Receipt

				return super.createFacts(as);

			}else if (getDocumentType().equals(DOCTYPE_MatShipment) && !isSOTrx()){ //Purchasing - return

				return super.createFacts(as);

			}else{
				p_Error = "DocumentType unknown: " + getDocumentType();
				log.log(Level.SEVERE, p_Error);
				return null;
			}

			FactLine[]  factLine = fact.getLines();
			for(int i = 0; i < factLine.length; i++)
			{
				if(inOut.getC_Order_ID() > 0)
				{
					factLine[i].set_ValueNoCheck("JP_Order_ID", inOut.getC_Order_ID());
				}else if(inOut.getM_RMA_ID() > 0){
					int M_RMA_ID = inOut.getM_RMA_ID();
					MRMA rma = new MRMA (Env.getCtx(),M_RMA_ID,null);
					int JP_Order_ID = rma.get_ValueAsInt("JP_Order_ID");
					if(JP_Order_ID > 0)
						factLine[i].set_ValueNoCheck("JP_Order_ID", JP_Order_ID);
				}

				factLine[i].set_ValueNoCheck("JP_ContractContent_ID", JP_ContractContent_ID);
			}//for

		}else if(contractAcct.isPostingRecognitionDocJP()){

			if (getDocumentType().equals(DOCTYPE_MatShipment) && isSOTrx()) //Sales - Shipment
			{
				if(MSysConfig.getBooleanValue("JP_CREATE_COST_DETAIL", true, as.getAD_Client_ID()))
				{
					//Delete Journal. Because Remain cost detail only.
					//Journal will be created by Doc_JPRecongnition. 
					costDetailForJPR(as, contractAcct, fact);
					if(fact != null)
					{
						FactLine[] fLines = fact.getLines();
						for(FactLine fl : fLines)
							fact.remove(fl);
					}
				}
				
			}else if ( getDocumentType().equals(DOCTYPE_MatReceipt) && isSOTrx() ){//Sales - Return

				if(MSysConfig.getBooleanValue("JP_CREATE_COST_DETAIL", true, as.getAD_Client_ID()))
				{
					//Delete Journal. Because Remain cost detail only.
					//Journal will be created by Doc_JPRecongnition. 
					costDetailForJPS(as, contractAcct, fact);
					if(fact != null)
					{
						FactLine[] fLines = fact.getLines();
						for(FactLine fl : fLines)
							fact.remove(fl);
					}
				}
				
			}else if (getDocumentType().equals(DOCTYPE_MatReceipt) && !isSOTrx()){//Purchasing - Receipt

				return super.createFacts(as);

			}else if (getDocumentType().equals(DOCTYPE_MatShipment) && !isSOTrx()){ //Purchasing - return

				return super.createFacts(as);

			}else{
				p_Error = "DocumentType unknown: " + getDocumentType();
				log.log(Level.SEVERE, p_Error);
				return null;
			}

		}else{

			//It is impossible to reach this code. I know. But I write it daringly.
			p_Error = "DocumentType unknown: " + getDocumentType();
			log.log(Level.SEVERE, p_Error);
			return null;
		}

		facts.add(fact);
		return facts;
	}

	private String postSalesShipment(MAcctSchema as, MContractAcct contractAcct, Fact fact)
	{
		//  Line pointers
		FactLine dr = null;
		FactLine cr = null;

		for (int i = 0; i < p_lines.length; i++)
		{
			Map<String, BigDecimal> batchLotCostMap = null;
			DocLine_InOut line = (DocLine_InOut) p_lines[i];
			MProduct product = line.getProduct();
			BigDecimal costs = null;
			if (!isReversal(line))
			{
				if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(product.getCostingLevel(as)) )
				{
					if (line.getM_AttributeSetInstance_ID() == 0 )
					{
						MInOutLine ioLine = (MInOutLine) line.getPO();
						MInOutLineMA mas[] = MInOutLineMA.get(getCtx(), ioLine.get_ID(), getTrxName());
						if (mas != null && mas.length > 0 )
						{
							batchLotCostMap = new HashMap<>();
							costs  = BigDecimal.ZERO;
							for (int j = 0; j < mas.length; j++)
							{
								MInOutLineMA ma = mas[j];
								BigDecimal QtyMA = ma.getMovementQty();
								ProductCost pc = line.getProductCost();
								pc.setQty(QtyMA);
								pc.setM_M_AttributeSetInstance_ID(ma.getM_AttributeSetInstance_ID());
								BigDecimal maCosts = line.getProductCosts(as, line.getAD_Org_ID(), true, "M_InOutLine_ID=?");
								batchLotCostMap.put(ma.getM_InOutLineMA_UU(), maCosts);
								costs = costs.add(maCosts);
							}
						}
					}
					else
					{
						costs = line.getProductCosts(as, line.getAD_Org_ID(), true, "M_InOutLine_ID=?");
					}
				}
				else
				{
					// MZ Goodwill
					// if Shipment CostDetail exist then get Cost from Cost Detail
					costs = line.getProductCosts(as, line.getAD_Org_ID(), true, "M_InOutLine_ID=?");
				}

				// end MZ
				if (costs == null || costs.signum() == 0)	//	zero costs OK
				{
					if (product.isStocked())
					{
						//ok if we have purchased zero cost item from vendor before
						int count = DB.getSQLValue(null, "SELECT Count(*) FROM M_CostDetail WHERE M_Product_ID=? AND Processed='Y' AND Amt=0.00 AND Qty > 0 AND (C_OrderLine_ID > 0 OR C_InvoiceLine_ID > 0)",
								product.getM_Product_ID());
						if (count > 0)
						{
							costs = BigDecimal.ZERO;
						}
						else
						{
							p_Error = Msg.getMsg(getCtx(), "No Costs for") + " " + line.getProduct().getName();
							log.log(Level.WARNING, p_Error);
							return null;
						}
					}
					else	//	ignore service
						continue;
				}
			}
			else
			{
				//temp to avoid NPE
				costs = BigDecimal.ZERO;
			}

			//  CoGS            DR
			dr = fact.createLine(line,
				getCOGSAccount(line, contractAcct, as),//JPIERE-0363
				as.getC_Currency_ID(), costs, null);
			if (dr == null)
			{
				p_Error = Msg.getMsg(getCtx(),"FactLine DR not created:" + " ") + line;
				log.log(Level.WARNING, p_Error);
				return null;
			}
			dr.setM_Locator_ID(line.getM_Locator_ID());
			dr.setLocationFromLocator(line.getM_Locator_ID(), true);    //  from Loc
			dr.setLocationFromBPartner(getC_BPartner_Location_ID(), false);  //  to Loc
			dr.setAD_Org_ID(line.getOrder_Org_ID());		//	Revenue X-Org
			dr.setQty(line.getQty().negate());

			if (isReversal(line))
			{
				//	Set AmtAcctDr from Original Shipment/Receipt
				if (!dr.updateReverseLine (MInOut.Table_ID,
						m_Reversal_ID, line.getReversalLine_ID(),Env.ONE))
				{
					if (! product.isStocked())	{ //	ignore service
						fact.remove(dr);
						continue;
					}
					p_Error = Msg.getMsg(getCtx(),"Original Shipment/Receipt not posted yet");
					return null;
				}
			}

			//  Inventory               CR
			cr = fact.createLine(line,
				line.getAccount(ProductCost.ACCTTYPE_P_Asset, as),
				as.getC_Currency_ID(), null, costs);
			if (cr == null)
			{
				p_Error = Msg.getMsg(getCtx(),"FactLine CR not created:") + " " + line;
				log.log(Level.WARNING, p_Error);
				return null;
			}
			cr.setM_Locator_ID(line.getM_Locator_ID());
			cr.setLocationFromLocator(line.getM_Locator_ID(), true);    // from Loc
			cr.setLocationFromBPartner(getC_BPartner_Location_ID(), false);  // to Loc

			if (isReversal(line))
			{
				//	Set AmtAcctCr from Original Shipment/Receipt
				if (!cr.updateReverseLine (MInOut.Table_ID,
						m_Reversal_ID, line.getReversalLine_ID(),Env.ONE))
				{
					p_Error = Msg.getMsg(getCtx(),"Original Shipment/Receipt not posted yet");
					return null;
				}
				costs = cr.getAcctBalance(); //get original cost
			}
			if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(product.getCostingLevel(as)) )
			{
				if (line.getM_AttributeSetInstance_ID() == 0 )
				{
					MInOutLine ioLine = (MInOutLine) line.getPO();
					MInOutLineMA mas[] = MInOutLineMA.get(getCtx(), ioLine.get_ID(), getTrxName());
					if (mas != null && mas.length > 0 )
					{
						for (int j = 0; j < mas.length; j++)
						{
							MInOutLineMA ma = mas[j];
							BigDecimal qty = ma.getMovementQty();
							if (qty.signum() != line.getQty().signum())
								qty = qty.negate();
							BigDecimal amt = batchLotCostMap != null ? batchLotCostMap.get(ma.getM_InOutLineMA_UU()) : null;
							if (amt == null && isReversal(line))
							{
								amt = findReversalCostDetailAmt(as, line.getM_Product_ID(), ma, line.getReversalLine_ID());
								if (amt != null)
									amt = amt.negate();
							}
							if (amt == null) 
							{
								amt = costs.divide(line.getProductCost().getQty(), RoundingMode.HALF_UP);
								amt = amt.multiply(qty);
							}
							else if (!isReversal(line) && line.getProductCost().getQty().signum() != line.getQty().signum())
							{
								if (amt.signum() != (costs.signum() * -1)) {
									amt = amt.negate();
								}
							}
							int Ref_CostDetail_ID = 0;
							if (line.getReversalLine_ID() > 0 && line.get_ID() > line.getReversalLine_ID())
							{
								MCostDetail cd = MCostDetail.getShipment(as, line.getM_Product_ID(), ma.getM_AttributeSetInstance_ID(),
										line.getReversalLine_ID(), 0, getTrxName());
								if (cd != null)
									Ref_CostDetail_ID = cd.getM_CostDetail_ID();
							}
							if (!MCostDetail.createShipment(as, line.getAD_Org_ID(),
									line.getM_Product_ID(), ma.getM_AttributeSetInstance_ID(),
									line.get_ID(), 0,
									amt, qty,
									line.getDescription(), true, line.getDateAcct(), Ref_CostDetail_ID, getTrxName()))
							{
								p_Error = Msg.getMsg(getCtx(),"Failed to create cost detail record");
								return null;
							}							
						}						
					}
				}
				else
				{
					//
					if (line.getM_Product_ID() != 0)
					{
						BigDecimal amt = costs;
						if (line.getProductCost().getQty().signum() != line.getQty().signum() && !isReversal(line))
							amt = amt.negate();
						int Ref_CostDetail_ID = 0;
						if (line.getReversalLine_ID() > 0 && line.get_ID() > line.getReversalLine_ID())
						{
							MCostDetail cd = MCostDetail.getShipment(as, line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
									line.getReversalLine_ID(), 0, getTrxName());
							if (cd != null)
								Ref_CostDetail_ID = cd.getM_CostDetail_ID();
						}
						if (!MCostDetail.createShipment(as, line.getAD_Org_ID(),
							line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
							line.get_ID(), 0,
							amt, line.getQty(),
							line.getDescription(), true, line.getDateAcct(), Ref_CostDetail_ID, getTrxName()))
						{
							p_Error = Msg.getMsg(getCtx(),"Failed to create cost detail record");
							return null;
						}
					}
				}
			}
			else
			{
				//
				if (line.getM_Product_ID() != 0)
				{
					BigDecimal amt = costs;
					if (line.getProductCost().getQty().signum() != line.getQty().signum() && !isReversal(line))
						amt = amt.negate();
					int Ref_CostDetail_ID = 0;
					if (line.getReversalLine_ID() > 0 && line.get_ID() > line.getReversalLine_ID())
					{
						MCostDetail cd = MCostDetail.getShipment(as, line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
								line.getReversalLine_ID(), 0, getTrxName());
						if (cd != null)
							Ref_CostDetail_ID = cd.getM_CostDetail_ID();
					}
					if (!MCostDetail.createShipment(as, line.getAD_Org_ID(),
						line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
						line.get_ID(), 0,
						amt, line.getQty(),
						line.getDescription(), true, line.getDateAcct(), Ref_CostDetail_ID, getTrxName()))
					{
						p_Error = Msg.getMsg(getCtx(),"Failed to create cost detail record");
						return null;
					}
				}
			}
		}	//	for all lines

		/** Commitment release										****/
		//JPIERE-0363 Comment Out because Doc_Order.getCommitmentRelease() method is protected method
//		if (as.isAccrual() && as.isCreateSOCommitment())
//		{
//			for (int i = 0; i < p_lines.length; i++)
//			{
//				DocLine line = p_lines[i];
//				Fact factcomm = Doc_Order.getCommitmentSalesRelease(as, this,
//					line.getQty(), line.get_ID(), Env.ONE);
//				if (factcomm != null)
//					facts.add(factcomm);
//			}
//		}	//	Commitment

		return null;
	}

	private String postSalesReturn(MAcctSchema as, MContractAcct contractAcct, Fact fact)
	{
		//  Line pointers
		FactLine dr = null;
		FactLine cr = null;

		for (int i = 0; i < p_lines.length; i++)
		{
			DocLine_InOut line = (DocLine_InOut) p_lines[i];
			MProduct product = line.getProduct();
			BigDecimal costs = null;
			Map<String, BigDecimal> batchLotCostMap = null;
			if (!isReversal(line))
			{
				if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(product.getCostingLevel(as)) )
				{
					if (line.getM_AttributeSetInstance_ID() == 0 )
					{
						MInOutLine ioLine = (MInOutLine) line.getPO();
						MInOutLineMA mas[] = MInOutLineMA.get(getCtx(), ioLine.get_ID(), getTrxName());
						costs = BigDecimal.ZERO;
						if (mas != null && mas.length > 0 )
						{
							batchLotCostMap = new HashMap<>();
							for (int j = 0; j < mas.length; j++)
							{
								MInOutLineMA ma = mas[j];
								BigDecimal QtyMA = ma.getMovementQty();
								ProductCost pc = line.getProductCost();
								pc.setQty(QtyMA);
								pc.setM_M_AttributeSetInstance_ID(ma.getM_AttributeSetInstance_ID());
								BigDecimal maCosts = line.getProductCosts(as, line.getAD_Org_ID(), true, "M_InOutLine_ID=?");
								batchLotCostMap.put(ma.getM_InOutLineMA_UU(), maCosts);
								costs = costs.add(maCosts);
							}
						}
					}
					else
					{
						costs = line.getProductCosts(as, line.getAD_Org_ID(), true, "M_InOutLine_ID=?");
					}
				}
				else
				{
					// MZ Goodwill
					// if Shipment CostDetail exist then get Cost from Cost Detail
					costs = line.getProductCosts(as, line.getAD_Org_ID(), true, "M_InOutLine_ID=?");
					// end MZ
				}
				if (costs == null || costs.signum() == 0)	//	zero costs OK
				{
					if (product.isStocked())
					{
						p_Error = Msg.getMsg(getCtx(),"No Costs for") + " " + line.getProduct().getName();
						log.log(Level.WARNING, p_Error);
						return null;
					}
					else	//	ignore service
						continue;
				}
			}
			else
			{
				costs = BigDecimal.ZERO;
			}
			//  Inventory               DR
			dr = fact.createLine(line,
				line.getAccount(ProductCost.ACCTTYPE_P_Asset, as),
				as.getC_Currency_ID(), costs, null);
			if (dr == null)
			{
				p_Error = Msg.getMsg(getCtx(),"FactLine DR not created:" + " ") + line;
				log.log(Level.WARNING, p_Error);
				return null;
			}
			dr.setM_Locator_ID(line.getM_Locator_ID());
			dr.setLocationFromLocator(line.getM_Locator_ID(), true);    // from Loc
			dr.setLocationFromBPartner(getC_BPartner_Location_ID(), false);  // to Loc
			if (isReversal(line))
			{
				//	Set AmtAcctDr from Original Shipment/Receipt
				if (!dr.updateReverseLine (MInOut.Table_ID,
						m_Reversal_ID, line.getReversalLine_ID(),Env.ONE))
				{
					if (! product.isStocked())	{ //	ignore service
						fact.remove(dr);
						continue;
					}
					p_Error = Msg.getMsg(getCtx(),"Original Shipment/Receipt not posted yet");
					return null;
				}
				costs = dr.getAcctBalance(); //get original cost
			}
			//
			if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(product.getCostingLevel(as)) )
			{
				if (line.getM_AttributeSetInstance_ID() == 0 ) 
				{
					MInOutLine ioLine = (MInOutLine) line.getPO();
					MInOutLineMA mas[] = MInOutLineMA.get(getCtx(), ioLine.get_ID(), getTrxName());
					if (mas != null && mas.length > 0 )
					{
						for (int j = 0; j < mas.length; j++)
						{
							MInOutLineMA ma = mas[j];
							BigDecimal qty = ma.getMovementQty();
							if (qty.signum() != line.getQty().signum())
								qty = qty.negate();
							BigDecimal amt = batchLotCostMap != null ? batchLotCostMap.get(ma.getM_InOutLineMA_UU()) : null;
							if (amt == null && isReversal(line))
							{
								amt = findReversalCostDetailAmt(as, line.getM_Product_ID(), ma, line.getReversalLine_ID());
								if (amt != null)
									amt = amt.negate();
							}
							if (amt == null) 
							{
								amt = costs.divide(line.getProductCost().getQty(), RoundingMode.HALF_UP);
								amt = amt.multiply(qty);
							}
							else if (!isReversal(line) && line.getProductCost().getQty().signum() != line.getQty().signum())
							{
								if (amt.signum() != (costs.signum() * -1)) {
									amt = amt.negate();
								}
							}
							int Ref_CostDetail_ID = 0;
							if (line.getReversalLine_ID() > 0 && line.get_ID() > line.getReversalLine_ID())
							{
								MCostDetail cd = MCostDetail.getShipment(as, line.getM_Product_ID(), ma.getM_AttributeSetInstance_ID(),
										line.getReversalLine_ID(), 0, getTrxName());
								if (cd != null)
									Ref_CostDetail_ID = cd.getM_CostDetail_ID();
							}
							if (!MCostDetail.createShipment(as, line.getAD_Org_ID(),
									line.getM_Product_ID(), ma.getM_AttributeSetInstance_ID(),
									line.get_ID(), 0,
									amt, qty,
									line.getDescription(), true, line.getDateAcct(), Ref_CostDetail_ID, getTrxName()))
							{
								p_Error = Msg.getMsg(getCtx(),"Failed to create cost detail record");
								return null;
							}
						}
					}
				} else
				{
					if (line.getM_Product_ID() != 0)
					{
						BigDecimal amt = costs;
						if (line.getProductCost().getQty().signum() != line.getQty().signum() && !isReversal(line))
							amt = amt.negate();
						int Ref_CostDetail_ID = 0;
						if (line.getReversalLine_ID() > 0 && line.get_ID() > line.getReversalLine_ID())
						{
							MCostDetail cd = MCostDetail.getShipment(as, line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
									line.getReversalLine_ID(), 0, getTrxName());
							if (cd != null)
								Ref_CostDetail_ID = cd.getM_CostDetail_ID();
						}
						if (!MCostDetail.createShipment(as, line.getAD_Org_ID(),
							line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
							line.get_ID(), 0,
							amt, line.getQty(),
							line.getDescription(), true, line.getDateAcct(), Ref_CostDetail_ID, getTrxName()))
						{
							p_Error = Msg.getMsg(getCtx(),"Failed to create cost detail record");
							return null;
						}
					}
				}
			} else
			{
				//
				if (line.getM_Product_ID() != 0)
				{
					BigDecimal amt = costs;
					if (line.getProductCost().getQty().signum() != line.getQty().signum() && !isReversal(line))
						amt = amt.negate();
					int Ref_CostDetail_ID = 0;
					if (line.getReversalLine_ID() > 0 && line.get_ID() > line.getReversalLine_ID())
					{
						MCostDetail cd = MCostDetail.getShipment(as, line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
								line.getReversalLine_ID(), 0, getTrxName());
						if (cd != null)
							Ref_CostDetail_ID = cd.getM_CostDetail_ID();
					}
					if (!MCostDetail.createShipment(as, line.getAD_Org_ID(),
						line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
						line.get_ID(), 0,
						amt, line.getQty(),
						line.getDescription(), true, line.getDateAcct(), Ref_CostDetail_ID, getTrxName()))
					{
						p_Error = Msg.getMsg(getCtx(),"Failed to create cost detail record");
						return null;
					}
				}
			}

			//  CoGS            CR
			cr = fact.createLine(line,
				getCOGSAccount(line, contractAcct, as),//JPIERE-0363
				as.getC_Currency_ID(), null, costs);
			if (cr == null)
			{
				p_Error = Msg.getMsg(getCtx(),"FactLine CR not created:") + " " + line;
				log.log(Level.WARNING, p_Error);
				return null;
			}
			cr.setM_Locator_ID(line.getM_Locator_ID());
			cr.setLocationFromLocator(line.getM_Locator_ID(), true);    //  from Loc
			cr.setLocationFromBPartner(getC_BPartner_Location_ID(), false);  //  to Loc
			cr.setAD_Org_ID(line.getOrder_Org_ID());		//	Revenue X-Org
			cr.setQty(line.getQty().negate());
			if (isReversal(line))
			{
				//	Set AmtAcctCr from Original Shipment/Receipt
				if (!cr.updateReverseLine (MInOut.Table_ID,
						m_Reversal_ID, line.getReversalLine_ID(),Env.ONE))
				{
					p_Error = Msg.getMsg(getCtx(),"Original Shipment/Receipt not posted yet");
					return null;
				}
			}
		}	//	for all lines

		return null;
	}
	
	private Fact costDetailForJPR(MAcctSchema as, MContractAcct contractAcct, Fact fact)
	{		
		for (int i = 0; i < p_lines.length; i++)
		{
			Map<String, BigDecimal> batchLotCostMap = null;
			DocLine_InOut line = (DocLine_InOut) p_lines[i];				
			MProduct product = line.getProduct();
			BigDecimal costs = null;
			if (!isReversal(line))
			{
				if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(product.getCostingLevel(as)) ) 
				{	
					if (line.getM_AttributeSetInstance_ID() == 0 ) 
					{
						MInOutLine ioLine = (MInOutLine) line.getPO();
						MInOutLineMA mas[] = MInOutLineMA.get(getCtx(), ioLine.get_ID(), getTrxName());
						if (mas != null && mas.length > 0 )
						{
							batchLotCostMap = new HashMap<>();
							costs  = BigDecimal.ZERO;
							for (int j = 0; j < mas.length; j++)
							{
								MInOutLineMA ma = mas[j];
								BigDecimal QtyMA = ma.getMovementQty();
								ProductCost pc = line.getProductCost();
								pc.setQty(QtyMA);
								pc.setM_M_AttributeSetInstance_ID(ma.getM_AttributeSetInstance_ID());
								BigDecimal maCosts = line.getProductCosts(as, line.getAD_Org_ID(), true, "M_InOutLine_ID=?");
								batchLotCostMap.put(ma.getM_InOutLineMA_UU(), maCosts);
								costs = costs.add(maCosts);
							}						
						}
					} 
					else 
					{							
						costs = line.getProductCosts(as, line.getAD_Org_ID(), true, "M_InOutLine_ID=?");				
					}
				} 
				else
				{
					// MZ Goodwill
					// if Shipment CostDetail exist then get Cost from Cost Detail
					costs = line.getProductCosts(as, line.getAD_Org_ID(), true, "M_InOutLine_ID=?");			
				}
		
				// end MZ
				if (costs == null || costs.signum() == 0)	//	zero costs OK
				{
					if (product.isStocked())
					{
						//ok if we have purchased zero cost item from vendor before
						int count = DB.getSQLValue(null, "SELECT Count(*) FROM M_CostDetail WHERE M_Product_ID=? AND Processed='Y' AND Amt=0.00 AND Qty > 0 AND (C_OrderLine_ID > 0 OR C_InvoiceLine_ID > 0)", 
								product.getM_Product_ID());
						if (count > 0)
						{
							costs = BigDecimal.ZERO;
						}
						else
						{
							p_Error = Msg.getMsg(getCtx(), "No Costs for") + " " + line.getProduct().getName();
							log.log(Level.WARNING, p_Error);
							return null;
						}
					}
					else	//	ignore service
						continue;
				}
			}
			else//Reversal
			{
				if(line.getReversalLine_ID() > 0)
				{
					MCostDetail cd = MCostDetail.getShipment(as, line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
							line.getReversalLine_ID(), 0, getTrxName());
					costs = cd.getAmt().negate();
				}else {
					p_Error = "Could not find original Cost Detail for reversal. ";
					log.log(Level.SEVERE, p_Error);
					return null;
				}
			}
			
			if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(product.getCostingLevel(as)) ) 
			{	
				if (line.getM_AttributeSetInstance_ID() == 0 ) 
				{
					MInOutLine ioLine = (MInOutLine) line.getPO();
					MInOutLineMA mas[] = MInOutLineMA.get(getCtx(), ioLine.get_ID(), getTrxName());
					if (mas != null && mas.length > 0 )
					{
						for (int j = 0; j < mas.length; j++)
						{
							MInOutLineMA ma = mas[j];
							BigDecimal qty = ma.getMovementQty();
							if (qty.signum() != line.getQty().signum())
								qty = qty.negate();
							BigDecimal amt = batchLotCostMap != null ? batchLotCostMap.get(ma.getM_InOutLineMA_UU()) : null;
							if (amt == null && isReversal(line))
							{
								amt = findReversalCostDetailAmt(as, line.getM_Product_ID(), ma, line.getReversalLine_ID());
								if (amt != null)
									amt = amt.negate();
							}
							if (amt == null) 
							{
								amt = costs.divide(line.getProductCost().getQty(), RoundingMode.HALF_UP);
								amt = amt.multiply(qty);
							}
							else if (!isReversal(line) && line.getProductCost().getQty().signum() != line.getQty().signum())
							{
								if (amt.signum() != (costs.signum() * -1)) {
									amt = amt.negate();
								}
							}
							int Ref_CostDetail_ID = 0;
							if (line.getReversalLine_ID() > 0 && line.get_ID() > line.getReversalLine_ID())
							{
								MCostDetail cd = MCostDetail.getShipment(as, line.getM_Product_ID(), ma.getM_AttributeSetInstance_ID(),
										line.getReversalLine_ID(), 0, getTrxName());
								if (cd != null)
									Ref_CostDetail_ID = cd.getM_CostDetail_ID();
							}
							if (!MCostDetail.createShipment(as, line.getAD_Org_ID(),
									line.getM_Product_ID(), ma.getM_AttributeSetInstance_ID(),
									line.get_ID(), 0,
									amt, qty,
									line.getDescription(), true, line.getDateAcct(), Ref_CostDetail_ID, getTrxName()))
							{
								p_Error = Msg.getMsg(getCtx(),"Failed to create cost detail record");
								return null;
							}							
						}						
					}
				} 
				else
				{
					//
					if (line.getM_Product_ID() != 0)
					{
						BigDecimal amt = costs;
						if (line.getProductCost().getQty().signum() != line.getQty().signum() && !isReversal(line))
							amt = amt.negate();
						int Ref_CostDetail_ID = 0;
						if (line.getReversalLine_ID() > 0 && line.get_ID() > line.getReversalLine_ID())
						{
							MCostDetail cd = MCostDetail.getShipment(as, line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
									line.getReversalLine_ID(), 0, getTrxName());
							if (cd != null)
								Ref_CostDetail_ID = cd.getM_CostDetail_ID();
						}
						if (!MCostDetail.createShipment(as, line.getAD_Org_ID(),
							line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
							line.get_ID(), 0,
							amt, line.getQty(),
							line.getDescription(), true, line.getDateAcct(), Ref_CostDetail_ID, getTrxName()))
						{
							p_Error = Msg.getMsg(getCtx(),"Failed to create cost detail record");
							return null;
						}
					}
				}
			} 
			else
			{
				//
				if (line.getM_Product_ID() != 0)
				{
					BigDecimal amt = costs;
					if (line.getProductCost().getQty().signum() != line.getQty().signum() && !isReversal(line))
						amt = amt.negate();
					int Ref_CostDetail_ID = 0;
					if (line.getReversalLine_ID() > 0 && line.get_ID() > line.getReversalLine_ID())
					{
						MCostDetail cd = MCostDetail.getShipment(as, line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
								line.getReversalLine_ID(), 0, getTrxName());
						if (cd != null)
							Ref_CostDetail_ID = cd.getM_CostDetail_ID();
					}
					if (!MCostDetail.createShipment(as, line.getAD_Org_ID(),
						line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
						line.get_ID(), 0,
						amt, line.getQty(),
						line.getDescription(), true, line.getDateAcct(), Ref_CostDetail_ID, getTrxName()))
					{
						p_Error = Msg.getMsg(getCtx(),"Failed to create cost detail record");
						return null;
					}
				}
			}
		}	//	for all lines
	
		return fact;
	}

	private Fact costDetailForJPS(MAcctSchema as, MContractAcct contractAcct, Fact fact)
	{		
		for (int i = 0; i < p_lines.length; i++)
		{
			DocLine_InOut line = (DocLine_InOut) p_lines[i];
			MProduct product = line.getProduct();
			BigDecimal costs = null;
			Map<String, BigDecimal> batchLotCostMap = null;
			if (!isReversal(line)) 
			{
				if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(product.getCostingLevel(as)) ) 
				{	
					if (line.getM_AttributeSetInstance_ID() == 0 ) 
					{
						MInOutLine ioLine = (MInOutLine) line.getPO();
						MInOutLineMA mas[] = MInOutLineMA.get(getCtx(), ioLine.get_ID(), getTrxName());
						costs = BigDecimal.ZERO;
						if (mas != null && mas.length > 0 )
						{
							batchLotCostMap = new HashMap<>();
							for (int j = 0; j < mas.length; j++)
							{
								MInOutLineMA ma = mas[j];
								BigDecimal QtyMA = ma.getMovementQty();
								ProductCost pc = line.getProductCost();
								pc.setQty(QtyMA);
								pc.setM_M_AttributeSetInstance_ID(ma.getM_AttributeSetInstance_ID());
								BigDecimal maCosts = line.getProductCosts(as, line.getAD_Org_ID(), true, "M_InOutLine_ID=?");
								batchLotCostMap.put(ma.getM_InOutLineMA_UU(), maCosts);
								costs = costs.add(maCosts);
							}
						}
					} 
					else
					{
						costs = line.getProductCosts(as, line.getAD_Org_ID(), true, "M_InOutLine_ID=?");
					}
				}
				else
				{
					// MZ Goodwill
					// if Shipment CostDetail exist then get Cost from Cost Detail
					costs = line.getProductCosts(as, line.getAD_Org_ID(), true, "M_InOutLine_ID=?");
					// end MZ
				}
				if (costs == null || costs.signum() == 0)	//	zero costs OK
				{
					if (product.isStocked())
					{
						p_Error = Msg.getMsg(getCtx(),"No Costs for") + " " + line.getProduct().getName();  
						log.log(Level.WARNING, p_Error);
						return null;
					}
					else	//	ignore service
						continue;
				}
			} 
			else//Reversal
			{
				if(line.getReversalLine_ID() > 0)
				{
					MCostDetail cd = MCostDetail.getShipment(as, line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
							line.getReversalLine_ID(), 0, getTrxName());
					costs = cd.getAmt().negate();
				}else {
					p_Error = "Could not find original Cost Detail for reversal. ";
					log.log(Level.SEVERE, p_Error);
					return null;
				}
			}

			if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(product.getCostingLevel(as)) ) 
			{	
				if (line.getM_AttributeSetInstance_ID() == 0 ) 
				{
					MInOutLine ioLine = (MInOutLine) line.getPO();
					MInOutLineMA mas[] = MInOutLineMA.get(getCtx(), ioLine.get_ID(), getTrxName());
					if (mas != null && mas.length > 0 )
					{
						for (int j = 0; j < mas.length; j++)
						{
							MInOutLineMA ma = mas[j];
							BigDecimal qty = ma.getMovementQty();
							if (qty.signum() != line.getQty().signum())
								qty = qty.negate();
							BigDecimal amt = batchLotCostMap != null ? batchLotCostMap.get(ma.getM_InOutLineMA_UU()) : null;
							if (amt == null && isReversal(line))
							{
								amt = findReversalCostDetailAmt(as, line.getM_Product_ID(), ma, line.getReversalLine_ID());
								if (amt != null)
									amt = amt.negate();
							}
							if (amt == null) 
							{
								amt = costs.divide(line.getProductCost().getQty(), RoundingMode.HALF_UP);
								amt = amt.multiply(qty);
							}
							else if (!isReversal(line) && line.getProductCost().getQty().signum() != line.getQty().signum())
							{
								if (amt.signum() != (costs.signum() * -1)) {
									amt = amt.negate();
								}
							}
							int Ref_CostDetail_ID = 0;
							if (line.getReversalLine_ID() > 0 && line.get_ID() > line.getReversalLine_ID())
							{
								MCostDetail cd = MCostDetail.getShipment(as, line.getM_Product_ID(), ma.getM_AttributeSetInstance_ID(),
										line.getReversalLine_ID(), 0, getTrxName());
								if (cd != null)
									Ref_CostDetail_ID = cd.getM_CostDetail_ID();
							}
							if (!MCostDetail.createShipment(as, line.getAD_Org_ID(),
									line.getM_Product_ID(), ma.getM_AttributeSetInstance_ID(),
									line.get_ID(), 0,
									amt, qty,
									line.getDescription(), true, line.getDateAcct(), Ref_CostDetail_ID, getTrxName()))
							{
								p_Error = Msg.getMsg(getCtx(),"Failed to create cost detail record");
								return null;
							}
						}
					}
				} else
				{
					if (line.getM_Product_ID() != 0)
					{
						BigDecimal amt = costs;
						if (line.getProductCost().getQty().signum() != line.getQty().signum() && !isReversal(line))
							amt = amt.negate();
						int Ref_CostDetail_ID = 0;
						if (line.getReversalLine_ID() > 0 && line.get_ID() > line.getReversalLine_ID())
						{
							MCostDetail cd = MCostDetail.getShipment(as, line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
									line.getReversalLine_ID(), 0, getTrxName());
							if (cd != null)
								Ref_CostDetail_ID = cd.getM_CostDetail_ID();
						}
						if (!MCostDetail.createShipment(as, line.getAD_Org_ID(),
							line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
							line.get_ID(), 0,
							amt, line.getQty(),
							line.getDescription(), true, line.getDateAcct(), Ref_CostDetail_ID, getTrxName()))
						{
							p_Error = Msg.getMsg(getCtx(),"Failed to create cost detail record");
							return null;
						}
					}
				}
			} else
			{
				//
				if (line.getM_Product_ID() != 0)
				{
					BigDecimal amt = costs;
					if (line.getProductCost().getQty().signum() != line.getQty().signum() && !isReversal(line))
						amt = amt.negate();
					int Ref_CostDetail_ID = 0;
					if (line.getReversalLine_ID() > 0 && line.get_ID() > line.getReversalLine_ID())
					{
						MCostDetail cd = MCostDetail.getShipment(as, line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
								line.getReversalLine_ID(), 0, getTrxName());
						if (cd != null)
							Ref_CostDetail_ID = cd.getM_CostDetail_ID();
					}
					if (!MCostDetail.createShipment(as, line.getAD_Org_ID(),
						line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
						line.get_ID(), 0,
						amt, line.getQty(),
						line.getDescription(), true, line.getDateAcct(), Ref_CostDetail_ID, getTrxName()))
					{
						p_Error = Msg.getMsg(getCtx(),"Failed to create cost detail record");
						return null;
					}
				}
			}
			
		}	//	for all lines
	
		return fact;
	}
	
	/**
	 * @param as
	 * @param M_Product_ID
	 * @param ma
	 * @param reversalLine_ID
	 * @return MCostDetail.Amt
	 */
	private BigDecimal findReversalCostDetailAmt(MAcctSchema as, int M_Product_ID, MInOutLineMA ma, int reversalLine_ID) {
		StringBuilder select = new StringBuilder("SELECT ").append(MCostDetail.COLUMNNAME_Amt)
				.append(" FROM ").append(MCostDetail.Table_Name).append(" WHERE ")
				.append(MCostDetail.COLUMNNAME_C_AcctSchema_ID).append("=? ")
				.append("AND ").append(MCostDetail.COLUMNNAME_M_AttributeSetInstance_ID).append("=? ")
				.append("AND ").append(MCostDetail.COLUMNNAME_M_InOutLine_ID).append("=? ")
				.append("AND ").append(MCostDetail.COLUMNNAME_M_Product_ID).append("=? ");
		BigDecimal amt = DB.getSQLValueBDEx(getTrxName(), select.toString(), as.getC_AcctSchema_ID(), ma.getM_AttributeSetInstance_ID(), reversalLine_ID, M_Product_ID);
		return amt;
	}

	/**
	 * @param line
	 * @return true if line is for reversal
	 */	
	private boolean isReversal(DocLine line) {
		return m_Reversal_ID !=0 && line.getReversalLine_ID() != 0;
	}

	private MAccount getCOGSAccount(DocLine docLine, MContractAcct contractAcct, MAcctSchema as)
	{
		MInOutLine line = (MInOutLine)docLine.getPO();
		//Charge Account
		if (line.getM_Product_ID() == 0 && line.getC_Charge_ID() != 0)
		{
			MContractChargeAcct contractChargeAcct =  contractAcct.getContracChargeAcct(line.getC_Charge_ID(), as.getC_AcctSchema_ID(), false);
			if(contractChargeAcct != null && contractChargeAcct.getCh_Expense_Acct() > 0)
			{
				return MAccount.get(getCtx(), contractChargeAcct.getCh_Expense_Acct());
			}else{
				return docLine.getAccount(ProductCost.ACCTTYPE_P_Cogs, as) ;
			}

		}else if(line.getM_Product_ID() > 0){
			MContractProductAcct contractProductAcct = contractAcct.getContractProductAcct(line.getM_Product().getM_Product_Category_ID(), as.getC_AcctSchema_ID(), false);
			if(contractProductAcct != null && contractProductAcct.getP_COGS_Acct() > 0)
			{
				return MAccount.get(getCtx(),contractProductAcct.getP_COGS_Acct());
			}else{
				return docLine.getAccount(ProductCost.ACCTTYPE_P_Cogs, as) ;
			}
		}else{
			return docLine.getAccount(ProductCost.ACCTTYPE_P_Cogs, as) ;
		}
	}
}
