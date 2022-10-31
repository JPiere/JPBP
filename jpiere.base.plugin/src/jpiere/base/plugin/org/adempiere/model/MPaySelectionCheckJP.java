package jpiere.base.plugin.org.adempiere.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.model.MPaySelectionLine;


/**
*
* JPIERE-0581: Deduct Bank Transfer Fee
*
*
* @author h.hagiwara
*
*/
public class MPaySelectionCheckJP extends X_T_PaySelectionCheckJP {

	public MPaySelectionCheckJP(Properties ctx, int T_PaySelectionCheckJP_ID, String trxName) 
	{
		super(ctx, T_PaySelectionCheckJP_ID, trxName);
	}

	public MPaySelectionCheckJP(Properties ctx, int T_PaySelectionCheckJP_ID, String trxName, String... virtualColumns) 
	{
		super(ctx, T_PaySelectionCheckJP_ID, trxName, virtualColumns);
	}

	public MPaySelectionCheckJP(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}
	
	public MPaySelectionCheckJP (MPaySelectionLine line)
	{
		this (line.getCtx(), 0, line.get_TrxName());
		setClientOrg(line);
		setC_PaySelection_ID (line.getC_PaySelection_ID());
		int C_BPartner_ID = line.getInvoice().getC_BPartner_ID();
		setC_BPartner_ID (C_BPartner_ID);
		setIsReceipt(line.isSOTrx());
		setPayAmt (line.getPayAmt());
		setDiscountAmt(line.getDiscountAmt());
		setWriteOffAmt(line.getWriteOffAmt());
		setQty (1);
	}	//	MPaySelectionCheckJP

	/**
	 * 	Add Payment Selection Line
	 *	@param line line
	 */
	public void addLine (MPaySelectionLine line)
	{
		if (getC_BPartner_ID() != line.getInvoice().getC_BPartner_ID())
			throw new IllegalArgumentException("Line for different BPartner");
		//
		if (isReceipt() == line.isSOTrx())
		{
			setPayAmt (getPayAmt().add(line.getPayAmt()));
			setDiscountAmt(getDiscountAmt().add(line.getDiscountAmt()));
			setWriteOffAmt(getWriteOffAmt().add(line.getWriteOffAmt()));
		}
		else
		{
			setPayAmt (getPayAmt().subtract(line.getPayAmt()));
			setDiscountAmt(getDiscountAmt().subtract(line.getDiscountAmt()));
			setWriteOffAmt(getWriteOffAmt().subtract(line.getWriteOffAmt()));
		}
		setQty (getQty()+1);
	}	//	addLine
}
