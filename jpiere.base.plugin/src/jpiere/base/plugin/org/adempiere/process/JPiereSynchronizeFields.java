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

import java.util.logging.Level;

import org.compiere.model.MField;
import org.compiere.model.MTab;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.AdempiereUserError;
import org.compiere.util.DB;


/**
 *	Synchronize Fields Setting
 *	
 *  @author Hideaki Hagiwara
 */
public class JPiereSynchronizeFields extends SvrProcess
{
	/**	Tab	To					*/
	private int			p_AD_TabTo_ID = 0;
	/**	Tab	From				*/
	private int			p_AD_TabFrom_ID = 0;

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		p_AD_TabFrom_ID = getRecord_ID();
		
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("AD_Tab_ID"))
				p_AD_TabTo_ID = para[i].getParameterAsInt();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
	}	//	prepare

	/**
	 * 	Process
	 *	@return message
	 *	@throws Exception
	 */
	protected String doIt() throws Exception
	{
		
		if (log.isLoggable(Level.INFO)) log.info("To AD_Tab_ID=" + p_AD_TabTo_ID + ", From=" + p_AD_TabFrom_ID);
		MTab fromTab = new MTab (getCtx(), p_AD_TabFrom_ID, get_TrxName());
		if (fromTab.get_ID() == 0)
			throw new AdempiereUserError("@NotFound@ (from->) @AD_Tab_ID@");
		MTab toTab = new MTab (getCtx(), p_AD_TabTo_ID, get_TrxName());
		if (toTab.get_ID() == 0)
			throw new AdempiereUserError("@NotFound@ (to<-) @AD_Tab_ID@");
		if (fromTab.getAD_Table_ID() != toTab.getAD_Table_ID())
			throw new AdempiereUserError("@Error@ @AD_Table_ID@");
		
		final String sqluptrlfld = ""
				+ "UPDATE ad_field_trl "
				+ "SET    name = (SELECT name "
				+ "               FROM   ad_field_trl t2 "
				+ "               WHERE  t2.ad_field_id = ? AND t2.ad_language = ad_field_trl.ad_language), "
				+ "       description = (SELECT description "
				+ "                      FROM   ad_field_trl t2 "
				+ "                      WHERE  t2.ad_field_id = ? AND t2.ad_language = ad_field_trl.ad_language), "
				+ "       help = (SELECT help "
				+ "               FROM   ad_field_trl t2 "
				+ "               WHERE  t2.ad_field_id = ? AND t2.ad_language = ad_field_trl.ad_language), "
				+ "       istranslated = (SELECT istranslated "
				+ "                       FROM   ad_field_trl t2 "
				+ "                       WHERE  t2.ad_field_id = ? AND t2.ad_language = ad_field_trl.ad_language) "
				+ "WHERE  ad_field_id = ?";		
		
		int count = 0;
		boolean isCopyOK = false;
		for (MField fromField : fromTab.getFields(false, get_TrxName()))
		{
			isCopyOK = false;
			if(fromField.getDisplayLength() == -1)
				continue;
			
			for(MField toField : toTab.getFields(false, get_TrxName()))
			{
				if(fromField.getAD_Column_ID()==toField.getAD_Column_ID())
				{
					if(toField.getDisplayLength() == -1)
					{
						isCopyOK = true;
						break;
					}
					
					PO.copyValues(fromField, toField);
					if (!fromField.isActive())
						toField.setIsActive(false);
					
					toField.setAD_Tab_ID(toTab.getAD_Tab_ID());
					toField.saveEx(get_TrxName());
					DB.executeUpdateEx(sqluptrlfld, new Object[]{fromField.get_ID(),fromField.get_ID(),fromField.get_ID(),fromField.get_ID(),toField.get_ID()}, get_TrxName());
					count++;
					isCopyOK = true;
					break;
				}
			}//for
			
			if(isCopyOK)
			{
				continue;
			}else{
				MField newField = new MField (toTab, fromField);
				if (! fromField.isActive())
					newField.setIsActive(false);
				newField.saveEx(get_TrxName());
				DB.executeUpdateEx(sqluptrlfld, new Object[]{fromField.get_ID(),fromField.get_ID(),fromField.get_ID(),fromField.get_ID(),newField.get_ID()}, get_TrxName());
				count++;
			}
		}//for (MField fromField : fromTab.getFields(false, get_TrxName()))
		
		
		for (MField toField : toTab.getFields(false, get_TrxName()))
		{
			isCopyOK = false;
			for(MField fromField : fromTab.getFields(false, get_TrxName()))
			{
				if(fromField.getAD_Column_ID()==toField.getAD_Column_ID())
				{
					isCopyOK = true;
					break;
				}
			}//for

			if(isCopyOK)
			{
				continue;
			}else{
				
				if(toField.getDisplayLength() == -1)
				{
					continue;
				}
				toField.setIsDisplayed(false);
				toField.setIsDisplayedGrid(false);
				toField.saveEx(get_TrxName());
				count++;
			}
		}//for (MField toField : toTab.getFields(false, get_TrxName()))	

		StringBuilder msgreturn = new StringBuilder("@Copied@ #").append(count);
		return msgreturn.toString();
	}	//	doIt

}	//	Synchronize Fields Setting
