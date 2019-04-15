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

/** Generated Interface for I_TableColumnJP
 *  @author iDempiere (generated) 
 *  @version Release 6.2
 */
@SuppressWarnings("all")
public interface I_I_TableColumnJP 
{

    /** TableName=I_TableColumnJP */
    public static final String Table_Name = "I_TableColumnJP";

    /** AD_Table_ID=1000232 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 4 - System 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(4);

    /** Load Meta Data */

    /** Column name AD_Chart_ID */
    public static final String COLUMNNAME_AD_Chart_ID = "AD_Chart_ID";

	/** Set Chart	  */
	public void setAD_Chart_ID (int AD_Chart_ID);

	/** Get Chart	  */
	public int getAD_Chart_ID();

	public org.compiere.model.I_AD_Chart getAD_Chart() throws RuntimeException;

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Client.
	  * Client/Tenant for this installation.
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

    /** Column name AD_Element_ID */
    public static final String COLUMNNAME_AD_Element_ID = "AD_Element_ID";

	/** Set System Element.
	  * System Element enables the central maintenance of column description and help.
	  */
	public void setAD_Element_ID (int AD_Element_ID);

	/** Get System Element.
	  * System Element enables the central maintenance of column description and help.
	  */
	public int getAD_Element_ID();

	public org.compiere.model.I_AD_Element getAD_Element() throws RuntimeException;

    /** Column name AD_Language */
    public static final String COLUMNNAME_AD_Language = "AD_Language";

	/** Set Language.
	  * Language for this entity
	  */
	public void setAD_Language (String AD_Language);

	/** Get Language.
	  * Language for this entity
	  */
	public String getAD_Language();

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

    /** Column name AD_Process_ID */
    public static final String COLUMNNAME_AD_Process_ID = "AD_Process_ID";

	/** Set Process.
	  * Process or Report
	  */
	public void setAD_Process_ID (int AD_Process_ID);

	/** Get Process.
	  * Process or Report
	  */
	public int getAD_Process_ID();

	public org.compiere.model.I_AD_Process getAD_Process() throws RuntimeException;

    /** Column name AD_Reference_ID */
    public static final String COLUMNNAME_AD_Reference_ID = "AD_Reference_ID";

	/** Set Reference.
	  * System Reference and Validation
	  */
	public void setAD_Reference_ID (int AD_Reference_ID);

	/** Get Reference.
	  * System Reference and Validation
	  */
	public int getAD_Reference_ID();

	public org.compiere.model.I_AD_Reference getAD_Reference() throws RuntimeException;

    /** Column name AD_Reference_Value_ID */
    public static final String COLUMNNAME_AD_Reference_Value_ID = "AD_Reference_Value_ID";

	/** Set Reference Key.
	  * Required to specify, if data type is Table or List
	  */
	public void setAD_Reference_Value_ID (int AD_Reference_Value_ID);

	/** Get Reference Key.
	  * Required to specify, if data type is Table or List
	  */
	public int getAD_Reference_Value_ID();

	public org.compiere.model.I_AD_Reference getAD_Reference_Value() throws RuntimeException;

    /** Column name AD_Table_ID */
    public static final String COLUMNNAME_AD_Table_ID = "AD_Table_ID";

	/** Set Table.
	  * Database Table information
	  */
	public void setAD_Table_ID (int AD_Table_ID);

	/** Get Table.
	  * Database Table information
	  */
	public int getAD_Table_ID();

	public org.compiere.model.I_AD_Table getAD_Table() throws RuntimeException;

    /** Column name AD_Val_Rule_ID */
    public static final String COLUMNNAME_AD_Val_Rule_ID = "AD_Val_Rule_ID";

	/** Set Dynamic Validation.
	  * Dynamic Validation Rule
	  */
	public void setAD_Val_Rule_ID (int AD_Val_Rule_ID);

	/** Get Dynamic Validation.
	  * Dynamic Validation Rule
	  */
	public int getAD_Val_Rule_ID();

	public org.compiere.model.I_AD_Val_Rule getAD_Val_Rule() throws RuntimeException;

