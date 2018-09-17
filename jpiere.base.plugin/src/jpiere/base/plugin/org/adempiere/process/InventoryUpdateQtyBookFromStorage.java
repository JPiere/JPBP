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

import org.compiere.model.MInventory;
import org.compiere.model.MInventoryLine;
import org.compiere.process.DocAction;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 * JPIERE-0418 : Update Inventory Book Qty at Now
 * @author Hideaki.Hagiwara
 *
 */
public class InventoryUpdateQtyBookFromStorage extends SvrProcess {


	int record_ID = 0;

	MInventory m_Inventory = null;

	@Override
	protected void prepare()
	{
		record_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception
	{
		if(record_ID == 0)
			return Msg.getElement(getCtx(), "NoRecordID");//Record ID doesn't exist in the table

		m_Inventory = new MInventory(getCtx(),record_ID, get_TrxName());

		if(m_Inventory.getDocStatus().equals(DocAction.STATUS_Completed)
				|| m_Inventory.getDocStatus().equals(DocAction.STATUS_Closed)
				|| m_Inventory.getDocStatus().equals(DocAction.STATUS_Voided)
				|| m_Inventory.getDocStatus().equals(DocAction.STATUS_Reversed)
				)
		{
			return Msg.getMsg(getCtx(), "JP_NotValidDocStatus");//Not Valid Doc Status
		}


		MInventoryLine[] inventoryLines = m_Inventory.getLines(true);
		if(inventoryLines.length == 0)
			return Msg.getMsg(getCtx(), "NoLines");//No Document Lines found


		MInventoryLine line = null;
		BigDecimal qtyBook = Env.ZERO;
		for(int i = 0; i < inventoryLines.length; i++)
		{
			qtyBook = Env.ZERO;
			line = inventoryLines[i];

			if(line.getM_Product_ID() <= 0)
				continue;

			if(line.getM_AttributeSetInstance_ID() > 0)
			{
				qtyBook = getQtyBookWithASI(line);

			}else {
				qtyBook = getQtyBookWithoutASI(line);
			}

			line.setQtyBook(qtyBook);
			line.saveEx(get_TrxName());
		}

		return Msg.getMsg(getCtx(), "Success");
	}

	private BigDecimal getQtyBookWithASI(MInventoryLine line)
	{
		BigDecimal qtyBook = Env.ZERO;

		String sql = "SELECT SUM(t.QtyOnHand) FROM M_StorageOnHand t "
								+ " WHERE t.M_Product_ID = ? AND t.M_Locator_ID = ? AND t.M_AttributeSetInstance_ID = ? "
								+"  GROUP BY t.M_Product_ID, t.M_Locator_ID, t.M_AttributeSetInstance_ID";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, line.getM_Product_ID());
			pstmt.setInt(2, line.getM_Locator_ID());
			pstmt.setInt(3, line.getM_AttributeSetInstance_ID());

			rs = pstmt.executeQuery();
			if (rs.next())
			{
				qtyBook = rs.getBigDecimal(1);
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return qtyBook;
	}

	private BigDecimal getQtyBookWithoutASI(MInventoryLine line)
	{
		BigDecimal qtyBook = Env.ZERO;

		String sql = "SELECT SUM(t.QtyOnHand) FROM M_StorageOnHand t "
								+ " WHERE t.M_Product_ID = ? AND t.M_Locator_ID = ? "
								+"  GROUP BY t.M_Product_ID, t.M_Locator_ID ";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, line.getM_Product_ID());
			pstmt.setInt(2, line.getM_Locator_ID());

			rs = pstmt.executeQuery();
			if (rs.next())
			{
				qtyBook = rs.getBigDecimal(1);
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return qtyBook;
	}
}
