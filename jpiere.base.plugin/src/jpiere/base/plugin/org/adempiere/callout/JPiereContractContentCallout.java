/******************************************************************************
 * Product: JPiere                                                            *
 * Copyright (C) Hideaki Hagiwara (h.hagiwara@oss-erp.co.jp)                  *
 *                                                                            *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY.                          *
 * See the GNU General Public License for more details.                       *
 *                                                                            *
 * JPiere is maintained by OSS ERP Solutions Co., Ltd.                        *
 * (http://www.oss-erp.co.jp)                                                 *
 *****************************************************************************/
package jpiere.base.plugin.org.adempiere.callout;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MDocType;
import org.compiere.model.MLocator;
import org.compiere.model.MOrder;
import org.compiere.model.MOrgInfo;
import org.compiere.model.MPriceList;
import org.compiere.model.MPriceListVersion;
import org.compiere.model.MWarehouse;
import org.compiere.model.X_C_Order;
import org.compiere.process.DocAction;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.I_JP_ContractContent;
import jpiere.base.plugin.org.adempiere.model.I_JP_ContractContentT;
import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractCalender;
import jpiere.base.plugin.org.adempiere.model.MContractCalenderList;
import jpiere.base.plugin.org.adempiere.model.MContractCalenderRef;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractContentT;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;
import jpiere.base.plugin.org.adempiere.model.MContractProcessList;
import jpiere.base.plugin.org.adempiere.model.MContractProcessRef;

/**
 *
 *  JPiere Contract Content CallOut
 *
 *  JPIERE-0363:JPBP
 *
 * @author Hideaki Hagiwara
 *
 */
public class JPiereContractContentCallout implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{

		String msg = null;

