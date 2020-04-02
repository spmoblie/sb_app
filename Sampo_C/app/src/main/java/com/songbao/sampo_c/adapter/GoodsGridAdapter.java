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
import com.songbao.sampo_c.entity.GoodsEntity;
import com.songbao.sampo_c.utils.ClickUtils;
import com.songbao.sampo_c.widgets.RoundImageView;

/**
 * 商品表格适配器
 */
public class GoodsGridAdapter extends AppBaseAdapter<GoodsEntity> {

	public GoodsGridAdapter(Context context) {
		super(context);
	}

	static class ViewHolder {
		ConstraintLayout item_main;
		RoundImageView iv_goods_img;
		TextView tv_goods_name;
	}

	/**代表了ListView中的一个item对象*/
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			convertView = View.inflate(context, R.layout.item_grid_goods, null);
			
			holder = new ViewHolder();
			holder.item_main = convertView.findViewById(R.id.grid_item_goods_main);
			holder.iv_goods_img = convertView.findViewById(R.id.grid_item_goods_iv_goods_img);
			holder.tv_goods_name = convertView.findViewById(R.id.grid_item_goods_tv_goods_name);

			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		final GoodsEntity data = mDataList.get(position);

		Glide.with(AppApplication.getAppContext())
				.load(data.getPicUrl())
				.apply(AppApplication.getShowOptions())
				.into(holder.iv_goods_img);

		holder.tv_goods_name.setText(data.getName());

		holder.item_main.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (ClickUtils.isDoubleClick(v.getId())) return;
				if (apCallback != null) {
					apCallback.setOnClick(data, position, 0);
				}
			}
		});
		return convertView;
	}

}
