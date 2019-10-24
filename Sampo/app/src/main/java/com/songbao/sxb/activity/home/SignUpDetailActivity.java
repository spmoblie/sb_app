package com.songbao.sxb.activity.home;


import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.songbao.sxb.AppApplication;
import com.songbao.sxb.AppConfig;
import com.songbao.sxb.R;
import com.songbao.sxb.activity.BaseActivity;
import com.songbao.sxb.entity.BaseEntity;
import com.songbao.sxb.entity.ThemeEntity;
import com.songbao.sxb.utils.ExceptionUtil;
import com.songbao.sxb.utils.JsonUtils;
import com.songbao.sxb.utils.LogUtil;
import com.songbao.sxb.utils.StringUtil;
import com.songbao.sxb.utils.retrofit.HttpRequests;
import com.songbao.sxb.widgets.ObservableWebView;

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
    private int themeId; //课程Id
    private boolean isSignUp = false;
    private boolean isLoadOk = false;
    private boolean isOnClick = true;
    private String imgUrl, webUrl, titleStr;
    private ArrayList<String> al_head = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_detail);

        pageType = getIntent().getIntExtra(AppConfig.PAGE_TYPE, 0);
        data = (ThemeEntity) getIntent().getExtras().getSerializable(AppConfig.PAGE_DATA);
        if (data != null) {
            themeId = data.getId();
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
            status = data.getStatus();
            tv_title.setText(titleStr);
            tv_name.setText(data.getUserName());
            tv_series.setText(data.getSeries());
            tv_time.setText(getString(R.string.sign_up_info_time, data.getStartTime(), data.getEndTime()));
            tv_place.setText(data.getAddress());
            tv_people.setText(getString(R.string.number_p) + getString(R.string.sign_up_info_number, data.getPeople(), data.getQuantity()));
            tv_suit.setText(data.getSuit());

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

            myWebView.setVerticalScrollBarEnabled(false);
            //开启硬件加速(华为部分手机会出现卡顿)
            myWebView.setLayerType(View.LAYER_TYPE_HARDWARE,null);

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
                if (!checkOnClick()) return;
                if (isOnClick) {
                    openSignUpActivity(data);
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
        if (pageType == 1)
            return false;
        return true;
    }

    @Override
    protected void onResume() {
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
        // 页面开始
        AppApplication.onPageStart(this, TAG);

        if (pageType == 1) { //我的报名
            if (status == 2) { //已过期
                setClickState(getString(R.string.sign_up_success_end), true);
            } else {
                setClickState(getString(R.string.sign_up_success), true);
            }
        } else {
            if (isLogin()) {
                // 报名状态
                isSignUp = userManager.isThemeSignUp(themeId);
                if (isSignUp) { //已报名
                    setClickState(getString(R.string.sign_up_already), false);
                } else {
                    if (status == 2) { //已截止
                        setClickState(getString(R.string.sign_up_end), false);
                    }
                }
            }
        }

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
        map.put("activityId", String.valueOf(themeId));
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
