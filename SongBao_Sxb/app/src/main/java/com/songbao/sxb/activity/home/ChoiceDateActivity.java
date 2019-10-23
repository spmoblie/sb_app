package com.songbao.sxb.activity.home;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.songbao.sxb.AppApplication;
import com.songbao.sxb.AppConfig;
import com.songbao.sxb.R;
import com.songbao.sxb.activity.BaseActivity;
import com.songbao.sxb.adapter.AdapterCallback;
import com.songbao.sxb.adapter.ChoiceListAdapter;
import com.songbao.sxb.entity.BaseEntity;
import com.songbao.sxb.entity.DayFinish;
import com.songbao.sxb.entity.OptionEntity;
import com.songbao.sxb.entity.ThemeEntity;
import com.songbao.sxb.utils.CommonTools;
import com.songbao.sxb.utils.ExceptionUtil;
import com.songbao.sxb.utils.JsonUtils;
import com.songbao.sxb.utils.LogUtil;
import com.songbao.sxb.utils.StringUtil;
import com.songbao.sxb.utils.retrofit.HttpRequests;
import com.songbao.sxb.widgets.CustomCalendar;
import com.songbao.sxb.widgets.ScrollViewListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
    private int themeId; //课程Id
    private boolean isChange = false;
    private boolean loadDateOk = false;
    private String selectDay, assignDay, assignTime, selectTime, selectTimeId;
    private ArrayList<String> al_date = new ArrayList<>();
    private ArrayList<OptionEntity> al_show = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_date);

        data = (ThemeEntity) getIntent().getExtras().getSerializable(AppConfig.PAGE_DATA);
        if (data != null) {
            themeId = data.getId();
        }

        initView();
    }

    private void initView() {
        setTitle(getString(R.string.choice_date));

        tv_confirm.setOnClickListener(this);

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
                if (!StringUtil.isNull(selectDay) && selectDay.equals(dayStr)) return;
                clearData();
                selectDay = dayStr;
                getTimeData();
            }
        });

        updateViewSate();
    }

    private void setCalendarView() {
        if (al_date.size() > 0) {
            // 设置可选日期
            calendar.setActiveDateList(al_date);

            int now_mon = Calendar.getInstance().get(Calendar.MONTH) + 1;
            int now_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            int ass_mon = 0;
            int ass_day = 0;
            int i = 0;
            String date = "";
            do {
                if (i < al_date.size()) {
                    assignDay = al_date.get(i);
                    if (assignDay.contains("-")) {
                        String[] dates = assignDay.split("-");
                        if (dates.length > 2) {
                            date = dates[0] + "年" + dates[1] + "月";
                            ass_mon = Integer.valueOf(dates[1]);
                            ass_day = Integer.valueOf(dates[2]);
                        }
                    }
                } else {
                    assignDay = "";
                    date = "";
                    ass_mon = 0;
                    ass_day = 0;
                }
                i++;
            } while (i <= al_date.size() && (ass_mon < now_mon || (ass_mon == now_mon && ass_day < now_day)));

            calendar.setSelectDate(date, ass_day); //指定日期
        }
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
        if (!StringUtil.isNull(assignDay) && !StringUtil.isNull(assignTime) && assignDay.equals(selectDay)) {
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
                        selectTimeId = optionEn.getEntityId();
                    } else {
                        selectTime = "";
                        selectTimeId = "";
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
            tv_confirm.setBackgroundResource(R.drawable.shape_style_solid_6_34);
        } else {
            tv_confirm.setBackgroundResource(R.drawable.shape_style_solid_1_34);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choice_date_tv_confirm:
                if (!checkClickState()) return;
                checkData();
                break;
        }
    }

    /**
     * 校验点击事件
     */
    private boolean checkClickState() {
        if (!loadDateOk) {
            dataErrorHandle();
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
        // 页面开始
        AppApplication.onPageStart(this, TAG);

        if (!loadDateOk) {
            loadDateData();
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

    @Override
    public void finish() {
        // 清空旧数据
        if (calendar != null) {
            calendar.setActiveDateList(null);
        }
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

    /**
     * 数据报错处理
     */
    private void dataErrorHandle() {
        //showDataError();
        loadDateData();
    }

    private void clearData() {
        selectDay = "";
        selectTime = "";
        al_show.clear();
        initListView();
    }

    private void checkData() {
        if (StringUtil.isNull(selectDay)) {
            CommonTools.showToast(getString(R.string.choice_select_date));
            return;
        }
        if (StringUtil.isNull(selectTime)) {
            if (al_show.size() > 0) {
                CommonTools.showToast(getString(R.string.choice_select_time));
            } else {
                CommonTools.showToast(getString(R.string.choice_select_time_empty));
            }
            return;
        }
        postCheckData();
    }

    private void getTimeData() {
        loadTimeData();
        /*al_show.clear();
        al_show.addAll(getDemoData());
        initListView();*/
    }

    /**
     * 加载日期数据
     */
    private void loadDateData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("activityId", String.valueOf(themeId));
        loadSVData(AppConfig.URL_RESERVATION_DATE, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_RESERVATION_DATE);
    }

    /**
     * 加载时段数据
     */
    private void loadTimeData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("activityId", String.valueOf(themeId));
        map.put("raStartTime", selectDay + " 00:00:00");
        loadSVData(AppConfig.URL_RESERVATION_TIME, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_RESERVATION_TIME);
    }

    /**
     * 提交校验时段数据
     */
    private void postCheckData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("activityId", String.valueOf(themeId));
        map.put("reservationActivityId", selectTimeId);
        loadSVData(AppConfig.URL_RESERVATION_IS, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_RESERVATION_IS);
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        BaseEntity baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_RESERVATION_DATE:
                    baseEn = JsonUtils.getDateList(jsonObject);
                    if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
                        List<String> lists = baseEn.getLists();
                        if (lists.size() > 0) {
                            al_date.clear();
                            al_date.addAll(lists);
                            setCalendarView();
                            loadDateOk = true;
                        }
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_RESERVATION_TIME:
                    baseEn = JsonUtils.getTimeSlot(jsonObject);
                    if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
                        List<OptionEntity> lists = baseEn.getLists();
                        if (lists.size() > 0) {
                            al_show.clear();
                            al_show.addAll(lists);
                            initListView();
                        }
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_RESERVATION_IS:
                    baseEn = JsonUtils.getBaseErrorData(jsonObject);
                    if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
                        isChange = true;
                        finish();
                    } else
                    if (baseEn.getErrno() == AppConfig.ERROR_CODE_FULL) { //该时段已约满
                        getTimeData(); //刷新预约状态
                        CommonTools.showToast(baseEn.getErrmsg());
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

    private List<OptionEntity> getDemoData() {
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
