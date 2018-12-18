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

/** Generated Interface for JP_ContractProcessList
 *  @author iDempiere (generated) 
 *  @version Release 6.1
 */
@SuppressWarnings("all")
public interface I_JP_ContractProcessList 
{

    /** TableName=JP_ContractProcessList */
    public static final String Table_Name = "JP_ContractProcessList";

    /** AD_Table_ID=1000175 */
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

    /** Column name JP_ContractProcessList_ID */
    public static final String COLUMNNAME_JP_ContractProcessList_ID = "JP_ContractProcessList_ID";

	/** Set Contract Process List	  */
	public void setJP_ContractProcessList_ID (int JP_ContractProcessList_ID);

	/** Get Contract Process List	  */
	public int getJP_ContractProcessList_ID();

    /** Column name JP_ContractProcessList_UU */
    public static final String COLUMNNAME_JP_ContractProcessList_UU = "JP_ContractProcessList_UU";

	/** Set Contract Process List(UU)	  */
	public void setJP_ContractProcessList_UU (String JP_ContractProcessList_UU);

	/** Get Contract Process List(UU)	  */
	public String getJP_ContractProcessList_UU();

    /** Column name JP_ContractProcessRef_ID */
    public static final String COLUMNNAME_JP_ContractProcessRef_ID = "JP_ContractProcessRef_ID";

	/** Set Contract Process Reference	  */
	public void setJP_ContractProcessRef_ID (int JP_ContractProcessRef_ID);

	/** Get Contract Process Reference	  */
	public int getJP_ContractProcessRef_ID();

	public I_JP_ContractProcessRef getJP_ContractProcessRef() throws RuntimeException;

    /** Column name JP_ContractProcess_ID */
    public static final String COLUMNNAME_JP_ContractProcess_ID = "JP_ContractProcess_ID";

	/** Set Contract Process	  */
	public void setJP_ContractProcess_ID (int JP_ContractProcess_ID);

	/** Get Contract Process	  */
	public int getJP_ContractProcess_ID();

	public I_JP_ContractProcess getJP_ContractProcess() throws RuntimeException;

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

    /** Column name ValidFrom */
    public static final String COLUMNNAME_ValidFrom = "ValidFrom";

	/** Set Valid from.
	  * Valid from including this date (first day)
	  */
	public void setValidFrom (Timestamp ValidFrom);

	/** Get Valid from.
	  * Valid from including this date (first day)
	  */
	public Timestamp getValidFrom();

    /** Column name ValidTo */
    public static final String COLUMNNAME_ValidTo = "ValidTo";

	/** Set Valid to.
	  * Valid to including this date (last day)
	  */
	public void setValidTo (Timestamp ValidTo);

	/** Get Valid to.
	  * Valid to including this date (last day)
	  */
	public Timestamp getValidTo();
}
