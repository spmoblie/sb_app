package com.songbao.sampo.activity.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.songbao.sampo.AppApplication;
import com.songbao.sampo.AppConfig;
import com.songbao.sampo.R;
import com.songbao.sampo.activity.BaseFragment;
import com.songbao.sampo.activity.common.MyWebViewActivity;
import com.songbao.sampo.adapter.AdapterCallback;
import com.songbao.sampo.adapter.ThemeListAdapter;
import com.songbao.sampo.entity.BaseEntity;
import com.songbao.sampo.entity.ShareEntity;
import com.songbao.sampo.entity.ThemeEntity;
import com.songbao.sampo.utils.CommonTools;
import com.songbao.sampo.utils.ExceptionUtil;
import com.songbao.sampo.utils.FileManager;
import com.songbao.sampo.utils.JsonUtils;
import com.songbao.sampo.utils.LogUtil;
import com.songbao.sampo.utils.retrofit.HttpRequests;
import com.songbao.sampo.widgets.RoundImageView;
import com.songbao.sampo.widgets.ViewPagerScroller;
import com.songbao.sampo.widgets.pullrefresh.PullToRefreshBase;
import com.songbao.sampo.widgets.pullrefresh.PullToRefreshRecyclerView;
import com.songbao.sampo.widgets.recycler.MyRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


@SuppressLint("UseSparseArrays")
public class ChildFragmentHome extends BaseFragment implements OnClickListener {

    String TAG = ChildFragmentHome.class.getSimpleName();

    @BindView(R.id.fg_home_refresh_lv)
    PullToRefreshRecyclerView refresh_lv;

    MyRecyclerView mRecyclerView;
    ViewPager fg_home_vp;
    ImageView iv_buy, iv_reserve;
    LinearLayout ll_head_main, fg_home_indicator;

    private Context mContext;
    private Runnable mPagerAction;
    private AdapterCallback apCallback;
    private ThemeListAdapter lv_Adapter;
    private LinearLayout.LayoutParams bannerLP;
    private LinearLayout.LayoutParams indicatorsLP;

    private boolean vprStop = false;
    private boolean isLoadHead = true;
    private int idsSize, idsPosition, vprPosition;
    private int data_total = -1; //数据总量
    private int load_page = 1; //加载页数
    private int load_type = 1; //加载类型(0:下拉刷新/1:翻页加载)
    private boolean isLoadOk = true; //加载控制
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

        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onCreate");
        mContext = getActivity();

        // 动态调整宽高
        int ind_margin = CommonTools.dpToPx(mContext, 5);
        indicatorsLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        indicatorsLP.setMargins(ind_margin, 0, 0, 0);

