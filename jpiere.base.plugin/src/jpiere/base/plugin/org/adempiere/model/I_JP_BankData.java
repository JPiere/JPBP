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

/** Generated Interface for JP_BankData
 *  @author iDempiere (generated) 
 *  @version Release 4.1
 */
@SuppressWarnings("all")
public interface I_JP_BankData 
{

    /** TableName=JP_BankData */
    public static final String Table_Name = "JP_BankData";

    /** AD_Table_ID=1000112 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 1 - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(1);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Client.
	  * Client/Tenant for this installation.
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
	  * Organizational entity within client
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within client
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

    /** Column name C_BankStatement_ID */
    public static final String COLUMNNAME_C_BankStatement_ID = "C_BankStatement_ID";

	/** Set Bank Statement.
	  * Bank Statement of account
	  */
	public void setC_BankStatement_ID (int C_BankStatement_ID);

	/** Get Bank Statement.
	  * Bank Statement of account
	  */
	public int getC_BankStatement_ID();

	public org.compiere.model.I_C_BankStatement getC_BankStatement() throws RuntimeException;

    /** Column name C_Currency_ID */
    public static final String COLUMNNAME_C_Currency_ID = "C_Currency_ID";

	/** Set Currency.
	  * The Currency for this record
	  */
	public void setC_Currency_ID (int C_Currency_ID);

	/** Get Currency.
	  * The Currency for this record
	  */
	public int getC_Currency_ID();

	public org.compiere.model.I_C_Currency getC_Currency() throws RuntimeException;

    /** Column name ChargeAmt */
    public static final String COLUMNNAME_ChargeAmt = "ChargeAmt";

	/** Set Charge amount.
	  * Charge Amount
	  */
	public void setChargeAmt (BigDecimal ChargeAmt);

	/** Get Charge amount.
	  * Charge Amount
	  */
	public BigDecimal getChargeAmt();

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

    /** Column name Description */
    public static final String COLUMNNAME_Description = "Description";

	/** Set Description.
	  * Optional short description of the record
	  */
	public void setDescription (String Description);

	/** Get Description.
	  * Optional short description of the record
	  */
	public String getDescription();

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

    /** Column name InterestAmt */
    public static final String COLUMNNAME_InterestAmt = "InterestAmt";

	/** Set Interest Amount.
	  * Interest Amount
	  */
	public void setInterestAmt (BigDecimal InterestAmt);

	/** Get Interest Amount.
	  * Interest Amount
	  */
	public BigDecimal getInterestAmt();

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

    /** Column name IsReceipt */
    public static final String COLUMNNAME_IsReceipt = "IsReceipt";

	/** Set Receipt.
	  * This is a sales transaction (receipt)
	  */
	public void setIsReceipt (boolean IsReceipt);

	/** Get Receipt.
	  * This is a sales transaction (receipt)
	  */
	public boolean isReceipt();

    /** Column name JP_BankAccountType */
    public static final String COLUMNNAME_JP_BankAccountType = "JP_BankAccountType";

	/** Set Bank Account Type	  */
	public void setJP_BankAccountType (String JP_BankAccountType);

	/** Get Bank Account Type	  */
	public String getJP_BankAccountType();

    /** Column name JP_BankDataClassification */
    public static final String COLUMNNAME_JP_BankDataClassification = "JP_BankDataClassification";

	/** Set Bank Data Classification	  */
	public void setJP_BankDataClassification (String JP_BankDataClassification);

	/** Get Bank Data Classification	  */
	public String getJP_BankDataClassification();

    /** Column name JP_BankDataCodeType */
    public static final String COLUMNNAME_JP_BankDataCodeType = "JP_BankDataCodeType";

	/** Set Bank Data Code Type	  */
	public void setJP_BankDataCodeType (String JP_BankDataCodeType);

	/** Get Bank Data Code Type	  */
	public String getJP_BankDataCodeType();

    /** Column name JP_BankDataSchema_ID */
    public static final String COLUMNNAME_JP_BankDataSchema_ID = "JP_BankDataSchema_ID";

	/** Set Import Bank Data Schema	  */
	public void setJP_BankDataSchema_ID (int JP_BankDataSchema_ID);

