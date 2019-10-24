package com.songbao.sxb.activity.home;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.songbao.sxb.utils.CommonTools;
import com.songbao.sxb.utils.ExceptionUtil;
import com.songbao.sxb.utils.JsonUtils;
import com.songbao.sxb.utils.LogUtil;
import com.songbao.sxb.utils.StringUtil;
import com.songbao.sxb.utils.retrofit.HttpRequests;
import com.songbao.sxb.wxapi.WXPayEntryActivity;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;

import butterknife.BindView;

public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    String TAG = SignUpActivity.class.getSimpleName();

    @BindView(R.id.sign_iv_show)
    ImageView iv_show;

    @BindView(R.id.sign_et_name)
    EditText et_name;

    @BindView(R.id.sign_tv_gender_man)
    TextView tv_gender_man;

    @BindView(R.id.sign_tv_gender_woman)
    TextView tv_gender_woman;

    @BindView(R.id.sign_et_age)
    EditText et_age;

    @BindView(R.id.sign_et_phone)
    EditText et_phone;

    @BindView(R.id.sign_tv_explain)
    TextView tv_explain;

    @BindView(R.id.sign_tv_cost)
    TextView tv_cost;

    @BindView(R.id.sign_tv_click)
    TextView tv_click;

    LinearLayout.LayoutParams showImgLP;

    private DecimalFormat df;
    private ThemeEntity data;
    private int themeId; //课程Id
    private int status; //1:报名中, 2:已截止
    private int genderCode = 1; //1:男, 2:女
    private double payAmount = 0.00;
    private boolean isSignUp = false;
    private boolean isPay = false;
    private boolean isName_Ok = false;
    private boolean isAge_Ok = false;
    private boolean isPhone_Ok = false;
    private boolean isLoadOk = false;
    private boolean isOnClick = false;
    private String imgUrl, nameStr, ageStr, phoneStr, orderNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        data = (ThemeEntity) getIntent().getExtras().getSerializable(AppConfig.PAGE_DATA);
        if (data != null) {
            themeId = data.getId();
            status = data.getStatus();
        }
        df = new DecimalFormat("0.00");

        initView();

        loadServerData();
    }

    private void initView() {
        setTitle(getString(R.string.sign_up_title));

        selectGender(genderCode);
        tv_gender_man.setOnClickListener(this);
        tv_gender_woman.setOnClickListener(this);

        et_age.setFocusable(false);
        et_age.setFocusableInTouchMode(false);
        et_age.setOnClickListener(this);

        tv_click.setOnClickListener(this);

        showImgLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        showImgLP.height = screenWidth / 2;
        iv_show.setLayoutParams(showImgLP);

        initEditText();
    }

    private void initEditText() {
        et_name.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isName_Ok = false;
                if (!s.toString().isEmpty()) {
                    isName_Ok = true;
                }
                changeState();
            }
        });
        et_phone.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    int length = s.toString().length();
                    if (length == 3 || length == 8) {
                        et_phone.setText(s + " ");
                        et_phone.setSelection(et_phone.getText().length());
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isPhone_Ok = false;
                phoneStr = s.toString();
                if (phoneStr.length() >= 13) {
                    // 号码去空
                    if (phoneStr.contains(" ")) {
                        phoneStr = phoneStr.replace(" ", "");
                    }
                    // 校验格式
                    if (!StringUtil.isMobileNO(phoneStr)) {
                        CommonTools.showToast(getString(R.string.login_phone_input_error));
                        return;
                    }
                    isPhone_Ok = true;
                }
                changeState();
            }
        });
    }

    private void setView(ThemeEntity data) {
        if (data != null) {
            if (data.getPicUrls() != null && data.getPicUrls().size() > 0) {
                imgUrl = data.getPicUrls().get(0);
                Glide.with(AppApplication.getAppContext())
                        .load(imgUrl)
                        .apply(AppApplication.getShowOptions())
                        .into(iv_show);
            }
            status = data.getStatus();
            payAmount = data.getFees();
            if (payAmount > 0) {
                isPay = true;
            }
            tv_cost.setText(df.format(payAmount));

            String timeStr = getString(R.string.time) + getString(R.string.sign_up_info_time, data.getStartTime(), data.getEndTime());
            String infoStr = timeStr +
                    "\n\n" + getString(R.string.place) + data.getAddress() +
                    "\n\n" + getString(R.string.number_p) + getString(R.string.sign_up_info_number, data.getPeople(), data.getQuantity()) +
                    "\n\n" + getString(R.string.suit) + data.getSuit();

            String otherStr = data.getDescription();
            if (StringUtil.isNull(otherStr)) {
                otherStr = getString(R.string.sign_up_cost_hint);
            }

            String showStr = infoStr +
                    "\n\n" + getString(R.string.other) + otherStr;

            tv_explain.setText(showStr);

            isLoadOk = true;
        }
    }

    private void changeState() {
        if (isSignUp) return;
        setClickState("", false);
        if (isName_Ok && isAge_Ok && isPhone_Ok) {
            setClickState("", true);
        }
    }

    private void setClickState(String text, boolean isState) {
        if (!StringUtil.isNull(text)) {
            tv_click.setText(text);
        }
        isOnClick = isState;
        changeViewState(tv_click, isState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_tv_gender_man:
                selectGender(1);
                break;
            case R.id.sign_tv_gender_woman:
                selectGender(2);
                break;
            case R.id.sign_et_age:
                selectAge();
                break;
            case R.id.sign_tv_click:
                if (!isLoadOk) {
                    dataErrorHandle();
                    return;
                }
                if (!isLogin()) { //未登录
                    openLoginActivity();
                    return;
                }
                if (isSignUp) return; //已报名
                if (status == 1 && checkData() && isOnClick) {
                    postSignUpData();
                }
                break;
        }
    }

    /**
     * 选择性别
     */
    private void selectGender(int code) {
        switch (code) {
            case 1:
                tv_gender_man.setSelected(true);
                tv_gender_woman.setSelected(false);
                break;
            case 2:
                tv_gender_man.setSelected(false);
                tv_gender_woman.setSelected(true);
                break;
        }
        genderCode = code;
    }

    /**
     * 选择年龄
     */
    private void selectAge() {
        final CharSequence[] items = getResources().getStringArray(R.array.array_gender);
        showListDialog(R.string.sign_up_input_age, items, true, new Handler() {

            @Override
            public void handleMessage(Message msg) {
                if (msg == null) return;
                String ageStr = items[msg.what].toString();
                if (et_age != null) {
                    et_age.setText(ageStr);
                    isAge_Ok = true;
                }
            }

        });
    }

    /**
     * 校验数据
     */
    private boolean checkData() {
        // 姓名非空
        nameStr = et_name.getText().toString();
        if (nameStr.isEmpty()) {
            CommonTools.showToast(getString(R.string.sign_up_input_name));
            return false;
        }
        // 年龄非空
        ageStr = et_age.getText().toString();
        if (ageStr.isEmpty()) {
            CommonTools.showToast(getString(R.string.sign_up_input_age));
            return false;
        }
        // 电话非空
        phoneStr = et_phone.getText().toString();
        if (phoneStr.isEmpty()) {
            CommonTools.showToast(getString(R.string.sign_up_input_phone));
            return false;
        }
        // 号码去空
        if (phoneStr.contains(" ")) {
            phoneStr = phoneStr.replace(" ", "");
        }
        // 校验格式
        if (!StringUtil.isMobileNO(phoneStr)) {
            CommonTools.showToast(getString(R.string.login_phone_input_error));
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
        // 页面开始
        AppApplication.onPageStart(this, TAG);

        // 报名状态
        isSignUp = userManager.isThemeSignUp(themeId);
        if (isLogin()) {
            if (isSignUp) { //已报名
                setClickState(getString(R.string.sign_up_already), false);
            } else {
                if (status == 2) { //已截止
                    setClickState(getString(R.string.sign_up_end), false);
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

    /**
     * 提交报名数据
     */
    private void postSignUpData() {
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
                map.put("name", nameStr);
                map.put("gender", String.valueOf(genderCode));
                map.put("ageStage", ageStr);
                map.put("mobile", phoneStr);
                loadSVData(AppConfig.URL_SIGN_UP_ADD, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_SIGN_UP_ADD);
            }
        }, AppConfig.LOADING_TIME);
    }

    /**
     * 在线支付
     */
    private void startPay() {
        Intent intent = new Intent(mContext, WXPayEntryActivity.class);
        intent.putExtra("orderSn", orderNo);
        intent.putExtra("orderTotal", df.format(payAmount));
        startActivityForResult(intent, AppConfig.ACTIVITY_CODE_PAY_DATA);
    }

    /**
     * 校验报名状态
     */
    private void checkSignUpState() {
        startAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> map = new HashMap<>();
                map.put("orderNo", orderNo);
                loadSVData(AppConfig.URL_SIGN_UP_CALLBACK, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_SIGN_UP_CALLBACK);
            }
        }, AppConfig.LOADING_TIME);
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        BaseEntity baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_ACTIVITY_DETAIL:
                    baseEn = JsonUtils.getThemeDetail(jsonObject);
                    if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
                        setView((ThemeEntity) baseEn.getData());
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_SIGN_UP_ADD:
                    baseEn = JsonUtils.getPayOrderOn(jsonObject);
                    if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
                        orderNo = (String) baseEn.getData();
                        if (!StringUtil.isNull(orderNo)) {
                            startPay();
                        } else {
                            //无需支付处理
                        }
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_SIGN_UP_CALLBACK:
                    baseEn = JsonUtils.getBaseErrorData(jsonObject);
                    if (baseEn != null) {
                        if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
                            showSuccessDialog(getString(R.string.sign_up_success), true);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 2000);
                        } else {
                            showSuccessDialog(getString(R.string.sign_up_fail), false);
                        }
                    } else {
                        showSuccessDialog(getString(R.string.sign_up_error), false);
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
            if (requestCode == AppConfig.ACTIVITY_CODE_PAY_DATA) {
                boolean isPayOk = data.getBooleanExtra(AppConfig.ACTIVITY_KEY_PAY_RESULT, false);
                if (isPayOk) {
                    isSignUp = true;
                    userManager.saveThemeId(themeId);
                    setClickState(getString(R.string.sign_up_already), false);

                    checkSignUpState();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
