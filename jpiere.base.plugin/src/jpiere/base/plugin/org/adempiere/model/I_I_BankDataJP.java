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

/** Generated Interface for I_BankDataJP
 *  @author iDempiere (generated) 
 *  @version Release 10
 */
@SuppressWarnings("all")
public interface I_I_BankDataJP 
{

    /** TableName=I_BankDataJP */
    public static final String Table_Name = "I_BankDataJP";

    /** AD_Table_ID=1000307 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 3 - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(3);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Tenant.
	  * Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_OrgTrx_ID */
    public static final String COLUMNNAME_AD_OrgTrx_ID = "AD_OrgTrx_ID";

	/** Set Trx Organization.
	  * Performing or initiating organization
	  */
	public void setAD_OrgTrx_ID (int AD_OrgTrx_ID);

	/** Get Trx Organization.
	  * Performing or initiating organization
	  */
	public int getAD_OrgTrx_ID();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within tenant
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within tenant
	  */
	public int getAD_Org_ID();

    /** Column name AccountNo */
    public static final String COLUMNNAME_AccountNo = "AccountNo";

	/** Set Account No.
	  * Account Number
	  */
	public void setAccountNo (String AccountNo);

	/** Get Account No.
	  * Account Number
	  */
	public String getAccountNo();

    /** Column name BankAccountType */
    public static final String COLUMNNAME_BankAccountType = "BankAccountType";

	/** Set Bank Account Type.
	  * Bank Account Type
	  */
	public void setBankAccountType (String BankAccountType);

	/** Get Bank Account Type.
	  * Bank Account Type
	  */
	public String getBankAccountType();

    /** Column name C_BankAccount_ID */
    public static final String COLUMNNAME_C_BankAccount_ID = "C_BankAccount_ID";

	/** Set Bank Account.
	  * Account at the Bank
	  */
	public void setC_BankAccount_ID (int C_BankAccount_ID);

	/** Get Bank Account.
	  * Account at the Bank
	  */
	public int getC_BankAccount_ID();

	public org.compiere.model.I_C_BankAccount getC_BankAccount() throws RuntimeException;

    /** Column name C_Bank_ID */
    public static final String COLUMNNAME_C_Bank_ID = "C_Bank_ID";

	/** Set Bank.
	  * Bank
	  */
	public void setC_Bank_ID (int C_Bank_ID);

	/** Get Bank.
	  * Bank
	  */
	public int getC_Bank_ID();

	public org.compiere.model.I_C_Bank getC_Bank() throws RuntimeException;

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

    /** Column name I_BankDataJP_ID */
    public static final String COLUMNNAME_I_BankDataJP_ID = "I_BankDataJP_ID";

	/** Set I_BankDataJP.
	  * JPIERE-0595:JPBP
	  */
	public void setI_BankDataJP_ID (int I_BankDataJP_ID);

	/** Get I_BankDataJP.
	  * JPIERE-0595:JPBP
	  */
	public int getI_BankDataJP_ID();

    /** Column name I_BankDataJP_UU */
    public static final String COLUMNNAME_I_BankDataJP_UU = "I_BankDataJP_UU";

	/** Set I_BankDataJP_UU	  */
	public void setI_BankDataJP_UU (String I_BankDataJP_UU);

	/** Get I_BankDataJP_UU	  */
	public String getI_BankDataJP_UU();

    /** Column name I_ErrorMsg */
    public static final String COLUMNNAME_I_ErrorMsg = "I_ErrorMsg";

	/** Set Import Error Message.
	  * Messages generated from import process
	  */
	public void setI_ErrorMsg (String I_ErrorMsg);

	/** Get Import Error Message.
	  * Messages generated from import process
	  */
	public String getI_ErrorMsg();

    /** Column name I_IsImported */
    public static final String COLUMNNAME_I_IsImported = "I_IsImported";

	/** Set Imported.
	  * Has this import been processed
	  */
	public void setI_IsImported (boolean I_IsImported);

	/** Get Imported.
	  * Has this import been processed
	  */
	public boolean isI_IsImported();

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

