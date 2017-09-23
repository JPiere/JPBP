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

/** Generated Model for JP_ContractLog
 *  @author iDempiere (generated) 
 *  @version Release 4.1 - $Id$ */
public class X_JP_ContractLog extends PO implements I_JP_ContractLog, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20170923L;

    /** Standard Constructor */
    public X_JP_ContractLog (Properties ctx, int JP_ContractLog_ID, String trxName)
    {
      super (ctx, JP_ContractLog_ID, trxName);
      /** if (JP_ContractLog_ID == 0)
        {
			setJP_ContractLog_ID (0);
        } */
    }

    /** Load Constructor */
    public X_JP_ContractLog (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_ContractLog[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_PInstance getAD_PInstance() throws RuntimeException
    {
		return (org.compiere.model.I_AD_PInstance)MTable.get(getCtx(), org.compiere.model.I_AD_PInstance.Table_Name)
			.getPO(getAD_PInstance_ID(), get_TrxName());	}

	/** Set Process Instance.
		@param AD_PInstance_ID 
		Instance of the process
	  */
	public void setAD_PInstance_ID (int AD_PInstance_ID)
	{
		if (AD_PInstance_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_AD_PInstance_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_AD_PInstance_ID, Integer.valueOf(AD_PInstance_ID));
	}

	/** Get Process Instance.
		@return Instance of the process
	  */
	public int getAD_PInstance_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_PInstance_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** Set Contract Management Log.
		@param JP_ContractLog_ID Contract Management Log	  */
	public void setJP_ContractLog_ID (int JP_ContractLog_ID)
	{
		if (JP_ContractLog_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ContractLog_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ContractLog_ID, Integer.valueOf(JP_ContractLog_ID));
	}

	/** Get Contract Management Log.
		@return Contract Management Log	  */
	public int getJP_ContractLog_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractLog_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_ContractLog_UU.
		@param JP_ContractLog_UU JP_ContractLog_UU	  */
	public void setJP_ContractLog_UU (String JP_ContractLog_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_ContractLog_UU, JP_ContractLog_UU);
	}

	/** Get JP_ContractLog_UU.
		@return JP_ContractLog_UU	  */
	public String getJP_ContractLog_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_ContractLog_UU);
	}

	/** Information = FIN */
	public static final String JP_CONTRACTPROCESSTRACELEVEL_Information = "FIN";
	/** Error = ERR */
	public static final String JP_CONTRACTPROCESSTRACELEVEL_Error = "ERR";
	/** Warning = WAR */
	public static final String JP_CONTRACTPROCESSTRACELEVEL_Warning = "WAR";
	/** No log = NON */
	public static final String JP_CONTRACTPROCESSTRACELEVEL_NoLog = "NON";
	/** To Be Confirmed = TBC */
	public static final String JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed = "TBC";
	/** Set Contract Process Trace Level.
		@param JP_ContractProcessTraceLevel Contract Process Trace Level	  */
	public void setJP_ContractProcessTraceLevel (String JP_ContractProcessTraceLevel)
	{

		set_ValueNoCheck (COLUMNNAME_JP_ContractProcessTraceLevel, JP_ContractProcessTraceLevel);
	}

	/** Get Contract Process Trace Level.
		@return Contract Process Trace Level	  */
	public String getJP_ContractProcessTraceLevel () 
	{
		return (String)get_Value(COLUMNNAME_JP_ContractProcessTraceLevel);
	}

	/** Contract Process Period Group = GPP */
	public static final String JP_CONTRACTPROCESSUNIT_ContractProcessPeriodGroup = "GPP";
	/** Contract Process Period  = CPP */
	public static final String JP_CONTRACTPROCESSUNIT_ContractProcessPeriod = "CPP";
	/** Contract Process Value of Contract Process Period Group = GPV */
	public static final String JP_CONTRACTPROCESSUNIT_ContractProcessValueOfContractProcessPeriodGroup = "GPV";
	/** Contract Process Value of Contract Process Period = CPV */
	public static final String JP_CONTRACTPROCESSUNIT_ContractProcessValueOfContractProcessPeriod = "CPV";
	/** Account Date = DAT */
	public static final String JP_CONTRACTPROCESSUNIT_AccountDate = "DAT";
	/** Document Date = DDT */
	public static final String JP_CONTRACTPROCESSUNIT_DocumentDate = "DDT";
	/** Per Contract Content = PCC */
	public static final String JP_CONTRACTPROCESSUNIT_PerContractContent = "PCC";
	/** Set Contract Process Unit.
		@param JP_ContractProcessUnit Contract Process Unit	  */
	public void setJP_ContractProcessUnit (String JP_ContractProcessUnit)
	{

		set_ValueNoCheck (COLUMNNAME_JP_ContractProcessUnit, JP_ContractProcessUnit);
	}

	/** Get Contract Process Unit.
		@return Contract Process Unit	  */
	public String getJP_ContractProcessUnit () 
	{
		return (String)get_Value(COLUMNNAME_JP_ContractProcessUnit);
	}
}