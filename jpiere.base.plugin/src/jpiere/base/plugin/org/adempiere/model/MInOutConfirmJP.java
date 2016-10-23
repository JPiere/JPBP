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

package jpiere.base.plugin.org.adempiere.model;

import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MInOut;
import org.compiere.model.MInOutConfirm;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.process.DocOptions;
import org.compiere.process.DocumentEngine;
import org.compiere.util.DB;
import org.compiere.util.Msg;

/**
 * JPIERE-0208
 * 
 * @author Hideaki Hagiwara
 *
 */
public class MInOutConfirmJP extends MInOutConfirm implements DocOptions {
	
	public MInOutConfirmJP(Properties ctx, int M_InOutConfirm_ID, String trxName) {
		super(ctx, M_InOutConfirm_ID, trxName);
	}
	
	public MInOutConfirmJP(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	public MInOutConfirmJP(MInOut ship, String confirmType) {
		super(ship, confirmType);
	}
	
	/**	Process Message 			*/
	private String		m_processMsg = null;
	
	@Override
	public boolean reActivateIt() {
		if (log.isLoggable(Level.INFO)) log.info(toString());
		// Before reActivate
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REACTIVATE);
		if (m_processMsg != null)
			return false;	
		
		if(getM_InOut().getDocStatus().equals(DocumentEngine.STATUS_Completed) || getM_InOut().getDocStatus().equals(DocumentEngine.STATUS_Closed)
				|| getM_InOut().getDocStatus().equals(DocumentEngine.STATUS_Voided) || getM_InOut().getDocStatus().equals(DocumentEngine.STATUS_Reversed))
		{
			//for erroe message
			setDescription(Msg.getMsg(getCtx(), "JP_CanNotReActivateInOutConfirm"));//You can not ReActivate. Because Shipment of Receipt was completed.
			return false;
		}
		// After reActivate
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REACTIVATE);
		if (m_processMsg != null)
			return false;
		
		setDocAction(DOCACTION_Complete);
		setProcessed(false);
		
		return true;
	}
	
	public void setProcessed (boolean processed)
	{
		super.setProcessed (processed);
		if (get_ID() == 0)
			return;
		String set = "SET Processed='"
			+ (processed ? "Y" : "N")
			+ "' WHERE M_InOutConfirm_ID=" + getM_InOutConfirm_ID();
		int noLine = DB.executeUpdateEx("UPDATE M_InOutLineConfirm " + set, get_TrxName());
		getLines(true);
		
		if (log.isLoggable(Level.FINE)) log.fine("setProcessed - " + processed + " - Lines=" + noLine);
	}	//	setProcessed

	@Override
	public int customizeValidActions(String docStatus, Object processing, String orderType, String isSOTrx,
			int AD_Table_ID, String[] docAction, String[] options, int index) {

		if(docStatus.equals(DocumentEngine.STATUS_Drafted))
		{
			index = 0; //initialize the index
			options[index++] = DocumentEngine.ACTION_Prepare; 
//			options[index++] = DocumentEngine.ACTION_Void; 
			options[index++] = DocumentEngine.ACTION_Complete; 
			return index;
			
		}else if(docStatus.equals(DocumentEngine.STATUS_InProgress)){
			index = 0; //initialize the index
//			options[index++] = DocumentEngine.ACTION_Void; 
			options[index++] = DocumentEngine.ACTION_Complete; 
			return index;
			
		}else if(docStatus.equals(DocumentEngine.STATUS_Completed)){
			index = 0; //initialize the index
//			options[index++] = DocumentEngine.ACTION_Void; 
			options[index++] = DocumentEngine.ACTION_Close; 
			options[index++] = DocumentEngine.ACTION_ReActivate; 
			return index;
		}
		
		return 0;
	}
	
}
