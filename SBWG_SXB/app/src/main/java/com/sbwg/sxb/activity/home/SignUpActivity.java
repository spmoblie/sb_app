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
import com.sbwg.sxb.utils.CommonTools;
import com.sbwg.sxb.utils.ExceptionUtil;
import com.sbwg.sxb.utils.JsonUtils;
import com.sbwg.sxb.utils.LogUtil;
import com.sbwg.sxb.utils.StringUtil;
import com.sbwg.sxb.utils.UserManager;
import com.sbwg.sxb.utils.retrofit.HttpRequests;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;

public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = SignUpActivity.class.getSimpleName();

    @BindView(R.id.sign_iv_show)
    ImageView sign_iv_show;

    @BindView(R.id.sign_tv_name)
    TextView sign_tv_name;

    @BindView(R.id.sign_et_name)
    EditText sign_et_name;

    @BindView(R.id.sign_tv_gender)
    TextView sign_tv_gender;

    @BindView(R.id.sign_tv_gender_man)
    TextView sign_tv_gender_man;

    @BindView(R.id.sign_tv_gender_woman)
    TextView sign_tv_gender_woman;

    @BindView(R.id.sign_tv_age)
    TextView sign_tv_age;

    @BindView(R.id.sign_et_age)
    EditText sign_et_age;

    @BindView(R.id.sign_tv_phone)
    TextView sign_tv_phone;

    @BindView(R.id.sign_et_phone)
    EditText sign_et_phone;

    @BindView(R.id.sign_tv_cost)
    TextView sign_tv_cost;

    @BindView(R.id.sign_tv_cost_pay)
    TextView sign_tv_cost_pay;

    @BindView(R.id.sign_tv_explain)
    TextView sign_tv_explain;

    @BindView(R.id.sign_tv_sign_up)
    TextView sign_tv_sign_up;

    private LinearLayout.LayoutParams showImgLP;

    private ThemeEntity data;
    private double payAmount = 0.00;
    private int courseId; //课程Id
    private int genderCode = 1; //1:男，2:女
    private boolean isSignUp = false;
    private boolean isPay = false;
    private boolean isPay_Ok = false;
    private boolean isName_Ok = false;
    private boolean isAge_Ok = false;
    private boolean isPhone_Ok = false;
    private boolean isPostData = false;
    private String imgUrl, nameStr, ageStr, phoneStr, explainStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        data = (ThemeEntity) getIntent().getExtras().getSerializable("data");
        if (data != null) {
            courseId = data.getId();
            imgUrl = data.getPicUrl();
            payAmount = data.getFees();
            explainStr = data.getDescription();
        }

        initView();
    }

    private void initView() {
        setTitle("我要报名");

        selectGender(genderCode);
        sign_tv_gender_man.setOnClickListener(this);
        sign_tv_gender_woman.setOnClickListener(this);

        sign_et_age.setFocusable(false);
        sign_et_age.setFocusableInTouchMode(false);
        sign_et_age.setOnClickListener(this);

        sign_tv_sign_up.setOnClickListener(this);
        sign_tv_cost_pay.setOnClickListener(this);

        showImgLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        showImgLP.height = screenWidth / 2;
        sign_iv_show.setLayoutParams(showImgLP);
        Glide.with(AppApplication.getAppContext())
                .load(imgUrl)
                .apply(AppApplication.getShowOpeions())
                .into(sign_iv_show);

        if (payAmount > 0) {
            isPay = true;
        }
        setPayState(isPay);
        sign_tv_cost.setText(getString(R.string.sign_up_cost_show, String.valueOf(payAmount)));

        if (StringUtil.isNull(explainStr)) {
            explainStr = getString(R.string.sign_up_cost_hint);
        }
        sign_tv_explain.setText(explainStr);

        initEditText();
    }

    private void initEditText() {
        sign_et_name.addTextChangedListener(new TextWatcher() {

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
        sign_et_phone.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    int length = s.toString().length();
                    if (length == 3 || length == 8) {
                        sign_et_phone.setText(s + " ");
                        sign_et_phone.setSelection(sign_et_phone.getText().length());
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
            sign_tv_sign_up.setText(text);
        }
        isPostData = isState;
        changeViewState(sign_tv_sign_up, isPostData);
    }

    private void setPayState(boolean isState) {
        setPayState("", isState);
    }

    private void setPayState(String text, boolean isState) {
        if (!StringUtil.isNull(text)) {
            sign_tv_cost_pay.setText(text);
        }
        changeViewState(sign_tv_cost_pay, isState);
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
            case R.id.sign_tv_cost_pay:
                if (!isLogin()) {
                    openLoginActivity(TAG);
                    return;
                }
                if (isSignUp) return;
                if (isPay) {
                    if (isPay_Ok) {
                        CommonTools.showToast("您已完成支付，请勿重复支付。");
                    } else {
                        payCostAmount();
                    }
                }
                break;
            case R.id.sign_tv_sign_up:
                if (!isLogin()) {
                    openLoginActivity(TAG);
                    return;
                }
                if (isSignUp) return;
                if (checkData() && isPostData) {
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
                sign_tv_gender_man.setSelected(true);
                sign_tv_gender_woman.setSelected(false);
                break;
            case 2:
                sign_tv_gender_man.setSelected(false);
                sign_tv_gender_woman.setSelected(true);
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
                if (sign_et_age != null) {
                    sign_et_age.setText(ageStr);
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
        nameStr = sign_et_name.getText().toString();
        if (nameStr.isEmpty()) {
            CommonTools.showToast(getString(R.string.sign_up_input_name));
            return false;
        }
        // 年龄非空
        ageStr = sign_et_age.getText().toString();
        if (ageStr.isEmpty()) {
            CommonTools.showToast(getString(R.string.sign_up_input_age));
            return false;
        }
        // 电话非空
        phoneStr = sign_et_phone.getText().toString();
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
        CommonTools.showToast("模拟支付成功");
        setPayState("已支付", false);

        checkState();
    }

    @Override
    protected void onResume() {
        LogUtil.i(TAG, "onResume");
        // 页面开始
        AppApplication.onPageStart(this, TAG);

        // 报名状态
        isSignUp = UserManager.getInstance().isCourseSignUp(courseId);
        if (isLogin() && isSignUp) {
            setPayState(false);
            setSignState("已报名", false);
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        LogUtil.i(TAG, "onPause");
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
        if (data == null) return;
        startAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> map = new HashMap<>();
                map.put("activityId", String.valueOf(courseId));
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
                        setSignState("已报名", false);
                        UserManager.getInstance().saveCourseId(courseId);
                        CommonTools.showToast("报名成功");
                    } else {
                        showServerBusy(baseEn.getErrmsg());
                    }
                    break;
            }
        } catch (Exception e) {
            loadFailHandle();
            ExceptionUtil.handle(e);
        }
    }

}
