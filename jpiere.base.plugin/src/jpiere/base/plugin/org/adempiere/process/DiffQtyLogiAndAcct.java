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

import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;

/**
 * JPIERE-0164:Check List of Difference Qty between Logistics and Acct
 * 
 * @author Hideaki Hagiwara
 *
 */
public class DiffQtyLogiAndAcct extends SvrProcess {
	
	private int			p_AD_Org_ID = 0;
	private int			p_M_Warehouse_ID = 0;
	private int			p_M_Product_ID = 0;
	private Timestamp  	p_DateValue = null;
	
	@Override
	protected void prepare() 
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("AD_Org_ID"))
				p_AD_Org_ID = para[i].getParameterAsInt();
			else if (name.equals("M_Warehouse_ID"))
				p_M_Warehouse_ID  = para[i].getParameterAsInt();
			else if (name.equals("M_Product_ID"))
				p_M_Product_ID  = para[i].getParameterAsInt();
			else if (name.equals("DateValue"))
				p_DateValue = para[i].getParameterAsTimestamp();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}//for
	}
	
	@Override
	protected String doIt() throws Exception
	{
		String sql = CreateSQL(true);
		DB.executeUpdateEx(sql.toString(), get_TrxName());
		
		sql = CreateSQL(false);
		DB.executeUpdateEx(sql.toString(), get_TrxName());
		
		return "";
	}

	private String CreateSQL(boolean IsFutureDateAcct)
	{		 
		//YYYY-MM-DD HH24:MI:SS.mmmm  JDBC Timestamp format
		StringBuilder DateValue = new StringBuilder(p_DateValue.toString());
		StringBuilder DateValue_00 = new StringBuilder("TO_DATE('").append(DateValue.substring(0,10)).append(" 00:00:00','YYYY-MM-DD HH24:MI:SS')");
		StringBuilder DateValue_24 = new StringBuilder("TO_DATE('").append(DateValue.substring(0,10)).append(" 24:00:00','YYYY-MM-DD HH24:MI:SS')");
		
		StringBuilder sql = new StringBuilder ("INSERT INTO T_InOutTransactionJP ( ")
			.append("AD_Pinstance_ID")
			.append(",AD_Client_ID")
			.append(",AD_Org_ID")
			.append(",M_InOut_ID")
			.append(",DocumentNo")
			.append(",C_DocType_ID")
			.append(",DateAcct")
			.append(",SalesRep_ID")
			.append(",C_BPartner_ID")
			.append(",M_Warehouse_ID")
			.append(",DocStatus")
			.append(",posted")
			.append(",Description")
			.append(",M_InOutLine_ID")
			.append(",line")
			.append(",QtyEntered")
			.append(",C_UOM_ID")
			.append(",ConfirmedQty")
			.append(",PickedQty")
			.append(",ScrappedQty")
			.append(",TargetQty")
			.append(",M_InOutLine_Description")
			.append(",M_Transaction_ID")
			.append(",MovementType")
			.append(",M_Locator_ID")
			.append(",M_Product_ID")
			.append(",M_AttributeSetInstance_ID")
			.append(",MovementDate")
			.append(",MovementQty")
			.append(",DocbaseType")
			.append(",DateValue")
			.append(",JP_AdjustToAcctQty")
			.append(")");
		
		sql.append(" SELECT ")
			.append(getAD_PInstance_ID())
			.append(",AD_Client_ID")
			.append(",AD_Org_ID")
			.append(",M_InOut_ID")
			.append(",DocumentNo")
			.append(",C_DocType_ID")
			.append(",DateAcct")
			.append(",SalesRep_ID")
			.append(",C_BPartner_ID")
			.append(",M_Warehouse_ID")
			.append(",DocStatus")
			.append(",posted")
			.append(",Description")
			.append(",M_InOutLine_ID")
			.append(",line")
			.append(",QtyEntered")
			.append(",C_UOM_ID")
			.append(",ConfirmedQty")
			.append(",PickedQty")
			.append(",ScrappedQty")
			.append(",TargetQty")
			.append(",M_InOutLine_Description")
			.append(",M_Transaction_ID")
			.append(",MovementType")
			.append(",M_Locator_ID")
			.append(",M_Product_ID")
			.append(",M_AttributeSetInstance_ID")
			.append(",MovementDate")
			.append(",MovementQty")
			.append(",DocbaseType")
			.append("," + DateValue_00);
		
		if(IsFutureDateAcct)
			sql.append(",MovementQty*-1");//JP_AdjustToAcctQty
		else
			sql.append(",MovementQty");
			
		sql.append(" FROM JP_InOutTransaction ")
			.append("WHERE AD_Client_ID = " + getAD_Client_ID() + " AND DocStatus IN ('CO', 'CL')");
		
		if(IsFutureDateAcct)
		{
			sql.append(" AND MovementDate < " + DateValue_24)
				.append(" AND DateAcct >= " + DateValue_24);
		}else{
			sql.append(" AND DateAcct < " + DateValue_24)
				.append(" AND MovementDate >= " + DateValue_24);		
		}
		
		if(p_AD_Org_ID != 0)
			sql.append(" AND AD_Org_ID = " + p_AD_Org_ID);
		
		if(p_M_Warehouse_ID != 0)
			sql.append(" AND M_Warehouse_ID = " + p_M_Warehouse_ID);
		if(p_M_Product_ID != 0)
			sql.append(" AND M_Product_ID = " + p_M_Product_ID);
		
		return sql.toString();
	}
}
