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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import org.adempiere.util.Callback;
import org.adempiere.util.IProcessUI;
import org.compiere.model.MBankAccount;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoice;
import org.compiere.model.MPaySelection;
import org.compiere.model.MPaySelectionLine;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MBill;
import jpiere.base.plugin.org.adempiere.model.MBillLine;

/**
 * Create PaySelection from Bill
 *
 * JPIERE-0311:Create Pay Selection from Payment Request at Window
 * JPIERE-0312:Create Pay Selection from Payment Request at Info Window
 * JPIERE-0313:Create Pay Selection from Payment Request at Process
 *
 * JPIERE-0314:Create Pay Selection from Bill at Window
 * JPIERE-0315:Create Pay Selection from Bill at Info Window
 * JPIERE-0316:Create Pay Selection from Bill at Process
 *
 *  @author Hiroshi Iwama
 *
 *  Modified by Hideaki Hagiwara
 *
 */
public class CreatePaySelectionFromBill extends SvrProcess{

	/** BPartner					*/
	private int			p_C_BPartner_ID = 0;
	/** BPartner Group				*/
	private int			p_C_BP_Group_ID = 0;
	/** Promised Pay Data*/
	private Timestamp  p_JP_PromisedPayDate_From = null;
	private Timestamp  p_JP_PromisedPayDate_To = null;

	/** IsSOTrx */
	private boolean p_IsSOTrx = false;

	/**	Target Payment Rule			*/
	@SuppressWarnings("unused")
	private String		p_TargetPaymentRule = null;

	/** Target BankAccount */
	private int 			p_C_BankAccount_ID = 0;
	/** Target Promised Pay Date */
	private Timestamp  p_PayDate = null;

	/** Current Record ID			*/
	private int p_JP_Bill_ID = 0;
	
	private int p_C_PaySelection_ID = 0;

	private MPaySelection m_PaySelection = null;
	
	/** Called from document window */
	private boolean isCalledFromDocumentWindow = false;
	/** Called from process window */
	private boolean isCalledFromProcessWindow = false;
	/** Called from info window */
	private boolean isCalledFromInfoWindow = false;

	/** Payments requests selected in the info window */
	ArrayList<MBill> p_selectedBillList = new ArrayList<MBill>();

	/** Payments requests searched in the process window */
	ArrayList<MBill> p_searchedBillList = new ArrayList<MBill>();

	private MBankAccount m_BankAccount = null;

	/**
	 *  Prepare.
	 */
	protected void prepare()
	{

		String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE T_Selection.AD_PInstance_ID=? " +
				"AND T_Selection.T_Selection_ID = JP_Bill.JP_Bill_ID)";

		Collection<MBill> selectedBills = new Query(getCtx(), "JP_Bill", whereClause, get_TrxName())
						.setClient_ID()
						.setParameters(new Object[]{getAD_PInstance_ID()})
						.list();

		//get the current record
		p_JP_Bill_ID = getRecord_ID();

		//if got current record, then called from doc window
		if(p_JP_Bill_ID != 0){
			isCalledFromDocumentWindow = true;
		}

		//if records are selected, then called from info window
		if(selectedBills.size() != 0){
			isCalledFromInfoWindow = true;
			p_selectedBillList = (ArrayList<MBill>) selectedBills;
		}

		//otherwise, called from process window
		if(!isCalledFromInfoWindow && !isCalledFromDocumentWindow){
			isCalledFromProcessWindow  = true;
		}

	}//	prepare

	private IProcessUI 		processUI = null;
	private boolean 		isOpenDialog = false;
	private boolean 		isAskAnswer = true;
	private boolean 		isCreatePaySelection = false;

