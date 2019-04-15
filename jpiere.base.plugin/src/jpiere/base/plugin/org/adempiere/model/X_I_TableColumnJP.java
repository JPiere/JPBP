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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;

/** Generated Model for I_TableColumnJP
 *  @author iDempiere (generated) 
 *  @version Release 6.2 - $Id$ */
public class X_I_TableColumnJP extends PO implements I_I_TableColumnJP, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20190414L;

    /** Standard Constructor */
    public X_I_TableColumnJP (Properties ctx, int I_TableColumnJP_ID, String trxName)
    {
      super (ctx, I_TableColumnJP_ID, trxName);
      /** if (I_TableColumnJP_ID == 0)
        {
			setI_TableColumnJP_ID (0);
        } */
    }

    /** Load Constructor */
    public X_I_TableColumnJP (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 4 - System 
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
      StringBuffer sb = new StringBuffer ("X_I_TableColumnJP[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_Chart getAD_Chart() throws RuntimeException
    {
		return (org.compiere.model.I_AD_Chart)MTable.get(getCtx(), org.compiere.model.I_AD_Chart.Table_Name)
			.getPO(getAD_Chart_ID(), get_TrxName());	}

	/** Set Chart.
		@param AD_Chart_ID Chart	  */
	public void setAD_Chart_ID (int AD_Chart_ID)
	{
		if (AD_Chart_ID < 1) 
			set_Value (COLUMNNAME_AD_Chart_ID, null);
		else 
			set_Value (COLUMNNAME_AD_Chart_ID, Integer.valueOf(AD_Chart_ID));
	}

	/** Get Chart.
		@return Chart	  */
	public int getAD_Chart_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Chart_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_Column getAD_Column() throws RuntimeException
    {
		return (org.compiere.model.I_AD_Column)MTable.get(getCtx(), org.compiere.model.I_AD_Column.Table_Name)
			.getPO(getAD_Column_ID(), get_TrxName());	}

	/** Set Column.
		@param AD_Column_ID 
		Column in the table
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
	public int getAD_Column_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Column_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_Element getAD_Element() throws RuntimeException
    {
		return (org.compiere.model.I_AD_Element)MTable.get(getCtx(), org.compiere.model.I_AD_Element.Table_Name)
			.getPO(getAD_Element_ID(), get_TrxName());	}

	/** Set System Element.
		@param AD_Element_ID 
		System Element enables the central maintenance of column description and help.
	  */
	public void setAD_Element_ID (int AD_Element_ID)
	{
		if (AD_Element_ID < 1) 
			set_Value (COLUMNNAME_AD_Element_ID, null);
		else 
			set_Value (COLUMNNAME_AD_Element_ID, Integer.valueOf(AD_Element_ID));
	}

	/** Get System Element.
		@return System Element enables the central maintenance of column description and help.
	  */
	public int getAD_Element_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Element_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** AD_Language AD_Reference_ID=106 */
	public static final int AD_LANGUAGE_AD_Reference_ID=106;
	/** Set Language.
		@param AD_Language 
		Language for this entity
	  */
	public void setAD_Language (String AD_Language)
	{

		set_Value (COLUMNNAME_AD_Language, AD_Language);
	}

	/** Get Language.
		@return Language for this entity
	  */
	public String getAD_Language () 
	{
		return (String)get_Value(COLUMNNAME_AD_Language);
	}

	public org.compiere.model.I_AD_Process getAD_Process() throws RuntimeException
    {
		return (org.compiere.model.I_AD_Process)MTable.get(getCtx(), org.compiere.model.I_AD_Process.Table_Name)
			.getPO(getAD_Process_ID(), get_TrxName());	}

	/** Set Process.
		@param AD_Process_ID 
		Process or Report
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
	public int getAD_Process_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Process_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_Reference getAD_Reference() throws RuntimeException
    {
		return (org.compiere.model.I_AD_Reference)MTable.get(getCtx(), org.compiere.model.I_AD_Reference.Table_Name)
			.getPO(getAD_Reference_ID(), get_TrxName());	}

	/** Set Reference.
		@param AD_Reference_ID 
		System Reference and Validation
	  */
	public void setAD_Reference_ID (int AD_Reference_ID)
	{
		if (AD_Reference_ID < 1) 
			set_Value (COLUMNNAME_AD_Reference_ID, null);
		else 
			set_Value (COLUMNNAME_AD_Reference_ID, Integer.valueOf(AD_Reference_ID));
	}

	/** Get Reference.
		@return System Reference and Validation
	  */
	public int getAD_Reference_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Reference_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_Reference getAD_Reference_Value() throws RuntimeException
    {
		return (org.compiere.model.I_AD_Reference)MTable.get(getCtx(), org.compiere.model.I_AD_Reference.Table_Name)
			.getPO(getAD_Reference_Value_ID(), get_TrxName());	}

	/** Set Reference Key.
		@param AD_Reference_Value_ID 
		Required to specify, if data type is Table or List
	  */
	public void setAD_Reference_Value_ID (int AD_Reference_Value_ID)
	{
		if (AD_Reference_Value_ID < 1) 
			set_Value (COLUMNNAME_AD_Reference_Value_ID, null);
		else 
			set_Value (COLUMNNAME_AD_Reference_Value_ID, Integer.valueOf(AD_Reference_Value_ID));
	}

	/** Get Reference Key.
		@return Required to specify, if data type is Table or List
	  */
	public int getAD_Reference_Value_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Reference_Value_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_Table getAD_Table() throws RuntimeException
    {
		return (org.compiere.model.I_AD_Table)MTable.get(getCtx(), org.compiere.model.I_AD_Table.Table_Name)
			.getPO(getAD_Table_ID(), get_TrxName());	}

	/** Set Table.
		@param AD_Table_ID 
		Database Table information
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
	public int getAD_Table_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Table_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_Val_Rule getAD_Val_Rule() throws RuntimeException
    {
		return (org.compiere.model.I_AD_Val_Rule)MTable.get(getCtx(), org.compiere.model.I_AD_Val_Rule.Table_Name)
			.getPO(getAD_Val_Rule_ID(), get_TrxName());	}

	/** Set Dynamic Validation.
		@param AD_Val_Rule_ID 
		Dynamic Validation Rule
	  */
	public void setAD_Val_Rule_ID (int AD_Val_Rule_ID)
	{
		if (AD_Val_Rule_ID < 1) 
			set_Value (COLUMNNAME_AD_Val_Rule_ID, null);
		else 
			set_Value (COLUMNNAME_AD_Val_Rule_ID, Integer.valueOf(AD_Val_Rule_ID));
	}

	/** Get Dynamic Validation.
		@return Dynamic Validation Rule
	  */
	public int getAD_Val_Rule_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Val_Rule_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_Window getAD_Window() throws RuntimeException
    {
		return (org.compiere.model.I_AD_Window)MTable.get(getCtx(), org.compiere.model.I_AD_Window.Table_Name)
			.getPO(getAD_Window_ID(), get_TrxName());	}

	/** Set Window.
		@param AD_Window_ID 
		Data entry or display window
	  */
	public void setAD_Window_ID (int AD_Window_ID)
	{
		if (AD_Window_ID < 1) 
			set_Value (COLUMNNAME_AD_Window_ID, null);
		else 
			set_Value (COLUMNNAME_AD_Window_ID, Integer.valueOf(AD_Window_ID));
	}

	/** Get Window.
		@return Data entry or display window
	  */
	public int getAD_Window_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Window_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** AccessLevel AD_Reference_ID=5 */
	public static final int ACCESSLEVEL_AD_Reference_ID=5;
	/** Organization = 1 */
	public static final String ACCESSLEVEL_Organization = "1";
	/** Client+Organization = 3 */
	public static final String ACCESSLEVEL_ClientPlusOrganization = "3";
	/** System only = 4 */
	public static final String ACCESSLEVEL_SystemOnly = "4";
	/** All = 7 */
	public static final String ACCESSLEVEL_All = "7";
	/** System+Client = 6 */
	public static final String ACCESSLEVEL_SystemPlusClient = "6";
	/** Client only = 2 */
	public static final String ACCESSLEVEL_ClientOnly = "2";
	/** Set Data Access Level.
		@param AccessLevel 
		Access Level required
	  */
	public void setAccessLevel (String AccessLevel)
	{

		set_Value (COLUMNNAME_AccessLevel, AccessLevel);
	}

	/** Get Data Access Level.
		@return Access Level required
	  */
	public String getAccessLevel () 
	{
		return (String)get_Value(COLUMNNAME_AccessLevel);
	}

	/** Set Callout.
		@param Callout 
		Fully qualified class names and method - separated by semicolons
	  */
	public void setCallout (String Callout)
	{
		set_Value (COLUMNNAME_Callout, Callout);
	}

	/** Get Callout.
		@return Fully qualified class names and method - separated by semicolons
	  */
	public String getCallout () 
	{
		return (String)get_Value(COLUMNNAME_Callout);
	}

	/** Set DB Column Name.
		@param ColumnName 
		Name of the column in the database
	  */
	public void setColumnName (String ColumnName)
	{
		set_Value (COLUMNNAME_ColumnName, ColumnName);
	}

	/** Get DB Column Name.
		@return Name of the column in the database
	  */
	public String getColumnName () 
	{
		return (String)get_Value(COLUMNNAME_ColumnName);
	}

	/** Set Column SQL.
		@param ColumnSQL 
		Virtual Column (r/o)
	  */
	public void setColumnSQL (String ColumnSQL)
	{
		set_Value (COLUMNNAME_ColumnSQL, ColumnSQL);
	}

	/** Get Column SQL.
		@return Virtual Column (r/o)
	  */
	public String getColumnSQL () 
	{
		return (String)get_Value(COLUMNNAME_ColumnSQL);
	}

	/** Set Default Logic.
		@param DefaultValue 
		Default value hierarchy, separated by ;
	  */
	public void setDefaultValue (String DefaultValue)
	{
		set_Value (COLUMNNAME_DefaultValue, DefaultValue);
	}

	/** Get Default Logic.
		@return Default value hierarchy, separated by ;
	  */
	public String getDefaultValue () 
	{
		return (String)get_Value(COLUMNNAME_DefaultValue);
	}

	/** Set Constraint Name.
		@param FKConstraintName Constraint Name	  */
	public void setFKConstraintName (String FKConstraintName)
	{
		set_Value (COLUMNNAME_FKConstraintName, FKConstraintName);
	}

	/** Get Constraint Name.
		@return Constraint Name	  */
	public String getFKConstraintName () 
	{
		return (String)get_Value(COLUMNNAME_FKConstraintName);
	}

	/** FKConstraintType AD_Reference_ID=200075 */
	public static final int FKCONSTRAINTTYPE_AD_Reference_ID=200075;
	/** Do Not Create = D */
	public static final String FKCONSTRAINTTYPE_DoNotCreate = "D";
	/** No Action = N */
	public static final String FKCONSTRAINTTYPE_NoAction = "N";
	/** Cascade = C */
	public static final String FKCONSTRAINTTYPE_Cascade = "C";
	/** Set Null = S */
	public static final String FKCONSTRAINTTYPE_SetNull = "S";
	/** Model Cascade = M */
	public static final String FKCONSTRAINTTYPE_ModelCascade = "M";
	/** Set Constraint Type.
		@param FKConstraintType Constraint Type	  */
	public void setFKConstraintType (String FKConstraintType)
	{

		set_Value (COLUMNNAME_FKConstraintType, FKConstraintType);
	}

	/** Get Constraint Type.
		@return Constraint Type	  */
	public String getFKConstraintType () 
	{
		return (String)get_Value(COLUMNNAME_FKConstraintType);
	}

	/** Set Length.
		@param FieldLength 
		Length of the column in the database
	  */
	public void setFieldLength (int FieldLength)
	{
		set_Value (COLUMNNAME_FieldLength, Integer.valueOf(FieldLength));
	}

	/** Get Length.
		@return Length of the column in the database
	  */
	public int getFieldLength () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_FieldLength);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Format Pattern.
		@param FormatPattern 
		The pattern used to format a number or date.
	  */
	public void setFormatPattern (String FormatPattern)
	{
		set_Value (COLUMNNAME_FormatPattern, FormatPattern);
	}

	/** Get Format Pattern.
		@return The pattern used to format a number or date.
	  */
	public String getFormatPattern () 
	{
		return (String)get_Value(COLUMNNAME_FormatPattern);
	}

	/** Set Import Error Message.
		@param I_ErrorMsg 
		Messages generated from import process
	  */
	public void setI_ErrorMsg (String I_ErrorMsg)
	{
		set_Value (COLUMNNAME_I_ErrorMsg, I_ErrorMsg);
	}

	/** Get Import Error Message.
		@return Messages generated from import process
	  */
	public String getI_ErrorMsg () 
	{
		return (String)get_Value(COLUMNNAME_I_ErrorMsg);
	}

	/** Set Imported.
		@param I_IsImported 
		Has this import been processed
	  */
	public void setI_IsImported (boolean I_IsImported)
	{
		set_Value (COLUMNNAME_I_IsImported, Boolean.valueOf(I_IsImported));
	}

	/** Get Imported.
		@return Has this import been processed
	  */
	public boolean isI_IsImported () 
	{
		Object oo = get_Value(COLUMNNAME_I_IsImported);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set I_TableColumnJP.
		@param I_TableColumnJP_ID I_TableColumnJP	  */
	public void setI_TableColumnJP_ID (int I_TableColumnJP_ID)
	{
		if (I_TableColumnJP_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_I_TableColumnJP_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_I_TableColumnJP_ID, Integer.valueOf(I_TableColumnJP_ID));
	}

	/** Get I_TableColumnJP.
		@return I_TableColumnJP	  */
	public int getI_TableColumnJP_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_I_TableColumnJP_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set I_TableColumnJP_UU.
		@param I_TableColumnJP_UU I_TableColumnJP_UU	  */
	public void setI_TableColumnJP_UU (String I_TableColumnJP_UU)
	{
		set_ValueNoCheck (COLUMNNAME_I_TableColumnJP_UU, I_TableColumnJP_UU);
	}

	/** Get I_TableColumnJP_UU.
		@return I_TableColumnJP_UU	  */
	public String getI_TableColumnJP_UU () 
	{
		return (String)get_Value(COLUMNNAME_I_TableColumnJP_UU);
	}

	/** IsAllowCopy AD_Reference_ID=319 */
	public static final int ISALLOWCOPY_AD_Reference_ID=319;
	/** Yes = Y */
	public static final String ISALLOWCOPY_Yes = "Y";
	/** No = N */
	public static final String ISALLOWCOPY_No = "N";
	/** Set Allow Copy.
		@param IsAllowCopy 
		Determine if a column must be copied when pushing the button to copy record
	  */
	public void setIsAllowCopy (String IsAllowCopy)
	{

		set_Value (COLUMNNAME_IsAllowCopy, IsAllowCopy);
	}

	/** Get Allow Copy.
		@return Determine if a column must be copied when pushing the button to copy record
	  */
	public String getIsAllowCopy () 
	{
		return (String)get_Value(COLUMNNAME_IsAllowCopy);
	}

	/** IsAllowLogging AD_Reference_ID=319 */
	public static final int ISALLOWLOGGING_AD_Reference_ID=319;
	/** Yes = Y */
	public static final String ISALLOWLOGGING_Yes = "Y";
	/** No = N */
	public static final String ISALLOWLOGGING_No = "N";
	/** Set Allow Logging.
		@param IsAllowLogging 
		Determine if a column must be recorded into the change log
	  */
	public void setIsAllowLogging (String IsAllowLogging)
	{

		set_Value (COLUMNNAME_IsAllowLogging, IsAllowLogging);
	}

	/** Get Allow Logging.
		@return Determine if a column must be recorded into the change log
	  */
	public String getIsAllowLogging () 
	{
		return (String)get_Value(COLUMNNAME_IsAllowLogging);
	}

	/** IsAlwaysUpdateable AD_Reference_ID=319 */
	public static final int ISALWAYSUPDATEABLE_AD_Reference_ID=319;
	/** Yes = Y */
	public static final String ISALWAYSUPDATEABLE_Yes = "Y";
	/** No = N */
	public static final String ISALWAYSUPDATEABLE_No = "N";
	/** Set Always Updatable.
		@param IsAlwaysUpdateable 
		The column is always updateable, even if the record is not active or processed
	  */
	public void setIsAlwaysUpdateable (String IsAlwaysUpdateable)
	{

		set_Value (COLUMNNAME_IsAlwaysUpdateable, IsAlwaysUpdateable);
	}

	/** Get Always Updatable.
		@return The column is always updateable, even if the record is not active or processed
	  */
	public String getIsAlwaysUpdateable () 
	{
		return (String)get_Value(COLUMNNAME_IsAlwaysUpdateable);
	}

	/** IsAutocomplete AD_Reference_ID=319 */
	public static final int ISAUTOCOMPLETE_AD_Reference_ID=319;
	/** Yes = Y */
	public static final String ISAUTOCOMPLETE_Yes = "Y";
	/** No = N */
	public static final String ISAUTOCOMPLETE_No = "N";
	/** Set Autocomplete.
		@param IsAutocomplete 
		Automatic completion for textfields
	  */
	public void setIsAutocomplete (String IsAutocomplete)
	{

		set_Value (COLUMNNAME_IsAutocomplete, IsAutocomplete);
	}

	/** Get Autocomplete.
		@return Automatic completion for textfields
	  */
	public String getIsAutocomplete () 
	{
		return (String)get_Value(COLUMNNAME_IsAutocomplete);
	}

	/** IsCentrallyMaintained AD_Reference_ID=319 */
	public static final int ISCENTRALLYMAINTAINED_AD_Reference_ID=319;
	/** Yes = Y */
	public static final String ISCENTRALLYMAINTAINED_Yes = "Y";
	/** No = N */
	public static final String ISCENTRALLYMAINTAINED_No = "N";
	/** Set Centrally maintained.
		@param IsCentrallyMaintained 
		Information maintained in System Element table
	  */
	public void setIsCentrallyMaintained (String IsCentrallyMaintained)
	{

		set_Value (COLUMNNAME_IsCentrallyMaintained, IsCentrallyMaintained);
	}

	/** Get Centrally maintained.
		@return Information maintained in System Element table
	  */
	public String getIsCentrallyMaintained () 
	{
		return (String)get_Value(COLUMNNAME_IsCentrallyMaintained);
	}

	/** IsChangeLog AD_Reference_ID=319 */
	public static final int ISCHANGELOG_AD_Reference_ID=319;
	/** Yes = Y */
	public static final String ISCHANGELOG_Yes = "Y";
	/** No = N */
	public static final String ISCHANGELOG_No = "N";
	/** Set Maintain Change Log.
		@param IsChangeLog 
		Maintain a log of changes
	  */
	public void setIsChangeLog (String IsChangeLog)
	{

		set_Value (COLUMNNAME_IsChangeLog, IsChangeLog);
	}

	/** Get Maintain Change Log.
		@return Maintain a log of changes
	  */
	public String getIsChangeLog () 
	{
		return (String)get_Value(COLUMNNAME_IsChangeLog);
	}

	/** IsDeleteable AD_Reference_ID=319 */
	public static final int ISDELETEABLE_AD_Reference_ID=319;
	/** Yes = Y */
	public static final String ISDELETEABLE_Yes = "Y";
	/** No = N */
	public static final String ISDELETEABLE_No = "N";
	/** Set Records deletable.
		@param IsDeleteable 
		Indicates if records can be deleted from the database
	  */
	public void setIsDeleteable (String IsDeleteable)
	{

		set_Value (COLUMNNAME_IsDeleteable, IsDeleteable);
	}

	/** Get Records deletable.
		@return Indicates if records can be deleted from the database
	  */
	public String getIsDeleteable () 
	{
		return (String)get_Value(COLUMNNAME_IsDeleteable);
	}

	/** IsEncrypted AD_Reference_ID=319 */
	public static final int ISENCRYPTED_AD_Reference_ID=319;
	/** Yes = Y */
	public static final String ISENCRYPTED_Yes = "Y";
	/** No = N */
	public static final String ISENCRYPTED_No = "N";
	/** Set Encrypted.
		@param IsEncrypted 
		Display or Storage is encrypted
	  */
	public void setIsEncrypted (String IsEncrypted)
	{

		set_Value (COLUMNNAME_IsEncrypted, IsEncrypted);
	}

	/** Get Encrypted.
		@return Display or Storage is encrypted
	  */
	public String getIsEncrypted () 
	{
		return (String)get_Value(COLUMNNAME_IsEncrypted);
	}

	/** IsHighVolume AD_Reference_ID=319 */
	public static final int ISHIGHVOLUME_AD_Reference_ID=319;
	/** Yes = Y */
	public static final String ISHIGHVOLUME_Yes = "Y";
	/** No = N */
	public static final String ISHIGHVOLUME_No = "N";
	/** Set High Volume.
		@param IsHighVolume 
		Use Search instead of Pick list
	  */
	public void setIsHighVolume (String IsHighVolume)
	{

		set_Value (COLUMNNAME_IsHighVolume, IsHighVolume);
	}

	/** Get High Volume.
		@return Use Search instead of Pick list
	  */
	public String getIsHighVolume () 
	{
		return (String)get_Value(COLUMNNAME_IsHighVolume);
	}

	/** IsHtml AD_Reference_ID=319 */
	public static final int ISHTML_AD_Reference_ID=319;
	/** Yes = Y */
	public static final String ISHTML_Yes = "Y";
	/** No = N */
	public static final String ISHTML_No = "N";
	/** Set HTML.
		@param IsHtml 
		Text has HTML tags
	  */
	public void setIsHtml (String IsHtml)
	{

		set_Value (COLUMNNAME_IsHtml, IsHtml);
	}

	/** Get HTML.
		@return Text has HTML tags
	  */
	public String getIsHtml () 
	{
		return (String)get_Value(COLUMNNAME_IsHtml);
	}

	/** IsIdentifier AD_Reference_ID=319 */
	public static final int ISIDENTIFIER_AD_Reference_ID=319;
	/** Yes = Y */
	public static final String ISIDENTIFIER_Yes = "Y";
	/** No = N */
	public static final String ISIDENTIFIER_No = "N";
	/** Set Identifier.
		@param IsIdentifier 
		This column is part of the record identifier
	  */
	public void setIsIdentifier (String IsIdentifier)
	{

		set_Value (COLUMNNAME_IsIdentifier, IsIdentifier);
	}

	/** Get Identifier.
		@return This column is part of the record identifier
	  */
	public String getIsIdentifier () 
	{
		return (String)get_Value(COLUMNNAME_IsIdentifier);
	}

	/** IsKey AD_Reference_ID=319 */
	public static final int ISKEY_AD_Reference_ID=319;
	/** Yes = Y */
	public static final String ISKEY_Yes = "Y";
	/** No = N */
	public static final String ISKEY_No = "N";
	/** Set Key column.
		@param IsKey 
		This column is the key in this table
	  */
	public void setIsKey (String IsKey)
	{

		set_Value (COLUMNNAME_IsKey, IsKey);
	}

	/** Get Key column.
		@return This column is the key in this table
	  */
	public String getIsKey () 
	{
		return (String)get_Value(COLUMNNAME_IsKey);
	}

	/** IsMandatory AD_Reference_ID=319 */
	public static final int ISMANDATORY_AD_Reference_ID=319;
	/** Yes = Y */
	public static final String ISMANDATORY_Yes = "Y";
	/** No = N */
	public static final String ISMANDATORY_No = "N";
	/** Set Mandatory.
		@param IsMandatory 
		Data entry is required in this column
	  */
	public void setIsMandatory (String IsMandatory)
	{

		set_Value (COLUMNNAME_IsMandatory, IsMandatory);
	}

	/** Get Mandatory.
		@return Data entry is required in this column
	  */
	public String getIsMandatory () 
	{
		return (String)get_Value(COLUMNNAME_IsMandatory);
	}

	/** IsParent AD_Reference_ID=319 */
	public static final int ISPARENT_AD_Reference_ID=319;
	/** Yes = Y */
	public static final String ISPARENT_Yes = "Y";
	/** No = N */
	public static final String ISPARENT_No = "N";
	/** Set Parent link column.
		@param IsParent 
		This column is a link to the parent table (e.g. header from lines) - incl. Association key columns
	  */
	public void setIsParent (String IsParent)
	{

		set_Value (COLUMNNAME_IsParent, IsParent);
	}

	/** Get Parent link column.
		@return This column is a link to the parent table (e.g. header from lines) - incl. Association key columns
	  */
	public String getIsParent () 
	{
		return (String)get_Value(COLUMNNAME_IsParent);
	}

	/** IsSecure AD_Reference_ID=319 */
	public static final int ISSECURE_AD_Reference_ID=319;
	/** Yes = Y */
	public static final String ISSECURE_Yes = "Y";
	/** No = N */
	public static final String ISSECURE_No = "N";
	/** Set Secure content.
		@param IsSecure 
		Defines whether content must be treated as secure
	  */
	public void setIsSecure (String IsSecure)
	{

		set_Value (COLUMNNAME_IsSecure, IsSecure);
	}

	/** Get Secure content.
		@return Defines whether content must be treated as secure
	  */
	public String getIsSecure () 
	{
		return (String)get_Value(COLUMNNAME_IsSecure);
	}

	/** IsSelectionColumn AD_Reference_ID=319 */
	public static final int ISSELECTIONCOLUMN_AD_Reference_ID=319;
	/** Yes = Y */
	public static final String ISSELECTIONCOLUMN_Yes = "Y";
	/** No = N */
	public static final String ISSELECTIONCOLUMN_No = "N";
	/** Set Selection Column.
		@param IsSelectionColumn 
		Is this column used for finding rows in windows
	  */
	public void setIsSelectionColumn (String IsSelectionColumn)
	{

		set_Value (COLUMNNAME_IsSelectionColumn, IsSelectionColumn);
	}

	/** Get Selection Column.
		@return Is this column used for finding rows in windows
	  */
	public String getIsSelectionColumn () 
	{
		return (String)get_Value(COLUMNNAME_IsSelectionColumn);
	}

	/** IsToolbarButton AD_Reference_ID=200099 */
	public static final int ISTOOLBARBUTTON_AD_Reference_ID=200099;
	/** Toolbar = Y */
	public static final String ISTOOLBARBUTTON_Toolbar = "Y";
	/** Window = N */
	public static final String ISTOOLBARBUTTON_Window = "N";
	/** Both = B */
	public static final String ISTOOLBARBUTTON_Both = "B";
	/** Set Toolbar Button.
		@param IsToolbarButton 
		Show the button on the toolbar, the window, or both
	  */
	public void setIsToolbarButton (String IsToolbarButton)
	{

		set_Value (COLUMNNAME_IsToolbarButton, IsToolbarButton);
	}

	/** Get Toolbar Button.
		@return Show the button on the toolbar, the window, or both
	  */
	public String getIsToolbarButton () 
	{
		return (String)get_Value(COLUMNNAME_IsToolbarButton);
	}

	/** IsTranslated AD_Reference_ID=319 */
	public static final int ISTRANSLATED_AD_Reference_ID=319;
	/** Yes = Y */
	public static final String ISTRANSLATED_Yes = "Y";
	/** No = N */
	public static final String ISTRANSLATED_No = "N";
	/** Set Translated.
		@param IsTranslated 
		This column is translated
	  */
	public void setIsTranslated (String IsTranslated)
	{

		set_Value (COLUMNNAME_IsTranslated, IsTranslated);
	}

	/** Get Translated.
		@return This column is translated
	  */
	public String getIsTranslated () 
	{
		return (String)get_Value(COLUMNNAME_IsTranslated);
	}

	/** IsUpdateable AD_Reference_ID=319 */
	public static final int ISUPDATEABLE_AD_Reference_ID=319;
	/** Yes = Y */
	public static final String ISUPDATEABLE_Yes = "Y";
	/** No = N */
	public static final String ISUPDATEABLE_No = "N";
	/** Set Updatable.
		@param IsUpdateable 
		Determines, if the field can be updated
	  */
	public void setIsUpdateable (String IsUpdateable)
	{

		set_Value (COLUMNNAME_IsUpdateable, IsUpdateable);
	}

	/** Get Updatable.
		@return Determines, if the field can be updated
	  */
	public String getIsUpdateable () 
	{
		return (String)get_Value(COLUMNNAME_IsUpdateable);
	}

	/** IsView AD_Reference_ID=319 */
	public static final int ISVIEW_AD_Reference_ID=319;
	/** Yes = Y */
	public static final String ISVIEW_Yes = "Y";
	/** No = N */
	public static final String ISVIEW_No = "N";
	/** Set View.
		@param IsView 
		This is a view
	  */
	public void setIsView (String IsView)
	{

		set_Value (COLUMNNAME_IsView, IsView);
	}

	/** Get View.
		@return This is a view
	  */
	public String getIsView () 
	{
		return (String)get_Value(COLUMNNAME_IsView);
	}

	/** Set Chart(Name).
		@param JP_Chart_Name Chart(Name)	  */
	public void setJP_Chart_Name (String JP_Chart_Name)
	{
		set_Value (COLUMNNAME_JP_Chart_Name, JP_Chart_Name);
	}

	/** Get Chart(Name).
		@return Chart(Name)	  */
	public String getJP_Chart_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_Chart_Name);
	}

	/** Set Description of Column.
		@param JP_Column_Description Description of Column	  */
	public void setJP_Column_Description (String JP_Column_Description)
	{
		set_Value (COLUMNNAME_JP_Column_Description, JP_Column_Description);
	}

	/** Get Description of Column.
		@return Description of Column	  */
	public String getJP_Column_Description () 
	{
		return (String)get_Value(COLUMNNAME_JP_Column_Description);
	}

	/** Set Entity Type of Column.
		@param JP_Column_EntityType Entity Type of Column	  */
	public void setJP_Column_EntityType (String JP_Column_EntityType)
	{
		set_Value (COLUMNNAME_JP_Column_EntityType, JP_Column_EntityType);
	}

	/** Get Entity Type of Column.
		@return Entity Type of Column	  */
	public String getJP_Column_EntityType () 
	{
		return (String)get_Value(COLUMNNAME_JP_Column_EntityType);
	}

	/** Set Help of Column.
		@param JP_Column_Help Help of Column	  */
	public void setJP_Column_Help (String JP_Column_Help)
	{
		set_Value (COLUMNNAME_JP_Column_Help, JP_Column_Help);
	}

	/** Get Help of Column.
		@return Help of Column	  */
	public String getJP_Column_Help () 
	{
		return (String)get_Value(COLUMNNAME_JP_Column_Help);
	}

	/** Set Name of Column.
		@param JP_Column_Name Name of Column	  */
	public void setJP_Column_Name (String JP_Column_Name)
	{
		set_Value (COLUMNNAME_JP_Column_Name, JP_Column_Name);
	}

	/** Get Name of Column.
		@return Name of Column	  */
	public String getJP_Column_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_Column_Name);
	}

	/** Set Placeholder of Column.
		@param JP_Column_Placeholder Placeholder of Column	  */
	public void setJP_Column_Placeholder (String JP_Column_Placeholder)
	{
		set_Value (COLUMNNAME_JP_Column_Placeholder, JP_Column_Placeholder);
	}

	/** Get Placeholder of Column.
		@return Placeholder of Column	  */
	public String getJP_Column_Placeholder () 
	{
		return (String)get_Value(COLUMNNAME_JP_Column_Placeholder);
	}

	/** Set Dashboard Content(Name).
		@param JP_DashboardContent_Name Dashboard Content(Name)	  */
	public void setJP_DashboardContent_Name (String JP_DashboardContent_Name)
	{
		set_Value (COLUMNNAME_JP_DashboardContent_Name, JP_DashboardContent_Name);
	}

	/** Get Dashboard Content(Name).
		@return Dashboard Content(Name)	  */
	public String getJP_DashboardContent_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_DashboardContent_Name);
	}

	/** Set Description of Element.
		@param JP_Element_Description Description of Element	  */
	public void setJP_Element_Description (String JP_Element_Description)
	{
		set_Value (COLUMNNAME_JP_Element_Description, JP_Element_Description);
	}

	/** Get Description of Element.
		@return Description of Element	  */
	public String getJP_Element_Description () 
	{
		return (String)get_Value(COLUMNNAME_JP_Element_Description);
	}

	/** Set Entity Type of Element.
		@param JP_Element_EntityType Entity Type of Element	  */
	public void setJP_Element_EntityType (String JP_Element_EntityType)
	{
		set_Value (COLUMNNAME_JP_Element_EntityType, JP_Element_EntityType);
	}

	/** Get Entity Type of Element.
		@return Entity Type of Element	  */
	public String getJP_Element_EntityType () 
	{
		return (String)get_Value(COLUMNNAME_JP_Element_EntityType);
	}

	/** Set Help of Element.
		@param JP_Element_Help Help of Element	  */
	public void setJP_Element_Help (String JP_Element_Help)
	{
		set_Value (COLUMNNAME_JP_Element_Help, JP_Element_Help);
	}

	/** Get Help of Element.
		@return Help of Element	  */
	public String getJP_Element_Help () 
	{
		return (String)get_Value(COLUMNNAME_JP_Element_Help);
	}

	/** Set Name of Element.
		@param JP_Element_Name Name of Element	  */
	public void setJP_Element_Name (String JP_Element_Name)
	{
		set_Value (COLUMNNAME_JP_Element_Name, JP_Element_Name);
	}

	/** Get Name of Element.
		@return Name of Element	  */
	public String getJP_Element_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_Element_Name);
	}

	/** Set PO Description of Element.
		@param JP_Element_PO_Description PO Description of Element	  */
	public void setJP_Element_PO_Description (String JP_Element_PO_Description)
	{
		set_Value (COLUMNNAME_JP_Element_PO_Description, JP_Element_PO_Description);
	}

	/** Get PO Description of Element.
		@return PO Description of Element	  */
	public String getJP_Element_PO_Description () 
	{
		return (String)get_Value(COLUMNNAME_JP_Element_PO_Description);
	}

	/** Set PO Help of Element.
		@param JP_Element_PO_Help PO Help of Element	  */
	public void setJP_Element_PO_Help (String JP_Element_PO_Help)
	{
		set_Value (COLUMNNAME_JP_Element_PO_Help, JP_Element_PO_Help);
	}

	/** Get PO Help of Element.
		@return PO Help of Element	  */
	public String getJP_Element_PO_Help () 
	{
		return (String)get_Value(COLUMNNAME_JP_Element_PO_Help);
	}

	/** Set PO Name of Element.
		@param JP_Element_PO_Name PO Name of Element	  */
	public void setJP_Element_PO_Name (String JP_Element_PO_Name)
	{
		set_Value (COLUMNNAME_JP_Element_PO_Name, JP_Element_PO_Name);
	}

	/** Get PO Name of Element.
		@return PO Name of Element	  */
	public String getJP_Element_PO_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_Element_PO_Name);
	}

	/** Set PO Print Name of Element.
		@param JP_Element_PO_PrintName PO Print Name of Element	  */
	public void setJP_Element_PO_PrintName (String JP_Element_PO_PrintName)
	{
		set_Value (COLUMNNAME_JP_Element_PO_PrintName, JP_Element_PO_PrintName);
	}

	/** Get PO Print Name of Element.
		@return PO Print Name of Element	  */
	public String getJP_Element_PO_PrintName () 
	{
		return (String)get_Value(COLUMNNAME_JP_Element_PO_PrintName);
	}

	/** Set Placeholder of Element.
		@param JP_Element_Placeholder Placeholder of Element	  */
	public void setJP_Element_Placeholder (String JP_Element_Placeholder)
	{
		set_Value (COLUMNNAME_JP_Element_Placeholder, JP_Element_Placeholder);
	}

	/** Get Placeholder of Element.
		@return Placeholder of Element	  */
	public String getJP_Element_Placeholder () 
	{
		return (String)get_Value(COLUMNNAME_JP_Element_Placeholder);
	}

	/** Set PrintName of Element.
		@param JP_Element_PrintName PrintName of Element	  */
	public void setJP_Element_PrintName (String JP_Element_PrintName)
	{
		set_Value (COLUMNNAME_JP_Element_PrintName, JP_Element_PrintName);
	}

	/** Get PrintName of Element.
		@return PrintName of Element	  */
	public String getJP_Element_PrintName () 
	{
		return (String)get_Value(COLUMNNAME_JP_Element_PrintName);
	}

	/** Set Description of Element Trl.
		@param JP_Element_Trl_Description Description of Element Trl	  */
	public void setJP_Element_Trl_Description (String JP_Element_Trl_Description)
	{
		set_Value (COLUMNNAME_JP_Element_Trl_Description, JP_Element_Trl_Description);
	}

	/** Get Description of Element Trl.
		@return Description of Element Trl	  */
	public String getJP_Element_Trl_Description () 
	{
		return (String)get_Value(COLUMNNAME_JP_Element_Trl_Description);
	}

	/** Set Help of Element Trl.
		@param JP_Element_Trl_Help Help of Element Trl	  */
	public void setJP_Element_Trl_Help (String JP_Element_Trl_Help)
	{
		set_Value (COLUMNNAME_JP_Element_Trl_Help, JP_Element_Trl_Help);
	}

	/** Get Help of Element Trl.
		@return Help of Element Trl	  */
	public String getJP_Element_Trl_Help () 
	{
		return (String)get_Value(COLUMNNAME_JP_Element_Trl_Help);
	}

	/** Set Name of Element Trl.
		@param JP_Element_Trl_Name Name of Element Trl	  */
	public void setJP_Element_Trl_Name (String JP_Element_Trl_Name)
	{
		set_Value (COLUMNNAME_JP_Element_Trl_Name, JP_Element_Trl_Name);
	}

	/** Get Name of Element Trl.
		@return Name of Element Trl	  */
	public String getJP_Element_Trl_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_Element_Trl_Name);
	}

	/** Set PO Description of Element Trl.
		@param JP_Element_Trl_PO_Description PO Description of Element Trl	  */
	public void setJP_Element_Trl_PO_Description (String JP_Element_Trl_PO_Description)
	{
		set_Value (COLUMNNAME_JP_Element_Trl_PO_Description, JP_Element_Trl_PO_Description);
	}

	/** Get PO Description of Element Trl.
		@return PO Description of Element Trl	  */
	public String getJP_Element_Trl_PO_Description () 
	{
		return (String)get_Value(COLUMNNAME_JP_Element_Trl_PO_Description);
	}

	/** Set PO Help of Element Trl.
		@param JP_Element_Trl_PO_Help PO Help of Element Trl	  */
	public void setJP_Element_Trl_PO_Help (String JP_Element_Trl_PO_Help)
	{
		set_Value (COLUMNNAME_JP_Element_Trl_PO_Help, JP_Element_Trl_PO_Help);
	}

	/** Get PO Help of Element Trl.
		@return PO Help of Element Trl	  */
	public String getJP_Element_Trl_PO_Help () 
	{
		return (String)get_Value(COLUMNNAME_JP_Element_Trl_PO_Help);
	}

	/** Set PO Name of Element Trl.
		@param JP_Element_Trl_PO_Name PO Name of Element Trl	  */
	public void setJP_Element_Trl_PO_Name (String JP_Element_Trl_PO_Name)
	{
		set_Value (COLUMNNAME_JP_Element_Trl_PO_Name, JP_Element_Trl_PO_Name);
	}

	/** Get PO Name of Element Trl.
		@return PO Name of Element Trl	  */
	public String getJP_Element_Trl_PO_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_Element_Trl_PO_Name);
	}

	/** Set Print Name of Element Trl.
		@param JP_Element_Trl_PO_PrintName Print Name of Element Trl	  */
	public void setJP_Element_Trl_PO_PrintName (String JP_Element_Trl_PO_PrintName)
	{
		set_Value (COLUMNNAME_JP_Element_Trl_PO_PrintName, JP_Element_Trl_PO_PrintName);
	}

	/** Get Print Name of Element Trl.
		@return Print Name of Element Trl	  */
	public String getJP_Element_Trl_PO_PrintName () 
	{
		return (String)get_Value(COLUMNNAME_JP_Element_Trl_PO_PrintName);
	}

	/** Set Placeholder of Element Trl.
		@param JP_Element_Trl_Placeholder Placeholder of Element Trl	  */
	public void setJP_Element_Trl_Placeholder (String JP_Element_Trl_Placeholder)
	{
		set_Value (COLUMNNAME_JP_Element_Trl_Placeholder, JP_Element_Trl_Placeholder);
	}

	/** Get Placeholder of Element Trl.
		@return Placeholder of Element Trl	  */
	public String getJP_Element_Trl_Placeholder () 
	{
		return (String)get_Value(COLUMNNAME_JP_Element_Trl_Placeholder);
	}

	/** Set Print Name of Element Trl.
		@param JP_Element_Trl_PrintName Print Name of Element Trl	  */
	public void setJP_Element_Trl_PrintName (String JP_Element_Trl_PrintName)
	{
		set_Value (COLUMNNAME_JP_Element_Trl_PrintName, JP_Element_Trl_PrintName);
	}

	/** Get Print Name of Element Trl.
		@return Print Name of Element Trl	  */
	public String getJP_Element_Trl_PrintName () 
	{
		return (String)get_Value(COLUMNNAME_JP_Element_Trl_PrintName);
	}

	/** Set PO Window(Name).
		@param JP_PO_Window_Name PO Window(Name)	  */
	public void setJP_PO_Window_Name (String JP_PO_Window_Name)
	{
		set_Value (COLUMNNAME_JP_PO_Window_Name, JP_PO_Window_Name);
	}

	/** Get PO Window(Name).
		@return PO Window(Name)	  */
	public String getJP_PO_Window_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_PO_Window_Name);
	}

	/** Set Process(Value).
		@param JP_Process_Value Process(Value)	  */
	public void setJP_Process_Value (String JP_Process_Value)
	{
		set_Value (COLUMNNAME_JP_Process_Value, JP_Process_Value);
	}

	/** Get Process(Value).
		@return Process(Value)	  */
	public String getJP_Process_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Process_Value);
	}

	/** Set Reference(Name).
		@param JP_Reference_Name Reference(Name)	  */
	public void setJP_Reference_Name (String JP_Reference_Name)
	{
		set_Value (COLUMNNAME_JP_Reference_Name, JP_Reference_Name);
	}

	/** Get Reference(Name).
		@return Reference(Name)	  */
	public String getJP_Reference_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_Reference_Name);
	}

	/** Set Reference Key(Name).
		@param JP_Reference_Value_Name Reference Key(Name)	  */
	public void setJP_Reference_Value_Name (String JP_Reference_Value_Name)
	{
		set_Value (COLUMNNAME_JP_Reference_Value_Name, JP_Reference_Value_Name);
	}

	/** Get Reference Key(Name).
		@return Reference Key(Name)	  */
	public String getJP_Reference_Value_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_Reference_Value_Name);
	}

	/** Set Description of Table.
		@param JP_Table_Description Description of Table	  */
	public void setJP_Table_Description (String JP_Table_Description)
	{
		set_Value (COLUMNNAME_JP_Table_Description, JP_Table_Description);
	}

	/** Get Description of Table.
		@return Description of Table	  */
	public String getJP_Table_Description () 
	{
		return (String)get_Value(COLUMNNAME_JP_Table_Description);
	}

	/** Set Entity Type of Table.
		@param JP_Table_EntityType Entity Type of Table	  */
	public void setJP_Table_EntityType (String JP_Table_EntityType)
	{
		set_Value (COLUMNNAME_JP_Table_EntityType, JP_Table_EntityType);
	}

	/** Get Entity Type of Table.
		@return Entity Type of Table	  */
	public String getJP_Table_EntityType () 
	{
		return (String)get_Value(COLUMNNAME_JP_Table_EntityType);
	}

	/** Set Help of Table.
		@param JP_Table_Help Help of Table	  */
	public void setJP_Table_Help (String JP_Table_Help)
	{
		set_Value (COLUMNNAME_JP_Table_Help, JP_Table_Help);
	}

	/** Get Help of Table.
		@return Help of Table	  */
	public String getJP_Table_Help () 
	{
		return (String)get_Value(COLUMNNAME_JP_Table_Help);
	}

	/** Set Table(Name).
		@param JP_Table_Name Table(Name)	  */
	public void setJP_Table_Name (String JP_Table_Name)
	{
		set_Value (COLUMNNAME_JP_Table_Name, JP_Table_Name);
	}

	/** Get Table(Name).
		@return Table(Name)	  */
	public String getJP_Table_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_Table_Name);
	}

	/** Set Dynamic Validation(Name).
		@param JP_Val_Rule_Name Dynamic Validation(Name)	  */
	public void setJP_Val_Rule_Name (String JP_Val_Rule_Name)
	{
		set_Value (COLUMNNAME_JP_Val_Rule_Name, JP_Val_Rule_Name);
	}

	/** Get Dynamic Validation(Name).
		@return Dynamic Validation(Name)	  */
	public String getJP_Val_Rule_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_Val_Rule_Name);
	}

	/** Set Window(Name).
		@param JP_Window_Name Window(Name)	  */
	public void setJP_Window_Name (String JP_Window_Name)
	{
		set_Value (COLUMNNAME_JP_Window_Name, JP_Window_Name);
	}

	/** Get Window(Name).
		@return Window(Name)	  */
	public String getJP_Window_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_Window_Name);
	}

	/** Set Mandatory Logic.
		@param MandatoryLogic Mandatory Logic	  */
	public void setMandatoryLogic (String MandatoryLogic)
	{
		set_Value (COLUMNNAME_MandatoryLogic, MandatoryLogic);
	}

	/** Get Mandatory Logic.
		@return Mandatory Logic	  */
	public String getMandatoryLogic () 
	{
		return (String)get_Value(COLUMNNAME_MandatoryLogic);
	}

	public org.compiere.model.I_PA_DashboardContent getPA_DashboardContent() throws RuntimeException
    {
		return (org.compiere.model.I_PA_DashboardContent)MTable.get(getCtx(), org.compiere.model.I_PA_DashboardContent.Table_Name)
			.getPO(getPA_DashboardContent_ID(), get_TrxName());	}

	/** Set Dashboard Content.
		@param PA_DashboardContent_ID Dashboard Content	  */
	public void setPA_DashboardContent_ID (int PA_DashboardContent_ID)
	{
		if (PA_DashboardContent_ID < 1) 
			set_Value (COLUMNNAME_PA_DashboardContent_ID, null);
		else 
			set_Value (COLUMNNAME_PA_DashboardContent_ID, Integer.valueOf(PA_DashboardContent_ID));
	}

	/** Get Dashboard Content.
		@return Dashboard Content	  */
	public int getPA_DashboardContent_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_PA_DashboardContent_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_Window getPO_Window() throws RuntimeException
    {
		return (org.compiere.model.I_AD_Window)MTable.get(getCtx(), org.compiere.model.I_AD_Window.Table_Name)
			.getPO(getPO_Window_ID(), get_TrxName());	}

	/** Set PO Window.
		@param PO_Window_ID 
		Purchase Order Window
	  */
	public void setPO_Window_ID (int PO_Window_ID)
	{
		if (PO_Window_ID < 1) 
			set_Value (COLUMNNAME_PO_Window_ID, null);
		else 
			set_Value (COLUMNNAME_PO_Window_ID, Integer.valueOf(PO_Window_ID));
	}

	/** Get PO Window.
		@return Purchase Order Window
	  */
	public int getPO_Window_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_PO_Window_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Processed.
		@param Processed 
		The document has been processed
	  */
	public void setProcessed (boolean Processed)
	{
		set_Value (COLUMNNAME_Processed, Boolean.valueOf(Processed));
	}

	/** Get Processed.
		@return The document has been processed
	  */
	public boolean isProcessed () 
	{
		Object oo = get_Value(COLUMNNAME_Processed);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Process Now.
		@param Processing Process Now	  */
	public void setProcessing (boolean Processing)
	{
		set_Value (COLUMNNAME_Processing, Boolean.valueOf(Processing));
	}

	/** Get Process Now.
		@return Process Now	  */
	public boolean isProcessing () 
	{
		Object oo = get_Value(COLUMNNAME_Processing);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Read Only Logic.
		@param ReadOnlyLogic 
		Logic to determine if field is read only (applies only when field is read-write)
	  */
	public void setReadOnlyLogic (String ReadOnlyLogic)
	{
		set_Value (COLUMNNAME_ReadOnlyLogic, ReadOnlyLogic);
	}

	/** Get Read Only Logic.
		@return Logic to determine if field is read only (applies only when field is read-write)
	  */
	public String getReadOnlyLogic () 
	{
		return (String)get_Value(COLUMNNAME_ReadOnlyLogic);
	}

	/** Set Sequence.
		@param SeqNo 
		Method of ordering records; lowest number comes first
	  */
	public void setSeqNo (int SeqNo)
	{
		set_Value (COLUMNNAME_SeqNo, Integer.valueOf(SeqNo));
	}

	/** Get Sequence.
		@return Method of ordering records; lowest number comes first
	  */
	public int getSeqNo () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_SeqNo);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Selection Column Sequence.
		@param SeqNoSelection 
		Selection Column Sequence
	  */
	public void setSeqNoSelection (int SeqNoSelection)
	{
		set_Value (COLUMNNAME_SeqNoSelection, Integer.valueOf(SeqNoSelection));
	}

	/** Get Selection Column Sequence.
		@return Selection Column Sequence
	  */
	public int getSeqNoSelection () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_SeqNoSelection);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set DB Table Name.
		@param TableName 
		Name of the table in the database
	  */
	public void setTableName (String TableName)
	{
		set_Value (COLUMNNAME_TableName, TableName);
	}

	/** Get DB Table Name.
		@return Name of the table in the database
	  */
	public String getTableName () 
	{
		return (String)get_Value(COLUMNNAME_TableName);
	}

	/** Set Value Format.
		@param VFormat 
		Format of the value; Can contain fixed format elements, Variables: "_lLoOaAcCa09"
	  */
	public void setVFormat (String VFormat)
	{
		set_Value (COLUMNNAME_VFormat, VFormat);
	}

	/** Get Value Format.
		@return Format of the value; Can contain fixed format elements, Variables: "_lLoOaAcCa09"
	  */
	public String getVFormat () 
	{
		return (String)get_Value(COLUMNNAME_VFormat);
	}

	/** Set Max. Value.
		@param ValueMax 
		Maximum Value for a field
	  */
	public void setValueMax (String ValueMax)
	{
		set_Value (COLUMNNAME_ValueMax, ValueMax);
	}

	/** Get Max. Value.
		@return Maximum Value for a field
	  */
	public String getValueMax () 
	{
		return (String)get_Value(COLUMNNAME_ValueMax);
	}

	/** Set Min. Value.
		@param ValueMin 
		Minimum Value for a field
	  */
	public void setValueMin (String ValueMin)
	{
		set_Value (COLUMNNAME_ValueMin, ValueMin);
	}

	/** Get Min. Value.
		@return Minimum Value for a field
	  */
	public String getValueMin () 
	{
		return (String)get_Value(COLUMNNAME_ValueMin);
	}

	/** Set Version.
		@param Version 
		Version of the table definition
	  */
	public void setVersion (BigDecimal Version)
	{
		set_Value (COLUMNNAME_Version, Version);
	}

	/** Get Version.
		@return Version of the table definition
	  */
	public BigDecimal getVersion () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Version);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}