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

import org.compiere.util.Env;
import org.compiere.wf.MWFNode;
import org.compiere.wf.MWFNodeNext;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.layout.LayoutFactory.ConnectionWidgetLayoutAlignment;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;

/**
 * JPIERE-0516 - Workflow Editor
 *
 * Ref: org.compiere.apps.wf.WorkflowGraphScene
 *
 * @author Low Heng Sin
 * @author h.hagiwara
 *
 */
public class WFGraphSceneJP extends GraphScene<Integer, MWFNodeNext> {

	private LayerWidget mainLayer;
    private LayerWidget connectionLayer;

    private WidgetAction selectAction = createSelectAction();

    public WFGraphSceneJP() {
    	mainLayer = new LayerWidget (this);
    	mainLayer.setBackground(new Color(255,255,255,0));
        connectionLayer = new LayerWidget (this);
        connectionLayer.setBackground(new Color(255,255,255,0));
        addChild (mainLayer);
        addChild (connectionLayer);
        this.setBackground(new Color(255,255,255,0));
    }

	@Override
	protected void attachEdgeSourceAnchor(MWFNodeNext edge, Integer oldsource,
			Integer sourceNode) {
		((ConnectionWidget) findWidget (edge)).setSourceAnchor (AnchorFactory.createRectangularAnchor (findWidget (sourceNode)));
	}

	@Override
	protected void attachEdgeTargetAnchor(MWFNodeNext edge, Integer oldtarget,
			Integer targetNode) {
		((ConnectionWidget) findWidget (edge)).setTargetAnchor (AnchorFactory.createRectangularAnchor (findWidget (targetNode)));
	}

	@Override
	protected Widget attachEdgeWidget(MWFNodeNext edge) {
		 ConnectionWidget connection = new ConnectionWidget (this);
		 connection.setTargetAnchorShape (AnchorShape.TRIANGLE_FILLED);
		 connection.setRouter (RouterFactory.createOrthogonalSearchRouter (mainLayer, connectionLayer));
		 connection.setRoutingPolicy (ConnectionWidget.RoutingPolicy.ALWAYS_ROUTE);

		 String description = edge.getDescription();
		 if (description != null && description.length() > 0) {
			 description = "{" + String.valueOf(edge.getSeqNo())
						+ ": " + description + "}";
			 LabelWidget label = new LabelWidget(this, description);
			 connection.addChild(label);
			 connection.setConstraint (label, ConnectionWidgetLayoutAlignment.TOP_CENTER, 0.5f);
		 }

	     connectionLayer.addChild (connection);
	     return connection;
	}

	@Override
	protected Widget attachNodeWidget(Integer node)
	{
		WFNodeWidgetJP widget = (WFNodeWidgetJP) findWidget(node);
		if (widget == null) {
			widget = new WFNodeWidgetJP(this, MWFNode.getCopy(Env.getCtx(), node, null));
			widget.getActions ().addAction (selectAction);
			mainLayer.addChild (widget);
		}
		return widget;
	}

}
