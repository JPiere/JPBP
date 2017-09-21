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

/** Generated Model for JP_ContractProcess
 *  @author iDempiere (generated) 
 *  @version Release 4.1 - $Id$ */
public class X_JP_ContractProcess extends PO implements I_JP_ContractProcess, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20170921L;

    /** Standard Constructor */
    public X_JP_ContractProcess (Properties ctx, int JP_ContractProcess_ID, String trxName)
    {
      super (ctx, JP_ContractProcess_ID, trxName);
      /** if (JP_ContractProcess_ID == 0)
        {
			setDocBaseType (null);
			setIsCreateBaseDocJP (false);
// N
			setJP_ContractProcess_ID (0);
			setName (null);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_JP_ContractProcess (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_ContractProcess[")
        .append(get_ID()).append("]");
      return sb.toString();
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

	/** DocAction AD_Reference_ID=135 */
	public static final int DOCACTION_AD_Reference_ID=135;
	/** Complete = CO */
	public static final String DOCACTION_Complete = "CO";
	/** Approve = AP */
	public static final String DOCACTION_Approve = "AP";
	/** Reject = RJ */
	public static final String DOCACTION_Reject = "RJ";
	/** Post = PO */
	public static final String DOCACTION_Post = "PO";
	/** Void = VO */
	public static final String DOCACTION_Void = "VO";
	/** Close = CL */
	public static final String DOCACTION_Close = "CL";
	/** Reverse - Correct = RC */
	public static final String DOCACTION_Reverse_Correct = "RC";
	/** Reverse - Accrual = RA */
	public static final String DOCACTION_Reverse_Accrual = "RA";
	/** Invalidate = IN */
	public static final String DOCACTION_Invalidate = "IN";
	/** Re-activate = RE */
	public static final String DOCACTION_Re_Activate = "RE";
	/** <None> = -- */
	public static final String DOCACTION_None = "--";
	/** Prepare = PR */
	public static final String DOCACTION_Prepare = "PR";
	/** Unlock = XL */
	public static final String DOCACTION_Unlock = "XL";
	/** Wait Complete = WC */
	public static final String DOCACTION_WaitComplete = "WC";
	/** Set Document Action.
		@param DocAction 
		The targeted status of the document
	  */
	public void setDocAction (String DocAction)
	{

		set_Value (COLUMNNAME_DocAction, DocAction);
	}

	/** Get Document Action.
		@return The targeted status of the document
	  */
	public String getDocAction () 
	{
		return (String)get_Value(COLUMNNAME_DocAction);
	}

	/** AP Invoice = API */
	public static final String DOCBASETYPE_APInvoice = "API";
	/** AR Invoice = ARI */
	public static final String DOCBASETYPE_ARInvoice = "ARI";
	/** Purchase Order = POO */
	public static final String DOCBASETYPE_PurchaseOrder = "POO";
	/** Sales Order = SOO */
	public static final String DOCBASETYPE_SalesOrder = "SOO";
	/** Material Receipt = MMR */
	public static final String DOCBASETYPE_MaterialReceipt = "MMR";
	/** Material Delivery = MMS */
	public static final String DOCBASETYPE_MaterialDelivery = "MMS";
	/** Set Document BaseType.
		@param DocBaseType 
		Logical type of document
	  */
	public void setDocBaseType (String DocBaseType)
	{

		set_ValueNoCheck (COLUMNNAME_DocBaseType, DocBaseType);
	}

	/** Get Document BaseType.
		@return Logical type of document
	  */
	public String getDocBaseType () 
	{
		return (String)get_Value(COLUMNNAME_DocBaseType);
	}

	/** Set Create Base Doc.
		@param IsCreateBaseDocJP Create Base Doc	  */
	public void setIsCreateBaseDocJP (boolean IsCreateBaseDocJP)
	{
		set_ValueNoCheck (COLUMNNAME_IsCreateBaseDocJP, Boolean.valueOf(IsCreateBaseDocJP));
	}

	/** Get Create Base Doc.
		@return Create Base Doc	  */
	public boolean isCreateBaseDocJP () 
	{
		Object oo = get_Value(COLUMNNAME_IsCreateBaseDocJP);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Contract Process.
		@param JP_ContractProcess_ID Contract Process	  */
	public void setJP_ContractProcess_ID (int JP_ContractProcess_ID)
	{
		if (JP_ContractProcess_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ContractProcess_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ContractProcess_ID, Integer.valueOf(JP_ContractProcess_ID));
	}

	/** Get Contract Process.
		@return Contract Process	  */
	public int getJP_ContractProcess_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractProcess_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Contract Process(UU).
		@param JP_ContractProcess_UU Contract Process(UU)	  */
	public void setJP_ContractProcess_UU (String JP_ContractProcess_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_ContractProcess_UU, JP_ContractProcess_UU);
	}

	/** Get Contract Process(UU).
		@return Contract Process(UU)	  */
	public String getJP_ContractProcess_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_ContractProcess_UU);
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