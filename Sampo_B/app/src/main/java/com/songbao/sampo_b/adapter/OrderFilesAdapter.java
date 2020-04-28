package com.songbao.sampo_b.adapter;

import android.content.Context;
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
 * 订单文件备注列表适配器
 */
public class OrderFilesAdapter extends AppBaseAdapter<String> {

	public OrderFilesAdapter(Context context) {
		super(context);
	}

	static class ViewHolder {
		ConstraintLayout item_main;
		TextView tv_name;
	}

	/**
	 * 代表了ListView中的一个item对象
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_list_order_files, null);

			holder = new ViewHolder();
			holder.item_main = convertView.findViewById(R.id.order_files_item_main);
			holder.tv_name = convertView.findViewById(R.id.order_files_item_tv_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 绑定View
		final String fileName = mDataList.get(position);

		holder.tv_name.setText(fileName);

		holder.item_main.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ClickUtils.isDoubleClick(v.getId())) return;
				if (apCallback != null) {
					apCallback.setOnClick(fileName, position, 0);
				}
			}
		});

		return convertView;
	}

}
