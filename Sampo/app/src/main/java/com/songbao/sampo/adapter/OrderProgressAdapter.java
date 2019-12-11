package com.songbao.sampo.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo.AppApplication;
import com.songbao.sampo.R;
import com.songbao.sampo.entity.OProgressEntity;
import com.songbao.sampo.utils.CommonTools;
import com.songbao.sampo.widgets.RoundImageView;

import java.util.ArrayList;

/**
 * 定制产品生产进度列表适配器
 */
public class OrderProgressAdapter extends AppBaseAdapter {

	private int imageTotalWidth;
	private RelativeLayout.LayoutParams goodsImgLP;

	public OrderProgressAdapter(Context context) {
		super(context);

		imageTotalWidth = AppApplication.screen_width - CommonTools.dpToPx(context, 95);
		goodsImgLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	}

	static class ViewHolder {
		ConstraintLayout item_main;
		TextView tv_time;
		TextView tv_content;
		HorizontalScrollView sv_main;
		LinearLayout ll_main;
	}

	/**代表了ListView中的一个item对象*/
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			convertView = View.inflate(context, R.layout.item_list_order_progress, null);
			
			holder = new ViewHolder();
			holder.item_main = convertView.findViewById(R.id.order_progress_item_main);
			holder.tv_time = convertView.findViewById(R.id.order_progress_tv_time);
			holder.tv_content = convertView.findViewById(R.id.order_progress_tv_content);
			holder.sv_main = convertView.findViewById(R.id.order_progress_view_hsv);
			holder.ll_main = convertView.findViewById(R.id.order_progress_hsv_ll_main);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}

		// 绑定View
		final OProgressEntity data = (OProgressEntity) mDataList.get(position);

		holder.tv_time.setText(data.getAddTime());
		holder.tv_content.setText(data.getContent());

		if (data.getType() == 1) {
			holder.tv_content.setVisibility(View.GONE);
			holder.sv_main.setVisibility(View.VISIBLE);
			initImageView(holder.ll_main, data.getImgList());
		} else {
			holder.ll_main.removeAllViews();
			holder.sv_main.setVisibility(View.GONE);
			holder.tv_content.setVisibility(View.VISIBLE);
		}

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

	private void initImageView(LinearLayout ll_main, ArrayList<String> imgList) {
		if (ll_main == null || imgList == null || imgList.size() <= 0) return;
		ll_main.removeAllViews();

		/*int imageSize;
		int imgCount = imgList.size();
		if (imgCount == 1) {
			imageSize = imageTotalWidth * 2/3;
		} else
		if (imgCount <= 3) {
			imageSize = imageTotalWidth / imgCount;
		} else {
			imageSize = imageTotalWidth / 3;
		}*/
		int imgCount = imgList.size();
		int imageSize = imageTotalWidth / 3;
		goodsImgLP.width = imageSize;
		goodsImgLP.height = imageSize;
		goodsImgLP.setMargins(0, 0, 10, 0);
		for (int i = 0; i < imgCount; i++) {
			final String imgUrl = imgList.get(i);
			RoundImageView iv_img = new RoundImageView(context);
			iv_img.setScaleType(ImageView.ScaleType.CENTER_CROP);

			if (i == imgCount - 1) {
				goodsImgLP.setMargins(0, 0, 0, 0);
			}
			iv_img.setLayoutParams(goodsImgLP);

			iv_img.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (apCallback != null) {
						apCallback.setOnClick(null, 0, 0);
					}
				}
			});

			Glide.with(AppApplication.getAppContext())
					.load(imgUrl)
					.apply(AppApplication.getShowOptions())
					.into(iv_img);

			ll_main.addView(iv_img);
		}
	}

}