		if(mField.getColumnName().equals("JP_BaseDocDocType_ID"))
		{

			msg = calloutJP_BaseDocDocType_ID(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("JP_ContractCalender_ID")){

			msg = calloutJP_ContractCalender_ID(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mTab.getTableName().equals(MContractContent.Table_Name) &&  mField.getColumnName().equals("JP_ContractContentT_ID")){

			msg = calloutJP_ContractContentT_ID(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mTab.getTableName().equals(MContractContentT.Table_Name) &&  mField.getColumnName().equals("JP_ContractContentT_ID")){

			msg = calloutSetPriceListInfo(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("M_PriceList_ID") || mField.getColumnName().equals("DateDoc")
				|| mField.getColumnName().equals("JP_ContractContent_ID") || mField.getColumnName().equals("DateInvoiced") ){

			msg = calloutSetPriceListInfo(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("IsAutomaticUpdateJP")){

			msg = calloutIsAutomaticUpdateJP(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("C_BPartner_ID")){

			msg = calloutC_BPartner_ID(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("DocBaseType")){

			msg = calloutDocBaseType(ctx, WindowNo, mTab, mField, value, oldValue);
		}

		return msg;

	}

	private String calloutJP_BaseDocDocType_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		if( value == null)
		{
			mTab.setValue ("OrderType",  "--");

		}else{

			Integer JP_BaseDocDocType_ID = (Integer)value;
			MDocType docType = MDocType.get(ctx, JP_BaseDocDocType_ID.intValue());
			mTab.setValue("IsSOTrx", docType.isSOTrx());

			if(docType.getDocBaseType().equals(MDocType.DOCBASETYPE_SalesOrder)
					|| docType.getDocBaseType().equals(MDocType.DOCBASETYPE_PurchaseOrder))
			{
				String DocSubTypeSO = docType.getDocSubTypeSO();
				mTab.setValue ("OrderType", DocSubTypeSO);
				if(Util.isEmpty(DocSubTypeSO))
				{
					return Msg.getMsg(ctx,"JP_Null") + Msg.getElement(ctx, "DocSubTypeSO") +" - " + Msg.getElement(ctx, "C_DocType_ID")  ;
				}


				if(!docType.getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_StandardOrder)
						&& !docType.getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_Quotation)
						&& !docType.getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_Proposal) )
				{
					String JP_ContractType = (String)Env.getContext(ctx, WindowNo, "JP_ContractType");
					if(JP_ContractType.equals("PDC"))
						mTab.setValue("JP_CreateDerivativeDocPolicy", "MA");
					else if(JP_ContractType.equals("STC"))
						mTab.setValue("JP_CreateDerivativeDocPolicy", null);
				}

			}else{
				mTab.setValue ("OrderType", "--");
				mTab.setValue("JP_CreateDerivativeDocPolicy", null);
			}


		}

		return null;
	}

	private String calloutJP_ContractCalender_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		if( value != null)
		{
			int JP_ContractContentT_ID =  ((Integer)mTab.getValue("JP_ContractContentT_ID")).intValue();
			MContractContentT contentTemplate= MContractContentT.get(ctx, JP_ContractContentT_ID);
			int JP_Contract_ID = ((Integer)mTab.getValue("JP_Contract_ID")).intValue();
			MContract contract = MContract.get(ctx, JP_Contract_ID);

			//Calculate JP_ContractProcDate_From
			if(contentTemplate.getJP_ContractProcPOffset()==0)
			{
				mTab.setValue ("JP_ContractProcDate_From", contract.getJP_ContractPeriodDate_From());

			}else{

				int processPeriodOffset = contentTemplate.getJP_ContractProcPOffset();
				if(processPeriodOffset > 0)
					processPeriodOffset++;
				else
					processPeriodOffset--;

				int JP_ContractCalender_ID = ((Integer)value).intValue();
				MContractCalender calender = MContractCalender.get(ctx, JP_ContractCalender_ID);
				MContractProcPeriod period = calender.getContractProcessPeriod(ctx, contract.getJP_ContractPeriodDate_From(), null ,processPeriodOffset);
				if(period == null)
					return Msg.getMsg(ctx, "NotFound") +" : " +Msg.getElement(ctx, "JP_ContractProcPeriod_ID");

				mTab.setValue ("JP_ContractProcDate_From", period.getStartDate());
			}

			//Calculate JP_ContractProcDate_To
			if(contentTemplate.getJP_ContractProcPeriodNum()==0)
			{
				mTab.setValue ("JP_ContractProcDate_To", contract.getJP_ContractPeriodDate_To());
			}else{

				int JP_ContractCalender_ID = ((Integer)value).intValue();
				MContractCalender calender = MContractCalender.get(ctx, JP_ContractCalender_ID);
				MContractProcPeriod period = calender.getContractProcessPeriod(ctx, (Timestamp)mTab.getValue("JP_ContractProcDate_From"), null
																	,contentTemplate.getJP_ContractProcPeriodNum());

				if(period == null)
					return Msg.getMsg(ctx, "NotFound") +" : " +Msg.getElement(ctx, "JP_ContractProcPeriod_ID");

				mTab.setValue ("JP_ContractProcDate_To", period.getEndDate());
			}
		}

		return null;
	}

	private String calloutJP_ContractContentT_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		if( value != null)
		{
			int JP_ContractContentT_ID =  Integer.parseInt(value.toString());
			MContractContentT contentTemplate= MContractContentT.get(ctx, JP_ContractContentT_ID);
			GridField[] fields = mTab.getFields();
			String columnName = null;
			int columnIndex = -1;
			Object objectValue = null;
			for(int i = 0 ; i < fields.length; i++)
			{
				columnName = fields[i].getColumnName();
				columnIndex = -1;
				objectValue = null;
				if(columnName.equals("JP_ContractContentT_ID")
						|| columnName.equals("JP_ContractContent_ID")
						|| columnName.equals("JP_ContractContent_UU")
						|| columnName.equals("JP_Contract_ID")
						|| columnName.equals("AD_Client_ID")
						|| columnName.equals("AD_Org_ID")
						|| columnName.equals("AD_OrgTrx_ID")
						|| columnName.equals("IsActive")
						|| columnName.equals("Created")
						|| columnName.equals("CreatedBy")
						|| columnName.equals("Updated")
						|| columnName.equals("UpdatedBy")
						|| columnName.equals("TotalLines")
					)
				{
					continue;
				}

				if(!fields[i].isAllowCopy())
					continue;

				columnIndex = contentTemplate.get_ColumnIndex(columnName);
				if(columnIndex > -1)
				{
					objectValue = contentTemplate.get_Value(columnIndex);
					if(objectValue != null)
					{
						if(columnName.equals("M_Warehouse_ID"))
						{
							MWarehouse wh = MWarehouse.get(Env.getCtx(), Integer.parseInt(objectValue.toString()));
							int AD_Org_ID =  ((Integer)mTab.getValue("AD_Org_ID")).intValue();
							if(wh.getAD_Org_ID() == AD_Org_ID)
							{
								mTab.setValue(columnName, objectValue);
							}else {

								MOrgInfo orgInfo = MOrgInfo.get(Env.getCtx(), AD_Org_ID, null);
								if(orgInfo.getM_Warehouse_ID() > 0)
								{
									mTab.setValue(columnName, orgInfo.getM_Warehouse_ID());
								}

							}

						}else if(columnName.equals("JP_Locator_ID")) {

							MLocator loc = MLocator.get(Env.getCtx(), Integer.parseInt(objectValue.toString()));
							MWarehouse wh = MWarehouse.get(Env.getCtx(), loc.getM_Warehouse_ID());
							int AD_Org_ID =  ((Integer)mTab.getValue("AD_Org_ID")).intValue();
							Object objectM_Warehouse_ID = contentTemplate.get_Value("M_Warehouse_ID");
							int M_Warehouse_ID = Integer.parseInt(objectM_Warehouse_ID.toString());

							if(wh.getAD_Org_ID() == AD_Org_ID && wh.getM_Warehouse_ID() == M_Warehouse_ID)
							{
								mTab.setValue(columnName, objectValue);
							}

						}else {
							mTab.setValue(columnName, objectValue);
						}
					}

				}else if(columnName.equals("JP_ContractCalender_ID")) {

					int JP_ContractCalenderRef_ID = contentTemplate.getJP_ContractCalenderRef_ID();
					if(JP_ContractCalenderRef_ID > 0)
					{
						MContractCalenderRef   ccr = MContractCalenderRef.get(Env.getCtx(), JP_ContractCalenderRef_ID);
						MContractCalenderList[]  ccList =  ccr.getContractCalenderList(Env.getCtx(), false, null);
						if(ccList.length==1)
						{
							mTab.setValue("JP_ContractCalender_ID", ccList[0].getJP_ContractCalender_ID());
						}
					}

				}else if(columnName.equals("JP_ContractProcess_ID")) {

					int JP_ContractProcessRef_ID = contentTemplate.getJP_ContractProcessRef_ID();
					if(JP_ContractProcessRef_ID > 0)
					{
						MContractProcessRef   cpr = MContractProcessRef.get(Env.getCtx(), JP_ContractProcessRef_ID);
						MContractProcessList[]  cpList =  cpr.getContractProcessList(Env.getCtx(), false, null);
						if(cpList.length==1)
						{
							mTab.setValue("JP_ContractProcess_ID", cpList[0].getJP_ContractProcess_ID());
						}
					}
				}
			}//for
		}

		return calloutSetPriceListInfo(ctx, WindowNo, mTab,mField, value, oldValue);
	}

