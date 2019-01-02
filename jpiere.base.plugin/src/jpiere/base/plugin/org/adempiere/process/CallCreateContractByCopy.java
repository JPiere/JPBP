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

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.util.ProcessUtil;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Trx;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractT;




/** JPIERE-0432 Create Contarct By Copy
*
* @author Hideaki Hagiwara
*
*/
public class CallCreateContractByCopy extends SvrProcess {

	MContractT m_ContractTemplate = null;
	int Record_ID = 0;

	private  String p_JP_ContractTabLevel = null;
	private  static final String JP_ContractTabLevel_Document  = "CD";
	private  static final String JP_ContractTabLevel_Content  = "CC";


	@Override
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();

			if (para[i].getParameter() == null)
			{
				;

			}else if (name.equals("JP_ContractTabLevel")){

				p_JP_ContractTabLevel = para[i].getParameterAsString();

			}else{
//				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if
		}

		Record_ID = getRecord_ID();
		if(Record_ID > 0)
		{
			if(p_JP_ContractTabLevel.equals(JP_ContractTabLevel_Document))
			{
				MContract m_Contract = new MContract(getCtx(), Record_ID, get_TrxName());
				m_ContractTemplate = new MContractT(getCtx(),m_Contract.getJP_ContractT_ID(), get_TrxName());

			}else if(p_JP_ContractTabLevel.equals(JP_ContractTabLevel_Content)){

				MContractContent m_ContractContent = new MContractContent(getCtx(), Record_ID, get_TrxName());
				m_ContractTemplate = new MContractT(getCtx(), m_ContractContent.getParent().getJP_ContractT_ID(), get_TrxName());
			}

		}else{
			log.log(Level.SEVERE, "Record_ID <= 0 ");
		}
	}

	@Override
	protected String doIt() throws Exception
	{
		ProcessInfo pi = new ProcessInfo("Title", 0, getTable_ID(), Record_ID);
		if(Util.isEmpty(m_ContractTemplate.getClassname()))
		{
			pi.setClassName("jpiere.base.plugin.org.adempiere.process.DefaultCreateContractByCopy");
		}else{
			pi.setClassName(m_ContractTemplate.getClassname());
		}
		pi.setAD_Client_ID(getAD_Client_ID());
		pi.setAD_User_ID(getAD_User_ID());
		pi.setAD_PInstance_ID(getAD_PInstance_ID());
		pi.setParameter(getParameter());
		boolean isOK = ProcessUtil.startJavaProcess(getCtx(), pi, Trx.get(get_TrxName(), true), false, Env.getProcessUI(getCtx()));

		if(isOK)
		{
			;
		}else{
			throw new AdempiereException(pi.getSummary());
		}

		return pi.getSummary();

	}

}
