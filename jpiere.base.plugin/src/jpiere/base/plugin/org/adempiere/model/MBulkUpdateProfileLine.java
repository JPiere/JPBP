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
import java.util.Properties;

import org.compiere.model.MColumn;
import org.compiere.model.MTable;
import org.compiere.util.Msg;
import org.compiere.util.Util;

/**
 * JPIERE-0621 Bulk Update Profile
 *
 *  @author Hideaki Hagiwara
 *
 */
public class MBulkUpdateProfileLine extends X_JP_BulkUpdateProfileLine {

	public MBulkUpdateProfileLine(Properties ctx, int JP_BulkUpdateProfileLine_ID, String trxName) 
	{
		super(ctx, JP_BulkUpdateProfileLine_ID, trxName);
	}

	public MBulkUpdateProfileLine(Properties ctx, int JP_BulkUpdateProfileLine_ID, String trxName, String... virtualColumns)
	{
		super(ctx, JP_BulkUpdateProfileLine_ID, trxName, virtualColumns);
	}

	public MBulkUpdateProfileLine(Properties ctx, String JP_BulkUpdateProfileLine_UU, String trxName) 
	{
		super(ctx, JP_BulkUpdateProfileLine_UU, trxName);
	}

	public MBulkUpdateProfileLine(Properties ctx, String JP_BulkUpdateProfileLine_UU, String trxName, String... virtualColumns) 
	{
		super(ctx, JP_BulkUpdateProfileLine_UU, trxName, virtualColumns);
	}

	public MBulkUpdateProfileLine(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}
	
	@Override
	protected boolean beforeSave(boolean newRecord) 
	{
		MTable m_Table = MTable.get(getAD_Table_ID());
		if(m_Table.isView())
		{
			log.saveError("Error", m_Table.getTableName() + " - " + Msg.getElement(getCtx(), "IsView"));
			return false; 
		}
		
		
		if(JP_BULKUPDATETYPE_SQLUPDATE.equals(getJP_BulkUpdateType()))
		{
			 if(isChangeLog()) 
			 {
				 setIsChangeLog(false);
			 }
			
			if(Util.isEmpty(getJP_UpdateSetClause()))
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), COLUMNNAME_JP_UpdateSetClause));
				return false; 
			}
				
			setAD_Column_ID(0);
			setJP_TargetString(null);
			setJP_ReplacementString(null);		
			setJP_MaskingString(null);
			setJP_MaskingType(null);
			setJP_NumOfCharExcludeMasking(0);
				
		}else if(JP_BULKUPDATETYPE_ReplaceString.equals(getJP_BulkUpdateType())) {
			
			if(getAD_Column_ID()==0)
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), COLUMNNAME_AD_Column_ID));
				return false; 
			}
			
			MColumn m_Column = MColumn.get(getAD_Column_ID());
			if(!Util.isEmpty(m_Column.getColumnSQL()))
			{
				log.saveError("Error", Msg.getElement(getCtx(), "ColumnSQL"));
				return false; 
			}
			
			if(m_Column.isSecure())
				setIsChangeLog(false);
			
			setJP_MaskingString(null);
			setJP_MaskingType(null);
			setJP_NumOfCharExcludeMasking(0);
			setJP_UpdateSetClause(null);
			
		}else if(JP_BULKUPDATETYPE_ReplaceStringRegex.equals(getJP_BulkUpdateType())) {
			
			if(Util.isEmpty(getJP_TargetString()))
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), COLUMNNAME_JP_TargetString));
				return false; 
			}
			
			if(getAD_Column_ID()==0)
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), COLUMNNAME_AD_Column_ID));
				return false; 
			}
			
			MColumn m_Column = MColumn.get(getAD_Column_ID());
			if(!Util.isEmpty(m_Column.getColumnSQL()))
			{
				log.saveError("Error", Msg.getElement(getCtx(), "ColumnSQL"));
				return false; 
			}
			
			if(m_Column.isSecure())
				setIsChangeLog(false);
			
			setJP_MaskingString(null);
			setJP_MaskingType(null);
			setJP_NumOfCharExcludeMasking(0);
			setJP_UpdateSetClause(null);
			
		}else if(JP_BULKUPDATETYPE_MaskingExcludeFirstSomeCharacters.equals(getJP_BulkUpdateType())
				|| JP_BULKUPDATETYPE_MaskingExcludeLastSomeCharacters.equals(getJP_BulkUpdateType())
				|| JP_BULKUPDATETYPE_MaskingExcludeFirstLastSomeCharacters.equals(getJP_BulkUpdateType()) ) {
			
			if(getAD_Column_ID()==0)
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), COLUMNNAME_AD_Column_ID));
				return false; 
			}
			
			MColumn m_Column = MColumn.get(getAD_Column_ID());
			if(!Util.isEmpty(m_Column.getColumnSQL()))
			{
				log.saveError("Error", Msg.getElement(getCtx(), "ColumnSQL"));
				return false; 
			}
			
			if(m_Column.isSecure())
				setIsChangeLog(false);
			
			if(Util.isEmpty(getJP_MaskingString()))
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), COLUMNNAME_JP_MaskingString));
				return false; 
			}
			
			if(Util.isEmpty(getJP_MaskingType()))
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), COLUMNNAME_JP_MaskingType));
				return false; 
			}
			
			setJP_TargetString(null);
			setJP_ReplacementString(null);
			setJP_UpdateSetClause(null);
			
		}else {
			;
		}
	
		return true;
	}

}
