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
package jpiere.base.plugin.factory;

import java.lang.reflect.Constructor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.adempiere.base.IDocFactory;
import org.compiere.acct.Doc;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MAllocationHdr;
import org.compiere.model.MBankStatement;
import org.compiere.model.MInOut;
import org.compiere.model.MInvoice;
import org.compiere.model.MMatchInv;
import org.compiere.model.MTable;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;

import jpiere.base.plugin.org.adempiere.model.MBill;
import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractProcSchedule;
import jpiere.base.plugin.org.adempiere.model.MEstimation;
import jpiere.base.plugin.org.adempiere.model.MInvValAdjust;
import jpiere.base.plugin.org.adempiere.model.MInvValCal;
import jpiere.base.plugin.org.adempiere.model.MPPDoc;
import jpiere.base.plugin.org.adempiere.model.MPPFact;
import jpiere.base.plugin.org.adempiere.model.MPPPlan;
import jpiere.base.plugin.org.adempiere.model.MRecognition;


/**
 *  JPiere Doc Factory
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPiereBasePluginDocFactory implements IDocFactory {

	private final static CLogger s_log = CLogger.getCLogger(JPiereBasePluginDocFactory.class);

	@Override
	public Doc getDocument(MAcctSchema as, int AD_Table_ID, int Record_ID,
			String trxName) {

		Doc doc = null;
		if(	       AD_Table_ID==MInvoice.Table_ID //318
				|| AD_Table_ID==MInOut.Table_ID //319
				|| AD_Table_ID==MMatchInv.Table_ID//472
				|| AD_Table_ID==MAllocationHdr.Table_ID//735
				|| AD_Table_ID==MBankStatement.Table_ID //392
				|| AD_Table_ID==MRecognition.Table_ID //1000188
				|| AD_Table_ID==MEstimation.Table_ID //1000080
				|| AD_Table_ID==MBill.Table_ID //1000032
				|| AD_Table_ID==MContract.Table_ID //1000180
				|| AD_Table_ID==MContractContent.Table_ID //1000186
				|| AD_Table_ID==MContractProcSchedule.Table_ID//1000227
				|| AD_Table_ID==MInvValCal.Table_ID //1000067
				|| AD_Table_ID==MInvValAdjust.Table_ID//1000071
				|| AD_Table_ID==MPPDoc.Table_ID//1000268
				|| AD_Table_ID==MPPPlan.Table_ID//1000269
				|| AD_Table_ID==MPPFact.Table_ID//1000271
			)
		{

			String tableName = MTable.get(Env.getCtx(), AD_Table_ID).get_TableName();
			StringBuffer sql = new StringBuffer("SELECT * FROM ")
				.append(tableName)
				.append(" WHERE ").append(tableName).append("_ID=? AND Processed='Y'");
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement (sql.toString(), trxName);
				pstmt.setInt (1, Record_ID);
				rs = pstmt.executeQuery ();
				if (rs.next ())
				{
					doc = getDocument(as, AD_Table_ID, rs, trxName);
				}
				else
					s_log.severe("Not Found: " + tableName + "_ID=" + Record_ID);
			}
			catch (Exception e)
			{
				s_log.log (Level.SEVERE, sql.toString(), e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}


		}

		return doc;
	}

	@Override
	public Doc getDocument(MAcctSchema as, int AD_Table_ID, ResultSet rs, String trxName) {
		Doc doc = null;

		String className = null;

		if(AD_Table_ID == MInvoice.Table_ID){
			className = "jpiere.base.plugin.org.compiere.acct.Doc_InvoiceJP";
		}else if(AD_Table_ID == MInOut.Table_ID){//319
			className = "jpiere.base.plugin.org.compiere.acct.Doc_InOutJP";
		}else if(AD_Table_ID == MMatchInv.Table_ID){//472
			className = "jpiere.base.plugin.org.compiere.acct.Doc_MatchInvJP";
		}else if(AD_Table_ID == MAllocationHdr.Table_ID){//735
			className = "jpiere.base.plugin.org.compiere.acct.Doc_AllocationHdrJP";
		}else if(AD_Table_ID == MBankStatement.Table_ID){//392
			className = "jpiere.base.plugin.org.compiere.acct.Doc_BankStatementJP";
		}else if(AD_Table_ID == MRecognition.Table_ID){
			className = "jpiere.base.plugin.org.compiere.acct.Doc_JPRecognition";
		}else if(AD_Table_ID == MBill.Table_ID){
			className = "jpiere.base.plugin.org.compiere.acct.Doc_JPBill";
		}else if(AD_Table_ID == MInvValCal.Table_ID){
			className = "jpiere.base.plugin.org.compiere.acct.Doc_JPInvValCal";
		}else if(AD_Table_ID == MInvValAdjust.Table_ID){
			className = "jpiere.base.plugin.org.compiere.acct.Doc_JPInvValAdjust";
		}else if(AD_Table_ID == MEstimation.Table_ID){
			className = "jpiere.base.plugin.org.compiere.acct.Doc_JPEstimation";
		}else if(AD_Table_ID == MContract.Table_ID){
			className = "jpiere.base.plugin.org.compiere.acct.Doc_JPContract";
		}else if(AD_Table_ID == MContractContent.Table_ID){
			className = "jpiere.base.plugin.org.compiere.acct.Doc_JPContractContent";
		}else if(AD_Table_ID == MContractProcSchedule.Table_ID){
			className = "jpiere.base.plugin.org.compiere.acct.Doc_JPContractProcSchedule";
		}else if(AD_Table_ID == MPPDoc.Table_ID){
			className = "jpiere.base.plugin.org.compiere.acct.Doc_JPPPDoc";
		}else if(AD_Table_ID == MPPPlan.Table_ID){
			className = "jpiere.base.plugin.org.compiere.acct.Doc_JPPPPlan";
		}else if(AD_Table_ID == MPPFact.Table_ID){
			className = "jpiere.base.plugin.org.compiere.acct.Doc_JPPPFact";
		}else {
			return null;
		}


		try
		{
			Class<?> cClass = Class.forName(className);
			Constructor<?> cnstr = cClass.getConstructor(new Class[] {MAcctSchema.class, ResultSet.class, String.class});
			doc = (Doc) cnstr.newInstance(as, rs, trxName);
		}
		catch (Exception e)
		{
			doc = null;
		}

		return doc;
	}

}
