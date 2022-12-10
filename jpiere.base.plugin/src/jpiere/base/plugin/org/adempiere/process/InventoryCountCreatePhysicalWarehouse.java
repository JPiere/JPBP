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
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;

import org.compiere.model.MInventory;
import org.compiere.model.MInventoryLine;
import org.compiere.model.MInventoryLineMA;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.AdempiereSystemError;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;


/**
 *	
 *  JPIERE-0587:JPBP
 *  
 *  Create Inventory Count List at Physical Warehouse with current Book value
 *	
 *  Ref: InventoryCountCreate
 *  
 *  @author Hideaki Hagiwara
 *  
 */
@org.adempiere.base.annotation.Process
public class InventoryCountCreatePhysicalWarehouse extends SvrProcess
{
	
	/** Physical Inventory Parameter		*/
	private int			p_M_Inventory_ID = 0;
	/** Physical Inventory					*/
	private MInventory 	m_inventory = null;
	/** Locator Parameter			*/
	private int			p_M_Locator_ID = 0;
	/** Locator Type Parameter			*/
	private int			p_M_LocatorType_ID = 0;//JPIERE
	/** Locator Parameter			*/
	private String		p_LocatorValue = null;
	/** Product Parameter			*/
	private String		p_ProductValue = null;
	/** Product Category Parameter	*/
	private int			p_M_Product_Category_ID = 0;
	/** Qty Range Parameter			*/
	private String		p_QtyRange = null;
	/** Update to What			*/
	private boolean	p_InventoryCountSetZero = false;
	/** Delete Parameter			*/
	private boolean		p_DeleteOld = false;
	
