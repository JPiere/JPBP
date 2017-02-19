package jpiere.base.plugin.org.adempiere.process;

import java.util.Properties;

import org.compiere.model.MPaymentTerm;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;

import jpiere.base.plugin.org.adempiere.model.MBillSchema;

/**
 *
 *
 * @author Hiroshi Iwama
 *
 */

public interface I_CreateBill {

	public String createBills(Properties ctx, int AD_PInstance_ID ,SvrProcess process, ProcessInfoParameter[] para
			, MBillSchema billSchema, MPaymentTerm paymentTerm, boolean isSOTrx, boolean isCalledInfoWindow, String trxName) throws Exception;


}
