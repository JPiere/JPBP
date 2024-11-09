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

/** Generated Interface for JP_GenericExpFormat_Row
 *  @author iDempiere (generated) 
 *  @version Release 11
 */
@SuppressWarnings("all")
public interface I_JP_GenericExpFormat_Row 
{

    /** TableName=JP_GenericExpFormat_Row */
    public static final String Table_Name = "JP_GenericExpFormat_Row";

    /** AD_Table_ID=1000320 */
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

    /** Column name ConstantValue */
    public static final String COLUMNNAME_ConstantValue = "ConstantValue";

	/** Set Constant Value.
	  * Constant value
	  */
	public void setConstantValue (String ConstantValue);

	/** Get Constant Value.
	  * Constant value
	  */
	public String getConstantValue();

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

    /** Column name DataFormat */
    public static final String COLUMNNAME_DataFormat = "DataFormat";

	/** Set Data Format.
	  * Format String in Java Notation, e.g. ddMMyy
	  */
	public void setDataFormat (String DataFormat);

	/** Get Data Format.
	  * Format String in Java Notation, e.g. ddMMyy
	  */
	public String getDataFormat();

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

    /** Column name FieldLength */
    public static final String COLUMNNAME_FieldLength = "FieldLength";

	/** Set Length.
	  * Length of the column in the database
	  */
	public void setFieldLength (int FieldLength);

	/** Get Length.
	  * Length of the column in the database
	  */
	public int getFieldLength();

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

    /** Column name IsEncloseWithEnclosingCharaJP */
    public static final String COLUMNNAME_IsEncloseWithEnclosingCharaJP = "IsEncloseWithEnclosingCharaJP";

	/** Set Enclose with enclosing characters	  */
	public void setIsEncloseWithEnclosingCharaJP (boolean IsEncloseWithEnclosingCharaJP);

	/** Get Enclose with enclosing characters	  */
	public boolean isEncloseWithEnclosingCharaJP();

    /** Column name IsEscapEnclosingCharJP */
    public static final String COLUMNNAME_IsEscapEnclosingCharJP = "IsEscapEnclosingCharJP";

	/** Set Escaping Enclosing Characters	  */
	public void setIsEscapEnclosingCharJP (boolean IsEscapEnclosingCharJP);

	/** Get Escaping Enclosing Characters	  */
	public boolean isEscapEnclosingCharJP();

    /** Column name IsEscapSeparatorCharJP */
    public static final String COLUMNNAME_IsEscapSeparatorCharJP = "IsEscapSeparatorCharJP";

	/** Set Escaping Separator Character	  */
	public void setIsEscapSeparatorCharJP (boolean IsEscapSeparatorCharJP);

	/** Get Escaping Separator Character	  */
	public boolean isEscapSeparatorCharJP();

    /** Column name JP_ExportType */
    public static final String COLUMNNAME_JP_ExportType = "JP_ExportType";

	/** Set Export Type	  */
	public void setJP_ExportType (String JP_ExportType);

	/** Get Export Type	  */
	public String getJP_ExportType();

    /** Column name JP_GenericExpFormat_ID */
    public static final String COLUMNNAME_JP_GenericExpFormat_ID = "JP_GenericExpFormat_ID";

	/** Set Generic Export Format.
	  * JPIERE-0628:JPBP
	  */
	public void setJP_GenericExpFormat_ID (int JP_GenericExpFormat_ID);

	/** Get Generic Export Format.
	  * JPIERE-0628:JPBP
	  */
	public int getJP_GenericExpFormat_ID();

	public I_JP_GenericExpFormat getJP_GenericExpFormat() throws RuntimeException;

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

    /** Column name JP_GenericExpFormat_Row_UU */
    public static final String COLUMNNAME_JP_GenericExpFormat_Row_UU = "JP_GenericExpFormat_Row_UU";

	/** Set Format Field(UU)	  */
	public void setJP_GenericExpFormat_Row_UU (String JP_GenericExpFormat_Row_UU);

	/** Get Format Field(UU)	  */
	public String getJP_GenericExpFormat_Row_UU();

    /** Column name JP_Header */
    public static final String COLUMNNAME_JP_Header = "JP_Header";

	/** Set Header	  */
	public void setJP_Header (String JP_Header);

	/** Get Header	  */
	public String getJP_Header();

    /** Column name JP_PaddingChar */
    public static final String COLUMNNAME_JP_PaddingChar = "JP_PaddingChar";

	/** Set Padding character	  */
	public void setJP_PaddingChar (String JP_PaddingChar);

	/** Get Padding character	  */
	public String getJP_PaddingChar();

    /** Column name JP_PaddingType */
    public static final String COLUMNNAME_JP_PaddingType = "JP_PaddingType";

	/** Set Padding Type	  */
	public void setJP_PaddingType (String JP_PaddingType);

	/** Get Padding Type	  */
	public String getJP_PaddingType();

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
