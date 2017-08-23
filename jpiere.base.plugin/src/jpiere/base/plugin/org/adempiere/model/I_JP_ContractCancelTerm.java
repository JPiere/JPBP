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

/** Generated Interface for JP_ContractCancelTerm
 *  @author iDempiere (generated) 
 *  @version Release 4.1
 */
@SuppressWarnings("all")
public interface I_JP_ContractCancelTerm 
{

    /** TableName=JP_ContractCancelTerm */
    public static final String Table_Name = "JP_ContractCancelTerm";

    /** AD_Table_ID=1000167 */
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

    /** Column name IsDueFixed */
    public static final String COLUMNNAME_IsDueFixed = "IsDueFixed";

	/** Set Fixed due date.
	  * Payment is due on a fixed date
	  */
	public void setIsDueFixed (boolean IsDueFixed);

	/** Get Fixed due date.
	  * Payment is due on a fixed date
	  */
	public boolean isDueFixed();

    /** Column name JP_ContractCancelTerm_ID */
    public static final String COLUMNNAME_JP_ContractCancelTerm_ID = "JP_ContractCancelTerm_ID";

	/** Set Contract Cancel Term	  */
	public void setJP_ContractCancelTerm_ID (int JP_ContractCancelTerm_ID);

	/** Get Contract Cancel Term	  */
	public int getJP_ContractCancelTerm_ID();

    /** Column name JP_ContractCancelTerm_UU */
    public static final String COLUMNNAME_JP_ContractCancelTerm_UU = "JP_ContractCancelTerm_UU";

	/** Set Contract Cancel Term(UU)	  */
	public void setJP_ContractCancelTerm_UU (String JP_ContractCancelTerm_UU);

	/** Get Contract Cancel Term(UU)	  */
	public String getJP_ContractCancelTerm_UU();

    /** Column name JP_Day */
    public static final String COLUMNNAME_JP_Day = "JP_Day";

	/** Set Day	  */
	public void setJP_Day (int JP_Day);

	/** Get Day	  */
	public int getJP_Day();

    /** Column name JP_Month */
    public static final String COLUMNNAME_JP_Month = "JP_Month";

	/** Set Month	  */
	public void setJP_Month (int JP_Month);

	/** Get Month	  */
	public int getJP_Month();

    /** Column name JP_Year */
    public static final String COLUMNNAME_JP_Year = "JP_Year";

	/** Set Year	  */
	public void setJP_Year (int JP_Year);

	/** Get Year	  */
	public int getJP_Year();

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
