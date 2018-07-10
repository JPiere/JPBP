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

/** Generated Interface for I_GLJournalJP
 *  @author iDempiere (generated) 
 *  @version Release 5.1
 */
@SuppressWarnings("all")
public interface I_I_GLJournalJP 
{

    /** TableName=I_GLJournalJP */
    public static final String Table_Name = "I_GLJournalJP";

    /** AD_Table_ID=1000218 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 3 - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(3);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Client.
	  * Client/Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_OrgTrx_ID */
    public static final String COLUMNNAME_AD_OrgTrx_ID = "AD_OrgTrx_ID";

	/** Set Trx Organization.
	  * Performing or initiating organization
	  */
	public void setAD_OrgTrx_ID (int AD_OrgTrx_ID);

	/** Get Trx Organization.
	  * Performing or initiating organization
	  */
	public int getAD_OrgTrx_ID();

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

    /** Column name A_Asset_ID */
    public static final String COLUMNNAME_A_Asset_ID = "A_Asset_ID";

	/** Set Asset.
	  * Asset used internally or by customers
	  */
	public void setA_Asset_ID (int A_Asset_ID);

	/** Get Asset.
	  * Asset used internally or by customers
	  */
	public int getA_Asset_ID();

	public org.compiere.model.I_A_Asset getA_Asset() throws RuntimeException;

    /** Column name Account_ID */
    public static final String COLUMNNAME_Account_ID = "Account_ID";

	/** Set Account.
	  * Account used
	  */
	public void setAccount_ID (int Account_ID);

	/** Get Account.
	  * Account used
	  */
	public int getAccount_ID();

	public org.compiere.model.I_C_ElementValue getAccount() throws RuntimeException;

    /** Column name AmtAcctCr */
    public static final String COLUMNNAME_AmtAcctCr = "AmtAcctCr";

	/** Set Accounted Credit.
	  * Accounted Credit Amount
	  */
	public void setAmtAcctCr (BigDecimal AmtAcctCr);

	/** Get Accounted Credit.
	  * Accounted Credit Amount
	  */
	public BigDecimal getAmtAcctCr();

    /** Column name AmtAcctDr */
    public static final String COLUMNNAME_AmtAcctDr = "AmtAcctDr";

	/** Set Accounted Debit.
	  * Accounted Debit Amount
	  */
	public void setAmtAcctDr (BigDecimal AmtAcctDr);

	/** Get Accounted Debit.
	  * Accounted Debit Amount
	  */
	public BigDecimal getAmtAcctDr();

    /** Column name AmtSourceCr */
    public static final String COLUMNNAME_AmtSourceCr = "AmtSourceCr";

	/** Set Source Credit.
	  * Source Credit Amount
	  */
	public void setAmtSourceCr (BigDecimal AmtSourceCr);

	/** Get Source Credit.
	  * Source Credit Amount
	  */
	public BigDecimal getAmtSourceCr();

    /** Column name AmtSourceDr */
    public static final String COLUMNNAME_AmtSourceDr = "AmtSourceDr";

	/** Set Source Debit.
	  * Source Debit Amount
	  */
	public void setAmtSourceDr (BigDecimal AmtSourceDr);

	/** Get Source Debit.
	  * Source Debit Amount
	  */
	public BigDecimal getAmtSourceDr();

    /** Column name C_AcctSchema_ID */
    public static final String COLUMNNAME_C_AcctSchema_ID = "C_AcctSchema_ID";

	/** Set Accounting Schema.
	  * Rules for accounting
	  */
	public void setC_AcctSchema_ID (int C_AcctSchema_ID);

	/** Get Accounting Schema.
	  * Rules for accounting
	  */
	public int getC_AcctSchema_ID();

	public org.compiere.model.I_C_AcctSchema getC_AcctSchema() throws RuntimeException;

    /** Column name C_Activity_ID */
    public static final String COLUMNNAME_C_Activity_ID = "C_Activity_ID";

	/** Set Activity.
	  * Business Activity
	  */
	public void setC_Activity_ID (int C_Activity_ID);

	/** Get Activity.
	  * Business Activity
	  */
	public int getC_Activity_ID();

	public org.compiere.model.I_C_Activity getC_Activity() throws RuntimeException;

    /** Column name C_BPartner_ID */
    public static final String COLUMNNAME_C_BPartner_ID = "C_BPartner_ID";