	protected String doIt() throws Exception
	{
		processUI = Env.getProcessUI(getCtx());

		if(isCalledFromDocumentWindow)
		{
			ProcessInfoParameter[] para = getParameter();
			for(int i = 0; i < para.length; i++)
			{
				String name = para[i].getParameterName();
				if (para[i].getParameter() == null)
					;
				else if(name.equals("TargetPaymentRule"))
					p_TargetPaymentRule = para[i].getParameterAsString();
				else if (name.equals("C_BankAccount_ID"))
					p_C_BankAccount_ID = para[i].getParameterAsInt();
				else if(name.equals("PayDate"))
					p_PayDate = (Timestamp)para[i].getParameter();
				else if (name.equals("C_PaySelection_ID"))
					p_C_PaySelection_ID = para[i].getParameterAsInt();
				else
					log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}
			
			isPaySelectionProcessed();

			if(p_JP_Bill_ID == 0)
				throw new Exception(Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), "JP_Bill_ID"));
			
			MBill m_Bill = new MBill(getCtx(), p_JP_Bill_ID, get_TrxName());
			if(!m_Bill.getDocStatus().equals(DocAction.STATUS_Completed) &&  !m_Bill.getDocStatus().equals(DocAction.STATUS_Closed))
			{
				throw new Exception(Msg.getMsg(getCtx(), "JP_Not_Completed_Document"));
			}
			
			p_IsSOTrx = m_Bill.isSOTrx();
			
			MPaySelection psel = new MPaySelection (getCtx(), p_C_PaySelection_ID, get_TrxName());
			if(p_C_PaySelection_ID == 0)
			{
				m_BankAccount = new MBankAccount(getCtx(), p_C_BankAccount_ID, get_TrxName());

				if(m_BankAccount.getC_Currency_ID() != m_Bill.getC_Currency_ID())
					throw new Exception(Msg.getMsg(getCtx(), "JP_DifferentCurrency"));
				
				psel.setAD_Org_ID(m_BankAccount.getAD_Org_ID());
				psel.setC_BankAccount_ID(p_C_BankAccount_ID);
				psel.setPayDate(p_PayDate);
				LocalDateTime dateTime = LocalDateTime.now();
				psel.setName(dateTime.truncatedTo(ChronoUnit.SECONDS).toString().replaceAll("T", " "));
				psel.set_ValueNoCheck("IsReceiptJP", m_Bill.isSOTrx());
				psel.saveEx(get_TrxName());
				
			}else {
				
				if(m_Bill.isSOTrx() != psel.get_ValueAsBoolean("IsReceiptJP"))
					throw new Exception("JP_UnexpectedError");
				
				m_BankAccount = new MBankAccount(getCtx(), psel.getC_BankAccount_ID(), get_TrxName());
				
			}
			
			getMaxLineNo();

			if(processUI != null && m_Bill.getC_PaySelection_ID() != 0)
			{
				isOpenDialog = true;
				//Already Pay Selection created, Do you want to create  Pay Selection again?
				processUI.ask("JP_CreatePSfromBillAgain", new Callback<Boolean>() {

					@Override
					public void onCallback(Boolean result)
					{
						if (result)
						{
							createPaySelectionLine(psel, m_Bill);
							isCreatePaySelection = true;
						}else{
							isAskAnswer = false;
						}
			        }

				});//FDialog.

			}else{
				createPaySelectionLine(psel, m_Bill);
				isCreatePaySelection = true;
			}

			while (isOpenDialog && isAskAnswer && !isCreatePaySelection)
			{
				Thread.sleep(1000*2);
			}

			//if(isCreatePaySelection)
				//addBufferLog(0, null, null, Msg.getElement(getCtx(), "C_PaySelection_ID", mBill.isSOTrx()) + " : "+ psel.getName() , psel.get_Table_ID(), psel.getC_PaySelection_ID());

			MPaySelectionLine[] lines = psel.getLines(true);
			if(lines.length == 0)
			{
				throw new Exception(Msg.getMsg(getCtx(), "FindZeroRecords") + Msg.getElement(getCtx(), "IsPaid", m_Bill.isSOTrx()));
			}
			
