package com.songbao.sampo_b.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.entity.DesignerEntity;

/**
 * 设计师表格适配器
 */
public class DesignerAdapter extends AppBaseAdapter {

	public DesignerAdapter(Context context) {
		super(context);
	}

	static class ViewHolder {
		ConstraintLayout item_main;
		ImageView iv_head, iv_select;
		TextView tv_name, tv_phone, tv_info;
	}

	/**代表了ListView中的一个item对象*/
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			convertView = View.inflate(context, R.layout.item_grid_designer, null);
			
			holder = new ViewHolder();
			holder.item_main = convertView.findViewById(R.id.list_item_designer_main);
			holder.iv_head = convertView.findViewById(R.id.list_item_designer_iv_head);
			holder.iv_select = convertView.findViewById(R.id.list_item_designer_iv_select);
			holder.tv_name = convertView.findViewById(R.id.list_item_designer_tv_name);
			holder.tv_phone = convertView.findViewById(R.id.list_item_designer_tv_phone);
			holder.tv_info = convertView.findViewById(R.id.list_item_designer_tv_info);

			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		final DesignerEntity data = (DesignerEntity) mDataList.get(position);
		Glide.with(AppApplication.getAppContext())
				.load(data.getImgUrl())
				.apply(AppApplication.getShowOptions())
				.into(holder.iv_head);

		if (data.isSelect()) {
			holder.iv_select.setSelected(true);
		} else {
			holder.iv_select.setSelected(false);
		}

		holder.tv_name.setText(data.getName());
		holder.tv_phone.setText(data.getPhone());
		holder.tv_info.setText(data.getInfo());

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
