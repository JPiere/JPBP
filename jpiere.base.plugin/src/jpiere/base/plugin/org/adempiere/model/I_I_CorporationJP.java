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

/** Generated Interface for I_CorporationJP
 *  @author iDempiere (generated) 
 *  @version Release 11
 */
@SuppressWarnings("all")
public interface I_I_CorporationJP 
{

    /** TableName=I_CorporationJP */
    public static final String Table_Name = "I_CorporationJP";

    /** AD_Table_ID=1000022 */
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

    /** Column name BPValue */
    public static final String COLUMNNAME_BPValue = "BPValue";

	/** Set BP Search Key.
	  * Business Partner Key Value
	  */
	public void setBPValue (String BPValue);

	/** Get BP Search Key.
	  * Business Partner Key Value
	  */
	public String getBPValue();

    /** Column name C_BPartner_ID */
    public static final String COLUMNNAME_C_BPartner_ID = "C_BPartner_ID";

	/** Set Business Partner.
	  * Identifies a Business Partner
	  */
	public void setC_BPartner_ID (int C_BPartner_ID);

	/** Get Business Partner.
	  * Identifies a Business Partner
	  */
	public int getC_BPartner_ID();

	public org.compiere.model.I_C_BPartner getC_BPartner() throws RuntimeException;

    /** Column name C_Greeting_ID */
    public static final String COLUMNNAME_C_Greeting_ID = "C_Greeting_ID";

	/** Set Greeting.
	  * Greeting to print on correspondence
	  */
	public void setC_Greeting_ID (int C_Greeting_ID);

	/** Get Greeting.
	  * Greeting to print on correspondence
	  */
	public int getC_Greeting_ID();

	public org.compiere.model.I_C_Greeting getC_Greeting() throws RuntimeException;

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

    /** Column name DUNS */
    public static final String COLUMNNAME_DUNS = "DUNS";

	/** Set D-U-N-S.
	  * Dun &amp;
 Bradstreet Number
	  */
	public void setDUNS (String DUNS);

	/** Get D-U-N-S.
	  * Dun &amp;
 Bradstreet Number
	  */
	public String getDUNS();

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

    /** Column name I_CorporationJP_ID */
    public static final String COLUMNNAME_I_CorporationJP_ID = "I_CorporationJP_ID";

	/** Set I_CorporationJP	  */
	public void setI_CorporationJP_ID (int I_CorporationJP_ID);

	/** Get I_CorporationJP	  */
	public int getI_CorporationJP_ID();

    /** Column name I_CorporationJP_UU */
    public static final String COLUMNNAME_I_CorporationJP_UU = "I_CorporationJP_UU";

	/** Set I_CorporationJP_UU	  */
	public void setI_CorporationJP_UU (String I_CorporationJP_UU);

	/** Get I_CorporationJP_UU	  */
	public String getI_CorporationJP_UU();

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

    /** Column name JP_CM_CorpType_ID */
    public static final String COLUMNNAME_JP_CM_CorpType_ID = "JP_CM_CorpType_ID";

	/** Set Consolidated Corp Type.
	  * JPIERE-0635:JPPS
	  */
	public void setJP_CM_CorpType_ID (int JP_CM_CorpType_ID);

	/** Get Consolidated Corp Type.
	  * JPIERE-0635:JPPS
	  */
	public int getJP_CM_CorpType_ID();

	public I_JP_CM_CorpType getJP_CM_CorpType() throws RuntimeException;

    /** Column name JP_CM_CorpType_Value */
    public static final String COLUMNNAME_JP_CM_CorpType_Value = "JP_CM_CorpType_Value";

	/** Set Consolidated Corp Type(Search Key).
	  * JPIERE-0635:JPPS
	  */
	public void setJP_CM_CorpType_Value (String JP_CM_CorpType_Value);

	/** Get Consolidated Corp Type(Search Key).
	  * JPIERE-0635:JPPS
	  */
	public String getJP_CM_CorpType_Value();

    /** Column name JP_Capital */
    public static final String COLUMNNAME_JP_Capital = "JP_Capital";

	/** Set Capital	  */
	public void setJP_Capital (BigDecimal JP_Capital);

	/** Get Capital	  */
	public BigDecimal getJP_Capital();

    /** Column name JP_CorpType_ID */
    public static final String COLUMNNAME_JP_CorpType_ID = "JP_CorpType_ID";

	/** Set Corp Type	  */
	public void setJP_CorpType_ID (int JP_CorpType_ID);

	/** Get Corp Type	  */
	public int getJP_CorpType_ID();

	public I_JP_CorpType getJP_CorpType() throws RuntimeException;

    /** Column name JP_CorpType_Value */
    public static final String COLUMNNAME_JP_CorpType_Value = "JP_CorpType_Value";

	/** Set Corp Type(Search Key)	  */
	public void setJP_CorpType_Value (String JP_CorpType_Value);

	/** Get Corp Type(Search Key)	  */
	public String getJP_CorpType_Value();

    /** Column name JP_Corporation_ID */
    public static final String COLUMNNAME_JP_Corporation_ID = "JP_Corporation_ID";

	/** Set Corporation	  */
	public void setJP_Corporation_ID (int JP_Corporation_ID);

	/** Get Corporation	  */
	public int getJP_Corporation_ID();

	public I_JP_Corporation getJP_Corporation() throws RuntimeException;

    /** Column name JP_Greeting_Name */
    public static final String COLUMNNAME_JP_Greeting_Name = "JP_Greeting_Name";

	/** Set Greeting(Name)	  */
	public void setJP_Greeting_Name (String JP_Greeting_Name);

	/** Get Greeting(Name)	  */
	public String getJP_Greeting_Name();

    /** Column name JP_IndustryType_ID */
    public static final String COLUMNNAME_JP_IndustryType_ID = "JP_IndustryType_ID";

	/** Set Industry Type	  */
	public void setJP_IndustryType_ID (int JP_IndustryType_ID);

	/** Get Industry Type	  */
	public int getJP_IndustryType_ID();

	public I_JP_IndustryType getJP_IndustryType() throws RuntimeException;

    /** Column name JP_IndustryType_Value */
    public static final String COLUMNNAME_JP_IndustryType_Value = "JP_IndustryType_Value";

	/** Set Industry Type(Search Key)	  */
	public void setJP_IndustryType_Value (String JP_IndustryType_Value);

	/** Get Industry Type(Search Key)	  */
	public String getJP_IndustryType_Value();

    /** Column name JP_Org_Value */
    public static final String COLUMNNAME_JP_Org_Value = "JP_Org_Value";

	/** Set Organization(Search Key)	  */
	public void setJP_Org_Value (String JP_Org_Value);

	/** Get Organization(Search Key)	  */
	public String getJP_Org_Value();

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

    /** Column name Name2 */
    public static final String COLUMNNAME_Name2 = "Name2";

	/** Set Name 2.
	  * Additional Name
	  */
	public void setName2 (String Name2);

	/** Get Name 2.
	  * Additional Name
	  */
	public String getName2();

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

    /** Column name URL */
    public static final String COLUMNNAME_URL = "URL";

	/** Set URL.
	  * Full URL address - e.g. http://www.idempiere.org
	  */
	public void setURL (String URL);

	/** Get URL.
	  * Full URL address - e.g. http://www.idempiere.org
	  */
	public String getURL();

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

    /** Column name Value */
    public static final String COLUMNNAME_Value = "Value";

	/** Set Search Key.
	  * Search key for the record in the format required - must be unique
	  */
	public void setValue (String Value);

	/** Get Search Key.
	  * Search key for the record in the format required - must be unique
	  */
	public String getValue();
}
