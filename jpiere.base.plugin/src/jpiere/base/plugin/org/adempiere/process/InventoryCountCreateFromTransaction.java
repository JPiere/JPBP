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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;

import org.compiere.model.MInventory;
import org.compiere.model.MInventoryLine;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.AdempiereSystemError;
import org.compiere.util.DB;


/**
 * JPIERE-0288 : Inventory Count create from Transaction
 * @author BIT
 *
 */
public class InventoryCountCreateFromTransaction extends SvrProcess {


	private int				p_M_Locator_ID = 0;
	private String				p_LocatorValue = null;
	private String				p_ProductValue = null;
	private int				p_M_Product_Category_ID = 0;
	private String				p_QtyRange = null;
	private boolean			p_InventoryCountSetZero = false;
	private boolean			p_DeleteOld = false;

	private MInventoryLine	m_line = null;

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("M_Locator_ID")){
				p_M_Locator_ID = para[i].getParameterAsInt();
			}else if (name.equals("LocatorValue")){
				p_LocatorValue = para[i].getParameterAsString();
			}else if (name.equals("ProductValue")){
				p_ProductValue = para[i].getParameterAsString();
			}else if (name.equals("M_Product_Category_ID")){
				p_M_Product_Category_ID = para[i].getParameterAsInt();
			}else if (name.equals("QtyRange")){
				p_QtyRange = para[i].getParameterAsString();
			}else if (name.equals("InventoryCountSet")){
				p_InventoryCountSetZero = "Z".equals(para[i].getParameter());
			}else if (name.equals("DeleteOld")){
				p_DeleteOld = "Y".equals(para[i].getParameter());
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if

		}//for
	}

	@Override
	protected String doIt() throws Exception {

		int inventoryID = getRecord_ID();

		MInventory inventory = new MInventory(getCtx(), inventoryID, get_TrxName());
		if (inventory.get_ID() == 0){
			throw new AdempiereSystemError ("Not found: M_Inventory_ID=" + inventoryID);
		}
		if (inventory.isProcessed()){
			throw new AdempiereSystemError ("@M_Inventory_ID@ @Processed@");
		}

		if(p_DeleteOld){
			deleteOldRecord(inventoryID);
		}

		String sql = createSelectStatement();
		int count = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			setSelectStatementParameter(pstmt, inventory);
			rs = pstmt.executeQuery();

			while (rs.next ())
			{
				int M_Product_ID = rs.getInt(1);
				int M_Locator_ID = rs.getInt(2);
				BigDecimal qty = rs.getBigDecimal(3);
				//int M_AttributeSetInstance_ID = rs.getInt(6);
				int M_AttributeSetInstance_ID = 0;
				count += createInventoryLine(M_Locator_ID, M_Product_ID,
						M_AttributeSetInstance_ID, qty, inventory);

			}
		}
		catch (Exception e){
			log.log(Level.SEVERE, sql.toString(), e);
		}
		finally{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		StringBuilder msgreturn = new StringBuilder("@M_InventoryLine_ID@ - #").append(count);
		return msgreturn.toString();
	}

	/**
	 * Delete Old Record
	 * @param inventoryID current record ID
	 */
	private void deleteOldRecord(int inventoryID){
		StringBuilder sql = new StringBuilder("DELETE FROM M_InventoryLineMA ma WHERE EXISTS ")
			.append("(SELECT * FROM M_InventoryLine l WHERE l.M_InventoryLine_ID=ma.M_InventoryLine_ID")
			.append(" AND Processed='N' AND M_Inventory_ID=").append(inventoryID).append(")");
		int no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)){
			log.fine("doIt - Deleted MA #" + no);
		}

		sql = new StringBuilder("DELETE M_InventoryLine WHERE Processed='N' ")
			.append("AND M_Inventory_ID=").append(inventoryID);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)){
			log.fine("doIt - Deleted #" + no);
		}

	}

	/**
	 * Create select statement
	 * @return select statement
	 * @throws Exception
	 */
	private String createSelectStatement() throws Exception {
		StringBuilder sql = new StringBuilder("SELECT t.M_Product_ID, t.M_Locator_ID, ");

		if(p_InventoryCountSetZero){
			sql.append("0 Qty, ");
		}else{
			sql.append("SUM(t.MovementQty) Qty, ");
		}
		sql.append("l.Value, p.Value ");
		//sql.append(",t.M_AttributeSetInstance_ID ");

		sql.append("FROM M_Product p ");
		sql.append("INNER JOIN M_Transaction t ON (p.m_product_id = t.m_product_id) ");
		sql.append("INNER JOIN M_Locator l ON (t.M_Locator_ID=l.M_Locator_ID) ");
		sql.append("WHERE l.M_Warehouse_ID=? ");
		sql.append(" AND p.IsActive='Y' AND p.IsStocked='Y' AND p.ProductType='I' ");
		sql.append(" AND t.MovementDate <= TO_DATE(? ,'YYYY-MM-DD HH24:MI:SS') ");

		if (p_M_Locator_ID != 0){
			sql.append(" AND t.M_Locator_ID=? ");
		}

		if (p_LocatorValue != null &&
		(p_LocatorValue.trim().length() == 0 || p_LocatorValue.equals("%"))){
			p_LocatorValue = null;
		}else if (p_LocatorValue != null){
			sql.append(" AND UPPER(l.Value) LIKE ? ");
		}

		if (p_ProductValue != null &&
		(p_ProductValue.trim().length() == 0 || p_ProductValue.equals("%"))){
			p_ProductValue = null;
		}else if (p_ProductValue != null){
			sql.append(" AND UPPER(p.Value) LIKE ? ");
		}

		if (p_M_Product_Category_ID != 0){
			sql.append(" AND p.M_Product_Category_ID IN (")
			.append(getSubCategoryWhereClause(p_M_Product_Category_ID))
			.append(") ");
		}

		//	Do not overwrite existing records
		if (!p_DeleteOld){
			sql.append(" AND NOT EXISTS (SELECT * FROM M_InventoryLine il ")
			.append("WHERE il.M_Inventory_ID=? ")
			.append(" AND il.M_Product_ID=t.M_Product_ID ")
			.append(" AND il.M_Locator_ID=t.M_Locator_ID ")
			.append(" AND COALESCE(il.M_AttributeSetInstance_ID,0)=COALESCE(t.M_AttributeSetInstance_ID,0)) ");
		}

		sql.append("GROUP BY l.Value, p.Value, t.M_Product_ID, t.M_Locator_ID ");

		if (p_QtyRange != null &&
				((p_QtyRange.equals(">"))
						|| (p_QtyRange.equals("<"))
						|| (p_QtyRange.equals("="))
						|| (p_QtyRange.equals("N")))){
			String operator = "";
			if(!p_QtyRange.equals("N")){
				operator = p_QtyRange;
			}else{
				operator = "<>";
			}
			sql.append("HAVING SUM(t.MovementQty) ").append(operator).append(" 0 ");
		}
		//sql.append(", t.M_AttributeSetInstance_ID ");
		sql.append("ORDER BY l.Value, p.Value");	//	Locator/Product
		//sql.append(", t.M_AttributeSetInstance_ID ");

		return sql.toString();
	}

	/**
	 * Set select statement parameter
	 * @param pstmt PreparedStatement
	 * @param inventory MInventory
	 * @throws Exception
	 */
	private void setSelectStatementParameter(PreparedStatement pstmt, MInventory inventory)
			throws Exception{
		int index = 1;
		pstmt.setInt(index++, inventory.getM_Warehouse_ID());
		StringBuilder movementDateStr = new StringBuilder(inventory.getMovementDate().toString().substring(0, 10));
		pstmt.setString(index++, movementDateStr.append(" 23:59:59'").toString());

		if (p_M_Locator_ID != 0){
			pstmt.setInt(index++, p_M_Locator_ID);
		}
		if (p_LocatorValue != null){
			pstmt.setString(index++, p_LocatorValue.toUpperCase());
		}
		if (p_ProductValue != null) {
			pstmt.setString(index++, p_ProductValue.toUpperCase());
		}
		if (!p_DeleteOld){
			pstmt.setInt(index++, inventory.get_ID());
		}
	}

	/**
	 * 	Create/Add to Inventory Line
	 *  † Partial copy from org.compiere.process.InventoryCountCreate#createInventoryLine
	 *	@param M_Product_ID product
	 *	@param M_Locator_ID locator
	 *	@param M_AttributeSetInstance_ID asi
	 *	@param qty qty
	 *	@param inventory MInventory
	 *	@return lines added
	 */
	private int createInventoryLine (int M_Locator_ID, int M_Product_ID,
		int M_AttributeSetInstance_ID, BigDecimal qty, MInventory inventory)
	{
		if (qty.signum() == 0){
			M_AttributeSetInstance_ID = 0;
		}
		if (m_line != null
			&& m_line.getM_Locator_ID() == M_Locator_ID
			&& m_line.getM_Product_ID() == M_Product_ID){
			if (qty.signum() == 0){
				return 0;
			}
			//	Same ASI and Date
			if (m_line.getM_AttributeSetInstance_ID() == M_AttributeSetInstance_ID){
				m_line.setQtyBook(m_line.getQtyBook().add(qty));
				m_line.setQtyCount(m_line.getQtyCount().add(qty));
				m_line.saveEx();
				return 0;
			}
			//	Save Old Line info
//			else if (m_line.getM_AttributeSetInstance_ID() != 0){
//				MInventoryLineMA ma = new MInventoryLineMA (m_line,
//					m_line.getM_AttributeSetInstance_ID(), m_line.getQtyBook(),null,true);
//				if (!ma.save()){
//					log.warning("Could not save " + ma);
//				}
//			}
			m_line.setM_AttributeSetInstance_ID(0);
			m_line.setQtyBook(m_line.getQtyBook().add(qty));
			m_line.setQtyCount(m_line.getQtyCount().add(qty));
			m_line.saveEx();

			//
//			MInventoryLineMA ma = new MInventoryLineMA (m_line,
//				M_AttributeSetInstance_ID, qty, null,true);
//			if (!ma.save()){
//				log.warning("Could not save " + ma);
//			}
			return 0;
		}
		//	new line
		m_line = new MInventoryLine (inventory, M_Locator_ID,
			M_Product_ID, 0,
			qty, qty);		//	book/count

		if (m_line.save()){
			return 1;
		}
		return 0;
	}	//	createInventoryLine

	// Copy From org.compiere.process.InventoryCountCreate ---Start
	/**
	 * Returns a sql where string with the given category id and all of its subcategory ids.
	 * It is used as restriction in MQuery.
	 * @param productCategoryId
	 * @return
	 */
	private String getSubCategoryWhereClause(int productCategoryId) throws SQLException, AdempiereSystemError{
		//if a node with this id is found later in the search we have a loop in the tree
		int subTreeRootParentId = 0;
		StringBuilder retString = new StringBuilder();
		String sql = " SELECT M_Product_Category_ID, M_Product_Category_Parent_ID FROM M_Product_Category";
		final Vector<SimpleTreeNode> categories = new Vector<SimpleTreeNode>(100);
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			stmt = DB.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				if(rs.getInt(1)==productCategoryId) {
					subTreeRootParentId = rs.getInt(2);
				}
				categories.add(new SimpleTreeNode(rs.getInt(1), rs.getInt(2)));
			}
			retString.append(getSubCategoriesString(productCategoryId, categories, subTreeRootParentId));
		} catch (SQLException e)
		{
			throw e;
		}
		finally
		{
			DB.close(rs, stmt);
			rs = null;stmt = null;
		}
		return retString.toString();
	}

	/**
	 * Recursive search for subcategories with loop detection.
	 * @param productCategoryId
	 * @param categories
	 * @param loopIndicatorId
	 * @return comma seperated list of category ids
	 * @throws AdempiereSystemError if a loop is detected
	 */
	private String getSubCategoriesString(int productCategoryId, Vector<SimpleTreeNode> categories, int loopIndicatorId) throws AdempiereSystemError {
		StringBuilder ret = new StringBuilder();
		final Iterator<SimpleTreeNode> iter = categories.iterator();
		while (iter.hasNext()) {
			SimpleTreeNode node = iter.next();
			if (node.getParentId() == productCategoryId) {
				if (node.getNodeId() == loopIndicatorId) {
					throw new AdempiereSystemError("The product category tree contains a loop on categoryId: " + loopIndicatorId);
				}
				ret.append(getSubCategoriesString(node.getNodeId(), categories, loopIndicatorId));
				ret.append(",");
			}
		}
		if (log.isLoggable(Level.FINE)) log.fine(ret.toString());
		StringBuilder msgreturn = new StringBuilder(ret).append(productCategoryId);
		return msgreturn.toString();
	}

	/**
	 * Simple tree node class for product category tree search.
	 * @author Karsten Thiemann, kthiemann@adempiere.org
	 *
	 */
	private static class SimpleTreeNode {

		private int nodeId;

		private int parentId;

		public SimpleTreeNode(int nodeId, int parentId) {
			this.nodeId = nodeId;
			this.parentId = parentId;
		}

		public int getNodeId() {
			return nodeId;
		}

		public int getParentId() {
			return parentId;
		}
	}
	// Copy From org.compiere.process.InventoryCountCreate ---End

}
