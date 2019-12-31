package com.songbao.sampo_b.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.entity.DesignerEntity;
import com.songbao.sampo_b.entity.GoodsEntity;
import com.songbao.sampo_b.entity.OCustomizeEntity;
import com.songbao.sampo_b.widgets.RoundImageView;

public class CustomizeAdapter extends BaseRecyclerAdapter {

    public CustomizeAdapter(Context context, int resLayout) {
        super(context, resLayout);
    }

    @Override
    public void bindData(BaseRecyclerHolder holder, final int pos) {
        // 获取View
        LinearLayout item_main = holder.getView(R.id.customize_item_main);
        RelativeLayout tv_top = holder.getView(R.id.customize_item_rl_top);
        TextView tv_time = holder.getView(R.id.customize_item_tv_time);
        TextView tv_status = holder.getView(R.id.customize_item_tv_status);
        RoundImageView iv_show = holder.getView(R.id.customize_item_iv_show);
        TextView tv_title = holder.getView(R.id.customize_item_tv_title);
        TextView tv_name = holder.getView(R.id.customize_item_tv_name);
        TextView tv_phone = holder.getView(R.id.customize_item_tv_phone);
        TextView tv_click_01 = holder.getView(R.id.customize_item_tv_click_01);
        TextView tv_click_02 = holder.getView(R.id.customize_item_tv_click_02);

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

        tv_click_01.setVisibility(View.GONE);
        tv_click_02.setVisibility(View.GONE);
        switch (data.getStatus()) {
            case AppConfig.ORDER_STATUS_101: //待付款
                tv_status.setText(context.getString(R.string.order_wait_pay));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_09_04);
                tv_click_02.setVisibility(View.VISIBLE);
                tv_click_02.setText(context.getString(R.string.order_cancel));
                tv_click_02.setTextColor(context.getResources().getColor(R.color.app_color_gray_5));
                tv_click_02.setBackgroundResource(R.drawable.shape_style_empty_02_08);
                break;
            case AppConfig.ORDER_STATUS_201: //生产中
                tv_status.setText(context.getString(R.string.order_producing));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_10_04);
                tv_click_02.setVisibility(View.VISIBLE);
                tv_click_02.setText(context.getString(R.string.order_progress_check));
                tv_click_02.setTextColor(context.getResources().getColor(R.color.tv_color_status));
                tv_click_02.setBackgroundResource(R.drawable.shape_style_empty_04_08);
                break;
            case AppConfig.ORDER_STATUS_301: //待发货
                tv_status.setText(context.getString(R.string.order_wait_send));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_08_04);
                break;
            case AppConfig.ORDER_STATUS_401: //待收货
                tv_status.setText(context.getString(R.string.order_wait_receive));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_04_04);
                tv_click_01.setVisibility(View.VISIBLE);
                tv_click_01.setText(context.getString(R.string.order_logistics));
                tv_click_01.setTextColor(context.getResources().getColor(R.color.tv_color_status));
                tv_click_01.setBackgroundResource(R.drawable.shape_style_empty_04_08);
                tv_click_02.setVisibility(View.VISIBLE);
                tv_click_02.setText(context.getString(R.string.order_confirm_receipt));
                tv_click_02.setTextColor(context.getResources().getColor(R.color.app_color_white));
                tv_click_02.setBackgroundResource(R.drawable.shape_style_solid_04_08);
                break;
            case AppConfig.ORDER_STATUS_501: //已签收
            case AppConfig.ORDER_STATUS_701: //待安装
                tv_status.setText(context.getString(R.string.order_wait_install));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_07_04);
                tv_click_02.setVisibility(View.VISIBLE);
                tv_click_02.setText(context.getString(R.string.order_confirm_install));
                tv_click_02.setTextColor(context.getResources().getColor(R.color.app_color_white));
                tv_click_02.setBackgroundResource(R.drawable.shape_style_solid_07_08);
                break;
            case AppConfig.ORDER_STATUS_801: //已完成
                tv_status.setText(context.getString(R.string.order_completed));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_03_04);
                tv_click_02.setVisibility(View.VISIBLE);
                tv_click_02.setText(context.getString(R.string.order_delete));
                tv_click_02.setTextColor(context.getResources().getColor(R.color.app_color_gray_5));
                tv_click_02.setBackgroundResource(R.drawable.shape_style_empty_02_08);
                break;
            case AppConfig.ORDER_STATUS_102: //已取消
            default:
                tv_status.setText(context.getString(R.string.order_cancelled));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_03_04);
                tv_click_02.setVisibility(View.VISIBLE);
                tv_click_02.setText(context.getString(R.string.order_delete));
                tv_click_02.setTextColor(context.getResources().getColor(R.color.app_color_gray_5));
                tv_click_02.setBackgroundResource(R.drawable.shape_style_empty_02_08);
                break;
        }

        tv_click_01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (apCallback != null) {
                    apCallback.setOnClick(data, pos, 1);
                }
            }
        });

        tv_click_02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (apCallback != null) {
                    apCallback.setOnClick(data, pos, 2);
                }
            }
        });

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
