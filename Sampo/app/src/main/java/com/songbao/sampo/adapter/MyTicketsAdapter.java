package com.songbao.sampo.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbao.sampo.R;
import com.songbao.sampo.entity.CouponEntity;

public class MyTicketsAdapter extends BaseRecyclerAdapter {

    public MyTicketsAdapter(Context context, int resLayout) {
        super(context, resLayout);
    }

    @Override
    public void bindData(BaseRecyclerHolder holder, final int pos) {
        // 获取View
        ConstraintLayout item_main = holder.getView(R.id.my_tickets_item_main);
        ImageView iv_top = holder.getView(R.id.my_tickets_item_iv_top);
        TextView tv_title = holder.getView(R.id.my_tickets_item_tv_title);
        TextView tv_state = holder.getView(R.id.my_tickets_item_tv_state);
        TextView tv_time = holder.getView(R.id.my_tickets_item_tv_time);

        // 绑定View
        final CouponEntity data = (CouponEntity) mDataList.get(pos);
        if (pos == 0) {
            iv_top.setVisibility(View.VISIBLE);
        } else {
            iv_top.setVisibility(View.GONE);
        }
        tv_title.setText(data.getName());
        tv_time.setText(context.getString(R.string.coupon_expired_time, data.getTermTime()));

        switch (data.getStatus()) {
            case 2:
                tv_state.setText(context.getString(R.string.cancelled));
                tv_state.setBackgroundResource(R.drawable.shape_style_solid_9_18);
                break;
            case 3:
                tv_state.setText(context.getString(R.string.expired));
                tv_state.setBackgroundResource(R.drawable.shape_style_solid_3_18);
                break;
            default:
                tv_state.setText(context.getString(R.string.active));
                tv_state.setBackgroundResource(R.drawable.shape_style_solid_5_18);
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
