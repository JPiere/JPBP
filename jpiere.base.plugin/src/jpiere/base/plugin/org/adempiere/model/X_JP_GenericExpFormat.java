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
import org.compiere.util.KeyNamePair;

/** Generated Model for JP_GenericExpFormat
 *  @author iDempiere (generated)
 *  @version Release 11 - $Id$ */
@org.adempiere.base.Model(table="JP_GenericExpFormat")
public class X_JP_GenericExpFormat extends PO implements I_JP_GenericExpFormat, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20241108L;

    /** Standard Constructor */
    public X_JP_GenericExpFormat (Properties ctx, int JP_GenericExpFormat_ID, String trxName)
    {
      super (ctx, JP_GenericExpFormat_ID, trxName);
      /** if (JP_GenericExpFormat_ID == 0)
        {
			setAD_Table_ID (0);
			setCharacterSet (null);
			setFormatType (null);
			setIsAttachmentFileJP (false);
// N
			setIsHeaderRequiredJP (false);
// N
			setIsZipJP (false);
// N
			setJP_GenericExpFormat_ID (0);
			setJP_LineBreakChar (null);
// CL
			setName (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_GenericExpFormat (Properties ctx, int JP_GenericExpFormat_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_GenericExpFormat_ID, trxName, virtualColumns);
      /** if (JP_GenericExpFormat_ID == 0)
        {
			setAD_Table_ID (0);
			setCharacterSet (null);
			setFormatType (null);
			setIsAttachmentFileJP (false);
// N
			setIsHeaderRequiredJP (false);
// N
			setIsZipJP (false);
// N
			setJP_GenericExpFormat_ID (0);
			setJP_LineBreakChar (null);
// CL
			setName (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_GenericExpFormat (Properties ctx, String JP_GenericExpFormat_UU, String trxName)
    {
      super (ctx, JP_GenericExpFormat_UU, trxName);
      /** if (JP_GenericExpFormat_UU == null)
        {
			setAD_Table_ID (0);
			setCharacterSet (null);
			setFormatType (null);
			setIsAttachmentFileJP (false);
// N
			setIsHeaderRequiredJP (false);
// N
			setIsZipJP (false);
// N
			setJP_GenericExpFormat_ID (0);
			setJP_LineBreakChar (null);
// CL
			setName (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_GenericExpFormat (Properties ctx, String JP_GenericExpFormat_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_GenericExpFormat_UU, trxName, virtualColumns);
      /** if (JP_GenericExpFormat_UU == null)
        {
			setAD_Table_ID (0);
			setCharacterSet (null);
			setFormatType (null);
			setIsAttachmentFileJP (false);
// N
			setIsHeaderRequiredJP (false);
// N
			setIsZipJP (false);
// N
			setJP_GenericExpFormat_ID (0);
			setJP_LineBreakChar (null);
// CL
			setName (null);
        } */
    }

    /** Load Constructor */
    public X_JP_GenericExpFormat (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_JP_GenericExpFormat[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_Process getAD_Process() throws RuntimeException
	{
		return (org.compiere.model.I_AD_Process)MTable.get(getCtx(), org.compiere.model.I_AD_Process.Table_ID)
			.getPO(getAD_Process_ID(), get_TrxName());
	}

	/** Set Process.
		@param AD_Process_ID Process or Report
	*/
	public void setAD_Process_ID (int AD_Process_ID)
	{
		if (AD_Process_ID < 1)
			set_Value (COLUMNNAME_AD_Process_ID, null);
		else
			set_Value (COLUMNNAME_AD_Process_ID, Integer.valueOf(AD_Process_ID));
	}

	/** Get Process.
		@return Process or Report
	  */
	public int getAD_Process_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Process_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_StorageProvider getAD_StorageProvider() throws RuntimeException
	{
		return (org.compiere.model.I_AD_StorageProvider)MTable.get(getCtx(), org.compiere.model.I_AD_StorageProvider.Table_ID)
			.getPO(getAD_StorageProvider_ID(), get_TrxName());
	}

	/** Set Storage Provider.
		@param AD_StorageProvider_ID Storage Provider
	*/
	public void setAD_StorageProvider_ID (int AD_StorageProvider_ID)
	{
		if (AD_StorageProvider_ID < 1)
			set_Value (COLUMNNAME_AD_StorageProvider_ID, null);
		else
			set_Value (COLUMNNAME_AD_StorageProvider_ID, Integer.valueOf(AD_StorageProvider_ID));
	}

	/** Get Storage Provider.
		@return Storage Provider	  */
	public int getAD_StorageProvider_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_StorageProvider_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_Table getAD_Table() throws RuntimeException
	{
		return (org.compiere.model.I_AD_Table)MTable.get(getCtx(), org.compiere.model.I_AD_Table.Table_ID)
			.getPO(getAD_Table_ID(), get_TrxName());
	}

	/** Set Table.
		@param AD_Table_ID Database Table information
	*/
	public void setAD_Table_ID (int AD_Table_ID)
	{
		if (AD_Table_ID < 1)
			set_Value (COLUMNNAME_AD_Table_ID, null);
		else
			set_Value (COLUMNNAME_AD_Table_ID, Integer.valueOf(AD_Table_ID));
	}

	/** Get Table.
		@return Database Table information
	  */
	public int getAD_Table_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Table_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Character Set.
		@param CharacterSet Character Set
	*/
	public void setCharacterSet (String CharacterSet)
	{
		set_Value (COLUMNNAME_CharacterSet, CharacterSet);
	}

	/** Get Character Set.
		@return Character Set	  */
	public String getCharacterSet()
	{
		return (String)get_Value(COLUMNNAME_CharacterSet);
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

	/** Set File Name Pattern.
		@param FileNamePattern File Name Pattern
	*/
	public void setFileNamePattern (String FileNamePattern)
	{
		set_Value (COLUMNNAME_FileNamePattern, FileNamePattern);
	}

	/** Get File Name Pattern.
		@return File Name Pattern	  */
	public String getFileNamePattern()
	{
		return (String)get_Value(COLUMNNAME_FileNamePattern);
	}

	/** FormatType AD_Reference_ID=209 */
	public static final int FORMATTYPE_AD_Reference_ID=209;
	/** Comma Separated = C */
	public static final String FORMATTYPE_CommaSeparated = "C";
	/** Fixed Position = F */
	public static final String FORMATTYPE_FixedPosition = "F";
	/** Tab Separated = T */
	public static final String FORMATTYPE_TabSeparated = "T";
	/** Custom Separator Char = U */
	public static final String FORMATTYPE_CustomSeparatorChar = "U";
	/** XML = X */
	public static final String FORMATTYPE_XML = "X";
	/** Set Format.
		@param FormatType Format of the data
	*/
	public void setFormatType (String FormatType)
	{

		set_Value (COLUMNNAME_FormatType, FormatType);
	}

	/** Get Format.
		@return Format of the data
	  */
	public String getFormatType()
	{
		return (String)get_Value(COLUMNNAME_FormatType);
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

	/** Set Attachment File.
		@param IsAttachmentFileJP Attachment File
	*/
	public void setIsAttachmentFileJP (boolean IsAttachmentFileJP)
	{
		set_Value (COLUMNNAME_IsAttachmentFileJP, Boolean.valueOf(IsAttachmentFileJP));
	}

	/** Get Attachment File.
		@return Attachment File	  */
	public boolean isAttachmentFileJP()
	{
		Object oo = get_Value(COLUMNNAME_IsAttachmentFileJP);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Header Require.
		@param IsHeaderRequiredJP Header Require
	*/
	public void setIsHeaderRequiredJP (boolean IsHeaderRequiredJP)
	{
		set_Value (COLUMNNAME_IsHeaderRequiredJP, Boolean.valueOf(IsHeaderRequiredJP));
	}

	/** Get Header Require.
		@return Header Require	  */
	public boolean isHeaderRequiredJP()
	{
		Object oo = get_Value(COLUMNNAME_IsHeaderRequiredJP);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Zip.
		@param IsZipJP Zip
	*/
	public void setIsZipJP (boolean IsZipJP)
	{
		set_Value (COLUMNNAME_IsZipJP, Boolean.valueOf(IsZipJP));
	}

	/** Get Zip.
		@return Zip	  */
	public boolean isZipJP()
	{
		Object oo = get_Value(COLUMNNAME_IsZipJP);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Double quotes = D */
	public static final String JP_ENCLOSINGCHAR_DoubleQuotes = "D";
	/** Nothing = N */
	public static final String JP_ENCLOSINGCHAR_Nothing = "N";
	/** Set Enclosing character.
		@param JP_EnclosingChar Enclosing character
	*/
	public void setJP_EnclosingChar (String JP_EnclosingChar)
	{

		set_Value (COLUMNNAME_JP_EnclosingChar, JP_EnclosingChar);
	}

	/** Get Enclosing character.
		@return Enclosing character	  */
	public String getJP_EnclosingChar()
	{
		return (String)get_Value(COLUMNNAME_JP_EnclosingChar);
	}

	/** Set File Extension.
		@param JP_FileExtension File Extension
	*/
	public void setJP_FileExtension (String JP_FileExtension)
	{
		set_Value (COLUMNNAME_JP_FileExtension, JP_FileExtension);
	}

	/** Get File Extension.
		@return File Extension	  */
	public String getJP_FileExtension()
	{
		return (String)get_Value(COLUMNNAME_JP_FileExtension);
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

	/** Set Generic Expprt Format(UU).
		@param JP_GenericExpFormat_UU Generic Expprt Format(UU)
	*/
	public void setJP_GenericExpFormat_UU (String JP_GenericExpFormat_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_GenericExpFormat_UU, JP_GenericExpFormat_UU);
	}

	/** Get Generic Expprt Format(UU).
		@return Generic Expprt Format(UU)	  */
	public String getJP_GenericExpFormat_UU()
	{
		return (String)get_Value(COLUMNNAME_JP_GenericExpFormat_UU);
	}

	/** CRLF = CL */
	public static final String JP_LINEBREAKCHAR_CRLF = "CL";
	/** CR = CR */
	public static final String JP_LINEBREAKCHAR_CR = "CR";
	/** LF = LF */
	public static final String JP_LINEBREAKCHAR_LF = "LF";
	/** Set Line break character.
		@param JP_LineBreakChar Line break character
	*/
	public void setJP_LineBreakChar (String JP_LineBreakChar)
	{

		set_Value (COLUMNNAME_JP_LineBreakChar, JP_LineBreakChar);
	}

	/** Get Line break character.
		@return Line break character	  */
	public String getJP_LineBreakChar()
	{
		return (String)get_Value(COLUMNNAME_JP_LineBreakChar);
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

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair()
    {
        return new KeyNamePair(get_ID(), getName());
    }

	/** Set Sql ORDER BY.
		@param OrderByClause Fully qualified ORDER BY clause
	*/
	public void setOrderByClause (String OrderByClause)
	{
		set_Value (COLUMNNAME_OrderByClause, OrderByClause);
	}

	/** Get Sql ORDER BY.
		@return Fully qualified ORDER BY clause
	  */
	public String getOrderByClause()
	{
		return (String)get_Value(COLUMNNAME_OrderByClause);
	}

	/** Set Separator Character.
		@param SeparatorChar Separator Character
	*/
	public void setSeparatorChar (String SeparatorChar)
	{
		set_Value (COLUMNNAME_SeparatorChar, SeparatorChar);
	}

	/** Get Separator Character.
		@return Separator Character	  */
	public String getSeparatorChar()
	{
		return (String)get_Value(COLUMNNAME_SeparatorChar);
	}

	/** Set Search Key.
		@param Value Search key for the record in the format required - must be unique
	*/
	public void setValue (String Value)
	{
		set_Value (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue()
	{
		return (String)get_Value(COLUMNNAME_Value);
	}

	/** Set Sql WHERE.
		@param WhereClause Fully qualified SQL WHERE clause
	*/
	public void setWhereClause (String WhereClause)
	{
		set_Value (COLUMNNAME_WhereClause, WhereClause);
	}

	/** Get Sql WHERE.
		@return Fully qualified SQL WHERE clause
	  */
	public String getWhereClause()
	{
		return (String)get_Value(COLUMNNAME_WhereClause);
	}
}