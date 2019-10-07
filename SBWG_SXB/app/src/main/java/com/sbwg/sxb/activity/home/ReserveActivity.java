package com.sbwg.sxb.activity.home;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppConfig;
import com.sbwg.sxb.R;
import com.sbwg.sxb.activity.BaseActivity;
import com.sbwg.sxb.entity.OptionEntity;
import com.sbwg.sxb.entity.ThemeEntity;
import com.sbwg.sxb.utils.LogUtil;
import com.sbwg.sxb.utils.StringUtil;

import butterknife.BindView;

public class ReserveActivity extends BaseActivity implements View.OnClickListener {

    String TAG = ReserveActivity.class.getSimpleName();

    @BindView(R.id.reserve_iv_show)
    ImageView iv_show;

    @BindView(R.id.reserve_tv_title)
    TextView tv_title;

    @BindView(R.id.reserve_tv_author)
    TextView tv_author;

    @BindView(R.id.reserve_tv_info)
    TextView tv_info;

    @BindView(R.id.reserve_tv_date)
    TextView tv_date;

    @BindView(R.id.reserve_tv_time)
    TextView tv_time;

    @BindView(R.id.reserve_iv_line_3)
    ImageView iv_line;

    @BindView(R.id.reserve_tv_explain)
    TextView tv_explain;

    @BindView(R.id.reserve_tv_cost)
    TextView tv_cost;

    @BindView(R.id.reserve_tv_reserve)
    TextView tv_reserve;

    LinearLayout.LayoutParams showImgLP;

    private ThemeEntity data;
    private double payAmount = 0.00;
    private int status; //1:报名中, 2:已截止
    private int themeId; //课程Id
    private int pageType; //页面类型
    private int themeType; //课程类型
    private boolean isSignUp = false;
    private boolean isPayOk = false;
    private boolean isDateOk = false;
    private boolean isTimeOk = false;
    private boolean isOnClick = true;
    private String imgUrl, titleStr, authorStr, infoStr, dateStr, timeStr, explainStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);

        pageType = getIntent().getIntExtra(AppConfig.PAGE_TYPE, 0);
        data = (ThemeEntity) getIntent().getExtras().getSerializable(AppConfig.PAGE_DATA);
        if (data != null) {
            themeId = data.getId();
            themeType = data.getThemeType();
            status = data.getStatus();
            imgUrl = data.getPicUrl();
            payAmount = data.getFees();
            titleStr = data.getTitle();
            authorStr = data.getAuthor();
            explainStr = data.getSynopsis();
        }

        initView();
    }

    private void initView() {
        setTitle(getString(R.string.sign_up_title));

        tv_date.setOnClickListener(this);
        tv_time.setOnClickListener(this);
        tv_reserve.setOnClickListener(this);

        showImgLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        showImgLP.height = screenWidth / 2;
        iv_show.setLayoutParams(showImgLP);
        Glide.with(AppApplication.getAppContext())
                .load(imgUrl)
                .apply(AppApplication.getShowOptions())
                .into(iv_show);

        tv_title.setText(titleStr);
        tv_author.setText(authorStr);
        tv_cost.setText(getString(R.string.pay_rmb) + String.valueOf(payAmount));

        String time = getString(R.string.reserve_time);
        String place = getString(R.string.reserve_place);
        String suit = getString(R.string.reserve_suit);
        String empty = getString(R.string.reserve_empty);
        String number = getString(R.string.number_p);
        if (themeType == 1) { //报名
            time = getString(R.string.time);
            place = getString(R.string.place);
            suit = getString(R.string.suit);
            if (data != null) {
                infoStr = time + getString(R.string.sign_up_info_time, data.getStartTime(), data.getEndTime()) +
                        "\n" + place + data.getAddress() +
                        "\n" + number + getString(R.string.sign_up_info_number, data.getPeople(), data.getQuantity()) +
                        "\n" + suit + data.getSuit();
            } else {
                infoStr = time + "\n" + place + "\n" + number + "\n" + suit;
            }
        } else {
            if (data != null) {
                infoStr = time + "09:00-10:30" + "  " + "11:00-12:30" +
                        "\n" + empty + "13:00-14:30" + "  " + "16:00-17:30" +
                        "\n" + place + data.getAddress() +
                        "\n" + suit + data.getSuit();
            } else {
                infoStr = time + "\n" + place + "\n" + suit;
            }
        }
        tv_info.setText(infoStr);

        if (themeType == 1) { //报名
            tv_date.setVisibility(View.GONE);
            tv_time.setVisibility(View.GONE);
            iv_line.setVisibility(View.GONE);
            changeViewText(getString(R.string.sign_up_now), true);
        } else {
            tv_date.setText(getString(R.string.sign_up_reserve_date, ""));
            tv_time.setText(getString(R.string.sign_up_reserve_time, ""));
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                String showStr = StringUtil.htmlDecode(explainStr);
                Message msg = new Message();
                msg.what = 1;
                msg.obj = showStr;
                mHandler.handleMessage(msg);
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reserve_tv_date:
            case R.id.reserve_tv_time:
                if (!isLogin()) { //未登录
                    openLoginActivity();
                    return;
                }
                openChoiceDateActivity(data);
                break;
            case R.id.reserve_tv_reserve:
                if (themeType == 1) { //报名
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
        showSuccessDialog(getString(R.string.reserve_success));
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
        tv_date.setText(getString(R.string.sign_up_reserve_date, dateStr));

        if (StringUtil.isNull(timeStr)) {
            timeStr = "";
            isTimeOk = false;
        } else {
            isTimeOk = true;
        }
        tv_time.setText(getString(R.string.sign_up_reserve_time, timeStr));

        if (isDateOk && isTimeOk) {
            changeViewText(getString(R.string.pay_now), true);
        } else {
            changeViewText(getString(R.string.reserve_choice), true);
        }
    }

    private void changeViewText(String text, boolean isState) {
        if (!StringUtil.isNull(text)) {
            tv_reserve.setText(text);
        }
        isOnClick = isState;
        if (isState) {
            tv_reserve.setTextColor(getResources().getColor(R.color.app_color_black));
        } else {
            tv_reserve.setTextColor(getResources().getColor(R.color.app_color_greys));
        }
    }

    @Override
    protected void onResume() {
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
        // 页面开始
        AppApplication.onPageStart(this, TAG);

        if (themeType == 1) { //报名
            if (isLogin()) {
                // 报名状态
                isSignUp = userManager.isThemeSignUp(themeId);
                if (isSignUp) { //已报名
                    changeViewText(getString(R.string.sign_up_already), false);
                } else {
                    if (status == 2) { //已截止
                        changeViewText(getString(R.string.sign_up_end), false);
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

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    tv_explain.setText((String)msg.obj);
                    break;
            }
        }
    };

}
