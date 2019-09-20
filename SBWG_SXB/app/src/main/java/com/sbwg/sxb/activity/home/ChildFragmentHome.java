package com.sbwg.sxb.activity.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppConfig;
import com.sbwg.sxb.R;
import com.sbwg.sxb.activity.BaseFragment;
import com.sbwg.sxb.activity.common.MyWebViewActivity;
import com.sbwg.sxb.adapter.AdapterCallback;
import com.sbwg.sxb.adapter.HomeListAdapter;
import com.sbwg.sxb.entity.BaseEntity;
import com.sbwg.sxb.entity.ShareEntity;
import com.sbwg.sxb.entity.ThemeEntity;
import com.sbwg.sxb.utils.CommonTools;
import com.sbwg.sxb.utils.ExceptionUtil;
import com.sbwg.sxb.utils.FileManager;
import com.sbwg.sxb.utils.JsonUtils;
import com.sbwg.sxb.utils.LogUtil;
import com.sbwg.sxb.utils.retrofit.HttpRequests;
import com.sbwg.sxb.widgets.pullrefresh.PullToRefreshBase;
import com.sbwg.sxb.widgets.pullrefresh.PullToRefreshListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


@SuppressLint("UseSparseArrays")
public class ChildFragmentHome extends BaseFragment implements OnClickListener {

    private static final String TAG = "ChildFragmentHome";

    @BindView(R.id.fg_home_refresh_lv)
    PullToRefreshListView refresh_lv;

    @BindView(R.id.loading_fail_main)
    ConstraintLayout rl_load_fail;

    @BindView(R.id.loading_fail_tv_update)
    TextView tv_load_again;

    private ListView mListView;
    private ViewPager fg_home_vp;
    private LinearLayout ll_head_main, fg_home_indicator;

    private Context mContext;
    private Runnable mPagerAction;
    private AdapterCallback apCallback;
    private HomeListAdapter lv_Adapter;
    private FrameLayout.LayoutParams brandLP;
    private LinearLayout.LayoutParams indicatorsLP;

    private boolean vprStop = false;
    private boolean isUpdateHead = true;
    private int data_total = 0; //数据总量
    private int current_Page = 1;  //当前列表加载页
    private int idsSize, idsPosition, vprPosition;
    private ThemeEntity headEn;
    private ImageView[] indicators = null;
    private ArrayList<ImageView> viewLists = new ArrayList<>();
    private ArrayList<ThemeEntity> al_head = new ArrayList<>();
    private ArrayList<ThemeEntity> al_show = new ArrayList<>();
    private ArrayMap<String, Boolean> am_show = new ArrayMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 与Activity不一样
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LogUtil.i(TAG, "onCreate");
        mContext = getActivity();

        // 动态调整宽高
        indicatorsLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        indicatorsLP.setMargins(10, 0, 10, 0);

        int screenWidth = AppApplication.getSharedPreferences().getInt(AppConfig.KEY_SCREEN_WIDTH, 0);
        brandLP = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        brandLP.width = screenWidth;
        brandLP.height = screenWidth / 2;