	private String calloutSetPriceListInfo(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		Integer M_PriceList_ID = (Integer) mTab.getValue("M_PriceList_ID");
		if (M_PriceList_ID == null || M_PriceList_ID.intValue()== 0)
		{
			Env.setContext(ctx, WindowNo, "M_PriceList_ID", "");
			Env.setContext(ctx, WindowNo, "M_PriceList_Version_ID", "");
			return "";
		}

		MPriceList pl = MPriceList.get(ctx, M_PriceList_ID, null);
		Env.setContext(ctx, WindowNo, "M_PriceList_ID", M_PriceList_ID);
		if (pl != null && pl.getM_PriceList_ID() == M_PriceList_ID)
		{

			//	Tax Included
			mTab.setValue("IsTaxIncluded", pl.isTaxIncluded());
			//	Currency
			mTab.setValue("C_Currency_ID", pl.getC_Currency_ID());

			//	Price Limit Enforce
			Env.setContext(ctx, WindowNo, "EnforcePriceLimit", pl.isEnforcePriceLimit());

			if(mTab.getValue("C_BPartner_ID") != null)
				Env.setContext(ctx, WindowNo, "C_BPartner_ID", (Integer)mTab.getValue("C_BPartner_ID"));

			//PriceList Version
			Timestamp date = null;
			if (mTab.getAD_Table_ID() == I_JP_ContractContent.Table_ID)
				date =(Timestamp)mTab.getValue("DateDoc");
			else if (mTab.getAD_Table_ID() == I_JP_ContractContentT.Table_ID)
				date = (Timestamp)mTab.getValue("DateInvoiced");

			MPriceListVersion plv = pl.getPriceListVersion(date);
			if (plv != null && plv.getM_PriceList_Version_ID() > 0) {
				Env.setContext(ctx, WindowNo, "M_PriceList_Version_ID", plv.getM_PriceList_Version_ID());
			} else {
				Env.setContext(ctx, WindowNo, "M_PriceList_Version_ID", "");
			}

		}//if

		return null;
	}

