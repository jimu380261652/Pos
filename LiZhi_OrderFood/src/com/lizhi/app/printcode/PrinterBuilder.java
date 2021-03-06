package com.lizhi.app.printcode;

public class PrinterBuilder {

	/**
	 * normal string
	 * @param s
	 * @return
	 */
	public String normal(String s) {
		return s;
	}
	
	/**
	 * bold string
	 * @param s
	 * @return
	 */
	public String bold(String s) {
		return "<b>" + s + "</b>";
	}
	
	/**
	 * italic string
	 * @param s
	 * @return
	 */
	public String italic(String s) {
		return "<i>" + s + "</i>";
	}
	
	/**
	 * align center string
	 * @param s
	 * @return
	 */
	public String center(String s) {
		return "<c>" + s + "</c>";
	}
	
	/**
	 * align left string
	 * @param s
	 * @return
	 */
	public String left(String s) {
		return "<l>" + s + "</l>";
	}
	
	/**
	 * align right string
	 * @param s
	 * @return
	 */
	public String right(String s) {
		return "</r>" + s;
	}
	
	/**
	 * make character wider
	 * @param s
	 * @return
	 */
	public String wide(String s) {
		return "<w>" + s + "</w>";
	}
	
	/**
	 * append tab symbol with string
	 * @param s
	 * @return
	 */
	public String tab(String s) {
		return "<t>" + s + "</t>";
	}
	
	/**
	 * print bar code
	 * @param s bar code string
	 * @return
	 */
	public String barcode(String s) {
		return "<bc>" + s + "</bc>";
	}
	
	/**
	 * print qr code
	 * @param s qr code string
	 * @return
	 */
	public String qrcode(String s) {
		return "<qc>" + s + "</qc>";
	}
	
	/**
	 * new line symbol
	 * @return
	 */
	public String br() {
		return "</br>";
	}
	
	/**
	 * add underline to string
	 * @param s
	 * @return
	 */
	public String underLine(String s) {
		return "<ul>" + s + "</ul>";
	}
	
	/**
	 * print image
	 * @param s image url
	 * @return
	 */
	public String img(String s) {
		return "<img>" + s + "</img>";
	}
	
	/**
	 * one times font
	 * @param s
	 * @return
	 */
	public String one(String s) {
		return "<1>" + s + "</1>";
	}
	
	/**
	 * two times font
	 * @param s
	 * @return
	 */
	public String two(String s) {
		return "<2>" + s + "</2>";
	}
	
	/**
	 * three times font
	 * @param s
	 * @return
	 */
	public String three(String s) {
		return "<3>" + s + "</3>";
	}
	
	/**
	 * four times font
	 * @param s
	 * @return
	 */
	public String four(String s) {
		return "<4>" + s + "</4>";
	}
	
	/**
	 * five times font
	 * @param s
	 * @return
	 */
	public String five(String s) {
		return "<5>" + s + "</5>";
	}
	
	/**
	 * six times font
	 * @param s
	 * @return
	 */
	public String six(String s) {
		return "<6>" + s + "</6>";
	}
	
	/**
	 * seven times font
	 * @param s
	 * @return
	 */
	public String seven(String s) {
		return "<7>" + s + "</7>";
	}
	
	/**
	 * eight times font
	 * @param s
	 * @return
	 */
	public String eight(String s) {
		return "<8>" + s + "</8>";
	}
		
}
