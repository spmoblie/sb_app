package com.sbwg.sxb.activity.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.sbwg.sxb.utils.JsonUtils;
import com.sbwg.sxb.utils.LogUtil;
import com.sbwg.sxb.utils.retrofit.HttpRequests;
import com.sbwg.sxb.widgets.ScrollViewListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


@SuppressLint("UseSparseArrays")
public class ChildFragmentHome extends BaseFragment implements OnClickListener {

    private static final String TAG = "ChildFragmentHome";
    private static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;

    @BindView(R.id.fg_home_head_viewPager)
    ViewPager fg_home_vp;

    @BindView(R.id.fg_home_head_indicator)
    LinearLayout fg_home_indicator;

    @BindView(R.id.fg_home_items_svlv)
    ScrollViewListView svlv;

    private Context mContext;
    private Runnable mPagerAction;
    private AdapterCallback apCallback;
    private HomeListAdapter lv_Adapter;
    private FrameLayout.LayoutParams brandLP;
    private LinearLayout.LayoutParams indicatorsLP;

    private boolean vprStop = false;
    private int page_goods = 1;
    private int idsSize, idsPosition, vprPosition;
    private ImageView[] indicators = null;
    private ThemeEntity bannerEn = new ThemeEntity();
    private ArrayList<ImageView> viewLists = new ArrayList<>();
    private ArrayList<ThemeEntity> imgEns = new ArrayList<>();
    private ArrayList<ThemeEntity> showLists = new ArrayList<>();

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

