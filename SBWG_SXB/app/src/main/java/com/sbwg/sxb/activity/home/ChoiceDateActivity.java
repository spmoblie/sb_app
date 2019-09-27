package com.sbwg.sxb.activity.home;


import android.os.Bundle;
import android.view.View;

import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.R;
import com.sbwg.sxb.activity.BaseActivity;
import com.sbwg.sxb.entity.DayFinish;
import com.sbwg.sxb.entity.ThemeEntity;
import com.sbwg.sxb.utils.CommonTools;
import com.sbwg.sxb.utils.LogUtil;
import com.sbwg.sxb.widgets.CustomCalendar;

import java.util.Date;

import butterknife.BindView;

public class ChoiceDateActivity extends BaseActivity implements View.OnClickListener {

    String TAG = ChoiceDateActivity.class.getSimpleName();

    @BindView(R.id.choice_date_cal)
    CustomCalendar calendar;

    private ThemeEntity data;
    private int themeId; //课程Id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_date);

        data = (ThemeEntity) getIntent().getExtras().getSerializable("data");
        if (data != null) {
            themeId = data.getId();
        }

        initView();
    }

    private void initView() {
        setTitle(getString(R.string.choice_date));

        calendar.setOnClickListener(new CustomCalendar.onClickListener() {

            @Override
            public void onLeftRowClick() {
                CommonTools.showToast("点击减箭头");
                calendar.monthChange(-1);
            }

            @Override
            public void onRightRowClick() {
                CommonTools.showToast("点击加箭头");
                calendar.monthChange(1);
            }

            @Override
            public void onTitleClick(String monthStr, Date month) {
                CommonTools.showToast("点击了标题：" + monthStr);
            }

            @Override
            public void onWeekClick(int weekIndex, String weekStr) {
                CommonTools.showToast("点击了星期：" + weekStr);
            }

            @Override
            public void onDayClick(int day, String dayStr, DayFinish finish) {
                CommonTools.showToast("点击了日期：" + dayStr);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choice_date_cal:

                break;
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

}
