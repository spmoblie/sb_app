package com.songbao.sampo_b.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.utils.CommonTools;
import com.songbao.sampo_b.widgets.RoundImageView;

import java.util.List;


public class PhotoGridAdapter extends AppBaseAdapter<String> {

	private ConstraintLayout.LayoutParams imageLP;
	private RequestOptions showOptions;

	public PhotoGridAdapter(Context context, List<String> data) {
		super(context);

		addData(data);

		int imageSize = (AppApplication.screen_width - CommonTools.dpToPx(context, 70)) / 3;
		this.imageLP = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		this.imageLP.width = imageSize;
		this.imageLP.height = imageSize;

		showOptions = new RequestOptions()
				.placeholder(R.drawable.icon_photo_add)
				.fallback(R.drawable.icon_photo_add)
				.error(R.drawable.icon_photo_add);
	}

	static class ViewHolder {
		RoundImageView iv_show;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_grid_photo_goods, null);
			holder = new ViewHolder();
			holder.iv_show = convertView.findViewById(R.id.item_grid_photo_iv_show);
			holder.iv_show.setLayoutParams(imageLP);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 通过ID 获取缩略图
		String imgUrl = mDataList.get(position);

		Glide.with(AppApplication.getAppContext())
				.load(imgUrl)
				.apply(showOptions)
				.into(holder.iv_show);

		return convertView;
	}

}
