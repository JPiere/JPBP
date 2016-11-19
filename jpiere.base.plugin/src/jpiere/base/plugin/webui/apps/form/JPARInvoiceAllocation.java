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
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListModelTable;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Textbox;
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
	public static CLogger log = CLogger.getCLogger(JPARInvoiceAllocation.class);

	private boolean     m_calculating = false;

	/*コンポーネントとバイディングしている変数群*/
	//売上請求伝票検索条件
	public int         	Invoice_Org_ID = 0;			//C_Invoice.AD_Org_ID 売上請求伝票の検索に使用する
	public int         	Invoice_Currency_ID = 0;	//C_Invoice.C_Currency_ID
	public int         	Invoice_BP_ID = 0;			//C_Invoice.C_BPartner_ID 売上請求伝票の検索に使用する
	public int			JP_Corporation_ID= 0;
	public boolean 		isCorporation = false;

	//入金伝票作成条件
	public int			Payment_Org_ID = 0;			//C_Payment.AD_Org_ID
	public int         	Payment_BP_ID = 0;			//C_Payment.C_Bpartner_ID
	public int         	Payment_DocType_ID = 0;		//C_Payment.C_DocType_ID
	public int         	Payment_BankAccount_ID = 0;	//C_Payment.C_BankAccount_ID
	public int         	Payment_Currency_ID = 0;	//C_Payment.C_Currency_ID


	public BigDecimal   Payment_PayAmt = Env.ZERO;	//C_Payment.PayAmt
	public int          Payment_Charge_ID = 0;		//C_Payment.C_Charge_ID


	private ArrayList<Integer>	m_bpartnerCheck = new ArrayList<Integer>();

	public DecimalFormat format = DisplayType.getNumberFormat(DisplayType.Amount,Env.getLanguage(Env.getCtx()));

	private int         m_noInvoices = 0;
	public BigDecimal	totalInv = Env.ZERO;
	public BigDecimal 	totalPay = Env.ZERO;
	public BigDecimal	totalDiff = Env.ZERO;


	//  Index	changed if multi-currency
//	private int         i_payment = 7;

	private int         i_open = 6;
	private int         i_discount = 7;
	private int         i_writeOff = 8;
	private int         i_applied = 9;
	private int 		i_overUnder = 10;
