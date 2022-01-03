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

/** Generated Interface for JP_Contract_Acct
 *  @author iDempiere (generated) 
 *  @version Release 8.2
 */
@SuppressWarnings("all")
public interface I_JP_Contract_Acct 
{

    /** TableName=JP_Contract_Acct */
    public static final String Table_Name = "JP_Contract_Acct";

    /** AD_Table_ID=1000181 */
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

    /** Column name DocAction */
    public static final String COLUMNNAME_DocAction = "DocAction";

	/** Set Document Action.
	  * The targeted status of the document
	  */
	public void setDocAction (String DocAction);

	/** Get Document Action.
	  * The targeted status of the document
	  */
	public String getDocAction();

    /** Column name DocBaseType */
    public static final String COLUMNNAME_DocBaseType = "DocBaseType";

	/** Set Document BaseType.
	  * Logical type of document
	  */
	public void setDocBaseType (String DocBaseType);

	/** Get Document BaseType.
	  * Logical type of document
	  */
	public String getDocBaseType();

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

    /** Column name IsPostingContractAcctJP */
    public static final String COLUMNNAME_IsPostingContractAcctJP = "IsPostingContractAcctJP";

	/** Set Use Contract Acct Info	  */
	public void setIsPostingContractAcctJP (boolean IsPostingContractAcctJP);

	/** Get Use Contract Acct Info	  */
	public boolean isPostingContractAcctJP();

    /** Column name IsPostingRecognitionDocJP */
    public static final String COLUMNNAME_IsPostingRecognitionDocJP = "IsPostingRecognitionDocJP";

	/** Set Use Recognition Doc	  */
	public void setIsPostingRecognitionDocJP (boolean IsPostingRecognitionDocJP);

	/** Get Use Recognition Doc	  */
	public boolean isPostingRecognitionDocJP();

    /** Column name IsSOTrx */
    public static final String COLUMNNAME_IsSOTrx = "IsSOTrx";

	/** Set Sales Transaction.
	  * This is a Sales Transaction
	  */
	public void setIsSOTrx (boolean IsSOTrx);

	/** Get Sales Transaction.
	  * This is a Sales Transaction
	  */
	public boolean isSOTrx();

    /** Column name IsSplitWhenDifferenceJP */
    public static final String COLUMNNAME_IsSplitWhenDifferenceJP = "IsSplitWhenDifferenceJP";

	/** Set Split when Difference.
	  * Split document when there is a difference
	  */
	public void setIsSplitWhenDifferenceJP (boolean IsSplitWhenDifferenceJP);

	/** Get Split when Difference.
	  * Split document when there is a difference
	  */
	public boolean isSplitWhenDifferenceJP();

    /** Column name JP_Contract_Acct_ID */
    public static final String COLUMNNAME_JP_Contract_Acct_ID = "JP_Contract_Acct_ID";

	/** Set Contract Acct Info	  */
	public void setJP_Contract_Acct_ID (int JP_Contract_Acct_ID);

	/** Get Contract Acct Info	  */
	public int getJP_Contract_Acct_ID();

    /** Column name JP_Contract_Acct_UU */
    public static final String COLUMNNAME_JP_Contract_Acct_UU = "JP_Contract_Acct_UU";

	/** Set Contract Acct Info(UU)	  */
	public void setJP_Contract_Acct_UU (String JP_Contract_Acct_UU);

	/** Get Contract Acct Info(UU)	  */
	public String getJP_Contract_Acct_UU();

    /** Column name JP_RecogToInvoicePolicy */
    public static final String COLUMNNAME_JP_RecogToInvoicePolicy = "JP_RecogToInvoicePolicy";

	/** Set Policy of Create Invoice From Recognition	  */
	public void setJP_RecogToInvoicePolicy (String JP_RecogToInvoicePolicy);

	/** Get Policy of Create Invoice From Recognition	  */
	public String getJP_RecogToInvoicePolicy();

    /** Column name JP_Recognition_JournalPolicy */
    public static final String COLUMNNAME_JP_Recognition_JournalPolicy = "JP_Recognition_JournalPolicy";

	/** Set Journal Policy of Recognition Doc if no accounting config.
	  * JPIERE-0536:JPBP
	  */
	public void setJP_Recognition_JournalPolicy (String JP_Recognition_JournalPolicy);

	/** Get Journal Policy of Recognition Doc if no accounting config.
	  * JPIERE-0536:JPBP
	  */
	public String getJP_Recognition_JournalPolicy();

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

    /** Column name Processing */
    public static final String COLUMNNAME_Processing = "Processing";

	/** Set Process Now	  */
	public void setProcessing (boolean Processing);

	/** Get Process Now	  */
	public boolean isProcessing();

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
