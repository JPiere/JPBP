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

/** Generated Interface for JP_User_History
 *  @author iDempiere (generated) 
 *  @version Release 9
 */
@SuppressWarnings("all")
public interface I_JP_User_History 
{

    /** TableName=JP_User_History */
    public static final String Table_Name = "JP_User_History";

    /** AD_Table_ID=1000300 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 7 - System - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(7);

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

    /** Column name AD_User_ID */
    public static final String COLUMNNAME_AD_User_ID = "AD_User_ID";

	/** Set User/Contact.
	  * User within the system - Internal or Business Partner Contact
	  */
	public void setAD_User_ID (int AD_User_ID);

	/** Get User/Contact.
	  * User within the system - Internal or Business Partner Contact
	  */
	public int getAD_User_ID();

	public org.compiere.model.I_AD_User getAD_User() throws RuntimeException;

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

    /** Column name DateFrom */
    public static final String COLUMNNAME_DateFrom = "DateFrom";

	/** Set Date From.
	  * Starting date for a range
	  */
	public void setDateFrom (Timestamp DateFrom);

	/** Get Date From.
	  * Starting date for a range
	  */
	public Timestamp getDateFrom();

    /** Column name DateTo */
    public static final String COLUMNNAME_DateTo = "DateTo";

	/** Set Date To.
	  * End date of a date range
	  */
	public void setDateTo (Timestamp DateTo);

	/** Get Date To.
	  * End date of a date range
	  */
	public Timestamp getDateTo();

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

    /** Column name JP_FirstName */
    public static final String COLUMNNAME_JP_FirstName = "JP_FirstName";

	/** Set First Name	  */
	public void setJP_FirstName (String JP_FirstName);

	/** Get First Name	  */
	public String getJP_FirstName();

    /** Column name JP_FirstName_Kana */
    public static final String COLUMNNAME_JP_FirstName_Kana = "JP_FirstName_Kana";

	/** Set First Name(Kana)	  */
	public void setJP_FirstName_Kana (String JP_FirstName_Kana);

	/** Get First Name(Kana)	  */
	public String getJP_FirstName_Kana();

    /** Column name JP_LastName */
    public static final String COLUMNNAME_JP_LastName = "JP_LastName";

	/** Set Last Name	  */
	public void setJP_LastName (String JP_LastName);

	/** Get Last Name	  */
	public String getJP_LastName();

    /** Column name JP_LastName_Kana */
    public static final String COLUMNNAME_JP_LastName_Kana = "JP_LastName_Kana";

	/** Set Last Name(Kana)	  */
	public void setJP_LastName_Kana (String JP_LastName_Kana);

	/** Get Last Name(Kana)	  */
	public String getJP_LastName_Kana();

    /** Column name JP_MiddleName */
    public static final String COLUMNNAME_JP_MiddleName = "JP_MiddleName";

	/** Set Middle Name	  */
	public void setJP_MiddleName (String JP_MiddleName);

	/** Get Middle Name	  */
	public String getJP_MiddleName();

    /** Column name JP_MiddleName_Kana */
    public static final String COLUMNNAME_JP_MiddleName_Kana = "JP_MiddleName_Kana";

	/** Set Middle Name(Kana)	  */
	public void setJP_MiddleName_Kana (String JP_MiddleName_Kana);

	/** Get Middle Name(Kana)	  */
	public String getJP_MiddleName_Kana();

    /** Column name JP_User_History_ID */
    public static final String COLUMNNAME_JP_User_History_ID = "JP_User_History_ID";

	/** Set JP_User_History.
	  * JPIERE-0564
	  */
	public void setJP_User_History_ID (int JP_User_History_ID);

	/** Get JP_User_History.
	  * JPIERE-0564
	  */
	public int getJP_User_History_ID();

    /** Column name JP_User_History_UU */
    public static final String COLUMNNAME_JP_User_History_UU = "JP_User_History_UU";

	/** Set JP_User_History_UU	  */
	public void setJP_User_History_UU (String JP_User_History_UU);

	/** Get JP_User_History_UU	  */
	public String getJP_User_History_UU();

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
}