//	private int			i_multiplier = 10;

	public Timestamp allocDate = null;

	/*【メインレイアウト】*/
	private Borderlayout mainLayout = new Borderlayout();
	private Panel southPanel = new Panel();

	/*【パラメータパネル】*/
	private Panel parameterPanel = new Panel();						//検索条件などを設定するパラメータパネル
	private Grid parameterLayout = GridFactory.newGridLayout();		//パラメータパネルのレイアウト
	//売上請求伝票検索パネル
	//1段目
	private Label Invoice_Org_Label = new Label();					//売上請求伝票検索用組織マスタラベル
	private WTableDirEditor Invoice_Org_Editor;						//売上請求伝票検索用組織マスタ選択リスト
	private Label Invoice_Currency_Label = new Label();				//入金伝票作成用通貨ラベル
	private WTableDirEditor Invoice_Currency_Editor = null;			//入金伝票作成用通貨検索
	//2段目
	private Label Invoice_BP_Label = new Label();					//売上請求伝票検索用取引先マスタラベル
	private WSearchEditor Invoice_BP_Editor = null;					//売上請求伝票検索用取引先マスタ検索
	private Label Corportion_Label = new Label();					//売上請求伝票検索用法人マスタラベル
	private WTableDirEditor Corportion_Editor = null;				//売上請求伝票検索用法人マスタ検索

	//入金伝票作成条件パネル
	//1段目
	private Label Payment_Org_Label = new Label();					//入金伝票作成用組織マスタラベル
	private WTableDirEditor Payment_Org_Editor;						//入金伝票作成用組織マスタ選択リスト
	private Label Payment_DocType_Label = new Label();				//入金伝票作成用伝票タイプラベル
	private WTableDirEditor Payment_DocType_Editor = null;			//入金伝票作成用伝票タイプ選択リスト
	private Label Payment_Date_Label = new Label();					//入金伝票作成用日付ラベル
	private WDateEditor Payment_Date_Editor = new WDateEditor();	//入金伝票作成用日付入力
	//2段目
	private Label Payment_Account_Label = new Label();				//入金伝票作成用アカウントラベル
	private WTableDirEditor Payment_Account_Editor = null;			//入金伝票作成用アカウント選択リスト
	private Label Payment_BP_Label = new Label();					//入金伝票作成用取引先マスタラベル
	private WSearchEditor Payment_BP_Editor = null;					//入金伝票作成用取引先マスタ検索
	//3段目
	private Label Payment_Currency_Label = new Label();				//入金伝票作成用通貨ラベル
	private WTableDirEditor Payment_Currency_Editor = null;			//入金伝票作成用通貨検索
	private Checkbox multiCurrency = new Checkbox();				//マルチ通貨
	private Label PayAmt_Label = new Label();						//入金伝票作成用支払(入金）金額ラベル
	private WNumberEditor PayAmt_Editor = new WNumberEditor();		//入金伝票作成用支払(入金）金額
	private Checkbox autoWriteOff = new Checkbox();					//入金伝票作成用自動貸倒処理


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
	private Label chargeLabel = new Label();
	private WTableDirEditor chargePick = null;
	private Button allocateButton = new Button();
	private Button refreshButton = new Button();

	/*【ステータスバー】*/
	private Hlayout statusBar = new Hlayout();


    public JPARInvoiceAllocation() throws IOException
    {

    	Env.setContext(Env.getCtx(), form.getWindowNo(), "IsSOTrx", "Y");   //  defaults to no
		try
		{
			dynInit();
			zkInit();
			calculate();
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
		//
		if (log.isLoggable(Level.INFO)) log.info("Currency=" + Invoice_Currency_ID);

		Invoice_Org_ID = Env.getAD_Org_ID(Env.getCtx());


		// 組織マスタ選択リストの初期化
		int AD_Column_ID = 0;

		AD_Column_ID = MColumn.getColumn_ID("C_BPartner", "AD_Org_ID");
		MLookup lookupOrg = MLookupFactory.get(Env.getCtx(), form.getWindowNo(), 0, AD_Column_ID, DisplayType.TableDir);
		Invoice_Org_Editor = new WTableDirEditor("Invoice_Org_ID", true, false, true, lookupOrg);
		Invoice_Org_Editor.setValue(Env.getAD_Org_ID(Env.getCtx()));
		Invoice_Org_ID = Env.getAD_Org_ID(Env.getCtx());
		Invoice_Org_Editor.addValueChangeListener(this);

		Payment_Org_Editor = new WTableDirEditor("Payment_Org_ID", true, false, true, lookupOrg);
		Payment_Org_Editor.setValue(Env.getAD_Org_ID(Env.getCtx()));
		Payment_Org_ID = Env.getAD_Org_ID(Env.getCtx());
		Payment_Org_Editor.addValueChangeListener(this);

		//取引先マスタ検索の初期化
		AD_Column_ID = MColumn.getColumn_ID("C_Order","C_BPartner_ID");
		MLookup lookupBP = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, AD_Column_ID, DisplayType.Search);
		Invoice_BP_Editor = new WSearchEditor("Invoice_BPartner_ID", true, false, true, lookupBP);
		Invoice_BP_Editor.addValueChangeListener(this);

		Payment_BP_Editor = new WSearchEditor("Payment_BPartner_ID", true, false, true, lookupBP);
		Payment_BP_Editor.addValueChangeListener(this);

		//法人マスタ検索の初期化
		AD_Column_ID = MColumn.getColumn_ID("C_BPartner","JP_Corporation_ID");
		if(AD_Column_ID > 0)
		{
			MLookup lookupCorp = MLookupFactory.get(Env.getCtx(), form.getWindowNo(), AD_Column_ID,
					DisplayType.TableDir, Env.getLanguage(Env.getCtx()), "JP_Corporation_ID", 0,
					false, "JP_Corporation.JP_Corporation_ID = (SELECT JP_Corporation_ID FROM C_BPartner WHERE C_BPartner_ID = @C_BPartner_ID@)");
			Corportion_Editor = new WTableDirEditor("JP_Corporation_ID", false, false, true, lookupCorp);
			Corportion_Editor.addValueChangeListener(this);
			isCorporation = true;
		}

		//伝票タイプ選択リストの初期化
		AD_Column_ID = MColumn.getColumn_ID("C_Payment","C_DocType_ID");//5302;
		MLookup lookupDocType = MLookupFactory.get(Env.getCtx(), form.getWindowNo(), AD_Column_ID,
				DisplayType.TableDir, Env.getLanguage(Env.getCtx()), "C_DocType_ID", 0,
				false, "C_DocType.DocBaseType = 'ARR' AND C_DocType.IsSOTrx = 'Y'");
		Payment_DocType_Editor = new WTableDirEditor("Payment_DocType_ID", true, false, true, lookupDocType);
		Payment_DocType_Editor.addValueChangeListener(this);

		//アカウント選択リストの初期化
		AD_Column_ID = MColumn.getColumn_ID("C_Payment","C_BankAccount_ID");//3880;
		MLookup lookupAcount = MLookupFactory.get(Env.getCtx(), form.getWindowNo(), AD_Column_ID,
				DisplayType.TableDir, Env.getLanguage(Env.getCtx()), "C_BankAccount_ID", 0,
				false, "C_BankAccount.AD_Org_ID = @AD_Org_ID@");
		Payment_Account_Editor = new WTableDirEditor("Payment_BankAccount_ID", true, false, true, lookupAcount);
		Payment_Account_Editor.addValueChangeListener(this);

		// 日付けにログイン日付(≒入金日)をセット
		Payment_Date_Editor.setValue(Env.getContextAsDate(Env.getCtx(), "#Date"));
		allocDate = Env.getContextAsDate(Env.getCtx(), "#Date");
		Payment_Date_Editor.addValueChangeListener(this);

		//通貨マスタ選択リストの初期化
		AD_Column_ID = MColumn.getColumn_ID("C_Invoice","C_Currency_ID");
		MLookup lookupCur = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, AD_Column_ID, DisplayType.TableDir);
		Invoice_Currency_Editor = new WTableDirEditor("Invoice_Currency_ID", true, false, true, lookupCur);
		Invoice_Currency_Editor.setValue(new Integer(Invoice_Currency_ID));
		Invoice_Currency_Editor.addValueChangeListener(this);

		Payment_Currency_Editor = new WTableDirEditor("Payment_Currency_ID", true, true, true, lookupCur);
		Payment_Currency_Editor.setValue(new Integer(Invoice_Currency_ID));


		//支払(入金)金額の初期化
		PayAmt_Editor = new WNumberEditor("Payment_PayAmt", true, false, true, DisplayType.Amount, "payAmt");
		PayAmt_Editor.setValue(Env.ZERO);
		PayAmt_Editor.addValueChangeListener(this);

		//  Translation
		statusBar.appendChild(new Label(Msg.getMsg(Env.getCtx(), "AllocateStatus")));
		statusBar.setVflex("min");

		//  Charge
		AD_Column_ID = 61804;    //  C_AllocationLine.C_Charge_ID
		MLookup lookupCharge = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, AD_Column_ID, DisplayType.TableDir);
		chargePick = new WTableDirEditor("C_Charge_ID", false, false, true, lookupCharge);
		chargePick.setValue(new Integer(Payment_Charge_ID));
		chargePick.addValueChangeListener(this);
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
			invoiceSearchGB.appendChild(new Caption("売上請求伝票検索"));
			Grid invoiceSearch  = new Grid();
			invoiceSearch.setStyle("background-color: #E9F0FF");
			invoiceSearch.setStyle("border: none");
			invoiceSearchGB.appendChild(invoiceSearch);
			Rows rows = invoiceSearch.newRows();
			row = rows.newRow();
				Invoice_Org_Label.setText(Msg.translate(Env.getCtx(), "AD_Org_ID"));	//組織ラベル
				row.appendCellChild(Invoice_Org_Label.rightAlign());
				ZKUpdateUtil.setHflex(Invoice_Org_Editor.getComponent(), "true");
				row.appendCellChild(Invoice_Org_Editor.getComponent(),1);
				row.appendCellChild(new Space(),1);
				Invoice_Currency_Label.setText(Msg.translate(Env.getCtx(), "C_Currency_ID"));	//通貨ラベル
				row.appendCellChild(Invoice_Currency_Label.rightAlign(),1);
				ZKUpdateUtil.setHflex(Invoice_Currency_Editor.getComponent(), "true");	//通貨マスタ検索
				row.appendCellChild(Invoice_Currency_Editor.getComponent(),1);
				row.appendCellChild(new Space(),1);
				row.setStyle("background-color: #ffffff");
			row = rows.newRow();
				Invoice_BP_Label.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));	//取引先マスタラベル
				row.appendCellChild(Invoice_BP_Label.rightAlign());
				ZKUpdateUtil.setHflex(Invoice_BP_Editor.getComponent(), "true");	//取引先マスタ検索
				row.appendCellChild(Invoice_BP_Editor.getComponent(),2);
				if(isCorporation)
				{
					Corportion_Label.setText(Msg.translate(Env.getCtx(), "JP_Corporation_ID"));	//取引先マスタラベル
					row.appendCellChild(Corportion_Label.rightAlign());
					ZKUpdateUtil.setHflex(Corportion_Editor.getComponent(), "true");	//取引先マスタ検索
					row.appendCellChild(Corportion_Editor.getComponent(),2);
				}
				row.setStyle("background-color: #ffffff");


		//パラメータパネル-2段目(入金伝票作成条件パネル)
		row = parameterLayoutRows.newRow();
			Groupbox paymentGB = new Groupbox();
			row.appendCellChild(paymentGB,8);
			paymentGB.appendChild(new Caption("入金伝票作成情報"));
			Grid paymentCondition  = new Grid();
			paymentCondition.setStyle("border: none");
			paymentGB.appendChild(paymentCondition);
			rows = paymentCondition.newRows();

			//入金伝票作成条件パネル1段目
			row = rows.newRow();
				Payment_Org_Label.setText(Msg.translate(Env.getCtx(), "AD_Org_ID"));	//組織ラベル
				row.appendCellChild(Payment_Org_Label.rightAlign());
				ZKUpdateUtil.setHflex(Payment_Org_Editor.getComponent(), "true");
				Payment_Org_Editor.setMandatory(true);
				row.appendCellChild(Payment_Org_Editor.getComponent(),1);
				Payment_DocType_Label.setText(Msg.translate(Env.getCtx(), "C_DocType_ID"));		//伝票タイプラベル
				row.appendCellChild(Payment_DocType_Label.rightAlign(),1);
				ZKUpdateUtil.setHflex(Payment_DocType_Editor.getComponent(), "true");
				Payment_DocType_Editor.setMandatory(true);
				row.appendCellChild(Payment_DocType_Editor.getComponent(),1);
				Payment_Date_Label.setText(Msg.getMsg(Env.getCtx(), "Date"));					//日付ラベル
				row.appendCellChild(Payment_Date_Label.rightAlign(),1);
				row.appendCellChild(Payment_Date_Editor.getComponent());
				row.setStyle("background-color: #ffffff");

			//入金伝票作成条件パネル2段目
			row = rows.newRow();
				Payment_Account_Label.setText(Msg.translate(Env.getCtx(), "C_BankAccount_ID"));	//アカウントラベル
				row.appendCellChild(Payment_Account_Label.rightAlign(),1);
				ZKUpdateUtil.setHflex(Payment_Account_Editor.getComponent(), "true");
				row.appendCellChild(Payment_Account_Editor.getComponent(),2);
				row.setStyle("background-color: #ffffff");
				Payment_BP_Label.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));	//取引先マスタラベル
				row.appendCellChild(Payment_BP_Label.rightAlign());
				ZKUpdateUtil.setHflex(Payment_BP_Editor.getComponent(), "true");	//取引先マスタ検索
				row.appendCellChild(Payment_BP_Editor.getComponent(),2);
				row.setStyle("background-color: #ffffff");

			//入金伝票作成条件パネル3段目
			row = rows.newRow();
				Payment_Currency_Label.setText(Msg.translate(Env.getCtx(), "C_Currency_ID"));	//通貨ラベル
				row.appendCellChild(Payment_Currency_Label.rightAlign(),1);
				ZKUpdateUtil.setHflex(Payment_Currency_Editor.getComponent(), "true");//通貨マスタ検索
				row.appendCellChild(Payment_Currency_Editor.getComponent(),1);
