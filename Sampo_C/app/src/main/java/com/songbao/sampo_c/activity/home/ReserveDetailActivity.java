package com.songbao.sampo_c.activity.home;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.activity.BaseActivity;
import com.songbao.sampo_c.adapter.AdapterCallback;
import com.songbao.sampo_c.adapter.ImageListAdapter;
import com.songbao.sampo_c.entity.BaseEntity;
import com.songbao.sampo_c.entity.OptionEntity;
import com.songbao.sampo_c.entity.ThemeEntity;
import com.songbao.sampo_c.utils.BitmapUtil;
import com.songbao.sampo_c.utils.ExceptionUtil;
import com.songbao.sampo_c.utils.JsonUtils;
import com.songbao.sampo_c.utils.LogUtil;
import com.songbao.sampo_c.utils.QRCodeUtil;
import com.songbao.sampo_c.utils.StringUtil;
import com.songbao.sampo_c.utils.retrofit.HttpRequests;
import com.songbao.sampo_c.widgets.ObservableWebView;
import com.songbao.sampo_c.widgets.ScrollViewListView;
import com.songbao.sampo_c.wxapi.WXPayEntryActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;

public class ReserveDetailActivity extends BaseActivity implements View.OnClickListener {

    String TAG = ReserveDetailActivity.class.getSimpleName();

    @BindView(R.id.reserve_detail_iv_show)
    ImageView iv_show;

    @BindView(R.id.reserve_detail_tv_title)
    TextView tv_title;

    @BindView(R.id.reserve_detail_tv_name)
    TextView tv_name;

    @BindView(R.id.reserve_detail_tv_series)
    TextView tv_series;

    @BindView(R.id.reserve_detail_tv_slot)
    TextView tv_slot;

    @BindView(R.id.reserve_detail_tv_place)
    TextView tv_place;

    @BindView(R.id.reserve_detail_tv_suit)
    TextView tv_suit;

    @BindView(R.id.reserve_detail_date_main)
    ConstraintLayout date_main;

    @BindView(R.id.reserve_detail_tv_date)
    TextView tv_date;

    @BindView(R.id.reserve_detail_iv_date_go)
    ImageView iv_date_go;

    @BindView(R.id.reserve_detail_time_main)
    ConstraintLayout time_main;

    @BindView(R.id.reserve_detail_tv_time)
    TextView tv_time;

    @BindView(R.id.reserve_detail_iv_time_go)
    ImageView iv_time_go;

    @BindView(R.id.reserve_detail_code_main)
    ConstraintLayout code_main;

    @BindView(R.id.reserve_detail_tv_code)
    TextView tv_code;

    @BindView(R.id.reserve_detail_iv_code)
    ImageView iv_code;

    @BindView(R.id.reserve_detail_iv_code_large)
    ImageView iv_code_large;

    @BindView(R.id.reserve_detail_tv_cover)
    TextView tv_cover;

    @BindView(R.id.reserve_detail_click_main)
    ConstraintLayout click_main;

    @BindView(R.id.reserve_detail_tv_price)
    TextView tv_price;

    @BindView(R.id.reserve_detail_tv_click)
    TextView tv_click;

    @BindView(R.id.reserve_detail_tv_success)
    TextView tv_success;

    @BindView(R.id.reserve_detail_web_view)
    ObservableWebView myWebView;

    @BindView(R.id.reserve_lv_detail)
    ScrollViewListView lv_detail;

    private ImageListAdapter lv_detail_Adapter;

