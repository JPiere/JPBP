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

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.adempiere.webui.event.WTableModelEvent;
import org.adempiere.webui.event.WTableModelListener;
import org.compiere.model.PO;
import org.zkoss.zul.AbstractListModel;

import jpiere.base.plugin.org.adempiere.model.MAttachmentFileRecord;

/**
*
* JPIERE-0436: JPiere Attachment File
*
*
* @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
*
*/
public class JPiereAttachmentFileRecordListModel extends AbstractListModel<Object> implements TableModelListener {

	private static final long serialVersionUID = 698185856751242764L;
	private JPiereAttachmentFileRecordGridTable tableModel;

	private int pageSize = -1;
	private int pageNo = 0;


	public JPiereAttachmentFileRecordListModel(JPiereAttachmentFileRecordGridTable tableModel)
	{
		this.tableModel = tableModel;
		tableModel.addTableModelListener(this);
	}

	public JPiereAttachmentFileRecordGridTable getJPiereAttachmentFileRecordGridTable()
	{
		return tableModel;
	}


	public Object getElementAt(int rowIndex)
	{
		int columnCount = tableModel.getColumnCount();
		Object[] values = new Object[columnCount];
		if (pageSize > 0) {
			rowIndex = (pageNo * pageSize) + rowIndex;
		}
		if (rowIndex < tableModel.getRowCount()) {
			for (int i = 0; i < columnCount; i++) {
				values[i] = tableModel.getValueAt(rowIndex, i);
			}
		}

		return values;
	}


	public PO getPO(int rowIndex)
	{
		if (pageSize > 0) {
			rowIndex = (pageNo * pageSize) + rowIndex;
		}
		if (rowIndex < tableModel.getRowCount()) {
			return tableModel.getPOs().get(rowIndex);
		}

		return null;
	}


	public int getRowIndexFromID(int po_id)
	{
		ArrayList<MAttachmentFileRecord>  list_POs = tableModel.getPOs();
		int i = 0;
		for(PO po: list_POs)
		{
			if(po.get_ID()==po_id)
			{
				return i;
			}
			i++;
		}
		return -1;
	}

	public void setPO(MAttachmentFileRecord po)
	{
		tableModel.setPO(po);
		WTableModelEvent tcEvent = new WTableModelEvent(this, 0, 0);
		fireTableChange(tcEvent);
	}

	public void removePO(int rowIndex)
	{
		tableModel.removePO(rowIndex);
	}

	public int getSize() {
		int total = tableModel.getRowCount();
		if (pageSize <= 0)
			return total;
		else if ((total - ( pageNo * pageSize)) < 0) {
			pageNo = 0;
			return pageSize > total ? total : pageSize;
		} else {
			int end = (pageNo + 1) * pageSize;
			if (end > total)
				return total - ( pageNo * pageSize);
			else
				return pageSize;
		}
	}

	private void fireTableChange(WTableModelEvent event)
	{
		;
	}

	public void addTableModelListener(WTableModelListener listener)
	{
		;
	}


	public void tableChanged(TableModelEvent e)
	{
		;
	}

}
