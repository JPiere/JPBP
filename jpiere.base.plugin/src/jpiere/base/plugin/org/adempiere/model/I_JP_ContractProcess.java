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

/** Generated Interface for JP_ContractProcess
 *  @author iDempiere (generated) 
 *  @version Release 6.2
 */
@SuppressWarnings("all")
public interface I_JP_ContractProcess 
{

    /** TableName=JP_ContractProcess */
    public static final String Table_Name = "JP_ContractProcess";

    /** AD_Table_ID=1000171 */
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

    /** Column name IsCreateBaseDocJP */
    public static final String COLUMNNAME_IsCreateBaseDocJP = "IsCreateBaseDocJP";

	/** Set Create Base Doc	  */
	public void setIsCreateBaseDocJP (boolean IsCreateBaseDocJP);

	/** Get Create Base Doc	  */
	public boolean isCreateBaseDocJP();

    /** Column name JP_ContractAutoRenewClass */
    public static final String COLUMNNAME_JP_ContractAutoRenewClass = "JP_ContractAutoRenewClass";

	/** Set Class(Auto Renew Contract).
	  * Java Classname
	  */
	public void setJP_ContractAutoRenewClass (String JP_ContractAutoRenewClass);

	/** Get Class(Auto Renew Contract).
	  * Java Classname
	  */
	public String getJP_ContractAutoRenewClass();

    /** Column name JP_ContractProcess_ID */
    public static final String COLUMNNAME_JP_ContractProcess_ID = "JP_ContractProcess_ID";

	/** Set Contract Process	  */
	public void setJP_ContractProcess_ID (int JP_ContractProcess_ID);

	/** Get Contract Process	  */
	public int getJP_ContractProcess_ID();

    /** Column name JP_ContractProcess_UU */
    public static final String COLUMNNAME_JP_ContractProcess_UU = "JP_ContractProcess_UU";

	/** Set Contract Process(UU)	  */
	public void setJP_ContractProcess_UU (String JP_ContractProcess_UU);

	/** Get Contract Process(UU)	  */
	public String getJP_ContractProcess_UU();

    /** Column name JP_ContractStatusUpdateClass */
    public static final String COLUMNNAME_JP_ContractStatusUpdateClass = "JP_ContractStatusUpdateClass";

	/** Set Class(Contract Status Update).
	  * Java Classname
	  */
	public void setJP_ContractStatusUpdateClass (String JP_ContractStatusUpdateClass);

	/** Get Class(Contract Status Update).
	  * Java Classname
	  */
	public String getJP_ContractStatusUpdateClass();

    /** Column name JP_CreateContractPSClass */
    public static final String COLUMNNAME_JP_CreateContractPSClass = "JP_CreateContractPSClass";

	/** Set Class(Create Contract Process Schedule).
	  * Java Classname
	  */
	public void setJP_CreateContractPSClass (String JP_CreateContractPSClass);

	/** Get Class(Create Contract Process Schedule).
	  * Java Classname
	  */
	public String getJP_CreateContractPSClass();

    /** Column name JP_IndirectContractProcClass */
    public static final String COLUMNNAME_JP_IndirectContractProcClass = "JP_IndirectContractProcClass";

	/** Set Class(Indirect Contract Process).
	  * Java Classname
	  */
	public void setJP_IndirectContractProcClass (String JP_IndirectContractProcClass);

	/** Get Class(Indirect Contract Process).
	  * Java Classname
	  */
	public String getJP_IndirectContractProcClass();

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
