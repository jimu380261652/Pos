package com.lizhi.app.printcode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

import com.wizarpos.devices.AccessException;
import com.wizarpos.devices.DeviceManager;
import com.wizarpos.devices.printer.PrinterControl;
import com.wizarpos.impl.devices.printer.entity.CharacterSetting_Command;
import com.wizarpos.impl.devices.printer.entity.FormatSetting_Command;
import com.wizarpos.impl.devices.printer.entity.Printer_Command;

public class TangshiPrinterHelp extends ConstantPrint{
	public static String json;
	public static String jsonArray;
	private String shopName;//店铺名标题
	private String paymentId;//单号
	private String date;//时间
	private String shopContact;//店员姓名
	private String memo;//商品名称
	private String payAmount;//支付金额
	public void print(Bitmap qr) throws AccessException{
		
		getJson();
		
		PrinterControl control =  DeviceManager.getInstance().getPrinterControl();
		
		control.open();
		
//		效果 ： 加粗 双倍高 双倍宽 居中。
		control.sendESC(FormatSetting_Command.getESCan((byte)0x49));				//居中
		control.sendESC(CharacterSetting_Command.getESCEn((byte)0x01));				//加粗
		control.sendESC(CharacterSetting_Command.getGSExclamationN((byte)0x11));	//双倍宽高
		control.printText(shopName+"\n\n");
		//取消效果
//		control.sendESC(FormatSetting_Command.getESCan((byte)0x48));
		control.sendESC(CharacterSetting_Command.getESCEn((byte)0x00));				//加粗
		control.sendESC(CharacterSetting_Command.getGSExclamationN((byte)0x00));	//双倍宽高
		
//		左对齐
		control.sendESC(FormatSetting_Command.getESCan((byte)0x48));
		control.printText(TAG_ORDER_ID + paymentId+"\n");
		
		control.printText("时间:"+date+"\n");
		
		control.printText(TAG_OPERATOR + shopContact+"\n");
		
		control.printText(TAG_SEPARATOR + "\n\n");
		byte[] cmds = new byte[] { 0x1B, (byte) 0x44, 0x0C,((byte)(0x12)), ((byte)(0x1A/*09*3*/)), 0x00 };//制表位格式
		control.sendESC(cmds);
		for(String s : TAG_TABLE){
			control.printText(s);
			control.sendESC(Printer_Command.getHt());
		}
		
		control.printText("\n");
		JSONArray json;
		try {
			json = new JSONArray(jsonArray);
			for(int i=0;i<json.length();i++){
				
				String[] table_row=new String[]{json.getJSONObject(i).getString("name"),json.getJSONObject(i).getString("buyNum"),json.getJSONObject(i).getString("price")};
				for(String s : table_row){
					control.printText(s);
//					control.sendESC(cmds);
					control.sendESC(Printer_Command.getHt());
				}
				control.printText("\n");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		control.printText("\n\n");
		
		control.printText("\t\t"+TAG_TOTAL+payAmount+"\n");
		control.printText("\t\t"+TAG_PAY+payAmount+"\n");
		
		control.printText(TAG_SEPARATOR + "\n");
		
		control.sendESC(FormatSetting_Command.getESCan((byte)0x49));
		
		control.printText(THINKS+"\n");
		control.printText(PHONE+"\n");
		control.printText(TEL+"\n");
		control.printText("\n");
		control.printText("\n");
		control.printText("\n");
		control.printText("\n");
		control.printText("\n");
		
		control.close();
	}
	//解析json
	public void getJson() {
		try {
			JSONObject jsonObject=new JSONObject(json);
			shopName=jsonObject.getString("shopName");
			paymentId=jsonObject.getString("paymentId");
			date=jsonObject.getString("date");
			shopContact=jsonObject.getString("shopContact");
			memo=jsonObject.getString("memo");
			payAmount=jsonObject.getString("payAmount");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
