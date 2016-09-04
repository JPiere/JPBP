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

import java.util.logging.Level;

import org.compiere.model.MRefList;
import org.compiere.model.MTree;
import org.compiere.model.MTree_Base;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Msg;


/**
 *	JPIERE-0189:Synchronize Tree Node
 *
 *  @author Hideaki Hagiwara
 */
public class JPiereSynchronizeTreeNodes extends SvrProcess
{
	/**	Tab	To					*/
	private int			p_AD_TreeFrom_ID = 0;
	/**	Tab	From				*/
	private int			p_AD_TreeTo_ID = 0;

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		p_AD_TreeTo_ID = getRecord_ID();

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("AD_Tree_ID"))
				p_AD_TreeFrom_ID = para[i].getParameterAsInt();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
	}	//	prepare

	/**
	 * 	Process
	 *	@return message
	 *	@throws Exception
	 */
	protected String doIt() throws Exception
	{


		MTree treeTo =  new MTree(getCtx(), p_AD_TreeTo_ID, get_TrxName());
		MTree treeFrom =  new MTree(getCtx(), p_AD_TreeFrom_ID, get_TrxName());

		if(p_AD_TreeTo_ID == p_AD_TreeFrom_ID)
		{
			String msg = Msg.getMsg(getCtx(), "JP_CannotSynchronizeSameTree") ; //You can not synchronize same Tree;
			addLog(msg);
			return "";
		}

		if(!treeTo.getTreeType().equals(treeFrom.getTreeType()))
		{
			String msg = Msg.getMsg(getCtx(), "JP_SelectSameTreeType") ; //Please select same Tree Type;
			addLog(msg);
			return "";
		}


		if(treeTo.isAllNodes())
		{
			if(!treeFrom.isAllNodes())
			{
				String msg = Msg.getMsg(getCtx(), "JP_NotAllNodes") ; //Please select a Tree that contains All Nodes. Because You ticked All Nodes ;
				addLog(msg);
				return "";
			}
		}


		String tableName = MTree_Base.getNodeTableName(treeTo.getTreeType());
		if(treeTo.getTreeType().equals(MTree.TREETYPE_CustomTable))
		{
			String msg = Msg.getMsg(getCtx(), "CopyError") + MRefList.get(getCtx(), 120, treeTo.getTreeType(), get_TrxName());
			addLog(msg);
			return "";
		}

		String deleteSQL = "DELETE FROM " + tableName + " WHERE AD_Tree_ID=" +p_AD_TreeTo_ID;
		int deleteNo = DB.executeUpdateEx(deleteSQL, get_TrxName());


		String insertSQL = "INSERT INTO " + tableName
				+ " (AD_Tree_ID, Node_ID, AD_Client_ID, AD_Org_ID, IsActive, Created, CreatedBy, Updated, UpdatedBy, Parent_ID, SeqNo,"
				+ tableName + "_UU)"
				+ "SELECT "+ p_AD_TreeTo_ID + ", Node_ID," + getAD_Client_ID() +", AD_Org_ID, IsActive, SysDate," + getAD_User_ID() + ", SysDate," + getAD_User_ID() + ", Parent_ID, SeqNo,"
				+ "generate_uuid()" + " FROM " + tableName + " WHERE AD_Tree_ID=" +p_AD_TreeFrom_ID;

		int insertNo =DB.executeUpdateEx(insertSQL, get_TrxName());

		return Msg.getMsg(getCtx(), "Deleted") + " = " +  deleteNo + " / "+Msg.getMsg(getCtx(), "Inserted") + " = " + insertNo;

	}	//	doIt

}	//	Synchronize Tree Node
