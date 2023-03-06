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
import java.util.logging.Level;

import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;

import jpiere.base.plugin.org.adempiere.model.MCorporationGroup;
import jpiere.base.plugin.org.adempiere.model.MGroupCorporations;
import jpiere.base.plugin.org.adempiere.model.X_I_CorporationGroupJP;

/**
 * 	JPIERE-0094: Import Corporation Group
 *
 *  @author Hideaki Hagiwara
 *  @version $Id: ImportCorporationGroup.java,v 1.0 2015/05/06 $
 *
 */
public class JPiereImportCorporationGroup extends SvrProcess
{

	private boolean p_deleteOldImported = false;

	/**	Only validate, don't import		*/
	private boolean			p_IsValidateOnly = false;

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (name.equals("DeleteOldImported"))
				p_deleteOldImported = "Y".equals(para[i].getParameter());
			else if (name.equals("IsValidateOnly"))
				p_IsValidateOnly = para[i].getParameterAsBoolean();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
	}	//	prepare

	/**
	 * 	Process
	 *	@return info
	 *	@throws Exception
	 */
	protected String doIt() throws Exception
	{
		StringBuilder sql = null;
		int no = 0;
		StringBuilder clientCheck = new StringBuilder(" AND AD_Client_ID=").append(getAD_Client_ID());


		//Delete Old Imported data
		if (p_deleteOldImported)
		{
			sql = new StringBuilder ("DELETE FROM I_CorporationGroupJP ")
				  .append("WHERE I_IsImported='Y'").append (clientCheck);
			no = DB.executeUpdate(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Delete Old Impored =" + no);
		}


		//	Existing Corporation Group ? Match Value
		sql = new StringBuilder ("UPDATE I_CorporationGroupJP i ")
				.append("SET JP_CorporationGroup_ID=(SELECT JP_CorporationGroup_ID FROM JP_CorporationGroup p")
				.append(" WHERE i.Value=p.Value AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE JP_CorporationGroup_ID is Null AND Value IS NOT NULL")
				.append(" AND I_IsImported='N'").append(clientCheck);
		no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Found Corporation Group=" + no);


		commitEx();

		//	Existing Corporation ? Match Value
		sql = new StringBuilder ("UPDATE I_CorporationGroupJP i ")
				.append("SET JP_Corporation_ID=(SELECT JP_Corporation_ID FROM JP_Corporation p")
				.append(" WHERE i.CorporationValue=p.Value AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE JP_Corporation_ID is Null AND CorporationValue IS NOT NULL")
				.append(" AND I_IsImported='N'").append(clientCheck);
		no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Found Corporation=" + no);

		commitEx();

		if (p_IsValidateOnly)
		{
			return "Validated";
		}

		//
		sql = new StringBuilder ("SELECT * FROM I_CorporationGroupJP WHERE I_IsImported='N'")
					.append(clientCheck);
		sql.append(" ORDER BY Value, CorporationValue");
		PreparedStatement pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
		ResultSet rs = pstmt.executeQuery();

		String Old_CGValue = "";
		MCorporationGroup corpGroup = null;
		MGroupCorporations groupCorps = null;

		while (rs.next())
		{
			String New_CGValue = rs.getString("Value") ;

			X_I_CorporationGroupJP imp = new X_I_CorporationGroupJP (getCtx (), rs, get_TrxName());

			if ( ! New_CGValue.equals(Old_CGValue)) {

				boolean isNew = true;
				if(imp.getJP_CorporationGroup_ID()!=0){
					isNew =false;
				}

				if(isNew){
					if(imp.getName()!=null && !imp.getName().isEmpty()){
						corpGroup = new MCorporationGroup(getCtx (), 0, get_TrxName());
						corpGroup.setValue(imp.getValue());
						corpGroup.setName(imp.getName());
						corpGroup.setName2(imp.getName2());
						corpGroup.setDescription(imp.getDescription());


						corpGroup.saveEx();
						imp.setJP_CorporationGroup_ID(corpGroup.getJP_CorporationGroup_ID());
						imp.setI_ErrorMsg("New Record");
						imp.setI_IsImported(true);
						imp.setProcessed(true);

					}else{
						imp.setI_ErrorMsg("No Name");
						imp.setI_IsImported(false);
						imp.setProcessed(false);
					}

				}else{//Update
					corpGroup = new MCorporationGroup(getCtx (), imp.getJP_CorporationGroup_ID(), get_TrxName());
					corpGroup.setName(imp.getName());
					corpGroup.setName2(imp.getName2());
					corpGroup.setDescription(imp.getDescription());

					corpGroup.saveEx();
					imp.setI_ErrorMsg("Update Record");
					imp.setI_IsImported(true);
					imp.setProcessed(true);

				}
				imp.saveEx();

			}//if ( ! New_CGValue.equals(Old_CGValue))

			if (imp.getJP_Corporation_ID() != 0)
			{
				groupCorps = new MGroupCorporations (getCtx(), 0, get_TrxName());
				groupCorps.setJP_CorporationGroup_ID(corpGroup.getJP_CorporationGroup_ID());
				groupCorps.setJP_Corporation_ID(imp.getJP_Corporation_ID());
				groupCorps.saveEx();
			}

			Old_CGValue = New_CGValue ;

		}//while (rs.next())


		return "";
	}	//	doIt

}	//	ImportPayment
