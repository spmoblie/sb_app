package com.songbao.sampo_b.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.entity.PhotoEntity;
import com.songbao.sampo_b.utils.CommonTools;
import com.songbao.sampo_b.widgets.RoundImageView;

import java.util.List;


/**
 * 选择相片适配器
 */
public class PhotoItemAdapter extends AppBaseAdapter<PhotoEntity> {

	private boolean isShowSelect;
	private ConstraintLayout.LayoutParams imageLP;

	public PhotoItemAdapter(Context context, List<PhotoEntity> pathList) {
		super(context);
		addData(pathList);

		int imageSize = (AppApplication.screen_width - CommonTools.dpToPx(context, 40)) / 3;
		this.imageLP = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		this.imageLP.width = imageSize;
		this.imageLP.height = imageSize;
	}

	public void setShowSelect(boolean isShow) {
		this.isShowSelect = isShow;
	}

	static class ViewHolder {
		ImageView iv_select;
		RoundImageView iv_show;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_grid_photo, null);
			holder = new ViewHolder();
			holder.iv_select = convertView.findViewById(R.id.photo_item_iv_select);
			holder.iv_show = convertView.findViewById(R.id.photo_item_iv_img);
			holder.iv_show.setLayoutParams(imageLP);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		PhotoEntity data = mDataList.get(position);

		Glide.with(AppApplication.getAppContext())
				.load(data.getPhotoUrl())
				.apply(AppApplication.getShowOptions())
				.into(holder.iv_show);

		if (isShowSelect) {
			holder.iv_select.setVisibility(View.VISIBLE);
			holder.iv_select.setSelected(data.isSelect());
		} else {
			holder.iv_select.setVisibility(View.GONE);
		}

		return convertView;
	}

}
