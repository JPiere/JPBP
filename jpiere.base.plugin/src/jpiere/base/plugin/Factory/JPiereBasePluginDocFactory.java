/******************************************************************************
 * Product: JPiere Plugin Bank Statement Tax
 * Copyright (C) 2014 Hideaki Hagiwara(OSS ERP Solutions)
 *****************************************************************************/
package jpiere.base.plugin.factory;

import java.lang.reflect.Constructor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.adempiere.base.IDocFactory;
import org.compiere.acct.Doc;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MBankStatement;
import org.compiere.util.CLogger;
import org.compiere.util.DB;

/**
 *  JPiere Bank Statement Tax Doc Factory
 *
 *  @author Hideaki Hagiwara
 *  @version  $Id: Doc_JPiereBankStatementTaxDocFactory.java,v 1.0 2014/08/20
 *
 */
public class JPiereBasePluginDocFactory implements IDocFactory {

	private final static CLogger s_log = CLogger.getCLogger(JPiereBasePluginDocFactory.class);

	@Override
	public Doc getDocument(MAcctSchema as, int AD_Table_ID, int Record_ID,
			String trxName) {

		Doc doc = null;
		if(AD_Table_ID==MBankStatement.Table_ID){//392

			String tableName = MBankStatement.Table_Name;
			StringBuffer sql = new StringBuffer("SELECT * FROM ")
				.append(tableName)
				.append(" WHERE ").append(tableName).append("_ID=? AND Processed='Y'");
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement (sql.toString(), trxName);
				pstmt.setInt (1, Record_ID);
				rs = pstmt.executeQuery ();
				if (rs.next ())
				{
					doc = getDocument(as, AD_Table_ID, rs, trxName);
				}
				else
					s_log.severe("Not Found: " + tableName + "_ID=" + Record_ID);
			}
			catch (Exception e)
			{
				s_log.log (Level.SEVERE, sql.toString(), e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}
		}

		return doc;
	}

	@Override
	public Doc getDocument(MAcctSchema as, int AD_Table_ID, ResultSet rs,
			String trxName) {
		Doc doc = null;

		String className = null;

		if(AD_Table_ID == MBankStatement.Table_ID){//392
			className = "jpiere.base.plugin.org.compiere.acct.Doc_JPiereBankStatementTax";
		}else {
			return null;
		}


		try
		{
			Class<?> cClass = Class.forName(className);
			Constructor<?> cnstr = cClass.getConstructor(new Class[] {MAcctSchema.class, ResultSet.class, String.class});
			doc = (Doc) cnstr.newInstance(as, rs, trxName);
		}
		catch (Exception e)
		{
			doc = null;
		}

		return doc;
	}

}
