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

import java.util.List;

import org.compiere.model.MColumn;
import org.compiere.model.MTable;
import org.compiere.model.Query;
import org.compiere.process.SvrProcess;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MBulkUpdateProfile;
import jpiere.base.plugin.org.adempiere.model.MBulkUpdateProfileLine;

/**
 * JPIERE-0622 Create Bulk Update Profile Line From Secure content
 * 
 *  @author Hideaki Hagiwara
 *
 */
public class CreateBulkUpdateProfileLineFromSecureContent extends SvrProcess {

	private int p_JP_BulkUpdateProfile_ID = 0;
	private MBulkUpdateProfile m_BulkUpdateProfile = null;
	
	@Override
	protected void prepare() 
	{
		p_JP_BulkUpdateProfile_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception 
	{
		if(p_JP_BulkUpdateProfile_ID == 0)
			throw new Exception(Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), MBulkUpdateProfile.COLUMNNAME_JP_BulkUpdateProfile_ID));
		
		m_BulkUpdateProfile = new MBulkUpdateProfile(getCtx(), p_JP_BulkUpdateProfile_ID, get_TrxName());
		if(m_BulkUpdateProfile.getAD_Client_ID() != getAD_Client_ID())
			throw new Exception(Msg.getMsg(getCtx(), "JP_Diff_Tenant") + " : " + m_BulkUpdateProfile.getName()) ;//Tenants are different
		
		List<MColumn> list = new Query(getCtx(), "AD_Column", "IsSecure='Y'", get_TrxName())
				//.setClient_ID(true)
				.list();
		int lineNo = 0;
		int count = 0;
		for(MColumn m_Column : list)
		{	
			MTable m_Table = MTable.get(m_Column.getAD_Table_ID());
			if(m_Table.isView() || !m_Table.isActive() || !m_Column.isActive())
				continue;
			
			MBulkUpdateProfileLine line = new MBulkUpdateProfileLine(getCtx(), 0, get_TrxName());
			line.setJP_BulkUpdateProfile_ID(p_JP_BulkUpdateProfile_ID);
			line.setAD_Org_ID(m_BulkUpdateProfile.getAD_Org_ID());
			lineNo = lineNo + 10;
			line.setLine(lineNo);
			line.setJP_BulkUpdateType(MBulkUpdateProfileLine.JP_BULKUPDATETYPE_ReplaceString);
			line.setAD_Table_ID(m_Column.getAD_Table_ID());
			line.setAD_Column_ID(m_Column.getAD_Column_ID());
			line.setIsChangeLog(false);			
			line.setDescription(null);
			line.setHelp(null);
			line.setJP_TargetString(null);
			line.setJP_ReplacementString(null);
			line.setJP_MaskingString(null);
			line.setJP_MaskingType(null);
			line.setJP_NumOfCharExcludeMasking(0);		
			line.setJP_UpdateSetClause(null);
			line.setWhereClause(null);
			line.setJP_BulkUpdateCommitType(MBulkUpdateProfileLine.JP_BULKUPDATECOMMITTYPE_Line);

			line.saveEx(get_TrxName());
			count++;
		}
		
		
		return Msg.getMsg(getCtx(), "RowCount", new Object[] {count} );
	}

}
