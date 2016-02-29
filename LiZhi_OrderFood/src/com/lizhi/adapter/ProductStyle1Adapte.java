package com.lizhi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lizhi.bean.Temporary;
import com.lizhi.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pos.lizhiorder.R;

public class ProductStyle1Adapte extends BaseAdapter {
	private Context context;
	private List<Temporary> list = new ArrayList<Temporary>();
	private ProductStyle1AdapteCallBack callback;

	public ProductStyle1Adapte(Context context, List<Temporary> name,
			ProductStyle1AdapteCallBack callback) {
		this.context = context;
		this.callback = callback;
		if (name != null)
			this.list = name;
	}
	
	public ProductStyle1Adapte(Context context, List<Temporary> name) {
		this.context = context;
		if (name != null)
			this.list = name;
	}

	public void update(List<Temporary> name) {
		list = name;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (getCount() > 0)
			return list.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.adapter_product_style1, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.img_minus = (ImageView) convertView.findViewById(R.id.img_minus);
			holder.img_add = (ImageView) convertView.findViewById(R.id.img_add);
			holder.img_logo = (ImageView) convertView.findViewById(R.id.img_logo);
			holder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (list.get(position) != null) {
			if(StringUtil.isEmpty(list.get(position).getLogo())){
				ImageLoader.getInstance().displayImage(list.get(position).getLogo(),
						holder.img_logo);
			}else if(list.get(position).getNativeLogo() != 0){
				holder.img_logo.setImageResource(list.get(position).getNativeLogo());
			}
			holder.tv_num.setText(list.get(position).getNum() + "");
			holder.tv_name.setText(list.get(position).getName());
			holder.img_add.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int num = Integer.parseInt(holder.tv_num.getText().toString());
					num++;
					holder.tv_num.setText(num + "");
//					callback.changenum(position, num, list);
				}
			});
			holder.img_minus.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int num = Integer.parseInt(holder.tv_num.getText().toString());
					if (num > 1) {
						num--;
						holder.tv_num.setText(num + "");
//						callback.changenum(position, num, list);
					}
				}
			});
		}

		return convertView;
	}

	private class ViewHolder {
		private TextView tv_name, tv_num;
		private ImageView img_minus, img_add,img_logo;		
	}

	public interface ProductStyle1AdapteCallBack {
		public void changenum(int position, int num, List<Temporary> list);

	}

}
