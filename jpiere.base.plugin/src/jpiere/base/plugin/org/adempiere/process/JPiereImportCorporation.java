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

import jpiere.base.plugin.org.adempiere.model.MCorporation;
import jpiere.base.plugin.org.adempiere.model.X_I_CorporationJP;

import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;

/**
 * 	Import Corporation
 *
 *  @author Hideaki Hagiwara
 *  @version $Id: ImportCorporation.java,v 1.0 2015/05/06 $
 *
 */
public class JPiereImportCorporation extends SvrProcess
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
			sql = new StringBuilder ("DELETE I_CorporationJP ")
				  .append("WHERE I_IsImported='Y'").append (clientCheck);
			no = DB.executeUpdate(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Delete Old Impored =" + no);
		}


		//	Existing Corporation ? Match Value
		sql = new StringBuilder ("UPDATE I_CorporationJP i ")
				.append("SET JP_Corporation_ID=(SELECT JP_Corporation_ID FROM JP_Corporation p")
				.append(" WHERE i.Value=p.Value AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE JP_Corporation_ID is Null AND Value IS NOT NULL")
				.append(" AND I_IsImported='N'").append(clientCheck);
		no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Found Corporation=" + no);


		commitEx();

		//	Existing Business Partner ? Match Value
		sql = new StringBuilder ("UPDATE I_CorporationJP i ")
				.append("SET C_BPartner_ID=(SELECT C_BPartner_ID FROM C_BPartner p")
				.append(" WHERE i.BPValue=p.Value AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE C_BPartner_ID is Null AND BPValue IS NOT NULL")
				.append(" AND I_IsImported='N'").append(clientCheck);
		no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Found Business Partner=" + no);

		commitEx();

		if (p_IsValidateOnly)
		{
			return "Validated";
		}

		//
		sql = new StringBuilder ("SELECT * FROM I_CorporationJP WHERE I_IsImported='N'")
					.append(clientCheck);
		PreparedStatement pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
		ResultSet rs = pstmt.executeQuery();
		while (rs.next())
		{
			X_I_CorporationJP imp = new X_I_CorporationJP (getCtx (), rs, get_TrxName());

			boolean isNew = true;
			if(imp.getJP_Corporation_ID()!=0){
				isNew =false;
			}

			if(isNew){
				if(imp.getName()!=null && !imp.getName().isEmpty()){
					MCorporation newCorp = new MCorporation(getCtx (), 0, get_TrxName());
					newCorp.setValue(imp.getValue());
					newCorp.setName(imp.getName());
					newCorp.setName2(imp.getName2());
					newCorp.setDescription(imp.getDescription());
					newCorp.setDUNS(imp.getDUNS());
					newCorp.setC_BPartner_ID(imp.getC_BPartner_ID());

					newCorp.saveEx();
					imp.setJP_Corporation_ID(newCorp.getJP_Corporation_ID());
					imp.setI_ErrorMsg("New Record");
					imp.setI_IsImported(true);
					imp.setProcessed(true);

				}else{
					imp.setI_ErrorMsg("No Name");
					imp.setI_IsImported(false);
					imp.setProcessed(false);
				}

			}else{//Update
				MCorporation updateCorp = new MCorporation(getCtx (), imp.getJP_Corporation_ID(), get_TrxName());
				updateCorp.setName(imp.getName());
				updateCorp.setName2(imp.getName2());
				updateCorp.setDescription(imp.getDescription());
				updateCorp.setDUNS(imp.getDUNS());
				updateCorp.setC_BPartner_ID(imp.getC_BPartner_ID());

				updateCorp.saveEx();
				imp.setI_ErrorMsg("Update Record");
				imp.setI_IsImported(true);
				imp.setProcessed(true);

			}
			imp.saveEx();
		}


		return "";
	}	//	doIt

}	//	ImportPayment