        int ban_margin = CommonTools.dpToPx(mContext, 15 * 2);
        int ban_widths = AppApplication.screen_width - ban_margin;
        bannerLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bannerLP.width = ban_widths;
        bannerLP.height = ban_widths * AppConfig.IMG_HEIGHT / AppConfig.IMG_WIDTHS;

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
        initListView();
        loadDBData();
    }

    private void initListView() {
        refresh_lv.setPullRefreshEnabled(true); //下拉刷新
        refresh_lv.setPullLoadEnabled(true); //上拉加载
        refresh_lv.setScrollLoadEnabled(false); //加载更多
        refresh_lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<MyRecyclerView>() {
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
                if (!isStopLoadMore(al_show.size(), data_total, 0)) {
                    loadMoreData();
                } else {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            refresh_lv.onPullUpRefreshComplete();
                            refresh_lv.setHasMoreData(false);
                        }
                    }, AppConfig.LOADING_TIME);
                }
            }
        });

        // 创建布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        // 设置布局管理器
        mRecyclerView = refresh_lv.getRefreshableView();
        mRecyclerView.setLayoutManager(layoutManager);

        // 创建适配器
        apCallback = new AdapterCallback() {

            @Override
            public void setOnClick(Object data, int position, int type) {
                if (position < 0 || position >= al_show.size()) return;
                ThemeEntity themeEn = al_show.get(position);
                if (themeEn != null) {
                    switch (type) {
                        case 0:
                            if (themeEn.getThemeType() == AppConfig.THEME_TYPE_1) {
                                openReserveDetailActivity(themeEn);
                            } else {
                                openSignUpDetailActivity(themeEn);
                            }
                            break;
                        case 1:
                            if (themeEn.getThemeType() == AppConfig.THEME_TYPE_1) {
                                openReserveDetailActivity(themeEn);
                            } else {
                                openSignUpActivity(themeEn);
                            }
                            break;
                    }
                } else {
                    CommonTools.showToast(getString(R.string.toast_error_data_app));
                }
            }
        };
        lv_Adapter = new ThemeListAdapter(mContext, al_show, apCallback);

        // 添加头部View
        ll_head_main = (LinearLayout) FrameLayout.inflate(mContext, R.layout.layout_list_head_home, null);
        iv_buy = ll_head_main.findViewById(R.id.fg_home_head_iv_buy);
        iv_reserve = ll_head_main.findViewById(R.id.fg_home_head_iv_reserve);
        fg_home_vp = ll_head_main.findViewById(R.id.fg_home_head_viewPager);
        fg_home_indicator = ll_head_main.findViewById(R.id.fg_home_head_indicator);

        iv_buy.setOnClickListener(this);
        iv_reserve.setOnClickListener(this);

        ll_head_main.setVisibility(View.GONE);
        lv_Adapter.setHeaderView(ll_head_main);

        // 配置适配器
        mRecyclerView.setAdapter(lv_Adapter);
    }

    private void initHeadView() {
        if (ll_head_main.getVisibility() == View.GONE) {
            ll_head_main.setVisibility(View.VISIBLE);
        }
        initViewPager();
    }

    private void initViewPager() {
        if (fg_home_vp == null) return;
        if (al_head.size() > 0) {
            clearViewPagerData();
            idsSize = al_head.size();
            indicators = new ImageView[idsSize]; // 定义指示器数组大小
            if (idsSize == 2 || idsSize == 3) {
                al_head.addAll(al_head);
            }
            for (int i = 0; i < al_head.size(); i++) {
                final ThemeEntity items = al_head.get(i);

                RoundImageView iv_show = new RoundImageView(mContext);
                iv_show.setScaleType(ImageView.ScaleType.CENTER_CROP);

                String imgUrl = items.getPicUrl();
                Glide.with(AppApplication.getAppContext())
                        .load(imgUrl)
                        .apply(AppApplication.getShowOptions())
                        .into(iv_show);

                iv_show.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        openWebviewActivity(items);
                    }
                });
                viewLists.add(iv_show);

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
            fg_home_vp.setLayoutParams(bannerLP);
            /*fg_home_vp.setPageTransformer(true, new ViewPager.PageTransformer() {

                @Override
                public void transformPage(@NonNull View page, float position) {
                    int width = page.getWidth();
                    int pivotX = 0;
                    if (position <= 1 && position > 0) {// right scrolling
                        pivotX = 0;
                    } else if (position == 0) {

                    } else if (position < 0 && position >= -1) {// left scrolling
                        pivotX = width;
                    }
                    //设置x轴的锚点
                    page.setPivotX(pivotX);
                    //设置绕Y轴旋转的角度
                    page.setRotationY(90f * position);
                }
            });*/
            fg_home_vp.setAdapter(new PagerAdapter() {

                // 创建
                @NonNull
                @Override
                public Object instantiateItem(@NonNull ViewGroup container, int position) {
                    if (viewLists.size() <= 0) return null;
                    try {
                        View layout;
                        if (loop) {
                            layout = viewLists.get(position % viewLists.size());
                        } else {
                            layout = viewLists.get(position);
                        }
                        container.addView(layout);
                        return layout;
                    } catch (Exception e) {
                        ExceptionUtil.handle(e);
                        return null;
                    }
                }

                // 销毁
                @Override
                public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                    if (viewLists.size() <= 0) return;
                    try {
                        View layout;
                        if (loop) {
                            layout = viewLists.get(position % viewLists.size());
                        } else {
                            layout = viewLists.get(position);
                        }
                        container.removeView(layout);
                    } catch (Exception e) {
                        ExceptionUtil.handle(e);
                    }
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
                            fg_home_vp.postDelayed(mPagerAction, 5000);
                        }
                    };
                }
                fg_home_vp.post(mPagerAction);

                ViewPagerScroller scroller = new ViewPagerScroller(mContext);
                scroller.initViewPagerScroll(fg_home_vp);
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
            lv_Adapter.updateData(al_show);
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
        intent.putExtra(AppConfig.PAGE_DATA, shareEn);
        intent.putExtra("title", data.getTitle());
        intent.putExtra("lodUrl", data.getLinkUrl());
        startActivity(intent);
    }

    /**
     * 跳转至预约详情页面
     * @param data
     */
    private void openReserveDetailActivity(ThemeEntity data) {
        if (data == null) return;
        Intent intent = new Intent(getActivity(), ReserveDetailActivity.class);
        intent.putExtra(AppConfig.PAGE_DATA, data);
        startActivity(intent);
    }

    /**
     * 跳转至活动详情页面
     * @param data
     */
    private void openSignUpDetailActivity(ThemeEntity data) {
        if (data == null) return;
        Intent intent = new Intent(getActivity(), SignUpDetailActivity.class);
        intent.putExtra(AppConfig.PAGE_DATA, data);
        startActivity(intent);
    }

    /**
     * 跳转至报名页面
     * @param data
     */
    private void openSignUpActivity(ThemeEntity data) {
        if (data == null) return;
        Intent intent = new Intent(getActivity(), SignUpActivity.class);
        intent.putExtra(AppConfig.PAGE_DATA, data);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fg_home_head_iv_buy:
                CommonTools.showToast(getString(R.string.toast_no_open));
                //startActivity(new Intent(getActivity(), TicketsActivity.class));
                break;
            case R.id.fg_home_head_iv_reserve:
                startActivity(new Intent(getActivity(), ReserveListActivity.class));
                break;
        }
    }

    @Override
    public void onResume() {
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
        // 页面开始
        AppApplication.onPageStart(TAG);
        super.onResume();
    }

    @Override
    public void onPause() {
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onPause");
        // 页面结束
        AppApplication.onPageEnd(getActivity(), TAG);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onDestroy");
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
                load_page = 1;
                isLoadHead = true;
                loadMoreData();
            }
        }, AppConfig.LOADING_TIME);
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
     * 加载列表翻页数据
     */
    private void loadServerData() {
        if (!isLoadOk) return; //加载频率控制
        isLoadOk = false;
        if (isLoadHead) { //加载头部数据
            loadHeadData();
        }
        String page = String.valueOf(load_page);
        if (load_type == 0) {
            page = "1";
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("page", page);
        map.put("size", AppConfig.LOAD_SIZE);
        loadSVData(AppConfig.URL_HOME_LIST, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_HOME_LIST);
    }

    /**
     * 加载头部展示数据
     */
    private void loadHeadData() {
        HashMap<String, String> map = new HashMap<>();
        loadSVData(AppConfig.URL_HOME_BANNER, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_HOME_HEAD);
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        BaseEntity baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_HOME_HEAD:
                    baseEn = JsonUtils.getHomeHead(jsonObject);
                    if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
                        List<ThemeEntity> lists = baseEn.getLists();
                        if (lists != null && lists.size() > 0) {
                            ThemeEntity headEn = new ThemeEntity();
                            headEn.setHeadLists(lists);
                            al_head.clear();
                            al_head.addAll(lists);
                            initHeadView();
                            isLoadHead = false;
                            FileManager.writeFileSaveObject(AppConfig.homeHeadFileName, headEn, 1);
                        } else {
                            loadFailHandle();
                            LogUtil.i(LogUtil.LOG_HTTP, TAG + " Head数据加载失败 —> null or size is 0");
                        }
                    } else {
                        loadFailHandle();
                        LogUtil.i(LogUtil.LOG_HTTP, TAG + " Head数据加载失败 —> " + baseEn.getErrmsg());
                    }
                    break;
                case AppConfig.REQUEST_SV_HOME_LIST:
                    baseEn = JsonUtils.getHomeList(jsonObject);
                    if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
                        data_total = baseEn.getDataTotal();
                        if (load_page == 1) {
                            al_show.clear();
                            am_show.clear();
                        }
                        List<ThemeEntity> lists = filterData(baseEn.getLists(), am_show);
                        if (lists != null && lists.size() > 0) {
                            if (load_type == 0) {
                                //下拉
                                LogUtil.i(LogUtil.LOG_HTTP, TAG + " 刷新数据 —> size = " + lists.size());
                                lists.addAll(al_show);
                                al_show.clear();
                            }else {
                                //翻页
                                LogUtil.i(LogUtil.LOG_HTTP, TAG + " 翻页数据 —> page = " + load_page);
                                if (load_page == 1) { //缓存第1页数据
                                    ThemeEntity listEn = new ThemeEntity();
                                    listEn.setMainLists(lists);
                                    FileManager.writeFileSaveObject(AppConfig.homeListFileName, listEn, 1);
                                }
                                load_page++;
                            }
                            al_show.addAll(lists);
                        }
                    } else {
                        loadFailHandle();
                        LogUtil.i(LogUtil.LOG_HTTP, TAG + " 加载失败 —> page = " + load_page + " error msg = " + baseEn.getErrmsg());
                    }
                    updateListData();
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
    }

    /**
     * 显示缓冲动画
     */
    @Override
    protected void startAnimation() {
        super.startAnimation();
    }

    /**
     * 停止缓冲动画
     */
    @Override
    protected void stopAnimation() {
        super.stopAnimation();
        isLoadOk = true;
        refresh_lv.onPullUpRefreshComplete();
        refresh_lv.onPullDownRefreshComplete();
    }

    /**
     * 加载本地缓存数据
     */
    private void loadDBData() {
        Observable.create(new Observable.OnSubscribe<ThemeEntity>() {
            @Override
            public void call(Subscriber<? super ThemeEntity> subscriber) {
                // Head
                ThemeEntity baseEn = (ThemeEntity) FileManager.readFileSaveObject(AppConfig.homeHeadFileName, 1);
                if (baseEn == null) {
                    baseEn = new ThemeEntity();
                }
                // List
                ThemeEntity listEn = (ThemeEntity) FileManager.readFileSaveObject(AppConfig.homeListFileName, 1);
                if (listEn != null) {
                    baseEn.setMainLists(listEn.getMainLists());
                }
                subscriber.onNext(baseEn);
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ThemeEntity>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        initData(null);
                    }

                    @Override
                    public void onNext(ThemeEntity baseEn) {
                        initData(baseEn);
                    }
                });
    }

    /**
     * 初始化缓存数据
     */
    private void initData(ThemeEntity baseEn) {
        ThemeEntity demoData = getDemoData();
        if (baseEn == null) {
            baseEn = demoData;
        } else {
            if (baseEn.getHeadLists() == null) {
                baseEn.setHeadLists(demoData.getHeadLists());
            }
            if (baseEn.getMainLists() == null) {
                baseEn.setMainLists(demoData.getMainLists());
            }
        }
        al_head.addAll(baseEn.getHeadLists());
        al_show.addAll(baseEn.getMainLists());

        if (al_show.size() > 0) {
            initHeadView();
            updateListData();
            loadMoreData();
        } else {
            resetData();
        }
    }

    /**
     * 构建Demo数据
     */
    private ThemeEntity getDemoData() {
        ThemeEntity baseEn = new ThemeEntity();
        ThemeEntity chEn_1 = new ThemeEntity();
        ThemeEntity chEn_2 = new ThemeEntity();
        ThemeEntity chEn_3 = new ThemeEntity();
        ThemeEntity chEn_4 = new ThemeEntity();
        ThemeEntity chEn_5 = new ThemeEntity();
        List<ThemeEntity> headLists = new ArrayList<>();

        chEn_1.setPicUrl(AppConfig.IMAGE_URL+ "banner_001.png");
        chEn_1.setTitle("松小堡绘画设计大赛");
        chEn_1.setLinkUrl("https://mp.weixin.qq.com/s/uhg0hWDZCvtkyFQUs5FguQ");
        headLists.add(chEn_1);
        chEn_2.setPicUrl(AppConfig.IMAGE_URL+ "banner_002.png");
        chEn_2.setTitle("松堡王国儿童房间，你值得拥有！");
        chEn_2.setLinkUrl("https://mp.weixin.qq.com/s/uhg0hWDZCvtkyFQUs5FguQ");
        headLists.add(chEn_2);
        chEn_3.setPicUrl(AppConfig.IMAGE_URL+ "banner_003.png");
        chEn_3.setTitle("现场直击 |松堡王国2019深圳家具展，给你好看");
        chEn_3.setLinkUrl("https://mp.weixin.qq.com/s/OgWdS8oSZZlSWZWRgeONow");
        headLists.add(chEn_3);
        chEn_4.setPicUrl(AppConfig.IMAGE_URL+ "banner_004.png");
        chEn_4.setTitle("松堡王国来博白了，尽情上演属于自己的公主王子梦⋯⋯快来耍");
        chEn_4.setLinkUrl("https://mp.weixin.qq.com/s/iasaC_yR8_SxvwKstEfnKg");
        headLists.add(chEn_4);
        chEn_5.setPicUrl(AppConfig.IMAGE_URL+ "banner_005.png");
        chEn_5.setTitle("厉害了！我的松堡王国");
        chEn_5.setLinkUrl("https://mp.weixin.qq.com/s/1YJ_sqhekFTTS23G9ZCfiA");
        headLists.add(chEn_5);

        baseEn.setHeadLists(headLists);

        ThemeEntity isEn_1 = new ThemeEntity();
        ThemeEntity isEn_2 = new ThemeEntity();
        ThemeEntity isEn_3 = new ThemeEntity();
        ThemeEntity isEn_4 = new ThemeEntity();
        ThemeEntity isEn_5 = new ThemeEntity();
        List<ThemeEntity> mainLists = new ArrayList<>();

        isEn_1.setPicUrl(AppConfig.IMAGE_URL+ "items_001.png");
        isEn_1.setLinkUrl("https://mp.weixin.qq.com/s/tMi8j08jb7oEHKtmYqdl0g");
        isEn_1.setTitle("北欧教育 | 比NOKIA更震惊世界的芬兰品牌");
        isEn_1.setUserName("松堡王国设计部");
        isEn_1.setSeries("美学培养系列");
        isEn_1.setAddTime("2019-10-18 10:30");
        isEn_1.setThemeType(1);
        mainLists.add(isEn_1);
        isEn_2.setPicUrl(AppConfig.IMAGE_URL+ "items_002.png");
        isEn_2.setLinkUrl("https://mp.weixin.qq.com/s/p1j-Mv0yAW45tkVvjqLBTA");
        isEn_2.setTitle("全球都在追捧的北欧教育，到底有哪些秘密？");
        isEn_2.setUserName("松小堡线下运营");
        isEn_2.setSeries("益智系列");
        isEn_2.setAddTime("2019-10-10 15:30");
        isEn_2.setThemeType(1);
        mainLists.add(isEn_2);
        isEn_3.setPicUrl(AppConfig.IMAGE_URL+ "items_003.png");
        isEn_3.setLinkUrl("https://mp.weixin.qq.com/s/Ln0z3fqwBxT9dUP_dJL1uQ");
        isEn_3.setTitle("上海妈妈在挪威，享受北欧式教育的幸福");
        isEn_3.setUserName("安安和全全");
        isEn_3.setSeries("手工积木系列");
        isEn_3.setAddTime("2019-10-08 10:30");
        isEn_3.setThemeType(2);
        mainLists.add(isEn_3);
        isEn_4.setPicUrl(AppConfig.IMAGE_URL+ "items_004.png");
        isEn_4.setLinkUrl("https://mp.weixin.qq.com/s/7wPFWTCMn850gxgGqaOchw");
        isEn_4.setTitle("芬兰：北欧小国的大教育观");
        isEn_4.setUserName("Sampo");
        isEn_4.setSeries("益智系列");
        isEn_4.setAddTime("2019-09-26 14:00");
        isEn_4.setThemeType(1);
        mainLists.add(isEn_4);
        isEn_5.setPicUrl(AppConfig.IMAGE_URL+ "items_005.png");
        isEn_5.setLinkUrl("http://www.sohu.com/a/195309958_100007192");
        isEn_5.setTitle("走进北欧教育——每个孩子都是独一无二的天使");
        isEn_5.setUserName("松堡王国设计部");
        isEn_5.setSeries("动手动脑系列");
        isEn_5.setAddTime("2019-09-18 13:30");
        isEn_5.setThemeType(2);
        mainLists.add(isEn_5);

        baseEn.setMainLists(mainLists);

        return baseEn;
    }

}