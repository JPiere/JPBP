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
package jpiere.base.plugin.org.adempiere.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Interface for T_Yayoi_JournalJP
 *  @author iDempiere (generated) 
 *  @version Release 7.1
 */
@SuppressWarnings("all")
public interface I_T_Yayoi_JournalJP 
{

    /** TableName=T_Yayoi_JournalJP */
    public static final String Table_Name = "T_Yayoi_JournalJP";

    /** AD_Table_ID=1000241 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 3 - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(3);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Client.
	  * Client/Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within client
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within client
	  */
	public int getAD_Org_ID();

    /** Column name AD_PInstance_ID */
    public static final String COLUMNNAME_AD_PInstance_ID = "AD_PInstance_ID";

	/** Set Process Instance.
	  * Instance of the process
	  */
	public void setAD_PInstance_ID (int AD_PInstance_ID);

	/** Get Process Instance.
	  * Instance of the process
	  */
	public int getAD_PInstance_ID();

	public org.compiere.model.I_AD_PInstance getAD_PInstance() throws RuntimeException;

    /** Column name AD_Table_ID */
    public static final String COLUMNNAME_AD_Table_ID = "AD_Table_ID";

	/** Set Table.
	  * Database Table information
	  */
	public void setAD_Table_ID (int AD_Table_ID);

	/** Get Table.
	  * Database Table information
	  */
	public int getAD_Table_ID();

	public org.compiere.model.I_AD_Table getAD_Table() throws RuntimeException;

    /** Column name C_AcctSchema_ID */
    public static final String COLUMNNAME_C_AcctSchema_ID = "C_AcctSchema_ID";

	/** Set Accounting Schema.
	  * Rules for accounting
	  */
	public void setC_AcctSchema_ID (int C_AcctSchema_ID);

	/** Get Accounting Schema.
	  * Rules for accounting
	  */
	public int getC_AcctSchema_ID();

	public org.compiere.model.I_C_AcctSchema getC_AcctSchema() throws RuntimeException;

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

    /** Column name DateAcct */
    public static final String COLUMNNAME_DateAcct = "DateAcct";

	/** Set Account Date.
	  * Accounting Date
	  */
	public void setDateAcct (Timestamp DateAcct);

	/** Get Account Date.
	  * Accounting Date
	  */
	public Timestamp getDateAcct();

    /** Column name Fact_Acct_ID */
    public static final String COLUMNNAME_Fact_Acct_ID = "Fact_Acct_ID";

	/** Set Accounting Fact	  */
	public void setFact_Acct_ID (int Fact_Acct_ID);

	/** Get Accounting Fact	  */
	public int getFact_Acct_ID();

	public org.compiere.model.I_Fact_Acct getFact_Acct() throws RuntimeException;

    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/** Set Active.
	  * The record is active in the system
	  */
	public void setIsActive (boolean IsActive);

	/** Get Active.
	  * The record is active in the system
	  */
	public boolean isActive();

    /** Column name JP_Yayoi_Bango */
    public static final String COLUMNNAME_JP_Yayoi_Bango = "JP_Yayoi_Bango";

	/** Set 18:Yayoi Bango	  */
	public void setJP_Yayoi_Bango (String JP_Yayoi_Bango);

	/** Get 18:Yayoi Bango	  */
	public String getJP_Yayoi_Bango();

    /** Column name JP_Yayoi_Chousei */
    public static final String COLUMNNAME_JP_Yayoi_Chousei = "JP_Yayoi_Chousei";

	/** Set 25:Yayoi Chousei	  */
	public void setJP_Yayoi_Chousei (String JP_Yayoi_Chousei);

	/** Get 25:Yayoi Chousei	  */
	public String getJP_Yayoi_Chousei();

    /** Column name JP_Yayoi_CrAcct */
    public static final String COLUMNNAME_JP_Yayoi_CrAcct = "JP_Yayoi_CrAcct";

	/** Set 11:Yayoi Cr Acct	  */
	public void setJP_Yayoi_CrAcct (String JP_Yayoi_CrAcct);

	/** Get 11:Yayoi Cr Acct	  */
	public String getJP_Yayoi_CrAcct();

    /** Column name JP_Yayoi_CrAmt */
    public static final String COLUMNNAME_JP_Yayoi_CrAmt = "JP_Yayoi_CrAmt";

	/** Set 15:Yayoi Cr Amt	  */
	public void setJP_Yayoi_CrAmt (int JP_Yayoi_CrAmt);

	/** Get 15:Yayoi Cr Amt	  */
	public int getJP_Yayoi_CrAmt();

    /** Column name JP_Yayoi_CrOrg */
    public static final String COLUMNNAME_JP_Yayoi_CrOrg = "JP_Yayoi_CrOrg";

	/** Set 13:Yayoi Cr Org	  */
	public void setJP_Yayoi_CrOrg (String JP_Yayoi_CrOrg);

	/** Get 13:Yayoi Cr Org	  */
	public String getJP_Yayoi_CrOrg();

    /** Column name JP_Yayoi_CrSubAcct */
    public static final String COLUMNNAME_JP_Yayoi_CrSubAcct = "JP_Yayoi_CrSubAcct";

