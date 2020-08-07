package com.songbao.sampo_b.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.entity.GoodsEntity;
import com.songbao.sampo_b.utils.ClickUtils;
import com.songbao.sampo_b.widgets.RoundImageView;

import java.text.DecimalFormat;

/**
 * 商品列表适配器
 */
public class GoodsListAdapter extends BaseRecyclerAdapter<GoodsEntity> {

    private DecimalFormat df;

    public GoodsListAdapter(Context context, int resLayout) {
        super(context, resLayout);
        df = new DecimalFormat("0.00");
    }

    @Override
    public void bindData(BaseRecyclerHolder holder, final int pos) {
        // 获取View
        ConstraintLayout item_main = holder.getView(R.id.goods_list_item_main);
        RelativeLayout rl_top = holder.getView(R.id.goods_list_item_rl_top);
        RoundImageView iv_show = holder.getView(R.id.goods_list_item_iv_show);
        TextView tv_name = holder.getView(R.id.goods_list_item_tv_name);
        TextView tv_attr = holder.getView(R.id.goods_list_item_tv_attr);
        TextView tv_price = holder.getView(R.id.goods_list_item_tv_price);

        // 绑定View
        final GoodsEntity data = mDataList.get(pos);

        if (pos == 0) {
            rl_top.setVisibility(View.VISIBLE);
        } else {
            rl_top.setVisibility(View.GONE);
        }

        Glide.with(AppApplication.getAppContext())
                .load(data.getPicUrl())
                .apply(AppApplication.getShowOptions())
                .into(iv_show);

        tv_name.setText(data.getName());
        tv_attr.setText(data.getAttribute());
        tv_price.setText(df.format(data.getCostPrice()));

        item_main.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ClickUtils.isDoubleClick(v.getId())) return;
                if (apCallback != null) {
                    apCallback.setOnClick(data, pos, 0);
                }
            }
        });
    }

}