	/** Set Business Partner .
	  * Identifies a Business Partner
	  */
	public void setC_BPartner_ID (int C_BPartner_ID);

	/** Get Business Partner .
	  * Identifies a Business Partner
	  */
	public int getC_BPartner_ID();

	public org.compiere.model.I_C_BPartner getC_BPartner() throws RuntimeException;

    /** Column name C_Campaign_ID */
    public static final String COLUMNNAME_C_Campaign_ID = "C_Campaign_ID";

	/** Set Campaign.
	  * Marketing Campaign
	  */
	public void setC_Campaign_ID (int C_Campaign_ID);

	/** Get Campaign.
	  * Marketing Campaign
	  */
	public int getC_Campaign_ID();

	public org.compiere.model.I_C_Campaign getC_Campaign() throws RuntimeException;

    /** Column name C_ConversionType_ID */
    public static final String COLUMNNAME_C_ConversionType_ID = "C_ConversionType_ID";

	/** Set Currency Type.
	  * Currency Conversion Rate Type
	  */
	public void setC_ConversionType_ID (int C_ConversionType_ID);

	/** Get Currency Type.
	  * Currency Conversion Rate Type
	  */
	public int getC_ConversionType_ID();

	public org.compiere.model.I_C_ConversionType getC_ConversionType() throws RuntimeException;

    /** Column name C_Currency_ID */
    public static final String COLUMNNAME_C_Currency_ID = "C_Currency_ID";

	/** Set Currency.
	  * The Currency for this record
	  */
	public void setC_Currency_ID (int C_Currency_ID);

	/** Get Currency.
	  * The Currency for this record
	  */
	public int getC_Currency_ID();

	public org.compiere.model.I_C_Currency getC_Currency() throws RuntimeException;

    /** Column name C_DocType_ID */
    public static final String COLUMNNAME_C_DocType_ID = "C_DocType_ID";

	/** Set Document Type.
	  * Document type or rules
	  */
	public void setC_DocType_ID (int C_DocType_ID);

	/** Get Document Type.
	  * Document type or rules
	  */
	public int getC_DocType_ID();

	public org.compiere.model.I_C_DocType getC_DocType() throws RuntimeException;

    /** Column name C_LocFrom_ID */
    public static final String COLUMNNAME_C_LocFrom_ID = "C_LocFrom_ID";

	/** Set Location From.
	  * Location that inventory was moved from
	  */
	public void setC_LocFrom_ID (int C_LocFrom_ID);

	/** Get Location From.
	  * Location that inventory was moved from
	  */
	public int getC_LocFrom_ID();

	public org.compiere.model.I_C_Location getC_LocFrom() throws RuntimeException;

    /** Column name C_LocTo_ID */
    public static final String COLUMNNAME_C_LocTo_ID = "C_LocTo_ID";

	/** Set Location To.
	  * Location that inventory was moved to
	  */
	public void setC_LocTo_ID (int C_LocTo_ID);

	/** Get Location To.
	  * Location that inventory was moved to
	  */
	public int getC_LocTo_ID();

	public org.compiere.model.I_C_Location getC_LocTo() throws RuntimeException;

    /** Column name C_Period_ID */
    public static final String COLUMNNAME_C_Period_ID = "C_Period_ID";

	/** Set Period.
	  * Period of the Calendar
	  */
	public void setC_Period_ID (int C_Period_ID);

	/** Get Period.
	  * Period of the Calendar
	  */
	public int getC_Period_ID();

	public org.compiere.model.I_C_Period getC_Period() throws RuntimeException;

    /** Column name C_ProjectPhase_ID */
    public static final String COLUMNNAME_C_ProjectPhase_ID = "C_ProjectPhase_ID";

	/** Set Project Phase.
	  * Phase of a Project
	  */
	public void setC_ProjectPhase_ID (int C_ProjectPhase_ID);

	/** Get Project Phase.
	  * Phase of a Project
	  */
	public int getC_ProjectPhase_ID();

	public org.compiere.model.I_C_ProjectPhase getC_ProjectPhase() throws RuntimeException;

    /** Column name C_ProjectTask_ID */
    public static final String COLUMNNAME_C_ProjectTask_ID = "C_ProjectTask_ID";

