package com.songbao.sxb.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.songbao.sxb.AppApplication;
import com.songbao.sxb.R;

public class DescriptionAdapter extends AppBaseAdapter {

	private Context context;

	public DescriptionAdapter(Context context) {
		super(context);
		this.context = context;
	}

	static class ViewHolder {

		LinearLayout item_main;
		ImageView iv_show;

	}

	/**代表了ListView中的一个item对象*/
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			convertView = View.inflate(context, R.layout.item_list_description, null);
			
			holder = new ViewHolder();
			holder.item_main = convertView.findViewById(R.id.list_item_description_main);
			holder.iv_show = convertView.findViewById(R.id.description_iv_show);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		final String imgUrl = (String) mDataList.get(position);

		Glide.with(AppApplication.getAppContext())
				.load(imgUrl)
				.apply(AppApplication.getShowOptions())
				.into(holder.iv_show);

		holder.item_main.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (apCallback != null) {
					apCallback.setOnClick(imgUrl, position, 1);
				}
			}
		});
		return convertView;
	}

}
