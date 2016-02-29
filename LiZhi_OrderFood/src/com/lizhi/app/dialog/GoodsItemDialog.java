package com.lizhi.app.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pos.lizhiorder.R;

public class GoodsItemDialog extends BaseDialog implements
		android.view.View.OnClickListener {
	private Context context;
	private ImageView image;
	private int drawable;
	private String name;
	private Button add;
	private TextView tv_name,tv_num;
	private GoodsItemListener listen;
	private ImageView iv_add,iv_minus;
	private int num=1;
	public GoodsItemDialog(Context context) {
		super(context);
		this.context = context;
		initUI();
	}

	public GoodsItemDialog(Context context, int id, String name,
			GoodsItemListener listen) {// 图片id
		super(context);
		this.listen = listen;
		this.name = name;
		drawable = id;
		this.context = context;
		initUI();
	}

	public void initUI() {

		View v = getLayoutInflater().inflate(R.layout.dialog_goods_item, null,
				false);
		tv_num=(TextView) v.findViewById(R.id.tv_num);
		iv_add=(ImageView) v.findViewById(R.id.iv_add);
		iv_minus=(ImageView) v.findViewById(R.id.iv_minus);
		iv_add.setOnClickListener(this);
		iv_minus.setOnClickListener(this);
		image = (ImageView) v.findViewById(R.id.iv_image);
		image.setImageResource(drawable);
		tv_name = (TextView) v.findViewById(R.id.tv_name);
		tv_name.setText(name);
		add = (Button) v.findViewById(R.id.btn_add);
		add.setOnClickListener(this);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(900,
				600);
		this.addContentView(v, lp);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_add:{
			num++;
			tv_num.setText(num+"");
		}
		break;
		case R.id.iv_minus:{
			if(num>1){
				num--;
				tv_num.setText(num+"");
			}
		}
		break;
		case R.id.btn_add: {
			listen.callback(name,num);
			 
			GoodsItemDialog.this.dismiss();
		}
			break;
		default:
			break;
		}
	}

	public interface GoodsItemListener {
		public void callback(String name,int num);
	 
	}

}
