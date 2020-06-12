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

import java.awt.Font;
import java.awt.GraphicsEnvironment;

import org.compiere.process.SvrProcess;
import org.compiere.util.DB;


/**
 * JPIERE-0467 Font List
 *
 *
 *  @author Hideaki Hagiwara
 *
 */
public class FontList extends SvrProcess {

	@Override
	protected void prepare()
	{
		;
	}

	@Override
	protected String doIt() throws Exception
	{
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    Font fonts[] = ge.getAllFonts();

	    for(Font f : fonts)
	    {
	    	StringBuilder sql = new StringBuilder("INSERT INTO T_FontListJP (AD_PInstance_ID, AD_Client_ID, AD_Org_ID, Name, Name2, Description) ")
	    			.append(" VALUES (").append(getAD_PInstance_ID()).append(",0,0")
	    			.append(",'").append(f.getName()).append("'")
	    			.append(",'").append(f.getFontName()).append("'")
	    			.append(",'").append(f.getPSName()).append("'")
	    			.append(")")
	    			;

	    	DB.executeUpdateEx(sql.toString(), get_TrxName());
	    }

		return "OK";
	}

}