    private MyBroadcastReceiver myReceiver;
    private ThemeEntity data;
    private Bitmap qrImage;
    private int dataPos = 0; //列表定位器
    private int pageType = 0; //2:我的预约
    private int status = 0;
    private int writeOffStatus = 0;
    private double payAmount = 0.00;
    private boolean isDateOk = false;
    private boolean isTimeOk = false;
    private boolean isLoadOk = false;
    private boolean isChange = false;
    private String themeId, reserveId, titleStr, dateStr, timeStr, timeId;
    private ArrayList<String> al_head = new ArrayList<>();
    private ArrayList<String> al_detail = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_detail);

        dataPos = getIntent().getIntExtra("dataPos", 0);
        pageType = getIntent().getIntExtra(AppConfig.PAGE_TYPE, 0);
        data = (ThemeEntity) getIntent().getSerializableExtra(AppConfig.PAGE_DATA);
        if (data != null) {
            themeId = data.getThemeId();
            titleStr = data.getTitle();
            if (pageType == 2) { //我的预约
                reserveId = data.getEntityId();
            }
        }

        // 注册广播
        myReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConfig.RA_PAGE_RESERVE);
        registerReceiver(myReceiver, filter);

        initView();
        loadServerData();
    }

    private void initView() {
        setTitle(titleStr);

        date_main.setOnClickListener(this);
        time_main.setOnClickListener(this);
        iv_code.setOnClickListener(this);
        iv_code_large.setOnClickListener(this);
        tv_click.setOnClickListener(this);

        LinearLayout.LayoutParams showImgLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        showImgLP.width = AppApplication.screen_width;
        showImgLP.height = AppApplication.screen_width * AppConfig.IMG_HEIGHT / AppConfig.IMG_WIDTHS;
        iv_show.setLayoutParams(showImgLP);
    }

    private void setView(ThemeEntity data) {
        if (data != null) {
            if (data.getPicUrls() != null && data.getPicUrls().size() > 0) {
                al_head.clear();
                al_head.addAll(data.getPicUrls());
                Glide.with(AppApplication.getAppContext())
                        .load(al_head.get(0))
                        .apply(AppApplication.getShowOptions())
                        .into(iv_show);
            }

            tv_title.setText(titleStr);
            tv_name.setText(data.getUserName());
            tv_series.setText(data.getSeries());
            payAmount = data.getFees();
            tv_price.setText(df.format(payAmount));

            StringBuilder sb = new StringBuilder();
            String dateSlot = data.getDateSlot();
            if (!StringUtil.isNull(dateSlot)) {
                String[] dates = dateSlot.split(",");
                for (int i = 0; i < dates.length; i++) {
                    if(i > 0 && i%2 == 0) {
                        sb.append("\n");
                    }
                    sb.append(dates[i]);
                    if(i%2 == 0) {
                        sb.append("  ");
                    }
                }
            }
            tv_slot.setText(sb.toString());
            tv_place.setText(data.getAddress());
            tv_suit.setText(data.getSuit());

            status = data.getStatus();
            checkDateState();

            if (pageType == 2) { //我的预约
                tv_date.setText(data.getReserveDate());
                iv_date_go.setVisibility(View.GONE);
                tv_time.setText(data.getReserveTime());
                iv_time_go.setVisibility(View.GONE);

                code_main.setVisibility(View.VISIBLE);
                tv_code.setVisibility(View.GONE);
                click_main.setVisibility(View.GONE);
                tv_success.setVisibility(View.VISIBLE);

                writeOffStatus = data.getWriteOffStatus();
                switch (writeOffStatus) {
                    case 3: //已核销
                        tv_cover.setText(mContext.getString(R.string.cancelled));
                        tv_cover.setVisibility(View.VISIBLE);
                        tv_success.setText(mContext.getString(R.string.reserve_success_can));
                        break;
                    case 10: //已过期
                        tv_cover.setText(mContext.getString(R.string.expired));
                        tv_cover.setVisibility(View.VISIBLE);
                        tv_success.setText(mContext.getString(R.string.reserve_success_end));
                        break;
                    default:
                        tv_cover.setVisibility(View.GONE);
                        tv_success.setText(mContext.getString(R.string.reserve_success));
                        tv_code.setText(data.getCheckValue());
                        tv_code.setVisibility(View.VISIBLE);
                        iv_code.setOnClickListener(this);
                        break;
                }

                //Bitmap logoImg = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_logo);
                int imgSize = AppApplication.screen_width;
                qrImage = QRCodeUtil.createQRImage(data.getCheckValue(), imgSize, imgSize, 0, null);
                iv_code.setImageBitmap(BitmapUtil.getBitmap(qrImage, 360, 360));
            }

            if (!StringUtil.isNull(data.getLinkUrl())) {
                //网页详情
                lv_detail.setVisibility(View.GONE);
                myWebView.setVisibility(View.VISIBLE);
                initWebView(data.getLinkUrl());
            } else {
                //图片详情
                myWebView.setVisibility(View.GONE);
                lv_detail.setVisibility(View.VISIBLE);
                if (data.getDesUrls() != null) {
                    al_detail.clear();
                    al_detail.addAll(data.getDesUrls());
                }
                initListView();
            }
        }
    }

    @SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
    private void initWebView(String webUrl) {
        if (myWebView != null){
            //WebView属性设置
            WebSettings webSettings = myWebView.getSettings();
            webSettings.setDefaultTextEncodingName("UTF-8");
            webSettings.setJavaScriptEnabled(true); //设置支持javascript脚本
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); //设置缓冲的模式
            webSettings.setSupportZoom(true); //设置是否支持缩放
            webSettings.setBuiltInZoomControls(false); //设置是否支持缩放
            webSettings.setBlockNetworkImage(false); //解决图片不显示
            webSettings.setUseWideViewPort(true);  //设置推荐使用的窗口
            webSettings.setLoadWithOverviewMode(true);  //设置加载页面的模式
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局

            //设置可同时加载Https、Http的混合模式（解决微信链文图片不显示的问题）
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

            //开启硬件加速(华为部分手机会出现卡顿)
            myWebView.setLayerType(View.LAYER_TYPE_HARDWARE,null);

            //隐藏垂直滚动条
            myWebView.setVerticalScrollBarEnabled(false);
            myWebView.setHorizontalScrollBarEnabled(false);

            //设置不允许外部浏览器打开
            myWebView.setWebViewClient(new WebViewClient(){

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.contains("sxb")) {
                        view.loadUrl(url);
                        return true; //当加载重定向URL时，物理返回按键myWebView.canGoBack()判断为true。
                    }
                    return false;
                }
            });

            //加载Url
            myWebView.loadUrl(webUrl);
        }
    }

    private void initListView() {
        //商品详情
        if (lv_detail_Adapter == null) {
            lv_detail_Adapter = new ImageListAdapter(mContext);
            lv_detail_Adapter.addCallback(new AdapterCallback() {
                @Override
                public void setOnClick(Object data, int position, int type) {

                }
            });
        }
        lv_detail_Adapter.updateData(al_detail);
        lv_detail.setAdapter(lv_detail_Adapter);
        lv_detail.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reserve_detail_date_main:
            case R.id.reserve_detail_time_main:
                if (!checkOnClick()) return;
                openChoiceDateActivity(data);
                break;
            case R.id.reserve_detail_iv_code:
                if (writeOffStatus == 2 && qrImage != null) {
                    iv_code_large.setVisibility(View.VISIBLE);
                    iv_code_large.setImageBitmap(qrImage);
                }
                break;
            case R.id.reserve_detail_iv_code_large:
                iv_code_large.setVisibility(View.GONE);
                break;
            case R.id.reserve_detail_tv_click:
                if (!checkOnClick()) return;
                if (isDateOk && isTimeOk) {
                    postReserveData();
                } else {
                    openChoiceDateActivity(data);
                }
                break;
        }
    }

    /**
     * 校验点击事件
     */
    private boolean checkOnClick() {
        if (!isLoadOk) {
            dataErrorHandle();
            return false;
        }
        if (pageType == 2 || status != 1)
            return false;
        if (!isLogin()) { //未登录
            openLoginActivity();
            return false;
        }
        return true;
    }

    /**
     * 跳转至选择时间页面
     * @param data
     */
    private void openChoiceDateActivity(ThemeEntity data) {
        if (data == null) return;
        Intent intent = new Intent(mContext, ChoiceDateActivity.class);
        intent.putExtra("assignDay", dateStr);
        intent.putExtra("assignTime", timeStr);
        intent.putExtra(AppConfig.PAGE_DATA, data);
        startActivityForResult(intent, AppConfig.ACTIVITY_CODE_CHOICE_DATE);
    }

    private void checkDateState() {
        switch (status) {
            case 1: //已上架
                if (StringUtil.isNull(dateStr)) {
                    dateStr = "";
                    isDateOk = false;
                } else {
                    isDateOk = true;
                }
                tv_date.setText(dateStr);

                if (StringUtil.isNull(timeStr)) {
                    timeStr = "";
                    isTimeOk = false;
                } else {
                    isTimeOk = true;
                }
                tv_time.setText(timeStr);

                if (isDateOk && isTimeOk) {
                    setClickState(getString(R.string.pay_now), true);
                } else {
                    setClickState(getString(R.string.reserve_choice), true);
                }
                break;
            case 2: //已截止
                setClickState(getString(R.string.reserve_stop), false);
                break;
            default: //已下架
                setClickState(getString(R.string.reserve_lower), false);
                break;
        }
    }

    private void setClickState(String text, boolean isState) {
        if (!StringUtil.isNull(text)) {
            tv_click.setText(text);
        }
        changeViewState(tv_click, isState);
    }

    @Override
    protected void OnListenerLeft() {
        finish();
        super.OnListenerLeft();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
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
        // 注销广播
        if (myReceiver != null) {
            unregisterReceiver(myReceiver);
        }
        // 销毁容器
        if (myWebView != null) {
            //myWebView.clearCache(true);
            myWebView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void finish() {
        if (isChange) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(AppConfig.ACTIVITY_KEY_RESERVE_POS, dataPos);
            setResult(RESULT_OK, returnIntent);
        }
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
        HashMap<String, Object> map = new HashMap<>();
        map.put("activityId", themeId);
        if (pageType == 2) {
            map.put("reservationId", reserveId);
        }
        loadSVData(AppConfig.URL_ACTIVITY_DETAIL, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_ACTIVITY_DETAIL);
    }

    /**
     * 预约提交
     */
    private void postReserveData() {
        startAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HashMap<String, Object> map = new HashMap<>();
                map.put("activityId", themeId);
                map.put("reservationActivityId", timeId);
                loadSVData(AppConfig.URL_RESERVATION_ADD, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_RESERVATION_ADD);
            }
        }, AppConfig.LOADING_TIME);
    }

    /**
     * 在线支付
     */
    private void startPay(String orderNo) {
        if (StringUtil.isNull(orderNo) || payAmount <= 0) return;
        Intent intent = new Intent(mContext, WXPayEntryActivity.class);
        intent.putExtra("sourceType", WXPayEntryActivity.SOURCE_TYPE_2);
        intent.putExtra("orderSn", orderNo);
        intent.putExtra("orderTotal", df.format(payAmount));
        startActivityForResult(intent, AppConfig.ACTIVITY_CODE_PAY_DATA);
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        BaseEntity<ThemeEntity> baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_ACTIVITY_DETAIL:
                    baseEn = JsonUtils.getThemeDetail(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        data = baseEn.getData();
                        setView(data);
                        isLoadOk = true;
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_RESERVATION_ADD:
                    BaseEntity resultEn = JsonUtils.getPayOrderOn(jsonObject);
                    if (resultEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        startPay(resultEn.getOthers());
                    } else if (resultEn.getErrNo() == AppConfig.ERROR_CODE_NO_PAY) {
                        reserveSuccess();
                    } else {
                        showSuccessDialog(resultEn.getErrMsg(), false);
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
            if (requestCode == AppConfig.ACTIVITY_CODE_CHOICE_DATE) {
                OptionEntity optionEn = (OptionEntity) data.getSerializableExtra(AppConfig.PAGE_DATA);
                if (optionEn != null) {
                    dateStr = optionEn.getDate();
                    timeStr = optionEn.getTime();
                    timeId = optionEn.getEntityId();
                    checkDateState();
                }
            } else
            if (requestCode == AppConfig.ACTIVITY_CODE_PAY_DATA) {
                boolean isPayOk = data.getBooleanExtra(AppConfig.ACTIVITY_KEY_PAY_RESULT, false);
                if (isPayOk) {
                    reserveSuccess();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void reserveSuccess() {
        pageType = 2;
        iv_date_go.setVisibility(View.GONE);
        iv_time_go.setVisibility(View.GONE);
        click_main.setVisibility(View.GONE);

        handleReserveResult(1);
    }

    private void handleReserveResult(int resultCode) {
        String showStr;
        switch (resultCode) {
            case 2: //失败
                showStr = getString(R.string.reserve_fail);
                showSuccessDialog(showStr, false);
                break;
            case 3: //错误
                showStr = getString(R.string.reserve_error);
                showSuccessDialog(showStr, false);
                break;
            default: //成功
                showStr = getString(R.string.reserve_success_show);
                showSuccessDialog(showStr, true);
                break;
        }
        tv_success.setText(showStr);
        tv_success.setVisibility(View.VISIBLE);
    }

    class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int msgType = intent.getIntExtra(AppConfig.RA_PAGE_RESERVE_KEY, 0);
            LogUtil.i("PushManager", TAG + " onReceive msgType = " + msgType);
            switch (msgType) {
                case AppConfig.PUSH_MSG_TYPE_001: //刷新核销码
                    isChange = true;
                    loadServerData();
                    break;
            }
        }
    }

}