	/** Set 12:Yayoi Cr Sub Acct	  */
	public void setJP_Yayoi_CrSubAcct (String JP_Yayoi_CrSubAcct);

	/** Get 12:Yayoi Cr Sub Acct	  */
	public String getJP_Yayoi_CrSubAcct();

    /** Column name JP_Yayoi_CrTax */
    public static final String COLUMNNAME_JP_Yayoi_CrTax = "JP_Yayoi_CrTax";

	/** Set 14:Yayoi Cr Tax	  */
	public void setJP_Yayoi_CrTax (String JP_Yayoi_CrTax);

	/** Get 14:Yayoi Cr Tax	  */
	public String getJP_Yayoi_CrTax();

    /** Column name JP_Yayoi_CrTaxAmt */
    public static final String COLUMNNAME_JP_Yayoi_CrTaxAmt = "JP_Yayoi_CrTaxAmt";

	/** Set 16:Yayoi Cr Tax Amt	  */
	public void setJP_Yayoi_CrTaxAmt (int JP_Yayoi_CrTaxAmt);

	/** Get 16:Yayoi Cr Tax Amt	  */
	public int getJP_Yayoi_CrTaxAmt();

    /** Column name JP_Yayoi_DateAcct */
    public static final String COLUMNNAME_JP_Yayoi_DateAcct = "JP_Yayoi_DateAcct";

	/** Set 04:Yayoi DateAcct	  */
	public void setJP_Yayoi_DateAcct (String JP_Yayoi_DateAcct);

	/** Get 04:Yayoi DateAcct	  */
	public String getJP_Yayoi_DateAcct();

    /** Column name JP_Yayoi_DocNo */
    public static final String COLUMNNAME_JP_Yayoi_DocNo = "JP_Yayoi_DocNo";

	/** Set 02:Yayoi Doc No	  */
	public void setJP_Yayoi_DocNo (int JP_Yayoi_DocNo);

	/** Get 02:Yayoi Doc No	  */
	public int getJP_Yayoi_DocNo();

    /** Column name JP_Yayoi_DrAcct */
    public static final String COLUMNNAME_JP_Yayoi_DrAcct = "JP_Yayoi_DrAcct";

	/** Set 05:Yayoi Dr Acct	  */
	public void setJP_Yayoi_DrAcct (String JP_Yayoi_DrAcct);

	/** Get 05:Yayoi Dr Acct	  */
	public String getJP_Yayoi_DrAcct();

    /** Column name JP_Yayoi_DrAmt */
    public static final String COLUMNNAME_JP_Yayoi_DrAmt = "JP_Yayoi_DrAmt";

	/** Set 09:Yayoi Dr Amt	  */
	public void setJP_Yayoi_DrAmt (int JP_Yayoi_DrAmt);

	/** Get 09:Yayoi Dr Amt	  */
	public int getJP_Yayoi_DrAmt();

    /** Column name JP_Yayoi_DrOrg */
    public static final String COLUMNNAME_JP_Yayoi_DrOrg = "JP_Yayoi_DrOrg";

	/** Set 07:Yayoi Dr Org	  */
	public void setJP_Yayoi_DrOrg (String JP_Yayoi_DrOrg);

	/** Get 07:Yayoi Dr Org	  */
	public String getJP_Yayoi_DrOrg();

    /** Column name JP_Yayoi_DrSubAcct */
    public static final String COLUMNNAME_JP_Yayoi_DrSubAcct = "JP_Yayoi_DrSubAcct";

	/** Set 06:Yayoi Dr Sub Acct	  */
	public void setJP_Yayoi_DrSubAcct (String JP_Yayoi_DrSubAcct);

	/** Get 06:Yayoi Dr Sub Acct	  */
	public String getJP_Yayoi_DrSubAcct();

    /** Column name JP_Yayoi_DrTax */
    public static final String COLUMNNAME_JP_Yayoi_DrTax = "JP_Yayoi_DrTax";

	/** Set 08:Yayoi Dr Tax	  */
	public void setJP_Yayoi_DrTax (String JP_Yayoi_DrTax);

	/** Get 08:Yayoi Dr Tax	  */
	public String getJP_Yayoi_DrTax();

    /** Column name JP_Yayoi_DrTaxAmt */
    public static final String COLUMNNAME_JP_Yayoi_DrTaxAmt = "JP_Yayoi_DrTaxAmt";

	/** Set 10:Yayoi Dr Tax Amt	  */
	public void setJP_Yayoi_DrTaxAmt (int JP_Yayoi_DrTaxAmt);

	/** Get 10:Yayoi Dr Tax Amt	  */
	public int getJP_Yayoi_DrTaxAmt();

    /** Column name JP_Yayoi_Fusen1 */
    public static final String COLUMNNAME_JP_Yayoi_Fusen1 = "JP_Yayoi_Fusen1";

	/** Set 23:Yayoi Fusen1	  */
	public void setJP_Yayoi_Fusen1 (int JP_Yayoi_Fusen1);

	/** Get 23:Yayoi Fusen1	  */
	public int getJP_Yayoi_Fusen1();

