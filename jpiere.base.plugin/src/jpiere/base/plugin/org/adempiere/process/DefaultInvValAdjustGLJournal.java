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

import jpiere.base.plugin.org.adempiere.model.MInvValAdjust;
import jpiere.base.plugin.org.adempiere.model.MInvValAdjustLine;
import jpiere.base.plugin.org.adempiere.model.MInvValProfile;
import jpiere.base.plugin.org.adempiere.model.MInvValProfileOrg;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MConversionType;
import org.compiere.model.MJournal;
import org.compiere.model.MJournalLine;
import org.compiere.model.MPeriod;
import org.compiere.process.DocAction;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

/**
 * JPIERE-0163 Inventory Valuation Adjust Doc
 *
 *  Default Create GL Journal for Inventory Valuation Adjust
 *
 *  @author Hideaki Hagiwara
 *
 */
public class DefaultInvValAdjustGLJournal extends SvrProcess {

	MInvValProfile m_InvValProfile = null;
	MInvValAdjust m_InvValAdjust = null;
	int Record_ID = 0;

	@Override
	protected void prepare()
	{
		Record_ID = getRecord_ID();
		if(Record_ID > 0)
		{
			m_InvValAdjust = new MInvValAdjust(getCtx(), Record_ID, null);
			m_InvValProfile = MInvValProfile.get(getCtx(), m_InvValAdjust.getJP_InvValProfile_ID());
		}else{
			log.log(Level.SEVERE, "Record_ID <= 0 ");
		}
	}