//				multiCurrency.setText(Msg.getMsg(Env.getCtx(), "MultiCurrency"));		//マルチ通貨は未実装
//				multiCurrency.addActionListener(this);
//				row.appendCellChild(multiCurrency,1);
//				row.appendCellChild(new Space(),1);
				PayAmt_Label.setText(Msg.translate(Env.getCtx(), "PayAMT"));
				row.appendCellChild(PayAmt_Label.rightAlign());
				row.appendCellChild(PayAmt_Editor.getComponent());						//支払(入金)額
//				autoWriteOff.setSelected(false);										//自動貸倒処理は未実装
//				autoWriteOff.setText(Msg.getMsg(Env.getCtx(), "AutoWriteOff", true));
//				autoWriteOff.setTooltiptext(Msg.getMsg(Env.getCtx(), "AutoWriteOff", false));
//				row.appendCellChild(autoWriteOff);
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
		//情報パネル-北：請求書パネル-南
		South south = new South();
		south.setStyle("border: none");
		invoiceLayout.appendChild(south);
		south.appendChild(invoiceInfo.rightAlign());



		//【メインレイアウト(Borderlayout)-南】
		south = new South();
		mainLayout.appendChild(south);
		south.setStyle("border: none");

		//南パネル
		south.appendChild(southPanel);

		southPanel.appendChild(new Separator());
		southPanel.appendChild(statusBar);					//StatusBar = Hlayout
		southPanel.appendChild(allocationPanel);

		allocationPanel.appendChild(allocationLayout);		//allocationLayout = Grid
		ZKUpdateUtil.setHflex(allocationLayout, "min");
		rows = allocationLayout.newRows();
		row = rows.newRow();
		differenceLabel.setText(Msg.getMsg(Env.getCtx(), "Difference"));
		row.appendCellChild(differenceLabel.rightAlign());
		row.appendCellChild(allocCurrencyLabel.rightAlign());
		ZKUpdateUtil.setHflex(differenceField, "true");
		differenceField.setText("0");
		differenceField.setReadonly(true);
		differenceField.setStyle("text-align: right");
		row.appendCellChild(differenceField);
		chargeLabel.setText(" " + Msg.translate(Env.getCtx(), "C_Charge_ID"));
		row.appendCellChild(chargeLabel.rightAlign());
		ZKUpdateUtil.setHflex(chargePick.getComponent(), "true");
		row.appendCellChild(chargePick.getComponent());
		allocateButton.setLabel(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "Process")));
		allocateButton.addActionListener(this);
		ZKUpdateUtil.setHflex(allocateButton, "true");
		row.appendCellChild(allocateButton);
		refreshButton.setLabel(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "Refresh")));
		refreshButton.addActionListener(this);
		refreshButton.setAutodisable("self");
		row.appendCellChild(refreshButton);

	}

	public Vector<Vector<Object>> getInvoiceData(boolean isMultiCurrency, Object date, IMiniTable invoiceTable)
	{
		/********************************
		 *  Load unpaid Invoices
		 *      1-TrxDate, 2-Value, (3-Currency, 4-InvAmt,)
		 *      5-ConvAmt, 6-ConvOpen, 7-ConvDisc, 8-WriteOff, 9-Applied
		 *
		 SELECT i.DateInvoiced,i.DocumentNo,i.C_Invoice_ID,c.ISO_Code,
		 i.GrandTotal*i.MultiplierAP "GrandTotal",
		 currencyConvert(i.GrandTotal*i.MultiplierAP,i.C_Currency_ID,i.C_Currency_ID,i.DateInvoiced,i.C_ConversionType_ID,i.AD_Client_ID,i.AD_Org_ID) "GrandTotal $",
		 invoiceOpen(C_Invoice_ID,C_InvoicePaySchedule_ID) "Open",
		 currencyConvert(invoiceOpen(C_Invoice_ID,C_InvoicePaySchedule_ID),i.C_Currency_ID,i.C_Currency_ID,i.DateInvoiced,i.C_ConversionType_ID,i.AD_Client_ID,i.AD_Org_ID)*i.MultiplierAP "Open $",
		 invoiceDiscount(i.C_Invoice_ID,SysDate,C_InvoicePaySchedule_ID) "Discount",
		 currencyConvert(invoiceDiscount(i.C_Invoice_ID,SysDate,C_InvoicePaySchedule_ID),i.C_Currency_ID,i.C_Currency_ID,i.DateInvoiced,i.C_ConversionType_ID,i.AD_Client_ID,i.AD_Org_ID)*i.Multiplier*i.MultiplierAP "Discount $",
		 i.MultiplierAP, i.Multiplier
		 FROM C_Invoice_v i INNER JOIN C_Currency c ON (i.C_Currency_ID=c.C_Currency_ID)
		 WHERE -- i.IsPaid='N' AND i.Processed='Y' AND i.C_BPartner_ID=1000001
		 */
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		StringBuilder sql = new StringBuilder("SELECT i.DateInvoiced,i.DocumentNo,i.C_Invoice_ID," //  1..3
			+ "c.ISO_Code,i.GrandTotal*i.MultiplierAP, "                            //  4..5    Orig Currency
			+ "currencyConvert(i.GrandTotal*i.MultiplierAP,i.C_Currency_ID,?,?,i.C_ConversionType_ID,i.AD_Client_ID,i.AD_Org_ID), " //  6   #1  Converted, #2 Date
			+ "currencyConvert(invoiceOpen(C_Invoice_ID,C_InvoicePaySchedule_ID),i.C_Currency_ID,?,?,i.C_ConversionType_ID,i.AD_Client_ID,i.AD_Org_ID)*i.MultiplierAP, "  //  7   #3, #4  Converted Open
			+ "currencyConvert(invoiceDiscount"                               //  8       AllowedDiscount
			+ "(i.C_Invoice_ID,?,C_InvoicePaySchedule_ID),i.C_Currency_ID,?,i.DateInvoiced,i.C_ConversionType_ID,i.AD_Client_ID,i.AD_Org_ID)*i.Multiplier*i.MultiplierAP,"               //  #5, #6
			+ "i.MultiplierAP ,org.Value ");
		if(isCorporation)
			sql.append(" ,bp.Value ");

		sql.append("FROM C_Invoice_v i"		//  corrected for CM/Split
			+ " INNER JOIN C_Currency c ON (i.C_Currency_ID=c.C_Currency_ID)"
			+ " INNER JOIN AD_Org org ON (i.AD_Org_ID = org.AD_Org_ID)" );
		if(isCorporation)
			sql.append(" LEFT OUTER JOIN C_BPartner bp on( i.C_BPartner_ID = bp.C_BPartner_ID) ");

		sql.append("WHERE i.IsPaid='N' AND i.Processed='Y'");
		if(JP_Corporation_ID == 0)					//  #7
			sql.append(" AND i.C_BPartner_ID=?");
		else
			sql.append(" AND bp.JP_Corporation_ID=?");
		if (!isMultiCurrency)
			sql.append(" AND i.C_Currency_ID=?");                                   //  #8
		if (Invoice_Org_ID != 0 )
			sql.append(" AND i.AD_Org_ID=" + Invoice_Org_ID);
		sql.append(" ORDER BY i.DateInvoiced, i.DocumentNo");
		if (log.isLoggable(Level.FINE)) log.fine("InvSQL=" + sql.toString());

		// role security
		sql = new StringBuilder( MRole.getDefault(Env.getCtx(), false).addAccessSQL( sql.toString(), "i", MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO ) );

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), null);
			pstmt.setInt(1, Invoice_Currency_ID);
			pstmt.setTimestamp(2, (Timestamp)date);
			pstmt.setInt(3, Invoice_Currency_ID);
			pstmt.setTimestamp(4, (Timestamp)date);
			pstmt.setTimestamp(5, (Timestamp)date);
			pstmt.setInt(6, Invoice_Currency_ID);
			if(JP_Corporation_ID == 0)
				pstmt.setInt(7, Invoice_BP_ID);
			else
				pstmt.setInt(7, JP_Corporation_ID);
			if (!isMultiCurrency)
				pstmt.setInt(8, Invoice_Currency_ID);
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				Vector<Object> line = new Vector<Object>();
				line.add(new Boolean(false));       //  0-Selection
				line.add(rs.getTimestamp(1));       //  1-TrxDate
				KeyNamePair pp = new KeyNamePair(rs.getInt(3), rs.getString(2));//C_Invoice_ID,DocumentNo
				line.add(pp);                       //  2-Value
				if (isMultiCurrency)
				{
					line.add(rs.getString(4));      //  3-Currency
					line.add(rs.getBigDecimal(5));  //  4-Orig Amount
				}
				line.add(rs.getBigDecimal(6));      //  3/5-ConvAmt
				BigDecimal open = rs.getBigDecimal(7);
				if (open == null)		//	no conversion rate
					open = Env.ZERO;
				line.add(open);      				//  4/6-ConvOpen
				BigDecimal discount = rs.getBigDecimal(8);
				if (discount == null)	//	no concersion rate
					discount = Env.ZERO;
				line.add(discount);					//  5/7-ConvAllowedDisc
				line.add(Env.ZERO);      			//  6/8-WriteOff
				line.add(Env.ZERO);					// 7/9-Applied
				line.add(open);				    //  8/10-OverUnder


