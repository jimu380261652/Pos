package com.lizhi.util;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lizhi.bean.ERP_Account;
import com.lizhi.bean.ERP_Member;
import com.lizhi.bean.ERP_Order;
import com.lizhi.bean.ERP_OrderDetail;
import com.lizhi.bean.ERP_Shop;
import com.lizhi.bean.OrderMessage;

public class JsonUtil {

	/**
	 * 
	 * @param strJson
	 * @param strResult
	 * @param strStatus
	 * @return 1.success;2.error;3.网络故障
	 * @throws JSONException
	 */
	public static Boolean isSuccess(String strJson) {
		if (strJson == null) {
			return false;
		}
		try {
			JSONObject object = new JSONObject(strJson);
			String Status = object.getString("code");

			if (Status.equals("0000") || "1".equals(Status)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}

	}

	public static int Total(String strJson) {
		try {
			JSONObject object = new JSONObject(strJson);
			String total = object.getString("total");

			return Integer.parseInt(total);
		} catch (Exception e) {
			return 0;
		}
	}

	public static int GetTotal(String strJson) {

		try {
			JSONObject object = new JSONObject(strJson);
			String total = object.getString("totalpage");

			return Integer.parseInt(total);
		} catch (Exception e) {
			return 0;
		}

	}
	
	// 生成json字符串
	public static String toJsonStr(Object object) {
		Gson gson = new Gson();
		return gson.toJson(object);
	}

	// 生成json字符串
	public static List<String> toObject(String object) {
		List<String> infos = new ArrayList<String>();
		if (StringUtil.isEmpty(object))
			return infos;
		Gson gson = new Gson();
		infos = gson.fromJson(object, new TypeToken<List<String>>() {
		}.getType());
		return infos;
	}

	// 获取某一字段
	public static String getString(String strJson, String name) {
		if (strJson == null) {
			return null;
		}
		try {
			JSONObject object = new JSONObject(strJson);
			return object.getString(name);
		} catch (Exception e) {
			return null;
		}

	}

	// 获取信息字段
	public static String getMessage(String strJson) {
		if (strJson == null) {
			return null;
		}
		try {
			JSONObject object = new JSONObject(strJson);
			return object.getString("message");
		} catch (Exception e) {
			return null;
		}

	}

	// 获取用户信息
	public static ERP_Shop getSimpleUser(String strJson) {
		ERP_Shop info = new ERP_Shop();
		if (StringUtil.isEmpty(strJson))
			return info;
		Gson gson = new Gson();
		info = gson.fromJson(strJson, new TypeToken<ERP_Shop>() {
		}.getType());
		return info;
	}

	// 获取订单列表
	public static List<ERP_Order> getOrdersNormal(String strJson) {
		List<ERP_Order> info = new ArrayList<ERP_Order>();
		if (StringUtil.isEmpty(getString(strJson, "orders")))
			return info;
		Gson gson = new Gson();
		info = gson.fromJson(getString(strJson, "orders"),
				new TypeToken<List<ERP_Order>>() {
				}.getType());
		return info;
	}

	// 获取订单消息列表
	public static List<OrderMessage> getOrderMessage(String strJson) {
		List<OrderMessage> info = new ArrayList<OrderMessage>();
		if (StringUtil.isEmpty(getString(strJson, "messageList")))
			return info;
		Gson gson = new Gson();
		info = gson.fromJson(getString(strJson, "messageList"),
				new TypeToken<List<OrderMessage>>() {
				}.getType());
		return info;
	}

	// 获取订单详情
	public static ERP_OrderDetail getOrderDetail(String strJson) {
		ERP_OrderDetail info = new ERP_OrderDetail();
		if (StringUtil.isEmpty(strJson))
			return info;
		Gson gson = new Gson();
		info = gson.fromJson(strJson, new TypeToken<ERP_OrderDetail>() {
		}.getType());
		return info;
	}

	// 获取流水信息
	public static List<ERP_Account> getAccount(String strJson) {
		List<ERP_Account> info = new ArrayList<ERP_Account>();
		if (StringUtil.isEmpty(getString(strJson, "AccountDatas")))
			return info;
		Gson gson = new Gson();
		info = gson.fromJson(getString(strJson, "AccountDatas"),
				new TypeToken<List<ERP_Account>>() {
				}.getType());
		return info;
	}

	// 获取会员列表
	public static List<ERP_Member> getMemberList(String strJson) {
		List<ERP_Member> info = new ArrayList<ERP_Member>();
		if (StringUtil.isEmpty(getString(strJson, "memberList")))
			return info;
		Gson gson = new Gson();
		info = gson.fromJson(getString(strJson, "memberList"),
				new TypeToken<List<ERP_Member>>() {
				}.getType());
		return info;
	}

	// 获取会员详情
	public static ERP_Member getMemberDetail(String strJson) {
		ERP_Member info = new ERP_Member();
		if (StringUtil.isEmpty(getString(strJson, "member")))
			return info;
		Gson gson = new Gson();
		info = gson.fromJson(getString(strJson, "member"),
				new TypeToken<ERP_Member>() {
				}.getType());
		return info;
	}
}
