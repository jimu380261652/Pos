package com.lizhi.app.printcode;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

import com.lizhi.bean.Commodity;
import com.wizarpos.devices.AccessException;
import com.wizarpos.devices.DeviceManager;
import com.wizarpos.devices.printer.PrinterControl;
import com.wizarpos.impl.devices.printer.entity.CharacterSetting_Command;
import com.wizarpos.impl.devices.printer.entity.FormatSetting_Command;
import com.wizarpos.impl.devices.printer.entity.Printer_Command;

public class TangshiPrinterForOrderHelp extends ConstantPrint{
	public static String json;
	private String shopName;//店铺名标题
	private String paymentId;//单号
	private String date;//时间
	private String shopContact;//店员姓名
	private String statusName;//状态
	private String totalAmount;//实付金额
	private String shipName;//收货人
	private String shipMobile;//联系手机
	private String shipAddress;//收货人地址
	private String shopAddress,shopPhone;
	public static List<Commodity>list;
	public void print(Bitmap qr) throws AccessException{
		setJson();
//		getJson();
		
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
//		String[] table_row=new String[]{memo,number,payAmount};
		for(int i = 0;i<list.size();i++){
			control.printText((i+1)+"."+list.get(i).getName());
			control.printText("\n");
			String[] table_row=new String[]{"",list.get(i).getNumber()+"",list.get(i).getPrice()+"",list.get(i).getCount()+""};
			for (int j = 0; j < table_row.length; j++) {
				control.printText(table_row[j]);
				control.sendESC(Printer_Command.getHt());
			}
//			control.sendESC(cmds);
			control.printText("\n");
		}
		control.printText("\n");
		control.printText("\n");
		control.printText("交易状态："+statusName);
		control.printText("\n");
		control.printText("收货人:"+shipName);
		control.printText("\n");
		control.printText("收货人电话:"+shipMobile);
		control.printText("\n");
		control.printText("收货人地址："+shipAddress);
		control.printText("\n\n");
		
		control.printText("\t\t"+TAG_TOTAL+totalAmount+"\n");
		control.printText("\t\t"+TAG_PAY+totalAmount+"\n");
		
		control.sendESC(FormatSetting_Command.getESCan((byte)0x49));
		control.printText("欢迎再次光临"+shopName+"\n");
		control.printText("地址:"+shopAddress+"\n");
		control.printText("电话:"+shopPhone+"\n");
		
		control.printText(TAG_SEPARATOR + "\n");
		
		control.sendESC(FormatSetting_Command.getESCan((byte)0x49));
		
		control.printText("扫一扫，有惊喜！"+"\n");
		control.sendESC(FormatSetting_Command.getESCan((byte)0x49));
		control.printImage(qr);
		control.printText("\n");
		control.printText("我家微店在这里"+"\n");
		control.printText("http://www.duiduifu.com"+"\n");
		control.printText("\n");
		control.printText("\n");
		control.printText("\n");
		control.printText("\n");
		control.printText("\n");
		
		control.close();
	}
	public void setJson() {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObject=new JSONObject(json);
			shopName=getJsonString("shopName",jsonObject);
			paymentId=getJsonString("orderId",jsonObject);
			date=getJsonString("orderDate",jsonObject);
			shopContact=getJsonString("shopContact",jsonObject);
			statusName=getJsonString("statusName",jsonObject);
			totalAmount=getJsonString("totalAmount",jsonObject);
			shipName=getJsonString("shipName",jsonObject);
			shipMobile=getJsonString("shipMobile",jsonObject);
			shipAddress=getJsonString("shipAddress",jsonObject);
			shopPhone=getJsonString("shopPhone", jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public String getJsonString(String name,JSONObject jo){
		if(jo.has(name)){
			try {
				return jo.getString(name);
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}else{
			return null;
		}
	}
	
}
