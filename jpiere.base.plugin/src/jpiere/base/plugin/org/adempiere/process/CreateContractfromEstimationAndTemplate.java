package jpiere.base.plugin.org.adempiere.process;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.adempiere.util.Callback;
import org.adempiere.util.IProcessUI;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.util.DB;
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
import jpiere.base.plugin.org.adempiere.model.MEstimationLine;

/**
 * JPIERE-0433: Create Contract From Estimation and Contract Template
 * JPIERE-0443: Create Contract Content From Estimation and Contract Template
 *
 * @author Hideaki Hagiwara
 *
 */
public class CreateContractfromEstimationAndTemplate extends AbstractCreateContractFromTemplate {

	private int	p_JP_Estimation_ID = 0;
	private int	p_JP_ContractT_ID = 0;
	private int	p_JP_ContractContentT_ID = 0;
	private String p_JP_ContractLineCreatePolicy = null;
	private int	p_JP_Para_BPartner_ID = 0;
	private int	p_JP_Para_BPartner_Location_ID = 0;
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

			}else if (name.equals("JP_ContractT_ID")){

				p_JP_ContractT_ID = para[i].getParameterAsInt();

			}else if (name.equals("JP_ContractContentT_ID")){

				p_JP_ContractContentT_ID = para[i].getParameterAsInt();

			}else if (name.equals("JP_ContractLineCreatePolicy")){

				p_JP_ContractLineCreatePolicy = para[i].getParameterAsString();

			}else if (name.equals("JP_Para_BPartner_ID")){

				p_JP_Para_BPartner_ID = para[i].getParameterAsInt();

			}else if (name.equals("JP_Para_BPartner_Location_ID")){

				p_JP_Para_BPartner_Location_ID = para[i].getParameterAsInt();

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
		if(p_JP_Para_BPartner_ID == 0 && estimation.getC_BPartner_ID() == 0)
		{
			errorMsg = errorMsg + Msg.getMsg(getCtx(), "FillMandatory") + " : " + Msg.getElement(getCtx(), "C_BPartner_ID") + System.lineSeparator();
		}

		if(estimation.getC_BPartner_ID() == 0)
			estimation.setC_BPartner_ID(p_JP_Para_BPartner_ID);

		if(p_JP_Para_BPartner_Location_ID == 0 && estimation.getC_BPartner_Location_ID() == 0)
		{
			errorMsg = errorMsg + Msg.getMsg(getCtx(), "FillMandatory") + " : " + Msg.getElement(getCtx(), "C_BPartner_Location_ID")+ System.lineSeparator();
		}

		if(estimation.getC_BPartner_Location_ID() == 0)
			estimation.setC_BPartner_Location_ID(p_JP_Para_BPartner_Location_ID);

		if(!Util.isEmpty(errorMsg))
		{
			throw new Exception(errorMsg);
		}


		MContract[] contracts = MContract.getContractByEstimation(getCtx(), p_JP_Estimation_ID, get_TrxName());
		MContractContent[] contractContents = MContractContent.getContractContentByEstimation(getCtx(), p_JP_CreateTo_Contract_ID, p_JP_Estimation_ID, get_TrxName());


		if( (p_JP_CreateTo_Contract_ID == 0 && processUI != null && contracts.length > 0) || (p_JP_CreateTo_Contract_ID > 0 && processUI != null && contractContents.length > 0))
		{
			isOpenDialog = true;

			String msg = null;
			if(p_JP_CreateTo_Contract_ID == 0)
			{
				//Already Contract was created from this Estimation, Do you want to create Contract again?
				msg = "JP_CreateContractFromEstimationAgain";
			}else {

				//Already Contract Content was created from this Estimation, Do you want to create Contract Content again?
				msg = "JP_CreateContracContentFromEstimationAgain";

			}

			processUI.ask(msg, new Callback<Boolean>() {

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

	/**
	 * Create Contract
	 *
	 * @return
	 * @throws Exception
	 */
	private String createContract() throws Exception
	{
		MContractT contractTemplate = MContractT.get(getCtx(), p_JP_ContractT_ID);

		if(p_JP_CreateTo_Contract_ID == 0) // Create Contract Document and Contract Content.
		{
			m_Contract = new MContract(getCtx(), 0, get_TrxName());
			PO.copyValues(contractTemplate, m_Contract);
			PO.copyValues(estimation, m_Contract);

			m_Contract.setAD_Org_ID(estimation.getAD_Org_ID());
			m_Contract.setJP_ContractT_ID(contractTemplate.getJP_ContractT_ID());
			m_Contract.setC_DocType_ID(contractTemplate.getC_DocType_ID());
			m_Contract.setDateDoc(estimation.getDateOrdered());
			m_Contract.setDateAcct(estimation.getDateAcct());
			m_Contract.setJP_ContractPeriodDate_From(estimation.getDateAcct());
			if(p_JP_Para_BPartner_ID != 0)
				m_Contract.setC_BPartner_ID(p_JP_Para_BPartner_ID);
			if(p_JP_Para_BPartner_Location_ID != 0)
				m_Contract.setC_BPartner_Location_ID(p_JP_Para_BPartner_Location_ID);

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

		}else { //Create Contract Content, Not create Contract Document.

			m_Contract = new MContract(getCtx(), p_JP_CreateTo_Contract_ID, get_TrxName());

			if(m_Contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_GeneralContract))
			{
				//General Contract can not have Contract Content.
				throw new Exception(Msg.getMsg(getCtx(), "JP_GeneralContract_NotHave_ContractContent"));
			}

			if(!m_Contract.getJP_ContractType().equals(MContractContentT.get(getCtx(), p_JP_ContractContentT_ID).getJP_ContractType()))
			{
				//Different between {0} and {1}
				String msg0 = Msg.getElement(Env.getCtx(), "JP_CreateTo_Contract_ID")+" - " + Msg.getElement(Env.getCtx(), "JP_ContractType");
				String msg1 = Msg.getElement(Env.getCtx(), "JP_ContractContentT_ID")+" - " + Msg.getElement(Env.getCtx(), "JP_ContractType");
				return Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1});
			}
		}



		if(p_JP_ContractContentT_ID == 0)
		{
			MContractContentT[] contractContentTemplates = contractTemplate.getContractContentTemplates();
			for(int i = 0; i < contractContentTemplates.length; i++)
			{
				try {
					createContractContent(contractContentTemplates[i]);
				} catch (Exception e) {
					return e.getMessage();
				}

			}//for i

		}else {

			MContractContentT contractContentTemplate = MContractContentT.get(getCtx(), p_JP_ContractContentT_ID);
			if(contractContentTemplate.getJP_ContractContentT_ID() == 0)
			{
				return Msg.getMsg(getCtx(), "NotFound") + "  " + Msg.getElement(getCtx(), "JP_ContractContentT_ID");
			}

			if(!contractContentTemplate.isSOTrx())
			{
				return Msg.getMsg(getCtx(), "Invalid") + "  " + Msg.getMsg(getCtx(), "JP_Purchase_Contract") + " - " + Msg.getElement(getCtx(), "JP_ContractContentT_ID");
			}

			try {
				createContractContent(contractContentTemplate);
			} catch (Exception e) {
				return e.getMessage();
			}
		}

		estimation.setJP_Contract_ID(m_Contract.getJP_Contract_ID());
		try {
			estimation.saveEx(get_TrxName());
		}catch (Exception e) {
			return Msg.getMsg(getCtx(), "SaveError") + Msg.getElement(getCtx(), "JP_Estimation_ID") + " >>> " + e.getMessage();
		}

		return "";
	}

	/**
	 *
	 * Create Contract Content
	 *
	 * @param contractContentTemplate
	 * @throws Exception
	 */
	protected void createContractContent(MContractContentT contractContentTemplate) throws Exception
	{
		MContractContent contractContent = new MContractContent(getCtx(), 0, get_TrxName());
		PO.copyValues(contractContentTemplate, contractContent);
		PO.copyValues(estimation, contractContent);

		contractContent.setJP_Estimation_ID(estimation.getJP_Estimation_ID());

		contractContent.setAD_Org_ID(estimation.getAD_Org_ID());
		contractContent.setAD_OrgTrx_ID(estimation.getAD_OrgTrx_ID());
		contractContent.setJP_Contract_ID(m_Contract.getJP_Contract_ID());
		contractContent.setJP_ContractContentT_ID(contractContentTemplate.getJP_ContractContentT_ID());
		contractContent.setC_DocType_ID(contractContentTemplate.getC_DocType_ID());
		if(contractContent.getC_DocType().isDocNoControlled())
			contractContent.setDocumentNo(null);
		contractContent.setJP_Contract_Acct_ID(contractContentTemplate.getJP_Contract_Acct_ID());

		if(contractContentTemplate.getC_BPartner_ID() == 0)
		{
			if(p_JP_Para_BPartner_ID != 0)
			{
				contractContent.setC_BPartner_ID(p_JP_Para_BPartner_ID);
				contractContent.setC_BPartner_Location_ID(p_JP_Para_BPartner_Location_ID);
			}else {
				contractContent.setC_BPartner_ID(m_Contract.getC_BPartner_ID());
				contractContent.setC_BPartner_Location_ID(m_Contract.getC_BPartner_Location_ID());
				contractContent.setAD_User_ID(m_Contract.getAD_User_ID());
			}
		}

		contractContent.setDateDoc(m_Contract.getDateDoc());
		contractContent.setDateAcct(m_Contract.getDateAcct());
		contractContent.setDatePromised(calculateDate(m_Contract.getDateAcct(), contractContentTemplate.getDeliveryTime_Promised()));
		contractContent.setDateInvoiced(m_Contract.getDateAcct());
		setContractContentProcDate(contractContent, contractContentTemplate);

		int JP_ContractProcessRef_ID = contractContentTemplate.getJP_ContractProcessRef_ID();
		if(JP_ContractProcessRef_ID > 0)
		{
			MContractProcessRef  contractProcessRef = MContractProcessRef.get(getCtx(), JP_ContractProcessRef_ID);
			MContractProcessList[] contractProcessLists = contractProcessRef.getContractProcessList(getCtx(), true, get_TrxName());
			if(contractProcessLists.length==1)
				contractContent.setJP_ContractProcess_ID(contractProcessLists[0].getJP_ContractProcess_ID());
		}

		contractContent.setTotalLines(Env.ZERO);
		contractContent.setDocStatus(DocAction.STATUS_Drafted);
		contractContent.setDocAction(DocAction.ACTION_Complete);
		contractContent.setJP_ContractProcStatus(MContractContent.JP_CONTRACTPROCSTATUS_Unprocessed);
		contractContent.setC_Currency_ID(contractContent.getM_PriceList().getC_Currency_ID());

		try {
			setWarehouseOfContractContent(contractContentTemplate, contractContent);
		} catch (Exception e) {
			throw e;
		}


		try {

			contractContent.saveEx(get_TrxName());
		} catch (Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "SaveError") + Msg.getElement(getCtx(), "CopyFrom") + " : "
					+ Msg.getElement(getCtx(), "JP_ContractContentT_ID") + "_" + contractContentTemplate.getValue() + " >>> " + e.getMessage());
		}

		try {

			if(p_JP_ContractLineCreatePolicy.equals("EL") && p_JP_ContractContentT_ID > 0)
			{
				createContractLineFromEstimation(contractContent,contractContentTemplate);
			}else {
				createContractLineFromTemplate(contractContent,contractContentTemplate);
			}

		} catch (Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + Msg.getElement(getCtx(), "CopyFrom") + " : "
					+ Msg.getElement(getCtx(), "JP_ContractContentT_ID") + "_" + contractContentTemplate.getValue() + " >>> " + e.getMessage());
		}
	}

	/**
	 *
	 * Create Contract Content Line From Estimation Line
	 *
	 * @param contractContent
	 * @param template
	 * @throws Exception
	 */
	protected void createContractLineFromEstimation(MContractContent contractContent, MContractContentT contractContentTemplate) throws Exception
	{
		MEstimationLine[] eLines = estimation.getLines(true,null);
		for(int i = 0; i < eLines.length; i++)
		{
			MContractLine contractLine = new MContractLine(getCtx(), 0, get_TrxName());
			PO.copyValues(eLines[i], contractLine);
			contractLine.setAD_Org_ID(contractContent.getAD_Org_ID());
			contractLine.setAD_OrgTrx_ID(contractContent.getAD_OrgTrx_ID());
			contractLine.setDateOrdered(contractContent.getDateOrdered());
			contractLine.setJP_ContractContent_ID(contractContent.getJP_ContractContent_ID());

			contractLine.setQtyEntered(eLines[i].getQtyEntered());
			contractLine.setC_UOM_ID(eLines[i].getC_UOM_ID());
			contractLine.setQtyOrdered(eLines[i].getQtyOrdered());
			contractLine.setPriceEntered(eLines[i].getPriceEntered());
			contractLine.setPriceActual(eLines[i].getPriceActual());
			contractLine.setC_Tax_ID(eLines[i].getC_Tax_ID());
			contractLine.setLineNetAmt(eLines[i].getLineNetAmt());

			MContractLineT contractLineT = getContractLineTemplate(eLines[i], contractContentTemplate);
			if(contractLineT != null)
			{
				setContractLineFromTemplate(contractLine, contractLineT);

				BigDecimal coefficient = Env.ONE;
				if(contractLine.getQtyOrdered().compareTo(Env.ZERO) != 0 && contractLineT.getQtyOrdered().compareTo(Env.ZERO) != 0 )
				{
					coefficient = contractLine.getQtyOrdered().divide(contractLineT.getQtyOrdered(), 2, RoundingMode.HALF_UP);
				}

				if(!Util.isEmpty(contractContent.getJP_CreateDerivativeDocPolicy())
						&& ( contractContent.getJP_CreateDerivativeDocPolicy().equals("BT") || contractContent.getJP_CreateDerivativeDocPolicy().equals("IO")) )
				{
					contractLine.setJP_DerivativeDocPolicy_InOut(contractLineT.getJP_DerivativeDocPolicy_InOut());
					if(contractLine.getM_Product_ID() > 0)
					{
						contractLine.setMovementQty(contractLineT.getMovementQty().multiply(coefficient).divide(Env.ONE, contractLine.getM_Product().getC_UOM().getStdPrecision(), RoundingMode.HALF_UP ));
					}else {
						contractLine.setMovementQty(contractLineT.getMovementQty().multiply(coefficient).divide(Env.ONE, eLines[i].getC_UOM().getStdPrecision(), RoundingMode.HALF_UP));
					}
				}


				if(!Util.isEmpty(contractContent.getJP_CreateDerivativeDocPolicy())
						&& ( contractContent.getJP_CreateDerivativeDocPolicy().equals("BT") || contractContent.getJP_CreateDerivativeDocPolicy().equals("IV")) )
				{
					contractLine.setJP_DerivativeDocPolicy_Inv(contractLineT.getJP_DerivativeDocPolicy_Inv());
					if(contractLine.getM_Product_ID() > 0)
					{
						contractLine.setQtyInvoiced(contractLineT.getQtyInvoiced().multiply(coefficient).divide(Env.ONE, contractLine.getM_Product().getC_UOM().getStdPrecision(), RoundingMode.HALF_UP ));
					}else {
						contractLine.setQtyInvoiced(contractLineT.getQtyInvoiced().multiply(coefficient).divide(Env.ONE, eLines[i].getC_UOM().getStdPrecision(), RoundingMode.HALF_UP));
					}
				}


			}else {

				if( (!Util.isEmpty(contractContent.getJP_CreateDerivativeDocPolicy()) && contractContent.getJP_CreateDerivativeDocPolicy().equals("MA"))
						|| Util.isEmpty(contractContent.getJP_CreateDerivativeDocPolicy()) )
				{
					contractLine.setJP_BaseDocLinePolicy(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_ForTheDurationOfContractProcessPeriod);
					contractLine.getParent().setJP_ContractCalender_ID(0);
				}

				if(!Util.isEmpty(contractContent.getJP_CreateDerivativeDocPolicy())
						&& ( contractContent.getJP_CreateDerivativeDocPolicy().equals("BT") || contractContent.getJP_CreateDerivativeDocPolicy().equals("IO")) )
				{
					contractLine.setJP_DerivativeDocPolicy_InOut(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_ForTheDurationOfContractProcessPeriod);
					contractLine.getParent().setJP_ContractCalender_ID(0);
					contractLine.setMovementQty(Env.ZERO);
				}

				if(!Util.isEmpty(contractContent.getJP_CreateDerivativeDocPolicy())
						&& ( contractContent.getJP_CreateDerivativeDocPolicy().equals("BT") || contractContent.getJP_CreateDerivativeDocPolicy().equals("IV")) )
				{
					contractLine.setJP_DerivativeDocPolicy_Inv(MContractLine.JP_DERIVATIVEDOCPOLICY_INV_ForTheDurationOfContractProcessPeriod);
					contractLine.getParent().setJP_ContractCalender_ID(0);
					contractLine.setQtyInvoiced(Env.ZERO);
				}

			}//if


			try {
				contractLine.saveEx(get_TrxName());
			}catch (Exception e) {
				throw new Exception(Msg.getMsg(getCtx(), "SaveError") + Msg.getElement(getCtx(), "CopyFrom") + " : "
										+ Msg.getElement(getCtx(), "JP_EstimationLine_ID") + "_" + eLines[i].getLine() + " >>> " + e.getMessage() );
			}

		}

	}

	/**
	 *
	 * Get Contract Line Template
	 *
	 * @param eLine
	 * @return
	 */
	private MContractLineT getContractLineTemplate(MEstimationLine eLine, MContractContentT contractContentTemplate)
	{
		MContractLineT[] lineTemplates = contractContentTemplate.getContractLineTemplates();

		if(eLine.getM_Product_ID() > 0)
		{
			for(int i = 0; i < lineTemplates.length; i++)
			{
				if(eLine.getM_Product_ID() == lineTemplates[i].getM_Product_ID())
				{
					return lineTemplates[i];
				}
			}

			StringBuffer sql = new StringBuffer("SELECT * FROM JP_ContractLineT WHERE M_Product_ID=? ") //1
					.append(" AND (AD_Org_ID = ? OR AD_Org_ID = 0) AND JP_ContractType = ? AND IsSOTrx = ? AND DocBaseType = ? AND M_PriceList_ID = ? ") //2 ... 6
					.append(" AND JP_ContractContentT_ID IS NULL AND IsActive='Y'");
			if(contractContentTemplate.getDocBaseType().equals(MContractContentT.DOCBASETYPE_SalesOrder) || contractContentTemplate.getDocBaseType().equals(MContractContentT.DOCBASETYPE_PurchaseOrder))
			{
						sql.append(" AND OrderType = ? ");//7
			}
			sql.append(" ORDER BY AD_Org_ID DESC ");

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
				pstmt.setInt(1, eLine.getM_Product_ID());
				pstmt.setInt(2, eLine.getAD_Org_ID());
				pstmt.setString(3, contractContentTemplate.getJP_ContractType());
				pstmt.setString(4, "Y");
				pstmt.setString(5, contractContentTemplate.getDocBaseType());
				pstmt.setInt(6, contractContentTemplate.getM_PriceList_ID());
				if(contractContentTemplate.getDocBaseType().equals(MContractContentT.DOCBASETYPE_SalesOrder) || contractContentTemplate.getDocBaseType().equals(MContractContentT.DOCBASETYPE_PurchaseOrder))
					pstmt.setString(7, contractContentTemplate.getOrderType());
				rs = pstmt.executeQuery();
				if(rs.next())
				{
					return new MContractLineT (getCtx(), rs, get_TrxName());
				}

			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, sql.toString(), e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null; pstmt = null;
			}

			return null;

		}else if(eLine.getC_Charge_ID() > 0) {

			for(int i = 0; i < lineTemplates.length; i++)
			{
				if(eLine.getC_Charge_ID() == lineTemplates[i].getC_Charge_ID())
				{
					return lineTemplates[i];
				}
			}

			StringBuffer sql = new StringBuffer("SELECT * FROM JP_ContractLineT WHERE C_Charge_ID=? ") //1
						.append(" AND (AD_Org_ID = ? OR AD_Org_ID = 0) AND JP_ContractType = ? AND IsSOTrx = ? AND DocBaseType = ? AND M_PriceList_ID = ? ") //2 ... 6
						.append(" AND JP_ContractContentT_ID IS NULL AND IsActive='Y'");
			if(contractContentTemplate.getDocBaseType().equals(MContractContentT.DOCBASETYPE_SalesOrder) || contractContentTemplate.getDocBaseType().equals(MContractContentT.DOCBASETYPE_PurchaseOrder))
			{
						sql.append(" AND OrderType = ? ");//7
			}
			sql.append(" ORDER BY AD_Org_ID DESC ");

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
				pstmt.setInt(1, eLine.getC_Charge_ID());
				pstmt.setInt(2, eLine.getAD_Org_ID());
				pstmt.setString(3, contractContentTemplate.getJP_ContractType());
				pstmt.setString(4, "Y");
				pstmt.setString(5, contractContentTemplate.getDocBaseType());
				pstmt.setInt(6, contractContentTemplate.getM_PriceList_ID());
				if(contractContentTemplate.getDocBaseType().equals(MContractContentT.DOCBASETYPE_SalesOrder) || contractContentTemplate.getDocBaseType().equals(MContractContentT.DOCBASETYPE_PurchaseOrder))
					pstmt.setString(7, contractContentTemplate.getOrderType());
				rs = pstmt.executeQuery();
				if(rs.next())
				{
					return new MContractLineT (getCtx(), rs, get_TrxName());
				}

			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, sql.toString(), e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null; pstmt = null;
			}

			return null;

		}else {

			for(int i = 0; i < lineTemplates.length; i++)
			{
				if(eLine.getM_Product_ID() == lineTemplates[i].getM_Product_ID() && eLine.getC_Charge_ID() == lineTemplates[i].getC_Charge_ID())
				{
					return lineTemplates[i];
				}
			}

			StringBuffer sql = new StringBuffer("SELECT * FROM JP_ContractLineT WHERE M_Product_ID IS NULL AND C_Charge_ID IS NULL ")
					.append(" AND (AD_Org_ID = ? OR AD_Org_ID = 0) AND JP_ContractType = ? AND IsSOTrx = ? AND DocBaseType = ? AND M_PriceList_ID = ? ") //1 ... 5
					.append(" AND JP_ContractContentT_ID IS NULL AND IsActive='Y'");
			if(contractContentTemplate.getDocBaseType().equals(MContractContentT.DOCBASETYPE_SalesOrder) || contractContentTemplate.getDocBaseType().equals(MContractContentT.DOCBASETYPE_PurchaseOrder))
			{
						sql.append(" AND OrderType = ? ");//6
			}
			sql.append(" ORDER BY AD_Org_ID DESC ");

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
				pstmt.setInt(1, eLine.getAD_Org_ID());
				pstmt.setString(2, contractContentTemplate.getJP_ContractType());
				pstmt.setString(3, "Y");
				pstmt.setString(4, contractContentTemplate.getDocBaseType());
				pstmt.setInt(5, contractContentTemplate.getM_PriceList_ID());
				if(contractContentTemplate.getDocBaseType().equals(MContractContentT.DOCBASETYPE_SalesOrder) || contractContentTemplate.getDocBaseType().equals(MContractContentT.DOCBASETYPE_PurchaseOrder))
					pstmt.setString(6, contractContentTemplate.getOrderType());
				rs = pstmt.executeQuery();
				if(rs.next())
				{
					return new MContractLineT (getCtx(), rs, get_TrxName());
				}

			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, sql.toString(), e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null; pstmt = null;
			}

			return null;

		}

	}

	/**
	 *
	 * Create Contract Content Line From Contract Content Line Template
	 *
	 * @param contractContent
	 * @param template
	 * @throws Exception
	 */
	protected void createContractLineFromTemplate(MContractContent contractContent, MContractContentT template) throws Exception
	{

		//Create Contract Content Line
		MContractLineT[] m_ContractLineTemplates = template.getContractLineTemplates();
		for(int i = 0; i < m_ContractLineTemplates.length; i++)
		{
			MContractLine contractLine = new MContractLine(getCtx(), 0, get_TrxName());
			PO.copyValues(m_ContractLineTemplates[i], contractLine);
			contractLine.setAD_Org_ID(contractContent.getAD_Org_ID());
			contractLine.setAD_OrgTrx_ID(contractContent.getAD_OrgTrx_ID());
			contractLine.setDateOrdered(contractContent.getDateOrdered());
			contractLine.setJP_ContractContent_ID(contractContent.getJP_ContractContent_ID());
			setContractLineFromTemplate(contractLine, m_ContractLineTemplates[i]);

			try {
				contractLine.saveEx(get_TrxName());
			}catch (Exception e) {
				throw new Exception(Msg.getMsg(getCtx(), "SaveError") + Msg.getElement(getCtx(), "CopyFrom") + " : "
										+ Msg.getElement(getCtx(), "JP_ContractLineT_ID") + "_" + m_ContractLineTemplates[i].getLine() + " >>> " + e.getMessage() );
			}
		}//For i

	}//createContractLineFromTemplate

	/**
	 *
	 * Set Contract Content Line From Template
	 *
	 * @param contrcatLine
	 * @param contractLineT
	 */
	private void setContractLineFromTemplate(MContractLine contrcatLine, MContractLineT contractLineT)
	{
		contrcatLine.setJP_ContractLineT_ID(contractLineT.getJP_ContractLineT_ID());
		contrcatLine.setDatePromised(calculateDate(contrcatLine.getParent().getDateAcct(), contractLineT.getDeliveryTime_Promised())) ;
		contrcatLine.setJP_BaseDocLinePolicy(contractLineT.getJP_BaseDocLinePolicy());
		contrcatLine.setJP_DerivativeDocPolicy_InOut(contractLineT.getJP_DerivativeDocPolicy_InOut());
		contrcatLine.setJP_DerivativeDocPolicy_Inv(contractLineT.getJP_DerivativeDocPolicy_Inv());
		if(contrcatLine.getJP_BaseDocLinePolicy() != null)
		{
			setBaseDocLineProcPeriod(contrcatLine, contractLineT);
		}


		int JP_ContractCalRef_InOut_ID = contractLineT.getJP_ContractCalRef_InOut_ID();
		if(JP_ContractCalRef_InOut_ID > 0 && !Util.isEmpty(contrcatLine.getParent().getJP_CreateDerivativeDocPolicy()) )
		{
			setDerivativeInOutLineProcPeriod(contrcatLine, contractLineT);

		}//if(JP_ContractCalRef_InOut_ID > 0)


		int JP_ContractCalRef_Inv_ID = contractLineT.getJP_ContractCalRef_Inv_ID();
		if(JP_ContractCalRef_Inv_ID > 0 && !Util.isEmpty(contrcatLine.getParent().getJP_CreateDerivativeDocPolicy()))
		{
			setDerivativeInvoiceLineProcPeriod(contrcatLine, contractLineT);

		}//if(JP_ContractCalRef_Inv_ID > 0)


		int JP_ContractProcRef_InOut_ID = contractLineT.getJP_ContractProcRef_InOut_ID();
		if(JP_ContractProcRef_InOut_ID > 0)
		{
			MContractProcessRef  contractProcessRef = MContractProcessRef.get(getCtx(), JP_ContractProcRef_InOut_ID);
			MContractProcessList[] contractProcessLists = contractProcessRef.getContractProcessList(getCtx(), true, get_TrxName());
			if(contractProcessLists.length==1)
			{
				contrcatLine.setJP_ContractProcess_InOut_ID(contractProcessLists[0].getJP_ContractProcess_ID());
			}
		}

		int JP_ContractProcRef_Inv_ID = contractLineT.getJP_ContractProcRef_Inv_ID();
		if(JP_ContractProcRef_Inv_ID > 0)
		{
			MContractProcessRef  contractProcessRef = MContractProcessRef.get(getCtx(), JP_ContractProcRef_Inv_ID);
			MContractProcessList[] contractProcessLists = contractProcessRef.getContractProcessList(getCtx(), true, get_TrxName());
			if(contractProcessLists.length==1)
			{
				contrcatLine.setJP_ContractProcess_Inv_ID(contractProcessLists[0].getJP_ContractProcess_ID());
			}
		}

	}
}