    /** Column name AD_Window_ID */
    public static final String COLUMNNAME_AD_Window_ID = "AD_Window_ID";

	/** Set Window.
	  * Data entry or display window
	  */
	public void setAD_Window_ID (int AD_Window_ID);

	/** Get Window.
	  * Data entry or display window
	  */
	public int getAD_Window_ID();

	public org.compiere.model.I_AD_Window getAD_Window() throws RuntimeException;

    /** Column name AccessLevel */
    public static final String COLUMNNAME_AccessLevel = "AccessLevel";

	/** Set Data Access Level.
	  * Access Level required
	  */
	public void setAccessLevel (String AccessLevel);

	/** Get Data Access Level.
	  * Access Level required
	  */
	public String getAccessLevel();

    /** Column name Callout */
    public static final String COLUMNNAME_Callout = "Callout";

	/** Set Callout.
	  * Fully qualified class names and method - separated by semicolons
	  */
	public void setCallout (String Callout);

	/** Get Callout.
	  * Fully qualified class names and method - separated by semicolons
	  */
	public String getCallout();

    /** Column name ColumnName */
    public static final String COLUMNNAME_ColumnName = "ColumnName";

	/** Set DB Column Name.
	  * Name of the column in the database
	  */
	public void setColumnName (String ColumnName);

	/** Get DB Column Name.
	  * Name of the column in the database
	  */
	public String getColumnName();

    /** Column name ColumnSQL */
    public static final String COLUMNNAME_ColumnSQL = "ColumnSQL";

	/** Set Column SQL.
	  * Virtual Column (r/o)
	  */
	public void setColumnSQL (String ColumnSQL);

	/** Get Column SQL.
	  * Virtual Column (r/o)
	  */
	public String getColumnSQL();

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

    /** Column name DefaultValue */
    public static final String COLUMNNAME_DefaultValue = "DefaultValue";

	/** Set Default Logic.
	  * Default value hierarchy, separated by ;

	  */
	public void setDefaultValue (String DefaultValue);

	/** Get Default Logic.
	  * Default value hierarchy, separated by ;

	  */
	public String getDefaultValue();

    /** Column name FKConstraintName */
    public static final String COLUMNNAME_FKConstraintName = "FKConstraintName";

	/** Set Constraint Name	  */
	public void setFKConstraintName (String FKConstraintName);

	/** Get Constraint Name	  */
	public String getFKConstraintName();

    /** Column name FKConstraintType */
    public static final String COLUMNNAME_FKConstraintType = "FKConstraintType";

	/** Set Constraint Type	  */
	public void setFKConstraintType (String FKConstraintType);

	/** Get Constraint Type	  */
	public String getFKConstraintType();

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

    /** Column name FormatPattern */
    public static final String COLUMNNAME_FormatPattern = "FormatPattern";

	/** Set Format Pattern.
	  * The pattern used to format a number or date.
	  */
	public void setFormatPattern (String FormatPattern);

	/** Get Format Pattern.
	  * The pattern used to format a number or date.
	  */
	public String getFormatPattern();

    /** Column name I_ErrorMsg */
    public static final String COLUMNNAME_I_ErrorMsg = "I_ErrorMsg";

	/** Set Import Error Message.
	  * Messages generated from import process
	  */
	public void setI_ErrorMsg (String I_ErrorMsg);

	/** Get Import Error Message.
	  * Messages generated from import process
	  */
	public String getI_ErrorMsg();

    /** Column name I_IsImported */
    public static final String COLUMNNAME_I_IsImported = "I_IsImported";

	/** Set Imported.
	  * Has this import been processed
	  */
	public void setI_IsImported (boolean I_IsImported);

	/** Get Imported.
	  * Has this import been processed
	  */
	public boolean isI_IsImported();

    /** Column name I_TableColumnJP_ID */
    public static final String COLUMNNAME_I_TableColumnJP_ID = "I_TableColumnJP_ID";

	/** Set I_TableColumnJP	  */
	public void setI_TableColumnJP_ID (int I_TableColumnJP_ID);

	/** Get I_TableColumnJP	  */
	public int getI_TableColumnJP_ID();

