package com.songbao.sampo.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songbao.sampo.R;
import com.songbao.sampo.entity.GoodsEntity;
import com.songbao.sampo.entity.PurchaseEntity;
import com.songbao.sampo.utils.CommonTools;
import com.songbao.sampo.widgets.ScrollViewListView;

import java.text.DecimalFormat;

public class MyPurchaseAdapter extends BaseRecyclerAdapter {

    private DecimalFormat df;
    private GoodsListAdapter lv_adapter;

    public MyPurchaseAdapter(Context context, int resLayout) {
        super(context, resLayout);
        df = new DecimalFormat("0.00");
        lv_adapter = new GoodsListAdapter(context);
        lv_adapter.addCallback(new AdapterCallback() {
            @Override
            public void setOnClick(Object data, int position, int type) {
                GoodsEntity goodsEn = (GoodsEntity) data;
                if (goodsEn != null) {
                    CommonTools.showToast("点击了商品 " + goodsEn.getId());
                }
            }
        });
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
        final PurchaseEntity data = (PurchaseEntity) mDataList.get(pos);

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
                tv_click_02.setText(context.getString(R.string.order_progress));
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
            case 4: //待评价
                tv_status.setText(context.getString(R.string.order_completed));
                tv_status.setBackgroundResource(R.drawable.shape_style_solid_03_04);
                tv_click_01.setVisibility(View.VISIBLE);
                tv_click_01.setText(context.getString(R.string.order_after_sale));
                tv_click_01.setTextColor(context.getResources().getColor(R.color.app_color_gray_5));
                tv_click_01.setBackgroundResource(R.drawable.shape_style_empty_02_08);
                tv_click_02.setText(context.getString(R.string.order_evaluate));
                tv_click_02.setBackgroundResource(R.drawable.shape_style_solid_06_08);
                break;
            case 5: //已完成
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
            case 6: //退换货
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
