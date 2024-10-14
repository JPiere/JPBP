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

/** Generated Interface for JP_BulkUpdateProfileLine
 *  @author iDempiere (generated) 
 *  @version Release 11
 */
@SuppressWarnings("all")
public interface I_JP_BulkUpdateProfileLine 
{

    /** TableName=JP_BulkUpdateProfileLine */
    public static final String Table_Name = "JP_BulkUpdateProfileLine";

    /** AD_Table_ID=1000316 */
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

    /** Column name AD_Column_ID */
    public static final String COLUMNNAME_AD_Column_ID = "AD_Column_ID";

	/** Set Column.
	  * Column in the table
	  */
	public void setAD_Column_ID (int AD_Column_ID);

	/** Get Column.
	  * Column in the table
	  */
	public int getAD_Column_ID();

	public org.compiere.model.I_AD_Column getAD_Column() throws RuntimeException;

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

    /** Column name AD_Table_ID */
    public static final String COLUMNNAME_AD_Table_ID = "AD_Table_ID";

	/** Set Table.
	  * Database Table information
	  */
	public void setAD_Table_ID (int AD_Table_ID);

	/** Get Table.
	  * Database Table information
	  */
	public int getAD_Table_ID();

	public org.compiere.model.I_AD_Table getAD_Table() throws RuntimeException;

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

    /** Column name Help */
    public static final String COLUMNNAME_Help = "Help";

	/** Set Comment/Help.
	  * Comment or Hint
	  */
	public void setHelp (String Help);

	/** Get Comment/Help.
	  * Comment or Hint
	  */
	public String getHelp();

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

    /** Column name IsChangeLog */
    public static final String COLUMNNAME_IsChangeLog = "IsChangeLog";

	/** Set Maintain Change Log.
	  * Maintain a log of changes
	  */
	public void setIsChangeLog (boolean IsChangeLog);

	/** Get Maintain Change Log.
	  * Maintain a log of changes
	  */
	public boolean isChangeLog();

    /** Column name JP_BulkUpdateCommitType */
    public static final String COLUMNNAME_JP_BulkUpdateCommitType = "JP_BulkUpdateCommitType";

	/** Set Commit type	  */
	public void setJP_BulkUpdateCommitType (String JP_BulkUpdateCommitType);

	/** Get Commit type	  */
	public String getJP_BulkUpdateCommitType();

    /** Column name JP_BulkUpdateProfileLine_ID */
    public static final String COLUMNNAME_JP_BulkUpdateProfileLine_ID = "JP_BulkUpdateProfileLine_ID";

	/** Set Bulk Update Profile Line	  */
	public void setJP_BulkUpdateProfileLine_ID (int JP_BulkUpdateProfileLine_ID);

	/** Get Bulk Update Profile Line	  */
	public int getJP_BulkUpdateProfileLine_ID();

    /** Column name JP_BulkUpdateProfileLine_UU */
    public static final String COLUMNNAME_JP_BulkUpdateProfileLine_UU = "JP_BulkUpdateProfileLine_UU";

	/** Set Bulk Update Profile Line(UU)	  */
	public void setJP_BulkUpdateProfileLine_UU (String JP_BulkUpdateProfileLine_UU);

	/** Get Bulk Update Profile Line(UU)	  */
	public String getJP_BulkUpdateProfileLine_UU();

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

    /** Column name JP_BulkUpdateType */
    public static final String COLUMNNAME_JP_BulkUpdateType = "JP_BulkUpdateType";

	/** Set Bulk update type	  */
	public void setJP_BulkUpdateType (String JP_BulkUpdateType);

	/** Get Bulk update type	  */
	public String getJP_BulkUpdateType();

    /** Column name JP_MaskingString */
    public static final String COLUMNNAME_JP_MaskingString = "JP_MaskingString";

	/** Set Masking String	  */
	public void setJP_MaskingString (String JP_MaskingString);

	/** Get Masking String	  */
	public String getJP_MaskingString();

    /** Column name JP_MaskingType */
    public static final String COLUMNNAME_JP_MaskingType = "JP_MaskingType";

	/** Set Masking Type	  */
	public void setJP_MaskingType (String JP_MaskingType);

	/** Get Masking Type	  */
	public String getJP_MaskingType();

    /** Column name JP_NumOfCharExcludeMasking */
    public static final String COLUMNNAME_JP_NumOfCharExcludeMasking = "JP_NumOfCharExcludeMasking";

	/** Set Number of characters to exclude from masking	  */
	public void setJP_NumOfCharExcludeMasking (int JP_NumOfCharExcludeMasking);

	/** Get Number of characters to exclude from masking	  */
	public int getJP_NumOfCharExcludeMasking();

    /** Column name JP_ReplacementString */
    public static final String COLUMNNAME_JP_ReplacementString = "JP_ReplacementString";

	/** Set Replacement String 	  */
	public void setJP_ReplacementString (String JP_ReplacementString);

	/** Get Replacement String 	  */
	public String getJP_ReplacementString();

    /** Column name JP_TargetString */
    public static final String COLUMNNAME_JP_TargetString = "JP_TargetString";

	/** Set Target string	  */
	public void setJP_TargetString (String JP_TargetString);

	/** Get Target string	  */
	public String getJP_TargetString();

    /** Column name JP_UpdateSetClause */
    public static final String COLUMNNAME_JP_UpdateSetClause = "JP_UpdateSetClause";

	/** Set Sql UPDATE SET 	  */
	public void setJP_UpdateSetClause (String JP_UpdateSetClause);

	/** Get Sql UPDATE SET 	  */
	public String getJP_UpdateSetClause();

    /** Column name Line */
    public static final String COLUMNNAME_Line = "Line";

	/** Set Line No.
	  * Unique line for this document
	  */
	public void setLine (int Line);

	/** Get Line No.
	  * Unique line for this document
	  */
	public int getLine();

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

    /** Column name WhereClause */
    public static final String COLUMNNAME_WhereClause = "WhereClause";

	/** Set Sql WHERE.
	  * Fully qualified SQL WHERE clause
	  */
	public void setWhereClause (String WhereClause);

	/** Get Sql WHERE.
	  * Fully qualified SQL WHERE clause
	  */
	public String getWhereClause();
}
