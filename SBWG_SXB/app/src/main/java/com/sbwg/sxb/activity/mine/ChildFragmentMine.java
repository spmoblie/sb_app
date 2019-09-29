package com.sbwg.sxb.activity.mine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
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
import com.bumptech.glide.request.FutureTarget;
import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppConfig;
import com.sbwg.sxb.R;
import com.sbwg.sxb.activity.BaseFragment;
import com.sbwg.sxb.activity.common.MyWebViewActivity;
import com.sbwg.sxb.activity.common.ViewPagerActivity;
import com.sbwg.sxb.activity.home.SignUpActivity;
import com.sbwg.sxb.adapter.AdapterCallback;
import com.sbwg.sxb.adapter.MineListAdapter;
import com.sbwg.sxb.entity.BaseEntity;
import com.sbwg.sxb.entity.DesignEntity;
import com.sbwg.sxb.entity.ThemeEntity;
import com.sbwg.sxb.entity.UserInfoEntity;
import com.sbwg.sxb.utils.CommonTools;
import com.sbwg.sxb.utils.ExceptionUtil;
import com.sbwg.sxb.utils.FileManager;
import com.sbwg.sxb.utils.JsonUtils;
import com.sbwg.sxb.utils.LogUtil;
import com.sbwg.sxb.utils.UserManager;
import com.sbwg.sxb.utils.retrofit.HttpRequests;
import com.sbwg.sxb.widgets.RoundImageView;
import com.sbwg.sxb.widgets.pullrefresh.PullToRefreshBase;
import com.sbwg.sxb.widgets.pullrefresh.PullToRefreshListView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


@SuppressLint("NewApi")
public class ChildFragmentMine extends BaseFragment implements OnClickListener {

    String TAG = ChildFragmentMine.class.getSimpleName();

    PullToRefreshListView refresh_lv;
    ListView mListView;
    RoundImageView iv_user_head;
    ImageView iv_setting, iv_debunk;
    TextView tv_user_name, tv_user_id;
    LinearLayout ll_design_main, ll_head_main;

    private Context mContext;
    private AdapterCallback apCallback;
    private MineListAdapter lv_Adapter;
    private LinearLayout.LayoutParams designItemLP;

    private UserInfoEntity infoEn;
    private UserManager userManager;
    private int data_total = 0; //数据总量
    private int current_Page = 1;  //当前列表加载页
    private boolean isDesignOk = false;
    private ArrayList<String> urlLists = new ArrayList<String>();
    private ArrayList<DesignEntity> al_design = new ArrayList<>();
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
        userManager = UserManager.getInstance();

        AppApplication.updateMineData(true);

        int screenWidth = AppApplication.getSharedPreferences().getInt(AppConfig.KEY_SCREEN_WIDTH, 0);
        int goodsWidth = (screenWidth - CommonTools.dpToPx(mContext, 16)) / 3;
        designItemLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        designItemLP.setMargins(8, 0, 8, 0);
        designItemLP.width = goodsWidth;
        designItemLP.height = goodsWidth;

