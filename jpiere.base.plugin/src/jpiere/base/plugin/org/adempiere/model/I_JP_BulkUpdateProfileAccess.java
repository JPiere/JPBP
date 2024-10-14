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

/** Generated Interface for JP_BulkUpdateProfileAccess
 *  @author iDempiere (generated) 
 *  @version Release 11
 */
@SuppressWarnings("all")
public interface I_JP_BulkUpdateProfileAccess 
{

    /** TableName=JP_BulkUpdateProfileAccess */
    public static final String Table_Name = "JP_BulkUpdateProfileAccess";

    /** AD_Table_ID=1000317 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 7 - System - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(7);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Tenant.
	  * Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within tenant
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within tenant
	  */
	public int getAD_Org_ID();

    /** Column name AD_Role_ID */
    public static final String COLUMNNAME_AD_Role_ID = "AD_Role_ID";

	/** Set Role.
	  * Responsibility Role
	  */
	public void setAD_Role_ID (int AD_Role_ID);

	/** Get Role.
	  * Responsibility Role
	  */
	public int getAD_Role_ID();

	public org.compiere.model.I_AD_Role getAD_Role() throws RuntimeException;

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

    /** Column name JP_BulkUpdateProfileAccess_ID */
    public static final String COLUMNNAME_JP_BulkUpdateProfileAccess_ID = "JP_BulkUpdateProfileAccess_ID";

	/** Set Bulk Update Profile Access	  */
	public void setJP_BulkUpdateProfileAccess_ID (int JP_BulkUpdateProfileAccess_ID);

	/** Get Bulk Update Profile Access	  */
	public int getJP_BulkUpdateProfileAccess_ID();

    /** Column name JP_BulkUpdateProfileAccess_UU */
    public static final String COLUMNNAME_JP_BulkUpdateProfileAccess_UU = "JP_BulkUpdateProfileAccess_UU";

	/** Set Bulk Update Profile Access(UU)	  */
	public void setJP_BulkUpdateProfileAccess_UU (String JP_BulkUpdateProfileAccess_UU);

	/** Get Bulk Update Profile Access(UU)	  */
	public String getJP_BulkUpdateProfileAccess_UU();

    /** Column name JP_BulkUpdateProfile_ID */
    public static final String COLUMNNAME_JP_BulkUpdateProfile_ID = "JP_BulkUpdateProfile_ID";

	/** Set Bulk Update Profile.
	  * JPIERE-0621:JPBP
	  */
	public void setJP_BulkUpdateProfile_ID (int JP_BulkUpdateProfile_ID);

	/** Get Bulk Update Profile.
	  * JPIERE-0621:JPBP
	  */
	public int getJP_BulkUpdateProfile_ID();

	public I_JP_BulkUpdateProfile getJP_BulkUpdateProfile() throws RuntimeException;

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