    /** Column name JP_A_Name */
    public static final String COLUMNNAME_JP_A_Name = "JP_A_Name";

	/** Set Account Name	  */
	public void setJP_A_Name (String JP_A_Name);

	/** Get Account Name	  */
	public String getJP_A_Name();

    /** Column name JP_A_Name_Kana */
    public static final String COLUMNNAME_JP_A_Name_Kana = "JP_A_Name_Kana";

	/** Set Account Name(Kana)	  */
	public void setJP_A_Name_Kana (String JP_A_Name_Kana);

	/** Get Account Name(Kana)	  */
	public String getJP_A_Name_Kana();

    /** Column name JP_AcctDate */
    public static final String COLUMNNAME_JP_AcctDate = "JP_AcctDate";

	/** Set Date of Account Date	  */
	public void setJP_AcctDate (String JP_AcctDate);

	/** Get Date of Account Date	  */
	public String getJP_AcctDate();

    /** Column name JP_AcctMonth */
    public static final String COLUMNNAME_JP_AcctMonth = "JP_AcctMonth";

	/** Set Month of Account Date	  */
	public void setJP_AcctMonth (String JP_AcctMonth);

	/** Get Month of Account Date	  */
	public String getJP_AcctMonth();

    /** Column name JP_BankAccountType */
    public static final String COLUMNNAME_JP_BankAccountType = "JP_BankAccountType";

	/** Set Bank Account Type	  */
	public void setJP_BankAccountType (String JP_BankAccountType);

	/** Get Bank Account Type	  */
	public String getJP_BankAccountType();

    /** Column name JP_BankAccount_Value */
    public static final String COLUMNNAME_JP_BankAccount_Value = "JP_BankAccount_Value";

	/** Set Bank Account(Search Key)	  */
	public void setJP_BankAccount_Value (String JP_BankAccount_Value);

	/** Get Bank Account(Search Key)	  */
	public String getJP_BankAccount_Value();

    /** Column name JP_BankDataCustomerCode1 */
    public static final String COLUMNNAME_JP_BankDataCustomerCode1 = "JP_BankDataCustomerCode1";

	/** Set Bank Data Customer Code1	  */
	public void setJP_BankDataCustomerCode1 (String JP_BankDataCustomerCode1);

	/** Get Bank Data Customer Code1	  */
	public String getJP_BankDataCustomerCode1();

    /** Column name JP_BankDataCustomerCode2 */
    public static final String COLUMNNAME_JP_BankDataCustomerCode2 = "JP_BankDataCustomerCode2";

	/** Set Bank Data Customer Code2	  */
	public void setJP_BankDataCustomerCode2 (String JP_BankDataCustomerCode2);

	/** Get Bank Data Customer Code2	  */
	public String getJP_BankDataCustomerCode2();

    /** Column name JP_BankDataLine_ID */
    public static final String COLUMNNAME_JP_BankDataLine_ID = "JP_BankDataLine_ID";

	/** Set Import Bank Data Line	  */
	public void setJP_BankDataLine_ID (int JP_BankDataLine_ID);

	/** Get Import Bank Data Line	  */
	public int getJP_BankDataLine_ID();

	public I_JP_BankDataLine getJP_BankDataLine() throws RuntimeException;

    /** Column name JP_BankData_EDI_Info */
    public static final String COLUMNNAME_JP_BankData_EDI_Info = "JP_BankData_EDI_Info";

	/** Set BankData EDI Info	  */
	public void setJP_BankData_EDI_Info (String JP_BankData_EDI_Info);

	/** Get BankData EDI Info	  */
	public String getJP_BankData_EDI_Info();

    /** Column name JP_BankData_ID */
    public static final String COLUMNNAME_JP_BankData_ID = "JP_BankData_ID";

	/** Set Import Bank Data	  */
	public void setJP_BankData_ID (int JP_BankData_ID);

	/** Get Import Bank Data	  */
	public int getJP_BankData_ID();

	public I_JP_BankData getJP_BankData() throws RuntimeException;

