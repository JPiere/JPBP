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

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import org.adempiere.util.IProcessUI;
import org.compiere.model.MPeriod;
import org.compiere.model.MYear;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MReportCubeJP;

/*
 * Populate Fact_Acct_Summary table with pre-calculated totals of
 * accounting facts, grouped by the dimensions selected in active report cubes.
 * @author Paul Bowden
 */
/**
 *
 * JPIERE-0460 JPiere Fact Acct BS
 *
 * @author h.hagiwara
 *
 */
public class FactAcctBS extends SvrProcess {


	private int p_Cube_ID = 0;
	private int p_C_AcctSchema_ID = 0;
	private int p_C_Calendar_ID = 0;
	private int p_C_Year_ID = 0;
	private int p_C_Period_ID = 0;
	private boolean p_IsDeleteDataOnlyJP = false;

	@Override
	protected void prepare() {

		ProcessInfoParameter[] params = getParameter();
		for (ProcessInfoParameter p : params)
		{
			if ( p.getParameterName().equals("PA_ReportCubeJP_ID"))
				p_Cube_ID = p.getParameterAsInt();
			else if ( p.getParameterName().equals("C_AcctSchema_ID"))
				p_C_AcctSchema_ID = p.getParameterAsInt();
			else if ( p.getParameterName().equals("C_Calendar_ID"))
				p_C_Calendar_ID = p.getParameterAsInt();
			else if ( p.getParameterName().equals("C_Year_ID"))
				p_C_Year_ID = p.getParameterAsInt();
			else if ( p.getParameterName().equals("C_Period_ID"))
				p_C_Period_ID = p.getParameterAsInt();
			else if ( p.getParameterName().equals("IsDeleteDataOnlyJP"))
				p_IsDeleteDataOnlyJP = p.getParameterAsBoolean();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + p.getParameterName());
		}
	}


	private IProcessUI processUI = null;

	@Override
	protected String doIt() throws Exception
	{

		processUI = Env.getProcessUI(getCtx());

		MReportCubeJP cube = new MReportCubeJP(getCtx(),p_Cube_ID, get_TrxName());

		if(processUI!=null)
			processUI.statusUpdate(Msg.getMsg(getCtx(), "Update") + " : " + cube.getName());

		cube.update(false, false);
		commitEx();

		if(p_C_Year_ID == 0)
		{
			calculateYear(cube, p_C_Calendar_ID);
		}
		if(p_C_Period_ID == 0)
		{
			calculatePeriod(cube , p_C_Year_ID);

		}else {

			MPeriod m_Period = MPeriod.get(getCtx(), p_C_Period_ID);
			if(processUI!=null)
				processUI.statusUpdate(m_Period.getName());

			cube.updateBS(p_C_AcctSchema_ID, p_C_Calendar_ID, p_C_Year_ID, m_Period,p_IsDeleteDataOnlyJP);
		}

		return "@OK@";
	}


	private void calculateYear(MReportCubeJP cube, int C_Calendar_ID) throws SQLException
	{
		StringBuilder whereClause = new StringBuilder(MYear.COLUMNNAME_C_Calendar_ID + " =? ");
		StringBuilder orderClause = new StringBuilder(MYear.COLUMNNAME_C_Calendar_ID);
		//
		List<MYear> list = new Query(getCtx(), MYear.Table_Name, whereClause.toString(), get_TrxName())
										.setParameters(C_Calendar_ID)
										.setOrderBy(orderClause.toString())
										.list();

		for(MYear m_Year : list)
		{
			calculatePeriod(cube , m_Year.getC_Year_ID());
		}

	}

	private void calculatePeriod(MReportCubeJP cube, int C_Year_ID) throws SQLException
	{
		StringBuilder whereClause = new StringBuilder(MPeriod.COLUMNNAME_C_Year_ID+"=? ");
		StringBuilder orderClause = new StringBuilder(MPeriod.COLUMNNAME_StartDate);
		//
		List<MPeriod> list = new Query(getCtx(), MPeriod.Table_Name, whereClause.toString(), get_TrxName())
										.setParameters(C_Year_ID)
										.setOrderBy(orderClause.toString())
										.list();

		for(MPeriod m_Period : list)
		{
			if(processUI!=null)
				processUI.statusUpdate(m_Period.getName());

			cube.updateBS(p_C_AcctSchema_ID, p_C_Calendar_ID, p_C_Year_ID, m_Period, p_IsDeleteDataOnlyJP);
			commitEx();
		}

	}

}
