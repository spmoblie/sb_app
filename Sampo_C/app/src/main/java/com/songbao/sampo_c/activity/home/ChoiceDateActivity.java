package com.songbao.sampo_c.activity.home;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.activity.BaseActivity;
import com.songbao.sampo_c.adapter.AdapterCallback;
import com.songbao.sampo_c.adapter.ChoiceListAdapter;
import com.songbao.sampo_c.entity.BaseEntity;
import com.songbao.sampo_c.entity.DayFinish;
import com.songbao.sampo_c.entity.OptionEntity;
import com.songbao.sampo_c.entity.ThemeEntity;
import com.songbao.sampo_c.utils.CommonTools;
import com.songbao.sampo_c.utils.ExceptionUtil;
import com.songbao.sampo_c.utils.JsonUtils;
import com.songbao.sampo_c.utils.LogUtil;
import com.songbao.sampo_c.utils.StringUtil;
import com.songbao.sampo_c.utils.retrofit.HttpRequests;
import com.songbao.sampo_c.widgets.CustomCalendar;
import com.songbao.sampo_c.widgets.MyScrollView;
import com.songbao.sampo_c.widgets.ScrollViewListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

public class ChoiceDateActivity extends BaseActivity implements View.OnClickListener {

    String TAG = ChoiceDateActivity.class.getSimpleName();

    @BindView(R.id.choice_date_sv)
    MyScrollView myScrollView;

    @BindView(R.id.choice_date_cal)
    CustomCalendar calendar;

    @BindView(R.id.choice_date_listView)
    ScrollViewListView listView;

    @BindView(R.id.choice_date_tv_confirm)
    TextView tv_confirm;

    private ThemeEntity data;
    private OptionEntity selectEn;
    private ChoiceListAdapter lv_Adapter;
    private boolean isChange = false;
    private boolean loadDateOk = false;
    private String themeId, selectDay, assignDay, assignTime, selectTime, selectTimeId;
    private ArrayList<String> al_date = new ArrayList<>();
    private ArrayList<OptionEntity> al_show = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_date);

        assignDay = getIntent().getStringExtra("assignDay");
        assignTime = getIntent().getStringExtra("assignTime");
        data = (ThemeEntity) getIntent().getSerializableExtra(AppConfig.PAGE_DATA);
        if (data != null) {
            themeId = data.getThemeId();
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
                initListView();
            }

            @Override
            public void onRightRowClick() {
                calendar.monthChange(1);
                clearData();
                initListView();
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
                    String dateStr = al_date.get(i);
                    if (!StringUtil.isNull(assignDay)) {
                        dateStr = assignDay;
                        i = al_date.size();
                    }
                    if (dateStr.contains("-")) {
                        String[] dates = dateStr.split("-");
                        if (dates.length > 2) {
                            date = dates[0] + "年" + dates[1] + "月";
                            ass_mon = Integer.valueOf(dates[1]);
                            ass_day = Integer.valueOf(dates[2]);
                        }
                    }
                } else {
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
        if (lv_Adapter == null) {
            lv_Adapter = new ChoiceListAdapter(mContext);
            lv_Adapter.addCallback(new AdapterCallback() {
                @Override
                public void setOnClick(Object data, int position, int type) {
                    updateItemState(position);
                }
            });
        }
        lv_Adapter.updateData(al_show);
        listView.setAdapter(lv_Adapter);
        listView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
        updateViewSate();
    }

    /**
     * 勾选已选择场次
     */
    private void reviseData() {
        if (!StringUtil.isNull(assignTime)) {
            for (int i = 0; i < al_show.size(); i++) {
                String timeStr = al_show.get(i).getTime();
                if (assignTime.equals(timeStr) && al_show.get(i).isState()) {
                    updateItemState(i); //选中
                    return;
                }
            }
        }
        initListView();
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
                        selectEn = optionEn;
                        selectTime = optionEn.getTime();
                        selectTimeId = optionEn.getEntityId();
                    } else {
                        selectEn = null;
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
            tv_confirm.setBackgroundResource(R.drawable.shape_style_solid_03_08);
        } else {
            tv_confirm.setBackgroundResource(R.drawable.shape_style_solid_04_08);
        }
    }

    /**
     * 清除缓存数据
     */
    private void clearData() {
        selectEn = null;
        selectDay = "";
        selectTime = "";
        selectTimeId = "";
        al_show.clear();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choice_date_tv_confirm:
                if (!checkOnClick()) return;
                checkData();
                break;
        }
    }

    /**
     * 校验点击事件
     */
    private boolean checkOnClick() {
        if (!loadDateOk) {
            dataErrorHandle();
            return false;
        }
        return true;
    }

    /**
     * 校验数据
     */
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
        if (isChange && selectEn != null) {
            selectEn.setDate(selectDay);

            Intent returnIntent = new Intent();
            returnIntent.putExtra(AppConfig.PAGE_DATA, selectEn);
            setResult(RESULT_OK, returnIntent);
        }
        super.finish();
    }

    /**
     * 数据报错处理
     */
    private void dataErrorHandle() {
        loadDateData();
    }

    private void getTimeData() {
        loadTimeData();
    }

    /**
     * 加载日期数据
     */
    private void loadDateData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("activityId", themeId);
        loadSVData(AppConfig.URL_RESERVATION_DATE, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_RESERVATION_DATE);
    }

    /**
     * 加载时段数据
     */
    private void loadTimeData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("activityId", themeId);
        map.put("raStartTime", selectDay + " 00:00:00");
        loadSVData(AppConfig.URL_RESERVATION_TIME, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_RESERVATION_TIME);
    }

    /**
     * 提交校验时段数据
     */
    private void postCheckData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("activityId", themeId);
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
                            if (!StringUtil.isNull(assignDay) && assignDay.equals(selectDay)) {
                                reviseData(); //选中日期==指定日期
                            } else {
                                initListView();
                            }
                        }
                    } else {
                        initListView();
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
                        String dayStr = selectDay;
                        clearData();
                        selectDay = dayStr;
                        getTimeData(); //刷新预约状态
                        CommonTools.showToast(baseEn.getErrmsg());
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
            }
        } catch (Exception e) {
            if (dataType == AppConfig.REQUEST_SV_RESERVATION_TIME) {
                initListView();
            }
            loadFailHandle();
            ExceptionUtil.handle(e);
        }
    }

}
