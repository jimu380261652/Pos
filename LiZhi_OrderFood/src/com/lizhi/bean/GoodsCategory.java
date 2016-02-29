package com.lizhi.bean;

import java.util.ArrayList;
import java.util.List;


//商品类别
public class GoodsCategory {
private String name;//类别名称
private String id;//类别id 
private String devel;//判断是几级类别
private List<Good>list=new ArrayList<Good>();
public List<Good> getList() {
	return list;
}
public void setList(List<Good> list) {
	this.list = list;
}
public String getDevel() {
	return devel;
}
public void setDevel(String devel) {
	this.devel = devel;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
}
