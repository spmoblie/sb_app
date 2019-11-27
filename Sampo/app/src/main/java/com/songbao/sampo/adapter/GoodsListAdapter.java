package com.songbao.sampo.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo.AppApplication;
import com.songbao.sampo.R;
import com.songbao.sampo.entity.GoodsEntity;
import com.songbao.sampo.widgets.RoundImageView;

import java.text.DecimalFormat;

/**
 * 订单商品列表适配器
 */
public class GoodsListAdapter extends AppBaseAdapter {

	private DecimalFormat df;

	public GoodsListAdapter(Context context) {
		super(context);
		df = new DecimalFormat("0.00");
	}

	static class ViewHolder {
		ConstraintLayout item_main;
		RoundImageView iv_show;
		TextView tv_name, tv_attr, tv_number, tv_price;
	}

	/**代表了ListView中的一个item对象*/
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			convertView = View.inflate(context, R.layout.item_list_goods, null);
			
			holder = new ViewHolder();
			holder.item_main = convertView.findViewById(R.id.list_item_goods_main);
			holder.iv_show = convertView.findViewById(R.id.list_item_goods_iv_show);
			holder.tv_name = convertView.findViewById(R.id.list_item_goods_tv_name);
			holder.tv_attr = convertView.findViewById(R.id.list_item_goods_tv_attr);
			holder.tv_number = convertView.findViewById(R.id.list_item_goods_tv_number);
			holder.tv_price = convertView.findViewById(R.id.list_item_goods_tv_price);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		final GoodsEntity data = (GoodsEntity) mDataList.get(position);

		Glide.with(AppApplication.getAppContext())
				.load(data.getPicUrl())
				.apply(AppApplication.getShowOptions())
				.into(holder.iv_show);

		holder.tv_name.setText(data.getName());
		holder.tv_attr.setText(data.getAttribute());
		holder.tv_number.setText(context.getString(R.string.cart_goods_num, data.getNumber()));
		holder.tv_price.setText(context.getString(R.string.pay_rmb, df.format(data.getPrice())));

		holder.item_main.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (apCallback != null) {
					apCallback.setOnClick(data, position, 1);
				}
			}
		});
		return convertView;
	}

}
