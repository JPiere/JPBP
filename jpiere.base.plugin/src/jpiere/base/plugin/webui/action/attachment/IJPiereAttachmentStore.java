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

import jpiere.base.plugin.org.adempiere.model.MAttachmentFileRecord;

/**
*
* JPIERE-0436: JPiere Attachment File
*
*
* @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
*
*/
public interface IJPiereAttachmentStore {

    public boolean upLoadFile(MAttachmentFileRecord attach, byte[] data, MJPiereStorageProvider prov);

    public boolean deleteFile(MAttachmentFileRecord attach, MJPiereStorageProvider prov);

    public StringBuilder getFileAbsolutePath(MAttachmentFileRecord attach, MJPiereStorageProvider prov);

    public StringBuilder getDirectoryAbsolutePath(MAttachmentFileRecord attach, MJPiereStorageProvider prov);

}