    /** Column name I_TableColumnJP_UU */
    public static final String COLUMNNAME_I_TableColumnJP_UU = "I_TableColumnJP_UU";

	/** Set I_TableColumnJP_UU	  */
	public void setI_TableColumnJP_UU (String I_TableColumnJP_UU);

	/** Get I_TableColumnJP_UU	  */
	public String getI_TableColumnJP_UU();

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

    /** Column name IsAllowCopy */
    public static final String COLUMNNAME_IsAllowCopy = "IsAllowCopy";

	/** Set Allow Copy.
	  * Determine if a column must be copied when pushing the button to copy record
	  */
	public void setIsAllowCopy (String IsAllowCopy);

	/** Get Allow Copy.
	  * Determine if a column must be copied when pushing the button to copy record
	  */
	public String getIsAllowCopy();

    /** Column name IsAllowLogging */
    public static final String COLUMNNAME_IsAllowLogging = "IsAllowLogging";

	/** Set Allow Logging.
	  * Determine if a column must be recorded into the change log
	  */
	public void setIsAllowLogging (String IsAllowLogging);

	/** Get Allow Logging.
	  * Determine if a column must be recorded into the change log
	  */
	public String getIsAllowLogging();

    /** Column name IsAlwaysUpdateable */
    public static final String COLUMNNAME_IsAlwaysUpdateable = "IsAlwaysUpdateable";

	/** Set Always Updatable.
	  * The column is always updateable, even if the record is not active or processed
	  */
	public void setIsAlwaysUpdateable (String IsAlwaysUpdateable);

	/** Get Always Updatable.
	  * The column is always updateable, even if the record is not active or processed
	  */
	public String getIsAlwaysUpdateable();

    /** Column name IsAutocomplete */
    public static final String COLUMNNAME_IsAutocomplete = "IsAutocomplete";

	/** Set Autocomplete.
	  * Automatic completion for textfields
	  */
	public void setIsAutocomplete (String IsAutocomplete);

	/** Get Autocomplete.
	  * Automatic completion for textfields
	  */
	public String getIsAutocomplete();

    /** Column name IsCentrallyMaintained */
    public static final String COLUMNNAME_IsCentrallyMaintained = "IsCentrallyMaintained";

	/** Set Centrally maintained.
	  * Information maintained in System Element table
	  */
	public void setIsCentrallyMaintained (String IsCentrallyMaintained);

	/** Get Centrally maintained.
	  * Information maintained in System Element table
	  */
	public String getIsCentrallyMaintained();

    /** Column name IsChangeLog */
    public static final String COLUMNNAME_IsChangeLog = "IsChangeLog";

	/** Set Maintain Change Log.
	  * Maintain a log of changes
	  */
	public void setIsChangeLog (String IsChangeLog);

	/** Get Maintain Change Log.
	  * Maintain a log of changes
	  */
	public String getIsChangeLog();

    /** Column name IsDeleteable */
    public static final String COLUMNNAME_IsDeleteable = "IsDeleteable";

	/** Set Records deletable.
	  * Indicates if records can be deleted from the database
	  */
	public void setIsDeleteable (String IsDeleteable);

	/** Get Records deletable.
	  * Indicates if records can be deleted from the database
	  */
	public String getIsDeleteable();

    /** Column name IsEncrypted */
    public static final String COLUMNNAME_IsEncrypted = "IsEncrypted";

	/** Set Encrypted.
	  * Display or Storage is encrypted
	  */
	public void setIsEncrypted (String IsEncrypted);

	/** Get Encrypted.
	  * Display or Storage is encrypted
	  */
	public String getIsEncrypted();

    /** Column name IsHighVolume */
    public static final String COLUMNNAME_IsHighVolume = "IsHighVolume";

	/** Set High Volume.
	  * Use Search instead of Pick list
	  */
	public void setIsHighVolume (String IsHighVolume);

	/** Get High Volume.
	  * Use Search instead of Pick list
	  */
	public String getIsHighVolume();

    /** Column name IsHtml */
    public static final String COLUMNNAME_IsHtml = "IsHtml";

