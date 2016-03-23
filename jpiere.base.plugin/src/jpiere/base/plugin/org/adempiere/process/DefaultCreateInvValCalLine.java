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

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.logging.Level;

import jpiere.base.plugin.org.adempiere.model.MInvValCal;
import jpiere.base.plugin.org.adempiere.model.MInvValCalLine;
import jpiere.base.plugin.org.adempiere.model.MInvValProfile;
import jpiere.base.plugin.util.JPiereInvValUtil;

import org.compiere.model.MProduct;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 * JPIERE-0161 Inventory Valuation Calculate Doc
 *
 *
 *  @author Hideaki Hagiwara
 *
 */
public class DefaultCreateInvValCalLine extends SvrProcess {

	MInvValProfile m_InvValProfile = null;
	MInvValCal m_InvValCal = null;
	int Record_ID = 0;

	@Override
	protected void prepare()
	{
		Record_ID = getRecord_ID();
		if(Record_ID > 0)
		{
			m_InvValCal = new MInvValCal(getCtx(), Record_ID, null);
			m_InvValProfile = new MInvValProfile(getCtx(), m_InvValCal.getJP_InvValProfile_ID(), null);
		}else{
			log.log(Level.SEVERE, "Record_ID <= 0 ");
		}
	}

	@Override
	protected String doIt() throws Exception
	{
		StringBuilder sqlDelete = new StringBuilder ("DELETE JP_InvValCalLine ")
											.append(" WHERE JP_InvValCal_ID=").append(m_InvValCal.getJP_InvValCal_ID());
		int deleteNo = DB.executeUpdateEx(sqlDelete.toString(), get_TrxName());

		LinkedHashMap<Integer, BigDecimal> map_Product_Qty = JPiereInvValUtil.getAllQtyBookFromStockOrg(getCtx(), m_InvValCal.getDateValue()
				, m_InvValProfile.getOrgs(), " p.M_Product_Category_ID, p.Value");
		Set<Integer> set_M_Product_IDs = map_Product_Qty.keySet();
		int line = 0;
		for(Integer M_Product_ID :set_M_Product_IDs)
		{
			MProduct product = MProduct.get(getCtx(), M_Product_ID);
			if(product.getM_Product_ID()==0 || !product.getProductType().equals(MProduct.PRODUCTTYPE_Item) || !product.isStocked())
				continue;

			BigDecimal QtyBook =map_Product_Qty.get(M_Product_ID);
			if(QtyBook.compareTo(Env.ZERO)==0)
			{
				if(!m_InvValProfile.isZeroStockInvValJP())
					continue;
			}

			MInvValCalLine ivcLine = new MInvValCalLine(m_InvValCal);
			line++;
			ivcLine.setLine(line*10);
			ivcLine.setM_Product_ID(M_Product_ID.intValue());
			ivcLine.setQtyBook(QtyBook);
			ivcLine.setC_AcctSchema_ID(m_InvValProfile.getC_AcctSchema_ID());
			ivcLine.setCostingMethod(m_InvValProfile.getCostingMethod());
			ivcLine.setCostingLevel(m_InvValProfile.getCostingLevel());
			ivcLine.saveEx(get_TrxName());

		}


		int insertedNo = line;
		String deleted = Msg.getMsg(getCtx(), "Deleted");
		String inserted = Msg.getMsg(getCtx(), "Inserted");
		String retVal = null;
		if(deleteNo == 0)
			retVal = inserted + " : " + insertedNo;
		else
			retVal = deleted + " : " + deleteNo + " / " +inserted + " : " + insertedNo;
		addLog(retVal);

		return retVal;
	}

}
