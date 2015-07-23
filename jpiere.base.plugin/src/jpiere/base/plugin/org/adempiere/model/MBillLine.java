/******************************************************************************
 * Product: JPiere(ジェイピエール) - JPiere Base Plugin                       *
 * Copyright (C) Hideaki Hagiwara All Rights Reserved.                        *
 * このプログラムはGNU Gneral Public Licens Version2のもと公開しています。    *
 * このプログラムは自由に活用してもらう事を期待して公開していますが、         *
 * いかなる保証もしていません。                                               *
 * 著作権は萩原秀明(h.hagiwara@oss-erp.co.jp)が保持し、サポートサービスは     *
 * 株式会社オープンソース・イーアールピー・ソリューションズで                 *
 * 提供しています。サポートをご希望の際には、                                 *
 * 株式会社オープンソース・イーアールピー・ソリューションズまでご連絡下さい。 *
 * http://www.oss-erp.co.jp/                                                  *
 *****************************************************************************/

package jpiere.base.plugin.org.adempiere.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.model.MDocType;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceTax;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

public class MBillLine extends X_JP_BillLine {

	public MBillLine(Properties ctx, int JP_BillLine_ID, String trxName) {
		super(ctx, JP_BillLine_ID, trxName);
	}

	public MBillLine(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {

		if(newRecord || is_ValueChanged("C_Invoice_ID"))
		{
			if(getC_Invoice_ID()== 0)
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "JPiere"));//TODO:多言語化
				return false;
			}

			MInvoice inv = MInvoice.get(getCtx(), getC_Invoice_ID());
			if(getDescription()==null || getDescription().isEmpty())
				setDescription(inv.getDescription());

			setC_DocType_ID(inv.getC_DocType_ID());
			setDateInvoiced(inv.getDateInvoiced());
			setDateAcct(inv.getDateAcct());
			setC_BPartner_ID(inv.getC_BPartner_ID());
			setC_BPartner_Location_ID(inv.getC_BPartner_Location_ID());
			setAD_User_ID(inv.getAD_User_ID());
			setM_PriceList_ID(inv.getM_PriceList_ID());
			setSalesRep_ID(inv.getSalesRep_ID());
			setPaymentRule(inv.getPaymentRule());
			setC_PaymentTerm_ID(inv.getC_PaymentTerm_ID());
			setC_Currency_ID(inv.getC_Currency_ID());

			setTotalLines(inv.getTotalLines());
			setGrandTotal(inv.getGrandTotal());

			BigDecimal TaxBaseAmt = Env.ZERO;
			BigDecimal TaxAmt = Env.ZERO;
			MInvoiceTax[] invTaxes = inv.getTaxes(false);
			for(int i = 0; i < invTaxes.length; i++)
			{
				TaxBaseAmt = TaxBaseAmt.add(invTaxes[i].getTaxBaseAmt());
				TaxAmt = TaxAmt.add(invTaxes[i].getTaxAmt());
			}
			setTaxBaseAmt(TaxBaseAmt);
			setTaxAmt(TaxAmt);
			setPayAmt(inv.getGrandTotal().subtract(inv.getOpenAmt()));
			setOverUnderAmt(inv.getOpenAmt());

