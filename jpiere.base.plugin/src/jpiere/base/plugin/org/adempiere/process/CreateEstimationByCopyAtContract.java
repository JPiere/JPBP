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
package jpiere.base.plugin.org.adempiere.process;

import java.sql.Timestamp;
import java.util.logging.Level;

import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MOrgInfo;
import org.compiere.model.MWarehouse;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MEstimation;

/**
 *  JPIERE-0442: Create Estimation by Copy at Contract
 *
 *	@author Hideaki Hagiwara
 */

public class CreateEstimationByCopyAtContract extends SvrProcess
{
	/**	The Estimation 		*/
	private int p_JP_Estimation_ID = 0;
	private int p_JP_Contract_ID = 0;
	private int p_C_BPartner_ID = 0;
	private MContract p_Contract = null;

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		p_JP_Contract_ID = getRecord_ID();
		p_Contract = new MContract(getCtx(), p_JP_Contract_ID, get_TrxName());

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("JP_Estimation_ID"))
				p_JP_Estimation_ID = para[i].getParameterAsInt();
			else if (name.equals("C_BPartner_ID"))
				p_C_BPartner_ID = para[i].getParameterAsInt();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
	}	//	prepare

	/**
	 *  Perform process.
	 *  @return Message (clear text)
	 *  @throws Exception if not successful
	 */
	protected String doIt() throws Exception
	{
		MEstimation from_Estimation = new MEstimation (getCtx(), p_JP_Estimation_ID, get_TrxName());
		MEstimation to_Estimation = new MEstimation (getCtx(), 0, get_TrxName());
		PO.copyValues(from_Estimation, to_Estimation);

		Timestamp now = new Timestamp (System.currentTimeMillis());
		to_Estimation.setJP_EstimationDate(now);
		to_Estimation.setDateOrdered(now);
		to_Estimation.setDateAcct(now);
		to_Estimation.setDatePromised(now);

		to_Estimation.setC_DocTypeTarget_ID(from_Estimation.getC_DocTypeTarget_ID());
		if(from_Estimation.getC_DocTypeTarget().isDocNoControlled())
		{

			to_Estimation.setDocumentNo("");

		}else {

			to_Estimation.setDocumentNo(now.toString()) ;

		}

		//C_Opportunity_ID
		if(from_Estimation.getC_Opportunity_ID() > 0)
		{
			if(from_Estimation.getC_Opportunity().getC_BPartner_ID() == p_C_BPartner_ID)
			{
				to_Estimation.setC_Opportunity_ID(from_Estimation.getC_Opportunity_ID());

			}else if(p_C_BPartner_ID == 0 && from_Estimation.getC_Opportunity().getC_BPartner_ID() == p_Contract.getC_BPartner_ID()) {

				to_Estimation.setC_Opportunity_ID(from_Estimation.getC_Opportunity_ID());

			}else {

				to_Estimation.setC_Opportunity_ID(0);
			}
		}

		to_Estimation.setAD_Org_ID(p_Contract.getAD_Org_ID());
		setWarehouseOfContractContent(from_Estimation,to_Estimation);

		to_Estimation.setDocStatus(DocAction.STATUS_Drafted);
		to_Estimation.setDocAction(DocAction.ACTION_Complete);
		to_Estimation.setLink_Order_ID(0);
		to_Estimation.setRef_Order_ID(0);
		to_Estimation.setM_InOut_ID(0);
		to_Estimation.setC_Invoice_ID(0);
		to_Estimation.setJP_Bill_ID(0);
		to_Estimation.setC_Payment_ID(0);
		to_Estimation.setM_RMA_ID(0);
		to_Estimation.setJP_Contract_ID(0);
		to_Estimation.setJP_ContractContent_ID(0);

		if(p_C_BPartner_ID != 0)
		{
			if(p_C_BPartner_ID == p_Contract.getC_BPartner_ID())
			{
				to_Estimation.setC_BPartner_ID(p_C_BPartner_ID);
				to_Estimation.setC_BPartner_Location_ID(p_Contract.getC_BPartner_Location_ID());
				to_Estimation.setAD_User_ID(p_Contract.getAD_User_ID());

			}else {

				MBPartner bp = new MBPartner(getCtx(), p_C_BPartner_ID, get_TrxName());
				to_Estimation.setC_BPartner_ID(p_C_BPartner_ID);
				to_Estimation.setC_BPartner_Location_ID(0);
				to_Estimation.setAD_User_ID(0);

				MBPartnerLocation[] locations = bp.getLocations(false);
				if(locations.length > 0)
					to_Estimation.setC_BPartner_Location_ID(locations[0].getC_BPartner_Location_ID());

			}

			to_Estimation.setBill_BPartner_ID(0);
			to_Estimation.setBill_Location_ID(0);
			to_Estimation.setBill_User_ID(0);
			to_Estimation.setDropShip_BPartner_ID(0);
			to_Estimation.setDropShip_Location_ID(0);
			to_Estimation.setDropShip_User_ID(0);
		}
		to_Estimation.setJP_Contract_ID(p_JP_Contract_ID);
		to_Estimation.saveEx(get_TrxName());

		//
		int no = to_Estimation.copyLinesFrom (from_Estimation, false, false);		//	no Attributes
		if (log.isLoggable(Level.FINE)) log.fine("copy Lines -> #" + no);
		addBufferLog(0, null, null, Msg.getElement(getCtx(), "DocumentNo") + " : "+  to_Estimation.getDocumentNo(), MEstimation.Table_ID, to_Estimation.get_ID());
		//
		return to_Estimation.getDocumentInfo();

	}	//	doIt

	protected void setWarehouseOfContractContent(MEstimation from, MEstimation to) throws Exception
	{
		if(from.getM_Warehouse() != null && from.getM_Warehouse().getAD_Org_ID() == to.getAD_Org_ID())
		{
			to.setM_Warehouse_ID(from.getM_Warehouse_ID());

		}else{

			if(MOrgInfo.get(null, to.getAD_Org_ID(),get_TrxName()).getM_Warehouse_ID() != 0)
			{
				to.setM_Warehouse_ID(MOrgInfo.get(null, to.getAD_Org_ID(),get_TrxName()).getM_Warehouse_ID());

			}else{

				MWarehouse[] warehouses =  MWarehouse.getForOrg(getCtx(), from.getAD_Org_ID());
				if(warehouses.length > 0)
				{
					to.setM_Warehouse_ID(warehouses[0].getM_Warehouse_ID());

				}else {

					throw new Exception(Msg.getMsg(Env.getCtx(),"NotFound") + Msg.getElement(getCtx(), "M_Warehouse_ID") + " : "
											+ Msg.getElement(getCtx(), "CopyFrom") + " : " + Msg.getElement(getCtx(), "JP_ContractContent_ID") + "_"+  from.getDocumentNo());

				}

			}

		}
	}
}
