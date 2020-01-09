package com.songbao.sampo_b.activity.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.activity.BaseFragment;
import com.songbao.sampo_b.activity.common.MyWebViewActivity;
import com.songbao.sampo_b.adapter.AdapterCallback;
import com.songbao.sampo_b.adapter.ThemeListAdapter;
import com.songbao.sampo_b.entity.BaseEntity;
import com.songbao.sampo_b.entity.ShareEntity;
import com.songbao.sampo_b.entity.ThemeEntity;
import com.songbao.sampo_b.utils.ClickUtils;
import com.songbao.sampo_b.utils.CommonTools;
import com.songbao.sampo_b.utils.ExceptionUtil;
import com.songbao.sampo_b.utils.FileManager;
import com.songbao.sampo_b.utils.JsonUtils;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.utils.retrofit.HttpRequests;
import com.songbao.sampo_b.widgets.RoundImageView;
import com.songbao.sampo_b.widgets.ViewPagerScroller;
import com.songbao.sampo_b.widgets.pullrefresh.PullToRefreshBase;
import com.songbao.sampo_b.widgets.pullrefresh.PullToRefreshRecyclerView;
import com.songbao.sampo_b.widgets.recycler.MyRecyclerView;

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


public class ChildFragmentHome extends BaseFragment implements OnClickListener {

    String TAG = ChildFragmentHome.class.getSimpleName();

    @BindView(R.id.fg_home_refresh_lv)
    PullToRefreshRecyclerView refresh_lv;

    MyRecyclerView mRecyclerView;
    ViewPager fg_home_vp;
    LinearLayout ll_head_main, vp_indicator;

    private Context mContext;
    private Runnable mPagerAction;
    private ThemeListAdapter lv_Adapter;
    private LinearLayout.LayoutParams bannerLP;
    private LinearLayout.LayoutParams indicatorsLP;

    private boolean isLoop = false;
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
    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_layout_home, null);
        //Butter Knife初始化
        ButterKnife.bind(this, view);

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

        initView();
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
        lv_Adapter = new ThemeListAdapter(mContext, al_show, new AdapterCallback() {

            @Override
            public void setOnClick(Object data, int position, int type) {
                //if (position < 0 || position >= al_show.size()) return;
            }
        });

        // 添加头部View
        ll_head_main = (LinearLayout) FrameLayout.inflate(mContext, R.layout.layout_list_head_home, null);
        fg_home_vp = ll_head_main.findViewById(R.id.fg_home_head_viewPager);
        vp_indicator = ll_head_main.findViewById(R.id.fg_home_head_indicator);

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
            isLoop = false;
            clearViewPagerData();
            ArrayList<ThemeEntity> al_show = new ArrayList<>();
            al_show.addAll(al_head);
            idsSize = al_show.size();
            indicators = new ImageView[idsSize]; // 定义指示器数组大小
            if (idsSize == 2 || idsSize == 3) {
                al_show.addAll(al_head);
            }
            for (int i = 0; i < al_show.size(); i++) {
                final ThemeEntity items = al_show.get(i);

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
                        if (ClickUtils.isDoubleClick()) return;
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
                    vp_indicator.addView(indicators[i]);
                }
            }
            if (viewLists.size() > 3) {
                isLoop = true;
            }
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
                    try {
                        View layout;
                        if (isLoop) {
                            layout = viewLists.get(position % viewLists.size());
                        } else {
                            layout = viewLists.get(position);
                        }
                        container.addView(layout);
                        return layout;
                    } catch (Exception e) {
                        ExceptionUtil.handle(e);
                        return  new ImageView(mContext);
                    }
                }

                // 销毁
                @Override
                public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                    if (viewLists.size() <= 0) return;
                    try {
                        View layout;
                        if (isLoop) {
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
                    if (isLoop) {
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
                    if (isLoop) {
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
            if (isLoop) {
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
        if (vp_indicator != null) {
            vp_indicator.removeAllViews();
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

    @Override
    public void onClick(View v) {

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
        BaseEntity<ThemeEntity> baseEn;
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
        if (baseEn != null) {
            if (baseEn.getHeadLists() != null) {
                al_head.addAll(baseEn.getHeadLists());
            }
            if (baseEn.getMainLists() != null) {
                al_show.addAll(baseEn.getMainLists());
            }
        }

        if (al_show.size() > 0) {
            initHeadView();
            updateListData();
            loadMoreData();
        } else {
            resetData();
        }
    }

}