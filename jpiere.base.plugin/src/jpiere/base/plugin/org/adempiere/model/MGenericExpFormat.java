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

import org.compiere.model.MClient;
import org.compiere.model.MClientInfo;
import org.compiere.model.Query;
import org.compiere.util.Msg;
import org.compiere.util.Util;

/**
 * JPIERE-0628 Generic Export Format
 *
 *  @author Hideaki Hagiwara
 *
 */
public class MGenericExpFormat extends X_JP_GenericExpFormat {

	public MGenericExpFormat(Properties ctx, int JP_GenericExpFormat_ID, String trxName) 
	{
		super(ctx, JP_GenericExpFormat_ID, trxName);
	}

	public MGenericExpFormat(Properties ctx, int JP_GenericExpFormat_ID, String trxName, String... virtualColumns)
	{
		super(ctx, JP_GenericExpFormat_ID, trxName, virtualColumns);
	}

	public MGenericExpFormat(Properties ctx, String JP_GenericExpFormat_UU, String trxName) 
	{
		super(ctx, JP_GenericExpFormat_UU, trxName);
	}

	public MGenericExpFormat(Properties ctx, String JP_GenericExpFormat_UU, String trxName, String... virtualColumns) 
	{
		super(ctx, JP_GenericExpFormat_UU, trxName, virtualColumns);
	}

	public MGenericExpFormat(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}

	public MGenericExpFormatRow[] getRows ()
	{
		StringBuilder whereClauseFinal = new StringBuilder(MGenericExpFormatRow.COLUMNNAME_JP_GenericExpFormat_ID+"=? AND IsActive='Y'");
		String 	orderClause = MGenericExpFormatRow.COLUMNNAME_SeqNo;
		
		List<MGenericExpFormatRow> list = new Query(getCtx(), MGenericExpFormatRow.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();
		
		return list.toArray(new MGenericExpFormatRow[list.size()]);		
	}	//	getRows

	@Override
	protected boolean beforeSave(boolean newRecord) 
	{
		if(FORMATTYPE_FixedPosition.equals(getFormatType()))
		{
			setJP_EnclosingChar(null);
		}
		
		if(FORMATTYPE_CustomSeparatorChar.equals(getFormatType()))
		{
			if(Util.isEmpty(getSeparatorChar()))
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), COLUMNNAME_SeparatorChar));
				return false;
			}
			
		}else {
			setSeparatorChar(null);
		}
		
		if(isAttachmentFileJP() && (newRecord || is_ValueChanged(COLUMNNAME_IsAttachmentFileJP)) )
		{
			MClient m_Client = MClient.get(getAD_Client_ID());
			MClientInfo m_ClientInfo = m_Client.getInfo();
			int JP_StorageAttachment_ID = m_ClientInfo.get_ValueAsInt("JP_StorageAttachment_ID");
			if(JP_StorageAttachment_ID == 0)
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "NotFound") + Msg.getElement(getCtx(), "JP_StorageAttachment_ID"));
				return false;
			}
		}
		
		return true;
	}
	
	
}
