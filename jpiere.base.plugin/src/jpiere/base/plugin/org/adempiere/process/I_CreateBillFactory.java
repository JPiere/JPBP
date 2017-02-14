package jpiere.base.plugin.org.adempiere.process;


/**
 *
 *
 * @author Hiroshi Iwama
 *
 */

public interface I_CreateBillFactory {

	/**
	 *
	 * @param className
	 * @return matching Callout
	 */
	public I_CreateBill getCreateBill(String className);


}
