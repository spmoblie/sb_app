package com.songbao.sampo_b.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.entity.OCustomizeEntity;
import com.songbao.sampo_b.utils.ClickUtils;
import com.songbao.sampo_b.widgets.ScrollViewListView;

public class CustomizeAdapter extends BaseRecyclerAdapter<OCustomizeEntity> {

    private GoodsOrderShowAdapter lv_adapter;

    public CustomizeAdapter(Context context, int resLayout) {
        super(context, resLayout);

        lv_adapter = new GoodsOrderShowAdapter(context);
    }

    @Override
    public void bindData(BaseRecyclerHolder holder, final int pos) {
        // 获取View
        LinearLayout item_main = holder.getView(R.id.customize_item_main);
        RelativeLayout tv_top = holder.getView(R.id.customize_item_rl_top);
        TextView tv_time = holder.getView(R.id.customize_item_tv_time);
        TextView tv_status = holder.getView(R.id.customize_item_tv_status);
        TextView tv_click = holder.getView(R.id.customize_item_tv_click);
        ScrollViewListView lv_goods = holder.getView(R.id.customize_item_lv_goods);

        // 绑定View
        final OCustomizeEntity data = mDataList.get(pos);

        if (pos == 0) {
            tv_top.setVisibility(View.VISIBLE);
        } else {
            tv_top.setVisibility(View.GONE);
        }

        tv_time.setText(data.getNodeTime1());

        lv_adapter.updateData(data.getGoodsList());
        lv_goods.setAdapter(lv_adapter);

        tv_click.setVisibility(View.GONE);
        switch (data.getStatus()) {
            case AppConfig.ORDER_STATUS_101: //待审核
                tv_status.setText(context.getString(R.string.order_wait_check));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_08_04);
                tv_click.setVisibility(View.VISIBLE);
                tv_click.setText(context.getString(R.string.order_cancel));
                tv_click.setTextColor(context.getResources().getColor(R.color.app_color_gray_5));
                tv_click.setBackgroundResource(R.drawable.shape_style_empty_02_08);
                break;
            case AppConfig.ORDER_STATUS_201: //待初核
            case AppConfig.ORDER_STATUS_202: //待复核
                tv_status.setText(context.getString(R.string.order_wait_price));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_09_04);
                tv_click.setVisibility(View.VISIBLE);
                tv_click.setText(context.getString(R.string.order_cancel));
                tv_click.setTextColor(context.getResources().getColor(R.color.app_color_gray_5));
                tv_click.setBackgroundResource(R.drawable.shape_style_empty_02_08);
                break;
            case AppConfig.ORDER_STATUS_301: //生产中
                tv_status.setText(context.getString(R.string.order_producing));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_04_04);
                break;
            case AppConfig.ORDER_STATUS_401: //已发货
                tv_status.setText(context.getString(R.string.order_wait_receive));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_11_04);
                tv_click.setVisibility(View.VISIBLE);
                tv_click.setText(context.getString(R.string.order_confirm_receive));
                tv_click.setTextColor(context.getResources().getColor(R.color.app_color_white));
                tv_click.setBackgroundResource(R.drawable.shape_style_solid_11_08);
                break;
            case AppConfig.ORDER_STATUS_801: //已完成
                tv_status.setText(context.getString(R.string.order_completed));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_03_04);
                tv_click.setVisibility(View.VISIBLE);
                tv_click.setText(context.getString(R.string.order_delete));
                tv_click.setTextColor(context.getResources().getColor(R.color.app_color_gray_5));
                tv_click.setBackgroundResource(R.drawable.shape_style_empty_02_08);
                break;
            case AppConfig.ORDER_STATUS_104: //已拒绝
                tv_status.setText(context.getString(R.string.order_refused));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_05_04);
                tv_click.setVisibility(View.VISIBLE);
                tv_click.setText(context.getString(R.string.order_cancel));
                tv_click.setTextColor(context.getResources().getColor(R.color.app_color_gray_5));
                tv_click.setBackgroundResource(R.drawable.shape_style_empty_02_08);
                break;
            case AppConfig.ORDER_STATUS_102: //已取消
            default:
                tv_status.setText(context.getString(R.string.order_cancelled));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_03_04);
                tv_click.setVisibility(View.VISIBLE);
                tv_click.setText(context.getString(R.string.order_delete));
                tv_click.setTextColor(context.getResources().getColor(R.color.app_color_gray_5));
                tv_click.setBackgroundResource(R.drawable.shape_style_empty_02_08);
                break;
        }

        tv_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtils.isDoubleClick(v.getId())) return;
                if (apCallback != null) {
                    apCallback.setOnClick(data, pos, 1);
                }
            }
        });

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
