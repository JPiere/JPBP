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
package jpiere.base.plugin.org.adempiere.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import jpiere.base.plugin.org.adempiere.model.MInvValAdjust;
import jpiere.base.plugin.org.adempiere.model.MInvValAdjustLine;
import jpiere.base.plugin.org.adempiere.model.MInvValProfile;
import jpiere.base.plugin.org.adempiere.model.MInventoryDiffQtyLog;
import jpiere.base.plugin.util.JPiereInvValUtil;

import org.compiere.model.MCost;
import org.compiere.model.MCostElement;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;

/**
 * JPIERE-0163 Inventory Valuation Adjust Doc
 *
 *  Default Inventory Valuation Adjust
 *
 *  @author Hideaki Hagiwara
 *
 */
public class DefaultInvValAdjustCalculate extends SvrProcess {

	MInvValProfile m_InvValProfile = null;
	MInvValAdjust m_InvValAdjust = null;
	MInvValAdjustLine[] lines = null;
	int Record_ID = 0;

	@Override
	protected void prepare()
	{
		Record_ID = getRecord_ID();
		if(Record_ID > 0)
		{
			m_InvValAdjust = new MInvValAdjust(getCtx(), Record_ID, null);
			m_InvValProfile = MInvValProfile.get(getCtx(), m_InvValAdjust.getJP_InvValProfile_ID());
			lines = m_InvValAdjust.getLines();

		}else{
			log.log(Level.SEVERE, "Record_ID <= 0 ");
		}
	}

	@Override
	protected String doIt() throws Exception
	{
		int AD_Org_ID = 0;
		int M_CostType_ID = m_InvValProfile.getC_AcctSchema().getM_CostType_ID();
		int C_AcctSchema_ID =  m_InvValProfile.getC_AcctSchema_ID();
		MCostElement[] costElements =JPiereInvValUtil.getMaterialStandardCostElements(getCtx());
		int M_CostElement_ID = costElements[0].get_ID();
		MCost m_Cost = null;
		BigDecimal currentCostPrice = Env.ZERO;
		BigDecimal QtyOnHand = Env.ZERO;
		BigDecimal DifferenceQty = Env.ZERO;
		
		for(int i = 0; i < lines.length; i++)
		{
			
			StringBuilder sqlDelete = new StringBuilder ("DELETE JP_InventoryDiffQtyLog ")
			.append(" WHERE JP_InvValAdjustLine_ID=").append(lines[i].getJP_InvValAdjustLine_ID());
			DB.executeUpdateEx(sqlDelete.toString(), get_TrxName());
			
			if(lines[i].getCostingLevel().equals(MInvValAdjustLine.COSTINGLEVEL_Client))
				AD_Org_ID = 0;
			else if(lines[i].getCostingLevel().equals(MInvValAdjustLine.COSTINGLEVEL_BatchLot))
				AD_Org_ID = 0;
			else if(lines[i].getCostingLevel().equals(MInvValAdjustLine.COSTINGLEVEL_Organization))
				AD_Org_ID = lines[i].getAD_OrgTrx_ID();
			else 
				AD_Org_ID = 0;
			
			m_Cost = MCost.get(getCtx(), lines[i].getAD_Client_ID(), AD_Org_ID, lines[i].getM_Product_ID()
					, M_CostType_ID,C_AcctSchema_ID, M_CostElement_ID, lines[i].getM_AttributeSetInstance_ID(), get_TrxName());
			
			if(m_Cost == null)
				continue;
			
			currentCostPrice = m_Cost.getCurrentCostPrice();
			lines[i].setJP_InvValAmt(currentCostPrice);
			lines[i].setJP_InvValTotalAmt(lines[i].getQtyBook().multiply(currentCostPrice));
			lines[i].setDifferenceAmt(lines[i].getJP_InvValTotalAmt().subtract(lines[i].getAmtAcctBalance()));
			QtyOnHand = JPiereInvValUtil.getQtyBookFromStockOrg(getCtx(), m_InvValAdjust.getDateValue(), lines[i].getM_Product_ID(), lines[i].getAD_OrgTrx_ID());
			lines[i].setQtyOnHand(QtyOnHand);
			DifferenceQty = QtyOnHand.subtract(lines[i].getQtyBook());
			lines[i].setDifferenceQty(DifferenceQty);
			if(DifferenceQty.compareTo(Env.ZERO)==0)
			{
				lines[i].setIsConfirmed(true);
				lines[i].saveEx(get_TrxName());
			}else{
				lines[i].saveEx(get_TrxName());
				
				//Analyize Diffrence Qty
				StringBuilder DateValue = new StringBuilder(lines[i].getParent().getDateValue().toString());
				StringBuilder DateValue_24 = new StringBuilder("TO_DATE('").append(DateValue.substring(0,10)).append(" 24:00:00','YYYY-MM-DD HH24:MI:SS')");
				
				StringBuilder sqlBase = new StringBuilder("SELECT AD_Org_ID, M_Product_ID, M_InOutLine_ID, M_Transaction_ID")
										.append(" ,MovementDate, DateAcct, MovementType, MovementQty")
										.append(" FROM JP_InOutTransaction")
										.append(" WHERE AD_Org_ID= " + lines[i].getAD_OrgTrx_ID() + " AND M_Product_ID =" + lines[i].getM_Product_ID())
										.append(" AND DocStatus IN ('CO', 'CL')");
				
				String sqlFutureDateAcct = sqlBase.toString() + " AND MovementDate < " + DateValue_24 + " AND DateAcct >= " + DateValue_24;
				
				int lineNo = createMInventoryDiffQtyLog(sqlFutureDateAcct, true, lines[i], 0);
				
				String sqlFutureMovementDate = sqlBase.toString() + " AND DateAcct < " + DateValue_24 + " AND MovementDate >= " + DateValue_24;	
				
				createMInventoryDiffQtyLog(sqlFutureMovementDate, false, lines[i], lineNo);
				
				MInventoryDiffQtyLog[] diffLogs = lines[i].getDiffQtyLogs(true,"");
				BigDecimal JP_AdjustToAcctQty = Env.ZERO;
				for(int j = 0; j < diffLogs.length; j++)
				{
					JP_AdjustToAcctQty = JP_AdjustToAcctQty.add(diffLogs[j].getJP_AdjustToAcctQty());
				}
				
				if(Env.ZERO.equals(JP_AdjustToAcctQty.add(lines[i].getDifferenceQty())))
				{
					lines[i].setIsConfirmed(true);
					lines[i].saveEx(get_TrxName());
				}
			}
			
			
			currentCostPrice = Env.ZERO;
			QtyOnHand = Env.ZERO;
			DifferenceQty = Env.ZERO;
			
		}//For
		
		return "";
	}

