package com.songbao.sampo_c.activity.home;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.activity.BaseActivity;
import com.songbao.sampo_c.adapter.AdapterCallback;
import com.songbao.sampo_c.adapter.HouseAdapter;
import com.songbao.sampo_c.entity.BaseEntity;
import com.songbao.sampo_c.entity.HouseEntity;
import com.songbao.sampo_c.utils.ExceptionUtil;
import com.songbao.sampo_c.utils.JsonUtils;
import com.songbao.sampo_c.utils.LogUtil;
import com.songbao.sampo_c.utils.retrofit.HttpRequests;
import com.songbao.sampo_c.widgets.pullrefresh.PullToRefreshBase;
import com.songbao.sampo_c.widgets.pullrefresh.PullToRefreshRecyclerView;
import com.songbao.sampo_c.widgets.recycler.MyRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;


public class HouseListActivity extends BaseActivity implements View.OnClickListener {

    String TAG = HouseListActivity.class.getSimpleName();

    @BindView(R.id.refresh_view_rv)
    PullToRefreshRecyclerView refresh_rv;

    HouseAdapter rvAdapter;
    MyRecyclerView mRecyclerView;

    private int data_total = -1; //数据总量
    private int load_page = 1; //加载页数
    private int load_type = 1; //加载类型(0:下拉刷新/1:翻页加载)
    private boolean isLoadOk = true; //加载控制
    private ArrayList<HouseEntity> al_show = new ArrayList<>();
    private ArrayMap<String, Boolean> am_show = new ArrayMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_list);

        initView();
    }

    private void initView() {
        setTitle(getString(R.string.customize_title_house));

        initRecyclerView();
        loadMoreData();
    }

    private void initRecyclerView() {
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

        // 设置布局管理器
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView = refresh_rv.getRefreshableView();
        mRecyclerView.setLayoutManager(layoutManager);

        // 配置内容适配器
        rvAdapter = new HouseAdapter(mContext, R.layout.item_grid_house);
        rvAdapter.addData(al_show);
        rvAdapter.addCallback(new AdapterCallback() {
            @Override
            public void setOnClick(Object data, int position, int type) {
                if (position < 0 || position >= al_show.size()) return;
                HouseEntity houseEn = al_show.get(position);
                openWebViewActivity(houseEn.getName(), houseEn.getWebUrl());
            }
        });
        mRecyclerView.setAdapter(rvAdapter);
    }

    private void updateListData() {
        if (al_show.size() <= 0) {
             setNullVisibility(View.VISIBLE);
        } else {
            setNullVisibility(View.GONE);
        }
        rvAdapter.updateData(al_show);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
     * 数据报错处理
     */
    private void dataErrorHandle() {
        loadServerData();
    }

    /**
     *下拉刷新
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
        map.put("page", page);
        map.put("size", AppConfig.LOAD_SIZE);
        loadSVData(AppConfig.URL_HOUSE_LIST, map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_HOUSE_LIST);
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        BaseEntity<HouseEntity> baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_HOUSE_LIST:
                    baseEn = JsonUtils.getHouseData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        data_total = baseEn.getDataTotal();
                        List<HouseEntity> lists = filterData(baseEn.getLists(), am_show);
                        if (lists != null && lists.size() > 0) {
                            if (load_type == 0) {
                                //下拉
                                LogUtil.i(LogUtil.LOG_HTTP, TAG + " 刷新数据 —> size = " + lists.size());
                                lists.addAll(al_show);
                                al_show.clear();
                            }else {
                                //翻页
                                LogUtil.i(LogUtil.LOG_HTTP, TAG + " 翻页数据 —> size = " + lists.size());
                                load_page++;
                            }
                            al_show.addAll(lists);
                        }
                        updateListData();
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
        handleErrorCode(null);
    }

    @Override
    protected void stopAnimation() {
        super.stopAnimation();
        isLoadOk = true;
        refresh_rv.onPullUpRefreshComplete();
        refresh_rv.onPullDownRefreshComplete();
    }

}
