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

package jpiere.base.plugin.org.adempiere.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.I_M_InOutLine;
import org.compiere.model.MCharge;
import org.compiere.model.MCurrency;
import org.compiere.model.MInOutLine;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPriceList;
import org.compiere.model.MProduct;
import org.compiere.model.MProductPricing;
import org.compiere.model.MRMALine;
import org.compiere.model.MRole;
import org.compiere.model.MTax;
import org.compiere.model.MTaxCategory;
import org.compiere.model.MTaxProvider;
import org.compiere.model.MUOM;
import org.compiere.model.Query;
import org.compiere.model.Tax;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.base.IJPiereTaxProvider;
import jpiere.base.plugin.util.JPiereUtil;


/**
 * JPIERE-0363
 *
 * @author Hideaki Hagiwara
 *
 */
public class MRecognitionLine extends X_JP_RecognitionLine
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6174490999732876285L;

	/**
	 * 	Get Invoice Line referencing InOut Line
	 *	@param sLine shipment line
	 *	@return (first) invoice line
	 */
	public static MRecognitionLine getOfInOutLine (MInOutLine sLine)
	{
		if (sLine == null)
			return null;
		final String whereClause = I_M_InOutLine.COLUMNNAME_M_InOutLine_ID+"=?";
		List<MRecognitionLine> list = new Query(sLine.getCtx(),I_JP_RecognitionLine.Table_Name,whereClause,sLine.get_TrxName())
		.setParameters(sLine.getM_InOutLine_ID())
		.list();
		
		MRecognitionLine retValue = null;
		if (list.size() > 0) {
			retValue = list.get(0);
			if (list.size() > 1)
				s_log.warning("More than one C_InvoiceLine of " + sLine);
		}

		return retValue;
	}	//	getOfInOutLine

	/**
	 * 	Get Invoice Line referencing InOut Line - from MatchInv
	 *	@param sLine shipment line
	 *	@return (first) invoice line
	 */
	public static MRecognitionLine getOfInOutLineFromMatchInv(MInOutLine sLine) {
		if (sLine == null)
			return null;
		final String whereClause = "C_InvoiceLine_ID IN (SELECT C_InvoiceLine_ID FROM M_MatchInv WHERE M_InOutLine_ID=?)";
		List<MRecognitionLine> list = new Query(sLine.getCtx(),I_JP_RecognitionLine.Table_Name,whereClause,sLine.get_TrxName())
		.setParameters(sLine.getM_InOutLine_ID())
		.list();
		
		MRecognitionLine retValue = null;
		if (list.size() > 0) {
			retValue = list.get(0);
			if (list.size() > 1)
				s_log.warning("More than one C_InvoiceLine of " + sLine);
		}

		return retValue;
	}

	/**	Static Logger	*/
	private static CLogger	s_log	= CLogger.getCLogger (MRecognitionLine.class);

	/** Tax							*/
	private MTax 		m_tax = null;
	
	
	/**************************************************************************
	 * 	Invoice Line Constructor
	 * 	@param ctx context
	 * 	@param C_InvoiceLine_ID invoice line or 0
	 * 	@param trxName transaction name
	 */
	public MRecognitionLine (Properties ctx, int C_InvoiceLine_ID, String trxName)
	{
		super (ctx, C_InvoiceLine_ID, trxName);
		if (C_InvoiceLine_ID == 0)
		{
			setIsDescription(false);
			setIsPrinted (true);
			setLineNetAmt (Env.ZERO);
			setPriceEntered (Env.ZERO);
			setPriceActual (Env.ZERO);
			setPriceLimit (Env.ZERO);
			setPriceList (Env.ZERO);
			setM_AttributeSetInstance_ID(0);
			setTaxAmt(Env.ZERO);
			//
			setQtyEntered(Env.ZERO);
			setQtyInvoiced(Env.ZERO);
		}
	}	//	MInvoiceLine

	/**
	 * 	Parent Constructor
	 * 	@param recognition parent
	 */
	public MRecognitionLine (MRecognition recognition)
	{
		this (recognition.getCtx(), 0, recognition.get_TrxName());
		if (recognition.get_ID() == 0)
			throw new IllegalArgumentException("Header not saved");
		setClientOrg(recognition.getAD_Client_ID(), recognition.getAD_Org_ID());
		setJP_Recognition_ID(recognition.getJP_Recognition_ID());
		setRecognition(recognition);
	}	//	MRecognitionLine


	/**
	 *  Load Constructor
	 *  @param ctx context
	 *  @param rs result set record
	 *  @param trxName transaction
	 */
	public MRecognitionLine (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MInvoiceLine

	private int			m_M_PriceList_ID = 0;
	private Timestamp	m_DateInvoiced = null;
	private int			m_C_BPartner_ID = 0;
	private int			m_C_BPartner_Location_ID = 0;
	private boolean		m_IsSOTrx = true;
	private boolean		m_priceSet = false;
	private MProduct	m_product = null;
	/**	Charge					*/
	private MCharge 		m_charge = null;
	
	/**	Cached Name of the line		*/
	private String		m_name = null;
	/** Cached Precision			*/
	private Integer		m_precision = null;
	/** Product Pricing				*/
	private MProductPricing	m_productPricing = null;
	/** Parent						*/
	private MRecognition	m_parent = null;

	/**
	 * 	Set Defaults from Order.
	 * 	Called also from copy lines from invoice
	 * 	Does not set Parent !!
	 * 	@param recognition invoice
	 */
	public void setRecognition (MRecognition recognition)
	{
		m_parent = recognition;
		m_M_PriceList_ID = recognition.getM_PriceList_ID();
		m_DateInvoiced = recognition.getDateInvoiced();
		m_C_BPartner_ID = recognition.getC_BPartner_ID();
		m_C_BPartner_Location_ID = recognition.getC_BPartner_Location_ID();
		m_IsSOTrx = recognition.isSOTrx();
		m_precision = new Integer(recognition.getPrecision());
	}	//	setOrder

	/**
	 * 	Get Parent
	 *	@return parent
	 */
	public MRecognition getParent()
	{
		if (m_parent == null)
			m_parent = new MRecognition(getCtx(), getJP_Recognition_ID(), get_TrxName());
		return m_parent;
	}	//	getParent

	/**
	 * 	Set values from Order Line.
	 * 	Does not set quantity!
	 *	@param oLine line
	 */
	public void setOrderLine (MOrderLine oLine)
	{
		setC_OrderLine_ID(oLine.getC_OrderLine_ID());
		//
		setLine(oLine.getLine());
		setIsDescription(oLine.isDescription());
		setDescription(oLine.getDescription());
		//
		if(oLine.getM_Product_ID() == 0)
		setC_Charge_ID(oLine.getC_Charge_ID());
		//
		setM_Product_ID(oLine.getM_Product_ID());
		setM_AttributeSetInstance_ID(oLine.getM_AttributeSetInstance_ID());
		setS_ResourceAssignment_ID(oLine.getS_ResourceAssignment_ID());
		setC_UOM_ID(oLine.getC_UOM_ID());
		//
		setPriceEntered(oLine.getPriceEntered());
		setPriceActual(oLine.getPriceActual());
		setPriceLimit(oLine.getPriceLimit());
		setPriceList(oLine.getPriceList());
		//
		setC_Tax_ID(oLine.getC_Tax_ID());
		setLineNetAmt(oLine.getLineNetAmt());
		//
		setC_Project_ID(oLine.getC_Project_ID());
		setC_ProjectPhase_ID(oLine.getC_ProjectPhase_ID());
		setC_ProjectTask_ID(oLine.getC_ProjectTask_ID());
		setC_Activity_ID(oLine.getC_Activity_ID());
		setC_Campaign_ID(oLine.getC_Campaign_ID());
		setAD_OrgTrx_ID(oLine.getAD_OrgTrx_ID());
		setUser1_ID(oLine.getUser1_ID());
		setUser2_ID(oLine.getUser2_ID());

	}	//	setOrderLine

	/**
	 * 	Set values from Shipment Line.
	 * 	Does not set quantity!
	 *	@param sLine ship line
	 */
	public void setRecogLine (MInOutLine sLine)
	{
		setM_InOutLine_ID(sLine.getM_InOutLine_ID());
		setC_OrderLine_ID(sLine.getC_OrderLine_ID());
		// Set RMALine ID if shipment/receipt is based on RMA Doc
        setM_RMALine_ID(sLine.getM_RMALine_ID());

		//
		setLine(sLine.getLine());
		setIsDescription(sLine.isDescription());
		setDescription(sLine.getDescription());
		//
		setM_Product_ID(sLine.getM_Product_ID());
		if (sLine.sameOrderLineUOM() || getProduct() == null)
			setC_UOM_ID(sLine.getC_UOM_ID());
		else
			// use product UOM if the shipment hasn't the same uom than the order
			setC_UOM_ID(getProduct().getC_UOM_ID());
		setM_AttributeSetInstance_ID(sLine.getM_AttributeSetInstance_ID());
	//	setS_ResourceAssignment_ID(sLine.getS_ResourceAssignment_ID());
		if(getM_Product_ID() == 0)
		    setC_Charge_ID(sLine.getC_Charge_ID());
		//
		int C_OrderLine_ID = sLine.getC_OrderLine_ID();
		if (C_OrderLine_ID != 0)
		{
			MOrderLine oLine = new MOrderLine (getCtx(), C_OrderLine_ID, get_TrxName());
			setS_ResourceAssignment_ID(oLine.getS_ResourceAssignment_ID());
			//
			if (sLine.sameOrderLineUOM())
				setPriceEntered(oLine.getPriceEntered());
			else
				setPriceEntered(oLine.getPriceActual());
			setPriceActual(oLine.getPriceActual());
			setPriceLimit(oLine.getPriceLimit());
			setPriceList(oLine.getPriceList());
			//
			setC_Tax_ID(oLine.getC_Tax_ID());
			setLineNetAmt(oLine.getLineNetAmt());
			setC_Project_ID(oLine.getC_Project_ID());
		}
		// Check if shipment line is based on RMA
        else if (sLine.getM_RMALine_ID() != 0)
        {
        	// Set Pricing details from the RMA Line on which it is based
            MRMALine rmaLine = new MRMALine(getCtx(), sLine.getM_RMALine_ID(), get_TrxName());

            setPrice();
            setPrice(rmaLine.getAmt());
            setC_Tax_ID(rmaLine.getC_Tax_ID());
            setLineNetAmt(rmaLine.getLineNetAmt());
        }
		else
		{
			setPrice();
			setTax();
		}
		//
		setC_Project_ID(sLine.getC_Project_ID());
		setC_ProjectPhase_ID(sLine.getC_ProjectPhase_ID());
		setC_ProjectTask_ID(sLine.getC_ProjectTask_ID());
		setC_Activity_ID(sLine.getC_Activity_ID());
		setC_Campaign_ID(sLine.getC_Campaign_ID());
		setAD_OrgTrx_ID(sLine.getAD_OrgTrx_ID());
		setUser1_ID(sLine.getUser1_ID());
		setUser2_ID(sLine.getUser2_ID());
		setJP_ContractLine_ID(sLine.get_ValueAsInt("JP_ContractLine_ID") );
		setJP_ContractProcPeriod_ID(sLine.get_ValueAsInt("JP_ContractProcPeriod_ID") );
	}	//	setShipLine

	/**
	 * 	Add to Description
	 *	@param description text
	 */
	public void addDescription (String description)
	{
		String desc = getDescription();
		if (desc == null)
			setDescription(description);
		else{
			StringBuilder msgd = new StringBuilder(desc).append(" | ").append(description);
			setDescription(msgd.toString());
		}	
	}	//	addDescription

	/**
	 * 	Set M_AttributeSetInstance_ID
	 *	@param M_AttributeSetInstance_ID id
	 */
	public void setM_AttributeSetInstance_ID (int M_AttributeSetInstance_ID)
	{
		if (M_AttributeSetInstance_ID == 0)		//	 0 is valid ID
			set_Value("M_AttributeSetInstance_ID", new Integer(0));
		else
			super.setM_AttributeSetInstance_ID (M_AttributeSetInstance_ID);
	}	//	setM_AttributeSetInstance_ID


	/**************************************************************************
	 * 	Set Price for Product and PriceList.
	 * 	Uses standard SO price list of not set by invoice constructor
	 */
	public void setPrice()
	{
		if (getM_Product_ID() == 0 || isDescription())
			return;
		if (m_M_PriceList_ID == 0 || m_C_BPartner_ID == 0)
			setRecognition(getParent());
		if (m_M_PriceList_ID == 0 || m_C_BPartner_ID == 0)
			throw new IllegalStateException("setPrice - PriceList unknown!");
		setPrice (m_M_PriceList_ID, m_C_BPartner_ID);
	}	//	setPrice

	/**
	 * 	Set Price for Product and PriceList
	 * 	@param M_PriceList_ID price list
	 * 	@param C_BPartner_ID business partner
	 */
	public void setPrice (int M_PriceList_ID, int C_BPartner_ID)
	{
		if (getM_Product_ID() == 0 || isDescription())
			return;
		//
		if (log.isLoggable(Level.FINE)) log.fine("M_PriceList_ID=" + M_PriceList_ID);
		m_productPricing = new MProductPricing (getM_Product_ID(),
			C_BPartner_ID, getQtyInvoiced(), m_IsSOTrx);
		m_productPricing.setM_PriceList_ID(M_PriceList_ID);
		m_productPricing.setPriceDate(m_DateInvoiced);
		//
		setPriceActual (m_productPricing.getPriceStd());
		setPriceList (m_productPricing.getPriceList());
		setPriceLimit (m_productPricing.getPriceLimit());
		//
		if (getQtyEntered().compareTo(getQtyInvoiced()) == 0)
			setPriceEntered(getPriceActual());
		else
			setPriceEntered(getPriceActual().multiply(getQtyInvoiced()
				.divide(getQtyEntered(), 6, BigDecimal.ROUND_HALF_UP)));	//	precision
		//
		if (getC_UOM_ID() == 0)
			setC_UOM_ID(m_productPricing.getC_UOM_ID());
		//
		m_priceSet = true;
	}	//	setPrice

	/**
	 * 	Set Price Entered/Actual.
	 * 	Use this Method if the Line UOM is the Product UOM
	 *	@param PriceActual price
	 */
	public void setPrice (BigDecimal PriceActual)
	{
		setPriceEntered(PriceActual);
		setPriceActual (PriceActual);
	}	//	setPrice

	/**
	 * 	Set Price Actual.
	 * 	(actual price is not updateable)
	 *	@param PriceActual actual price
	 */
	public void setPriceActual (BigDecimal PriceActual)
	{
		if (PriceActual == null)
			throw new IllegalArgumentException ("PriceActual is mandatory");
		set_ValueNoCheck("PriceActual", PriceActual);
	}	//	setPriceActual


	/**
	 *	Set Tax - requires Warehouse
	 *	@return true if found
	 */
	public boolean setTax()
	{
		if (isDescription())
			return true;
		//
		int M_Warehouse_ID = Env.getContextAsInt(getCtx(), "#M_Warehouse_ID");
		//
		int C_Tax_ID = Tax.get(getCtx(), getM_Product_ID(), getC_Charge_ID() , m_DateInvoiced, m_DateInvoiced,
			getAD_Org_ID(), M_Warehouse_ID,
			m_C_BPartner_Location_ID,		//	should be bill to
			m_C_BPartner_Location_ID, m_IsSOTrx, get_TrxName());
		if (C_Tax_ID == 0)
		{
			log.log(Level.SEVERE, "No Tax found");
			return false;
		}
		setC_Tax_ID (C_Tax_ID);
		return true;
	}	//	setTax


	/**
	 * 	Calculate Tax Amt.
	 * 	Assumes Line Net is calculated
	 */
	public void setTaxAmt ()
	{
		BigDecimal TaxAmt = Env.ZERO;
		if (getC_Tax_ID() == 0)
			return;
	//	setLineNetAmt();
		MTax tax = MTax.get (getCtx(), getC_Tax_ID());
		if (tax.isDocumentLevel() && m_IsSOTrx)		//	AR Inv Tax
			return;
		//
		TaxAmt = tax.calculateTax(getLineNetAmt(), isTaxIncluded(), getPrecision());
		if (isTaxIncluded())
			setLineTotalAmt(getLineNetAmt());
		else
			setLineTotalAmt(getLineNetAmt().add(TaxAmt));
		super.setTaxAmt (TaxAmt);
	}	//	setTaxAmt

	/**
	 * 	Calculate Extended Amt.
	 * 	May or may not include tax
	 */
	public void setLineNetAmt ()
	{
		//	Calculations & Rounding
		BigDecimal bd = getPriceActual().multiply(getQtyInvoiced());
		
		boolean documentLevel = getTax().isDocumentLevel();

		//	juddm: Tax Exempt & Tax Included in Price List & not Document Level - Adjust Line Amount
		//  http://sourceforge.net/tracker/index.php?func=detail&aid=1733602&group_id=176962&atid=879332
		if (isTaxIncluded() && !documentLevel)	{
			BigDecimal taxStdAmt = Env.ZERO, taxThisAmt = Env.ZERO;
			
			MTax invoiceTax = getTax();
			MTax stdTax = null;
			
			if (getProduct() == null)
			{
				if (getCharge() != null)	// Charge 
				{
					stdTax = new MTax (getCtx(), 
							((MTaxCategory) getCharge().getC_TaxCategory()).getDefaultTax().getC_Tax_ID(),
							get_TrxName());
				}
					
			}
			else	// Product
				stdTax = new MTax (getCtx(), 
							((MTaxCategory) getProduct().getC_TaxCategory()).getDefaultTax().getC_Tax_ID(), 
							get_TrxName());

			if (stdTax != null)
			{
				
				if (log.isLoggable(Level.FINE)) log.fine("stdTax rate is " + stdTax.getRate());
				if (log.isLoggable(Level.FINE)) log.fine("invoiceTax rate is " + invoiceTax.getRate());
				
				taxThisAmt = taxThisAmt.add(invoiceTax.calculateTax(bd, isTaxIncluded(), getPrecision()));
				taxStdAmt = taxStdAmt.add(stdTax.calculateTax(bd, isTaxIncluded(), getPrecision()));
				
				bd = bd.subtract(taxStdAmt).add(taxThisAmt);
				
				if (log.isLoggable(Level.FINE)) log.fine("Price List includes Tax and Tax Changed on Invoice Line: New Tax Amt: " 
						+ taxThisAmt + " Standard Tax Amt: " + taxStdAmt + " Line Net Amt: " + bd);	
			}
		}
		int precision = getPrecision();
		if (bd.scale() > precision)
			bd = bd.setScale(precision, BigDecimal.ROUND_HALF_UP);
		super.setLineNetAmt (bd);
	}	//	setLineNetAmt
	/**
	 * 	Get Charge
	 *	@return product or null
	 */
	public MCharge getCharge()
	{
		if (m_charge == null && getC_Charge_ID() != 0)
			m_charge =  MCharge.get (getCtx(), getC_Charge_ID());
		return m_charge;
	}
	/**
	 * 	Get Tax
	 *	@return tax
	 */
	protected MTax getTax()
	{
		if (m_tax == null)
			m_tax = MTax.get(getCtx(), getC_Tax_ID());
		return m_tax;
	}	//	getTax

	/**
	 * 	Set Qty Invoiced/Entered.
	 *	@param Qty Invoiced/Ordered
	 */
	public void setQty (int Qty)
	{
		setQty(new BigDecimal(Qty));
	}	//	setQtyInvoiced

	/**
	 * 	Set Qty Invoiced
	 *	@param Qty Invoiced/Entered
	 */
	public void setQty (BigDecimal Qty)
	{
		setQtyEntered(Qty);
		setQtyInvoiced(getQtyEntered());
		setJP_QtyRecognized(getQtyInvoiced());
		setJP_TargetQtyRecognized(getQtyInvoiced());
	}	//	setQtyInvoiced

	/**
	 * 	Set Qty Entered - enforce entered UOM
	 *	@param QtyEntered
	 */
	public void setQtyEntered (BigDecimal QtyEntered)
	{
		if (QtyEntered != null && getC_UOM_ID() != 0)
		{
			int precision = MUOM.getPrecision(getCtx(), getC_UOM_ID());
			QtyEntered = QtyEntered.setScale(precision, BigDecimal.ROUND_HALF_UP);
		}
		super.setQtyEntered (QtyEntered);
	}	//	setQtyEntered

	/**
	 * 	Set Qty Invoiced - enforce Product UOM
	 *	@param QtyInvoiced
	 */
	public void setQtyInvoiced (BigDecimal QtyInvoiced)
	{
		MProduct product = getProduct();
		if (QtyInvoiced != null && product != null)
		{
			int precision = product.getUOMPrecision();
			QtyInvoiced = QtyInvoiced.setScale(precision, BigDecimal.ROUND_HALF_UP);
		}
		super.setQtyInvoiced(QtyInvoiced);
	}	//	setQtyInvoiced

	/**
	 * 	Set Product
	 *	@param product product
	 */
	public void setProduct (MProduct product)
	{
		m_product = product;
		if (m_product != null)
		{
			setM_Product_ID(m_product.getM_Product_ID());
			setC_UOM_ID (m_product.getC_UOM_ID());
		}
		else
		{
			setM_Product_ID(0);
			setC_UOM_ID (0);
		}
		setM_AttributeSetInstance_ID(0);
	}	//	setProduct


	/**
	 * 	Set M_Product_ID
	 *	@param M_Product_ID product
	 *	@param setUOM set UOM from product
	 */
	public void setM_Product_ID (int M_Product_ID, boolean setUOM)
	{
		if (setUOM)
			setProduct(MProduct.get(getCtx(), M_Product_ID));
		else
			super.setM_Product_ID (M_Product_ID);
		setM_AttributeSetInstance_ID(0);
	}	//	setM_Product_ID

	/**
	 * 	Set Product and UOM
	 *	@param M_Product_ID product
	 *	@param C_UOM_ID uom
	 */
	public void setM_Product_ID (int M_Product_ID, int C_UOM_ID)
	{
		super.setM_Product_ID (M_Product_ID);
		super.setC_UOM_ID(C_UOM_ID);
		setM_AttributeSetInstance_ID(0);
	}	//	setM_Product_ID

	/**
	 * 	Get Product
	 *	@return product or null
	 */
	public MProduct getProduct()
	{
		if (m_product == null && getM_Product_ID() != 0)
			m_product =  MProduct.get (getCtx(), getM_Product_ID());
		return m_product;
	}	//	getProduct

	/**
	 * 	Get C_Project_ID
	 *	@return project
	 */
	public int getC_Project_ID()
	{
		int ii = super.getC_Project_ID ();
		if (ii == 0)
			ii = getParent().getC_Project_ID();
		return ii;
	}	//	getC_Project_ID

	/**
	 * 	Get C_Activity_ID
	 *	@return Activity
	 */
	public int getC_Activity_ID()
	{
		int ii = super.getC_Activity_ID ();
		if (ii == 0)
			ii = getParent().getC_Activity_ID();
		return ii;
	}	//	getC_Activity_ID

	/**
	 * 	Get C_Campaign_ID
	 *	@return Campaign
	 */
	public int getC_Campaign_ID()
	{
		int ii = super.getC_Campaign_ID ();
		if (ii == 0)
			ii = getParent().getC_Campaign_ID();
		return ii;
	}	//	getC_Campaign_ID

	/**
	 * 	Get User2_ID
	 *	@return User2
	 */
	public int getUser1_ID ()
	{
		int ii = super.getUser1_ID ();
		if (ii == 0)
			ii = getParent().getUser1_ID();
		return ii;
	}	//	getUser1_ID

	/**
	 * 	Get User2_ID
	 *	@return User2
	 */
	public int getUser2_ID ()
	{
		int ii = super.getUser2_ID ();
		if (ii == 0)
			ii = getParent().getUser2_ID();
		return ii;
	}	//	getUser2_ID

	/**
	 * 	Get AD_OrgTrx_ID
	 *	@return trx org
	 */
	public int getAD_OrgTrx_ID()
	{
		int ii = super.getAD_OrgTrx_ID();
		if (ii == 0)
			ii = getParent().getAD_OrgTrx_ID();
		return ii;
	}	//	getAD_OrgTrx_ID

	/**
	 * 	String Representation
	 *	@return info
	 */
	public String toString ()
	{
		StringBuilder sb = new StringBuilder ("MRecotnitionLine[")
			.append(get_ID()).append(",").append(getLine())
			.append(",QtyInvoiced=").append(getQtyInvoiced())
			.append(",LineNetAmt=").append(getLineNetAmt())
			.append ("]");
		return sb.toString ();
	}	//	toString

	/**
	 * 	Get (Product/Charge) Name
	 * 	@return name
	 */
	public String getName ()
	{
		if (m_name == null)
		{
			String sql = "SELECT COALESCE (p.Name, c.Name) "
				+ "FROM JP_RecognitionLine il"
				+ " LEFT OUTER JOIN M_Product p ON (il.M_Product_ID=p.M_Product_ID)"
				+ " LEFT OUTER JOIN C_Charge C ON (il.C_Charge_ID=c.C_Charge_ID) "
				+ "WHERE JP_RecognitionLine_ID=?";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql, get_TrxName());
				pstmt.setInt(1, getJP_Recognition_ID());
				rs = pstmt.executeQuery();
				if (rs.next())
					m_name = rs.getString(1);
				if (m_name == null)
					m_name = "??";
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, "getName", e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}
		}
		return m_name;
	}	//	getName

	/**
	 * 	Set Temporary (cached) Name
	 * 	@param tempName Cached Name
	 */
	public void setName (String tempName)
	{
		m_name = tempName;
	}	//	setName

	/**
	 * 	Get Description Text.
	 * 	For jsp access (vs. isDescription)
	 *	@return description
	 */
	public String getDescriptionText()
	{
		return super.getDescription();
	}	//	getDescriptionText

	/**
	 * 	Get Currency Precision
	 *	@return precision
	 */
	public int getPrecision()
	{
		if (m_precision != null)
			return m_precision.intValue();

		String sql = "SELECT c.StdPrecision "
			+ "FROM C_Currency c INNER JOIN JP_Recognition x ON (x.C_Currency_ID=c.C_Currency_ID) "
			+ "WHERE x.JP_Recognition_ID=?";
		int i = DB.getSQLValue(get_TrxName(), sql, getJP_Recognition_ID());
		if (i < 0)
		{
			log.warning("getPrecision = " + i + " - set to 2");
			i = 2;
		}
		m_precision = new Integer(i);
		return m_precision.intValue();
	}	//	getPrecision

	/**
	 *	Is Tax Included in Amount
	 *	@return true if tax is included
	 */
	public boolean isTaxIncluded()
	{
		if (m_M_PriceList_ID == 0)
		{
			m_M_PriceList_ID = DB.getSQLValue(get_TrxName(),
				"SELECT M_PriceList_ID FROM JP_Recognition WHERE JP_Recognition_ID=?",
				getJP_Recognition_ID());
		}
		MPriceList pl = MPriceList.get(getCtx(), m_M_PriceList_ID, get_TrxName());
		return pl.isTaxIncluded();
	}	//	isTaxIncluded


	/**************************************************************************
	 * 	Before Save
	 *	@param newRecord
	 *	@return true if save
	 */
	protected boolean beforeSave (boolean newRecord)
	{		
		if(newRecord || is_ValueChanged("QtyInvoiced") || is_ValueChanged("QtyEntered"))
		{
			if(newRecord)
			{
				if(getJP_TargetQtyRecognized().signum() == 0)
				{
					setJP_TargetQtyRecognized(getM_InOutLine().getMovementQty());
				}
			}
			
			setJP_QtyRecognized(getQtyInvoiced());
			if(getJP_TargetQtyRecognized().signum() != getJP_QtyRecognized().signum())
			{
				log.saveError("Error",Msg.getMsg(getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), "JP_TargetQtyRecognized"),Msg.getElement(Env.getCtx(), "JP_QtyRecognized")}));
				return false;
			}
			
			if(getJP_QtyRecognized().abs().compareTo(getJP_TargetQtyRecognized().abs()) > 0)
			{
				log.saveError("Error",Msg.getMsg(getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), "JP_TargetQtyRecognized"),Msg.getElement(Env.getCtx(), "JP_QtyRecognized")}));
				return false;
			}
		}
		
		
		if (log.isLoggable(Level.FINE)) log.fine("New=" + newRecord);
		if (newRecord && getParent().isComplete()) {
			log.saveError("ParentComplete", Msg.translate(getCtx(), "C_InvoiceLine"));
			return false;
		}
		// Re-set invoice header (need to update m_IsSOTrx flag) - phib [ 1686773 ]
		setRecognition(getParent());
		//	Charge
		if (getC_Charge_ID() != 0)
		{
			if (getM_Product_ID() != 0)
				setM_Product_ID(0);
		}
		else	//	Set Product Price
		{
			if (!m_priceSet
				&&  Env.ZERO.compareTo(getPriceActual()) == 0
				&&  Env.ZERO.compareTo(getPriceList()) == 0)
				setPrice();
				// IDEMPIERE-1574 Sales Order Line lets Price under the Price Limit when updating
				//	Check PriceLimit
				boolean enforce = m_IsSOTrx && getParent().getM_PriceList().isEnforcePriceLimit();
				if (enforce && MRole.getDefault().isOverwritePriceLimit())
					enforce = false;
				//	Check Price Limit?
				if (enforce && getPriceLimit() != Env.ZERO
				  && getPriceActual().compareTo(getPriceLimit()) < 0)
				{
					log.saveError("UnderLimitPrice", "PriceEntered=" + getPriceEntered() + ", PriceLimit=" + getPriceLimit()); 
					return false;
				}
				//
		}

		//	Set Tax
		if (getC_Tax_ID() == 0)
			setTax();

		//	Get Line No
		if (getLine() == 0)
		{
			String sql = "SELECT COALESCE(MAX(Line),0)+10 FROM JP_RecognitionLine WHERE JP_Recognition_ID=?";
			int ii = DB.getSQLValue (get_TrxName(), sql, getJP_Recognition_ID());
			setLine (ii);
		}
		//	UOM
		if (getC_UOM_ID() == 0)
		{
			int C_UOM_ID = MUOM.getDefault_UOM_ID(getCtx());
			if (C_UOM_ID > 0)
				setC_UOM_ID (C_UOM_ID);
		}
		//	Qty Precision
		if (newRecord || is_ValueChanged("QtyEntered"))
			setQtyEntered(getQtyEntered());
		if (newRecord || is_ValueChanged("QtyInvoiced"))
			setQtyInvoiced(getQtyInvoiced());

		//	Calculations & Rounding
		setLineNetAmt();
		// TaxAmt recalculations should be done if the TaxAmt is zero
		// or this is an Invoice(Customer) - teo_sarca, globalqss [ 1686773 ]
		if (m_IsSOTrx || getTaxAmt().compareTo(Env.ZERO) == 0)
			setTaxAmt();
		//
		
		/* Carlos Ruiz - globalqss
		 * IDEMPIERE-178 Orders and Invoices must disallow amount lines without product/charge
		 */
		if (getParent().getC_DocTypeTarget().isChargeOrProductMandatory()) {
			if (getC_Charge_ID() == 0 && getM_Product_ID() == 0 && (getPriceEntered().signum() != 0 || getQtyEntered().signum() != 0)) {
				log.saveError("FillMandatory", Msg.translate(getCtx(), "ChargeOrProductMandatory"));
				return false;
			}
		}
		
		//Tax Calculation
		if(newRecord || is_ValueChanged("LineNetAmt") || is_ValueChanged("C_Tax_ID"))
		{
			BigDecimal taxAmt = Env.ZERO;
			MTax m_tax = MTax.get(Env.getCtx(), getC_Tax_ID());
			if(m_tax == null)
			{
				;//Nothing to do;
			}else{

				IJPiereTaxProvider taxCalculater = JPiereUtil.getJPiereTaxProvider(m_tax);
				if(taxCalculater != null)
				{
					taxAmt = taxCalculater.calculateTax(m_tax, getLineNetAmt(), isTaxIncluded()
							, MCurrency.getStdPrecision(getCtx(), getParent().getC_Currency_ID())
							, JPiereTaxProvider.getRoundingMode(getParent().getC_BPartner_ID(), getParent().isSOTrx(), m_tax.getC_TaxProvider()));
				}else{
					taxAmt = m_tax.calculateTax(getLineNetAmt(), isTaxIncluded(), MCurrency.getStdPrecision(getCtx(), getParent().getC_Currency_ID()));
				}
	
				if(isTaxIncluded())
				{
					set_ValueNoCheck("JP_TaxBaseAmt",  getLineNetAmt().subtract(taxAmt));
				}else{
					set_ValueNoCheck("JP_TaxBaseAmt",  getLineNetAmt());
				}
	
				set_ValueOfColumn("JP_TaxAmt", taxAmt);
				
			}
		}//Tax Calculation
		
		return true;
	}	//	beforeSave

	/**
	 * Recalculate invoice tax
	 * @param oldTax true if the old C_Tax_ID should be used
	 * @return true if success, false otherwise
	 *
	 * @author teo_sarca [ 1583825 ]
	 */
	protected boolean updateInvoiceTax(boolean oldTax) {
		MRecognitionTax tax = MRecognitionTax.get (this, getPrecision(), oldTax, get_TrxName());
		if (tax != null) {
			if (!tax.calculateTaxFromLines())
				return false;
		
			// red1 - solving BUGS #[ 1701331 ] , #[ 1786103 ]
			if (tax.getTaxAmt().signum() != 0) {
				if (!tax.save(get_TrxName()))
					return false;
			}
			else {
				if (!tax.is_new() && !tax.delete(false, get_TrxName()))
					return false;
			}
		}
		return true;
	}

	/**
	 * 	After Save
	 *	@param newRecord new
	 *	@param success success
	 *	@return saved
	 */
	protected boolean afterSave (boolean newRecord, boolean success)
	{
		if (!success)
			return success;
		if (getParent().isProcessed())
			return success;
		if (newRecord
			|| is_ValueChanged(MRecognitionLine.COLUMNNAME_C_Tax_ID)
			|| is_ValueChanged(MRecognitionLine.COLUMNNAME_LineNetAmt)) {
			MTax m_tax = new MTax(getCtx(), getC_Tax_ID(), get_TrxName());
			IJPiereTaxProvider taxCalculater = JPiereUtil.getJPiereTaxProvider(m_tax);
			MTaxProvider provider = new MTaxProvider(m_tax.getCtx(), m_tax.getC_TaxProvider_ID(), m_tax.get_TrxName());
			if (taxCalculater == null)
				throw new AdempiereException(Msg.getMsg(getCtx(), "TaxNoProvider"));
			success = taxCalculater.recalculateTax(provider, this, newRecord);
	    	if(!success)
	    		return false;
		}
		
		return success;
		
	}	//	afterSave

	/**
	 * 	After Delete
	 *	@param success success
	 *	@return deleted
	 */
	protected boolean afterDelete (boolean success)
	{
		if (!success)
			return success;

		MTax m_tax = new MTax(getCtx(), getC_Tax_ID(), get_TrxName());
		IJPiereTaxProvider taxCalculater = JPiereUtil.getJPiereTaxProvider(m_tax);
		MTaxProvider provider = new MTaxProvider(m_tax.getCtx(), m_tax.getC_TaxProvider_ID(), m_tax.get_TrxName());
		if (taxCalculater == null)
			throw new AdempiereException(Msg.getMsg(getCtx(), "TaxNoProvider"));
    	return taxCalculater.recalculateTax(provider, this, false);
	}	//	afterDelete


	/**
	 * @param rmaline
	 */
	public void setRMALine(MRMALine rmaLine)
	{
		// Check if this invoice is CreditMemo - teo_sarca [ 2804142 ]
		if (!getParent().isCreditMemo())
		{
			throw new AdempiereException("InvoiceNotCreditMemo");
		}
		setAD_Org_ID(rmaLine.getAD_Org_ID());
        setM_RMALine_ID(rmaLine.getM_RMALine_ID());
        setDescription(rmaLine.getDescription());
        setLine(rmaLine.getLine());
        setC_Charge_ID(rmaLine.getC_Charge_ID());
        setM_Product_ID(rmaLine.getM_Product_ID());
        setC_UOM_ID(rmaLine.getC_UOM_ID());
        setC_Tax_ID(rmaLine.getC_Tax_ID());
        setPrice(rmaLine.getAmt());
        BigDecimal qty = rmaLine.getQty();
        if (rmaLine.getQtyInvoiced() != null)
        	qty = qty.subtract(rmaLine.getQtyInvoiced());
        setQty(qty);
        setLineNetAmt();
        setTaxAmt();
        setLineTotalAmt(rmaLine.getLineNetAmt());
        setC_Project_ID(rmaLine.getC_Project_ID());
        setC_Activity_ID(rmaLine.getC_Activity_ID());
        setC_Campaign_ID(rmaLine.getC_Campaign_ID());
	}


	public void clearParent()
	{
		this.m_parent = null;
	}

}	//	MInvoiceLine