	private String calloutIsAutomaticUpdateJP(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		boolean  isAutomaticUpdateJP = mTab.getValueAsBoolean("IsAutomaticUpdateJP");

		if(isAutomaticUpdateJP)
		{
			Integer JP_Contract_ID = (Integer) mTab.getValue("JP_Contract_ID");
			MContract contract = new MContract(ctx, JP_Contract_ID.intValue(), null);

			if(contract.getJP_ContractType().contentEquals(MContract.JP_CONTRACTTYPE_PeriodContract))
			{
				if(contract.isAutomaticUpdateJP())
				{
					String JP_ContractProcStatus = (String) mTab.getValue("JP_ContractProcStatus");

					if(JP_ContractProcStatus.equals(MContractContent.JP_CONTRACTPROCSTATUS_Invalid)
							|| JP_ContractProcStatus.equals(MContractContent.JP_CONTRACTPROCSTATUS___) )
					{
						;//Nothing to do;
					}else {

						String JP_ContractC_AutoUpdatePolicy = (String) mTab.getValue("JP_ContractC_AutoUpdatePolicy");
						String DocStatus = (String) mTab.getValue("DocStatus");

						if(JP_ContractC_AutoUpdatePolicy == null)
						{
							if(DocStatus.equals(DocAction.STATUS_Closed) || DocStatus.equals(DocAction.STATUS_Reversed) || DocStatus.equals(DocAction.STATUS_Voided))
							{
								;//Nothing to do;

							}else {
								mTab.setValue("JP_ContractProcDate_To", contract.getJP_ContractPeriodDate_To());
							}

						}else if(JP_ContractC_AutoUpdatePolicy.equals(MContractContent.JP_CONTRACTC_AUTOUPDATEPOLICY_RenewTheContractContent)){

							if(DocStatus.equals(DocAction.STATUS_Closed) || DocStatus.equals(DocAction.STATUS_Reversed) || DocStatus.equals(DocAction.STATUS_Voided))
							{
								;//Nothing to do;
							}else if(mTab.getValueAsBoolean("IsRenewedContractContentJP")) {
								;//Nothing to do;
							}else {
								mTab.setValue("JP_ContractProcDate_To", contract.getJP_ContractPeriodDate_To());
							}

						}else if(JP_ContractC_AutoUpdatePolicy.equals(MContractContent.JP_CONTRACTC_AUTOUPDATEPOLICY_ExtendContractProcessDate)) {

							if(DocStatus.equals(DocAction.STATUS_Closed) || DocStatus.equals(DocAction.STATUS_Reversed) || DocStatus.equals(DocAction.STATUS_Voided))
							{
								;//Nothing to do;

							}else {
								mTab.setValue("JP_ContractProcDate_To", contract.getJP_ContractPeriodDate_To());
							}

						}
					}

				}else {
					return Msg.getMsg(ctx, "JP_IsAutomaticUpdateJP_UpdateError");
				}
			}
		}

		return null;
	}

	private String calloutC_BPartner_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		Integer C_BPartner_ID = (Integer)value;
		if (C_BPartner_ID == null || C_BPartner_ID.intValue() == 0)
		{
			Env.setContext(ctx, WindowNo, "C_BPartner_ID", "");
			return "";
		}

