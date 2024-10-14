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

/** Generated Model for JP_BulkUpdateProfileLine
 *  @author iDempiere (generated)
 *  @version Release 11 - $Id$ */
@org.adempiere.base.Model(table="JP_BulkUpdateProfileLine")
public class X_JP_BulkUpdateProfileLine extends PO implements I_JP_BulkUpdateProfileLine, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20241012L;

    /** Standard Constructor */
    public X_JP_BulkUpdateProfileLine (Properties ctx, int JP_BulkUpdateProfileLine_ID, String trxName)
    {
      super (ctx, JP_BulkUpdateProfileLine_ID, trxName);
      /** if (JP_BulkUpdateProfileLine_ID == 0)
        {
			setAD_Table_ID (0);
			setIsChangeLog (false);
// N
			setJP_BulkUpdateCommitType (null);
// N
			setJP_BulkUpdateProfileLine_ID (0);
			setJP_BulkUpdateProfile_ID (0);
			setJP_BulkUpdateType (null);
			setJP_NumOfCharExcludeMasking (0);
// 0
			setLine (0);
// @SQL=SELECT COALESCE(MAX(Line),0)+10 AS DefaultValue FROM JP_BulkUpdateProfileLine WHERE JP_BulkUpdateProfile_ID=@JP_BulkUpdateProfile_ID@
        } */
    }

    /** Standard Constructor */
    public X_JP_BulkUpdateProfileLine (Properties ctx, int JP_BulkUpdateProfileLine_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_BulkUpdateProfileLine_ID, trxName, virtualColumns);
      /** if (JP_BulkUpdateProfileLine_ID == 0)
        {
			setAD_Table_ID (0);
			setIsChangeLog (false);
// N
			setJP_BulkUpdateCommitType (null);
// N
			setJP_BulkUpdateProfileLine_ID (0);
			setJP_BulkUpdateProfile_ID (0);
			setJP_BulkUpdateType (null);
			setJP_NumOfCharExcludeMasking (0);
// 0
			setLine (0);
// @SQL=SELECT COALESCE(MAX(Line),0)+10 AS DefaultValue FROM JP_BulkUpdateProfileLine WHERE JP_BulkUpdateProfile_ID=@JP_BulkUpdateProfile_ID@
        } */
    }

    /** Standard Constructor */
    public X_JP_BulkUpdateProfileLine (Properties ctx, String JP_BulkUpdateProfileLine_UU, String trxName)
    {
      super (ctx, JP_BulkUpdateProfileLine_UU, trxName);
      /** if (JP_BulkUpdateProfileLine_UU == null)
        {
			setAD_Table_ID (0);
			setIsChangeLog (false);
// N
			setJP_BulkUpdateCommitType (null);
// N
			setJP_BulkUpdateProfileLine_ID (0);
			setJP_BulkUpdateProfile_ID (0);
			setJP_BulkUpdateType (null);
			setJP_NumOfCharExcludeMasking (0);
// 0
			setLine (0);
// @SQL=SELECT COALESCE(MAX(Line),0)+10 AS DefaultValue FROM JP_BulkUpdateProfileLine WHERE JP_BulkUpdateProfile_ID=@JP_BulkUpdateProfile_ID@
        } */
    }

    /** Standard Constructor */
    public X_JP_BulkUpdateProfileLine (Properties ctx, String JP_BulkUpdateProfileLine_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_BulkUpdateProfileLine_UU, trxName, virtualColumns);
      /** if (JP_BulkUpdateProfileLine_UU == null)
        {
			setAD_Table_ID (0);
			setIsChangeLog (false);
// N
			setJP_BulkUpdateCommitType (null);
// N
			setJP_BulkUpdateProfileLine_ID (0);
			setJP_BulkUpdateProfile_ID (0);
			setJP_BulkUpdateType (null);
			setJP_NumOfCharExcludeMasking (0);
// 0
			setLine (0);
// @SQL=SELECT COALESCE(MAX(Line),0)+10 AS DefaultValue FROM JP_BulkUpdateProfileLine WHERE JP_BulkUpdateProfile_ID=@JP_BulkUpdateProfile_ID@
        } */
    }

    /** Load Constructor */
    public X_JP_BulkUpdateProfileLine (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_JP_BulkUpdateProfileLine[")
        .append(get_ID()).append("]");
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

	/** Set Maintain Change Log.
		@param IsChangeLog Maintain a log of changes
	*/
	public void setIsChangeLog (boolean IsChangeLog)
	{
		set_Value (COLUMNNAME_IsChangeLog, Boolean.valueOf(IsChangeLog));
	}

	/** Get Maintain Change Log.
		@return Maintain a log of changes
	  */
	public boolean isChangeLog()
	{
		Object oo = get_Value(COLUMNNAME_IsChangeLog);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Line = L */
	public static final String JP_BULKUPDATECOMMITTYPE_Line = "L";
	/** Nothing = N */
	public static final String JP_BULKUPDATECOMMITTYPE_Nothing = "N";
	/** Record = R */
	public static final String JP_BULKUPDATECOMMITTYPE_Record = "R";
	/** Set Commit type.
		@param JP_BulkUpdateCommitType Commit type
	*/
	public void setJP_BulkUpdateCommitType (String JP_BulkUpdateCommitType)
	{

		set_Value (COLUMNNAME_JP_BulkUpdateCommitType, JP_BulkUpdateCommitType);
	}

	/** Get Commit type.
		@return Commit type	  */
	public String getJP_BulkUpdateCommitType()
	{
		return (String)get_Value(COLUMNNAME_JP_BulkUpdateCommitType);
	}

	/** Set Bulk Update Profile Line.
		@param JP_BulkUpdateProfileLine_ID Bulk Update Profile Line
	*/
	public void setJP_BulkUpdateProfileLine_ID (int JP_BulkUpdateProfileLine_ID)
	{
		if (JP_BulkUpdateProfileLine_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_BulkUpdateProfileLine_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_BulkUpdateProfileLine_ID, Integer.valueOf(JP_BulkUpdateProfileLine_ID));
	}

	/** Get Bulk Update Profile Line.
		@return Bulk Update Profile Line	  */
	public int getJP_BulkUpdateProfileLine_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_BulkUpdateProfileLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Bulk Update Profile Line(UU).
		@param JP_BulkUpdateProfileLine_UU Bulk Update Profile Line(UU)
	*/
	public void setJP_BulkUpdateProfileLine_UU (String JP_BulkUpdateProfileLine_UU)
	{
		set_Value (COLUMNNAME_JP_BulkUpdateProfileLine_UU, JP_BulkUpdateProfileLine_UU);
	}

	/** Get Bulk Update Profile Line(UU).
		@return Bulk Update Profile Line(UU)	  */
	public String getJP_BulkUpdateProfileLine_UU()
	{
		return (String)get_Value(COLUMNNAME_JP_BulkUpdateProfileLine_UU);
	}

	public I_JP_BulkUpdateProfile getJP_BulkUpdateProfile() throws RuntimeException
	{
		return (I_JP_BulkUpdateProfile)MTable.get(getCtx(), I_JP_BulkUpdateProfile.Table_ID)
			.getPO(getJP_BulkUpdateProfile_ID(), get_TrxName());
	}

	/** Set Bulk Update Profile.
		@param JP_BulkUpdateProfile_ID JPIERE-0621:JPBP
	*/
	public void setJP_BulkUpdateProfile_ID (int JP_BulkUpdateProfile_ID)
	{
		if (JP_BulkUpdateProfile_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_BulkUpdateProfile_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_BulkUpdateProfile_ID, Integer.valueOf(JP_BulkUpdateProfile_ID));
	}

	/** Get Bulk Update Profile.
		@return JPIERE-0621:JPBP
	  */
	public int getJP_BulkUpdateProfile_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_BulkUpdateProfile_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Masking  exclude first/last some characters = MB */
	public static final String JP_BULKUPDATETYPE_MaskingExcludeFirstLastSomeCharacters = "MB";
	/** Masking exclude first some characters = MF */
	public static final String JP_BULKUPDATETYPE_MaskingExcludeFirstSomeCharacters = "MF";
	/** Masking exclude last some characters = ML */
	public static final String JP_BULKUPDATETYPE_MaskingExcludeLastSomeCharacters = "ML";
	/** Replace string(Regex) = RR */
	public static final String JP_BULKUPDATETYPE_ReplaceStringRegex = "RR";
	/** Replace string = RS */
	public static final String JP_BULKUPDATETYPE_ReplaceString = "RS";
	/** SQL(UPDATE) = SQ */
	public static final String JP_BULKUPDATETYPE_SQLUPDATE = "SQ";
	/** Set Bulk update type.
		@param JP_BulkUpdateType Bulk update type
	*/
	public void setJP_BulkUpdateType (String JP_BulkUpdateType)
	{

		set_Value (COLUMNNAME_JP_BulkUpdateType, JP_BulkUpdateType);
	}

	/** Get Bulk update type.
		@return Bulk update type	  */
	public String getJP_BulkUpdateType()
	{
		return (String)get_Value(COLUMNNAME_JP_BulkUpdateType);
	}

	/** Set Masking String.
		@param JP_MaskingString Masking String
	*/
	public void setJP_MaskingString (String JP_MaskingString)
	{
		set_Value (COLUMNNAME_JP_MaskingString, JP_MaskingString);
	}

	/** Get Masking String.
		@return Masking String	  */
	public String getJP_MaskingString()
	{
		return (String)get_Value(COLUMNNAME_JP_MaskingString);
	}

	/** Match masking string = MS */
	public static final String JP_MASKINGTYPE_MatchMaskingString = "MS";
	/** Match the number of characters to be masked = NC */
	public static final String JP_MASKINGTYPE_MatchTheNumberOfCharactersToBeMasked = "NC";
	/** Set Masking Type.
		@param JP_MaskingType Masking Type
	*/
	public void setJP_MaskingType (String JP_MaskingType)
	{

		set_Value (COLUMNNAME_JP_MaskingType, JP_MaskingType);
	}

	/** Get Masking Type.
		@return Masking Type	  */
	public String getJP_MaskingType()
	{
		return (String)get_Value(COLUMNNAME_JP_MaskingType);
	}

	/** Set Number of characters to exclude from masking.
		@param JP_NumOfCharExcludeMasking Number of characters to exclude from masking
	*/
	public void setJP_NumOfCharExcludeMasking (int JP_NumOfCharExcludeMasking)
	{
		set_Value (COLUMNNAME_JP_NumOfCharExcludeMasking, Integer.valueOf(JP_NumOfCharExcludeMasking));
	}

	/** Get Number of characters to exclude from masking.
		@return Number of characters to exclude from masking	  */
	public int getJP_NumOfCharExcludeMasking()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_NumOfCharExcludeMasking);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Replacement String .
		@param JP_ReplacementString Replacement String 
	*/
	public void setJP_ReplacementString (String JP_ReplacementString)
	{
		set_Value (COLUMNNAME_JP_ReplacementString, JP_ReplacementString);
	}

	/** Get Replacement String .
		@return Replacement String 	  */
	public String getJP_ReplacementString()
	{
		return (String)get_Value(COLUMNNAME_JP_ReplacementString);
	}

	/** Set Target string.
		@param JP_TargetString Target string
	*/
	public void setJP_TargetString (String JP_TargetString)
	{
		set_Value (COLUMNNAME_JP_TargetString, JP_TargetString);
	}

	/** Get Target string.
		@return Target string	  */
	public String getJP_TargetString()
	{
		return (String)get_Value(COLUMNNAME_JP_TargetString);
	}

	/** Set Sql UPDATE SET .
		@param JP_UpdateSetClause Sql UPDATE SET 
	*/
	public void setJP_UpdateSetClause (String JP_UpdateSetClause)
	{
		set_Value (COLUMNNAME_JP_UpdateSetClause, JP_UpdateSetClause);
	}

	/** Get Sql UPDATE SET .
		@return Sql UPDATE SET 	  */
	public String getJP_UpdateSetClause()
	{
		return (String)get_Value(COLUMNNAME_JP_UpdateSetClause);
	}

	/** Set Line No.
		@param Line Unique line for this document
	*/
	public void setLine (int Line)
	{
		set_Value (COLUMNNAME_Line, Integer.valueOf(Line));
	}

	/** Get Line No.
		@return Unique line for this document
	  */
	public int getLine()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Line);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair()
    {
        return new KeyNamePair(get_ID(), String.valueOf(getLine()));
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