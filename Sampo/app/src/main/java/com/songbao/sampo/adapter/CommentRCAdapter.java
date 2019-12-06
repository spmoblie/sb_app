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
import com.songbao.sampo.widgets.RoundImageView;

import java.util.ArrayList;

public class CommentRCAdapter extends BaseRecyclerAdapter {

    private int imageTotalWidth;
    private RelativeLayout.LayoutParams goodsImgLP;

    public CommentRCAdapter(Context context, int resLayout) {
        super(context, resLayout);

        imageTotalWidth = AppApplication.screen_width - CommonTools.dpToPx(context, 65);
        goodsImgLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void bindData(BaseRecyclerHolder holder, final int pos) {
        // 获取View
        ConstraintLayout item_main = holder.getView(R.id.comment_item_main);
        RoundImageView iv_head = holder.getView(R.id.comment_iv_head);
        TextView tv_nick = holder.getView(R.id.comment_tv_nick);
        TextView tv_time = holder.getView(R.id.comment_tv_time);
        TextView tv_attr = holder.getView(R.id.comment_tv_attr);
        final RatingBar rb_star = holder.getView(R.id.comment_rb_star);
        TextView tv_content = holder.getView(R.id.comment_tv_content);
        HorizontalScrollView sv_main = holder.getView(R.id.comment_view_hsv);
        LinearLayout ll_main = holder.getView(R.id.comment_hsv_ll_main);

        // 绑定View
        final CommentEntity data = (CommentEntity) mDataList.get(pos);

        Glide.with(AppApplication.getAppContext())
                .load(data.getHeadUrl())
                .apply(AppApplication.getHeadOptions())
                .into(iv_head);

        tv_nick.setText(data.getNick());
        tv_time.setText(data.getAddTime());
        tv_attr.setText(data.getGoodsAttr());
        tv_content.setText(data.getContent());

        rb_star.setRating(data.getStarNum());

        if (data.getType() == 1) {
            sv_main.setVisibility(View.VISIBLE);
            initImageView(ll_main, data.getImgList());
        } else {
            ll_main.removeAllViews();
            sv_main.setVisibility(View.GONE);
        }

        item_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (apCallback != null) {
                    apCallback.setOnClick(data, pos, 0);
                }
            }
        });
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

            Glide.with(AppApplication.getAppContext())
                    .load(imgUrl)
                    .apply(AppApplication.getShowOptions())
                    .into(iv_img);

            ll_main.addView(iv_img);
        }
    }

}
