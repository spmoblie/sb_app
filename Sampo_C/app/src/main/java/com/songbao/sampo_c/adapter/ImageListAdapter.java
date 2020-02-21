package com.songbao.sampo_c.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.R;

/**
 * 图片列表适配器
 */
public class ImageListAdapter extends AppBaseAdapter<String> {

	public ImageListAdapter(Context context) {
		super(context);
	}

	static class ViewHolder {
		ImageView iv_show;
	}

	/**代表了ListView中的一个item对象*/
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			convertView = View.inflate(context, R.layout.item_list_image, null);

			holder = new ViewHolder();
			holder.iv_show = convertView.findViewById(R.id.list_item_image_show);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		String imgUrl = mDataList.get(position);

		Glide.with(AppApplication.getAppContext())
				.load(imgUrl)
				.apply(AppApplication.getShowOptions())
				.into(holder.iv_show);

		return convertView;
	}

}
