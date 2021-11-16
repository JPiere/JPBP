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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.compiere.apps.wf.MultilineLabelWidget;
import org.compiere.model.MRefList;
import org.compiere.model.MUser;
import org.compiere.model.MUserRoles;
import org.compiere.model.MWFActivityApprover;
import org.compiere.model.Query;
import org.compiere.model.X_AD_Workflow;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.compiere.wf.MWFActivity;
import org.compiere.wf.MWFNode;
import org.compiere.wf.MWFNodeNext;
import org.compiere.wf.MWFResponsible;
import org.compiere.wf.MWorkflow;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.graph.layout.GraphLayout;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.layout.SceneLayout;

/**
 * JPIERE-0516 - Workflow Editor
 *
 * Ref: org.compiere.apps.wf.WFNodeContainer
 *
 * @author Low Heng Sin
 * @author h.hagiwara
 *
 */
public class WFNodeContainerJP
{
	/**	Logger			*/
	@SuppressWarnings("unused")
	private static final CLogger	log = CLogger.getCLogger(WFNodeContainerJP.class);

	/** The Workflow		*/
	private MWorkflow	m_wf = null;

	private int currentRow = 1;
	private int currentColumn = 0;
	private int noOfColumns = 4;
	private int maxColumn = 0;
	private int rowCount = 0;

	private WFGraphSceneJP graphScene = new WFGraphSceneJP();

	private Map<Integer, Integer[]> matrix = null;

	/**
	 * 	WFContentPanel
	 */
	public WFNodeContainerJP ()
	{
		matrix = new HashMap<Integer, Integer[]>();
	}	//	WFContentPanel

	/**
	 * 	Set Workflow
	 *	@param wf workflow
	 */
	public void setWorkflow (MWorkflow wf)
	{
		m_wf = wf;
	}	//	setWorkflow


	/**
	 * 	Remove All and their listeners
	 */
	public void removeAll ()
	{
		graphScene = new WFGraphSceneJP();
		currentColumn = 0;
		currentRow = 1;
		matrix = new HashMap<Integer, Integer[]>();
	}	//	removeAll


