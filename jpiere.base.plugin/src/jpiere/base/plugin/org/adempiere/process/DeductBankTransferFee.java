package jpiere.base.plugin.org.adempiere.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;

import org.compiere.model.MBPBankAccount;
import org.compiere.model.MBPartner;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MPaySelection;
import org.compiere.model.MPaySelectionLine;
import org.compiere.process.DocAction;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MBankTransferFeeDeduct;
import jpiere.base.plugin.org.adempiere.model.MPaySelectionCheckJP;

/**
*
* JPIERE-0581: Deduct Bank Transfer Fee
*
* Auto Calculate Bank Transfer Fee at Payment Selection Line.
*
* @author h.hagiwara
*
*/
public class DeductBankTransferFee extends SvrProcess {

	private int p_C_PaySelection_ID = 0;
	private MPaySelection m_PaySelection = null;
	
	private boolean p_onepaymentPerInvoice	= false;
	private ArrayList<MPaySelectionCheckJP>	m_list = new ArrayList<MPaySelectionCheckJP>();

	
	private static final String JP_BP_BANKkACCOUNT_ID = "JP_BP_BankAccount_ID";
	private static final String IS_AUTO_CAL_BANK_TRANSFER_FEE = "IsAutoCalBankTransferfeeJP";
	private static final String JP_BANK_TRANSFER_FEE_DEDUCT_ID = "JP_BankTransferFeeDeduct_ID";
	
