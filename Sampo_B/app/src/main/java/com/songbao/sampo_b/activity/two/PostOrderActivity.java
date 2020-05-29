package com.songbao.sampo_b.activity.two;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.songbao.sampo_b.entity.UserInfoEntity;
import com.songbao.sampo_b.utils.CleanDataManager;
import com.songbao.sampo_b.utils.CommonTools;
import com.songbao.sampo_b.utils.ExceptionUtil;
import com.songbao.sampo_b.utils.FileManager;
import com.songbao.sampo_b.utils.JsonUtils;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.utils.StringUtil;
import com.songbao.sampo_b.utils.retrofit.HttpRequests;
import com.songbao.sampo_b.widgets.ScrollViewEditText;
import com.songbao.sampo_b.widgets.ScrollViewListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;

public class PostOrderActivity extends BaseActivity implements View.OnClickListener {

    String TAG = PostOrderActivity.class.getSimpleName();

    @BindView(R.id.post_order_view_lv)
    ScrollViewListView lv_goods;

    @BindView(R.id.post_order_tv_goods_add)
    TextView tv_goods_add;

    @BindView(R.id.post_order_tv_price_show)
    TextView tv_price_total;

    @BindView(R.id.post_order_et_customer_name)
    EditText et_customer_name;

    @BindView(R.id.post_order_et_customer_phone)
    EditText et_customer_phone;

    @BindView(R.id.post_order_et_build_name)
    EditText et_build_name;

    @BindView(R.id.post_order_et_deal_store)
    TextView tv_deal_store;

    @BindView(R.id.post_order_et_hope_date)
    TextView tv_hope_date;

    @BindView(R.id.post_order_et_order_remarks)
    ScrollViewEditText et_remarks;

    @BindView(R.id.post_order_tv_order_remarks_byte)
    TextView tv_remarks_byte;

    @BindView(R.id.post_order_tv_confirm)
    TextView tv_confirm;

    GoodsOrderEditAdapter lv_adapter;

