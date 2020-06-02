package com.songbao.sampo_b.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.entity.GoodsEntity;
import com.songbao.sampo_b.utils.ClickUtils;
import com.songbao.sampo_b.widgets.RoundImageView;

import java.text.DecimalFormat;

/**
 * 订单展示商品列表适配器
 */
public class GoodsOrderShowAdapter extends AppBaseAdapter<GoodsEntity> {

	private DecimalFormat df;
	private boolean isDetail = false;

	public GoodsOrderShowAdapter(Context context) {
		super(context);
		this.df = new DecimalFormat("0.00");
	}

	public void showDetailView(boolean isDetail) {
		this.isDetail = isDetail;
	}

	static class ViewHolder {
		ConstraintLayout item_main;
		RoundImageView iv_show;
		TextView tv_name, tv_price_one, tv_price_two, tv_number, tv_detail;
	}

	/**
	 * 代表了ListView中的一个item对象
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_list_goods_order_show, null);

			holder = new ViewHolder();
			holder.item_main = convertView.findViewById(R.id.order_goods_item_main);
			holder.iv_show = convertView.findViewById(R.id.order_goods_item_iv_show);
			holder.tv_name = convertView.findViewById(R.id.order_goods_item_tv_goods_name);
			holder.tv_price_one = convertView.findViewById(R.id.order_goods_item_tv_price_one);
			holder.tv_price_two = convertView.findViewById(R.id.order_goods_item_tv_price_two);
			holder.tv_number = convertView.findViewById(R.id.order_goods_item_tv_number);
			holder.tv_detail = convertView.findViewById(R.id.order_goods_item_tv_detail);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 绑定View
		final GoodsEntity data = mDataList.get(position);

		if (data != null) {
			Glide.with(AppApplication.getAppContext())
					.load(data.getPicUrl())
					.apply(AppApplication.getShowOptions())
					.into(holder.iv_show);

			holder.tv_name.setText(data.getName());
			holder.tv_price_one.setText(context.getString(R.string.order_rmb, df.format(data.getOnePrice())));
			if (data.getTwoPrice() > 0 && data.getTwoPrice() != data.getOnePrice()) {
				holder.tv_price_one.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
				holder.tv_price_one.setTextColor(context.getResources().getColor(R.color.debar_text_color));
				holder.tv_price_two.setText(context.getString(R.string.order_rmb, df.format(data.getTwoPrice())));
			} else {
				holder.tv_price_one.setTextColor(context.getResources().getColor(R.color.app_color_gray_5));
				holder.tv_price_two.setText("");
			}
			holder.tv_number.setText(context.getString(R.string.order_goods_num, data.getNumber()));
		}

		if (isDetail) {
			holder.tv_detail.setVisibility(View.VISIBLE);
			holder.item_main.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (ClickUtils.isDoubleClick(v.getId())) return;
					if (apCallback != null) {
						apCallback.setOnClick(data, position, 0);
					}
				}
			});
		} else {
			holder.tv_detail.setVisibility(View.GONE);
		}

		return convertView;
	}

}
