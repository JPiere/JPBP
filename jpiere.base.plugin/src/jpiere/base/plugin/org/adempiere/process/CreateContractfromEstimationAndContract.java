package jpiere.base.plugin.org.adempiere.process;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.logging.Level;

import org.adempiere.util.Callback;
import org.adempiere.util.IProcessUI;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractContentT;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractLineT;
import jpiere.base.plugin.org.adempiere.model.MEstimation;

/**
 * JPIERE-0434: Create New Contract From Estimation and  Existing Contract
 * JPIERE-0444: Create New Contract Content From Estimation and Existing Contract
 *
 * @author Hideaki Hagiwara
 *
 */
public class CreateContractfromEstimationAndContract extends AbstractCreateContractByCopy {

	private int	p_JP_Estimation_ID = 0;
	private int	from_JP_Contract_ID = 0;
	private int	from_JP_ContractContent_ID = 0;
	private int	p_JP_CreateTo_Contract_ID = 0;

	private MEstimation 	estimation = null;
	private IProcessUI 		processUI = null;
	private boolean 		isCreateSO = false;
	private boolean 		isOpenDialog = false;
	private boolean 		isAskAnswer = true;
	private String 			errorMsg = "";
	private String 			returnMsg = "";

	@Override
	protected void prepare()
	{
		p_JP_Estimation_ID = getRecord_ID();

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("JP_CreateTo_Contract_ID")){

				p_JP_CreateTo_Contract_ID = para[i].getParameterAsInt();

			}else if (name.equals("JP_CopyFrom_Contract_ID")){

				from_JP_Contract_ID = para[i].getParameterAsInt();

			}else if (name.equals("JP_CopyFrom_ContractContent_ID")){

				from_JP_ContractContent_ID = para[i].getParameterAsInt();

			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if

		}//for

