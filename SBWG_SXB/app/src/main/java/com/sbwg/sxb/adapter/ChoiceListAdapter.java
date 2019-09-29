package com.sbwg.sxb.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbwg.sxb.R;
import com.sbwg.sxb.entity.OptionEntity;

import java.util.List;

/**
 * 课程可选场次列表适配器
 */
public class ChoiceListAdapter extends AppBaseAdapter {

	private Context context;
	private List<OptionEntity> datas;
	private AdapterCallback apCallback;

	public ChoiceListAdapter(Context context, List<OptionEntity> datas, AdapterCallback apCallback) {
		super(context);
		this.context = context;
		this.datas = datas;
		this.apCallback = apCallback;
	}

	public void updateAdapter(List<OptionEntity> datas){
		if (datas != null) {
			this.datas = datas;
			notifyDataSetChanged();
		}
	}

	/**获得总共有多少条数据*/
	@Override
	public int getCount() {
		return datas.size();
	}

	/**在ListView中显示的每个item内容*/
	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	/**返回集合中个某个元素的位置*/
	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder{

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
			holder=(ViewHolder)convertView.getTag();
		}
		final OptionEntity data = datas.get(position);
		holder.tv_time.setText(data.getTime());
		if (data.isState()) {
			holder.tv_state.setText(context.getString(R.string.choice_reserve_yes));
			holder.tv_state.setTextColor(context.getResources().getColor(R.color.app_color_r_red));
		} else {
			holder.tv_state.setText(context.getString(R.string.choice_reserve_no));
			holder.tv_state.setTextColor(context.getResources().getColor(R.color.debar_text_color));
		}
		holder.iv_select.setSelected(data.isSelect());

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
