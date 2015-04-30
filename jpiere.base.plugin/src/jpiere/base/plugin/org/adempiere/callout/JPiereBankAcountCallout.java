package jpiere.base.plugin.org.adempiere.callout;

import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MBankAccount;

public class JPiereBankAcountCallout implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue) {

		Integer C_BankAccount_ID = (Integer)value;
		MBankAccount ba = MBankAccount.get(ctx, C_BankAccount_ID);

		mTab.setValue("C_Currency_ID", ba.getC_Currency_ID());

		return null;
	}

}
