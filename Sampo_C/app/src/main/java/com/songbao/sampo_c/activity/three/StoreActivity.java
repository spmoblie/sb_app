package com.songbao.sampo_c.activity.three;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.activity.BaseActivity;
import com.songbao.sampo_c.activity.mine.CustomizeActivity;
import com.songbao.sampo_c.adapter.AdapterCallback;
import com.songbao.sampo_c.adapter.DesignerAdapter;
import com.songbao.sampo_c.adapter.StoreAdapter;
import com.songbao.sampo_c.entity.BaseEntity;
import com.songbao.sampo_c.entity.DesignerEntity;
import com.songbao.sampo_c.entity.OCustomizeEntity;
import com.songbao.sampo_c.entity.StoreEntity;
import com.songbao.sampo_c.utils.CommonTools;
import com.songbao.sampo_c.utils.ExceptionUtil;
import com.songbao.sampo_c.utils.JsonUtils;
import com.songbao.sampo_c.utils.LogUtil;
import com.songbao.sampo_c.utils.StringUtil;
import com.songbao.sampo_c.utils.retrofit.HttpRequests;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;


public class StoreActivity extends BaseActivity implements View.OnClickListener {

    String TAG = StoreActivity.class.getSimpleName();

    @BindView(R.id.refresh_view_rv)
    RecyclerView refresh_rv;

    StoreAdapter rvAdapter;

    private String skuCode = "";
    private ArrayList<StoreEntity> al_show = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        skuCode = getIntent().getStringExtra("skuCode");

        initView();
    }

    private void initView() {
        setTitle(getString(R.string.customize_title_store));

        initRecyclerView();
        loadServerData();
    }

    private void initRecyclerView() {
        rvAdapter = new StoreAdapter(mContext, R.layout.item_grid_store);
        rvAdapter.addData(al_show);
        rvAdapter.addCallback(new AdapterCallback() {
            @Override
            public void setOnClick(Object data, int position, int type) {
                if (position < 0 || position >= al_show.size()) return;
                StoreEntity storeEn = al_show.get(position);
                openDesignerActivity(skuCode, storeEn.getId());
            }
        });
        refresh_rv.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        refresh_rv.setAdapter(rvAdapter);
    }

    private void updateListData() {
        if (al_show.size() <= 0) {
            setNullVisibility(View.VISIBLE);
        } else {
            setNullVisibility(View.GONE);
        }
        rvAdapter.updateData(al_show);
    }

    /**
     * 打开设计师列表
     */
    protected void openDesignerActivity(String skuCode, int storeId) {
        if (isLogin()) {
            Intent intent = new Intent(mContext, DesignerActivity.class);
            intent.putExtra("skuCode", skuCode);
            intent.putExtra("storeId", storeId);
            startActivity(intent);
        } else {
            openLoginActivity();
        }
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
     * 加载数据
     */
    private void loadServerData() {
        HashMap<String, Object> map = new HashMap<>();
        loadSVData(AppConfig.URL_STORE_LIST, map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_STORE_LIST);
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        BaseEntity<StoreEntity> baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_STORE_LIST:
                    baseEn = JsonUtils.getStoreData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        al_show.clear();
                        al_show.addAll(baseEn.getLists());
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
    }

}
