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

/** Generated Model for T_Yayoi_JournalJP
 *  @author iDempiere (generated) 
 *  @version Release 7.1 - $Id$ */
public class X_T_Yayoi_JournalJP extends PO implements I_T_Yayoi_JournalJP, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20200415L;

    /** Standard Constructor */
    public X_T_Yayoi_JournalJP (Properties ctx, int T_Yayoi_JournalJP_ID, String trxName)
    {
      super (ctx, T_Yayoi_JournalJP_ID, trxName);
      /** if (T_Yayoi_JournalJP_ID == 0)
        {
			setAD_PInstance_ID (0);
			setAD_Table_ID (0);
			setC_AcctSchema_ID (0);
			setDateAcct (new Timestamp( System.currentTimeMillis() ));
			setFact_Acct_ID (0);
			setJP_Yayoi_CrAmt (0);
// 0
			setJP_Yayoi_DateAcct (null);
			setJP_Yayoi_DrAmt (0);
// 0
			setJP_Yayoi_IdentifierFlag (null);
			setJP_Yayoi_Type (0);
// 0
			setPostingType (null);
			setRecord_ID (0);
			setT_Yayoi_JournalJP_ID (0);
        } */
    }

    /** Load Constructor */
    public X_T_Yayoi_JournalJP (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_T_Yayoi_JournalJP[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_PInstance getAD_PInstance() throws RuntimeException
    {
		return (org.compiere.model.I_AD_PInstance)MTable.get(getCtx(), org.compiere.model.I_AD_PInstance.Table_Name)
			.getPO(getAD_PInstance_ID(), get_TrxName());	}

	/** Set Process Instance.
		@param AD_PInstance_ID 
		Instance of the process
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
	public int getAD_PInstance_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_PInstance_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_Table getAD_Table() throws RuntimeException
    {
		return (org.compiere.model.I_AD_Table)MTable.get(getCtx(), org.compiere.model.I_AD_Table.Table_Name)
			.getPO(getAD_Table_ID(), get_TrxName());	}

	/** Set Table.
		@param AD_Table_ID 
		Database Table information
	  */
	public void setAD_Table_ID (int AD_Table_ID)
	{
		if (AD_Table_ID < 1) 
			set_Value (COLUMNNAME_AD_Table_ID, null);
		else 
			set_Value (COLUMNNAME_AD_Table_ID, Integer.valueOf(AD_Table_ID));
	}

	/** Get Table.
		@return Database Table information
	  */
	public int getAD_Table_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Table_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_AcctSchema getC_AcctSchema() throws RuntimeException
    {
		return (org.compiere.model.I_C_AcctSchema)MTable.get(getCtx(), org.compiere.model.I_C_AcctSchema.Table_Name)
			.getPO(getC_AcctSchema_ID(), get_TrxName());	}

	/** Set Accounting Schema.
		@param C_AcctSchema_ID 
		Rules for accounting
	  */
	public void setC_AcctSchema_ID (int C_AcctSchema_ID)
	{
		if (C_AcctSchema_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_AcctSchema_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_AcctSchema_ID, Integer.valueOf(C_AcctSchema_ID));
	}

	/** Get Accounting Schema.
		@return Rules for accounting
	  */
	public int getC_AcctSchema_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_AcctSchema_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Account Date.
		@param DateAcct 
		Accounting Date
	  */
	public void setDateAcct (Timestamp DateAcct)
	{
		set_ValueNoCheck (COLUMNNAME_DateAcct, DateAcct);
	}

	/** Get Account Date.
		@return Accounting Date
	  */
	public Timestamp getDateAcct () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateAcct);
	}

	public org.compiere.model.I_Fact_Acct getFact_Acct() throws RuntimeException
    {
		return (org.compiere.model.I_Fact_Acct)MTable.get(getCtx(), org.compiere.model.I_Fact_Acct.Table_Name)
			.getPO(getFact_Acct_ID(), get_TrxName());	}

	/** Set Accounting Fact.
		@param Fact_Acct_ID Accounting Fact	  */
	public void setFact_Acct_ID (int Fact_Acct_ID)
	{
		if (Fact_Acct_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_Fact_Acct_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_Fact_Acct_ID, Integer.valueOf(Fact_Acct_ID));
	}

	/** Get Accounting Fact.
		@return Accounting Fact	  */
	public int getFact_Acct_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Fact_Acct_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set 18:Yayoi Bango.
		@param JP_Yayoi_Bango 18:Yayoi Bango	  */
	public void setJP_Yayoi_Bango (String JP_Yayoi_Bango)
	{
		set_Value (COLUMNNAME_JP_Yayoi_Bango, JP_Yayoi_Bango);
	}

	/** Get 18:Yayoi Bango.
		@return 18:Yayoi Bango	  */
	public String getJP_Yayoi_Bango () 
	{
		return (String)get_Value(COLUMNNAME_JP_Yayoi_Bango);
	}

	/** Set 25:Yayoi Chousei.
		@param JP_Yayoi_Chousei 25:Yayoi Chousei	  */
	public void setJP_Yayoi_Chousei (String JP_Yayoi_Chousei)
	{
		set_Value (COLUMNNAME_JP_Yayoi_Chousei, JP_Yayoi_Chousei);
	}

	/** Get 25:Yayoi Chousei.
		@return 25:Yayoi Chousei	  */
	public String getJP_Yayoi_Chousei () 
	{
		return (String)get_Value(COLUMNNAME_JP_Yayoi_Chousei);
	}

	/** Set 11:Yayoi Cr Acct.
		@param JP_Yayoi_CrAcct 11:Yayoi Cr Acct	  */
	public void setJP_Yayoi_CrAcct (String JP_Yayoi_CrAcct)
	{
		set_Value (COLUMNNAME_JP_Yayoi_CrAcct, JP_Yayoi_CrAcct);
	}

	/** Get 11:Yayoi Cr Acct.
		@return 11:Yayoi Cr Acct	  */
	public String getJP_Yayoi_CrAcct () 
	{
		return (String)get_Value(COLUMNNAME_JP_Yayoi_CrAcct);
	}

	/** Set 15:Yayoi Cr Amt.
		@param JP_Yayoi_CrAmt 15:Yayoi Cr Amt	  */
	public void setJP_Yayoi_CrAmt (int JP_Yayoi_CrAmt)
	{
		set_Value (COLUMNNAME_JP_Yayoi_CrAmt, Integer.valueOf(JP_Yayoi_CrAmt));
	}

	/** Get 15:Yayoi Cr Amt.
		@return 15:Yayoi Cr Amt	  */
	public int getJP_Yayoi_CrAmt () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Yayoi_CrAmt);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set 13:Yayoi Cr Org.
		@param JP_Yayoi_CrOrg 13:Yayoi Cr Org	  */
	public void setJP_Yayoi_CrOrg (String JP_Yayoi_CrOrg)
	{
		set_Value (COLUMNNAME_JP_Yayoi_CrOrg, JP_Yayoi_CrOrg);
	}

	/** Get 13:Yayoi Cr Org.
		@return 13:Yayoi Cr Org	  */
	public String getJP_Yayoi_CrOrg () 
	{
		return (String)get_Value(COLUMNNAME_JP_Yayoi_CrOrg);
	}

	/** Set 12:Yayoi Cr Sub Acct.
		@param JP_Yayoi_CrSubAcct 12:Yayoi Cr Sub Acct	  */
	public void setJP_Yayoi_CrSubAcct (String JP_Yayoi_CrSubAcct)
	{
		set_Value (COLUMNNAME_JP_Yayoi_CrSubAcct, JP_Yayoi_CrSubAcct);
	}

	/** Get 12:Yayoi Cr Sub Acct.
		@return 12:Yayoi Cr Sub Acct	  */
	public String getJP_Yayoi_CrSubAcct () 
	{
		return (String)get_Value(COLUMNNAME_JP_Yayoi_CrSubAcct);
	}

	/** Set 14:Yayoi Cr Tax.
		@param JP_Yayoi_CrTax 14:Yayoi Cr Tax	  */
	public void setJP_Yayoi_CrTax (String JP_Yayoi_CrTax)
	{
		set_Value (COLUMNNAME_JP_Yayoi_CrTax, JP_Yayoi_CrTax);
	}

	/** Get 14:Yayoi Cr Tax.
		@return 14:Yayoi Cr Tax	  */
	public String getJP_Yayoi_CrTax () 
	{
		return (String)get_Value(COLUMNNAME_JP_Yayoi_CrTax);
	}

	/** Set 16:Yayoi Cr Tax Amt.
		@param JP_Yayoi_CrTaxAmt 16:Yayoi Cr Tax Amt	  */
	public void setJP_Yayoi_CrTaxAmt (int JP_Yayoi_CrTaxAmt)
	{
		set_Value (COLUMNNAME_JP_Yayoi_CrTaxAmt, Integer.valueOf(JP_Yayoi_CrTaxAmt));
	}

	/** Get 16:Yayoi Cr Tax Amt.
		@return 16:Yayoi Cr Tax Amt	  */
	public int getJP_Yayoi_CrTaxAmt () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Yayoi_CrTaxAmt);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set 04:Yayoi DateAcct.
		@param JP_Yayoi_DateAcct 04:Yayoi DateAcct	  */
	public void setJP_Yayoi_DateAcct (String JP_Yayoi_DateAcct)
	{
		set_Value (COLUMNNAME_JP_Yayoi_DateAcct, JP_Yayoi_DateAcct);
	}

	/** Get 04:Yayoi DateAcct.
		@return 04:Yayoi DateAcct	  */
	public String getJP_Yayoi_DateAcct () 
	{
		return (String)get_Value(COLUMNNAME_JP_Yayoi_DateAcct);
	}

	/** Set 02:Yayoi Doc No.
		@param JP_Yayoi_DocNo 02:Yayoi Doc No	  */
	public void setJP_Yayoi_DocNo (int JP_Yayoi_DocNo)
	{
		set_Value (COLUMNNAME_JP_Yayoi_DocNo, Integer.valueOf(JP_Yayoi_DocNo));
	}

	/** Get 02:Yayoi Doc No.
		@return 02:Yayoi Doc No	  */
	public int getJP_Yayoi_DocNo () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Yayoi_DocNo);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set 05:Yayoi Dr Acct.
		@param JP_Yayoi_DrAcct 05:Yayoi Dr Acct	  */
	public void setJP_Yayoi_DrAcct (String JP_Yayoi_DrAcct)
	{
		set_Value (COLUMNNAME_JP_Yayoi_DrAcct, JP_Yayoi_DrAcct);
	}

	/** Get 05:Yayoi Dr Acct.
		@return 05:Yayoi Dr Acct	  */
	public String getJP_Yayoi_DrAcct () 
	{
		return (String)get_Value(COLUMNNAME_JP_Yayoi_DrAcct);
	}

	/** Set 09:Yayoi Dr Amt.
		@param JP_Yayoi_DrAmt 09:Yayoi Dr Amt	  */
	public void setJP_Yayoi_DrAmt (int JP_Yayoi_DrAmt)
	{
		set_Value (COLUMNNAME_JP_Yayoi_DrAmt, Integer.valueOf(JP_Yayoi_DrAmt));
	}

	/** Get 09:Yayoi Dr Amt.
		@return 09:Yayoi Dr Amt	  */
	public int getJP_Yayoi_DrAmt () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Yayoi_DrAmt);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set 07:Yayoi Dr Org.
		@param JP_Yayoi_DrOrg 07:Yayoi Dr Org	  */
	public void setJP_Yayoi_DrOrg (String JP_Yayoi_DrOrg)
	{
		set_Value (COLUMNNAME_JP_Yayoi_DrOrg, JP_Yayoi_DrOrg);
	}

	/** Get 07:Yayoi Dr Org.
		@return 07:Yayoi Dr Org	  */
	public String getJP_Yayoi_DrOrg () 
	{
		return (String)get_Value(COLUMNNAME_JP_Yayoi_DrOrg);
	}

	/** Set 06:Yayoi Dr Sub Acct.
		@param JP_Yayoi_DrSubAcct 06:Yayoi Dr Sub Acct	  */
	public void setJP_Yayoi_DrSubAcct (String JP_Yayoi_DrSubAcct)
	{
		set_Value (COLUMNNAME_JP_Yayoi_DrSubAcct, JP_Yayoi_DrSubAcct);
	}

	/** Get 06:Yayoi Dr Sub Acct.
		@return 06:Yayoi Dr Sub Acct	  */
	public String getJP_Yayoi_DrSubAcct () 
	{
		return (String)get_Value(COLUMNNAME_JP_Yayoi_DrSubAcct);
	}

	/** Set 08:Yayoi Dr Tax.
		@param JP_Yayoi_DrTax 08:Yayoi Dr Tax	  */
	public void setJP_Yayoi_DrTax (String JP_Yayoi_DrTax)
	{
		set_Value (COLUMNNAME_JP_Yayoi_DrTax, JP_Yayoi_DrTax);
	}

	/** Get 08:Yayoi Dr Tax.
		@return 08:Yayoi Dr Tax	  */
	public String getJP_Yayoi_DrTax () 
	{
		return (String)get_Value(COLUMNNAME_JP_Yayoi_DrTax);
	}

	/** Set 10:Yayoi Dr Tax Amt.
		@param JP_Yayoi_DrTaxAmt 10:Yayoi Dr Tax Amt	  */
	public void setJP_Yayoi_DrTaxAmt (int JP_Yayoi_DrTaxAmt)
	{
		set_Value (COLUMNNAME_JP_Yayoi_DrTaxAmt, Integer.valueOf(JP_Yayoi_DrTaxAmt));
	}

	/** Get 10:Yayoi Dr Tax Amt.
		@return 10:Yayoi Dr Tax Amt	  */
	public int getJP_Yayoi_DrTaxAmt () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Yayoi_DrTaxAmt);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set 23:Yayoi Fusen1.
		@param JP_Yayoi_Fusen1 23:Yayoi Fusen1	  */
	public void setJP_Yayoi_Fusen1 (int JP_Yayoi_Fusen1)
	{
		set_Value (COLUMNNAME_JP_Yayoi_Fusen1, Integer.valueOf(JP_Yayoi_Fusen1));
	}

	/** Get 23:Yayoi Fusen1.
		@return 23:Yayoi Fusen1	  */
	public int getJP_Yayoi_Fusen1 () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Yayoi_Fusen1);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set 24:Yayoi Fusen2.
		@param JP_Yayoi_Fusen2 24:Yayoi Fusen2	  */
	public void setJP_Yayoi_Fusen2 (int JP_Yayoi_Fusen2)
	{
		set_Value (COLUMNNAME_JP_Yayoi_Fusen2, Integer.valueOf(JP_Yayoi_Fusen2));
	}

	/** Get 24:Yayoi Fusen2.
		@return 24:Yayoi Fusen2	  */
	public int getJP_Yayoi_Fusen2 () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Yayoi_Fusen2);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set 01:Yayoi Identifier Flag.
		@param JP_Yayoi_IdentifierFlag 01:Yayoi Identifier Flag	  */
	public void setJP_Yayoi_IdentifierFlag (String JP_Yayoi_IdentifierFlag)
	{
		set_Value (COLUMNNAME_JP_Yayoi_IdentifierFlag, JP_Yayoi_IdentifierFlag);
	}

	/** Get 01:Yayoi Identifier Flag.
		@return 01:Yayoi Identifier Flag	  */
	public String getJP_Yayoi_IdentifierFlag () 
	{
		return (String)get_Value(COLUMNNAME_JP_Yayoi_IdentifierFlag);
	}

	/** Set 03:Yayoi Kessan.
		@param JP_Yayoi_Kessan 03:Yayoi Kessan	  */
	public void setJP_Yayoi_Kessan (String JP_Yayoi_Kessan)
	{
		set_Value (COLUMNNAME_JP_Yayoi_Kessan, JP_Yayoi_Kessan);
	}

	/** Get 03:Yayoi Kessan.
		@return 03:Yayoi Kessan	  */
	public String getJP_Yayoi_Kessan () 
	{
		return (String)get_Value(COLUMNNAME_JP_Yayoi_Kessan);
	}

	/** Set 19:Yayoi Kijitu.
		@param JP_Yayoi_Kijitu 19:Yayoi Kijitu	  */
	public void setJP_Yayoi_Kijitu (String JP_Yayoi_Kijitu)
	{
		set_Value (COLUMNNAME_JP_Yayoi_Kijitu, JP_Yayoi_Kijitu);
	}

	/** Get 19:Yayoi Kijitu.
		@return 19:Yayoi Kijitu	  */
	public String getJP_Yayoi_Kijitu () 
	{
		return (String)get_Value(COLUMNNAME_JP_Yayoi_Kijitu);
	}

	/** Set 21:Yayoi Seiseimoto.
		@param JP_Yayoi_Seiseimoto 21:Yayoi Seiseimoto	  */
	public void setJP_Yayoi_Seiseimoto (String JP_Yayoi_Seiseimoto)
	{
		set_Value (COLUMNNAME_JP_Yayoi_Seiseimoto, JP_Yayoi_Seiseimoto);
	}

	/** Get 21:Yayoi Seiseimoto.
		@return 21:Yayoi Seiseimoto	  */
	public String getJP_Yayoi_Seiseimoto () 
	{
		return (String)get_Value(COLUMNNAME_JP_Yayoi_Seiseimoto);
	}

	/** Set 22:Yayoi Shiwakememo.
		@param JP_Yayoi_Shiwakememo 22:Yayoi Shiwakememo	  */
	public void setJP_Yayoi_Shiwakememo (String JP_Yayoi_Shiwakememo)
	{
		set_Value (COLUMNNAME_JP_Yayoi_Shiwakememo, JP_Yayoi_Shiwakememo);
	}

	/** Get 22:Yayoi Shiwakememo.
		@return 22:Yayoi Shiwakememo	  */
	public String getJP_Yayoi_Shiwakememo () 
	{
		return (String)get_Value(COLUMNNAME_JP_Yayoi_Shiwakememo);
	}

	/** Set 17:Yayoi Tekiyou.
		@param JP_Yayoi_Tekiyou 17:Yayoi Tekiyou	  */
	public void setJP_Yayoi_Tekiyou (String JP_Yayoi_Tekiyou)
	{
		set_Value (COLUMNNAME_JP_Yayoi_Tekiyou, JP_Yayoi_Tekiyou);
	}

	/** Get 17:Yayoi Tekiyou.
		@return 17:Yayoi Tekiyou	  */
	public String getJP_Yayoi_Tekiyou () 
	{
		return (String)get_Value(COLUMNNAME_JP_Yayoi_Tekiyou);
	}

	/** Set 20:Yayoi Type.
		@param JP_Yayoi_Type 20:Yayoi Type	  */
	public void setJP_Yayoi_Type (int JP_Yayoi_Type)
	{
		set_Value (COLUMNNAME_JP_Yayoi_Type, Integer.valueOf(JP_Yayoi_Type));
	}

	/** Get 20:Yayoi Type.
		@return 20:Yayoi Type	  */
	public int getJP_Yayoi_Type () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Yayoi_Type);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** PostingType AD_Reference_ID=125 */
	public static final int POSTINGTYPE_AD_Reference_ID=125;
	/** Actual = A */
	public static final String POSTINGTYPE_Actual = "A";
	/** Budget = B */
	public static final String POSTINGTYPE_Budget = "B";
	/** Commitment = E */
	public static final String POSTINGTYPE_Commitment = "E";
	/** Statistical = S */
	public static final String POSTINGTYPE_Statistical = "S";
	/** Reservation = R */
	public static final String POSTINGTYPE_Reservation = "R";
	/** Set PostingType.
		@param PostingType 
		The type of posted amount for the transaction
	  */
	public void setPostingType (String PostingType)
	{

		set_Value (COLUMNNAME_PostingType, PostingType);
	}

	/** Get PostingType.
		@return The type of posted amount for the transaction
	  */
	public String getPostingType () 
	{
		return (String)get_Value(COLUMNNAME_PostingType);
	}

	/** Set Record ID.
		@param Record_ID 
		Direct internal record ID
	  */
	public void setRecord_ID (int Record_ID)
	{
		if (Record_ID < 0) 
			set_ValueNoCheck (COLUMNNAME_Record_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_Record_ID, Integer.valueOf(Record_ID));
	}

	/** Get Record ID.
		@return Direct internal record ID
	  */
	public int getRecord_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Record_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Yayoi Journal.
		@param T_Yayoi_JournalJP_ID Yayoi Journal	  */
	public void setT_Yayoi_JournalJP_ID (int T_Yayoi_JournalJP_ID)
	{
		if (T_Yayoi_JournalJP_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_T_Yayoi_JournalJP_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_T_Yayoi_JournalJP_ID, Integer.valueOf(T_Yayoi_JournalJP_ID));
	}

	/** Get Yayoi Journal.
		@return Yayoi Journal	  */
	public int getT_Yayoi_JournalJP_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_T_Yayoi_JournalJP_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Yayoi Journal.
		@param T_Yayoi_JournalJP_UU Yayoi Journal	  */
	public void setT_Yayoi_JournalJP_UU (String T_Yayoi_JournalJP_UU)
	{
		set_ValueNoCheck (COLUMNNAME_T_Yayoi_JournalJP_UU, T_Yayoi_JournalJP_UU);
	}

	/** Get Yayoi Journal.
		@return Yayoi Journal	  */
	public String getT_Yayoi_JournalJP_UU () 
	{
		return (String)get_Value(COLUMNNAME_T_Yayoi_JournalJP_UU);
	}
}