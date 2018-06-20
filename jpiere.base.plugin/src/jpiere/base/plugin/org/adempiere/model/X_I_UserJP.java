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
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for I_UserJP
 *  @author iDempiere (generated) 
 *  @version Release 5.1 - $Id$ */
public class X_I_UserJP extends PO implements I_I_UserJP, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20180615L;

    /** Standard Constructor */
    public X_I_UserJP (Properties ctx, int I_UserJP_ID, String trxName)
    {
      super (ctx, I_UserJP_ID, trxName);
      /** if (I_UserJP_ID == 0)
        {
			setFailedLoginCount (0);
			setI_IsActiveJP (true);
// Y
			setI_UserJP_ID (0);
			setIsAddMailTextAutomatically (false);
// N
			setIsExpired (false);
// N
			setIsFullBPAccess (true);
// Y
			setIsInPayroll (false);
// N
			setIsLocked (false);
// 'N'
			setIsMenuAutoExpand (null);
// N
			setIsNoPasswordReset (false);
// 'N'
			setIsSalesLead (false);
// N
        } */
    }

    /** Load Constructor */
    public X_I_UserJP (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_I_UserJP[")
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

	public org.compiere.model.I_AD_User getAD_User() throws RuntimeException
    {
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_Name)
			.getPO(getAD_User_ID(), get_TrxName());	}

	/** Set User/Contact.
		@param AD_User_ID 
		User within the system - Internal or Business Partner Contact
	  */
	public void setAD_User_ID (int AD_User_ID)
	{
		if (AD_User_ID < 1) 
			set_Value (COLUMNNAME_AD_User_ID, null);
		else 
			set_Value (COLUMNNAME_AD_User_ID, Integer.valueOf(AD_User_ID));
	}

	/** Get User/Contact.
		@return User within the system - Internal or Business Partner Contact
	  */
	public int getAD_User_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_User_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Answer.
		@param Answer Answer	  */
	public void setAnswer (String Answer)
	{
		set_Value (COLUMNNAME_Answer, Answer);
	}

	/** Get Answer.
		@return Answer	  */
	public String getAnswer () 
	{
		return (String)get_Value(COLUMNNAME_Answer);
	}

	/** Set BP Name.
		@param BPName BP Name	  */
	public void setBPName (String BPName)
	{
		set_Value (COLUMNNAME_BPName, BPName);
	}

	/** Get BP Name.
		@return BP Name	  */
	public String getBPName () 
	{
		return (String)get_Value(COLUMNNAME_BPName);
	}

	public org.compiere.model.I_C_Location getBP_Location() throws RuntimeException
    {
		return (org.compiere.model.I_C_Location)MTable.get(getCtx(), org.compiere.model.I_C_Location.Table_Name)
			.getPO(getBP_Location_ID(), get_TrxName());	}

	/** Set BP Address.
		@param BP_Location_ID 
		Address of the Business Partner
	  */
	public void setBP_Location_ID (int BP_Location_ID)
	{
		if (BP_Location_ID < 1) 
			set_Value (COLUMNNAME_BP_Location_ID, null);
		else 
			set_Value (COLUMNNAME_BP_Location_ID, Integer.valueOf(BP_Location_ID));
	}

	/** Get BP Address.
		@return Address of the Business Partner
	  */
	public int getBP_Location_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_BP_Location_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Birthday.
		@param Birthday 
		Birthday or Anniversary day
	  */
	public void setBirthday (Timestamp Birthday)
	{
		set_Value (COLUMNNAME_Birthday, Birthday);
	}

	/** Get Birthday.
		@return Birthday or Anniversary day
	  */
	public Timestamp getBirthday () 
	{
		return (Timestamp)get_Value(COLUMNNAME_Birthday);
	}

	public org.compiere.model.I_C_BPartner getC_BPartner() throws RuntimeException
    {
		return (org.compiere.model.I_C_BPartner)MTable.get(getCtx(), org.compiere.model.I_C_BPartner.Table_Name)
			.getPO(getC_BPartner_ID(), get_TrxName());	}

	/** Set Business Partner .
		@param C_BPartner_ID 
		Identifies a Business Partner
	  */
	public void setC_BPartner_ID (int C_BPartner_ID)
	{
		if (C_BPartner_ID < 1) 
			set_Value (COLUMNNAME_C_BPartner_ID, null);
		else 
			set_Value (COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
	}

	/** Get Business Partner .
		@return Identifies a Business Partner
	  */
	public int getC_BPartner_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_BPartner_Location getC_BPartner_Location() throws RuntimeException
    {
		return (org.compiere.model.I_C_BPartner_Location)MTable.get(getCtx(), org.compiere.model.I_C_BPartner_Location.Table_Name)
			.getPO(getC_BPartner_Location_ID(), get_TrxName());	}

	/** Set Partner Location.
		@param C_BPartner_Location_ID 
		Identifies the (ship to) address for this Business Partner
	  */
	public void setC_BPartner_Location_ID (int C_BPartner_Location_ID)
	{
		if (C_BPartner_Location_ID < 1) 
			set_Value (COLUMNNAME_C_BPartner_Location_ID, null);
		else 
			set_Value (COLUMNNAME_C_BPartner_Location_ID, Integer.valueOf(C_BPartner_Location_ID));
	}

	/** Get Partner Location.
		@return Identifies the (ship to) address for this Business Partner
	  */
	public int getC_BPartner_Location_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_Location_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Campaign getC_Campaign() throws RuntimeException
    {
		return (org.compiere.model.I_C_Campaign)MTable.get(getCtx(), org.compiere.model.I_C_Campaign.Table_Name)
			.getPO(getC_Campaign_ID(), get_TrxName());	}

	/** Set Campaign.
		@param C_Campaign_ID 
		Marketing Campaign
	  */
	public void setC_Campaign_ID (int C_Campaign_ID)
	{
		if (C_Campaign_ID < 1) 
			set_Value (COLUMNNAME_C_Campaign_ID, null);
		else 
			set_Value (COLUMNNAME_C_Campaign_ID, Integer.valueOf(C_Campaign_ID));
	}

	/** Get Campaign.
		@return Marketing Campaign
	  */
	public int getC_Campaign_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Campaign_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Greeting getC_Greeting() throws RuntimeException
    {
		return (org.compiere.model.I_C_Greeting)MTable.get(getCtx(), org.compiere.model.I_C_Greeting.Table_Name)
			.getPO(getC_Greeting_ID(), get_TrxName());	}

	/** Set Greeting.
		@param C_Greeting_ID 
		Greeting to print on correspondence
	  */
	public void setC_Greeting_ID (int C_Greeting_ID)
	{
		if (C_Greeting_ID < 1) 
			set_Value (COLUMNNAME_C_Greeting_ID, null);
		else 
			set_Value (COLUMNNAME_C_Greeting_ID, Integer.valueOf(C_Greeting_ID));
	}

	/** Get Greeting.
		@return Greeting to print on correspondence
	  */
	public int getC_Greeting_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Greeting_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Job getC_Job() throws RuntimeException
    {
		return (org.compiere.model.I_C_Job)MTable.get(getCtx(), org.compiere.model.I_C_Job.Table_Name)
			.getPO(getC_Job_ID(), get_TrxName());	}

	/** Set Position.
		@param C_Job_ID 
		Job Position
	  */
	public void setC_Job_ID (int C_Job_ID)
	{
		if (C_Job_ID < 1) 
			set_Value (COLUMNNAME_C_Job_ID, null);
		else 
			set_Value (COLUMNNAME_C_Job_ID, Integer.valueOf(C_Job_ID));
	}

	/** Get Position.
		@return Job Position
	  */
	public int getC_Job_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Job_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Location getC_Location() throws RuntimeException
    {
		return (org.compiere.model.I_C_Location)MTable.get(getCtx(), org.compiere.model.I_C_Location.Table_Name)
			.getPO(getC_Location_ID(), get_TrxName());	}

	/** Set Address.
		@param C_Location_ID 
		Location or Address
	  */
	public void setC_Location_ID (int C_Location_ID)
	{
		if (C_Location_ID < 1) 
			set_Value (COLUMNNAME_C_Location_ID, null);
		else 
			set_Value (COLUMNNAME_C_Location_ID, Integer.valueOf(C_Location_ID));
	}

	/** Get Address.
		@return Location or Address
	  */
	public int getC_Location_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Location_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Comments.
		@param Comments 
		Comments or additional information
	  */
	public void setComments (String Comments)
	{
		set_Value (COLUMNNAME_Comments, Comments);
	}

	/** Get Comments.
		@return Comments or additional information
	  */
	public String getComments () 
	{
		return (String)get_Value(COLUMNNAME_Comments);
	}

	/** ConnectionProfile AD_Reference_ID=364 */
	public static final int CONNECTIONPROFILE_AD_Reference_ID=364;
	/** LAN = L */
	public static final String CONNECTIONPROFILE_LAN = "L";
	/** Terminal Server = T */
	public static final String CONNECTIONPROFILE_TerminalServer = "T";
	/** VPN = V */
	public static final String CONNECTIONPROFILE_VPN = "V";
	/** WAN = W */
	public static final String CONNECTIONPROFILE_WAN = "W";
	/** Set Connection Profile.
		@param ConnectionProfile 
		How a Java Client connects to the server(s)
	  */
	public void setConnectionProfile (String ConnectionProfile)
	{

		set_Value (COLUMNNAME_ConnectionProfile, ConnectionProfile);
	}

	/** Get Connection Profile.
		@return How a Java Client connects to the server(s)
	  */
	public String getConnectionProfile () 
	{
		return (String)get_Value(COLUMNNAME_ConnectionProfile);
	}

	/** Set Date Account Locked.
		@param DateAccountLocked Date Account Locked	  */
	public void setDateAccountLocked (Timestamp DateAccountLocked)
	{
		set_Value (COLUMNNAME_DateAccountLocked, DateAccountLocked);
	}

	/** Get Date Account Locked.
		@return Date Account Locked	  */
	public Timestamp getDateAccountLocked () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateAccountLocked);
	}

	/** Set Date Last Login.
		@param DateLastLogin Date Last Login	  */
	public void setDateLastLogin (Timestamp DateLastLogin)
	{
		set_Value (COLUMNNAME_DateLastLogin, DateLastLogin);
	}

	/** Get Date Last Login.
		@return Date Last Login	  */
	public Timestamp getDateLastLogin () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateLastLogin);
	}

	/** Set Date Password Changed.
		@param DatePasswordChanged Date Password Changed	  */
	public void setDatePasswordChanged (Timestamp DatePasswordChanged)
	{
		set_Value (COLUMNNAME_DatePasswordChanged, DatePasswordChanged);
	}

	/** Get Date Password Changed.
		@return Date Password Changed	  */
	public Timestamp getDatePasswordChanged () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DatePasswordChanged);
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

	/** Set EMail Address.
		@param EMail 
		Electronic Mail Address
	  */
	public void setEMail (String EMail)
	{
		set_Value (COLUMNNAME_EMail, EMail);
	}

	/** Get EMail Address.
		@return Electronic Mail Address
	  */
	public String getEMail () 
	{
		return (String)get_Value(COLUMNNAME_EMail);
	}

	/** Set EMail User ID.
		@param EMailUser 
		User Name (ID) in the Mail System
	  */
	public void setEMailUser (String EMailUser)
	{
		set_Value (COLUMNNAME_EMailUser, EMailUser);
	}

	/** Get EMail User ID.
		@return User Name (ID) in the Mail System
	  */
	public String getEMailUser () 
	{
		return (String)get_Value(COLUMNNAME_EMailUser);
	}

	/** Set EMail User Password.
		@param EMailUserPW 
		Password of your email user id
	  */
	public void setEMailUserPW (String EMailUserPW)
	{
		set_Value (COLUMNNAME_EMailUserPW, EMailUserPW);
	}

	/** Get EMail User Password.
		@return Password of your email user id
	  */
	public String getEMailUserPW () 
	{
		return (String)get_Value(COLUMNNAME_EMailUserPW);
	}

	/** Set Verification Info.
		@param EMailVerify 
		Verification information of EMail Address
	  */
	public void setEMailVerify (String EMailVerify)
	{
		set_Value (COLUMNNAME_EMailVerify, EMailVerify);
	}

	/** Get Verification Info.
		@return Verification information of EMail Address
	  */
	public String getEMailVerify () 
	{
		return (String)get_Value(COLUMNNAME_EMailVerify);
	}

	/** Set EMail Verify.
		@param EMailVerifyDate 
		Date Email was verified
	  */
	public void setEMailVerifyDate (Timestamp EMailVerifyDate)
	{
		set_Value (COLUMNNAME_EMailVerifyDate, EMailVerifyDate);
	}

	/** Get EMail Verify.
		@return Date Email was verified
	  */
	public Timestamp getEMailVerifyDate () 
	{
		return (Timestamp)get_Value(COLUMNNAME_EMailVerifyDate);
	}

	/** Set Failed Login Count.
		@param FailedLoginCount Failed Login Count	  */
	public void setFailedLoginCount (int FailedLoginCount)
	{
		set_Value (COLUMNNAME_FailedLoginCount, Integer.valueOf(FailedLoginCount));
	}

	/** Get Failed Login Count.
		@return Failed Login Count	  */
	public int getFailedLoginCount () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_FailedLoginCount);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Fax.
		@param Fax 
		Facsimile number
	  */
	public void setFax (String Fax)
	{
		set_Value (COLUMNNAME_Fax, Fax);
	}

	/** Get Fax.
		@return Facsimile number
	  */
	public String getFax () 
	{
		return (String)get_Value(COLUMNNAME_Fax);
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

	/** Set Active(For Import).
		@param I_IsActiveJP 
		Active flag for Import Date
	  */
	public void setI_IsActiveJP (boolean I_IsActiveJP)
	{
		set_Value (COLUMNNAME_I_IsActiveJP, Boolean.valueOf(I_IsActiveJP));
	}

	/** Get Active(For Import).
		@return Active flag for Import Date
	  */
	public boolean isI_IsActiveJP () 
	{
		Object oo = get_Value(COLUMNNAME_I_IsActiveJP);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
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

	/** Set I_UserJP.
		@param I_UserJP_ID I_UserJP	  */
	public void setI_UserJP_ID (int I_UserJP_ID)
	{
		if (I_UserJP_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_I_UserJP_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_I_UserJP_ID, Integer.valueOf(I_UserJP_ID));
	}

	/** Get I_UserJP.
		@return I_UserJP	  */
	public int getI_UserJP_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_I_UserJP_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set I_UserJP_UU.
		@param I_UserJP_UU I_UserJP_UU	  */
	public void setI_UserJP_UU (String I_UserJP_UU)
	{
		set_ValueNoCheck (COLUMNNAME_I_UserJP_UU, I_UserJP_UU);
	}

	/** Get I_UserJP_UU.
		@return I_UserJP_UU	  */
	public String getI_UserJP_UU () 
	{
		return (String)get_Value(COLUMNNAME_I_UserJP_UU);
	}

	/** Set Add Mail Text Automatically.
		@param IsAddMailTextAutomatically 
		The selected mail template will be automatically inserted when creating an email
	  */
	public void setIsAddMailTextAutomatically (boolean IsAddMailTextAutomatically)
	{
		set_Value (COLUMNNAME_IsAddMailTextAutomatically, Boolean.valueOf(IsAddMailTextAutomatically));
	}

	/** Get Add Mail Text Automatically.
		@return The selected mail template will be automatically inserted when creating an email
	  */
	public boolean isAddMailTextAutomatically () 
	{
		Object oo = get_Value(COLUMNNAME_IsAddMailTextAutomatically);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Expired.
		@param IsExpired Expired	  */
	public void setIsExpired (boolean IsExpired)
	{
		set_Value (COLUMNNAME_IsExpired, Boolean.valueOf(IsExpired));
	}

	/** Get Expired.
		@return Expired	  */
	public boolean isExpired () 
	{
		Object oo = get_Value(COLUMNNAME_IsExpired);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Full BP Access.
		@param IsFullBPAccess 
		The user/contact has full access to Business Partner information and resources
	  */
	public void setIsFullBPAccess (boolean IsFullBPAccess)
	{
		set_Value (COLUMNNAME_IsFullBPAccess, Boolean.valueOf(IsFullBPAccess));
	}

	/** Get Full BP Access.
		@return The user/contact has full access to Business Partner information and resources
	  */
	public boolean isFullBPAccess () 
	{
		Object oo = get_Value(COLUMNNAME_IsFullBPAccess);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Is In Payroll.
		@param IsInPayroll 
		Defined if any User Contact will be used for Calculate Payroll
	  */
	public void setIsInPayroll (boolean IsInPayroll)
	{
		set_Value (COLUMNNAME_IsInPayroll, Boolean.valueOf(IsInPayroll));
	}

	/** Get Is In Payroll.
		@return Defined if any User Contact will be used for Calculate Payroll
	  */
	public boolean isInPayroll () 
	{
		Object oo = get_Value(COLUMNNAME_IsInPayroll);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Locked.
		@param IsLocked Locked	  */
	public void setIsLocked (boolean IsLocked)
	{
		set_Value (COLUMNNAME_IsLocked, Boolean.valueOf(IsLocked));
	}

	/** Get Locked.
		@return Locked	  */
	public boolean isLocked () 
	{
		Object oo = get_Value(COLUMNNAME_IsLocked);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** IsMenuAutoExpand AD_Reference_ID=319 */
	public static final int ISMENUAUTOEXPAND_AD_Reference_ID=319;
	/** Yes = Y */
	public static final String ISMENUAUTOEXPAND_Yes = "Y";
	/** No = N */
	public static final String ISMENUAUTOEXPAND_No = "N";
	/** Set Auto expand menu.
		@param IsMenuAutoExpand 
		If ticked, the menu is automatically expanded
	  */
	public void setIsMenuAutoExpand (String IsMenuAutoExpand)
	{

		set_Value (COLUMNNAME_IsMenuAutoExpand, IsMenuAutoExpand);
	}

	/** Get Auto expand menu.
		@return If ticked, the menu is automatically expanded
	  */
	public String getIsMenuAutoExpand () 
	{
		return (String)get_Value(COLUMNNAME_IsMenuAutoExpand);
	}

	/** Set No Password Reset.
		@param IsNoPasswordReset No Password Reset	  */
	public void setIsNoPasswordReset (boolean IsNoPasswordReset)
	{
		set_Value (COLUMNNAME_IsNoPasswordReset, Boolean.valueOf(IsNoPasswordReset));
	}

	/** Get No Password Reset.
		@return No Password Reset	  */
	public boolean isNoPasswordReset () 
	{
		Object oo = get_Value(COLUMNNAME_IsNoPasswordReset);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Sales Lead.
		@param IsSalesLead 
		This contact is a sales lead
	  */
	public void setIsSalesLead (boolean IsSalesLead)
	{
		set_Value (COLUMNNAME_IsSalesLead, Boolean.valueOf(IsSalesLead));
	}

	/** Get Sales Lead.
		@return This contact is a sales lead
	  */
	public boolean isSalesLead () 
	{
		Object oo = get_Value(COLUMNNAME_IsSalesLead);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set BP Address(Label).
		@param JP_BP_Location_Label BP Address(Label)	  */
	public void setJP_BP_Location_Label (String JP_BP_Location_Label)
	{
		set_Value (COLUMNNAME_JP_BP_Location_Label, JP_BP_Location_Label);
	}

	/** Get BP Address(Label).
		@return BP Address(Label)	  */
	public String getJP_BP_Location_Label () 
	{
		return (String)get_Value(COLUMNNAME_JP_BP_Location_Label);
	}

	/** Set Partner Location(Name).
		@param JP_BPartner_Location_Name Partner Location(Name)	  */
	public void setJP_BPartner_Location_Name (String JP_BPartner_Location_Name)
	{
		set_Value (COLUMNNAME_JP_BPartner_Location_Name, JP_BPartner_Location_Name);
	}

	/** Get Partner Location(Name).
		@return Partner Location(Name)	  */
	public String getJP_BPartner_Location_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_BPartner_Location_Name);
	}

	/** Set Business Partner(Search Key).
		@param JP_BPartner_Value Business Partner(Search Key)	  */
	public void setJP_BPartner_Value (String JP_BPartner_Value)
	{
		set_Value (COLUMNNAME_JP_BPartner_Value, JP_BPartner_Value);
	}

	/** Get Business Partner(Search Key).
		@return Business Partner(Search Key)	  */
	public String getJP_BPartner_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_BPartner_Value);
	}

	/** Set Campaign(Search Key).
		@param JP_Campaign_Value Campaign(Search Key)	  */
	public void setJP_Campaign_Value (String JP_Campaign_Value)
	{
		set_Value (COLUMNNAME_JP_Campaign_Value, JP_Campaign_Value);
	}

	/** Get Campaign(Search Key).
		@return Campaign(Search Key)	  */
	public String getJP_Campaign_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Campaign_Value);
	}

	public I_JP_Corporation getJP_Corporation() throws RuntimeException
    {
		return (I_JP_Corporation)MTable.get(getCtx(), I_JP_Corporation.Table_Name)
			.getPO(getJP_Corporation_ID(), get_TrxName());	}

	/** Set Corporation.
		@param JP_Corporation_ID Corporation	  */
	public void setJP_Corporation_ID (int JP_Corporation_ID)
	{
		if (JP_Corporation_ID < 1) 
			set_Value (COLUMNNAME_JP_Corporation_ID, null);
		else 
			set_Value (COLUMNNAME_JP_Corporation_ID, Integer.valueOf(JP_Corporation_ID));
	}

	/** Get Corporation.
		@return Corporation	  */
	public int getJP_Corporation_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Corporation_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Corporation(Search Key).
		@param JP_Corporation_Value Corporation(Search Key)	  */
	public void setJP_Corporation_Value (String JP_Corporation_Value)
	{
		set_Value (COLUMNNAME_JP_Corporation_Value, JP_Corporation_Value);
	}

	/** Get Corporation(Search Key).
		@return Corporation(Search Key)	  */
	public String getJP_Corporation_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Corporation_Value);
	}

	/** Set Greeting.
		@param JP_Greeting_Name Greeting	  */
	public void setJP_Greeting_Name (String JP_Greeting_Name)
	{
		set_Value (COLUMNNAME_JP_Greeting_Name, JP_Greeting_Name);
	}

	/** Get Greeting.
		@return Greeting	  */
	public String getJP_Greeting_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_Greeting_Name);
	}

	/** Set Position(Name).
		@param JP_Job_Name Position(Name)	  */
	public void setJP_Job_Name (String JP_Job_Name)
	{
		set_Value (COLUMNNAME_JP_Job_Name, JP_Job_Name);
	}

	/** Get Position(Name).
		@return Position(Name)	  */
	public String getJP_Job_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_Job_Name);
	}

	/** Set Location Label.
		@param JP_Location_Label Location Label	  */
	public void setJP_Location_Label (String JP_Location_Label)
	{
		set_Value (COLUMNNAME_JP_Location_Label, JP_Location_Label);
	}

	/** Get Location Label.
		@return Location Label	  */
	public String getJP_Location_Label () 
	{
		return (String)get_Value(COLUMNNAME_JP_Location_Label);
	}

	/** Set Mail Template(Name).
		@param JP_MailText_Name 
		Text templates for mailings
	  */
	public void setJP_MailText_Name (String JP_MailText_Name)
	{
		set_Value (COLUMNNAME_JP_MailText_Name, JP_MailText_Name);
	}

	/** Get Mail Template(Name).
		@return Text templates for mailings
	  */
	public String getJP_MailText_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_MailText_Name);
	}

	/** Set Trx Organization(Search Key).
		@param JP_OrgTrx_Value Trx Organization(Search Key)	  */
	public void setJP_OrgTrx_Value (String JP_OrgTrx_Value)
	{
		set_Value (COLUMNNAME_JP_OrgTrx_Value, JP_OrgTrx_Value);
	}

	/** Get Trx Organization(Search Key).
		@return Trx Organization(Search Key)	  */
	public String getJP_OrgTrx_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_OrgTrx_Value);
	}

	/** Set Organization(Search Key).
		@param JP_Org_Value Organization(Search Key)	  */
	public void setJP_Org_Value (String JP_Org_Value)
	{
		set_Value (COLUMNNAME_JP_Org_Value, JP_Org_Value);
	}

	/** Get Organization(Search Key).
		@return Organization(Search Key)	  */
	public String getJP_Org_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Org_Value);
	}

	/** Set Sales Rep(E-Mail).
		@param JP_SalesRep_EMail Sales Rep(E-Mail)	  */
	public void setJP_SalesRep_EMail (String JP_SalesRep_EMail)
	{
		set_Value (COLUMNNAME_JP_SalesRep_EMail, JP_SalesRep_EMail);
	}

	/** Get Sales Rep(E-Mail).
		@return Sales Rep(E-Mail)	  */
	public String getJP_SalesRep_EMail () 
	{
		return (String)get_Value(COLUMNNAME_JP_SalesRep_EMail);
	}

	/** Set Supervisor(E-Mail).
		@param JP_Supervisor_EMail Supervisor(E-Mail)	  */
	public void setJP_Supervisor_EMail (String JP_Supervisor_EMail)
	{
		set_Value (COLUMNNAME_JP_Supervisor_EMail, JP_Supervisor_EMail);
	}

	/** Get Supervisor(E-Mail).
		@return Supervisor(E-Mail)	  */
	public String getJP_Supervisor_EMail () 
	{
		return (String)get_Value(COLUMNNAME_JP_Supervisor_EMail);
	}

	/** Set LDAP User Name.
		@param LDAPUser 
		User Name used for authorization via LDAP (directory) services
	  */
	public void setLDAPUser (String LDAPUser)
	{
		set_Value (COLUMNNAME_LDAPUser, LDAPUser);
	}

	/** Get LDAP User Name.
		@return User Name used for authorization via LDAP (directory) services
	  */
	public String getLDAPUser () 
	{
		return (String)get_Value(COLUMNNAME_LDAPUser);
	}

	/** Set Last Contact.
		@param LastContact 
		Date this individual was last contacted
	  */
	public void setLastContact (Timestamp LastContact)
	{
		set_Value (COLUMNNAME_LastContact, LastContact);
	}

	/** Get Last Contact.
		@return Date this individual was last contacted
	  */
	public Timestamp getLastContact () 
	{
		return (Timestamp)get_Value(COLUMNNAME_LastContact);
	}

	/** Set Last Result.
		@param LastResult 
		Result of last contact
	  */
	public void setLastResult (String LastResult)
	{
		set_Value (COLUMNNAME_LastResult, LastResult);
	}

	/** Get Last Result.
		@return Result of last contact
	  */
	public String getLastResult () 
	{
		return (String)get_Value(COLUMNNAME_LastResult);
	}

	/** LeadSource AD_Reference_ID=53415 */
	public static final int LEADSOURCE_AD_Reference_ID=53415;
	/** Cold Call = CC */
	public static final String LEADSOURCE_ColdCall = "CC";
	/** Existing Customer = EC */
	public static final String LEADSOURCE_ExistingCustomer = "EC";
	/** Employee = EM */
	public static final String LEADSOURCE_Employee = "EM";
	/** Partner = PT */
	public static final String LEADSOURCE_Partner = "PT";
	/** Conference = CN */
	public static final String LEADSOURCE_Conference = "CN";
	/** Trade Show = TS */
	public static final String LEADSOURCE_TradeShow = "TS";
	/** Web Site = WS */
	public static final String LEADSOURCE_WebSite = "WS";
	/** Word of Mouth = WM */
	public static final String LEADSOURCE_WordOfMouth = "WM";
	/** Email = EL */
	public static final String LEADSOURCE_Email = "EL";
	/** Set Lead Source.
		@param LeadSource 
		The source of this lead/opportunity
	  */
	public void setLeadSource (String LeadSource)
	{

		set_Value (COLUMNNAME_LeadSource, LeadSource);
	}

	/** Get Lead Source.
		@return The source of this lead/opportunity
	  */
	public String getLeadSource () 
	{
		return (String)get_Value(COLUMNNAME_LeadSource);
	}

	/** Set Lead Source Description.
		@param LeadSourceDescription 
		Additional information on the source of this lead/opportunity
	  */
	public void setLeadSourceDescription (String LeadSourceDescription)
	{
		set_Value (COLUMNNAME_LeadSourceDescription, LeadSourceDescription);
	}

	/** Get Lead Source Description.
		@return Additional information on the source of this lead/opportunity
	  */
	public String getLeadSourceDescription () 
	{
		return (String)get_Value(COLUMNNAME_LeadSourceDescription);
	}

	/** LeadStatus AD_Reference_ID=53416 */
	public static final int LEADSTATUS_AD_Reference_ID=53416;
	/** New = N */
	public static final String LEADSTATUS_New = "N";
	/** Working = W */
	public static final String LEADSTATUS_Working = "W";
	/** Expired = E */
	public static final String LEADSTATUS_Expired = "E";
	/** Recycled = R */
	public static final String LEADSTATUS_Recycled = "R";
	/** Converted = C */
	public static final String LEADSTATUS_Converted = "C";
	/** Set Lead Status.
		@param LeadStatus 
		The status of this lead/opportunity in the sales cycle
	  */
	public void setLeadStatus (String LeadStatus)
	{

		set_Value (COLUMNNAME_LeadStatus, LeadStatus);
	}

	/** Get Lead Status.
		@return The status of this lead/opportunity in the sales cycle
	  */
	public String getLeadStatus () 
	{
		return (String)get_Value(COLUMNNAME_LeadStatus);
	}

	/** Set Lead Status Description.
		@param LeadStatusDescription 
		Additional information on the status of this lead/opportunity
	  */
	public void setLeadStatusDescription (String LeadStatusDescription)
	{
		set_Value (COLUMNNAME_LeadStatusDescription, LeadStatusDescription);
	}

	/** Get Lead Status Description.
		@return Additional information on the status of this lead/opportunity
	  */
	public String getLeadStatusDescription () 
	{
		return (String)get_Value(COLUMNNAME_LeadStatusDescription);
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

	/** NotificationType AD_Reference_ID=344 */
	public static final int NOTIFICATIONTYPE_AD_Reference_ID=344;
	/** EMail = E */
	public static final String NOTIFICATIONTYPE_EMail = "E";
	/** Notice = N */
	public static final String NOTIFICATIONTYPE_Notice = "N";
	/** None = X */
	public static final String NOTIFICATIONTYPE_None = "X";
	/** EMail+Notice = B */
	public static final String NOTIFICATIONTYPE_EMailPlusNotice = "B";
	/** Set Notification Type.
		@param NotificationType 
		Type of Notifications
	  */
	public void setNotificationType (String NotificationType)
	{

		set_Value (COLUMNNAME_NotificationType, NotificationType);
	}

	/** Get Notification Type.
		@return Type of Notifications
	  */
	public String getNotificationType () 
	{
		return (String)get_Value(COLUMNNAME_NotificationType);
	}

	/** Set Password.
		@param Password 
		Password of any length (case sensitive)
	  */
	public void setPassword (String Password)
	{
		set_Value (COLUMNNAME_Password, Password);
	}

	/** Get Password.
		@return Password of any length (case sensitive)
	  */
	public String getPassword () 
	{
		return (String)get_Value(COLUMNNAME_Password);
	}

	/** Set Phone.
		@param Phone 
		Identifies a telephone number
	  */
	public void setPhone (String Phone)
	{
		set_Value (COLUMNNAME_Phone, Phone);
	}

	/** Get Phone.
		@return Identifies a telephone number
	  */
	public String getPhone () 
	{
		return (String)get_Value(COLUMNNAME_Phone);
	}

	/** Set 2nd Phone.
		@param Phone2 
		Identifies an alternate telephone number.
	  */
	public void setPhone2 (String Phone2)
	{
		set_Value (COLUMNNAME_Phone2, Phone2);
	}

	/** Get 2nd Phone.
		@return Identifies an alternate telephone number.
	  */
	public String getPhone2 () 
	{
		return (String)get_Value(COLUMNNAME_Phone2);
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

	public org.compiere.model.I_R_MailText getR_DefaultMailText() throws RuntimeException
    {
		return (org.compiere.model.I_R_MailText)MTable.get(getCtx(), org.compiere.model.I_R_MailText.Table_Name)
			.getPO(getR_DefaultMailText_ID(), get_TrxName());	}

	/** Set Default mail template.
		@param R_DefaultMailText_ID Default mail template	  */
	public void setR_DefaultMailText_ID (int R_DefaultMailText_ID)
	{
		if (R_DefaultMailText_ID < 1) 
			set_Value (COLUMNNAME_R_DefaultMailText_ID, null);
		else 
			set_Value (COLUMNNAME_R_DefaultMailText_ID, Integer.valueOf(R_DefaultMailText_ID));
	}

	/** Get Default mail template.
		@return Default mail template	  */
	public int getR_DefaultMailText_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_R_DefaultMailText_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_User getSalesRep() throws RuntimeException
    {
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_Name)
			.getPO(getSalesRep_ID(), get_TrxName());	}

	/** Set Sales Rep.
		@param SalesRep_ID 
		Sales Representative or Company Agent
	  */
	public void setSalesRep_ID (int SalesRep_ID)
	{
		if (SalesRep_ID < 1) 
			set_Value (COLUMNNAME_SalesRep_ID, null);
		else 
			set_Value (COLUMNNAME_SalesRep_ID, Integer.valueOf(SalesRep_ID));
	}

	/** Get Sales Rep.
		@return Sales Representative or Company Agent
	  */
	public int getSalesRep_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_SalesRep_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Salt.
		@param Salt 
		Random data added to improve password hash effectiveness
	  */
	public void setSalt (String Salt)
	{
		set_Value (COLUMNNAME_Salt, Salt);
	}

	/** Get Salt.
		@return Random data added to improve password hash effectiveness
	  */
	public String getSalt () 
	{
		return (String)get_Value(COLUMNNAME_Salt);
	}

	/** Set Security Question.
		@param SecurityQuestion Security Question	  */
	public void setSecurityQuestion (String SecurityQuestion)
	{
		set_Value (COLUMNNAME_SecurityQuestion, SecurityQuestion);
	}

	/** Get Security Question.
		@return Security Question	  */
	public String getSecurityQuestion () 
	{
		return (String)get_Value(COLUMNNAME_SecurityQuestion);
	}

	public org.compiere.model.I_AD_User getSupervisor() throws RuntimeException
    {
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_Name)
			.getPO(getSupervisor_ID(), get_TrxName());	}

	/** Set Supervisor.
		@param Supervisor_ID 
		Supervisor for this user/organization - used for escalation and approval
	  */
	public void setSupervisor_ID (int Supervisor_ID)
	{
		if (Supervisor_ID < 1) 
			set_Value (COLUMNNAME_Supervisor_ID, null);
		else 
			set_Value (COLUMNNAME_Supervisor_ID, Integer.valueOf(Supervisor_ID));
	}

	/** Get Supervisor.
		@return Supervisor for this user/organization - used for escalation and approval
	  */
	public int getSupervisor_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Supervisor_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Title.
		@param Title 
		Name this entity is referred to as
	  */
	public void setTitle (String Title)
	{
		set_Value (COLUMNNAME_Title, Title);
	}

	/** Get Title.
		@return Name this entity is referred to as
	  */
	public String getTitle () 
	{
		return (String)get_Value(COLUMNNAME_Title);
	}

	/** Set User PIN.
		@param UserPIN User PIN	  */
	public void setUserPIN (String UserPIN)
	{
		set_Value (COLUMNNAME_UserPIN, UserPIN);
	}

	/** Get User PIN.
		@return User PIN	  */
	public String getUserPIN () 
	{
		return (String)get_Value(COLUMNNAME_UserPIN);
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