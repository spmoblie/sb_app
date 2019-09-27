package com.sbwg.sxb.activity.home;


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
import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppConfig;
import com.sbwg.sxb.R;
import com.sbwg.sxb.activity.BaseActivity;
import com.sbwg.sxb.entity.BaseEntity;
import com.sbwg.sxb.entity.ThemeEntity;
import com.sbwg.sxb.entity.UserInfoEntity;
import com.sbwg.sxb.utils.CommonTools;
import com.sbwg.sxb.utils.ExceptionUtil;
import com.sbwg.sxb.utils.JsonUtils;
import com.sbwg.sxb.utils.LogUtil;
import com.sbwg.sxb.utils.StringUtil;
import com.sbwg.sxb.utils.retrofit.HttpRequests;

import org.json.JSONObject;

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

    @BindView(R.id.sign_tv_cost)
    TextView tv_cost;

    @BindView(R.id.sign_tv_cost_pay)
    TextView tv_cost_pay;

    @BindView(R.id.sign_tv_explain)
    TextView tv_explain;

    @BindView(R.id.sign_tv_sign_up)
    TextView tv_sign_up;

    LinearLayout.LayoutParams showImgLP;

    private ThemeEntity data;
    private double payAmount = 0.00;
    private int pageType = 0; //2:查看我的活动
    private int themeId; //课程Id
    private int status; //1:报名中, 2:已截止
    private int genderCode = 1; //1:男, 2:女
    private boolean isSignUp = false;
    private boolean isPay = false;
    private boolean isPay_Ok = false;
    private boolean isName_Ok = false;
    private boolean isAge_Ok = false;
    private boolean isPhone_Ok = false;
    private boolean isPostData = false;
    private String imgUrl, nameStr, ageStr, phoneStr, infoStr, explainStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        pageType = getIntent().getIntExtra("type", 0);
        data = (ThemeEntity) getIntent().getExtras().getSerializable("data");
        if (data != null) {
            themeId = data.getId();
            status = data.getStatus();
            imgUrl = data.getPicUrl();
            payAmount = data.getFees();
            explainStr = data.getDescription();
        }

        initView();
    }

    private void initView() {
        setTitle(getString(R.string.sign_up_title));

        selectGender(genderCode);
        tv_gender_man.setOnClickListener(this);
        tv_gender_woman.setOnClickListener(this);

        et_age.setFocusable(false);
        et_age.setFocusableInTouchMode(false);
        et_age.setOnClickListener(this);

        tv_sign_up.setOnClickListener(this);
        tv_cost_pay.setOnClickListener(this);

        showImgLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        showImgLP.height = screenWidth / 2;
        iv_show.setLayoutParams(showImgLP);
        Glide.with(AppApplication.getAppContext())
                .load(imgUrl)
                .apply(AppApplication.getShowOptions())
                .into(iv_show);

        if (payAmount > 0) {
            isPay = true;
        }
        setPayState(isPay);
        tv_cost.setText(getString(R.string.sign_up_cost_show, String.valueOf(payAmount)));

        String explain = getString(R.string.other);
        String suit = getString(R.string.suit);
        String time = getString(R.string.time);
        String place = getString(R.string.place);
        String number = getString(R.string.number_p);
        if (data != null) {
            infoStr = time + getString(R.string.sign_up_info_time, data.getStartTime(), data.getEndTime()) +
                    "\n" + place + data.getAddress() +
                    "\n" + number + getString(R.string.sign_up_info_number, data.getPeople(), data.getQuantity()) +
                    "\n" + suit + data.getSuit();
        } else {
            infoStr = time + "\n" + place + "\n" + number + "\n" + suit;
        }
        if (StringUtil.isNull(explainStr)) {
            explainStr = getString(R.string.sign_up_cost_hint);
        }
        tv_explain.setText(getString(R.string.sign_up_show) + "\n" +
                           "\n" + infoStr + "\n" +
                           "\n" + explain + explainStr);

        initEditText();

        if (pageType == 2) { //查看我的活动
            setTitle(getString(R.string.mine_text_activity));
            UserInfoEntity userData = data.getUserData();
            if (userData != null) {
                et_name.setFocusable(false);
                et_name.setFocusableInTouchMode(false);
                et_name.setText(userData.getUserName());
                selectGender(userData.getGenderCode());
                et_age.setText(userData.getBirthday());
                et_phone.setFocusable(false);
                et_phone.setFocusableInTouchMode(false);
                et_phone.setText(userData.getUserPhone());
            }
        }
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
                checkState();
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
                        CommonTools.showToast(getString(R.string.login_phone_input));
                        return;
                    }
                    isPhone_Ok = true;
                }
                checkState();
            }
        });
    }

    private void checkState() {
        if (isSignUp) return;
        setSignState(false);
        if (isName_Ok && isAge_Ok && isPhone_Ok) {
            if (isPay) {
                if (isPay_Ok) {
                    setSignState(true);
                }
            } else {
                setSignState(true);
            }
        }
    }

    private void setSignState(boolean isState) {
        setSignState("", isState);
    }

    private void setSignState(String text, boolean isState) {
        if (!StringUtil.isNull(text)) {
            tv_sign_up.setText(text);
        }
        isPostData = isState;
        changeViewState(tv_sign_up, isPostData);
    }

    private void setPayState(boolean isState) {
        setPayState("", isState);
    }

    private void setPayState(String text, boolean isState) {
        if (!StringUtil.isNull(text)) {
            tv_cost_pay.setText(text);
        }
        changeViewState(tv_cost_pay, isState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_tv_gender_man:
                if (pageType == 2) return;
                selectGender(1);
                break;
            case R.id.sign_tv_gender_woman:
                if (pageType == 2) return;
                selectGender(2);
                break;
            case R.id.sign_et_age:
                if (pageType == 2) return;
                selectAge();
                break;
            case R.id.sign_tv_cost_pay:
                if (!isLogin()) { //未登录
                    openLoginActivity();
                    return;
                }
                if (isSignUp) return; //已报名
                if (isPay) {
                    if (isPay_Ok) {
                        CommonTools.showToast(getString(R.string.pay_hint));
                    } else {
                        if (status == 1) { //报名中
                            payCostAmount();
                        }
                    }
                }
                break;
            case R.id.sign_tv_sign_up:
                if (!isLogin()) { //未登录
                    openLoginActivity();
                    return;
                }
                if (isSignUp) return; //已报名
                if (status == 1 && checkData() && isPostData) {
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
     * 报名
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
            CommonTools.showToast(getString(R.string.login_phone_input));
            return false;
        }
        // 校验支付
        if (isPay && !isPay_Ok) {
            CommonTools.showToast(getString(R.string.sign_up_cost_pay, String.valueOf(payAmount)));
            return false;
        }
        return true;
    }

    /**
     * 支付报名费
     */
    private void payCostAmount() {
        isPay_Ok = true;
        CommonTools.showToast(getString(R.string.pay_success));
        setPayState(getString(R.string.pay_ok), false);

        checkState();
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
                if (isPay) {
                    setPayState(getString(R.string.pay_ok), false);
                }
                setSignState(getString(R.string.sign_up_already), false);
            } else {
                if (status == 2) { //已截止
                    setPayState(false);
                    setSignState(getString(R.string.sign_up_end), false);
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
                map.put("paymentType", "0");
                loadSVData(AppConfig.URL_SIGN_UP_ADD, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_POST_SIGN_DATA);
            }
        }, AppConfig.LOADING_TIME);
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        BaseEntity baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_POST_SIGN_DATA:
                    baseEn = JsonUtils.getBaseErrorData(jsonObject);
                    if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
                        isSignUp = true;
                        setSignState(getString(R.string.sign_up_already), false);
                        userManager.saveThemeId(themeId);
                        AppApplication.updateMineData(true);
                        CommonTools.showToast(getString(R.string.sign_up_success));
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

}
