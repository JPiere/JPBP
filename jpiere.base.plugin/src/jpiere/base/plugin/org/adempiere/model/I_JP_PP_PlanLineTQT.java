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

/** Generated Interface for JP_PP_PlanLineTQT
 *  @author iDempiere (generated) 
 *  @version Release 8.2
 */
@SuppressWarnings("all")
public interface I_JP_PP_PlanLineTQT 
{

    /** TableName=JP_PP_PlanLineTQT */
    public static final String Table_Name = "JP_PP_PlanLineTQT";

    /** AD_Table_ID=1000277 */
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

    /** Column name ExpectedResult */
    public static final String COLUMNNAME_ExpectedResult = "ExpectedResult";

	/** Set Expected Result	  */
	public void setExpectedResult (String ExpectedResult);

	/** Get Expected Result	  */
	public String getExpectedResult();

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

    /** Column name JP_PP_PlanLineTQT_ID */
    public static final String COLUMNNAME_JP_PP_PlanLineTQT_ID = "JP_PP_PlanLineTQT_ID";

	/** Set PP Quality Test Template	  */
	public void setJP_PP_PlanLineTQT_ID (int JP_PP_PlanLineTQT_ID);

	/** Get PP Quality Test Template	  */
	public int getJP_PP_PlanLineTQT_ID();

    /** Column name JP_PP_PlanLineTQT_UU */
    public static final String COLUMNNAME_JP_PP_PlanLineTQT_UU = "JP_PP_PlanLineTQT_UU";

	/** Set PP Quality Test Template (UU)	  */
	public void setJP_PP_PlanLineTQT_UU (String JP_PP_PlanLineTQT_UU);

	/** Get PP Quality Test Template (UU)	  */
	public String getJP_PP_PlanLineTQT_UU();

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

    /** Column name M_QualityTest_ID */
    public static final String COLUMNNAME_M_QualityTest_ID = "M_QualityTest_ID";

	/** Set Quality Test	  */
	public void setM_QualityTest_ID (int M_QualityTest_ID);

	/** Get Quality Test	  */
	public int getM_QualityTest_ID();

	public org.compiere.model.I_M_QualityTest getM_QualityTest() throws RuntimeException;

    /** Column name SeqNo */
    public static final String COLUMNNAME_SeqNo = "SeqNo";

	/** Set Sequence.
	  * Method of ordering records;
 lowest number comes first
	  */
	public void setSeqNo (int SeqNo);

	/** Get Sequence.
	  * Method of ordering records;
 lowest number comes first
	  */
	public int getSeqNo();

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
