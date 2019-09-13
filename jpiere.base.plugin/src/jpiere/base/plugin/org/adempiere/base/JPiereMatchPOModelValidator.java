package jpiere.base.plugin.org.adempiere.base;

import java.util.logging.Level;

import org.compiere.model.MClient;
import org.compiere.model.MDocType;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MMatchPO;
import org.compiere.model.MOrderLine;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Msg;

/**
*
* JPiere Match PO Model Validator
*
* JPIERE-0225: Match PO control.
*
* @author h.hagiwara
*
*/
public class JPiereMatchPOModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereMatchPOModelValidator.class);
	private int AD_Client_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MMatchPO.Table_Name, this);

		if (log.isLoggable(Level.FINE)) log.fine("Initialize JPiereMatchPOModelValidator");
	}

	@Override
	public int getAD_Client_ID() {
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

		//JPIERE-0225:Match PO control
		if(type == ModelValidator.TYPE_BEFORE_NEW ||
				(type == ModelValidator.TYPE_BEFORE_CHANGE && (po.is_ValueChanged("M_InOutLine_ID") || po.is_ValueChanged("C_OrderLine_ID") || po.is_ValueChanged("C_InvoiceLine_ID") ) ) )
		{
			MMatchPO matchPO = (MMatchPO)po;

			if(matchPO.getM_InOutLine_ID() > 0 && matchPO.getC_OrderLine_ID() > 0)
			{
				MInOutLine iol = new MInOutLine(matchPO.getCtx(),matchPO.getM_InOutLine_ID(), matchPO.get_TrxName());
				MOrderLine ol = new MOrderLine(matchPO.getCtx(),matchPO.getC_OrderLine_ID(), matchPO.get_TrxName());

				if(ol.getParent().getC_DocTypeTarget().getDocBaseType().equals(MDocType.DOCBASETYPE_PurchaseOrder))//POO
				{
					if(!iol.getParent().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialReceipt))//MMR
					{
						return Msg.getMsg(ol.getCtx(), "JP_Can_Not_Match_Because_DocType") +
								Msg.getMsg(ol.getCtx(), "JP_API_MATCH_MMR_ONLY");//API of Doc Base Type can match MMR of Doc Base type only.
					}
				}else if(ol.getParent().getC_DocTypeTarget().getDocBaseType().equals(MDocType.DOCBASETYPE_SalesOrder)){//SOO

					if(!iol.getParent().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialDelivery))//MMS
					{
						return Msg.getMsg(ol.getCtx(), "JP_Can_Not_Match_Because_DocType") +
								Msg.getMsg(ol.getCtx(), "JP_APC_MATCH_MMS_ONLY");//API of Doc Base Type can match MMR of Doc Base type only.
					}
				}
			}

			if(matchPO.getC_InvoiceLine_ID() > 0 && matchPO.getC_OrderLine_ID() > 0)
			{
				MInvoiceLine ivl = new MInvoiceLine(matchPO.getCtx(),matchPO.getC_InvoiceLine_ID(), matchPO.get_TrxName());
				MOrderLine ol = new MOrderLine(matchPO.getCtx(),matchPO.getC_OrderLine_ID(), matchPO.get_TrxName());
				if(ol.getParent().getC_DocTypeTarget().getDocBaseType().equals(MDocType.DOCBASETYPE_PurchaseOrder))//POO
				{
					if(!ivl.getParent().getC_DocTypeTarget().getDocBaseType().equals(MDocType.DOCBASETYPE_APInvoice))//API
					{
						return Msg.getMsg(ol.getCtx(), "JP_Can_Not_Match_Because_DocType") +
								Msg.getMsg(ol.getCtx(), "JP_API_MATCH_MMR_ONLY");//API of Doc Base Type can match MMR of Doc Base type only.
					}
				}else if(ol.getParent().getC_DocTypeTarget().getDocBaseType().equals(MDocType.DOCBASETYPE_SalesOrder)){//SOO

					if(!ivl.getParent().getC_DocTypeTarget().getDocBaseType().equals(MDocType.DOCBASETYPE_APCreditMemo))//APC
					{
						return Msg.getMsg(ol.getCtx(), "JP_Can_Not_Match_Because_DocType") +
								Msg.getMsg(ol.getCtx(), "JP_APC_MATCH_MMS_ONLY");//API of Doc Base Type can match MMR of Doc Base type only.
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