    /** Column name JP_Yayoi_Fusen2 */
    public static final String COLUMNNAME_JP_Yayoi_Fusen2 = "JP_Yayoi_Fusen2";

	/** Set 24:Yayoi Fusen2	  */
	public void setJP_Yayoi_Fusen2 (int JP_Yayoi_Fusen2);

	/** Get 24:Yayoi Fusen2	  */
	public int getJP_Yayoi_Fusen2();

    /** Column name JP_Yayoi_IdentifierFlag */
    public static final String COLUMNNAME_JP_Yayoi_IdentifierFlag = "JP_Yayoi_IdentifierFlag";

	/** Set 01:Yayoi Identifier Flag	  */
	public void setJP_Yayoi_IdentifierFlag (String JP_Yayoi_IdentifierFlag);

	/** Get 01:Yayoi Identifier Flag	  */
	public String getJP_Yayoi_IdentifierFlag();

    /** Column name JP_Yayoi_Kessan */
    public static final String COLUMNNAME_JP_Yayoi_Kessan = "JP_Yayoi_Kessan";

	/** Set 03:Yayoi Kessan	  */
	public void setJP_Yayoi_Kessan (String JP_Yayoi_Kessan);

	/** Get 03:Yayoi Kessan	  */
	public String getJP_Yayoi_Kessan();

    /** Column name JP_Yayoi_Kijitu */
    public static final String COLUMNNAME_JP_Yayoi_Kijitu = "JP_Yayoi_Kijitu";

	/** Set 19:Yayoi Kijitu	  */
	public void setJP_Yayoi_Kijitu (String JP_Yayoi_Kijitu);

	/** Get 19:Yayoi Kijitu	  */
	public String getJP_Yayoi_Kijitu();

    /** Column name JP_Yayoi_Seiseimoto */
    public static final String COLUMNNAME_JP_Yayoi_Seiseimoto = "JP_Yayoi_Seiseimoto";

	/** Set 21:Yayoi Seiseimoto	  */
	public void setJP_Yayoi_Seiseimoto (String JP_Yayoi_Seiseimoto);

	/** Get 21:Yayoi Seiseimoto	  */
	public String getJP_Yayoi_Seiseimoto();

    /** Column name JP_Yayoi_Shiwakememo */
    public static final String COLUMNNAME_JP_Yayoi_Shiwakememo = "JP_Yayoi_Shiwakememo";

	/** Set 22:Yayoi Shiwakememo	  */
	public void setJP_Yayoi_Shiwakememo (String JP_Yayoi_Shiwakememo);

	/** Get 22:Yayoi Shiwakememo	  */
	public String getJP_Yayoi_Shiwakememo();

    /** Column name JP_Yayoi_Tekiyou */
    public static final String COLUMNNAME_JP_Yayoi_Tekiyou = "JP_Yayoi_Tekiyou";

	/** Set 17:Yayoi Tekiyou	  */
	public void setJP_Yayoi_Tekiyou (String JP_Yayoi_Tekiyou);

	/** Get 17:Yayoi Tekiyou	  */
	public String getJP_Yayoi_Tekiyou();

    /** Column name JP_Yayoi_Type */
    public static final String COLUMNNAME_JP_Yayoi_Type = "JP_Yayoi_Type";

	/** Set 20:Yayoi Type	  */
	public void setJP_Yayoi_Type (int JP_Yayoi_Type);

	/** Get 20:Yayoi Type	  */
	public int getJP_Yayoi_Type();

    /** Column name PostingType */
    public static final String COLUMNNAME_PostingType = "PostingType";

	/** Set PostingType.
	  * The type of posted amount for the transaction
	  */
	public void setPostingType (String PostingType);

	/** Get PostingType.
	  * The type of posted amount for the transaction
	  */
	public String getPostingType();

    /** Column name Record_ID */
    public static final String COLUMNNAME_Record_ID = "Record_ID";

	/** Set Record ID.
	  * Direct internal record ID
	  */
	public void setRecord_ID (int Record_ID);

	/** Get Record ID.
	  * Direct internal record ID
	  */
	public int getRecord_ID();

    /** Column name T_Yayoi_JournalJP_ID */
    public static final String COLUMNNAME_T_Yayoi_JournalJP_ID = "T_Yayoi_JournalJP_ID";

	/** Set Yayoi Journal	  */
	public void setT_Yayoi_JournalJP_ID (int T_Yayoi_JournalJP_ID);

	/** Get Yayoi Journal	  */
	public int getT_Yayoi_JournalJP_ID();

    /** Column name T_Yayoi_JournalJP_UU */
    public static final String COLUMNNAME_T_Yayoi_JournalJP_UU = "T_Yayoi_JournalJP_UU";

	/** Set Yayoi Journal	  */
	public void setT_Yayoi_JournalJP_UU (String T_Yayoi_JournalJP_UU);

	/** Get Yayoi Journal	  */
	public String getT_Yayoi_JournalJP_UU();

    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/** Get Updated.
	  * Date this record was updated
	  */
	public Timestamp getUpdated();

    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";

	/** Get Updated By.
	  * User who updated this records
	  */
	public int getUpdatedBy();
}