	public void addNode(MWFNode node, MWFActivity wfa) {
		int oldRow = currentRow;
		int oldColumn = currentColumn;
		if (node.getXPosition() > 0 && node.getYPosition() > 0) {
			currentColumn = node.getXPosition();
			currentRow = node.getYPosition();
			if (currentColumn > noOfColumns) {
				currentColumn = 1;
				currentRow ++;
			}
		} else if (currentColumn == noOfColumns) {
			currentColumn = 1;
			if (m_wf.getWorkflowType().equals(X_AD_Workflow.WORKFLOWTYPE_General)) {
				currentRow++;
			} else {
				currentRow = currentRow + 2;
			}
		} else {
			if (m_wf.getWorkflowType().equals(X_AD_Workflow.WORKFLOWTYPE_General) || currentColumn == 0) {
				currentColumn++;
			} else {
				currentColumn = currentColumn + 2;
				if (currentColumn > noOfColumns) {
					currentColumn = 1;
					currentRow = currentRow + 2;
				}
			}
		}

		if (currentRow > rowCount) {
			rowCount = currentRow;
		}

		Integer[] nodes = matrix.get(currentRow);
		if (nodes == null) {
			nodes = new Integer[noOfColumns];
			matrix.put(currentRow, nodes);
		} else {
			//detect collision
			while (nodes[currentColumn - 1] != null) {
				if (nodes[currentColumn - 1] == node.getAD_WF_Node_ID()) {
					break;
				} else if (currentColumn == noOfColumns) {
					currentColumn = 1;
					currentRow ++;
					nodes = matrix.get(currentRow);
					if (nodes == null) {
						nodes = new Integer[noOfColumns];
						matrix.put(currentRow, nodes);
					}
				} else {
					currentColumn ++;
				}
			}
		}

		WFNodeWidgetJP w = (WFNodeWidgetJP) graphScene.addNode(node.getAD_WF_Node_ID());
		w.setColumn(currentColumn);
		w.setRow(currentRow);

		nodes[currentColumn - 1] = node.getAD_WF_Node_ID();
		if (currentColumn > maxColumn) {
			maxColumn = currentColumn;
		}

		if (currentRow < oldRow) {
			currentRow = oldRow;
			currentColumn = oldColumn;
		} else if ( currentRow == oldRow && currentColumn < oldColumn) {
			currentColumn = oldColumn;
		}

		if(wfa == null)
		{
			if(node.getAD_WF_Responsible_ID() > 0 && node.isUserChoice())
			{
				MWFResponsible responsible = MWFResponsible.get(node.getAD_WF_Responsible_ID());
				MultilineLabelWidget label = new MultilineLabelWidget(graphScene, Msg.getElement(Env.getCtx(), MWFActivity.COLUMNNAME_AD_WF_Responsible_ID)
														+ " : " + responsible.getName() + (Util.isEmpty(node.getDescription())? "" : " ( "+ node.getDescription() +" )" ) );
				label.setPreferredSize(new Dimension(WFNodeWidgetJP.NODE_WIDTH - 20, WFNodeWidgetJP.NODE_HEIGHT - 20));
				label.setJustified(false);
				w.addChild(label);

			}else {

				MultilineLabelWidget label = new MultilineLabelWidget(graphScene, node.getDescription());
				label.setJustified(false);
				label.setPreferredSize(new Dimension(WFNodeWidgetJP.NODE_WIDTH - 20, WFNodeWidgetJP.NODE_HEIGHT - 20));
				w.addChild(label);
			}

		}else{

			String wfstate = MRefList.getListName(Env.getCtx(), 305, wfa.getWFState());

			if(wfa.getWFState().equals(MWFActivity.WFSTATE_Completed))
			{
				MultilineLabelWidget label = new MultilineLabelWidget(graphScene, Msg.getElement(Env.getCtx(), MWFActivity.COLUMNNAME_WFState) + " : " + wfstate);
				label.setJustified(false);
				w.addChild(label);
				w.setBackground(new Color(160,160,160));
				if(node.isUserChoice())
				{
					label = new MultilineLabelWidget(graphScene, Msg.getElement(Env.getCtx(), MUser.COLUMNNAME_AD_User_ID) + " : " + MUser.getNameOfUser(wfa.getAD_User_ID()) );
					w.addChild(label);
				}

			}else if(wfa.getWFState().equals(MWFActivity.WFSTATE_Suspended)) {

				MultilineLabelWidget label = new MultilineLabelWidget(graphScene, getActivityApprovers(wfa) );
				label.setJustified(false);
				label.setPreferredSize(new Dimension(WFNodeWidgetJP.NODE_WIDTH - 20, WFNodeWidgetJP.NODE_HEIGHT - 20));
				w.addChild(label);
				//w.setBackground(new Color(255, 174, 201));
				w.setBackground(new Color(233, 241, 255));

			}else {

				MultilineLabelWidget label = new MultilineLabelWidget(graphScene, node.getDescription());
				label.setJustified(false);
				label.setPreferredSize(new Dimension(WFNodeWidgetJP.NODE_WIDTH - 20, WFNodeWidgetJP.NODE_HEIGHT - 20));
				w.addChild(label);
			}
		}
	}

	public void addEdge(MWFNodeNext edge) {
		graphScene.addEdge(edge);
		graphScene.setEdgeSource(edge, edge.getAD_WF_Node_ID());
		graphScene.setEdgeTarget(edge, edge.getAD_WF_Next_ID());
	}

	/**
	 *
	 * @param row row #, starting from 1
	 * @param column column #, starting from 1
	 * @return WFNodeWidget
	 */
	public WFNodeWidgetJP findWidget(int row, int column) {
		WFNodeWidgetJP widget = null;
		Integer[] nodeRow = matrix.get(row);
		if (nodeRow != null && column <= nodeRow.length) {
			widget = (WFNodeWidgetJP) graphScene.findWidget(nodeRow[column - 1]);
		}
		return widget;
	}

