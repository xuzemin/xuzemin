package com.android.lottery.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
	
	public static String getMD5(String text){
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[]  result = md.digest(text.getBytes());
			StringBuilder sb = new StringBuilder();
			for(byte b : result){
				int number = b & 0xff ;
				String str = Integer.toHexString(number);
				if(str.length()==1){
					sb.append("0");
				}
				sb.append(str);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			//cant reach
			return "";
		}
	}
	
	public static String getSign() {
		return getMD5(DateHelper.getCurrentHour() + NiceConstants.KEY);//hh+key
	}
}
