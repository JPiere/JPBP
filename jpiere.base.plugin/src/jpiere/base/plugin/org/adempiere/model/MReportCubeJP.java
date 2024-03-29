/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.exceptions.DBException;
import org.compiere.model.MPeriod;
import org.compiere.util.DB;
import org.compiere.util.KeyNamePair;

/**
*
* JPIERE-0458 JPiere Fact Acct summary
*
* @author h.hagiwara
*
*/
public class MReportCubeJP extends X_PA_ReportCubeJP {

	/**
	 *
	 */
	private static final long serialVersionUID = -4771117572936231607L;

	public MReportCubeJP(Properties ctx, int PA_ReportCubeJP_ID, String trxName) {
		super(ctx, PA_ReportCubeJP_ID, trxName);
	}

	public MReportCubeJP(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	public String update(boolean reset, boolean force) {

		String result = getName() + ": ";
		Timestamp ts = null;
		long start;
		long elapsed;

		String where = " WHERE PA_ReportCubeJP_ID = " + getPA_ReportCubeJP_ID();
		String periods = " (-1) ";
		if ( getLastRecalculated() != null && !reset )
		{
			StringBuilder periodList = new StringBuilder();
			StringBuilder periodNames = new StringBuilder();

			String sql = "SELECT DISTINCT p.C_Period_ID, p.Name FROM C_Period p " +
			 "INNER JOIN C_Year y ON (y.C_Year_ID=p.C_Year_ID) " +
			 "INNER JOIN PA_ReportCubeJP c ON (c.C_Calendar_ID = y.C_Calendar_ID) " +
			 "INNER JOIN Fact_Acct fact ON (fact.dateacct between p.startdate and p.enddate " +
             "                      and fact.ad_client_id = c.ad_client_id) " +
			 "WHERE c.PA_ReportCubeJP_ID = ? " +
			 "AND fact.updated > c.LastRecalculated";

			log.log (Level.FINE, sql);

			start = System.currentTimeMillis();
			KeyNamePair[] changedPeriods = DB.getKeyNamePairs(sql, false, getPA_ReportCubeJP_ID());
			elapsed = (System.currentTimeMillis() - start)/1000;
			if (log.isLoggable(Level.FINE))log.log(Level.FINE, "Selecting changed periods took:" + elapsed + "s");

			if (changedPeriods != null && changedPeriods.length > 0 )
			{
				periodList.append(" (");
				for (KeyNamePair p : changedPeriods )
				{
					periodList.append(p.getID() + ", ");
					periodNames.append(p.getName() + ", ");
				}
				periodList.delete(periodList.length() - 2, periodList.length());
				periodList.append(" )");

				if (log.isLoggable(Level.FINE))log.log(Level.FINE, "Periods requiring update: " + periodNames.toString());
			}
			else
				return "Nothing to update in " + getName();

			periods = periodList.toString();
			where += (" AND C_Period_ID IN " + periods);
		}


		if ( !force )
		{
			String lockSQL = "UPDATE PA_ReportCubeJP SET Processing = 'Y' " +
			"WHERE Processing = 'N' AND PA_ReportCubeJP_ID = " + getPA_ReportCubeJP_ID();
			int locked = DB.executeUpdateEx(lockSQL, get_TrxName());
			if (locked != 1)
			{
				throw new AdempiereException("Unable to lock cube for update:" + getName());
			}
		}
		try
		{
			// delete
			String delSQL = "DELETE FROM Fact_Acct_SummaryJP fas " + where;
			if (log.isLoggable(Level.FINE))log.log(Level.FINE, "Delete sql: " + delSQL);
			start = System.currentTimeMillis();
			int deleted = DB.executeUpdateEx(delSQL, get_TrxName());
			elapsed = (System.currentTimeMillis() - start)/1000;
			result += "Deleted " + deleted + " in " + elapsed + " s;";

			if (log.isLoggable(Level.FINE))log.log(Level.FINE, result);

			// insert
			StringBuilder insert = new StringBuilder("INSERT " +
					"INTO FACT_ACCT_SUMMARYJP (PA_ReportCubeJP_ID , AD_Client_ID, " +
					"AD_Org_ID, Created, CreatedBy, Updated, UpdatedBy, IsActive, " +
					"C_AcctSchema_ID, Account_ID, PostingType, " +
			"GL_Budget_ID, C_Period_ID, DateAcct, AmtAcctDr, AmtAcctCr, Qty");

			StringBuilder select = new StringBuilder(" ) SELECT " +
					"?, f.AD_CLIENT_ID, f.AD_ORG_ID, " +
					"max(f.Created), max(f.CreatedBy), max(f.Updated), max(f.UpdatedBy), 'Y', " +
					"f.C_ACCTSCHEMA_ID, f.ACCOUNT_ID, f.POSTINGTYPE, GL_Budget_ID, " +
					"p.c_period_id,	p.StartDate, COALESCE(SUM(AmtAcctDr),0), COALESCE(SUM(AmtAcctCr),0), " +
			"COALESCE(SUM(Qty),0)");
			String from = " FROM fact_acct f " +
			" INNER JOIN C_Period p ON ( f.C_Period_ID = p.C_Period_ID ) " +
			" INNER JOIN C_Year y ON ( p.C_Year_ID = y.C_Year_ID ) " +
			" WHERE y.C_Calendar_ID = ? AND f.AD_Client_ID = ? ";
			if ( getLastRecalculated() != null && !reset )
				from += "AND  p.C_Period_ID IN " + periods;

			StringBuilder groups = new StringBuilder(" GROUP BY " +
					"f.AD_CLIENT_ID, f.AD_ORG_ID, f.C_ACCTSCHEMA_ID, f.ACCOUNT_ID, " +
			"f.POSTINGTYPE, GL_Budget_ID, p.c_period_id, p.StartDate ");

			ArrayList<String> values = new ArrayList<String>();

			if ( isProductDim() )
				values.add("M_Product_ID");
			if ( isBPartnerDim() )
				values.add("C_BPartner_ID");
			if ( isProjectDim() )
				values.add("C_Project_ID");
			if ( isOrgTrxDim() )
				values.add("AD_OrgTrx_ID");
			if ( isSalesRegionDim() )
				values.add("C_SalesRegion_ID");
			if ( isActivityDim() )
				values.add("C_Activity_ID");
			if ( isCampaignDim() )
				values.add("C_Campaign_ID");
			if ( isLocToDim() )
				values.add("C_LocTo_ID");
			if ( isLocFromDim() )
				values.add("C_LocFrom_ID");
			if ( isUser1Dim() )
				values.add("User1_ID");
			if ( isUser2Dim() )
				values.add("User2_ID");
			if ( isUserElement1Dim() )
				values.add("UserElement1_ID");
			if ( isUserElement2Dim() )
				values.add("UserElement2_ID");
			if ( isSubAcctDim() )
				values.add("C_SubAcct_ID");
			if ( isProjectPhaseDim() )
				values.add("C_ProjectPhase_ID");
			if ( isProjectTaskDim() )
				values.add("C_ProjectTask_ID");
			if ( isContractDimJP() )
			{
				values.add("JP_Contract_ID");
				values.add("JP_ContractContent_ID");
				values.add("JP_ContractProcPeriod_ID");
			}

			//  --(CASE v.IsGL_Category_ID WHEN 'Y' THEN f."GL_Category_ID END) GL_Category_ID

			Iterator<String> iter = values.iterator();
			while ( iter.hasNext() )
			{
				String dim = iter.next();
				insert.append(", " + dim );
				select.append(", f." + dim);
				groups.append(", f." + dim);
			}


			String sql = insert.append(select.toString()).append(from).append(groups.toString()).toString();
			if (log.isLoggable(Level.FINE))log.log(Level.FINE, sql);
			Object[] params = new Object[] { getPA_ReportCubeJP_ID(), getC_Calendar_ID(), getAD_Client_ID() };

			start = System.currentTimeMillis();
			int rows = DB.executeUpdateEx(sql, params, get_TrxName());
			long seconds = (System.currentTimeMillis() - start)/1000;

			String insertResult = "Inserted " + rows  + " in " + seconds + " s.";
			if (log.isLoggable(Level.FINE))log.log(Level.FINE, insertResult);
			result += insertResult;


			// set timestamp
			String tsSQL = "SELECT max(fas.Updated)" +
			" FROM Fact_Acct_SummaryJP fas" +
			" WHERE fas.PA_ReportCubeJP_ID = " + getPA_ReportCubeJP_ID();
			ts = DB.getSQLValueTS(get_TrxName(), tsSQL);
			if (log.isLoggable(Level.FINE))log.log(Level.FINE, "Last updated: " + ts);

		}
		catch (DBException e)
		{
			// failure results in null timestamp => rebuild on next run
			// nothing else to do
			if (log.isLoggable(Level.FINE))log.log(Level.FINE, getName() + " update failed:" + e.getMessage());
		}
		finally
		{
			// unlock
			String unlockSQL = "UPDATE PA_ReportCubeJP SET Processing = 'N', " +
			"LastRecalculated = " + ( ts == null ? "null" : "?") +
			" WHERE PA_ReportCubeJP_ID = " + getPA_ReportCubeJP_ID();
			Object[] parameters = ts == null ? new Object[] {} : new Object[] {ts};
			DB.executeUpdateEx(unlockSQL, parameters, get_TrxName());
		}
		return result;
	}



	public String updateBS(int C_AcctSchema_ID ,int C_Calendar_ID, int C_Year_ID, MPeriod m_Period, boolean isDeleteDataOnlyJP )
	{
		String result = null;
		long start = System.currentTimeMillis();

		try
		{
			// delete
			String delSQL = "DELETE FROM Fact_Acct_BS_JP WHERE PA_ReportCubeJP_ID = ? AND C_AcctSchema_ID = ? AND C_Period_ID = ?";
			Object[] deleteParams = new Object[] { getPA_ReportCubeJP_ID(), C_AcctSchema_ID, m_Period.getC_Period_ID() };
			DB.executeUpdateEx(delSQL, deleteParams, get_TrxName());

			if(isDeleteDataOnlyJP)
				return "";

			// insert
			StringBuilder insert = new StringBuilder("INSERT " +
					"INTO FACT_ACCT_BS_JP (PA_ReportCubeJP_ID , AD_Client_ID, " +
					"AD_Org_ID, Created, CreatedBy, Updated, UpdatedBy, IsActive, " +
					"C_AcctSchema_ID, Account_ID, PostingType, " +
			"GL_Budget_ID, C_Period_ID, DateAcct, AmtAcctDr, AmtAcctCr, Qty");

			StringBuilder select = new StringBuilder(" ) SELECT " +
					"?, f.AD_CLIENT_ID, f.AD_ORG_ID, " +								//?1
					"max(f.Created), max(f.CreatedBy), max(f.Updated), max(f.UpdatedBy), 'Y', " +
					"f.C_ACCTSCHEMA_ID, f.ACCOUNT_ID, f.POSTINGTYPE, GL_Budget_ID, " +
					"?,	?, COALESCE(SUM(AmtAcctDr),0), COALESCE(SUM(AmtAcctCr),0), " + //?2, ?3
			"COALESCE(SUM(Qty),0)");
			String from = " FROM Fact_Acct_SummaryJP f " +
					" INNER JOIN C_ElementValue ev ON ( f.Account_ID = ev.C_ElementValue_ID ) " +
			" WHERE f.C_AcctSchema_ID = ? AND f.PA_ReportCubeJP_ID = ? AND f.DateAcct <= ?  AND ev.AccountType IN ('A','L','O')";	//?4, ?5, ?6

			StringBuilder groups = new StringBuilder(" GROUP BY " +
					"f.AD_CLIENT_ID, f.AD_ORG_ID, f.C_ACCTSCHEMA_ID, f.ACCOUNT_ID, " +
			"f.POSTINGTYPE, f.GL_Budget_ID");

			ArrayList<String> values = new ArrayList<String>();

			if ( isProductDim() )
				values.add("M_Product_ID");
			if ( isBPartnerDim() )
				values.add("C_BPartner_ID");
			if ( isProjectDim() )
				values.add("C_Project_ID");
			if ( isOrgTrxDim() )
				values.add("AD_OrgTrx_ID");
			if ( isSalesRegionDim() )
				values.add("C_SalesRegion_ID");
			if ( isActivityDim() )
				values.add("C_Activity_ID");
			if ( isCampaignDim() )
				values.add("C_Campaign_ID");
			if ( isLocToDim() )
				values.add("C_LocTo_ID");
			if ( isLocFromDim() )
				values.add("C_LocFrom_ID");
			if ( isUser1Dim() )
				values.add("User1_ID");
			if ( isUser2Dim() )
				values.add("User2_ID");
			if ( isUserElement1Dim() )
				values.add("UserElement1_ID");
			if ( isUserElement2Dim() )
				values.add("UserElement2_ID");
			if ( isSubAcctDim() )
				values.add("C_SubAcct_ID");
			if ( isProjectPhaseDim() )
				values.add("C_ProjectPhase_ID");
			if ( isProjectTaskDim() )
				values.add("C_ProjectTask_ID");
			if ( isContractDimJP() )
			{
				values.add("JP_Contract_ID");
				values.add("JP_ContractContent_ID");
				values.add("JP_ContractProcPeriod_ID");
			}

			//  --(CASE v.IsGL_Category_ID WHEN 'Y' THEN f."GL_Category_ID END) GL_Category_ID

			Iterator<String> iter = values.iterator();
			while ( iter.hasNext() )
			{
				String dim = iter.next();
				insert.append(", " + dim );
				select.append(", f." + dim);
				groups.append(", f." + dim);
			}


			String sql = insert.append(select.toString()).append(from).append(groups.toString()).toString();
			Object[] params = new Object[] { getPA_ReportCubeJP_ID(), m_Period.getC_Period_ID(), m_Period.getStartDate(), C_AcctSchema_ID, getPA_ReportCubeJP_ID(), m_Period.getEndDate() };

			int rows = DB.executeUpdateEx(sql, params, get_TrxName());
			long seconds = (System.currentTimeMillis() - start)/1000;

			result = "Inserted " + rows  + " in " + seconds + " seconds.";
			if (log.isLoggable(Level.FINE))log.log(Level.FINE, result);


		}
		catch (DBException e)
		{
			// failure results in null timestamp => rebuild on next run
			// nothing else to do
			if (log.isLoggable(Level.FINE))log.log(Level.FINE, getName() + " update failed:" + e.getMessage());
		}
		finally
		{
			// unlock
//			String unlockSQL = "UPDATE PA_ReportCubeJP SET Processing = 'N', " +
//			"LastRecalculated = " + ( ts == null ? "null" : "?") +
//			" WHERE PA_ReportCubeJP_ID = " + getPA_ReportCubeJP_ID();
//			Object[] parameters = ts == null ? new Object[] {} : new Object[] {ts};
//			DB.executeUpdateEx(unlockSQL, parameters, get_TrxName());
		}

		return result;
	}
}