		processUI = Env.getProcessUI(getCtx());
		estimation = new MEstimation(getCtx(), p_JP_Estimation_ID, get_TrxName()) ;

	}

	@Override
	protected String doIt() throws Exception
	{
		//PreCheck
		if(estimation.getC_BPartner_ID() == 0)
		{
			errorMsg = errorMsg + Msg.getMsg(getCtx(), "FillMandatory") + " : " + Msg.getElement(getCtx(), "C_BPartner_ID") + System.lineSeparator();
		}

		if(estimation.getC_BPartner_Location_ID() == 0)
		{
			errorMsg = errorMsg + Msg.getMsg(getCtx(), "FillMandatory") + " : " + Msg.getElement(getCtx(), "C_BPartner_Location_ID")+ System.lineSeparator();
		}

		if(!Util.isEmpty(errorMsg))
		{
			throw new Exception(errorMsg);
		}


		MContract[] contracts = MContract.getContractByEstimation(getCtx(), p_JP_Estimation_ID, get_TrxName());

		if(processUI != null && contracts.length > 0)
		{
			isOpenDialog = true;
			//Already Contract created, Do you want to create Contract again?
			processUI.ask("JP_CreateContractFromEstimationAgain", new Callback<Boolean>() {

				@Override
				public void onCallback(Boolean result)
				{
					if (result)
					{
						try {
							returnMsg = createContract();
						}catch (Exception e) {
							returnMsg = e.getMessage();
						}finally {
							isCreateSO = true;
						}

					}else{

						isAskAnswer = false;

					}
		        }

			});//FDialog.

		}else{

			returnMsg = createContract();
			isCreateSO = true;

		}

		while (isOpenDialog && isAskAnswer && !isCreateSO)
		{
			Thread.sleep(1000*2);
		}

		if(!Util.isEmpty(returnMsg))
		{
			throw new Exception(returnMsg);
		}

		if(isCreateSO)
			addBufferLog(0, null, null, m_Contract.getDocumentNo(), MContract.Table_ID, m_Contract.getJP_Contract_ID());

		return "";//Msg.getMsg(getCtx(), "Success");

	}

	private String createContract()  throws Exception
	{
		MContract from_Contract = MContract.get(getCtx(), from_JP_Contract_ID);
		MContract to_Contract = null;

		if(p_JP_CreateTo_Contract_ID == 0) // Create Contract Document and Contract Content.
		{
			to_Contract = new MContract(getCtx(), 0, get_TrxName());
			PO.copyValues(from_Contract, to_Contract);
			PO.copyValues(estimation, to_Contract);
			to_Contract.setJP_Contract_Link_ID(from_Contract.getJP_Contract_ID());
			to_Contract.setAD_Org_ID(estimation.getAD_Org_ID());
			to_Contract.setJP_ContractT_ID(from_Contract.getJP_ContractT_ID());
			to_Contract.setC_DocType_ID(from_Contract.getC_DocType_ID());
			to_Contract.setDateDoc(estimation.getDateOrdered());
			to_Contract.setDateAcct(estimation.getDateAcct());
			to_Contract.setJP_ContractPeriodDate_From(estimation.getDateAcct());
			if(from_Contract.getJP_ContractPeriodDate_To() != null)
			{
				LocalDateTime from_Date = from_Contract.getJP_ContractPeriodDate_From().toLocalDateTime();
				LocalDateTime to_Date = from_Contract.getJP_ContractPeriodDate_To().toLocalDateTime();
				Duration duration = Duration.between(from_Date, to_Date);
				to_Contract.setJP_ContractPeriodDate_To(calculateDate(to_Contract.getJP_ContractPeriodDate_From(), (int)duration.toDays()));

			}else {
				to_Contract.setJP_ContractPeriodDate_To(null);
			}

			//Set DocumentNo
			if(from_Contract.getC_DocType().isDocNoControlled())
				to_Contract.setDocumentNo(null);

			to_Contract.setJP_Estimation_ID(estimation.getJP_Estimation_ID());
			to_Contract.setDocStatus(DocAction.STATUS_Drafted);
			to_Contract.setDocAction(DocAction.ACTION_Complete);
			to_Contract.setJP_ContractStatus(MContract.JP_CONTRACTSTATUS_Prepare);
			try {
				to_Contract.saveEx(get_TrxName());
			}catch (Exception e) {
				return Msg.getMsg(getCtx(), "SaveError") + Msg.getElement(getCtx(), "JP_Contract_ID")+ " >>> "+ e.getMessage();
			}

		}else {//Create Contract Content, Not create Contract Document.

			to_Contract = new MContract(getCtx(), p_JP_CreateTo_Contract_ID, get_TrxName());

			if(to_Contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_GeneralContract))
			{
				//General Contract can not have Contract Content.
				throw new Exception(Msg.getMsg(getCtx(), "JP_GeneralContract_NotHave_ContractContent"));
			}

			if(!to_Contract.getJP_ContractType().equals(from_Contract.getJP_ContractType()))
			{
				//Different between {0} and {1}
				String msg0 = Msg.getElement(Env.getCtx(), "JP_CreateTo_Contract_ID")+" - " + Msg.getElement(Env.getCtx(), "JP_ContractType");
				String msg1 = Msg.getElement(Env.getCtx(), "JP_CopyFrom_Contract_ID")+" - " + Msg.getElement(Env.getCtx(), "JP_ContractType");
				return Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1});
			}
		}


		MContractContent[]  from_ContractContents = from_Contract.getContractContents();
		//Create Contract Content
		for(int i = 0 ; i < from_ContractContents.length; i++)
		{
			if(from_JP_ContractContent_ID != 0 && from_JP_ContractContent_ID != from_ContractContents[i].getJP_ContractContent_ID())
				continue;

			MContractContent to_ContractContent = new MContractContent(getCtx(), 0, get_TrxName());
			PO.copyValues(from_ContractContents[i], to_ContractContent);
			to_ContractContent.setAD_Org_ID(to_Contract.getAD_Org_ID());
			to_ContractContent.setAD_OrgTrx_ID(to_Contract.getAD_OrgTrx_ID());
			to_ContractContent.setJP_Contract_ID(to_Contract.get_ID());
			to_ContractContent.setJP_ContractContentT_ID(from_ContractContents[i].getJP_ContractContentT_ID());
			to_ContractContent.setJP_Contract_Acct_ID(from_ContractContents[i].getJP_Contract_Acct_ID());
			if(to_ContractContent.getC_DocType().isDocNoControlled())
				to_ContractContent.setDocumentNo(null);

			to_ContractContent.setDateDoc(to_Contract.getDateDoc());
			to_ContractContent.setDateAcct(to_Contract.getDateAcct());
			to_ContractContent.setDatePromised(calculateDate(to_Contract.getDateAcct(), from_ContractContents[i].getJP_ContractContentT().getDeliveryTime_Promised())) ;
			to_ContractContent.setDateInvoiced(from_ContractContents[i].getDateInvoiced());
			setContractContentProcDate(to_ContractContent, MContractContentT.get(getCtx(), from_ContractContents[i].getJP_ContractContentT_ID()) );

			to_ContractContent.setJP_Estimation_ID(estimation.getJP_Estimation_ID());
			to_ContractContent.setTotalLines(Env.ZERO);
			to_ContractContent.setDocStatus(DocAction.STATUS_Drafted);
			to_ContractContent.setDocAction(DocAction.ACTION_Complete);
			to_ContractContent.setIsScheduleCreatedJP(false);
			to_ContractContent.setJP_ContractProcStatus(MContractContent.JP_CONTRACTPROCSTATUS_Unprocessed);

			setDocumentNoOfContractContent(from_ContractContents[i], to_ContractContent);
			setBPartnerOfContractContent(from_ContractContents[i], to_ContractContent);

			try {
				setWarehouseOfContractContent(from_ContractContents[i], to_ContractContent);
			} catch (Exception e) {
				return e.getMessage();
			}

			to_ContractContent.setC_Currency_ID(to_ContractContent.getM_PriceList().getC_Currency_ID());

			try {
				to_ContractContent.saveEx(get_TrxName());
			} catch (Exception e) {
				return Msg.getMsg(getCtx(), "SaveError") + Msg.getElement(getCtx(), "CopyFrom") + " : "
										+ Msg.getElement(getCtx(), "JP_ContractContent_ID") + "_" + from_ContractContents[i].getDocumentNo() + " >>> " + e.getMessage();
			}

			try {
				createContractLine(to_ContractContent, from_ContractContents[i], true);
			} catch (Exception e) {
				return Msg.getMsg(getCtx(), "Error") + Msg.getElement(getCtx(), "CopyFrom") + " : "
						+ Msg.getElement(getCtx(), "JP_ContractContent_ID") + "_" + from_ContractContents[i].getDocumentNo() + " >>> " + e.getMessage();
			}

		}//For i

		m_Contract = to_Contract;

		estimation.setJP_Contract_ID(m_Contract.getJP_Contract_ID());
		try {
			estimation.saveEx(get_TrxName());
		}catch (Exception e) {
			return Msg.getMsg(getCtx(), "SaveError") + Msg.getElement(getCtx(), "JP_Estimation_ID") + " >>> " + e.getMessage();
		}

		return "";
	}


	protected void createContractLine(MContractContent to_ContractContent, MContractContent from_ContractContent, boolean isReSetPeriod) throws Exception
	{

		//Create Contract Content Line
		MContractLine[] from_ContractLines = from_ContractContent.getLines();
		for(int i = 0; i < from_ContractLines.length; i++)
		{
			MContractLine to_ContractLine = new MContractLine(getCtx(), 0, get_TrxName());
			PO.copyValues(from_ContractLines[i], to_ContractLine);
			to_ContractLine.setAD_Org_ID(to_ContractContent.getAD_Org_ID());
			to_ContractLine.setAD_OrgTrx_ID(to_ContractContent.getAD_OrgTrx_ID());
			to_ContractLine.setDateOrdered(to_ContractContent.getDateOrdered());
			to_ContractLine.setDatePromised(calculateDate(to_ContractContent.getDateAcct(), from_ContractLines[i].getJP_ContractLineT().getDeliveryTime_Promised())) ;
			to_ContractLine.setJP_ContractContent_ID(to_ContractContent.getJP_ContractContent_ID());
			to_ContractLine.setJP_ContractLineT_ID(from_ContractLines[i].getJP_ContractLineT_ID());

			if(isReSetPeriod)
			{
				setBaseDocLineProcPeriod(to_ContractLine, MContractLineT.get(getCtx(), to_ContractLine.getJP_ContractLineT_ID()));
				setDerivativeInOutLineProcPeriod(to_ContractLine, MContractLineT.get(getCtx(), to_ContractLine.getJP_ContractLineT_ID()));
				setDerivativeInvoiceLineProcPeriod(to_ContractLine, MContractLineT.get(getCtx(), to_ContractLine.getJP_ContractLineT_ID()));
			}

			try
			{
				to_ContractLine.saveEx(get_TrxName());
			}catch (Exception e) {
				throw new Exception(Msg.getMsg(getCtx(), "SaveError") + Msg.getElement(getCtx(), "CopyFrom") + " : "
										+ Msg.getElement(getCtx(), "JP_ContractLine_ID") + "_" + from_ContractLines[i].getLine() + " >>> " + e.getMessage() );
			}

		}//For i

	}//createContractLine

}