	/** Set HTML.
	  * Text has HTML tags
	  */
	public void setIsHtml (String IsHtml);

	/** Get HTML.
	  * Text has HTML tags
	  */
	public String getIsHtml();

    /** Column name IsIdentifier */
    public static final String COLUMNNAME_IsIdentifier = "IsIdentifier";

	/** Set Identifier.
	  * This column is part of the record identifier
	  */
	public void setIsIdentifier (String IsIdentifier);

	/** Get Identifier.
	  * This column is part of the record identifier
	  */
	public String getIsIdentifier();

    /** Column name IsKey */
    public static final String COLUMNNAME_IsKey = "IsKey";

	/** Set Key column.
	  * This column is the key in this table
	  */
	public void setIsKey (String IsKey);

	/** Get Key column.
	  * This column is the key in this table
	  */
	public String getIsKey();

    /** Column name IsMandatory */
    public static final String COLUMNNAME_IsMandatory = "IsMandatory";

	/** Set Mandatory.
	  * Data entry is required in this column
	  */
	public void setIsMandatory (String IsMandatory);

	/** Get Mandatory.
	  * Data entry is required in this column
	  */
	public String getIsMandatory();

    /** Column name IsParent */
    public static final String COLUMNNAME_IsParent = "IsParent";

	/** Set Parent link column.
	  * This column is a link to the parent table (e.g. header from lines) - incl. Association key columns
	  */
	public void setIsParent (String IsParent);

	/** Get Parent link column.
	  * This column is a link to the parent table (e.g. header from lines) - incl. Association key columns
	  */
	public String getIsParent();

    /** Column name IsSecure */
    public static final String COLUMNNAME_IsSecure = "IsSecure";

	/** Set Secure content.
	  * Defines whether content must be treated as secure
	  */
	public void setIsSecure (String IsSecure);

	/** Get Secure content.
	  * Defines whether content must be treated as secure
	  */
	public String getIsSecure();

    /** Column name IsSelectionColumn */
    public static final String COLUMNNAME_IsSelectionColumn = "IsSelectionColumn";

	/** Set Selection Column.
	  * Is this column used for finding rows in windows
	  */
	public void setIsSelectionColumn (String IsSelectionColumn);

	/** Get Selection Column.
	  * Is this column used for finding rows in windows
	  */
	public String getIsSelectionColumn();

    /** Column name IsToolbarButton */
    public static final String COLUMNNAME_IsToolbarButton = "IsToolbarButton";

	/** Set Toolbar Button.
	  * Show the button on the toolbar, the window, or both
	  */
	public void setIsToolbarButton (String IsToolbarButton);

	/** Get Toolbar Button.
	  * Show the button on the toolbar, the window, or both
	  */
	public String getIsToolbarButton();

    /** Column name IsTranslated */
    public static final String COLUMNNAME_IsTranslated = "IsTranslated";

	/** Set Translated.
	  * This column is translated
	  */
	public void setIsTranslated (String IsTranslated);

	/** Get Translated.
	  * This column is translated
	  */
	public String getIsTranslated();

    /** Column name IsUpdateable */
    public static final String COLUMNNAME_IsUpdateable = "IsUpdateable";

	/** Set Updatable.
	  * Determines, if the field can be updated
	  */
	public void setIsUpdateable (String IsUpdateable);

	/** Get Updatable.
	  * Determines, if the field can be updated
	  */
	public String getIsUpdateable();

    /** Column name IsView */
    public static final String COLUMNNAME_IsView = "IsView";

	/** Set View.
	  * This is a view
	  */
	public void setIsView (String IsView);

	/** Get View.
	  * This is a view
	  */
	public String getIsView();

    /** Column name JP_Chart_Name */
    public static final String COLUMNNAME_JP_Chart_Name = "JP_Chart_Name";

	/** Set Chart(Name)	  */
	public void setJP_Chart_Name (String JP_Chart_Name);

	/** Get Chart(Name)	  */
	public String getJP_Chart_Name();

    /** Column name JP_Column_Description */
    public static final String COLUMNNAME_JP_Column_Description = "JP_Column_Description";

