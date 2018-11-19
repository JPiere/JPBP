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
package jpiere.base.plugin.webui.apps.form;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListModelTable;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.component.WAppsAction;
import org.adempiere.webui.component.WListbox;
import org.adempiere.webui.editor.WDateEditor;
import org.adempiere.webui.editor.WNumberEditor;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.event.WTableModelEvent;
import org.adempiere.webui.event.WTableModelListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.IFormController;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.adempiere.webui.window.FDialog;
import org.compiere.minigrid.IMiniTable;
import org.compiere.model.MAllocationHdr;
import org.compiere.model.MAllocationLine;
import org.compiere.model.MBankAccount;
import org.compiere.model.MColumn;
import org.compiere.model.MInvoice;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MPayment;
import org.compiere.model.MRole;
import org.compiere.process.DocAction;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;
import org.compiere.util.TimeUtil;
import org.compiere.util.Trx;
import org.compiere.util.TrxRunnable;
import org.compiere.util.Util;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.A;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Center;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.North;
import org.zkoss.zul.Separator;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;

public class JPARInvoiceAllocation implements IFormController, EventListener<Event>, ValueChangeListener,WTableModelListener{

	//form
	private CustomForm form = new CustomForm();

	//Logger
	private static CLogger log = CLogger.getCLogger(JPARInvoiceAllocation.class);

	private boolean     m_calculating = false;

	private static final String SELECT_DESELECT_ALL = "SelectAll";

	/***Binding Variables with component***/
	//search criteria of AR Invoice
	private int         Invoice_Org_ID = 0;			//C_Invoice.AD_Org_ID for Search AR Invoices
	private int         Invoice_Currency_ID = 0;		//C_Invoice.C_Currency_ID for Search AR Invoices
	private int         Invoice_BP_ID = 0;				//C_Invoice.C_BPartner_ID for Search AR Invoices
	private int         JP_Corporation_ID= 0;
	private Timestamp 	 DateInvoiedFrom = null;
	private Timestamp 	 DateInvoiedTo = null;


	//Creating condition of Income Payment
	private int		 Payment_Org_ID = 0;			//C_Payment.AD_Org_ID for Createing Income Payment
	private int         Payment_BP_ID = 0;				//C_Payment.C_Bpartner_ID for Createing Income Payment
	private int         Payment_DocType_ID = 0;		//C_Payment.C_DocType_ID for Createing Income Payment
	private int         Payment_BankAccount_ID = 0;	//C_Payment.C_BankAccount_ID for Createing Income Payment
	private int         Payment_Currency_ID = 0;		//C_Payment.C_Currency_ID for Createing Income Payment
	private BigDecimal  Payment_PayAmt = Env.ZERO;		//C_Payment.PayAmt for Createing Income Payment


	private ArrayList<Integer>	m_bpartnerCheck = new ArrayList<Integer>();

	private DecimalFormat format = DisplayType.getNumberFormat(DisplayType.Amount,Env.getLanguage(Env.getCtx()));

	private int         m_noInvoices = 0;
	private BigDecimal	totalInv = Env.ZERO;
	private BigDecimal 	totalPay = Env.ZERO;
	private BigDecimal	totalDiff = Env.ZERO;

	private Timestamp allocDate = null;

	/*【メインレイアウト】*/
	private Borderlayout mainLayout = new Borderlayout();
	private Panel southPanel = new Panel();

	/*【パラメータパネル】*/
	private Panel parameterPanel = new Panel();						//検索条件などを設定するパラメータパネル
	private Grid parameterLayout = GridFactory.newGridLayout();		//パラメータパネルのレイアウト
	//売上請求伝票検索パネル
	//1段目
	private Label DateInvoiceFrom_Label = new Label();					//売上請求伝票検索用日付ラベル
	private WDateEditor DateInvoiceFrom_Editor = new WDateEditor("DateInvoiceFrom",true,false,true,"");
	private WDateEditor DateInvoiceTo_Editor = new WDateEditor("DateInvoiceTo",true,false,true,"");
	private Label Invoice_Org_Label = new Label();					//売上請求伝票検索用組織マスタラベル
	private WSearchEditor Invoice_Org_Editor;						//売上請求伝票検索用組織マスタ選択リスト
	private Label Invoice_Currency_Label = new Label();				//入金伝票作成用通貨ラベル
	private WSearchEditor Invoice_Currency_Editor = null;			//入金伝票作成用通貨検索
	//2段目
	private Label Invoice_BP_Label = new Label();					//売上請求伝票検索用取引先マスタラベル
	private WSearchEditor Invoice_BP_Editor = null;					//売上請求伝票検索用取引先マスタ検索
	private Label Corportion_Label = new Label();					//売上請求伝票検索用法人マスタラベル
	private WTableDirEditor Corportion_Editor = null;				//売上請求伝票検索用法人マスタ検索

	//入金伝票作成条件パネル
	//1段目
	private Label Payment_Org_Label = new Label();					//入金伝票作成用組織マスタラベル
	private WSearchEditor Payment_Org_Editor;						//入金伝票作成用組織マスタ選択リスト
	private Label Payment_DocType_Label = new Label();				//入金伝票作成用伝票タイプラベル
	private WTableDirEditor Payment_DocType_Editor = null;			//入金伝票作成用伝票タイプ選択リスト
	private Label Payment_Date_Label = new Label();					//入金伝票作成用日付ラベル
	private WDateEditor Payment_Date_Editor = new WDateEditor("Payment_Date",true,false,true,"");
	//2段目
	private Label Payment_Account_Label = new Label();				//入金伝票作成用アカウントラベル
	private WTableDirEditor Payment_Account_Editor = null;			//入金伝票作成用アカウント選択リスト
	private Label Payment_BP_Label = new Label();					//入金伝票作成用取引先マスタラベル
	private WSearchEditor Payment_BP_Editor = null;					//入金伝票作成用取引先マスタ検索
	//3段目
	private Label Payment_Currency_Label = new Label();				//入金伝票作成用通貨ラベル
	private WTableDirEditor Payment_Currency_Editor = null;			//入金伝票作成用通貨検索
	private Label PayAmt_Label = new Label();						//入金伝票作成用支払(入金）金額ラベル
	private WNumberEditor PayAmt_Editor = new WNumberEditor();		//入金伝票作成用支払(入金）金額


	/*【情報パネル】*/
	private Borderlayout infoPanel = new Borderlayout();

	/*【請求書パネル】*/
	private Panel invoicePanel = new Panel();
	private Borderlayout invoiceLayout = new Borderlayout();
	private Label invoiceLabel = new Label();
	private Label invoiceInfo = new Label();
	private WListbox invoiceTable = ListboxFactory.newDataTable();


	/*【消込処理パネル】*/
	private Panel allocationPanel = new Panel();
	private Grid allocationLayout = GridFactory.newGridLayout();
	private Label differenceLabel = new Label();
	private Label allocCurrencyLabel = new Label();
	private Textbox differenceField = new Textbox();
	private Button allocateButton = new Button();
	private Button refreshButton = new Button();
	private Button selectAllButton = new Button();

	/*【ステータスバー】*/
	private Hlayout statusBar = new Hlayout();


