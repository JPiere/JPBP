package jpiere.base.plugin.org.adempiere.process;

import java.sql.Timestamp;
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

import java.util.logging.Level;

import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;

import jpiere.base.plugin.org.adempiere.model.MContractCalender;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;


/** JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class AbstractContractProcess extends SvrProcess 
{
	private int Record_ID = 0;
	private MContractContent contractContent = null;
		
	private String p_JP_ContractProcessUnit = null;
	private int p_JP_ContractCalender_ID = 0;
	private int p_JP_ContractProcPeriodG_ID = 0;
	private int p_JP_ContractProcPeriod_ID = 0;
	private String p_JP_ContractProcessValue = null;
	private Timestamp p_DateAcct = null;
	private Timestamp p_DateDoc = null;
	private String p_DocAction = null;
	private int p_AD_Org_ID = 0;
	private int p_JP_ContractCategory_ID = 0;
	private int p_C_DocType_ID = 0;
	private String p_DocBaseType = null;
	boolean p_IsCreateBaseDocJP = false;
	
	@Override
	protected void prepare() 
	{
		Record_ID = getRecord_ID();
		if(Record_ID > 0)
		{
			contractContent = new MContractContent(getCtx(), Record_ID, get_TrxName());
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
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if

		}//fo
		
	}
	
	@Override
	protected String doIt() throws Exception 
	{
		
		if(p_DateAcct==null)
			p_DateAcct = MContractProcPeriod.get(getCtx(), getJP_ContractProcPeriod_ID()).getDateAcct();
		
		if(p_DateDoc ==null)
			p_DateAcct = MContractProcPeriod.get(getCtx(), getJP_ContractProcPeriod_ID()).getDateDoc();
		
		if(getJP_ContractProcPeriod_ID() == 0)
		{
			MContractCalender calender = MContractCalender.get(getCtx(), getJP_ContractCalender_ID());
			MContractProcPeriod period = calender.getContractProcessPeriod(getCtx(), getDateAcct());
			p_JP_ContractProcPeriod_ID = period.getJP_ContractProcPeriod_ID();
		}
		
		
		return null;
	}
	
	public MContractContent getMContractContent()
	{
		return contractContent;
	}
	
	
	public String getJP_ContractProcessUnit()
	{
		return p_JP_ContractProcessUnit ;
	}
	
	public int getJP_ContractCalender_ID()
	{
		return p_JP_ContractCalender_ID ;
	}
	

	public int getJP_ContractProcPeriodG_ID()
	{
		return p_JP_ContractProcPeriodG_ID ;
	}
	
	public int getJP_ContractProcPeriod_ID()
	{
		return p_JP_ContractProcPeriod_ID ;
	}
	
	public String getJP_ContractProcessValue()
	{
		return p_JP_ContractProcessValue ;
	}
	
	public Timestamp getDateAcct()
	{
		return p_DateAcct ;
	}	

//	public void setDateAcct(Timestamp date)
//	{
//		p_DateAcct = date;
//	}	
	
	public Timestamp getDateDoc()
	{
		return p_DateDoc ;
	}
	
//	public void setDateDoc(Timestamp date)
//	{
//		p_DateDoc = date;
//	}	

	public String getDocAction()
	{
		return p_DocAction ;
	}
	
	public int getAD_Org_ID()
	{
		return p_AD_Org_ID ;
	}
	
	public int getJP_ContractCategory_ID()
	{
		return p_JP_ContractCategory_ID ;
	}
	
	public int getC_DocType_ID()
	{
		return p_C_DocType_ID ;
	}
	
	public String getDocBaseType()
	{
		return p_DocBaseType ;
	}
	
	public boolean isCreateBaseDocJP()
	{
		return p_IsCreateBaseDocJP ;
	}
	
	
}
