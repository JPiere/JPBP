/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2012 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
/** Generated Model - DO NOT CHANGE */
package jpiere.base.plugin.org.adempiere.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;

/** Generated Model for T_PaySelectionCheckJP
 *  @author iDempiere (generated) 
 *  @version Release 9 - $Id$ */
@org.adempiere.base.Model(table="T_PaySelectionCheckJP")
public class X_T_PaySelectionCheckJP extends PO implements I_T_PaySelectionCheckJP, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20221101L;

    /** Standard Constructor */
    public X_T_PaySelectionCheckJP (Properties ctx, int T_PaySelectionCheckJP_ID, String trxName)
    {
      super (ctx, T_PaySelectionCheckJP_ID, trxName);
      /** if (T_PaySelectionCheckJP_ID == 0)
        {
			setAD_PInstance_ID (0);
			setC_BPartner_ID (0);
			setC_PaySelection_ID (0);
			setDiscountAmt (Env.ZERO);
			setIsReceipt (false);
			setJP_BankTransferFee (Env.ZERO);
// 0
			setPayAmt (Env.ZERO);
			setQty (0);
			setT_PaySelectionCheckJP_ID (0);
			setWriteOffAmt (Env.ZERO);
// 0
        } */
    }

    /** Standard Constructor */
    public X_T_PaySelectionCheckJP (Properties ctx, int T_PaySelectionCheckJP_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, T_PaySelectionCheckJP_ID, trxName, virtualColumns);
      /** if (T_PaySelectionCheckJP_ID == 0)
        {
			setAD_PInstance_ID (0);
			setC_BPartner_ID (0);
			setC_PaySelection_ID (0);
			setDiscountAmt (Env.ZERO);
			setIsReceipt (false);
			setJP_BankTransferFee (Env.ZERO);
// 0
			setPayAmt (Env.ZERO);
			setQty (0);
			setT_PaySelectionCheckJP_ID (0);
			setWriteOffAmt (Env.ZERO);
// 0
        } */
    }

    /** Load Constructor */
    public X_T_PaySelectionCheckJP (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 1 - Org 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuilder sb = new StringBuilder ("X_T_PaySelectionCheckJP[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_PInstance getAD_PInstance() throws RuntimeException
	{
		return (org.compiere.model.I_AD_PInstance)MTable.get(getCtx(), org.compiere.model.I_AD_PInstance.Table_ID)
			.getPO(getAD_PInstance_ID(), get_TrxName());
	}

	/** Set Process Instance.
		@param AD_PInstance_ID Instance of the process
	*/
	public void setAD_PInstance_ID (int AD_PInstance_ID)
	{
		if (AD_PInstance_ID < 1)
			set_ValueNoCheck (COLUMNNAME_AD_PInstance_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_AD_PInstance_ID, Integer.valueOf(AD_PInstance_ID));
	}

	/** Get Process Instance.
		@return Instance of the process
	  */
	public int getAD_PInstance_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_PInstance_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_BP_BankAccount getC_BP_BankAccount() throws RuntimeException
	{
		return (org.compiere.model.I_C_BP_BankAccount)MTable.get(getCtx(), org.compiere.model.I_C_BP_BankAccount.Table_ID)
			.getPO(getC_BP_BankAccount_ID(), get_TrxName());
	}

	/** Set Partner Bank Account.
		@param C_BP_BankAccount_ID Bank Account of the Business Partner
	*/
	public void setC_BP_BankAccount_ID (int C_BP_BankAccount_ID)
	{
		if (C_BP_BankAccount_ID < 1)
			set_Value (COLUMNNAME_C_BP_BankAccount_ID, null);
		else
			set_Value (COLUMNNAME_C_BP_BankAccount_ID, Integer.valueOf(C_BP_BankAccount_ID));
	}

	/** Get Partner Bank Account.
		@return Bank Account of the Business Partner
	  */
	public int getC_BP_BankAccount_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BP_BankAccount_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_BPartner getC_BPartner() throws RuntimeException
	{
		return (org.compiere.model.I_C_BPartner)MTable.get(getCtx(), org.compiere.model.I_C_BPartner.Table_ID)
			.getPO(getC_BPartner_ID(), get_TrxName());
	}

	/** Set Business Partner.
		@param C_BPartner_ID Identifies a Business Partner
	*/
	public void setC_BPartner_ID (int C_BPartner_ID)
	{
		if (C_BPartner_ID < 1)
			set_Value (COLUMNNAME_C_BPartner_ID, null);
		else
			set_Value (COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
	}

	/** Get Business Partner.
		@return Identifies a Business Partner
	  */
	public int getC_BPartner_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Invoice getC_Invoice() throws RuntimeException
	{
		return (org.compiere.model.I_C_Invoice)MTable.get(getCtx(), org.compiere.model.I_C_Invoice.Table_ID)
			.getPO(getC_Invoice_ID(), get_TrxName());
	}

	/** Set Invoice.
		@param C_Invoice_ID Invoice Identifier
	*/
	public void setC_Invoice_ID (int C_Invoice_ID)
	{
		if (C_Invoice_ID < 1)
			set_Value (COLUMNNAME_C_Invoice_ID, null);
		else
			set_Value (COLUMNNAME_C_Invoice_ID, Integer.valueOf(C_Invoice_ID));
	}

	/** Get Invoice.
		@return Invoice Identifier
	  */
	public int getC_Invoice_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Invoice_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_PaySelection getC_PaySelection() throws RuntimeException
	{
		return (org.compiere.model.I_C_PaySelection)MTable.get(getCtx(), org.compiere.model.I_C_PaySelection.Table_ID)
			.getPO(getC_PaySelection_ID(), get_TrxName());
	}

	/** Set Payment Selection.
		@param C_PaySelection_ID Payment Selection
	*/
	public void setC_PaySelection_ID (int C_PaySelection_ID)
	{
		if (C_PaySelection_ID < 1)
			set_ValueNoCheck (COLUMNNAME_C_PaySelection_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_C_PaySelection_ID, Integer.valueOf(C_PaySelection_ID));
	}

	/** Get Payment Selection.
		@return Payment Selection
	  */
	public int getC_PaySelection_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_PaySelection_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Discount Amount.
		@param DiscountAmt Calculated amount of discount
	*/
	public void setDiscountAmt (BigDecimal DiscountAmt)
	{
		set_Value (COLUMNNAME_DiscountAmt, DiscountAmt);
	}

	/** Get Discount Amount.
		@return Calculated amount of discount
	  */
	public BigDecimal getDiscountAmt()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_DiscountAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Receipt.
		@param IsReceipt This is a sales transaction (receipt)
	*/
	public void setIsReceipt (boolean IsReceipt)
	{
		set_Value (COLUMNNAME_IsReceipt, Boolean.valueOf(IsReceipt));
	}

	/** Get Receipt.
		@return This is a sales transaction (receipt)
	  */
	public boolean isReceipt()
	{
		Object oo = get_Value(COLUMNNAME_IsReceipt);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Bank Transfer Fee.
		@param JP_BankTransferFee Bank Transfer Fee
	*/
	public void setJP_BankTransferFee (BigDecimal JP_BankTransferFee)
	{
		set_Value (COLUMNNAME_JP_BankTransferFee, JP_BankTransferFee);
	}

	/** Get Bank Transfer Fee.
		@return Bank Transfer Fee	  */
	public BigDecimal getJP_BankTransferFee()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_JP_BankTransferFee);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Payment amount.
		@param PayAmt Amount being paid
	*/
	public void setPayAmt (BigDecimal PayAmt)
	{
		set_Value (COLUMNNAME_PayAmt, PayAmt);
	}

	/** Get Payment amount.
		@return Amount being paid
	  */
	public BigDecimal getPayAmt()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PayAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Quantity.
		@param Qty Quantity
	*/
	public void setQty (int Qty)
	{
		set_Value (COLUMNNAME_Qty, Integer.valueOf(Qty));
	}

	/** Get Quantity.
		@return Quantity
	  */
	public int getQty()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Qty);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set T_PaySelectionCheckJP.
		@param T_PaySelectionCheckJP_ID T_PaySelectionCheckJP
	*/
	public void setT_PaySelectionCheckJP_ID (int T_PaySelectionCheckJP_ID)
	{
		if (T_PaySelectionCheckJP_ID < 1)
			set_ValueNoCheck (COLUMNNAME_T_PaySelectionCheckJP_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_T_PaySelectionCheckJP_ID, Integer.valueOf(T_PaySelectionCheckJP_ID));
	}

	/** Get T_PaySelectionCheckJP.
		@return T_PaySelectionCheckJP	  */
	public int getT_PaySelectionCheckJP_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_T_PaySelectionCheckJP_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set T_PaySelectionCheckJP_UU.
		@param T_PaySelectionCheckJP_UU T_PaySelectionCheckJP_UU
	*/
	public void setT_PaySelectionCheckJP_UU (String T_PaySelectionCheckJP_UU)
	{
		set_Value (COLUMNNAME_T_PaySelectionCheckJP_UU, T_PaySelectionCheckJP_UU);
	}

	/** Get T_PaySelectionCheckJP_UU.
		@return T_PaySelectionCheckJP_UU	  */
	public String getT_PaySelectionCheckJP_UU()
	{
		return (String)get_Value(COLUMNNAME_T_PaySelectionCheckJP_UU);
	}

	/** Set Write-off Amount.
		@param WriteOffAmt Amount to write-off
	*/
	public void setWriteOffAmt (BigDecimal WriteOffAmt)
	{
		set_Value (COLUMNNAME_WriteOffAmt, WriteOffAmt);
	}

	/** Get Write-off Amount.
		@return Amount to write-off
	  */
	public BigDecimal getWriteOffAmt()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_WriteOffAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}