package com.songbao.sampo_b.activity.mine;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.RadioButton;

import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.activity.BaseActivity;
import com.songbao.sampo_b.adapter.AdapterCallback;
import com.songbao.sampo_b.adapter.CustomizeAdapter;
import com.songbao.sampo_b.entity.BaseEntity;
import com.songbao.sampo_b.entity.OCustomizeEntity;
import com.songbao.sampo_b.utils.ExceptionUtil;
import com.songbao.sampo_b.utils.JsonUtils;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.utils.retrofit.HttpRequests;
import com.songbao.sampo_b.widgets.pullrefresh.PullToRefreshBase;
import com.songbao.sampo_b.widgets.pullrefresh.PullToRefreshRecyclerView;
import com.songbao.sampo_b.widgets.recycler.MyRecyclerView;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;


public class CustomizeListActivity extends BaseActivity implements View.OnClickListener {

    String TAG = CustomizeListActivity.class.getSimpleName();

    @BindView(R.id.top_bar_radio_rb_1)
    RadioButton rb_1;

    @BindView(R.id.top_bar_radio_rb_2)
    RadioButton rb_2;

    @BindView(R.id.top_bar_radio_rb_3)
    RadioButton rb_3;

    @BindView(R.id.top_bar_radio_rb_4)
    RadioButton rb_4;

    @BindView(R.id.top_bar_radio_rb_5)
    RadioButton rb_5;

    @BindView(R.id.refresh_view_rv)
    PullToRefreshRecyclerView refresh_rv;

    MyRecyclerView mRecyclerView;
    CustomizeAdapter rvAdapter;

    public static final int TYPE_1 = 1;  //全部
    public static final int TYPE_2 = 2;  //待审核
    public static final int TYPE_3 = 3;  //生产中
    public static final int TYPE_4 = 4;  //已发货
    public static final int TYPE_5 = 5;  //已完成

    private boolean isLoadOk = true; //加载控制
    private int data_total = -1; //数据总量
    private int load_type = 1; //加载类型(0:下拉刷新/1:翻页加载)
    private int load_page = 1; //加载页数
    private int load_page_1 = 1;
    private int load_page_2 = 1;
    private int load_page_3 = 1;
    private int load_page_4 = 1;
    private int load_page_5 = 1;
    private int top_type = TYPE_1; //Top标记
    private int total_1, total_2, total_3, total_4, total_5;
    private int selectPosition = 0; //选择的订单索引
    private String selectOrderNo = ""; //选择的订单编号

