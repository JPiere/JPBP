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

/** Generated Interface for JP_BankDataLine
 *  @author iDempiere (generated) 
 *  @version Release 4.1
 */
@SuppressWarnings("all")
public interface I_JP_BankDataLine 
{

    /** TableName=JP_BankDataLine */
    public static final String Table_Name = "JP_BankDataLine";

    /** AD_Table_ID=1000113 */
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

    /** Column name C_BPartner_ID */
    public static final String COLUMNNAME_C_BPartner_ID = "C_BPartner_ID";

	/** Set Business Partner .
	  * Identifies a Business Partner
	  */
	public void setC_BPartner_ID (int C_BPartner_ID);

	/** Get Business Partner .
	  * Identifies a Business Partner
	  */
	public int getC_BPartner_ID();

	public org.compiere.model.I_C_BPartner getC_BPartner() throws RuntimeException;

    /** Column name C_BankStatementLine_ID */
    public static final String COLUMNNAME_C_BankStatementLine_ID = "C_BankStatementLine_ID";

	/** Set Bank statement line.
	  * Line on a statement from this Bank
	  */
	public void setC_BankStatementLine_ID (int C_BankStatementLine_ID);

	/** Get Bank statement line.
	  * Line on a statement from this Bank
	  */
	public int getC_BankStatementLine_ID();

	public org.compiere.model.I_C_BankStatementLine getC_BankStatementLine() throws RuntimeException;

    /** Column name C_Charge_ID */
    public static final String COLUMNNAME_C_Charge_ID = "C_Charge_ID";

	/** Set Charge.
	  * Additional document charges
	  */
	public void setC_Charge_ID (int C_Charge_ID);

	/** Get Charge.
	  * Additional document charges
	  */
	public int getC_Charge_ID();

	public org.compiere.model.I_C_Charge getC_Charge() throws RuntimeException;

    /** Column name C_Invoice_ID */
    public static final String COLUMNNAME_C_Invoice_ID = "C_Invoice_ID";

	/** Set Invoice.
	  * Invoice Identifier
	  */
	public void setC_Invoice_ID (int C_Invoice_ID);

	/** Get Invoice.
	  * Invoice Identifier
	  */
	public int getC_Invoice_ID();

	public org.compiere.model.I_C_Invoice getC_Invoice() throws RuntimeException;

    /** Column name C_Payment_ID */
    public static final String COLUMNNAME_C_Payment_ID = "C_Payment_ID";

	/** Set Payment.
	  * Payment identifier
	  */
	public void setC_Payment_ID (int C_Payment_ID);

	/** Get Payment.
	  * Payment identifier
	  */
	public int getC_Payment_ID();

	public org.compiere.model.I_C_Payment getC_Payment() throws RuntimeException;

    /** Column name C_Tax_ID */
    public static final String COLUMNNAME_C_Tax_ID = "C_Tax_ID";

	/** Set Tax.
	  * Tax identifier
	  */
	public void setC_Tax_ID (int C_Tax_ID);

	/** Get Tax.
	  * Tax identifier
	  */
	public int getC_Tax_ID();

	public org.compiere.model.I_C_Tax getC_Tax() throws RuntimeException;

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

    /** Column name IsMatchedJP */
    public static final String COLUMNNAME_IsMatchedJP = "IsMatchedJP";

	/** Set Matched	  */
	public void setIsMatchedJP (boolean IsMatchedJP);

	/** Get Matched	  */
	public boolean isMatchedJP();

    /** Column name JP_A_Name_Kana */
    public static final String COLUMNNAME_JP_A_Name_Kana = "JP_A_Name_Kana";

	/** Set Account Name(Kana)	  */
	public void setJP_A_Name_Kana (String JP_A_Name_Kana);

	/** Get Account Name(Kana)	  */
	public String getJP_A_Name_Kana();

    /** Column name JP_BankAccountType */
    public static final String COLUMNNAME_JP_BankAccountType = "JP_BankAccountType";

	/** Set Bank Account Type	  */
	public void setJP_BankAccountType (String JP_BankAccountType);

	/** Get Bank Account Type	  */
	public String getJP_BankAccountType();

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

    /** Column name JP_BankDataLine_UU */
    public static final String COLUMNNAME_JP_BankDataLine_UU = "JP_BankDataLine_UU";

	/** Set JP_BankDataLine_UU	  */
	public void setJP_BankDataLine_UU (String JP_BankDataLine_UU);

	/** Get JP_BankDataLine_UU	  */
	public String getJP_BankDataLine_UU();

    /** Column name JP_BankDataNewCode */
    public static final String COLUMNNAME_JP_BankDataNewCode = "JP_BankDataNewCode";

	/** Set Bank Data New Code	  */
	public void setJP_BankDataNewCode (String JP_BankDataNewCode);

	/** Get Bank Data New Code	  */
	public String getJP_BankDataNewCode();

    /** Column name JP_BankDataType_Line */
    public static final String COLUMNNAME_JP_BankDataType_Line = "JP_BankDataType_Line";

	/** Set Bank Data Type(Line)	  */
	public void setJP_BankDataType_Line (String JP_BankDataType_Line);

	/** Get Bank Data Type(Line)	  */
	public String getJP_BankDataType_Line();

    /** Column name JP_BankData_ID */
    public static final String COLUMNNAME_JP_BankData_ID = "JP_BankData_ID";

	/** Set Import Bank Data	  */
	public void setJP_BankData_ID (int JP_BankData_ID);

	/** Get Import Bank Data	  */
	public int getJP_BankData_ID();

	public I_JP_BankData getJP_BankData() throws RuntimeException;

    /** Column name JP_BankName_Kana */
    public static final String COLUMNNAME_JP_BankName_Kana = "JP_BankName_Kana";

	/** Set Bank Name(Kana)	  */
	public void setJP_BankName_Kana (String JP_BankName_Kana);

	/** Get Bank Name(Kana)	  */
	public String getJP_BankName_Kana();

    /** Column name JP_Bill_ID */
    public static final String COLUMNNAME_JP_Bill_ID = "JP_Bill_ID";

	/** Set JP Bill	  */
	public void setJP_Bill_ID (int JP_Bill_ID);

	/** Get JP Bill	  */
	public int getJP_Bill_ID();

	public I_JP_Bill getJP_Bill() throws RuntimeException;

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

    /** Column name JP_ClearingHouse */
    public static final String COLUMNNAME_JP_ClearingHouse = "JP_ClearingHouse";

	/** Set Clearing House	  */
	public void setJP_ClearingHouse (String JP_ClearingHouse);

	/** Get Clearing House	  */
	public String getJP_ClearingHouse();

    /** Column name Line */
    public static final String COLUMNNAME_Line = "Line";

	/** Set Line No.
	  * Unique line for this document
	  */
	public void setLine (int Line);

	/** Get Line No.
	  * Unique line for this document
	  */
	public int getLine();

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

    /** Column name StatementLineDate */
    public static final String COLUMNNAME_StatementLineDate = "StatementLineDate";

	/** Set Statement Line Date.
	  * Date of the Statement Line
	  */
	public void setStatementLineDate (Timestamp StatementLineDate);

	/** Get Statement Line Date.
	  * Date of the Statement Line
	  */
	public Timestamp getStatementLineDate();

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

    /** Column name ValutaDate */
    public static final String COLUMNNAME_ValutaDate = "ValutaDate";

	/** Set Effective date.
	  * Date when money is available
	  */
	public void setValutaDate (Timestamp ValutaDate);

	/** Get Effective date.
	  * Date when money is available
	  */
	public Timestamp getValutaDate();
}