			if(inv.getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_ARCreditMemo))
			{
				setTotalLines(getTotalLines().negate());
				setGrandTotal(getGrandTotal().negate());
				setTaxBaseAmt(getTaxBaseAmt().negate());
				setTaxAmt(getTaxAmt().negate());
				setPayAmt(inv.getGrandTotal().add(inv.getOpenAmt()));
//				setOverUnderAmt(getOverUnderAmt().negate());
			}


		}

		return true;
	}

	@Override
	protected boolean afterSave(boolean newRecord, boolean success) {

//		String sql = "UPDATE JP_Bill b"
//				+ " SET TotalLines = (SELECT COALESCE(SUM(TotalLines),0) FROM JP_BillLine bl WHERE b.JP_Bill_ID=bl.JP_Bill_ID) "
//					+ ",GrandTotal = (SELECT COALESCE(SUM(GrandTotal),0) FROM JP_BillLine bl WHERE b.JP_Bill_ID=bl.JP_Bill_ID) "
//					+ ",TaxBaseAmt = (SELECT COALESCE(SUM(TaxBaseAmt),0) FROM JP_BillLine bl WHERE b.JP_Bill_ID=bl.JP_Bill_ID) "
//					+ ",TaxAmt = (SELECT COALESCE(SUM(TaxAmt),0) FROM JP_BillLine bl WHERE b.JP_Bill_ID=bl.JP_Bill_ID) "
//					+ ",PayAmt     = (SELECT COALESCE(SUM(PayAmt),0) FROM JP_BillLine bl WHERE b.JP_Bill_ID=bl.JP_Bill_ID) "
//					+ ",OverUnderAmt     = (SELECT COALESCE(SUM(OverUnderAmt),0) FROM JP_BillLine bl WHERE b.JP_Bill_ID=bl.JP_Bill_ID) "
//				+ "WHERE JP_Bill_ID=" + getJP_Bill_ID();

		String sql = "UPDATE JP_Bill b"
				+ " SET (TotalLines"
					+ " ,GrandTotal"
					+ " ,TaxBaseAmt"
					+ " ,TaxAmt"
					+ " ,PayAmt"
					+ " ,OverUnderAmt )"
				+ " = (SELECT COALESCE(SUM(TotalLines),0)"
						+ "  ,COALESCE(SUM(GrandTotal),0)"
						+ "  ,COALESCE(SUM(TaxBaseAmt),0)"
						+ "  ,COALESCE(SUM(TaxAmt),0)"
						+ "  ,COALESCE(SUM(PayAmt),0)"
						+ "  ,COALESCE(SUM(OverUnderAmt),0)"
				+ " FROM JP_BillLine bl WHERE b.JP_Bill_ID=bl.JP_Bill_ID) "
				+ " WHERE JP_Bill_ID=" + getJP_Bill_ID() ;

		int no = DB.executeUpdate(sql, get_TrxName());
		if (no != 1)
		{
			log.warning("(1) #" + no);
			return false;
		}

		sql = "UPDATE JP_Bill b"
				+" SET JPBillAmt =(SELECT COALESCE(OverUnderAmt,0) + COALESCE(JPCarriedForwardAmt,0) FROM JP_Bill WHERE JP_Bill_ID="+ getJP_Bill_ID() +" )"
				+ " WHERE JP_Bill_ID=" + getJP_Bill_ID() ;
		no = DB.executeUpdate(sql, get_TrxName());
		if (no != 1)
		{
			log.warning("(1) #" + no);
			return false;
		}

		return true;
	}

	@Override
	protected boolean afterDelete(boolean success) {

		String sql = "UPDATE JP_Bill b"
				+ " SET (TotalLines"
					+ " ,GrandTotal"
					+ " ,TaxBaseAmt"
					+ " ,TaxAmt"
					+ " ,PayAmt"
					+ " ,OverUnderAmt )"
				+ " = (SELECT COALESCE(SUM(TotalLines),0)"
						+ "  ,COALESCE(SUM(GrandTotal),0)"
						+ "  ,COALESCE(SUM(TaxBaseAmt),0)"
						+ "  ,COALESCE(SUM(TaxAmt),0)"
						+ "  ,COALESCE(SUM(PayAmt),0)"
						+ "  ,COALESCE(SUM(OverUnderAmt),0)"
				+ " FROM JP_BillLine bl WHERE b.JP_Bill_ID=bl.JP_Bill_ID) "
				+ " WHERE JP_Bill_ID=" + getJP_Bill_ID() ;

		int no = DB.executeUpdate(sql, get_TrxName());
		if (no != 1)
		{
			log.warning("(1) #" + no);
			return false;
		}

		sql = "UPDATE JP_Bill b"
				+" SET JPBillAmt =(SELECT COALESCE(OverUnderAmt,0) + COALESCE(JPCarriedForwardAmt,0) FROM JP_Bill WHERE JP_Bill_ID="+ getJP_Bill_ID() +" )";
		no = DB.executeUpdate(sql, get_TrxName());
		if (no != 1)
		{
			log.warning("(1) #" + no);
			return false;
		}

		return true;
	}



}
