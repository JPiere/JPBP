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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.util.CCache;
import org.compiere.util.DB;
import org.compiere.util.Msg;


/** JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class MContractProcessRef extends X_JP_ContractProcessRef {

	private static final long serialVersionUID = 5426659875990092715L;


	public MContractProcessRef(Properties ctx, int JP_ContractProcessRef_ID, String trxName)
	{
		super(ctx, JP_ContractProcessRef_ID, trxName);
	}

	public MContractProcessRef(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		if(newRecord || is_ValueChanged("DocBaseType"))
		{
			if(getDocBaseType().equals(MContractProcessRef.DOCBASETYPE_SalesOrder))
			{
				setIsSOTrx(true);
				setIsCreateBaseDocJP(true);
			}else if(getDocBaseType().equals(MContractProcessRef.DOCBASETYPE_MaterialDelivery)){
				setIsSOTrx(true);
				setIsCreateBaseDocJP(false);
			}else if(getDocBaseType().equals(MContractProcessRef.DOCBASETYPE_ARInvoice)){
				setIsSOTrx(true);
			}else if(getDocBaseType().equals(MContractProcessRef.DOCBASETYPE_PurchaseOrder)){
				setIsSOTrx(false);
				setIsCreateBaseDocJP(true);
			}else if(getDocBaseType().equals(MContractProcessRef.DOCBASETYPE_MaterialReceipt)){
				setIsSOTrx(false);
				setIsCreateBaseDocJP(false);
			}else if(getDocBaseType().equals(MContractProcessRef.DOCBASETYPE_APInvoice)){
				setIsSOTrx(false);
			}else{

				log.saveError("Error", Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "DocBaseType") );
				return false;

			}
		}


		return true;
	}

	/**	Cache				*/
	private static CCache<Integer,MContractProcessRef>	s_cache = new CCache<Integer,MContractProcessRef>(Table_Name, 20);


	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_ContractProcessRef_ID id
	 *	@return Contract Process Reference
	 */
	public static MContractProcessRef get (Properties ctx, int JP_ContractProcessRef_ID)
	{
		Integer ii = Integer.valueOf(JP_ContractProcessRef_ID);
		MContractProcessRef retValue = (MContractProcessRef)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MContractProcessRef (ctx, JP_ContractProcessRef_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_ContractProcessRef_ID, retValue);
		return retValue;
	}	//	get


	private MContractProcessList[]		m_ContractProcessLists = null;

	/**
	 * Get Contract Process List
	 *
	 * @param ctx
	 * @param requery
	 * @param trxName
	 * @return
	 */
	public MContractProcessList[] getContractProcessList(Properties ctx, boolean requery, String trxName)
	{
		if (m_ContractProcessLists != null && m_ContractProcessLists.length >= 0 && !requery)	//	re-load
			return m_ContractProcessLists;
		//
		ArrayList<MContractProcessList> list = new ArrayList<MContractProcessList>();
		String sql = "SELECT * FROM JP_ContractProcessList WHERE JP_ContractProcessRef_ID=? AND IsActive='Y'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, trxName);
			pstmt.setInt(1, getJP_ContractProcessRef_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MContractProcessList (ctx, rs, trxName));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		m_ContractProcessLists = new MContractProcessList[list.size()];
		list.toArray(m_ContractProcessLists);
		return m_ContractProcessLists;
	}



}
