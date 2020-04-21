package com.songbao.sampo_b.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.utils.CommonTools;
import com.songbao.sampo_b.widgets.RoundImageView;

import java.util.List;


/**
 * 选择相片适配器
 */
public class ClipPhotoAllAdapter extends AppBaseAdapter<String> {

	private ConstraintLayout.LayoutParams imageLP;

	public ClipPhotoAllAdapter(Context context, List<String> pathList) {
		super(context);
		addData(pathList);

		int imageSize = (AppApplication.screen_width - CommonTools.dpToPx(context, 40)) / 3;
		this.imageLP = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		this.imageLP.width = imageSize;
		this.imageLP.height = imageSize;
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
		String imgUrl = mDataList.get(position);

		Glide.with(AppApplication.getAppContext())
				.load(imgUrl)
				.apply(AppApplication.getShowOptions())
				.into(holder.iv_show);

		return convertView;
	}

}