	//Column Number
	private int i_Select = 0;
	private int i_DateInvoiced = 1;
	private int i_DocumentNo = 2;
	private int i_GrandTotal = 3;
	private int i_OpenAmt = 4;
	private int i_Discount = 5;
	private int i_WriteOff = 6;
	private int i_AppliedAmt = 7;
	private int i_OverUnderAmt = 8;
	private int i_AD_Org_ID = 9;
	private int i_C_BPartner_ID = 10;


    public JPARInvoiceAllocation() throws IOException
    {
    	Env.setContext(Env.getCtx(), form.getWindowNo(), "IsSOTrx", "Y");   //  defaults to no
		try
		{
			dynInit();
			zkInit();
			calculate(true);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE, "", e);
		}
    }

	@Override
	public ADForm getForm()
	{
		return form;
	}

	public void dynInit() throws Exception
	{
		Invoice_Currency_ID = Env.getContextAsInt(Env.getCtx(), "$C_Currency_ID");   //  default
		Env.setContext(Env.getCtx(), form.getWindowNo(), "C_Currency_ID", Invoice_Currency_ID);//for Dynamic Validation
		//
		if (log.isLoggable(Level.INFO)) log.info("Currency=" + Invoice_Currency_ID);

		Invoice_Org_ID = Env.getAD_Org_ID(Env.getCtx());


		Timestamp dateLogin = Env.getContextAsDate(Env.getCtx(), "#Date");
		long dateLoginLong = dateLogin.getTime();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(dateLoginLong);

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.DATE, -1);
		DateInvoiceTo_Editor.setValue(new Timestamp(calendar.getTimeInMillis()));
		DateInvoiceTo_Editor.addValueChangeListener(this);
		DateInvoiedTo = new Timestamp(calendar.getTimeInMillis());

		calendar.add(Calendar.DATE, 1);
		calendar.add(Calendar.MONTH, -1);
		DateInvoiceFrom_Editor.setValue(new Timestamp(calendar.getTimeInMillis()));
		DateInvoiceFrom_Editor.addValueChangeListener(this);
		DateInvoiedFrom = new Timestamp(calendar.getTimeInMillis());

		int AD_Column_ID = 0;

		// Initialization of Org
		AD_Column_ID = MColumn.getColumn_ID("C_Invoice", "AD_Org_ID");
		MLookup lookupOrg = MLookupFactory.get(Env.getCtx(), form.getWindowNo(), 0, AD_Column_ID,  DisplayType.Search);
		Invoice_Org_Editor = new WSearchEditor("Invoice_Org_ID", true, false, true, lookupOrg);
		Invoice_Org_Editor.setValue(Env.getAD_Org_ID(Env.getCtx()));
		Invoice_Org_ID = Env.getAD_Org_ID(Env.getCtx());
		Invoice_Org_Editor.addValueChangeListener(this);

		Payment_Org_Editor = new WSearchEditor("Payment_Org_ID", true, false, true, lookupOrg);
		Payment_Org_Editor.setValue(Env.getAD_Org_ID(Env.getCtx()));
		Payment_Org_ID = Env.getAD_Org_ID(Env.getCtx());
		Payment_Org_Editor.addValueChangeListener(this);


		//Initialization of BPartner
		AD_Column_ID = MColumn.getColumn_ID("C_Invoice","C_BPartner_ID");
		MLookup lookupBP = MLookupFactory.get(Env.getCtx(), form.getWindowNo(), AD_Column_ID
						, DisplayType.Search,Env.getLanguage(Env.getCtx()), "C_BPartner_ID", 1000018, false, "");
		Invoice_BP_Editor = new WSearchEditor("Invoice_BPartner_ID", true, false, true, lookupBP);
		Invoice_BP_Editor.addValueChangeListener(this);

		Payment_BP_Editor = new WSearchEditor("Payment_BPartner_ID", true, false, true, lookupBP);
		Payment_BP_Editor.addValueChangeListener(this);


		//Initialization of Corporation Master
		AD_Column_ID = MColumn.getColumn_ID("C_BPartner","JP_Corporation_ID");
		if(AD_Column_ID > 0)
		{
			MLookup lookupCorp = MLookupFactory.get(Env.getCtx(), form.getWindowNo(), AD_Column_ID,
					DisplayType.TableDir, Env.getLanguage(Env.getCtx()), "JP_Corporation_ID", 0,
					false, "JP_Corporation.JP_Corporation_ID = (SELECT JP_Corporation_ID FROM C_BPartner WHERE C_BPartner_ID = @C_BPartner_ID@)");
			Corportion_Editor = new WTableDirEditor("JP_Corporation_ID", false, false, true, lookupCorp);
			Corportion_Editor.addValueChangeListener(this);
		}


		//Initialization of Doc Type
		AD_Column_ID = MColumn.getColumn_ID("C_Payment","C_DocType_ID");//5302;
		MLookup lookupDocType = MLookupFactory.get(Env.getCtx(), form.getWindowNo(), AD_Column_ID,
				DisplayType.TableDir, Env.getLanguage(Env.getCtx()), "C_DocType_ID", 0,
				false, "C_DocType.DocBaseType = 'ARR' AND C_DocType.IsSOTrx = 'Y'");
		Payment_DocType_Editor = new WTableDirEditor("Payment_DocType_ID", true, false, true, lookupDocType);
		Payment_DocType_Editor.addValueChangeListener(this);


		//Initialization of Bank Account
		AD_Column_ID = MColumn.getColumn_ID("C_Payment","C_BankAccount_ID");//3880;
		MLookup lookupAcount = MLookupFactory.get(Env.getCtx(), form.getWindowNo(), AD_Column_ID,
				DisplayType.TableDir, Env.getLanguage(Env.getCtx()), "C_BankAccount_ID", 0,
				false, "C_BankAccount.AD_Org_ID = @AD_Org_ID@ AND C_BankAccount.C_Currency_ID=@C_Currency_ID@");
		Payment_Account_Editor = new WTableDirEditor("Payment_BankAccount_ID", true, false, true, lookupAcount);
		Payment_Account_Editor.addValueChangeListener(this);


		//
		Payment_Date_Editor.setValue(Env.getContextAsDate(Env.getCtx(), "#Date"));
		allocDate = Env.getContextAsDate(Env.getCtx(), "#Date");
		Payment_Date_Editor.addValueChangeListener(this);


		//Initialization of Currency
		AD_Column_ID = MColumn.getColumn_ID("C_Invoice","C_Currency_ID");//1000033
