package com.sbwg.sxb.activity.home;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppManager;
import com.sbwg.sxb.R;
import com.sbwg.sxb.activity.BaseActivity;
import com.sbwg.sxb.utils.CommonTools;
import com.sbwg.sxb.utils.LogUtil;
import com.sbwg.sxb.utils.StringUtil;

import butterknife.BindView;

public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SignUpActivity";

    @BindView(R.id.sign_iv_show)
    ImageView sign_iv_show;

    @BindView(R.id.sign_tv_name)
    TextView sign_tv_name;

    @BindView(R.id.sign_et_name)
    EditText sign_et_name;

    @BindView(R.id.sign_tv_gender)
    TextView sign_tv_gender;

    @BindView(R.id.sign_tv_gender_woman)
    TextView sign_tv_gender_woman;

    @BindView(R.id.sign_tv_gender_man)
    TextView sign_tv_gender_man;

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

    private double payAmount = 0.00;
    private int genderCode = 0; //0:女，1:男
    private String imgUrl, nameStr, ageStr, phoneStr, explainStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        AppManager.getInstance().addActivity(this);//添加Activity到堆栈
        LogUtil.i(TAG, "onCreate");

        mContext = this;
        Bundle bundle = getIntent().getExtras();
        imgUrl = bundle.getString("imgUrl", "");
        payAmount = bundle.getDouble("payAmount", 0.00);
        explainStr = bundle.getString("explainStr", "");

        initView();
    }

    private void initView() {
        setTitle("我要报名");

        sign_tv_gender_woman.setSelected(true);
        sign_tv_gender_woman.setOnClickListener(this);
        sign_tv_gender_man.setOnClickListener(this);

        sign_et_age.setFocusable(false);
        sign_et_age.setFocusableInTouchMode(false);
        sign_et_age.setOnClickListener(this);

        sign_tv_sign_up.setOnClickListener(this);
        sign_tv_cost_pay.setOnClickListener(this);

        showImgLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        showImgLP.height = screenWidth / 2;
        sign_iv_show.setLayoutParams(showImgLP);
        Glide.with(AppApplication.getAppContext())
                .load(IMAGE_URL_HTTP + imgUrl)
                .apply(AppApplication.getShowOpeions())
                .into(sign_iv_show);

        sign_tv_cost.setText(getString(R.string.sign_up_cost_show, String.valueOf(payAmount)));
        if (StringUtil.isNull(explainStr)) {
            explainStr = getString(R.string.sign_up_cost_hint);
        }
        sign_tv_explain.setText(explainStr);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_tv_gender_man:
                selectGender(1);
                break;
            case R.id.sign_tv_gender_woman:
                selectGender(0);
                break;
            case R.id.sign_et_age:
                selectAge();
                break;
            case R.id.sign_tv_sign_up:
                signUp();
                break;
            case R.id.sign_tv_cost_pay:
                payCostAmount();
                break;
        }
    }

    /**
     * 选择性别
     */
    private void selectGender(int code) {
        switch (code) {
            case 0:
                sign_tv_gender_man.setSelected(false);
                sign_tv_gender_woman.setSelected(true);
                break;
            case 1:
                sign_tv_gender_man.setSelected(true);
                sign_tv_gender_woman.setSelected(false);
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
                }
            }

        });
    }

    /**
     * 报名
     */
    private void signUp() {
        // 姓名非空
        nameStr = sign_et_name.getText().toString();
        if (nameStr.isEmpty()) {
            CommonTools.showToast(getString(R.string.sign_up_input_name), 1000);
            return;
        }
        // 年龄非空
        ageStr = sign_et_age.getText().toString();
        if (ageStr.isEmpty()) {
            CommonTools.showToast(getString(R.string.sign_up_input_age), 1000);
            return;
        }
        // 电话非空
        phoneStr = sign_et_phone.getText().toString();
        if (phoneStr.isEmpty()) {
            CommonTools.showToast(getString(R.string.sign_up_input_phone), 1000);
            return;
        }
        // 验证手机号码
        if (!StringUtil.isMobileNO(phoneStr)) {
            CommonTools.showToast(getString(R.string.sign_up_input_phone_error), 1000);
            return;
        }
        postSignUpData();
    }

    /**
     * 提交报名数据
     */
    private void postSignUpData() {
        startAnimation();
    }

    /**
     * 支付报名费
     */
    private void payCostAmount() {
        CommonTools.showToast("功能开发中。。。");
    }

    @Override
    protected void onResume() {
        LogUtil.i(TAG, "onResume");
        // 页面开始
        AppApplication.onPageStart(this, TAG);

        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.i(TAG, "onPause");
        // 页面结束
        AppApplication.onPageEnd(this, TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestroy");
    }

}
