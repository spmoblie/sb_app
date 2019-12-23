package com.songbao.sampo.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo.AppApplication;
import com.songbao.sampo.R;
import com.songbao.sampo.entity.CommentEntity;
import com.songbao.sampo.utils.CommonTools;
import com.songbao.sampo.utils.StringUtil;
import com.songbao.sampo.widgets.RoundImageView;

import java.util.ArrayList;

/**
 * 商品精彩评价列表适配器
 */
public class CommentGLVAdapter extends AppBaseAdapter {

	private int imageTotalWidth;
	private RelativeLayout.LayoutParams goodsImgLP;

	public CommentGLVAdapter(Context context) {
		super(context);

		imageTotalWidth = AppApplication.screen_width - CommonTools.dpToPx(context, 65);
		goodsImgLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	}

	static class ViewHolder {
		ConstraintLayout item_main;
		RoundImageView iv_head;
		TextView tv_nick;
		TextView tv_time;
		TextView tv_attr;
		TextView tv_content;
		RatingBar rb_star;
		HorizontalScrollView sv_main;
		LinearLayout ll_main;
		TextView tv_add_day;
		TextView tv_add_content;
		ImageView iv_line;
	}

	/**代表了ListView中的一个item对象*/
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			convertView = View.inflate(context, R.layout.item_list_comment_goods, null);
			
			holder = new ViewHolder();
			holder.item_main = convertView.findViewById(R.id.comment_goods_item_main);
			holder.iv_head = convertView.findViewById(R.id.comment_goods_item_iv_head);
			holder.tv_nick = convertView.findViewById(R.id.comment_goods_item_tv_nick);
			holder.tv_time = convertView.findViewById(R.id.comment_goods_item_tv_time);
			holder.tv_attr = convertView.findViewById(R.id.comment_goods_item_tv_attr);
			holder.tv_content = convertView.findViewById(R.id.comment_goods_item_tv_content);
			holder.rb_star = convertView.findViewById(R.id.comment_goods_item_rb_star);
			holder.sv_main = convertView.findViewById(R.id.comment_goods_item_view_hsv);
			holder.ll_main = convertView.findViewById(R.id.comment_goods_item_hsv_ll_main);
			holder.tv_add_day = convertView.findViewById(R.id.comment_goods_item_tv_content_add_day);
			holder.tv_add_content = convertView.findViewById(R.id.comment_goods_item_tv_add_content);
			holder.iv_line = convertView.findViewById(R.id.comment_goods_item_iv_line);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}

		// 绑定View
		final CommentEntity data = (CommentEntity) mDataList.get(position);

		if (position == getCount() - 1) {
			holder.iv_line.setVisibility(View.GONE);
		} else {
			holder.iv_line.setVisibility(View.VISIBLE);
		}

		Glide.with(AppApplication.getAppContext())
				.load(data.getHeadUrl())
				.apply(AppApplication.getHeadOptions())
				.into(holder.iv_head);

		holder.tv_nick.setText(data.getNick());
		holder.tv_time.setText(data.getAddTime());
		holder.tv_attr.setText(data.getGoodsAttr());
		holder.tv_content.setText(data.getContent());

		holder.rb_star.setRating(data.getStarNum());

		// 评论图片
		if (data.isImg()) {
			holder.sv_main.setVisibility(View.VISIBLE);
			initImageView(holder.ll_main, data.getImgList());
		} else {
			holder.ll_main.removeAllViews();
			holder.sv_main.setVisibility(View.GONE);
		}

		// 追加评价
		holder.tv_add_day.setVisibility(View.GONE);
		holder.tv_add_content.setVisibility(View.GONE);
		if (data.getAddDay() > 0 && !StringUtil.isNull(data.getAddContent())) {
			holder.tv_add_day.setVisibility(View.VISIBLE);
			holder.tv_add_content.setVisibility(View.VISIBLE);
			holder.tv_add_day.setText(context.getString(R.string.order_comment_day, data.getAddDay()));
			holder.tv_add_content.setText(data.getAddContent());
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

		int imageSize;
		int imgCount = imgList.size();
		if (imgCount == 1) {
			imageSize = imageTotalWidth * 2/3;
		} else
		if (imgCount <= 3) {
			imageSize = imageTotalWidth / imgCount;
		} else {
			imageSize = imageTotalWidth / 3;
		}
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
