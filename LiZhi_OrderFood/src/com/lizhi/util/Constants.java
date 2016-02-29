package com.lizhi.util;
import java.util.ArrayList;

import com.lizhi.bean.ERP_Shop;
import com.lizhi.bean.TemporarilyOrder;




public class Constants {
	public static String message="";//订单id
	public static int page=0;
	public static String price="";
	public static String orderid="";
	public static final String get_nodata="无法获取数据，请检查网络是否连接";
	public static String loginSign="";
	public static String channelId;//百度推送Id
	public static ERP_Shop shop;
	public static String terminalId="WP13431000000121";//"WP13431000000121";
	public static ArrayList<TemporarilyOrder> temps=new ArrayList<TemporarilyOrder>();
	public static String dispatchStatus="1";
}
