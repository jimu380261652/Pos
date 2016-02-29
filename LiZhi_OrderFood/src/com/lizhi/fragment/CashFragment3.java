package com.lizhi.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;

import com.duifudui.mutiradiogroup.MutilRadioGroup;
import com.pos.lizhiorder.R;

//无商品收银台
public class CashFragment3 extends NoBugFragment implements OnClickListener {
	private Context context;
	private MutilRadioGroup paytype;
	private RadioButton rb_cash, rb_appliy, rb_campus, rb_card, rb_wx,
			rb_apple;
	private Button btn10, btn20, btn50, btn100, btn7, btn4, btn1, btn8, btn5,
			btn2, btn0, btn9, btn6, btn3, btnpoint, btnEnter;
	private ImageButton btndelete;
	private Button bill;
	private EditText et_account, et_money, et_discount, et_factmoney;
	private String pay = "cash";

	@Override
	public View oncreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = View.inflate(get_Activity(), R.layout.fragment_cash3, null);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		context = get_Activity();
		initview();
	}

	private void initview() {
		paytype = (MutilRadioGroup) view.findViewById(R.id.rg_paytype);
		paytype.setOnCheckedChangeListener(new MutilRadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(MutilRadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_cash:
					pay = "cash";
					break;
				case R.id.rb_appliy:
					pay = "appliy";
					break;
				case R.id.rb_campus:
					pay = "campus";
					break;
				case R.id.rb_card:
					pay = "card";
					break;
				case R.id.rb_wx:
					pay = "wx";
					break;
				case R.id.rb_apple:
					pay = "apple";
					break;
				default:
					break;
				}

			}
		});
		bill = (Button) view.findViewById(R.id.btn_bill);
		bill.setOnClickListener(this);

		btn10 = (Button) view.findViewById(R.id.btn10);
		btn20 = (Button) view.findViewById(R.id.btn20);
		btn50 = (Button) view.findViewById(R.id.btn50);
		btn100 = (Button) view.findViewById(R.id.btn100);
		btn7 = (Button) view.findViewById(R.id.btn7);
		btn4 = (Button) view.findViewById(R.id.btn4);
		btn1 = (Button) view.findViewById(R.id.btn1);
		btn8 = (Button) view.findViewById(R.id.btn8);
		btn5 = (Button) view.findViewById(R.id.btn5);
		btn2 = (Button) view.findViewById(R.id.btn2);
		btn0 = (Button) view.findViewById(R.id.btn0);
		btn9 = (Button) view.findViewById(R.id.btn9);
		btn6 = (Button) view.findViewById(R.id.btn6);
		btn3 = (Button) view.findViewById(R.id.btn3);
		btnpoint = (Button) view.findViewById(R.id.btnpoint);
		btnEnter = (Button) view.findViewById(R.id.btnEnter);
		btndelete = (ImageButton) view.findViewById(R.id.btndelete);

		btn10.setOnClickListener(this);
		btn20.setOnClickListener(this);
		btn50.setOnClickListener(this);
		btn100.setOnClickListener(this);
		btn7.setOnClickListener(this);
		btn4.setOnClickListener(this);
		btn1.setOnClickListener(this);
		btn8.setOnClickListener(this);
		btn5.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn0.setOnClickListener(this);
		btn9.setOnClickListener(this);
		btn6.setOnClickListener(this);
		btn3.setOnClickListener(this);
		btnpoint.setOnClickListener(this);
		btnEnter.setOnClickListener(this);
		btndelete.setOnClickListener(this);

		et_money = (EditText) view.findViewById(R.id.et_money);
		et_discount = (EditText) view.findViewById(R.id.et_discount);
		et_factmoney = (EditText) view.findViewById(R.id.et_factmoney);
		et_account = (EditText) view.findViewById(R.id.et_account);
		et_account.setOnClickListener(this);
		et_money.setOnClickListener(this);
		et_discount.setOnClickListener(this);
		et_factmoney.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_bill:

			break;
		case R.id.et_account:
			ChangEditBackground();
			break;
		case R.id.et_money:
			ChangEditBackground();
			et_money.setBackgroundResource(R.drawable.bg_edit_round_orage);
			break;
		case R.id.et_discount:
			ChangEditBackground();
			et_discount.setBackgroundResource(R.drawable.bg_edit_round_orage);
			break;
		case R.id.et_factmoney:
			ChangEditBackground();
			et_factmoney.setBackgroundResource(R.drawable.bg_edit_round_orage);
			break;
		default:
			break;
		}
	}

	private void ChangEditBackground() {

		et_money.setBackgroundResource(R.drawable.bg_edit_round);
		et_discount.setBackgroundResource(R.drawable.bg_edit_round);
		et_factmoney.setBackgroundResource(R.drawable.bg_edit_round);
	}

}