    private CharSequence[] storeItems;
    private int editPos = -1;
    private int goodsPos, imagePos;
    private double priceTotal;
    private String nameStr, phoneStr, buildName, storeName, hopeDate, orderRemarks;
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
        tv_deal_store.setOnClickListener(this);
        tv_hope_date.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);

        String storeStr = userManager.getStoreStr();
        if (!StringUtil.isNull(storeStr) && storeStr.contains("_")) {
            storeItems = storeStr.split("_");
            if (storeItems.length > 0) {
                storeName = storeItems[0].toString();
                tv_deal_store.setText(storeName);
            }
        }

        et_customer_phone.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.length() == 0) return;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < s.length(); i++) {
                    if (i != 3 && i != 8 && s.charAt(i) == ' ') {
                        break;
                    } else {
                        sb.append(s.charAt(i));
                        if ((sb.length() == 4 || sb.length() == 9) && sb.charAt(sb.length() - 1) != ' ') {
                            sb.insert(sb.length() - 1, ' ');
                        }
                    }
                }
                if (!sb.toString().equals(s.toString())) {
                    int index = start + 1;
                    if (sb.charAt(start) == ' ') {
                        if (before == 0) {
                            index++;
                        } else {
                            index--;
                        }
                    } else {
                        if (before == 1) {
                            index--;
                        }
                    }
                    et_customer_phone.setText(sb.toString());
                    et_customer_phone.setSelection(index);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_remarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tv_remarks_byte.setText(getString(R.string.viewpager_indicator, s.length(), 500));
            }
        });
        tv_remarks_byte.setText(getString(R.string.viewpager_indicator, 0, 500));
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
                    editPos = position;
                    GoodsEntity goodsEn = al_goods.get(position);
                    if (goodsEn != null) {
                        switch (type) {
                            case 1: //編輯
                                openGoodsEditActivity(goodsEn);
                                break;
                            case 2: //删除
                                showConfirmDialog(getString(R.string.order_goods_delete), getString(R.string.cancel), getString(R.string.confirm), new MyHandler(PostOrderActivity.this), 1001);
                                break;
                            case 6: //滚动
                                lv_adapter.reset(editPos);
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
        priceTotal = 0;
        for (GoodsEntity goodsEn : al_goods) {
            priceTotal += goodsEn.getOnePrice() * goodsEn.getNumber();
        }
        tv_price_total.setText(df.format(priceTotal));
    }

    private void deleteOrderData() {
        al_goods.remove(editPos);
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
        intent.putExtra("editPos", editPos);
        intent.putExtra(AppConfig.PAGE_DATA, goodsEn);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_order_tv_goods_add:
                openActivity(GoodsSortActivity.class);
                break;
            case R.id.post_order_et_deal_store:
                if (storeItems != null && storeItems.length > 0) {
                    selectStore();
                } else {
                    loadStoreData();
                }
                break;
            case R.id.post_order_et_hope_date:
                showDateDialog();
                break;
            case R.id.post_order_tv_confirm:
                if (checkData()) {
                    checkPhotoUrl();
                }
                break;
        }
    }

    /**
     * 选择门店
     */
    private void selectStore() {
        showListDialog(getString(R.string.order_select_store), storeItems, true, new MyHandler(this));
    }

    private void handleSelectStore(Message msg) {
        if (msg == null || storeItems == null) return;
        int pos = msg.what;
        if (pos >= 0 && pos < storeItems.length) {
            storeName = storeItems[pos].toString();
            if (tv_deal_store != null) {
                tv_deal_store.setText(storeName);
            }
        }
    }

    /**
     * 选择日期
     */
    private void showDateDialog() {
        String[] dates = null;
        if (!StringUtil.isNull(hopeDate) && hopeDate.contains("-")) { //解析当前生日
            dates = hopeDate.split("-");
        }
        int year = 0;
        int month = 0;
        int day = 0;
        if (dates != null && dates.length > 2) { //判定当前生日有效性1
            year = StringUtil.getInteger(dates[0]);
            month = StringUtil.getInteger(dates[1]) - 1;
            day = StringUtil.getInteger(dates[2]);
        }
        if (year == 0 || month == 0 || day == 0) { //判定当前生日有效性2
            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }
        new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // 修改本地生日
                String yearStr = year + "-";
                String monthStr;
                if (monthOfYear < 9) {
                    monthStr = "0" + (monthOfYear + 1) + "-";
                } else {
                    monthStr = (monthOfYear + 1) + "-";
                }
                String dayStr;
                if (dayOfMonth < 10) {
                    dayStr = "0" + dayOfMonth;
                } else {
                    dayStr = "" + dayOfMonth;
                }
                hopeDate = yearStr + monthStr + dayStr;
                tv_hope_date.setText(hopeDate);
            }
        }, year, month, day).show();
    }

    private boolean checkData() {
        // 产品非空
        if (al_goods.size() <= 0) {
            CommonTools.showToast(getString(R.string.order_goods_null_hint));
            return false;
        }
        // 客户名称非空
        nameStr = et_customer_name.getText().toString();
        if (StringUtil.isNull(nameStr)) {
            CommonTools.showToast(getString(R.string.order_customer_name_hint));
            return false;
        }
        // 校验客户电话
        phoneStr = et_customer_phone.getText().toString();
        if (phoneStr.contains(" ")) { //号码去空
            phoneStr = phoneStr.replace(" ", "");
        }
        if (StringUtil.isNull(phoneStr)) {
            CommonTools.showToast(getString(R.string.order_customer_phone_hint));
            return false;
        }
        if (phoneStr.length() < 11 || !StringUtil.isMobileNO(phoneStr)) {
            CommonTools.showToast(getString(R.string.login_phone_input_error));
            return false;
        }
        // 楼盘名称非空
        buildName = et_build_name.getText().toString();
        if (StringUtil.isNull(buildName)) {
            CommonTools.showToast(getString(R.string.order_build_name_hint));
            return false;
        }
        // 成交门店非空
        if (StringUtil.isNull(storeName)) {
            CommonTools.showToast(getString(R.string.order_deal_store_hint));
            return false;
        }
        orderRemarks = et_remarks.getText().toString();
        return true;
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
        CleanDataManager.cleanCustomCache(AppConfig.PATH_TEXT_CACHE + AppConfig.orderDataFileName);
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
        showConfirmDialog(getString(R.string.order_abandon), getString(R.string.delete_think),getString(R.string.leave_confirm), new MyHandler(this), 1004);
    }

    private void checkPhotoUrl() {
        if (isUpload()) {
            String photoUrl = al_goods.get(goodsPos).getImageList().get(imagePos);
            uploadPhoto(photoUrl);
        } else {
            postOrderData();
        }
    }

    private boolean isUpload() {
        GoodsEntity goodsEn;
        String photoUrl;
        for (int i = 0; i < al_goods.size(); i++) {
            goodsPos = i;
            goodsEn = al_goods.get(goodsPos);
            if (goodsEn != null && goodsEn.getImageList() != null) {
                for (int j = 0; j < goodsEn.getImageList().size(); j++) {
                    imagePos = j;
                    photoUrl = goodsEn.getImageList().get(imagePos);
                    if (!StringUtil.isNull(photoUrl) && !photoUrl.contains("http://")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 上传照片
     */
    private void uploadPhoto(String photoUrl) {
        if (!StringUtil.isNull(photoUrl)) {
            startAnimation();
            uploadPushFile(new File(photoUrl), 2, AppConfig.REQUEST_SV_UPLOAD_PHOTO);
        }
    }

    /**
     * 加载门店数据
     */
    private void loadStoreData() {
        startAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HashMap<String, Object> map = new HashMap<>();
                loadSVData(AppConfig.URL_USER_GET, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_USER_GET);
            }
        }, AppConfig.LOADING_TIME);
    }

    /**
     * 提交订单
     */
    private void postOrderData() {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("customPrice", priceTotal);
            jsonObj.put("clientName", nameStr);
            jsonObj.put("clientPhone", phoneStr);
            jsonObj.put("propertyName", buildName);
            jsonObj.put("dealShop", storeName);
            jsonObj.put("expectedSpan", hopeDate);
            jsonObj.put("remark", orderRemarks);

            JSONArray jsonArr = new JSONArray();
            for (int i = 0; i < al_goods.size(); i++) {
                GoodsEntity goodsEn = al_goods.get(i);
                if (goodsEn != null) {
                    JSONObject goodsJson = new JSONObject();
                    goodsJson.put("productName", goodsEn.getName());
                    goodsJson.put("isIsPicture", goodsEn.isPicture());
                    goodsJson.put("vcrUrl", goodsEn.getEffectUrl());
                    goodsJson.put("buyNum", goodsEn.getNumber());
                    goodsJson.put("buyPrice", goodsEn.getOnePrice());
                    goodsJson.put("remark", goodsEn.getRemarks());
                    goodsJson.put("pics", new JSONArray(goodsEn.getImageList()));
                    jsonArr.put(i, goodsJson);
                }
            }
            jsonObj.put("products", jsonArr);

            postJsonData(AppConfig.URL_ORDER_CREATE, jsonObj, AppConfig.REQUEST_SV_ORDER_CREATE);
        } catch (JSONException e) {
            ExceptionUtil.handle(e);
        }
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        BaseEntity baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_UPLOAD_PHOTO:
                    baseEn = JsonUtils.getUploadResult(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        if (goodsPos >= 0 && goodsPos < al_goods.size()) {
                            ArrayList<String> imgList = al_goods.get(goodsPos).getImageList();
                            if (imgList != null) {
                                ArrayList<String> newList = new ArrayList<>();
                                for (int i = 0; i < imgList.size(); i++) {
                                    if (imagePos == i) {
                                        newList.add(baseEn.getOthers());
                                    } else {
                                        newList.add(imgList.get(i));
                                    }
                                }
                                al_goods.get(goodsPos).setImageList(newList);
                            }
                        }
                        checkPhotoUrl();
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_USER_GET:
                    BaseEntity<UserInfoEntity> userEn = JsonUtils.getUserInfo(jsonObject);
                    if (userEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        userManager.saveUserInfo(userEn.getData());
                        String storeStr = userManager.getStoreStr();
                        if (!StringUtil.isNull(storeStr) && storeStr.contains("_")) {
                            storeItems = storeStr.split("_");
                            selectStore();
                        } else {
                            CommonTools.showToast(getString(R.string.order_deal_store_null));
                        }
                    }
                    break;
                case AppConfig.REQUEST_SV_ORDER_CREATE:
                    baseEn = JsonUtils.getBaseErrorData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        CommonTools.showToast(getString(R.string.order_post_ok));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 500);
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
            if (theActivity != null) {
                switch (msg.what) {
                    case 1001: //删除
                        theActivity.deleteOrderData();
                        break;
                    case 1004: //离开
                        theActivity.finish();
                        break;
                    default:
                        theActivity.handleSelectStore(msg);
                        break;
                }
            }
        }
    }

}