	private int createMInventoryDiffQtyLog(String sql, boolean IsFutureDateAcct, MInvValAdjustLine ivaLine, int lineNo)
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, get_TrxName());
			rs = pstmt.executeQuery ();
			while (rs.next ())
			{
				lineNo = lineNo + 10;
				MInventoryDiffQtyLog diffLog = new MInventoryDiffQtyLog(getCtx(), 0, get_TrxName());
				diffLog.setJP_InvValAdjustLine_ID(ivaLine.getJP_InvValAdjustLine_ID());
				diffLog.setLine(lineNo);
				diffLog.setAD_Org_ID(ivaLine.getAD_Org_ID());
				diffLog.setAD_OrgTrx_ID(rs.getInt(1));
				diffLog.setM_Product_ID(rs.getInt(2));
				diffLog.setM_InOutLine_ID(rs.getInt(3));
				diffLog.setM_Transaction_ID(rs.getInt(4));
				diffLog.setMovementDate(rs.getTimestamp(5));
				diffLog.setDateAcct(rs.getTimestamp(6));
				diffLog.setMovementType(rs.getString(7));
				diffLog.setMovementQty(rs.getBigDecimal(8));
				if(IsFutureDateAcct)
					diffLog.setJP_AdjustToAcctQty(rs.getBigDecimal(8).negate());
				else
					diffLog.setJP_AdjustToAcctQty(rs.getBigDecimal(8));
				diffLog.saveEx(get_TrxName());
			}
		}
		catch (Exception e)
		{
//			s_log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		
		return lineNo;
	}
}
