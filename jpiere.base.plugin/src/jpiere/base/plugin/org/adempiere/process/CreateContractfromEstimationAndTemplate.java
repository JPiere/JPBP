package jpiere.base.plugin.org.adempiere.process;

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
import jpiere.base.plugin.org.adempiere.model.MContractProcessList;
import jpiere.base.plugin.org.adempiere.model.MContractProcessRef;
import jpiere.base.plugin.org.adempiere.model.MContractT;
import jpiere.base.plugin.org.adempiere.model.MEstimation;

/**
 * JPIERE-0433: Create Contract From Estimation and Contract Template
 *
 *
 * @author hhagi
 *
 */
public class CreateContractfromEstimationAndTemplate extends AbstractCreateContractFromTemplate {

	private int	p_JP_Estimation_ID = 0;
	private int	p_JP_ContractT_ID = 0;
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
			}else if (name.equals("JP_ContractT_ID")){

				p_JP_ContractT_ID = para[i].getParameterAsInt();

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

	private String createContract()
	{
		m_Contract = new MContract(getCtx(), 0, get_TrxName());
		MContractT contractTemplate = MContractT.get(getCtx(), p_JP_ContractT_ID);
		PO.copyValues(contractTemplate, m_Contract);
		PO.copyValues(estimation, m_Contract);

		m_Contract.setAD_Org_ID(estimation.getAD_Org_ID());
		m_Contract.setJP_ContractT_ID(contractTemplate.getJP_ContractT_ID());
		m_Contract.setC_DocType_ID(contractTemplate.getC_DocType_ID());
		m_Contract.setDateDoc(estimation.getDateOrdered());
		m_Contract.setDateAcct(estimation.getDateAcct());
		m_Contract.setJP_ContractPeriodDate_From(estimation.getDateAcct());

		//Set DocumentNo
		if(contractTemplate.getC_DocType().isDocNoControlled())
			m_Contract.setDocumentNo(null);

		m_Contract.setJP_Estimation_ID(estimation.getJP_Estimation_ID());
		m_Contract.setDocStatus(DocAction.STATUS_Drafted);
		m_Contract.setDocAction(DocAction.ACTION_Complete);
		m_Contract.setJP_ContractStatus(MContract.JP_CONTRACTSTATUS_Prepare);
		try {
			m_Contract.saveEx(get_TrxName());
		}catch (Exception e) {
			return Msg.getMsg(getCtx(), "SaveError") + Msg.getElement(getCtx(), "JP_Contract_ID")+ " >>> "+ e.getMessage();
		}

		MContractContentT[] contractContentTemplates = contractTemplate.getContractContentTemplates();
		for(int i = 0; i < contractContentTemplates.length; i++)
		{

			MContractContent contractContent = new MContractContent(getCtx(), 0, get_TrxName());
			PO.copyValues(contractContentTemplates[i], contractContent);
			PO.copyValues(estimation, contractContent);

			contractContent.setAD_Org_ID(estimation.getAD_Org_ID());
			contractContent.setAD_OrgTrx_ID(estimation.getAD_OrgTrx_ID());
			contractContent.setJP_Contract_ID(m_Contract.getJP_Contract_ID());
			contractContent.setJP_ContractContentT_ID(contractContentTemplates[i].getJP_ContractContentT_ID());
			contractContent.setC_DocType_ID(contractContentTemplates[i].getC_DocType_ID());
			if(contractContent.getC_DocType().isDocNoControlled())
				contractContent.setDocumentNo(null);
			contractContent.setJP_Contract_Acct_ID(contractContentTemplates[i].getJP_Contract_Acct_ID());

			contractContent.setDateDoc(m_Contract.getDateDoc());
			contractContent.setDateAcct(m_Contract.getDateAcct());
			contractContent.setDatePromised(calculateDate(m_Contract.getDateAcct(), contractContentTemplates[i].getDeliveryTime_Promised()));
			contractContent.setDateInvoiced(m_Contract.getDateAcct());
			setContractContentProcDate(contractContent, contractContentTemplates[i]);

			int JP_ContractProcessRef_ID = contractContentTemplates[i].getJP_ContractProcessRef_ID();
			if(JP_ContractProcessRef_ID > 0)
			{
				MContractProcessRef  contractProcessRef = MContractProcessRef.get(getCtx(), JP_ContractProcessRef_ID);
				MContractProcessList[] contractProcessLists = contractProcessRef.getContractProcessList(getCtx(), true, get_TrxName());
				if(contractProcessLists.length==1)
					contractContent.setJP_ContractProcess_ID(contractProcessLists[0].getJP_ContractProcess_ID());
			}


			if(contractContentTemplates[i].getC_BPartner_ID()==0)
			{
				contractContent.setC_BPartner_ID(m_Contract.getC_BPartner_ID());
				contractContent.setC_BPartner_Location_ID(m_Contract.getC_BPartner_Location_ID());
				contractContent.setAD_User_ID(m_Contract.getAD_User_ID());
			}

			contractContent.setTotalLines(Env.ZERO);
			contractContent.setDocStatus(DocAction.STATUS_Drafted);
			contractContent.setDocAction(DocAction.ACTION_Complete);
			contractContent.setJP_ContractProcStatus(MContractContent.JP_CONTRACTPROCSTATUS_Unprocessed);
			contractContent.setC_Currency_ID(contractContent.getM_PriceList().getC_Currency_ID());

			try {
				setWarehouseOfContractContent(contractContentTemplates[i], contractContent);
			} catch (Exception e) {
				return e.getMessage();
			}


			try {

				contractContent.saveEx(get_TrxName());
			} catch (Exception e) {
				return Msg.getMsg(getCtx(), "SaveError") + Msg.getElement(getCtx(), "CopyFrom") + " : "
						+ Msg.getElement(getCtx(), "JP_ContractContentT_ID") + "_" + contractContentTemplates[i].getValue() + " >>> " + e.getMessage();
			}

			try {
				createContractLine(contractContent,contractContentTemplates[i]);
			} catch (Exception e) {
				return Msg.getMsg(getCtx(), "Error") + Msg.getElement(getCtx(), "CopyFrom") + " : "
						+ Msg.getElement(getCtx(), "JP_ContractContentT_ID") + "_" + contractContentTemplates[i].getValue() + " >>> " + e.getMessage();
			}

		}//for i

		estimation.setJP_Contract_ID(m_Contract.getJP_Contract_ID());
		try {
			estimation.saveEx(get_TrxName());
		}catch (Exception e) {
			return Msg.getMsg(getCtx(), "SaveError") + Msg.getElement(getCtx(), "JP_Estimation_ID") + " >>> " + e.getMessage();
		}

		return "";
	}


	protected void createContractLine(MContractContent contractContent, MContractContentT template) throws Exception
	{

		//Create Contract Content Line
		MContractLineT[] m_ContractLineTemplates = template.getContractLineTemplates();
		for(int i = 0; i < m_ContractLineTemplates.length; i++)
		{
			MContractLine contrctLine = new MContractLine(getCtx(), 0, get_TrxName());
			PO.copyValues(m_ContractLineTemplates[i], contrctLine);
			contrctLine.setAD_Org_ID(contractContent.getAD_Org_ID());
			contrctLine.setAD_OrgTrx_ID(contractContent.getAD_OrgTrx_ID());
			contrctLine.setDateOrdered(contractContent.getDateOrdered());
			contrctLine.setDatePromised(calculateDate(contractContent.getDateAcct(), m_ContractLineTemplates[i].getDeliveryTime_Promised())) ;
			contrctLine.setJP_ContractContent_ID(contractContent.getJP_ContractContent_ID());
			contrctLine.setJP_ContractLineT_ID(m_ContractLineTemplates[i].getJP_ContractLineT_ID());

			if(contrctLine.getJP_BaseDocLinePolicy() != null)
			{
				setBaseDocLineProcPeriod(contrctLine, m_ContractLineTemplates[i]);
			}


			int JP_ContractCalRef_InOut_ID = m_ContractLineTemplates[i].getJP_ContractCalRef_InOut_ID();
			if(JP_ContractCalRef_InOut_ID > 0 && !Util.isEmpty(contractContent.getJP_CreateDerivativeDocPolicy()) )
			{
				setDerivativeInOutLineProcPeriod(contrctLine, m_ContractLineTemplates[i]);

			}//if(JP_ContractCalRef_InOut_ID > 0)


			int JP_ContractCalRef_Inv_ID = m_ContractLineTemplates[i].getJP_ContractCalRef_Inv_ID();
			if(JP_ContractCalRef_Inv_ID > 0 && !Util.isEmpty(contractContent.getJP_CreateDerivativeDocPolicy()))
			{
				setDerivativeInvoiceLineProcPeriod(contrctLine, m_ContractLineTemplates[i]);

			}//if(JP_ContractCalRef_Inv_ID > 0)


			int JP_ContractProcRef_InOut_ID = m_ContractLineTemplates[i].getJP_ContractProcRef_InOut_ID();
			if(JP_ContractProcRef_InOut_ID > 0)
			{
				MContractProcessRef  contractProcessRef = MContractProcessRef.get(getCtx(), JP_ContractProcRef_InOut_ID);
				MContractProcessList[] contractProcessLists = contractProcessRef.getContractProcessList(getCtx(), true, get_TrxName());
				if(contractProcessLists.length==1)
				{
					contrctLine.setJP_ContractProcess_InOut_ID(contractProcessLists[0].getJP_ContractProcess_ID());
				}
			}

			int JP_ContractProcRef_Inv_ID = m_ContractLineTemplates[i].getJP_ContractProcRef_Inv_ID();
			if(JP_ContractProcRef_Inv_ID > 0)
			{
				MContractProcessRef  contractProcessRef = MContractProcessRef.get(getCtx(), JP_ContractProcRef_Inv_ID);
				MContractProcessList[] contractProcessLists = contractProcessRef.getContractProcessList(getCtx(), true, get_TrxName());
				if(contractProcessLists.length==1)
				{
					contrctLine.setJP_ContractProcess_Inv_ID(contractProcessLists[0].getJP_ContractProcess_ID());
				}
			}

			try {
				contrctLine.saveEx(get_TrxName());
			}catch (Exception e) {
				throw new Exception(Msg.getMsg(getCtx(), "SaveError") + Msg.getElement(getCtx(), "CopyFrom") + " : "
										+ Msg.getElement(getCtx(), "JP_ContractLineT_ID") + "_" + m_ContractLineTemplates[i].getLine() + " >>> " + e.getMessage() );
			}
		}//For i

	}//createContractLine

}
