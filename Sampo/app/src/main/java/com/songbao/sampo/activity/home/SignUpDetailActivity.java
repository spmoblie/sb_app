package com.songbao.sampo.activity.home;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import com.songbao.sampo.entity.ThemeEntity;
import com.songbao.sampo.utils.ExceptionUtil;
import com.songbao.sampo.utils.JsonUtils;
import com.songbao.sampo.utils.LogUtil;
import com.songbao.sampo.utils.StringUtil;
import com.songbao.sampo.utils.retrofit.HttpRequests;
import com.songbao.sampo.widgets.ObservableWebView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;

public class SignUpDetailActivity extends BaseActivity implements View.OnClickListener {

    String TAG = SignUpDetailActivity.class.getSimpleName();

    @BindView(R.id.sign_up_detail_iv_show)
    ImageView iv_show;

    @BindView(R.id.sign_up_detail_tv_title)
    TextView tv_title;

    @BindView(R.id.sign_up_detail_tv_name)
    TextView tv_name;

    @BindView(R.id.sign_up_detail_tv_series)
    TextView tv_series;

    @BindView(R.id.sign_up_detail_tv_time)
    TextView tv_time;

    @BindView(R.id.sign_up_detail_tv_place)
    TextView tv_place;

    @BindView(R.id.sign_up_detail_tv_people)
    TextView tv_people;

    @BindView(R.id.sign_up_detail_tv_suit)
    TextView tv_suit;

    @BindView(R.id.sign_up_detail_tv_click)
    TextView tv_click;

    @BindView(R.id.sign_up_detail_web_view)
    ObservableWebView myWebView;

    private LinearLayout.LayoutParams showImgLP;

    private ThemeEntity data;
    private int pageType = 0; //1:我的报名
    private int status; //1:报名中, 2:已截止
    private boolean isLoadOk = false;
    private boolean isOnClick = true;
    private String themeId, imgUrl, webUrl, titleStr;
    private ArrayList<String> al_head = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_detail);

        pageType = getIntent().getIntExtra(AppConfig.PAGE_TYPE, 0);
        data = (ThemeEntity) getIntent().getExtras().getSerializable(AppConfig.PAGE_DATA);
        if (data != null) {
            themeId = data.getThemeId();
            status = data.getStatus();
            titleStr = data.getTitle();
        }

        initView();
        loadServerData();
    }

    private void initView() {
        setTitle(titleStr);

        tv_click.setOnClickListener(this);

        showImgLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        showImgLP.width = AppApplication.screen_width;
        showImgLP.height = AppApplication.screen_width * AppConfig.IMG_HEIGHT / AppConfig.IMG_WIDTHS;
        iv_show.setLayoutParams(showImgLP);

        setView(data);
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
            tv_time.setText(getString(R.string.sign_up_info_time, data.getStartTime(), data.getEndTime()));
            tv_place.setText(data.getAddress());
            tv_people.setText(getString(R.string.sign_up_info_number, data.getPeople(), data.getQuantity()));
            tv_suit.setText(data.getSuit());

            status = data.getStatus();
            checkState();

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
            webSettings.setUseWideViewPort(true);  //设置推荐使用的窗口
            webSettings.setLoadWithOverviewMode(true);  //设置加载页面的模式

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

    private void setClickState(String text, boolean isState) {
        if (!StringUtil.isNull(text)) {
            tv_click.setText(text);
        }
        isOnClick = isState;
        changeViewState(tv_click, isState);
    }

    /**
     * 跳转至报名页面
     * @param data
     */
    private void openSignUpActivity(ThemeEntity data) {
        if (data == null) return;
        Intent intent = new Intent(mContext, SignUpActivity.class);
        intent.putExtra(AppConfig.PAGE_DATA, data);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_up_detail_tv_click:
                if (!checkClick()) return;
                if (isOnClick) {
                    openSignUpActivity(data);
                }
                break;
        }
    }

    /**
     * 校验事件
     */
    private boolean checkClick() {
        if (!isLoadOk) {
            dataErrorHandle();
            return false;
        }
        if (pageType == 1 || status == 2)
            return false;
        return true;
    }

    /**
     * 校验状态
     */
    private void checkState() {
        if (pageType == 1) { //我的报名
            if (status == 2) { //已过期
                setClickState(getString(R.string.sign_up_success_end), true);
            } else {
                setClickState(getString(R.string.sign_up_success), true);
            }
        } else {
            if (status == 2) { //已截止
                setClickState(getString(R.string.sign_up_end), false);
            }
        }
    }

    @Override
    protected void onResume() {
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
        // 页面开始
        AppApplication.onPageStart(this, TAG);

        checkState();

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
        loadSVData(AppConfig.URL_ACTIVITY_DETAIL, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_ACTIVITY_DETAIL);
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
            }
        } catch (Exception e) {
            loadFailHandle();
            ExceptionUtil.handle(e);
        }
    }

}
