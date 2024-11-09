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
import java.util.List;
import java.util.Properties;

import org.compiere.model.MColumn;
import org.compiere.model.MRefList;
import org.compiere.model.Query;
import org.compiere.util.Msg;
import org.compiere.util.Util;

/**
 * JPIERE-0628 Generic Export Format
 *
 *  @author Hideaki Hagiwara
 *
 */
public class MGenericExpFormatRow extends X_JP_GenericExpFormat_Row {

	public MGenericExpFormatRow(Properties ctx, int JP_GenericExpFormat_Row_ID, String trxName) 
	{
		super(ctx, JP_GenericExpFormat_Row_ID, trxName);
	}

	public MGenericExpFormatRow(Properties ctx, int JP_GenericExpFormat_Row_ID, String trxName, String... virtualColumns) 
	{
		super(ctx, JP_GenericExpFormat_Row_ID, trxName, virtualColumns);
	}

	public MGenericExpFormatRow(Properties ctx, String JP_GenericExpFormat_Row_UU, String trxName) 
	{
		super(ctx, JP_GenericExpFormat_Row_UU, trxName);
	}

	public MGenericExpFormatRow(Properties ctx, String JP_GenericExpFormat_Row_UU, String trxName, String... virtualColumns) 
	{
		super(ctx, JP_GenericExpFormat_Row_UU, trxName, virtualColumns);
	}

	public MGenericExpFormatRow(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	MGenericExpFormatReplace[] m_Replaces = null;
	
	public MGenericExpFormatReplace[] getReplaces (boolean requery)
	{
		if(!requery && m_Replaces != null)
			return m_Replaces;
		
		StringBuilder whereClauseFinal = new StringBuilder(MGenericExpFormatReplace.COLUMNNAME_JP_GenericExpFormat_Row_ID+"=? AND IsActive='Y'");
		String 	orderClause = MGenericExpFormatReplace.COLUMNNAME_SeqNo;
		
		List<MGenericExpFormatReplace> list = new Query(getCtx(), MGenericExpFormatReplace.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();
		m_Replaces = list.toArray(new MGenericExpFormatReplace[list.size()]);
		
		return m_Replaces;
		
	}	//	getReplaces

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		if(getFieldLength() < 0)
		{
			log.saveError("Error", Msg.getElement(p_ctx, COLUMNNAME_FieldLength) +" < 0");
			return false;
		}
		
		
		parent = getParent();
		
		//Check Export Type
		if(JP_EXPORTTYPE_Variable.equals(getJP_ExportType()))
		{
			if(getAD_Column_ID() == 0)
			{
				log.saveError("Error", Msg.getMsg(p_ctx, "FillMandatory") + Msg.getElement(p_ctx, COLUMNNAME_AD_Column_ID));
				return false;
			}
			
			MColumn col = MColumn.get(getAD_Column_ID());
			if(parent.getAD_Table_ID() != col.getAD_Table_ID())
			{
				log.saveError("Error", Msg.getMsg(p_ctx, "JP_Inconsistency", new Object[]{Msg.getElement(p_ctx, "AD_Table_ID"),Msg.getElement(p_ctx, "AD_Column_ID")}) + " : " + col.getColumnName());
				return false;
			}
		
			if(col.isSecure())
			{
				//Can not export the date of Secure content
				log.saveError("Error",Msg.getMsg(getCtx(), "JP_CouldNotExportIsSecure") + " : " + col.getColumnName());
				return false;
			}
			
			setConstantValue(null);
			
		}else if(JP_EXPORTTYPE_Constant.equals(getJP_ExportType())) {
			
			if(Util.isEmpty(getConstantValue()))
			{
				log.saveError("Error", Msg.getMsg(p_ctx, "FillMandatory") + Msg.getElement(p_ctx, COLUMNNAME_ConstantValue));
				return false;
			}
			
			setAD_Column_ID(0);
			setDataFormat(null);
			
		}else if(JP_EXPORTTYPE_Blank.equals(getJP_ExportType())) {
			
			setConstantValue(null);
			setAD_Column_ID(0);
			setDataFormat(null);
			
		}
		
		//Check Format Type
		if(MGenericExpFormat.FORMATTYPE_CommaSeparated.equals(parent.getFormatType()))
		{
			setJP_PaddingType(null);
			setJP_PaddingChar(null);
			
		}else if(MGenericExpFormat.FORMATTYPE_TabSeparated.equals(parent.getFormatType())){
			
			setJP_PaddingType(null);
			setJP_PaddingChar(null);
			
		}else if(MGenericExpFormat.FORMATTYPE_CustomSeparatorChar.equals(parent.getFormatType())){
			
			setJP_PaddingType(null);
			setJP_PaddingChar(null);
			
		}else if(MGenericExpFormat.FORMATTYPE_FixedPosition.equals(parent.getFormatType())){
			
			if(getFieldLength() == 0)
			{
				MColumn col = MColumn.get(p_ctx, MGenericExpFormat.Table_Name, MGenericExpFormat.COLUMNNAME_FormatType);
				String msg = MRefList.getListName(getCtx(), col.getAD_Reference_Value_ID(), MGenericExpFormat.FORMATTYPE_FixedPosition);
						
				log.saveError("Error", msg + " : " +Msg.getElement(p_ctx, COLUMNNAME_FieldLength) +" = 0");
				return false;
			}
			
			if(Util.isEmpty(getJP_PaddingType()))
			{
				log.saveError("Error", Msg.getMsg(p_ctx, "FillMandatory") + Msg.getElement(p_ctx, COLUMNNAME_JP_PaddingType));
				return false;
			}
			
			if(Util.isEmpty(getJP_PaddingChar()))
			{
				log.saveError("Error", Msg.getMsg(p_ctx, "FillMandatory") + Msg.getElement(p_ctx, COLUMNNAME_JP_PaddingChar));
				return false;
			}
			
			setIsEscapSeparatorCharJP(false);
			setIsEncloseWithEnclosingCharaJP(false);
			setIsEscapEnclosingCharJP(false);
		}
		
		return true;
	}
	
	private MGenericExpFormat parent = null;
	
	public MGenericExpFormat getParent()
	{
		if(parent == null)
			parent = new MGenericExpFormat(getCtx(), getJP_GenericExpFormat_ID(), get_TrxName());
			
		return parent;
	}
	
}