            initData();
            loadGoods();
            initView();
        } catch (Exception e) {
            ExceptionUtil.handle(e);
        }
        return view;
    }

    private void initData() {
        ThemeEntity chEn_1 = new ThemeEntity();
        ThemeEntity chEn_2 = new ThemeEntity();
        ThemeEntity chEn_3 = new ThemeEntity();
        ThemeEntity chEn_4 = new ThemeEntity();
        ThemeEntity chEn_5 = new ThemeEntity();
        List<ThemeEntity> mainLists = new ArrayList<ThemeEntity>();

        chEn_1.setImgUrl("banner_001.png");
        chEn_1.setTitle("松小堡绘画设计大赛");
        chEn_1.setLink("https://mp.weixin.qq.com/s/uhg0hWDZCvtkyFQUs5FguQ");
        mainLists.add(chEn_1);
        chEn_2.setImgUrl("banner_002.jpg");
        chEn_2.setTitle("松堡王国儿童房间，你值得拥有！");
        chEn_2.setLink("https://mp.weixin.qq.com/s/uhg0hWDZCvtkyFQUs5FguQ");
        mainLists.add(chEn_2);
        chEn_3.setImgUrl("banner_003.png");
        chEn_3.setTitle("现场直击 |松堡王国2019深圳家具展，给你好看");
        chEn_3.setLink("https://mp.weixin.qq.com/s/OgWdS8oSZZlSWZWRgeONow");
        mainLists.add(chEn_3);
        chEn_4.setImgUrl("banner_004.png");
        chEn_4.setTitle("松堡王国来博白了，尽情上演属于自己的公主王子梦⋯⋯快来耍");
        chEn_4.setLink("https://mp.weixin.qq.com/s/iasaC_yR8_SxvwKstEfnKg");
        mainLists.add(chEn_4);
        chEn_5.setImgUrl("banner_005.png");
        chEn_5.setTitle("厉害了！我的松堡王国");
        chEn_5.setLink("https://mp.weixin.qq.com/s/1YJ_sqhekFTTS23G9ZCfiA");
        mainLists.add(chEn_5);

        bannerEn.setMainLists(mainLists);

        /*itemsEn = new ThemeEntity();
        ThemeEntity isEn_1 = new ThemeEntity();
        ThemeEntity isEn_2 = new ThemeEntity();
        ThemeEntity isEn_3 = new ThemeEntity();
        ThemeEntity isEn_4 = new ThemeEntity();
        ThemeEntity isEn_5 = new ThemeEntity();
        List<ThemeEntity> isLists = new ArrayList<ThemeEntity>();

        isEn_1.setImgUrl("items_001.jpg");
        isEn_1.setLink("https://mp.weixin.qq.com/s/tMi8j08jb7oEHKtmYqdl0g");
        isEn_1.setTitle("北欧教育 | 比NOKIA更震惊世界的芬兰品牌");
        isEn_1.setUserHead("head_001.jpg");
        isEn_1.setUserName("北欧教育创新中心");
        isLists.add(isEn_1);
        isEn_2.setImgUrl("items_002.jpg");
        isEn_2.setLink("https://mp.weixin.qq.com/s/p1j-Mv0yAW45tkVvjqLBTA");
        isEn_2.setTitle("全球都在追捧的北欧教育，到底有哪些秘密？");
        isEn_2.setUserHead("head_002.jpg");
        isEn_2.setUserName("君学海外");
        isLists.add(isEn_2);
        isEn_3.setImgUrl("items_003.jpg");
        isEn_3.setLink("https://mp.weixin.qq.com/s/Ln0z3fqwBxT9dUP_dJL1uQ");
        isEn_3.setTitle("上海妈妈在挪威，享受北欧式教育的幸福");
        isEn_3.setUserHead("head_003.jpg");
        isEn_3.setUserName("泡爸讲知识");
        isLists.add(isEn_3);
        isEn_4.setImgUrl("items_004.jpg");
        isEn_4.setLink("https://mp.weixin.qq.com/s/7wPFWTCMn850gxgGqaOchw");
        isEn_4.setTitle("芬兰：北欧小国的大教育观");
        isEn_4.setUserHead("head_004.jpg");
        isEn_4.setUserName("北欧童话奇幻");
        isLists.add(isEn_4);
        isEn_5.setImgUrl("items_005.jpeg");
        isEn_5.setLink("http://www.sohu.com/a/195309958_100007192");
        isEn_5.setTitle("走进北欧教育——每个孩子都是独一无二的天使");
        isEn_5.setUserHead("head_004.jpg");
        isEn_5.setUserName("北欧童话奇幻");
        isLists.add(isEn_5);

        itemsEn.setMainLists(isLists);*/
    }

    private void initView() {
        initViewPager(bannerEn);
        initItemsView();
    }

    private void initViewPager(ThemeEntity adEn) {
        if (fg_home_vp == null) return;
        if (adEn != null && adEn.getMainLists() != null) {
            viewLists.clear();
            imgEns.clear();
            imgEns.addAll(adEn.getMainLists());
            idsSize = imgEns.size();
            indicators = new ImageView[idsSize]; // 定义指示器数组大小
            if (idsSize == 2 || idsSize == 3) {
                imgEns.addAll(adEn.getMainLists());
            }
            for (int i = 0; i < imgEns.size(); i++) {
                final ThemeEntity items = imgEns.get(i);
                ImageView imageView = new ImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                String imgUrl = IMAGE_URL_HTTP + items.getImgUrl();
                Glide.with(AppApplication.getAppContext())
                        .load(imgUrl)
                        .apply(AppApplication.getShowOpeions())
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
                @Override
                public Object instantiateItem(View container, int position) {
                    View layout = null;
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
                public void destroyItem(View container, int position, Object object) {
                    View layout = null;
                    if (loop) {
                        layout = viewLists.get(position % viewLists.size());
                    } else {
                        layout = viewLists.get(position);
                    }
                    fg_home_vp.removeView(layout);
                }

                @Override
                public boolean isViewFromObject(View arg0, Object arg1) {
                    return arg0 == arg1;

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
            fg_home_vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(final int arg0) {
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
                mPagerAction = new Runnable() {

                    @Override
                    public void run() {
                        if (!vprStop) {
                            vprPosition++;
                            if (fg_home_vp != null) {
                                fg_home_vp.setCurrentItem(vprPosition);
                            }
                        }
                        vprStop = false;
                        if (fg_home_vp != null) {
                            fg_home_vp.postDelayed(mPagerAction, 3000);
                        }
                    }
                };
                if (fg_home_vp != null) {
                    fg_home_vp.postDelayed(mPagerAction, 3000);
                }
            }
        }
    }

    private void initItemsView() {
        apCallback = new AdapterCallback() {

            @Override
            public void setOnClick(Object entity, int position, int type) {
                ThemeEntity data = showLists.get(position);
                if (data != null) {
                    switch (type) {
                        case 0:
                            openWebviewActivity(data);
                            break;
                        case 1: //报名
                            openSignUpActivity(data.getImgUrl());
                            break;
                    }
                }else {
                    CommonTools.showToast(getString(R.string.toast_error_data_null));
                }
            }
        };
        lv_Adapter = new HomeListAdapter(mContext, showLists, apCallback);
        svlv.setAdapter(lv_Adapter);
        svlv.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
    }

    private void updateShowListDatas() {
        if (lv_Adapter != null) {
            lv_Adapter.updateAdapter(showLists);
        }
    }

    // 跳转至WebView
    private void openWebviewActivity(ThemeEntity data) {
        if (data == null) return;
        // 创建分享数据
        ShareEntity shareEn = new ShareEntity();
        shareEn.setTitle(data.getTitle());
        shareEn.setText(data.getTitle());
        shareEn.setImageUrl(data.getImgUrl());
        shareEn.setUrl(data.getLink());
        // 跳转至WebView
        Intent intent = new Intent(getActivity(), MyWebViewActivity.class);
        intent.putExtra("shareEn", shareEn);
        intent.putExtra("title", data.getTitle());
        intent.putExtra("lodUrl", data.getLink());
        startActivity(intent);
    }

    // 跳转至报名页面
    private void openSignUpActivity(String imgUrl) {
        Intent intent = new Intent(getActivity(), SignUpActivity.class);
        intent.putExtra("imgUrl", imgUrl);
        //intent.putExtra("payAmount", 6.18);
        //intent.putExtra("explainStr", "活动说明：\n1、费用为大赛统一报名费，主要用于场地租赁和组织等\n2、大赛时间：\n3、大赛地点：\n4、其他注意事项");
        startActivity(intent);
    }

//    private void initRecyclerView() {
//        // 创建布局管理器
//        LinearLayoutManager layoutManager_1 = new LinearLayoutManager(mContext);
//        layoutManager_1.setOrientation(LinearLayoutManager.VERTICAL);
//        // 设置布局管理器
//        fg_home_rv.setLayoutManager(layoutManager_1);
//        // 创建Adapter
//        homeAdapter = new HomeAdapter(mContext, al_home);
//        homeAdapter.setOnItemClickListener(new MyOnItemClickListener());
//        // 设置Adapter
//        fg_home_rv.setAdapter(homeAdapter);
//
//        // 创建布局管理器
//        LinearLayoutManager layoutManager_2 = new LinearLayoutManager(mContext);
//        layoutManager_2.setOrientation(LinearLayoutManager.VERTICAL);
//    }
//
//    class MyOnItemClickListener implements HomeAdapter.OnItemClickListener {
//        @Override
//        public void onItemClick(int position, HomeListEntity data) {
//
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

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
        super.onPause();
        LogUtil.i(TAG, "onPause");
        // 页面结束
        AppApplication.onPageEnd(getActivity(), TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestroy");
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (this.getView() != null)
            this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
    }

    /**
     * 加载商品数据
     */
    private void loadGoods() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("size", "10");
        map.put("page", String.valueOf(page_goods));
        loadDatas("activity/list", map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_POST_HOME_LIST);
    }

    @Override
    protected void callbackDatas(JSONObject jsonObject, int dataType) {
        BaseEntity baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_POST_HOME_LIST:
                    baseEn = JsonUtils.getHomeLists(jsonObject);
                    if (baseEn != null) {
                        List<ThemeEntity> lists = baseEn.getLists();
                        if (lists != null) {
                            showLists.addAll(lists);
                            updateShowListDatas();
                        } else {
                            //加载失败
                            LogUtil.i("Retrofit", TAG + " 数据加载失败 —> " + page_goods);
                        }
                    } else {
                        //加载失败
                        LogUtil.i("Retrofit", TAG + " 数据加载失败 —> " + page_goods);
                    }
                    break;
            }
        } catch (JSONException e) {
            ExceptionUtil.handle(e);
        }finally {

        }
    }

}

