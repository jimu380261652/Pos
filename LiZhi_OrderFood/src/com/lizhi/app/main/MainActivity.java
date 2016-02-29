package com.lizhi.app.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.duiduifu.view.TipsToast;
import com.lizhi.fragment.AccountFragment;
import com.lizhi.fragment.CashierFragment;
import com.lizhi.fragment.GoodsFragment;
import com.lizhi.fragment.HomeFragment;
import com.lizhi.fragment.IndexFragment;
import com.lizhi.fragment.MemberFragment;
import com.lizhi.fragment.MessageFragment;
import com.lizhi.fragment.OrderFoodFragment;
import com.lizhi.fragment.OrderFragment;
import com.lizhi.fragment.PosFragment;
import com.lizhi.fragment.ShopManagerFragment;
import com.lizhi.push.MyApplication;
import com.lizhi.service.HeartService.HeartSyncCallback;
import com.lizhi.service.WindowService;
import com.lizhi.util.Constants;
import com.pos.lizhiorder.R;

/**
 * 
 * @ClassName: MainActivity.java
 * 
 * @Description: 主界面
 * 
 * 
 * @date 2015-5-12 上午18:06:09
 * 
 */
public class MainActivity extends FragmentActivity implements HeartSyncCallback {
	private Context context;
	private String auths = "";
	private HomeFragment fra_home;
	private AccountFragment fra_account;

//	private CashierFragment fra_cashier;
	private OrderFoodFragment fra_cashier;
	private MemberFragment fra_member;
	private MessageFragment fra_message;
	private PosFragment fra_pos;
	private OrderFragment fra_order;
	private ShopManagerFragment fra_shop;
	private GoodsFragment fra_goods;
	private boolean flag = false;
	private Button home, cashier, account, member, order, message, pos, goods,
			shop;

	private String userId, loginSign;
	FragmentManager fm;
	private int pre_index = 1;// 上一个fragment的索引
	public final static int PAYRESULTREQUESTCODE = 8;
	public final static int PAYRESULRESULTTCODE = 8;
//	private ICloudPay mCupService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		context = this;
		((MyApplication) getApplication()).addActivity(this);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		setContentView(R.layout.activity_main);
		userId = getIntent().getStringExtra("userId");
		loginSign = getIntent().getStringExtra("loginSign");
		pre_index = getIntent().getIntExtra("index", 1);
		auths = Constants.shop.getAuths();
		fm = getSupportFragmentManager();
		initView();
		initFragment();

