package net.nmss.nice.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
                                      
/* 
 * @author Msquirrel
 */
@SuppressLint("SimpleDateFormat")
public class DateUtils {
                                          
    private static SimpleDateFormat sf = null;
    /*获取系统时间 格式为："yyyy-MM-dd "*/
    public static String getCurrentDate() {
        Date d = new Date();
         sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(d);
    }
    
    /*获取年份跟月数："yyyy-MM"*/
    public static String getCurrentYearMouth() {
        Date d = new Date();
         sf = new SimpleDateFormat("yyyy-MM");
        return sf.format(d);
    }
    
    /*获取年份："yyyy"*/
    public static String getCurrentYear() {
        Date d = new Date();
         sf = new SimpleDateFormat("yyyy");
        return sf.format(d);
    }
    
    /*获取本月份*/
    public static String getCurrentThisMouth(){
    	Date date = new Date();
    	sf = new SimpleDateFormat("yyyy-MM-dd");
    	String d = sf.format(date);
    	String mouth = d.substring(5, 7);
    	return mouth;
    }
    
    /*获取上个月并转化为int型*/
    public static String getCurrentLastMouth(){
    	Date date = new Date();
    	sf = new SimpleDateFormat("yyyy-MM-dd");
    	String d = sf.format(date);
    	int mouth = Integer.parseInt(d.substring(5, 7));
    	String last = String.valueOf(mouth-1);
    	return last;
    }
    
    /*获取下个月并转化为int型*/
    public static String getCurrentNextMouth(){
    	Date date = new Date();
    	sf = new SimpleDateFormat("yyyy-MM-dd");
    	String d = sf.format(date);
    	int mouth = Integer.parseInt(d.substring(5, 7));
    	String next = String.valueOf(mouth+1);
    	return next;
    }
                                      
    /*时间戳转换成字符窜*/
	public static String getDateToString(long time) {
        Date d = new Date(time);
        sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(d);
    }
                                      
    /*将字符串转为时间戳*/
    public static long getStringToDate(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try{
            date = sdf.parse(time);
        } catch(ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }
}