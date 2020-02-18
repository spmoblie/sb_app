package com.songbao.sampo_c.activity.two;

import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.Group;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.activity.BaseActivity;
import com.songbao.sampo_c.adapter.AdapterCallback;
import com.songbao.sampo_c.adapter.CartGoodsAdapter;
import com.songbao.sampo_c.entity.BaseEntity;
import com.songbao.sampo_c.entity.CartEntity;
import com.songbao.sampo_c.entity.GoodsAttrEntity;
import com.songbao.sampo_c.entity.GoodsEntity;
import com.songbao.sampo_c.utils.CommonTools;
import com.songbao.sampo_c.utils.ExceptionUtil;
import com.songbao.sampo_c.utils.JsonUtils;
import com.songbao.sampo_c.utils.LogUtil;
import com.songbao.sampo_c.utils.StringUtil;
import com.songbao.sampo_c.utils.retrofit.HttpRequests;
import com.songbao.sampo_c.widgets.pullrefresh.PullToRefreshBase;
import com.songbao.sampo_c.widgets.pullrefresh.PullToRefreshRecyclerView;
import com.songbao.sampo_c.widgets.recycler.MyRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;


public class CartActivity extends BaseActivity implements View.OnClickListener {

    String TAG = CartActivity.class.getSimpleName();

    @BindView(R.id.cart_rv_goods)
    PullToRefreshRecyclerView refresh_rv;

    @BindView(R.id.cart_view_iv_select_all)
    ImageView iv_select_all;

    @BindView(R.id.cart_view_tv_select_all)
    TextView tv_select_all;

    @BindView(R.id.cart_view_tv_price)
    TextView tv_price;

    @BindView(R.id.cart_view_tv_price_group)
    Group tv_price_group;

    @BindView(R.id.cart_view_tv_confirm)
    TextView tv_confirm;

    CartGoodsAdapter rvAdapter;

    private int mPosition = 0;
    private int buyNumber = 0;
    private int totalNum;
    private double totalPrice;
    private boolean isSelectAll;
    private boolean isManage;
    private ArrayList<Integer> selectIds = new ArrayList<>();
    private ArrayList<CartEntity> al_show = new ArrayList<>();
    private ArrayMap<String, Boolean> am_show = new ArrayMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        AppApplication.updateCartData(true);

