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

/** Generated Interface for JP_BankDataSchema
 *  @author iDempiere (generated) 
 *  @version Release 4.1
 */
@SuppressWarnings("all")
public interface I_JP_BankDataSchema 
{

    /** TableName=JP_BankDataSchema */
    public static final String Table_Name = "JP_BankDataSchema";

    /** AD_Table_ID=1000111 */
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

    /** Column name IsDefault */
    public static final String COLUMNNAME_IsDefault = "IsDefault";

	/** Set Default.
	  * Default value
	  */
	public void setIsDefault (boolean IsDefault);

	/** Get Default.
	  * Default value
	  */
	public boolean isDefault();

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

    /** Column name JP_AcceptableDiffAmt */
    public static final String COLUMNNAME_JP_AcceptableDiffAmt = "JP_AcceptableDiffAmt";

	/** Set Acceptable Difference Amount	  */
	public void setJP_AcceptableDiffAmt (BigDecimal JP_AcceptableDiffAmt);

	/** Get Acceptable Difference Amount	  */
	public BigDecimal getJP_AcceptableDiffAmt();

    /** Column name JP_BankDataCreateDocClass */
    public static final String COLUMNNAME_JP_BankDataCreateDocClass = "JP_BankDataCreateDocClass";

	/** Set Class of Bank Data create Doc	  */
	public void setJP_BankDataCreateDocClass (String JP_BankDataCreateDocClass);

	/** Get Class of Bank Data create Doc	  */
	public String getJP_BankDataCreateDocClass();

    /** Column name JP_BankDataImportClass */
    public static final String COLUMNNAME_JP_BankDataImportClass = "JP_BankDataImportClass";

	/** Set Class of Bank Data Import	  */
	public void setJP_BankDataImportClass (String JP_BankDataImportClass);

	/** Get Class of Bank Data Import	  */
	public String getJP_BankDataImportClass();

    /** Column name JP_BankDataMatchBPClass */
    public static final String COLUMNNAME_JP_BankDataMatchBPClass = "JP_BankDataMatchBPClass";

	/** Set Class of Bank Data Match BP	  */
	public void setJP_BankDataMatchBPClass (String JP_BankDataMatchBPClass);

	/** Get Class of Bank Data Match BP	  */
	public String getJP_BankDataMatchBPClass();

    /** Column name JP_BankDataMatchBillClass */
    public static final String COLUMNNAME_JP_BankDataMatchBillClass = "JP_BankDataMatchBillClass";

	/** Set Class of Bank Data Match Bill	  */
	public void setJP_BankDataMatchBillClass (String JP_BankDataMatchBillClass);

	/** Get Class of Bank Data Match Bill	  */
	public String getJP_BankDataMatchBillClass();

    /** Column name JP_BankDataMatchInvClass */
    public static final String COLUMNNAME_JP_BankDataMatchInvClass = "JP_BankDataMatchInvClass";

	/** Set Class of Bank Data Match Invoice	  */
	public void setJP_BankDataMatchInvClass (String JP_BankDataMatchInvClass);

	/** Get Class of Bank Data Match Invoice	  */
	public String getJP_BankDataMatchInvClass();

    /** Column name JP_BankDataMatchPaymentClass */
    public static final String COLUMNNAME_JP_BankDataMatchPaymentClass = "JP_BankDataMatchPaymentClass";

	/** Set Class of Bank Data Match Payment	  */
	public void setJP_BankDataMatchPaymentClass (String JP_BankDataMatchPaymentClass);

	/** Get Class of Bank Data Match Payment	  */
	public String getJP_BankDataMatchPaymentClass();

    /** Column name JP_BankDataSchema_ID */
    public static final String COLUMNNAME_JP_BankDataSchema_ID = "JP_BankDataSchema_ID";

	/** Set Import Bank Data Schema	  */
	public void setJP_BankDataSchema_ID (int JP_BankDataSchema_ID);

	/** Get Import Bank Data Schema	  */
	public int getJP_BankDataSchema_ID();

    /** Column name JP_BankDataSchema_UU */
    public static final String COLUMNNAME_JP_BankDataSchema_UU = "JP_BankDataSchema_UU";

	/** Set JP_BankDataSchema_UU	  */
	public void setJP_BankDataSchema_UU (String JP_BankDataSchema_UU);

	/** Get JP_BankDataSchema_UU	  */
	public String getJP_BankDataSchema_UU();

    /** Column name JP_BankStmt_DocAction */
    public static final String COLUMNNAME_JP_BankStmt_DocAction = "JP_BankStmt_DocAction";

	/** Set Bank Stmt Doc Action	  */
	public void setJP_BankStmt_DocAction (String JP_BankStmt_DocAction);

	/** Get Bank Stmt Doc Action	  */
	public String getJP_BankStmt_DocAction();

    /** Column name JP_PaymentDocType_ID */
    public static final String COLUMNNAME_JP_PaymentDocType_ID = "JP_PaymentDocType_ID";

	/** Set Payment Doc Type	  */
	public void setJP_PaymentDocType_ID (int JP_PaymentDocType_ID);

	/** Get Payment Doc Type	  */
	public int getJP_PaymentDocType_ID();

	public org.compiere.model.I_C_DocType getJP_PaymentDocType() throws RuntimeException;

    /** Column name JP_Payment_DocAction */
    public static final String COLUMNNAME_JP_Payment_DocAction = "JP_Payment_DocAction";

	/** Set Payment Doc Action	  */
	public void setJP_Payment_DocAction (String JP_Payment_DocAction);

	/** Get Payment Doc Action	  */
	public String getJP_Payment_DocAction();

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

    /** Column name Value */
    public static final String COLUMNNAME_Value = "Value";

	/** Set Search Key.
	  * Search key for the record in the format required - must be unique
	  */
	public void setValue (String Value);

	/** Get Search Key.
	  * Search key for the record in the format required - must be unique
	  */
	public String getValue();
}
