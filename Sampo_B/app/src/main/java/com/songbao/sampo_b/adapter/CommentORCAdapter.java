package com.songbao.sampo_b.adapter;

import android.content.Context;
import android.support.constraint.Group;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.entity.CommentEntity;
import com.songbao.sampo_b.entity.GoodsEntity;
import com.songbao.sampo_b.utils.ClickUtils;
import com.songbao.sampo_b.utils.CommonTools;
import com.songbao.sampo_b.utils.StringUtil;
import com.songbao.sampo_b.widgets.RoundImageView;

import java.util.ArrayList;

public class CommentORCAdapter extends BaseRecyclerAdapter {

    private RelativeLayout.LayoutParams goodsImgLP;

    public CommentORCAdapter(Context context, int resLayout) {
        super(context, resLayout);

        int imageSize = CommonTools.dpToPx(context, 60);
        goodsImgLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        goodsImgLP.setMargins(0, 0, 10, 0);
        goodsImgLP.width = imageSize;
        goodsImgLP.height = imageSize;
    }

    @Override
    public void bindData(BaseRecyclerHolder holder, final int pos) {
        // 获取View
        LinearLayout item_main = holder.getView(R.id.comment_order_item_main);
        RoundImageView iv_goods = holder.getView(R.id.comment_order_item_iv_goods);
        TextView tv_name = holder.getView(R.id.comment_order_item_tv_name);
        TextView tv_attr = holder.getView(R.id.comment_order_item_tv_attr);
        RatingBar rb_star = holder.getView(R.id.comment_order_item_rb_star);
        TextView tv_time = holder.getView(R.id.comment_order_item_tv_time);
        TextView tv_content = holder.getView(R.id.comment_order_item_tv_content);
        HorizontalScrollView sv_main = holder.getView(R.id.comment_order_item_view_hsv);
        LinearLayout ll_main = holder.getView(R.id.comment_order_item_hsv_ll_main);
        TextView tv_add_to = holder.getView(R.id.comment_order_item_tv_content_add_to);
        TextView tv_add_day = holder.getView(R.id.comment_order_item_tv_content_add_day);
        TextView tv_add_content = holder.getView(R.id.comment_order_item_tv_add_content);
        Group group_content_add = holder.getView(R.id.comment_order_item_tv_content_add_group);

        // 绑定View
        final CommentEntity data = (CommentEntity) mDataList.get(pos);

        GoodsEntity goodsEn = data.getGoodsEn();
        if (goodsEn != null) {
            Glide.with(AppApplication.getAppContext())
                    .load(goodsEn.getPicUrl())
                    .apply(AppApplication.getShowOptions())
                    .into(iv_goods);

            tv_name.setText(goodsEn.getName());
            tv_attr.setText(goodsEn.getAttribute());
        }

        tv_time.setText(data.getAddTime());
        rb_star.setRating(data.getStarNum());
        tv_content.setText(data.getContent());

        // 评论图片
        if (data.isImg()) {
            sv_main.setVisibility(View.VISIBLE);
            initImageView(ll_main, data.getImgList());
        } else {
            ll_main.removeAllViews();
            sv_main.setVisibility(View.GONE);
        }

        // 追加评价
        tv_add_to.setVisibility(View.GONE);
        group_content_add.setVisibility(View.GONE);
        if (data.isAdd()) {
            tv_add_to.setVisibility(View.VISIBLE);
        } else {
            if (data.getAddDay() > 0 && !StringUtil.isNull(data.getAddContent())) {
                group_content_add.setVisibility(View.VISIBLE);
                tv_add_day.setText(context.getString(R.string.comment_add_day, data.getAddDay()));
                tv_add_content.setText(data.getAddContent());
            }
        }

        tv_add_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtils.isDoubleClick()) return;
                if (data.isAdd()) {
                    if (apCallback != null) {
                        apCallback.setOnClick(data, pos, 0);
                    }
                }
            }
        });
    }

    private void initImageView(LinearLayout ll_main, ArrayList<String> imgList) {
        if (ll_main == null || imgList == null || imgList.size() <= 0) return;
        ll_main.removeAllViews();

        int imgCount = imgList.size();
        for (int i = 0; i < imgCount; i++) {
            final String imgUrl = imgList.get(i);
            RoundImageView iv_img = new RoundImageView(context);
            iv_img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv_img.setLayoutParams(goodsImgLP);

            Glide.with(AppApplication.getAppContext())
                    .load(imgUrl)
                    .apply(AppApplication.getShowOptions())
                    .into(iv_img);

            ll_main.addView(iv_img);
        }
    }

}