	/** Set Description of Column	  */
	public void setJP_Column_Description (String JP_Column_Description);

	/** Get Description of Column	  */
	public String getJP_Column_Description();

    /** Column name JP_Column_EntityType */
    public static final String COLUMNNAME_JP_Column_EntityType = "JP_Column_EntityType";

	/** Set Entity Type of Column	  */
	public void setJP_Column_EntityType (String JP_Column_EntityType);

	/** Get Entity Type of Column	  */
	public String getJP_Column_EntityType();

    /** Column name JP_Column_Help */
    public static final String COLUMNNAME_JP_Column_Help = "JP_Column_Help";

	/** Set Help of Column	  */
	public void setJP_Column_Help (String JP_Column_Help);

	/** Get Help of Column	  */
	public String getJP_Column_Help();

    /** Column name JP_Column_Name */
    public static final String COLUMNNAME_JP_Column_Name = "JP_Column_Name";

	/** Set Name of Column	  */
	public void setJP_Column_Name (String JP_Column_Name);

	/** Get Name of Column	  */
	public String getJP_Column_Name();

    /** Column name JP_Column_Placeholder */
    public static final String COLUMNNAME_JP_Column_Placeholder = "JP_Column_Placeholder";

	/** Set Placeholder of Column	  */
	public void setJP_Column_Placeholder (String JP_Column_Placeholder);

	/** Get Placeholder of Column	  */
	public String getJP_Column_Placeholder();

    /** Column name JP_DashboardContent_Name */
    public static final String COLUMNNAME_JP_DashboardContent_Name = "JP_DashboardContent_Name";

	/** Set Dashboard Content(Name)	  */
	public void setJP_DashboardContent_Name (String JP_DashboardContent_Name);

	/** Get Dashboard Content(Name)	  */
	public String getJP_DashboardContent_Name();

    /** Column name JP_Element_Description */
    public static final String COLUMNNAME_JP_Element_Description = "JP_Element_Description";

	/** Set Description of Element	  */
	public void setJP_Element_Description (String JP_Element_Description);

	/** Get Description of Element	  */
	public String getJP_Element_Description();

    /** Column name JP_Element_EntityType */
    public static final String COLUMNNAME_JP_Element_EntityType = "JP_Element_EntityType";

	/** Set Entity Type of Element	  */
	public void setJP_Element_EntityType (String JP_Element_EntityType);

	/** Get Entity Type of Element	  */
	public String getJP_Element_EntityType();

    /** Column name JP_Element_Help */
    public static final String COLUMNNAME_JP_Element_Help = "JP_Element_Help";

	/** Set Help of Element	  */
	public void setJP_Element_Help (String JP_Element_Help);

	/** Get Help of Element	  */
	public String getJP_Element_Help();

    /** Column name JP_Element_Name */
    public static final String COLUMNNAME_JP_Element_Name = "JP_Element_Name";

	/** Set Name of Element	  */
	public void setJP_Element_Name (String JP_Element_Name);

	/** Get Name of Element	  */
	public String getJP_Element_Name();

    /** Column name JP_Element_PO_Description */
    public static final String COLUMNNAME_JP_Element_PO_Description = "JP_Element_PO_Description";

	/** Set PO Description of Element	  */
	public void setJP_Element_PO_Description (String JP_Element_PO_Description);

	/** Get PO Description of Element	  */
	public String getJP_Element_PO_Description();

    /** Column name JP_Element_PO_Help */
    public static final String COLUMNNAME_JP_Element_PO_Help = "JP_Element_PO_Help";

	/** Set PO Help of Element	  */
	public void setJP_Element_PO_Help (String JP_Element_PO_Help);

	/** Get PO Help of Element	  */
	public String getJP_Element_PO_Help();

    /** Column name JP_Element_PO_Name */
    public static final String COLUMNNAME_JP_Element_PO_Name = "JP_Element_PO_Name";

	/** Set PO Name of Element	  */
	public void setJP_Element_PO_Name (String JP_Element_PO_Name);

	/** Get PO Name of Element	  */
	public String getJP_Element_PO_Name();

