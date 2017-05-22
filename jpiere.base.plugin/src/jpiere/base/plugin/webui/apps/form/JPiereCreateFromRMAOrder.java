/******************************************************************************
 * Copyright (C) 2009 Low Heng Sin                                            *
 * Copyright (C) 2009 Idalica Corporation                                     *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/
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
package jpiere.base.plugin.webui.apps.form;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;

import org.compiere.apps.IStatusBar;
import org.compiere.grid.CreateFrom;
import org.compiere.minigrid.IMiniTable;
import org.compiere.model.GridTab;
import org.compiere.model.MDocType;
import org.compiere.model.MRMA;
import org.compiere.model.MRMALine;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Language;
import org.compiere.util.Msg;
import org.compiere.util.ValueNamePair;

/**
 * JPIERE-0235: Craete From RMA Order
 * 
 *  Create Transactions for RMA
 * @author ashley
 * @author Teo Sarca, www.arhipac.ro
 * 			<li>BF [ 2007837 ] VCreateFrom.save() should run in trx
 * 
 * @author Hideaki Hagiwara
 * 
 */
public abstract class JPiereCreateFromRMAOrder extends CreateFrom {

	protected MDocType 		m_DocType = null;
	protected boolean		isSOTrx = true;
	
	public JPiereCreateFromRMAOrder(GridTab mTab)
	{
		super(mTab);
		Integer C_DocType_ID = (Integer)mTab.getField("C_DocType_ID").getValue();
		m_DocType = MDocType.get(Env.getCtx(), C_DocType_ID.intValue());
		isSOTrx = m_DocType.isSOTrx();
		
		if (log.isLoggable(Level.INFO)) log.info(mTab.toString());
	}
	
	@Override
	public boolean dynInit() throws Exception 
	{
		log.config("");
		setTitle(Msg.getElement(Env.getCtx(), "M_RMA_ID",isSOTrx) + " .. " + Msg.translate(Env.getCtx(), "CreateFrom"));

		return true;
	}
	
