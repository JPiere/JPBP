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

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.model.ImportValidator;
import org.adempiere.process.ImportProcess;
import org.adempiere.util.IProcessUI;
import org.compiere.model.MColumn;
import org.compiere.model.MProduct;
import org.compiere.model.MProductPO;
import org.compiere.model.MTable;
import org.compiere.model.MUser;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.X_I_ProductJP;

/**
 *	JPIERE-0096:Import Products from I_ProductJP
 *
 * 	@author 	Jorg Janke
 * 	@version 	$Id: ImportProduct.java,v 1.3 2006/07/30 00:51:01 jjanke Exp $
 *
 *  @author Carlos Ruiz, globalqss
 * 			<li>FR [ 2788278 ] Data Import Validator - migrate core processes
 * 				https://sourceforge.net/tracker/?func=detail&aid=2788278&group_id=176962&atid=879335
 *
 *  @author Hideaki Hagiwara
 */
public class JPiereImportProduct extends SvrProcess implements ImportProcess
{
	/**	Client to be imported to		*/
	private int				p_AD_Client_ID = 0;
	/**	Delete old Imported				*/
	private boolean			p_deleteOldImported = false;

	/**	Only validate, don't import		*/
	private boolean			p_IsValidateOnly = false;

	private String p_JP_ImportSalesRepIdentifier = JPiereImportUser.JP_ImportUserIdentifier_ValueName;//VN

	private IProcessUI processMonitor = null;

