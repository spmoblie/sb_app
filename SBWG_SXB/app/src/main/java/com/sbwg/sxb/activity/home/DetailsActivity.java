package com.sbwg.sxb.activity.home;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.R;
import com.sbwg.sxb.activity.BaseActivity;
import com.sbwg.sxb.entity.ThemeEntity;
import com.sbwg.sxb.entity.UserInfoEntity;
import com.sbwg.sxb.utils.LogUtil;
import com.sbwg.sxb.utils.StringUtil;

import butterknife.BindView;

public class DetailsActivity extends BaseActivity implements View.OnClickListener {

    String TAG = DetailsActivity.class.getSimpleName();

    @BindView(R.id.details_iv_show)
    ImageView iv_show;

    @BindView(R.id.details_tv_information)
    TextView tv_information;

    @BindView(R.id.details_tv_explain)
    TextView tv_explain;

    @BindView(R.id.details_tv_sign_up)
    TextView tv_sign_up;

    LinearLayout.LayoutParams showImgLP;

    private ThemeEntity data;
    private int pageType = 0; //2:查看我的活动
    private int courseId; //课程Id
    private int status; //1:报名中, 2:已截止
    private boolean isSignUp = false;
    private boolean isOnClick = true;
    private String imgUrl, informationStr, explainStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        pageType = getIntent().getIntExtra("type", 0);
        data = (ThemeEntity) getIntent().getExtras().getSerializable("data");
        if (data != null) {
            courseId = data.getId();
            status = data.getStatus();
            imgUrl = data.getPicUrl();
            explainStr = data.getSynopsis();
        }

        initView();
    }

    private void initView() {
        setTitle(getString(R.string.sign_up_title));

        tv_sign_up.setOnClickListener(this);

        showImgLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        showImgLP.height = screenWidth / 2;
        iv_show.setLayoutParams(showImgLP);
        Glide.with(AppApplication.getAppContext())
                .load(imgUrl)
                .apply(AppApplication.getShowOptions())
                .into(iv_show);

        if (StringUtil.isNull(explainStr)) {
            explainStr = getString(R.string.sign_up_cost_hint);
        }
        tv_explain.setText(explainStr);

        if (data != null) {
            informationStr = "时间：" + data.getStartTime() + " 至 " + data.getEndTime() +
                    "\n" + "地点：" + data.getAddress() +
                    "\n" + "人数：" + "已报名" + data.getPeople() + "人/限" + data.getQuantity() + "人报名";
        } else {
            informationStr = "时间：\n地点：\n人数：";
        }
        tv_information.setText(informationStr);


        if (pageType == 2) { //查看我的活动
            setTitle(getString(R.string.mine_text_activity));
            UserInfoEntity userData = data.getUserData();
            if (userData != null) {

            }
        }
    }

    private void setSignState(String text, boolean isState) {
        if (!StringUtil.isNull(text)) {
            tv_sign_up.setText(text);
        }
        isOnClick = isState;
        changeViewState(tv_sign_up, isOnClick);
    }

    // 跳转至报名页面
    private void openSignUpActivity(ThemeEntity data) {
        if (data == null) return;
        Intent intent = new Intent(mContext, SignUpActivity.class);
        intent.putExtra("data", data);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.details_tv_sign_up:
                if (!isOnClick) return; //已报名
                openSignUpActivity(data);
                break;
        }
    }

    @Override
    protected void onResume() {
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
        // 页面开始
        AppApplication.onPageStart(this, TAG);

        // 报名状态
        isSignUp = userManager.isCourseSignUp(courseId);
        if (isLogin()) {
            if (isSignUp) { //已报名
                setSignState(getString(R.string.sign_up_already), false);
            } else {
                if (status == 2) { //已截止
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

}
