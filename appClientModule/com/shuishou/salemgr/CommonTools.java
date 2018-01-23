package com.shuishou.salemgr;

public class CommonTools {

	public static String transferDouble2Scale(double d){
		return String.format(ConstantValue.FORMAT_DOUBLE, d);
	}
	
	/**
	 * if d is positive, return d with 2 decimal
	 * if d is negative, return -d with 2 decimal, surrounding the bracket()
	 * @param d
	 * @return
	 */
	public static String transferNumberByPM(double d, String currencyIcon){
		if (d >= 0){
			if (currencyIcon != null)
				return transferDouble2Scale(d);
			else 
				return currencyIcon + transferDouble2Scale(d);
		} else {
			if (currencyIcon != null)
				return "(" + currencyIcon + transferDouble2Scale(d * (-1)) + ")";
			else 
				return "(" + transferDouble2Scale(d * (-1)) + ")";
		}
	}
}
