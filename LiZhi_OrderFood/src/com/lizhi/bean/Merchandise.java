package com.lizhi.bean;

import java.io.Serializable;

/*
 * 商品信息
 * */
public class Merchandise implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public String barCode;//条形码
	public String merchandiseName;//商品名称
	public String price;
	public String invertory;//库存
	public String merchandiseId;//商品代码
	public String merchandiseTypeId;//商品分类代码
	public String merchandiseTypeName;//商品分类名称
	public String close;//是否下架（1否，0是）
	public String sales;//销量
	public int num=1;
	public String cost;
	public String parMerchandiseTypeName;
	public String parMerchandiseTypeId;
	
	public String getCost() {
		return cost;
	}
	public void setCost(String cost) {
		this.cost = cost;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getParMerchandiseTypeName() {
		return parMerchandiseTypeName;
	}
	public void setParMerchandiseTypeName(String parMerchandiseTypeName) {
		this.parMerchandiseTypeName = parMerchandiseTypeName;
	}
	public String getParMerchandiseTypeId() {
		return parMerchandiseTypeId;
	}
	public void setParMerchandiseTypeId(String parMerchandiseTypeId) {
		this.parMerchandiseTypeId = parMerchandiseTypeId;
	}
	public String getBarCode() {
		return barCode;
	}
	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}
	public String getMerchandiseName() {
		return merchandiseName;
	}
	public void setMerchandiseName(String merchandiseName) {
		this.merchandiseName = merchandiseName;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getInvertory() {
		return invertory;
	}
	public void setInvertory(String invertory) {
		this.invertory = invertory;
	}
	public String getMerchandiseId() {
		return merchandiseId;
	}
	public void setMerchandiseId(String merchandiseId) {
		this.merchandiseId = merchandiseId;
	}
	public String getMerchandiseTypeId() {
		return merchandiseTypeId;
	}
	public void setMerchandiseTypeId(String merchandiseTypeId) {
		this.merchandiseTypeId = merchandiseTypeId;
	}
	public String getMerchandiseTypeName() {
		return merchandiseTypeName;
	}
	public void setMerchandiseTypeName(String merchandiseTypeName) {
		this.merchandiseTypeName = merchandiseTypeName;
	}
	public String getClose() {
		return close;
	}
	public void setClose(String close) {
		this.close = close;
	}
	public String getSales() {
		return sales;
	}
	public void setSales(String sales) {
		this.sales = sales;
	}
}
