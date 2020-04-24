package com.songbao.sampo_b.activity.two;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.activity.BaseActivity;
import com.songbao.sampo_b.adapter.AdapterCallback;
import com.songbao.sampo_b.adapter.GoodsOrderAdapter;
import com.songbao.sampo_b.entity.BaseEntity;
import com.songbao.sampo_b.entity.GoodsEntity;
import com.songbao.sampo_b.entity.OCustomizeEntity;
import com.songbao.sampo_b.utils.ExceptionUtil;
import com.songbao.sampo_b.utils.JsonUtils;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.widgets.ScrollViewListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;

public class PostOrderActivity extends BaseActivity implements View.OnClickListener {

    String TAG = PostOrderActivity.class.getSimpleName();

    @BindView(R.id.post_order_view_lv)
    ScrollViewListView lv_goods;

    @BindView(R.id.post_order_tv_goods_add)
    TextView tv_goods_add;

    @BindView(R.id.post_order_tv_price_show)
    TextView tv_price_total;

    @BindView(R.id.post_order_tv_confirm)
    TextView tv_confirm;

    GoodsOrderAdapter lv_Adapter;

    private OCustomizeEntity ocEn;
    private int mPosition = -1;
    private ArrayList<GoodsEntity> al_goods = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_order);

        ocEn = (OCustomizeEntity) getIntent().getSerializableExtra(AppConfig.PAGE_DATA);

        initView();
    }

    private void initView() {
        setTitle(R.string.order_confirm);

        tv_goods_add.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);

        initShowData();
    }

    private void initShowData() {
        if (ocEn != null) {
            al_goods.clear();
            al_goods.addAll(ocEn.getGoodsList());
            initListView();
            updateOrderPrice();
        }
    }

    private void initListView() {
        if (lv_Adapter == null) {
            lv_Adapter = new GoodsOrderAdapter(mContext);
            lv_Adapter.addCallback(new AdapterCallback() {
                @Override
                public void setOnClick(Object data, int position, int type) {
                    if (position < 0 || position >= al_goods.size()) return;
                    mPosition = position;
                    GoodsEntity goodsEn = al_goods.get(position);
                    if (goodsEn != null) {
                        switch (type) {
                            case 1: //編輯

                                break;
                            case 2: //删除

                                break;
                            case 6: //滚动
                                lv_Adapter.reset(mPosition);
                                break;
                        }
                    }
                }
            });
        }
        lv_Adapter.updateData(al_goods);
        lv_goods.setAdapter(lv_Adapter);
    }

    private void updateOrderPrice() {
        double priceTotal = 0;
        for (GoodsEntity goodsEn : al_goods) {
            priceTotal += goodsEn.getPrice() * goodsEn.getNumber();
        }
        tv_price_total.setText(df.format(priceTotal));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_order_tv_goods_add:
                openActivity(GoodsSortActivity.class);
                break;
            case R.id.post_order_tv_confirm:
                postOrderData();
                break;
        }
    }

    @Override
    protected void onResume() {
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
        // 页面开始
        AppApplication.onPageStart(this, TAG);

        super.onResume();
    }

    @Override
    protected void onPause() {
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onPause");
        // 页面结束
        AppApplication.onPageEnd(this, TAG);

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 提交订单
     */
    private void postOrderData() {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("consigneeId", "");
            //postJsonData(AppConfig.URL_ORDER_CREATE, jsonObj, AppConfig.REQUEST_SV_ORDER_CREATE);
        } catch (JSONException e) {
            ExceptionUtil.handle(e);
        }
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        BaseEntity<OCustomizeEntity> baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_AUTH_OAUTH:
                    baseEn = JsonUtils.getPayOrderOn(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        ocEn = baseEn.getData();
                        initShowData();
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
            }
        } catch (Exception e) {
            loadFailHandle();
            ExceptionUtil.handle(e);
        }
    }

    @Override
    protected void loadFailHandle() {
        super.loadFailHandle();
        handleErrorCode(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == AppConfig.ACTIVITY_CODE_SELECT_ADDS) {
                OCustomizeEntity ocEn = (OCustomizeEntity) data.getSerializableExtra(AppConfig.PAGE_DATA);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
