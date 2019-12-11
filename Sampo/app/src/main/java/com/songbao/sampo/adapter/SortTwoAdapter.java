package com.songbao.sampo.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo.AppApplication;
import com.songbao.sampo.R;
import com.songbao.sampo.entity.GoodsEntity;
import com.songbao.sampo.entity.GoodsSortEntity;
import com.songbao.sampo.widgets.RoundImageView;

import java.text.DecimalFormat;

public class SortTwoAdapter extends BaseRecyclerAdapter {

    private DecimalFormat df;

    public SortTwoAdapter(Context context, int resLayout) {
        super(context, resLayout);
        df = new DecimalFormat("0.00");
    }

    @Override
    public void bindData(BaseRecyclerHolder holder, final int pos) {
        // 获取View
        ConstraintLayout item_main = holder.getView(R.id.sort_two_item_main);
        TextView tv_title = holder.getView(R.id.sort_two_item_tv_title);
        TextView tv_title_en = holder.getView(R.id.sort_two_item_tv_title_en);
        ImageView iv_new = holder.getView(R.id.sort_two_item_iv_new);
        RoundImageView iv_show_1 = holder.getView(R.id.sort_two_item_iv_show_1);
        TextView tv_name_1 = holder.getView(R.id.sort_two_item_tv_name_1);
        TextView tv_attr_1 = holder.getView(R.id.sort_two_item_tv_attr_1);
        TextView tv_price_1 = holder.getView(R.id.sort_two_item_tv_price_1);
        RoundImageView iv_show_2 = holder.getView(R.id.sort_two_item_iv_show_2);
        TextView tv_name_2 = holder.getView(R.id.sort_two_item_tv_name_2);
        TextView tv_attr_2 = holder.getView(R.id.sort_two_item_tv_attr_2);
        TextView tv_price_2 = holder.getView(R.id.sort_two_item_tv_price_2);

        // 绑定View
        final GoodsSortEntity data = (GoodsSortEntity) mDataList.get(pos);

        if (pos == 0) {
            tv_title.setText(context.getString(R.string.goods_new_line));
            tv_title_en.setText(context.getString(R.string.goods_new_line_en));
            iv_new.setVisibility(View.VISIBLE);
        } else {
            tv_title.setText(context.getString(R.string.goods_hot_single));
            tv_title_en.setText(context.getString(R.string.goods_hot_single_en));
            iv_new.setVisibility(View.GONE);
        }

        if (data.getGoodsLists().size() > 1) {
            final GoodsEntity goods_1 = data.getGoodsLists().get(0);
            Glide.with(AppApplication.getAppContext())
                    .load(goods_1.getPicUrl())
                    .apply(AppApplication.getShowOptions())
                    .into(iv_show_1);

            tv_name_1.setText(goods_1.getName());
            tv_attr_1.setText(goods_1.getAttribute());
            tv_price_1.setText(df.format(goods_1.getPrice()));

            final GoodsEntity goods_2 = data.getGoodsLists().get(1);
            Glide.with(AppApplication.getAppContext())
                    .load(goods_2.getPicUrl())
                    .apply(AppApplication.getShowOptions())
                    .into(iv_show_2);

            tv_name_2.setText(goods_2.getName());
            tv_attr_2.setText(goods_2.getAttribute());
            tv_price_2.setText(df.format(goods_2.getPrice()));
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

}
