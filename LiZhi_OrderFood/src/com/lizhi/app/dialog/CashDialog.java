package com.lizhi.app.dialog;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duiduifu.view.TipsToast;
import com.lizhi.bean.Merchandise;
import com.pos.lizhiorder.R;

public class CashDialog extends BaseDialog implements
		android.view.View.OnClickListener {
	private Context context;
	private EditText et_fact;
	private TextView btn_ok;
	private TextView tv_dibs, should_tv;
	private CashListener listener;
	private double money = 0;
	private DecimalFormat df;
	private boolean flag=false;
	private List<Merchandise> mers = new ArrayList<Merchandise>();

	public CashDialog(Context context, List<Merchandise> mers,
			CashListener listener) {
		super(context);
		this.listener = listener;
		this.mers = mers;
		this.context = context;
		initUI();
	}

	public interface CashListener {
		public void clear(String money, String should);
	}

	public void initUI() {
		View v = getLayoutInflater().inflate(R.layout.dialog_cash, null);
		btn_ok = (TextView) v.findViewById(R.id.btn_ok);
		// btn_cancle = (TextView) v.findViewById(R.id.btn_cancle);
		// btn_cancle.setOnClickListener(this);
		btn_ok.setOnClickListener(this);
		// et_should = (EditText) v.findViewById(R.id.et_should);
		et_fact = (EditText) v.findViewById(R.id.et_fact);
		tv_dibs = (TextView) v.findViewById(R.id.tv_dibs);
		should_tv = (TextView) v.findViewById(R.id.should_tv);

		for (int i = 0; i < mers.size(); i++) {
			money += Double.parseDouble(mers.get(i).getPrice())
					* mers.get(i).getNum();
		}
		df = new DecimalFormat("#.##");
		should_tv.setText(df.format(money));
		tv_dibs.setText("-" + df.format(money));
		et_fact.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER
						&& event.getAction() == KeyEvent.ACTION_DOWN&&et_fact.getText().toString().length()>0) {
					InputMethodManager imm = (InputMethodManager) v
							.getContext().getSystemService(
									Context.INPUT_METHOD_SERVICE);
					 
					String barcode = et_fact.getText().toString();
					/*
					 * if (TextUtils.isEmpty(barcode) ||
					 * !(barcode.matches("^[0-9]+(\\.[0-9]{1,2})?$"))) {
					 * TipsToast.makeText(context, "请输入正确的实收款", 1).show(); }
					 * else {
					 */
					if (Double.parseDouble(barcode)
							- money >= 0&&imm.isActive()) {
						imm.hideSoftInputFromWindow(
								v.getApplicationWindowToken(), 0);
						double f1 = new BigDecimal(Double.parseDouble(barcode)
								- money).setScale(2, BigDecimal.ROUND_HALF_UP)
								.doubleValue();
						tv_dibs.setText(f1 + "");
						flag=true;
					} else {
						flag=false;
						TipsToast.makeText(context, "实收款应该不小于应付款", 1).show();
					}

					return false;
				}

				return false;
			}
		});

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(700,
				400);
		this.addContentView(v, lp);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_ok&&flag) {
			if (!TextUtils.isEmpty(et_fact.getText().toString())) {

				listener.clear(Double.parseDouble(et_fact.getText().toString())
						- money + "", money + "");
				CashDialog.this.dismiss();
				InputMethodManager imm = (InputMethodManager) context
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(et_fact.getWindowToken(), 0);
			} else {
				TipsToast.makeText(context, "输入有误", 1).show();
			}

		}
	}

}
