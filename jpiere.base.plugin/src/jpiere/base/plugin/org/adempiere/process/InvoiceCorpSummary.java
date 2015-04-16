/******************************************************************************
 * Product: JPiere(ジェイピエール) - JPiere Base Plugin                       *
 * Copyright (C) Hideaki Hagiwara All Rights Reserved.                        *
 * このプログラムはGNU Gneral Public Licens Version2のもと公開しています。    *
 * このプログラムは自由に活用してもらう事を期待して公開していますが、         *
 * いかなる保証もしていません。                                               *
 * 著作権は萩原秀明(h.hagiwara@oss-erp.co.jp)が保持し、サポートサービスは     *
 * 株式会社オープンソース・イーアールピー・ソリューションズで                 *
 * 提供しています。サポートをご希望の際には、                                 *
 * 株式会社オープンソース・イーアールピー・ソリューションズまでご連絡下さい。 *
 * http://www.oss-erp.co.jp/                                                  *
 *****************************************************************************/

package jpiere.base.plugin.org.adempiere.process;

import java.sql.Timestamp;
import java.util.logging.Level;

import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;

public class InvoiceCorpSummary extends SvrProcess {


	private int			p_AD_Client_ID;;
	private Timestamp	p_DateInvoiced_From = null;	//Mandatory
	private Timestamp	p_DateInvoiced_To = null;	//Mandatory
	private int			p_AD_Org_ID = 0;
	private int			p_JP_CorporationGroup_ID = 0;
	private int			p_JP_Corporation_ID = 0;
	private int			p_C_Currency_ID = 0;
	private boolean		p_IsSOTrx = true;			//Mandatory



	@Override
	protected void prepare() {

		p_AD_Client_ID =getProcessInfo().getAD_Client_ID();
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("DateInvoiced")){
				p_DateInvoiced_From = (Timestamp)para[i].getParameter();
				p_DateInvoiced_To = (Timestamp)para[i].getParameter_To();

			}else if(name.equals("AD_Org_ID")){
				p_AD_Org_ID = para[i].getParameterAsInt();

			}else if(name.equals("JP_CorporationGroup_ID")){
				p_JP_CorporationGroup_ID = para[i].getParameterAsInt();

			}else if(name.equals("JP_Corporation_ID")){
				p_JP_Corporation_ID = para[i].getParameterAsInt();

			}else if(name.equals("C_Currency_ID")){
				p_C_Currency_ID = para[i].getParameterAsInt();

			}else if(name.equals("IsSOTrx")){
				p_IsSOTrx = para[i].getParameterAsBoolean();

			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if
		}//for

	}

	@Override
	protected String doIt() throws Exception {
		String DateFrom = p_DateInvoiced_From.toString();
		String DateTo = p_DateInvoiced_To.toString();

		StringBuilder sql = new StringBuilder ("INSERT INTO T_InvoiceCorpSummaryJP ")
							.append("(AD_PInstance_ID, AD_Client_ID, AD_Org_ID, JP_CorporationGroup_ID, JP_Corporation_ID, DocBaseTYpe, C_Currency_ID, IsSOTrx, TotalLines, GrandTotal,DateInvoiced)")
							.append(" SELECT ").append(getAD_PInstance_ID())
								.append(" , inv.AD_Client_ID, inv.AD_Org_ID,gc.JP_CorporationGroup_ID, bp.JP_Corporation_ID, dt.DocBaseType, inv.C_Currency_ID, inv.IsSOTrx,")
								.append(" CASE WHEN  dt.DocBaseType = 'ARC'THEN sum(inv.TotalLines)*-1 WHEN  dt.DocBaseType = 'APC'THEN sum(inv.TotalLines)*-1 ELSE sum(inv.TotalLines) END,")
								.append(" CASE WHEN  dt.DocBaseType = 'ARC'THEN sum(inv.GrandTotal)*-1 WHEN  dt.DocBaseType = 'APC'THEN sum(inv.GrandTotal)*-1 ELSE sum(inv.GrandTotal) END,")
								.append(" TO_DATE('").append(DateFrom.substring(0,10)).append(" 12:00:00','YYYY-MM-DD HH24:MI:SS')")
							.append(" FROM C_Invoice inv")
								.append(" INNER JOIN C_BPartner bp ON (inv.C_BPartner_ID = bp.C_BPartner_ID)")
								.append(" INNER JOIN C_DocType dt ON (inv.C_DocType_ID = dt.C_DocType_ID)")
								.append(" LEFT OUTER JOIN JP_GroupCorporations gc ON (bp.JP_Corporation_ID = gc.JP_Corporation_ID )")
							.append(" WHERE inv.docstatus in ('CO','CL') AND bp.JP_Corporation_ID is not null ")
								.append(" AND inv.AD_Client_ID = ").append(p_AD_Client_ID)
								.append(" AND inv.DateInvoiced >= TO_DATE('").append(DateFrom.substring(0,10)).append(" 00:00:00','YYYY-MM-DD HH24:MI:SS')")
								.append(" AND inv.DateInvoiced <= TO_DATE('").append(DateTo.substring(0,10)).append(" 23:59:59','YYYY-MM-DD HH24:MI:SS')");
						if(p_AD_Org_ID != 0)
						{
							sql = sql.append(" AND inv.AD_Org_ID=").append(p_AD_Org_ID);
						}

						if(p_JP_CorporationGroup_ID != 0)
						{
							sql = sql.append(" AND gc.JP_CorporationGroup_ID=").append(p_JP_CorporationGroup_ID);
						}

						if(p_JP_Corporation_ID != 0)
						{
							sql = sql.append(" AND bp.JP_Corporation_ID=").append(p_JP_Corporation_ID);
						}

						if(p_C_Currency_ID != 0)
						{
							sql = sql.append(" AND inv.C_Currency_ID=").append(p_C_Currency_ID);
						}

						if(p_IsSOTrx)
						{
							sql = sql.append(" AND inv.IsSOTrx='Y'");
						}else{
							sql = sql.append(" AND inv.IsSOTrx='N'");
						}

						sql = sql.append(" GROUP BY inv.AD_Client_ID,inv.AD_Org_ID,gc.JP_CorporationGroup_ID, bp.JP_Corporation_ID, dt.DocBaseType, inv.C_Currency_ID, inv.IsSOTrx ;");


		int InsertRecords = DB.executeUpdateEx(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Inserted Records=" + InsertRecords);


		return "Inserted Records=" + InsertRecords;
	}

}
