package com.songbao.sampo_b.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.activity.BaseActivity;
import com.songbao.sampo_b.adapter.AdapterCallback;
import com.songbao.sampo_b.adapter.AddressAdapter;
import com.songbao.sampo_b.entity.AddressEntity;
import com.songbao.sampo_b.entity.BaseEntity;
import com.songbao.sampo_b.utils.ClickUtils;
import com.songbao.sampo_b.utils.ExceptionUtil;
import com.songbao.sampo_b.utils.JsonUtils;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.utils.StringUtil;
import com.songbao.sampo_b.utils.retrofit.HttpRequests;
import com.songbao.sampo_b.widgets.pullrefresh.PullToRefreshBase;
import com.songbao.sampo_b.widgets.pullrefresh.PullToRefreshRecyclerView;
import com.songbao.sampo_b.widgets.recycler.MyRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;


public class AddressActivity extends BaseActivity implements View.OnClickListener {

    String TAG = AddressActivity.class.getSimpleName();

    @BindView(R.id.address_rv_address)
    PullToRefreshRecyclerView refresh_rv;

    @BindView(R.id.address_view_tv_address_add)
    TextView tv_address_add;

    AddressAdapter rvAdapter;

    private AddressEntity selectEn;
    private int mPosition = 0;
    private int data_total = -1; //数据总量
    private int load_page = 1; //加载页数
    private int load_type = 1; //加载类型(0:下拉刷新/1:翻页加载)
    private boolean isLoadOk = true; //加载控制

    private int totalNum;
    private int selectId;
    private boolean isFinish;
    private boolean isManage;
    private boolean orSelect;
    private String selectIdStr = "";
    private ArrayList<AddressEntity> al_show = new ArrayList<>();
    private ArrayMap<String, Boolean> am_show = new ArrayMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        isFinish = getIntent().getBooleanExtra("isFinish", false);
        selectId = getIntent().getIntExtra("selectId", 0);

