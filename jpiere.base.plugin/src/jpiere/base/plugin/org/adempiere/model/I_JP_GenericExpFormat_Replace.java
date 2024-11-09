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

/** Generated Interface for JP_GenericExpFormat_Replace
 *  @author iDempiere (generated) 
 *  @version Release 11
 */
@SuppressWarnings("all")
public interface I_JP_GenericExpFormat_Replace 
{

    /** TableName=JP_GenericExpFormat_Replace */
    public static final String Table_Name = "JP_GenericExpFormat_Replace";

    /** AD_Table_ID=1000321 */
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

    /** Column name JP_GenericExpFormat_Replace_ID */
    public static final String COLUMNNAME_JP_GenericExpFormat_Replace_ID = "JP_GenericExpFormat_Replace_ID";

	/** Set Replace.
	  * JPIERE-0628:JPBP
	  */
	public void setJP_GenericExpFormat_Replace_ID (int JP_GenericExpFormat_Replace_ID);

	/** Get Replace.
	  * JPIERE-0628:JPBP
	  */
	public int getJP_GenericExpFormat_Replace_ID();

    /** Column name JP_GenericExpFormat_Replace_UU */
    public static final String COLUMNNAME_JP_GenericExpFormat_Replace_UU = "JP_GenericExpFormat_Replace_UU";

	/** Set Replace(UU)	  */
	public void setJP_GenericExpFormat_Replace_UU (String JP_GenericExpFormat_Replace_UU);

	/** Get Replace(UU)	  */
	public String getJP_GenericExpFormat_Replace_UU();

    /** Column name JP_GenericExpFormat_Row_ID */
    public static final String COLUMNNAME_JP_GenericExpFormat_Row_ID = "JP_GenericExpFormat_Row_ID";

	/** Set Format Field.
	  * JPIERE-0628:JPBP
	  */
	public void setJP_GenericExpFormat_Row_ID (int JP_GenericExpFormat_Row_ID);

	/** Get Format Field.
	  * JPIERE-0628:JPBP
	  */
	public int getJP_GenericExpFormat_Row_ID();

	public I_JP_GenericExpFormat_Row getJP_GenericExpFormat_Row() throws RuntimeException;

    /** Column name JP_ReplaceMethod */
    public static final String COLUMNNAME_JP_ReplaceMethod = "JP_ReplaceMethod";

	/** Set Replace Method	  */
	public void setJP_ReplaceMethod (String JP_ReplaceMethod);

	/** Get Replace Method	  */
	public String getJP_ReplaceMethod();

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
