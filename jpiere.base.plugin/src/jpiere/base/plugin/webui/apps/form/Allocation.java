/******************************************************************************
 * Copyright (C) 2009 Low Heng Sin                                            *
 * Copyright (C) 2009 Idalica Corporation                                     *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/
package jpiere.base.plugin.webui.apps.form;


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
import org.compiere.minigrid.IMiniTable;
import org.compiere.model.MAllocationHdr;
import org.compiere.model.MAllocationLine;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoice;
import org.compiere.model.MPayment;
import org.compiere.model.MRole;
import org.compiere.model.MSysConfig;
import org.compiere.process.DocAction;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;
import org.compiere.util.TimeUtil;
import org.compiere.util.Util;

public class Allocation
{
	public DecimalFormat format = DisplayType.getNumberFormat(DisplayType.Amount);

	/**	Logger			*/
	public static final CLogger log = CLogger.getCLogger(Allocation.class);

	private boolean     m_calculating = false;
	public int         	m_C_Currency_ID = 0;
	public int         m_C_Charge_ID = 0;
	public int         m_C_DocType_ID = 0;
	public int         	m_C_BPartner_ID = 0;
	public int         	m_C_BPartner2_ID = 0;//JPIERE-0026
	private int         m_noInvoices = 0;
	private int         m_noPayments = 0;
	public BigDecimal	totalInv = Env.ZERO;
	public BigDecimal 	totalPay = Env.ZERO;
	public BigDecimal	totalDiff = Env.ZERO;
	
	public Timestamp allocDate = null;

	//  Index	changed if multi-currency
	private int         i_payment = 7;
	//
	private int         i_open = 6;
	private int         i_discount = 7;
	private int         i_writeOff = 8; 
	private int         i_applied = 9;
	private int 		i_overUnder = 10;
