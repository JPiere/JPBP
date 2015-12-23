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

package jpiere.base.plugin.factory;

import static org.compiere.model.SystemIDs.*;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.logging.Level;

import org.adempiere.base.IDisplayTypeFactory;
import org.compiere.util.CLogger;
import org.compiere.util.Language;

public class JPiereBasePluginDisplayTypeFactory implements IDisplayTypeFactory {

	/** Display Type 10	String	*/
	public static final int String     = REFERENCE_DATATYPE_STRING;
	/** Display Type 11	Integer	*/
	public static final int Integer    = REFERENCE_DATATYPE_INTEGER;
	/** Display Type 12	Amount	*/
	public static final int Amount     = REFERENCE_DATATYPE_AMOUNT;
	/** Display Type 13	ID	*/
	public static final int ID         = REFERENCE_DATATYPE_ID;
	/** Display Type 14	Text	*/
	public static final int Text       = REFERENCE_DATATYPE_TEXT;
	/** Display Type 15	Date	*/
	public static final int Date       = REFERENCE_DATATYPE_DATE;
	/** Display Type 16	DateTime	*/
	public static final int DateTime   = REFERENCE_DATATYPE_DATETIME;
	/** Display Type 17	List	*/
	public static final int List       = REFERENCE_DATATYPE_LIST;
	/** Display Type 18	Table	*/
	public static final int Table      = REFERENCE_DATATYPE_TABLE;
	/** Display Type 19	TableDir	*/
	public static final int TableDir   = REFERENCE_DATATYPE_TABLEDIR;
	/** Display Type 20	YN	*/
	public static final int YesNo      = REFERENCE_DATATYPE_YES_NO;
	/** Display Type 21	Location	*/
	public static final int Location   = REFERENCE_DATATYPE_LOCATION;
	/** Display Type 22	Number	*/
	public static final int Number     = REFERENCE_DATATYPE_NUMBER;
	/** Display Type 23	BLOB	*/
	public static final int Binary     = REFERENCE_DATATYPE_BINARY;
	/** Display Type 24	Time	*/
	public static final int Time       = REFERENCE_DATATYPE_TIME;
	/** Display Type 25	Account	*/
	public static final int Account    = REFERENCE_DATATYPE_ACCOUNT;
	/** Display Type 26	RowID	*/
	public static final int RowID      = REFERENCE_DATATYPE_ROWID;
	/** Display Type 27	Color   */
	public static final int Color      = REFERENCE_DATATYPE_COLOR;
	/** Display Type 28	Button	*/
	public static final int Button	   = REFERENCE_DATATYPE_BUTTON;
	/** Display Type 29	Quantity	*/
	public static final int Quantity   = REFERENCE_DATATYPE_QUANTITY;
	/** Display Type 30	Search	*/
	public static final int Search     = REFERENCE_DATATYPE_SEARCH;
	/** Display Type 31	Locator	*/
	public static final int Locator    = REFERENCE_DATATYPE_LOCATOR;
	/** Display Type 32 Image	*/
	public static final int Image      = REFERENCE_DATATYPE_IMAGE;
	/** Display Type 33 Assignment	*/
	public static final int Assignment = REFERENCE_DATATYPE_ASSIGNMENT;
	/** Display Type 34	Memo	*/
	public static final int Memo       = REFERENCE_DATATYPE_MEMO;
	/** Display Type 35	PAttribute	*/
	public static final int PAttribute = REFERENCE_DATATYPE_PRODUCTATTRIBUTE;
	/** Display Type 36	CLOB	*/
	public static final int TextLong   = REFERENCE_DATATYPE_TEXTLONG;
	/** Display Type 37	CostPrice	*/
	public static final int CostPrice  = REFERENCE_DATATYPE_COSTPRICE;
	/** Display Type 38	File Path	*/
	public static final int FilePath  = REFERENCE_DATATYPE_FILEPATH;
	/** Display Type 39 File Name	*/
	public static final int FileName  = REFERENCE_DATATYPE_FILENAME;
	/** Display Type 40	URL	*/
	public static final int URL  = REFERENCE_DATATYPE_URL;
	/** Display Type 42	PrinterName	*/
	public static final int PrinterName  = REFERENCE_DATATYPE_PRINTNAME;
	//	Candidates:
	/** Display Type 200012	Payment	*/
	public static final int Payment  = REFERENCE_DATATYPE_PAYMENT;

	public static final int Chart = REFERENCE_DATATYPE_CHART;

