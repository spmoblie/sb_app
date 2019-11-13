package com.songbao.sampo.activity.home;


import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo.AppApplication;
import com.songbao.sampo.AppConfig;
import com.songbao.sampo.R;
import com.songbao.sampo.activity.BaseActivity;
import com.songbao.sampo.entity.BaseEntity;
import com.songbao.sampo.entity.OptionEntity;
import com.songbao.sampo.entity.ThemeEntity;
import com.songbao.sampo.utils.BitmapUtil;
import com.songbao.sampo.utils.ExceptionUtil;
import com.songbao.sampo.utils.JsonUtils;
import com.songbao.sampo.utils.LogUtil;
import com.songbao.sampo.utils.QRCodeUtil;
import com.songbao.sampo.utils.StringUtil;
import com.songbao.sampo.utils.retrofit.HttpRequests;
import com.songbao.sampo.widgets.ObservableWebView;
import com.songbao.sampo.wxapi.WXPayEntryActivity;

import org.json.JSONObject;

import java.text.DecimalFormat;
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

    @BindView(R.id.reserve_detail_tv_cost)
    TextView tv_cost;

    @BindView(R.id.reserve_detail_tv_click)
    TextView tv_click;

    @BindView(R.id.reserve_detail_tv_success)
    TextView tv_success;

    @BindView(R.id.reserve_detail_web_view)
    ObservableWebView myWebView;

    private LinearLayout.LayoutParams showImgLP;

    private DecimalFormat df;
    private ThemeEntity data;
    private Bitmap qrImage;
    private int pageType = 0; //2:我的预约
    private double payAmount = 0.00;
    private boolean isDateOk = false;
    private boolean isTimeOk = false;
    private boolean isLoadOk = false;
    private String themeId, reserveId, imgUrl, webUrl, titleStr, dateStr, timeStr, timeId, orderNo;
    private ArrayList<String> al_head = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_detail);

        pageType = getIntent().getIntExtra(AppConfig.PAGE_TYPE, 0);
        data = (ThemeEntity) getIntent().getExtras().getSerializable(AppConfig.PAGE_DATA);
        if (data != null) {
            themeId = data.getThemeId();
            titleStr = data.getTitle();
            if (pageType == 2) { //我的预约
                reserveId = data.getEntityId();
            }
        }
        df = new DecimalFormat("0.00");

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

        showImgLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        showImgLP.width = AppApplication.screen_width;
        showImgLP.height = AppApplication.screen_width * AppConfig.IMG_HEIGHT / AppConfig.IMG_WIDTHS;
        iv_show.setLayoutParams(showImgLP);
    }

    private void setView(ThemeEntity data) {
        if (data != null) {
            if (data.getPicUrls() != null && data.getPicUrls().size() > 0) {
                al_head.clear();
                al_head.addAll(data.getPicUrls());
                imgUrl = al_head.get(0);
                Glide.with(AppApplication.getAppContext())
                        .load(imgUrl)
                        .apply(AppApplication.getShowOptions())
                        .into(iv_show);
            }

            tv_title.setText(titleStr);
            tv_name.setText(data.getUserName());
            tv_series.setText(data.getSeries());
            payAmount = data.getFees();
            tv_cost.setText(df.format(payAmount));

            StringBuffer sb = new StringBuffer();
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

            if (pageType == 2) { //我的预约
                tv_date.setText(data.getReserveDate());
                iv_date_go.setVisibility(View.GONE);
                tv_time.setText(data.getReserveTime());
                iv_time_go.setVisibility(View.GONE);

                code_main.setVisibility(View.VISIBLE);
                click_main.setVisibility(View.GONE);
                tv_success.setVisibility(View.VISIBLE);

                switch (data.getWriteOffStatus()) {
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

            webUrl = data.getLinkUrl();
            initWebView();
        }
    }

    @SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
    private void initWebView() {
        if (myWebView != null){
            //WebView属性设置
            WebSettings webSettings = myWebView.getSettings();
            webSettings.setDefaultTextEncodingName("UTF-8");
            webSettings.setJavaScriptEnabled(true); //设置支持javascript脚本
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); //设置缓冲的模式
            webSettings.setBuiltInZoomControls(false); //设置是否支持缩放
            webSettings.setBlockNetworkImage(false); //解决图片不显示

            //设置可同时加载Https、Http的混合模式（解决微信链文图片不显示的问题）
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

            //开启硬件加速(华为部分手机会出现卡顿)
            myWebView.setLayerType(View.LAYER_TYPE_HARDWARE,null);

            //隐藏垂直滚动条
            myWebView.setVerticalScrollBarEnabled(false);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reserve_detail_date_main:
            case R.id.reserve_detail_time_main:
                if (!checkOnClick()) return;
                openChoiceDateActivity(data);
                break;
            case R.id.reserve_detail_iv_code:
                if (qrImage != null) {
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
        if (pageType == 2)
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
    }

    private void setClickState(String text, boolean isState) {
        if (!StringUtil.isNull(text)) {
            tv_click.setText(text);
        }
        changeViewState(tv_click, isState);
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
        //清除缓存
        if (myWebView != null) {
            myWebView.clearCache(true);
        }
        super.onDestroy();
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
                HashMap<String, String> map = new HashMap<>();
                map.put("activityId", themeId);
                map.put("reservationActivityId", timeId);
                loadSVData(AppConfig.URL_RESERVATION_ADD, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_RESERVATION_ADD);
            }
        }, AppConfig.LOADING_TIME);
    }

    /**
     * 在线支付
     */
    private void startPay() {
        Intent intent = new Intent(mContext, WXPayEntryActivity.class);
        intent.putExtra(AppConfig.PAGE_TYPE, 2);
        intent.putExtra("orderSn", orderNo);
        intent.putExtra("orderTotal", df.format(payAmount));
        startActivityForResult(intent, AppConfig.ACTIVITY_CODE_PAY_DATA);
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        BaseEntity baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_ACTIVITY_DETAIL:
                    baseEn = JsonUtils.getThemeDetail(jsonObject);
                    if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
                        data = (ThemeEntity) baseEn.getData();
                        setView(data);
                        isLoadOk = true;
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_RESERVATION_ADD:
                    baseEn = JsonUtils.getPayOrderOn(jsonObject);
                    if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
                        orderNo = (String) baseEn.getData();
                        if (!StringUtil.isNull(orderNo)) {
                            startPay();
                        } else {
                            //无需支付处理
                        }
                    } else {
                        showSuccessDialog(baseEn.getErrmsg(), false);
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
                OptionEntity optionEn = (OptionEntity) data.getSerializableExtra(AppConfig.ACTIVITY_KEY_CHOICE_DATE);
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
                    pageType = 2;
                    iv_date_go.setVisibility(View.GONE);
                    iv_time_go.setVisibility(View.GONE);
                    click_main.setVisibility(View.GONE);

                    handleReserveResult(1);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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

}