	/** Set Project Task.
	  * Actual Project Task in a Phase
	  */
	public void setC_ProjectTask_ID (int C_ProjectTask_ID);

	/** Get Project Task.
	  * Actual Project Task in a Phase
	  */
	public int getC_ProjectTask_ID();

	public org.compiere.model.I_C_ProjectTask getC_ProjectTask() throws RuntimeException;

    /** Column name C_Project_ID */
    public static final String COLUMNNAME_C_Project_ID = "C_Project_ID";

	/** Set Project.
	  * Financial Project
	  */
	public void setC_Project_ID (int C_Project_ID);

	/** Get Project.
	  * Financial Project
	  */
	public int getC_Project_ID();

	public org.compiere.model.I_C_Project getC_Project() throws RuntimeException;

    /** Column name C_SalesRegion_ID */
    public static final String COLUMNNAME_C_SalesRegion_ID = "C_SalesRegion_ID";

	/** Set Sales Region.
	  * Sales coverage region
	  */
	public void setC_SalesRegion_ID (int C_SalesRegion_ID);

	/** Get Sales Region.
	  * Sales coverage region
	  */
	public int getC_SalesRegion_ID();

	public org.compiere.model.I_C_SalesRegion getC_SalesRegion() throws RuntimeException;

    /** Column name C_SubAcct_ID */
    public static final String COLUMNNAME_C_SubAcct_ID = "C_SubAcct_ID";

	/** Set Sub Account.
	  * Sub account for Element Value
	  */
	public void setC_SubAcct_ID (int C_SubAcct_ID);

	/** Get Sub Account.
	  * Sub account for Element Value
	  */
	public int getC_SubAcct_ID();

	public org.compiere.model.I_C_SubAcct getC_SubAcct() throws RuntimeException;

    /** Column name C_Tax_ID */
    public static final String COLUMNNAME_C_Tax_ID = "C_Tax_ID";

	/** Set Tax.
	  * Tax identifier
	  */
	public void setC_Tax_ID (int C_Tax_ID);

	/** Get Tax.
	  * Tax identifier
	  */
	public int getC_Tax_ID();

	public org.compiere.model.I_C_Tax getC_Tax() throws RuntimeException;

    /** Column name C_UOM_ID */
    public static final String COLUMNNAME_C_UOM_ID = "C_UOM_ID";

	/** Set UOM.
	  * Unit of Measure
	  */
	public void setC_UOM_ID (int C_UOM_ID);

	/** Get UOM.
	  * Unit of Measure
	  */
	public int getC_UOM_ID();

	public org.compiere.model.I_C_UOM getC_UOM() throws RuntimeException;

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

    /** Column name CurrencyRate */
    public static final String COLUMNNAME_CurrencyRate = "CurrencyRate";

	/** Set Rate.
	  * Currency Conversion Rate
	  */
	public void setCurrencyRate (BigDecimal CurrencyRate);

	/** Get Rate.
	  * Currency Conversion Rate
	  */
	public BigDecimal getCurrencyRate();

    /** Column name DateAcct */
    public static final String COLUMNNAME_DateAcct = "DateAcct";

	/** Set Account Date.
	  * Accounting Date
	  */
	public void setDateAcct (Timestamp DateAcct);

	/** Get Account Date.
	  * Accounting Date
	  */
	public Timestamp getDateAcct();

    /** Column name DateTrx */
    public static final String COLUMNNAME_DateTrx = "DateTrx";

	/** Set Transaction Date.
	  * Transaction Date
	  */
	public void setDateTrx (Timestamp DateTrx);

	/** Get Transaction Date.
	  * Transaction Date
	  */
	public Timestamp getDateTrx();

    /** Column name DocumentNo */
    public static final String COLUMNNAME_DocumentNo = "DocumentNo";

	/** Set Document No.
	  * Document sequence number of the document
	  */
	public void setDocumentNo (String DocumentNo);

	/** Get Document No.
	  * Document sequence number of the document
	  */
	public String getDocumentNo();

    /** Column name GL_Budget_ID */
    public static final String COLUMNNAME_GL_Budget_ID = "GL_Budget_ID";

	/** Set Budget.
	  * General Ledger Budget
	  */
	public void setGL_Budget_ID (int GL_Budget_ID);

	/** Get Budget.
	  * General Ledger Budget
	  */
	public int getGL_Budget_ID();

