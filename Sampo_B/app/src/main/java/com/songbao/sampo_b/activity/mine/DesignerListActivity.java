package com.songbao.sampo_b.activity.mine;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.activity.BaseActivity;
import com.songbao.sampo_b.adapter.AdapterCallback;
import com.songbao.sampo_b.adapter.DesignerAdapter;
import com.songbao.sampo_b.entity.BaseEntity;
import com.songbao.sampo_b.entity.DesignerEntity;
import com.songbao.sampo_b.utils.CommonTools;
import com.songbao.sampo_b.utils.ExceptionUtil;
import com.songbao.sampo_b.utils.JsonUtils;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.utils.retrofit.HttpRequests;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;


public class DesignerListActivity extends BaseActivity implements View.OnClickListener {

    String TAG = DesignerListActivity.class.getSimpleName();

    @BindView(R.id.refresh_view_gv)
    GridView mGridView;

    @BindView(R.id.designer_tv_click)
    TextView tv_click;

    DesignerAdapter gvAdapter;

    private int data_total = -1; //数据总量
    private int load_page = 1; //加载页数
    private int load_type = 1; //加载类型(0:下拉刷新/1:翻页加载)
    private boolean isLoadOk = true; //加载控制
    private String skuCode = "";
    private String dgName; //选择的设计师姓名
    private ArrayList<DesignerEntity> al_show = new ArrayList<>();
    private ArrayMap<String, Boolean> am_show = new ArrayMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designer);

        skuCode = getIntent().getStringExtra("skuCode");

        initView();
    }

    private void initView() {
        setTitle(getString(R.string.designer_shop));

        tv_click.setOnClickListener(this);

        initRecyclerView();
        loadMoreData();
    }

    private void initRecyclerView() {
        /*refresh_gv.setPullRefreshEnabled(true); //下拉刷新
        refresh_gv.setPullLoadEnabled(true); //上拉加载
		refresh_gv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<GridView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
				// 下拉刷新
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						refreshData();
					}
				}, AppConfig.LOADING_TIME);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
				// 加载更多
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						if (!isStopLoadMore(al_show.size(), data_total, 0)) {
							loadMoreData();
						} else {
							refresh_gv.setHasMoreData(false);
							refresh_gv.onPullUpRefreshComplete();
						}
					}
				}, AppConfig.LOADING_TIME);
			}
		});
		GridView mGridView = refresh_gv.getRefreshableView();*/
        mGridView.setNumColumns(2);
        mGridView.setHorizontalSpacing(CommonTools.dpToPx(mContext, 6));
        mGridView.setVerticalSpacing(CommonTools.dpToPx(mContext, 6));
        mGridView.setVerticalScrollBarEnabled(false);

        // 配置适配器
        gvAdapter = new DesignerAdapter(mContext);
        gvAdapter.addData(al_show);
        gvAdapter.addCallback(new AdapterCallback() {
            @Override
            public void setOnClick(Object data, int position, int type) {
                updateListData(position);
            }
        });
        mGridView.setAdapter(gvAdapter);
    }

    private void updateListData(int position) {
        if (al_show.size() <= 0) {
            setNullVisibility(View.VISIBLE);
        } else {
            setNullVisibility(View.GONE);
            for (int i = 0; i < al_show.size(); i++) {
                al_show.get(i).setSelect(false);
                if (i == position) {
                    al_show.get(i).setSelect(true);
                    dgName = al_show.get(i).getName();
                }
            }
        }
        gvAdapter.updateData(al_show);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.designer_tv_click:
                showConfirmDialog(getString(R.string.designer_subscribe_tips, dgName), new MyHandler(this));
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
        loadServerData();
    }

    /**
     * 加载数据
     */
    private void loadServerData() {
        if (!isLoadOk) return; //加载频率控制
        isLoadOk = false;
        HashMap<String, String> map = new HashMap<>();
        /*String page = String.valueOf(load_page);
        if (load_type == 0) {
            page = "1";
        }
        map.put("page", page);
        map.put("size", AppConfig.LOAD_SIZE);*/
        loadSVData(AppConfig.URL_USER_DESIGNER, map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_USER_DESIGNER);
    }

    /**
     * 提交预约
     */
    private void postCustomizeData() {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("designerId", 26);
            jsonObj.put("skuCode", skuCode);
            postJsonData(AppConfig.BASE_URL_3, AppConfig.URL_BOOKING_CREATE, jsonObj, AppConfig.REQUEST_SV_BOOKING_CREATE);
        } catch (JSONException e) {
            ExceptionUtil.handle(e);
        }
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        BaseEntity baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_USER_DESIGNER:
                    baseEn = JsonUtils.getDesignData(jsonObject);
                    if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
                        data_total = baseEn.getDataTotal();
                        List<DesignerEntity> lists = filterData(baseEn.getLists(), am_show);
                        if (lists != null && lists.size() > 0) {
                            if (load_type == 0) {
                                //下拉
                                LogUtil.i(LogUtil.LOG_HTTP, TAG + " 刷新数据 —> size = " + lists.size());
                                lists.addAll(al_show);
                                al_show.clear();
                            } else {
                                //翻页
                                LogUtil.i(LogUtil.LOG_HTTP, TAG + " 翻页数据 —> size = " + lists.size());
                                load_page++;
                            }
                            al_show.addAll(lists);
                        }
                        updateListData(0);
                    } else if (baseEn.getErrno() == AppConfig.ERROR_CODE_TIMEOUT) {
                        handleTimeOut();
                        finish();
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_BOOKING_CREATE:
                    baseEn = JsonUtils.getDesignData(jsonObject);
                    if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
                        CommonTools.showToast(getString(R.string.designer_subscribe_ok));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 关闭之前的页面
                                closeCustomizeActivity();
                                // 回退至“我的”
                                shared.edit().putBoolean(AppConfig.KEY_JUMP_PAGE, true).apply();
                                shared.edit().putInt(AppConfig.KEY_MAIN_CURRENT_INDEX, 2).apply();
                                // 跳转至“我的订制”
                                openActivity(CustomizeActivity.class);
                            }
                        }, 500);
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
        //refresh_gv.onPullUpRefreshComplete();
        //refresh_gv.onPullDownRefreshComplete();
    }

    static class MyHandler extends Handler {

        WeakReference<DesignerListActivity> mActivity;

        MyHandler(DesignerListActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            DesignerListActivity theActivity = mActivity.get();
            switch (msg.what) {
                case AppConfig.DIALOG_CLICK_OK:
                    theActivity.postCustomizeData();
                    break;
            }
        }
    }

}