		Env.setContext(ctx, WindowNo, "C_BPartner_ID", C_BPartner_ID);
		String sql = "SELECT p.AD_Language,p.C_PaymentTerm_ID,"
			+ " COALESCE(p.M_PriceList_ID,g.M_PriceList_ID) AS M_PriceList_ID, p.PaymentRule,p.POReference,"
			+ " p.SO_Description,p.IsDiscountPrinted,"
			+ " p.InvoiceRule,p.DeliveryRule,p.FreightCostRule,DeliveryViaRule,"
			+ " p.SO_CreditLimit, p.SO_CreditLimit-p.SO_CreditUsed AS CreditAvailable,"
			+ " (select max(lship.C_BPartner_Location_ID) from C_BPartner_Location lship where p.C_BPartner_ID=lship.C_BPartner_ID AND lship.IsShipTo='Y' AND lship.IsActive='Y') as C_BPartner_Location_ID,"
			+ " (select max(c.AD_User_ID) from AD_User c where p.C_BPartner_ID=c.C_BPartner_ID AND c.IsActive='Y') as AD_User_ID,"
			+ " COALESCE(p.PO_PriceList_ID,g.PO_PriceList_ID) AS PO_PriceList_ID, p.PaymentRulePO,p.PO_PaymentTerm_ID,"
			+ " (select max(lbill.C_BPartner_Location_ID) from C_BPartner_Location lbill where p.C_BPartner_ID=lbill.C_BPartner_ID AND lbill.IsBillTo='Y' AND lbill.IsActive='Y') AS Bill_Location_ID, "
			+ " p.SOCreditStatus, "
			+ " p.SalesRep_ID "
			+ "FROM C_BPartner p"
			+ " INNER JOIN C_BP_Group g ON (p.C_BP_Group_ID=g.C_BP_Group_ID)"
			+ "WHERE p.C_BPartner_ID=? AND p.IsActive='Y'";		//	#1