	public org.compiere.model.I_GL_Budget getGL_Budget() throws RuntimeException;

    /** Column name GL_Category_ID */
    public static final String COLUMNNAME_GL_Category_ID = "GL_Category_ID";

	/** Set GL Category.
	  * General Ledger Category
	  */
	public void setGL_Category_ID (int GL_Category_ID);

	/** Get GL Category.
	  * General Ledger Category
	  */
	public int getGL_Category_ID();

	public org.compiere.model.I_GL_Category getGL_Category() throws RuntimeException;

    /** Column name GL_JournalLine_ID */
    public static final String COLUMNNAME_GL_JournalLine_ID = "GL_JournalLine_ID";

	/** Set Journal Line.
	  * General Ledger Journal Line
	  */
	public void setGL_JournalLine_ID (int GL_JournalLine_ID);

	/** Get Journal Line.
	  * General Ledger Journal Line
	  */
	public int getGL_JournalLine_ID();

	public org.compiere.model.I_GL_JournalLine getGL_JournalLine() throws RuntimeException;

    /** Column name GL_Journal_ID */
    public static final String COLUMNNAME_GL_Journal_ID = "GL_Journal_ID";

	/** Set Journal.
	  * General Ledger Journal
	  */
	public void setGL_Journal_ID (int GL_Journal_ID);

	/** Get Journal.
	  * General Ledger Journal
	  */
	public int getGL_Journal_ID();

	public org.compiere.model.I_GL_Journal getGL_Journal() throws RuntimeException;

    /** Column name ISO_Code */
    public static final String COLUMNNAME_ISO_Code = "ISO_Code";

	/** Set ISO Currency Code.
	  * Three letter ISO 4217 Code of the Currency
	  */
	public void setISO_Code (String ISO_Code);

	/** Get ISO Currency Code.
	  * Three letter ISO 4217 Code of the Currency
	  */
	public String getISO_Code();

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

    /** Column name I_GLJournalJP_ID */
    public static final String COLUMNNAME_I_GLJournalJP_ID = "I_GLJournalJP_ID";

	/** Set I_GLJournalJP	  */
	public void setI_GLJournalJP_ID (int I_GLJournalJP_ID);

	/** Get I_GLJournalJP	  */
	public int getI_GLJournalJP_ID();

    /** Column name I_GLJournalJP_UU */
    public static final String COLUMNNAME_I_GLJournalJP_UU = "I_GLJournalJP_UU";

	/** Set I_GLJournalJP_UU	  */
	public void setI_GLJournalJP_UU (String I_GLJournalJP_UU);

	/** Get I_GLJournalJP_UU	  */
	public String getI_GLJournalJP_UU();

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

    /** Column name JP_AcctSchema_Name */
    public static final String COLUMNNAME_JP_AcctSchema_Name = "JP_AcctSchema_Name";

	/** Set Accounting Schema(Name)	  */
	public void setJP_AcctSchema_Name (String JP_AcctSchema_Name);

	/** Get Accounting Schema(Name)	  */
	public String getJP_AcctSchema_Name();

    /** Column name JP_Activity_Value */
    public static final String COLUMNNAME_JP_Activity_Value = "JP_Activity_Value";

	/** Set Activity(Search Key)	  */
	public void setJP_Activity_Value (String JP_Activity_Value);

	/** Get Activity(Search Key)	  */
	public String getJP_Activity_Value();

    /** Column name JP_Asset_Value */
    public static final String COLUMNNAME_JP_Asset_Value = "JP_Asset_Value";

	/** Set Asset(Search Key)	  */
	public void setJP_Asset_Value (String JP_Asset_Value);

	/** Get Asset(Search Key)	  */
	public String getJP_Asset_Value();

    /** Column name JP_BPartner_Value */
    public static final String COLUMNNAME_JP_BPartner_Value = "JP_BPartner_Value";

	/** Set Business Partner(Search Key)	  */
	public void setJP_BPartner_Value (String JP_BPartner_Value);

	/** Get Business Partner(Search Key)	  */
	public String getJP_BPartner_Value();

    /** Column name JP_Campaign_Value */
    public static final String COLUMNNAME_JP_Campaign_Value = "JP_Campaign_Value";

	/** Set Campaign(Search Key)	  */
	public void setJP_Campaign_Value (String JP_Campaign_Value);

