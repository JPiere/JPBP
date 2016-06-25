package jpiere.base.plugin.org.adempiere.process;

import org.compiere.model.MColumn;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;

public class CreateIDSequence extends SvrProcess {
	
	@Override
	protected void prepare() 
	{

		
	}
	
	@Override
	protected String doIt() throws Exception 
	{
		int increment = 1;
		int minvalue = 1000000;
		int maxvalue = 2147483647;
		int start = 1000000;

		StringBuilder tableNames = new StringBuilder("");
		
		int[] AD_Table_IDs = PO.getAllIDs("AD_Table", "IsActive <> 'N'", get_TrxName());
		
		try{
		
			for(int i = 0; i < AD_Table_IDs.length; i++)
			{
				MTable table = MTable.get(getCtx(), AD_Table_IDs[i], get_TrxName());
				if(table.isView())
					continue;
					
				String tableName_ID = table.getTableName() + "_ID";
				String tableName_SQ = table.getTableName() + "_SQ";
				
				MColumn[] columns = table.getColumns(true);
				boolean isOK = false;
				for(int j = 0; j < columns.length ; j++)
				{
					if(tableName_ID.equalsIgnoreCase(columns[j].getColumnName()))
					{
						isOK = true;
						break;
					}
				}//for j
	
				if(isOK)
				{
					start = DB.getNextID(getAD_Client_ID(), table.getTableName() , get_TrxName());
				
					DB.executeUpdateEx("CREATE SEQUENCE "+tableName_SQ.toUpperCase()
						+ " INCREMENT BY " + increment
						+ " MINVALUE " + minvalue
						+ " MAXVALUE " + maxvalue
						+ " START WITH " + start, get_TrxName());			
				}else{
					addLog(table.getTableName() + " did Not Craete Squence ");
				}
				
			}//for i
			
		}catch(Exception e){
			log.saveError("Error", e);
			addLog(e.getMessage());
			throw e;
		}
		
		return "OK" + tableNames.toString();
	}
	
}
