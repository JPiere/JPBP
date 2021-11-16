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

import java.awt.Point;
import java.util.Collection;

import org.compiere.wf.MWFNodeNext;
import org.netbeans.api.visual.graph.layout.GraphLayout;
import org.netbeans.api.visual.graph.layout.UniversalGraph;

/**
 * JPIERE-0516 - Workflow Editor
 *
 * Ref: org.compiere.apps.wf.WFGraphLayout
 *
 * @author Low Heng Sin
 * @author h.hagiwara
 *
 */
public class WFGraphLayoutJP extends GraphLayout<Integer, MWFNodeNext> {

	public final static int COLUMN_WIDTH = 240;//184
	public final static int ROW_HEIGHT = 133;

	@Override
	protected void performGraphLayout(UniversalGraph<Integer, MWFNodeNext> graph) {
		Collection<Integer> nodes = graph.getNodes();
		performNodesLayout(graph, nodes);
	}

	@Override
	protected void performNodesLayout(UniversalGraph<Integer, MWFNodeNext> graph,
			Collection<Integer> nodes) {

		for(Integer node : nodes) {
			WFNodeWidgetJP widget = (WFNodeWidgetJP) graph.getScene().findWidget(node);
			int x = (widget.getColumn() - 1) * COLUMN_WIDTH;
			int y = (widget.getRow() - 1) * ROW_HEIGHT;
			Point point = new Point(x, y);
			setResolvedNodeLocation(graph, node, point);
			widget.setPreferredLocation(point);
		}
	}

}

