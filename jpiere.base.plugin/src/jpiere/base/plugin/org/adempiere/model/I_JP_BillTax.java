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

/** Generated Interface for JP_BillTax
 *  @author iDempiere (generated) 
 *  @version Release 8.2
 */
@SuppressWarnings("all")
public interface I_JP_BillTax 
{

    /** TableName=JP_BillTax */
    public static final String Table_Name = "JP_BillTax";

    /** AD_Table_ID=1000278 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 1 - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(1);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Client.
	  * Client/Tenant for this installation.
	  */
	public int getAD_Client_ID();

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

    /** Column name C_TaxProvider_ID */
    public static final String COLUMNNAME_C_TaxProvider_ID = "C_TaxProvider_ID";

	/** Set Tax Provider	  */
	public void setC_TaxProvider_ID (int C_TaxProvider_ID);

	/** Get Tax Provider	  */
	public int getC_TaxProvider_ID();

	public org.compiere.model.I_C_TaxProvider getC_TaxProvider() throws RuntimeException;

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

    /** Column name IsDocumentLevel */
    public static final String COLUMNNAME_IsDocumentLevel = "IsDocumentLevel";

	/** Set Document Level.
	  * Tax is calculated on document level (rather than line by line)
	  */
	public void setIsDocumentLevel (boolean IsDocumentLevel);

	/** Get Document Level.
	  * Tax is calculated on document level (rather than line by line)
	  */
	public boolean isDocumentLevel();

    /** Column name IsTaxIncluded */
    public static final String COLUMNNAME_IsTaxIncluded = "IsTaxIncluded";

	/** Set Price includes Tax.
	  * Tax is included in the price 
	  */
	public void setIsTaxIncluded (boolean IsTaxIncluded);

	/** Get Price includes Tax.
	  * Tax is included in the price 
	  */
	public boolean isTaxIncluded();

    /** Column name JP_BillTax_UU */
    public static final String COLUMNNAME_JP_BillTax_UU = "JP_BillTax_UU";

	/** Set JP_BillTax_UU	  */
	public void setJP_BillTax_UU (String JP_BillTax_UU);

	/** Get JP_BillTax_UU	  */
	public String getJP_BillTax_UU();

    /** Column name JP_Bill_ID */
    public static final String COLUMNNAME_JP_Bill_ID = "JP_Bill_ID";

	/** Set Bill	  */
	public void setJP_Bill_ID (int JP_Bill_ID);

	/** Get Bill	  */
	public int getJP_Bill_ID();

	public I_JP_Bill getJP_Bill() throws RuntimeException;

    /** Column name JP_RecalculatedTaxAmt */
    public static final String COLUMNNAME_JP_RecalculatedTaxAmt = "JP_RecalculatedTaxAmt";

	/** Set Recalculated Tax Amount.
	  * JPIERE-0508:JPBP
	  */
	public void setJP_RecalculatedTaxAmt (BigDecimal JP_RecalculatedTaxAmt);

	/** Get Recalculated Tax Amount.
	  * JPIERE-0508:JPBP
	  */
	public BigDecimal getJP_RecalculatedTaxAmt();

    /** Column name JP_RecalculatedTaxBaseAmt */
    public static final String COLUMNNAME_JP_RecalculatedTaxBaseAmt = "JP_RecalculatedTaxBaseAmt";

	/** Set Recalculated Tax base Amount.
	  * JPIERE-0508:JPBP
	  */
	public void setJP_RecalculatedTaxBaseAmt (BigDecimal JP_RecalculatedTaxBaseAmt);

	/** Get Recalculated Tax base Amount.
	  * JPIERE-0508:JPBP
	  */
	public BigDecimal getJP_RecalculatedTaxBaseAmt();

    /** Column name JP_TaxAdjust_InvoiceLine_ID */
    public static final String COLUMNNAME_JP_TaxAdjust_InvoiceLine_ID = "JP_TaxAdjust_InvoiceLine_ID";

	/** Set Invoice Line of Tax Adjust.
	  * JPIERE-0508:JPBP
	  */
	public void setJP_TaxAdjust_InvoiceLine_ID (int JP_TaxAdjust_InvoiceLine_ID);

	/** Get Invoice Line of Tax Adjust.
	  * JPIERE-0508:JPBP
	  */
	public int getJP_TaxAdjust_InvoiceLine_ID();

	public org.compiere.model.I_C_InvoiceLine getJP_TaxAdjust_InvoiceLine() throws RuntimeException;

    /** Column name JP_TaxAdjust_TaxAmt */
    public static final String COLUMNNAME_JP_TaxAdjust_TaxAmt = "JP_TaxAdjust_TaxAmt";

	/** Set Tax Amount of Tax Adjust.
	  * JPIERE-0508:JPBP
	  */
	public void setJP_TaxAdjust_TaxAmt (BigDecimal JP_TaxAdjust_TaxAmt);

	/** Get Tax Amount of Tax Adjust.
	  * JPIERE-0508:JPBP
	  */
	public BigDecimal getJP_TaxAdjust_TaxAmt();

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

    /** Column name TaxAmt */
    public static final String COLUMNNAME_TaxAmt = "TaxAmt";

	/** Set Tax Amount.
	  * Tax Amount for a document
	  */
	public void setTaxAmt (BigDecimal TaxAmt);

	/** Get Tax Amount.
	  * Tax Amount for a document
	  */
	public BigDecimal getTaxAmt();

    /** Column name TaxBaseAmt */
    public static final String COLUMNNAME_TaxBaseAmt = "TaxBaseAmt";

	/** Set Tax base Amount.
	  * Base for calculating the tax amount
	  */
	public void setTaxBaseAmt (BigDecimal TaxBaseAmt);

	/** Get Tax base Amount.
	  * Base for calculating the tax amount
	  */
	public BigDecimal getTaxBaseAmt();

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
