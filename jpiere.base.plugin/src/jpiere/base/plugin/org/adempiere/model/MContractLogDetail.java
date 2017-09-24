package jpiere.base.plugin.org.adempiere.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MContractLogDetail extends X_JP_ContractLogDetail {
	
	public MContractLogDetail(Properties ctx, int JP_ContractLog_ID, String trxName) 
	{
		super(ctx, JP_ContractLog_ID, trxName);
	}
	
	public MContractLogDetail(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}
	
}
