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

/** Generated Interface for JP_ContractProcPeriod
 *  @author iDempiere (generated) 
 *  @version Release 4.1
 */
@SuppressWarnings("all")
public interface I_JP_ContractProcPeriod 
{

    /** TableName=JP_ContractProcPeriod */
    public static final String Table_Name = "JP_ContractProcPeriod";

    /** AD_Table_ID=1000162 */
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

    /** Column name DateDoc */
    public static final String COLUMNNAME_DateDoc = "DateDoc";

	/** Set Document Date.
	  * Date of the Document
	  */
	public void setDateDoc (Timestamp DateDoc);

	/** Get Document Date.
	  * Date of the Document
	  */
	public Timestamp getDateDoc();

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

    /** Column name EndDate */
    public static final String COLUMNNAME_EndDate = "EndDate";

	/** Set End Date.
	  * Last effective date (inclusive)
	  */
	public void setEndDate (Timestamp EndDate);

	/** Get End Date.
	  * Last effective date (inclusive)
	  */
	public Timestamp getEndDate();

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

    /** Column name JP_ContractCalender_ID */
    public static final String COLUMNNAME_JP_ContractCalender_ID = "JP_ContractCalender_ID";

	/** Set Contract Calender	  */
	public void setJP_ContractCalender_ID (int JP_ContractCalender_ID);

	/** Get Contract Calender	  */
	public int getJP_ContractCalender_ID();

	public I_JP_ContractCalender getJP_ContractCalender() throws RuntimeException;

    /** Column name JP_ContractProcPeriodG_ID */
    public static final String COLUMNNAME_JP_ContractProcPeriodG_ID = "JP_ContractProcPeriodG_ID";

	/** Set Contract Process Period Group	  */
	public void setJP_ContractProcPeriodG_ID (int JP_ContractProcPeriodG_ID);

	/** Get Contract Process Period Group	  */
	public int getJP_ContractProcPeriodG_ID();

	public I_JP_ContractProcPeriodG getJP_ContractProcPeriodG() throws RuntimeException;

    /** Column name JP_ContractProcPeriod_ID */
    public static final String COLUMNNAME_JP_ContractProcPeriod_ID = "JP_ContractProcPeriod_ID";

	/** Set Contract Process Period	  */
	public void setJP_ContractProcPeriod_ID (int JP_ContractProcPeriod_ID);

	/** Get Contract Process Period	  */
	public int getJP_ContractProcPeriod_ID();

    /** Column name JP_ContractProcPeriod_UU */
    public static final String COLUMNNAME_JP_ContractProcPeriod_UU = "JP_ContractProcPeriod_UU";

	/** Set Contract Process Period(UU)	  */
	public void setJP_ContractProcPeriod_UU (String JP_ContractProcPeriod_UU);

	/** Get Contract Process Period(UU)	  */
	public String getJP_ContractProcPeriod_UU();

    /** Column name JP_ContractProcessValue */
    public static final String COLUMNNAME_JP_ContractProcessValue = "JP_ContractProcessValue";

	/** Set Contract Process Value	  */
	public void setJP_ContractProcessValue (String JP_ContractProcessValue);

	/** Get Contract Process Value	  */
	public String getJP_ContractProcessValue();

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

    /** Column name StartDate */
    public static final String COLUMNNAME_StartDate = "StartDate";

	/** Set Start Date.
	  * First effective day (inclusive)
	  */
	public void setStartDate (Timestamp StartDate);

	/** Get Start Date.
	  * First effective day (inclusive)
	  */
	public Timestamp getStartDate();

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
