package com.songbao.sampo.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo.AppApplication;
import com.songbao.sampo.R;
import com.songbao.sampo.entity.OrderEntity;
import com.songbao.sampo.widgets.RoundImageView;

public class MyOrderAdapter extends BaseRecyclerAdapter {

    public MyOrderAdapter(Context context, int resLayout) {
        super(context, resLayout);
    }

    @Override
    public void bindData(BaseRecyclerHolder holder, final int pos) {
        // 获取View
        ConstraintLayout item_main = holder.getView(R.id.my_order_item_main);
        ImageView iv_top = holder.getView(R.id.my_order_item_iv_top);
        TextView tv_name = holder.getView(R.id.my_order_item_tv_name);
        TextView tv_state = holder.getView(R.id.my_order_item_tv_state);
        RoundImageView iv_show = holder.getView(R.id.my_order_item_iv_show);
        TextView tv_title = holder.getView(R.id.my_order_item_tv_title);
        TextView tv_pay_type = holder.getView(R.id.my_order_item_tv_pay_type);
        TextView tv_cost = holder.getView(R.id.my_order_item_tv_cost);
        TextView tv_time = holder.getView(R.id.my_order_item_tv_time);

        // 绑定View
        final OrderEntity data = (OrderEntity) mDataList.get(pos);
        if (pos == 0) {
            iv_top.setVisibility(View.VISIBLE);
        } else {
            iv_top.setVisibility(View.GONE);
        }
        Glide.with(AppApplication.getAppContext())
                .load(data.getPicUrl())
                .apply(AppApplication.getShowOptions())
                .into(iv_show);

        tv_name.setText(data.getName());
        tv_title.setText(data.getTitle());
        tv_pay_type.setText(data.getPayType());
        tv_cost.setText(data.getCost());
        tv_time.setText(data.getAddTime());

        switch (data.getStatus()) {
            case 2:
                tv_state.setText(context.getString(R.string.cancelled));
                tv_state.setTextColor(context.getResources().getColor(R.color.app_color_red_7));
                break;
            case 3:
                tv_state.setText(context.getString(R.string.expired));
                tv_state.setTextColor(context.getResources().getColor(R.color.app_color_gray_5));
                break;
            default:
                tv_state.setText(context.getString(R.string.pay_ok));
                tv_state.setTextColor(context.getResources().getColor(R.color.tv_color_status));
                break;
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
