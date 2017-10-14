package jpiere.base.plugin.org.adempiere.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import org.adempiere.model.GenericPO;
import org.compiere.model.MBPBankAccount;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;

import jpiere.base.plugin.org.adempiere.model.MContractAcct;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MCorporation;
import jpiere.base.plugin.org.adempiere.model.MRecognition;

public class CreateInvoiceFromRecogManual extends SvrProcess {
	
	private Timestamp p_DateInvoiced = null;
	private Timestamp p_DateAcct = null;
	private String p_DocAction = null;
	
	
	@Override
	protected void prepare() 
	{
		ProcessInfoParameter[] para = getParameter();
		for(int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if(para[i].getParameter() == null){
				;
			}else if (name.equals("DateInvoiced")){
				
				p_DateInvoiced = para[i].getParameterAsTimestamp();
				
			}else if (name.equals("DateAcct")){
				
				p_DateAcct = para[i].getParameterAsTimestamp();
				
			}else if (name.equals("DocAction")){
				
				p_DocAction = para[i].getParameterAsString();
				
			}else {
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}
		}//for
	}
	
	@Override
	protected String doIt() throws Exception 
	{
		MRecognition[] recogs = getRocognitions();
		boolean isCreateHeader = false;
		boolean isRMA = false;
		for(int i = 0; i < recogs.length; i++)
		{
			if(i == 0)
			{
				MContractContent content =  MContractContent.get(getCtx(), recogs[i].getJP_ContractContent_ID());
				MContractAcct acct = MContractAcct.get(getCtx(), content.getJP_Contract_Acct_ID());
				if(acct.getJP_RecogToInvoicePolicy() == null ||
						acct.getJP_RecogToInvoicePolicy().equals(MContractAcct.JP_RECOGTOINVOICEPOLICY_NotCreateInvoiceFromRecognition))
				{
					//TODO 対象外
					break;
				}
			}
			
			//Check Recog Doc Status
			if(!recogs[i].getDocStatus().equals(DocAction.ACTION_Complete)
					&& !recogs[i].getDocStatus().equals(DocAction.ACTION_Close))
			{
				;//TODO Skip for Invalid Doc Status
				continue;
			}
			
			//Check RMA
			if(recogs[i].getM_RMA_ID() > 0)
			{
				;//TODO Skip for RMA
				continue;
			}
			
			
		}
		
		return null;
	}
	
	private MRecognition[] getRocognitions()
	{
		MRecognition[] recogs = null;
		ArrayList<MRecognition> list = new ArrayList<MRecognition>();
		String sql = " SELECT r.* FROM T_Selection t INNER JOIN JP_Recognition r ON (t.T_Selection_ID = r.JP_Recognition_ID) WHERE t.AD_PInstance_ID=? ORDER BY r.DateAcct ASC, r.JP_Recognition_ID ASC";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getAD_PInstance_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MRecognition (getCtx(), rs, get_TrxName()));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		recogs = new MRecognition[list.size()];
		list.toArray(recogs);
		
		return recogs;
	}
}
