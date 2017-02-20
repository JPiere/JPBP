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

import org.compiere.model.MDocType;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.model.MProductBOM;
import org.compiere.model.MProject;
import org.compiere.model.MSysConfig;
import org.compiere.process.DocOptions;
import org.compiere.process.DocumentEngine;
import org.compiere.util.DB;
import org.compiere.util.Env;

/**
 * JPIERE-0142
 * JPIERE-0294
 * 
 * @author Hideaki Hagiwara
 *
 */
public class MOrderJP extends MOrder implements DocOptions {
	
	public MOrderJP(Properties ctx, int C_Order_ID, String trxName) {
		super(ctx, C_Order_ID, trxName);
	}
	
	public MOrderJP(MProject project, boolean IsSOTrx, String DocSubTypeSO) {
		super(project, IsSOTrx, DocSubTypeSO);
	}
	
	public MOrderJP(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	@Override
	public int customizeValidActions(String docStatus, Object processing, String orderType, String isSOTrx,
			int AD_Table_ID, String[] docAction, String[] options, int index) {
		
		if(isSOTrx.equals("Y") && (orderType.equals(MDocType.DOCSUBTYPESO_Proposal)
				|| orderType.equals(MDocType.DOCSUBTYPESO_Quotation)))
		{
			index = 0; //initialize the index
			options[index++] = DocumentEngine.ACTION_Prepare; 
			options[index++] = DocumentEngine.ACTION_Void; 
			return index;
		}
		
//		if (docStatus.equals(DocumentEngine.STATUS_Completed)) 
//		{
//			index = 0; //initialize the index
//			options[index++] = DocumentEngine.ACTION_Close; 
//			options[index++] = DocumentEngine.ACTION_ReActivate;
//			return index;
//		}
		
		return index;
	}
	

	@Override
	public boolean explodeBOM() {
		boolean retValue = false;
		String where = "AND IsActive='Y' AND EXISTS "
			+ "(SELECT * FROM M_Product p WHERE C_OrderLine.M_Product_ID=p.M_Product_ID"
			+ " AND	p.IsBOM='Y' AND p.IsVerified='Y' AND p.IsStocked='N')";
		//
		String sql = "SELECT COUNT(*) FROM C_OrderLine "
			+ "WHERE C_Order_ID=? " + where; 
		int count = DB.getSQLValue(get_TrxName(), sql, getC_Order_ID());
		while (count != 0)
		{
			retValue = true;
			renumberLines (1000);		//	max 999 bom items	

			//	Order Lines with non-stocked BOMs
			MOrderLine[] lines = getLines (where, MOrderLine.COLUMNNAME_Line);
			for (int i = 0; i < lines.length; i++)
			{
				MOrderLine line = lines[i];
				MProduct product = MProduct.get (getCtx(), line.getM_Product_ID());
				if (log.isLoggable(Level.FINE)) log.fine(product.getName());
				//	New Lines
				int lineNo = line.getLine ();
				//find default BOM with valid dates and to this product
				/*/MPPProductBOM bom = MPPProductBOM.get(product, getAD_Org_ID(),getDatePromised(), get_TrxName());
				if(bom != null)
				{	
					MPPProductBOMLine[] bomlines = bom.getLines(getDatePromised());
					for (int j = 0; j < bomlines.length; j++)
					{
						MPPProductBOMLine bomline = bomlines[j];
						MOrderLine newLine = new MOrderLine (this);
						newLine.setLine (++lineNo);
						newLine.setM_Product_ID (bomline.getM_Product_ID ());
						newLine.setC_UOM_ID (bomline.getC_UOM_ID ());
						newLine.setQty (line.getQtyOrdered ().multiply (
							bomline.getQtyBOM()));
						if (bomline.getDescription () != null)
							newLine.setDescription (bomline.getDescription ());
						//
						newLine.setPrice ();
						newLine.save (get_TrxName());
					}
				}	*/

				
				boolean isRemain = MSysConfig.getBooleanValue("JP_REMAIN_EXPLODE_PRODUCT_LINE", false, getAD_Client_ID(), getAD_Org_ID());
				boolean isFirstLine = true;
				int JP_ProductExplodeBOM_ID = line.getM_Product_ID();
				
				for (MProductBOM bom : MProductBOM.getBOMLines(product))
				{
					if(isRemain || !isFirstLine)
					{
						MOrderLine newLine = new MOrderLine(this);
						newLine.setLine(++lineNo);
						if(newLine.get_ColumnIndex("JP_ProductExplodeBOM_ID") >= 0)
						{
							newLine.set_ValueNoCheck("JP_ProductExplodeBOM_ID", JP_ProductExplodeBOM_ID);
						}
						newLine.setM_Product_ID(bom.getM_ProductBOM_ID(), true);
						newLine.setQty(line.getQtyOrdered().multiply(bom.getBOMQty()));
						if (bom.getDescription() != null)
							newLine.setDescription(bom.getDescription());
						newLine.setPrice();
						newLine.save(get_TrxName());
						
					}else{

						if(line.get_ColumnIndex("JP_ProductExplodeBOM_ID") >= 0)
						{
							line.set_ValueNoCheck("JP_ProductExplodeBOM_ID", JP_ProductExplodeBOM_ID);
						}
						line.setM_Product_ID(bom.getM_ProductBOM_ID(), true);
						
						//Reset once
						line.setM_AttributeSetInstance_ID (0);
						line.setPrice (Env.ZERO);
						line.setPriceLimit (Env.ZERO);
						line.setPriceList (Env.ZERO);
						line.setLineNetAmt (Env.ZERO);
						line.setFreightAmt (Env.ZERO);
						
						//Set again
						line.setQty(line.getQtyOrdered().multiply(bom.getBOMQty()));
						String description  =line.getDescription ();
						if (bom.getDescription() != null)
							line.setDescription(description + " : " +bom.getDescription());
						line.setPrice();
						line.save (get_TrxName());
						isFirstLine = false;
					}
				}//for
				
				//	Convert into Comment Line
				if(isRemain)
				{
					if(line.get_ColumnIndex("JP_ProductExplodeBOM_ID") >= 0)
					{
						line.set_ValueNoCheck("JP_ProductExplodeBOM_ID", line.getM_Product_ID());
					}
					line.setM_Product_ID (0);
					line.setM_AttributeSetInstance_ID (0);
					line.setPrice (Env.ZERO);
					line.setPriceLimit (Env.ZERO);
					line.setPriceList (Env.ZERO);
					line.setLineNetAmt (Env.ZERO);
					line.setFreightAmt (Env.ZERO);
					
					String description = product.getName ();
					if (product.getDescription () != null)
						description += " " + product.getDescription ();
					if (line.getDescription () != null)
						description += " " + line.getDescription ();
					line.setDescription (description);
					line.save (get_TrxName());
				}
				
			}	//	for all lines with BOM

			m_lines = null;		//	force requery
			count = DB.getSQLValue (get_TrxName(), sql, getC_Invoice_ID ());
			renumberLines (10);
		}	//	while count != 0
		return retValue;
	}
	
	
	
}