	/** Get Campaign(Search Key)	  */
	public String getJP_Campaign_Value();

    /** Column name JP_ContractContent_DocNo */
    public static final String COLUMNNAME_JP_ContractContent_DocNo = "JP_ContractContent_DocNo";

	/** Set Contract Content(Document No)	  */
	public void setJP_ContractContent_DocNo (String JP_ContractContent_DocNo);

	/** Get Contract Content(Document No)	  */
	public String getJP_ContractContent_DocNo();

    /** Column name JP_ContractContent_ID */
    public static final String COLUMNNAME_JP_ContractContent_ID = "JP_ContractContent_ID";

	/** Set Contract Content	  */
	public void setJP_ContractContent_ID (int JP_ContractContent_ID);

	/** Get Contract Content	  */
	public int getJP_ContractContent_ID();

	public I_JP_ContractContent getJP_ContractContent() throws RuntimeException;

    /** Column name JP_ConversionType_Value */
    public static final String COLUMNNAME_JP_ConversionType_Value = "JP_ConversionType_Value";

	/** Set Conversion Type(Search key)	  */
	public void setJP_ConversionType_Value (String JP_ConversionType_Value);

	/** Get Conversion Type(Search key)	  */
	public String getJP_ConversionType_Value();

    /** Column name JP_DataMigration_Identifier */
    public static final String COLUMNNAME_JP_DataMigration_Identifier = "JP_DataMigration_Identifier";

	/** Set Data Migration Identifier	  */
	public void setJP_DataMigration_Identifier (String JP_DataMigration_Identifier);

	/** Get Data Migration Identifier	  */
	public String getJP_DataMigration_Identifier();

    /** Column name JP_Description_Header */
    public static final String COLUMNNAME_JP_Description_Header = "JP_Description_Header";

	/** Set Description(Header)	  */
	public void setJP_Description_Header (String JP_Description_Header);

	/** Get Description(Header)	  */
	public String getJP_Description_Header();

    /** Column name JP_Description_Line */
    public static final String COLUMNNAME_JP_Description_Line = "JP_Description_Line";

	/** Set Description(Line)	  */
	public void setJP_Description_Line (String JP_Description_Line);

	/** Get Description(Line)	  */
	public String getJP_Description_Line();

    /** Column name JP_DocType_Name */
    public static final String COLUMNNAME_JP_DocType_Name = "JP_DocType_Name";

	/** Set Document Type(Name)	  */
	public void setJP_DocType_Name (String JP_DocType_Name);

	/** Get Document Type(Name)	  */
	public String getJP_DocType_Name();

    /** Column name JP_ElementValue_Value */
    public static final String COLUMNNAME_JP_ElementValue_Value = "JP_ElementValue_Value";

	/** Set Account Element(Search Key)	  */
	public void setJP_ElementValue_Value (String JP_ElementValue_Value);

	/** Get Account Element(Search Key)	  */
	public String getJP_ElementValue_Value();

    /** Column name JP_GL_Budget_Name */
    public static final String COLUMNNAME_JP_GL_Budget_Name = "JP_GL_Budget_Name";

	/** Set Budget(Name)	  */
	public void setJP_GL_Budget_Name (String JP_GL_Budget_Name);

	/** Get Budget(Name)	  */
	public String getJP_GL_Budget_Name();

    /** Column name JP_GL_Category_Name */
    public static final String COLUMNNAME_JP_GL_Category_Name = "JP_GL_Category_Name";

	/** Set GL Category(Name)	  */
	public void setJP_GL_Category_Name (String JP_GL_Category_Name);

	/** Get GL Category(Name)	  */
	public String getJP_GL_Category_Name();

    /** Column name JP_LocFrom_Label */
    public static final String COLUMNNAME_JP_LocFrom_Label = "JP_LocFrom_Label";

	/** Set Location From(Label)	  */
	public void setJP_LocFrom_Label (String JP_LocFrom_Label);

	/** Get Location From(Label)	  */
	public String getJP_LocFrom_Label();

    /** Column name JP_LocTo_Label */
    public static final String COLUMNNAME_JP_LocTo_Label = "JP_LocTo_Label";

	/** Set Location To(Label)	  */
	public void setJP_LocTo_Label (String JP_LocTo_Label);

	/** Get Location To(Label)	  */
	public String getJP_LocTo_Label();

