package com.sbwg.sxb.activity.home;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppConfig;
import com.sbwg.sxb.R;
import com.sbwg.sxb.activity.BaseActivity;
import com.sbwg.sxb.adapter.AdapterCallback;
import com.sbwg.sxb.adapter.ChoiceListAdapter;
import com.sbwg.sxb.entity.DayFinish;
import com.sbwg.sxb.entity.OptionEntity;
import com.sbwg.sxb.entity.ThemeEntity;
import com.sbwg.sxb.utils.CommonTools;
import com.sbwg.sxb.utils.LogUtil;
import com.sbwg.sxb.utils.StringUtil;
import com.sbwg.sxb.utils.TimeUtil;
import com.sbwg.sxb.widgets.CustomCalendar;
import com.sbwg.sxb.widgets.ScrollViewListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

public class ChoiceDateActivity extends BaseActivity implements View.OnClickListener {

    String TAG = ChoiceDateActivity.class.getSimpleName();

    @BindView(R.id.choice_date_cal)
    CustomCalendar calendar;

    @BindView(R.id.choice_date_listView)
    ScrollViewListView listView;

    @BindView(R.id.choice_date_tv_confirm)
    TextView tv_confirm;

    private AdapterCallback apCallback;
    private ChoiceListAdapter lv_Adapter;
    private ThemeEntity data;
    private OptionEntity optionEn;
    private int themeId; //课程Id
    private boolean isChange = false;
    private String selectDay, assignDay, selectTime, assignTime;
    private ArrayList<OptionEntity> al_show = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_date);

        data = (ThemeEntity) getIntent().getExtras().getSerializable(AppConfig.PAGE_DATA);
        if (data != null) {
            themeId = data.getId();
            optionEn = data.getOption();
        }

        initView();
    }

    private void initView() {
        setTitle(getString(R.string.choice_date));

        tv_confirm.setOnClickListener(this);

        if (optionEn != null && !StringUtil.isNull(optionEn.getDate())) {
            assignDay = optionEn.getDate();
            assignTime = optionEn.getTime();
            if (assignDay.contains("-")) {
                String[] dates = assignDay.split("-");
                if (dates.length > 2) {
                    String date = dates[0] + "年" + dates[1] + "月";
                    int day = Integer.valueOf(dates[2]);
                    calendar.setSelectDate(date, day); //指定日期
                }
            }
            selectDay = assignDay;
        } else {
            selectDay = TimeUtil.getStringDateShort();
        }

        calendar.setOnClickListener(new CustomCalendar.onClickListener() {

            @Override
            public void onLeftRowClick() {
                calendar.monthChange(-1);
                clearData();
            }

            @Override
            public void onRightRowClick() {
                calendar.monthChange(1);
                clearData();
            }

            @Override
            public void onTitleClick(String monthStr, Date month) {
                LogUtil.i(LogUtil.LOG_TAG, TAG + " 点击了标题：" + monthStr);
            }

            @Override
            public void onWeekClick(int weekIndex, String weekStr) {
                LogUtil.i(LogUtil.LOG_TAG, TAG + " 点击了星期：" + weekStr);
            }

            @Override
            public void onDayClick(int day, String dayStr, DayFinish finish) {
                LogUtil.i(LogUtil.LOG_TAG, TAG + " 点击了日期：" + dayStr);
                clearData();
                selectDay = dayStr;
                getTimeData();
            }
        });

        getTimeData();
    }

    private void initListView() {
        reviseData();
        apCallback = new AdapterCallback() {
            @Override
            public void setOnClick(Object data, int position, int type) {
                updateItemState(position);
            }
        };
        lv_Adapter = new ChoiceListAdapter(mContext);
        lv_Adapter.setDataList(al_show);
        lv_Adapter.setCallback(apCallback);

        listView.setAdapter(lv_Adapter);
        listView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

        updateViewSate();
    }

    /**
     * 勾选已预约场次
     */
    private void reviseData() {
        if (!StringUtil.isNull(assignDay)
                && !StringUtil.isNull(assignTime)
                && assignDay.equals(selectDay)) {
            for (int i = 0; i < al_show.size(); i++) {
                String time = al_show.get(i).getTime();
                if (assignTime.equals(time)) {
                    al_show.get(i).setReserve(true); //已预约
                    al_show.get(i).setSelect(true);  //勾选中
                    al_show.get(i).setState(false);  //不可选
                }
            }
        }
    }

    /**
     * 刷新Item勾选状态
     * @param position
     */
    private void updateItemState(int position) {
        for (int i = 0; i < al_show.size(); i++) {
            OptionEntity optionEn = al_show.get(i);
            if (optionEn != null && optionEn.isState()) {
                if (i == position) {
                    boolean isSelect = !optionEn.isSelect();
                    if (isSelect) {
                        selectTime = optionEn.getTime();
                    } else {
                        selectTime = "";
                    }
                    al_show.get(i).setSelect(isSelect);
                } else {
                    al_show.get(i).setSelect(false);
                }
            }
        }
        initListView();
    }

    /**
     * 刷新“确定”状态
     */
    private void updateViewSate() {
        if (StringUtil.isNull(selectTime)) {
            tv_confirm.setBackgroundColor(getResources().getColor(R.color.debar_text_color));
        } else {
            tv_confirm.setBackgroundColor(getResources().getColor(R.color.ui_color_button));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choice_date_tv_confirm:
                checkData();
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

    @Override
    public void finish() {
        if (isChange) {
            OptionEntity optionEn = new OptionEntity();
            optionEn.setDate(selectDay);
            optionEn.setTime(selectTime);

            Intent returnIntent = new Intent();
            returnIntent.putExtra(AppConfig.ACTIVITY_KEY_CHOICE_DATE, optionEn);
            setResult(RESULT_OK, returnIntent);
        }
        super.finish();
    }

    private void clearData() {
        selectDay = "";
        selectTime = "";
        al_show.clear();
        initListView();
    }

    private void getTimeData() {
        al_show.addAll(initTimeData());
        initListView();
    }

    private void checkData() {
        if (StringUtil.isNull(selectDay)) {
            CommonTools.showToast(getString(R.string.choice_select_date));
            return;
        }
        if (StringUtil.isNull(selectTime)) {
            CommonTools.showToast(getString(R.string.choice_select_time));
            return;
        }
        isChange = true;
        finish();
    }

    private List<OptionEntity> initTimeData() {
        List<OptionEntity> lists = new ArrayList<>();

        OptionEntity option_1 = new OptionEntity();
        option_1.setTime("09:00-10:30");
        option_1.setState(true);
        lists.add(option_1);

        OptionEntity option_2 = new OptionEntity();
        option_2.setTime("11:00-12:30");
        option_2.setState(true);
        lists.add(option_2);

        OptionEntity option_3 = new OptionEntity();
        option_3.setTime("13:00-14:30");
        option_3.setState(true);
        lists.add(option_3);

        OptionEntity option_4 = new OptionEntity();
        option_4.setTime("16:00-17:30");
        option_4.setState(false);
        lists.add(option_4);

        return lists;
    }

}
