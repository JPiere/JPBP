/******************************************************************************
 * Product: JPiere                                                            *
 * Copyright (C) Hideaki Hagiwara (h.hagiwara@oss-erp.co.jp)                  *
 *                                                                            *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY.                          *
 * See the GNU General Public License for more details.                       *
 *                                                                            *
 * JPiere is maintained by OSS ERP Solutions Co., Ltd.                        *
 * (http://www.oss-erp.co.jp)                                                 *
 *****************************************************************************/
package jpiere.base.plugin.org.adempiere.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;

import org.compiere.model.MCity;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;

import jpiere.base.plugin.org.adempiere.model.X_I_CityJP;

/**
 * 	JPIERE-0015:Import City
 *
 *  @author Hideaki Hagiwara
 *  @version $Id: ImportCity.java,v 1.0 2014/09/02 $
 *
 */
public class JPiereImportCity extends SvrProcess
{

	private boolean p_deleteOldImported = false;
	private boolean isMatchNameJP = true;

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (name.equals("DeleteOldImported"))
				p_deleteOldImported = "Y".equals(para[i].getParameter());
			if (name.equals("IsMatchNameJP"))
				isMatchNameJP = "Y".equals(para[i].getParameter());
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
	}	//	prepare

	/**
	 * 	Process
	 *	@return info
	 *	@throws Exception
	 */
	protected String doIt() throws Exception
	{
		StringBuilder sql = null;
		int no = 0;
		StringBuilder clientCheck = new StringBuilder(" AND AD_Client_ID=").append(getAD_Client_ID());


		//Delete Old Imported data
		if (p_deleteOldImported)
		{
			sql = new StringBuilder ("DELETE I_CityJP ")
				  .append("WHERE I_IsImported='Y'").append (clientCheck);
			no = DB.executeUpdate(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Delete Old Impored =" + no);
		}


		//Update C_Region_ID from Region Name
		sql = new StringBuilder ("UPDATE I_CityJP ")
		.append("SET C_Region_ID=(SELECT C_Region_ID FROM C_Region")
		.append(" WHERE i_CityJP.RegionName =C_Region.Name AND i_CityJP.AD_Client_ID=C_Region.AD_Client_ID) ")
		.append("WHERE RegionName IS NOT NULL AND C_Region_ID IS NULL")
		.append(" AND I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());

		commitEx();

		//Update C_Country_ID from C_Country_ID of C_Region Table
		sql = new StringBuilder ("UPDATE I_CityJP ")
		.append("SET C_Country_ID=(SELECT C_Country_ID FROM C_Region")
		.append(" WHERE i_CityJP.C_Region_ID =C_Region.C_Region_ID) ")
		.append(" WHERE I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());

		commitEx();

		//Update C_Country_ID from Country Name
		sql = new StringBuilder ("UPDATE I_CityJP ")
		.append("SET C_Country_ID=(SELECT C_Country_ID FROM C_Country")
		.append(" WHERE i_CityJP.CountryName =C_Country.Name AND i_CityJP.AD_Client_ID=C_Country.AD_Client_ID) ")
		.append("WHERE CountryName IS NOT NULL AND C_Country_ID IS NULL")
		.append(" AND I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());

		commitEx();


		//Check whether import data is only one country
		//Specifications of this import process,to bulk import data, is only one country.
		sql = new StringBuilder("SELECT DISTINCT C_Country_ID FROM I_CityJP WHERE I_IsImported<>'Y'")
								.append(clientCheck);

		PreparedStatement checkOneCountry = DB.prepareStatement (sql.toString(), get_TrxName());
		ResultSet checkOneCountryRS = checkOneCountry.executeQuery ();

		int C_Country_ID = 0;
		int countryCounter = 0;
		while (checkOneCountryRS.next ())
		{
			if(countryCounter > 0){
				addLog("This import data contain several country data.");
				return "This import data contain several country data.";
			}

			C_Country_ID = checkOneCountryRS.getInt(1);
			countryCounter++;
		}


		//Update C_City_ID form key "C_Region_ID + (Name or AreaCode)"
		//To maintain the city master,this process can select name or area code to match import data from C_Ctity table data.
		if(isMatchNameJP ){
			sql = new StringBuilder ("UPDATE I_CityJP ")
			.append("SET C_City_ID = (SELECT C_City_ID FROM C_City")
			.append(" WHERE C_City.C_Country_ID = I_CityJP.C_Country_ID AND C_City.C_Region_ID = I_CityJP.C_Region_ID  ")
			.append(" AND C_City.Name = I_CityJP.Name) ")
			.append(" WHERE I_IsImported<>'Y'").append(clientCheck);
			no = DB.executeUpdate(sql.toString(), get_TrxName());
		}else{
			sql = new StringBuilder ("UPDATE I_CityJP ")
			.append("SET C_City_ID = (SELECT C_City_ID FROM C_City")
			.append(" WHERE C_City.C_Country_ID = I_CityJP.C_Country_ID AND C_City.C_Region_ID = I_CityJP.C_Region_ID  ")
			.append(" AND C_City.AreaCode = I_CityJP.AreaCode) ")
			.append(" WHERE I_IsImported<>'Y'").append(clientCheck);
			no = DB.executeUpdate(sql.toString(), get_TrxName());
		}

		commitEx();



		//
		if(isMatchNameJP)
		{
			//UPdate時に同じ名称のCityが複数あった場合は一番小さいエリアコードが設定される仕様
			sql = new StringBuilder("SELECT * FROM C_City WHERE C_Country_ID = " + C_Country_ID)
								.append(clientCheck)
								.append("ORDER BY C_Region_ID,Name,AreaCode ");
		}else{
			//UPdate時に同じアリアコードのCityが複数あった場合はが一番小さい名称が設定される仕様
			sql = new StringBuilder("SELECT * FROM C_City WHERE C_Country_ID = " + C_Country_ID)
								.append(clientCheck)
								.append("ORDER BY C_Region_ID,AreaCode,Name ");
		}


		PreparedStatement pstmt1 = DB.prepareStatement(sql.toString(), get_TrxName());
		ResultSet rs1 = pstmt1.executeQuery();
		ArrayList<MCity> list = new ArrayList<MCity>();
		while (rs1.next())
		{
			list.add(new MCity (getCtx (), rs1, get_TrxName()));
		}


		//
		sql = new StringBuilder ("SELECT * FROM I_CityJP WHERE I_IsImported='N'")
					.append(clientCheck);
		PreparedStatement pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
		ResultSet rs = pstmt.executeQuery();
		while (rs.next())
		{
			X_I_CityJP imp = new X_I_CityJP (getCtx (), rs, get_TrxName());

			boolean isNew = true;
			if(imp.getC_City_ID()!=0){
				isNew =false;
			}

			if(isNew){
				if(imp.getName()!=null && !imp.getName().isEmpty()){
					MCity newCity = new MCity(getCtx (), 0, get_TrxName());
					newCity.setC_Country_ID(imp.getC_Country_ID());
					newCity.setC_Region_ID(imp.getC_Region_ID());
					newCity.setName(imp.getName());
					newCity.setAreaCode(imp.getAreaCode());
					newCity.saveEx(get_TrxName());
					imp.setC_City_ID(newCity.getC_City_ID());
					imp.setI_ErrorMsg("New Record");
					imp.setI_IsImported(true);
					imp.setProcessed(true);
				}else{
					imp.setI_IsImported(false);
					imp.setProcessed(false);
					imp.setI_ErrorMsg("No Name");
				}

			}else{//Update

				for(MCity city : list)
				{
					if(imp.getName()!=null && !imp.getName().isEmpty())
					{
						if(isMatchNameJP && imp.getC_Region_ID()==city.getC_Region_ID() && imp.getName().equals(city.getName()))
						{
							city.setPostal(imp.getPostal());
							city.setAreaCode(imp.getAreaCode());
							city.setLocode(imp.getLocode());
							city.setCoordinates(imp.getCoordinates());
							city.saveEx();
							imp.setI_ErrorMsg("Update data matching name");
							imp.setI_IsImported(true);
							imp.setProcessed(true);
							break;//UPdate時に同じ名称のCityが複数あった場合は一番小さいエリアコードが設定される仕様

						}else if(!isMatchNameJP && imp.getC_Region_ID()==city.getC_Region_ID() && imp.getAreaCode().equals(city.getAreaCode())){

							city.setName(imp.getName());
							city.setPostal(imp.getPostal());
							city.setLocode(imp.getLocode());
							city.setCoordinates(imp.getCoordinates());
							city.saveEx();
							imp.setI_ErrorMsg("Update data matching AreaCode");
							imp.setI_IsImported(true);
							imp.setProcessed(true);
							break;////UPdate時に同じアリアコードのCityが複数あった場合はが一番小さい名称が設定される仕様
						}

					}else{
						imp.setI_IsImported(false);
						imp.setProcessed(false);
						imp.setI_ErrorMsg("No Name");
						break;
					}
				}//for(MCity city : list)
			}//else

			imp.saveEx();

		}//while (rs.next())


		return "";
	}	//	doIt

}	//	ImportPayment
