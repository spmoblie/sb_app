package com.songbao.sampo_b.activity.two;

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
import com.songbao.sampo_b.activity.mine.CustomizeActivity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;


public class DesignerActivity extends BaseActivity implements View.OnClickListener {

    String TAG = DesignerActivity.class.getSimpleName();

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
        setTitle("预约设计师");

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
                for (int i = 0; i < al_show.size(); i++) {
                    al_show.get(i).setSelect(false);
                    if (i == position) {
                        al_show.get(i).setSelect(true);
                        dgName = al_show.get(i).getName();
                    }
                }
                updateListData();
            }
        });
        mGridView.setAdapter(gvAdapter);
    }

    private void updateListData() {
        if (al_show.size() <= 0) {
            setNullVisibility(View.VISIBLE);
        } else {
            setNullVisibility(View.GONE);
        }
        gvAdapter.updateData(al_show);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.designer_tv_click:
                showConfirmDialog("确认预约" + dgName + "么?", null, null, true, true,
                        new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                switch (msg.what) {
                                    case AppConfig.DIALOG_CLICK_NO:
                                        break;
                                    case AppConfig.DIALOG_CLICK_OK:
                                        postCustomizeData();
                                        break;
                                }
                            }
                        });
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
        //loadServerData();
        loadDemoData();
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
        HashMap<String, String> map = new HashMap<>();
        map.put("page", page);
        map.put("size", AppConfig.LOAD_SIZE);
        loadSVData(AppConfig.URL_DESIGN_ALL, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_DESIGN_ALL);
    }

    /**
     * 提交预约
     */
    private void postCustomizeData() {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("designerId", 26);
            jsonObj.put("skuCode", skuCode);
            postJsonData(AppConfig.BASE_URL_3, AppConfig.URL_CREATE_BOOKING, jsonObj, AppConfig.REQUEST_SV_CREATE_BOOKING);
        } catch (JSONException e) {
            ExceptionUtil.handle(e);
        }
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        BaseEntity baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_DESIGN_ALL:
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
                        updateListData();
                    } else if (baseEn.getErrno() == AppConfig.ERROR_CODE_TIMEOUT) {
                        handleTimeOut();
                        finish();
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_CREATE_BOOKING:
                    baseEn = JsonUtils.getDesignData(jsonObject);
                    if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
                        CommonTools.showToast("预约成功，可在“我的定制”查看进度。");
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

    /**
     * 构建Demo数据
     */
    private void loadDemoData() {
        al_show.clear();

        DesignerEntity chEn_1 = new DesignerEntity();
        DesignerEntity chEn_2 = new DesignerEntity();
        DesignerEntity chEn_3 = new DesignerEntity();
        DesignerEntity chEn_4 = new DesignerEntity();

        chEn_1.setImgUrl("");
        chEn_1.setName("设计师A");
        chEn_1.setPhone("13686436466");
        chEn_1.setInfo("设计师A，国际著名设计师，首位获得德意志联邦共和国 国家设计奖的中国设计师。曾赢得2012年German Design Award。");
        chEn_1.setSelect(true);
        al_show.add(chEn_1);
        chEn_2.setImgUrl("");
        chEn_2.setName("设计师B");
        chEn_2.setPhone("15866631336");
        chEn_2.setInfo("设计师B，著名华裔设计师，出生于纽约曼哈顿上东区，祖籍江苏徐州是永不会出错、绝不会让女星得到“最差劲服装奖”的品质保证。");
        chEn_2.setSelect(false);
        al_show.add(chEn_2);
        chEn_3.setImgUrl("");
        chEn_3.setName("设计师C");
        chEn_3.setPhone("13686436466");
        chEn_3.setInfo("设计师C，国际著名设计师， 首位获得德意志联邦共和国 国家设计奖的中国设计师。 曾赢得2012年German Design Award。");
        chEn_3.setSelect(false);
        al_show.add(chEn_3);
        chEn_4.setImgUrl("");
        chEn_4.setName("设计师D");
        chEn_4.setPhone("15866631336");
        chEn_4.setInfo("设计师D，著名华裔设计师，出生于纽约曼哈顿上东区，祖籍江苏徐州是永不会出错、绝不会让女星得到“最差劲服装奖”的品质保证。");
        chEn_4.setSelect(false);
        al_show.add(chEn_4);

        dgName = al_show.get(0).getName();

        updateListData();
        stopAnimation();
    }

}