    /** Column name JP_Element_PO_PrintName */
    public static final String COLUMNNAME_JP_Element_PO_PrintName = "JP_Element_PO_PrintName";

	/** Set PO Print Name of Element	  */
	public void setJP_Element_PO_PrintName (String JP_Element_PO_PrintName);

	/** Get PO Print Name of Element	  */
	public String getJP_Element_PO_PrintName();

    /** Column name JP_Element_Placeholder */
    public static final String COLUMNNAME_JP_Element_Placeholder = "JP_Element_Placeholder";

	/** Set Placeholder of Element	  */
	public void setJP_Element_Placeholder (String JP_Element_Placeholder);

	/** Get Placeholder of Element	  */
	public String getJP_Element_Placeholder();

    /** Column name JP_Element_PrintName */
    public static final String COLUMNNAME_JP_Element_PrintName = "JP_Element_PrintName";

	/** Set PrintName of Element	  */
	public void setJP_Element_PrintName (String JP_Element_PrintName);

	/** Get PrintName of Element	  */
	public String getJP_Element_PrintName();

    /** Column name JP_Element_Trl_Description */
    public static final String COLUMNNAME_JP_Element_Trl_Description = "JP_Element_Trl_Description";

	/** Set Description of Element Trl	  */
	public void setJP_Element_Trl_Description (String JP_Element_Trl_Description);

	/** Get Description of Element Trl	  */
	public String getJP_Element_Trl_Description();

    /** Column name JP_Element_Trl_Help */
    public static final String COLUMNNAME_JP_Element_Trl_Help = "JP_Element_Trl_Help";

	/** Set Help of Element Trl	  */
	public void setJP_Element_Trl_Help (String JP_Element_Trl_Help);

	/** Get Help of Element Trl	  */
	public String getJP_Element_Trl_Help();

    /** Column name JP_Element_Trl_Name */
    public static final String COLUMNNAME_JP_Element_Trl_Name = "JP_Element_Trl_Name";

	/** Set Name of Element Trl	  */
	public void setJP_Element_Trl_Name (String JP_Element_Trl_Name);

	/** Get Name of Element Trl	  */
	public String getJP_Element_Trl_Name();

    /** Column name JP_Element_Trl_PO_Description */
    public static final String COLUMNNAME_JP_Element_Trl_PO_Description = "JP_Element_Trl_PO_Description";

	/** Set PO Description of Element Trl	  */
	public void setJP_Element_Trl_PO_Description (String JP_Element_Trl_PO_Description);

	/** Get PO Description of Element Trl	  */
	public String getJP_Element_Trl_PO_Description();

    /** Column name JP_Element_Trl_PO_Help */
    public static final String COLUMNNAME_JP_Element_Trl_PO_Help = "JP_Element_Trl_PO_Help";

	/** Set PO Help of Element Trl	  */
	public void setJP_Element_Trl_PO_Help (String JP_Element_Trl_PO_Help);

	/** Get PO Help of Element Trl	  */
	public String getJP_Element_Trl_PO_Help();

    /** Column name JP_Element_Trl_PO_Name */
    public static final String COLUMNNAME_JP_Element_Trl_PO_Name = "JP_Element_Trl_PO_Name";

	/** Set PO Name of Element Trl	  */
	public void setJP_Element_Trl_PO_Name (String JP_Element_Trl_PO_Name);

	/** Get PO Name of Element Trl	  */
	public String getJP_Element_Trl_PO_Name();

    /** Column name JP_Element_Trl_PO_PrintName */
    public static final String COLUMNNAME_JP_Element_Trl_PO_PrintName = "JP_Element_Trl_PO_PrintName";

	/** Set Print Name of Element Trl	  */
	public void setJP_Element_Trl_PO_PrintName (String JP_Element_Trl_PO_PrintName);

	/** Get Print Name of Element Trl	  */
	public String getJP_Element_Trl_PO_PrintName();

    /** Column name JP_Element_Trl_Placeholder */
    public static final String COLUMNNAME_JP_Element_Trl_Placeholder = "JP_Element_Trl_Placeholder";

