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

/** Generated Model for JP_ContractT
 *  @author iDempiere (generated) 
 *  @version Release 4.1 - $Id$ */
public class X_JP_ContractT extends PO implements I_JP_ContractT, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20170906L;

    /** Standard Constructor */
    public X_JP_ContractT (Properties ctx, int JP_ContractT_ID, String trxName)
    {
      super (ctx, JP_ContractT_ID, trxName);
      /** if (JP_ContractT_ID == 0)
        {
			setC_DocType_ID (0);
			setIsAutomaticUpdateJP (false);
// N
			setJP_ContractCategory_ID (0);
			setJP_ContractT_ID (0);
			setJP_ContractType (null);
// PDC
			setName (null);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_JP_ContractT (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org 
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
      StringBuffer sb = new StringBuffer ("X_JP_ContractT[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Trx Organization.
		@param AD_OrgTrx_ID 
		Performing or initiating organization
	  */
	public void setAD_OrgTrx_ID (int AD_OrgTrx_ID)
	{
		if (AD_OrgTrx_ID < 1) 
			set_Value (COLUMNNAME_AD_OrgTrx_ID, null);
		else 
			set_Value (COLUMNNAME_AD_OrgTrx_ID, Integer.valueOf(AD_OrgTrx_ID));
	}

	/** Get Trx Organization.
		@return Performing or initiating organization
	  */
	public int getAD_OrgTrx_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_OrgTrx_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_DocType getC_DocType() throws RuntimeException
    {
		return (org.compiere.model.I_C_DocType)MTable.get(getCtx(), org.compiere.model.I_C_DocType.Table_Name)
			.getPO(getC_DocType_ID(), get_TrxName());	}

	/** Set Document Type.
		@param C_DocType_ID 
		Document type or rules
	  */
	public void setC_DocType_ID (int C_DocType_ID)
	{
		if (C_DocType_ID < 0) 
			set_Value (COLUMNNAME_C_DocType_ID, null);
		else 
			set_Value (COLUMNNAME_C_DocType_ID, Integer.valueOf(C_DocType_ID));
	}

	/** Get Document Type.
		@return Document type or rules
	  */
	public int getC_DocType_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_DocType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Classname.
		@param Classname 
		Java Classname
	  */
	public void setClassname (String Classname)
	{
		set_Value (COLUMNNAME_Classname, Classname);
	}

	/** Get Classname.
		@return Java Classname
	  */
	public String getClassname () 
	{
		return (String)get_Value(COLUMNNAME_Classname);
	}

	/** Set Description.
		@param Description 
		Optional short description of the record
	  */
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription () 
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Automatic Update.
		@param IsAutomaticUpdateJP Automatic Update	  */
	public void setIsAutomaticUpdateJP (boolean IsAutomaticUpdateJP)
	{
		set_Value (COLUMNNAME_IsAutomaticUpdateJP, Boolean.valueOf(IsAutomaticUpdateJP));
	}

	/** Get Automatic Update.
		@return Automatic Update	  */
	public boolean isAutomaticUpdateJP () 
	{
		Object oo = get_Value(COLUMNNAME_IsAutomaticUpdateJP);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	public I_JP_ContractCancelTerm getJP_ContractCancelTerm() throws RuntimeException
    {
		return (I_JP_ContractCancelTerm)MTable.get(getCtx(), I_JP_ContractCancelTerm.Table_Name)
			.getPO(getJP_ContractCancelTerm_ID(), get_TrxName());	}

	/** Set Contract Cancel Term.
		@param JP_ContractCancelTerm_ID Contract Cancel Term	  */
	public void setJP_ContractCancelTerm_ID (int JP_ContractCancelTerm_ID)
	{
		if (JP_ContractCancelTerm_ID < 1) 
			set_Value (COLUMNNAME_JP_ContractCancelTerm_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ContractCancelTerm_ID, Integer.valueOf(JP_ContractCancelTerm_ID));
	}

	/** Get Contract Cancel Term.
		@return Contract Cancel Term	  */
	public int getJP_ContractCancelTerm_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractCancelTerm_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractCategory getJP_ContractCategory() throws RuntimeException
    {
		return (I_JP_ContractCategory)MTable.get(getCtx(), I_JP_ContractCategory.Table_Name)
			.getPO(getJP_ContractCategory_ID(), get_TrxName());	}

	/** Set Contract Category.
		@param JP_ContractCategory_ID Contract Category	  */
	public void setJP_ContractCategory_ID (int JP_ContractCategory_ID)
	{
		if (JP_ContractCategory_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ContractCategory_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ContractCategory_ID, Integer.valueOf(JP_ContractCategory_ID));
	}

	/** Get Contract Category.
		@return Contract Category	  */
	public int getJP_ContractCategory_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractCategory_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractExtendPeriod getJP_ContractExtendPeriod() throws RuntimeException
    {
		return (I_JP_ContractExtendPeriod)MTable.get(getCtx(), I_JP_ContractExtendPeriod.Table_Name)
			.getPO(getJP_ContractExtendPeriod_ID(), get_TrxName());	}

	/** Set Contract Extend Period.
		@param JP_ContractExtendPeriod_ID Contract Extend Period	  */
	public void setJP_ContractExtendPeriod_ID (int JP_ContractExtendPeriod_ID)
	{
		if (JP_ContractExtendPeriod_ID < 1) 
			set_Value (COLUMNNAME_JP_ContractExtendPeriod_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ContractExtendPeriod_ID, Integer.valueOf(JP_ContractExtendPeriod_ID));
	}

	/** Get Contract Extend Period.
		@return Contract Extend Period	  */
	public int getJP_ContractExtendPeriod_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractExtendPeriod_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Contract Doc Template.
		@param JP_ContractT_ID Contract Doc Template	  */
	public void setJP_ContractT_ID (int JP_ContractT_ID)
	{
		if (JP_ContractT_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ContractT_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ContractT_ID, Integer.valueOf(JP_ContractT_ID));
	}

	/** Get Contract Doc Template.
		@return Contract Doc Template	  */
	public int getJP_ContractT_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractT_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractT getJP_ContractT_Parent() throws RuntimeException
    {
		return (I_JP_ContractT)MTable.get(getCtx(), I_JP_ContractT.Table_Name)
			.getPO(getJP_ContractT_Parent_ID(), get_TrxName());	}

	/** Set Parent Contract Template .
		@param JP_ContractT_Parent_ID Parent Contract Template 	  */
	public void setJP_ContractT_Parent_ID (int JP_ContractT_Parent_ID)
	{
		if (JP_ContractT_Parent_ID < 1) 
			set_Value (COLUMNNAME_JP_ContractT_Parent_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ContractT_Parent_ID, Integer.valueOf(JP_ContractT_Parent_ID));
	}

	/** Get Parent Contract Template .
		@return Parent Contract Template 	  */
	public int getJP_ContractT_Parent_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractT_Parent_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Contract Doc Template(UU).
		@param JP_ContractT_UU Contract Doc Template(UU)	  */
	public void setJP_ContractT_UU (String JP_ContractT_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_ContractT_UU, JP_ContractT_UU);
	}

	/** Get Contract Doc Template(UU).
		@return Contract Doc Template(UU)	  */
	public String getJP_ContractT_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_ContractT_UU);
	}

	/** Period Contract = PDC */
	public static final String JP_CONTRACTTYPE_PeriodContract = "PDC";
	/** Spot Contract = STC */
	public static final String JP_CONTRACTTYPE_SpotContract = "STC";
	/** General Contract = GLC */
	public static final String JP_CONTRACTTYPE_GeneralContract = "GLC";
	/** Set Contract Type.
		@param JP_ContractType Contract Type	  */
	public void setJP_ContractType (String JP_ContractType)
	{

		set_ValueNoCheck (COLUMNNAME_JP_ContractType, JP_ContractType);
	}

	/** Get Contract Type.
		@return Contract Type	  */
	public String getJP_ContractType () 
	{
		return (String)get_Value(COLUMNNAME_JP_ContractType);
	}

	/** Set Name.
		@param Name 
		Alphanumeric identifier of the entity
	  */
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName () 
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

	/** Set Search Key.
		@param Value 
		Search key for the record in the format required - must be unique
	  */
	public void setValue (String Value)
	{
		set_Value (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue () 
	{
		return (String)get_Value(COLUMNNAME_Value);
	}
}