	private String message = null;

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		p_AD_Client_ID = getAD_Client_ID();

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (name.equals("DeleteOldImported"))
				p_deleteOldImported = "Y".equals(para[i].getParameter());
			else if (name.equals("IsValidateOnly"))
				p_IsValidateOnly = para[i].getParameterAsBoolean();
			else if (name.equals("JP_ImportSalesRepIdentifier"))
				p_JP_ImportSalesRepIdentifier = para[i].getParameterAsString();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
	}	//	prepare

	/**
	 *  Perform process.
	 *  @return Message
	 *  @throws Exception
	 */
	protected String doIt() throws java.lang.Exception
	{
		processMonitor = Env.getProcessUI(getCtx());

		StringBuilder sql = null;
		int no = 0;
		String clientCheck = getWhereClause();

		/** Delete Old Imported */
		if (p_deleteOldImported)
		{
			sql = new StringBuilder ("DELETE I_ProductJP ")
				.append("WHERE I_IsImported='Y' AND Processed='Y' ").append(clientCheck);
			try {
				no = DB.executeUpdate(sql.toString(), get_TrxName());
				if (log.isLoggable(Level.INFO)) log.info("Delete Old Imported =" + no);
			}catch (Exception e) {
				throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
			}
		}

		/** Reset Message */
		sql = new StringBuilder ("UPDATE I_ProductJP ")
				.append("SET I_ErrorMsg='' ")
				.append(" WHERE I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(String.valueOf(no));
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}

		ModelValidationEngine.get().fireImportValidate(this, null, null, ImportValidator.TIMING_BEFORE_VALIDATE);

		/** Reverse Lookup Surrogate Key */
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "M_Product_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupM_Product_ID())
			commitEx();
		else
			return message;


		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_Org_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupAD_Org_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "M_Product_Category_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupM_Product_Category_ID())
			commitEx();
		else
			return message;


		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_TaxCategory_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_TaxCategory_ID())
			commitEx();
		else
			return message;


		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_UOM_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_UOM_ID())
			commitEx();
		else
			return message;


		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "M_FreightCategory_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupM_FreightCategory_ID())
			commitEx();
		else
			return message;


		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "M_PartType_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupM_PartType_ID())
			commitEx();
		else
			return message;


		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "S_ExpenseType_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupS_ExpenseType_ID())
			commitEx();
		else
			return message;


		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "S_Resource_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupS_Resource_ID())
			commitEx();
		else
			return message;


		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "M_AttributeSet_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupM_AttributeSet_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "SalesRep_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupSalesRep_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "R_MailText_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupR_MailText_ID())
			commitEx();
		else
			return message;


		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "M_Locator_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupM_Locator_ID())
			commitEx();
		else
			return message;


		ModelValidationEngine.get().fireImportValidate(this, null, null, ImportValidator.TIMING_AFTER_VALIDATE);

		commitEx();
		if (p_IsValidateOnly)
		{
			return "Validated";
		}

		/** Register & Update Product */
		String msg = Msg.getMsg(getCtx(), "Register") +" & "+ Msg.getMsg(getCtx(), "Update")  + " " + Msg.getElement(getCtx(), "M_Product_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		sql = new StringBuilder ("SELECT * FROM I_ProductJP WHERE I_IsImported='N' OR Processed='N' ")
				.append(clientCheck).append(" ORDER BY Value ");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int recordsNum = 0;
		int successNewNum = 0;
		int successUpdateNum = 0;
		int failureNewNum = 0;
		int failureUpdateNum = 0;
		String records = Msg.getMsg(getCtx(), "JP_NumberOfRecords");
		String success = Msg.getMsg(getCtx(), "JP_Success");
		String failure = Msg.getMsg(getCtx(), "JP_Failure");
		String newRecord = Msg.getMsg(getCtx(), "New");
		String updateRecord = Msg.getMsg(getCtx(), "Update");

		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			rs = pstmt.executeQuery();
			String lastValue = "";
			MProduct product = null;
			boolean isNew = true;

			while (rs.next())
			{
				X_I_ProductJP imp = new X_I_ProductJP (getCtx (), rs, get_TrxName());

				isNew = true;
				if(imp.getM_Product_ID()!=0)
				{
					isNew =false;
					product = new MProduct(getCtx(), imp.getM_Product_ID(), get_TrxName());
					lastValue = product.getValue();

				}else{

					if(lastValue.equals(imp.getValue()))
					{
						isNew = false;

					}else {

						lastValue = imp.getValue();

					}

				}

				if(isNew)
				{
					product = new MProduct(getCtx(), 0, get_TrxName());
					if(createNewProduct(imp,product))
					{
						successNewNum++;
						imp.setI_ErrorMsg(Msg.getMsg(getCtx(), "NewRecord"));
						imp.setI_IsImported(true);
						imp.setProcessed(true);
						imp.saveEx(get_TrxName());

					}else {

						failureNewNum++;
						rollback();
						imp.setI_ErrorMsg(message);
						imp.setI_IsImported(false);
						imp.setProcessed(false);
						imp.saveEx(get_TrxName());
					}

				}else{

					if(updateProduct(imp,product))
					{
						successUpdateNum++;
						imp.setI_ErrorMsg(Msg.getMsg(getCtx(), "Update"));
						imp.setI_IsImported(true);
						imp.setProcessed(true);
						imp.saveEx(get_TrxName());

					}else {

						failureUpdateNum++;
						rollback();
						imp.setI_ErrorMsg(message);
						imp.setI_IsImported(false);
						imp.setProcessed(false);
						imp.saveEx(get_TrxName());
					}

				}

				commitEx();

				recordsNum++;
				if (processMonitor != null)
				{
					processMonitor.statusUpdate(
						newRecord + "( "+  success + " : " + successNewNum + "  /  " +  failure + " : " + failureNewNum + " ) + "
						+ updateRecord + " ( "+  success + " : " + successUpdateNum + "  /  " +  failure + " : " + failureUpdateNum+ " ) "
						);
				}

			}//while

		}catch (Exception e) {

			log.log(Level.SEVERE, e.toString(), e);
			throw e;

		}finally{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return records + recordsNum + " = "	+
					newRecord + "( "+  success + " : " + successNewNum + "  /  " +  failure + " : " + failureNewNum + " ) + "
					+ updateRecord + " ( "+  success + " : " + successUpdateNum + "  /  " +  failure + " : " + failureUpdateNum+ " ) ";

	}	//	doIt


	@Override
	public String getImportTableName() {
		return X_I_ProductJP.Table_Name;
	}


	@Override
	public String getWhereClause() {
		StringBuilder msgreturn = new StringBuilder(" AND AD_Client_ID=").append(p_AD_Client_ID);
		return msgreturn.toString();
	}

	/**
	 * Reverse Look up Product From Value and UPC , VendorProduct No
	 * @throws Exception
	 *
	 */
	private boolean reverseLookupM_Product_ID() throws Exception
	{
		int no = 0;

		//Reverse lookup M_Product_ID From Value
		StringBuilder sql = new StringBuilder ("UPDATE I_ProductJP i ")
				.append("SET M_Product_ID=(SELECT M_Product_ID FROM M_Product p")
				.append(" WHERE i.Value=p.Value AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.M_Product_ID IS NULL AND i.Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Reverse lookup M_Product_ID From UPC
		sql = new StringBuilder ("UPDATE I_ProductJP i ")
			.append("SET M_Product_ID=(SELECT M_Product_ID FROM M_Product p")
			.append(" WHERE i.UPC=p.UPC AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append(" WHERE i.UPC IS NOT NULL AND i.M_Product_ID IS NULL")
			.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Reverse lookup C_BPartner_ID From BPartner_Value for Update M_Product_ID From VendorProductNo
		reverseLookupC_BPartner_ID();

		//Reverse lookup M_Product_ID From VendorProductNo
		sql = new StringBuilder ("UPDATE I_ProductJP i ")
			.append("SET M_Product_ID=(SELECT M_Product_ID FROM M_Product_po p")
			.append(" WHERE i.C_BPartner_ID=p.C_BPartner_ID")
			.append(" AND i.VendorProductNo=p.VendorProductNo AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.VendorProductNo IS NOT NULL AND i.M_Product_ID IS NULL")
			.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Error : Search Key is null
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "JP_Null")+Msg.getElement(getCtx(), "Value");
		sql = new StringBuilder ("UPDATE I_ProductJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE Value IS NULL AND M_Product_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message  +" : " +  e.toString() +" : " + sql );
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupM_Product_ID

	/**
	 * Reverse look up C_BPartner_ID From BPartner_Value for Update M_Product_ID From VendorProductNo
	 *
	 * @throws Exception
	 */
	private void reverseLookupC_BPartner_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		//Reverse lookup C_BPartner_ID From BPartner_Value for Update M_Product_ID From VendorProductNo
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_BPartner_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "BPartner_Value") ;
		sql = new StringBuilder ("UPDATE I_ProductJP i ")
			.append("SET C_BPartner_ID=(SELECT C_BPartner_ID FROM C_BPartner p")
			.append(" WHERE i.BPartner_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.C_BPartner_ID IS NULL AND i.BPartner_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Found Business Partner =" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid BPartner_Value
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "BPartner_Value");
		sql = new StringBuilder ("UPDATE I_ProductJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append("WHERE C_BPartner_ID IS NULL AND BPartner_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no + ":" + sql);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		if(no > 0)
		{
			commitEx();
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg );
		}

		commitEx();

	}//reverseLookupC_BPartner_ID


	/**
	 * Reverse Look up Organization From JP_Org_Value
	 *
	 **/
	private boolean reverseLookupAD_Org_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_ProductJP i ")
				.append("SET AD_Org_ID=(SELECT AD_Org_ID FROM AD_org p")
				.append(" WHERE i.JP_Org_Value=p.Value AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) AND p.IsSummary='N') ")
				.append(" WHERE i.JP_Org_Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message + " : " + e.toString()  + " : " +  sql );
		}

		//Invalid JP_Org_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Org_Value");
		sql = new StringBuilder ("UPDATE I_ProductJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE AD_Org_ID = 0 AND JP_Org_Value IS NOT NULL AND JP_Org_Value <> '0' ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message + " : " + e.toString()  + " : " +  sql );
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupAD_Org_ID

	/**
	 * Reverse Look up Product Category From ProductCategory_Value
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupM_Product_Category_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_ProductJP i ")
				.append("SET M_Product_Category_ID=(SELECT M_Product_Category_ID FROM M_Product_Category p")
				.append(" WHERE i.ProductCategory_Value=p.Value AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.ProductCategory_Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message  +" : " +  e.toString() +" : " + sql );
		}

		//Invalid ProuctCategory_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "ProductCategory_Value");
		sql = new StringBuilder ("UPDATE I_ProductJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE ProductCategory_Value IS NOT NULL AND M_Product_Category_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message  +" : " +  e.toString() +" : " + sql);
		}

		if(no > 0)
		{
			return false;
		}

		//Set Default Product Category in case of New Product
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "M_Product_Category_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "IsDefault") ;
		sql = new StringBuilder ("UPDATE I_ProductJP ")
			.append("SET ProductCategory_Value=(SELECT MAX(Value) FROM M_Product_Category")
			.append(" WHERE IsDefault='Y' AND AD_Client_ID=").append(p_AD_Client_ID).append(") ")
			.append("WHERE ProductCategory_Value IS NULL AND M_Product_Category_ID IS NULL")
			.append(" AND M_Product_ID IS NULL")	//	set category only if product not found
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		return true;

	} //reverseLookupM_Product_Category_ID

	/**
	 *
	 * Reverse Look up C_TaxCategory_ID From JP_TaxCategory_Name
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupC_TaxCategory_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_ProductJP i ")
				.append("SET C_TaxCategory_ID=(SELECT C_TaxCategory_ID FROM C_TaxCategory p")
				.append(" WHERE i.JP_TaxCategory_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.JP_TaxCategory_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_TaxCategory_Name
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_TaxCategory_Name");
		sql = new StringBuilder ("UPDATE I_ProductJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE JP_TaxCategory_Name IS NOT NULL AND C_TaxCategory_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " + e.toString() +" : " + sql);
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupC_TaxCategory_ID


	/**
	 * Reverse Look up C_UOM_ID From X12DE355
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupC_UOM_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_ProductJP i ")
				.append("SET C_UOM_ID=(SELECT C_UOM_ID FROM C_UOM p")
				.append(" WHERE i.X12DE355=p.X12DE355 AND (i.AD_Client_ID=p.AD_Client_ID OR p.AD_Client_ID = 0) ) ")
				.append("WHERE X12DE355 IS NOT NULL")
				.append(" AND I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " +  e.toString() +" : " + sql );
		}

		//Invalid X12DE355
		message = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "X12DE355");
		sql = new StringBuilder ("UPDATE I_ProductJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE X12DE355 IS NOT NULL AND C_UOM_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " +  e.toString() +" : " + sql);
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupC_UOM_ID


	/**
	 * Reverse Look up Freight Category From JP_FreightCategory_Value
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupM_FreightCategory_ID()throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_ProductJP i ")
				.append("SET M_FreightCategory_ID=(SELECT M_FreightCategory_ID FROM M_FreightCategory p")
				.append(" WHERE i.JP_FreightCategory_Value=p.Value AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.JP_FreightCategory_Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message  +" : " +  e.toString() +" : " + sql );
		}

		//Invalid JP_FreightCategory_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_FreightCategory_Value");
		sql = new StringBuilder ("UPDATE I_ProductJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE JP_FreightCategory_Value IS NOT NULL AND M_FreightCategory_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message  +" : " +  e.toString() +" : " + sql);
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupM_FreightCategory_ID


	/**
	 * Reverse Look up M_PartType_ID From JP_PartType_Nam
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupM_PartType_ID()throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_ProductJP i ")
				.append("SET M_PartType_ID=(SELECT M_PartType_ID FROM M_PartType p")
				.append(" WHERE i.JP_PartType_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.JP_PartType_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_PartType_Name
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_PartType_Name");
		sql = new StringBuilder ("UPDATE I_ProductJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE JP_PartType_Name IS NOT NULL AND M_PartType_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " + e.toString() +" : " + sql );
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupM_PartType_ID


	/**
	 * Reverse look up M_PartType_ID From JP_PartType_Name
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupS_ExpenseType_ID()throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_ProductJP i ")
				.append("SET S_ExpenseType_ID=(SELECT S_ExpenseType_ID FROM S_ExpenseType p")
				.append(" WHERE i.JP_ExpenseType_Value=p.Value AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.JP_ExpenseType_Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message  +" : " +  e.toString() +" : " + sql );
		}

		//Invalid JP_ExpenseType_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_ExpenseType_Value");
		sql = new StringBuilder ("UPDATE I_ProductJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE JP_ExpenseType_Value IS NOT NULL AND S_ExpenseType_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message  +" : " +  e.toString() +" : " + sql );
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupS_ExpenseType_ID

	/**
	 * Reverse Look up S_Resource_ID From JP_Resource_Value
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupS_Resource_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_ProductJP i ")
				.append("SET S_Resource_ID=(SELECT S_Resource_ID FROM S_Resource p")
				.append(" WHERE i.JP_Resource_Value=p.Value AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.JP_Resource_Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_Resource_Value
		message = Msg.getMsg(getCtx(), "Error")  + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Resource_Value");
		sql = new StringBuilder ("UPDATE I_ProductJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE JP_Resource_Value IS NOT NULL AND S_Resource_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " + e.toString() +" : " + sql );
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupS_Resource_ID

	/**
	 *
	 * Reverse Look up M_AttributeSet_ID From JP_AttributeSet_Name
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupM_AttributeSet_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_ProductJP i ")
				.append("SET M_AttributeSet_ID=(SELECT M_AttributeSet_ID FROM M_AttributeSet p")
				.append(" WHERE i.JP_AttributeSet_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.JP_AttributeSet_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_AttributeSet_Name
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_AttributeSet_Name");
		sql = new StringBuilder ("UPDATE I_ProductJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE JP_AttributeSet_Name IS NOT NULL AND M_AttributeSet_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " + e.toString() +" : " + sql );
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupM_AttributeSet_ID

	/**
	 * Reverse Look up SalesRep_ID From JP_User_Value
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupSalesRep_ID() throws Exception
	{
		if(Util.isEmpty(p_JP_ImportSalesRepIdentifier) || p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_NotCollate))
			return true;

		StringBuilder sql = null;
		int no = 0;

		if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_EMail)) //E-Mail
		{
			sql = new StringBuilder ("UPDATE I_ProductJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_User_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y' ) ")
					.append(" WHERE i.JP_User_EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Name)) { //Name

			sql = new StringBuilder ("UPDATE I_ProductJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_User_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y' ) ")
					.append(" WHERE i.JP_User_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Value)) { //Value

			sql = new StringBuilder ("UPDATE I_ProductJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_User_Value=p.Value AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y') ")
					.append(" WHERE i.JP_User_Value IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueEMail)) { //Value + E-Mail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_ProductJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_User_Value=p.Value AND i.JP_User_EMail=p.EMail  AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y') ")
					.append(" WHERE i.JP_User_Value IS NOT NULL AND i.JP_User_EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

			try {
				no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			}catch(Exception e) {
				throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
			}


			//In case of EMail is null
			sql = new StringBuilder ("UPDATE I_ProductJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_User_Value=p.Value AND p.EMail IS NULL  AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y') ")
					.append(" WHERE i.JP_User_Value IS NOT NULL AND i.JP_User_EMail IS NULL AND i.SalesRep_ID IS NULL ")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());


		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueName)) { //Value + Name

			sql = new StringBuilder ("UPDATE I_ProductJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_User_Value=p.Value AND i.JP_User_Name=p.Name  AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y') ")
					.append(" WHERE i.JP_User_Value IS NOT NULL AND i.JP_User_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueNameEmail)) { //Value + Name + EMail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_ProductJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_User_Value=p.Value AND i.JP_User_Name=p.Name AND i.JP_User_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y' ) ")
					.append(" WHERE i.JP_User_Value IS NOT NULL AND i.JP_User_Name IS NOT NULL AND i.JP_User_EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

			try {
				no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			}catch(Exception e) {
				throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
			}

			//In case of EMail is null
			sql = new StringBuilder ("UPDATE I_ProductJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_User_Value=p.Value AND i.JP_User_Name=p.Name AND p.EMail IS NULL AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y' ) ")
					.append(" WHERE i.JP_User_Value IS NOT NULL AND i.JP_User_Name IS NOT NULL AND i.JP_User_EMail IS NULL AND i.SalesRep_ID IS NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_NotCollate)) {

			return true;

		}else {

			return true;

		}

		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		return true;

	}//reverseSalesRep_ID


	/**
	 * Reverse Look up R_MailText_ID From JP_MailText_Name
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupR_MailText_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_ProductJP i ")
				.append("SET R_MailText_ID=(SELECT R_MailText_ID FROM R_MailText p")
				.append(" WHERE i.JP_MailText_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.JP_MailText_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message  +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_MailText_Name
		message = Msg.getMsg(getCtx(), "Error")  + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_MailText_Name");
		sql = new StringBuilder ("UPDATE I_ProductJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE JP_MailText_Name IS NOT NULL AND R_MailText_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message  +" : " + e.toString() +" : " + sql);
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupR_MailText_ID


	/**
	 * Reverse Look up M_Locator_ID From JP_Locator_Value
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupM_Locator_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_ProductJP i ")
				.append("SET M_Locator_ID=(SELECT M_Locator_ID FROM M_Locator p")
				.append(" WHERE i.JP_Locator_Value=p.Value AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.JP_Locator_Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_MailText_Name
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Locator_Value");
		sql = new StringBuilder ("UPDATE I_ProductJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE JP_Locator_Value IS NOT NULL AND M_Locator_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " + e.toString() +" : " + sql);
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupM_Locator_ID



	/**
	 * Create New Product
	 *
	 * @param importProduct
	 * @throws SQLException
	 */
	private boolean createNewProduct(X_I_ProductJP importProduct, MProduct newProduct) throws SQLException
	{
		//Mandatory Check!
		if(Util.isEmpty(importProduct.getValue()))
		{
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "Value")};
			message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
			return false;
		}

		if(Util.isEmpty(importProduct.getName()))
		{
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "Name")};
			message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
			return false;
		}

		if(importProduct.getM_Product_Category_ID() == 0)
		{
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "M_Product_Category_ID")};
			message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
			return false;
		}

		if(importProduct.getC_TaxCategory_ID() == 0)
		{
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "C_TaxCategory_ID")};
			message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
			return false;
		}

		if(importProduct.getC_UOM_ID() == 0)
		{
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "C_UOM_ID")};
			message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
			return false;
		}

		if(Util.isEmpty(importProduct.getProductType()))
		{
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "ProductType")};
			message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
			return false;
		}

		ModelValidationEngine.get().fireImportValidate(this, importProduct, newProduct, ImportValidator.TIMING_BEFORE_IMPORT);

		//Copy
		PO.copyValues(importProduct, newProduct);
		if(!Util.isEmpty(importProduct.getJP_User_Value()) && importProduct.getSalesRep_ID() == 0)
		{
			setSalesRep_ID(importProduct, newProduct);
		}

		newProduct.setIsActive(importProduct.isI_IsActiveJP());

		ModelValidationEngine.get().fireImportValidate(this, importProduct, newProduct, ImportValidator.TIMING_AFTER_IMPORT);

		try {
			newProduct.saveEx(get_TrxName());
		}catch (Exception e) {
			message = Msg.getMsg(getCtx(),"SaveIgnored") + Msg.getElement(getCtx(), "M_Product_ID");
			return false;
		}

		importProduct.setM_Product_ID(newProduct.getM_Product_ID());

		if(importProduct.getC_BPartner_ID()> 0 )
		{
			if(!createProductPOInfo(importProduct, newProduct.getM_Product_ID()))
			{
				return false;
			}
		}

		return true;

	}

	/**
	 *
	 * Update Product
	 *
	 * @param importProduct
	 * @throws SQLException
	 */
	private boolean updateProduct(X_I_ProductJP importProduct, MProduct updateProduct) throws SQLException
	{
		ModelValidationEngine.get().fireImportValidate(this, importProduct, updateProduct, ImportValidator.TIMING_BEFORE_IMPORT);

		//Update Product
		MTable M_Product_Table = MTable.get(getCtx(), MProduct.Table_ID, get_TrxName());
		MColumn[] M_Product_Columns = M_Product_Table.getColumns(true);

		MTable I_ProductJP_Table = MTable.get(getCtx(), X_I_ProductJP.Table_ID, get_TrxName());
		MColumn[] I_ProductJP_Columns = I_ProductJP_Table.getColumns(true);

		MColumn i_Column = null;
		for(int i = 0 ; i < M_Product_Columns.length; i++)
		{
			i_Column = M_Product_Columns[i];
			if(i_Column.isVirtualColumn() || i_Column.isKey() || i_Column.isUUIDColumn())
				continue;//i

			if(i_Column.getColumnName().equals("IsActive")
				|| i_Column.getColumnName().equals("IsStocked")
				|| i_Column.getColumnName().equals("ProductType")
				|| i_Column.getColumnName().equals("C_UOM_ID")
				|| i_Column.getColumnName().equals("AD_Client_ID")
				|| i_Column.getColumnName().equals("Value")
				|| i_Column.getColumnName().equals("Processing")
				|| i_Column.getColumnName().equals("Created")
				|| i_Column.getColumnName().equals("CreatedBy")
				|| i_Column.getColumnName().equals("Updated")
				|| i_Column.getColumnName().equals("UpdatedBy") )
				continue;//i

			MColumn j_Column = null;
			Object importValue = null;
			for(int j = 0 ; j < I_ProductJP_Columns.length; j++)
			{
				j_Column = I_ProductJP_Columns[j];

				if(i_Column.getColumnName().equals(j_Column.getColumnName()))
				{
					importValue = importProduct.get_Value(j_Column.getColumnName());

					if(j_Column.getColumnName().equals("SalesRep_ID"))//Reverse Look Up Sales Rep
					{
						if(importValue == null && !Util.isEmpty(importProduct.getJP_User_Value()))
						{
							setSalesRep_ID(importProduct, updateProduct);
						}
					}

					if(importValue == null )
					{
						break;//j

					}else if(importValue instanceof BigDecimal) {

						BigDecimal bigDecimal_Value = (BigDecimal)importValue;
						if(bigDecimal_Value.compareTo(Env.ZERO) == 0)
							break;

					}else if(j_Column.getAD_Reference_ID()==DisplayType.String) {

						String string_Value = (String)importValue;
						if(!Util.isEmpty(string_Value))
						{
							updateProduct.set_ValueNoCheck(i_Column.getColumnName(), importValue);
						}

						break;

					}else if(j_Column.getColumnName().endsWith("_ID")) {

						Integer p_key = (Integer)importValue;
						if(p_key.intValue() <= 0)
							break;
					}

					if(importValue != null)
					{
						try {
							updateProduct.set_ValueNoCheck(i_Column.getColumnName(), importValue);
						}catch (Exception e) {
							message = Msg.getMsg(getCtx(), "Error") + " Column = " + i_Column.getColumnName() + " & " + "Value = " +importValue.toString() + " -> " + e.toString();
							return false;
						}
					}

					break;
				}
			}//for j

		}//for i

		if(!importProduct.isI_IsActiveJP())
			updateProduct.setIsActive(importProduct.isI_IsActiveJP());

		if(importProduct.getM_Product_ID() == 0)
			importProduct.setM_Product_ID(updateProduct.getM_Product_ID());

		ModelValidationEngine.get().fireImportValidate(this, importProduct, updateProduct, ImportValidator.TIMING_AFTER_IMPORT);

		try {
			updateProduct.saveEx(get_TrxName());
		}catch (Exception e) {
			message = Msg.getMsg(getCtx(),"SaveError") + Msg.getElement(getCtx(), "M_Product_ID")+" :  " + e.toString();
			return false;
		}



		//Update Product Purchase Order Info
		if(importProduct.getC_BPartner_ID()> 0 )
		{
			MProductPO[] productPOs = getProductPO(getCtx(), importProduct.getM_Product_ID(),  get_TrxName());
			boolean isNew = true;
			for(int i = 0; i < productPOs.length; i++)
			{
				if(productPOs[i].getC_BPartner_ID() == importProduct.getC_BPartner_ID())
				{
					if(!updateProductPOInfo(importProduct, productPOs[i]))
					{
						return false;
					}

					isNew = false;
					break;
				}
			}//for

			if(isNew)
			{
				if(!createProductPOInfo(importProduct,importProduct.getM_Product_ID()))
				{
					return false;
				}
			}
		}

		return true;

	}

	private boolean createProductPOInfo(X_I_ProductJP importProduct, int M_Product_ID)
	{
		MProductPO newProductPO = new MProductPO(getCtx(), 0, get_TrxName());
		ModelValidationEngine.get().fireImportValidate(this, importProduct, newProductPO, ImportValidator.TIMING_BEFORE_IMPORT);

		PO.copyValues(importProduct, newProductPO);

		newProductPO.setC_BPartner_ID(importProduct.getC_BPartner_ID());
		newProductPO.setM_Product_ID(importProduct.getM_Product_ID());
		newProductPO.setUPC(importProduct.getJP_VendorUPC());
		newProductPO.setC_UOM_ID(importProduct.getJP_VendorUOM_ID());
		newProductPO.setIsActive(importProduct.isI_IsActiveJP());

		ModelValidationEngine.get().fireImportValidate(this, importProduct, newProductPO, ImportValidator.TIMING_AFTER_IMPORT);

		try {
			newProductPO.saveEx(get_TrxName());
		}catch (Exception e) {
			message = Msg.getMsg(getCtx(),"SaveIgnored") + "Product PO Info : " + e.toString();
			return false;
		}

		return true;
	}



	/**
	 * Update Product PO Info
	 *
	 * @param importProduct
	 * @param updateProductPO
	 * @return
	 */
	private boolean updateProductPOInfo(X_I_ProductJP importProduct, MProductPO updateProductPO)
	{
		ModelValidationEngine.get().fireImportValidate(this, importProduct, updateProductPO, ImportValidator.TIMING_BEFORE_IMPORT);

		//Update Product Info
		MTable M_ProductPO_Table = MTable.get(getCtx(), MProductPO.Table_ID, get_TrxName());
		MColumn[] M_ProductPO_Columns = M_ProductPO_Table.getColumns(true);

		MTable I_ProductJP_Table = MTable.get(getCtx(), X_I_ProductJP.Table_ID, get_TrxName());
		MColumn[] I_ProductJP_Columns = I_ProductJP_Table.getColumns(true);

		MColumn i_Column = null;
		for(int i = 0 ; i < M_ProductPO_Columns.length; i++)
		{
			i_Column = M_ProductPO_Columns[i];
			if(i_Column.isVirtualColumn() || i_Column.isKey() || i_Column.isUUIDColumn())
				continue;//i

			if(i_Column.getColumnName().equals("IsActive")
				|| i_Column.getColumnName().equals("Value")
				|| i_Column.getColumnName().equals("Processing")
				|| i_Column.getColumnName().equals("Created")
				|| i_Column.getColumnName().equals("CreatedBy")
				|| i_Column.getColumnName().equals("Updated")
				|| i_Column.getColumnName().equals("UpdatedBy") )
				continue;//i

			MColumn j_Column = null;
			Object importValue = null;
			for(int j = 0 ; j < I_ProductJP_Columns.length; j++)
			{
				j_Column = I_ProductJP_Columns[j];
				if(i_Column.getColumnName().equals(j_Column.getColumnName()))
				{
					importValue = importProduct.get_Value(j_Column.getColumnName());

					if(importValue == null )
					{
						break;//j

					}else if(importValue instanceof BigDecimal) {

						BigDecimal number = (BigDecimal)importProduct.get_Value(j_Column.getColumnName());
						if(number.compareTo(Env.ZERO) == 0)
							break;

					}else if(j_Column.getAD_Reference_ID()==DisplayType.String) {

						String string_Value = (String)importValue;
						if(!Util.isEmpty(string_Value))
						{
							updateProductPO.set_ValueNoCheck(i_Column.getColumnName(), importValue);
						}

						break;

					}else if(i_Column.getColumnName().endsWith("_ID")) {

						Integer p_key = (Integer)importProduct.get_Value(j_Column.getColumnName());
						if(p_key.intValue() <= 0)
							break;
					}
					if(importValue != null)
					{
						try {
							updateProductPO.set_ValueNoCheck(i_Column.getColumnName(), importProduct.get_Value(j_Column.getColumnName()));
						}catch (Exception e) {

							message = Msg.getMsg(getCtx(), "Error") + " Column = " + i_Column.getColumnName() + " & " + "Value = " +importValue.toString() + " -> " +e.toString();
							return false;
						}

					}
					break;
				}
			}//for j

		}//for i

		updateProductPO.setIsActive(importProduct.isI_IsActiveJP());
		ModelValidationEngine.get().fireImportValidate(this, importProduct, updateProductPO, ImportValidator.TIMING_AFTER_IMPORT);

		try {
			updateProductPO.saveEx(get_TrxName());
		}catch (Exception e) {
			message = Msg.getMsg(getCtx(),"SaveError") + Msg.getElement(getCtx(), "Product PO Info") + " -> " +e.toString();
			return false;
		}

		return true;
	}

	/**
	 * Set SalesRep_ID
	 *
	 * @param importProduct
	 * @param m_Product
	 */
	private void setSalesRep_ID(X_I_ProductJP importProduct, MProduct m_Product)
	{
		String JP_User_Value = importProduct.getJP_User_Value();
		int[] AD_User_IDs = PO.getAllIDs(MUser.Table_Name, "Value='" + JP_User_Value +"'"
				+ " AND (AD_Client_ID=" + p_AD_Client_ID +" OR AD_Client_ID=0) ", get_TrxName() );
		MUser m_SalesRep = null;

		if(AD_User_IDs != null)
		{
			for(int i = 0; i < AD_User_IDs.length; i++)
			{
				m_SalesRep = new MUser(getCtx(), AD_User_IDs[i], get_TrxName());
				if(m_SalesRep.getAD_Client_ID() == p_AD_Client_ID && m_SalesRep.getAD_Org_ID() == 0)
				{
					break;
				}
			}
		}

		if(m_SalesRep != null)
		{
			m_Product.setSalesRep_ID(m_SalesRep.getAD_User_ID());
		}else {
			String msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_User_Value");
			if(Util.isEmpty(importProduct.getI_ErrorMsg()))
			{
				importProduct.setI_ErrorMsg(msg);
			}else{
				importProduct.setI_ErrorMsg(importProduct.getI_ErrorMsg()+ " : "+ msg);
			}
		}
	}//setSalesRep_ID


	private MProductPO[] getProductPO (Properties ctx, int M_Product_ID, String trxName)
	{
		final String whereClause = "M_Product_ID=?";
		List<MProductPO> list = new Query(ctx, MProductPO.Table_Name, whereClause, trxName)
									.setParameters(M_Product_ID)
									.setOnlyActiveRecords(false)
									.setOrderBy("IsCurrentVendor DESC")
									.list();
		return list.toArray(new MProductPO[list.size()]);
	}
}	//	ImportProduct
