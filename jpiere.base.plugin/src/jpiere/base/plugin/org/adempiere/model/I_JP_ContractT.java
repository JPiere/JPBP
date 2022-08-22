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

/** Generated Interface for JP_ContractT
 *  @author iDempiere (generated) 
 *  @version Release 9
 */
@SuppressWarnings("all")
public interface I_JP_ContractT 
{

    /** TableName=JP_ContractT */
    public static final String Table_Name = "JP_ContractT";

    /** AD_Table_ID=1000178 */
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

    /** Column name IsAutomaticUpdateJP */
    public static final String COLUMNNAME_IsAutomaticUpdateJP = "IsAutomaticUpdateJP";

	/** Set Automatic Update	  */
	public void setIsAutomaticUpdateJP (boolean IsAutomaticUpdateJP);

	/** Get Automatic Update	  */
	public boolean isAutomaticUpdateJP();

    /** Column name JP_Classname1 */
    public static final String COLUMNNAME_JP_Classname1 = "JP_Classname1";

	/** Set Classname.
	  * Java Classname
	  */
	public void setJP_Classname1 (String JP_Classname1);

	/** Get Classname.
	  * Java Classname
	  */
	public String getJP_Classname1();

    /** Column name JP_CommunicationColumn */
    public static final String COLUMNNAME_JP_CommunicationColumn = "JP_CommunicationColumn";

	/** Set Communication Column	  */
	public void setJP_CommunicationColumn (String JP_CommunicationColumn);

	/** Get Communication Column	  */
	public String getJP_CommunicationColumn();

    /** Column name JP_ContractCancelTerm_ID */
    public static final String COLUMNNAME_JP_ContractCancelTerm_ID = "JP_ContractCancelTerm_ID";

	/** Set Contract Cancel Term	  */
	public void setJP_ContractCancelTerm_ID (int JP_ContractCancelTerm_ID);

	/** Get Contract Cancel Term	  */
	public int getJP_ContractCancelTerm_ID();

	public I_JP_ContractCancelTerm getJP_ContractCancelTerm() throws RuntimeException;

    /** Column name JP_ContractCategory_ID */
    public static final String COLUMNNAME_JP_ContractCategory_ID = "JP_ContractCategory_ID";

	/** Set Contract Category	  */
	public void setJP_ContractCategory_ID (int JP_ContractCategory_ID);

	/** Get Contract Category	  */
	public int getJP_ContractCategory_ID();

	public I_JP_ContractCategory getJP_ContractCategory() throws RuntimeException;

    /** Column name JP_ContractExtendPeriod_ID */
    public static final String COLUMNNAME_JP_ContractExtendPeriod_ID = "JP_ContractExtendPeriod_ID";

	/** Set Contract Extend Period	  */
	public void setJP_ContractExtendPeriod_ID (int JP_ContractExtendPeriod_ID);

	/** Get Contract Extend Period	  */
	public int getJP_ContractExtendPeriod_ID();

	public I_JP_ContractExtendPeriod getJP_ContractExtendPeriod() throws RuntimeException;

    /** Column name JP_ContractT_ID */
    public static final String COLUMNNAME_JP_ContractT_ID = "JP_ContractT_ID";

	/** Set Contract Doc Template	  */
	public void setJP_ContractT_ID (int JP_ContractT_ID);

	/** Get Contract Doc Template	  */
	public int getJP_ContractT_ID();

    /** Column name JP_ContractT_Parent_ID */
    public static final String COLUMNNAME_JP_ContractT_Parent_ID = "JP_ContractT_Parent_ID";

	/** Set Parent Contract Template 	  */
	public void setJP_ContractT_Parent_ID (int JP_ContractT_Parent_ID);

	/** Get Parent Contract Template 	  */
	public int getJP_ContractT_Parent_ID();

	public I_JP_ContractT getJP_ContractT_Parent() throws RuntimeException;

    /** Column name JP_ContractT_UU */
    public static final String COLUMNNAME_JP_ContractT_UU = "JP_ContractT_UU";

	/** Set Contract Doc Template(UU)	  */
	public void setJP_ContractT_UU (String JP_ContractT_UU);

	/** Get Contract Doc Template(UU)	  */
	public String getJP_ContractT_UU();

    /** Column name JP_ContractType */
    public static final String COLUMNNAME_JP_ContractType = "JP_ContractType";

	/** Set Contract Type	  */
	public void setJP_ContractType (String JP_ContractType);

	/** Get Contract Type	  */
	public String getJP_ContractType();

    /** Column name JP_Remarks */
    public static final String COLUMNNAME_JP_Remarks = "JP_Remarks";

	/** Set Remarks.
	  * JPIERE-0490:JPBP
	  */
	public void setJP_Remarks (String JP_Remarks);

	/** Get Remarks.
	  * JPIERE-0490:JPBP
	  */
	public String getJP_Remarks();

    /** Column name JP_Subject */
    public static final String COLUMNNAME_JP_Subject = "JP_Subject";

	/** Set Subject.
	  * JPIERE-0490:JPBP
	  */
	public void setJP_Subject (String JP_Subject);

	/** Get Subject.
	  * JPIERE-0490:JPBP
	  */
	public String getJP_Subject();

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