	@Override
	protected String doIt() throws Exception
	{
		MInvValProfileOrg[] orgs = m_InvValProfile.getOrgs();
		int C_AcctSchema_ID = m_InvValProfile.getC_AcctSchema_ID();
		
		if(orgs==null || orgs.length < 1)
		{
			//Can Not Generate GL Journal ;
			//Inventory valuation profile is not setting Organization
			throw new AdempiereException(Msg.getMsg(getCtx(), "JP_CanNotGenerateGLJournal")+" - "+Msg.getMsg(getCtx(), "JP_InvValProfileNotOrganization"));
		}
		
		for(int i = 0; i < orgs.length; i++)
		{
			MInvValAdjustLine[] lines = m_InvValAdjust.getLines("AND AD_OrgTrx_ID="+orgs[i].getAD_Org_ID(), "");
			
			//Befor Check
			boolean isDifferenceAmt = false;
			for(int j = 0 ; j < lines.length; j++)
			{
				if(lines[j].getDifferenceAmt().compareTo(Env.ZERO)!=0)
					isDifferenceAmt = true;
			}
			
			if(!isDifferenceAmt)
				break;
			
			
			MJournal journal = new MJournal(getCtx(), 0 , get_TrxName());
			journal.setAD_Org_ID(orgs[i].getAD_Org_ID());
			journal.setC_AcctSchema_ID(C_AcctSchema_ID);
			journal.setC_DocType_ID(m_InvValProfile.getC_DocType_ID());
			journal.setGL_Category_ID(m_InvValProfile.getC_DocType().getGL_Category_ID());
			journal.setPostingType(MJournal.POSTINGTYPE_Actual);
			journal.setC_Currency_ID(m_InvValProfile.getC_Currency_ID());
			journal.setC_ConversionType_ID(m_InvValProfile.getC_ConversionType_ID());
			journal.setDateDoc(m_InvValAdjust.getDateAcct());
			journal.setDateAcct(m_InvValAdjust.getDateAcct());
			journal.setC_Period_ID(MPeriod.getC_Period_ID(getCtx(),  m_InvValAdjust.getDateAcct(), orgs[i].getAD_Org_ID()));
			journal.setDescription(Msg.getElement(getCtx(), MInvValAdjust.COLUMNNAME_JP_InvValAdjust_ID)+" : "+m_InvValAdjust.getDocumentNo());
			journal.save(get_TrxName());
			
			int lineNo = 0;
			for(int j = 0 ; j < lines.length; j++)
			{

				if(lines[j].getDifferenceAmt().compareTo(Env.ZERO)==0)
					continue;
				
				lineNo = lineNo + 10;
				MJournalLine jl1 = new MJournalLine(getCtx(), 0, get_TrxName());
				jl1.setGL_Journal_ID(journal.getGL_Journal_ID());
				jl1.setAD_Org_ID(orgs[i].getAD_Org_ID());
				jl1.setLine(lineNo);
				jl1.setAccount_ID(lines[j].getAccount_ID());
				jl1.setC_Currency_ID(m_InvValProfile.getC_Currency_ID());
				jl1.setC_ConversionType_ID(MConversionType.getDefault(getAD_Client_ID()));
				jl1.setM_Product_ID(lines[j].getM_Product_ID());
				jl1.setDateAcct(m_InvValAdjust.getDateAcct());
				jl1.setDescription(Msg.getElement(getCtx(), MInvValAdjust.COLUMNNAME_JP_InvValAdjust_ID)+" : "+m_InvValAdjust.getDocumentNo()+" - "+lines[j].getLine());
				if(lines[j].getDifferenceAmt().compareTo(Env.ZERO) > 0)
				{
					jl1.setAmtSourceDr(lines[j].getDifferenceAmt());
					jl1.setAmtAcctDr(lines[j].getDifferenceAmt());
				}else{
					jl1.setAmtSourceCr(lines[j].getDifferenceAmt().negate());
					jl1.setAmtAcctCr(lines[j].getDifferenceAmt().negate());
				}
				jl1.saveEx(get_TrxName());
				
				lineNo = lineNo + 10;
				MJournalLine jl2 = new MJournalLine(getCtx(), 0, get_TrxName());
				jl2.setGL_Journal_ID(journal.getGL_Journal_ID());
				jl2.setAD_Org_ID(orgs[i].getAD_Org_ID());
				jl2.setLine(lineNo);
				jl2.setAccount_ID(m_InvValProfile.getAccount_ID());
				jl2.setC_Currency_ID(m_InvValProfile.getC_Currency_ID());
				jl2.setC_ConversionType_ID(MConversionType.getDefault(getAD_Client_ID()));
				jl2.setM_Product_ID(lines[j].getM_Product_ID());
				jl2.setDateAcct(m_InvValAdjust.getDateAcct());
				jl2.setDescription(Msg.getElement(getCtx(), MInvValAdjust.COLUMNNAME_JP_InvValAdjust_ID)+" : "+m_InvValAdjust.getDocumentNo()+" - "+lines[j].getLine());
				if(lines[j].getDifferenceAmt().compareTo(Env.ZERO) > 0)
				{
					jl2.setAmtSourceCr(lines[j].getDifferenceAmt());
					jl2.setAmtAcctCr(lines[j].getDifferenceAmt());
				}else{
					jl2.setAmtSourceDr(lines[j].getDifferenceAmt().negate());
					jl2.setAmtAcctDr(lines[j].getDifferenceAmt().negate());
				}
				jl2.saveEx(get_TrxName());
				
				if(lines[j].getDifferenceAmt().compareTo(Env.ZERO) > 0)
				{
					lines[j].setJP_JournalLineDr_ID(jl1.getGL_JournalLine_ID());
					lines[j].setJP_JournalLineCr_ID(jl2.getGL_JournalLine_ID());
				}else{
					lines[j].setJP_JournalLineDr_ID(jl2.getGL_JournalLine_ID());
					lines[j].setJP_JournalLineCr_ID(jl1.getGL_JournalLine_ID());
				}
				
				lines[j].saveEx(get_TrxName());
				
			}//for j
			
			journal.processIt(Util.isEmpty(m_InvValProfile.getDocAction())==true ? DocAction.ACTION_Complete : m_InvValProfile.getDocAction());
			journal.saveEx(get_TrxName());
			addBufferLog(0, null, null, journal.getDocumentNo(), MJournal.Table_ID, journal.getGL_Journal_ID());
			
		}//for i
		
		return null;
	}

}