//	private int			i_multiplier = 10;
	private int		i_Payment_OrgInfoCorp = 0;			//JPIERE-0026
	private int		i_Invoice_OrgInfoCorp = 0;			//JPIERE-0026
	
	public int         	m_AD_Org_ID = 0;
	public int				m_AD_Org2_ID = 0;				//JPIERE-0026
	public int         	m_Doc_AD_Org_ID = 0;			//JPIERE-0026
	public int				m_JP_OrgInfo_Corporation_ID = 0;//JPIERE-0026
	public int				m_JP_Corporation_ID = 0;		//JPIERE-0026

	private ArrayList<Integer>	m_bpartnerCheck = new ArrayList<Integer>(); 

	public void dynInit() throws Exception
	{
		m_C_Currency_ID = Env.getContextAsInt(Env.getCtx(), Env.C_CURRENCY_ID);   //  default
		//
		if (log.isLoggable(Level.INFO)) log.info("Currency=" + m_C_Currency_ID);
		
		m_AD_Org_ID = Env.getAD_Org_ID(Env.getCtx());
		m_C_DocType_ID= MDocType.getDocType("CMA");
		
	}
	
	/**
	 *  Load Business Partner Info
	 *  - Payments
	 *  - Invoices
	 */
	public void checkBPartner()
	{		
		if (log.isLoggable(Level.CONFIG)) log.config("BPartner=" + m_C_BPartner_ID + ", Cur=" + m_C_Currency_ID);
		//  Need to have both values
		if (m_C_BPartner_ID == 0 || m_C_Currency_ID == 0)
			return;

		//	Async BPartner Test
		Integer key = Integer.valueOf(m_C_BPartner_ID);
		if (!m_bpartnerCheck.contains(key))
		{
			new Thread()
			{
				public void run()
				{
					MPayment.setIsAllocated (Env.getCtx(), m_C_BPartner_ID, null);
					MInvoice.setIsPaid (Env.getCtx(), m_C_BPartner_ID, null);
				}
			}.start();
			m_bpartnerCheck.add(key);
		}
	}
	
	public Vector<Vector<Object>> getPaymentData(boolean isMultiCurrency, Object date, IMiniTable paymentTable)
	{		
		/********************************
		 *  Load unallocated Payments
		 *      1-TrxDate, 2-DocumentNo, (3-Currency, 4-PayAmt,)
		 *      5-ConvAmt, 6-ConvOpen, 7-Allocated
		 */
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		
		if(m_C_BPartner_ID == 0 && m_C_BPartner2_ID == 0 && m_JP_Corporation_ID == 0)
			return data;
		
		StringBuilder sql = new StringBuilder("SELECT p.DateTrx,p.DocumentNo,p.C_Payment_ID,"  //  1..3
			+ "c.ISO_Code,p.PayAmt,"                            //  4..5
			+ "currencyConvertPayment(p.C_Payment_ID,?,null,?),"//  6   #1, #2
			+ "currencyConvertPayment(p.C_Payment_ID,?,paymentAvailable(p.C_Payment_ID),?),"  //  7   #3, #4
			+ "p.MultiplierAP, bp.Name, bc.Name, o.Name, cp.Name "	//JPIERE-0026
			+ "FROM C_Payment_v p"		//	Corrected for AP/AR
			+ " INNER JOIN C_Currency c ON (p.C_Currency_ID=c.C_Currency_ID) "
			+ " INNER JOIN C_BPartner bp ON (p.C_BPartner_ID=bp.C_BPartner_ID) "
			+ " INNER JOIN AD_Org o ON (p.AD_Org_ID=o.AD_Org_ID) "
				+ " LEFT OUTER JOIN JP_Corporation bc ON (bp.JP_Corporation_ID = bc.JP_Corporation_ID) "
			+ " LEFT OUTER JOIN AD_OrgInfo oi ON (p.AD_Org_ID=oi.AD_Org_ID) "
				+ " LEFT OUTER JOIN JP_Corporation cp ON (oi.JP_Corporation_ID=cp.JP_Corporation_ID) "
			+ "WHERE p.IsAllocated='N' AND p.Processed='Y'"
			+ " AND p.C_Charge_ID IS NULL");		//	Prepayments OK
		if(m_C_BPartner_ID != 0 || m_C_BPartner2_ID != 0)
			sql.append(" AND p.C_BPartner_ID in (?,?)");
		if (m_JP_Corporation_ID != 0 )
			sql.append(" AND bp.JP_Corporation_ID=?");
		if (!isMultiCurrency)
			sql.append(" AND p.C_Currency_ID=?");
		if (m_AD_Org_ID != 0 || m_AD_Org2_ID != 0)
			sql.append(" AND p.AD_Org_ID in (?,?) ");
		if (m_JP_OrgInfo_Corporation_ID != 0 )
			sql.append(" AND oi.JP_Corporation_ID=?");
		//JPIERE-0026
		
		sql.append(" ORDER BY p.DateTrx,p.DocumentNo");
		
		// role security
		sql = new StringBuilder( MRole.getDefault(Env.getCtx(), false).addAccessSQL( sql.toString(), "p", MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO ) );
		
		if (log.isLoggable(Level.FINE)) log.fine("PaySQL=" + sql.toString());
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			int i = 0;
			pstmt = DB.prepareStatement(sql.toString(), null);
			pstmt.setInt(++i, m_C_Currency_ID);
			pstmt.setTimestamp(++i, (Timestamp)date);
			pstmt.setInt(++i, m_C_Currency_ID);
			pstmt.setTimestamp(++i, (Timestamp)date);
			if(m_C_BPartner_ID != 0 || m_C_BPartner2_ID != 0)
			{
				pstmt.setInt(++i, m_C_BPartner_ID);
				pstmt.setInt(++i, m_C_BPartner2_ID);
			}
			if (m_JP_Corporation_ID != 0 )
				pstmt.setInt(++i, m_JP_Corporation_ID);
			if (!isMultiCurrency)
				pstmt.setInt(++i, m_C_Currency_ID);
			if (m_AD_Org_ID != 0 || m_AD_Org2_ID != 0)
			{
				pstmt.setInt(++i, m_AD_Org_ID);
				pstmt.setInt(++i, m_AD_Org2_ID);
			}
			if (m_JP_OrgInfo_Corporation_ID != 0 )
				pstmt.setInt(++i, m_JP_OrgInfo_Corporation_ID);
			
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				Vector<Object> line = new Vector<Object>();
				line.add(Boolean.FALSE);       //  0-Selection
				line.add(rs.getTimestamp(1));       //  1-TrxDate
				KeyNamePair pp = new KeyNamePair(rs.getInt(3), rs.getString(2));
				line.add(pp);                       //  2-DocumentNo
				if (isMultiCurrency)
				{
					line.add(rs.getString(4));      //  3-Currency
					line.add(rs.getBigDecimal(5));  //  4-PayAmt
				}
				line.add(rs.getBigDecimal(6));      //  3/5-ConvAmt
				BigDecimal available = rs.getBigDecimal(7);
				if (available == null || available.signum() == 0)	//	nothing available
					continue;
				line.add(available);				//  4/6-ConvOpen/Available
				line.add(Env.ZERO);					//  5/7-Payment
//				line.add(rs.getBigDecimal(8));		//  6/8-Multiplier
				line.add(rs.getString(9));			//  6/9-BPartner JPIERE-0026
				line.add(rs.getString(10));			//  7/10-Corporation JPIERE-0026
				line.add(rs.getString(11));			//  8/11-Org JPIERE-0026
				line.add(rs.getString(12));			//  9/12-OrgInfo Corporation JPIERE-0026
				//
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
	
	public Vector<String> getPaymentColumnNames(boolean isMultiCurrency)
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
		columnNames.add(Msg.getMsg(Env.getCtx(), "AppliedAmt"));
//		columnNames.add(" ");	//	Multiplier
		columnNames.add(Msg.getElement(Env.getCtx(), "C_BPartner_ID"));				//JPIERE-0026
		columnNames.add(Msg.getElement(Env.getCtx(), "JP_Corporation_ID"));			//JPIERE-0026
		columnNames.add(Msg.getElement(Env.getCtx(), "AD_Org_ID"));					//JPIERE-0026
		columnNames.add(Msg.getElement(Env.getCtx(), "JP_OrgInfo_Corporation_ID"));	//JPIERE-0026
		
		return columnNames;
	}
	
	public void setPaymentColumnClass(IMiniTable paymentTable, boolean isMultiCurrency)
	{
		int i = 0;
		paymentTable.setColumnClass(i++, Boolean.class, false);         //  0-Selection
		paymentTable.setColumnClass(i++, Timestamp.class, true);        //  1-TrxDate
		paymentTable.setColumnClass(i++, String.class, true);           //  2-Value
		if (isMultiCurrency)
		{
			paymentTable.setColumnClass(i++, String.class, true);       //  3-Currency
			paymentTable.setColumnClass(i++, BigDecimal.class, true);   //  4-PayAmt
		}
		paymentTable.setColumnClass(i++, BigDecimal.class, true);       //  5-ConvAmt
		paymentTable.setColumnClass(i++, BigDecimal.class, true);       //  6-ConvOpen
		paymentTable.setColumnClass(i++, BigDecimal.class, false);      //  7-Allocated
//		paymentTable.setColumnClass(i++, BigDecimal.class, true);      	//  8-Multiplier
		paymentTable.setColumnClass(i++, String.class, true);  	    //  8-BPartner Name JPIERE-0026
		paymentTable.setColumnClass(i++, String.class, true);  	    //  9-Corporation Name JPIERE-0026
		paymentTable.setColumnClass(i++, String.class, true);  	    // 10-Org Name JPIERE-0026
		paymentTable.setColumnClass(i++, String.class, true);  	    // 11-OrgInfo Corporation Name JPIERE-0026
		//
		i_payment = isMultiCurrency ? 7 : 5;
		i_Payment_OrgInfoCorp = isMultiCurrency ? 11 : 9;//JPIERE-0026

		//  Table UI
		paymentTable.autoSize();
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
		 invoiceDiscount(i.C_Invoice_ID,getDate(),C_InvoicePaySchedule_ID) "Discount",
		 currencyConvert(invoiceDiscount(i.C_Invoice_ID,getDate(),C_InvoicePaySchedule_ID),i.C_Currency_ID,i.C_Currency_ID,i.DateInvoiced,i.C_ConversionType_ID,i.AD_Client_ID,i.AD_Org_ID)*i.Multiplier*i.MultiplierAP "Discount $",
		 i.MultiplierAP, i.Multiplier 
		 FROM C_Invoice_v i INNER JOIN C_Currency c ON (i.C_Currency_ID=c.C_Currency_ID) 
		 WHERE -- i.IsPaid='N' AND i.Processed='Y' AND i.C_BPartner_ID=1000001
		 */
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		
		if(m_C_BPartner_ID == 0 && m_C_BPartner2_ID == 0 && m_JP_Corporation_ID == 0)
			return data;
		
		StringBuilder sql = new StringBuilder("SELECT i.DateInvoiced,i.DocumentNo,i.C_Invoice_ID," //  1..3
			+ "c.ISO_Code,i.GrandTotal*i.MultiplierAP, "                            //  4..5    Orig Currency
			+ "currencyConvertInvoice(i.C_Invoice_ID,?,i.GrandTotal*i.MultiplierAP,?), " //  6   #1  Converted, #2 Date
			+ "currencyConvertInvoice(i.C_Invoice_ID,?,invoiceOpen(C_Invoice_ID,C_InvoicePaySchedule_ID),?)*i.MultiplierAP, "  //  7   #3, #4  Converted Open
			+ "currencyConvertInvoice(i.C_Invoice_ID"                               //  8       AllowedDiscount
			+ ",?,invoiceDiscount(i.C_Invoice_ID,?,C_InvoicePaySchedule_ID),i.DateInvoiced)*i.Multiplier*i.MultiplierAP,"               //  #5, #6
			+ "i.MultiplierAP, bp.Name, bc.Name, o.Name, cp.Name "	//JPIERE-0026
			+ "FROM C_Invoice_v i"		//  corrected for CM/Split
			+ " INNER JOIN C_Currency c ON (i.C_Currency_ID=c.C_Currency_ID) "
			+ " INNER JOIN C_BPartner bp ON (i.C_BPartner_ID=bp.C_BPartner_ID) "	
				+ " LEFT OUTER JOIN JP_Corporation bc ON (bp.JP_Corporation_ID = bc.JP_Corporation_ID) "
			+ " INNER JOIN AD_Org o ON (i.AD_Org_ID=o.AD_Org_ID) "
			+ " LEFT OUTER JOIN AD_OrgInfo oi ON (i.AD_Org_ID=oi.AD_Org_ID) "
				+ " LEFT OUTER JOIN JP_Corporation cp ON (oi.JP_Corporation_ID=cp.JP_Corporation_ID) "
			+ "WHERE i.IsPaid='N' AND i.Processed='Y'");
		if(m_C_BPartner_ID != 0 || m_C_BPartner2_ID != 0)
			sql.append(" AND i.C_BPartner_ID in (?,?)");
		if (m_JP_Corporation_ID != 0 )
			sql.append(" AND bp.JP_Corporation_ID=?");
		if (!isMultiCurrency)
			sql.append(" AND i.C_Currency_ID=?");
		if (m_AD_Org_ID != 0 || m_AD_Org2_ID != 0)
			sql.append(" AND i.AD_Org_ID in (?,?)");
		if (m_JP_OrgInfo_Corporation_ID != 0 )
			sql.append(" AND oi.JP_Corporation_ID=?");
		//JPIERE-0026
		
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
			pstmt.setInt(++i, m_C_Currency_ID);
			pstmt.setTimestamp(++i, (Timestamp)date);
			pstmt.setInt(++i, m_C_Currency_ID);
			pstmt.setTimestamp(++i, (Timestamp)date);
			pstmt.setInt(++i, m_C_Currency_ID);
			pstmt.setTimestamp(++i, (Timestamp)date);
			if(m_C_BPartner_ID != 0 || m_C_BPartner2_ID != 0)
			{
				pstmt.setInt(++i, m_C_BPartner_ID);
				pstmt.setInt(++i, m_C_BPartner2_ID);//JPIERE-0026
			}
			if (m_JP_Corporation_ID != 0 )
				pstmt.setInt(++i, m_JP_Corporation_ID);
			if (!isMultiCurrency)
				pstmt.setInt(++i, m_C_Currency_ID);
			if (m_AD_Org_ID != 0 || m_AD_Org2_ID != 0)
			{
				pstmt.setInt(++i, m_AD_Org_ID);
				pstmt.setInt(++i, m_AD_Org2_ID);
			}
			if (m_JP_OrgInfo_Corporation_ID != 0 )
				pstmt.setInt(++i, m_JP_OrgInfo_Corporation_ID);
				
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				Vector<Object> line = new Vector<Object>();
				line.add(Boolean.FALSE);       //  0-Selection
				line.add(rs.getTimestamp(1));       //  1-TrxDate
				KeyNamePair pp = new KeyNamePair(rs.getInt(3), rs.getString(2));
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
				line.add(rs.getString(10));			//  9/10 BPartner Name JPIERE-0026
				line.add(rs.getString(11));			//  10/11 Corporation Name JPIERE-0026
				line.add(rs.getString(12));			//  11/12 Org Name JPIERE-0026
				line.add(rs.getString(13));			//  12/13 Org Corporation JPIERE-0026

				//	Add when open <> 0 (i.e. not if no conversion rate)
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
		columnNames.add(Msg.getElement(Env.getCtx(), "C_BPartner_ID"));				//JPIERE-0026
		columnNames.add(Msg.getElement(Env.getCtx(), "JP_Corporation_ID"));			//JPIERE-0026
		columnNames.add(Msg.getElement(Env.getCtx(), "AD_Org_ID"));					//JPIERE-0026
		columnNames.add(Msg.getElement(Env.getCtx(), "JP_OrgInfo_Corporation_ID"));	//JPIERE-0026
		
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
		invoiceTable.setColumnClass(i++, String.class, true);			//	11-BPartner Name JPIERE-0026
		invoiceTable.setColumnClass(i++, String.class, true);			//	12-Corporation Name JPIERE-0026
		invoiceTable.setColumnClass(i++, String.class, true);			//	13-Org Name JPIERE-0026
		invoiceTable.setColumnClass(i++, String.class, true);			//	14-Org Info Corporation Name JPIERE-0026
		//  Table UI
		invoiceTable.autoSize();
		
		i_Invoice_OrgInfoCorp = isMultiCurrency ? 14 : 12;//JPIERE-0026
	}
	
	public void calculate(boolean isMultiCurrency)
	{
		i_open = isMultiCurrency ? 6 : 4;
		i_discount = isMultiCurrency ? 7 : 5;
		i_writeOff = isMultiCurrency ? 8 : 6;
		i_applied = isMultiCurrency ? 9 : 7;
		i_overUnder = isMultiCurrency ? 10 : 8;
//		i_multiplier = isMultiCurrency ? 10 : 8;
	}   //  loadBPartner
	
	public String writeOff(int row, int col, boolean isInvoice, IMiniTable payment, IMiniTable invoice, boolean isAutoWriteOff)
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
        
		//  Payments
		if (!isInvoice)
		{
			BigDecimal open = (BigDecimal)payment.getValueAt(row, i_open);
			BigDecimal applied = (BigDecimal)payment.getValueAt(row, i_payment);
			
			if (col == 0)
			{
				// selection of payment row
				if (((Boolean)payment.getValueAt(row, 0)).booleanValue())
				{
					applied = open;   //  Open Amount
					if (totalDiff.abs().compareTo(applied.abs()) < 0			// where less is available to allocate than open
							&& totalDiff.signum() == -applied.signum() )    	// and the available amount has the opposite sign
						applied = totalDiff.negate();						// reduce the amount applied to what's available
					
				}
				else    //  de-selected
					applied = Env.ZERO;
			}
			
			
			if (col == i_payment)
			{
				if (! MSysConfig.getBooleanValue(MSysConfig.ALLOW_APPLY_PAYMENT_TO_CREDITMEMO, false, Env.getAD_Client_ID(Env.getCtx())) 
						&& open.signum() > 0 && applied.signum() == -open.signum() )
					applied = applied.negate();
				if (! MSysConfig.getBooleanValue(MSysConfig.ALLOW_OVER_APPLIED_PAYMENT, false, Env.getAD_Client_ID(Env.getCtx())))
					if ( open.abs().compareTo( applied.abs() ) < 0 )
						applied = open;
			}
			
			payment.setValueAt(applied, row, i_payment);
		}

		//  Invoice
		else 
		{
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
		}

		m_calculating = false;
		
		return msg;
	}
	
	/**
	 *  Calculate Allocation info
	 */
	public String calculatePayment(IMiniTable payment, boolean isMultiCurrency)
	{
		log.config("");

		//  Payment
		totalPay = Env.ZERO;
		int rows = payment.getRowCount();
		m_noPayments = 0;
		for (int i = 0; i < rows; i++)
		{
			if (((Boolean)payment.getValueAt(i, 0)).booleanValue())
			{
				Timestamp ts = (Timestamp)payment.getValueAt(i, 1);
				if ( !isMultiCurrency )  // the converted amounts are only valid for the selected date
					allocDate = TimeUtil.max(allocDate, ts);
				BigDecimal bd = (BigDecimal)payment.getValueAt(i, i_payment);
				totalPay = totalPay.add(bd);  //  Applied Pay
				m_noPayments++;
				if (log.isLoggable(Level.FINE)) log.fine("Payment_" + i + " = " + bd + " - Total=" + totalPay);
			}
		}
		return String.valueOf(m_noPayments) + " - "
			+ Msg.getMsg(Env.getCtx(), "Sum") + "  " + format.format(totalPay) + " ";
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
	
	/**************************************************************************
	 *  Save Data
	 */
	public MAllocationHdr saveData(int m_WindowNo, Object date, IMiniTable payment, IMiniTable invoice, String trxName)
	{
		if (m_noInvoices + m_noPayments == 0)
			return null;

		//  fixed fields
		int AD_Client_ID = Env.getContextAsInt(Env.getCtx(), m_WindowNo, "AD_Client_ID");
		int AD_Org_ID = m_Doc_AD_Org_ID;//JPIERE-0026
		String orgInfoCorporation = null;//JPIERE-0026
		int i_CheckOrgInfoCorporationDiff = 0;//JPIERE-0026
		boolean isPaymentSelected = false;//JPIERE-0026
		int C_BPartner_ID = m_C_BPartner_ID;
		int C_Order_ID = 0;
		int C_CashLine_ID = 0;
		Timestamp DateTrx = (Timestamp)date;
		if(DateTrx == null)
			DateTrx = Env.getContextAsDate(Env.getCtx(),"#Date");
		int C_Currency_ID = m_C_Currency_ID;	//	the allocation currency
		//
		if (AD_Org_ID == 0)
		{
			//ADialog.error(m_WindowNo, this, "Org0NotAllowed", null);
			String msg = Msg.getElement(Env.getCtx(),"AD_OrgDoc_ID") + " : " + Msg.getMsg(Env.getCtx(),"Org0NotAllowed");
			throw new AdempiereException(msg);
		}
		//
		if (log.isLoggable(Level.CONFIG)) log.config("Client=" + AD_Client_ID + ", Org=" + AD_Org_ID
			+ ", BPartner=" + C_BPartner_ID + ", Date=" + DateTrx);

		//  Payment - Loop and add them to paymentList/amountList
		int pRows = payment.getRowCount();
		ArrayList<Integer> paymentList = new ArrayList<Integer>(pRows);
		ArrayList<BigDecimal> amountList = new ArrayList<BigDecimal>(pRows);
		BigDecimal paymentAppliedAmt = Env.ZERO;
		for (int i = 0; i < pRows; i++)
		{
			//  Payment line is selected
			if (((Boolean)payment.getValueAt(i, 0)).booleanValue())
			{
				KeyNamePair pp = (KeyNamePair)payment.getValueAt(i, 2);   //  Value
				//  Payment variables
				int C_Payment_ID = pp.getKey();
				paymentList.add(Integer.valueOf(C_Payment_ID));
				//
				BigDecimal PaymentAmt = (BigDecimal)payment.getValueAt(i, i_payment);  //  Applied Payment
				amountList.add(PaymentAmt);
				//
				paymentAppliedAmt = paymentAppliedAmt.add(PaymentAmt);
				//
				if (log.isLoggable(Level.FINE)) log.fine("C_Payment_ID=" + C_Payment_ID 
					+ " - PaymentAmt=" + PaymentAmt); // + " * " + Multiplier + " = " + PaymentAmtAbs);
						
				//JPIERE-0026 - Check Corporation
				String corporation = (String)payment.getValueAt(i, i_Payment_OrgInfoCorp);
				if(i_CheckOrgInfoCorporationDiff == 0)
				{
					orgInfoCorporation = corporation;
				}else {
					
					if(Util.isEmpty(orgInfoCorporation) != Util.isEmpty(corporation) ) 
					{
						//Allocation between different Group Corporation is forbidden.
						String msg =Msg.getMsg(Env.getCtx(),"JP_Allocation_DiffCorp");
						throw new AdempiereException(msg);
						
					}else if(!Util.isEmpty(orgInfoCorporation) && !orgInfoCorporation.equals(corporation)){
						
						//Allocation between different Group Corporation is forbidden.
						String msg =Msg.getMsg(Env.getCtx(),"JP_Allocation_DiffCorp");
						throw new AdempiereException(msg);
					}
				}
				i_CheckOrgInfoCorporationDiff++;
				isPaymentSelected = true;
				//JPIERE - 0026		
			}
			

		}
		if (log.isLoggable(Level.CONFIG)) log.config("Number of Payments=" + paymentList.size() + " - Total=" + paymentAppliedAmt);

		//  Invoices - Loop and generate allocations
		int iRows = invoice.getRowCount();
		i_CheckOrgInfoCorporationDiff = 0;//JPIERE-0026 - Check Corporation
		
		//	Create Allocation
		MAllocationHdr alloc = new MAllocationHdr (Env.getCtx(), true,	//	manual
			DateTrx, C_Currency_ID, Env.getContext(Env.getCtx(), Env.AD_USER_NAME), trxName);
		alloc.setAD_Org_ID(AD_Org_ID);
		alloc.setC_DocType_ID(m_C_DocType_ID);
		alloc.setDescription(alloc.getDescriptionForManualAllocation(m_C_BPartner_ID, trxName));
		alloc.saveEx();
		//	For all invoices
		BigDecimal unmatchedApplied = Env.ZERO;
		for (int i = 0; i < iRows; i++)
		{
			//  Invoice line is selected
			if (((Boolean)invoice.getValueAt(i, 0)).booleanValue())
			{
				KeyNamePair pp = (KeyNamePair)invoice.getValueAt(i, 2);    //  Value
				//  Invoice variables
				int C_Invoice_ID = pp.getKey();
				BigDecimal AppliedAmt = (BigDecimal)invoice.getValueAt(i, i_applied);
				//  semi-fixed fields (reset after first invoice)
				BigDecimal DiscountAmt = (BigDecimal)invoice.getValueAt(i, i_discount);
				BigDecimal WriteOffAmt = (BigDecimal)invoice.getValueAt(i, i_writeOff);
				//	OverUnderAmt needs to be in Allocation Currency
				BigDecimal OverUnderAmt = ((BigDecimal)invoice.getValueAt(i, i_open))
					.subtract(AppliedAmt).subtract(DiscountAmt).subtract(WriteOffAmt);
				
				if (log.isLoggable(Level.CONFIG)) log.config("Invoice #" + i + " - AppliedAmt=" + AppliedAmt);// + " -> " + AppliedAbs);
				//  loop through all payments until invoice applied
				
				for (int j = 0; j < paymentList.size() && AppliedAmt.signum() != 0; j++)
				{
					int C_Payment_ID = ((Integer)paymentList.get(j)).intValue();
					BigDecimal PaymentAmt = (BigDecimal)amountList.get(j);
					if (PaymentAmt.signum() == AppliedAmt.signum())	// only match same sign (otherwise appliedAmt increases)
					{												// and not zero (appliedAmt was checked earlier)
						if (log.isLoggable(Level.CONFIG)) log.config(".. with payment #" + j + ", Amt=" + PaymentAmt);
						
						BigDecimal amount = AppliedAmt;
						if (amount.abs().compareTo(PaymentAmt.abs()) > 0)  // if there's more open on the invoice
							amount = PaymentAmt;							// than left in the payment
						
						//	Allocation Line
						MAllocationLine aLine = new MAllocationLine (alloc, amount, 
							DiscountAmt, WriteOffAmt, OverUnderAmt);
						aLine.setDocInfo(getC_BPartner_ID(C_Invoice_ID, C_Payment_ID), C_Order_ID, C_Invoice_ID);//JPIERE-0026
						aLine.setPaymentInfo(C_Payment_ID, C_CashLine_ID);
						aLine.saveEx();

						//  Apply Discounts and WriteOff only first time
						DiscountAmt = Env.ZERO;
						WriteOffAmt = Env.ZERO;
						//  subtract amount from Payment/Invoice
						AppliedAmt = AppliedAmt.subtract(amount);
						PaymentAmt = PaymentAmt.subtract(amount);
						if (log.isLoggable(Level.FINE)) log.fine("Allocation Amount=" + amount + " - Remaining  Applied=" + AppliedAmt + ", Payment=" + PaymentAmt);
						amountList.set(j, PaymentAmt);  //  update
					}	//	for all applied amounts
				}	//	loop through payments for invoice
			
				//JPIERE-0026 - Check Corporation
				String corporation = (String)invoice.getValueAt(i, i_Invoice_OrgInfoCorp);
				if(i_CheckOrgInfoCorporationDiff == 0 && !isPaymentSelected)
				{
					orgInfoCorporation = corporation;
				}else {
					
					if(Util.isEmpty(orgInfoCorporation) != Util.isEmpty(corporation) )
					{	
						//Allocation between different Group Corporation is forbidden.
						String msg =Msg.getMsg(Env.getCtx(),"JP_Allocation_DiffCorp");
						throw new AdempiereException(msg);
						
					}else if(!Util.isEmpty(orgInfoCorporation) && !orgInfoCorporation.equals(corporation)){
						
						//Allocation between different Group Corporation is forbidden.
						String msg =Msg.getMsg(Env.getCtx(),"JP_Allocation_DiffCorp");
						throw new AdempiereException(msg);
					}
				}
				i_CheckOrgInfoCorporationDiff++;
				//JPIERE - 0026
				
				if ( AppliedAmt.signum() == 0 && DiscountAmt.signum() == 0 && WriteOffAmt.signum() == 0)
					continue;
				else {			// remainder will need to match against other invoices
					int C_Payment_ID = 0;
					
					//	Allocation Line
					MAllocationLine aLine = new MAllocationLine (alloc, AppliedAmt, 
						DiscountAmt, WriteOffAmt, OverUnderAmt);
					aLine.setDocInfo(getC_BPartner_ID(C_Invoice_ID, C_Payment_ID), C_Order_ID, C_Invoice_ID);//JPIERE-0026
					aLine.setPaymentInfo(C_Payment_ID, C_CashLine_ID);
					aLine.saveEx();
					if (log.isLoggable(Level.FINE)) log.fine("Allocation Amount=" + AppliedAmt);
					unmatchedApplied = unmatchedApplied.add(AppliedAmt);
				}
			}   //  invoice selected
		}   //  invoice loop

		// check for unapplied payment amounts (eg from payment reversals)
		for (int i = 0; i < paymentList.size(); i++)	{
			BigDecimal payAmt = (BigDecimal) amountList.get(i);
			if ( payAmt.signum() == 0 )
					continue;
			int C_Payment_ID = ((Integer)paymentList.get(i)).intValue();
			if (log.isLoggable(Level.FINE)) log.fine("Payment=" + C_Payment_ID  
					+ ", Amount=" + payAmt);

			//	Allocation Line
			MAllocationLine aLine = new MAllocationLine (alloc, payAmt, 
				Env.ZERO, Env.ZERO, Env.ZERO);
			aLine.setDocInfo(getC_BPartner_ID(0, C_Payment_ID), 0, 0);//JPIERE-0026
			aLine.setPaymentInfo(C_Payment_ID, 0);
			aLine.saveEx();
			unmatchedApplied = unmatchedApplied.subtract(payAmt);
		}		
		
		// check for charge amount
		if ( m_C_Charge_ID > 0 && unmatchedApplied.compareTo(Env.ZERO) != 0 )
		{
			BigDecimal chargeAmt = totalDiff;
	
		//	Allocation Line
			MAllocationLine aLine = new MAllocationLine (alloc, chargeAmt.negate(), 
				Env.ZERO, Env.ZERO, Env.ZERO);
			aLine.setC_Charge_ID(m_C_Charge_ID);
			aLine.setC_BPartner_ID(m_C_BPartner_ID);
			if (!aLine.save(trxName)) {
				StringBuilder msg = new StringBuilder("Allocation Line not saved - Charge=").append(m_C_Charge_ID);
				throw new AdempiereException(msg.toString());
			}
			unmatchedApplied = unmatchedApplied.add(chargeAmt);
		}	
		
		if ( unmatchedApplied.signum() != 0 )
			throw new AdempiereException("Allocation not balanced -- out by " + unmatchedApplied);

		//	Should start WF
		if (alloc.get_ID() != 0)
		{
			if (!alloc.processIt(DocAction.ACTION_Complete))
				throw new AdempiereException("Cannot complete allocation: " + alloc.getProcessMsg());
			alloc.saveEx();
		}
		
		//  Test/Set IsPaid for Invoice - requires that allocation is posted
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
		//  Test/Set Payment is fully allocated
		for (int i = 0; i < paymentList.size(); i++)
		{
			int C_Payment_ID = ((Integer)paymentList.get(i)).intValue();
			MPayment pay = new MPayment (Env.getCtx(), C_Payment_ID, trxName);
			if (pay.testAllocation())
				pay.saveEx();
			if (log.isLoggable(Level.CONFIG)) log.config("Payment #" + i + (pay.isAllocated() ? " not" : " is") 
					+ " fully allocated");
		}
		paymentList.clear();
		amountList.clear();
		
		return alloc;
	}   //  saveData
	
	private int getC_BPartner_ID(int C_Invoice_ID, int C_Payment_ID)
	{
		int C_BPartner_ID = 0;
		
		if(C_Payment_ID != 0) { //Payment first
			C_BPartner_ID = new MPayment(Env.getCtx(),C_Payment_ID, null).getC_BPartner_ID();	
		}else if(C_Invoice_ID != 0){
			C_BPartner_ID = new MInvoice(Env.getCtx(),C_Invoice_ID, null).getC_BPartner_ID();	
		}else {
		
			C_BPartner_ID = (m_C_BPartner_ID == 0 ? m_C_BPartner2_ID :m_C_BPartner_ID);
		}
		
		return C_BPartner_ID;
	}
}