			if(m_Bill.getC_PaySelection_ID() == 0)
			{				
				m_Bill.setC_PaySelection_ID(psel.getC_PaySelection_ID());
				m_Bill.saveEx(get_TrxName());
			}
			
			addLog(Msg.getElement(getCtx(), "C_PaySelection_ID", m_Bill.isSOTrx()) + " : "+ psel.getName());

			if(createLines > 0)
				return Msg.getMsg(getCtx(), "Created")+ " : "+createLines + " - " + Msg.getElement(getCtx(), "C_PaySelection_ID", p_IsSOTrx) + " : "+ psel.getName();
			else
				return Msg.getMsg(getCtx(), "JP_CouldNotCreate");
			
		}else if(isCalledFromInfoWindow){

			ProcessInfoParameter[] para = getParameter();
			for (int i = 0; i < para.length; i++)
			{

				String name = para[i].getParameterName();
				if (para[i].getParameter() == null)
					;
				else if (name.equals("TargetPaymentRule"))
					p_TargetPaymentRule = (String)para[i].getParameter();
				else if (name.equals("IsSOTrx"))
					p_IsSOTrx = "Y".equals(para[i].getParameter());
				else if (name.equals("C_BankAccount_ID"))
					p_C_BankAccount_ID = para[i].getParameterAsInt();
				else if(name.equals("PayDate"))
					p_PayDate = (Timestamp)para[i].getParameter();
				else if (name.equals("C_PaySelection_ID"))
					p_C_PaySelection_ID = para[i].getParameterAsInt();
				else
					log.log(Level.SEVERE, "Unknown Parameter: " + name);

			}
			
			isPaySelectionProcessed();

			if(p_selectedBillList != null && p_selectedBillList.size() > 0)
			{
				MPaySelection psel = new MPaySelection (getCtx(), p_C_PaySelection_ID, get_TrxName());
				if(p_C_PaySelection_ID == 0)
				{
					m_BankAccount = new MBankAccount(getCtx(), p_C_BankAccount_ID, get_TrxName());

					if(m_BankAccount.getC_Currency_ID() != p_selectedBillList.get(0).getC_Currency_ID())
						throw new Exception(Msg.getMsg(getCtx(), "JP_DifferentCurrency"));
					
					psel.setAD_Org_ID(m_BankAccount.getAD_Org_ID());
					psel.setC_BankAccount_ID(p_C_BankAccount_ID);
					psel.setPayDate(p_PayDate);
					LocalDateTime dateTime = LocalDateTime.now();
					psel.setName(dateTime.truncatedTo(ChronoUnit.SECONDS).toString().replaceAll("T", " "));
					psel.set_ValueNoCheck("IsReceiptJP", p_IsSOTrx);
					psel.saveEx(get_TrxName());
					
				}else {
					
					m_BankAccount = new MBankAccount(getCtx(), psel.getC_BankAccount_ID(), get_TrxName());
					
				}
				
				getMaxLineNo();

				for(int i = 0; i < p_selectedBillList.size(); i++)
				{
					MBill m_Bill = new MBill(getCtx(), p_selectedBillList.get(i).getJP_Bill_ID(), get_TrxName());
					if(!m_Bill.getDocStatus().equals(DocAction.STATUS_Completed) &&  !m_Bill.getDocStatus().equals(DocAction.STATUS_Closed))
					{
						throw new Exception(Msg.getMsg(getCtx(), "JP_Not_Completed_Document") + " - " +m_Bill.getDocumentNo());
					}
					
					if(m_BankAccount.getC_Currency_ID() != m_Bill.getC_Currency_ID())
						continue;
					
					if(m_Bill.isSOTrx() != psel.get_ValueAsBoolean("IsReceiptJP"))
						throw new Exception("JP_UnexpectedError");

					createPaySelectionLine(psel, m_Bill);

					if(m_Bill.getC_PaySelection_ID() == 0)
					{
						m_Bill.setC_PaySelection_ID(psel.getC_PaySelection_ID());
						m_Bill.saveEx(get_TrxName());
					}
				}

				addBufferLog(0, null, null, Msg.getElement(getCtx(), "C_PaySelection_ID", p_IsSOTrx) + " : "+ psel.getName() , psel.get_Table_ID(), psel.getC_PaySelection_ID());

				if(createLines > 0)
					return Msg.getMsg(getCtx(), "Created")+ " : "+createLines + " - " + Msg.getElement(getCtx(), "C_PaySelection_ID", p_IsSOTrx) + " : "+ psel.getName();
				else
					return Msg.getMsg(getCtx(), "JP_CouldNotCreate");
			}

		}else if(isCalledFromProcessWindow){

			ProcessInfoParameter[] para = getParameter();
			for (int i = 0; i < para.length; i++)
			{

				String name = para[i].getParameterName();
				if (para[i].getParameter() == null)
					;
				else if (name.equals("C_BPartner_ID"))
					p_C_BPartner_ID = para[i].getParameterAsInt();
				else if (name.equals("C_BP_Group_ID"))
					p_C_BP_Group_ID = para[i].getParameterAsInt();
				else if (name.equals("JP_PromisedPayDate")){
					p_JP_PromisedPayDate_From = (Timestamp)para[i].getParameter();
					p_JP_PromisedPayDate_To = (Timestamp)para[i].getParameter_To();
				}
				else if (name.equals("TargetPaymentRule"))
					p_TargetPaymentRule = (String)para[i].getParameter();
				else if (name.equals("IsSOTrx"))
					p_IsSOTrx = "Y".equals(para[i].getParameter());
				else if (name.equals("C_BankAccount_ID"))
					p_C_BankAccount_ID = para[i].getParameterAsInt();
				else if(name.equals("PayDate"))
					p_PayDate = (Timestamp)para[i].getParameter();
				else if (name.equals("C_PaySelection_ID"))
					p_C_PaySelection_ID = para[i].getParameterAsInt();
				else
					log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}

			isPaySelectionProcessed();
			if(p_C_BankAccount_ID == 0 && m_PaySelection != null)
			{
				p_C_BankAccount_ID = m_PaySelection.getC_BankAccount_ID();
			}
			
			m_BankAccount = new MBankAccount(getCtx(), p_C_BankAccount_ID, get_TrxName());

			//Get the payment requests searched through parameters for the payment requests.
			StringBuilder sql = new StringBuilder("SELECT * FROM JP_Bill b INNER JOIN C_BPartner bp ON (b.C_BPartner_ID = bp.C_BPartner_ID) WHERE b.DocStatus IN ('CO','CL') ");
			if(p_C_BPartner_ID != 0)
				sql.append("AND b.C_BPartner_ID = ? ");
			if(p_C_BP_Group_ID != 0)
				sql.append(" AND bp.C_BP_Group_ID = ? ");
			if(p_JP_PromisedPayDate_From != null)
				sql.append(" AND b.JP_PromisedPayDate >= ?");
			if(p_JP_PromisedPayDate_To != null)
				sql.append(" AND b.JP_PromisedPayDate <= ?");
			if(p_IsSOTrx)
				sql.append(" AND b.IsSOTrx= ?");
			else
				sql.append(" AND b.IsSOTrx= ?");

			sql.append( " AND b.C_Currency_ID = ? ");
			sql.append(" AND b.C_PaySelection_ID is null ");
			sql.append(" ORDER BY b.C_BPartner_ID ");

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
				int i = 0;
				if(p_C_BPartner_ID != 0)
				{
					i++;
					pstmt.setInt(i, p_C_BPartner_ID);
				}
				if(p_C_BP_Group_ID != 0)
				{	
					i++;
					pstmt.setInt(i, p_C_BP_Group_ID);
				}
				if(p_JP_PromisedPayDate_From != null)
				{
					i++;
					pstmt.setTimestamp(i, p_JP_PromisedPayDate_From);
				}
				if(p_JP_PromisedPayDate_To != null)
				{
					i++;
					pstmt.setTimestamp(i, p_JP_PromisedPayDate_To);
				}
				
				i++;
				if(p_IsSOTrx)
				{

					pstmt.setString(i, "Y");
				}else {
					pstmt.setString(i, "N");
				}
				
				i++;
				pstmt.setInt(i, m_BankAccount.getC_Currency_ID());
				
				rs = pstmt.executeQuery ();
				while (rs.next ())
				{
					p_searchedBillList.add(new MBill(getCtx(), rs, get_TrxName()));
				}
			}catch (Exception e) {
				throw new Exception(Msg.getMsg(getCtx(), "DBExecuteError"));
			}finally {
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}

			if(p_searchedBillList != null && p_searchedBillList.size() > 0)
			{
				MPaySelection psel = new MPaySelection (getCtx(), p_C_PaySelection_ID, get_TrxName());
				if(p_C_PaySelection_ID == 0)
				{
					psel.setAD_Org_ID(m_BankAccount.getAD_Org_ID());
					psel.setC_BankAccount_ID(p_C_BankAccount_ID);
					psel.setPayDate(p_PayDate);
					LocalDateTime dateTime = LocalDateTime.now();
					psel.setName(dateTime.truncatedTo(ChronoUnit.SECONDS).toString().replaceAll("T", " "));
					psel.set_ValueNoCheck("IsReceiptJP", p_IsSOTrx);
					psel.saveEx(get_TrxName());
					
				}else {
					
					m_BankAccount = new MBankAccount(getCtx(), psel.getC_BankAccount_ID(), get_TrxName());
					
				}

				getMaxLineNo();
				
				for(int i = 0; i < p_searchedBillList.size(); i++)
				{
					MBill m_Bill = new MBill(getCtx(), p_searchedBillList.get(i).getJP_Bill_ID(), get_TrxName());
					if(!m_Bill.getDocStatus().equals(DocAction.STATUS_Completed) &&  !m_Bill.getDocStatus().equals(DocAction.STATUS_Closed))
					{
						throw new Exception(Msg.getMsg(getCtx(), "JP_Not_Completed_Document") + " - " +m_Bill.getDocumentNo());
					}
					
					if(m_BankAccount.getC_Currency_ID() != m_Bill.getC_Currency_ID())
						continue;
					
					if(m_Bill.isSOTrx() != psel.get_ValueAsBoolean("IsReceiptJP"))
						throw new Exception("JP_UnexpectedError");
					
					createPaySelectionLine(psel, p_searchedBillList.get(i));
					
					if(m_Bill.getC_PaySelection_ID() == 0)
					{

						m_Bill.setC_PaySelection_ID(psel.getC_PaySelection_ID());
						m_Bill.saveEx(get_TrxName());
					}
				}

				addBufferLog(0, null, null, Msg.getElement(getCtx(), "C_PaySelection_ID", p_IsSOTrx) + " : "+ psel.getName() , psel.get_Table_ID(), psel.getC_PaySelection_ID());

				if(createLines > 0 )
					return Msg.getMsg(getCtx(), "Created")+ " : "+createLines + " - " + Msg.getElement(getCtx(), "C_PaySelection_ID", p_IsSOTrx) + " : "+ psel.getName();
				else
					return Msg.getMsg(getCtx(), "JP_CouldNotCreate");
			}
		}

		return Msg.getMsg(getCtx(), "JP_CouldNotCreate");

	}

	private int createLines = 0;
	private int maxLineNo = 0;
	private int createPaySelectionLine(MPaySelection paySelection, MBill bill)
	{
		MInvoice invoice = null;
		MDocType docType = null;
		
		MPaySelectionLine[]  psls = paySelection.getLines(true);
		MBillLine[] billLines =  bill.getLines();

		boolean isAlreadyCreated = false;
		for(int i = 0; i < billLines.length; i++)
		{
			invoice = new MInvoice(getCtx(), billLines[i].getC_Invoice_ID(), get_TrxName());
			if(invoice.isPaid())
			{
				String msg = "Skip: "+ invoice.getDocumentNo() + " - " + Msg.getElement(getCtx(),MInvoice.COLUMNNAME_IsPaid, invoice.isSOTrx());
				addBufferLog(0, null, null, msg, MInvoice.Table_ID, invoice.getC_Invoice_ID());
				continue;
			}

			if(!(invoice.getDocStatus().equals(DocAction.STATUS_Completed) || invoice.getDocStatus().equals(DocAction.STATUS_Closed)) )
			{
				String msg = "Skip: "+ invoice.getDocumentNo() + " - " + Msg.getMsg(getCtx(),"JP_Not_Completed_Document");
				addBufferLog(0, null, null, msg, MInvoice.Table_ID, invoice.getC_Invoice_ID());
				continue;
			}

			if(m_BankAccount.getC_Currency_ID() != invoice.getC_Currency_ID())
			{
				String msg = "Skip: "+ invoice.getDocumentNo() + " - " + Msg.getMsg(getCtx(),"JP_DifferentCurrency");
				addBufferLog(0, null, null, msg, MInvoice.Table_ID, invoice.getC_Invoice_ID());
				continue;
			}
			
			isAlreadyCreated = false;
			for(MPaySelectionLine psl : psls)
			{
				if(invoice.getC_Invoice_ID() == psl.getC_Invoice_ID())
				{
					isAlreadyCreated = true;
					String msg = "Skip: "+ invoice.getDocumentNo() + " - " + Msg.getMsg(getCtx(),"JP_AlreadyRegistered");
					addBufferLog(0, null, null, msg, MInvoice.Table_ID, invoice.getC_Invoice_ID());
					break;
				}
			}
			
			if(!isAlreadyCreated)
			{
				createLines++;
				docType = MDocType.get(invoice.getC_DocTypeTarget_ID());
	
				BigDecimal openAmt = invoice.getOpenAmt();
				MPaySelectionLine pselLine = new MPaySelectionLine (paySelection, (createLines*10+maxLineNo), bill.getPaymentRule());
	
				if(docType.getDocBaseType().equals(MDocType.DOCBASETYPE_ARCreditMemo)
						|| docType.getDocBaseType().equals(MDocType.DOCBASETYPE_APCreditMemo))
				{
					pselLine.setInvoice (invoice.getC_Invoice_ID(), invoice.isSOTrx(), openAmt.negate(), openAmt.negate(), Env.ZERO, Env.ZERO);
				}else {
					pselLine.setInvoice (invoice.getC_Invoice_ID(), invoice.isSOTrx(), openAmt, openAmt, Env.ZERO, Env.ZERO);
				}
				pselLine.saveEx(get_TrxName());
				
			}
		}

		return createLines;
	}
	
	private int getMaxLineNo() throws Exception
	{
		if(m_PaySelection == null)
			return maxLineNo;
		
		String sql = "SELECT NVL(MAX(Line),0) FROM C_PaySelectionLine WHERE C_PaySelection_ID=?";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			pstmt.setInt(1, m_PaySelection.getC_PaySelection_ID());
			rs = pstmt.executeQuery ();
			if (rs.next ())
			{
				maxLineNo = rs.getInt(1);
			}
		}catch (Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "DBExecuteError"));
		}finally {
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		
		return maxLineNo;
		
	}
	
	private boolean isPaySelectionProcessed() throws Exception
	{
		if(p_C_PaySelection_ID == 0)
			return false;
		
		m_PaySelection = new MPaySelection(getCtx(),p_C_PaySelection_ID,get_TrxName());
		if(m_PaySelection.isProcessed())
		{
			throw new Exception(Msg.getElement(getCtx(), "Processed") +" - "+ m_PaySelection.getName());
		}
		
		if(!m_PaySelection.isActive())
		{
			throw new Exception(Msg.getMsg(getCtx(), "NotActive") +" - "+ m_PaySelection.getName());
		}
		
		return false;
	}
}