	/** Inventory Line				*/
	private MInventoryLine	m_line = null; 
	private Timestamp oldDateMPolicy = null;
	
	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("M_Locator_ID"))
				p_M_Locator_ID = para[i].getParameterAsInt();
			else if (name.equals("M_LocatorType_ID"))
				p_M_LocatorType_ID = para[i].getParameterAsInt();//JPIERE
			else if (name.equals("LocatorValue"))
				p_LocatorValue = (String)para[i].getParameter();
			else if (name.equals("ProductValue"))
				p_ProductValue = (String)para[i].getParameter();
			else if (name.equals("M_Product_Category_ID"))
				p_M_Product_Category_ID = para[i].getParameterAsInt();
			else if (name.equals("QtyRange"))
				p_QtyRange = (String)para[i].getParameter();
			else if (name.equals("InventoryCountSet"))
				p_InventoryCountSetZero = "Z".equals(para[i].getParameter());
			else if (name.equals("DeleteOld"))
				p_DeleteOld = "Y".equals(para[i].getParameter());
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		p_M_Inventory_ID = getRecord_ID();
	}	//	prepare

	
	/**
	 * 	Process
	 *	@return message
	 *	@throws Exception
	 */
	protected String doIt () throws Exception
	{
		if (log.isLoggable(Level.INFO)) log.info("M_Inventory_ID=" + p_M_Inventory_ID
			+ ", M_Locator_ID=" + p_M_Locator_ID + ", LocatorValue=" + p_LocatorValue
			+ ", ProductValue=" + p_ProductValue 
			+ ", M_Product_Category_ID=" + p_M_Product_Category_ID
			+ ", QtyRange=" + p_QtyRange + ", DeleteOld=" + p_DeleteOld);
		m_inventory = new MInventory (getCtx(), p_M_Inventory_ID, get_TrxName());
		if (m_inventory.get_ID() == 0)
			throw new AdempiereSystemError ("Not found: M_Inventory_ID=" + p_M_Inventory_ID);
		if (m_inventory.isProcessed())
			throw new AdempiereSystemError ("@M_Inventory_ID@ @Processed@");
		
		if(m_inventory.get_ValueAsInt("JP_PhysicalWarehouse_ID")==0)//JPIERE
			throw new AdempiereSystemError (Msg.getMsg(getCtx(), "FillMandatory")+Msg.getElement(getCtx(), "JP_PhysicalWarehouse_ID"));
		
		if (p_DeleteOld)
		{
			//Added Line by armen
			StringBuilder sql1 = new StringBuilder("DELETE FROM M_InventoryLineMA ma WHERE EXISTS ")
				.append("(SELECT * FROM M_InventoryLine l WHERE l.M_InventoryLine_ID=ma.M_InventoryLine_ID")
				.append(" AND Processed='N' AND M_Inventory_ID=").append(p_M_Inventory_ID).append(")");
			int no1 = DB.executeUpdate(sql1.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("doIt - Deleted MA #" + no1);
			//End of Added Line
			
			StringBuilder sql = new StringBuilder("DELETE FROM M_InventoryLine WHERE Processed='N' ")
				.append("AND M_Inventory_ID=").append(p_M_Inventory_ID);
			int no = DB.executeUpdate(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("doIt - Deleted #" + no);
		}
		
		//	Create Null Storage records
		if (p_QtyRange != null && p_QtyRange.equals("="))
		{
			StringBuilder sql = new StringBuilder("INSERT INTO M_StorageOnHand ");
								sql.append("(AD_Client_ID, AD_Org_ID, IsActive, Created, CreatedBy, Updated, UpdatedBy,");
								sql.append(" M_Locator_ID, M_Product_ID, M_AttributeSetInstance_ID,");
								sql.append(" QtyOnHand, DateLastInventory, DateMaterialPolicy, M_StorageOnHand_UU) ");
								sql.append("SELECT l.AD_CLIENT_ID, l.AD_ORG_ID, 'Y', getDate(), 0,getDate(), 0,");
								sql.append(" l.M_Locator_ID, p.M_Product_ID, 0,");
								sql.append(" 0,null,trunc(getdate()),generate_uuid() ");
								sql.append("FROM M_Locator l");
								sql.append(" INNER JOIN M_Product p ON (l.AD_Client_ID=p.AD_Client_ID) ");
								sql.append("WHERE l.JP_PhysicalWarehouse_ID=");//JPIERE
								sql.append(m_inventory.get_ValueAsInt("JP_PhysicalWarehouse_ID"));
								
			if (p_M_Locator_ID != 0)
				sql.append(" AND l.M_Locator_ID=").append(p_M_Locator_ID);
			sql.append(" AND l.IsDefault='Y'")
				.append(" AND p.IsActive='Y' AND p.IsStocked='Y' and p.ProductType='I'")
				.append(" AND NOT EXISTS (SELECT * FROM M_StorageOnHand s")
					.append(" INNER JOIN M_Locator sl ON (s.M_Locator_ID=sl.M_Locator_ID) ")
					.append("WHERE sl.M_Warehouse_ID=l.M_Warehouse_ID")
						.append(" AND s.M_Product_ID=p.M_Product_ID)");
			int no = DB.executeUpdate(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("'0' Inserted #" + no);
		}
		
		StringBuilder sql = new StringBuilder("SELECT s.M_Product_ID, s.M_Locator_ID, s.M_AttributeSetInstance_ID,");
							   sql.append(" s.QtyOnHand, p.M_AttributeSet_ID ,s.DateMaterialPolicy");
							   sql.append(" FROM M_Product p");
							   sql.append(" INNER JOIN M_StorageOnHand s ON (s.M_Product_ID=p.M_Product_ID)");
							   sql.append(" INNER JOIN M_Locator l ON (s.M_Locator_ID=l.M_Locator_ID) ");
							   sql.append("WHERE l.JP_PhysicalWarehouse_ID=?");//JPIERE
							   sql.append(" AND p.IsActive='Y' AND p.IsStocked='Y' and p.ProductType='I'");
		//
		if (p_M_Locator_ID != 0)
			sql.append(" AND s.M_Locator_ID=?");
		
		if (p_M_LocatorType_ID != 0)	//JPIERE
			sql.append(" AND l.M_LocatorType_ID=?");
		//
		if (p_LocatorValue != null && 
			(p_LocatorValue.trim().length() == 0 || p_LocatorValue.equals("%")))
			p_LocatorValue = null;
		if (p_LocatorValue != null)
			sql.append(" AND UPPER(l.Value) LIKE ?");
		//
		if (p_ProductValue != null && 
			(p_ProductValue.trim().length() == 0 || p_ProductValue.equals("%")))
			p_ProductValue = null;
		if (p_ProductValue != null)
			sql.append(" AND UPPER(p.Value) LIKE ?");
		//
		if (p_M_Product_Category_ID != 0)
			sql.append(" AND p.M_Product_Category_ID IN (")
			   .append(getSubCategoryWhereClause(p_M_Product_Category_ID))
			   .append(")");
		
		//	Do not overwrite existing records
		if (!p_DeleteOld)
			sql.append(" AND NOT EXISTS (SELECT * FROM M_InventoryLine il ")
			   .append("WHERE il.M_Inventory_ID=?")
			   .append(" AND il.M_Product_ID=s.M_Product_ID")
			   .append(" AND il.M_Locator_ID=s.M_Locator_ID")
			   .append(" AND COALESCE(il.M_AttributeSetInstance_ID,0)=COALESCE(s.M_AttributeSetInstance_ID,0))");
		//
		sql.append(" ORDER BY l.Value, p.Value, s.M_AttributeSetInstance_ID, s.DateMaterialPolicy, s.QtyOnHand DESC");	//	Locator/Product
		//
		int count = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), get_TrxName());
			int index = 1;
			pstmt.setInt (index++, m_inventory.get_ValueAsInt("JP_PhysicalWarehouse_ID"));//JPIERE
			if (p_M_Locator_ID != 0)
				pstmt.setInt(index++, p_M_Locator_ID);
			if (p_M_LocatorType_ID != 0)	//JPIERE
				pstmt.setInt(index++, p_M_LocatorType_ID);
			if (p_LocatorValue != null) 
				pstmt.setString(index++, p_LocatorValue.toUpperCase());
			if (p_ProductValue != null) 
				pstmt.setString(index++, p_ProductValue.toUpperCase());
			if (!p_DeleteOld)
				pstmt.setInt(index++, p_M_Inventory_ID);
			rs = pstmt.executeQuery ();
			while (rs.next ())
			{
				int M_Product_ID = rs.getInt(1);
				int M_Locator_ID = rs.getInt(2);
				int M_AttributeSetInstance_ID = rs.getInt(3);
				BigDecimal QtyOnHand = rs.getBigDecimal(4);
				if (QtyOnHand == null)
					QtyOnHand = Env.ZERO;
				int M_AttributeSet_ID = rs.getInt(5);
				
				Timestamp dateMpolicy = rs.getTimestamp(6);
				//
				int compare = QtyOnHand.compareTo(Env.ZERO);
		        if (p_QtyRange == null
		        	|| (p_QtyRange.equals(">") && compare > 0)
		            || (p_QtyRange.equals("<") && compare < 0)
		            || (p_QtyRange.equals("=") && compare == 0)
		            || (p_QtyRange.equals("N") && compare != 0))
		        {
					count += createInventoryLine (M_Locator_ID, M_Product_ID, 
						M_AttributeSetInstance_ID, QtyOnHand, M_AttributeSet_ID,dateMpolicy);
		        }
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql.toString(), e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		
		//	Set Count to Zero
		if (p_InventoryCountSetZero)
		{
			StringBuilder sql1 = new StringBuilder("UPDATE M_InventoryLine l ")
				.append("SET QtyCount=0 ")
				.append("WHERE M_Inventory_ID=").append(p_M_Inventory_ID);
			int no = DB.executeUpdate(sql1.toString(), get_TrxName());
			if (log.isLoggable(Level.INFO)) log.info("Set Cont to Zero=" + no);
		}
		
		//
		StringBuilder msgreturn = new StringBuilder("@M_InventoryLine_ID@ - #").append(count);
		return msgreturn.toString();
	}	//	doIt
	
	/**
	 * 	Create/Add to Inventory Line
	 *	@param M_Product_ID product
	 *	@param M_Locator_ID locator
	 *	@param M_AttributeSetInstance_ID asi
	 *	@param QtyOnHand qty
	 *	@param M_AttributeSet_ID as
	 *	@return lines added
	 */
	private int createInventoryLine (int M_Locator_ID, int M_Product_ID, 
		int M_AttributeSetInstance_ID, BigDecimal QtyOnHand, int M_AttributeSet_ID,Timestamp dateMPolicy)
	{
		if (QtyOnHand.signum() == 0)
			M_AttributeSetInstance_ID = 0;

		// TODO???? This is not working --- must create one line and multiple MA
		if (m_line != null 
			&& m_line.getM_Locator_ID() == M_Locator_ID
			&& m_line.getM_Product_ID() == M_Product_ID)
		{
			if (QtyOnHand.signum() == 0)
				return 0;
			//	Same ASI and Date
			if (m_line.getM_AttributeSetInstance_ID() == M_AttributeSetInstance_ID && ((dateMPolicy==null && oldDateMPolicy==null) || (dateMPolicy!=null && dateMPolicy.equals(oldDateMPolicy)) || (oldDateMPolicy!=null && oldDateMPolicy.equals(dateMPolicy))))
			{
				m_line.setQtyBook(m_line.getQtyBook().add(QtyOnHand));
				m_line.setQtyCount(m_line.getQtyCount().add(QtyOnHand));
				m_line.saveEx();
				return 0;
			}
			//	Save Old Line info
			else if (m_line.getM_AttributeSetInstance_ID() != 0 )
			{
				MInventoryLineMA ma = new MInventoryLineMA (m_line, 
					m_line.getM_AttributeSetInstance_ID(), m_line.getQtyBook(),oldDateMPolicy,true);
				if (!ma.save())
					log.warning("Could not save " + ma);
			}
			m_line.setM_AttributeSetInstance_ID(0);
			m_line.setQtyBook(m_line.getQtyBook().add(QtyOnHand));
			m_line.setQtyCount(m_line.getQtyCount().add(QtyOnHand));
			m_line.saveEx();
			
			//
			MInventoryLineMA ma = new MInventoryLineMA (m_line, 
				M_AttributeSetInstance_ID, QtyOnHand,dateMPolicy,true);
			if (!ma.save())
				log.warning("Could not save " + ma);
			return 0;
		}
		//	new line
		m_line = new MInventoryLine (m_inventory, M_Locator_ID, 
			M_Product_ID, M_AttributeSetInstance_ID,
			QtyOnHand, QtyOnHand);		//	book/count
		
		oldDateMPolicy = dateMPolicy;
		if (m_line.save())
			return 1;
		return 0;
	}	//	createInventoryLine
	
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
	
}	//	InventoryCountCreate
