package com.songbao.sampo_b.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.entity.GoodsEntity;
import com.songbao.sampo_b.utils.CommonTools;
import com.songbao.sampo_b.widgets.MyHorizontalScrollView;
import com.songbao.sampo_b.widgets.RoundImageView;

import java.text.DecimalFormat;
import java.util.List;

/**
 * 提交订单商品列表适配器
 */
public class GoodsOrderEditAdapter extends AppBaseAdapter<GoodsEntity> {

	private int scrollPos = -1;
	private DecimalFormat df;
	private LinearLayout.LayoutParams lp;

	public GoodsOrderEditAdapter(Context context) {
		super(context);
		df = new DecimalFormat("0.00");

		lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.width = AppApplication.screen_width - CommonTools.dpToPx(context, 57);
	}

	@Override
	public void updateData(List<GoodsEntity> data) {
		this.scrollPos = -1;
		super.updateData(data);
	}

	public void reset(int scrollPos) {
		this.scrollPos = scrollPos;
		notifyDataSetChanged();
	}

	static class ViewHolder {
		MyHorizontalScrollView item_sv_main;
		ConstraintLayout item_left_main;
		RoundImageView iv_show;
		TextView tv_name, tv_price, tv_number, tv_edit, tv_delete;
	}

	/**
	 * 代表了ListView中的一个item对象
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_list_goods_order_edit, null);

			holder = new ViewHolder();
			holder.item_sv_main = convertView.findViewById(R.id.order_goods_item_hsv_main);
			holder.item_left_main = convertView.findViewById(R.id.order_goods_item_left_main);
			holder.iv_show = convertView.findViewById(R.id.order_goods_item_iv_show);
			holder.tv_name = convertView.findViewById(R.id.order_goods_item_tv_goods_name);
			holder.tv_price = convertView.findViewById(R.id.order_goods_item_tv_price);
			holder.tv_number = convertView.findViewById(R.id.order_goods_item_tv_number);
			holder.tv_edit = convertView.findViewById(R.id.order_goods_item_tv_edit);
			holder.tv_delete = convertView.findViewById(R.id.order_goods_item_tv_delete);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 绑定View
		final GoodsEntity data = mDataList.get(position);

		holder.item_left_main.setLayoutParams(lp); //适配屏幕宽度
		/*if (scrollPos != position) { //对非当前滚动项进行复位
			holder.item_sv_main.smoothScrollTo(0, holder.item_sv_main.getScrollY());
		}
		holder.item_sv_main.setOnScrollStateChangedListener(new MyHorizontalScrollView.ScrollViewListener() {
			@Override
			public void onScrollChanged(MyHorizontalScrollView.ScrollType scrollType) {
				switch (scrollType) {
					case TOUCH_SCROLL: //手指拖动滚动
						break;
					case FLING: //滚动
						break;
					case IDLE: //滚动停止
						if (scrollPos != position) { //非同一滚动项
							apCallback.setOnClick(data, position, 6); //滚动
						}
						break;
				}
			}
		});*/

		if (data != null) {
			Glide.with(AppApplication.getAppContext())
					.load(data.getPicUrl())
					.apply(AppApplication.getShowOptions())
					.into(holder.iv_show);

			holder.tv_name.setText(data.getName());
			holder.tv_price.setText(df.format(data.getPrice()));
			holder.tv_number.setText(context.getString(R.string.order_goods_num, data.getNumber()));
		}

		holder.tv_edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (apCallback != null) {
					apCallback.setOnClick(data, position, 1);
				}
			}
		});
		holder.tv_delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (apCallback != null) {
					apCallback.setOnClick(data, position, 2);
				}
			}
		});

		return convertView;
	}

}
