package com.songbao.sxb.activity.home;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sxb.AppApplication;
import com.songbao.sxb.AppConfig;
import com.songbao.sxb.R;
import com.songbao.sxb.activity.BaseActivity;
import com.songbao.sxb.adapter.AdapterCallback;
import com.songbao.sxb.adapter.DescriptionAdapter;
import com.songbao.sxb.entity.BaseEntity;
import com.songbao.sxb.entity.OptionEntity;
import com.songbao.sxb.entity.ThemeEntity;
import com.songbao.sxb.utils.CommonTools;
import com.songbao.sxb.utils.ExceptionUtil;
import com.songbao.sxb.utils.JsonUtils;
import com.songbao.sxb.utils.LogUtil;
import com.songbao.sxb.utils.StringUtil;
import com.songbao.sxb.utils.retrofit.HttpRequests;
import com.songbao.sxb.widgets.ScrollViewListView;
import com.songbao.sxb.wxapi.WXPayEntryActivity;

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

    @BindView(R.id.reserve_detail_time_main)
    ConstraintLayout time_main;

    @BindView(R.id.reserve_detail_tv_time)
    TextView tv_time;

    @BindView(R.id.reserve_detail_tv_cost)
    TextView tv_cost;

    @BindView(R.id.reserve_detail_tv_click)
    TextView tv_click;

    @BindView(R.id.reserve_detail_desc_listView)
    ScrollViewListView listView;

    private AdapterCallback apCallback;
    private DescriptionAdapter lv_Adapter;
    private LinearLayout.LayoutParams showImgLP;

    private ThemeEntity data;
    private double payAmount = 0.00;
    private int themeId; //课程Id
    private boolean isPayOk = false;
    private boolean isDateOk = false;
    private boolean isTimeOk = false;
    private boolean isLoadOk = false;
    private boolean isOnClick = true;
    private String imgUrl, titleStr, dateStr, timeStr;
    private ArrayList<String> al_head = new ArrayList<>();
    private ArrayList<String> al_show = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_detail);

        data = (ThemeEntity) getIntent().getExtras().getSerializable(AppConfig.PAGE_DATA);
        if (data != null) {
            themeId = data.getId();
            imgUrl = data.getPicUrl();
            titleStr = data.getTitle();
        }

        initView();
        loadServerData();
    }

    private void initView() {
        setTitle(titleStr);

        date_main.setOnClickListener(this);
        time_main.setOnClickListener(this);
        tv_click.setOnClickListener(this);

        showImgLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        showImgLP.width = AppApplication.screen_width;
        showImgLP.height = AppApplication.screen_width * AppConfig.IMG_HEIGHT / AppConfig.IMG_WIDTHS;
        iv_show.setLayoutParams(showImgLP);
        Glide.with(AppApplication.getAppContext())
                .load(imgUrl)
                .apply(AppApplication.getShowOptions())
                .into(iv_show);

        tv_title.setText(titleStr);
        if (data != null) {
            tv_name.setText(data.getUserName());
            tv_series.setText(data.getSeries());
        }
    }

    private void setView(ThemeEntity data) {
        if (data != null) {
            al_head.clear();
            if (data.getPicUrls() != null && data.getPicUrls().size() > 0) {
                al_head.addAll(data.getPicUrls());
                imgUrl = al_head.get(0);
                Glide.with(AppApplication.getAppContext())
                        .load(imgUrl)
                        .apply(AppApplication.getShowOptions())
                        .into(iv_show);
            }

            payAmount = data.getFees();
            tv_cost.setText(new DecimalFormat("0.00").format(payAmount));

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

            al_show.clear();
            if (data.getDesUrls() != null) {
                al_show.addAll(data.getDesUrls());
            }
            initListView();

            isLoadOk = true;
        }
    }

    private void initListView() {
        apCallback = new AdapterCallback() {
            @Override
            public void setOnClick(Object data, int position, int type) {

            }
        };
        lv_Adapter = new DescriptionAdapter(mContext);
        lv_Adapter.setDataList(al_show);
        lv_Adapter.setCallback(apCallback);

        listView.setAdapter(lv_Adapter);
        listView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reserve_detail_date_main:
            case R.id.reserve_detail_time_main:
                if (!isLogin()) { //未登录
                    openLoginActivity();
                    return;
                }
                /*if (!isLoadOk) {
                    dataErrorHandle();
                    return;
                }*/
                openChoiceDateActivity(data);
                break;
            case R.id.reserve_detail_tv_click:
                if (!isLoadOk) {
                    dataErrorHandle();
                    return;
                }
                if (!isLogin()) { //未登录
                    openLoginActivity();
                    return;
                }
                if (isDateOk && isTimeOk) {
                    getPayOrderSn();
                } else {
                    openChoiceDateActivity(data);
                }
                break;
        }
    }

    /**
     * 跳转至选择时间页面
     * @param data
     */
    private void openChoiceDateActivity(ThemeEntity data) {
        if (data == null) return;
        Intent intent = new Intent(mContext, ChoiceDateActivity.class);
        intent.putExtra(AppConfig.PAGE_DATA, data);
        startActivityForResult(intent, AppConfig.ACTIVITY_CODE_CHOICE_DATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == AppConfig.ACTIVITY_CODE_CHOICE_DATE) {
                OptionEntity optionEn = (OptionEntity) data.getSerializableExtra(AppConfig.ACTIVITY_KEY_CHOICE_DATE);
                if (optionEn != null) {
                    dateStr = optionEn.getDate();
                    timeStr = optionEn.getTime();
                    checkDateState();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        isOnClick = isState;
        if (isState) {
            tv_click.setTextColor(getResources().getColor(R.color.shows_text_color));
        } else {
            tv_click.setTextColor(getResources().getColor(R.color.app_color_gray));
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

    /**
     * 获取支付参数
     */
    private void getPayOrderSn() {
        if (data == null || themeId <= 0) {
            CommonTools.showToast(getString(R.string.toast_error_data_app));
            return;
        }
        startAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> map = new HashMap<>();
                map.put("activityId", String.valueOf(themeId));
                map.put("dateStr", dateStr);
                map.put("timeStr", timeStr);
                loadSVData(AppConfig.URL_RESERVATION_ADD, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_RESERVATION_ADD);
            }
        }, AppConfig.LOADING_TIME);
    }

    /**
     * 在线支付
     */
    private void startPay() {
        Intent intent = new Intent(mContext, WXPayEntryActivity.class);
        intent.putExtra("orderSn", "51135156651");
        intent.putExtra("orderTotal", "88.88");
        startActivity(intent);
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
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_RESERVATION_ADD:
                    baseEn = JsonUtils.getThemeDetail(jsonObject);
                    if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {

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
