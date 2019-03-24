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
package jpiere.base.plugin.webui.action.attachment;

import java.util.ArrayList;
import java.util.List;

import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.adwindow.ADWindow;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ToolBarButton;
import org.adempiere.webui.theme.ThemeManager;
import org.compiere.util.Env;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.RendererCtrl;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.RowRendererExt;

import jpiere.base.plugin.org.adempiere.model.MAttachmentFileRecord;


/**
 *
 * JPIERE-0436: JPiere Attachment File
 *
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class JPiereAttachmntFileRecordRenderer implements RowRenderer<Object[]> ,RowRendererExt, RendererCtrl,EventListener<Event>
{

	private JPiereAttachmentFileRecordListModel listModel;
	private RowListener rowListener;
	private ADWindow adWindow;

	public JPiereAttachmntFileRecordRenderer(JPiereAttachmentFileRecordListModel listModel)
	{
		this.listModel = listModel;
	}

	@Override
	public void onEvent(Event arg0) throws Exception
	{
		;
	}

	@Override
	public void doCatch(Throwable var1) throws Throwable
	{
		;
	}

	@Override
	public void doFinally()
	{
		;
	}

	@Override
	public void doTry()
	{
		;
	}

	@Override
	public int getControls()
	{
		return DETACH_ON_RENDER;
	}

	@Override
	public Component newCell(Row var1)
	{
		return null;
	}

	@Override
	public Row newRow(Grid var1)
	{
		return null;
	}

	Grid grid = null;

	@Override
	public void render(Row row, Object[] data, int index) throws Exception
	{
		if (grid == null)
			grid = (Grid) row.getParent().getParent();

		this.adWindow = listModel.getJPiereAttachmentFileRecordGridTable().getADWindow();

		if (rowListener == null)
			rowListener = new RowListener((Grid)row.getParent().getParent(), adWindow);

		Cell div = null;
		for(int i = 0; i < data.length; i++)
		{
			if(i == 0)
			{
				//DownLoad Button
				div = new Cell();
		    	ToolBarButton btnExport = new ToolBarButton();
		    	btnExport.setAttribute("name","btnExport");
		        if (ThemeManager.isUseFontIconForImage())
		        	btnExport.setIconSclass("z-icon-Export");
		        else
		        	btnExport.setImage(ThemeManager.getThemeResource("images/Export16.png"));
		        btnExport.addEventListener(Events.ON_CLICK, rowListener);
		        btnExport.setId(String.valueOf(row.getIndex())+"_"+String.valueOf(i));//Set RowIndex(Y-axis) and Column(X-axis) in ID of Cell(div)
		        btnExport.setStyle("vertical-align: middle;");
		        if (ThemeManager.isUseFontIconForImage())
		        	LayoutUtils.addSclass("large-toolbarbutton", btnExport);

		        div.appendChild(btnExport);
		        div.setStyle("width:30px;");

			}else if(i == 1){

		    	//Zoom Across Button
				div = new Cell();
		    	ToolBarButton btnZoomAcross = new ToolBarButton();
		    	btnZoomAcross.setAttribute("name","btnEditRecord");
		        if (ThemeManager.isUseFontIconForImage())
		        	btnZoomAcross.setIconSclass("z-icon-Edit");
		        else
		        	btnZoomAcross.setImage(ThemeManager.getThemeResource("images/Editor16.png"));//Editor16.png or EditRecord16.png
		        btnZoomAcross.addEventListener(Events.ON_CLICK, rowListener);
		        btnZoomAcross.setId(String.valueOf(row.getIndex())+"_"+String.valueOf(i));//Set RowIndex(Y-axis) and Column(X-axis) in ID of Cell(div)
		        btnZoomAcross.setStyle("vertical-align: middle;");
		        if (ThemeManager.isUseFontIconForImage())
		        	LayoutUtils.addSclass("large-toolbarbutton", btnZoomAcross);

//		        btnZoomAcross.setDisabled(true);
		        div.appendChild(btnZoomAcross);
		        div.setStyle("width:30px;");

			}else if(i == 2) {

		    	//review
				div = new Cell();
		    	ToolBarButton btnZoomAcross = new ToolBarButton();
		    	btnZoomAcross.setAttribute("name","btnReview");
		        if (ThemeManager.isUseFontIconForImage())
		        	btnZoomAcross.setIconSclass("z-icon-ZoomAcross");
		        else
		        	btnZoomAcross.setImage(ThemeManager.getThemeResource("images/Zoom16.png"));
		        btnZoomAcross.addEventListener(Events.ON_CLICK, rowListener);
		        btnZoomAcross.setId(String.valueOf(row.getIndex())+"_"+String.valueOf(i));//Set RowIndex(Y-axis) and Column(X-axis) in ID of Cell(div)
		        btnZoomAcross.setStyle("vertical-align: middle;");
		        if (ThemeManager.isUseFontIconForImage())
		        	LayoutUtils.addSclass("large-toolbarbutton", btnZoomAcross);

		        div.appendChild(btnZoomAcross);
		        div.setStyle("width:30px;");

			}else if(i == 3) {

				div = new Cell();
				div.appendChild(new Label(data[i].toString()));


			}else if(i == 4) {

		    	//Delete
				div = new Cell();
		    	ToolBarButton btnZoomAcross = new ToolBarButton();
		    	btnZoomAcross.setAttribute("name","btnDelete");
		        if (ThemeManager.isUseFontIconForImage())
		        	btnZoomAcross.setIconSclass("z-icon-Trash");
		        else
		        	btnZoomAcross.setImage(ThemeManager.getThemeResource("images/Delete16.png"));
		        btnZoomAcross.addEventListener(Events.ON_CLICK, rowListener);
		        btnZoomAcross.setId(String.valueOf(row.getIndex())+"_"+String.valueOf(i));//Set RowIndex(Y-axis) and Column(X-axis) in ID of Cell(div)
		        btnZoomAcross.setStyle("vertical-align: middle;");
		        if (ThemeManager.isUseFontIconForImage())
		        	LayoutUtils.addSclass("large-toolbarbutton", btnZoomAcross);

		        div.appendChild(btnZoomAcross);
		        div.setStyle("width:30px;");


			}

			row.appendChild(div);
		}

//		row.setStyle("cursor:pointer");
		row.addEventListener(Events.ON_CLICK, rowListener);
		row.setTooltiptext("Row " + (index+1));

		row.addEventListener(Events.ON_CLICK, rowListener);


	}

	class RowListener implements EventListener<Event>
	{

		private Grid _grid;

		private int rowIndex = 0;
		private int columnIndex = 0;
		private ADWindow adWindow = null;
		private int AD_Table_ID = 0;
		private int  Record_ID = 0;

		public RowListener(Grid grid, ADWindow adWindow) {
			_grid = grid;
			this.adWindow = adWindow;
			this.AD_Table_ID =adWindow.getADWindowContent().getADTab().getSelectedGridTab().getAD_Table_ID();
			this.Record_ID =  adWindow.getADWindowContent().getADTab().getSelectedGridTab().getRecord_ID();

		}

		public int getRowIndex()
		{
			return rowIndex;
		}

		public int getColumnIndex()
		{
			return columnIndex;
		}

		public void setRowIndex(int rowIndex)
		{
			this.rowIndex = rowIndex;
		}

		public void setColumnIndex(int columnIndex)
		{
			this.columnIndex = columnIndex;
		}

		public void onEvent(Event event) throws Exception
		{

			if(event.getTarget() instanceof ToolBarButton)//Get Row Index
			{
				String[] yx = ((ToolBarButton)event.getTarget()).getId().split("_");
				rowIndex =Integer.valueOf(yx[0]).intValue();
	            columnIndex =Integer.valueOf(yx[1]).intValue();


				if (Events.ON_CLICK.equals(event.getName()))
				{
					if(columnIndex == 0)
					{

						;

					}else if(columnIndex == 1) {//Edit Record

						ListModel<Object> model = _grid.getModel();
						Object[] row = (Object[] )model.getElementAt(rowIndex);
						Integer JP_AttachmentFileRecord_ID = (Integer)row[0];
						AEnv.zoom(MAttachmentFileRecord.Table_ID, JP_AttachmentFileRecord_ID.intValue());

					}else if(columnIndex == 2) {

						;

					}else if(columnIndex == 3) {

						;

					}else if(columnIndex == 4) {

						ListModel<Object> model = _grid.getModel();
						Object[] row = (Object[] )model.getElementAt(rowIndex);
						Integer JP_AttachmentFileRecord_ID = (Integer)row[0];
						MAttachmentFileRecord  attachmentFileRecord = new MAttachmentFileRecord(Env.getCtx(),JP_AttachmentFileRecord_ID.intValue(), null);
						if(attachmentFileRecord.get_ID() != 0)
							attachmentFileRecord.deleteEx(true);

						List<Row> rowList = _grid.getRows().getChildren();
						rowList.remove(rowIndex);

						ArrayList<MAttachmentFileRecord>  attachmentFileRecordList = MAttachmentFileRecord.getAttachmentFileRecordPO(Env.getCtx(), AD_Table_ID, Record_ID, true, null);
						JPiereAttachmentFileRecordGridTable AFRGridTable= new JPiereAttachmentFileRecordGridTable(attachmentFileRecordList,adWindow);
						JPiereAttachmentFileRecordListModel listModel = new JPiereAttachmentFileRecordListModel(AFRGridTable);
						grid.setModel(listModel);
//						grid.renderAll();

					}

				}
			}

		}
	}


}
