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

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.compiere.model.I_C_BPartner;
import org.compiere.model.MBPartner;
import org.compiere.model.Query;
import org.compiere.util.CCache;
import org.compiere.util.Util;


/**
 *  JPIERE-0024 : Corporation Model.
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class MCorporation extends X_JP_Corporation {

	public MCorporation(Properties ctx, int JP_Corporation_ID, String trxName) {
		super(ctx, JP_Corporation_ID, trxName);
	}

	public MCorporation(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	/**	Categopry Cache				*/
	private static CCache<Integer,MCorporation>	s_cache = new CCache<Integer,MCorporation>(Table_Name, 20);

	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_Corporation_ID id
	 *	@return Corporation
	 */
	public static MCorporation get (Properties ctx, int JP_Corporation_ID)
	{
		Integer ii = Integer.valueOf(JP_Corporation_ID);
		MCorporation retValue = (MCorporation)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MCorporation (ctx, JP_Corporation_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_Corporation_ID, retValue);
		return retValue;
	}	//	get


	private MBPartner[] m_BPartners = null;

	public MBPartner[] getBPartners (String whereClause, String orderClause)
	{
		StringBuilder whereClauseFinal = new StringBuilder(" JP_Corporation_ID=? ");
		if(!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if(orderClause.length()==0)
			orderClause += "Value" ;

		List<MBPartner> list = new Query(getCtx(), I_C_BPartner.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.list();

		return list.toArray(new MBPartner[list.size()]);
	}

	public MBPartner[] getBPartners (boolean requery, String orderBy)
	{
		if(m_BPartners != null && !requery)
			return m_BPartners;

		String orderClause = "";
		if(orderBy != null && orderBy.length()>0)
			orderClause += orderBy;
		else
			orderClause += "Value" ;

		m_BPartners = getBPartners (null,orderClause);
		return m_BPartners;
	}

	public MBPartner[] getBPartners ()
	{
		return getBPartners (false, null);
	}



	private MCorporationGroup[] m_CorporationGroups = null;

	public MCorporationGroup[] getCorporationGroups (boolean requery)
	{

		if(m_CorporationGroups != null && !requery)
			return m_CorporationGroups;


		StringBuilder whereClause = new StringBuilder(" JP_Corporation_ID=? ");
		//
		List<MGroupCorporations> list = new Query(getCtx(), I_JP_GroupCorporations.Table_Name, whereClause.toString(), get_TrxName())
										.setParameters(get_ID())
										.list();

		ArrayList<MCorporationGroup> CorporationGroupList = new ArrayList<MCorporationGroup>();

		for(MGroupCorporations gc :list)
		{
			CorporationGroupList.add(gc.getParent());
		}

		m_CorporationGroups = CorporationGroupList.toArray(new MCorporationGroup[CorporationGroupList.size()]);

		return m_CorporationGroups ;
	}


	public MCorporationGroup[] getCorporationGroups ()
	{
		return 	getCorporationGroups (false);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {

		return super.beforeSave(newRecord);
	}

	@Override
	protected boolean afterSave(boolean newRecord, boolean success) {

		return super.afterSave(newRecord, success);
	}



}
