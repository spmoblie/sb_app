package com.songbao.sampo_c.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.songbao.sampo_c.R;
import com.songbao.sampo_c.entity.OLogisticsEntity;

/**
 * 订单物流单号列表适配器
 */
public class OrderLogisticsAdapter extends AppBaseAdapter<OLogisticsEntity> {

	public OrderLogisticsAdapter(Context context) {
		super(context);
	}

	static class ViewHolder {
		ConstraintLayout item_main;
		TextView tv_number;
		TextView tv_number_check;
	}

	/**代表了ListView中的一个item对象*/
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			convertView = View.inflate(context, R.layout.item_list_order_logistics, null);

			holder = new ViewHolder();
			holder.item_main = convertView.findViewById(R.id.order_logistics_item_main);
			holder.tv_number = convertView.findViewById(R.id.order_logistics_tv_number);
			holder.tv_number_check = convertView.findViewById(R.id.order_logistics_tv_number_check);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}

		// 绑定View
		final OLogisticsEntity data = mDataList.get(position);

		holder.tv_number.setText(context.getString(R.string.order_logistics_no, data.getName(), data.getNumber()));

		holder.item_main.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (apCallback != null) {
					apCallback.setOnClick(data, position, 0);
				}
			}
		});

		return convertView;
	}

}