//				line.add(rs.getBigDecimal(9));		//	8/10-Multiplier
				//	Add when open <> 0 (i.e. not if no conversion rate)
				if (Env.ZERO.compareTo(open) != 0)
					data.add(line);

				line.add(rs.getString(10));			//組織
				if(isCorporation)
					line.add(rs.getString(11));		//取引先
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

	public Vector<String> getInvoiceColumnNames(boolean isMultiCurrency)
	{
		//  Header Info
		Vector<String> columnNames = new Vector<String>();
		columnNames.add(Msg.getMsg(Env.getCtx(), "Select"));
		columnNames.add(Msg.translate(Env.getCtx(), "Date"));
		columnNames.add(Util.cleanAmp(Msg.translate(Env.getCtx(), "DocumentNo")));
		if (isMultiCurrency)
		{
			columnNames.add(Msg.getMsg(Env.getCtx(), "TrxCurrency"));
			columnNames.add(Msg.translate(Env.getCtx(), "Amount"));
		}
		columnNames.add(Msg.getMsg(Env.getCtx(), "ConvertedAmount"));
		columnNames.add(Msg.getMsg(Env.getCtx(), "OpenAmt"));
		columnNames.add(Msg.getMsg(Env.getCtx(), "Discount"));
		columnNames.add(Msg.getMsg(Env.getCtx(), "WriteOff"));
		columnNames.add(Msg.getMsg(Env.getCtx(), "AppliedAmt"));
		columnNames.add(Msg.getMsg(Env.getCtx(), "OverUnderAmt"));
//		columnNames.add(" ");	//	Multiplier

		columnNames.add(Msg.getElement(Env.getCtx(), "AD_Org_ID"));
		if(isCorporation)
			columnNames.add(Msg.getElement(Env.getCtx(), "C_BPartner_ID"));


		return columnNames;
	}

	public void setInvoiceColumnClass(IMiniTable invoiceTable, boolean isMultiCurrency)
	{
		int i = 0;
		invoiceTable.setColumnClass(i++, Boolean.class, false);         //  0-Selection
		invoiceTable.setColumnClass(i++, Timestamp.class, true);        //  1-TrxDate
		invoiceTable.setColumnClass(i++, String.class, true);           //  2-Value
		if (isMultiCurrency)
		{
			invoiceTable.setColumnClass(i++, String.class, true);       //  3-Currency
			invoiceTable.setColumnClass(i++, BigDecimal.class, true);   //  4-Amt
		}
		invoiceTable.setColumnClass(i++, BigDecimal.class, true);       //  5-ConvAmt
		invoiceTable.setColumnClass(i++, BigDecimal.class, true);       //  6-ConvAmt Open
		invoiceTable.setColumnClass(i++, BigDecimal.class, false);      //  7-Conv Discount
		invoiceTable.setColumnClass(i++, BigDecimal.class, false);      //  8-Conv WriteOff
		invoiceTable.setColumnClass(i++, BigDecimal.class, false);      //  9-Conv OverUnder
		invoiceTable.setColumnClass(i++, BigDecimal.class, true);		//	10-Conv Applied
//		invoiceTable.setColumnClass(i++, BigDecimal.class, true);      	//  10-Multiplier
//		invoiceTable.setColumnClass(i++, String.class, true);       	//  11-Currency
		invoiceTable.setColumnClass(i++, String.class, true);       	//  11-OrgValue
		if(isCorporation)
			invoiceTable.setColumnClass(i++, String.class, true);       	//  12-BPValue
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

	public void calculate(boolean isMultiCurrency)
	{
		i_open = isMultiCurrency ? 6 : 4;
		i_discount = isMultiCurrency ? 7 : 5;
		i_writeOff = isMultiCurrency ? 8 : 6;
		i_applied = isMultiCurrency ? 9 : 7;
		i_overUnder = isMultiCurrency ? 10 : 8;
//		i_multiplier = isMultiCurrency ? 10 : 8;
	}

	public String calculateInvoice(IMiniTable invoice, boolean isMultiCurrency)
	{
		//  Invoices
		totalInv = Env.ZERO;
		int rows = invoice.getRowCount();
		m_noInvoices = 0;

		for (int i = 0; i < rows; i++)
		{
			if (((Boolean)invoice.getValueAt(i, 0)).booleanValue())
			{
				Timestamp ts = (Timestamp)invoice.getValueAt(i, 1);
				if ( !isMultiCurrency )  // converted amounts only valid for selected date
					allocDate = TimeUtil.max(allocDate, ts);
				BigDecimal bd = (BigDecimal)invoice.getValueAt(i, i_applied);
				totalInv = totalInv.add(bd);  //  Applied Inv
				m_noInvoices++;
				if (log.isLoggable(Level.FINE)) log.fine("Invoice_" + i + " = " + bd + " - Total=" + totalPay);
			}
		}
		return String.valueOf(m_noInvoices) + " - "
			+ Msg.getMsg(Env.getCtx(), "Sum") + "  " + format.format(totalInv) + " ";
	}


	private void setAllocateButton() {
		if (totalDiff.signum() == 0 ^ Payment_Charge_ID > 0 )
		{
			allocateButton.setEnabled(true);
//			chargePick.setValue(m_C_Charge_ID);
		}
		else
		{
			allocateButton.setEnabled(false);
		}

		if ( totalDiff.signum() == 0 )
		{
				chargePick.setValue(null);
				Payment_Charge_ID = 0;
		}
	}

	private void loadBPartner ()
	{
		checkBPartner();

		Vector<Vector<Object>> data = getInvoiceData(multiCurrency.isSelected(), Payment_Date_Editor.getValue(), invoiceTable);
		Vector<String> columnNames = getInvoiceColumnNames(multiCurrency.isSelected());

		invoiceTable.clear();

		//  Remove previous listeners
		invoiceTable.getModel().removeTableModelListener(this);

		//  Set Model
		ListModelTable modelI = new ListModelTable(data);
		modelI.addTableModelListener(this);
		invoiceTable.setData(modelI, columnNames);
		setInvoiceColumnClass(invoiceTable, multiCurrency.isSelected());
		//

		calculate(multiCurrency.isSelected());

		//  Calculate Totals
		calculate();

		statusBar.getChildren().clear();
	}   //  loadBPartner


	public void calculate()
	{
//		allocDate = null;

		invoiceInfo.setText(calculateInvoice(invoiceTable, multiCurrency.isSelected()));

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
		BigDecimal open = (BigDecimal)invoice.getValueAt(row, i_open);
		BigDecimal discount = (BigDecimal)invoice.getValueAt(row, i_discount);
		BigDecimal applied = (BigDecimal)invoice.getValueAt(row, i_applied);
		BigDecimal writeOff = (BigDecimal) invoice.getValueAt(row, i_writeOff);
		BigDecimal overUnder = (BigDecimal) invoice.getValueAt(row, i_overUnder);
		int openSign = open.signum();

		if (col == 0)  //selection
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
				if ( col == i_discount )       // then edit writeoff
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
			if ( col == i_applied )
				overUnder = overUnder.subtract(difference);
			else
				applied = applied.subtract(difference);

		}

		//	Warning if write Off > 30%
		if (isAutoWriteOff && writeOff.doubleValue()/open.doubleValue() > .30)
			msg = "AllocationWriteOffWarn";

		invoice.setValueAt(discount, row, i_discount);
		invoice.setValueAt(applied, row, i_applied);
		invoice.setValueAt(writeOff, row, i_writeOff);
		invoice.setValueAt(overUnder, row, i_overUnder);


		m_calculating = false;

		return msg;
	}


	@Override
	public void valueChange(ValueChangeEvent e) {
		String name = e.getPropertyName();
		Object value = e.getNewValue();
		if (log.isLoggable(Level.CONFIG)) log.config(name + "=" + value);


		if (name.equals("Invoice_Org_ID"))
		{
			Invoice_Org_ID = value != null ? ((Integer) value).intValue() : 0;
			loadBPartner();

			//入金伝票用の組織マスタのIDが"0(*)"だったら、売上請求伝票の組織を設定する。
			if(Invoice_Org_ID != 0 && Payment_Org_ID == 0)
			{
				Payment_Org_ID = Invoice_Org_ID;
				Payment_Org_Editor.setValue(value);

				//アカウント情報の初期化
				Env.setContext(Env.getCtx(), form.getWindowNo(), "AD_Org_ID", Payment_Org_ID);//ダイナミックバリデーションのため
				MLookup mLookup = (MLookup)Payment_Account_Editor.getLookup();
				Payment_Account_Editor.setValue(null);
				Payment_BankAccount_ID = 0;
				mLookup.refresh();

			}
		}
		else if (name.equals("Payment_Org_ID") )
		{
			Payment_Org_ID = value != null ? ((Integer) value).intValue() : 0;

			//入金伝票作成用の組織マスタが変更したら、それに結びつくアカウント情報を初期化する。
			Env.setContext(Env.getCtx(), form.getWindowNo(), "AD_Org_ID", Payment_Org_ID);//ダイナミックバリデーションのため
			MLookup mLookup = (MLookup)Payment_Account_Editor.getLookup();
			Payment_Account_Editor.setValue(null);
			Payment_BankAccount_ID = 0;
			mLookup.refresh();
		}
		//charge
		else if (name.equals("C_Charge_ID") )
		{
			Payment_Charge_ID = value!=null? ((Integer) value).intValue() : 0;
			setAllocateButton();
		}
		//  BPartner
		else if (e.getSource().equals(Invoice_BP_Editor))
		{
			Invoice_BP_ID = value != null ? ((Integer) value).intValue() : 0;

			if(Payment_BP_ID == 0){
				Payment_BP_Editor.setValue(value);
				Payment_BP_ID = Invoice_BP_ID;
			}

			Env.setContext(Env.getCtx(), form.getWindowNo(), "C_BPartner_ID", Invoice_BP_ID);//ダイナミックバリデーションのためにコンテキストに変数追加
			MLookup mLookup = (MLookup)Corportion_Editor.getLookup();
			Corportion_Editor.setValue(null);
			JP_Corporation_ID = 0;
			mLookup.refresh();

			loadBPartner();

		}else if (e.getSource().equals(Payment_BP_Editor) )
		{
			Payment_BP_ID = value != null ? ((Integer) value).intValue() : 0;
			Payment_BP_ID  = ((Integer)value).intValue();
		}
		else if(name.equals("JP_Corporation_ID"))
		{
			JP_Corporation_ID = value != null ? ((Integer) value).intValue() : 0;
			loadBPartner();
		}
		//	Currency
		else if (name.equals("Invoice_Currency_ID"))
		{
			Invoice_Currency_ID = value != null ? ((Integer) value).intValue() : 0;
			loadBPartner();
		}
		//TODO:マルチ通貨は当面の間は対応しない。
//		else if (name.equals("Date") && multiCurrency.isSelected())
//		{
//			loadBPartner();
//		}
		else if (name.equals("Date"))
		{
			allocDate = value != null ? ((Timestamp) value) : null;
		}

		else if (name.equals("Payment_PayAmt")){
			Payment_PayAmt = value != null ? ((BigDecimal) value) : Env.ZERO;
			calculate();
		}

		else if (name.equals("Payment_BankAccount_ID")){
			Payment_BankAccount_ID = value != null ? ((Integer) value).intValue() : 0;
			Payment_Currency_ID = MBankAccount.get(Env.getCtx(), Payment_BankAccount_ID).getC_Currency_ID();
			Payment_Currency_Editor.setValue(Payment_Currency_ID);
		}

		else if (name.equals("Payment_DocType_ID")){
			Payment_DocType_ID = value != null ? ((Integer) value).intValue() : 0;
		}
	}



	@Override
	public void tableChanged(WTableModelEvent e) {
		boolean isUpdate = (e.getType() == WTableModelEvent.CONTENTS_CHANGED);
		//  Not a table update
		if (!isUpdate)
		{
			calculate();
			return;
		}

		int row = e.getFirstRow();
		int col = e.getColumn();

		if (row < 0)
			return;

		boolean isInvoice = (e.getModel().equals(invoiceTable.getModel()));
		boolean isAutoWriteOff = autoWriteOff.isSelected();

		String msg = writeOff(row, col, isInvoice, invoiceTable, isAutoWriteOff);//TODO 情報について修正する

		//render row
		ListModelTable model = invoiceTable.getModel();
		model.updateComponent(row);


		calculate();
	}



	@Override
	public void onEvent(Event e) throws Exception { //

		if (Invoice_Currency_ID != Payment_Currency_ID)
		{
			throw new AdempiereException("通貨が異なります。");
		}else if(Payment_DocType_ID == 0){
			throw new AdempiereException("伝票タイプを入力して下さい。");
		}

//		else if (e.getTarget().equals(multiCurrency))//マルチ通貨は未対応
//		{
//			loadBPartner();
//		}

		//	Allocate
		else if (e.getTarget().equals(allocateButton))
		{
			allocateButton.setEnabled(false);

			MAllocationHdr allocation = saveData();//入金伝票を作成し消込処理を行う。
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
	}


	/**************************************************************************
	 *  Save Data
	 */
	private MAllocationHdr saveData()//TODO:スレッドにする必要性ある!?
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
		payment.saveEx(trxName);//TODO:processITで保存されているので、最後にsaveEx(trxName)が必要かどうか、トランザクションの観点で要検証。

		return payment;
	}

	/**************************************************************************
	 *  Save Data
	 */
	public MAllocationHdr saveData(int m_WindowNo, Object date, MPayment payment, IMiniTable invoice, String trxName)
	{
		if (m_noInvoices == 0)
			return null;

		//  fixed fields
		int AD_Client_ID = Env.getContextAsInt(Env.getCtx(), m_WindowNo, "AD_Client_ID");
		int AD_Org_ID = Payment_Org_ID;			//入金伝票と同じ組織で消込伝票は作成する。
		int C_BPartner_ID = 0;					//売上請求伝票の取引先マスタを設定する。
		int C_Order_ID = 0;
		int C_CashLine_ID = 0;
		Timestamp DateTrx = (Timestamp)date;
		int C_Currency_ID = Invoice_Currency_ID;	//	the allocation currency
		int C_Payment_ID = payment.get_ID();
		//

		//必須入力チェックの実装
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

		//消込伝票ヘッダの作成
		MAllocationHdr alloc = new MAllocationHdr (Env.getCtx(), true,	//	manual
			DateTrx, C_Currency_ID, Env.getContext(Env.getCtx(), "#AD_User_Name"), trxName);
		alloc.setAD_Org_ID(AD_Org_ID);
		alloc.saveEx();

		//	For all invoices
		BigDecimal availablePaymentAmt = Payment_PayAmt;			 //入金額
		for (int i = 0; i < iRows; i++)
		{
			if (((Boolean)invoice.getValueAt(i, 0)).booleanValue())	 		//選択行だけの処理する
			{
				KeyNamePair pp = (KeyNamePair)invoice.getValueAt(i, 2);    //  Value
				//  Invoice variables
				int C_Invoice_ID = pp.getKey();
//				C_BPartner_ID = new MInvoice(Env.getCtx(),C_Invoice_ID,trxName).getC_BPartner_ID();	//消込伝票にInvoiceの取引先を設定したい場合。
				C_BPartner_ID = Payment_BP_ID;
				BigDecimal AppliedAmt = (BigDecimal)invoice.getValueAt(i, i_applied); 	//適用金額
				//  semi-fixed fields (reset after first invoice)
				BigDecimal DiscountAmt = (BigDecimal)invoice.getValueAt(i, i_discount);	//割引金額
				BigDecimal WriteOffAmt = (BigDecimal)invoice.getValueAt(i, i_writeOff); //貸倒金額
				//	OverUnderAmt needs to be in Allocation Currency
				BigDecimal OverUnderAmt = ((BigDecimal)invoice.getValueAt(i, i_open))	//過不足金額
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
				availablePaymentAmt = availablePaymentAmt.subtract(AppliedAmt);	//未消込金額
				if (log.isLoggable(Level.FINE)) log.fine("Allocation Amount=" + AppliedAmt + " - Payment=" + availablePaymentAmt);

			}   //  invoice selected

		}// for (int i = 0; i < iRows; i++)


		// check for unapplied payment amounts (eg from payment reversals)
		if (log.isLoggable(Level.FINE)) log.fine("Payment=" + C_Payment_ID + ", Amount=" + Payment_PayAmt);

			// check for charge amount
		if ( Payment_Charge_ID > 0 && Payment_PayAmt.compareTo(Env.ZERO) != 0 )
		{
			int test = availablePaymentAmt.compareTo(totalDiff);

			BigDecimal chargeAmt = totalDiff;

			//	Allocation Line
			MAllocationLine aLine = new MAllocationLine (alloc, chargeAmt.negate(),Env.ZERO, Env.ZERO, Env.ZERO);
			aLine.setC_Charge_ID(Payment_Charge_ID);
			aLine.setC_BPartner_ID(Invoice_BP_ID);
			if (!aLine.save(trxName)) {
				StringBuilder msg = new StringBuilder("Allocation Line not saved - Charge=").append(Payment_Charge_ID);
				throw new AdempiereException(msg.toString());
			}
		}

		if ( availablePaymentAmt.signum() != 0 )
			log.log(Level.SEVERE, "Allocation not balanced -- out by " + availablePaymentAmt );

		//	Should start WF
		if (alloc.get_ID() != 0)
		{
			if (!alloc.processIt(DocAction.ACTION_Complete))
				throw new AdempiereException("Cannot complete allocation: " + alloc.getProcessMsg());
			alloc.saveEx();
		}

		//売上請求伝票の消込 - requires that allocation is posted
		for (int i = 0; i < iRows; i++)
		{
			//  Invoice line is selected
			if (((Boolean)invoice.getValueAt(i, 0)).booleanValue())
			{
				KeyNamePair pp = (KeyNamePair)invoice.getValueAt(i, 2);    //  Value
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
		}


		return alloc;
	}   //  saveData
}