        initView();
    }

    private void initView() {
        setTitle(getString(R.string.address_title));

        tv_address_add.setOnClickListener(this);

        updateNumber();
        initRecyclerView();
        loadMoreData();
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
        MyRecyclerView mRecyclerView = refresh_rv.getRefreshableView();
        mRecyclerView.setLayoutManager(layoutManager);

        // 配置适配器
        orSelect = isFinish || isManage;
        rvAdapter = new AddressAdapter(mContext, R.layout.item_list_address);
        rvAdapter.updateData(al_show, orSelect);
        rvAdapter.addCallback(new AdapterCallback() {

            @Override
            public void setOnClick(Object data, int position, int type) {
                if (position < 0 || position >= al_show.size()) return;
                mPosition = position;
                AddressEntity addEn = al_show.get(position);
                if (addEn != null) {
                    switch (type) {
                        case 0: //选择
                            if (isManage) { //管理
                                al_show.get(position).setSelect(!addEn.isSelect());
                                updateListData();
                            } else {
                                if (isFinish) { //订单选择地址
                                    selectEn = addEn;
                                    finish();
                                    return;
                                }
                            }
                            break;
                        case 1: //编辑
                            openAddressEditActivity(addEn);
                            break;
                        case 5: //默认
                            selectIdStr = addEn.getEntityId();
                            postDefaultAddress();
                            break;
                        case 6: //删除
                            selectIdStr = addEn.getEntityId();
                            postDeleteAddress();
                            break;
                        case 7: //滚动
                            rvAdapter.reset(mPosition);
                            break;
                    }
                }
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
        orSelect = isFinish || isManage;
        rvAdapter.updateData(al_show, orSelect);
        checkAllDataStatus();
    }

    /**
     * 删除选择的地址
     */
    private void deleteSelectItem() {
        selectIdStr = "";
        StringBuilder sb = new StringBuilder();
        AddressEntity cartEn;
        for (int i = 0; i < al_show.size(); i++) {
            cartEn = al_show.get(i);
            if (cartEn.isSelect()) {
                sb.append(cartEn.getId());
                sb.append(",");
            }
        }
        if (sb.toString().contains(",")) {
            sb.deleteCharAt(sb.lastIndexOf(","));
            selectIdStr = sb.toString();
        }
        if (!StringUtil.isNull(selectIdStr)) {
            postDeleteAddress();
        }
    }

    /**
     * 初始化选中项
     */
    private void initDefaultStatus() {
        for (int i = 0; i < al_show.size(); i++) {
            int addId = al_show.get(i).getId();
            if (addId == selectId) {
                al_show.get(i).setSelect(true);
                break; //匹配到默认项，结束循环
            } else {
                al_show.get(i).setSelect(false);
            }
        }
        updateListData();
    }

    /**
     * 恢复默认状态
     */
    private void changeDefaultStatus() {
        for (int i = 0; i < al_show.size(); i++) {
            al_show.get(i).setSelect(false);
        }
        updateListData();
    }

    /**
     * 遍历检查所有数据状态
     */
    private void checkAllDataStatus() {
        AddressEntity addEn;
        totalNum = 0;
        for (int i = 0; i < al_show.size(); i++) {
            addEn = al_show.get(i);
            if (addEn.isSelect()) {
                totalNum++;
            }
        }
        updateNumber();
    }

    /**
     * 更新已选地址数量
     */
    private void updateNumber() {
        if (isManage) {
            setRightViewText(getString(R.string.done));
            tv_address_add.setText(getString(R.string.address_delete_num, totalNum));
            tv_address_add.setBackgroundResource(R.drawable.shape_style_solid_05_08);
        } else {
            setRightViewText(getString(R.string.address_manage));
            tv_address_add.setText(getString(R.string.address_add));
            tv_address_add.setBackgroundResource(R.drawable.shape_style_solid_04_08);
        }
    }

    /**
     * 打开编辑地址页
     */
    private void openAddressEditActivity(AddressEntity addEn) {
        Intent intent = new Intent(mContext, AddressEditActivity.class);
        intent.putExtra(AppConfig.PAGE_DATA, addEn);
        startActivityForResult(intent, AppConfig.ACTIVITY_CODE_EDIT_ADDRESS);
    }

    @Override
    protected void OnListenerRight() {
        isManage = !isManage;
        changeDefaultStatus();
        if (!isManage) {
            initDefaultStatus();
        }
    }

    @Override
    protected void OnListenerLeft() {
        super.OnListenerLeft();
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void finish() {
        if (al_show.size() == 0 || selectEn != null) {
            if (al_show.size() == 0) {
                selectEn = null;
            }
            Intent returnIntent = new Intent();
            returnIntent.putExtra(AppConfig.PAGE_DATA, selectEn);
            setResult(RESULT_OK, returnIntent);
        }
        super.finish();
    }

    @Override
    public void onClick(View v) {
        if (ClickUtils.isDoubleClick()) return;
        switch (v.getId()) {
            case R.id.address_view_tv_address_add:
                if (isManage) { //删除
                    deleteSelectItem();
                } else { //新增
                    openAddressEditActivity(null);
                }
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
     * 清空缓存
     */
    private void clearData() {
        al_show.clear();
        am_show.clear();
    }

    /**
     * 加载第一页数据
     */
    private void loadFirstPageData() {
        clearData();
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
        loadServerData();
    }

    /**
     * 加载数据
     */
    private void loadServerData() {
        if (!isLoadOk) return; //加载频率控制
        isLoadOk = false;
        HashMap<String, Object> map = new HashMap<>();
        loadSVData(AppConfig.BASE_URL_3, AppConfig.URL_ADDRESS_LIST, map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_ADDRESS_LIST);
    }

    /**
     * 提交删除收货地址
     */
    private void postDeleteAddress() {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("consigneeIds", selectIdStr);
            postJsonData(AppConfig.BASE_URL_3, AppConfig.URL_ADDRESS_DELETE, jsonObj, AppConfig.REQUEST_SV_ADDRESS_DELETE);
        } catch (JSONException e) {
            ExceptionUtil.handle(e);
        }
    }

    /**
     * 设置默认收货地址
     */
    private void postDefaultAddress() {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("consigneeId", selectIdStr);
            postJsonData(AppConfig.BASE_URL_3, AppConfig.URL_ADDRESS_DEFAULT, jsonObj, AppConfig.REQUEST_SV_ADDRESS_DEFAULT);
        } catch (JSONException e) {
            ExceptionUtil.handle(e);
        }
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        super.callbackData(jsonObject, dataType);
        BaseEntity<AddressEntity> baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_ADDRESS_LIST:
                    baseEn = JsonUtils.getAddressListData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        data_total = baseEn.getDataTotal();
                        List<AddressEntity> lists = filterData(baseEn.getLists(), am_show);
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
                        initDefaultStatus();
                    } else if (baseEn.getErrNo() == AppConfig.ERROR_CODE_TIMEOUT) {
                        handleTimeOut();
                        finish();
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_ADDRESS_DELETE:
                case AppConfig.REQUEST_SV_ADDRESS_DEFAULT:
                    baseEn = JsonUtils.getBaseErrorData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        loadFirstPageData();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == AppConfig.ACTIVITY_CODE_EDIT_ADDRESS) {
                boolean isUpdate = data.getBooleanExtra(AppConfig.PAGE_DATA, false);
                if (isUpdate) {
                    loadFirstPageData();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