    /** Column name JP_BankData_ReferenceNo */
    public static final String COLUMNNAME_JP_BankData_ReferenceNo = "JP_BankData_ReferenceNo";

	/** Set Bank Data ReferenceNo	  */
	public void setJP_BankData_ReferenceNo (String JP_BankData_ReferenceNo);

	/** Get Bank Data ReferenceNo	  */
	public String getJP_BankData_ReferenceNo();

    /** Column name JP_BankName_Kana */
    public static final String COLUMNNAME_JP_BankName_Kana = "JP_BankName_Kana";

	/** Set Bank Name(Kana)	  */
	public void setJP_BankName_Kana (String JP_BankName_Kana);

	/** Get Bank Name(Kana)	  */
	public String getJP_BankName_Kana();

    /** Column name JP_BankName_Kana_Line */
    public static final String COLUMNNAME_JP_BankName_Kana_Line = "JP_BankName_Kana_Line";

	/** Set Bank Name(Kana) Line	  */
	public void setJP_BankName_Kana_Line (String JP_BankName_Kana_Line);

	/** Get Bank Name(Kana) Line	  */
	public String getJP_BankName_Kana_Line();

    /** Column name JP_Bank_Name */
    public static final String COLUMNNAME_JP_Bank_Name = "JP_Bank_Name";

	/** Set Bank Name	  */
	public void setJP_Bank_Name (String JP_Bank_Name);

	/** Get Bank Name	  */
	public String getJP_Bank_Name();

    /** Column name JP_BranchCode */
    public static final String COLUMNNAME_JP_BranchCode = "JP_BranchCode";

	/** Set Branch Code	  */
	public void setJP_BranchCode (String JP_BranchCode);

	/** Get Branch Code	  */
	public String getJP_BranchCode();

    /** Column name JP_BranchName */
    public static final String COLUMNNAME_JP_BranchName = "JP_BranchName";

	/** Set Branch Name	  */
	public void setJP_BranchName (String JP_BranchName);

	/** Get Branch Name	  */
	public String getJP_BranchName();

    /** Column name JP_BranchName_Kana */
    public static final String COLUMNNAME_JP_BranchName_Kana = "JP_BranchName_Kana";

	/** Set Branch Name(Kana)	  */
	public void setJP_BranchName_Kana (String JP_BranchName_Kana);

	/** Get Branch Name(Kana)	  */
	public String getJP_BranchName_Kana();

    /** Column name JP_BranchName_Kana_Line */
    public static final String COLUMNNAME_JP_BranchName_Kana_Line = "JP_BranchName_Kana_Line";

	/** Set Branch Name(Kana) Line	  */
	public void setJP_BranchName_Kana_Line (String JP_BranchName_Kana_Line);

	/** Get Branch Name(Kana) Line	  */
	public String getJP_BranchName_Kana_Line();

    /** Column name JP_Date */
    public static final String COLUMNNAME_JP_Date = "JP_Date";

	/** Set Date.
	  * Date
	  */
	public void setJP_Date (String JP_Date);

	/** Get Date.
	  * Date
	  */
	public String getJP_Date();

    /** Column name JP_Line_Description */
    public static final String COLUMNNAME_JP_Line_Description = "JP_Line_Description";

	/** Set Line Description	  */
	public void setJP_Line_Description (String JP_Line_Description);

	/** Get Line Description	  */
	public String getJP_Line_Description();

    /** Column name JP_Month */
    public static final String COLUMNNAME_JP_Month = "JP_Month";

	/** Set Month	  */
	public void setJP_Month (String JP_Month);

	/** Get Month	  */
	public String getJP_Month();

    /** Column name JP_OrgTrx_Value */
    public static final String COLUMNNAME_JP_OrgTrx_Value = "JP_OrgTrx_Value";

	/** Set Trx Organization(Search Key)	  */
	public void setJP_OrgTrx_Value (String JP_OrgTrx_Value);

	/** Get Trx Organization(Search Key)	  */
	public String getJP_OrgTrx_Value();

