package com.lizhi.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * <strong>Title:</strong> StringUtil
 * 
 * <p><strong>Description:瀛楃涓插伐鍏风被</strong> </p>
 * 
 * @author: Roger.Luo  2014骞�2鏈�7鏃�
 * 
 * @version: 1.0
 */
public class StringUtil {

	/**
	 * 鍏冭浆鍒�
	 * @param yuan
	 * @return
	 */
    public static String yuanToFen(BigDecimal yuan) {
    	BigDecimal fen = yuan.multiply(new BigDecimal(100));
    	return String.valueOf(fen.longValue());
    }
    
    /**
     * 鎶婂垎杞崲涓哄厓
     * @param src
     * @return
     */
    public static String fenToYuan(String src){

		String amtTrimString = src.trim();
		int iLen = amtTrimString.length();
		if (iLen > 2) {
			int strPartOne = Integer.parseInt(amtTrimString.substring(0, iLen - 2));
			String strPartTwo = amtTrimString.substring(iLen - 2);
			return strPartOne + "." + strPartTwo;
		} else if (iLen == 2) {
			return "0." + amtTrimString;
		} else if (iLen == 1) {
			return "0.0" + amtTrimString;
		} else {
			return "0.00";
		}
    }
    //复制数组
    public static String[] addArray(String[] src,String[] object){
    	String[] result=new String[src.length+object.length];
    	for(int i=0;i<src.length;i++){
    		result[i]=src[i];
    	}
    	for(int j=src.length;j<object.length;j++){
    		result[j]=src[j-src.length];
    	}
    	return result;
    }
	/**
	 * 鎶ユ枃瀛楃宸﹀姞绌烘牸
	 * @param all  瀛楃涓插鐞嗗悗闀垮害
	 * @param str  瀛楃涓�
	 * @return String
	 */
	public static String addNullStringLeft(int all,String str){
		int length=0;
    	
		if(str==null){
		  str="";
		}
		length=str.getBytes().length;
    	
		if(length!=all){
		   StringBuffer sb=new StringBuffer();
		   String nullStr=" ";
		   int difference=all-length;
		   for (int i = 0; i < difference; i++) {
			  sb.append(nullStr);
		   }
		   sb.append(str);
		   return sb.toString();
		}
    	
		return str;
	}
	
    /**
     * 鎶ユ枃瀛楃鍔犵┖鏍�
     * @param all  瀛楃涓插鐞嗗悗闀垮害
     * @param str  瀛楃涓�
     * @return String
     */
    public static String addNullString(int all,String str){
    	int length=0;
    	
    	if(str==null){
    	  str="";
    	}
    	length=str.getBytes().length;
    	
    	if(length!=all){
		   StringBuffer sb=new StringBuffer();
		   sb.append(str);
    	   String nullStr=" ";
    	   int difference=all-length;
    	   for (int i = 0; i < difference; i++) {
			  sb.append(nullStr);
		   }
		   return sb.toString();
    	}
    	
    	return str;
    }
    /**
     * get default md5 hash of a string
     * @param str
     * @return
     */
    public static String MD5(String str){
    	return MD5(str,"MD5","UTF-8");
    }
    /**
	 * get the md5 hash of a string
	 * 
	 * @param str
	 * @return
	 */
	public static String MD5(String str,String signType,String charset) {

		if (str == null) {
			return null;
		}
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance(signType);
			messageDigest.reset();
			messageDigest.update(str.getBytes(charset));
		} catch (NoSuchAlgorithmException e) {

			return str;
		} catch (UnsupportedEncodingException e) {
			return str;
		}

		byte[] byteArray = messageDigest.digest();

		StringBuffer md5StrBuff = new StringBuffer();

		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}

		return md5StrBuff.toString();
	}
	
	/**
	 * 瀛楃涓茶浆鎴愰噾棰�
	 * @param str
	 * @return
	 */
	public static BigDecimal getBigDecimal(String str) {
		if (StringUtil.isEmpty(str)) {
			return null;
		} else {
			return new BigDecimal(str);
		}
	}
	
	/**
	 * 鏄惁涓虹┖
	 * @param str
	 */
	public static boolean isEmpty(String str){
		if(str==null)
			return true;
		if(str.length()<1)
			return true;
		return false;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
}
