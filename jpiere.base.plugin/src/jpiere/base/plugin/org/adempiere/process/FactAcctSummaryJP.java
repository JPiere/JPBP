/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
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

package jpiere.base.plugin.org.adempiere.process;

import java.util.logging.Level;

import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;

import jpiere.base.plugin.org.adempiere.model.MReportCubeJP;

/*
 * Populate Fact_Acct_Summary table with pre-calculated totals of
 * accounting facts, grouped by the dimensions selected in active report cubes.
 * @author Paul Bowden
 */
/**
 *
 * JPIERE-0458 JPiere Fact Acct summary
 *
 * @author h.hagiwara
 *
 */
public class FactAcctSummaryJP extends SvrProcess {


	private boolean p_reset = false;
	private int p_Cube_ID = 0;
	private boolean p_force = false;

	@Override
	protected void prepare() {

		ProcessInfoParameter[] params = getParameter();
		for (ProcessInfoParameter p : params)
		{
			if ( p.getParameterName().equals("Reset") )
				p_reset = p.getParameterAsBoolean();
			else if ( p.getParameterName().equals("PA_ReportCubeJP_ID"))
				p_Cube_ID = p.getParameterAsInt();
			else if ( p.getParameterName().equals("Force"))
				p_force = p.getParameterAsBoolean();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + p.getParameterName());
		}
	}

	@Override
	protected String doIt() throws Exception
	{

		MReportCubeJP cube = new MReportCubeJP(getCtx(),p_Cube_ID, get_TrxName());
		cube.update( p_reset, p_force );

		return "@OK@";
	}




}