	protected Vector<Vector<Object>> getRMAData()
	{
		int M_InOut_ID = Env.getContextAsInt(Env.getCtx(), getGridTab().getWindowNo(), "InOut_ID");
		int M_RMA_ID = Env.getContextAsInt(Env.getCtx(), getGridTab().getWindowNo(), "M_RMA_ID");
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		
		/**
         * 1 M_InOutLine_ID
         * 2 Line
         * 3 Product Name
         * 4 Qty Entered
         * 5 Movement Qty
         * 6 ASI
         */
        StringBuilder sqlStmt = new StringBuilder();
        
        Language loginLang = Env.getLoginLanguage(Env.getCtx());
        String loginLangString =Env.getAD_Language(Env.getCtx());
        Boolean isBaseLang = loginLang.isBaseLanguage();
        
        sqlStmt.append("SELECT iol.M_InOutLine_ID, iol.Line, "); 
        sqlStmt.append("COALESCE(p.Name, c.Name) AS ProductName, "); 
        sqlStmt.append("iol.QtyEntered, "); 
        if(isBaseLang)
        	sqlStmt.append("uom.Name, ");
        else
        	sqlStmt.append("uomt.Name, ");
        sqlStmt.append("iol.movementQty, ");
        sqlStmt.append("CASE WHEN iol.M_AttributeSetInstance_ID IS NOT NULL THEN (SELECT SerNo FROM M_AttributeSetInstance asi WHERE asi.M_AttributeSetInstance_ID=iol.M_AttributeSetInstance_ID) END as ASI ");
        sqlStmt.append("FROM M_InOutLine iol ");
        sqlStmt.append("LEFT JOIN M_Product p ON p.M_Product_ID = iol.M_Product_ID ");
        sqlStmt.append("LEFT JOIN C_Charge c ON c.C_Charge_ID = iol.C_Charge_ID ");
        sqlStmt.append("LEFT JOIN C_UOM uom ON uom.C_UOM_ID = iol.C_UOM_ID ");
        if(!isBaseLang)
        	sqlStmt.append("LEFT JOIN C_UOM_Trl uomt ON uom.C_UOM_ID = uomt.C_UOM_ID AND uomt.AD_Language = '"+loginLangString+"'");
        sqlStmt.append("WHERE M_InOut_ID=? ");
        sqlStmt.append("AND iol.M_InOutLine_ID NOT IN (SELECT rmal.M_InOutLine_ID FROM M_RMALine rmal WHERE rmal.M_RMA_ID=?) ORDER BY iol.Line");
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            pstmt = DB.prepareStatement(sqlStmt.toString(), null);
            pstmt.setInt(1, M_InOut_ID);
            pstmt.setInt(2, M_RMA_ID);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                Vector<Object> line = new Vector<Object>(7);
                BigDecimal qtyEntered = rs.getBigDecimal(4); 
                BigDecimal movementQty = rs.getBigDecimal(6);
                
                line.add(new Boolean(false));           //  0-Selection
                line.add(movementQty); //1-Return Qty
                
                KeyNamePair lineKNPair = new KeyNamePair(rs.getInt(1), rs.getString(2)); // 2-Line
                line.add(lineKNPair);
                line.add(rs.getString(3)); //3-Product
                line.add(rs.getString(7)); //4-ASI
                

                
                line.add(qtyEntered);  //5-QtyEntered
                line.add(rs.getString(5)); //6-UOM
                line.add(movementQty); //7-Movement Qty

                
                data.add(line);
            }
        }
        catch (SQLException e)
        {
            log.log(Level.SEVERE, sqlStmt.toString(), e);
        }
        finally
        {
        	DB.close(rs, pstmt);
        	rs = null;
        	pstmt = null;
        }
        
        return data;
	}

	@Override
	public void info(IMiniTable miniTable, IStatusBar statusBar) 
	{

	}
	
	protected void configureMiniTable (IMiniTable miniTable)
	{
		miniTable.setColumnClass(0, Boolean.class, false);      	//  0-Selection
		miniTable.setColumnClass(1, BigDecimal.class, false);		//  1-Return Qty
		miniTable.setColumnClass(2, String.class, true);        	//  2-Line
		miniTable.setColumnClass(3, String.class, true);        	//  3-Product 
		miniTable.setColumnClass(4, String.class, true);        	//  4-ASI
		miniTable.setColumnClass(5, BigDecimal.class, true);    	//  5-QtyEntered
		miniTable.setColumnClass(6, String.class, true);  			//  6-UOM
		miniTable.setColumnClass(7, BigDecimal.class, true); 		//  7-Movement Qty

        
        //  Table UI
		miniTable.autoSize();
	}

	@Override
	public boolean save(IMiniTable miniTable, String trxName) 
	{
		log.config("");
		int M_RMA_ID = Env.getContextAsInt(Env.getCtx(), getGridTab().getWindowNo(), "M_RMA_ID");
        
//        Integer bpId = (Integer)bPartnerField.getValue();
        MRMA rma = new MRMA(Env.getCtx(), M_RMA_ID, trxName);
        //update BP
//        rma.setC_BPartner_ID(bpId);
        
        for (int i = 0; i < miniTable.getRowCount(); i++)
        {
            if (((Boolean)miniTable.getValueAt(i, 0)).booleanValue())
            {
                BigDecimal d = (BigDecimal)miniTable.getValueAt(i, 1);              //  1-Return Qty
                KeyNamePair pp = (KeyNamePair)miniTable.getValueAt(i, 2);   //  2-Line
                
                int inOutLineId = pp.getKey();
                
                MRMALine rmaLine = new MRMALine(rma.getCtx(), 0, rma.get_TrxName());
                rmaLine.setM_RMA_ID(M_RMA_ID);
                rmaLine.setM_InOutLine_ID(inOutLineId);
                rmaLine.setQty(d);
                rmaLine.setAD_Org_ID(rma.getAD_Org_ID());
                if (!rmaLine.save())
                {
                	String msg = null;
        			ValueNamePair err = CLogger.retrieveError();
        			String val = err != null ? Msg.translate(Env.getCtx(), err.getValue()) : "";
        			if (err != null)
        				msg = (val != null ? val + ": " : "") + err.getName();
                    throw new IllegalStateException(Msg.getMsg(Env.getCtx(), "JP_Could_Not_Create_RMA_Line")+" "+msg);//Could not create RMA Line
                }
            }
        }
        rma.saveEx();
        return true;
	}
	
	protected Vector<String> getOISColumnNames()
	{
		//  Header Info
        Vector<String> columnNames = new Vector<String>(8);
        columnNames.add(Msg.getMsg(Env.getCtx(), "Select"));
        columnNames.add(Msg.getMsg(Env.getCtx(), "JP_ReturnQty"));
        columnNames.add(Msg.translate(Env.getCtx(), "Line"));
        columnNames.add(Msg.translate(Env.getCtx(), "M_Product_ID"));
        columnNames.add(Msg.translate(Env.getCtx(), "SerNo"));
        columnNames.add(Msg.translate(Env.getCtx(), "QtyEntered"));
        columnNames.add(Msg.translate(Env.getCtx(), "C_UOM_ID"));
        columnNames.add(Msg.getElement(Env.getCtx(), "MovementQty"));

        
	    return columnNames;
	}
}
