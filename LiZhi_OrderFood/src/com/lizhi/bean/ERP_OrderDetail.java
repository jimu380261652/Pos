package com.lizhi.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//订单商品
public class ERP_OrderDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String isPay;
	private String totalAmount;
	private String orderId;
	private String statusId;
	private String paymentId;
	private String statusName;
	private String orderDate;
	private String shipName;// 收货人
	private String shipMobile;
	private String shipTel;
	private String shipTime;
	private String paymentName;
	private String leaveMessage;
	private String shipAddress;
	private String memo;
	private String dispatchFee;// 配送费
	private String businessId;
	private String allowCancel;// 是否允许取消订单(0:不允许 1：允许
	private String allowSendGoods;// 是否允许发货(0:不允许 1：允许)
	private String allowRemindToUser;// 是否允许提醒用户收货(0:不允许 1：允许
	private String allowApplay;// 是否允许申请客服介入(0:不允许 1：允许
	private String attrName;// 属性名
	private String userName;// 买家帐号
	private List<ERP_OrderItem> items = new ArrayList<ERP_OrderItem>();
	private ERP_OrderItem orderItems = new ERP_OrderItem();

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	
	public String getDispatchFee() {
		return dispatchFee;
	}

	public void setDispatchFee(String dispatchFee) {
		this.dispatchFee = dispatchFee;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getStatusId() {
		return statusId;
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getAllowCancel() {
		return allowCancel;
	}

	public void setAllowCancel(String allowCancel) {
		this.allowCancel = allowCancel;
	}

	public String getAllowSendGoods() {
		return allowSendGoods;
	}

	public void setAllowSendGoods(String allowSendGoods) {
		this.allowSendGoods = allowSendGoods;
	}

	public String getAllowRemindToUser() {
		return allowRemindToUser;
	}

	public void setAllowRemindToUser(String allowRemindToUser) {
		this.allowRemindToUser = allowRemindToUser;
	}

	public String getAllowApplay() {
		return allowApplay;
	}

	public void setAllowApplay(String allowApplay) {
		this.allowApplay = allowApplay;
	}

	public String getAttrName() {
		return attrName;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}

	public List<ERP_OrderItem> getItems() {
		return items;
	}

	public void setItems(List<ERP_OrderItem> items) {
		this.items = items;
	}

	public String getShipMobile() {
		return shipMobile;
	}

	public void setShipMobile(String shipMobile) {
		this.shipMobile = shipMobile;
	}

	public String getShipName() {
		return shipName;
	}

	public void setShipName(String shipName) {
		this.shipName = shipName;
	}

	public String getShipAddress() {
		return shipAddress;
	}

	public void setShipAddress(String shipAddress) {
		this.shipAddress = shipAddress;
	}

	public String getShipTel() {
		return shipTel;
	}

	public void setShipTel(String shipTel) {
		this.shipTel = shipTel;
	}

	public String getPaymentName() {
		return paymentName;
	}

	public void setPaymentName(String paymentName) {
		this.paymentName = paymentName;
	}

	public String getLeaveMessage() {
		return leaveMessage;
	}

	public void setLeaveMessage(String leaveMessage) {
		this.leaveMessage = leaveMessage;
	}

	public String getShipTime() {
		return shipTime;
	}

	public void setShipTime(String shipTime) {
		this.shipTime = shipTime;
	}

	public String getIsPay() {
		return isPay;
	}

	public void setIsPay(String isPay) {
		this.isPay = isPay;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public ERP_OrderItem getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(ERP_OrderItem orderItems) {
		this.orderItems = orderItems;
	}

}
