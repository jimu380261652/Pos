package com.lizhi.util;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.util.Log;


public class DateUtil {

	public DateUtil( ){
	}
	
	//ʱ��
	//�ַ�ת����ʱ��Date
	public static Date Convert2Date(String dateString)
	{
		return Convert2Date(dateString,"yyyy-MM-dd");
	}
	public static Date Convert2Date(String dateString, String type) {
		if (dateString == null || dateString.trim().equals("") || dateString.trim().equals("null"))
			return null;
		DateFormat df = new SimpleDateFormat(type);
		Date date = new Date();
		try {
			date = df.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block\
			Log.e("StringToDate", dateString + "    " + e);
			e.printStackTrace();
		}
		return date;
	}
	public static String changeFormat(String dateString, String oldtype,String newtype ) {
		if (dateString == null || dateString.trim().equals("") || dateString.trim().equals("null"))
			return "";
		DateFormat df = new SimpleDateFormat(oldtype);
		Date date = new Date();
		try {
			date = df.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block\
			Log.e("StringToDate", dateString + "    " + e);
			e.printStackTrace();
		}
		return Date2String(date,newtype);
	}
	public static String Date2String(Date date,String format){
		 SimpleDateFormat dateformat=new SimpleDateFormat(format);
		 return dateformat.format(date);
	}
	public static String Date2String(Calendar date,String format){
		 SimpleDateFormat dateformat=new SimpleDateFormat(format);
		 return dateformat.format(date.getTime());
	}
	/** 
     * 日期逻辑 
     * @param dateStr 日期字符串 
     * @return 
     */  
    public static String getTimeString(String dateStr) {  
        Date now=new Date();
        if(Date2String(now, "yyyyMMdd").equals(changeFormat(dateStr, "yyyy-MM-dd HH:mm:ss", "yyyyMMdd"))){
        	return changeFormat(dateStr, "yyyy-MM-dd HH:mm:ss", "HH:mm");
        }
        return changeFormat(dateStr, "yyyy-MM-dd HH:mm:ss", "MM-dd");
    }  
    /**
     * 判断日期是否在n天内
     * @param sdate
     * @return
     */
    public static Boolean isInDays(String date,int days) {
    	Date time=Convert2Date(date, "yyyyMMddHHmmsss");
    	if(time==null){
    		return false;
    	}
        Calendar cal = Calendar.getInstance();
        
        //判断是否是同一天
        if(days==1){
        	String curDate = Date2String(cal, "yyyyMMdd");
            String paramDate = Date2String(time, "yyyyMMdd");
            if(curDate.equals(paramDate)){
            	return true;
            }else{
            	return false;
            }
        }else{
        	long lt = time.getTime()/86400000;
            long ct = cal.getTimeInMillis()/86400000;
            if(days<(int)(ct-lt)){
            	return false;
            }else{
            	return true;
            }
        }
        
    }

}
