package jpiere.base.plugin.org.adempiere.base;

import java.util.logging.Level;

import org.compiere.model.MClient;
import org.compiere.model.MDocType;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MMatchInv;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Msg;

/**
*
* JPiere Match INvoice Model Validator
*
* JPIERE-0223: Match Inv control
*
* @author h.hagiwara
*
*/
public class JPiereMatchInvModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereMatchInvModelValidator.class);
	private int AD_Client_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MMatchInv.Table_Name, this);

		if (log.isLoggable(Level.FINE)) log.fine("Initialize JPiereMatchInvModelValidator");

	}

	@Override
	public int getAD_Client_ID()
	{
		return AD_Client_ID;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID)
	{
		return null;
	}

	@Override
	public String modelChange(PO po, int type) throws Exception
	{

		//JPIERE-0223:Match Inv control
		if(type == ModelValidator.TYPE_BEFORE_NEW ||
				(type == ModelValidator.TYPE_BEFORE_CHANGE && (po.is_ValueChanged("M_InOutLine_ID") || po.is_ValueChanged("C_InvoiceLine_ID") ) ) )
		{
			MMatchInv mInv = (MMatchInv)po;

			if(mInv.getM_InOutLine_ID() > 0 && mInv.getC_InvoiceLine_ID() > 0)
			{
				MInOutLine iol = new MInOutLine(mInv.getCtx(),mInv.getM_InOutLine_ID(), mInv.get_TrxName());
				MInvoiceLine invl = new MInvoiceLine(mInv.getCtx(),mInv.getC_InvoiceLine_ID(), mInv.get_TrxName());

				if(invl.getParent().getC_DocTypeTarget().getDocBaseType().equals(MDocType.DOCBASETYPE_APInvoice))//AP Invoice
				{
					if(!iol.getParent().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialReceipt))
					{
						return Msg.getMsg(mInv.getCtx(), "JP_Can_Not_Match_Because_DocType") +
								Msg.getMsg(mInv.getCtx(), "JP_API_MATCH_MMR_ONLY");//API of Doc Base Type can match MMR of Doc Base type only.
					}
				}else if(invl.getParent().getC_DocTypeTarget().getDocBaseType().equals(MDocType.DOCBASETYPE_APCreditMemo)){//AP credit Memo

					if(!iol.getParent().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialDelivery))
					{
						return Msg.getMsg(mInv.getCtx(), "JP_Can_Not_Match_Because_DocType") +
								Msg.getMsg(mInv.getCtx(), "JP_APC_MATCH_MMS_ONLY");//API of Doc Base Type can match MMR of Doc Base type only.
					}
				}
			}

		}

		return null;
	}

	@Override
	public String docValidate(PO po, int timing) {
		return null;
	}

}
