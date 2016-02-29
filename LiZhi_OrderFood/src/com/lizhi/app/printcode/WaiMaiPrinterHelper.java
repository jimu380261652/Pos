package com.lizhi.app.printcode;

import android.graphics.Bitmap;

import com.wizarpos.devices.AccessException;
import com.wizarpos.devices.DeviceManager;
import com.wizarpos.devices.printer.PrinterControl;
import com.wizarpos.impl.devices.printer.entity.CharacterSetting_Command;
import com.wizarpos.impl.devices.printer.entity.FormatSetting_Command;
import com.wizarpos.impl.devices.printer.entity.Printer_Command;

public class WaiMaiPrinterHelper  extends ConstantPrint{
	
	public void print(Bitmap qr) throws AccessException{
		PrinterControl control =  DeviceManager.getInstance().getPrinterControl();
		
//		control.open();
		
//		效果 ： 加粗 双倍高 双倍宽 居中。
		control.sendESC(FormatSetting_Command.getESCan((byte)0x49));				//居中
		control.sendESC(CharacterSetting_Command.getESCEn((byte)0x01));				//加粗
		control.sendESC(CharacterSetting_Command.getGSExclamationN((byte)0x11));	//双倍宽高
		control.printText(TAG_TITLE+"\n\n");
		//取消效果
		control.sendESC(CharacterSetting_Command.getESCEn((byte)0x00));				//加粗
		control.sendESC(CharacterSetting_Command.getGSExclamationN((byte)0x00));	//双倍宽高
		
//		居中
		String time = "2013-11-29 11:41:23";
		control.printText(time+"\n\n");
		
//		左对齐
		control.sendESC(FormatSetting_Command.getESCan((byte)0x48));
		control.printText(TAG_ORDER_ID + "\n");
				
		control.printText(TAG_OPERATOR + "\n");
		
		control.printText(TAG_SEPARATOR + "\n");
		
		byte[] cmds = new byte[] { 0x1B, (byte) 0x44, 0x0C,((byte)(0x12)), ((byte)(0x1A/*09*3*/)), 0x00 };//制表位格式
		control.sendESC(cmds);
		for(String s : TAG_TABLE){
			control.printText(s);
			control.sendESC(Printer_Command.getHt());
		}
		control.printText("\n");
		for(String s : TAG_TABLE_ROW_1){
			control.printText(s);
//			control.sendESC(cmds);
			control.sendESC(Printer_Command.getHt());
		}
		control.printText("\n");
		control.printText("\n\n");
		
		control.printText("\t\t"+TAG_TOTAL+"\n");
		control.printText("\t\t"+TAG_PAY+"\n");
		
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
}