		boolean IsSOTrx = "Y".equals(Env.getContext(ctx, WindowNo, mTab.getTabNo(), "IsSOTrx"));
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, C_BPartner_ID.intValue());
			rs = pstmt.executeQuery();
			if (rs.next())
			{
				// Sales Rep - If BP has a default SalesRep then default it
				Integer salesRep = rs.getInt("SalesRep_ID");
				if (IsSOTrx && salesRep != 0 )
				{
					mTab.setValue("SalesRep_ID", salesRep);
				}

				//	PriceList (indirect: IsTaxIncluded & Currency)
				Integer ii = Integer.valueOf(rs.getInt(IsSOTrx ? "M_PriceList_ID" : "PO_PriceList_ID"));
				if (!rs.wasNull())
					mTab.setValue("M_PriceList_ID", ii);
				else
				{	//	get default PriceList
					int i = Env.getContextAsInt(ctx, "#M_PriceList_ID");
					if (i != 0)
					{
						MPriceList pl = new MPriceList(ctx, i, null);
						if (IsSOTrx == pl.isSOPriceList())
							mTab.setValue("M_PriceList_ID", Integer.valueOf(i));
						else
						{
							String sql2 = "SELECT M_PriceList_ID FROM M_PriceList WHERE AD_Client_ID=? AND IsSOPriceList=? AND IsActive='Y' ORDER BY IsDefault DESC";
							ii = DB.getSQLValue (null, sql2, Env.getAD_Client_ID(ctx), IsSOTrx);
							if (ii != 0)
								mTab.setValue("M_PriceList_ID", Integer.valueOf(ii));
						}
					}
				}

				//	Bill-To
				mTab.setValue("Bill_BPartner_ID", C_BPartner_ID);

				int shipTo_ID = 0;
				int bill_Location_ID =0;
				//	overwritten by InfoBP selection - works only if InfoWindow
				//	was used otherwise creates error (uses last value, may belong to different BP)
				if (C_BPartner_ID.toString().equals(Env.getContext(ctx, WindowNo, Env.TAB_INFO, "C_BPartner_ID")))
				{
					String loc = Env.getContext(ctx, WindowNo, Env.TAB_INFO, "C_BPartner_Location_ID");
					int locationId = 0;
					if (loc.length() > 0)
						locationId = Integer.parseInt(loc);
					if (locationId > 0) {
						MBPartnerLocation bpLocation = new MBPartnerLocation(ctx, locationId, null);
						if (bpLocation.isBillTo())
							bill_Location_ID = locationId;
						if (bpLocation.isShipTo())
							shipTo_ID = locationId;
					}
				}
				if (bill_Location_ID == 0)
					bill_Location_ID = rs.getInt("Bill_Location_ID");
				if (bill_Location_ID == 0)
					mTab.setValue("Bill_Location_ID", null);
				else
					mTab.setValue("Bill_Location_ID", Integer.valueOf(bill_Location_ID));
				// Ship-To Location
				if (shipTo_ID == 0)
					shipTo_ID = rs.getInt("C_BPartner_Location_ID");

				if (shipTo_ID == 0)
					mTab.setValue("C_BPartner_Location_ID", null);
				else
					mTab.setValue("C_BPartner_Location_ID", Integer.valueOf(shipTo_ID));

				//	Contact - overwritten by InfoBP selection
				int contID = rs.getInt("AD_User_ID");
				if (C_BPartner_ID.toString().equals(Env.getContext(ctx, WindowNo, Env.TAB_INFO, "C_BPartner_ID")))
				{
					String cont = Env.getContext(ctx, WindowNo, Env.TAB_INFO, "AD_User_ID");
					if (cont.length() > 0)
						contID = Integer.parseInt(cont);
				}
				if (contID == 0)
					mTab.setValue("AD_User_ID", null);
				else
				{
					mTab.setValue("AD_User_ID", Integer.valueOf(contID));
					mTab.setValue("Bill_User_ID", Integer.valueOf(contID));
				}

				//	CreditAvailable
				if (IsSOTrx)
				{
					double CreditLimit = rs.getDouble("SO_CreditLimit");
					if (CreditLimit != 0)
					{
						double CreditAvailable = rs.getDouble("CreditAvailable");
						if (!rs.wasNull() && CreditAvailable < 0)
							mTab.fireDataStatusEEvent("CreditLimitOver",
								DisplayType.getNumberFormat(DisplayType.Amount).format(CreditAvailable),
								false);
					}
				}

				//	PO Reference
				String s = rs.getString("POReference");
				if (s != null && s.length() != 0)
					mTab.setValue("POReference", s);
				//	SO Description
				s = rs.getString("SO_Description");
				if (s != null && s.trim().length() != 0)
					mTab.setValue("Description", s);
				//	IsDiscountPrinted
				s = rs.getString("IsDiscountPrinted");
				if (s != null && s.length() != 0)
					mTab.setValue("IsDiscountPrinted", s);
				else
					mTab.setValue("IsDiscountPrinted", "N");

				//	Defaults, if not Walkin Receipt or Walkin Invoice
				String OrderType = Env.getContext(ctx, WindowNo, "OrderType");
				mTab.setValue("InvoiceRule", X_C_Order.INVOICERULE_AfterDelivery);
				mTab.setValue("DeliveryRule", X_C_Order.DELIVERYRULE_Availability);
				mTab.setValue("PaymentRule", X_C_Order.PAYMENTRULE_OnCredit);
				if (OrderType.equals(MOrder.DocSubTypeSO_Prepay))
				{
					mTab.setValue("InvoiceRule", X_C_Order.INVOICERULE_Immediate);
					mTab.setValue("DeliveryRule", X_C_Order.DELIVERYRULE_AfterPayment);
				}
				else if (OrderType.equals(MOrder.DocSubTypeSO_POS))	//  for POS
					mTab.setValue("PaymentRule", X_C_Order.PAYMENTRULE_Cash);
				else
				{
					//	PaymentRule
					s = rs.getString(IsSOTrx ? "PaymentRule" : "PaymentRulePO");
					if (s != null && s.length() != 0)
						mTab.setValue("PaymentRule", s);
					//	Payment Term
					ii = Integer.valueOf(rs.getInt(IsSOTrx ? "C_PaymentTerm_ID" : "PO_PaymentTerm_ID"));
					if (!rs.wasNull())
						mTab.setValue("C_PaymentTerm_ID", ii);
					//	InvoiceRule
					s = rs.getString("InvoiceRule");
					if (s != null && s.length() != 0)
						mTab.setValue("InvoiceRule", s);
					//	DeliveryRule
					s = rs.getString("DeliveryRule");
					if (s != null && s.length() != 0)
						mTab.setValue("DeliveryRule", s);
					//	FreightCostRule
					s = rs.getString("FreightCostRule");
					if (s != null && s.length() != 0)
						mTab.setValue("FreightCostRule", s);
					//	DeliveryViaRule
					s = rs.getString("DeliveryViaRule");
					if (s != null && s.length() != 0)
						mTab.setValue("DeliveryViaRule", s);
				}
			}
		}
		catch (SQLException e)
		{
			return e.getLocalizedMessage();
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}


		return null;
	}

	private String calloutDocBaseType(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		if( value == null)
			return null;

		String docBaseType = (String)value;
		if(docBaseType.equals(MContractContent.DOCBASETYPE_SalesOrder)
				|| docBaseType.equals(MContractContent.DOCBASETYPE_MaterialDelivery)
				|| docBaseType.equals(MContractContent.DOCBASETYPE_ARInvoice))
		{
			mTab.setValue("IsSOTrx", true);
		}else {
			mTab.setValue("IsSOTrx", false);
		}


		return null;
	}
}