	@Override
	protected void prepare() 
	{
		p_C_PaySelection_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception
	{
		m_PaySelection = new MPaySelection (getCtx(), p_C_PaySelection_ID, get_TrxName());
		if (m_PaySelection.get_ID() == 0)
			throw new IllegalArgumentException("Not found C_PaySelection_ID=" + p_C_PaySelection_ID);
		if (m_PaySelection.isProcessed())
			throw new IllegalArgumentException("@Processed@");
		
		MPaySelectionLine[] lines = m_PaySelection.getLines(true);
		if(lines.length == 0)
		{
			//There are not Invoice that is targeted to create Payment Batch.
			throw new IllegalArgumentException(Msg.getMsg(getCtx(), "JP_NoInvoiceToPaymentBatch"));
		}
		
		p_onepaymentPerInvoice = m_PaySelection.isOnePaymentPerInvoice();
		if(p_onepaymentPerInvoice)
		{
			String msg = Msg.getMsg(getCtx(), "ProcessFailed") + " : "+Msg.getElement(getCtx(), MPaySelection.COLUMNNAME_IsOnePaymentPerInvoice, false) +" 'Y'";
			throw new IllegalArgumentException(Msg.getMsg(getCtx(), msg));
		}
		
		for (int i = 0; i < lines.length; i++)
		{
			MPaySelectionLine line = lines[i];
			if (!line.isActive() || line.get_ValueAsBoolean(IS_AUTO_CAL_BANK_TRANSFER_FEE))
				continue;
			creatTentativeCheck (line);
		}		

		MBPBankAccount bpba = null;
		MBankTransferFeeDeduct btfd = null;
		int line = getMaxLineNo();
		int count = 0;
		for(MPaySelectionCheckJP tentativeCheck : m_list)
		{
			int JP_BP_BankAccount_ID = tentativeCheck.getJP_BP_BankAccount_ID();
			if(JP_BP_BankAccount_ID == 0)
			{
				continue;
				
			}else {
				
				bpba = new MBPBankAccount(getCtx(), JP_BP_BankAccount_ID, get_TrxName());
				int JP_BankTransferFeeDeduct_ID = bpba.get_ValueAsInt(JP_BANK_TRANSFER_FEE_DEDUCT_ID);
				if(JP_BankTransferFeeDeduct_ID ==0)
				{
					continue;
				}
				
				//Check lower limit.
				btfd = MBankTransferFeeDeduct.get(JP_BankTransferFeeDeduct_ID);
				if(btfd.getJP_LowerLimitBankTransferFee().compareTo(tentativeCheck.getPayAmt()) > 0)
					continue;
				
				//Check Already created or not.
				if(isCreatedBankTansferFeeLine(tentativeCheck.getC_BPartner_ID(), tentativeCheck.getJP_BP_BankAccount_ID()))
					continue;
				
				line = line + 10;
				//Create AP Invoice for deduct Bank Transfer fee.
				if(createBankTransferFeeInvoice(tentativeCheck, btfd, line))
				{
					count++;
				}else {
					throw new Exception(Msg.getMsg(getCtx(), "JP_UnexpectedError"));
				}
			}
		}
		
		String returnMsg = Msg.getMsg(getCtx(), "JP_Bank_Transfer_Fee_Invoice") + " : " +count;
		addBufferLog(0, null, null, returnMsg, 0, 0);
		return Msg.getMsg(getCtx(), "JP_Success");
	}
	
	private int getMaxLineNo()
	{
		String sql = "SELECT NVL(MAX(Line),0) FROM C_PaySelectionLine WHERE C_PaySelection_ID=?";
		int lineNo = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, null);
			pstmt.setInt (1, p_C_PaySelection_ID);
			rs = pstmt.executeQuery ();
			if (rs.next ())
			{
				lineNo = rs.getInt(1);
			}
		}
		catch (Exception e)
		{
			log.log (Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
				
		return lineNo;
	}

	private void creatTentativeCheck (MPaySelectionLine line) throws Exception
	{
		if (!p_onepaymentPerInvoice)
		{
			// Try to find one
			for (int i = 0; i < m_list.size(); i++)
			{
				MPaySelectionCheckJP check = (MPaySelectionCheckJP) m_list.get(i);
				// Add to existing
				if (check.getC_BPartner_ID() == line.getInvoice().getC_BPartner_ID()
						&& check.get_ValueAsInt(JP_BP_BANKkACCOUNT_ID) == line.get_ValueAsInt(JP_BP_BANKkACCOUNT_ID)) //JPIERE-0580: Select BP Bank Account
				{
					check.addLine(line);
					if (!check.save())
						throw new IllegalStateException("Cannot save MPaySelectionCheck");
					return;
				}
			}
		}

		MPaySelectionCheckJP check = new MPaySelectionCheckJP(line);
		check.setAD_PInstance_ID(getAD_PInstance_ID());
		
		//JPIERE-0580: Select BP Bank Account - Start
		int JP_BP_BankAccount_ID = line.get_ValueAsInt(JP_BP_BANKkACCOUNT_ID);
		if(JP_BP_BankAccount_ID > 0)
		{
			check.set_ValueNoCheck(JP_BP_BANKkACCOUNT_ID, JP_BP_BankAccount_ID);
		}else {
			check.set_ValueNoCheck(JP_BP_BANKkACCOUNT_ID, null);
		}
		//JPIERE-0580: Select BP Bank Account - End
		
		if (!check.save())
			throw new IllegalStateException("Cannot save MPaySelectionCheckJP");
		m_list.add(check);
	}	//	createCheck
	
	
	private boolean isCreatedBankTansferFeeLine(int C_BPartner_ID, int JP_BP_BankAccount_ID)
	{
		String sql = "SELECT COUNT(*) FROM C_PaySelectionLine psl"
						+ " INNER JOIN C_Invoice inv ON (psl.C_Invoice_ID = inv.C_Invoice_ID) "
						+ " WHERE psl.C_PaySelection_ID=? AND inv.C_BPartner_ID = ? "
						+ " AND psl.JP_BP_BankAccount_ID = ? AND psl.IsAutoCalBankTransferfeeJP ='Y' ";
		
		int count = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, null);
			pstmt.setInt (1, p_C_PaySelection_ID);
			pstmt.setInt (2, C_BPartner_ID);
			pstmt.setInt (3, JP_BP_BankAccount_ID);
			rs = pstmt.executeQuery ();
			if (rs.next ())
			{
				count = rs.getInt(1);
			}
		}
		catch (Exception e)
		{
			log.log (Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		
		if(count > 0)
		{
			return true;
		}else {
			return false;
		}
		
	}
	
	private boolean createBankTransferFeeInvoice(MPaySelectionCheckJP tentativeCheck, MBankTransferFeeDeduct m_BankTransferFeeDeduct, int line)
	{
		MInvoice inv = new MInvoice(getCtx(),0, get_TrxName());
		inv.setAD_Org_ID(m_PaySelection.getAD_Org_ID());
		MBPartner bp = MBPartner.get(getCtx(), tentativeCheck.getC_BPartner_ID());
		inv.setC_BPartner_ID(bp.getC_BPartner_ID());
		inv.setC_BPartner_Location_ID(bp.getPrimaryC_BPartner_Location_ID());
		if(tentativeCheck.getJP_BP_BankAccount_ID() == 0)
			inv.set_ValueNoCheck(JP_BP_BANKkACCOUNT_ID, null);
		else
			inv.set_ValueNoCheck(JP_BP_BANKkACCOUNT_ID, tentativeCheck.getJP_BP_BankAccount_ID());

		if(Util.isEmpty(m_BankTransferFeeDeduct.getDescription()))
		{
			inv.setDescription(Msg.getMsg(getCtx(), "JP_Bank_Transfer_Fee_Invoice"));
		}else {
			inv.setDescription(m_BankTransferFeeDeduct.getDescription());
		}
		inv.setC_DocTypeTarget_ID(m_BankTransferFeeDeduct.getC_DocType_ID());	
		inv.setM_PriceList_ID(m_BankTransferFeeDeduct.getM_PriceList_ID());
		inv.setPaymentRule(m_BankTransferFeeDeduct.getPaymentRule());
		inv.setC_Payment_ID(m_BankTransferFeeDeduct.getC_PaymentTerm_ID());
		
		inv.saveEx(get_TrxName());
		
		MInvoiceLine invLine = new MInvoiceLine(inv);
		invLine.setLine(m_BankTransferFeeDeduct.getLine());
		if(m_BankTransferFeeDeduct.getM_Product_ID() > 0)
		{
			invLine.setM_Product_ID(m_BankTransferFeeDeduct.getM_Product_ID());
			invLine.setC_UOM_ID(m_BankTransferFeeDeduct.getM_Product().getC_UOM_ID());
			
		}else {
			
			invLine.setC_Charge_ID(m_BankTransferFeeDeduct.getC_Charge_ID());
			invLine.setC_UOM_ID(100);
		}
		
		invLine.setQtyEntered(Env.ONE);
		invLine.setQtyInvoiced(Env.ONE);
		invLine.setPrice(m_BankTransferFeeDeduct.getPriceEntered());
		invLine.setLine(m_BankTransferFeeDeduct.getC_Tax_ID());
		
		invLine.saveEx(get_TrxName());
		
		if(inv.processIt(DocAction.ACTION_Complete))
		{
			MPaySelectionLine psl = new MPaySelectionLine(m_PaySelection, line, m_BankTransferFeeDeduct.getPaymentRule());
			if(Util.isEmpty(m_BankTransferFeeDeduct.getDescription()))
			{
				psl.setDescription(Msg.getMsg(getCtx(), "JP_Bank_Transfer_Fee_Invoice"));
			}else {
				psl.setDescription(m_BankTransferFeeDeduct.getDescription());
			}
			
			if(m_BankTransferFeeDeduct.getC_DocType().getDocBaseType().equals("APC"))
			{
				psl.setInvoice (inv.getC_Invoice_ID(), inv.isSOTrx(),
					m_BankTransferFeeDeduct.getPriceEntered().negate(), m_BankTransferFeeDeduct.getPriceEntered().negate(), Env.ZERO,  Env.ZERO);
			}else {
				psl.setInvoice (inv.getC_Invoice_ID(), inv.isSOTrx(),
					m_BankTransferFeeDeduct.getPriceEntered(), m_BankTransferFeeDeduct.getPriceEntered(), Env.ZERO,  Env.ZERO);
			}
			psl.set_ValueNoCheck(IS_AUTO_CAL_BANK_TRANSFER_FEE, true);
			psl.saveEx(get_TrxName());
			
			return true;
		}else {
			return false;
		}
		
	}
}
