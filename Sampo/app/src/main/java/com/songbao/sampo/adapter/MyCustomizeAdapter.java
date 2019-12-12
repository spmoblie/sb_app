package com.songbao.sampo.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo.AppApplication;
import com.songbao.sampo.R;
import com.songbao.sampo.entity.DesignerEntity;
import com.songbao.sampo.entity.GoodsEntity;
import com.songbao.sampo.entity.OCustomizeEntity;
import com.songbao.sampo.widgets.RoundImageView;

public class MyCustomizeAdapter extends BaseRecyclerAdapter {

    public MyCustomizeAdapter(Context context, int resLayout) {
        super(context, resLayout);
    }

    @Override
    public void bindData(BaseRecyclerHolder holder, final int pos) {
        // 获取View
        LinearLayout item_main = holder.getView(R.id.my_customize_item_main);
        RelativeLayout tv_top = holder.getView(R.id.my_customize_item_rl_top);
        TextView tv_time = holder.getView(R.id.my_customize_item_tv_time);
        TextView tv_status = holder.getView(R.id.my_customize_item_tv_status);
        RoundImageView iv_show = holder.getView(R.id.my_customize_item_iv_show);
        TextView tv_title = holder.getView(R.id.my_customize_item_tv_title);
        TextView tv_name = holder.getView(R.id.my_customize_item_tv_name);
        TextView tv_phone = holder.getView(R.id.my_customize_item_tv_phone);
        TextView tv_click_01 = holder.getView(R.id.my_customize_item_tv_click_01);
        TextView tv_click_02 = holder.getView(R.id.my_customize_item_tv_click_02);

        // 绑定View
        final OCustomizeEntity data = (OCustomizeEntity) mDataList.get(pos);

        if (pos == 0) {
            tv_top.setVisibility(View.VISIBLE);
        } else {
            tv_top.setVisibility(View.GONE);
        }

        tv_time.setText(data.getNodeTime1());

        GoodsEntity gdEn = data.getGdEn();
        if (gdEn != null) {
            Glide.with(AppApplication.getAppContext())
                    .load(gdEn.getPicUrl())
                    .apply(AppApplication.getShowOptions())
                    .into(iv_show);

            tv_title.setText(gdEn.getName());
        }

        DesignerEntity dgEn = data.getDgEn();
        if (dgEn != null) {
            tv_name.setText(dgEn.getName());
            tv_phone.setText(dgEn.getPhone());
        }

        switch (data.getStatus()) {
            case 1: //待付款
                tv_status.setText(context.getString(R.string.order_wait_pay));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_09_04);
                tv_click_01.setVisibility(View.VISIBLE);
                tv_click_01.setText(context.getString(R.string.order_cancel));
                tv_click_01.setTextColor(context.getResources().getColor(R.color.app_color_gray_5));
                tv_click_01.setBackgroundResource(R.drawable.shape_style_empty_02_08);
                tv_click_02.setText(context.getString(R.string.order_pay));
                tv_click_02.setBackgroundResource(R.drawable.shape_style_solid_04_08);
                break;
            case 2: //生产中
                tv_status.setText(context.getString(R.string.order_producing));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_10_04);
                tv_click_01.setVisibility(View.GONE);
                tv_click_02.setText(context.getString(R.string.order_progress_check));
                tv_click_02.setBackgroundResource(R.drawable.shape_style_solid_04_08);
                break;
            case 3: //待收货
                tv_status.setText(context.getString(R.string.order_wait_receive));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_04_04);
                tv_click_01.setVisibility(View.VISIBLE);
                tv_click_01.setText(context.getString(R.string.order_logistics));
                tv_click_01.setTextColor(context.getResources().getColor(R.color.tv_color_status));
                tv_click_01.setBackgroundResource(R.drawable.shape_style_empty_04_08);
                tv_click_02.setText(context.getString(R.string.order_confirm_receipt));
                tv_click_02.setBackgroundResource(R.drawable.shape_style_solid_04_08);
                break;
            case 5: //待评价
                tv_status.setText(context.getString(R.string.order_completed));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_03_04);
                tv_click_01.setVisibility(View.VISIBLE);
                tv_click_01.setText(context.getString(R.string.order_after_sale));
                tv_click_01.setTextColor(context.getResources().getColor(R.color.app_color_gray_5));
                tv_click_01.setBackgroundResource(R.drawable.shape_style_empty_02_08);
                tv_click_02.setText(context.getString(R.string.order_evaluate));
                tv_click_02.setBackgroundResource(R.drawable.shape_style_solid_06_08);
                break;
            case 6: //已完成
            default:
                tv_status.setText(context.getString(R.string.order_completed));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_03_04);
                tv_click_01.setVisibility(View.VISIBLE);
                tv_click_01.setText(context.getString(R.string.order_after_sale));
                tv_click_01.setTextColor(context.getResources().getColor(R.color.app_color_gray_5));
                tv_click_01.setBackgroundResource(R.drawable.shape_style_empty_02_08);
                tv_click_02.setText(context.getString(R.string.order_delete));
                tv_click_02.setBackgroundResource(R.drawable.shape_style_solid_03_08);
                break;
            case 7: //退换货
                tv_status.setText(context.getString(R.string.order_repair_return));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_05_04);
                tv_click_01.setVisibility(View.VISIBLE);
                tv_click_01.setText(context.getString(R.string.order_refund_details));
                tv_click_01.setTextColor(context.getResources().getColor(R.color.app_color_gray_5));
                tv_click_01.setBackgroundResource(R.drawable.shape_style_empty_02_08);
                tv_click_02.setText(context.getString(R.string.order_revoke_refund));
                tv_click_02.setBackgroundResource(R.drawable.shape_style_solid_05_08);
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
