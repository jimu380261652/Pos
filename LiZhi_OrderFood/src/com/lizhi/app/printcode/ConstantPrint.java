package com.lizhi.app.printcode;

import android.graphics.Bitmap;

import com.wizarpos.devices.AccessException;


public abstract class ConstantPrint {
	
	static final String TAG_TITLE = "俏江南(陆家嘴店)";
	
	static final  String TAG_ORDER_ID = "单号：";
			
	static final  String TAG_TYPE_B = "订座";
	
	static final String TIME=null;
	
	static final  String TAG_TYPE_C = "WaiMaiPrinterHelper";
		
	static final  String TAG_OPERATOR = "操作员：";
	
	static final  String TAG_SEPARATOR = "********************************";
	
	static final  String[] TAG_TABLE_ROW_1 = new String[]{
			"红烧大排","1","6.00","6.00"
	};

	static final  String[] TAG_TABLE = new String[]{
		"项目","数量","单价"
	};
	
	static final  String TAG_TOTAL = "总计：";
	
	static final  String TAG_PAY = "已付：";
	
	static final String THINKS="多谢惠顾";
	
	static final String PHONE="店铺电话：020-62730905";
	
	static final String TEL="兑兑付 020-6273905";
	
	
	/*******************type c*********************/
	static final  String TAG_DELIVERY_TIME = "送餐时间：";
	
	static final  String TAG_DELIVERY_POSITION = "送餐地址：";
	
	static final  String TAG_CONTACT = "联系人：";
	
	static final  String TAG_TEL = "联系电话：";
	
	public abstract void print(Bitmap qr)throws AccessException;
}
