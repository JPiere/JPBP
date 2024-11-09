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

/** Generated Model for JP_GenericExpFormat_Replace
 *  @author iDempiere (generated)
 *  @version Release 11 - $Id$ */
@org.adempiere.base.Model(table="JP_GenericExpFormat_Replace")
public class X_JP_GenericExpFormat_Replace extends PO implements I_JP_GenericExpFormat_Replace, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20241108L;

    /** Standard Constructor */
    public X_JP_GenericExpFormat_Replace (Properties ctx, int JP_GenericExpFormat_Replace_ID, String trxName)
    {
      super (ctx, JP_GenericExpFormat_Replace_ID, trxName);
      /** if (JP_GenericExpFormat_Replace_ID == 0)
        {
			setJP_GenericExpFormat_Replace_ID (0);
			setJP_GenericExpFormat_Row_ID (0);
			setJP_ReplaceMethod (null);
// S
			setJP_TargetString (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_GenericExpFormat_Replace (Properties ctx, int JP_GenericExpFormat_Replace_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_GenericExpFormat_Replace_ID, trxName, virtualColumns);
      /** if (JP_GenericExpFormat_Replace_ID == 0)
        {
			setJP_GenericExpFormat_Replace_ID (0);
			setJP_GenericExpFormat_Row_ID (0);
			setJP_ReplaceMethod (null);
// S
			setJP_TargetString (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_GenericExpFormat_Replace (Properties ctx, String JP_GenericExpFormat_Replace_UU, String trxName)
    {
      super (ctx, JP_GenericExpFormat_Replace_UU, trxName);
      /** if (JP_GenericExpFormat_Replace_UU == null)
        {
			setJP_GenericExpFormat_Replace_ID (0);
			setJP_GenericExpFormat_Row_ID (0);
			setJP_ReplaceMethod (null);
// S
			setJP_TargetString (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_GenericExpFormat_Replace (Properties ctx, String JP_GenericExpFormat_Replace_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_GenericExpFormat_Replace_UU, trxName, virtualColumns);
      /** if (JP_GenericExpFormat_Replace_UU == null)
        {
			setJP_GenericExpFormat_Replace_ID (0);
			setJP_GenericExpFormat_Row_ID (0);
			setJP_ReplaceMethod (null);
// S
			setJP_TargetString (null);
        } */
    }

    /** Load Constructor */
    public X_JP_GenericExpFormat_Replace (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_JP_GenericExpFormat_Replace[")
        .append(get_ID()).append("]");
      return sb.toString();
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

	/** Set Replace.
		@param JP_GenericExpFormat_Replace_ID JPIERE-0628:JPBP
	*/
	public void setJP_GenericExpFormat_Replace_ID (int JP_GenericExpFormat_Replace_ID)
	{
		if (JP_GenericExpFormat_Replace_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_GenericExpFormat_Replace_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_GenericExpFormat_Replace_ID, Integer.valueOf(JP_GenericExpFormat_Replace_ID));
	}

	/** Get Replace.
		@return JPIERE-0628:JPBP
	  */
	public int getJP_GenericExpFormat_Replace_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_GenericExpFormat_Replace_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Replace(UU).
		@param JP_GenericExpFormat_Replace_UU Replace(UU)
	*/
	public void setJP_GenericExpFormat_Replace_UU (String JP_GenericExpFormat_Replace_UU)
	{
		set_Value (COLUMNNAME_JP_GenericExpFormat_Replace_UU, JP_GenericExpFormat_Replace_UU);
	}

	/** Get Replace(UU).
		@return Replace(UU)	  */
	public String getJP_GenericExpFormat_Replace_UU()
	{
		return (String)get_Value(COLUMNNAME_JP_GenericExpFormat_Replace_UU);
	}

	public I_JP_GenericExpFormat_Row getJP_GenericExpFormat_Row() throws RuntimeException
	{
		return (I_JP_GenericExpFormat_Row)MTable.get(getCtx(), I_JP_GenericExpFormat_Row.Table_ID)
			.getPO(getJP_GenericExpFormat_Row_ID(), get_TrxName());
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

	/** Regex = R */
	public static final String JP_REPLACEMETHOD_Regex = "R";
	/** String = S */
	public static final String JP_REPLACEMETHOD_String = "S";
	/** Set Replace Method.
		@param JP_ReplaceMethod Replace Method
	*/
	public void setJP_ReplaceMethod (String JP_ReplaceMethod)
	{

		set_Value (COLUMNNAME_JP_ReplaceMethod, JP_ReplaceMethod);
	}

	/** Get Replace Method.
		@return Replace Method	  */
	public String getJP_ReplaceMethod()
	{
		return (String)get_Value(COLUMNNAME_JP_ReplaceMethod);
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