    /** Column name JP_Org_Value */
    public static final String COLUMNNAME_JP_Org_Value = "JP_Org_Value";

	/** Set Organization(Search Key)	  */
	public void setJP_Org_Value (String JP_Org_Value);

	/** Get Organization(Search Key)	  */
	public String getJP_Org_Value();

    /** Column name JP_RequesterName */
    public static final String COLUMNNAME_JP_RequesterName = "JP_RequesterName";

	/** Set Requester Name	  */
	public void setJP_RequesterName (String JP_RequesterName);

	/** Get Requester Name	  */
	public String getJP_RequesterName();

    /** Column name JP_SalesRep_EMail */
    public static final String COLUMNNAME_JP_SalesRep_EMail = "JP_SalesRep_EMail";

	/** Set Sales Rep(E-Mail)	  */
	public void setJP_SalesRep_EMail (String JP_SalesRep_EMail);

	/** Get Sales Rep(E-Mail)	  */
	public String getJP_SalesRep_EMail();

    /** Column name JP_SalesRep_Name */
    public static final String COLUMNNAME_JP_SalesRep_Name = "JP_SalesRep_Name";

	/** Set Sales Rep(Name)	  */
	public void setJP_SalesRep_Name (String JP_SalesRep_Name);

	/** Get Sales Rep(Name)	  */
	public String getJP_SalesRep_Name();

    /** Column name JP_SalesRep_Value */
    public static final String COLUMNNAME_JP_SalesRep_Value = "JP_SalesRep_Value";

	/** Set Sales Rep(Search Key)	  */
	public void setJP_SalesRep_Value (String JP_SalesRep_Value);

	/** Get Sales Rep(Search Key)	  */
	public String getJP_SalesRep_Value();

    /** Column name Processed */
    public static final String COLUMNNAME_Processed = "Processed";

	/** Set Processed.
	  * The document has been processed
	  */
	public void setProcessed (boolean Processed);

	/** Get Processed.
	  * The document has been processed
	  */
	public boolean isProcessed();

    /** Column name Processing */
    public static final String COLUMNNAME_Processing = "Processing";

	/** Set Process Now	  */
	public void setProcessing (boolean Processing);

	/** Get Process Now	  */
	public boolean isProcessing();

    /** Column name RoutingNo */
    public static final String COLUMNNAME_RoutingNo = "RoutingNo";

	/** Set Routing No.
	  * Bank Routing Number
	  */
	public void setRoutingNo (String RoutingNo);

	/** Get Routing No.
	  * Bank Routing Number
	  */
	public String getRoutingNo();

    /** Column name SalesRep_ID */
    public static final String COLUMNNAME_SalesRep_ID = "SalesRep_ID";

	/** Set Sales Rep.
	  * Sales Representative or Company Agent
	  */
	public void setSalesRep_ID (int SalesRep_ID);

	/** Get Sales Rep.
	  * Sales Representative or Company Agent
	  */
	public int getSalesRep_ID();

	public org.compiere.model.I_AD_User getSalesRep() throws RuntimeException;

    /** Column name StatementDate */
    public static final String COLUMNNAME_StatementDate = "StatementDate";

	/** Set Statement date.
	  * Date of the statement
	  */
	public void setStatementDate (Timestamp StatementDate);

	/** Get Statement date.
	  * Date of the statement
	  */
	public Timestamp getStatementDate();

    /** Column name StmtAmt */
    public static final String COLUMNNAME_StmtAmt = "StmtAmt";

	/** Set Statement amount.
	  * Statement Amount
	  */
	public void setStmtAmt (BigDecimal StmtAmt);

	/** Get Statement amount.
	  * Statement Amount
	  */
	public BigDecimal getStmtAmt();

    /** Column name TrxAmt */
    public static final String COLUMNNAME_TrxAmt = "TrxAmt";

	/** Set Transaction Amount.
	  * Amount of a transaction
	  */
	public void setTrxAmt (BigDecimal TrxAmt);

	/** Get Transaction Amount.
	  * Amount of a transaction
	  */
	public BigDecimal getTrxAmt();

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
