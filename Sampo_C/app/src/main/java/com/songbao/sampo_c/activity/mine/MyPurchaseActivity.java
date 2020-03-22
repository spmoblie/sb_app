package com.songbao.sampo_c.activity.mine;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.RadioButton;

import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.activity.BaseActivity;
import com.songbao.sampo_c.adapter.AdapterCallback;
import com.songbao.sampo_c.adapter.MyPurchaseAdapter;
import com.songbao.sampo_c.entity.BaseEntity;
import com.songbao.sampo_c.entity.OPurchaseEntity;
import com.songbao.sampo_c.utils.ExceptionUtil;
import com.songbao.sampo_c.utils.JsonUtils;
import com.songbao.sampo_c.utils.LogUtil;
import com.songbao.sampo_c.utils.StringUtil;
import com.songbao.sampo_c.utils.retrofit.HttpRequests;
import com.songbao.sampo_c.widgets.pullrefresh.PullToRefreshBase;
import com.songbao.sampo_c.widgets.pullrefresh.PullToRefreshRecyclerView;
import com.songbao.sampo_c.widgets.recycler.MyRecyclerView;
import com.songbao.sampo_c.wxapi.WXPayEntryActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;


public class MyPurchaseActivity extends BaseActivity implements View.OnClickListener {

    String TAG = MyPurchaseActivity.class.getSimpleName();

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
    MyPurchaseAdapter rvAdapter;

    public static final int TYPE_1 = 0;  //全部
    public static final int TYPE_2 = 1;  //待付款
    public static final int TYPE_3 = 2;  //待收货
    public static final int TYPE_4 = 3;  //待评价
    public static final int TYPE_5 = 4;  //退换货

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
    private boolean isLoadOk = true; //加载控制
    private boolean isToTop = false; //是否回顶
    private int selectPosition = 0; //选择的订单索引
    private String selectOrderNo = ""; //选择的订单编号

