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

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for JP_Contract_Acct
 *  @author iDempiere (generated) 
 *  @version Release 8.2 - $Id$ */
public class X_JP_Contract_Acct extends PO implements I_JP_Contract_Acct, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20220202L;

    /** Standard Constructor */
    public X_JP_Contract_Acct (Properties ctx, int JP_Contract_Acct_ID, String trxName)
    {
      super (ctx, JP_Contract_Acct_ID, trxName);
      /** if (JP_Contract_Acct_ID == 0)
        {
			setDocBaseType (null);
// SOO
			setIsPostingContractAcctJP (false);
// N
			setIsPostingGLJournalJP (false);
// N
			setIsPostingRecognitionDocJP (false);
// N
			setIsSOTrx (true);
// Y
			setJP_Contract_Acct_ID (0);
			setName (null);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_JP_Contract_Acct (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org 
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
      StringBuilder sb = new StringBuilder ("X_JP_Contract_Acct[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
    }

	/** Set Description.
		@param Description 
		Optional short description of the record
	  */
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription () 
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** DocAction AD_Reference_ID=135 */
	public static final int DOCACTION_AD_Reference_ID=135;
	/** <None> = -- */
	public static final String DOCACTION_None = "--";
	/** Approve = AP */
	public static final String DOCACTION_Approve = "AP";
	/** Close = CL */
	public static final String DOCACTION_Close = "CL";
	/** Complete = CO */
	public static final String DOCACTION_Complete = "CO";
	/** Invalidate = IN */
	public static final String DOCACTION_Invalidate = "IN";
	/** Post = PO */
	public static final String DOCACTION_Post = "PO";
	/** Prepare = PR */
	public static final String DOCACTION_Prepare = "PR";
	/** Reverse - Accrual = RA */
	public static final String DOCACTION_Reverse_Accrual = "RA";
	/** Reverse - Correct = RC */
	public static final String DOCACTION_Reverse_Correct = "RC";
	/** Re-activate = RE */
	public static final String DOCACTION_Re_Activate = "RE";
	/** Reject = RJ */
	public static final String DOCACTION_Reject = "RJ";
	/** Void = VO */
	public static final String DOCACTION_Void = "VO";
	/** Wait Complete = WC */
	public static final String DOCACTION_WaitComplete = "WC";
	/** Unlock = XL */
	public static final String DOCACTION_Unlock = "XL";
	/** Set Document Action.
		@param DocAction 
		The targeted status of the document
	  */
	public void setDocAction (String DocAction)
	{

		set_Value (COLUMNNAME_DocAction, DocAction);
	}

	/** Get Document Action.
		@return The targeted status of the document
	  */
	public String getDocAction () 
	{
		return (String)get_Value(COLUMNNAME_DocAction);
	}

	/** AP Invoice = API */
	public static final String DOCBASETYPE_APInvoice = "API";
	/** AR Invoice = ARI */
	public static final String DOCBASETYPE_ARInvoice = "ARI";
	/** Contract Proc Schedule = JCS */
	public static final String DOCBASETYPE_ContractProcSchedule = "JCS";
	/** Material Receipt = MMR */
	public static final String DOCBASETYPE_MaterialReceipt = "MMR";
	/** Material Delivery = MMS */
	public static final String DOCBASETYPE_MaterialDelivery = "MMS";
	/** Purchase Order = POO */
	public static final String DOCBASETYPE_PurchaseOrder = "POO";
	/** Sales Order = SOO */
	public static final String DOCBASETYPE_SalesOrder = "SOO";
	/** Set Document BaseType.
		@param DocBaseType 
		Logical type of document
	  */
	public void setDocBaseType (String DocBaseType)
	{

		set_Value (COLUMNNAME_DocBaseType, DocBaseType);
	}

	/** Get Document BaseType.
		@return Logical type of document
	  */
	public String getDocBaseType () 
	{
		return (String)get_Value(COLUMNNAME_DocBaseType);
	}

	/** Set Use Contract Acct Info.
		@param IsPostingContractAcctJP Use Contract Acct Info	  */
	public void setIsPostingContractAcctJP (boolean IsPostingContractAcctJP)
	{
		set_Value (COLUMNNAME_IsPostingContractAcctJP, Boolean.valueOf(IsPostingContractAcctJP));
	}

	/** Get Use Contract Acct Info.
		@return Use Contract Acct Info	  */
	public boolean isPostingContractAcctJP () 
	{
		Object oo = get_Value(COLUMNNAME_IsPostingContractAcctJP);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Use GL Journal.
		@param IsPostingGLJournalJP Use GL Journal	  */
	public void setIsPostingGLJournalJP (boolean IsPostingGLJournalJP)
	{
		set_Value (COLUMNNAME_IsPostingGLJournalJP, Boolean.valueOf(IsPostingGLJournalJP));
	}

	/** Get Use GL Journal.
		@return Use GL Journal	  */
	public boolean isPostingGLJournalJP () 
	{
		Object oo = get_Value(COLUMNNAME_IsPostingGLJournalJP);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Use Recognition Doc.
		@param IsPostingRecognitionDocJP Use Recognition Doc	  */
	public void setIsPostingRecognitionDocJP (boolean IsPostingRecognitionDocJP)
	{
		set_Value (COLUMNNAME_IsPostingRecognitionDocJP, Boolean.valueOf(IsPostingRecognitionDocJP));
	}

	/** Get Use Recognition Doc.
		@return Use Recognition Doc	  */
	public boolean isPostingRecognitionDocJP () 
	{
		Object oo = get_Value(COLUMNNAME_IsPostingRecognitionDocJP);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Sales Transaction.
		@param IsSOTrx 
		This is a Sales Transaction
	  */
	public void setIsSOTrx (boolean IsSOTrx)
	{
		set_ValueNoCheck (COLUMNNAME_IsSOTrx, Boolean.valueOf(IsSOTrx));
	}

	/** Get Sales Transaction.
		@return This is a Sales Transaction
	  */
	public boolean isSOTrx () 
	{
		Object oo = get_Value(COLUMNNAME_IsSOTrx);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Split when Difference.
		@param IsSplitWhenDifferenceJP 
		Split document when there is a difference
	  */
	public void setIsSplitWhenDifferenceJP (boolean IsSplitWhenDifferenceJP)
	{
		set_Value (COLUMNNAME_IsSplitWhenDifferenceJP, Boolean.valueOf(IsSplitWhenDifferenceJP));
	}

	/** Get Split when Difference.
		@return Split document when there is a difference
	  */
	public boolean isSplitWhenDifferenceJP () 
	{
		Object oo = get_Value(COLUMNNAME_IsSplitWhenDifferenceJP);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Contract Acct Info.
		@param JP_Contract_Acct_ID Contract Acct Info	  */
	public void setJP_Contract_Acct_ID (int JP_Contract_Acct_ID)
	{
		if (JP_Contract_Acct_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_Contract_Acct_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_Contract_Acct_ID, Integer.valueOf(JP_Contract_Acct_ID));
	}

	/** Get Contract Acct Info.
		@return Contract Acct Info	  */
	public int getJP_Contract_Acct_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Contract_Acct_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Contract Acct Info(UU).
		@param JP_Contract_Acct_UU Contract Acct Info(UU)	  */
	public void setJP_Contract_Acct_UU (String JP_Contract_Acct_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_Contract_Acct_UU, JP_Contract_Acct_UU);
	}

	/** Get Contract Acct Info(UU).
		@return Contract Acct Info(UU)	  */
	public String getJP_Contract_Acct_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_Contract_Acct_UU);
	}

	/** Set Account Date of GL Journal.
		@param JP_GLJournal_DateAcct 
		JPIERE-0539:JPBP
	  */
	public void setJP_GLJournal_DateAcct (Timestamp JP_GLJournal_DateAcct)
	{
		set_Value (COLUMNNAME_JP_GLJournal_DateAcct, JP_GLJournal_DateAcct);
	}

	/** Get Account Date of GL Journal.
		@return JPIERE-0539:JPBP
	  */
	public Timestamp getJP_GLJournal_DateAcct () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_GLJournal_DateAcct);
	}

	/** Fixed Date = FX */
	public static final String JP_GLJOURNAL_DATEACCTSELECT_FixedDate = "FX";
	/** Account Date of Invoice = IV */
	public static final String JP_GLJOURNAL_DATEACCTSELECT_AccountDateOfInvoice = "IV";
	/** Set Account date selection of GL Journal.
		@param JP_GLJournal_DateAcctSelect 
		JPIERE-0539:JPBP
	  */
	public void setJP_GLJournal_DateAcctSelect (String JP_GLJournal_DateAcctSelect)
	{

		set_Value (COLUMNNAME_JP_GLJournal_DateAcctSelect, JP_GLJournal_DateAcctSelect);
	}

	/** Get Account date selection of GL Journal.
		@return JPIERE-0539:JPBP
	  */
	public String getJP_GLJournal_DateAcctSelect () 
	{
		return (String)get_Value(COLUMNNAME_JP_GLJournal_DateAcctSelect);
	}

	/** Both item line and no config  will not create GL Journal = BT */
	public static final String JP_GLJOURNAL_JOURNALPOLICY_BothItemLineAndNoConfigWillNotCreateGLJournal = "BT";
	/** Set Journal Policy of GL Journal.
		@param JP_GLJournal_JournalPolicy 
		JPIERE-0539:JPBP
	  */
	public void setJP_GLJournal_JournalPolicy (String JP_GLJournal_JournalPolicy)
	{

		set_Value (COLUMNNAME_JP_GLJournal_JournalPolicy, JP_GLJournal_JournalPolicy);
	}

	/** Get Journal Policy of GL Journal.
		@return JPIERE-0539:JPBP
	  */
	public String getJP_GLJournal_JournalPolicy () 
	{
		return (String)get_Value(COLUMNNAME_JP_GLJournal_JournalPolicy);
	}

	/** After Recognition = DD */
	public static final String JP_RECOGTOINVOICEPOLICY_AfterRecognition = "DD";
	/** Lump After Order All Recognized = LP */
	public static final String JP_RECOGTOINVOICEPOLICY_LumpAfterOrderAllRecognized = "LP";
	/** Manual = MA */
	public static final String JP_RECOGTOINVOICEPOLICY_Manual = "MA";
	/** Not Create Invoice from Recognition = NO */
	public static final String JP_RECOGTOINVOICEPOLICY_NotCreateInvoiceFromRecognition = "NO";
	/** Set Policy of Create Invoice From Recognition.
		@param JP_RecogToInvoicePolicy Policy of Create Invoice From Recognition	  */
	public void setJP_RecogToInvoicePolicy (String JP_RecogToInvoicePolicy)
	{

		set_Value (COLUMNNAME_JP_RecogToInvoicePolicy, JP_RecogToInvoicePolicy);
	}

	/** Get Policy of Create Invoice From Recognition.
		@return Policy of Create Invoice From Recognition	  */
	public String getJP_RecogToInvoicePolicy () 
	{
		return (String)get_Value(COLUMNNAME_JP_RecogToInvoicePolicy);
	}

	/** If no config, will be posted by default account. = DD */
	public static final String JP_RECOGNITION_JOURNALPOLICY_IfNoConfigWillBePostedByDefaultAccount = "DD";
	/** If no config,will be posted by default but tax be excluded. = DN */
	public static final String JP_RECOGNITION_JOURNALPOLICY_IfNoConfigWillBePostedByDefaultButTaxBeExcluded = "DN";
	/** If no config, the journal will not be posted. = NN */
	public static final String JP_RECOGNITION_JOURNALPOLICY_IfNoConfigTheJournalWillNotBePosted = "NN";
	/** Set Journal Policy of Recognition Doc.
		@param JP_Recognition_JournalPolicy 
		JPIERE-0536:JPBP
	  */
	public void setJP_Recognition_JournalPolicy (String JP_Recognition_JournalPolicy)
	{

		set_Value (COLUMNNAME_JP_Recognition_JournalPolicy, JP_Recognition_JournalPolicy);
	}

	/** Get Journal Policy of Recognition Doc.
		@return JPIERE-0536:JPBP
	  */
	public String getJP_Recognition_JournalPolicy () 
	{
		return (String)get_Value(COLUMNNAME_JP_Recognition_JournalPolicy);
	}

	/** Set Name.
		@param Name 
		Alphanumeric identifier of the entity
	  */
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName () 
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

	/** Set Process Now.
		@param Processing Process Now	  */
	public void setProcessing (boolean Processing)
	{
		set_Value (COLUMNNAME_Processing, Boolean.valueOf(Processing));
	}

	/** Get Process Now.
		@return Process Now	  */
	public boolean isProcessing () 
	{
		Object oo = get_Value(COLUMNNAME_Processing);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Search Key.
		@param Value 
		Search key for the record in the format required - must be unique
	  */
	public void setValue (String Value)
	{
		set_Value (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue () 
	{
		return (String)get_Value(COLUMNNAME_Value);
	}
}