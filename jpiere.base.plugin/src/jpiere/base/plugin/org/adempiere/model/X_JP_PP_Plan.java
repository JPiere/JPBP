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
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;

/** Generated Model for JP_PP_Plan
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="JP_PP_Plan")
public class X_JP_PP_Plan extends PO implements I_JP_PP_Plan, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20231205L;

    /** Standard Constructor */
    public X_JP_PP_Plan (Properties ctx, int JP_PP_Plan_ID, String trxName)
    {
      super (ctx, JP_PP_Plan_ID, trxName);
      /** if (JP_PP_Plan_ID == 0)
        {
			setC_DocType_ID (0);
// @SQL=SELECT C_DocType_ID AS DefaultValue FROM C_DocType WHERE DocBaseType='JDP' AND IsDefault='Y' ORDER BY C_DocType_ID
			setDateAcct (new Timestamp( System.currentTimeMillis() ));
// @DateAcct@
			setDocAction (null);
// CO
			setDocStatus (null);
// DR
			setIsApproved (false);
// N
			setIsCompleteAutoJP (true);
// Y
			setIsCreated (null);
// N
			setIsRecordRouteJP (false);
// N
			setIsSplitWhenDifferenceJP (false);
// N
			setIsSummary (true);
// Y
			setJP_Name (null);
			setJP_PP_Doc_ID (0);
			setJP_PP_Plan_ID (0);
			setJP_PP_Status (null);
// NY
			setJP_PP_Workload_Fact (Env.ZERO);
// 0
			setJP_PP_Workload_Plan (Env.ZERO);
// 0
			setJP_PP_Workload_UOM_ID (0);
// 101
			setJP_Processing1 (null);
// N
			setJP_Processing2 (null);
// N
			setJP_Processing3 (null);
// N
			setJP_Processing4 (null);
// N
			setJP_Processing5 (null);
// N
			setJP_Processing6 (null);
// N
			setJP_ProductionQtyFact (Env.ZERO);
// 0
			setName (null);
			setPosted (false);
// N
			setProcessed (false);
// N
			setProcessing (false);
// N
			setProductionQty (Env.ZERO);
// 0
			setSeqNo (0);
// @SQL=SELECT COALESCE(MAX(SeqNo),0)+10 AS DefaultValue FROM JP_PP_Plan WHERE JP_PP_Doc_ID=@JP_PP_Doc_ID@
        } */
    }

    /** Standard Constructor */
    public X_JP_PP_Plan (Properties ctx, int JP_PP_Plan_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_PP_Plan_ID, trxName, virtualColumns);
      /** if (JP_PP_Plan_ID == 0)
        {
			setC_DocType_ID (0);
// @SQL=SELECT C_DocType_ID AS DefaultValue FROM C_DocType WHERE DocBaseType='JDP' AND IsDefault='Y' ORDER BY C_DocType_ID
			setDateAcct (new Timestamp( System.currentTimeMillis() ));
// @DateAcct@
			setDocAction (null);
// CO
			setDocStatus (null);
// DR
			setIsApproved (false);
// N
			setIsCompleteAutoJP (true);
// Y
			setIsCreated (null);
// N
			setIsRecordRouteJP (false);
// N
			setIsSplitWhenDifferenceJP (false);
// N
			setIsSummary (true);
// Y
			setJP_Name (null);
			setJP_PP_Doc_ID (0);
			setJP_PP_Plan_ID (0);
			setJP_PP_Status (null);
// NY
			setJP_PP_Workload_Fact (Env.ZERO);
// 0
			setJP_PP_Workload_Plan (Env.ZERO);
// 0
			setJP_PP_Workload_UOM_ID (0);
// 101
			setJP_Processing1 (null);
// N
			setJP_Processing2 (null);
// N
			setJP_Processing3 (null);
// N
			setJP_Processing4 (null);
// N
			setJP_Processing5 (null);
// N
			setJP_Processing6 (null);
// N
			setJP_ProductionQtyFact (Env.ZERO);
// 0
			setName (null);
			setPosted (false);
// N
			setProcessed (false);
// N
			setProcessing (false);
// N
			setProductionQty (Env.ZERO);
// 0
			setSeqNo (0);
// @SQL=SELECT COALESCE(MAX(SeqNo),0)+10 AS DefaultValue FROM JP_PP_Plan WHERE JP_PP_Doc_ID=@JP_PP_Doc_ID@
        } */
    }

    /** Load Constructor */
    public X_JP_PP_Plan (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 1 - Org 
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
      StringBuilder sb = new StringBuilder ("X_JP_PP_Plan[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
    }

	/** Set Trx Organization.
		@param AD_OrgTrx_ID Performing or initiating organization
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
	public int getAD_OrgTrx_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_OrgTrx_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Activity getC_Activity() throws RuntimeException
	{
		return (org.compiere.model.I_C_Activity)MTable.get(getCtx(), org.compiere.model.I_C_Activity.Table_ID)
			.getPO(getC_Activity_ID(), get_TrxName());
	}

	/** Set Activity.
		@param C_Activity_ID Business Activity
	*/
	public void setC_Activity_ID (int C_Activity_ID)
	{
		if (C_Activity_ID < 1)
			set_Value (COLUMNNAME_C_Activity_ID, null);
		else
			set_Value (COLUMNNAME_C_Activity_ID, Integer.valueOf(C_Activity_ID));
	}

	/** Get Activity.
		@return Business Activity
	  */
	public int getC_Activity_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Activity_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Campaign getC_Campaign() throws RuntimeException
	{
		return (org.compiere.model.I_C_Campaign)MTable.get(getCtx(), org.compiere.model.I_C_Campaign.Table_ID)
			.getPO(getC_Campaign_ID(), get_TrxName());
	}

	/** Set Campaign.
		@param C_Campaign_ID Marketing Campaign
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
	public int getC_Campaign_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Campaign_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_DocType getC_DocType() throws RuntimeException
	{
		return (org.compiere.model.I_C_DocType)MTable.get(getCtx(), org.compiere.model.I_C_DocType.Table_ID)
			.getPO(getC_DocType_ID(), get_TrxName());
	}

	/** Set Document Type.
		@param C_DocType_ID Document type or rules
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
	public int getC_DocType_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_DocType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ProjectPhase getC_ProjectPhase() throws RuntimeException
	{
		return (org.compiere.model.I_C_ProjectPhase)MTable.get(getCtx(), org.compiere.model.I_C_ProjectPhase.Table_ID)
			.getPO(getC_ProjectPhase_ID(), get_TrxName());
	}

	/** Set Project Phase.
		@param C_ProjectPhase_ID Phase of a Project
	*/
	public void setC_ProjectPhase_ID (int C_ProjectPhase_ID)
	{
		if (C_ProjectPhase_ID < 1)
			set_Value (COLUMNNAME_C_ProjectPhase_ID, null);
		else
			set_Value (COLUMNNAME_C_ProjectPhase_ID, Integer.valueOf(C_ProjectPhase_ID));
	}

	/** Get Project Phase.
		@return Phase of a Project
	  */
	public int getC_ProjectPhase_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_ProjectPhase_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ProjectTask getC_ProjectTask() throws RuntimeException
	{
		return (org.compiere.model.I_C_ProjectTask)MTable.get(getCtx(), org.compiere.model.I_C_ProjectTask.Table_ID)
			.getPO(getC_ProjectTask_ID(), get_TrxName());
	}

	/** Set Project Task.
		@param C_ProjectTask_ID Actual Project Task in a Phase
	*/
	public void setC_ProjectTask_ID (int C_ProjectTask_ID)
	{
		if (C_ProjectTask_ID < 1)
			set_Value (COLUMNNAME_C_ProjectTask_ID, null);
		else
			set_Value (COLUMNNAME_C_ProjectTask_ID, Integer.valueOf(C_ProjectTask_ID));
	}

	/** Get Project Task.
		@return Actual Project Task in a Phase
	  */
	public int getC_ProjectTask_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_ProjectTask_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Project getC_Project() throws RuntimeException
	{
		return (org.compiere.model.I_C_Project)MTable.get(getCtx(), org.compiere.model.I_C_Project.Table_ID)
			.getPO(getC_Project_ID(), get_TrxName());
	}

	/** Set Project.
		@param C_Project_ID Financial Project
	*/
	public void setC_Project_ID (int C_Project_ID)
	{
		if (C_Project_ID < 1)
			set_Value (COLUMNNAME_C_Project_ID, null);
		else
			set_Value (COLUMNNAME_C_Project_ID, Integer.valueOf(C_Project_ID));
	}

	/** Get Project.
		@return Financial Project
	  */
	public int getC_Project_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Project_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_UOM getC_UOM() throws RuntimeException
	{
		return (org.compiere.model.I_C_UOM)MTable.get(getCtx(), org.compiere.model.I_C_UOM.Table_ID)
			.getPO(getC_UOM_ID(), get_TrxName());
	}

	/** Set UOM.
		@param C_UOM_ID Unit of Measure
	*/
	public void setC_UOM_ID (int C_UOM_ID)
	{
		if (C_UOM_ID < 1)
			set_ValueNoCheck (COLUMNNAME_C_UOM_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_C_UOM_ID, Integer.valueOf(C_UOM_ID));
	}

	/** Get UOM.
		@return Unit of Measure
	  */
	public int getC_UOM_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_UOM_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Account Date.
		@param DateAcct Accounting Date
	*/
	public void setDateAcct (Timestamp DateAcct)
	{
		set_Value (COLUMNNAME_DateAcct, DateAcct);
	}

	/** Get Account Date.
		@return Accounting Date
	  */
	public Timestamp getDateAcct()
	{
		return (Timestamp)get_Value(COLUMNNAME_DateAcct);
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

	/** DocAction AD_Reference_ID=135 */
	public static final int DOCACTION_AD_Reference_ID=135;
	/** &lt;None&gt; = -- */
	public static final String DOCACTION_None = "--";
	/** Approve = AP */
	public static final String DOCACTION_Approve = "AP";
	/** Close = CL */
	public static final String DOCACTION_Close = "CL";
	/** Complete = CO */
	public static final String DOCACTION_Complete = "CO";
	/** Invalidate = IN */
	public static final String DOCACTION_Invalidate = "IN";
	/** Post = PO */
	public static final String DOCACTION_Post = "PO";
	/** Prepare = PR */
	public static final String DOCACTION_Prepare = "PR";
	/** Reverse - Accrual = RA */
	public static final String DOCACTION_Reverse_Accrual = "RA";
	/** Reverse - Correct = RC */
	public static final String DOCACTION_Reverse_Correct = "RC";
	/** Re-activate = RE */
	public static final String DOCACTION_Re_Activate = "RE";
	/** Reject = RJ */
	public static final String DOCACTION_Reject = "RJ";
	/** Void = VO */
	public static final String DOCACTION_Void = "VO";
	/** Wait Complete = WC */
	public static final String DOCACTION_WaitComplete = "WC";
	/** Unlock = XL */
	public static final String DOCACTION_Unlock = "XL";
	/** Set Document Action.
		@param DocAction The targeted status of the document
	*/
	public void setDocAction (String DocAction)
	{

		set_Value (COLUMNNAME_DocAction, DocAction);
	}

	/** Get Document Action.
		@return The targeted status of the document
	  */
	public String getDocAction()
	{
		return (String)get_Value(COLUMNNAME_DocAction);
	}

	/** DocStatus AD_Reference_ID=131 */
	public static final int DOCSTATUS_AD_Reference_ID=131;
	/** Unknown = ?? */
	public static final String DOCSTATUS_Unknown = "??";
	/** Approved = AP */
	public static final String DOCSTATUS_Approved = "AP";
	/** Closed = CL */
	public static final String DOCSTATUS_Closed = "CL";
	/** Completed = CO */
	public static final String DOCSTATUS_Completed = "CO";
	/** Drafted = DR */
	public static final String DOCSTATUS_Drafted = "DR";
	/** Invalid = IN */
	public static final String DOCSTATUS_Invalid = "IN";
	/** In Progress = IP */
	public static final String DOCSTATUS_InProgress = "IP";
	/** Not Approved = NA */
	public static final String DOCSTATUS_NotApproved = "NA";
	/** Reversed = RE */
	public static final String DOCSTATUS_Reversed = "RE";
	/** Voided = VO */
	public static final String DOCSTATUS_Voided = "VO";
	/** Waiting Confirmation = WC */
	public static final String DOCSTATUS_WaitingConfirmation = "WC";
	/** Waiting Payment = WP */
	public static final String DOCSTATUS_WaitingPayment = "WP";
	/** Set Document Status.
		@param DocStatus The current status of the document
	*/
	public void setDocStatus (String DocStatus)
	{

		set_Value (COLUMNNAME_DocStatus, DocStatus);
	}

	/** Get Document Status.
		@return The current status of the document
	  */
	public String getDocStatus()
	{
		return (String)get_Value(COLUMNNAME_DocStatus);
	}

	/** Set Document No.
		@param DocumentNo Document sequence number of the document
	*/
	public void setDocumentNo (String DocumentNo)
	{
		set_ValueNoCheck (COLUMNNAME_DocumentNo, DocumentNo);
	}

	/** Get Document No.
		@return Document sequence number of the document
	  */
	public String getDocumentNo()
	{
		return (String)get_Value(COLUMNNAME_DocumentNo);
	}

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), getDocumentNo());
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

	/** Set Approved.
		@param IsApproved Indicates if this document requires approval
	*/
	public void setIsApproved (boolean IsApproved)
	{
		set_Value (COLUMNNAME_IsApproved, Boolean.valueOf(IsApproved));
	}

	/** Get Approved.
		@return Indicates if this document requires approval
	  */
	public boolean isApproved()
	{
		Object oo = get_Value(COLUMNNAME_IsApproved);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Auto Complete.
		@param IsCompleteAutoJP Auto Complete
	*/
	public void setIsCompleteAutoJP (boolean IsCompleteAutoJP)
	{
		set_Value (COLUMNNAME_IsCompleteAutoJP, Boolean.valueOf(IsCompleteAutoJP));
	}

	/** Get Auto Complete.
		@return Auto Complete	  */
	public boolean isCompleteAutoJP()
	{
		Object oo = get_Value(COLUMNNAME_IsCompleteAutoJP);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** IsCreated AD_Reference_ID=319 */
	public static final int ISCREATED_AD_Reference_ID=319;
	/** No = N */
	public static final String ISCREATED_No = "N";
	/** Yes = Y */
	public static final String ISCREATED_Yes = "Y";
	/** Set Records created.
		@param IsCreated Records created
	*/
	public void setIsCreated (String IsCreated)
	{

		set_Value (COLUMNNAME_IsCreated, IsCreated);
	}

	/** Get Records created.
		@return Records created	  */
	public String getIsCreated()
	{
		return (String)get_Value(COLUMNNAME_IsCreated);
	}

	/** Set Record the Route.
		@param IsRecordRouteJP Record the Route
	*/
	public void setIsRecordRouteJP (boolean IsRecordRouteJP)
	{
		set_ValueNoCheck (COLUMNNAME_IsRecordRouteJP, Boolean.valueOf(IsRecordRouteJP));
	}

	/** Get Record the Route.
		@return Record the Route	  */
	public boolean isRecordRouteJP()
	{
		Object oo = get_Value(COLUMNNAME_IsRecordRouteJP);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Split when Difference.
		@param IsSplitWhenDifferenceJP Split document when there is a difference
	*/
	public void setIsSplitWhenDifferenceJP (boolean IsSplitWhenDifferenceJP)
	{
		set_Value (COLUMNNAME_IsSplitWhenDifferenceJP, Boolean.valueOf(IsSplitWhenDifferenceJP));
	}

	/** Get Split when Difference.
		@return Split document when there is a difference
	  */
	public boolean isSplitWhenDifferenceJP()
	{
		Object oo = get_Value(COLUMNNAME_IsSplitWhenDifferenceJP);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Summary Level.
		@param IsSummary This is a summary entity
	*/
	public void setIsSummary (boolean IsSummary)
	{
		set_Value (COLUMNNAME_IsSummary, Boolean.valueOf(IsSummary));
	}

	/** Get Summary Level.
		@return This is a summary entity
	  */
	public boolean isSummary()
	{
		Object oo = get_Value(COLUMNNAME_IsSummary);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Movement Date(Destination).
		@param JP_MovementDateDst Movement Date(Destination)
	*/
	public void setJP_MovementDateDst (Timestamp JP_MovementDateDst)
	{
		set_Value (COLUMNNAME_JP_MovementDateDst, JP_MovementDateDst);
	}

	/** Get Movement Date(Destination).
		@return Movement Date(Destination)	  */
	public Timestamp getJP_MovementDateDst()
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_MovementDateDst);
	}

	/** Set Movement Date(Next).
		@param JP_MovementDateNext Movement Date(Next)
	*/
	public void setJP_MovementDateNext (Timestamp JP_MovementDateNext)
	{
		set_Value (COLUMNNAME_JP_MovementDateNext, JP_MovementDateNext);
	}

	/** Get Movement Date(Next).
		@return Movement Date(Next)	  */
	public Timestamp getJP_MovementDateNext()
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_MovementDateNext);
	}

	/** Set Name.
		@param JP_Name Alphanumeric identifier of the entity
	*/
	public void setJP_Name (String JP_Name)
	{
		set_Value (COLUMNNAME_JP_Name, JP_Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getJP_Name()
	{
		return (String)get_Value(COLUMNNAME_JP_Name);
	}

	public I_JP_PP_Doc getJP_PP_Doc() throws RuntimeException
	{
		return (I_JP_PP_Doc)MTable.get(getCtx(), I_JP_PP_Doc.Table_ID)
			.getPO(getJP_PP_Doc_ID(), get_TrxName());
	}

	/** Set PP Doc.
		@param JP_PP_Doc_ID JPIERE-0501:JPBP
	*/
	public void setJP_PP_Doc_ID (int JP_PP_Doc_ID)
	{
		if (JP_PP_Doc_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_PP_Doc_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_PP_Doc_ID, Integer.valueOf(JP_PP_Doc_ID));
	}

	/** Get PP Doc.
		@return JPIERE-0501:JPBP
	  */
	public int getJP_PP_Doc_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PP_Doc_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set End date and time.
		@param JP_PP_End End date and time
	*/
	public void setJP_PP_End (Timestamp JP_PP_End)
	{
		set_Value (COLUMNNAME_JP_PP_End, JP_PP_End);
	}

	/** Get End date and time.
		@return End date and time	  */
	public Timestamp getJP_PP_End()
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_PP_End);
	}

	public I_JP_PP_PlanT getJP_PP_PlanT() throws RuntimeException
	{
		return (I_JP_PP_PlanT)MTable.get(getCtx(), I_JP_PP_PlanT.Table_ID)
			.getPO(getJP_PP_PlanT_ID(), get_TrxName());
	}

	/** Set PP Plan Template.
		@param JP_PP_PlanT_ID JPIERE-0501:JPBP
	*/
	public void setJP_PP_PlanT_ID (int JP_PP_PlanT_ID)
	{
		if (JP_PP_PlanT_ID < 1)
			set_Value (COLUMNNAME_JP_PP_PlanT_ID, null);
		else
			set_Value (COLUMNNAME_JP_PP_PlanT_ID, Integer.valueOf(JP_PP_PlanT_ID));
	}

	/** Get PP Plan Template.
		@return JPIERE-0501:JPBP
	  */
	public int getJP_PP_PlanT_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PP_PlanT_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set PP Plan.
		@param JP_PP_Plan_ID JPIERE-0501:JPBP
	*/
	public void setJP_PP_Plan_ID (int JP_PP_Plan_ID)
	{
		if (JP_PP_Plan_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_PP_Plan_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_PP_Plan_ID, Integer.valueOf(JP_PP_Plan_ID));
	}

	/** Get PP Plan.
		@return JPIERE-0501:JPBP
	  */
	public int getJP_PP_Plan_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PP_Plan_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set PP Plan(UU).
		@param JP_PP_Plan_UU PP Plan(UU)
	*/
	public void setJP_PP_Plan_UU (String JP_PP_Plan_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_PP_Plan_UU, JP_PP_Plan_UU);
	}

	/** Get PP Plan(UU).
		@return PP Plan(UU)	  */
	public String getJP_PP_Plan_UU()
	{
		return (String)get_Value(COLUMNNAME_JP_PP_Plan_UU);
	}

	/** Set Scheduled to End.
		@param JP_PP_ScheduledEnd Scheduled to End
	*/
	public void setJP_PP_ScheduledEnd (Timestamp JP_PP_ScheduledEnd)
	{
		set_Value (COLUMNNAME_JP_PP_ScheduledEnd, JP_PP_ScheduledEnd);
	}

	/** Get Scheduled to End.
		@return Scheduled to End	  */
	public Timestamp getJP_PP_ScheduledEnd()
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_PP_ScheduledEnd);
	}

	/** Set Scheduled to Start.
		@param JP_PP_ScheduledStart Scheduled to Start
	*/
	public void setJP_PP_ScheduledStart (Timestamp JP_PP_ScheduledStart)
	{
		set_Value (COLUMNNAME_JP_PP_ScheduledStart, JP_PP_ScheduledStart);
	}

	/** Get Scheduled to Start.
		@return Scheduled to Start	  */
	public Timestamp getJP_PP_ScheduledStart()
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_PP_ScheduledStart);
	}

	/** Set Start date and time.
		@param JP_PP_Start Start date and time
	*/
	public void setJP_PP_Start (Timestamp JP_PP_Start)
	{
		set_Value (COLUMNNAME_JP_PP_Start, JP_PP_Start);
	}

	/** Get Start date and time.
		@return Start date and time	  */
	public Timestamp getJP_PP_Start()
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_PP_Start);
	}

	/** Completed = CO */
	public static final String JP_PP_STATUS_Completed = "CO";
	/** Not yet started = NY */
	public static final String JP_PP_STATUS_NotYetStarted = "NY";
	/** Void = VO */
	public static final String JP_PP_STATUS_Void = "VO";
	/** Work in progress = WP */
	public static final String JP_PP_STATUS_WorkInProgress = "WP";
	/** Set Production Status.
		@param JP_PP_Status Production Status
	*/
	public void setJP_PP_Status (String JP_PP_Status)
	{

		set_Value (COLUMNNAME_JP_PP_Status, JP_PP_Status);
	}

	/** Get Production Status.
		@return Production Status	  */
	public String getJP_PP_Status()
	{
		return (String)get_Value(COLUMNNAME_JP_PP_Status);
	}

	/** Material Movement = MMM */
	public static final String JP_PP_WORKPROCESSTYPE_MaterialMovement = "MMM";
	/** Material Production = MMP */
	public static final String JP_PP_WORKPROCESSTYPE_MaterialProduction = "MMP";
	/** Not create document = NON */
	public static final String JP_PP_WORKPROCESSTYPE_NotCreateDocument = "NON";
	/** Set Work Process Type.
		@param JP_PP_WorkProcessType JPIERE-0609:JPBP
	*/
	public void setJP_PP_WorkProcessType (String JP_PP_WorkProcessType)
	{

		set_Value (COLUMNNAME_JP_PP_WorkProcessType, JP_PP_WorkProcessType);
	}

	/** Get Work Process Type.
		@return JPIERE-0609:JPBP
	  */
	public String getJP_PP_WorkProcessType()
	{
		return (String)get_Value(COLUMNNAME_JP_PP_WorkProcessType);
	}

	public I_JP_PP_WorkProcess getJP_PP_WorkProcess() throws RuntimeException
	{
		return (I_JP_PP_WorkProcess)MTable.get(getCtx(), I_JP_PP_WorkProcess.Table_ID)
			.getPO(getJP_PP_WorkProcess_ID(), get_TrxName());
	}

	/** Set Work Process.
		@param JP_PP_WorkProcess_ID JPIERE-0609:JPBP
	*/
	public void setJP_PP_WorkProcess_ID (int JP_PP_WorkProcess_ID)
	{
		if (JP_PP_WorkProcess_ID < 1)
			set_Value (COLUMNNAME_JP_PP_WorkProcess_ID, null);
		else
			set_Value (COLUMNNAME_JP_PP_WorkProcess_ID, Integer.valueOf(JP_PP_WorkProcess_ID));
	}

	/** Get Work Process.
		@return JPIERE-0609:JPBP
	  */
	public int getJP_PP_WorkProcess_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PP_WorkProcess_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Workload(Fact).
		@param JP_PP_Workload_Fact Workload(Fact)
	*/
	public void setJP_PP_Workload_Fact (BigDecimal JP_PP_Workload_Fact)
	{
		set_Value (COLUMNNAME_JP_PP_Workload_Fact, JP_PP_Workload_Fact);
	}

	/** Get Workload(Fact).
		@return Workload(Fact)	  */
	public BigDecimal getJP_PP_Workload_Fact()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_JP_PP_Workload_Fact);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Workload(Plan).
		@param JP_PP_Workload_Plan Workload(Plan)
	*/
	public void setJP_PP_Workload_Plan (BigDecimal JP_PP_Workload_Plan)
	{
		set_Value (COLUMNNAME_JP_PP_Workload_Plan, JP_PP_Workload_Plan);
	}

	/** Get Workload(Plan).
		@return Workload(Plan)	  */
	public BigDecimal getJP_PP_Workload_Plan()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_JP_PP_Workload_Plan);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public org.compiere.model.I_C_UOM getJP_PP_Workload_UOM() throws RuntimeException
	{
		return (org.compiere.model.I_C_UOM)MTable.get(getCtx(), org.compiere.model.I_C_UOM.Table_ID)
			.getPO(getJP_PP_Workload_UOM_ID(), get_TrxName());
	}

	/** Set Workload UOM.
		@param JP_PP_Workload_UOM_ID Workload UOM
	*/
	public void setJP_PP_Workload_UOM_ID (int JP_PP_Workload_UOM_ID)
	{
		if (JP_PP_Workload_UOM_ID < 1)
			set_Value (COLUMNNAME_JP_PP_Workload_UOM_ID, null);
		else
			set_Value (COLUMNNAME_JP_PP_Workload_UOM_ID, Integer.valueOf(JP_PP_Workload_UOM_ID));
	}

	/** Get Workload UOM.
		@return Workload UOM	  */
	public int getJP_PP_Workload_UOM_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PP_Workload_UOM_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_PhysicalWarehouse getJP_PhysicalWarehouseDst() throws RuntimeException
	{
		return (I_JP_PhysicalWarehouse)MTable.get(getCtx(), I_JP_PhysicalWarehouse.Table_ID)
			.getPO(getJP_PhysicalWarehouseDst_ID(), get_TrxName());
	}

	/** Set Physical Warehouse(Destination).
		@param JP_PhysicalWarehouseDst_ID Physical Warehouse(Destination)
	*/
	public void setJP_PhysicalWarehouseDst_ID (int JP_PhysicalWarehouseDst_ID)
	{
		if (JP_PhysicalWarehouseDst_ID < 1)
			set_Value (COLUMNNAME_JP_PhysicalWarehouseDst_ID, null);
		else
			set_Value (COLUMNNAME_JP_PhysicalWarehouseDst_ID, Integer.valueOf(JP_PhysicalWarehouseDst_ID));
	}

	/** Get Physical Warehouse(Destination).
		@return Physical Warehouse(Destination)	  */
	public int getJP_PhysicalWarehouseDst_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PhysicalWarehouseDst_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_PhysicalWarehouse getJP_PhysicalWarehouseFrom() throws RuntimeException
	{
		return (I_JP_PhysicalWarehouse)MTable.get(getCtx(), I_JP_PhysicalWarehouse.Table_ID)
			.getPO(getJP_PhysicalWarehouseFrom_ID(), get_TrxName());
	}

	/** Set Physical Warehouse(From).
		@param JP_PhysicalWarehouseFrom_ID Physical Warehouse(From)
	*/
	public void setJP_PhysicalWarehouseFrom_ID (int JP_PhysicalWarehouseFrom_ID)
	{
		if (JP_PhysicalWarehouseFrom_ID < 1)
			set_Value (COLUMNNAME_JP_PhysicalWarehouseFrom_ID, null);
		else
			set_Value (COLUMNNAME_JP_PhysicalWarehouseFrom_ID, Integer.valueOf(JP_PhysicalWarehouseFrom_ID));
	}

	/** Get Physical Warehouse(From).
		@return Physical Warehouse(From)	  */
	public int getJP_PhysicalWarehouseFrom_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PhysicalWarehouseFrom_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_PhysicalWarehouse getJP_PhysicalWarehouseNext() throws RuntimeException
	{
		return (I_JP_PhysicalWarehouse)MTable.get(getCtx(), I_JP_PhysicalWarehouse.Table_ID)
			.getPO(getJP_PhysicalWarehouseNext_ID(), get_TrxName());
	}

	/** Set Physical Warehouse(Next).
		@param JP_PhysicalWarehouseNext_ID Physical Warehouse(Next)
	*/
	public void setJP_PhysicalWarehouseNext_ID (int JP_PhysicalWarehouseNext_ID)
	{
		if (JP_PhysicalWarehouseNext_ID < 1)
			set_Value (COLUMNNAME_JP_PhysicalWarehouseNext_ID, null);
		else
			set_Value (COLUMNNAME_JP_PhysicalWarehouseNext_ID, Integer.valueOf(JP_PhysicalWarehouseNext_ID));
	}

	/** Get Physical Warehouse(Next).
		@return Physical Warehouse(Next)	  */
	public int getJP_PhysicalWarehouseNext_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PhysicalWarehouseNext_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_PhysicalWarehouse getJP_PhysicalWarehouseTo() throws RuntimeException
	{
		return (I_JP_PhysicalWarehouse)MTable.get(getCtx(), I_JP_PhysicalWarehouse.Table_ID)
			.getPO(getJP_PhysicalWarehouseTo_ID(), get_TrxName());
	}

	/** Set Physical Warehouse(To).
		@param JP_PhysicalWarehouseTo_ID Physical Warehouse(To)
	*/
	public void setJP_PhysicalWarehouseTo_ID (int JP_PhysicalWarehouseTo_ID)
	{
		if (JP_PhysicalWarehouseTo_ID < 1)
			set_Value (COLUMNNAME_JP_PhysicalWarehouseTo_ID, null);
		else
			set_Value (COLUMNNAME_JP_PhysicalWarehouseTo_ID, Integer.valueOf(JP_PhysicalWarehouseTo_ID));
	}

	/** Get Physical Warehouse(To).
		@return Physical Warehouse(To)	  */
	public int getJP_PhysicalWarehouseTo_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PhysicalWarehouseTo_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Process Now.
		@param JP_Processing1 Process Now
	*/
	public void setJP_Processing1 (String JP_Processing1)
	{
		set_Value (COLUMNNAME_JP_Processing1, JP_Processing1);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing1()
	{
		return (String)get_Value(COLUMNNAME_JP_Processing1);
	}

	/** Set Process Now.
		@param JP_Processing2 Process Now
	*/
	public void setJP_Processing2 (String JP_Processing2)
	{
		set_Value (COLUMNNAME_JP_Processing2, JP_Processing2);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing2()
	{
		return (String)get_Value(COLUMNNAME_JP_Processing2);
	}

	/** Set Process Now.
		@param JP_Processing3 Process Now
	*/
	public void setJP_Processing3 (String JP_Processing3)
	{
		set_Value (COLUMNNAME_JP_Processing3, JP_Processing3);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing3()
	{
		return (String)get_Value(COLUMNNAME_JP_Processing3);
	}

	/** Set Process Now.
		@param JP_Processing4 Process Now
	*/
	public void setJP_Processing4 (String JP_Processing4)
	{
		set_Value (COLUMNNAME_JP_Processing4, JP_Processing4);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing4()
	{
		return (String)get_Value(COLUMNNAME_JP_Processing4);
	}

	/** Set Process Now.
		@param JP_Processing5 Process Now
	*/
	public void setJP_Processing5 (String JP_Processing5)
	{
		set_Value (COLUMNNAME_JP_Processing5, JP_Processing5);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing5()
	{
		return (String)get_Value(COLUMNNAME_JP_Processing5);
	}

	/** Set Process Now.
		@param JP_Processing6 Process Now
	*/
	public void setJP_Processing6 (String JP_Processing6)
	{
		set_Value (COLUMNNAME_JP_Processing6, JP_Processing6);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing6()
	{
		return (String)get_Value(COLUMNNAME_JP_Processing6);
	}

	/** Set Production Qty(Fact).
		@param JP_ProductionQtyFact Production Qty(Fact)
	*/
	public void setJP_ProductionQtyFact (BigDecimal JP_ProductionQtyFact)
	{
		set_Value (COLUMNNAME_JP_ProductionQtyFact, JP_ProductionQtyFact);
	}

	/** Get Production Qty(Fact).
		@return Production Qty(Fact)	  */
	public BigDecimal getJP_ProductionQtyFact()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_JP_ProductionQtyFact);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Remarks.
		@param JP_Remarks JPIERE-0490:JPBP
	*/
	public void setJP_Remarks (String JP_Remarks)
	{
		set_Value (COLUMNNAME_JP_Remarks, JP_Remarks);
	}

	/** Get Remarks.
		@return JPIERE-0490:JPBP
	  */
	public String getJP_Remarks()
	{
		return (String)get_Value(COLUMNNAME_JP_Remarks);
	}

	/** Set Subject.
		@param JP_Subject JPIERE-0490:JPBP
	*/
	public void setJP_Subject (String JP_Subject)
	{
		set_Value (COLUMNNAME_JP_Subject, JP_Subject);
	}

	/** Get Subject.
		@return JPIERE-0490:JPBP
	  */
	public String getJP_Subject()
	{
		return (String)get_Value(COLUMNNAME_JP_Subject);
	}

	public org.compiere.model.I_M_Warehouse getJP_WarehouseDst() throws RuntimeException
	{
		return (org.compiere.model.I_M_Warehouse)MTable.get(getCtx(), org.compiere.model.I_M_Warehouse.Table_ID)
			.getPO(getJP_WarehouseDst_ID(), get_TrxName());
	}

	/** Set Org Warehouse(Destination).
		@param JP_WarehouseDst_ID Org Warehouse(Destination)
	*/
	public void setJP_WarehouseDst_ID (int JP_WarehouseDst_ID)
	{
		if (JP_WarehouseDst_ID < 1)
			set_Value (COLUMNNAME_JP_WarehouseDst_ID, null);
		else
			set_Value (COLUMNNAME_JP_WarehouseDst_ID, Integer.valueOf(JP_WarehouseDst_ID));
	}

	/** Get Org Warehouse(Destination).
		@return Org Warehouse(Destination)	  */
	public int getJP_WarehouseDst_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_WarehouseDst_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Warehouse getJP_WarehouseFrom() throws RuntimeException
	{
		return (org.compiere.model.I_M_Warehouse)MTable.get(getCtx(), org.compiere.model.I_M_Warehouse.Table_ID)
			.getPO(getJP_WarehouseFrom_ID(), get_TrxName());
	}

	/** Set Org Warehouse(From).
		@param JP_WarehouseFrom_ID Storage Warehouse and Service Point
	*/
	public void setJP_WarehouseFrom_ID (int JP_WarehouseFrom_ID)
	{
		if (JP_WarehouseFrom_ID < 1)
			set_Value (COLUMNNAME_JP_WarehouseFrom_ID, null);
		else
			set_Value (COLUMNNAME_JP_WarehouseFrom_ID, Integer.valueOf(JP_WarehouseFrom_ID));
	}

	/** Get Org Warehouse(From).
		@return Storage Warehouse and Service Point
	  */
	public int getJP_WarehouseFrom_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_WarehouseFrom_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Warehouse getJP_WarehouseNext() throws RuntimeException
	{
		return (org.compiere.model.I_M_Warehouse)MTable.get(getCtx(), org.compiere.model.I_M_Warehouse.Table_ID)
			.getPO(getJP_WarehouseNext_ID(), get_TrxName());
	}

	/** Set Org Warehouse(Next).
		@param JP_WarehouseNext_ID Org Warehouse(Next)
	*/
	public void setJP_WarehouseNext_ID (int JP_WarehouseNext_ID)
	{
		if (JP_WarehouseNext_ID < 1)
			set_Value (COLUMNNAME_JP_WarehouseNext_ID, null);
		else
			set_Value (COLUMNNAME_JP_WarehouseNext_ID, Integer.valueOf(JP_WarehouseNext_ID));
	}

	/** Get Org Warehouse(Next).
		@return Org Warehouse(Next)	  */
	public int getJP_WarehouseNext_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_WarehouseNext_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Warehouse getJP_WarehouseTo() throws RuntimeException
	{
		return (org.compiere.model.I_M_Warehouse)MTable.get(getCtx(), org.compiere.model.I_M_Warehouse.Table_ID)
			.getPO(getJP_WarehouseTo_ID(), get_TrxName());
	}

	/** Set Org Warehouse(To).
		@param JP_WarehouseTo_ID Storage Warehouse and Service Point
	*/
	public void setJP_WarehouseTo_ID (int JP_WarehouseTo_ID)
	{
		if (JP_WarehouseTo_ID < 1)
			set_Value (COLUMNNAME_JP_WarehouseTo_ID, null);
		else
			set_Value (COLUMNNAME_JP_WarehouseTo_ID, Integer.valueOf(JP_WarehouseTo_ID));
	}

	/** Get Org Warehouse(To).
		@return Storage Warehouse and Service Point
	  */
	public int getJP_WarehouseTo_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_WarehouseTo_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Locator getM_Locator() throws RuntimeException
	{
		return (org.compiere.model.I_M_Locator)MTable.get(getCtx(), org.compiere.model.I_M_Locator.Table_ID)
			.getPO(getM_Locator_ID(), get_TrxName());
	}

	/** Set Locator.
		@param M_Locator_ID Warehouse Locator
	*/
	public void setM_Locator_ID (int M_Locator_ID)
	{
		if (M_Locator_ID < 1)
			set_Value (COLUMNNAME_M_Locator_ID, null);
		else
			set_Value (COLUMNNAME_M_Locator_ID, Integer.valueOf(M_Locator_ID));
	}

	/** Get Locator.
		@return Warehouse Locator
	  */
	public int getM_Locator_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Locator_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException
	{
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_ID)
			.getPO(getM_Product_ID(), get_TrxName());
	}

	/** Set Product.
		@param M_Product_ID Product, Service, Item
	*/
	public void setM_Product_ID (int M_Product_ID)
	{
		if (M_Product_ID < 1)
			set_ValueNoCheck (COLUMNNAME_M_Product_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
	}

	/** Get Product.
		@return Product, Service, Item
	  */
	public int getM_Product_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Name.
		@param Name Alphanumeric identifier of the entity
	*/
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName()
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

	/** Set Posted.
		@param Posted Posting status
	*/
	public void setPosted (boolean Posted)
	{
		set_Value (COLUMNNAME_Posted, Boolean.valueOf(Posted));
	}

	/** Get Posted.
		@return Posting status
	  */
	public boolean isPosted()
	{
		Object oo = get_Value(COLUMNNAME_Posted);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Processed.
		@param Processed The document has been processed
	*/
	public void setProcessed (boolean Processed)
	{
		set_Value (COLUMNNAME_Processed, Boolean.valueOf(Processed));
	}

	/** Get Processed.
		@return The document has been processed
	  */
	public boolean isProcessed()
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

	/** Set Processed On.
		@param ProcessedOn The date+time (expressed in decimal format) when the document has been processed
	*/
	public void setProcessedOn (BigDecimal ProcessedOn)
	{
		set_Value (COLUMNNAME_ProcessedOn, ProcessedOn);
	}

	/** Get Processed On.
		@return The date+time (expressed in decimal format) when the document has been processed
	  */
	public BigDecimal getProcessedOn()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_ProcessedOn);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Process Now.
		@param Processing Process Now
	*/
	public void setProcessing (boolean Processing)
	{
		set_Value (COLUMNNAME_Processing, Boolean.valueOf(Processing));
	}

	/** Get Process Now.
		@return Process Now	  */
	public boolean isProcessing()
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

	/** Set Production Quantity.
		@param ProductionQty Quantity of products to produce
	*/
	public void setProductionQty (BigDecimal ProductionQty)
	{
		set_Value (COLUMNNAME_ProductionQty, ProductionQty);
	}

	/** Get Production Quantity.
		@return Quantity of products to produce
	  */
	public BigDecimal getProductionQty()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_ProductionQty);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public org.compiere.model.I_AD_User getSalesRep() throws RuntimeException
	{
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_ID)
			.getPO(getSalesRep_ID(), get_TrxName());
	}

	/** Set Sales Rep.
		@param SalesRep_ID Sales Representative or Company Agent
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
	public int getSalesRep_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_SalesRep_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** Set UPC/EAN.
		@param UPC Bar Code (Universal Product Code or its superset European Article Number)
	*/
	public void setUPC (String UPC)
	{
		set_Value (COLUMNNAME_UPC, UPC);
	}

	/** Get UPC/EAN.
		@return Bar Code (Universal Product Code or its superset European Article Number)
	  */
	public String getUPC()
	{
		return (String)get_Value(COLUMNNAME_UPC);
	}

	public org.compiere.model.I_C_ElementValue getUser1() throws RuntimeException
	{
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_ID)
			.getPO(getUser1_ID(), get_TrxName());
	}

	/** Set User Element List 1.
		@param User1_ID User defined list element #1
	*/
	public void setUser1_ID (int User1_ID)
	{
		if (User1_ID < 1)
			set_Value (COLUMNNAME_User1_ID, null);
		else
			set_Value (COLUMNNAME_User1_ID, Integer.valueOf(User1_ID));
	}

	/** Get User Element List 1.
		@return User defined list element #1
	  */
	public int getUser1_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_User1_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ElementValue getUser2() throws RuntimeException
	{
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_ID)
			.getPO(getUser2_ID(), get_TrxName());
	}

	/** Set User Element List 2.
		@param User2_ID User defined list element #2
	*/
	public void setUser2_ID (int User2_ID)
	{
		if (User2_ID < 1)
			set_Value (COLUMNNAME_User2_ID, null);
		else
			set_Value (COLUMNNAME_User2_ID, Integer.valueOf(User2_ID));
	}

	/** Get User Element List 2.
		@return User defined list element #2
	  */
	public int getUser2_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_User2_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Search Key.
		@param Value Search key for the record in the format required - must be unique
	*/
	public void setValue (String Value)
	{
		set_Value (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue()
	{
		return (String)get_Value(COLUMNNAME_Value);
	}
}