	/**
	 * 	Get Bounds of WF Node Icon
	 * 	@param AD_WF_Node_ID node id
	 * 	@return bounds of node with ID or null
	 */
	public Rectangle findBounds (int AD_WF_Node_ID)
	{
		WFNodeWidgetJP widget = (WFNodeWidgetJP) graphScene.findWidget(AD_WF_Node_ID);
		if (widget == null)
			return null;

		Point p = widget.getPreferredLocation();
		return new Rectangle(p.x, p.y, WFNodeWidgetJP.NODE_WIDTH, WFNodeWidgetJP.NODE_HEIGHT);
	}	//	findBounds

	public Dimension getDimension()
	{
		return new Dimension(noOfColumns * WFGraphLayoutJP.COLUMN_WIDTH, currentRow * WFGraphLayoutJP.ROW_HEIGHT);
	}

	public void validate(Graphics2D graphics)
	{
		GraphLayout<Integer, MWFNodeNext> graphLayout = new WFGraphLayoutJP();
		graphLayout.setAnimated(false);
		SceneLayout sceneGraphLayout = LayoutFactory.createSceneGraphLayout (graphScene, graphLayout);
		sceneGraphLayout.invokeLayoutImmediately();

		graphScene.validate(graphics);
	}


	public void paint(Graphics2D graphics) {
		graphScene.paint(graphics);
	}

	public int getRowCount() {
		return rowCount;
	}

	public int getCurrentRow() {
		return currentRow;
	}

	public int getCurrentColumn() {
		return currentColumn;
	}

	public int getColumnCount() {
		return noOfColumns;
	}

	public int getMaxColumnWithNode() {
		return maxColumn;
	}

	public GraphScene<Integer, MWFNodeNext> getGraphScene() {
		return graphScene;
	}

	private String getActivityApprovers(MWFActivity wfa)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MWFActivityApprover.COLUMNNAME_AD_WF_Activity_ID+"=? ");
		String orderClause = MWFActivityApprover.COLUMNNAME_AD_User_ID;
		//
		List<MWFActivityApprover> list = new Query(Env.getCtx(), MWFActivityApprover.Table_Name, whereClauseFinal.toString(), null)
										.setParameters(wfa.getAD_WF_Activity_ID())
										.setOrderBy(orderClause)
										.setOnlyActiveRecords(true)
										.list();
		StringBuilder retValue = new StringBuilder();
		if(wfa.getAD_User_ID() > 0)
		{
			retValue = retValue.append(MUser.getNameOfUser(wfa.getAD_User_ID()));
		}

		if(wfa.getAD_WF_Responsible_ID() > 0)
		{
			MWFResponsible responsible = MWFResponsible.get(wfa.getAD_WF_Responsible_ID());
			if(responsible.getResponsibleType().equals(MWFResponsible.RESPONSIBLETYPE_Role))
			{

				MUserRoles[] userRoles =MUserRoles.getOfRole(Env.getCtx(), responsible.getAD_Role_ID());
				for(MUserRoles userRole : userRoles)
				{
					if(Util.isEmpty(retValue.toString()))
					{
						retValue = retValue.append(MUser.getNameOfUser(userRole.getAD_User_ID()));
					}else {
						retValue = retValue.append(" / "+MUser.getNameOfUser(userRole.getAD_User_ID()));
					}
				}
			}

		}

		for(MWFActivityApprover approver : list)
		{
			if(Util.isEmpty(retValue.toString()))
			{
				retValue = retValue.append(MUser.getNameOfUser(approver.getAD_User_ID()));
			}else {
				retValue = retValue.append(" / "+MUser.getNameOfUser(approver.getAD_User_ID()));
			}
		}


		return retValue.toString();
	}
}	//	WFContentPanel