    private ArrayList<OCustomizeEntity> al_show = new ArrayList<>();
    private ArrayList<OCustomizeEntity> al_all_1 = new ArrayList<>();
    private ArrayList<OCustomizeEntity> al_all_2 = new ArrayList<>();
    private ArrayList<OCustomizeEntity> al_all_3 = new ArrayList<>();
    private ArrayList<OCustomizeEntity> al_all_4 = new ArrayList<>();
    private ArrayList<OCustomizeEntity> al_all_5 = new ArrayList<>();
    private ArrayMap<String, Boolean> am_all_1 = new ArrayMap<>();
    private ArrayMap<String, Boolean> am_all_2 = new ArrayMap<>();
    private ArrayMap<String, Boolean> am_all_3 = new ArrayMap<>();
    private ArrayMap<String, Boolean> am_all_4 = new ArrayMap<>();
    private ArrayMap<String, Boolean> am_all_5 = new ArrayMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_top);

        initView();
    }

    private void initView() {
        setTitle(getString(R.string.mine_my_customize));

        initRadioGroup();
        initRecyclerView();
        setDefaultRadioButton();
        loadFirstPageData();
    }

    private void initRadioGroup() {
        rb_1.setText(getString(R.string.order_all));
        rb_2.setText(getString(R.string.order_wait_check));
        rb_3.setText(getString(R.string.order_producing));
        rb_4.setText(getString(R.string.order_wait_receive));
        rb_5.setText(getString(R.string.order_completed));
        rb_1.setOnClickListener(this);
        rb_2.setOnClickListener(this);
        rb_3.setOnClickListener(this);
        rb_4.setOnClickListener(this);
        rb_5.setOnClickListener(this);
    }

    private void initRecyclerView() {
        refresh_rv.setHeaderLayoutBackground(R.color.ui_color_app_bg_02);
        refresh_rv.setFooterLayoutBackground(R.color.ui_color_app_bg_02);
        refresh_rv.setPullRefreshEnabled(true); //下拉刷新
        refresh_rv.setPullLoadEnabled(true); //上拉加载
        refresh_rv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<MyRecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<MyRecyclerView> refreshView) {
                // 下拉刷新
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        refreshData();
                    }
                }, AppConfig.LOADING_TIME);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<MyRecyclerView> refreshView) {
                // 加载更多
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (!isStopLoadMore(al_show.size(), data_total, 0)) {
                            loadMoreData();
                        } else {
                            refresh_rv.onPullUpRefreshComplete();
                            refresh_rv.setHasMoreData(false);
                        }
                    }
                }, AppConfig.LOADING_TIME);
            }
        });

        // 创建布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        // 设置布局管理器
        mRecyclerView = refresh_rv.getRefreshableView();
        mRecyclerView.setLayoutManager(layoutManager);

        // 配置适配器
        rvAdapter = new CustomizeAdapter(mContext, R.layout.item_list_customize);
        rvAdapter.addData(al_show);
        rvAdapter.addCallback(new AdapterCallback() {

            @Override
            public void setOnClick(Object data, int position, int type) {
                if (position < 0 || position >= al_show.size()) return;
                OCustomizeEntity ocEn = al_show.get(position);
                selectPosition = position;
                selectOrderNo = ocEn.getOrderNo();
                int status = ocEn.getStatus();
                switch (type) {
                    case 1: //按键
                        switch (status) {
                            case AppConfig.ORDER_STATUS_101: //待审核
                            case AppConfig.ORDER_STATUS_104: //已拒绝
                            case AppConfig.ORDER_STATUS_201: //待初核
                            case AppConfig.ORDER_STATUS_202: //待复核
                                // 取消订单
                                showConfirmDialog(getString(R.string.order_cancel_confirm), new MyHandler(CustomizeListActivity.this), 101);
                                break;
                            case AppConfig.ORDER_STATUS_401: //已发货
                                // 确认收货
                                showConfirmDialog(getString(R.string.order_confirm_receive_hint), new MyHandler(CustomizeListActivity.this), 7);
                                break;
                            case AppConfig.ORDER_STATUS_801: //已完成
                            case AppConfig.ORDER_STATUS_102: //已取消
                            default:
                                // 删除订单
                                showConfirmDialog(getString(R.string.order_delete_confirm), new MyHandler(CustomizeListActivity.this), 102);
                                break;
                        }
                        break;
                    default:
                        openCustomizeActivity(ocEn);
                        break;
                }
            }
        });
        mRecyclerView.setAdapter(rvAdapter);
    }

    /**
     * 打开定制订单详情
     */
    private void openCustomizeActivity(OCustomizeEntity ocEn) {
        Intent intent = new Intent(mContext, CustomizeOrderActivity.class);
        intent.putExtra(AppConfig.PAGE_DATA, ocEn);
        startActivityForResult(intent, AppConfig.ACTIVITY_CODE_ORDER_UPDATE);
    }

    /**
     * 设置默认项
     */
    private void setDefaultRadioButton() {
        RadioButton defaultBtn;
        switch (top_type) {
            default:
            case TYPE_1:
                defaultBtn = rb_1;
                break;
            case TYPE_2:
                defaultBtn = rb_2;
                break;
            case TYPE_3:
                defaultBtn = rb_3;
                break;
            case TYPE_4:
                defaultBtn = rb_4;
                break;
            case TYPE_5:
                defaultBtn = rb_5;
                break;
        }
        changeItemStatus();
        defaultBtn.setChecked(true);
    }

    /**
     * 自定义Top Item状态切换
     */
    private void changeItemStatus() {
        rb_1.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
        rb_2.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
        rb_3.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
        rb_4.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
        rb_5.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
        switch (top_type) {
            default:
            case TYPE_1:
                rb_1.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                break;
            case TYPE_2:
                rb_2.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                break;
            case TYPE_3:
                rb_3.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                break;
            case TYPE_4:
                rb_4.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                break;
            case TYPE_5:
                rb_5.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (!isLoadOk) { //加载频率控制
            setDefaultRadioButton();
            return;
        }
        switch (v.getId()) {
            case R.id.top_bar_radio_rb_1:
                if (top_type == TYPE_1) return;
                top_type = TYPE_1;
                addOldListData(al_all_1, load_page_1, total_1);
                if (al_all_1.size() <= 0) {
                    load_page_1 = 1;
                    total_1 = 0;
                    loadFirstPageData();
                }
                changeItemStatus();
                break;
            case R.id.top_bar_radio_rb_2:
                if (top_type == TYPE_2) return;
                top_type = TYPE_2;
                addOldListData(al_all_2, load_page_2, total_2);
                if (al_all_2.size() <= 0) {
                    load_page_2 = 1;
                    total_2 = 0;
                    loadFirstPageData();
                }
                changeItemStatus();
                break;
            case R.id.top_bar_radio_rb_3:
                if (top_type == TYPE_3) return;
                top_type = TYPE_3;
                addOldListData(al_all_3, load_page_3, total_3);
                if (al_all_3.size() <= 0) {
                    load_page_3 = 1;
                    total_3 = 0;
                    loadFirstPageData();
                }
                changeItemStatus();
                break;
            case R.id.top_bar_radio_rb_4:
                if (top_type == TYPE_4) return;
                top_type = TYPE_4;
                addOldListData(al_all_4, load_page_4, total_4);
                if (al_all_4.size() <= 0) {
                    load_page_4 = 1;
                    total_4 = 0;
                    loadFirstPageData();
                }
                changeItemStatus();
                break;
            case R.id.top_bar_radio_rb_5:
                if (top_type == TYPE_5) return;
                top_type = TYPE_5;
                addOldListData(al_all_5, load_page_5, total_5);
                if (al_all_5.size() <= 0) {
                    load_page_5 = 1;
                    total_5 = 0;
                    loadFirstPageData();
                }
                changeItemStatus();
                break;
        }
    }

    /**
     * 展示已缓存的数据并至顶
     */
    private void addOldListData(List<OCustomizeEntity> oldLists, int oldPage, int oldTotal) {
        refreshAllShow(oldLists, oldTotal);
        load_page = oldPage;
        updateListData();
        if (load_page != 1) {
            toTop();
        }
        setLoadMoreState();
    }

    /**
     * 更新列表数据
     */
    private void updateListData() {
        if (load_page == 1) {
            toTop();
        }
        if (al_show.size() <= 0) {
            setNullVisibility(View.VISIBLE);
        } else {
            setNullVisibility(View.GONE);
        }
        rvAdapter.updateData(al_show);
    }

    /**
     * 刷新需要展示的数据
     */
    private void refreshAllShow(List<OCustomizeEntity> showLists, int total) {
        al_show.clear();
        al_show.addAll(showLists);
        data_total = total;
    }

    /**
     * 删除订单时刷新
     */
    private void deleteOrderUpdate() {
        if (selectPosition >= 0 && selectPosition < al_show.size()) {
            al_show.remove(selectPosition);
            switch (top_type) {
                case TYPE_1:
                    al_all_1.remove(selectPosition);
                    break;
                case TYPE_2:
                    al_all_2.remove(selectPosition);
                    break;
                case TYPE_3:
                    al_all_3.remove(selectPosition);
                    break;
                case TYPE_4:
                    al_all_4.remove(selectPosition);
                    break;
                case TYPE_5:
                    al_all_5.remove(selectPosition);
                    break;
            }
            updateListData();
        }
    }

    /**
     * 数据状态更新
     */
    private void dataStatusUpdate(int statusCode) {
        if (selectPosition >= 0 && selectPosition < al_show.size()) {
            al_show.clear();
            switch (top_type) {
                case TYPE_1: //全部
                    al_all_1.get(selectPosition).setStatus(statusCode);
                    al_show.addAll(al_all_1);
                    break;
                case TYPE_2: //待审核 (触发事件：取消订单)
                    al_all_1.clear();
                    am_all_1.clear();
                    al_all_3.clear();
                    am_all_3.clear();
                    al_all_2.remove(selectPosition);
                    al_show.addAll(al_all_2);
                    break;
                case TYPE_3: //生产中 (触发事件：确认收货)
                    al_all_1.clear();
                    am_all_1.clear();
                    al_all_5.clear();
                    am_all_5.clear();
                    al_all_3.remove(selectPosition);
                    al_show.addAll(al_all_3);
                    break;
                case TYPE_4: //待收货 (触发事件：确认收货)
                    al_all_1.clear();
                    am_all_1.clear();
                    al_all_5.clear();
                    am_all_5.clear();
                    al_all_4.remove(selectPosition);
                    al_show.addAll(al_all_4);
                    break;
                case TYPE_5: //已完成 (触发事件：删除订单)
                    al_all_1.clear();
                    am_all_1.clear();
                    al_all_5.remove(selectPosition);
                    al_show.addAll(al_all_5);
                    break;
            }
            updateListData();
        }
    }

    /**
     * 滚动到顶部
     */
    private void toTop() {
        mRecyclerView.smoothScrollToPosition(0);
    }

    /**
     * 设置允许加载更多
     */
    private void setLoadMoreState() {
        refresh_rv.setHasMoreData(true);
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

    /**
     * 加载第一页数据
     */
    private void loadFirstPageData() {
        refresh_rv.doPullRefreshing(true, 0);
    }

    /**
     * 下拉刷新
     */
    private void refreshData() {
        load_type = 0;
        loadServerData();
    }

    /**
     * 翻页加载
     */
    private void loadMoreData() {
        load_type = 1;
        switch (top_type) {
            default:
            case TYPE_1:
                load_page = load_page_1;
                break;
            case TYPE_2:
                load_page = load_page_2;
                break;
            case TYPE_3:
                load_page = load_page_3;
                break;
            case TYPE_4:
                load_page = load_page_4;
                break;
            case TYPE_5:
                load_page = load_page_5;
                break;
        }
        loadServerData();
    }

    /**
     * 加载数据
     */
    private void loadServerData() {
        if (!isLoadOk) return; //加载频率控制
        isLoadOk = false;
        String page = String.valueOf(load_page);
        if (load_type == 0) {
            page = "1";
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("roleId", userManager.getUserRoleIds());
        map.put("customStatus", top_type);
        map.put("current", page);
        map.put("size", AppConfig.LOAD_SIZE);
        loadSVData(AppConfig.URL_ORDER_LIST, map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_ORDER_LIST);
    }

    /**
     * 确认收货
     */
    private void postConfirmReceive() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("customCode", selectOrderNo);
        loadSVData(AppConfig.URL_ORDER_RECEIVE, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_ORDER_RECEIVE);
    }

    /**
     * 取消订单
     */
    private void postConfirmCancel() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("customCode", selectOrderNo);
        loadSVData(AppConfig.URL_ORDER_CANCEL, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_ORDER_CANCEL);
    }

    /**
     * 删除订单
     */
    private void postConfirmDelete() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("customCode", selectOrderNo);
        loadSVData(AppConfig.URL_ORDER_DELETE, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_ORDER_DELETE);
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        BaseEntity<OCustomizeEntity> baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_ORDER_LIST:
                    baseEn = JsonUtils.getCustomizeListData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        int newTotal = baseEn.getDataTotal();
                        List<OCustomizeEntity> lists = new ArrayList<>();
                        switch (top_type) {
                            case TYPE_1:
                                lists = filterData(baseEn.getLists(), am_all_1);
                                if (lists.size() > 0) {
                                    if (load_type == 0) {
                                        lists.addAll(al_all_1);
                                        al_all_1.clear();
                                        if (load_page_1 <= 1) {
                                            load_page_1 = 2;
                                        }
                                    } else {
                                        load_page_1++;
                                    }
                                    total_1 = newTotal;
                                    al_all_1.addAll(lists);
                                    refreshAllShow(al_all_1, total_1);
                                }
                                break;
                            case TYPE_2:
                                lists = filterData(baseEn.getLists(), am_all_2);
                                if (lists.size() > 0) {
                                    if (load_type == 0) {
                                        lists.addAll(al_all_2);
                                        al_all_2.clear();
                                        if (load_page_2 <= 1) {
                                            load_page_2 = 2;
                                        }
                                    } else {
                                        load_page_2++;
                                    }
                                    total_2 = newTotal;
                                    al_all_2.addAll(lists);
                                    refreshAllShow(al_all_2, total_2);
                                }
                                break;
                            case TYPE_3:
                                lists = filterData(baseEn.getLists(), am_all_3);
                                if (lists.size() > 0) {
                                    if (load_type == 0) {
                                        lists.addAll(al_all_3);
                                        al_all_3.clear();
                                        if (load_page_3 <= 1) {
                                            load_page_3 = 2;
                                        }
                                    } else {
                                        load_page_3++;
                                    }
                                    total_3 = newTotal;
                                    al_all_3.addAll(lists);
                                    refreshAllShow(al_all_3, total_3);
                                }
                                break;
                            case TYPE_4:
                                lists = filterData(baseEn.getLists(), am_all_4);
                                if (lists.size() > 0) {
                                    if (load_type == 0) {
                                        lists.addAll(al_all_4);
                                        al_all_4.clear();
                                        if (load_page_4 <= 1) {
                                            load_page_4 = 2;
                                        }
                                    } else {
                                        load_page_4++;
                                    }
                                    total_4 = newTotal;
                                    al_all_4.addAll(lists);
                                    refreshAllShow(al_all_4, total_4);
                                }
                                break;
                            case TYPE_5:
                                lists = filterData(baseEn.getLists(), am_all_5);
                                if (lists.size() > 0) {
                                    if (load_type == 0) {
                                        lists.addAll(al_all_5);
                                        al_all_5.clear();
                                        if (load_page_5 <= 1) {
                                            load_page_5 = 2;
                                        }
                                    } else {
                                        load_page_5++;
                                    }
                                    total_5 = newTotal;
                                    al_all_5.addAll(lists);
                                    refreshAllShow(al_all_5, total_5);
                                }
                                break;
                        }
                        if (load_type == 0) {
                            setLoadMoreState();
                            LogUtil.i(LogUtil.LOG_HTTP, TAG + " 刷新数据 —> size = " + lists.size());
                        } else {
                            LogUtil.i(LogUtil.LOG_HTTP, TAG + " 翻页数据 —> size = " + lists.size());
                        }
                        updateListData();
                    } else if (baseEn.getErrNo() == AppConfig.ERROR_CODE_TIMEOUT) {
                        handleTimeOut();
                        finish();
                    } else {
                        updateListData();
                        handleErrorCode(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_ORDER_RECEIVE:
                    baseEn = JsonUtils.getBaseErrorData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        dataStatusUpdate(AppConfig.ORDER_STATUS_801);
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_ORDER_CANCEL:
                    baseEn = JsonUtils.getBaseErrorData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        dataStatusUpdate(AppConfig.ORDER_STATUS_102);
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_ORDER_DELETE:
                    baseEn = JsonUtils.getBaseErrorData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        deleteOrderUpdate();
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

    @Override
    protected void loadFailHandle() {
        super.loadFailHandle();

        switch (top_type) {
            case TYPE_1:
                refreshAllShow(al_all_1, total_1);
                break;
            case TYPE_2:
                refreshAllShow(al_all_2, total_2);
                break;
            case TYPE_3:
                refreshAllShow(al_all_3, total_3);
                break;
            case TYPE_4:
                refreshAllShow(al_all_4, total_4);
                break;
            case TYPE_5:
                refreshAllShow(al_all_5, total_5);
                break;
        }
        updateListData();
    }

    @Override
    protected void stopAnimation() {
        super.stopAnimation();
        isLoadOk = true;
        refresh_rv.onPullUpRefreshComplete();
        refresh_rv.onPullDownRefreshComplete();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == AppConfig.ACTIVITY_CODE_ORDER_UPDATE) {
                int updateCode = data.getIntExtra(AppConfig.PAGE_DATA, 0);
                switch (updateCode) {
                    case -1: //删除订单
                        deleteOrderUpdate();
                        break;
                    case AppConfig.ORDER_STATUS_102: //取消订单—>已取消
                    case AppConfig.ORDER_STATUS_801: //确认收货—>已完成
                        dataStatusUpdate(updateCode);
                        break;
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    static class MyHandler extends Handler {

        WeakReference<CustomizeListActivity> mActivity;

        MyHandler(CustomizeListActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            CustomizeListActivity theActivity = mActivity.get();
            if (theActivity != null) {
                switch (msg.what) {
                    case 7: //确认收货
                        theActivity.postConfirmReceive();
                        break;
                    case 101: //取消订单
                        theActivity.postConfirmCancel();
                        break;
                    case 102: //删除订单
                        theActivity.postConfirmDelete();
                        break;
                }
            }
        }
    }
}