	/** Get Import Bank Data Schema	  */
	public int getJP_BankDataSchema_ID();

	public I_JP_BankDataSchema getJP_BankDataSchema() throws RuntimeException;

    /** Column name JP_BankDataType_Footer */
    public static final String COLUMNNAME_JP_BankDataType_Footer = "JP_BankDataType_Footer";

	/** Set Bank Datat Type(Footer)	  */
	public void setJP_BankDataType_Footer (String JP_BankDataType_Footer);

	/** Get Bank Datat Type(Footer)	  */
	public String getJP_BankDataType_Footer();

    /** Column name JP_BankDataType_Header */
    public static final String COLUMNNAME_JP_BankDataType_Header = "JP_BankDataType_Header";

	/** Set Bank Data Type(Header)	  */
	public void setJP_BankDataType_Header (String JP_BankDataType_Header);

	/** Get Bank Data Type(Header)	  */
	public String getJP_BankDataType_Header();

    /** Column name JP_BankData_ID */
    public static final String COLUMNNAME_JP_BankData_ID = "JP_BankData_ID";

	/** Set Import Bank Data	  */
	public void setJP_BankData_ID (int JP_BankData_ID);

	/** Get Import Bank Data	  */
	public int getJP_BankData_ID();

    /** Column name JP_BankData_UU */
    public static final String COLUMNNAME_JP_BankData_UU = "JP_BankData_UU";

	/** Set JP_BankData_UU	  */
	public void setJP_BankData_UU (String JP_BankData_UU);

	/** Get JP_BankData_UU	  */
	public String getJP_BankData_UU();

    /** Column name JP_BankName_Kana */
    public static final String COLUMNNAME_JP_BankName_Kana = "JP_BankName_Kana";

	/** Set Bank Name(Kana)	  */
	public void setJP_BankName_Kana (String JP_BankName_Kana);

	/** Get Bank Name(Kana)	  */
	public String getJP_BankName_Kana();

    /** Column name JP_BranchCode */
    public static final String COLUMNNAME_JP_BranchCode = "JP_BranchCode";

	/** Set Branch Code	  */
	public void setJP_BranchCode (String JP_BranchCode);

	/** Get Branch Code	  */
	public String getJP_BranchCode();

    /** Column name JP_BranchName_Kana */
    public static final String COLUMNNAME_JP_BranchName_Kana = "JP_BranchName_Kana";

	/** Set Branch Name(Kana)	  */
	public void setJP_BranchName_Kana (String JP_BranchName_Kana);

	/** Get Branch Name(Kana)	  */
	public String getJP_BranchName_Kana();

    /** Column name JP_ProcessedTime1 */
    public static final String COLUMNNAME_JP_ProcessedTime1 = "JP_ProcessedTime1";

	/** Set Processed Time	  */
	public void setJP_ProcessedTime1 (Timestamp JP_ProcessedTime1);

	/** Get Processed Time	  */
	public Timestamp getJP_ProcessedTime1();

    /** Column name JP_ProcessedTime2 */
    public static final String COLUMNNAME_JP_ProcessedTime2 = "JP_ProcessedTime2";

	/** Set Processed Time	  */
	public void setJP_ProcessedTime2 (Timestamp JP_ProcessedTime2);

	/** Get Processed Time	  */
	public Timestamp getJP_ProcessedTime2();

    /** Column name JP_ProcessedTime3 */
    public static final String COLUMNNAME_JP_ProcessedTime3 = "JP_ProcessedTime3";

	/** Set Processed Time	  */
	public void setJP_ProcessedTime3 (Timestamp JP_ProcessedTime3);

	/** Get Processed Time	  */
	public Timestamp getJP_ProcessedTime3();

    /** Column name JP_ProcessedTime4 */
    public static final String COLUMNNAME_JP_ProcessedTime4 = "JP_ProcessedTime4";

	/** Set Processed Time	  */
	public void setJP_ProcessedTime4 (Timestamp JP_ProcessedTime4);

	/** Get Processed Time	  */
	public Timestamp getJP_ProcessedTime4();

    /** Column name JP_ProcessedTime5 */
    public static final String COLUMNNAME_JP_ProcessedTime5 = "JP_ProcessedTime5";

