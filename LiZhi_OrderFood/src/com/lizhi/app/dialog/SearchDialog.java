package com.lizhi.app.dialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.duiduifu.http.AsyncListener;
import com.duiduifu.http.AsyncRunner;
import com.duiduifu.util.AppConstantByUtil;
import com.duiduifu.util.MD5Util;
import com.duiduifu.view.TipsToast;
import com.lizhi.adapter.SearchByBarcodeAdapter;
import com.lizhi.bean.Merchandise;
import com.lizhi.util.Constants;
import com.markmao.pulltorefresh.widget.XListView;
import com.markmao.pulltorefresh.widget.XListView.IXListViewListener;
import com.pos.lizhiorder.R;

//商品入库
public class SearchDialog extends BaseDialog implements
		android.view.View.OnClickListener {

	Context mContext;

	private XListView pulllv_pro;
	private CallBack callback;
	private RadioGroup group;
	private String isBarcode = "";
	private boolean flag,Sflag=false;// 判断页面
 
	private Handler mMainHadler;
 

	private int totalpage = 0;// 总页数
	private List<Merchandise> mers1 = new ArrayList<Merchandise>();
	private List<Merchandise> mers = new ArrayList<Merchandise>();
	// private ProListener listener;
	private int currentPage = 1;
	private SearchByBarcodeAdapter adapter;
	private String barcode = "";

	public SearchDialog(Context context, boolean flag, String barcode,CallBack callBack) {
		super(context);
		this.callback=callBack;
		this.barcode = barcode;
		mContext = context;
		this.flag = flag;
		initUI();
	}

	@SuppressLint("InlinedApi")
	private void initUI() {
		// this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
		View v = getLayoutInflater().inflate(R.layout.dialog_search, null);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(800,
				700);
		pulllv_pro = (XListView) v.findViewById(R.id.lv_retail);
		pulllv_pro.setPullLoadEnable(true);// 下拉刷新 /滚动
		pulllv_pro.setAutoLoadEnable(true);// 成功
		// 下拉刷新 /滚动 事件监听 IXListViewListener
		pulllv_pro.setXListViewListener(new IXListViewListener() {

					// 下拉: 显示第一页，清空集合，重新加载第一页
					@Override
					public void onRefresh() {
						if (!Sflag) {
							Sflag = true;
							// Toast.makeText(getBaseContext(), "下拉刷新.... ", 0).show();
							currentPage = 1;
							mers.clear();
							SearchGoods(barcode);// 从网络 数据
						}

					}

					// 滚动：添加下一页，刷新列表
					@Override
					public void onLoadMore() {
						if (!Sflag) {
							if((currentPage>=totalpage)&&totalpage!=0){
								stopTopOrButtom();
							}else{
							++currentPage;
							Sflag = true;
							SearchGoods(barcode);
							}
							
						}
						 
					}
				});
		pulllv_pro.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			//	Log.i("刘君杰", mers.get(position-1).getBarCode());
				 callback.CheckItem(mers.get(position-1),isBarcode);
				
			}
		});
		adapter = new SearchByBarcodeAdapter(mContext, mers);
		pulllv_pro.setAdapter(adapter);
		group = (RadioGroup) v.findViewById(R.id.rg_group);
		if (flag) {
			group.setVisibility(View.VISIBLE);
		} else {
			group.setVisibility(View.GONE);
		}
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.tv_mer1: {
					if(!isBarcode.equals("0")){
						isBarcode = "0";
						mers.clear();
						SearchGoods(barcode);
					}
				}
					break;
				case R.id.tv_mer2: {
					if(isBarcode.equals("0")){
						isBarcode = "1";
						mers.clear();
						SearchGoods(barcode);
					}
				}
					break;

				}
			}
		});
		mMainHadler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				switch (msg.what) {
				case AppConstantByUtil.SUCCESS:// 1
					Sflag=false;
					mers.addAll(mers1);
					
					adapter.update(mers);
					pulllv_pro.setAdapter(adapter);
					stopTopOrButtom();
					mers1.clear();
					break;
				case AppConstantByUtil.ERROR:// 0
					Sflag=false;
					TipsToast.makeText(mContext, msg.obj.toString(), 1).show();
					/*pulllv_pro.onRefreshComplete();
					SearchByBarcodeDialog.this.dismiss();*/
					stopTopOrButtom();
					break;

				default:
					break;
				}
			}
		};
		SearchGoods(barcode);
		this.addContentView(v, lp);
	}

	/*
	 * 模糊查询商品详情
	 */
	public void SearchGoods(String barcode) {
		HashMap<String, Object> mHashMap = new HashMap<String, Object>();
		mHashMap.put("merchandiseName", "");
		mHashMap.put("shopId", Constants.shop.getShopId());
		mHashMap.put("barCode", barcode);
		mHashMap.put("userId", Constants.shop.getUserId());
		mHashMap.put("terminalId", Constants.terminalId);
		mHashMap.put("loginSign", Constants.loginSign);
		mHashMap.put("currentPage", currentPage + "");
		mHashMap.put("pageSize", "10");
		mHashMap.put("isBarcode", isBarcode);
		mHashMap.put("sign", MD5Util.createSign(Constants.loginSign,
				Constants.terminalId, Constants.shop.getUserId(),
				Constants.shop.getShopId(), barcode));
		totalpage = currentPage;
		AsyncRunner
				.getInstance()
				.request(
						AppConstantByUtil.IP
								+ "community-api/shop/Merchandise/getMerMsgListForTer.do",
						"POST", mHashMap.entrySet(), new AsyncListener() {
							@Override
							public void onException(Object exceptionInfo) {

								sendMessageToHandler(AppConstantByUtil.ERROR,
										"网络异常");
							}

							@Override
							public void onComplete(String values) {
								// 需要得到商品详情
								try {

									JSONObject jsonObject = new JSONObject(
											values);
									String msg = jsonObject
											.getString("message");

									String code = jsonObject.getString("code");
									if (code.equals("1")) {
										JSONArray jsonArray = jsonObject
												.getJSONArray("merList");
										totalpage = Integer.parseInt(jsonObject
												.getString("totalpage"));
										if (Integer.parseInt(jsonObject
												.getString("totalpage")) >= currentPage) {

											for (int i = 0; i < jsonArray
													.length(); i++) {
												Merchandise mer = new Merchandise();
												// 下架，没有库存
												// if (flag) {
												AddItem(mer, jsonArray, i);
												mers1.add(mer);
					 
											}
										}

										if (mers1.size() > 0) {

											sendMessageToHandler(
													AppConstantByUtil.SUCCESS,
													msg);
										} else {
											sendMessageToHandler(
													AppConstantByUtil.ERROR,
													"没有相关商品");
										}

									} else {
										sendMessageToHandler(
												AppConstantByUtil.ERROR,
												"没有相关商品");
									}
								} catch (JSONException e) {
									e.printStackTrace();
									sendMessageToHandler(
											AppConstantByUtil.ERROR,
											AppConstantByUtil.app_json_exception_tip);
								}

							}
						});

	}

	public void AddItem(Merchandise mer, JSONArray jsonArray, int i)
			throws JSONException {
		mer.setClose(jsonArray.getJSONObject(i).getString("close"));
		mer.setBarCode(jsonArray.getJSONObject(i).getString("barCode"));
		mer.setMerchandiseName(jsonArray.getJSONObject(i).getString(
				"merchandiseName"));

		mer.setPrice(Double.parseDouble(jsonArray.getJSONObject(i).getString(
				"price"))
				+ "");
		mer.setInvertory(jsonArray.getJSONObject(i).getString("invertory"));
		mer.setMerchandiseId(jsonArray.getJSONObject(i).getString(
				"merchandiseId"));
		mer.setMerchandiseTypeId(jsonArray.getJSONObject(i).getString(
				"merchandiseTypeId"));
		mer.setMerchandiseTypeName(jsonArray.getJSONObject(i).getString(
				"merchandiseTypeName"));
		mer.setSales(jsonArray.getJSONObject(i).getString("sales"));
		mer.setParMerchandiseTypeName(jsonArray.getJSONObject(i).getString(
				"parMerchandiseTypeName"));
		mer.setParMerchandiseTypeId(jsonArray.getJSONObject(i).getString(
				"parMerchandiseTypeId"));
		/*
		 * mer.setCost(jsonArray .getJSONObject(i) .getString( "cost"));
		 */
		mer.setCost(jsonArray.getJSONObject(i).getString("cost"));
		mer.setNum(1);
	}

	public void sendMessageToHandler(int what, String msg) {

		Message message = mMainHadler.obtainMessage();
		message.what = what;
		message.obj = msg;
		mMainHadler.sendMessage(message);
	}
	private void stopTopOrButtom() {
		pulllv_pro.stopRefresh();// 隐藏顶部等待视图
		pulllv_pro.stopLoadMore();// 隐藏底部等待视图
		pulllv_pro.setRefreshTime(getTime());
	}
	private String getTime() {
		Date date = new Date();
		SimpleDateFormat formate = new SimpleDateFormat("MM月dd日 HH:mm");
		return formate.format(date);
	}
	public void setCallBack(CallBack callback) {
		this.callback = callback;
	}

	@Override
	public void onClick(View v) {
	}

	public interface CallBack {
		public void CheckItem(Merchandise mer,String code);
	}

}
