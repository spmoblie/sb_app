package com.songbao.sampo_c.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.entity.GoodsAttrEntity;
import com.songbao.sampo_c.entity.GoodsEntity;
import com.songbao.sampo_c.widgets.RoundImageView;

import java.text.DecimalFormat;

/**
 * 提交订单商品列表适配器
 */
public class GoodsOrder2Adapter extends AppBaseAdapter {

	private DecimalFormat df;

	public GoodsOrder2Adapter(Context context) {
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
			convertView = View.inflate(context, R.layout.item_list_goods_order_2, null);
			
			holder = new ViewHolder();
			holder.item_main = convertView.findViewById(R.id.goods_order_2_item_main);
			holder.iv_show = convertView.findViewById(R.id.goods_order_2_item_iv_show);
			holder.tv_name = convertView.findViewById(R.id.goods_order_2_item_tv_name);
			holder.tv_attr = convertView.findViewById(R.id.goods_order_2_item_tv_attr);
			holder.tv_price = convertView.findViewById(R.id.goods_order_2_item_tv_price);
			holder.tv_number = convertView.findViewById(R.id.goods_order_2_item_tv_number);
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
		holder.tv_price.setText(df.format(data.getPrice()));

		GoodsAttrEntity attrEn = data.getAttrEn();
		if (attrEn != null) {
			holder.tv_attr.setText(attrEn.getAttrNameStr());
			holder.tv_number.setText(context.getString(R.string.cart_goods_num, attrEn.getBuyNum()));
		}

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
