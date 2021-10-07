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

/** Generated Interface for JP_BillSchema
 *  @author iDempiere (generated) 
 *  @version Release 8.2
 */
@SuppressWarnings("all")
public interface I_JP_BillSchema 
{

    /** TableName=JP_BillSchema */
    public static final String Table_Name = "JP_BillSchema";

    /** AD_Table_ID=1000034 */
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

    /** Column name C_DocType_ID */
    public static final String COLUMNNAME_C_DocType_ID = "C_DocType_ID";

	/** Set Document Type.
	  * Document type or rules
	  */
	public void setC_DocType_ID (int C_DocType_ID);

	/** Get Document Type.
	  * Document type or rules
	  */
	public int getC_DocType_ID();

	public org.compiere.model.I_C_DocType getC_DocType() throws RuntimeException;

    /** Column name Classname */
    public static final String COLUMNNAME_Classname = "Classname";

	/** Set Classname.
	  * Java Classname
	  */
	public void setClassname (String Classname);

	/** Get Classname.
	  * Java Classname
	  */
	public String getClassname();

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

    /** Column name IsBillOrgJP */
    public static final String COLUMNNAME_IsBillOrgJP = "IsBillOrgJP";

	/** Set IsBillOrgJP	  */
	public void setIsBillOrgJP (boolean IsBillOrgJP);

	/** Get IsBillOrgJP	  */
	public boolean isBillOrgJP();

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

    /** Column name IsTaxRecalculateJP */
    public static final String COLUMNNAME_IsTaxRecalculateJP = "IsTaxRecalculateJP";

	/** Set Tax Recalculation.
	  * JPIERE-0508:JPBP
	  */
	public void setIsTaxRecalculateJP (boolean IsTaxRecalculateJP);

	/** Get Tax Recalculation.
	  * JPIERE-0508:JPBP
	  */
	public boolean isTaxRecalculateJP();

    /** Column name JP_BillOrg_ID */
    public static final String COLUMNNAME_JP_BillOrg_ID = "JP_BillOrg_ID";

	/** Set JP_BillOrg_ID	  */
	public void setJP_BillOrg_ID (int JP_BillOrg_ID);

	/** Get JP_BillOrg_ID	  */
	public int getJP_BillOrg_ID();

    /** Column name JP_BillSchema_ID */
    public static final String COLUMNNAME_JP_BillSchema_ID = "JP_BillSchema_ID";

	/** Set Bill Schema	  */
	public void setJP_BillSchema_ID (int JP_BillSchema_ID);

	/** Get Bill Schema	  */
	public int getJP_BillSchema_ID();

    /** Column name JP_BillSchema_UU */
    public static final String COLUMNNAME_JP_BillSchema_UU = "JP_BillSchema_UU";

	/** Set JP_BillSchema_UU	  */
	public void setJP_BillSchema_UU (String JP_BillSchema_UU);

	/** Get JP_BillSchema_UU	  */
	public String getJP_BillSchema_UU();

    /** Column name JP_TaxAdjust_Charge_ID */
    public static final String COLUMNNAME_JP_TaxAdjust_Charge_ID = "JP_TaxAdjust_Charge_ID";

	/** Set Charge of Tax Adjust Invoice.
	  * JPIERE-0508:JPBP
	  */
	public void setJP_TaxAdjust_Charge_ID (int JP_TaxAdjust_Charge_ID);

	/** Get Charge of Tax Adjust Invoice.
	  * JPIERE-0508:JPBP
	  */
	public int getJP_TaxAdjust_Charge_ID();

	public org.compiere.model.I_C_Charge getJP_TaxAdjust_Charge() throws RuntimeException;

    /** Column name JP_TaxAdjust_Description */
    public static final String COLUMNNAME_JP_TaxAdjust_Description = "JP_TaxAdjust_Description";

	/** Set Description of Tax Adjust Invoice.
	  * JPIERE-0508:JPBP
	  */
	public void setJP_TaxAdjust_Description (String JP_TaxAdjust_Description);

	/** Get Description of Tax Adjust Invoice.
	  * JPIERE-0508:JPBP
	  */
	public String getJP_TaxAdjust_Description();

    /** Column name JP_TaxAdjust_DocType_ID */
    public static final String COLUMNNAME_JP_TaxAdjust_DocType_ID = "JP_TaxAdjust_DocType_ID";

	/** Set Doc Type of Tax Adjust Invoice.
	  * JPIERE-0508:JPBP
	  */
	public void setJP_TaxAdjust_DocType_ID (int JP_TaxAdjust_DocType_ID);

	/** Get Doc Type of Tax Adjust Invoice.
	  * JPIERE-0508:JPBP
	  */
	public int getJP_TaxAdjust_DocType_ID();

	public org.compiere.model.I_C_DocType getJP_TaxAdjust_DocType() throws RuntimeException;

    /** Column name JP_TaxAdjust_PriceList_ID */
    public static final String COLUMNNAME_JP_TaxAdjust_PriceList_ID = "JP_TaxAdjust_PriceList_ID";

	/** Set Price List of Tax Adjust Invoice.
	  * JPIERE-0508:JPBP
	  */
	public void setJP_TaxAdjust_PriceList_ID (int JP_TaxAdjust_PriceList_ID);

	/** Get Price List of Tax Adjust Invoice.
	  * JPIERE-0508:JPBP
	  */
	public int getJP_TaxAdjust_PriceList_ID();

	public org.compiere.model.I_M_PriceList getJP_TaxAdjust_PriceList() throws RuntimeException;

    /** Column name JP_TaxAdjust_Tax_ID */
    public static final String COLUMNNAME_JP_TaxAdjust_Tax_ID = "JP_TaxAdjust_Tax_ID";

	/** Set Tax of Tax Adjust Invoice.
	  * JPIERE-0508:JPBP
	  */
	public void setJP_TaxAdjust_Tax_ID (int JP_TaxAdjust_Tax_ID);

	/** Get Tax of Tax Adjust Invoice.
	  * JPIERE-0508:JPBP
	  */
	public int getJP_TaxAdjust_Tax_ID();

	public org.compiere.model.I_C_Tax getJP_TaxAdjust_Tax() throws RuntimeException;

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
