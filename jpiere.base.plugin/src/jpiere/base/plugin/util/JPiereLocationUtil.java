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
package jpiere.base.plugin.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;

import org.compiere.model.MCity;
import org.compiere.model.MCountry;
import org.compiere.model.MLocation;
import org.compiere.model.MRegion;
import org.compiere.model.PO;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Util;


/**
 * 	JPIERE-0053:Import Organization
 *
 *  @author Hideaki Hagiwara
 *  @version $Id: ImportOrg.java,v 1.0 2015/01/02 $
 *
 */
public class JPiereLocationUtil {


	//ロケーションを新規作成する
	//ラベル検索を行う。なければ新規作成する。
	//ラベル検索を行う。ない場合は新規作成しない。
	//ラベル検索を行う。ある場合は、そのデータを更新する。なければ新規作成する。

	/**
	 *
	 * Search Location
	 *
	 * @param ctx
	 * @param JP_Location_Label
	 * @param trxName
	 * @return int
	 */
	public static int searchLocationByLabel (Properties ctx, String JP_Location_Label, String trxName )
	{

		String WhereClause = " JP_Location_Label='" +JP_Location_Label + "'" + " AND AD_Client_ID=" +Env.getAD_Client_ID(Env.getCtx());

		int[]  C_Location_IDs = PO.getAllIDs("C_Location", WhereClause, trxName);

		if(C_Location_IDs.length==0)
			return 0;
		else if(C_Location_IDs.length == 1)
			return C_Location_IDs[0];
		else if(C_Location_IDs.length > 1)
			return -1 ;


		return 0;

	}	//	createLocation



	public static int createLocation (Properties ctx
			,String JP_Location_Label
			,String Comments
			,String CountryCode
			,String Postal
			,String Postal_Add
			,String RegionName
			,String City
			,String Address1
			,String Address2
			,String Address3
			,String Address4
			,String Address5
			,String trxName )
	{

		MLocation location = new MLocation(ctx, 0, trxName);
		if(!Util.isEmpty(JP_Location_Label))
		{
			location.set_ValueNoCheck("JP_Location_Label", JP_Location_Label);
		}

		if(!Util.isEmpty(Comments))
		{
			location.setComments(Comments);
		}

		if(!Util.isEmpty(CountryCode))
		{
			ArrayList<MCountry> list = new ArrayList<MCountry>();
			String sql = "SELECT * FROM C_Country WHERE CountryCode=? AND IsActive='Y' AND (AD_Client_ID = 0 or AD_Client_ID=?)";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql, trxName);
				pstmt.setString(1, CountryCode);
				pstmt.setInt(2, Env.getAD_Client_ID(ctx));
				rs = pstmt.executeQuery();
				while (rs.next())
					list.add(new MCountry (ctx, rs, trxName));
			}
			catch (Exception e)
			{
				;
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}

			if(list.size()==1)
			{
				location.setC_Country_ID(list.get(0).getC_Country_ID());

			}else {

				location.setC_Country_ID(MCountry.getDefault(ctx).getC_Country_ID());
			}

		}else {

			location.setC_Country_ID(MCountry.getDefault(ctx).getC_Country_ID());

		}

		if(!Util.isEmpty(Postal))
		{
			location.setPostal(Postal);
		}

		if(!Util.isEmpty(Postal_Add))
		{
			location.setPostal_Add(Postal_Add);
		}

		if(!Util.isEmpty(RegionName))
		{
			location.setRegionName(RegionName);

			ArrayList<MRegion> list = new ArrayList<MRegion>();
			String sql = "SELECT * FROM C_Region WHERE C_Country_ID=? AND Name=? AND IsActive='Y' AND (AD_Client_ID = 0 or AD_Client_ID=?)";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql, trxName);
				pstmt.setInt(1, location.getC_Country_ID());
				pstmt.setString(2, RegionName);
				pstmt.setInt(3, Env.getAD_Client_ID(ctx));
				rs = pstmt.executeQuery();
				while (rs.next())
					list.add(new MRegion (ctx, rs, trxName));
			}
			catch (Exception e)
			{
				;
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}

			if(list.size()==1)
			{
				location.setC_Region_ID(list.get(0).getC_Region_ID());

			}
		}

		if(!Util.isEmpty(City))
		{
			location.setCity(City);

			ArrayList<MCity> list = new ArrayList<MCity>();
			String sql = "SELECT * FROM C_City WHERE C_Country_ID=? AND C_Region_ID =? AND Name=? AND IsActive='Y' AND (AD_Client_ID = 0 or AD_Client_ID=?)";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql, trxName);
				pstmt.setInt(1, location.getC_Country_ID());
				pstmt.setInt(2, location.getC_Region_ID());
				pstmt.setString(3, City);
				pstmt.setInt(4, Env.getAD_Client_ID(ctx));
				rs = pstmt.executeQuery();
				while (rs.next())
					list.add(new MCity (ctx, rs, trxName));
			}
			catch (Exception e)
			{
				;
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}

			if(list.size()==1)
			{
				location.setC_City_ID(list.get(0).getC_City_ID());

			}

		}

		if(!Util.isEmpty(Address1))
		{
			location.setAddress1(Address1);
		}

		if(!Util.isEmpty(Address2))
		{
			location.setAddress2(Address2);
		}

		if(!Util.isEmpty(Address3))
		{
			location.setAddress3(Address3);
		}

		if(!Util.isEmpty(Address4))
		{
			location.setAddress4(Address4);
		}

		if(!Util.isEmpty(Address5))
		{
			location.setAddress5(Address5);
		}

		location.saveEx(trxName);

		return location.getC_Location_ID();

	}	//	createLocation

}