	/** Set Placeholder of Element Trl	  */
	public void setJP_Element_Trl_Placeholder (String JP_Element_Trl_Placeholder);

	/** Get Placeholder of Element Trl	  */
	public String getJP_Element_Trl_Placeholder();

    /** Column name JP_Element_Trl_PrintName */
    public static final String COLUMNNAME_JP_Element_Trl_PrintName = "JP_Element_Trl_PrintName";

	/** Set Print Name of Element Trl	  */
	public void setJP_Element_Trl_PrintName (String JP_Element_Trl_PrintName);

	/** Get Print Name of Element Trl	  */
	public String getJP_Element_Trl_PrintName();

    /** Column name JP_PO_Window_Name */
    public static final String COLUMNNAME_JP_PO_Window_Name = "JP_PO_Window_Name";

	/** Set PO Window(Name)	  */
	public void setJP_PO_Window_Name (String JP_PO_Window_Name);

	/** Get PO Window(Name)	  */
	public String getJP_PO_Window_Name();

    /** Column name JP_Process_Value */
    public static final String COLUMNNAME_JP_Process_Value = "JP_Process_Value";

	/** Set Process(Value)	  */
	public void setJP_Process_Value (String JP_Process_Value);

	/** Get Process(Value)	  */
	public String getJP_Process_Value();

    /** Column name JP_Reference_Name */
    public static final String COLUMNNAME_JP_Reference_Name = "JP_Reference_Name";

	/** Set Reference(Name)	  */
	public void setJP_Reference_Name (String JP_Reference_Name);

	/** Get Reference(Name)	  */
	public String getJP_Reference_Name();

    /** Column name JP_Reference_Value_Name */
    public static final String COLUMNNAME_JP_Reference_Value_Name = "JP_Reference_Value_Name";

	/** Set Reference Key(Name)	  */
	public void setJP_Reference_Value_Name (String JP_Reference_Value_Name);

	/** Get Reference Key(Name)	  */
	public String getJP_Reference_Value_Name();

    /** Column name JP_Table_Description */
    public static final String COLUMNNAME_JP_Table_Description = "JP_Table_Description";

	/** Set Description of Table	  */
	public void setJP_Table_Description (String JP_Table_Description);

	/** Get Description of Table	  */
	public String getJP_Table_Description();

    /** Column name JP_Table_EntityType */
    public static final String COLUMNNAME_JP_Table_EntityType = "JP_Table_EntityType";

	/** Set Entity Type of Table	  */
	public void setJP_Table_EntityType (String JP_Table_EntityType);

	/** Get Entity Type of Table	  */
	public String getJP_Table_EntityType();

    /** Column name JP_Table_Help */
    public static final String COLUMNNAME_JP_Table_Help = "JP_Table_Help";

	/** Set Help of Table	  */
	public void setJP_Table_Help (String JP_Table_Help);

	/** Get Help of Table	  */
	public String getJP_Table_Help();

    /** Column name JP_Table_Name */
    public static final String COLUMNNAME_JP_Table_Name = "JP_Table_Name";

	/** Set Table(Name)	  */
	public void setJP_Table_Name (String JP_Table_Name);

	/** Get Table(Name)	  */
	public String getJP_Table_Name();

    /** Column name JP_Val_Rule_Name */
    public static final String COLUMNNAME_JP_Val_Rule_Name = "JP_Val_Rule_Name";

	/** Set Dynamic Validation(Name)	  */
	public void setJP_Val_Rule_Name (String JP_Val_Rule_Name);

	/** Get Dynamic Validation(Name)	  */
	public String getJP_Val_Rule_Name();

    /** Column name JP_Window_Name */
    public static final String COLUMNNAME_JP_Window_Name = "JP_Window_Name";

	/** Set Window(Name)	  */
	public void setJP_Window_Name (String JP_Window_Name);

	/** Get Window(Name)	  */
	public String getJP_Window_Name();

    /** Column name MandatoryLogic */
    public static final String COLUMNNAME_MandatoryLogic = "MandatoryLogic";