    private ArrayList<OPurchaseEntity> al_show = new ArrayList<>();
    private ArrayList<OPurchaseEntity> al_all_1 = new ArrayList<>();
    private ArrayList<OPurchaseEntity> al_all_2 = new ArrayList<>();
    private ArrayList<OPurchaseEntity> al_all_3 = new ArrayList<>();
    private ArrayList<OPurchaseEntity> al_all_4 = new ArrayList<>();
    private ArrayList<OPurchaseEntity> al_all_5 = new ArrayList<>();
    private ArrayMap<String, Boolean> am_all_1 = new ArrayMap<>();
    private ArrayMap<String, Boolean> am_all_2 = new ArrayMap<>();
    private ArrayMap<String, Boolean> am_all_3 = new ArrayMap<>();
    private ArrayMap<String, Boolean> am_all_4 = new ArrayMap<>();
    private ArrayMap<String, Boolean> am_all_5 = new ArrayMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_top);

        top_type = getIntent().getIntExtra("top_type", TYPE_1);

        initView();
    }

    private void initView() {
        setTitle(getString(R.string.mine_my_purchase));

        initRadioGroup();
        initRecyclerView();
        setDefaultRadioButton();
        loadFirstPageData();
    }

    private void initRadioGroup() {
        rb_1.setText(getString(R.string.order_all));
        rb_2.setText(getString(R.string.order_wait_pay));
        rb_3.setText(getString(R.string.order_wait_receive));
        rb_4.setText(getString(R.string.order_wait_opinion));
        rb_5.setText(getString(R.string.order_repair_return));
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
        rvAdapter = new MyPurchaseAdapter(mContext, R.layout.item_list_my_purchase);
        rvAdapter.addData(al_show);
        rvAdapter.addCallback(new AdapterCallback() {

            @Override
            public void setOnClick(Object data, int position, int type) {
                if (position < 0 || position >= al_show.size()) return;
                OPurchaseEntity opEn = al_show.get(position);
                selectPosition = position;
                selectOrderNo = opEn.getOrderNo();
                int status = opEn.getStatus();
                switch (type) {
                    case 1: //按键01
                        switch (status) {
                            case AppConfig.ORDER_STATUS_101: //待付款
                                // 取消订单
                                showConfirmDialog(getString(R.string.order_cancel_confirm), new MyHandler(MyPurchaseActivity.this), 101);
                                break;
                            case AppConfig.ORDER_STATUS_401: //待收货
                                // 查看物流
                                showErrorDialog(R.string.order_logistics_null);
                                break;
                        }
                        break;
                    case 2: //按键02
                        switch (status) {
                            case AppConfig.ORDER_STATUS_101: //待付款
                                // 立即付款
                                startPay(opEn.getOrderNo(), opEn.getTotalPrice());
                                break;
                            case AppConfig.ORDER_STATUS_401: //待收货
                                // 确认收货
                                showConfirmDialog(getString(R.string.order_confirm_receipt_hint), new MyHandler(MyPurchaseActivity.this), 103);
                                break;
                            case AppConfig.ORDER_STATUS_501: //待评价
                                // 我要评价
                                openPurchaseActivity(opEn);
                                break;
                            case AppConfig.ORDER_STATUS_303: //已退款
                            case AppConfig.ORDER_STATUS_801: //已完成
                            case AppConfig.ORDER_STATUS_102: //已取消
                            default:
                                // 删除订单
                                showConfirmDialog(getString(R.string.order_delete_confirm), new MyHandler(MyPurchaseActivity.this), 102);
                                break;
                        }
                        break;
                    default:
                        openPurchaseActivity(opEn);
                        break;
                }
            }
        });
        mRecyclerView.setAdapter(rvAdapter);
    }

    /**
     * 打开购买订单详情页
     */
    private void openPurchaseActivity(OPurchaseEntity opEn) {
        if (opEn != null) {
            Intent intent = new Intent(mContext, PurchaseActivity.class);
            intent.putExtra(AppConfig.PAGE_DATA, opEn);
            startActivityForResult(intent, AppConfig.ACTIVITY_CODE_ORDER_UPDATE);
        }
    }

    /**
     * 设置默认项
     */
    private void setDefaultRadioButton() {
        RadioButton defaultBtn;
        switch (top_type) {
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
            default:
                defaultBtn = rb_1;
                break;
        }
        defaultBtn.setChecked(true);
        changeItemStatus();
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
                changeItemStatus();
                addOldListData(al_all_1, load_page_1, total_1);
                if (al_all_1.size() <= 0) {
                    load_page_1 = 1;
                    total_1 = 0;
                    loadFirstPageData();
                }
                break;
            case R.id.top_bar_radio_rb_2:
                if (top_type == TYPE_2) return;
                top_type = TYPE_2;
                changeItemStatus();
                addOldListData(al_all_2, load_page_2, total_2);
                if (al_all_2.size() <= 0) {
                    load_page_2 = 1;
                    total_2 = 0;
                    loadFirstPageData();
                }
                break;
            case R.id.top_bar_radio_rb_3:
                if (top_type == TYPE_3) return;
                top_type = TYPE_3;
                changeItemStatus();
                addOldListData(al_all_3, load_page_3, total_3);
                if (al_all_3.size() <= 0) {
                    load_page_3 = 1;
                    total_3 = 0;
                    loadFirstPageData();
                }
                break;
            case R.id.top_bar_radio_rb_4:
                if (top_type == TYPE_4) return;
                top_type = TYPE_4;
                changeItemStatus();
                addOldListData(al_all_4, load_page_4, total_4);
                if (al_all_4.size() <= 0) {
                    load_page_4 = 1;
                    total_4 = 0;
                    loadFirstPageData();
                }
                break;
            case R.id.top_bar_radio_rb_5:
                if (top_type == TYPE_5) return;
                top_type = TYPE_5;
                changeItemStatus();
                addOldListData(al_all_5, load_page_5, total_5);
                if (al_all_5.size() <= 0) {
                    load_page_5 = 1;
                    total_5 = 0;
                    loadFirstPageData();
                }
                break;
        }
    }

    /**
     * 展示已缓存的数据并至顶
     */
    private void addOldListData(List<OPurchaseEntity> oldLists, int oldPage, int oldTotal) {
        refreshAllShow(oldLists, oldTotal);
        load_page = oldPage;
        updateListData();
        setLoadMoreState();
    }

    /**
     * 更新列表数据
     */
    private void updateListData() {
        toTop();
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
    private void refreshAllShow(List<OPurchaseEntity> showLists, int total) {
        al_show.clear();
        al_show.addAll(showLists);
        data_total = total;
    }

    /**
     * 取消订单刷新
     */
    private void cancelOrderUpdate() {
        if (top_type == TYPE_1) {
            al_show.get(selectPosition).setStatus(AppConfig.ORDER_STATUS_102);
            al_all_1.get(selectPosition).setStatus(AppConfig.ORDER_STATUS_102);
        }
        updateLimitData();
    }

    /**
     * 支付订单刷新
     */
    private void payOrderUpdate() {
        if (top_type == TYPE_1) {
            al_show.get(selectPosition).setStatus(AppConfig.ORDER_STATUS_301);
            al_all_1.get(selectPosition).setStatus(AppConfig.ORDER_STATUS_301);
        }
        updateLimitData();
    }

    /**
     * 删除订单刷新
     */
    private void deleteOrderUpdate() {
        if (top_type == TYPE_1) {
            al_show.remove(selectPosition);
            al_all_1.remove(selectPosition);
        }
        updateLimitData();
    }

    /**
     * 确认订单刷新
     */
    private void confirmOrderUpdate() {
        if (top_type == TYPE_1) {
            al_show.get(selectPosition).setStatus(AppConfig.ORDER_STATUS_501);
            al_all_1.get(selectPosition).setStatus(AppConfig.ORDER_STATUS_501);
        }
        updateLimitData();
    }

    /**
     * 限制刷新数据
     */
    private void updateLimitData() {
        al_all_2.clear();
        al_all_3.clear();
        al_all_4.clear();
        al_all_5.clear();
        am_all_2.clear();
        am_all_3.clear();
        am_all_4.clear();
        am_all_5.clear();
        load_page_2 = 1;
        load_page_3 = 1;
        load_page_4 = 1;
        load_page_5 = 1;

        if (top_type != TYPE_1) {
            al_show.clear();
            al_all_1.clear();
            am_all_1.clear();
            load_page_1 = 1;
            loadFirstPageData();
        } else { //全部
            updateListData();
        }
    }

    /**
     * 滚动到顶部
     */
    private void toTop() {
        if (isToTop) {
            isToTop = false;
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    /**
     * 设置允许加载更多
     */
    private void setLoadMoreState() {
        refresh_rv.setHasMoreData(true);
    }

    /**
     * 自定义Top Item状态切换
     */
    private void changeItemStatus() {
        isToTop = true;
        rb_1.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
        rb_2.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
        rb_3.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
        rb_4.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
        rb_5.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
        switch (top_type) {
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
        int page = load_page;
        if (load_type == 0) {
            page = 1;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("current", page);
        map.put("size", AppConfig.LOAD_SIZE);
        map.put("orderStatus", top_type);
        loadSVData(AppConfig.BASE_URL_3, AppConfig.URL_ORDER_LIST, map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_ORDER_LIST);
    }

    /**
     * 取消订单
     */
    private void postCancelOrder() {
        try {
            ArrayList<String> codeList = new ArrayList<>();
            codeList.add(selectOrderNo);
            JSONArray codes = new JSONArray(codeList);
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("codeList", codes);
            postJsonData(AppConfig.BASE_URL_3, AppConfig.URL_ORDER_CANCEL, jsonObj, AppConfig.REQUEST_SV_ORDER_CANCEL);
        } catch (JSONException e) {
            ExceptionUtil.handle(e);
        }
    }

    /**
     * 删除订单
     */
    private void postDeleteOrder() {
        try {
            ArrayList<String> codeList = new ArrayList<>();
            codeList.add(selectOrderNo);
            JSONArray codes = new JSONArray(codeList);
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("codeList", codes);
            postJsonData(AppConfig.BASE_URL_3, AppConfig.URL_ORDER_DELETE, jsonObj, AppConfig.REQUEST_SV_ORDER_DELETE);
        } catch (JSONException e) {
            ExceptionUtil.handle(e);
        }
    }

    /**
     * 确认收货
     */
    private void postConfirmReceipt() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("orderCode", selectOrderNo);
        loadSVData(AppConfig.BASE_URL_3, AppConfig.URL_ORDER_CONFIRM, map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_ORDER_CONFIRM);
    }

    /**
     * 在线支付
     */
    private void startPay(String orderSn, double payPrice) {
        if (StringUtil.isNull(orderSn) || payPrice <= 0) return;
        Intent intent = new Intent(mContext, WXPayEntryActivity.class);
        intent.putExtra("sourceType", WXPayEntryActivity.SOURCE_TYPE_3);
        intent.putExtra("orderSn", orderSn);
        intent.putExtra("orderTotal", df.format(payPrice));
        startActivityForResult(intent, AppConfig.ACTIVITY_CODE_PAY_DATA);
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        BaseEntity<OPurchaseEntity> baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_ORDER_LIST:
                    baseEn = JsonUtils.getOrderListData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        int newTotal = baseEn.getDataTotal();
                        List<OPurchaseEntity> lists = new ArrayList<>();
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
                case AppConfig.REQUEST_SV_ORDER_CANCEL:
                    baseEn = JsonUtils.getBaseErrorData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        cancelOrderUpdate();
                    } else if (baseEn.getErrNo() == AppConfig.ERROR_CODE_TIMEOUT) {
                        handleTimeOut();
                        finish();
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_ORDER_DELETE:
                    baseEn = JsonUtils.getBaseErrorData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        deleteOrderUpdate();
                    } else if (baseEn.getErrNo() == AppConfig.ERROR_CODE_TIMEOUT) {
                        handleTimeOut();
                        finish();
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_ORDER_CONFIRM:
                    baseEn = JsonUtils.getBaseErrorData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        confirmOrderUpdate();
                    } else if (baseEn.getErrNo() == AppConfig.ERROR_CODE_TIMEOUT) {
                        handleTimeOut();
                        finish();
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
            if (requestCode == AppConfig.ACTIVITY_CODE_PAY_DATA) {
                boolean isPayOk = data.getBooleanExtra(AppConfig.ACTIVITY_KEY_PAY_RESULT, false);
                if (isPayOk) {
                    payOrderUpdate();
                }
            } else if (requestCode == AppConfig.ACTIVITY_CODE_ORDER_UPDATE) {
                int updateType = data.getIntExtra(AppConfig.PAGE_DATA, 0);
                switch (updateType) {
                    case AppConfig.ORDER_STATUS_102: //取消订单
                        cancelOrderUpdate();
                        break;
                    case AppConfig.ORDER_STATUS_301: //支付订单
                        payOrderUpdate();
                        break;
                    case -999: //删除订单
                        deleteOrderUpdate();
                        break;
                    case AppConfig.ORDER_STATUS_501: //确认订单
                        confirmOrderUpdate();
                        break;
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    static class MyHandler extends Handler {

        WeakReference<MyPurchaseActivity> mActivity;

        MyHandler(MyPurchaseActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MyPurchaseActivity theActivity = mActivity.get();
            switch (msg.what) {
                case 101: //取消订单
                    theActivity.postCancelOrder();
                    break;
                case 102: //删除订单
                    theActivity.postDeleteOrder();
                    break;
                case 103: //确认收货
                    theActivity.postConfirmReceipt();
                    break;
            }
        }
    }
}
