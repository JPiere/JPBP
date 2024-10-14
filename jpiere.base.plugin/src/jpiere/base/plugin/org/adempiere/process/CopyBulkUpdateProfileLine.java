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

import org.compiere.model.MProcessPara;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MBulkUpdateProfile;
import jpiere.base.plugin.org.adempiere.model.MBulkUpdateProfileLine;

/**
 * JPIERE-0623 Copy Bulk Update Profile Line 
 * 
 *  @author Hideaki Hagiwara
 *
 */
public class CopyBulkUpdateProfileLine extends SvrProcess {

	private int p_JP_BulkUpdateProfileFrom_ID = 0;
	private int p_JP_BulkUpdateProfileTo_ID = 0;
	private MBulkUpdateProfile m_BulkUpdateProfileFrom = null;
	private MBulkUpdateProfile m_BulkUpdateProfileTo = null;
	private MBulkUpdateProfileLine[] fromLines = null;
	
	@Override
	protected void prepare() 
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals(MBulkUpdateProfile.COLUMNNAME_JP_BulkUpdateProfile_ID))
				p_JP_BulkUpdateProfileFrom_ID = para[i].getParameterAsInt();
			else
				MProcessPara.validateUnknownParameter(getProcessInfo().getAD_Process_ID(), para[i]);
		}
		
		p_JP_BulkUpdateProfileTo_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception 
	{
		if(p_JP_BulkUpdateProfileFrom_ID == 0)
			throw new Exception(Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), MBulkUpdateProfile.COLUMNNAME_JP_BulkUpdateProfile_ID));
		
		m_BulkUpdateProfileFrom = new MBulkUpdateProfile(getCtx(), p_JP_BulkUpdateProfileFrom_ID, get_TrxName());
		m_BulkUpdateProfileTo = new MBulkUpdateProfile(getCtx(), p_JP_BulkUpdateProfileTo_ID, get_TrxName());
		if(m_BulkUpdateProfileTo.getAD_Client_ID() != getAD_Client_ID())
			throw new Exception(Msg.getMsg(getCtx(), "JP_Diff_Tenant") + " : " + m_BulkUpdateProfileTo.getName()) ;//Tenants are different
		
		fromLines =  m_BulkUpdateProfileFrom.getLines();
		for(MBulkUpdateProfileLine fromLine : fromLines)
		{
			MBulkUpdateProfileLine toLine = new MBulkUpdateProfileLine(getCtx(), 0, get_TrxName());
			PO.copyValues(fromLine, toLine);
			toLine.setJP_BulkUpdateProfile_ID(p_JP_BulkUpdateProfileTo_ID);
			toLine.setAD_Org_ID(m_BulkUpdateProfileFrom.getAD_Org_ID());
			toLine.setLine(fromLine.getLine());
			toLine.setJP_BulkUpdateType(fromLine.getJP_BulkUpdateType());
			toLine.setAD_Table_ID(fromLine.getAD_Table_ID());
			
			if(MBulkUpdateProfileLine.JP_BULKUPDATETYPE_SQLUPDATE.equals(fromLine.getJP_BulkUpdateType()))
			{
				toLine.setAD_Column_ID(0);
				toLine.setIsChangeLog(false);
				toLine.setJP_UpdateSetClause(fromLine.getJP_UpdateSetClause());
			}else {
				toLine.setAD_Column_ID(fromLine.getAD_Column_ID());
				toLine.setIsChangeLog(fromLine.isChangeLog());
				toLine.setJP_UpdateSetClause(null);
			}
			
			toLine.setDescription(fromLine.getDescription());
			toLine.setHelp(fromLine.getHelp());
			
			if(MBulkUpdateProfileLine.JP_BULKUPDATETYPE_ReplaceString.equals(fromLine.getJP_BulkUpdateType())
					|| MBulkUpdateProfileLine.JP_BULKUPDATETYPE_ReplaceStringRegex.equals(fromLine.getJP_BulkUpdateType()))
			{
				toLine.setJP_TargetString(fromLine.getJP_TargetString());
				toLine.setJP_ReplacementString(fromLine.getJP_ReplacementString());
			}else {
				toLine.setJP_TargetString(null);
				toLine.setJP_ReplacementString(null);
			}
			
			if(MBulkUpdateProfileLine.JP_BULKUPDATETYPE_MaskingExcludeFirstSomeCharacters.equals(fromLine.getJP_BulkUpdateType())
					|| MBulkUpdateProfileLine.JP_BULKUPDATETYPE_MaskingExcludeLastSomeCharacters.equals(fromLine.getJP_BulkUpdateType())
					|| MBulkUpdateProfileLine.JP_BULKUPDATETYPE_MaskingExcludeFirstLastSomeCharacters.equals(fromLine.getJP_BulkUpdateType()))
			{
				toLine.setJP_MaskingString(fromLine.getJP_MaskingString());
				toLine.setJP_MaskingType(fromLine.getJP_MaskingType());
				toLine.setJP_NumOfCharExcludeMasking(fromLine.getJP_NumOfCharExcludeMasking());
			}else {
				
				toLine.setJP_MaskingString(null);
				toLine.setJP_MaskingType(null);
				toLine.setJP_NumOfCharExcludeMasking(0);
			}

			toLine.setWhereClause(fromLine.getWhereClause());
			toLine.setJP_BulkUpdateCommitType(fromLine.getJP_BulkUpdateCommitType());

			toLine.saveEx(get_TrxName());
		}
		
		return Msg.getMsg(getCtx(), "RowCount", new Object[] {fromLines.length} );
	}

}