	/** Set Mandatory Logic	  */
	public void setMandatoryLogic (String MandatoryLogic);

	/** Get Mandatory Logic	  */
	public String getMandatoryLogic();

    /** Column name PA_DashboardContent_ID */
    public static final String COLUMNNAME_PA_DashboardContent_ID = "PA_DashboardContent_ID";

	/** Set Dashboard Content	  */
	public void setPA_DashboardContent_ID (int PA_DashboardContent_ID);

	/** Get Dashboard Content	  */
	public int getPA_DashboardContent_ID();

	public org.compiere.model.I_PA_DashboardContent getPA_DashboardContent() throws RuntimeException;

    /** Column name PO_Window_ID */
    public static final String COLUMNNAME_PO_Window_ID = "PO_Window_ID";

	/** Set PO Window.
	  * Purchase Order Window
	  */
	public void setPO_Window_ID (int PO_Window_ID);

	/** Get PO Window.
	  * Purchase Order Window
	  */
	public int getPO_Window_ID();

	public org.compiere.model.I_AD_Window getPO_Window() throws RuntimeException;

    /** Column name Processed */
    public static final String COLUMNNAME_Processed = "Processed";

	/** Set Processed.
	  * The document has been processed
	  */
	public void setProcessed (boolean Processed);

	/** Get Processed.
	  * The document has been processed
	  */
	public boolean isProcessed();

    /** Column name Processing */
    public static final String COLUMNNAME_Processing = "Processing";

	/** Set Process Now	  */
	public void setProcessing (boolean Processing);

	/** Get Process Now	  */
	public boolean isProcessing();

    /** Column name ReadOnlyLogic */
    public static final String COLUMNNAME_ReadOnlyLogic = "ReadOnlyLogic";

	/** Set Read Only Logic.
	  * Logic to determine if field is read only (applies only when field is read-write)
	  */
	public void setReadOnlyLogic (String ReadOnlyLogic);

	/** Get Read Only Logic.
	  * Logic to determine if field is read only (applies only when field is read-write)
	  */
	public String getReadOnlyLogic();

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

    /** Column name SeqNoSelection */
    public static final String COLUMNNAME_SeqNoSelection = "SeqNoSelection";

	/** Set Selection Column Sequence.
	  * Selection Column Sequence
	  */
	public void setSeqNoSelection (int SeqNoSelection);

	/** Get Selection Column Sequence.
	  * Selection Column Sequence
	  */
	public int getSeqNoSelection();

    /** Column name TableName */
    public static final String COLUMNNAME_TableName = "TableName";

	/** Set DB Table Name.
	  * Name of the table in the database
	  */
	public void setTableName (String TableName);

	/** Get DB Table Name.
	  * Name of the table in the database
	  */
	public String getTableName();

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

    /** Column name VFormat */
    public static final String COLUMNNAME_VFormat = "VFormat";

	/** Set Value Format.
	  * Format of the value;
 Can contain fixed format elements, Variables: "_lLoOaAcCa09"
	  */
	public void setVFormat (String VFormat);

	/** Get Value Format.
	  * Format of the value;
 Can contain fixed format elements, Variables: "_lLoOaAcCa09"
	  */
	public String getVFormat();

    /** Column name ValueMax */
    public static final String COLUMNNAME_ValueMax = "ValueMax";

	/** Set Max. Value.
	  * Maximum Value for a field
	  */
	public void setValueMax (String ValueMax);

	/** Get Max. Value.
	  * Maximum Value for a field
	  */
	public String getValueMax();

    /** Column name ValueMin */
    public static final String COLUMNNAME_ValueMin = "ValueMin";

	/** Set Min. Value.
	  * Minimum Value for a field
	  */
	public void setValueMin (String ValueMin);

	/** Get Min. Value.
	  * Minimum Value for a field
	  */
	public String getValueMin();

    /** Column name Version */
    public static final String COLUMNNAME_Version = "Version";

	/** Set Version.
	  * Version of the table definition
	  */
	public void setVersion (BigDecimal Version);

	/** Get Version.
	  * Version of the table definition
	  */
	public BigDecimal getVersion();
}
