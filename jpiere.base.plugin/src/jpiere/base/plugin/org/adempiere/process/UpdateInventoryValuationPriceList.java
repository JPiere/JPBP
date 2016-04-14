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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Level;

import jpiere.base.plugin.org.adempiere.model.MInvValProfile;
import jpiere.base.plugin.org.adempiere.model.MInvValProfileOrg;
import jpiere.base.plugin.util.JPiereInvValUtil;

import org.compiere.model.I_M_PriceList_Version;
import org.compiere.model.MCost;
import org.compiere.model.MCostElement;
import org.compiere.model.MPriceList;
import org.compiere.model.MPriceListVersion;
import org.compiere.model.MProductPrice;
import org.compiere.model.MRefList;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Msg;

/**
 * JPIERE-0169: Update Inventory Valuation Price List
 * 
 * @author Hideaki Hagiwara
 *
 */
public class UpdateInventoryValuationPriceList extends SvrProcess {
	
	private int 		p_AD_Client_ID = 0;
	private int			p_JP_InvValProfile_ID = 0;
	private Timestamp	p_DateValue = null;
	
	@Override
	protected void prepare() 
	{
		p_AD_Client_ID =getProcessInfo().getAD_Client_ID();
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("JP_InvValProfile_ID")){
				p_JP_InvValProfile_ID = para[i].getParameterAsInt();
			}else if (name.equals("DateValue")){
				p_DateValue = para[i].getParameterAsTimestamp();
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if

		}//for
	}
	
	@Override
	protected String doIt() throws Exception {
		
		if(p_JP_InvValProfile_ID == 0 || p_DateValue==null)
			return Msg.getMsg(getCtx(), "ParameterMissing");//Error:  Parameter missing
		
		MInvValProfile ivp = new MInvValProfile(getCtx(), p_JP_InvValProfile_ID, null);
		if(ivp.getCostingLevel().equals(MInvValProfile.COSTINGLEVEL_BatchLot))
			return Msg.getMsg(getCtx(), "CopyError") + MRefList.getListName(getCtx(), MInvValProfile.COSTINGLEVEL_AD_Reference_ID, MInvValProfile.COSTINGLEVEL_BatchLot);
		
		String DateValueString = new String(p_DateValue.toString().substring(0,10));
		int M_PriceListVersion_ID = 0;
		String returnName = null;
		
		if(ivp.getCostingLevel().equals(MInvValProfile.COSTINGLEVEL_Client))
		{
			if(ivp.getM_PriceList_ID()==0)
				return Msg.getMsg(getCtx(), "ParameterMissing") + Msg.getElement(getCtx(), "M_PriceList_ID");//Error:  Parameter missing
			
			if(ivp.getM_DiscountSchema_ID()==0)
				return Msg.getMsg(getCtx(), "ParameterMissing") + Msg.getElement(getCtx(), "M_DiscountSchema_ID");//Error:  Parameter missing
			
			MPriceList pl = new MPriceList(getCtx(),ivp.getM_PriceList_ID(), get_TrxName());
			MPriceListVersion version =getPriceListVersion(pl.getM_PriceList_ID(), p_DateValue);
			if(version ==null)
			{
				version = new MPriceListVersion(pl);
				version.setName(ivp.getName()+" : "+DateValueString);
				version.setValidFrom(p_DateValue);
				version.setM_DiscountSchema_ID(ivp.getM_DiscountSchema_ID());
				version.saveEx(get_TrxName());
			}else{
				StringBuilder sqlDelete = new StringBuilder ("DELETE M_ProductPrice ")
				.append(" WHERE M_PriceList_Version_ID=").append(version.getM_PriceList_Version_ID())
				.append(" AND AD_Client_ID=").append(p_AD_Client_ID);
				DB.executeUpdateEx(sqlDelete.toString(), get_TrxName());
			}
			

			createProductPrice(ivp, 0, version);
			M_PriceListVersion_ID = version.getM_PriceList_Version_ID();
			returnName = pl.getName() + "-" +version.getName();
			addBufferLog(0, null, null, returnName, MPriceListVersion.Table_ID, M_PriceListVersion_ID);
		
		}else if(ivp.getCostingLevel().equals(MInvValProfile.COSTINGLEVEL_Organization)){
			MInvValProfileOrg[] orgs = ivp.getOrgs();
			for(int i = 0; i < orgs.length; i++)
			{
				if(orgs[i].getM_PriceList_ID()==0)
					continue;
				
				if(orgs[i].getM_DiscountSchema_ID()==0)
					continue;
				
				MPriceList pl = new MPriceList(getCtx(),ivp.getM_PriceList_ID(), get_TrxName());
				MPriceListVersion version =pl.getPriceListVersion(p_DateValue);
				if(version ==null)
				{
					version = new MPriceListVersion(pl);
					version.setName(ivp.getName()+" : "+DateValueString);
					version.setValidFrom(p_DateValue);
					version.setM_DiscountSchema_ID(ivp.getM_DiscountSchema_ID());
					version.saveEx(get_TrxName());
				}else{
					StringBuilder sqlDelete = new StringBuilder ("DELETE M_ProductPrice ")
					.append(" WHERE M_PriceList_Version_ID=").append(version.getM_PriceList_Version_ID())
					.append(" AND AD_Client_ID=").append(p_AD_Client_ID);
					DB.executeUpdateEx(sqlDelete.toString(), get_TrxName());
				}
				
				 createProductPrice(ivp, orgs[i].getAD_Org_ID(), version);
				 M_PriceListVersion_ID = version.getM_PriceList_Version_ID();
				 returnName = pl.getName() + "-" +version.getName();
				 addBufferLog(0, null, null, returnName, MPriceListVersion.Table_ID, M_PriceListVersion_ID);
			}//for i
		}
		
		return Msg.getMsg(getCtx(), "ProcessOK");
	}
	
	private void createProductPrice(MInvValProfile ivp, int AD_Org_ID, MPriceListVersion version) throws Exception
	{
		int M_Product_ID = 0;
		int C_AcctSchema_ID = ivp.getC_AcctSchema_ID();
		int M_CostType_ID = ivp.getC_AcctSchema().getM_CostType_ID();
		int M_CostElement_ID = 0;
		MCostElement[] CostElements = JPiereInvValUtil.getMaterialStandardCostElements(getCtx());
		if(CostElements.length > 0)
			M_CostElement_ID = CostElements[0].getM_CostElement_ID();
		
		MProductPrice pp = null;
		MCost cost = null;
		StringBuilder sql = new StringBuilder("SELECT DISTINCT s.M_Product_ID, p.Value ")
								.append("FROM JP_StockOrg s INNER JOIN M_Product p ON(p.M_Product_ID = s.M_Product_ID) ")
								.append(" WHERE s.DateValue=? Order by p.Value");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), get_TrxName());
			pstmt.setTimestamp(1, p_DateValue);
			rs = pstmt.executeQuery ();
			while (rs.next ())
			{
				M_Product_ID = rs.getInt(1);
				cost =MCost.get(getCtx(), p_AD_Client_ID, AD_Org_ID, M_Product_ID, M_CostType_ID, C_AcctSchema_ID, M_CostElement_ID, 0, get_TrxName());
				if(cost == null)
					continue;
				
				pp = new MProductPrice(getCtx(), version.getM_PriceList_Version_ID(), M_Product_ID, get_TrxName());
				pp.setAD_Org_ID(version.getAD_Org_ID());
				pp.setPriceList(cost.getFutureCostPrice());
				pp.setPriceStd(cost.getCurrentCostPrice());
				pp.saveEx(get_TrxName());
			}

		}
		catch (Exception e)
		{
				throw e;
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
	
	}
	
	private MPriceListVersion getPriceListVersion (int M_PriceList_ID, Timestamp valid)
	{
		if (valid == null)
			valid = new Timestamp (System.currentTimeMillis());

		final String whereClause = "M_PriceList_ID=? AND TRUNC(ValidFrom)=?";
		MPriceListVersion m_plv = new Query(getCtx(), I_M_PriceList_Version.Table_Name, whereClause, get_TrxName())
					.setParameters(M_PriceList_ID, valid)
					.setOnlyActiveRecords(true)
					.first();

		return m_plv;
	}	//	getPriceListVersion
}
