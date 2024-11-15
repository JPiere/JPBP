package jpiere.base.plugin.org.adempiere.model;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.I_C_InvoiceLine;
import org.compiere.model.MInOut;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceBatch;
import org.compiere.model.MInvoiceBatchLine;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MProduct;
import org.compiere.model.MSysConfig;
import org.compiere.model.Query;
import org.compiere.process.DocOptions;
import org.compiere.process.DocumentEngine;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.eevolution.model.MPPProductBOM;
import org.eevolution.model.MPPProductBOMLine;

public class MInvoiceJP extends MInvoice implements DocOptions {
	
	private static final long serialVersionUID = 1721489545075501605L;

	public MInvoiceJP(Properties ctx, int C_Invoice_ID, String trxName) 
	{
		super(ctx, C_Invoice_ID, trxName);
	}
	
	public MInvoiceJP(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}
	
	public MInvoiceJP(MOrder order, int C_DocTypeTarget_ID, Timestamp invoiceDate) 
	{
		super(order, C_DocTypeTarget_ID, invoiceDate);
	}
	
	public MInvoiceJP(MInOut ship, Timestamp invoiceDate)
	{
		super(ship, invoiceDate);
	}
	
	public MInvoiceJP(MInvoiceBatch batch, MInvoiceBatchLine line) 
	{
		super(batch, line);
	}
	
	
	@Override
	public int customizeValidActions(String docStatus, Object processing, String orderType, String isSOTrx,
			int AD_Table_ID, String[] docAction, String[] options, int index) 
	{
		
		if(docStatus.equals(DocumentEngine.STATUS_Drafted))
		{
			index = 0; //initialize the index
			options[index++] = DocumentEngine.ACTION_Complete; 
			options[index++] = DocumentEngine.ACTION_Prepare; 
			options[index++] = DocumentEngine.ACTION_Void; 
			return index;
		}
		
		return index;
	}
	
	
	public boolean explodeBOM() {
		boolean retValue = false;
		String where = "AND IsActive='Y' AND EXISTS "
			+ "(SELECT * FROM M_Product p WHERE C_InvoiceLine.M_Product_ID=p.M_Product_ID"
			+ " AND	p.IsBOM='Y' AND p.IsVerified='Y' AND p.IsStocked='N')";
		//
		String sql = "SELECT COUNT(*) FROM C_InvoiceLine "
			+ "WHERE C_Invoice_ID=? " + where; 
		int count = DB.getSQLValue(get_TrxName(), sql, getC_Invoice_ID());
		while (count != 0)
		{
			retValue = true;
			renumberLines (1000);		//	max 999 bom items	

			//	Order Lines with non-stocked BOMs
			MInvoiceLine[] lines = getLines (where);
			for (int i = 0; i < lines.length; i++)
			{
				MInvoiceLine line = lines[i];
				MProduct product = MProduct.get (getCtx(), line.getM_Product_ID());
				if (log.isLoggable(Level.FINE)) log.fine(product.getName());
				//	New Lines
				int lineNo = line.getLine ();
				MPPProductBOM bom = MPPProductBOM.getDefault(product, get_TrxName());
				if (bom == null)
					continue;
				
				boolean isRemain = MSysConfig.getBooleanValue("JP_REMAIN_EXPLODE_PRODUCT_LINE", false, getAD_Client_ID(), getAD_Org_ID());
				boolean isFirstLine = true;
				int JP_ProductExplodeBOM_ID = line.getM_Product_ID();
				
				for (MPPProductBOMLine bomLine : bom.getLines())
				{
					if(isRemain || !isFirstLine)
					{
						MInvoiceLine newLine = new MInvoiceLine(this);
						newLine.setLine(++lineNo);
						
						//JPIERE-0295
						newLine.set_ValueNoCheck("JP_ProductExplodeBOM_ID", JP_ProductExplodeBOM_ID);

						newLine.setM_Product_ID(bomLine.getM_Product_ID(), true);
						newLine.setQty(line.getQtyInvoiced().multiply(bomLine.getQtyBOM()));
						if (bom.getDescription() != null)
							newLine.setDescription(bom.getDescription());
						newLine.setPrice();
						newLine.save(get_TrxName());
						
					}else{

						//JPIERE-0295
						line.set_ValueNoCheck("JP_ProductExplodeBOM_ID", JP_ProductExplodeBOM_ID);
						
						line.setM_Product_ID(bomLine.getM_Product_ID(), true);
						
						//Reset once
						line.setM_AttributeSetInstance_ID (0);
						line.setPrice (Env.ZERO);
						line.setPriceLimit (Env.ZERO);
						line.setPriceList (Env.ZERO);
						line.setLineNetAmt (Env.ZERO);
						
						//Set again
						line.setQty(line.getQtyInvoiced().multiply(bomLine.getQtyBOM()));
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
					//JPIERE-0295
					line.set_ValueNoCheck("JP_ProductExplodeBOM_ID", line.getM_Product_ID());
					
					line.setM_Product_ID (0);
					line.setM_AttributeSetInstance_ID (0);
					line.setPrice (Env.ZERO);
					line.setPriceLimit (Env.ZERO);
					line.setPriceList (Env.ZERO);
					line.setLineNetAmt (Env.ZERO);
					
					String description = product.getName ();
					if (product.getDescription () != null)
						description += " " + product.getDescription ();
					if (line.getDescription () != null)
						description += " " + line.getDescription ();
					line.setDescription (description);
					line.save (get_TrxName());
				}
				
			}	//	for all lines with BOM

			count = DB.getSQLValue (get_TrxName(), sql, getC_Invoice_ID ());
			renumberLines (10);
		}	//	while count != 0
		return retValue;
	}
	
	/**
	 * 	Get Invoice Lines of Invoice
	 * 	@param whereClause starting with AND
	 * 	@return lines
	 */
	private MInvoiceLine[] getLines (String whereClause)
	{
		String whereClauseFinal = "C_Invoice_ID=? ";
		if (whereClause != null)
			whereClauseFinal += whereClause;
		List<MInvoiceLine> list = new Query(getCtx(), I_C_InvoiceLine.Table_Name, whereClauseFinal, get_TrxName())
										.setParameters(getC_Invoice_ID())
										.setOrderBy(I_C_InvoiceLine.COLUMNNAME_Line)
										.list();
		return list.toArray(new MInvoiceLine[list.size()]);
	}	//	getLines
	
	
}