        View view = null;
        try {
            view = inflater.inflate(R.layout.fragment_layout_home, null);
            //Butter Knife初始化
            ButterKnife.bind(this, view);

            initView();
        } catch (Exception e) {
            ExceptionUtil.handle(e);
        }
        return view;
    }

    private void initView() {
        tv_load_again.setOnClickListener(this);

        loadDBData();

        initListView();
    }

    private void initListView() {
        refresh_lv.setPullRefreshEnabled(true); //下拉刷新
        refresh_lv.setPullLoadEnabled(true); //上拉加载
        refresh_lv.setScrollLoadEnabled(false); //底部翻页
        refresh_lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉刷新
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        refresh_lv.onPullDownRefreshComplete();
                    }
                }, AppConfig.LOADING_TIME);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 加载更多
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (!isStopLoadMore(al_show.size(), data_total, 0)) {
                            loadListData();
                        } else {
                            refresh_lv.onPullUpRefreshComplete();
                            refresh_lv.setHasMoreData(false);
                        }
                    }
                }, AppConfig.LOADING_TIME);
            }
        });
        mListView = refresh_lv.getRefreshableView();
        mListView.setDivider(null);
        mListView.setVerticalScrollBarEnabled(false);

        // 配置适配器
        apCallback = new AdapterCallback() {

            @Override
            public void setOnClick(Object entity, int position, int type) {
                ThemeEntity data = al_show.get(position);
                if (data != null) {
                    switch (type) {
                        case 0:
                            openWebviewActivity(data);
                            break;
                        case 1: //报名
                            openSignUpActivity(data);
                            break;
                    }
                } else {
                    CommonTools.showToast(getString(R.string.toast_error_data_null));
                }
            }
        };
        lv_Adapter = new HomeListAdapter(mContext, al_show, apCallback);
        mListView.setAdapter(lv_Adapter);

        // 添加头部View
        ll_head_main = (LinearLayout) FrameLayout.inflate(mContext, R.layout.layout_list_head_home, null);
        fg_home_vp = ll_head_main.findViewById(R.id.fg_home_head_viewPager);
        fg_home_indicator = ll_head_main.findViewById(R.id.fg_home_head_indicator);
        ll_head_main.setVisibility(View.GONE);
        mListView.addHeaderView(ll_head_main);
    }

    private void initHeadView() {
        if (ll_head_main.getVisibility() == View.GONE) {
            ll_head_main.setVisibility(View.VISIBLE);
        }
        initViewPager();
    }

    private void initViewPager() {
        if (fg_home_vp == null) return;
        if (headEn != null) {
            al_head.clear();
            al_head.addAll(headEn.getHeadLists());
        }
        if (al_head.size() > 0) {
            clearViewPagerData();
            idsSize = al_head.size();
            indicators = new ImageView[idsSize]; // 定义指示器数组大小
            if (idsSize == 2 || idsSize == 3) {
                al_head.addAll(al_head);
            }
            for (int i = 0; i < al_head.size(); i++) {
                final ThemeEntity items = al_head.get(i);
                ImageView imageView = new ImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                String imgUrl = items.getPicUrl();
                Glide.with(AppApplication.getAppContext())
                        .load(imgUrl)
                        .apply(AppApplication.getShowOptions())
                        .into(imageView);

                imageView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        openWebviewActivity(items);
                    }
                });
                viewLists.add(imageView);

                if (i < idsSize) {
                    // 循环加入指示器
                    indicators[i] = new ImageView(mContext);
                    indicators[i].setLayoutParams(indicatorsLP);
                    indicators[i].setImageResource(R.mipmap.indicators_default);
                    if (i == 0) {
                        indicators[i].setImageResource(R.mipmap.indicators_now);
                    }
                    fg_home_indicator.addView(indicators[i]);
                }
            }
            final boolean loop = viewLists.size() > 3 ? true : false;
            fg_home_vp.setLayoutParams(brandLP);
            fg_home_vp.setAdapter(new PagerAdapter() {

                // 创建
                @NonNull
                @Override
                public Object instantiateItem(@NonNull ViewGroup container, int position) {
                    if (fg_home_vp == null || viewLists.size() <= 1) return null;
                    View layout;
                    if (loop) {
                        layout = viewLists.get(position % viewLists.size());
                    } else {
                        layout = viewLists.get(position);
                    }
                    fg_home_vp.addView(layout);
                    return layout;
                }

                // 销毁
                @Override
                public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                    if (fg_home_vp == null || viewLists.size() <= 1) return;
                    View layout;
                    if (loop) {
                        layout = viewLists.get(position % viewLists.size());
                    } else {
                        layout = viewLists.get(position);
                    }
                    fg_home_vp.removeView(layout);
                }

                @Override
                public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                    return view == object;
                }

                @Override
                public int getCount() {
                    if (loop) {
                        return Integer.MAX_VALUE;
                    } else {
                        return viewLists.size();
                    }
                }
            });
            fg_home_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(final int arg0) {
                    if (fg_home_vp == null || viewLists.size() <= 1) return;
                    if (loop) {
                        vprPosition = arg0;
                        idsPosition = arg0 % viewLists.size();
                        if (idsPosition == viewLists.size()) {
                            idsPosition = 0;
                            fg_home_vp.setCurrentItem(0);
                        }
                    } else {
                        idsPosition = arg0;
                    }
                    // 更改指示器图片
                    if ((idsSize == 2 || idsSize == 3) && idsPosition >= idsSize) {
                        idsPosition = idsPosition - idsSize;
                    }
                    for (int i = 0; i < idsSize; i++) {
                        ImageView imageView = indicators[i];
                        if (i == idsPosition)
                            imageView.setImageResource(R.mipmap.indicators_now);
                        else
                            imageView.setImageResource(R.mipmap.indicators_default);
                    }
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {

                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                    if (arg0 == 1) {
                        vprStop = true;
                    }
                }
            });
            if (loop) {
                fg_home_vp.setCurrentItem(viewLists.size() * 10);
                if (mPagerAction == null) {
                    mPagerAction = new Runnable() {

                        @Override
                        public void run() {
                            if (fg_home_vp == null || viewLists.size() <= 1) return;
                            if (!vprStop) {
                                vprPosition++;
                                if (fg_home_vp != null) {
                                    fg_home_vp.setCurrentItem(vprPosition);
                                }
                            }
                            vprStop = false;
                            fg_home_vp.postDelayed(mPagerAction, 3000);
                        }
                    };
                }
                fg_home_vp.post(mPagerAction);
            }
        }
    }

    private void clearViewPagerData() {
        if (viewLists.size() > 0) {
            viewLists.clear();
        }
        if (fg_home_vp != null) {
            fg_home_vp.removeAllViews();
            fg_home_vp.removeCallbacks(mPagerAction);
        }
        if (fg_home_indicator != null) {
            fg_home_indicator.removeAllViews();
        }
    }

    private void updateListData() {
        if (lv_Adapter != null) {
            lv_Adapter.updateAdapter(al_show);
        }
    }

    // 跳转至WebView
    private void openWebviewActivity(ThemeEntity data) {
        if (data == null) return;
        // 创建分享数据
        ShareEntity shareEn = new ShareEntity();
        shareEn.setTitle(data.getTitle());
        shareEn.setText(data.getTitle());
        shareEn.setImageUrl(data.getPicUrl());
        shareEn.setUrl(data.getLinkUrl());
        // 跳转至WebView
        Intent intent = new Intent(getActivity(), MyWebViewActivity.class);
        intent.putExtra("shareEn", shareEn);
        intent.putExtra("title", data.getTitle());
        intent.putExtra("lodUrl", data.getLinkUrl());
        startActivity(intent);
    }

    // 跳转至报名页面
    private void openSignUpActivity(ThemeEntity data) {
        if (data == null) return;
        Intent intent = new Intent(getActivity(), SignUpActivity.class);
        intent.putExtra("data", data);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loading_fail_tv_update: //重新加载
                resetData();
                break;
        }
    }

    @Override
    public void onResume() {
        LogUtil.i(TAG, "onResume");
        // 页面开始
        AppApplication.onPageStart(TAG);
        super.onResume();
    }

    @Override
    public void onPause() {
        LogUtil.i(TAG, "onPause");
        // 页面结束
        AppApplication.onPageEnd(getActivity(), TAG);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        LogUtil.i(TAG, "onDestroy");
        // 清除轮播数据
        clearViewPagerData();
        super.onDestroy();
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (this.getView() != null)
            this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
    }

    /**
     * 重置数据
     */
    private void resetData() {
        startAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                current_Page = 1;
                isUpdateHead = true;
                loadListData();
            }
        }, AppConfig.LOADING_TIME);
    }

    /**
     * 加载列表翻页数据
     */
    private void loadListData() {
        if (isUpdateHead) { //加载头部数据
            loadHeadData();
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("page", String.valueOf(current_Page));
        map.put("size", "3");
        loadSVData(AppConfig.URL_HOME_LIST, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_POST_HOME_LIST);
    }

    /**
     * 加载头部展示数据
     */
    private void loadHeadData() {
        HashMap<String, String> map = new HashMap<>();
        loadSVData(AppConfig.URL_HOME_BANNER, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_POST_HOME_HEAD);
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        BaseEntity baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_POST_HOME_HEAD:
                    baseEn = JsonUtils.getHomeHead(jsonObject);
                    headEn = new ThemeEntity();
                    headEn.setHeadLists(baseEn.getLists());
                    initHeadView();
                    isUpdateHead = false;
                    FileManager.writeFileSaveObject(AppConfig.homeHeadFileName, headEn, true);
                    break;
                case AppConfig.REQUEST_SV_POST_HOME_LIST:
                    baseEn = JsonUtils.getHomeList(jsonObject);
                    data_total = baseEn.getDataTotal(); //加载更多数据控制符
                    List<ThemeEntity> lists = baseEn.getLists();
                    if (lists != null && lists.size() > 0) {
                        if (current_Page == 1) { //缓存第1页数据
                            al_show.clear();
                            ThemeEntity listEn = new ThemeEntity();
                            listEn.setMainLists(lists);
                            FileManager.writeFileSaveObject(AppConfig.homeListFileName, listEn, true);
                        }
                        List<BaseEntity> newLists = addNewEntity(al_show, lists, am_show);
                        if (newLists != null) {
                            addNewShowLists(newLists);
                            current_Page++;
                        }
                        updateListData();
                        LogUtil.i("Retrofit", TAG + " List数据加载成功 —> " + current_Page);
                    } else {
                        loadFailHandle();
                        LogUtil.i("Retrofit", TAG + " List数据加载失败 —> " + current_Page);
                    }
                    break;
            }
        } catch (Exception e) {
            loadFailHandle();
            ExceptionUtil.handle(e);
        }
    }

    private void addNewShowLists(List<BaseEntity> showLists) {
        al_show.clear();
        for (int i = 0; i < showLists.size(); i++) {
            al_show.add((ThemeEntity) showLists.get(i));
        }
    }

    @Override
    protected void loadFailHandle() {
        super.loadFailHandle();
        if (al_show.size() == 0) {
            ll_head_main.setVisibility(View.GONE);
            rl_load_fail.setVisibility(View.VISIBLE);
        } else {
            if (ll_head_main.getVisibility() == View.GONE) {
                initHeadView();
            }
        }
    }

    /**
     * 显示缓冲动画
     */
    @Override
    protected void startAnimation() {
        super.startAnimation();
        rl_load_fail.setVisibility(View.GONE);
    }

    /**
     * 停止缓冲动画
     */
    @Override
    protected void stopAnimation() {
        super.stopAnimation();
        refresh_lv.onPullUpRefreshComplete();
    }

    /**
     * 加载本地缓存数据
     */
    private void loadDBData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Object headObj = FileManager.readFileSaveObject(AppConfig.homeHeadFileName, true);
                if (headObj != null) {
                    headEn = (ThemeEntity) headObj;
                }
                Object listObj = FileManager.readFileSaveObject(AppConfig.homeListFileName, true);
                if (listObj != null) {
                    ThemeEntity listEn = (ThemeEntity) listObj;
                    al_show.addAll(listEn.getMainLists());
                }
                mHandler.sendEmptyMessage(1);
            }
        }).start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message mMsg) {
            switch (mMsg.what) {
                case 1:
                    if (al_show.size() > 0) {
                        initHeadView();
                        updateListData();
                        loadListData();
                    } else {
                        resetData();
                    }
                    break;
            }
        }
    };

    private ThemeEntity initData() {
        ThemeEntity bannerEn = new ThemeEntity();
        ThemeEntity chEn_1 = new ThemeEntity();
        ThemeEntity chEn_2 = new ThemeEntity();
        ThemeEntity chEn_3 = new ThemeEntity();
        ThemeEntity chEn_4 = new ThemeEntity();
        ThemeEntity chEn_5 = new ThemeEntity();
        List<ThemeEntity> mainLists = new ArrayList<>();

        chEn_1.setPicUrl("banner_001.png");
        chEn_1.setTitle("松小堡绘画设计大赛");
        chEn_1.setLinkUrl("https://mp.weixin.qq.com/s/uhg0hWDZCvtkyFQUs5FguQ");
        mainLists.add(chEn_1);
        chEn_2.setPicUrl("banner_002.jpg");
        chEn_2.setTitle("松堡王国儿童房间，你值得拥有！");
        chEn_2.setLinkUrl("https://mp.weixin.qq.com/s/uhg0hWDZCvtkyFQUs5FguQ");
        mainLists.add(chEn_2);
        chEn_3.setPicUrl("banner_003.png");
        chEn_3.setTitle("现场直击 |松堡王国2019深圳家具展，给你好看");
        chEn_3.setLinkUrl("https://mp.weixin.qq.com/s/OgWdS8oSZZlSWZWRgeONow");
        mainLists.add(chEn_3);
        chEn_4.setPicUrl("banner_004.png");
        chEn_4.setTitle("松堡王国来博白了，尽情上演属于自己的公主王子梦⋯⋯快来耍");
        chEn_4.setLinkUrl("https://mp.weixin.qq.com/s/iasaC_yR8_SxvwKstEfnKg");
        mainLists.add(chEn_4);
        chEn_5.setPicUrl("banner_005.png");
        chEn_5.setTitle("厉害了！我的松堡王国");
        chEn_5.setLinkUrl("https://mp.weixin.qq.com/s/1YJ_sqhekFTTS23G9ZCfiA");
        mainLists.add(chEn_5);

        bannerEn.setHeadLists(mainLists);

        ThemeEntity isEn_1 = new ThemeEntity();
        ThemeEntity isEn_2 = new ThemeEntity();
        ThemeEntity isEn_3 = new ThemeEntity();
        ThemeEntity isEn_4 = new ThemeEntity();
        ThemeEntity isEn_5 = new ThemeEntity();
        List<ThemeEntity> isLists = new ArrayList<>();

        isEn_1.setPicUrl("items_001.jpg");
        isEn_1.setLinkUrl("https://mp.weixin.qq.com/s/tMi8j08jb7oEHKtmYqdl0g");
        isEn_1.setTitle("北欧教育 | 比NOKIA更震惊世界的芬兰品牌");
        isEn_1.setUserHead("head_001.jpg");
        isEn_1.setUserName("北欧教育创新中心");
        isLists.add(isEn_1);
        isEn_2.setPicUrl("items_002.jpg");
        isEn_2.setLinkUrl("https://mp.weixin.qq.com/s/p1j-Mv0yAW45tkVvjqLBTA");
        isEn_2.setTitle("全球都在追捧的北欧教育，到底有哪些秘密？");
        isEn_2.setUserHead("head_002.jpg");
        isEn_2.setUserName("君学海外");
        isLists.add(isEn_2);
        isEn_3.setPicUrl("items_003.jpg");
        isEn_3.setLinkUrl("https://mp.weixin.qq.com/s/Ln0z3fqwBxT9dUP_dJL1uQ");
        isEn_3.setTitle("上海妈妈在挪威，享受北欧式教育的幸福");
        isEn_3.setUserHead("head_003.jpg");
        isEn_3.setUserName("泡爸讲知识");
        isLists.add(isEn_3);
        isEn_4.setPicUrl("items_004.jpg");
        isEn_4.setLinkUrl("https://mp.weixin.qq.com/s/7wPFWTCMn850gxgGqaOchw");
        isEn_4.setTitle("芬兰：北欧小国的大教育观");
        isEn_4.setUserHead("head_004.jpg");
        isEn_4.setUserName("北欧童话奇幻");
        isLists.add(isEn_4);
        isEn_5.setPicUrl("items_005.jpeg");
        isEn_5.setLinkUrl("http://www.sohu.com/a/195309958_100007192");
        isEn_5.setTitle("走进北欧教育——每个孩子都是独一无二的天使");
        isEn_5.setUserHead("head_004.jpg");
        isEn_5.setUserName("北欧童话奇幻");
        isLists.add(isEn_5);

        bannerEn.setMainLists(isLists);

        return bannerEn;
    }

}

