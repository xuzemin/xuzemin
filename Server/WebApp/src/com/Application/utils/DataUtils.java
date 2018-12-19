package com.Application.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class DataUtils {
	private static DataUtils dataUtils;
	public static final String BaseDateType = "yyyy/MM/dd HH:mm:ss";
	
	public static DataUtils getInstance() {
		return (dataUtils == null)?(dataUtils = new DataUtils()):dataUtils;
	}
	
	public String getDateString(Timestamp timestamp,String Type) {
		String tsStr = null;
		DateFormat sdf = new SimpleDateFormat(Type);  
        try {  
            //·½·¨Ò»  
            tsStr = sdf.format(timestamp);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        return tsStr;
	}
}
