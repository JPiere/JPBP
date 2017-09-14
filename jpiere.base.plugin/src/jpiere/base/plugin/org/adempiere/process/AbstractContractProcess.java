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

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.logging.Level;

import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractCalender;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;
import jpiere.base.plugin.org.adempiere.model.MContractProcess;


/** JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class AbstractContractProcess extends SvrProcess 
{
	protected int Record_ID = 0;
	protected MContractContent m_ContractContent = null;
		
	protected String p_JP_ContractProcessUnit = null;
	protected int p_JP_ContractCalender_ID = 0;
	protected int p_JP_ContractProcPeriodG_ID = 0;
	protected int p_JP_ContractProcPeriod_ID = 0;
	protected String p_JP_ContractProcessValue = null;
	protected Timestamp p_DateAcct = null;
	protected Timestamp p_DateDoc = null;
	protected Timestamp p_DateOrdered = null;
	protected Timestamp p_DatePromised = null;
	protected String p_DocAction = null;
	protected int p_AD_Org_ID = 0;
	protected int p_JP_ContractCategory_ID = 0;
	protected int p_C_DocType_ID = 0;
	protected String p_DocBaseType = null;
	protected boolean p_IsCreateBaseDocJP = false;
	protected boolean p_IsRecordCommitJP = false;
	protected String p_JP_ContractProcessTraceLevel = null;
	
	protected int p_JP_ContractProcess_ID = 0; //use to create derivative Doc

	/** JP_ContractProcessUnit */
	public static final String JP_ContractProcessUnit_ContractProcessPeriod  = "CPP";
	public static final String JP_ContractProcessUnit_ContractProcessValueofContractProcessPeriod  = "CPV";
	public static final String JP_ContractProcessUnit_AccountDate  = "DAT";
	public static final String JP_ContractProcessUnit_DocumentDate  = "DDT";
	public static final String JP_ContractProcessUnit_ContractProcessPeriodGroup  = "GPP";
	public static final String JP_ContractProcessUnit_ContractProcessValueofContractProcessPeriodGroup  = "GPV";
	public static final String JP_ContractProcessUnit_PerContractContent  = "PCC";

	/** JP_ContractProcessTraceLevel */
	public static final String JP_ContractProcessTraceLevel_ALL  = "ALL";
	public static final String JP_ContractProcessTraceLevel_ErrorOnly  = "ERR";
	public static final String JP_ContractProcessTraceLevel_NoLog  = "NON";
	public static final String JP_ContractProcessTraceLevel_Warning  = "WAR";
	public static final String JP_ContractProcessTraceLevel_Fine  = "FIN";
	
	
	@Override
	protected void prepare() 
	{
		Record_ID = getRecord_ID();
		if(Record_ID > 0)
		{
			m_ContractContent = new MContractContent(getCtx(), Record_ID, get_TrxName());
		}else{
			log.log(Level.SEVERE, "Record_ID <= 0 ");
		}
		
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();

			if (para[i].getParameter() == null){
				;
			}else if (name.equals("JP_ContractProcessUnit")){
				p_JP_ContractProcessUnit = para[i].getParameterAsString();
			}else if (name.equals("JP_ContractCalender_ID")){
				p_JP_ContractCalender_ID = para[i].getParameterAsInt();		
			}else if (name.equals("JP_ContractProcPeriodG_ID")){
				p_JP_ContractProcPeriodG_ID = para[i].getParameterAsInt();						
			}else if (name.equals("JP_ContractProcPeriod_ID")){
				p_JP_ContractProcPeriod_ID = para[i].getParameterAsInt();					
			}else if (name.equals("JP_ContractProcessValue")){
				p_JP_ContractProcessValue = para[i].getParameterAsString();
			}else if (name.equals("DateAcct")){
				p_DateAcct = para[i].getParameterAsTimestamp();
			}else if (name.equals("DateDoc")){
				p_DateDoc = para[i].getParameterAsTimestamp();
			}else if (name.equals("DatePromised")){
				p_DatePromised = para[i].getParameterAsTimestamp();
			}else if (name.equals("DateOrdered")){
				p_DateOrdered = para[i].getParameterAsTimestamp();
			}else if (name.equals("DocAction")){
				p_DocAction = para[i].getParameterAsString();
			}else if (name.equals("AD_Org_ID")){
				p_AD_Org_ID = para[i].getParameterAsInt();
			}else if (name.equals("JP_ContractCategory_ID")){
				p_JP_ContractCategory_ID = para[i].getParameterAsInt();
			}else if (name.equals("C_DocType_ID")){
				p_C_DocType_ID = para[i].getParameterAsInt();
				p_JP_ContractCategory_ID = para[i].getParameterAsInt();
			}else if (name.equals("DocBaseType")){
				p_DocBaseType = para[i].getParameterAsString();
			}else if (name.equals("IsCreateBaseDocJP")){
				p_IsCreateBaseDocJP = para[i].getParameterAsBoolean();	
			}else if (name.equals("IsRecordCommitJP")){
				p_IsRecordCommitJP = para[i].getParameterAsBoolean();	
			}else if (name.equals("JP_ContractProcessTraceLevel")){
				p_JP_ContractProcessTraceLevel = para[i].getParameterAsString();	
			}else if (name.equals("JP_ContractProcess_ID")){				
				p_JP_ContractProcess_ID = para[i].getParameterAsInt();
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if

		}//fo
		
	}
	
	@Override
	protected String doIt() throws Exception 
	{
		return null;
	}
	
	
	protected Timestamp getDateDoc()
	{
		if(p_DateDoc !=null)
			return p_DateDoc;
		
		if(p_JP_ContractProcPeriod_ID > 0)
			return MContractProcPeriod.get(getCtx(), p_JP_ContractProcPeriod_ID).getDateDoc();
		
		
		return m_ContractContent.getDateDoc();
		
	}
	
	
	protected Timestamp getDateAcct()
	{
		if(p_DateAcct !=null)
			return p_DateAcct;
		
		if(p_JP_ContractProcPeriod_ID > 0)
			return MContractProcPeriod.get(getCtx(), p_JP_ContractProcPeriod_ID).getDateAcct();
		
		
		return m_ContractContent.getDateAcct();
		
	}
	
	protected Timestamp getDateOrdered()
	{
		if(p_DateOrdered != null)
			return p_DateOrdered;
		
		if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_SpotContract)
				&& p_JP_ContractProcessUnit.equals("PCC") 
				&& ( m_ContractContent.getDocBaseType().equals("SOO") ||  m_ContractContent.getDocBaseType().equals("POO")) )
		{
			
			return m_ContractContent.getDateOrdered();
			
		}
		
		
		if(p_DateDoc != null)
			return p_DateDoc;
		
		if(p_JP_ContractProcPeriod_ID > 0)
			return MContractProcPeriod.get(getCtx(), p_JP_ContractProcPeriod_ID).getDateDoc();
		
		
		return m_ContractContent.getDateOrdered();
		
	}
	
	protected Timestamp getOrderHeaderDatePromised(Timestamp dateFrom)
	{
		if(p_DatePromised != null)
			return p_DatePromised;
		
		if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_SpotContract)
				&& p_JP_ContractProcessUnit.equals("PCC") 
				&& ( m_ContractContent.getDocBaseType().equals("SOO") ||  m_ContractContent.getDocBaseType().equals("POO")) )
		{
			
			return m_ContractContent.getDatePromised();
			
		}
		
		if(dateFrom != null)
		{
			LocalDateTime dateAcctLocal = dateFrom.toLocalDateTime();
			dateAcctLocal = dateAcctLocal.plusDays(m_ContractContent.getDeliveryTime_Promised());
			return Timestamp.valueOf(dateAcctLocal) ;
		}
		
		if(getDateAcct() != null )
		{
			LocalDateTime dateAcctLocal = getDateAcct().toLocalDateTime();
			dateAcctLocal = dateAcctLocal.plusDays(m_ContractContent.getDeliveryTime_Promised());
			return Timestamp.valueOf(dateAcctLocal) ;
		}
		
		if(getDateDoc() != null )
		{
			LocalDateTime dateAcctLocal = getDateDoc().toLocalDateTime();
			dateAcctLocal = dateAcctLocal.plusDays(m_ContractContent.getDeliveryTime_Promised());
			return Timestamp.valueOf(dateAcctLocal) ;
		}
		
		return null;
	}
	
	protected Timestamp getOrderLineDatePromised(MContractLine m_Contractline)
	{
		if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_SpotContract)
				&& p_JP_ContractProcessUnit.equals("PCC") 
				&& ( m_ContractContent.getDocBaseType().equals("SOO") ||  m_ContractContent.getDocBaseType().equals("POO")) )
		{
			if(m_Contractline != null && m_Contractline.getDatePromised() != null)
				return m_Contractline.getDatePromised();
		}
		
		if(m_Contractline != null)
		{
			LocalDateTime dateAcctLocal = getDateAcct().toLocalDateTime();
			dateAcctLocal = dateAcctLocal.plusDays(m_Contractline.getDeliveryTime_Promised());
			return Timestamp.valueOf(dateAcctLocal) ;
		}
		
		return null;
	}
	
	protected String getDocAction()
	{
		if(!Util.isEmpty(p_DocAction))
			return p_DocAction;
		
		if(m_ContractContent.getJP_ContractProcess_ID () > 0 )
		{
			MContractProcess contractProcess = MContractProcess.get(getCtx(), m_ContractContent.getJP_ContractProcess_ID ());
			if(!Util.isEmpty(contractProcess.getDocAction()))
			{
				return contractProcess.getDocAction();
			}
		}
		
		return null;
	}
	
	
	protected int getJP_ContractProctPeriod_ID()
	{
		if(p_JP_ContractProcPeriod_ID > 0)
			return p_JP_ContractProcPeriod_ID;
		
		if(p_JP_ContractCalender_ID > 0)
		{
			MContractCalender cal = MContractCalender.get(getCtx(), p_JP_ContractCalender_ID);
			MContractProcPeriod period = cal.getContractProcessPeriod(getCtx(), getDateAcct());
			return period.getJP_ContractProcPeriod_ID();
		}
		
		
		if(p_DocBaseType.equals("SOO") || p_DocBaseType.equals("POO"))
		{
			if(m_ContractContent != null && m_ContractContent.getJP_ContractCalender_ID() > 0)
			{
				MContractCalender cal = MContractCalender.get(getCtx(), m_ContractContent.getJP_ContractCalender_ID() );
				MContractProcPeriod period = cal.getContractProcessPeriod(getCtx(), getDateAcct());
				return period.getJP_ContractProcPeriod_ID();
			}
		}
		
		return 0;
	}
	
	protected MContractProcPeriod getBaseDocContractProcPeriodFromDerivativeDocContractProcPeriod(int Derivative_ContractProcPeriod_ID)
	{
		MContractCalender calender = MContractCalender.get(getCtx(), m_ContractContent.getJP_ContractCalender_ID());
		if(calender == null)
			return null;
		
		MContractProcPeriod  derivativeDocContractProcPeriod = MContractProcPeriod.get(getCtx(), Derivative_ContractProcPeriod_ID);
		if(derivativeDocContractProcPeriod == null)
			return null;
		
		
		return calender.getContractProcessPeriod(getCtx(), derivativeDocContractProcPeriod.getStartDate(), derivativeDocContractProcPeriod.getEndDate());
	}
	
	protected int getJP_ContractProcess_ID()
	{
		return p_JP_ContractProcess_ID;
	}
	
	
	protected boolean isOverlapPeriodOrder(int JP_ContractProcPeriod_ID)
	{
		if(JP_ContractProcPeriod_ID > 0 && m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
		{
			MOrder[] orders= m_ContractContent.getOrderByContractPeriod(Env.getCtx(), JP_ContractProcPeriod_ID, get_TrxName());
			if(orders.length > 0)
			{
				return true;
			}
		}
		
		return false;
	}
	
	protected boolean isOverlapPeriodOrderLine(MContractLine line, int JP_ContractProcPeriod_ID)
	{
		if(JP_ContractProcPeriod_ID > 0 && m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
		{
			MOrderLine[] oLines = line.getOrderLineByContractPeriod(getCtx(), JP_ContractProcPeriod_ID, get_TrxName());
			if(oLines.length > 0)
			{
				return true;
			}
		}
		
		return false;
	}
	
	protected boolean isOverlapPeriodInOut(int JP_ContractProcPeriod_ID)
	{
		if(JP_ContractProcPeriod_ID > 0 && m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
		{
			MInOut[] inOuts = m_ContractContent.getInOutByContractPeriod(Env.getCtx(), JP_ContractProcPeriod_ID, get_TrxName());
			if(inOuts.length> 0 )
			{				
				return true;
			}
		}
		
		return false;
	}
	
	protected boolean isOverlapPeriodInOutLine(MContractLine line, int JP_ContractProcPeriod_ID)
	{
		if(JP_ContractProcPeriod_ID > 0 && m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
		{
			MInOutLine[] oLines = line.getInOutLineByContractPeriod(getCtx(), JP_ContractProcPeriod_ID, get_TrxName());
			if(oLines.length > 0)
			{
				return true;
			}
		}
		
		return false;
	}
	
	protected boolean isOverlapPeriodInvoice(int JP_ContractProcPeriod_ID)
	{
		if(JP_ContractProcPeriod_ID > 0 && m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
		{
			MInvoice[] invoices = m_ContractContent.getInvoiceByContractPeriod(Env.getCtx(), JP_ContractProcPeriod_ID, get_TrxName());
			if(invoices.length> 0 )
			{
				return true;
			}
		}
		
		return false;
	}
	
	
	protected boolean isOverlapPeriodInvoiceLine(MContractLine line, int JP_ContractProcPeriod_ID)
	{
		if(JP_ContractProcPeriod_ID > 0 && m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
		{
			MInvoiceLine[] iLines = line.getInvoiceLineByContractPeriod(getCtx(), JP_ContractProcPeriod_ID, get_TrxName());
			if(iLines.length> 0)
			{
				return true;
			}
		}
		
		return false;
	}
	
	
	//TODO
	protected void createLog(PO po, String msg, MContractLine contraceLine, String TraceLevel, boolean success)//TODO
	{
		addBufferLog(0, null, null, msg, po.get_Table_ID(), po.get_ID() ); //TODO メッセージ変更
		
		if(po.get_TableName().equals(MOrder.Table_Name))
		{
			;
		}
		
		if(Env.getProcessUI(getCtx()) != null && !p_JP_ContractProcessUnit.equals("PCC"))
		{
			Env.getProcessUI(getCtx()).statusUpdate(po.toString()); //TODO メッセージ変更
		}
		
		if(!success)
		{
			;//TODO 例外を投げる!!
		}
		
		//TODO 契約管理ログテーブルへのログの記録
	}
	
}