//		MLookup lookupCur = MLookupFactory.get(Env.getCtx(), form.getWindowNo(), 0, AD_Column_ID,  DisplayType.Search);
		MLookup lookupCur = MLookupFactory.get(Env.getCtx(), form.getWindowNo(), AD_Column_ID,
				 DisplayType.Search, Env.getLanguage(Env.getCtx()), "C_Currency_ID", 1000033,false,"");
		Invoice_Currency_Editor = new WSearchEditor("Invoice_Currency_ID", true, false, true, lookupCur);
		Invoice_Currency_Editor.setValue(new Integer(Invoice_Currency_ID));
		Invoice_Currency_Editor.addValueChangeListener(this);
		Env.setContext(Env.getCtx(), form.getWindowNo(), "C_Currency_ID", Invoice_Currency_ID);//for Dynamic Validation

		Payment_Currency_Editor = new WTableDirEditor("Payment_Currency_ID", true, true, true, lookupCur);
		Payment_Currency_Editor.setValue(new Integer(Invoice_Currency_ID));


		//
		PayAmt_Editor = new WNumberEditor("Payment_PayAmt", true, false, true, DisplayType.Amount, "payAmt");
		PayAmt_Editor.setValue(Env.ZERO);
		PayAmt_Editor.addValueChangeListener(this);

		//  Translation
		statusBar.appendChild(new Label(Msg.getMsg(Env.getCtx(), "PaymentAllocation")));
		statusBar.setVflex("min");

	}


	private void zkInit() throws Exception
	{
		form.appendChild(mainLayout);

		/*【メインレイアウト(Borderlayout)】*/
		ZKUpdateUtil.setWidth(mainLayout, "99%");
		ZKUpdateUtil.setHeight(mainLayout, "100%");

		//【メインレイアウト(Borderlayout)-北】
		North north = new North();
		mainLayout.appendChild(north);

		//パラメータパネル
		north.appendChild(parameterPanel);
		north.setStyle("border: none");
		parameterPanel.appendChild(parameterLayout); 		//parameterLayout = Grid
		ZKUpdateUtil.setWidth(parameterLayout, "90%");
		Rows parameterLayoutRows = parameterLayout.newRows();

		//パラメータパネル-1段目(売上請求伝票検索条件パネル)
		Row row = parameterLayoutRows.newRow();
			Groupbox invoiceSearchGB = new Groupbox();
			row.appendCellChild(invoiceSearchGB,8);
			invoiceSearchGB.appendChild(new Caption(Msg.getMsg(Env.getCtx(),"JP_SearchConditionForInvoice")));
			Grid invoiceSearch  = new Grid();
			invoiceSearch.setStyle("background-color: #E9F0FF");
			invoiceSearch.setStyle("border: none");
			invoiceSearchGB.appendChild(invoiceSearch);
			Rows rows = invoiceSearch.newRows();
			row = rows.newRow();
				DateInvoiceFrom_Label.setText(Msg.getElement(Env.getCtx(), "DateInvoiced"));
				row.appendCellChild(DateInvoiceFrom_Label.rightAlign(),1);
				ZKUpdateUtil.setHflex(DateInvoiceFrom_Editor.getComponent(), "true");
				row.appendCellChild(DateInvoiceFrom_Editor.getComponent(),1);
				row.setStyle("background-color: #ffffff");

				ZKUpdateUtil.setHflex(DateInvoiceTo_Editor.getComponent(), "true");
				row.appendCellChild(DateInvoiceTo_Editor.getComponent(),1);
				row.setStyle("background-color: #ffffff");

				Invoice_Org_Label.setText(Msg.translate(Env.getCtx(), "AD_Org_ID"));
				row.appendCellChild(Invoice_Org_Label.rightAlign());
				ZKUpdateUtil.setHflex(Invoice_Org_Editor.getComponent(), "true");
				row.appendCellChild(Invoice_Org_Editor.getComponent(),1);
				row.appendCellChild(new Space(),1);

				Invoice_Currency_Label.setText(Msg.translate(Env.getCtx(), "C_Currency_ID"));
				row.appendCellChild(Invoice_Currency_Label.rightAlign(),1);
				ZKUpdateUtil.setHflex(Invoice_Currency_Editor.getComponent(), "true");
				row.appendCellChild(Invoice_Currency_Editor.getComponent(),1);
				row.appendCellChild(new Space(),1);
				row.setStyle("background-color: #ffffff");

			row = rows.newRow();
				Invoice_BP_Label.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
				row.appendCellChild(Invoice_BP_Label.rightAlign());
				ZKUpdateUtil.setHflex(Invoice_BP_Editor.getComponent(), "true");
				row.appendCellChild(Invoice_BP_Editor.getComponent(),2);

				Corportion_Label.setText(Msg.translate(Env.getCtx(), "JP_Corporation_ID"));
				row.appendCellChild(Corportion_Label.rightAlign());
				ZKUpdateUtil.setHflex(Corportion_Editor.getComponent(), "true");
				row.appendCellChild(Corportion_Editor.getComponent(),2);

				row.setStyle("background-color: #ffffff");


		//パラメータパネル-2段目(入金伝票作成条件パネル)
		row = parameterLayoutRows.newRow();
			Groupbox paymentGB = new Groupbox();
			row.appendCellChild(paymentGB,8);
			paymentGB.appendChild(new Caption(Msg.getMsg(Env.getCtx(),"JP_CreatePaymentInfo")));
			Grid paymentCondition  = new Grid();
			paymentCondition.setStyle("border: none");
			paymentGB.appendChild(paymentCondition);
			rows = paymentCondition.newRows();

			//入金伝票作成条件パネル1段目
			row = rows.newRow();
				Payment_Org_Label.setText(Msg.translate(Env.getCtx(), "AD_Org_ID"));
				row.appendCellChild(Payment_Org_Label.rightAlign());
				ZKUpdateUtil.setHflex(Payment_Org_Editor.getComponent(), "true");
				Payment_Org_Editor.setMandatory(true);
				row.appendCellChild(Payment_Org_Editor.getComponent(),1);
				Payment_DocType_Label.setText(Msg.translate(Env.getCtx(), "C_DocType_ID"));
				row.appendCellChild(Payment_DocType_Label.rightAlign(),1);
				ZKUpdateUtil.setHflex(Payment_DocType_Editor.getComponent(), "true");
				Payment_DocType_Editor.setMandatory(true);
				row.appendCellChild(Payment_DocType_Editor.getComponent(),1);
				Payment_Date_Label.setText(Msg.getElement(Env.getCtx(), "DateTrx"));
				row.appendCellChild(Payment_Date_Label.rightAlign(),1);
				row.appendCellChild(Payment_Date_Editor.getComponent());
				row.setStyle("background-color: #ffffff");

			//入金伝票作成条件パネル2段目
			row = rows.newRow();
				Payment_Account_Label.setText(Msg.translate(Env.getCtx(), "C_BankAccount_ID"));
				row.appendCellChild(Payment_Account_Label.rightAlign(),1);
				ZKUpdateUtil.setHflex(Payment_Account_Editor.getComponent(), "true");
				row.appendCellChild(Payment_Account_Editor.getComponent(),2);
				row.setStyle("background-color: #ffffff");
				Payment_BP_Label.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
				row.appendCellChild(Payment_BP_Label.rightAlign());
				ZKUpdateUtil.setHflex(Payment_BP_Editor.getComponent(), "true");
				row.appendCellChild(Payment_BP_Editor.getComponent(),2);
				row.setStyle("background-color: #ffffff");

			//入金伝票作成条件パネル3段目
			row = rows.newRow();
				Payment_Currency_Label.setText(Msg.translate(Env.getCtx(), "C_Currency_ID"));
				row.appendCellChild(Payment_Currency_Label.rightAlign(),1);
				ZKUpdateUtil.setHflex(Payment_Currency_Editor.getComponent(), "true");
				row.appendCellChild(Payment_Currency_Editor.getComponent(),1);
				PayAmt_Label.setText(Msg.getElement(Env.getCtx(), "DepositAmt",true));
				row.appendCellChild(PayAmt_Label.rightAlign());
				row.appendCellChild(PayAmt_Editor.getComponent());
				row.setStyle("background-color: #ffffff");


		//【メインレイアウト(Borderlayout)-中央】
		Center center = new Center();
		mainLayout.appendChild(center);

		//情報パネル(BorderLayout)
		center.appendChild(infoPanel);
		ZKUpdateUtil.setHflex(infoPanel, "1");
		ZKUpdateUtil.setVflex(infoPanel, "1");
		infoPanel.setStyle("border: none");
		ZKUpdateUtil.setWidth(infoPanel, "100%");
		ZKUpdateUtil.setHeight(infoPanel, "100%");

		//情報パネル-北
		north = new North();
		infoPanel.appendChild(north);
		north.setStyle("border: none");
		north.setHeight("100%");

		//情報パネル-北：請求書パネル(BorderLayout)
		north.appendChild(invoicePanel);
		invoicePanel.appendChild(invoiceLayout);
		ZKUpdateUtil.setWidth(invoicePanel, "100%");
		ZKUpdateUtil.setHeight(invoicePanel, "100%");
		ZKUpdateUtil.setHflex(invoicePanel, "1");
		ZKUpdateUtil.setVflex(invoicePanel, "1");
		ZKUpdateUtil.setWidth(invoiceLayout, "100%");
		ZKUpdateUtil.setHeight(invoiceLayout, "100%");
		invoiceLayout.setStyle("border: none");
		invoiceInfo.setText(".");

		//情報パネル-北：請求書パネル-北:
		north = new North();
		north.setStyle("border: none");
		invoiceLayout.appendChild(north);
		invoiceLabel.setText(" " + Msg.translate(Env.getCtx(), "C_Invoice_ID"));
		north.appendChild(invoiceLabel);

		//情報パネル-北：請求書パネル-中央
		center = new Center();
		invoiceLayout.appendChild(center);
		center.appendChild(invoiceTable);
		ZKUpdateUtil.setWidth(invoiceTable, "99%");
		ZKUpdateUtil.setHeight(invoiceTable, "99%");
		center.setStyle("border: none");
		invoiceTable.setStyle("overflow-y: visible");//Scroll

		//情報パネル-北：請求書パネル-南
		South south = new South();
		south.setStyle("border: none");
		invoiceLayout.appendChild(south);
		south.appendChild(invoiceInfo.rightAlign());


		//【メインレイアウト(Borderlayout)-南】
		south = new South();
		mainLayout.appendChild(south);
		south.setStyle("border: none");

		//South Panel
		south.appendChild(southPanel);

		southPanel.appendChild(new Separator());
		southPanel.appendChild(statusBar);					//StatusBar = Hlayout
		southPanel.appendChild(allocationPanel);

		allocationPanel.appendChild(allocationLayout);		//allocationLayout = Grid
		ZKUpdateUtil.setHflex(allocationLayout, "min");
		rows = allocationLayout.newRows();
		row = rows.newRow();

		WAppsAction selectAllAction = new WAppsAction (SELECT_DESELECT_ALL, null, null);
		selectAllButton = selectAllAction.getButton();
		selectAllButton.setAttribute(SELECT_DESELECT_ALL, Boolean.FALSE);
		selectAllButton.addActionListener(this);
		row.appendCellChild(selectAllButton);

		differenceLabel.setText(Msg.getMsg(Env.getCtx(), "Difference"));
		row.appendCellChild(differenceLabel.rightAlign());
		row.appendCellChild(allocCurrencyLabel.rightAlign());
		ZKUpdateUtil.setHflex(differenceField, "true");
		differenceField.setText("0");
		differenceField.setReadonly(true);
		differenceField.setStyle("text-align: right");
		row.appendCellChild(differenceField);

		allocateButton.setLabel(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "Process")));
		allocateButton.addActionListener(this);
		ZKUpdateUtil.setHflex(allocateButton, "true");
		row.appendCellChild(allocateButton);

		refreshButton.setLabel(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "Refresh")));
		refreshButton.addActionListener(this);
		refreshButton.setAutodisable("self");
		row.appendCellChild(refreshButton);



	}

	public Vector<Vector<Object>> getInvoiceData(Object date, IMiniTable invoiceTable)
	{

		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		StringBuilder sql = new StringBuilder("SELECT i.DateInvoiced,i.DocumentNo,i.C_Invoice_ID," //  1,2,3
			+ "c.ISO_Code,i.GrandTotal*i.MultiplierAP, "                            //  4,5
			+ "i.GrandTotal, " //  6 - GrandTotal
			+ "invoiceOpen(C_Invoice_ID,C_InvoicePaySchedule_ID), "  //  7 - OpenAmt
			+ "invoiceDiscount(i.C_Invoice_ID,?,C_InvoicePaySchedule_ID)," //  8 - AllowedDiscount #1
			+ "i.MultiplierAP ,org.Name, bp.Name "); //9, 10, 11

		sql.append("FROM C_Invoice_v i"		//  corrected for CM/Split
			+ " INNER JOIN C_Currency c ON (i.C_Currency_ID=c.C_Currency_ID)"
			+ " INNER JOIN AD_Org org ON (i.AD_Org_ID = org.AD_Org_ID)" );
		sql.append(" LEFT OUTER JOIN C_BPartner bp on( i.C_BPartner_ID = bp.C_BPartner_ID) ");

		sql.append("WHERE i.IsPaid='N' AND i.Processed='Y'");
		if(JP_Corporation_ID == 0)					//  #2
			sql.append(" AND i.C_BPartner_ID=?");
		else
			sql.append(" AND bp.JP_Corporation_ID=?");

		sql.append(" AND i.C_Currency_ID=?");                                   //  #3
		if (Invoice_Org_ID != 0 )
			sql.append(" AND i.AD_Org_ID=?");					//  #4

		if(DateInvoiedFrom != null)
			sql.append(" AND i.DateInvoiced >= ?");

		if(DateInvoiedTo != null)
			sql.append(" AND i.DateInvoiced <= ?");

		sql.append(" ORDER BY i.DateInvoiced, i.DocumentNo");
		if (log.isLoggable(Level.FINE)) log.fine("InvSQL=" + sql.toString());

		// role security
		sql = new StringBuilder( MRole.getDefault(Env.getCtx(), false).addAccessSQL( sql.toString(), "i", MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO ) );

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			int i = 0;
			pstmt = DB.prepareStatement(sql.toString(), null);
			pstmt.setTimestamp(++i, (Timestamp)date);
			if(JP_Corporation_ID == 0)
				pstmt.setInt(++i, Invoice_BP_ID);
			else
				pstmt.setInt(++i, JP_Corporation_ID);
			pstmt.setInt(++i, Invoice_Currency_ID);

			if (Invoice_Org_ID != 0 )
				pstmt.setInt(++i, Invoice_Org_ID);

			if(DateInvoiedFrom != null)
				pstmt.setTimestamp(++i, DateInvoiedFrom);

			if(DateInvoiedTo != null)
				pstmt.setTimestamp(++i, DateInvoiedTo);

			rs = pstmt.executeQuery();
			while (rs.next())
			{
				Vector<Object> line = new Vector<Object>();
				line.add(Boolean.valueOf(false));       // 0-Selection
				line.add(rs.getTimestamp(1));       //  1-DateInvoiced
				KeyNamePair pp = new KeyNamePair(rs.getInt(3), rs.getString(2));//C_Invoice_ID,DocumentNo
				line.add(pp);                       //  2-DocumentNo
				line.add(rs.getBigDecimal(6));      //  3-Grand Total
				BigDecimal open = rs.getBigDecimal(7);
				if (open == null)
					open = Env.ZERO;
				line.add(open);      				//  4-Open Amount
				BigDecimal discount = rs.getBigDecimal(8);
				if (discount == null)
					discount = Env.ZERO;
				line.add(discount);					//  5-AllowedDisc
				line.add(Env.ZERO);      			//  6-WriteOff
				line.add(Env.ZERO);					//  7-Applied
				line.add(open);				    	//  8-OverUnder

				line.add(rs.getString(10));			//9-Org
				line.add(rs.getString(11));			//10-BPartner

				if (Env.ZERO.compareTo(open) != 0)
					data.add(line);

			}
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql.toString(), e);
		}
		finally
		{
			DB.close(rs, pstmt);
		}

		return data;
	}

	public Vector<String> getInvoiceColumnNames()
	{
		//  Header Info
		Vector<String> columnNames = new Vector<String>();
		columnNames.add(Msg.getMsg(Env.getCtx(), "Select"));							//0
		columnNames.add(Msg.getElement(Env.getCtx(), "DateInvoiced"));					//1
		columnNames.add(Util.cleanAmp(Msg.translate(Env.getCtx(), "DocumentNo")));	//2
		columnNames.add(Msg.getElement(Env.getCtx(), "GrandTotal"));					//3
		columnNames.add(Msg.getMsg(Env.getCtx(), "OpenAmt"));							//4
		columnNames.add(Msg.getMsg(Env.getCtx(), "Discount"));							//5
		columnNames.add(Msg.getMsg(Env.getCtx(), "WriteOff"));							//6
		columnNames.add(Msg.getMsg(Env.getCtx(), "AppliedAmt"));						//7
		columnNames.add(Msg.getMsg(Env.getCtx(), "OverUnderAmt"));						//8
		columnNames.add(Msg.getElement(Env.getCtx(), "AD_Org_ID"));					//9
		columnNames.add(Msg.getElement(Env.getCtx(), "C_BPartner_ID"));				//10


		return columnNames;
	}

	public void setInvoiceColumnClass(IMiniTable invoiceTable)
	{
		int i = 0;
		invoiceTable.setColumnClass(i++, Boolean.class, false);        //  0-Selection
		invoiceTable.setColumnClass(i++, Timestamp.class, true);       //  1-DateInvoiced
		invoiceTable.setColumnClass(i++, String.class, true);          //  2-DocumentNo
		invoiceTable.setColumnClass(i++, BigDecimal.class, true);      //  3-Grand Total
		invoiceTable.setColumnClass(i++, BigDecimal.class, true);      //  4-OpenAmt
		invoiceTable.setColumnClass(i++, BigDecimal.class, false);     //  5-Discount
		invoiceTable.setColumnClass(i++, BigDecimal.class, false);     //  6-WriteOff
		invoiceTable.setColumnClass(i++, BigDecimal.class, false);     //  7-OverUnder
		invoiceTable.setColumnClass(i++, BigDecimal.class, true);		//	8-Applied
		invoiceTable.setColumnClass(i++, String.class, true);       	//  9-Org Name
		invoiceTable.setColumnClass(i++, String.class, true); 		    //  10-BP Name
		//  Table UI
		invoiceTable.autoSize();
	}

	/**
	 *  Load Business Partner Info
	 *  - Payments
	 */
	public void checkBPartner()
	{
		if (log.isLoggable(Level.CONFIG)) log.config("BPartner=" + Invoice_BP_ID + ", Cur=" + Invoice_Currency_ID);
		//  Need to have both values
		if (Invoice_BP_ID == 0 || Invoice_Currency_ID == 0)
			return;

		//	Async BPartner Test
		Integer key = new Integer(Invoice_BP_ID);
		if (!m_bpartnerCheck.contains(key))
		{
			new Thread()
			{
				public void run()
				{
					MInvoice.setIsPaid (Env.getCtx(), Invoice_BP_ID, null);
				}
			}.start();
			m_bpartnerCheck.add(key);
		}
	}

	public String calculateInvoice(IMiniTable invoice)
	{
		//  Invoices
		totalInv = Env.ZERO;
		int rows = invoice.getRowCount();
		m_noInvoices = 0;

		for (int i = 0; i < rows; i++)
		{
			if (((Boolean)invoice.getValueAt(i, 0)).booleanValue())
			{
				Timestamp ts = (Timestamp)invoice.getValueAt(i, i_DateInvoiced);
				allocDate = TimeUtil.max(allocDate, ts);
				BigDecimal bd = (BigDecimal)invoice.getValueAt(i, i_AppliedAmt);
				totalInv = totalInv.add(bd);  //  Applied Inv
				m_noInvoices++;
				if (log.isLoggable(Level.FINE)) log.fine("Invoice_" + i + " = " + bd + " - Total=" + totalPay);
			}
		}
		return String.valueOf(m_noInvoices) + " - "
			+ Msg.getMsg(Env.getCtx(), "Sum") + "  " + format.format(totalInv) + " ";
	}


	private void setAllocateButton()
	{

		if (totalDiff.signum() == 0)
		{
			allocateButton.setEnabled(true);
		}
		else
		{
			allocateButton.setEnabled(false);
		}

	}

	private void loadBPartner ()
	{
		checkBPartner();

		Vector<Vector<Object>> data = getInvoiceData(Payment_Date_Editor.getValue(), invoiceTable);
		Vector<String> columnNames = getInvoiceColumnNames();

		invoiceTable.clear();

		//  Remove previous listeners
		invoiceTable.getModel().removeTableModelListener(this);

		//  Set Model
		ListModelTable modelI = new ListModelTable(data);
		modelI.addTableModelListener(this);
		invoiceTable.setData(modelI, columnNames);
		setInvoiceColumnClass(invoiceTable);

		//  Calculate Totals
		calculate(true);

		statusBar.getChildren().clear();
	}   //  loadBPartner


	public void calculate(boolean isUpdatePayAmt)
	{
		invoiceInfo.setText(calculateInvoice(invoiceTable));

		if(isUpdatePayAmt)
		{
			Payment_PayAmt = totalInv;
			PayAmt_Editor.setValue(Payment_PayAmt);
		}

		//	Set AllocationDate
		if (allocDate != null)
			Payment_Date_Editor.setValue(allocDate);
		//  Set Allocation Currency
		allocCurrencyLabel.setText(Payment_Currency_Editor.getDisplay());
		//  Difference
		totalDiff = Payment_PayAmt.subtract(totalInv);
		differenceField.setText(format.format(totalDiff));

		setAllocateButton();
	}


	public String writeOff(int row, int col, boolean isInvoice, IMiniTable invoice, boolean isAutoWriteOff)
	{
		String msg = "";
		/**
		 *  Setting defaults
		 */
		if (m_calculating)  //  Avoid recursive calls
			return msg;
		m_calculating = true;

		if (log.isLoggable(Level.CONFIG)) log.config("Row=" + row
			+ ", Col=" + col + ", InvoiceTable=" + isInvoice);

		boolean selected = ((Boolean) invoice.getValueAt(row, 0)).booleanValue();
		BigDecimal open = (BigDecimal)invoice.getValueAt(row, i_OpenAmt);
		BigDecimal discount = (BigDecimal)invoice.getValueAt(row, i_Discount);
		BigDecimal writeOff = (BigDecimal) invoice.getValueAt(row, i_WriteOff);
		BigDecimal applied = (BigDecimal)invoice.getValueAt(row, i_AppliedAmt);
		BigDecimal overUnder = (BigDecimal) invoice.getValueAt(row, i_OverUnderAmt);
		int openSign = open.signum();

		if (col == i_Select)  //selection
		{
			//  selected - set applied amount
			if ( selected )
			{
				applied = open;    //  Open Amount
				applied = applied.subtract(discount);
				writeOff = Env.ZERO;  //  to be sure
				overUnder = Env.ZERO;
				totalDiff = Env.ZERO;

				if (totalDiff.abs().compareTo(applied.abs()) < 0			// where less is available to allocate than open
						&& totalDiff.signum() == applied.signum() )     	// and the available amount has the same sign
					applied = totalDiff;									// reduce the amount applied to what's available

				if ( isAutoWriteOff )
					writeOff = open.subtract(applied.add(discount));
				else
					overUnder = open.subtract(applied.add(discount));
			}
			else    //  de-selected
			{
				writeOff = Env.ZERO;
				applied = Env.ZERO;
				overUnder = Env.ZERO;
			}
		}

		// check entered values are sensible and possibly auto write-off
		if ( selected && col != 0 )
		{

			// values should have same sign as open except possibly over/under
			if ( discount.signum() == -openSign )
				discount = discount.negate();
			if ( writeOff.signum() == -openSign)
				writeOff = writeOff.negate();
			if ( applied.signum() == -openSign )
				applied = applied.negate();

			// discount and write-off must be less than open amount
			if ( discount.abs().compareTo(open.abs()) > 0)
				discount = open;
			if ( writeOff.abs().compareTo(open.abs()) > 0)
				writeOff = open;


			/*
			 * Two rules to maintain:
			 *
			 * 1) |writeOff + discount| < |open|
			 * 2) discount + writeOff + overUnder + applied = 0
			 *
			 *   As only one column is edited at a time and the initial position was one of compliance
			 *   with the rules, we only need to redistribute the increase/decrease in the edited column to
			 *   the others.
			*/
			BigDecimal newTotal = discount.add(writeOff).add(applied).add(overUnder);  // all have same sign
			BigDecimal difference = newTotal.subtract(open);

			// rule 2
			BigDecimal diffWOD = writeOff.add(discount).subtract(open);

			if ( diffWOD.signum() == open.signum() )  // writeOff and discount are too large
			{
				if ( col == 5 ) // Discount Amount
				{
					writeOff = writeOff.subtract(diffWOD);
				}
				else                            // col = i_writeoff
				{
					discount = discount.subtract(diffWOD);
				}

				difference = difference.subtract(diffWOD);
			}

			// rule 1
			if ( col == 7 )// Applied Amount
				overUnder = overUnder.subtract(difference);
			else
				applied = applied.subtract(difference);

		}

		//	Warning if write Off > 30%
		if (isAutoWriteOff && writeOff.doubleValue()/open.doubleValue() > .30)
			msg = "AllocationWriteOffWarn";

		invoice.setValueAt(discount, row, i_Discount);
		invoice.setValueAt(writeOff, row, i_WriteOff);
		invoice.setValueAt(applied, row, i_AppliedAmt);
		invoice.setValueAt(overUnder, row, i_OverUnderAmt);

		m_calculating = false;

		return msg;
	}


	@Override
	public void valueChange(ValueChangeEvent e)
	{
		String name = e.getPropertyName();
		Object value = e.getNewValue();
		if (log.isLoggable(Level.CONFIG)) log.config(name + "=" + value);


		if (name.equals("Invoice_Org_ID"))
		{
			Invoice_Org_ID = value != null ? ((Integer) value).intValue() : 0;
			if(Invoice_BP_ID > 0 && Invoice_Currency_ID > 0)
			{
				loadBPartner();
			}else if (Invoice_BP_ID > 0){
				statusBar.getChildren().clear();
				Label label = new Label(Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), "C_Currency_ID"));
				label.setStyle("color: #ff0000");
				statusBar.appendChild(label);

			}else{
				statusBar.getChildren().clear();
				Label label = new Label(Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), "C_BPartner_ID"));
				label.setStyle("color: #ff0000");
				statusBar.appendChild(label);
			}

			if(Invoice_Org_ID != 0 && Payment_Org_ID == 0)
			{
				Payment_Org_ID = Invoice_Org_ID;
				Payment_Org_Editor.setValue(value);

				//Initialize of Bank Account
				Env.setContext(Env.getCtx(), form.getWindowNo(), "AD_Org_ID", Payment_Org_ID);//for Dynamic Validation
				MLookup mLookup = (MLookup)Payment_Account_Editor.getLookup();
				Payment_Account_Editor.setValue(null);
				Payment_BankAccount_ID = 0;
				mLookup.refresh();

			}
		}
		else if (name.equals("Payment_Org_ID") )
		{
			Payment_Org_ID = value != null ? ((Integer) value).intValue() : 0;

			//Initialize of Bank Account
			Env.setContext(Env.getCtx(), form.getWindowNo(), "AD_Org_ID", Payment_Org_ID);//for Dynamic Validation
			MLookup mLookup = (MLookup)Payment_Account_Editor.getLookup();
			Payment_Account_Editor.setValue(null);
			Payment_BankAccount_ID = 0;
			mLookup.refresh();
		}
		//  BPartner
		else if (e.getSource().equals(Invoice_BP_Editor))
		{
			Invoice_BP_ID = value != null ? ((Integer) value).intValue() : 0;

			Payment_BP_Editor.setValue(value);
			Payment_BP_ID = Invoice_BP_ID;

			Env.setContext(Env.getCtx(), form.getWindowNo(), "C_BPartner_ID", Invoice_BP_ID);//for Dynamic Validation
			MLookup mLookup = (MLookup)Corportion_Editor.getLookup();
			Corportion_Editor.setValue(null);
			JP_Corporation_ID = 0;
			mLookup.refresh();

			if(Invoice_BP_ID > 0 && Invoice_Currency_ID > 0)
			{
				loadBPartner();
			}else if (Invoice_BP_ID > 0){
				statusBar.getChildren().clear();
				Label label = new Label(Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), "C_Currency_ID"));
				label.setStyle("color: #ff0000");
				statusBar.appendChild(label);

			}else{
				statusBar.getChildren().clear();
				Label label = new Label(Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), "C_BPartner_ID"));
				label.setStyle("color: #ff0000");
				statusBar.appendChild(label);
			}


		}else if (e.getSource().equals(Payment_BP_Editor) )
		{
			Payment_BP_ID = value != null ? ((Integer) value).intValue() : 0;
			Payment_BP_ID  = ((Integer)value).intValue();
		}
		else if(name.equals("JP_Corporation_ID"))
		{
			JP_Corporation_ID = value != null ? ((Integer) value).intValue() : 0;
			if(Invoice_BP_ID > 0 && Invoice_Currency_ID > 0)
			{
				loadBPartner();
			}else if (Invoice_BP_ID > 0){
				statusBar.getChildren().clear();
				Label label = new Label(Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), "C_Currency_ID"));
				label.setStyle("color: #ff0000");
				statusBar.appendChild(label);

			}else{
				statusBar.getChildren().clear();
				Label label = new Label(Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), "C_BPartner_ID"));
				label.setStyle("color: #ff0000");
				statusBar.appendChild(label);
			}
		}
		//	Currency
		else if (name.equals("Invoice_Currency_ID"))
		{
			Invoice_Currency_ID = value != null ? ((Integer) value).intValue() : 0;
			Env.setContext(Env.getCtx(), form.getWindowNo(), "C_Currency_ID", Invoice_Currency_ID);//for Dynamic Validation
			Payment_Currency_Editor.setValue(null);
			MLookup mLookup = (MLookup)Payment_Account_Editor.getLookup();
			Payment_Account_Editor.setValue(null);
			Payment_BankAccount_ID = 0;
			mLookup.refresh();

			if(Invoice_BP_ID > 0 && Invoice_Currency_ID > 0)
			{
				loadBPartner();
			}else if (Invoice_BP_ID > 0){
				statusBar.getChildren().clear();
				Label label = new Label(Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), "C_Currency_ID"));
				label.setStyle("color: #ff0000");
				statusBar.appendChild(label);

			}else{
				statusBar.getChildren().clear();
				Label label = new Label(Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), "C_BPartner_ID"));
				label.setStyle("color: #ff0000");
				statusBar.appendChild(label);
			}
		}
		else if (name.equals("Payment_Date"))
		{
			allocDate = value != null ? ((Timestamp) value) : null;
		}
		else if (name.equals("Payment_PayAmt"))
		{
			Payment_PayAmt = value != null ? ((BigDecimal) value) : Env.ZERO;
			calculate(false);
		}
		else if (name.equals("Payment_BankAccount_ID"))
		{
			Payment_BankAccount_ID = value != null ? ((Integer) value).intValue() : 0;
			Payment_Currency_ID = MBankAccount.get(Env.getCtx(), Payment_BankAccount_ID).getC_Currency_ID();
			Payment_Currency_Editor.setValue(Payment_Currency_ID);
		}
		else if (name.equals("Payment_DocType_ID"))
		{
			Payment_DocType_ID = value != null ? ((Integer) value).intValue() : 0;
		}else if(name.equals("DateInvoiceFrom")){
			DateInvoiedFrom = value != null ? ((Timestamp) value) : null;
			if(Invoice_BP_ID > 0 && Invoice_Currency_ID > 0)
			{
				loadBPartner();
			}else if (Invoice_BP_ID > 0){
				statusBar.getChildren().clear();
				Label label = new Label(Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), "C_Currency_ID"));
				label.setStyle("color: #ff0000");
				statusBar.appendChild(label);

			}else{
				statusBar.getChildren().clear();
				Label label = new Label(Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), "C_BPartner_ID"));
				label.setStyle("color: #ff0000");
				statusBar.appendChild(label);
			}

		}else if(name.equals("DateInvoiceTo")){
			DateInvoiedFrom = value != null ? ((Timestamp) value) : null;
			if(Invoice_BP_ID > 0 && Invoice_Currency_ID > 0)
			{
				loadBPartner();
			}else if (Invoice_BP_ID > 0){
				statusBar.getChildren().clear();
				Label label = new Label(Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), "C_Currency_ID"));
				label.setStyle("color: #ff0000");
				statusBar.appendChild(label);

			}else{
				statusBar.getChildren().clear();
				Label label = new Label(Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), "C_BPartner_ID"));
				label.setStyle("color: #ff0000");
				statusBar.appendChild(label);
			}
		}
	}



	@Override
	public void tableChanged(WTableModelEvent e) {
		boolean isUpdate = (e.getType() == WTableModelEvent.CONTENTS_CHANGED);
		//  Not a table update
		if (!isUpdate)
		{
			calculate(true);
			return;
		}

		int row = e.getFirstRow();
		int col = e.getColumn();

		if (row < 0)
			return;

		boolean isInvoice = (e.getModel().equals(invoiceTable.getModel()));

		writeOff(row, col, isInvoice, invoiceTable, false);

		//render row
		ListModelTable model = invoiceTable.getModel();
		model.updateComponent(row);

		calculate(true);
	}



	@Override
	public void onEvent(Event e) throws Exception { //

		//	Allocate
		if (e.getTarget().equals(allocateButton))
		{
			if(Payment_Org_ID == 0)
				throw new AdempiereException(Msg.getElement(Env.getCtx(), "C_Payment_ID", true) +" "
							+	Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), "AD_Org_ID"));

			if(Payment_DocType_ID == 0)
				throw new AdempiereException(Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), "C_DocType_ID"));

			if(allocDate == null)
				throw new AdempiereException(Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), "DateTrx"));

			if(Payment_BankAccount_ID == 0)
				throw new AdempiereException(Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), "C_BankAccount_ID"));

			if(Payment_BP_ID == 0)
				throw new AdempiereException(Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), "C_BPartner_ID"));

			if (Invoice_Currency_ID != Payment_Currency_ID)
			{
				throw new AdempiereException(Msg.getMsg(Env.getCtx(), "JP_DifferentCurrency"));//Different Currency
			}


			allocateButton.setEnabled(false);

			MAllocationHdr allocation = saveData();//Create Income Payment and Allocation.
			loadBPartner();
			allocateButton.setEnabled(true);
			if (allocation != null)
			{
				A link = new A(allocation.getDocumentNo());
				link.setAttribute("Record_ID", allocation.get_ID());
				link.setAttribute("AD_Table_ID", allocation.get_Table_ID());
				link.addEventListener(Events.ON_CLICK, new EventListener<Event>()
						{
					@Override
					public void onEvent(Event event) throws Exception
					{
						Component comp = event.getTarget();
						Integer Record_ID = (Integer) comp.getAttribute("Record_ID");
						Integer AD_Table_ID = (Integer) comp.getAttribute("AD_Table_ID");
						if (Record_ID != null && Record_ID > 0 && AD_Table_ID != null && AD_Table_ID > 0)
						{
							AEnv.zoom(AD_Table_ID, Record_ID);
						}
					}
				});
				statusBar.appendChild(link);
			}
		}
		else if (e.getTarget().equals(refreshButton))
		{
			loadBPartner();
		}
		else if (e.getTarget().getId().equals(SELECT_DESELECT_ALL))
		{
			ListModelTable model = invoiceTable.getModel();
			int rows = model.getSize();
			Button selectAllBtn = (Button)e.getTarget();
			Boolean selectAll = (Boolean) selectAllBtn.getAttribute(SELECT_DESELECT_ALL);
			if (selectAll == null)
				selectAll = Boolean.FALSE;
			selectAll = !selectAll;
			for (int i = 0; i < rows; i++) {
				model.setValueAt(selectAll, i, 0);
			}
			invoiceTable.setModel(model);
			selectAllBtn.setAttribute(SELECT_DESELECT_ALL, selectAll);
		}
	}


	/**************************************************************************
	 *  Save Data
	 */
	private MAllocationHdr saveData()
	{
		if (Payment_Org_ID > 0)
			Env.setContext(Env.getCtx(), form.getWindowNo(), "AD_Org_ID", Payment_Org_ID);
		else
			Env.setContext(Env.getCtx(), form.getWindowNo(), "AD_Org_ID", "");

		try
		{
			final MAllocationHdr[] allocation = new MAllocationHdr[1];
			Trx.run(new TrxRunnable()
			{
				public void run(String trxName)
				{
					statusBar.getChildren().clear();
					MPayment m_payment = creatpayment(trxName);
					allocation[0] = saveData(form.getWindowNo(), Payment_Date_Editor.getValue(), m_payment, invoiceTable, trxName);
				}
			});

			return allocation[0];
		}
		catch (Exception e)
		{
			FDialog.error(form.getWindowNo(), form, "Error", e.getLocalizedMessage());
			return null;
		}
	}

	private MPayment creatpayment(String trxName) {

		if (Payment_Org_ID == 0)
		{
			//ADialog.error(m_WindowNo, this, "Org0NotAllowed", null);
			throw new AdempiereException("@Org0NotAllowed@");
		}

		MPayment payment = new MPayment(Env.getCtx(), 0, trxName);
		payment.setAD_Org_ID(Payment_Org_ID);
		payment.setC_DocType_ID(Payment_DocType_ID);
		payment.setC_Currency_ID(Payment_Currency_ID);
		payment.setIsReceipt(true);
		payment.setC_BankAccount_ID(Payment_BankAccount_ID);
		payment.setC_BPartner_ID(Payment_BP_ID);
		payment.setPayAmt(Payment_PayAmt);
		payment.processIt(DocAction.ACTION_Complete);
//		payment.saveEx(trxName);	//Method of processIT() saved already

		return payment;
	}

	/**************************************************************************
	 *  Save Data
	 */
	public MAllocationHdr saveData(int m_WindowNo, Object date, MPayment payment, IMiniTable invoice, String trxName)
	{
		if (m_noInvoices == 0)
			return null;


		int AD_Client_ID = Env.getContextAsInt(Env.getCtx(), m_WindowNo, "AD_Client_ID");
		int AD_Org_ID = Payment_Org_ID;
		int C_BPartner_ID = Payment_BP_ID;
		int C_Order_ID = 0;
		int C_CashLine_ID = 0;
		Timestamp DateTrx = (Timestamp)date;
		int C_Currency_ID = Invoice_Currency_ID;	//	the allocation currency
		int C_Payment_ID = payment.get_ID();
		//

		if (AD_Org_ID == 0)
		{
			//ADialog.error(m_WindowNo, this, "Org0NotAllowed", null);
			throw new AdempiereException("@Org0NotAllowed@");
		}
		//
		if (log.isLoggable(Level.CONFIG)) log.config("Client=" + AD_Client_ID + ", Org=" + AD_Org_ID
			+ ", BPartner=" + C_BPartner_ID + ", Date=" + DateTrx);

		//  Invoices - Loop and generate allocations
		int iRows = invoice.getRowCount();

		//Create Allocation Hdr
		MAllocationHdr alloc = new MAllocationHdr (Env.getCtx(), true,	//	manual
			DateTrx, C_Currency_ID, Env.getContext(Env.getCtx(), "#AD_User_Name"), trxName);
		alloc.setAD_Org_ID(AD_Org_ID);
		alloc.saveEx();

		//	For all invoices
		BigDecimal availablePaymentAmt = Payment_PayAmt;
		for (int i = 0; i < iRows; i++)
		{
			if (((Boolean)invoice.getValueAt(i, 0)).booleanValue())	 		//Selected Invoice Only
			{
				KeyNamePair pp = (KeyNamePair)invoice.getValueAt(i, i_DocumentNo);
				int C_Invoice_ID = pp.getKey();
				BigDecimal DiscountAmt = (BigDecimal)invoice.getValueAt(i, i_Discount);
				BigDecimal WriteOffAmt = (BigDecimal)invoice.getValueAt(i, i_WriteOff);
				BigDecimal AppliedAmt = (BigDecimal)invoice.getValueAt(i, i_AppliedAmt);
				BigDecimal OverUnderAmt = ((BigDecimal)invoice.getValueAt(i, i_OverUnderAmt))
					.subtract(AppliedAmt).subtract(DiscountAmt).subtract(WriteOffAmt);

				if (log.isLoggable(Level.CONFIG)) log.config(".. with payment #" + ", Amt=" + availablePaymentAmt);
				if (log.isLoggable(Level.CONFIG)) log.config("Invoice #" + i + " - AppliedAmt=" + AppliedAmt);// + " -> " + AppliedAbs);

				//	Allocation Line
				MAllocationLine aLine = new MAllocationLine (alloc, AppliedAmt, DiscountAmt, WriteOffAmt, OverUnderAmt);
				aLine.setDocInfo(C_BPartner_ID, C_Order_ID, C_Invoice_ID);
				aLine.setPaymentInfo(C_Payment_ID, C_CashLine_ID);
				aLine.saveEx();

				//  Apply Discounts and WriteOff only first time
				DiscountAmt = Env.ZERO;
				WriteOffAmt = Env.ZERO;
				//  subtract amount from Payment/Invoice
				availablePaymentAmt = availablePaymentAmt.subtract(AppliedAmt);	//UnAllocated Amount
				if (log.isLoggable(Level.FINE)) log.fine("Allocation Amount=" + AppliedAmt + " - Payment=" + availablePaymentAmt);

			}//Selected Invoice Only

		}// for (int i = 0; i < iRows; i++)


		// check for unapplied payment amounts (eg from payment reversals)
		if (log.isLoggable(Level.FINE)) log.fine("Payment=" + C_Payment_ID + ", Amount=" + Payment_PayAmt);


		if ( availablePaymentAmt.signum() != 0 )
			log.log(Level.SEVERE, "Allocation not balanced -- out by " + availablePaymentAmt );

		//	Should start WF
		if (alloc.get_ID() != 0)
		{
			if (!alloc.processIt(DocAction.ACTION_Complete))
				throw new AdempiereException("Cannot complete allocation: " + alloc.getProcessMsg());
			alloc.saveEx();
		}

		//Allocation of Invoice - requires that allocation is posted
		for (int i = 0; i < iRows; i++)
		{
			//  Invoice line is selected
			if (((Boolean)invoice.getValueAt(i, 0)).booleanValue())
			{
				KeyNamePair pp = (KeyNamePair)invoice.getValueAt(i, i_DocumentNo);
				//  Invoice variables
				int C_Invoice_ID = pp.getKey();
				String sql = "SELECT invoiceOpen(C_Invoice_ID, 0) "
					+ "FROM C_Invoice WHERE C_Invoice_ID=?";
				BigDecimal open = DB.getSQLValueBD(trxName, sql, C_Invoice_ID);
				if (open != null && open.signum() == 0)	 {
					sql = "UPDATE C_Invoice SET IsPaid='Y' "
						+ "WHERE C_Invoice_ID=" + C_Invoice_ID;
					int no = DB.executeUpdate(sql, trxName);
					if (log.isLoggable(Level.CONFIG)) log.config("Invoice #" + i + " is paid - updated=" + no);
				} else {
					if (log.isLoggable(Level.CONFIG)) log.config("Invoice #" + i + " is not paid - " + open);
				}
			}
		}//For


		return alloc;
	}   //  saveData
}
