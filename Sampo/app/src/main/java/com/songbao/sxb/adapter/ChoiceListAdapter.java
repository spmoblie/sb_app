package com.songbao.sxb.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbao.sxb.R;
import com.songbao.sxb.entity.OptionEntity;

/**
 * 课程可选场次列表适配器
 */
public class ChoiceListAdapter extends AppBaseAdapter {

	public ChoiceListAdapter(Context context) {
		super(context);
	}

	static class ViewHolder {
		LinearLayout item_main;
		TextView tv_time, tv_state;
		ImageView iv_select;
	}

	/**代表了ListView中的一个item对象*/
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			convertView = View.inflate(context, R.layout.item_list_choice, null);
			
			holder = new ViewHolder();
			holder.item_main = convertView.findViewById(R.id.list_item_choice_main);
			holder.tv_time = convertView.findViewById(R.id.list_item_choice_tv_time);
			holder.tv_state = convertView.findViewById(R.id.list_item_choice_tv_state);
			holder.iv_select = convertView.findViewById(R.id.list_item_choice_iv_select);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		final OptionEntity data = (OptionEntity) mDataList.get(position);
		holder.tv_time.setText(data.getTime());
		if (data.isState()) {
			holder.tv_state.setText(context.getString(R.string.choice_reserve_ok));
			holder.tv_state.setTextColor(context.getResources().getColor(R.color.tv_color_status));
		} else {
			if (data.isReserve()) {
				holder.tv_state.setText(context.getString(R.string.choice_reserve_yes));
				holder.tv_state.setTextColor(context.getResources().getColor(R.color.warns_text_color));
			} else {
				holder.tv_state.setText(context.getString(R.string.choice_reserve_no));
				holder.tv_state.setTextColor(context.getResources().getColor(R.color.debar_text_color));
			}
		}
		holder.iv_select.setSelected(data.isSelect());
		holder.item_main.setSelected(data.isSelect());

		holder.item_main.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!data.isState()) return;
				if (apCallback != null) {
					apCallback.setOnClick(data, position, 1);
				}
			}
		});
		return convertView;
	}

}
