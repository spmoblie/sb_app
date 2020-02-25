package com.songbao.sampo_b.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.activity.BaseActivity;
import com.songbao.sampo_b.adapter.AdapterCallback;
import com.songbao.sampo_b.adapter.DesignerAdapter;
import com.songbao.sampo_b.entity.BaseEntity;
import com.songbao.sampo_b.entity.DesignerEntity;
import com.songbao.sampo_b.entity.OCustomizeEntity;
import com.songbao.sampo_b.utils.CommonTools;
import com.songbao.sampo_b.utils.ExceptionUtil;
import com.songbao.sampo_b.utils.JsonUtils;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.utils.StringUtil;
import com.songbao.sampo_b.utils.retrofit.HttpRequests;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;


public class DesignerListActivity extends BaseActivity implements View.OnClickListener {

    String TAG = DesignerListActivity.class.getSimpleName();

    @BindView(R.id.refresh_view_rv)
    RecyclerView refresh_rv;

    @BindView(R.id.designer_tv_click)
    TextView tv_click;

    DesignerAdapter rvAdapter;

    private int currentPos = -1; //当前下标
    private int dgId = 0; //选择的设计师
    private String dgName; //选择的设计师姓名
    private String skuCode = "";
    private ArrayList<DesignerEntity> al_show = new ArrayList<>();

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

        if (StringUtil.isNull(skuCode)) {
            tv_click.setVisibility(View.GONE);
        }

        initRecyclerView();
        loadServerData();
    }

    private void initRecyclerView() {
        rvAdapter = new DesignerAdapter(mContext, R.layout.item_grid_designer);
        rvAdapter.addData(al_show);
        rvAdapter.addCallback(new AdapterCallback() {
            @Override
            public void setOnClick(Object data, int position, int type) {
                if (position == currentPos) return;
                currentPos = position;
                updateListData(position);
            }
        });
        refresh_rv.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        refresh_rv.setAdapter(rvAdapter);
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
                    dgId = al_show.get(i).getId();
                    dgName = al_show.get(i).getName();
                }
            }
        }
        rvAdapter.updateData(al_show);
    }

    /**
     * 打开定制订单详情
     */
    private void openCustomizeActivity(OCustomizeEntity ocEn, int nodePosition) {
        Intent intent = new Intent(mContext, CustomizeActivity.class);
        intent.putExtra(AppConfig.PAGE_DATA, ocEn);
        intent.putExtra("nodePosition", nodePosition);
        startActivityForResult(intent, AppConfig.ACTIVITY_CODE_ORDER_UPDATE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.designer_tv_click:
                if (StringUtil.isNull(dgName)) {
                    dataErrorHandle();
                    return;
                }
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
     * 数据报错处理
     */
    private void dataErrorHandle() {
        loadServerData();
    }

    /**
     * 加载数据
     */
    private void loadServerData() {
        HashMap<String, Object> map = new HashMap<>();
        loadSVData(AppConfig.URL_USER_DESIGNER, map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_USER_DESIGNER);
    }

    /**
     * 提交预约
     */
    private void postCustomizeData() {
        startAnimation();
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("designerId", dgId);
            jsonObj.put("skuCode", skuCode);
            postJsonData(AppConfig.BASE_URL_3, AppConfig.URL_BOOKING_CREATE, jsonObj, AppConfig.REQUEST_SV_BOOKING_CREATE);
        } catch (JSONException e) {
            ExceptionUtil.handle(e);
        }
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        BaseEntity<DesignerEntity> baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_USER_DESIGNER:
                    baseEn = JsonUtils.getDesignData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        al_show.clear();
                        al_show.addAll(baseEn.getLists());
                        updateListData(0);
                    } else if (baseEn.getErrNo() == AppConfig.ERROR_CODE_TIMEOUT) {
                        handleTimeOut();
                        finish();
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_BOOKING_CREATE:
                    baseEn = JsonUtils.getCustomizeResult(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        final String orderNo = baseEn.getOthers();
                        CommonTools.showToast(getString(R.string.designer_subscribe_ok));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 关闭之前的页面
                                closeCustomizeActivity();
                                // 回退至“我的”
                                AppApplication.jumpToHomePage(AppConfig.PAGE_MAIN_MINE);
                                // 跳转至“我的订制”
                                OCustomizeEntity ocEn = new OCustomizeEntity();
                                ocEn.setOrderNo(orderNo);
                                openCustomizeActivity(ocEn, -1);
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