		// Intent intent = new Intent();
		// intent.setClass(this, WindowService.class);
		// this.startService(intent);
		// pos机设备初始化
	}

	private void initView() {
		home = (Button) findViewById(R.id.btn_main_home);
		cashier = (Button) findViewById(R.id.btn_main_cashier);
		account = (Button) findViewById(R.id.btn_main_account);
		member = (Button) findViewById(R.id.btn_main_member);

		order = (Button) findViewById(R.id.btn_main_order);
		message = (Button) findViewById(R.id.btn_main_message);
		pos = (Button) findViewById(R.id.btn_main_pos);

		goods = (Button) findViewById(R.id.btn_main_goods);
		shop = (Button) findViewById(R.id.btn_main_shop);
		shop.setOnClickListener(l);
		goods.setOnClickListener(l);

		home.setOnClickListener(l);
		cashier.setOnClickListener(l);
		account.setOnClickListener(l);
		member.setOnClickListener(l);

		order.setOnClickListener(l);
		message.setOnClickListener(l);
		pos.setOnClickListener(l);
	}

	@Override
	protected void onResume() {
		super.onResume();

		try {
			// ISystemInf sysInf = SysImplFactory.getSystemInf();
			// sysInf.closePinpadKeyboard();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private OnClickListener l = new OnClickListener() {

		@Override
		public void onClick(View arg0) {

			switch (arg0.getId()) {
			case R.id.btn_main_home: {
				// setTab(0);

				Intent intent = new Intent(context, HomeActivity.class);
				startActivity(intent);
			}
				break;
			case R.id.btn_main_cashier: {
				if(auths.contains("pos_nogoods")){
					setTab(1);
				}else{
					TipsToast.makeText(context, "该用户没有这个权限", 1).show();
				}
				
			}
				break;
			case R.id.btn_main_account: {
				if (auths.contains("pos_account")) {
					setTab(2);
				} else {
					TipsToast.makeText(context, "该用户没有这个权限", 1).show();
				}
			}
				break;
			case R.id.btn_main_member: {
				if (auths.contains("pos_member")) {
					setTab(3);
				} else {
					TipsToast.makeText(context, "该用户没有这个权限", 1).show();
				}
			}
				break;

			case R.id.btn_main_order: {
				if (auths.contains("pos_orders")) {
					setTab(5);
				} else {
					TipsToast.makeText(context, "该用户没有这个权限", 1).show();
				}
			}
				break;
			case R.id.btn_main_message: {
				if (auths.contains("pos_msg")) {
					setTab(6);
				} else {
					TipsToast.makeText(context, "该用户没有这个权限", 1).show();
				}
			}
				break;
			case R.id.btn_main_pos: {
				if (auths.contains("pos_pos")) {
					setTab(7);
				} else {
					TipsToast.makeText(context, "该用户没有这个权限", 1).show();
				}
			}
				break;
			case R.id.btn_main_shop: {
				if (auths.contains("pos_shop")) {
					setTab(8);
				} else {
					TipsToast.makeText(context, "该用户没有这个权限", 1).show();
				}
			}
				break;
			case R.id.btn_main_goods: {
				// 如果是无商品收银台就无打开该功能的权限
				if (auths.contains("pos_goods")&&!Constants.shop.getShopTypeId().equals("1001")) {
					setTab(10);
				} else {
					TipsToast.makeText(context, "该用户没有这个权限", 1).show();
				}
			}
				break;
			}
		}
	};

	private void initFragment() {
		fra_account = new AccountFragment();
		fra_cashier = new OrderFoodFragment();
		fra_home = new HomeFragment();
		fra_member = new MemberFragment();
		fra_message = new MessageFragment();
		fra_message.setCallback(new MessageFragment.Callback() {

			@Override
			public void callback() {
				setTab(5);

			}
		});
		fra_order = new OrderFragment();
		fra_shop = new ShopManagerFragment();
		fra_pos = new PosFragment();

		fra_goods = new GoodsFragment();

		if (pre_index == 1) {

			fm.beginTransaction().add(R.id.fl_main_content, fra_cashier)
					.commit();
			cashier.setBackgroundResource(R.drawable.ic_checkstand);
		} else if (pre_index == 2) {
			fm.beginTransaction().add(R.id.fl_main_content, fra_account)
					.commit();
			account.setBackgroundResource(R.drawable.ic_account);
		} else if (pre_index == 3) {
			fm.beginTransaction().add(R.id.fl_main_content, fra_member)
					.commit();
			member.setBackgroundResource(R.drawable.ic_member);
		} else if (pre_index == 5) {
			fm.beginTransaction().add(R.id.fl_main_content, fra_order).commit();
			order.setBackgroundResource(R.drawable.ic_order);
		} else if (pre_index == 6) {
			fm.beginTransaction().add(R.id.fl_main_content, fra_message)
					.commit();
			message.setBackgroundResource(R.drawable.ic_inform);
		} else if (pre_index == 7) {
			fm.beginTransaction().add(R.id.fl_main_content, fra_pos).commit();
			pos.setBackgroundResource(R.drawable.ic_pos);
		} else if (pre_index == 8) {
			fm.beginTransaction().add(R.id.fl_main_content, fra_shop).commit();
			shop.setBackgroundResource(R.drawable.icon_manu_dianpu);
		} else if (pre_index == 10) {
			fm.beginTransaction().add(R.id.fl_main_content, fra_goods).commit();
			goods.setBackgroundResource(R.drawable.icon_manu_goods);
		}
	}

	private void setTab(int position) {
		home.setBackgroundResource(R.drawable.ic_home_no);
		cashier.setBackgroundResource(R.drawable.ic_checkstand_no);
		account.setBackgroundResource(R.drawable.ic_account_no);
		member.setBackgroundResource(R.drawable.ic_member_no);

		order.setBackgroundResource(R.drawable.ic_order_no);
		message.setBackgroundResource(R.drawable.ic_inform_no);
		pos.setBackgroundResource(R.drawable.ic_pos_no);
		goods.setBackgroundResource(R.drawable.icon_manu_goods_no);
		shop.setBackgroundResource(R.drawable.icon_manu_function_no);
		hideTab();
		// if(position!=7)
		HideKeyboard();
		switch (position) {
		case 0: {
			home.setBackgroundResource(R.drawable.ic_home);
			if (fra_home.isAdded()) {
				fm.beginTransaction().show(fra_home).commit();
			} else {
				fm.beginTransaction().add(R.id.fl_main_content, fra_home)
						.commit();
			}

		}
			break;
		case 1: {
			cashier.setBackgroundResource(R.drawable.ic_checkstand);
			if (fra_cashier.isAdded()) {
				fm.beginTransaction().show(fra_cashier).commit();
			} else {
				fm.beginTransaction().add(R.id.fl_main_content, fra_cashier)
						.commit();
			}

		}
			break;
		case 2: {
			account.setBackgroundResource(R.drawable.ic_account);
			if (fra_account.isAdded()) {
				fm.beginTransaction().show(fra_account).commit();
			} else {
				fm.beginTransaction().add(R.id.fl_main_content, fra_account)
						.commit();
			}

		}
			break;
		case 3: {
			member.setBackgroundResource(R.drawable.ic_member);
			if (fra_member.isAdded()) {
				fm.beginTransaction().show(fra_member).commit();
			} else {
				fm.beginTransaction().add(R.id.fl_main_content, fra_member)
						.commit();
			}

		}
			break;

		case 5: {
			order.setBackgroundResource(R.drawable.ic_order);
			if (fra_order.isAdded()) {
				fm.beginTransaction().show(fra_order).commit();
			} else {
				fm.beginTransaction().add(R.id.fl_main_content, fra_order)
						.commit();
			}
			if (flag) {
				fra_order.setCurrent(Constants.page);
			}
			flag = true;
		}
			break;
		case 6: {
			message.setBackgroundResource(R.drawable.ic_inform);
			if (fra_message.isAdded()) {

				fm.beginTransaction().show(fra_message).commit();
			} else {
				fm.beginTransaction().add(R.id.fl_main_content, fra_message)
						.commit();
			}

		}
			break;
		case 7: {
			pos.setBackgroundResource(R.drawable.ic_pos);
			if (fra_pos.isAdded()) {
				fm.beginTransaction().show(fra_pos).commit();
			} else {
				fm.beginTransaction().add(R.id.fl_main_content, fra_pos)
						.commit();
			}

		}
			break;
		case 8: {
			shop.setBackgroundResource(R.drawable.icon_manu_dianpu);
			if (fra_shop.isAdded()) {
				fm.beginTransaction().show(fra_shop).commit();
			} else {
				fm.beginTransaction().add(R.id.fl_main_content, fra_shop)
						.commit();
			}

		}
			break;
		case 10: {
			goods.setBackgroundResource(R.drawable.icon_manu_goods);
			if (fra_goods.isAdded()) {
				fm.beginTransaction().show(fra_goods).commit();
			} else {
				fm.beginTransaction().add(R.id.fl_main_content, fra_goods)
						.commit();
			}
		}
			break;
		}
		fm.beginTransaction().commit();
		pre_index = position;
	}

	// 隐藏上一个Fragment
	private void hideTab() {
		switch (pre_index) {
		case 0: {
			fm.beginTransaction().hide(fra_home).commit();
		}
			break;
		case 1: {
			fm.beginTransaction().hide(fra_cashier).commit();
		}
			break;
		case 2: {
			fm.beginTransaction().hide(fra_account).commit();
		}
			break;
		case 3: {
			fm.beginTransaction().hide(fra_member).commit();
		}
			break;

		case 5: {
			fm.beginTransaction().hide(fra_order).commit();
		}
			break;
		case 6: {
			fm.beginTransaction().hide(fra_message).commit();
		}
			break;
		case 7: {
			fm.beginTransaction().hide(fra_pos).commit();
		}
			break;
		case 8: {
			fm.beginTransaction().hide(fra_shop).commit();
		}
			break;
		case 10: {
			fm.beginTransaction().hide(fra_goods).commit();
		}
			break;
		}
	}

	// public void showPayResultActivity(String CardNum,String payAmount,String
	// memo,String result,boolean isUnBundler){
	// Intent intent=new Intent(this,PayResultActivity.class);
	// intent.putExtra("CardNum", CardNum);
	// intent.putExtra("payAmount", payAmount);
	// intent.putExtra("memo", memo);
	// intent.putExtra("result", result);
	// this.startActivityForResult(intent, PAYRESULTREQUESTCODE);
	//
	//
	// }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		// ((IndexFragment)mIndexFragment).loadReadCount();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_HOME) {

			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		// try {
		// Intent intent = new Intent();
		// intent.setAction("com.duiduifu.retail.sync.close");//发出自定义广播
		// this.sendBroadcast(intent);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// this.finish();
	}

	@Override
	protected void onDestroy() {

		System.out.println("===========MainActivity-Destroy!	");

		try {
			// this.unbindService(conn);

			// ((MyApplication)getApplication()).removeOnly(MainActivity.class);

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Intent intent = new Intent();
			intent.setClass(this, WindowService.class);
			this.stopService(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onDestroy();
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == IndexFragment.MESSAGE_LIST_REQUEST
				&& resultCode == MessageListActivity.MESSAGE_LIST_RESULT) {
			// if(mIndexFragment!=null){
			// // ((IndexFragment)mIndexFragment).setMessageHint();
			// }
		}
	};

	// ============= Service回调 ==============

	@Override
	public void setHintNumber(int messageNum, int orderNum) {
		// if(mIndexFragment!=null){
		// ((IndexFragment)mIndexFragment).setHint(orderNum, messageNum);
		// }
	}

	private void HideKeyboard() {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (((Activity) context).getCurrentFocus() != null)
			imm.hideSoftInputFromWindow(((Activity) context).getCurrentFocus()
					.getWindowToken(), 0);
	}

}