	/**
	 *	- New Display Type
		INSERT INTO AD_REFERENCE
		(AD_REFERENCE_ID, AD_CLIENT_ID,AD_ORG_ID,ISACTIVE,CREATED,CREATEDBY,UPDATED,UPDATEDBY,
		NAME,DESCRIPTION,HELP, VALIDATIONTYPE,VFORMAT,ENTITYTYPE)
		VALUES (35, 0,0,'Y',SysDate,0,SysDate,0,
		'PAttribute','Product Attribute',null,'D',null,'D');
	 *
	 *  - org.compiere.model.MModel (??)
	 *	- org.compiere.grid.ed.VEditor/Dialog
	 *	- org.compiere.grid.ed.VEditorFactory
	 *	- RColumn, WWindow
	 *  add/check 0_cleanupAD.sql
	 */

	//  See DBA_DisplayType.sql ----------------------------------------------

	/** Maximum number of digits    */
	private static final int    MAX_DIGITS = 28;        //  Oracle Standard Limitation 38 digits
	/** Digits of an Integer        */
	private static final int    INTEGER_DIGITS = 10;
	/** Maximum number of fractions */
	private static final int    MAX_FRACTION = 12;
	/** Default Amount Precision    */
	private static final int    AMOUNT_FRACTION = 2;

	public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
	public static final String DEFAULT_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

	/**	Logger	*/
	private static CLogger s_log = CLogger.getCLogger (JPiereBasePluginDisplayTypeFactory.class);

	@Override
	public boolean isID(int displayType) {
		return false;
	}

	@Override
	public boolean isNumeric(int displayType) {
		return false;
	}

	@Override
	public Integer getDefaultPrecision(int displayType) {
		return null;
	}

	@Override
	public boolean isText(int displayType) {
		return false;
	}

	@Override
	public boolean isDate(int displayType) {
		return false;
	}

	@Override
	public boolean isLookup(int displayType) {
		return false;
	}

	@Override
	public boolean isLOB(int displayType) {
		return false;
	}

	@Override
	public DecimalFormat getNumberFormat(int displayType, Language language, String pattern) {

		Language myLanguage = language;
		if (myLanguage == null)
			myLanguage = Language.getLoginLanguage();
		Locale locale = myLanguage.getLocale();
		DecimalFormat format = null;
		if (locale != null)
			format = (DecimalFormat)NumberFormat.getNumberInstance(locale);
		else
			format = (DecimalFormat)NumberFormat.getNumberInstance(Locale.US);
		//
		if (pattern != null && pattern.length() > 0)
		{
			try {
				format.applyPattern(pattern);
				return format;
			}
			catch (IllegalArgumentException e) {
				s_log.log(Level.WARNING, "Invalid number format: " + pattern);
			}
		}
		else if (displayType == Integer)
		{
			format.setParseIntegerOnly(true);
			format.setMaximumIntegerDigits(INTEGER_DIGITS);
			format.setMaximumFractionDigits(0);
		}
		else if (displayType == Quantity)
		{
			format.setMaximumIntegerDigits(MAX_DIGITS);
			format.setMaximumFractionDigits(MAX_FRACTION);
		}
		else if (displayType == Amount)
		{
			format.setMaximumIntegerDigits(MAX_DIGITS);
			format.setMaximumFractionDigits(MAX_FRACTION);
//			format.setMinimumFractionDigits(MCurrency.getStdPrecision(Env.getCtx(), getC_Currency_ID()));

		}
		else if (displayType == CostPrice)
		{
			format.setMaximumIntegerDigits(MAX_DIGITS);
			format.setMaximumFractionDigits(MAX_FRACTION);
//			format.setMinimumFractionDigits(MCurrency.getStdPrecision(Env.getCtx(), getC_Currency_ID()));
//			format.setMinimumFractionDigits(MCurrency.getCostingPrecision(Env.getCtx(), C_Currency_ID));

		}
		else //if (displayType == Number)
		{
//			List<IDisplayTypeFactory> factoryList = Service.locator().list(IDisplayTypeFactory.class).getServices();
//			for(IDisplayTypeFactory factory : factoryList){
//				DecimalFormat osgiFormat = factory.getNumberFormat(displayType, myLanguage, pattern);
//				if(osgiFormat!=null){
//					return osgiFormat;
//				}
//			}

			format.setMaximumIntegerDigits(MAX_DIGITS);
			format.setMaximumFractionDigits(MAX_FRACTION);
			format.setMinimumFractionDigits(1);
		}
		return format;
	}//getDecimalFormat

	@Override
	public SimpleDateFormat getDateFormat(int displayType, Language language, String pattern) {
		return null;
	}

	@Override
	public Class<?> getClass(int displayType, boolean yesNoAsBoolean) {
		return null;
	}

	@Override
	public String getSQLDataType(int displayType, String columnName, int fieldLength) {
		return null;
	}

	@Override
	public String getDescription(int displayType) {
		return null;
	}

}
