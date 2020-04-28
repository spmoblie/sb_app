package com.songbao.sampo_b.activity.two;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.activity.BaseActivity;
import com.songbao.sampo_b.adapter.AdapterCallback;
import com.songbao.sampo_b.adapter.GoodsOrderEditAdapter;
import com.songbao.sampo_b.entity.BaseEntity;
import com.songbao.sampo_b.entity.GoodsEntity;
import com.songbao.sampo_b.entity.OCustomizeEntity;
import com.songbao.sampo_b.utils.CleanDataManager;
import com.songbao.sampo_b.utils.ExceptionUtil;
import com.songbao.sampo_b.utils.FileManager;
import com.songbao.sampo_b.utils.JsonUtils;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.widgets.ScrollViewListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
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

    GoodsOrderEditAdapter lv_adapter;

    private int mPosition = -1;
    private ArrayList<GoodsEntity> al_goods = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_order);

        initView();
    }

    private void initView() {
        setTitle(R.string.order_confirm);

        tv_goods_add.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
    }

    private void initShowData() {
        al_goods.clear();

        OCustomizeEntity ocEn = (OCustomizeEntity) FileManager.readFileSaveObject(AppConfig.orderDataFileName, 0);
        if (ocEn != null && ocEn.getGoodsList() != null) {
            al_goods.addAll(ocEn.getGoodsList());
        }

        updateOrderData();
    }

    private void initListView() {
        if (lv_adapter == null) {
            lv_adapter = new GoodsOrderEditAdapter(mContext);
            lv_adapter.addCallback(new AdapterCallback() {
                @Override
                public void setOnClick(Object data, int position, int type) {
                    if (position < 0 || position >= al_goods.size()) return;
                    mPosition = position;
                    GoodsEntity goodsEn = al_goods.get(position);
                    if (goodsEn != null) {
                        switch (type) {
                            case 1: //編輯
                                openGoodsEditActivity(goodsEn);
                                break;
                            case 2: //删除
                                showConfirmDialog(getString(R.string.order_goods_delete), getString(R.string.cancel), getString(R.string.confirm), new MyHandler(PostOrderActivity.this), 1);
                                break;
                            case 6: //滚动
                                lv_adapter.reset(mPosition);
                                break;
                        }
                    }
                }
            });
        }
        lv_adapter.updateData(al_goods);
        lv_goods.setAdapter(lv_adapter);
    }

    private void updateOrderData() {
        initListView();
        updateOrderPrice();
    }

    private void updateOrderPrice() {
        double priceTotal = 0;
        for (GoodsEntity goodsEn : al_goods) {
            priceTotal += goodsEn.getPrice() * goodsEn.getNumber();
        }
        tv_price_total.setText(df.format(priceTotal));
    }

    private void deleteOrderData() {
        al_goods.remove(mPosition);
        OCustomizeEntity ocCacheEn = new OCustomizeEntity();
        ocCacheEn.setGoodsList(al_goods);
        FileManager.writeFileSaveObject(AppConfig.orderDataFileName, ocCacheEn, 0);
        updateOrderData();
    }

    /**
     * 打开商品编辑页
     */
    @Override
    protected void openGoodsEditActivity(GoodsEntity goodsEn) {
        Intent intent = new Intent(mContext, GoodsEditActivity.class);
        intent.putExtra("isEdit", true);
        intent.putExtra("editPos", mPosition);
        intent.putExtra(AppConfig.PAGE_DATA, goodsEn);
        startActivity(intent);
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

        initShowData();

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
        // 清除缓存的订单数据
        CleanDataManager.cleanCustomCache(AppConfig.SAVE_PATH_TXT_DICE + AppConfig.orderDataFileName);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        ask4Leave();
    }

    @Override
    public void OnListenerLeft() {
        ask4Leave();
    }

    private void ask4Leave() {
        showConfirmDialog(getString(R.string.order_abandon), getString(R.string.delete_think),getString(R.string.leave_confirm), new MyHandler(this), 4);
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

    static class MyHandler extends Handler {

        WeakReference<PostOrderActivity> mActivity;

        MyHandler(PostOrderActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            PostOrderActivity theActivity = mActivity.get();
            switch (msg.what) {
                case 1: //删除
                    theActivity.deleteOrderData();
                    break;
                case 4: //离开
                    theActivity.finish();
                    break;
            }
        }
    }

}
