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

/** Generated Interface for JP_PP_PlanLine
 *  @author iDempiere (generated) 
 *  @version Release 8.2
 */
@SuppressWarnings("all")
public interface I_JP_PP_PlanLine 
{

    /** TableName=JP_PP_PlanLine */
    public static final String Table_Name = "JP_PP_PlanLine";

    /** AD_Table_ID=1000270 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 1 - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(1);

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

    /** Column name DocumentNote */
    public static final String COLUMNNAME_DocumentNote = "DocumentNote";

	/** Set Document Note.
	  * Additional information for a Document
	  */
	public void setDocumentNote (String DocumentNote);

	/** Get Document Note.
	  * Additional information for a Document
	  */
	public String getDocumentNote();

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

    /** Column name IsEndProduct */
    public static final String COLUMNNAME_IsEndProduct = "IsEndProduct";

	/** Set End Product.
	  * End Product of production
	  */
	public void setIsEndProduct (boolean IsEndProduct);

	/** Get End Product.
	  * End Product of production
	  */
	public boolean isEndProduct();

    /** Column name JP_PP_PlanLineT_ID */
    public static final String COLUMNNAME_JP_PP_PlanLineT_ID = "JP_PP_PlanLineT_ID";

	/** Set PP Plan Line Template.
	  * JPIERE-0501:JPBP
	  */
	public void setJP_PP_PlanLineT_ID (int JP_PP_PlanLineT_ID);

	/** Get PP Plan Line Template.
	  * JPIERE-0501:JPBP
	  */
	public int getJP_PP_PlanLineT_ID();

	public I_JP_PP_PlanLineT getJP_PP_PlanLineT() throws RuntimeException;

    /** Column name JP_PP_PlanLine_ID */
    public static final String COLUMNNAME_JP_PP_PlanLine_ID = "JP_PP_PlanLine_ID";

	/** Set PP Plan Line	  */
	public void setJP_PP_PlanLine_ID (int JP_PP_PlanLine_ID);

	/** Get PP Plan Line	  */
	public int getJP_PP_PlanLine_ID();

    /** Column name JP_PP_PlanLine_UU */
    public static final String COLUMNNAME_JP_PP_PlanLine_UU = "JP_PP_PlanLine_UU";

	/** Set PP Plan Line(UU)	  */
	public void setJP_PP_PlanLine_UU (String JP_PP_PlanLine_UU);

	/** Get PP Plan Line(UU)	  */
	public String getJP_PP_PlanLine_UU();

    /** Column name JP_PP_Plan_ID */
    public static final String COLUMNNAME_JP_PP_Plan_ID = "JP_PP_Plan_ID";

	/** Set PP Plan.
	  * JPIERE-0501:JPBP
	  */
	public void setJP_PP_Plan_ID (int JP_PP_Plan_ID);

	/** Get PP Plan.
	  * JPIERE-0501:JPBP
	  */
	public int getJP_PP_Plan_ID();

	public I_JP_PP_Plan getJP_PP_Plan() throws RuntimeException;

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

    /** Column name M_Locator_ID */
    public static final String COLUMNNAME_M_Locator_ID = "M_Locator_ID";

	/** Set Locator.
	  * Warehouse Locator
	  */
	public void setM_Locator_ID (int M_Locator_ID);

	/** Get Locator.
	  * Warehouse Locator
	  */
	public int getM_Locator_ID();

	public org.compiere.model.I_M_Locator getM_Locator() throws RuntimeException;

    /** Column name M_Product_ID */
    public static final String COLUMNNAME_M_Product_ID = "M_Product_ID";

	/** Set Product.
	  * Product, Service, Item
	  */
	public void setM_Product_ID (int M_Product_ID);

	/** Get Product.
	  * Product, Service, Item
	  */
	public int getM_Product_ID();

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException;

    /** Column name MovementQty */
    public static final String COLUMNNAME_MovementQty = "MovementQty";

	/** Set Movement Quantity.
	  * Quantity of a product moved.
	  */
	public void setMovementQty (BigDecimal MovementQty);

	/** Get Movement Quantity.
	  * Quantity of a product moved.
	  */
	public BigDecimal getMovementQty();

    /** Column name PlannedQty */
    public static final String COLUMNNAME_PlannedQty = "PlannedQty";

	/** Set Planned Quantity.
	  * Planned quantity for this project
	  */
	public void setPlannedQty (BigDecimal PlannedQty);

	/** Get Planned Quantity.
	  * Planned quantity for this project
	  */
	public BigDecimal getPlannedQty();

    /** Column name QtyUsed */
    public static final String COLUMNNAME_QtyUsed = "QtyUsed";

	/** Set Quantity Used	  */
	public void setQtyUsed (BigDecimal QtyUsed);

	/** Get Quantity Used	  */
	public BigDecimal getQtyUsed();

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
