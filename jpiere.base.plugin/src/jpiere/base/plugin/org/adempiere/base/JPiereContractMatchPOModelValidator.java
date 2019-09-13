package jpiere.base.plugin.org.adempiere.base;

import java.util.List;
import java.util.logging.Level;

import org.compiere.acct.Fact;
import org.compiere.model.FactsValidator;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MClient;
import org.compiere.model.MMatchPO;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;

/**
 *  JPiere Contract Match PO Model Validator
 *
 *  JPIERE-0363: Contract Management
 *
 *
 *  @author  Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPiereContractMatchPOModelValidator implements ModelValidator,FactsValidator {

	private static CLogger log = CLogger.getCLogger(JPiereContractMatchPOModelValidator.class);
	private int AD_Client_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MMatchPO.Table_Name, this);
		engine.addDocValidate(MMatchPO.Table_Name, this);
		engine.addFactsValidate(MMatchPO.Table_Name, this);

		if (log.isLoggable(Level.FINE)) log.fine("Initialize JPiereContractMatchPOModelValidator");

	}

	@Override
	public int getAD_Client_ID()
	{
		return AD_Client_ID;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID)
	{
		return null;
	}

	@Override
	public String modelChange(PO po, int type) throws Exception
	{
		return null;
	}

	@Override
	public String docValidate(PO po, int timing) {
		return null;
	}

	@Override
	public String factsValidate(MAcctSchema schema, List<Fact> facts, PO po)
	{
		return null;
	}

}
