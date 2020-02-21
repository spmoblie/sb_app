package com.songbao.sampo_c.activity.mine;


import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.activity.BaseActivity;
import com.songbao.sampo_c.adapter.AdapterCallback;
import com.songbao.sampo_c.adapter.GoodsOrderAdapter;
import com.songbao.sampo_c.entity.AddressEntity;
import com.songbao.sampo_c.entity.BaseEntity;
import com.songbao.sampo_c.entity.CommentEntity;
import com.songbao.sampo_c.entity.GoodsEntity;
import com.songbao.sampo_c.entity.OPurchaseEntity;
import com.songbao.sampo_c.entity.ThemeEntity;
import com.songbao.sampo_c.utils.CommonTools;
import com.songbao.sampo_c.utils.ExceptionUtil;
import com.songbao.sampo_c.utils.JsonUtils;
import com.songbao.sampo_c.utils.LogUtil;
import com.songbao.sampo_c.utils.StringUtil;
import com.songbao.sampo_c.utils.retrofit.HttpRequests;
import com.songbao.sampo_c.widgets.ScrollViewListView;
import com.songbao.sampo_c.wxapi.WXPayEntryActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;

public class PurchaseActivity extends BaseActivity implements View.OnClickListener {

    String TAG = PurchaseActivity.class.getSimpleName();

    @BindView(R.id.purchase_address_main)
    ConstraintLayout address_main;

    @BindView(R.id.purchase_tv_address_district)
    TextView tv_address_district;

    @BindView(R.id.purchase_tv_address_detail)
    TextView tv_address_detail;

    @BindView(R.id.purchase_tv_address_name)
    TextView tv_address_name;

    @BindView(R.id.purchase_tv_address_go)
    ImageView iv_address_go;

    @BindView(R.id.purchase_tv_time_order)
    TextView tv_time_order;

    @BindView(R.id.purchase_tv_status)
    TextView tv_status;

    @BindView(R.id.purchase_lv_goods)
    ScrollViewListView lv_goods;

    @BindView(R.id.purchase_tv_goods_price_show)
    TextView tv_price_goods;

    @BindView(R.id.purchase_tv_freight_price_show)
    TextView tv_price_freight;

    @BindView(R.id.purchase_tv_discount_price_show)
    TextView tv_price_discount;

    @BindView(R.id.purchase_tv_total_price_show)
    TextView tv_price_total;

    @BindView(R.id.purchase_tv_order_number)
    TextView tv_order_number;

    @BindView(R.id.purchase_tv_order_number_copy)
    TextView tv_number_copy;

    @BindView(R.id.purchase_tv_order_pay_type)
    TextView tv_pay_type;

    @BindView(R.id.purchase_tv_order_pay_number)
    TextView tv_pay_number;

    @BindView(R.id.purchase_tv_order_time_add)
    TextView tv_time_add;

    @BindView(R.id.purchase_tv_order_time_pay)
    TextView tv_time_pay;

    @BindView(R.id.purchase_tv_order_time_send)
    TextView tv_time_send;

    @BindView(R.id.purchase_tv_order_time_done)
    TextView tv_time_done;

    @BindView(R.id.purchase_tv_click_01)
    TextView tv_click_01;

    @BindView(R.id.purchase_tv_click_02)
    TextView tv_click_02;

    GoodsOrderAdapter lv_Adapter;

    private OPurchaseEntity opEn;
    private int addressId = 0;
    private int updateType = 0;
    private ArrayList<GoodsEntity> al_goods = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        opEn = (OPurchaseEntity) getIntent().getSerializableExtra(AppConfig.PAGE_DATA);

