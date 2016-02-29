package com.lizhi.bean;

public class Good {
private String name;
private String price;
private int num;//购买数量
private String size;//规格
private int stock;
public int getStock() {
	return stock;
}
public void setStock(int stock) {
	this.stock = stock;
}
public String getSize() {
	return size;
}
public void setSize(String size) {
	this.size = size;
}
public int getNum() {
	return num;
}
public void setNum(int num) {
	this.num = num;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getPrice() {
	return price;
}
public void setPrice(String price) {
	this.price = price;
} 
}