        initView();
    }

    private void initView() {
        setTitle(getString(R.string.cart_shopping));

        iv_select_all.setOnClickListener(this);
        tv_select_all.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);

        initRecyclerView();
        updateListData();
    }

    private void initRecyclerView() {
        refresh_rv.setHeaderLayoutBackground(R.color.ui_color_app_bg_02);
        refresh_rv.setFooterLayoutBackground(R.color.ui_color_app_bg_02);
        refresh_rv.setPullRefreshEnabled(true); //下拉刷新
        refresh_rv.setPullLoadEnabled(false); //上拉加载
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
                        if (!isStopLoadMore(al_show.size(), -1, 0)) {
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
        rvAdapter = new CartGoodsAdapter(mContext, R.layout.item_list_goods_cart);
        rvAdapter.updateData(al_show);
        rvAdapter.addCallback(new AdapterCallback() {

            @Override
            public void setOnClick(Object data, int position, int type) {
                if (position < 0 || position >= al_show.size()) return;
                mPosition = position;
                CartEntity cartEn = al_show.get(position);
                GoodsEntity goodsEn = cartEn.getGoodsEn();
                if (goodsEn != null) {
                    GoodsAttrEntity attrEn = goodsEn.getAttrEn();
                    buyNumber = 1;
                    if (attrEn != null) {
                        buyNumber = attrEn.getBuyNum();
                    }
                    switch (type) {
                        case 0: //选择
                            boolean isSelect = !cartEn.isSelect();
                            al_show.get(position).setSelect(isSelect);
                            updateListData();
                            break;
                        case 1: //商品
                            openGoodsActivity(goodsEn.getSkuCode());
                            break;
                        case 2: //属性
                            loadGoodsAttrData(goodsEn.getGoodsCode(), attrEn);
                            break;
                        case 3: //减少
                            if (buyNumber > 1) {
                                buyNumber--;
                                updateGoodsNumber(cartEn.getId(), buyNumber, goodsEn.getSkuCode());
                            } else {
                                CommonTools.showToast("不能再减少了哟~");
                            }
                            break;
                        case 4: //增加
                            buyNumber++;
                            updateGoodsNumber(cartEn.getId(), buyNumber, goodsEn.getSkuCode());
                            break;
                        case 5: //定制
                            openDesignerActivity(goodsEn.getSkuCode());
                            break;
                        case 6: //删除
                            al_show.get(position).setSelect(true);
                            handleSelectItem(0);
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
        rvAdapter.updateData(al_show);
        checkAllDataStatus();
    }

    /**
     * 对选择的商品进行处理
     */
    private void handleSelectItem(int type) {
        selectIds.clear();
        CartEntity cartEn;
        for (int i = 0; i < al_show.size(); i++) {
            cartEn = al_show.get(i);
            if (cartEn.isSelect()) {
                selectIds.add(cartEn.getId());
            }
        }
        if (selectIds.size() > 0) {
            if (type == 1) { //结算
                checkedCartGoods();
            } else { //删除
                deleteCartGoods();
            }
        }
    }

    /**
     * 更新“全选”的状态
     */
    private void updateSelectAllStatus() {
        for (int i = 0; i < al_show.size(); i++) {
            al_show.get(i).setSelect(isSelectAll);
        }
        updateListData();
    }

    /**
     * 遍历检查所有数据状态
     */
    private void checkAllDataStatus() {
        CartEntity cartEn;
        GoodsEntity goodsEn;
        GoodsAttrEntity attrEn;
        totalNum = 0;
        totalPrice = 0;
        isSelectAll = true;
        for (int i = 0; i < al_show.size(); i++) {
            cartEn = al_show.get(i);
            if (cartEn.isSelect()) {
                goodsEn = cartEn.getGoodsEn();
                if (goodsEn != null) {
                    attrEn = goodsEn.getAttrEn();
                    if (attrEn != null) {
                        totalNum += attrEn.getBuyNum();
                        totalPrice += goodsEn.getPrice() * attrEn.getBuyNum();
                    }
                }
            } else {
                isSelectAll = false;
            }
        }
        iv_select_all.setSelected(isSelectAll);
        updatePriceTotal();
        updateNumber();
    }

    /**
     * 更新商品总价格
     */
    private void updatePriceTotal() {
        tv_price.setText(df.format(totalPrice));
    }

    /**
     * 更新已选商品数量
     */
    private void updateNumber() {
        if (isManage) {
            setRightViewText(getString(R.string.done));
            tv_price_group.setVisibility(View.GONE);
            tv_confirm.setText(getString(R.string.cart_delete_num, totalNum));
            tv_confirm.setBackgroundResource(R.drawable.shape_style_solid_05_08);
        } else {
            setRightViewText(getString(R.string.cart_manage));
            tv_price_group.setVisibility(View.VISIBLE);
            tv_confirm.setText(getString(R.string.cart_confirm_num, totalNum));
            tv_confirm.setBackgroundResource(R.drawable.shape_style_solid_04_08);
        }
    }

    @Override
    protected void updateSelectAttrStr(GoodsAttrEntity attrEn) {
        if (attrEn != null) {
            GoodsEntity goodsEn = al_show.get(mPosition).getGoodsEn();
            if (attrEn.getSkuCode().equals(goodsEn.getSkuCode())) {
                if (attrEn.getBuyNum() == goodsEn.getAttrEn().getBuyNum()) return;
                //修改数量
                buyNumber = attrEn.getBuyNum();
                updateGoodsNumber(al_show.get(mPosition).getId(), buyNumber, goodsEn.getSkuCode());
            } else {
                //修改属性
                updateGoodsAttr(al_show.get(mPosition).getId(), goodsEn.getGoodsCode(), attrEn);
            }
        }
    }

    @Override
    protected void OnListenerRight() {
        isManage = !isManage;
        isSelectAll = false;
        updateSelectAllStatus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cart_view_iv_select_all:
            case R.id.cart_view_tv_select_all:
                isSelectAll = !isSelectAll;
                updateSelectAllStatus();
                break;
            case R.id.cart_view_tv_confirm:
                if (isManage) { //删除
                    handleSelectItem(0);
                } else { //结算
                    if (totalNum > 0) {
                        handleSelectItem(1);
                    } else {
                        CommonTools.showToast("请选择结算的商品");
                    }
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
        // 页面开始
        AppApplication.onPageStart(this, TAG);

        if (shared.getBoolean(AppConfig.KEY_UPDATE_CART_DATA, true)) {
            updateAllData();
            AppApplication.updateCartData(false);
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

    /**
     * 刷新所有数据
     */
    private void updateAllData() {
        al_show.clear();
        am_show.clear();
        loadServerData();
        loadCartGoodsNum();
    }

    /**
     * 下拉刷新
     */
    private void refreshData() {
        loadServerData();
    }

    /**
     * 翻页加载
     */
    private void loadMoreData() {
        loadServerData();
    }

    /**
     * 加载数据
     */
    private void loadServerData() {
        HashMap<String, String> map = new HashMap<>();
        loadSVData(AppConfig.BASE_URL_3, AppConfig.URL_CART_GET, map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_CART_GET);
    }

    /**
     * 修改购物车商品数量
     */
    private void updateGoodsNumber(int cartId, int buyNum, String skuCode) {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("id", cartId);
            jsonObj.put("buyNum", buyNum);
            jsonObj.put("skuCode", skuCode);
            postJsonData(AppConfig.BASE_URL_3, AppConfig.URL_CART_UPDATE, jsonObj, AppConfig.REQUEST_SV_CART_UPDATE_NUM);
        } catch (JSONException e) {
            ExceptionUtil.handle(e);
        }
    }

    /**
     * 修改购物车商品属性
     */
    private void updateGoodsAttr(int cartId, String goodsCode, GoodsAttrEntity attrEn) {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("id", cartId);
            jsonObj.put("goodsCode", goodsCode);
            jsonObj.put("skuCode", attrEn.getSkuCode());
            jsonObj.put("buyNum", attrEn.getBuyNum());
            postJsonData(AppConfig.BASE_URL_3, AppConfig.URL_CART_ADD, jsonObj, AppConfig.REQUEST_SV_CART_UPDATE_ATTR);
        } catch (JSONException e) {
            ExceptionUtil.handle(e);
        }
    }

    /**
     * 删除购物车商品
     */
    private void deleteCartGoods() {
        try {
            JSONArray ids = new JSONArray(selectIds);
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("ids", ids);
            postJsonData(AppConfig.BASE_URL_3, AppConfig.URL_CART_DELETE, jsonObj, AppConfig.REQUEST_SV_CART_DELETE);
        } catch (JSONException e) {
            ExceptionUtil.handle(e);
        }
    }

    /**
     * 结算购物车商品
     */
    private void checkedCartGoods() {
        startAnimation();
        try {
            JSONArray ids = new JSONArray(selectIds);
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("ids", ids);
            postJsonData(AppConfig.BASE_URL_3, AppConfig.URL_CART_CHECKED, jsonObj, AppConfig.REQUEST_SV_CART_CHECKED);
        } catch (JSONException e) {
            ExceptionUtil.handle(e);
        }
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        super.callbackData(jsonObject, dataType);
        BaseEntity<CartEntity> baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_CART_GET:
                    baseEn = JsonUtils.getCartListData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        List<CartEntity> lists = filterData(baseEn.getLists(), am_show);
                        if (lists != null && lists.size() > 0) {
                            lists.addAll(al_show);
                            al_show.clear();
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
                case AppConfig.REQUEST_SV_CART_UPDATE_NUM:
                    baseEn = JsonUtils.getBaseErrorData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        al_show.get(mPosition).getGoodsEn().getAttrEn().setBuyNum(buyNumber);
                        updateListData();
                        loadCartGoodsNum();
                    } else if (baseEn.getErrNo() == AppConfig.ERROR_CODE_TIMEOUT) {
                        handleTimeOut();
                        finish();
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_CART_UPDATE_ATTR:
                case AppConfig.REQUEST_SV_CART_DELETE:
                    baseEn = JsonUtils.getBaseErrorData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        updateAllData();
                    } else if (baseEn.getErrNo() == AppConfig.ERROR_CODE_TIMEOUT) {
                        handleTimeOut();
                        finish();
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_CART_CHECKED:
                    baseEn = JsonUtils.getBaseErrorData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        openActivity(PostOrderActivity.class);
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
        refresh_rv.onPullUpRefreshComplete();
        refresh_rv.onPullDownRefreshComplete();
    }

}
