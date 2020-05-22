package com.songbao.sampo_c.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.entity.OPurchaseEntity;
import com.songbao.sampo_c.widgets.ScrollViewListView;

import java.text.DecimalFormat;

public class MyPurchaseAdapter extends BaseRecyclerAdapter<OPurchaseEntity> {

    private DecimalFormat df;
    private GoodsOrderAdapter lv_adapter;

    public MyPurchaseAdapter(Context context, int resLayout) {
        super(context, resLayout);
        df = new DecimalFormat("0.00");
        lv_adapter = new GoodsOrderAdapter(context, false);
    }

    @Override
    public void bindData(BaseRecyclerHolder holder, final int pos) {
        // 获取View
        LinearLayout item_main = holder.getView(R.id.my_purchase_item_main);
        RelativeLayout tv_top = holder.getView(R.id.my_purchase_item_rl_top);
        TextView tv_time = holder.getView(R.id.my_purchase_item_tv_time);
        TextView tv_status = holder.getView(R.id.my_purchase_item_tv_status);
        ScrollViewListView lv_goods = holder.getView(R.id.my_purchase_item_lv_goods);
        TextView tv_number = holder.getView(R.id.my_purchase_item_tv_number);
        TextView tv_price = holder.getView(R.id.my_purchase_item_tv_price);
        TextView tv_click_01 = holder.getView(R.id.my_purchase_item_tv_click_01);
        TextView tv_click_02 = holder.getView(R.id.my_purchase_item_tv_click_02);

        // 绑定View
        final OPurchaseEntity data = mDataList.get(pos);

        if (pos == 0) {
            tv_top.setVisibility(View.VISIBLE);
        } else {
            tv_top.setVisibility(View.GONE);
        }

        lv_adapter.updateData(data.getGoodsLists());
        lv_goods.setAdapter(lv_adapter);

        tv_time.setText(data.getAddTime());
        tv_number.setText(context.getString(R.string.order_goods_num, data.getGoodsNum()));
        tv_price.setText(df.format(data.getTotalPrice()));

        tv_click_01.setVisibility(View.VISIBLE);
        tv_click_02.setVisibility(View.VISIBLE);
        switch (data.getStatus()) {
            case AppConfig.ORDER_STATUS_101: //待付款
                tv_status.setText(context.getString(R.string.order_wait_pay));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_09_04);
                tv_click_01.setText(context.getString(R.string.order_cancel));
                tv_click_01.setTextColor(context.getResources().getColor(R.color.app_color_gray_5));
                tv_click_01.setBackgroundResource(R.drawable.shape_style_empty_02_08);
                tv_click_02.setText(context.getString(R.string.order_pay));
                tv_click_02.setTextColor(context.getResources().getColor(R.color.app_color_white));
                tv_click_02.setBackgroundResource(R.drawable.shape_style_solid_04_08);
                break;
            case AppConfig.ORDER_STATUS_301: //待发货
                tv_status.setText(context.getString(R.string.order_wait_send));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_10_04);
                tv_click_01.setVisibility(View.GONE);
                tv_click_02.setVisibility(View.GONE);
                break;
            case AppConfig.ORDER_STATUS_401: //待收货
                tv_status.setText(context.getString(R.string.order_wait_receive));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_04_04);
                tv_click_01.setText(context.getString(R.string.order_logistics));
                tv_click_01.setTextColor(context.getResources().getColor(R.color.tv_color_status));
                tv_click_01.setBackgroundResource(R.drawable.shape_style_empty_04_08);
                tv_click_02.setText(context.getString(R.string.order_confirm_receipt));
                tv_click_02.setTextColor(context.getResources().getColor(R.color.app_color_white));
                tv_click_02.setBackgroundResource(R.drawable.shape_style_solid_04_08);
                break;
            case AppConfig.ORDER_STATUS_302: //退款中
                tv_status.setText(context.getString(R.string.order_refund_wait));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_05_04);
                tv_click_01.setVisibility(View.GONE);
                tv_click_02.setVisibility(View.GONE);
                break;
            case AppConfig.ORDER_STATUS_303: //已退款
                tv_status.setText(context.getString(R.string.order_refund_done));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_03_04);
                tv_click_01.setVisibility(View.GONE);
                tv_click_02.setText(context.getString(R.string.order_delete));
                tv_click_02.setTextColor(context.getResources().getColor(R.color.app_color_white));
                tv_click_02.setBackgroundResource(R.drawable.shape_style_solid_03_08);
                break;
            case AppConfig.ORDER_STATUS_501: //待评价
                tv_status.setText(context.getString(R.string.order_wait_opinion));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_03_04);
                tv_click_01.setVisibility(View.GONE);
                tv_click_02.setText(context.getString(R.string.comment_want));
                tv_click_02.setTextColor(context.getResources().getColor(R.color.app_color_gray_5));
                tv_click_02.setBackgroundResource(R.drawable.shape_style_empty_02_08);
                break;
            case AppConfig.ORDER_STATUS_801: //已完成
                tv_status.setText(context.getString(R.string.order_completed));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_03_04);
                tv_click_01.setVisibility(View.GONE);
                tv_click_02.setText(context.getString(R.string.order_delete));
                tv_click_02.setTextColor(context.getResources().getColor(R.color.app_color_white));
                tv_click_02.setBackgroundResource(R.drawable.shape_style_solid_03_08);
                break;
            case AppConfig.ORDER_STATUS_102: //已取消
            default:
                tv_status.setText(context.getString(R.string.order_cancelled));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_03_04);
                tv_click_01.setVisibility(View.GONE);
                tv_click_02.setText(context.getString(R.string.order_delete));
                tv_click_02.setTextColor(context.getResources().getColor(R.color.app_color_white));
                tv_click_02.setBackgroundResource(R.drawable.shape_style_solid_03_08);
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