	/** Set Processed Time	  */
	public void setJP_ProcessedTime5 (Timestamp JP_ProcessedTime5);

	/** Get Processed Time	  */
	public Timestamp getJP_ProcessedTime5();

    /** Column name JP_ProcessedTime6 */
    public static final String COLUMNNAME_JP_ProcessedTime6 = "JP_ProcessedTime6";

	/** Set Processed Time	  */
	public void setJP_ProcessedTime6 (Timestamp JP_ProcessedTime6);

	/** Get Processed Time	  */
	public Timestamp getJP_ProcessedTime6();

    /** Column name JP_Processing1 */
    public static final String COLUMNNAME_JP_Processing1 = "JP_Processing1";

	/** Set Process Now	  */
	public void setJP_Processing1 (String JP_Processing1);

	/** Get Process Now	  */
	public String getJP_Processing1();

    /** Column name JP_Processing2 */
    public static final String COLUMNNAME_JP_Processing2 = "JP_Processing2";

	/** Set Process Now	  */
	public void setJP_Processing2 (String JP_Processing2);

	/** Get Process Now	  */
	public String getJP_Processing2();

    /** Column name JP_Processing3 */
    public static final String COLUMNNAME_JP_Processing3 = "JP_Processing3";

	/** Set Process Now	  */
	public void setJP_Processing3 (String JP_Processing3);

	/** Get Process Now	  */
	public String getJP_Processing3();

    /** Column name JP_Processing4 */
    public static final String COLUMNNAME_JP_Processing4 = "JP_Processing4";

	/** Set Process Now	  */
	public void setJP_Processing4 (String JP_Processing4);

	/** Get Process Now	  */
	public String getJP_Processing4();

    /** Column name JP_Processing5 */
    public static final String COLUMNNAME_JP_Processing5 = "JP_Processing5";

	/** Set Process Now	  */
	public void setJP_Processing5 (String JP_Processing5);

	/** Get Process Now	  */
	public String getJP_Processing5();

    /** Column name JP_Processing6 */
    public static final String COLUMNNAME_JP_Processing6 = "JP_Processing6";

	/** Set Process Now	  */
	public void setJP_Processing6 (String JP_Processing6);

	/** Get Process Now	  */
	public String getJP_Processing6();

    /** Column name JP_RequesterCode */
    public static final String COLUMNNAME_JP_RequesterCode = "JP_RequesterCode";

	/** Set Requester Code	  */
	public void setJP_RequesterCode (String JP_RequesterCode);

	/** Get Requester Code	  */
	public String getJP_RequesterCode();

    /** Column name JP_RequesterName */
    public static final String COLUMNNAME_JP_RequesterName = "JP_RequesterName";

	/** Set Requester Name	  */
	public void setJP_RequesterName (String JP_RequesterName);

	/** Get Requester Name	  */
	public String getJP_RequesterName();

    /** Column name Name */
    public static final String COLUMNNAME_Name = "Name";

	/** Set Name.
	  * Alphanumeric identifier of the entity
	  */
	public void setName (String Name);

	/** Get Name.
	  * Alphanumeric identifier of the entity
	  */
	public String getName();

    /** Column name NumLines */
    public static final String COLUMNNAME_NumLines = "NumLines";

	/** Set Number of Lines.
	  * Number of lines for a field
	  */
	public void setNumLines (int NumLines);

	/** Get Number of Lines.
	  * Number of lines for a field
	  */
	public int getNumLines();

    /** Column name PayDate */
    public static final String COLUMNNAME_PayDate = "PayDate";

	/** Set Payment date.
	  * Date Payment made
	  */
	public void setPayDate (Timestamp PayDate);

	/** Get Payment date.
	  * Date Payment made
	  */
	public Timestamp getPayDate();

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

	/** Set Sales Representative.
	  * Sales Representative or Company Agent
	  */
	public void setSalesRep_ID (int SalesRep_ID);

	/** Get Sales Representative.
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

    /** Column name TotalAmt */
    public static final String COLUMNNAME_TotalAmt = "TotalAmt";

	/** Set Total Amount.
	  * Total Amount
	  */
	public void setTotalAmt (BigDecimal TotalAmt);

	/** Get Total Amount.
	  * Total Amount
	  */
	public BigDecimal getTotalAmt();

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
