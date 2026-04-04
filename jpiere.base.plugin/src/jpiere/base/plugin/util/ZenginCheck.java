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

import java.util.Arrays;

public class ZenginCheck {

	//length of String
	public static final int     JP_RoutingNo = 4;

	public static final int     JP_BankName_Kana = 15;

	public static final int     JP_BranchCode = 3;

	public static final int     JP_BranchName_Kana = 15;

	public static final int     JP_AccountNo = 7;

	public static final int     JP_RequesterCode = 10;

	public static final int     JP_RequesterName = 40;

	public static final int     JP_A_Name_Kana = 30;

	public static final String  PAYMENT_EXPORT_CLASS = "org.compiere.util.JapanPaymentExport";


	static boolean isSorted = false;

	static char[] zenginAllCharacters = new char[]{
		'1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
		'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
		
	    '\uFF71','\uFF72','\uFF73','\uFF74','\uFF75', // ｱ ｲ ｳ ｴ ｵ
	    '\uFF76','\uFF77','\uFF78','\uFF79','\uFF7A', // ｶ ｷ ｸ ｹ ｺ
	    '\uFF7B','\uFF7C','\uFF7D','\uFF7E','\uFF7F', // ｻ ｼ ｽ ｾ ｿ
	    '\uFF80','\uFF81','\uFF82','\uFF83','\uFF84', // ﾀ ﾁ ﾂ ﾃ ﾄ
	    '\uFF85','\uFF86','\uFF87','\uFF88','\uFF89', // ﾅ ﾆ ﾇ ﾈ ﾉ
	    '\uFF8A','\uFF8B','\uFF8C','\uFF8D','\uFF8E', // ﾊ ﾋ ﾌ ﾍ ﾎ
	    '\uFF8F','\uFF90','\uFF91','\uFF92','\uFF93', // ﾏ ﾐ ﾑ ﾒ ﾓ
	    '\uFF94','\uFF95','\uFF96',                  // ﾔ ﾕ ﾖ
	    '\uFF97','\uFF98','\uFF99','\uFF9A','\uFF9B', // ﾗ ﾘ ﾙ ﾚ ﾛ
	    '\uFF9C','\uFF66','\uFF9D',                  // ﾜ ｦ ﾝ
	    '\uFF9E','\uFF9F',                           // ﾞ ﾟ
	    
		'(', ')','.', '-', '/',' '
	};

	static char[] numCharacters = new char[]{
		'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'
	};

	static public boolean stringCheck(String characters)
	{
		if(!isSorted)
		{
			Arrays.sort(zenginAllCharacters);
			isSorted = true;
		}

		for(int i = 0; i < characters.length(); i++)
		{
			char target = characters.charAt(i);
			int index = Arrays.binarySearch(zenginAllCharacters, target);
			if(index < 0)
			{
				return false;
			}
		}

		return true;
	}

	static public boolean charCheck(char character)
	{
		if(!isSorted)
		{
			Arrays.sort(zenginAllCharacters);
			isSorted = true;
		}

		int index = Arrays.binarySearch(zenginAllCharacters, character);
		if(index < 0)
		{
			return false;
		}

		return true;
	}

	static public boolean numStringCheck(String characters)
	{

		Arrays.sort(numCharacters);
		for(int i = 0; i < characters.length(); i++)
		{
			char target = characters.charAt(i);
			int index = Arrays.binarySearch(numCharacters, target);
			if(index < 0)
			{
				return false;
			}
		}

		return true;
	}
}