    /** Column name JP_Locator_Value */
    public static final String COLUMNNAME_JP_Locator_Value = "JP_Locator_Value";

	/** Set Locator(Search Key).
	  * Warehouse Locator
	  */
	public void setJP_Locator_Value (String JP_Locator_Value);

	/** Get Locator(Search Key).
	  * Warehouse Locator
	  */
	public String getJP_Locator_Value();

    /** Column name JP_Order_DocumentNo */
    public static final String COLUMNNAME_JP_Order_DocumentNo = "JP_Order_DocumentNo";

	/** Set JP_Order_DocumentNo	  */
	public void setJP_Order_DocumentNo (String JP_Order_DocumentNo);

	/** Get JP_Order_DocumentNo	  */
	public String getJP_Order_DocumentNo();

    /** Column name JP_Order_ID */
    public static final String COLUMNNAME_JP_Order_ID = "JP_Order_ID";

	/** Set Sales Order	  */
	public void setJP_Order_ID (int JP_Order_ID);

	/** Get Sales Order	  */
	public int getJP_Order_ID();

	public org.compiere.model.I_C_Order getJP_Order() throws RuntimeException;

    /** Column name JP_OrgTrx_Value */
    public static final String COLUMNNAME_JP_OrgTrx_Value = "JP_OrgTrx_Value";

	/** Set Trx Organization(Search Key)	  */
	public void setJP_OrgTrx_Value (String JP_OrgTrx_Value);

	/** Get Trx Organization(Search Key)	  */
	public String getJP_OrgTrx_Value();

    /** Column name JP_Org_Value */
    public static final String COLUMNNAME_JP_Org_Value = "JP_Org_Value";

	/** Set Organization(Search Key)	  */
	public void setJP_Org_Value (String JP_Org_Value);

	/** Get Organization(Search Key)	  */
	public String getJP_Org_Value();

    /** Column name JP_Product_Value */
    public static final String COLUMNNAME_JP_Product_Value = "JP_Product_Value";

	/** Set Product(Search Key)	  */
	public void setJP_Product_Value (String JP_Product_Value);

	/** Get Product(Search Key)	  */
	public String getJP_Product_Value();

    /** Column name JP_ProjectPhase_Name */
    public static final String COLUMNNAME_JP_ProjectPhase_Name = "JP_ProjectPhase_Name";

	/** Set Project Phase(Name)	  */
	public void setJP_ProjectPhase_Name (String JP_ProjectPhase_Name);

	/** Get Project Phase(Name)	  */
	public String getJP_ProjectPhase_Name();

    /** Column name JP_ProjectTask_Name */
    public static final String COLUMNNAME_JP_ProjectTask_Name = "JP_ProjectTask_Name";

	/** Set Project Task(Name)	  */
	public void setJP_ProjectTask_Name (String JP_ProjectTask_Name);

	/** Get Project Task(Name)	  */
	public String getJP_ProjectTask_Name();

    /** Column name JP_Project_Value */
    public static final String COLUMNNAME_JP_Project_Value = "JP_Project_Value";

	/** Set Project(Search Key)	  */
	public void setJP_Project_Value (String JP_Project_Value);

	/** Get Project(Search Key)	  */
	public String getJP_Project_Value();

    /** Column name JP_SalesRegion_Value */
    public static final String COLUMNNAME_JP_SalesRegion_Value = "JP_SalesRegion_Value";

	/** Set Sales Region(Search Key).
	  * Sales coverage region
	  */
	public void setJP_SalesRegion_Value (String JP_SalesRegion_Value);

	/** Get Sales Region(Search Key).
	  * Sales coverage region
	  */
	public String getJP_SalesRegion_Value();

    /** Column name JP_SubAcct_Value */
    public static final String COLUMNNAME_JP_SubAcct_Value = "JP_SubAcct_Value";

	/** Set Sub Account(Search Key)	  */
	public void setJP_SubAcct_Value (String JP_SubAcct_Value);

	/** Get Sub Account(Search Key)	  */
	public String getJP_SubAcct_Value();

    /** Column name JP_Tax_Name */
    public static final String COLUMNNAME_JP_Tax_Name = "JP_Tax_Name";

	/** Set Tax(Name)	  */
	public void setJP_Tax_Name (String JP_Tax_Name);

