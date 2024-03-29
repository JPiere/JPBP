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

/** Generated Interface for JP_WF_AutoForward
 *  @author iDempiere (generated) 
 *  @version Release 8.2
 */
@SuppressWarnings("all")
public interface I_JP_WF_AutoForward 
{

    /** TableName=JP_WF_AutoForward */
    public static final String Table_Name = "JP_WF_AutoForward";

    /** AD_Table_ID=1000284 */
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

    /** Column name AD_WF_Node_ID */
    public static final String COLUMNNAME_AD_WF_Node_ID = "AD_WF_Node_ID";

	/** Set Node.
	  * Workflow Node (activity), step or process
	  */
	public void setAD_WF_Node_ID (int AD_WF_Node_ID);

	/** Get Node.
	  * Workflow Node (activity), step or process
	  */
	public int getAD_WF_Node_ID();

	public org.compiere.model.I_AD_WF_Node getAD_WF_Node() throws RuntimeException;

    /** Column name AD_Workflow_ID */
    public static final String COLUMNNAME_AD_Workflow_ID = "AD_Workflow_ID";

	/** Set Workflow.
	  * Workflow or combination of tasks
	  */
	public void setAD_Workflow_ID (int AD_Workflow_ID);

	/** Get Workflow.
	  * Workflow or combination of tasks
	  */
	public int getAD_Workflow_ID();

	public org.compiere.model.I_AD_Workflow getAD_Workflow() throws RuntimeException;

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

    /** Column name JP_WF_AutoForward_ID */
    public static final String COLUMNNAME_JP_WF_AutoForward_ID = "JP_WF_AutoForward_ID";

	/** Set JP_WF_AutoForward.
	  * JPIERE-0519:JPBP
	  */
	public void setJP_WF_AutoForward_ID (int JP_WF_AutoForward_ID);

	/** Get JP_WF_AutoForward.
	  * JPIERE-0519:JPBP
	  */
	public int getJP_WF_AutoForward_ID();

    /** Column name JP_WF_AutoForward_UU */
    public static final String COLUMNNAME_JP_WF_AutoForward_UU = "JP_WF_AutoForward_UU";

	/** Set JP_WF_AutoForward_UU	  */
	public void setJP_WF_AutoForward_UU (String JP_WF_AutoForward_UU);

	/** Get JP_WF_AutoForward_UU	  */
	public String getJP_WF_AutoForward_UU();

    /** Column name JP_WF_Org_ID */
    public static final String COLUMNNAME_JP_WF_Org_ID = "JP_WF_Org_ID";

	/** Set WF Organization	  */
	public void setJP_WF_Org_ID (int JP_WF_Org_ID);

	/** Get WF Organization	  */
	public int getJP_WF_Org_ID();

    /** Column name JP_WF_User_From_ID */
    public static final String COLUMNNAME_JP_WF_User_From_ID = "JP_WF_User_From_ID";

	/** Set WF Approver(From)	  */
	public void setJP_WF_User_From_ID (int JP_WF_User_From_ID);

	/** Get WF Approver(From)	  */
	public int getJP_WF_User_From_ID();

	public org.compiere.model.I_AD_User getJP_WF_User_From() throws RuntimeException;

    /** Column name JP_WF_User_To_ID */
    public static final String COLUMNNAME_JP_WF_User_To_ID = "JP_WF_User_To_ID";

	/** Set WF Approver(To)	  */
	public void setJP_WF_User_To_ID (int JP_WF_User_To_ID);

	/** Get WF Approver(To)	  */
	public int getJP_WF_User_To_ID();

	public org.compiere.model.I_AD_User getJP_WF_User_To() throws RuntimeException;

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
}