        initView();
    }

    private void initView() {
        setTitle(getString(R.string.order_info));

        address_main.setOnClickListener(this);
        tv_number_copy.setOnClickListener(this);
        tv_click_01.setOnClickListener(this);
        tv_click_02.setOnClickListener(this);

        if (opEn != null) {
            loadServerData();
        }
    }

    private void initShowData() {
        if (opEn != null) {
            AddressEntity addEn = opEn.getAddEn();
            if (addEn != null) {
                addressId = addEn.getId();
                tv_address_district.setText(addEn.getDistrict());
                tv_address_detail.setText(addEn.getAddress());
                tv_address_name.setText(getString(R.string.address_name_phone, addEn.getName(), addEn.getPhone()));
            }

            al_goods.clear();
            al_goods.addAll(opEn.getGoodsLists());
            initListView();

            tv_time_order.setText(opEn.getAddTime());
            iv_address_go.setVisibility(View.GONE);
            tv_click_01.setVisibility(View.VISIBLE);
            tv_click_02.setVisibility(View.VISIBLE);
            switch (opEn.getStatus()) {
                case AppConfig.ORDER_STATUS_101: //待付款
                    tv_status.setText(getString(R.string.order_wait_pay));
                    tv_status.setBackgroundResource(R.drawable.shape_style_solid_09_04);
                    tv_click_01.setText(getString(R.string.order_cancel));
                    tv_click_01.setTextColor(getResources().getColor(R.color.app_color_gray_5));
                    tv_click_01.setBackgroundResource(R.drawable.shape_style_empty_02_08);
                    tv_click_02.setText(getString(R.string.order_pay));
                    tv_click_02.setBackgroundResource(R.drawable.shape_style_solid_04_08);

                    iv_address_go.setVisibility(View.VISIBLE);
                    break;
                case AppConfig.ORDER_STATUS_301: //待发货
                    tv_status.setText(getString(R.string.order_wait_send));
                    tv_status.setBackgroundResource(R.drawable.shape_style_solid_10_04);
                    tv_click_01.setVisibility(View.GONE);
                    tv_click_02.setVisibility(View.GONE);

                    iv_address_go.setVisibility(View.VISIBLE);
                    break;
                case AppConfig.ORDER_STATUS_401: //待收货
                    tv_status.setText(getString(R.string.order_wait_receive));
                    tv_status.setBackgroundResource(R.drawable.shape_style_solid_04_04);
                    tv_click_01.setText(getString(R.string.order_logistics));
                    tv_click_01.setTextColor(getResources().getColor(R.color.tv_color_status));
                    tv_click_01.setBackgroundResource(R.drawable.shape_style_empty_04_08);
                    tv_click_02.setText(getString(R.string.order_confirm_receipt));
                    tv_click_02.setBackgroundResource(R.drawable.shape_style_solid_04_08);
                    break;
                case AppConfig.ORDER_STATUS_302: //退款中
                    tv_status.setText(getString(R.string.order_refund_wait));
                    tv_status.setBackgroundResource(R.drawable.shape_style_solid_05_04);
                    tv_click_01.setText(getString(R.string.order_logistics));
                    tv_click_01.setTextColor(getResources().getColor(R.color.app_color_gray_5));
                    tv_click_01.setBackgroundResource(R.drawable.shape_style_empty_02_08);
                    tv_click_02.setVisibility(View.GONE);
                    break;
                case AppConfig.ORDER_STATUS_303: //已退款
                    tv_status.setText(getString(R.string.order_refund_done));
                    tv_status.setBackgroundResource(R.drawable.shape_style_solid_03_04);
                    tv_click_01.setText(getString(R.string.order_logistics));
                    tv_click_01.setTextColor(getResources().getColor(R.color.app_color_gray_5));
                    tv_click_01.setBackgroundResource(R.drawable.shape_style_empty_02_08);
                    tv_click_02.setText(getString(R.string.order_delete));
                    tv_click_02.setBackgroundResource(R.drawable.shape_style_solid_03_08);
                    break;
                case AppConfig.ORDER_STATUS_501: //待评价
                    tv_status.setText(getString(R.string.order_wait_opinion));
                    tv_status.setBackgroundResource(R.drawable.shape_style_solid_03_04);
                    tv_click_01.setText(getString(R.string.order_logistics));
                    tv_click_01.setTextColor(getResources().getColor(R.color.app_color_gray_5));
                    tv_click_01.setBackgroundResource(R.drawable.shape_style_empty_02_08);
                    tv_click_02.setVisibility(View.GONE);
                    break;
                case AppConfig.ORDER_STATUS_801: //已完成
                    tv_status.setText(getString(R.string.order_completed));
                    tv_status.setBackgroundResource(R.drawable.shape_style_solid_03_04);
                    tv_click_01.setText(getString(R.string.order_logistics));
                    tv_click_01.setTextColor(getResources().getColor(R.color.app_color_gray_5));
                    tv_click_01.setBackgroundResource(R.drawable.shape_style_empty_02_08);
                    tv_click_02.setText(getString(R.string.order_delete));
                    tv_click_02.setBackgroundResource(R.drawable.shape_style_solid_03_08);
                    break;
                case AppConfig.ORDER_STATUS_102: //已取消
                default:
                    tv_status.setText(getString(R.string.order_cancelled));
                    tv_status.setBackgroundResource(R.drawable.shape_style_solid_03_04);
                    tv_click_01.setVisibility(View.GONE);
                    tv_click_02.setText(getString(R.string.order_delete));
                    tv_click_02.setBackgroundResource(R.drawable.shape_style_solid_03_08);
                    break;
            }

            tv_price_goods.setText(getString(R.string.order_rmb, df.format(opEn.getGoodsPrice())));
            tv_price_freight.setText(getString(R.string.order_rmb_add, df.format(opEn.getFreightPrice())));
            tv_price_discount.setText(getString(R.string.order_rmb_minus, df.format(opEn.getDiscountPrice())));
            tv_price_total.setText(getString(R.string.order_rmb, df.format(opEn.getTotalPrice())));

            tv_order_number.setText(getString(R.string.order_order_no, opEn.getOrderNo()));
            tv_pay_type.setText(getString(R.string.order_pay_type, opEn.getPayType()));
            tv_pay_number.setText(getString(R.string.order_pay_no, opEn.getPayNo()));
            tv_time_add.setText(getString(R.string.order_time_add, opEn.getAddTime()));
            tv_time_pay.setText(getString(R.string.order_time_pay, opEn.getPayTime()));
            tv_time_send.setText(getString(R.string.order_time_send, opEn.getSendTime()));
            tv_time_done.setText(getString(R.string.order_time_done, opEn.getDoneTime()));
        }
    }

    private void initListView() {
        if (lv_Adapter == null) {
            lv_Adapter = new GoodsOrderAdapter(mContext, true);
            lv_Adapter.addCallback(new AdapterCallback() {
                @Override
                public void setOnClick(Object data, int position, int type) {
                    if (position < 0 || position >= al_goods.size()) return;
                    Intent intent;
                    switch (type) {
                        default: //查看商品
                            openGoodsActivity(al_goods.get(position).getSkuCode());
                            break;
                        case AppConfig.GOODS_SALE_01: //未售后
                            // 申请售后
                            /*intent = new Intent(mContext, PostSaleActivity.class);
                            intent.putExtra(AppConfig.PAGE_DATA, al_goods.get(position));
                            startActivity(intent);*/
                            showConfirmDialog(getString(R.string.order_post_sale_call),
                                    getString(R.string.cancel), getString(R.string.call),
                                    new MyHandler(PurchaseActivity.this), 104);
                            break;
                        case AppConfig.GOODS_SALE_02: //退款中
                        case AppConfig.GOODS_SALE_03: //已退款
                            // 退款详情
                            intent = new Intent(mContext, RefundActivity.class);
                            intent.putExtra(AppConfig.PAGE_DATA, al_goods.get(position));
                            startActivity(intent);
                            break;
                        case AppConfig.GOODS_SALE_04: //换货中
                        case AppConfig.GOODS_SALE_05: //已换货
                            // 退换详情
                            break;
                        case AppConfig.GOODS_COMM_01: //未评价
                            // 我要评价
                            CommentEntity cEn1 = new CommentEntity();
                            cEn1.setGoodsEn(al_goods.get(position));
                            openCommentPostActivity(cEn1);
                            break;
                        case AppConfig.GOODS_COMM_02: //已评价
                            // 追加评价
                            CommentEntity cEn2 = new CommentEntity();
                            cEn2.setGoodsEn(al_goods.get(position));
                            openCommentAddActivity(cEn2);
                            break;
                    }
                }
            });
        }
        lv_Adapter.updateData(al_goods);
        lv_Adapter.setOrderStatus(opEn.getStatus());
        lv_goods.setAdapter(lv_Adapter);
        lv_goods.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.purchase_address_main: //修改地址
                if (opEn != null && (opEn.getStatus() == AppConfig.ORDER_STATUS_101
                || opEn.getStatus() == AppConfig.ORDER_STATUS_301)) { //待付款或者待发货
                    Intent intent = new Intent(mContext, AddressActivity.class);
                    intent.putExtra("isFinish", true);
                    intent.putExtra("selectId", addressId);
                    startActivityForResult(intent, AppConfig.ACTIVITY_CODE_SELECT_ADDS);
                }
                break;
            case R.id.purchase_tv_order_number_copy:
                ClipboardManager clip = (ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                clip.setText(opEn.getOrderNo()); // Copy No
                CommonTools.showToast(getString(R.string.order_order_no_copy));
                break;
            case R.id.purchase_tv_click_01:
                if (opEn == null) return;
                switch (opEn.getStatus()) {
                    case AppConfig.ORDER_STATUS_101: //待付款
                        // 取消订单
                        showConfirmDialog(getString(R.string.order_cancel_confirm), new MyHandler(PurchaseActivity.this), 101);
                        break;
                    case AppConfig.ORDER_STATUS_302: //退款中
                    case AppConfig.ORDER_STATUS_303: //已退款
                    case AppConfig.ORDER_STATUS_401: //待收货
                    case AppConfig.ORDER_STATUS_501: //待评价
                    case AppConfig.ORDER_STATUS_801: //已完成
                        // 查看物流
                        showErrorDialog(R.string.order_logistics_null);
                        break;
                }
                break;
            case R.id.purchase_tv_click_02:
                if (opEn == null) return;
                switch (opEn.getStatus()) {
                    case AppConfig.ORDER_STATUS_101: //待付款
                        // 立即付款
                        startPay(opEn.getOrderNo(), opEn.getTotalPrice());
                        break;
                    case AppConfig.ORDER_STATUS_401: //待收货
                        // 确认收货
                        showConfirmDialog(getString(R.string.order_confirm_receipt_hint), new MyHandler(PurchaseActivity.this), 103);
                        break;
                    case AppConfig.ORDER_STATUS_303: //已退款
                    case AppConfig.ORDER_STATUS_801: //已完成
                    case AppConfig.ORDER_STATUS_102: //已取消
                    default:
                        // 删除订单
                        showConfirmDialog(getString(R.string.order_delete_confirm), new MyHandler(PurchaseActivity.this), 102);
                        break;
                }
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

    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(AppConfig.PAGE_DATA, updateType);
        setResult(RESULT_OK, returnIntent);
        super.finish();
    }

    /**
     * 数据报错处理
     */
    private void dataErrorHandle() {
        showDataError();
        loadServerData();
    }

    /**
     * 加载数据
     */
    private void loadServerData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("orderCode", opEn.getOrderNo());
        loadSVData(AppConfig.BASE_URL_3, AppConfig.URL_ORDER_INFO, map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_ORDER_INFO);
    }

    /**
     * 取消订单
     */
    private void postCancelOrder() {
        try {
            ArrayList<String> codeList = new ArrayList<>();
            codeList.add(opEn.getOrderNo());
            JSONArray codes = new JSONArray(codeList);
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("codeList", codes);
            postJsonData(AppConfig.BASE_URL_3, AppConfig.URL_ORDER_CANCEL, jsonObj, AppConfig.REQUEST_SV_ORDER_CANCEL);
        } catch (JSONException e) {
            ExceptionUtil.handle(e);
        }
    }

    /**
     * 删除订单
     */
    private void postDeleteOrder() {
        try {
            ArrayList<String> codeList = new ArrayList<>();
            codeList.add(opEn.getOrderNo());
            JSONArray codes = new JSONArray(codeList);
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("codeList", codes);
            postJsonData(AppConfig.BASE_URL_3, AppConfig.URL_ORDER_DELETE, jsonObj, AppConfig.REQUEST_SV_ORDER_DELETE);
        } catch (JSONException e) {
            ExceptionUtil.handle(e);
        }
    }

    /**
     * 确认收货
     */
    private void postConfirmReceipt() {
        HashMap<String, String> map = new HashMap<>();
        map.put("orderCode", opEn.getOrderNo());
        loadSVData(AppConfig.BASE_URL_3, AppConfig.URL_ORDER_CONFIRM, map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_ORDER_CONFIRM);
    }

    /**
     * 提交修改地址
     */
    private void postAddressData(AddressEntity addEn) {
        if (addEn == null) return;
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("orderCode", opEn.getOrderNo());
            jsonObj.put("recieverId", addEn.getId());
            postJsonData(AppConfig.BASE_URL_3, AppConfig.URL_ORDER_UPDATE, jsonObj, AppConfig.REQUEST_SV_ORDER_UPDATE);
        } catch (JSONException e) {
            ExceptionUtil.handle(e);
        }
    }

    /**
     * 在线支付
     */
    private void startPay(String orderSn, double payPrice) {
        if (StringUtil.isNull(orderSn) || payPrice <= 0) return;
        Intent intent = new Intent(mContext, WXPayEntryActivity.class);
        intent.putExtra("sourceType", WXPayEntryActivity.SOURCE_TYPE_3);
        intent.putExtra("orderSn", orderSn);
        intent.putExtra("orderTotal", df.format(payPrice));
        startActivityForResult(intent, AppConfig.ACTIVITY_CODE_PAY_DATA);
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        BaseEntity<OPurchaseEntity> baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_ORDER_INFO:
                    baseEn = JsonUtils.getOrderInfoData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        opEn = baseEn.getData();
                        initShowData();
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_ORDER_UPDATE:
                    baseEn = JsonUtils.getBaseErrorData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        loadServerData();
                        CommonTools.showToast(getString(R.string.address_change_ok));
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_ORDER_CANCEL:
                    baseEn = JsonUtils.getBaseErrorData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        updateType = AppConfig.ORDER_STATUS_102;
                        loadServerData();
                    } else if (baseEn.getErrNo() == AppConfig.ERROR_CODE_TIMEOUT) {
                        handleTimeOut();
                        finish();
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_ORDER_DELETE:
                    baseEn = JsonUtils.getBaseErrorData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        updateType = -999;
                        finish();
                    } else if (baseEn.getErrNo() == AppConfig.ERROR_CODE_TIMEOUT) {
                        handleTimeOut();
                        finish();
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_ORDER_CONFIRM:
                    baseEn = JsonUtils.getBaseErrorData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        updateType = AppConfig.ORDER_STATUS_501;
                        loadServerData();
                    } else if (baseEn.getErrNo() == AppConfig.ERROR_CODE_TIMEOUT) {
                        handleTimeOut();
                        finish();
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
                AddressEntity selectEn = (AddressEntity) data.getSerializableExtra(AppConfig.PAGE_DATA);
                postAddressData(selectEn);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    static class MyHandler extends Handler {

        WeakReference<PurchaseActivity> mActivity;

        MyHandler(PurchaseActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            PurchaseActivity theActivity = mActivity.get();
            switch (msg.what) {
                case 101: //取消订单
                    theActivity.postCancelOrder();
                    break;
                case 102: //删除订单
                    theActivity.postDeleteOrder();
                    break;
                case 103: //确认收货
                    theActivity.postConfirmReceipt();
                    break;
                case 104: //申请售后
                    theActivity.callPhone(AppConfig.SALE_PHONE);
                    break;
            }
        }
    }

}
