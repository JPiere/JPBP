package jpiere.base.plugin.org.adempiere.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MInvValAdjustLine extends X_JP_InvValAdjustLine {
	
	public MInvValAdjustLine(Properties ctx, int JP_InvValAdjustLine_ID, String trxName)
	{
		super(ctx, JP_InvValAdjustLine_ID, trxName);
	}
	
	public MInvValAdjustLine(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}
	
	public MInvValAdjustLine (MInvValAdjust invValAdjust)
	{
		this (invValAdjust.getCtx(), 0, invValAdjust.get_TrxName());
		if (invValAdjust.get_ID() == 0)
			throw new IllegalArgumentException("Header not saved");
		setJP_InvValAdjust_ID(invValAdjust.getJP_InvValAdjust_ID());	//	parent
		setAD_Org_ID(invValAdjust.getAD_Org_ID());
	}
	
}
