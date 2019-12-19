package com.songbao.sampo.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo.AppApplication;
import com.songbao.sampo.R;
import com.songbao.sampo.entity.GoodsEntity;
import com.songbao.sampo.widgets.RoundImageView;

import java.text.DecimalFormat;

/**
 * 订单商品适配器
 */
public class GoodsOrderAdapter extends AppBaseAdapter {

	private DecimalFormat df;
	private boolean isOnClick;

	public GoodsOrderAdapter(Context context, boolean isOnClick) {
		super(context);
		this.isOnClick = isOnClick;
		df = new DecimalFormat("0.00");
	}

	static class ViewHolder {
		ConstraintLayout item_main;
		RoundImageView iv_show;
		TextView tv_name, tv_attr, tv_number, tv_price, tv_after_sale, tv_comment;
		View view_fill;
	}

	/**代表了ListView中的一个item对象*/
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			convertView = View.inflate(context, R.layout.item_list_goods_order, null);
			
			holder = new ViewHolder();
			holder.item_main = convertView.findViewById(R.id.goods_order_item_main);
			holder.iv_show = convertView.findViewById(R.id.goods_order_item_iv_show);
			holder.tv_name = convertView.findViewById(R.id.goods_order_item_tv_name);
			holder.tv_attr = convertView.findViewById(R.id.goods_order_item_tv_attr);
			holder.tv_number = convertView.findViewById(R.id.goods_order_item_tv_number);
			holder.tv_price = convertView.findViewById(R.id.goods_order_item_tv_price);
			holder.view_fill = convertView.findViewById(R.id.goods_order_item_fill_tv_click);
			holder.tv_after_sale = convertView.findViewById(R.id.goods_order_item_tv_after_sale);
			holder.tv_comment = convertView.findViewById(R.id.goods_order_item_tv_comment);
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

		if (isOnClick) {
			holder.view_fill.setVisibility(View.VISIBLE);
			holder.tv_comment.setText(context.getString(R.string.order_comment));
			switch (data.getStatus()) {
				case 1: //评价+售后
					holder.tv_comment.setVisibility(View.VISIBLE);
					holder.tv_after_sale.setVisibility(View.VISIBLE);
					break;
				case 2: //追评+售后
					holder.tv_comment.setText(context.getString(R.string.order_comment_add));
					holder.tv_comment.setVisibility(View.VISIBLE);
					holder.tv_after_sale.setVisibility(View.VISIBLE);
					break;
				case 3: //售后
					holder.tv_after_sale.setVisibility(View.VISIBLE);
					break;
				case 4: //评价
					holder.tv_comment.setVisibility(View.VISIBLE);
					break;
				case 5: //追评
					holder.tv_comment.setText(context.getString(R.string.order_comment_add));
					holder.tv_comment.setVisibility(View.VISIBLE);
					break;
				default:
					holder.view_fill.setVisibility(View.GONE);
					holder.tv_comment.setVisibility(View.GONE);
					holder.tv_after_sale.setVisibility(View.GONE);
					break;
			}
			holder.tv_after_sale.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (apCallback != null) {
						apCallback.setOnClick(data, position, 1);
					}
				}
			});
			holder.tv_comment.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (apCallback != null) {
						if (data.getStatus() == 2 || data.getStatus() == 5) {
							apCallback.setOnClick(data, position, 3);
						} else {
							apCallback.setOnClick(data, position, 2);
						}
					}
				}
			});
			holder.item_main.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (apCallback != null) {
						apCallback.setOnClick(data, position, 0);
					}
				}
			});
		}
		return convertView;
	}

}