        View view = null;
        try {
            view = inflater.inflate(R.layout.fragment_layout_mine, null);
            //Butter Knife初始化
            ButterKnife.bind(this, view);

            findViewById(view);
            initView();
        } catch (Exception e) {
            ExceptionUtil.handle(e);
        }
        return view;
    }

    private void findViewById(View view) {
        refresh_lv = view.findViewById(R.id.fg_mine_refresh_lv);

        ll_head_main = (LinearLayout) FrameLayout.inflate(mContext, R.layout.layout_list_head_mine, null);
        iv_setting = ll_head_main.findViewById(R.id.fg_mine_iv_setting);
        iv_debunk = ll_head_main.findViewById(R.id.fg_mine_iv_debunk);
        iv_user_head = ll_head_main.findViewById(R.id.fg_mine_iv_head);
        tv_user_name = ll_head_main.findViewById(R.id.fg_mine_tv_used_name);
        tv_user_id = ll_head_main.findViewById(R.id.fg_mine_tv_used_id);
        ll_design_main = ll_head_main.findViewById(R.id.fg_mine_ll_design_main);
    }

    private void initView() {
        iv_setting.setOnClickListener(this);
        iv_debunk.setOnClickListener(this);
        iv_user_head.setOnClickListener(this);
        tv_user_name.setOnClickListener(this);

        initListView();
        loadDBData();
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
            public void setOnClick(Object data, int position, int type) {
                ThemeEntity themeEn = al_show.get(position);
                if (themeEn != null) {
                    openSignUpActivity(themeEn);
                } else {
                    CommonTools.showToast(getString(R.string.toast_error_data_page));
                }
            }
        };
        lv_Adapter = new MineListAdapter(mContext, al_show, apCallback);
        mListView.setAdapter(lv_Adapter);

        // 添加头部View
        mListView.addHeaderView(ll_head_main);
    }

    private void initUserView() {
        if (infoEn != null) {
            Bitmap headBitmap = BitmapFactory.decodeFile(AppConfig.SAVE_USER_HEAD_PATH);
            if (headBitmap != null) {
                iv_user_head.setImageBitmap(headBitmap);
            } else {
                loadUserHead();
                iv_user_head.setImageResource(R.drawable.icon_default_head);
            }
            tv_user_name.setText(infoEn.getUserNick());
            tv_user_id.setText(getString(R.string.mine_text_user_id, infoEn.getUserId()));
            tv_user_id.setVisibility(View.VISIBLE);
        } else {
            iv_user_head.setImageResource(R.drawable.icon_default_head);
            tv_user_name.setText(getString(R.string.mine_text_login));
            tv_user_id.setText(getString(R.string.mine_text_user_id, "000000"));
            tv_user_id.setVisibility(View.GONE);
        }
    }

    private void initDesignView() {
        urlLists.clear();
        ll_design_main.removeAllViews();

        for (int i = 0; i < al_design.size(); i++) {
            final int idsPosition = i;
            //DesignEntity items = new DesignEntity();
            DesignEntity items = al_design.get(i);
            /*if (i < 3) {
				if (i < al_design.size()) {
					items = al_design.get(i);
				} else {
					items.setImgUrl(null);
				}
			} else {
				items.setImgUrl("");
			}*/
            if (items != null) {
                String imgUrl = items.getImgUrl();
                ImageView imageView = new ImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                urlLists.add(imgUrl);
                Glide.with(AppApplication.getAppContext())
                        .load(imgUrl)
                        .apply(AppApplication.getShowOptions())
                        .into(imageView);
				/*if (i < 3) {
					urlLists.add(imgUrl);
					Glide.with(AppApplication.getAppContext())
							.load(imgUrl)
							.apply(AppApplication.getShowOptions())
							.into(imageView);
				} else {
					imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon_more, null));
				}*/
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ViewPagerActivity.class);
                        intent.putExtra(ViewPagerActivity.EXTRA_IMAGE_URLS, urlLists);
                        intent.putExtra(ViewPagerActivity.EXTRA_IMAGE_INDEX, idsPosition);
                        startActivity(intent);
						/*if (idsPosition < 3) {
						} else {
							CommonTools.showToast("没有更多了");
						}*/
                    }
                });
                ll_design_main.addView(imageView, designItemLP);
            }
        }
    }

    private void updateListData() {
        if (lv_Adapter != null) {
            lv_Adapter.updateAdapter(al_show);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fg_mine_iv_setting:
                startActivity(new Intent(mContext, SettingActivity.class));
                break;
            case R.id.fg_mine_iv_debunk:
                openWebViewActivity(getString(R.string.setting_question), "https://support.qq.com/product/1221");
                break;
            case R.id.fg_mine_iv_head:
            case R.id.fg_mine_tv_used_name:
                if (isLogin()) {
                    openPersonalActivity();
                } else {
                    openLoginActivity();
                }
                break;
        }
    }

    /**
     * 跳转个人专页
     */
    private void openPersonalActivity() {
        Intent intent = new Intent(mContext, PersonalActivity.class);
        intent.putExtra("data", infoEn);
        startActivity(intent);
    }

    // 跳转至WebView
    private void openWebViewActivity(String title, String url) {
        Intent intent = new Intent(getActivity(), MyWebViewActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("lodUrl", url);
        startActivity(intent);
    }

    // 跳转至报名页面
    private void openSignUpActivity(ThemeEntity data) {
        if (data == null) return;
        Intent intent = new Intent(getActivity(), SignUpActivity.class);
        intent.putExtra("type", 2);
        intent.putExtra("data", data);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
        // 页面开始
        AppApplication.onPageStart(TAG);
        // 用户信息
        if (isLogin()) {
            if (shared.getBoolean(AppConfig.KEY_UPDATE_USER_DATA, true)) {
                loadUserInfo();
            }
            if (shared.getBoolean(AppConfig.KEY_UPDATE_MINE_DATA, true)) {
                resetData();
            }
            if (infoEn == null) {
                infoEn = getUserInfoData();
                initUserView();
            }
        }
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
        current_Page = 1;
        isDesignOk = false;
        loadListData();
    }

    /**
     * 获取用户信息
     */
    private void loadUserInfo() {
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", userManager.getUserId());
        loadSVData(AppConfig.URL_USER_GET, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_POST_USER_GET);
    }

    /**
     * 加载头部展示数据
     */
    private void loadDesignData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", userManager.getUserId());
        loadSVData(AppConfig.URL_DESIGN_ALL, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_POST_DESIGN_ALL);
    }

    /**
     * 加载列表翻页数据
     */
    private void loadListData() {
        if (!isDesignOk) { //加载设计数据
            loadDesignData();
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("page", String.valueOf(current_Page));
        map.put("size", "10");
        map.put("userId", userManager.getUserId());
        loadSVData(AppConfig.URL_USER_ACTIVITY, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_POST_USER_ACTIVITY);
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        BaseEntity baseEn = null;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_POST_USER_GET:
                    baseEn = JsonUtils.getUserInfo(jsonObject);
                    if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
                        userManager.saveUserInfo((UserInfoEntity) baseEn.getData());
                        infoEn = getUserInfoData();
                        initUserView();
                        loadUserHead();
                        AppApplication.updateUserData(false);
                    }
                    break;
                case AppConfig.REQUEST_SV_POST_DESIGN_ALL:
                    baseEn = JsonUtils.getDesignData(jsonObject);
                    if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
                        List<DesignEntity> lists = baseEn.getLists();
                        if (lists != null && lists.size() > 0) {
                            al_design.clear();
                            al_design.addAll(lists);
                        }
                        isDesignOk = true;
                        initDesignView();
                        FileManager.writeFileSaveObject(AppConfig.mineHeadFileName, baseEn, 2);
                    }
                    break;
                case AppConfig.REQUEST_SV_POST_USER_ACTIVITY:
                    baseEn = JsonUtils.getMineList(jsonObject);
                    if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
                        data_total = baseEn.getDataTotal(); //加载更多数据控制符
                        List<ThemeEntity> lists = baseEn.getLists();
                        if (lists != null) {
                            if (lists.size() > 0) {
                                if (current_Page == 1) { //缓存第1页数据
                                    al_show.clear();
                                    am_show.clear();
                                    ThemeEntity listEn = new ThemeEntity();
                                    listEn.setMainLists(lists);
                                    FileManager.writeFileSaveObject(AppConfig.mineListFileName, listEn, 2);
                                }
                                List<BaseEntity> newLists = addNewEntity(al_show, lists, am_show);
                                if (newLists != null) {
                                    addNewShowLists(newLists);
                                    current_Page++;
                                }
                                updateListData();
                                LogUtil.i(LogUtil.LOG_HTTP, TAG + " List数据加载成功 —> " + current_Page + " size = " + newLists.size());
                            } else {
                                LogUtil.i(LogUtil.LOG_HTTP, TAG + " List数据没有更多 —> " + current_Page);
                            }
                        } else {
                            loadFailHandle();
                            LogUtil.i(LogUtil.LOG_HTTP, TAG + " List数据加载失败 —> " + current_Page);
                        }
                        AppApplication.updateMineData(false);
                    }
                    break;
            }
            handleErrorCode(baseEn);
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
        refresh_lv.onPullUpRefreshComplete();
    }

    /**
     * 加载本地缓存数据
     */
    private void loadDBData() {
        Observable.create(new Observable.OnSubscribe<BaseEntity>() {
            @Override
            public void call(Subscriber<? super BaseEntity> subscriber) {
                // 我的设计
                BaseEntity baseEn = (BaseEntity) FileManager.readFileSaveObject(AppConfig.mineHeadFileName, 2);
                if (baseEn == null) {
                    baseEn = new BaseEntity();
                }
                // 我的活动
                ThemeEntity listEn = (ThemeEntity) FileManager.readFileSaveObject(AppConfig.mineListFileName, 2);
                baseEn.setData(listEn);
                subscriber.onNext(baseEn);
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseEntity>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(BaseEntity baseEn) {
                if (baseEn != null) {
                    if (baseEn.getLists() != null) {
                        al_design.addAll(baseEn.getLists());
                    }
                    ThemeEntity listEn = (ThemeEntity) baseEn.getData();
                    if (listEn != null) {
                        al_show.addAll(listEn.getMainLists());
                    }
                }
                if (al_design.size() <= 0) {
                    al_design.addAll(initDesignData());
                }
                initDesignView();
                updateListData();
            }
        });
    }

    /**
     * 下载用户头像
     */
    private void loadUserHead() {
        if (infoEn != null) {
            Observable.create(new Observable.OnSubscribe<Bitmap>() {
                @Override
                public void call(Subscriber<? super Bitmap> subscriber) {
                    FutureTarget<Bitmap> ft = Glide
                            .with(AppApplication.getAppContext())
                            .asBitmap()
                            .load(infoEn.getUserHead())
                            .submit();
                    try {
                        Bitmap headBitmap = ft.get();
                        if (headBitmap != null) {
                            AppApplication.saveBitmapFile(headBitmap, new File(AppConfig.SAVE_USER_HEAD_PATH), 100);
                            subscriber.onNext(headBitmap);
                        }
                    } catch (Exception e) {
                        ExceptionUtil.handle(e);
                    }
                }
            })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Bitmap>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Bitmap bitmap) {
                            if (bitmap != null) {
                                iv_user_head.setImageBitmap(bitmap);
                            }
                        }
                    });
        }
    }

    /**
     * 获取缓存用户信息
     */
    private UserInfoEntity getUserInfoData() {
        UserInfoEntity userEn = new UserInfoEntity();
        userEn.setUserId(userManager.getUserId());
        userEn.setUserHead(userManager.getUserHead());
        userEn.setUserNick(userManager.getUserNick());
        userEn.setGenderCode(userManager.getUserGender());
        userEn.setBirthday(userManager.getUserBirthday());
        userEn.setUserArea(userManager.getUserArea());
        userEn.setUserIntro(userManager.getUserIntro());
        userEn.setUserEmail(userManager.getUserEmail());
        return userEn;
    }

    private List<DesignEntity> initDesignData() {
        List<DesignEntity> mainLists = new ArrayList<>();
        DesignEntity chEn_1 = new DesignEntity();
        DesignEntity chEn_2 = new DesignEntity();
        DesignEntity chEn_3 = new DesignEntity();
        DesignEntity chEn_4 = new DesignEntity();
        DesignEntity chEn_5 = new DesignEntity();

        chEn_1.setImgUrl(AppConfig.IMAGE_URL + "design_001.png");
        mainLists.add(chEn_1);
        chEn_2.setImgUrl(AppConfig.IMAGE_URL + "design_002.png");
        mainLists.add(chEn_2);
        chEn_3.setImgUrl(AppConfig.IMAGE_URL + "design_003.png");
        mainLists.add(chEn_3);
        chEn_4.setImgUrl(AppConfig.IMAGE_URL + "design_004.png");
        mainLists.add(chEn_4);
        chEn_5.setImgUrl(AppConfig.IMAGE_URL + "design_005.png");
        mainLists.add(chEn_5);

        return mainLists;
    }

}

