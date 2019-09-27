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
import com.sbwg.sxb.R;
import com.sbwg.sxb.activity.BaseActivity;
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

    @BindView(R.id.reserve_tv_data)
    TextView tv_data;

    @BindView(R.id.reserve_tv_time)
    TextView tv_time;

    @BindView(R.id.reserve_tv_explain)
    TextView tv_explain;

    @BindView(R.id.reserve_tv_cost)
    TextView tv_cost;

    @BindView(R.id.reserve_tv_reserve)
    TextView tv_reserve;

    LinearLayout.LayoutParams showImgLP;

    private ThemeEntity data;
    private int themeId; //课程Id
    private int status; //1:报名中, 2:已截止
    private boolean isPayOk = false;
    private boolean isDateOk = false;
    private boolean isTimeOk = false;
    private boolean isSignUp = false;
    private boolean isOnClick = true;
    private String imgUrl, titleStr, authorStr, infoStr, explainStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);

        data = (ThemeEntity) getIntent().getExtras().getSerializable("data");
        if (data != null) {
            themeId = data.getId();
            status = data.getStatus();
            imgUrl = data.getPicUrl();
            titleStr = data.getTitle();
            explainStr = data.getSynopsis();
        }

        initView();
    }

    private void initView() {
        setTitle(getString(R.string.sign_up_title));

        tv_data.setOnClickListener(this);
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
        tv_author.setText("此处是老师的信息");
        tv_cost.setText("￥199.00");

        String time = getString(R.string.reserve_time);
        String place = getString(R.string.reserve_place);
        String suit = getString(R.string.reserve_suit);
        String empty = getString(R.string.reserve_empty);
        if (data != null) {
            infoStr = time + "09:00-10:30" + "  " + "11:00-12:30" +
                    "\n" + empty + "13:00-14:30" + "  " + "16:00-17:30" +
                    "\n" + place + data.getAddress() +
                    "\n" + suit + data.getSuit();
        } else {
            infoStr = time + "\n" + place + "\n" + suit;
        }
        tv_info.setText(infoStr);

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

    private void setSignState(String text, boolean isState) {
        if (!StringUtil.isNull(text)) {
            tv_reserve.setText(text);
        }
        isOnClick = isState;
        changeViewState(tv_reserve, isOnClick);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reserve_tv_data:
            case R.id.reserve_tv_time:
                openChoiceDateActivity(data);
                break;
            case R.id.reserve_tv_reserve:
                if (!isDateOk || !isTimeOk) {
                    openChoiceDateActivity(data);
                } else {

                }
                break;
        }
    }

    // 跳转至选择时间页面
    private void openChoiceDateActivity(ThemeEntity data) {
        if (data == null) return;
        Intent intent = new Intent(mContext, ChoiceDateActivity.class);
        intent.putExtra("data", data);
        startActivity(intent);
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
