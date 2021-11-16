/******************************************************************************
 * Copyright (C) 2008 Low Heng Sin                                            *
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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import org.adempiere.webui.component.ConfirmPanel;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListItem;
import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.Mask;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.component.ToolBar;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.MRole;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfo;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;
import org.compiere.wf.MWFActivity;
import org.compiere.wf.MWFNode;
import org.compiere.wf.MWFNodeNext;
import org.compiere.wf.MWorkflow;
import org.zkoss.zhtml.Table;
import org.zkoss.zhtml.Td;
import org.zkoss.zhtml.Tr;
import org.zkoss.zk.au.out.AuScript;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Center;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.North;


/**
 * JPIERE-0516 - Workflow Editor
 *
 * Ref: org.adempiere.webui.apps.wf.WFEditor
 *
 * @author Low Heng Sin
 * @author h.hagiwara
 *
 */
public class WFEditorJP extends ADForm {
	/**
	 *
	 */
	private static final long serialVersionUID = 4293422396394778274L;

	private Listbox workflowList;

	@SuppressWarnings("unused")
	private int m_workflowId = 0;

	private Table table;
	private Center center;
	private MWorkflow m_wf;
	private WFNodeContainerJP nodeContainer;

	@Override
	protected void initForm()
	{

		setClosable(true);
		ZKUpdateUtil.setHeight(this, "96%");
		ZKUpdateUtil.setWidth(this, "68%");
		//showBusyMask();

		ProcessInfo pi = getProcessInfo();

		if(pi == null)
		{
			super.dispose();
			return;
		}

		String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE T_Selection.AD_PInstance_ID=? " +
				" AND T_Selection.T_Selection_ID = AD_WF_Activity .AD_WF_Activity_ID)";

		Collection<MWFActivity> m_WFAs = new Query(Env.getCtx(), MWFActivity.Table_Name, whereClause, null)
													.setClient_ID()
													.setParameters(new Object[]{pi.getAD_PInstance_ID()})
													.list();

		MWFActivity m_WFA = null;
		if(m_WFAs.size() == 1)
		{
			Object[] wfas = m_WFAs.toArray();
			m_WFA = (MWFActivity)wfas[0];

		}else {

			//Please select one WF Activity for display Workflow.
			Events.postEvent(Events.ON_CLOSE, this, null);
			FDialog.error(getWindowNo(),"JP_WF_WFEditorSelectRecordError");//TODO メッセージを変更
			return ;
		}

		showBusyMask();

		Borderlayout layout = new Borderlayout();
		layout.setStyle("width: 100%; height: 100%; position: relative;");
		appendChild(layout);
		String sql;
		boolean isBaseLanguage = Env.isBaseLanguage(Env.getCtx(), "AD_Workflow");
		if (isBaseLanguage)
			sql = MRole.getDefault().addAccessSQL(
				"SELECT AD_Workflow_ID, Name FROM AD_Workflow WHERE IsActive='Y' ORDER BY 2",
				"AD_Workflow", MRole.SQL_NOTQUALIFIED, MRole.SQL_RO);	//	all
		else
			sql = MRole.getDefault().addAccessSQL(
					"SELECT AD_Workflow.AD_Workflow_ID, AD_Workflow_Trl.Name FROM AD_Workflow INNER JOIN AD_Workflow_Trl ON (AD_Workflow.AD_Workflow_ID=AD_Workflow_Trl.AD_Workflow_ID) "
					+ " WHERE AD_Workflow.IsActive='Y' AND AD_Workflow_Trl.AD_Language='"+Env.getAD_Language(Env.getCtx())+"' ORDER BY 2","AD_Workflow", MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO);	//	all
		KeyNamePair[] pp = DB.getKeyNamePairs(sql, true);

		workflowList = ListboxFactory.newDropdownListbox();
		for (KeyNamePair knp : pp) {
			workflowList.addItem(knp);
		}
		//workflowList.addEventListener(Events.ON_SELECT, this);

		North north = new North();
		layout.appendChild(north);
		ToolBar toolbar = new ToolBar();
		north.appendChild(toolbar);
		toolbar.appendChild(workflowList);

		sql = "SELECT DocumentNo, Value, JP_Subject From JP_WF_Activity_Tables WHERE AD_Table_ID=? AND Record_ID=?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String documentNo = null;
		String value = null;
		String jp_Subject = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, m_WFA.getAD_Table_ID());
			pstmt.setInt(2, m_WFA.getRecord_ID());
			rs = pstmt.executeQuery();
			if (rs.next())
			{
				documentNo = rs.getString(1);
				value = rs.getString(2);
				jp_Subject  = rs.getString(3);
			}
		}
		catch (Exception e)
		{
			//log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}


		Div div = new Div();
		div.appendChild(new Html("&nbsp;"));
		toolbar.appendChild(div);

		Label label = new Label(Msg.getElement(Env.getCtx(), "DocumentNo")+ " : ");
		toolbar.appendChild(label);

		Textbox textBox = new Textbox(documentNo);
		toolbar.appendChild(textBox);
		textBox.setReadonly(true);

		div = new Div();
		div.appendChild(new Html("&nbsp;"));
		toolbar.appendChild(div);
		label = new Label(Msg.getElement(Env.getCtx(), "Value")+ " : ");
		toolbar.appendChild(label);

		textBox = new Textbox(value);
		toolbar.appendChild(textBox);
		textBox.setReadonly(true);

		div = new Div();
		div.appendChild(new Html("&nbsp;"));
		toolbar.appendChild(div);
		label = new Label(Msg.getElement(Env.getCtx(), "JP_Subject")+ " : ");
		toolbar.appendChild(label);

		textBox = new Textbox(jp_Subject);
		toolbar.appendChild(textBox);
		textBox.setReadonly(true);


		createTable();
		center = new Center();
		layout.appendChild(center);
		center.setAutoscroll(true);
		center.appendChild(table);

//		ConfirmPanel confirmPanel = new ConfirmPanel(true);
//		confirmPanel.addActionListener(this);
//		South south = new South();
//		layout.appendChild(south);
//		south.appendChild(confirmPanel);
//		ZKUpdateUtil.setHeight(south, "36px");


		int AD_Workflow_ID = m_WFA.getAD_Workflow_ID();

		List<Listitem> items =  workflowList.getItems();
		for(Listitem item : items)
		{
			Integer obj = (Integer)item.getValue();
			if(obj.intValue() == AD_Workflow_ID)
			{
				workflowList.setSelectedItem(item);
				break;
			}
		}

		center.removeChild(table);
		createTable();
		center.appendChild(table);
		ListItem item = workflowList.getSelectedItem();
		KeyNamePair knp = item != null ? item.toKeyNamePair() : null;
		if (knp != null && knp.getKey() > 0) {
			load(knp.getKey(), true, m_WFA);
		}
		workflowList.setDisabled(true);
		addEventListener(Events.ON_CLOSE, this);

	}

	private void createTable() {
		table = new Table();
		table.setDynamicProperty("cellpadding", "0");
		table.setDynamicProperty("cellspacing", "0");
		table.setDynamicProperty("border", "none");
		table.setStyle("margin:0;padding:0");
	}

	@Override
	public Mode getWindowMode()
	{
		return Mode.OVERLAPPED; //Mode.MODAL, Mode.HIGHLIGHTED, Mode.POPUP, Mode.OVERLAPPED
	}

	@Override
	public void onEvent(Event event) throws Exception
	{
		if (event.getName().equals(Events.ON_CLOSE))
		{
			hideBusyMask();
			this.detach();
		}else if (event.getTarget().getId().equals(ConfirmPanel.A_CANCEL)) {
			hideBusyMask();
			this.detach();
		}else if (event.getTarget().getId().equals(ConfirmPanel.A_OK)) {
			hideBusyMask();
			this.detach();
		}
	}

	private void load(int workflowId, boolean reread, MWFActivity m_WFA)
	{
		//	Get Workflow
		m_wf = MWorkflow.getCopy(Env.getCtx(), workflowId, (String)null);
		m_workflowId = workflowId;
		nodeContainer = new WFNodeContainerJP();
		nodeContainer.setWorkflow(m_wf);

		if (reread) {
			m_wf.reloadNodes();
		}

		String where = "AD_Table_ID=? AND Record_ID=? AND AD_WF_Process_ID = ?";
		List<MWFActivity> wfas = new Query(Env.getCtx(), MWFActivity.Table_Name, where, null)
				.setParameters(m_WFA.getAD_Table_ID(), m_WFA.getRecord_ID(), m_WFA.getAD_WF_Process_ID())
				.list();

		//	Add Nodes for Paint
		MWFNode[] nodes = m_wf.getNodes(true, Env.getAD_Client_ID(Env.getCtx()));
		List<Integer> added = new ArrayList<Integer>();
		for (int i = 0; i < nodes.length; i++)
		{
			if (!added.contains(nodes[i].getAD_WF_Node_ID()))
			{
				boolean hasWFAcitivity = false;
				for(MWFActivity wfa : wfas)
				{
					if(nodes[i].getAD_WF_Node_ID() == wfa.getAD_WF_Node_ID())
					{
						nodeContainer.addNode(nodes[i], wfa);
						hasWFAcitivity = true;
						break;
					}
				}


				if(!hasWFAcitivity)
				{
					nodeContainer.addNode(nodes[i], null);
				}
			}
		}//for i

		//  Add lines
		for (int i = 0; i < nodes.length; i++)
		{
			MWFNodeNext[] nexts = nodes[i].getTransitions(Env.getAD_Client_ID(Env.getCtx()));
			for (int j = 0; j < nexts.length; j++)
			{
				nodeContainer.addEdge(nexts[j]);
			}
		}

		Dimension dimension = nodeContainer.getDimension();
		BufferedImage bi = new BufferedImage (dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = bi.createGraphics();
		nodeContainer.validate(graphics);
		nodeContainer.paint(graphics);

		try {
			int row = nodeContainer.getRowCount();
			for(int i = 0; i < row+1; i++) {
				Tr tr = new Tr();
				table.appendChild(tr);
				for(int c = 0; c < 4; c++) {
					BufferedImage t = new BufferedImage(WFGraphLayoutJP.COLUMN_WIDTH, WFGraphLayoutJP.ROW_HEIGHT, BufferedImage.TYPE_INT_ARGB);
					Graphics2D tg = t.createGraphics();
					Td td = new Td();
					td.setStyle("border: 1px dotted lightgray");
					tr.appendChild(td);

					if (i < row)
					{
						int x = c * WFGraphLayoutJP.COLUMN_WIDTH;
						int y = i * WFGraphLayoutJP.ROW_HEIGHT;

						tg.drawImage(bi.getSubimage(x, y, WFGraphLayoutJP.COLUMN_WIDTH, WFGraphLayoutJP.ROW_HEIGHT), 0, 0, null);
						org.zkoss.zul.Image image = new org.zkoss.zul.Image();
						image.setContent(t);
						td.appendChild(image);
						String imgStyle = "border:none;margin:0;padding:0";

						WFNodeWidgetJP widget = nodeContainer.findWidget(i+1, c+1);
						if (widget != null)
						{
							MWFNode node = widget.getModel();
							if (node.getHelp(true) != null) {
								image.setTooltiptext(node.getHelp(true));
							}
							image.setAttribute("AD_WF_Node_ID", node.getAD_WF_Node_ID());
						}
						else
						{
							image.setAttribute("Node.XPosition", c+1);
							image.setAttribute("Node.YPosition", i+1);
						}
						image.setStyle(imgStyle);
					}
					else
					{
						Div div = new Div();
						ZKUpdateUtil.setWidth(div, (WFGraphLayoutJP.COLUMN_WIDTH) + "px");
						ZKUpdateUtil.setHeight(div, (WFGraphLayoutJP.ROW_HEIGHT) + "px");
						div.setAttribute("Node.XPosition", c+1);
						div.setAttribute("Node.YPosition", i+1);
						div.setDroppable("WFNode");
						div.addEventListener(Events.ON_DROP, this);
						td.appendChild(div);
					}

					tg.dispose();
				}
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}

	}

	private Mask mask = null;
	private Div getMask()
	{
		if (mask == null) {
			mask = new Mask();
		}
		return mask;
	}

	public void hideBusyMask()
	{
		if (mask != null && mask.getParent() != null) {
			mask.detach();
			StringBuilder script = new StringBuilder("var w=zk.Widget.$('#");
			script.append(getUuid()).append("');if(w) w.busy=false;");
			Clients.response(new AuScript(script.toString()));
		}
	}

	public void showBusyMask()
	{
		Component parent = SessionManager.getAppDesktop().getActiveWindow();
		parent.appendChild(getMask());

		StringBuilder script = new StringBuilder("var w=zk.Widget.$('#");
		script.append(getUuid()).append("');");
		script.append("var d=zk.Widget.$('#").append(parent.getUuid()).append("');w.busy=d;");

		Clients.response(new AuScript(script.toString()));
	}
}
