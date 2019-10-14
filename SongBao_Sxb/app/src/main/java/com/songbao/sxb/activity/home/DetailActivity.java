package com.songbao.sxb.activity.home;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sxb.AppApplication;
import com.songbao.sxb.AppConfig;
import com.songbao.sxb.R;
import com.songbao.sxb.activity.BaseActivity;
import com.songbao.sxb.entity.BaseEntity;
import com.songbao.sxb.entity.OptionEntity;
import com.songbao.sxb.entity.ThemeEntity;
import com.songbao.sxb.utils.ExceptionUtil;
import com.songbao.sxb.utils.JsonUtils;
import com.songbao.sxb.utils.LogUtil;
import com.songbao.sxb.utils.StringUtil;
import com.songbao.sxb.utils.TimeUtil;
import com.songbao.sxb.utils.retrofit.HttpRequests;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;

import butterknife.BindView;

public class DetailActivity extends BaseActivity implements View.OnClickListener {

    String TAG = DetailActivity.class.getSimpleName();

    @BindView(R.id.detail_iv_show)
    ImageView iv_show;

    @BindView(R.id.detail_tv_title)
    TextView tv_title;

    @BindView(R.id.detail_tv_author)
    TextView tv_author;

    @BindView(R.id.detail_tv_info)
    TextView tv_info;

    @BindView(R.id.detail_tv_date)
    TextView tv_date;

    @BindView(R.id.detail_tv_time)
    TextView tv_time;

    @BindView(R.id.detail_iv_line_3)
    ImageView iv_line;

    @BindView(R.id.detail_tv_explain)
    TextView tv_explain;

    @BindView(R.id.detail_tv_cost)
    TextView tv_cost;

    @BindView(R.id.detail_tv_click)
    TextView tv_click;

    LinearLayout.LayoutParams showImgLP;

    private ThemeEntity data;
    private double payAmount = 0.00;
    private int status; //1:报名中, 2:已截止
    private int themeId; //课程Id
    private int pageType; //页面类型
    private int themeType; //课程类型:0:报名/1:预约
    private boolean isSignUp = false;
    private boolean isPayOk = false;
    private boolean isDateOk = false;
    private boolean isTimeOk = false;
    private boolean isLoadOk = false;
    private boolean isOnClick = true;
    private String imgUrl, titleStr, dateStr, timeStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        pageType = getIntent().getIntExtra(AppConfig.PAGE_TYPE, 0);
        data = (ThemeEntity) getIntent().getExtras().getSerializable(AppConfig.PAGE_DATA);
        if (data != null) {
            themeId = data.getId();
            themeType = data.getThemeType();
            status = data.getStatus();
            imgUrl = data.getPicUrl();
            titleStr = data.getTitle();
        }

        initView();
        loadServerData();
    }

    private void initView() {
        setTitle(getString(R.string.sign_up_title));

        tv_date.setOnClickListener(this);
        tv_time.setOnClickListener(this);
        tv_click.setOnClickListener(this);

        showImgLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        showImgLP.height = screenWidth / 2;
        iv_show.setLayoutParams(showImgLP);
        Glide.with(AppApplication.getAppContext())
                .load(imgUrl)
                .apply(AppApplication.getShowOptions())
                .into(iv_show);

        tv_title.setText(titleStr);

        if (themeType == AppConfig.THEME_TYPE_0) { //报名
            tv_date.setVisibility(View.GONE);
            tv_time.setVisibility(View.GONE);
            iv_line.setVisibility(View.GONE);
            setClickState(getString(R.string.sign_up_now), true);
        } else {
            tv_date.setText(getString(R.string.reserve_date, ""));
            tv_time.setText(getString(R.string.reserve_time, ""));
        }
    }

    private void setView(ThemeEntity data) {
        if (data != null) {
            payAmount = data.getFees();

            tv_author.setText(data.getAuthor());
            tv_cost.setText(getString(R.string.pay_rmb, new DecimalFormat("0.00").format(payAmount)));

            String infoStr;
            if (themeType == AppConfig.THEME_TYPE_0) { //报名
                String timeStr = getString(R.string.time) + getString(R.string.sign_up_info_time,
                        TimeUtil.strToStrMdHm(data.getStartTime()), TimeUtil.strToStrMdHm(data.getEndTime()));
                infoStr = timeStr +
                        "\n" + getString(R.string.place) + data.getAddress() +
                        "\n" + getString(R.string.number_p) + getString(R.string.sign_up_info_number, data.getPeople(), data.getQuantity()) +
                        "\n" + getString(R.string.suit) + data.getSuit();
            } else {
                StringBuffer sb = new StringBuffer();
                sb.append(getString(R.string.reserve_slot));

                String dateSlot = data.getDateSlot();
                if (!StringUtil.isNull(dateSlot)) {
                    String[] dates = dateSlot.split(",");
                    for (int i = 0; i < dates.length; i++) {
                        if(i > 0 && i%2 == 0) {
                            sb.append("\n");
                            sb.append(getString(R.string.reserve_empty));
                        }
                        sb.append(dates[i]);
                        if(i%2 == 0) {
                            sb.append("  ");
                        }
                    }
                }

                infoStr = sb.toString() +
                        "\n" + getString(R.string.reserve_place, data.getAddress()) +
                        "\n" + getString(R.string.reserve_suit, data.getSuit());
            }
            tv_info.setText(infoStr);

            tv_explain.setText(StringUtil.htmlDecode(data.getSynopsis()));

            isLoadOk = true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detail_tv_date:
            case R.id.detail_tv_time:
                if (!isLogin()) { //未登录
                    openLoginActivity();
                    return;
                }
                if (!isLoadOk) {
                    dataErrorHandle();
                    return;
                }
                openChoiceDateActivity(data);
                break;
            case R.id.detail_tv_click:
                if (!isLoadOk) {
                    dataErrorHandle();
                    return;
                }
                if (themeType == AppConfig.THEME_TYPE_0) { //报名
                    if (isOnClick) {
                        openSignUpActivity(data);
                    }
                } else {
                    if (!isLogin()) { //未登录
                        openLoginActivity();
                        return;
                    }
                    if (isDateOk && isTimeOk) {
                        toPay();
                    } else {
                        openChoiceDateActivity(data);
                    }
                }
                break;
        }
    }

    /**
     * 在线支付
     */
    private void toPay() {
        showSuccessDialog(getString(R.string.reserve_success), false);
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

    /**
     * 跳转至报名页面
     * @param data
     */
    private void openSignUpActivity(ThemeEntity data) {
        if (data == null) return;
        Intent intent = new Intent(mContext, SignUpActivity.class);
        intent.putExtra(AppConfig.PAGE_TYPE, 1);
        intent.putExtra(AppConfig.PAGE_DATA, data);
        startActivity(intent);
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
        tv_date.setText(getString(R.string.reserve_date, dateStr));

        if (StringUtil.isNull(timeStr)) {
            timeStr = "";
            isTimeOk = false;
        } else {
            isTimeOk = true;
        }
        tv_time.setText(getString(R.string.reserve_time, timeStr));

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
            tv_click.setTextColor(getResources().getColor(R.color.app_color_black));
        } else {
            tv_click.setTextColor(getResources().getColor(R.color.app_color_greys));
        }
    }

    @Override
    protected void onResume() {
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
        // 页面开始
        AppApplication.onPageStart(this, TAG);

        if (themeType == AppConfig.THEME_TYPE_0) { //报名
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
