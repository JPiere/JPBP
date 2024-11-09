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
/** Generated Model - DO NOT CHANGE */
package jpiere.base.plugin.org.adempiere.model;

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for JP_GenericExpFormat_Row
 *  @author iDempiere (generated)
 *  @version Release 11 - $Id$ */
@org.adempiere.base.Model(table="JP_GenericExpFormat_Row")
public class X_JP_GenericExpFormat_Row extends PO implements I_JP_GenericExpFormat_Row, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20241108L;

    /** Standard Constructor */
    public X_JP_GenericExpFormat_Row (Properties ctx, int JP_GenericExpFormat_Row_ID, String trxName)
    {
      super (ctx, JP_GenericExpFormat_Row_ID, trxName);
      /** if (JP_GenericExpFormat_Row_ID == 0)
        {
			setFieldLength (0);
// 0
			setIsEncloseWithEnclosingCharaJP (false);
// N
			setIsEscapEnclosingCharJP (false);
// N
			setIsEscapSeparatorCharJP (false);
// N
			setJP_ExportType (null);
// V
			setJP_GenericExpFormat_ID (0);
// @JP_GenericExpFormat_ID@
			setJP_GenericExpFormat_Row_ID (0);
			setJP_Header (null);
			setName (null);
			setSeqNo (0);
// @SQL=SELECT NVL(MAX(SeqNo),0)+10 AS DefaultValue FROM JP_GenericExpFormat_Row WHERE JP_GenericExpFormat_ID=@JP_GenericExpFormat_ID@
        } */
    }

    /** Standard Constructor */
    public X_JP_GenericExpFormat_Row (Properties ctx, int JP_GenericExpFormat_Row_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_GenericExpFormat_Row_ID, trxName, virtualColumns);
      /** if (JP_GenericExpFormat_Row_ID == 0)
        {
			setFieldLength (0);
// 0
			setIsEncloseWithEnclosingCharaJP (false);
// N
			setIsEscapEnclosingCharJP (false);
// N
			setIsEscapSeparatorCharJP (false);
// N
			setJP_ExportType (null);
// V
			setJP_GenericExpFormat_ID (0);
// @JP_GenericExpFormat_ID@
			setJP_GenericExpFormat_Row_ID (0);
			setJP_Header (null);
			setName (null);
			setSeqNo (0);
// @SQL=SELECT NVL(MAX(SeqNo),0)+10 AS DefaultValue FROM JP_GenericExpFormat_Row WHERE JP_GenericExpFormat_ID=@JP_GenericExpFormat_ID@
        } */
    }

    /** Standard Constructor */
    public X_JP_GenericExpFormat_Row (Properties ctx, String JP_GenericExpFormat_Row_UU, String trxName)
    {
      super (ctx, JP_GenericExpFormat_Row_UU, trxName);
      /** if (JP_GenericExpFormat_Row_UU == null)
        {
			setFieldLength (0);
// 0
			setIsEncloseWithEnclosingCharaJP (false);
// N
			setIsEscapEnclosingCharJP (false);
// N
			setIsEscapSeparatorCharJP (false);
// N
			setJP_ExportType (null);
// V
			setJP_GenericExpFormat_ID (0);
// @JP_GenericExpFormat_ID@
			setJP_GenericExpFormat_Row_ID (0);
			setJP_Header (null);
			setName (null);
			setSeqNo (0);
// @SQL=SELECT NVL(MAX(SeqNo),0)+10 AS DefaultValue FROM JP_GenericExpFormat_Row WHERE JP_GenericExpFormat_ID=@JP_GenericExpFormat_ID@
        } */
    }

    /** Standard Constructor */
    public X_JP_GenericExpFormat_Row (Properties ctx, String JP_GenericExpFormat_Row_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_GenericExpFormat_Row_UU, trxName, virtualColumns);
      /** if (JP_GenericExpFormat_Row_UU == null)
        {
			setFieldLength (0);
// 0
			setIsEncloseWithEnclosingCharaJP (false);
// N
			setIsEscapEnclosingCharJP (false);
// N
			setIsEscapSeparatorCharJP (false);
// N
			setJP_ExportType (null);
// V
			setJP_GenericExpFormat_ID (0);
// @JP_GenericExpFormat_ID@
			setJP_GenericExpFormat_Row_ID (0);
			setJP_Header (null);
			setName (null);
			setSeqNo (0);
// @SQL=SELECT NVL(MAX(SeqNo),0)+10 AS DefaultValue FROM JP_GenericExpFormat_Row WHERE JP_GenericExpFormat_ID=@JP_GenericExpFormat_ID@
        } */
    }

    /** Load Constructor */
    public X_JP_GenericExpFormat_Row (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 7 - System - Client - Org
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuilder sb = new StringBuilder ("X_JP_GenericExpFormat_Row[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_Column getAD_Column() throws RuntimeException
	{
		return (org.compiere.model.I_AD_Column)MTable.get(getCtx(), org.compiere.model.I_AD_Column.Table_ID)
			.getPO(getAD_Column_ID(), get_TrxName());
	}

	/** Set Column.
		@param AD_Column_ID Column in the table
	*/
	public void setAD_Column_ID (int AD_Column_ID)
	{
		if (AD_Column_ID < 1)
			set_Value (COLUMNNAME_AD_Column_ID, null);
		else
			set_Value (COLUMNNAME_AD_Column_ID, Integer.valueOf(AD_Column_ID));
	}

	/** Get Column.
		@return Column in the table
	  */
	public int getAD_Column_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Column_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Constant Value.
		@param ConstantValue Constant value
	*/
	public void setConstantValue (String ConstantValue)
	{
		set_Value (COLUMNNAME_ConstantValue, ConstantValue);
	}

	/** Get Constant Value.
		@return Constant value
	  */
	public String getConstantValue()
	{
		return (String)get_Value(COLUMNNAME_ConstantValue);
	}

	/** Set Data Format.
		@param DataFormat Format String in Java Notation, e.g. ddMMyy
	*/
	public void setDataFormat (String DataFormat)
	{
		set_Value (COLUMNNAME_DataFormat, DataFormat);
	}

	/** Get Data Format.
		@return Format String in Java Notation, e.g. ddMMyy
	  */
	public String getDataFormat()
	{
		return (String)get_Value(COLUMNNAME_DataFormat);
	}

	/** Set Description.
		@param Description Optional short description of the record
	*/
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription()
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Document Note.
		@param DocumentNote Additional information for a Document
	*/
	public void setDocumentNote (String DocumentNote)
	{
		set_Value (COLUMNNAME_DocumentNote, DocumentNote);
	}

	/** Get Document Note.
		@return Additional information for a Document
	  */
	public String getDocumentNote()
	{
		return (String)get_Value(COLUMNNAME_DocumentNote);
	}

	/** Set Length.
		@param FieldLength Length of the column in the database
	*/
	public void setFieldLength (int FieldLength)
	{
		set_Value (COLUMNNAME_FieldLength, Integer.valueOf(FieldLength));
	}

	/** Get Length.
		@return Length of the column in the database
	  */
	public int getFieldLength()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_FieldLength);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Comment/Help.
		@param Help Comment or Hint
	*/
	public void setHelp (String Help)
	{
		set_Value (COLUMNNAME_Help, Help);
	}

	/** Get Comment/Help.
		@return Comment or Hint
	  */
	public String getHelp()
	{
		return (String)get_Value(COLUMNNAME_Help);
	}

	/** Set Enclose with enclosing characters.
		@param IsEncloseWithEnclosingCharaJP Enclose with enclosing characters
	*/
	public void setIsEncloseWithEnclosingCharaJP (boolean IsEncloseWithEnclosingCharaJP)
	{
		set_Value (COLUMNNAME_IsEncloseWithEnclosingCharaJP, Boolean.valueOf(IsEncloseWithEnclosingCharaJP));
	}

	/** Get Enclose with enclosing characters.
		@return Enclose with enclosing characters	  */
	public boolean isEncloseWithEnclosingCharaJP()
	{
		Object oo = get_Value(COLUMNNAME_IsEncloseWithEnclosingCharaJP);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Escaping Enclosing Characters.
		@param IsEscapEnclosingCharJP Escaping Enclosing Characters
	*/
	public void setIsEscapEnclosingCharJP (boolean IsEscapEnclosingCharJP)
	{
		set_Value (COLUMNNAME_IsEscapEnclosingCharJP, Boolean.valueOf(IsEscapEnclosingCharJP));
	}

	/** Get Escaping Enclosing Characters.
		@return Escaping Enclosing Characters	  */
	public boolean isEscapEnclosingCharJP()
	{
		Object oo = get_Value(COLUMNNAME_IsEscapEnclosingCharJP);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Escaping Separator Character.
		@param IsEscapSeparatorCharJP Escaping Separator Character
	*/
	public void setIsEscapSeparatorCharJP (boolean IsEscapSeparatorCharJP)
	{
		set_Value (COLUMNNAME_IsEscapSeparatorCharJP, Boolean.valueOf(IsEscapSeparatorCharJP));
	}

	/** Get Escaping Separator Character.
		@return Escaping Separator Character	  */
	public boolean isEscapSeparatorCharJP()
	{
		Object oo = get_Value(COLUMNNAME_IsEscapSeparatorCharJP);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Blank = B */
	public static final String JP_EXPORTTYPE_Blank = "B";
	/** Constant = C */
	public static final String JP_EXPORTTYPE_Constant = "C";
	/** Variable = V */
	public static final String JP_EXPORTTYPE_Variable = "V";
	/** Set Export Type.
		@param JP_ExportType Export Type
	*/
	public void setJP_ExportType (String JP_ExportType)
	{

		set_Value (COLUMNNAME_JP_ExportType, JP_ExportType);
	}

	/** Get Export Type.
		@return Export Type	  */
	public String getJP_ExportType()
	{
		return (String)get_Value(COLUMNNAME_JP_ExportType);
	}

	public I_JP_GenericExpFormat getJP_GenericExpFormat() throws RuntimeException
	{
		return (I_JP_GenericExpFormat)MTable.get(getCtx(), I_JP_GenericExpFormat.Table_ID)
			.getPO(getJP_GenericExpFormat_ID(), get_TrxName());
	}

	/** Set Generic Export Format.
		@param JP_GenericExpFormat_ID JPIERE-0628:JPBP
	*/
	public void setJP_GenericExpFormat_ID (int JP_GenericExpFormat_ID)
	{
		if (JP_GenericExpFormat_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_GenericExpFormat_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_GenericExpFormat_ID, Integer.valueOf(JP_GenericExpFormat_ID));
	}

	/** Get Generic Export Format.
		@return JPIERE-0628:JPBP
	  */
	public int getJP_GenericExpFormat_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_GenericExpFormat_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Format Field.
		@param JP_GenericExpFormat_Row_ID JPIERE-0628:JPBP
	*/
	public void setJP_GenericExpFormat_Row_ID (int JP_GenericExpFormat_Row_ID)
	{
		if (JP_GenericExpFormat_Row_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_GenericExpFormat_Row_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_GenericExpFormat_Row_ID, Integer.valueOf(JP_GenericExpFormat_Row_ID));
	}

	/** Get Format Field.
		@return JPIERE-0628:JPBP
	  */
	public int getJP_GenericExpFormat_Row_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_GenericExpFormat_Row_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Format Field(UU).
		@param JP_GenericExpFormat_Row_UU Format Field(UU)
	*/
	public void setJP_GenericExpFormat_Row_UU (String JP_GenericExpFormat_Row_UU)
	{
		set_Value (COLUMNNAME_JP_GenericExpFormat_Row_UU, JP_GenericExpFormat_Row_UU);
	}

	/** Get Format Field(UU).
		@return Format Field(UU)	  */
	public String getJP_GenericExpFormat_Row_UU()
	{
		return (String)get_Value(COLUMNNAME_JP_GenericExpFormat_Row_UU);
	}

	/** Set Header.
		@param JP_Header Header
	*/
	public void setJP_Header (String JP_Header)
	{
		set_Value (COLUMNNAME_JP_Header, JP_Header);
	}

	/** Get Header.
		@return Header	  */
	public String getJP_Header()
	{
		return (String)get_Value(COLUMNNAME_JP_Header);
	}

	/** Asterisk(*) = A */
	public static final String JP_PADDINGCHAR_Asterisk = "A";
	/** Space = S */
	public static final String JP_PADDINGCHAR_Space = "S";
	/** Zero(0) = Z */
	public static final String JP_PADDINGCHAR_Zero0 = "Z";
	/** Set Padding character.
		@param JP_PaddingChar Padding character
	*/
	public void setJP_PaddingChar (String JP_PaddingChar)
	{

		set_Value (COLUMNNAME_JP_PaddingChar, JP_PaddingChar);
	}

	/** Get Padding character.
		@return Padding character	  */
	public String getJP_PaddingChar()
	{
		return (String)get_Value(COLUMNNAME_JP_PaddingChar);
	}

	/** Left justified = L */
	public static final String JP_PADDINGTYPE_LeftJustified = "L";
	/** Right justified = R */
	public static final String JP_PADDINGTYPE_RightJustified = "R";
	/** Set Padding Type.
		@param JP_PaddingType Padding Type
	*/
	public void setJP_PaddingType (String JP_PaddingType)
	{

		set_Value (COLUMNNAME_JP_PaddingType, JP_PaddingType);
	}

	/** Get Padding Type.
		@return Padding Type	  */
	public String getJP_PaddingType()
	{
		return (String)get_Value(COLUMNNAME_JP_PaddingType);
	}

	/** Set Name.
		@param Name Alphanumeric identifier of the entity
	*/
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName()
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

	/** Set Sequence.
		@param SeqNo Method of ordering records; lowest number comes first
	*/
	public void setSeqNo (int SeqNo)
	{
		set_Value (COLUMNNAME_SeqNo, Integer.valueOf(SeqNo));
	}

	/** Get Sequence.
		@return Method of ordering records; lowest number comes first
	  */
	public int getSeqNo()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_SeqNo);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}