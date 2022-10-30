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
package jpiere.base.plugin.org.adempiere.process;

import java.util.ArrayList;
import java.util.logging.Level;

import org.compiere.model.MBPartner;
import org.compiere.model.MPaySelection;
import org.compiere.model.MPaySelectionCheck;
import org.compiere.model.MPaySelectionLine;
import org.compiere.model.X_C_Order;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.AdempiereUserError;
import org.compiere.util.Msg;
 

/**
 *	JPIERE-0580: Select BP Bank Account
 *
 *  Create Checks from Payment Selection Line
 *  
 *  Ref: PaySelectionCreateCheck.java
 *	
 *  @author Jorg Janke
 *  @author h.hagiwara
 */
public class JPierePaySelectionCreateCheck extends SvrProcess
{
	/**	Target Payment Rule			*/
	private String		p_PaymentRule = null;
	/**	Payment Selection			*/
	private int			p_C_PaySelection_ID = 0;
	/** one payment per invoice */
	private boolean							p_onepaymentPerInvoice	= false;
	/** The checks					*/
	private ArrayList<MPaySelectionCheck>	m_list = new ArrayList<MPaySelectionCheck>();
	
	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("PaymentRule"))
				p_PaymentRule = (String)para[i].getParameter();
			else if (name.equalsIgnoreCase(MPaySelection.COLUMNNAME_IsOnePaymentPerInvoice))
				p_onepaymentPerInvoice = para[i].getParameterAsBoolean();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		p_C_PaySelection_ID = getRecord_ID();
		if (p_PaymentRule != null && p_PaymentRule.equals(X_C_Order.PAYMENTRULE_DirectDebit))
			p_PaymentRule = null;
	}	//	prepare

	/**
	 *  Perform process.
	 *  @return Message (clear text)
	 *  @throws Exception if not successful
	 */
	protected String doIt () throws Exception
	{
		if (log.isLoggable(Level.INFO)) log.info ("C_PaySelection_ID=" + p_C_PaySelection_ID
			+ ", PaymentRule=" + p_PaymentRule);
		
		MPaySelection psel = new MPaySelection (getCtx(), p_C_PaySelection_ID, get_TrxName());
		if (psel.get_ID() == 0)
			throw new IllegalArgumentException("Not found C_PaySelection_ID=" + p_C_PaySelection_ID);
		if (psel.isProcessed())
			throw new IllegalArgumentException("@Processed@");
		
		//JPIERE-0580-Start
		MPaySelectionLine[] lines = psel.getLines(false);
		if(lines.length == 0)
		{
			//There are not Invoice that is targeted to create Payment Batch.
			throw new IllegalArgumentException(Msg.getMsg(getCtx(), "JP_NoInvoiceToPaymentBatch"));
		}
		//JPIERE-0580-End
		
		for (int i = 0; i < lines.length; i++)
		{
			MPaySelectionLine line = lines[i];
			if (!line.isActive() || line.isProcessed())
				continue;
			createCheck (line);
		}
		//
		psel.setProcessed(true);
		psel.saveEx();
		
		StringBuilder msgreturn = new StringBuilder("@C_PaySelectionCheck_ID@ - #").append(m_list.size());
		return msgreturn.toString();
	}	//	doIt

	
	private static final String JP_BP_BANKkACCOUNT_ID = "JP_BP_BankAccount_ID";
	
	/**
	 * 	Create Check from line
	 *	@param line
	 *	@throws Exception for invalid bank accounts
	 */
	private void createCheck (MPaySelectionLine line) throws Exception
	{
		if (!p_onepaymentPerInvoice)
		{
			// Try to find one
			for (int i = 0; i < m_list.size(); i++)
			{
				MPaySelectionCheck check = (MPaySelectionCheck) m_list.get(i);
				// Add to existing
				if (check.getC_BPartner_ID() == line.getInvoice().getC_BPartner_ID()
						&& check.get_ValueAsInt(JP_BP_BANKkACCOUNT_ID) == line.get_ValueAsInt(JP_BP_BANKkACCOUNT_ID)) //JPIERE-0580: Select BP Bank Account
				{
					check.addLine(line);
					if (!check.save())
						throw new IllegalStateException("Cannot save MPaySelectionCheck");
					line.setC_PaySelectionCheck_ID(check.getC_PaySelectionCheck_ID());
					line.setProcessed(true);
					if (!line.save())
						throw new IllegalStateException("Cannot save MPaySelectionLine");
					return;
				}
			}
		}
		//	Create new
		String PaymentRule = line.getPaymentRule();
		if (p_PaymentRule != null)
		{
			if (!X_C_Order.PAYMENTRULE_DirectDebit.equals(PaymentRule))
				PaymentRule = p_PaymentRule;
		}
		MPaySelectionCheck check = new MPaySelectionCheck(line, PaymentRule);
		if (!check.isValid())
		{
			int C_BPartner_ID = check.getC_BPartner_ID();
			MBPartner bp = MBPartner.get(getCtx(), C_BPartner_ID);
			StringBuilder msg = new StringBuilder("@NotFound@ @C_BP_BankAccount@: ").append(bp.getName());
			throw new AdempiereUserError(msg.toString());
		}
		
		//JPIERE-0580: Select BP Bank Account - Start
		int JP_BP_BankAccount_ID = line.get_ValueAsInt(JP_BP_BANKkACCOUNT_ID);
		if(JP_BP_BankAccount_ID > 0)
		{
			check.set_ValueNoCheck(JP_BP_BANKkACCOUNT_ID, JP_BP_BankAccount_ID);
			check.setC_BP_BankAccount_ID(0);
		}else {
			check.set_ValueNoCheck(JP_BP_BANKkACCOUNT_ID, 0);
			check.setC_BP_BankAccount_ID(0);
		}
		//JPIERE-0580: Select BP Bank Account - End
		
		if (!check.save())
			throw new IllegalStateException("Cannot save MPaySelectionCheck");
		line.setC_PaySelectionCheck_ID(check.getC_PaySelectionCheck_ID());
		line.setProcessed(true);
		if (!line.save())
			throw new IllegalStateException("Cannot save MPaySelectionLine");
		m_list.add(check);
	}	//	createCheck
	
}	//	PaySelectionCreateCheck