	/** Get Tax(Name)	  */
	public String getJP_Tax_Name();

    /** Column name JP_UserElement1_Value */
    public static final String COLUMNNAME_JP_UserElement1_Value = "JP_UserElement1_Value";

	/** Set User Column 1(Search key)	  */
	public void setJP_UserElement1_Value (String JP_UserElement1_Value);

	/** Get User Column 1(Search key)	  */
	public String getJP_UserElement1_Value();

    /** Column name JP_UserElement2_Value */
    public static final String COLUMNNAME_JP_UserElement2_Value = "JP_UserElement2_Value";

	/** Set User Column 2(Search key)	  */
	public void setJP_UserElement2_Value (String JP_UserElement2_Value);

	/** Get User Column 2(Search key)	  */
	public String getJP_UserElement2_Value();

    /** Column name Line */
    public static final String COLUMNNAME_Line = "Line";

	/** Set Line No.
	  * Unique line for this document
	  */
	public void setLine (int Line);

	/** Get Line No.
	  * Unique line for this document
	  */
	public int getLine();

    /** Column name M_Locator_ID */
    public static final String COLUMNNAME_M_Locator_ID = "M_Locator_ID";

	/** Set Locator.
	  * Warehouse Locator
	  */
	public void setM_Locator_ID (int M_Locator_ID);

	/** Get Locator.
	  * Warehouse Locator
	  */
	public int getM_Locator_ID();

	public org.compiere.model.I_M_Locator getM_Locator() throws RuntimeException;

    /** Column name M_Product_ID */
    public static final String COLUMNNAME_M_Product_ID = "M_Product_ID";

	/** Set Product.
	  * Product, Service, Item
	  */
	public void setM_Product_ID (int M_Product_ID);

	/** Get Product.
	  * Product, Service, Item
	  */
	public int getM_Product_ID();

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException;

    /** Column name PostingType */
    public static final String COLUMNNAME_PostingType = "PostingType";

	/** Set PostingType.
	  * The type of posted amount for the transaction
	  */
	public void setPostingType (String PostingType);

	/** Get PostingType.
	  * The type of posted amount for the transaction
	  */
	public String getPostingType();

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

    /** Column name Qty */
    public static final String COLUMNNAME_Qty = "Qty";

	/** Set Quantity.
	  * Quantity
	  */
	public void setQty (BigDecimal Qty);

	/** Get Quantity.
	  * Quantity
	  */
	public BigDecimal getQty();

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

    /** Column name User1_ID */
    public static final String COLUMNNAME_User1_ID = "User1_ID";

	/** Set User Element List 1.
	  * User defined list element #1
	  */
	public void setUser1_ID (int User1_ID);

	/** Get User Element List 1.
	  * User defined list element #1
	  */
	public int getUser1_ID();

	public org.compiere.model.I_C_ElementValue getUser1() throws RuntimeException;

    /** Column name User2_ID */
    public static final String COLUMNNAME_User2_ID = "User2_ID";

	/** Set User Element List 2.
	  * User defined list element #2
	  */
	public void setUser2_ID (int User2_ID);

	/** Get User Element List 2.
	  * User defined list element #2
	  */
	public int getUser2_ID();

	public org.compiere.model.I_C_ElementValue getUser2() throws RuntimeException;

    /** Column name UserElement1_ID */
    public static final String COLUMNNAME_UserElement1_ID = "UserElement1_ID";

	/** Set User Column 1.
	  * User defined accounting Element
	  */
	public void setUserElement1_ID (int UserElement1_ID);

	/** Get User Column 1.
	  * User defined accounting Element
	  */
	public int getUserElement1_ID();

    /** Column name UserElement2_ID */
    public static final String COLUMNNAME_UserElement2_ID = "UserElement2_ID";

	/** Set User Column 2.
	  * User defined accounting Element
	  */
	public void setUserElement2_ID (int UserElement2_ID);

	/** Get User Column 2.
	  * User defined accounting Element
	  */
	public int getUserElement2_ID();

    /** Column name X12DE355 */
    public static final String COLUMNNAME_X12DE355 = "X12DE355";

	/** Set UOM Code.
	  * UOM EDI X12 Code
	  */
	public void setX12DE355 (String X12DE355);

	/** Get UOM Code.
	  * UOM EDI X12 Code
	  */
	public String getX12DE355();
}
