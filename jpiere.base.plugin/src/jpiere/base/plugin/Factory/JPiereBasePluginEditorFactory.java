/******************************************************************************
 * Product: JPiere(ジェイピエール) - JPiere Base Plugin                       *
 * Copyright (C) Hideaki Hagiwara All Rights Reserved.                        *
 * このプログラムはGNU Gneral Public Licens Version2のもと公開しています。    *
 * このプログラムは自由に活用してもらう事を期待して公開していますが、         *
 * いかなる保証もしていません。                                               *
 * 著作権は萩原秀明(h.hagiwara@oss-erp.co.jp)が保持し、サポートサービスは     *
 * 株式会社オープンソース・イーアールピー・ソリューションズで                 *
 * 提供しています。サポートをご希望の際には、                                 *
 * 株式会社オープンソース・イーアールピー・ソリューションズまでご連絡下さい。 *
 * http://www.oss-erp.co.jp/                                                  *
 *****************************************************************************/

package jpiere.base.plugin.factory;

import jpiere.base.plugin.org.adempiere.webui.editor.WNumberEditorJP;

import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.factory.IEditorFactory;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.util.DisplayType;

public class JPiereBasePluginEditorFactory implements IEditorFactory {
	
	@Override
	public WEditor getEditor(GridTab gridTab, GridField gridField, boolean tableEditor) {
		if (gridField == null)
        {
            return null;
        }

        WEditor editor = null;
        int displayType = gridField.getDisplayType();

        /** Number */
        if (DisplayType.isNumeric(displayType))
        {
            editor = new WNumberEditorJP(tableEditor, gridField);
        }
		
        if(editor != null)
	        editor.setTableEditor(tableEditor);

        return editor;
	}
	
}
