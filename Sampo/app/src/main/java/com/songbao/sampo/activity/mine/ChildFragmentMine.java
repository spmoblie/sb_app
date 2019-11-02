package com.songbao.sampo.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.songbao.sampo.AppApplication;
import com.songbao.sampo.AppConfig;
import com.songbao.sampo.R;
import com.songbao.sampo.activity.BaseFragment;
import com.songbao.sampo.activity.common.MyWebViewActivity;
import com.songbao.sampo.entity.BaseEntity;
import com.songbao.sampo.entity.UserInfoEntity;
import com.songbao.sampo.utils.ExceptionUtil;
import com.songbao.sampo.utils.JsonUtils;
import com.songbao.sampo.utils.LogUtil;
import com.songbao.sampo.utils.UserManager;
import com.songbao.sampo.utils.retrofit.HttpRequests;
import com.songbao.sampo.widgets.RoundImageView;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class ChildFragmentMine extends BaseFragment implements OnClickListener {

    String TAG = ChildFragmentMine.class.getSimpleName();

    //PullToRefreshScrollView refresh_sv;
    //LinearLayout sv_main;
    ConstraintLayout cl_head_main;
    RoundImageView iv_user_head;
    ImageView iv_setting, iv_message;
    TextView tv_user_nick, tv_user_member;
    RelativeLayout rl_coupon_main, rl_sign_up_main, rl_reserve_main, rl_bill_main, rl_help_main;

    private Context mContext;

    private UserInfoEntity infoEn;
    private UserManager userManager;

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

        //AppApplication.updateMineData(true);

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
        //refresh_sv = view.findViewById(R.id.fg_mine_refresh_sv);
        //sv_main = (LinearLayout) FrameLayout.inflate(mContext, R.layout.layout_scrollview_mine, null);

        cl_head_main = view.findViewById(R.id.fg_mine_cl_head_main);
        iv_setting = view.findViewById(R.id.fg_mine_iv_setting);
        iv_message = view.findViewById(R.id.fg_mine_iv_message);
        iv_user_head = view.findViewById(R.id.fg_mine_iv_head);
        tv_user_nick = view.findViewById(R.id.fg_mine_tv_nick);
        tv_user_member = view.findViewById(R.id.fg_mine_tv_member);

        rl_coupon_main = view.findViewById(R.id.fg_mine_coupon_main);
        rl_sign_up_main = view.findViewById(R.id.fg_mine_sign_up_main);
        rl_reserve_main = view.findViewById(R.id.fg_mine_reserve_main);
        rl_bill_main = view.findViewById(R.id.fg_mine_bill_main);
        rl_help_main = view.findViewById(R.id.fg_mine_help_main);
    }

    private void initView() {
        iv_setting.setOnClickListener(this);
        iv_message.setOnClickListener(this);
        iv_user_head.setOnClickListener(this);
        rl_coupon_main.setOnClickListener(this);
        rl_sign_up_main.setOnClickListener(this);
        rl_reserve_main.setOnClickListener(this);
        rl_bill_main.setOnClickListener(this);
        rl_help_main.setOnClickListener(this);

        //initScrollView();
    }

    /*private void initScrollView() {
        refresh_sv.setPullRefreshEnabled(true);
        refresh_sv.setPullLoadEnabled(true);
        refresh_sv.setScrollLoadEnabled(false);
        refresh_sv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                // 下拉刷新
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        refresh_sv.onPullDownRefreshComplete();
                    }
                }, AppConfig.LOADING_TIME);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                // 加载更多
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        refresh_sv.onPullUpRefreshComplete();
                    }
                }, AppConfig.LOADING_TIME);
            }
        });

        ScrollView sv = refresh_sv.getRefreshableView();
        sv.addView(sv_main);
        sv.setVerticalScrollBarEnabled(false);
    }*/

    private void initUserView() {
        if (infoEn != null) {
            Bitmap headBitmap = BitmapFactory.decodeFile(AppConfig.SAVE_USER_HEAD_PATH);
            if (headBitmap != null) {
                iv_user_head.setImageBitmap(headBitmap);
            } else {
                loadUserHead();
                iv_user_head.setImageResource(R.drawable.icon_default_head);
            }
            tv_user_nick.setText(infoEn.getUserNick());
            tv_user_member.setText("门店年卡会员");
            tv_user_member.setVisibility(View.VISIBLE);
        } else {
            iv_user_head.setImageResource(R.drawable.icon_default_head);
            tv_user_nick.setText(getString(R.string.mine_login));
            tv_user_member.setText("无会员信息");
            tv_user_member.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fg_mine_iv_setting:
                startActivity(new Intent(mContext, SettingActivity.class));
                break;
            case R.id.fg_mine_iv_message:
                if (!checkClick()) return;
                startActivity(new Intent(mContext, MessageActivity.class));
                break;
            case R.id.fg_mine_iv_head:
                if (!checkClick()) return;
                openPersonalActivity();
                break;
            case R.id.fg_mine_coupon_main:
                if (!checkClick()) return;
                startActivity(new Intent(mContext, MyTicketsActivity.class));
                break;
            case R.id.fg_mine_sign_up_main:
                if (!checkClick()) return;
                startActivity(new Intent(mContext, MySignUpActivity.class));
                break;
            case R.id.fg_mine_reserve_main:
                if (!checkClick()) return;
                startActivity(new Intent(mContext, MyReserveActivity.class));
                break;
            case R.id.fg_mine_bill_main:
                if (!checkClick()) return;
                startActivity(new Intent(mContext, MyOrderActivity.class));
                break;
            case R.id.fg_mine_help_main:
                openWebViewActivity(getString(R.string.setting_question), "https://support.qq.com/product/1221");
                break;
        }
    }

    /**
     * 校验事件
     */
    private boolean checkClick() {
        if (!isLogin()) { //未登录
            openLoginActivity();
            return false;
        }
        return true;
    }

    /**
     * 跳转个人专页
     */
    private void openPersonalActivity() {
        Intent intent = new Intent(mContext, PersonalActivity.class);
        intent.putExtra(AppConfig.PAGE_DATA, infoEn);
        startActivity(intent);
    }

    /**
     * 跳转至WebView
     * @param title
     * @param url
     */
    private void openWebViewActivity(String title, String url) {
        Intent intent = new Intent(getActivity(), MyWebViewActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("lodUrl", url);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
        // 页面开始
        AppApplication.onPageStart(TAG);

        // 用户信息
        if (isLogin()) {
            if (shared.getBoolean(AppConfig.KEY_OPEN_MESSAGE, false)) {
                startActivity(new Intent(mContext, MessageActivity.class));
                shared.edit().putBoolean(AppConfig.KEY_OPEN_MESSAGE, false).apply();
            }
            if (shared.getBoolean(AppConfig.KEY_UPDATE_USER_DATA, true)) {
                loadUserInfo();
            }
            if (shared.getBoolean(AppConfig.KEY_UPDATE_MINE_DATA, true)) {
                // 刷新“我的”
            }
            if (infoEn == null) {
                infoEn = getUserInfoData();
                initUserView();
            }
        } else {
            infoEn = null;
            initUserView();
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
     * 获取用户信息
     */
    private void loadUserInfo() {
        HashMap<String, String> map = new HashMap<>();
        loadSVData(AppConfig.URL_USER_GET, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_USER_GET);
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        BaseEntity baseEn = null;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_USER_GET:
                    baseEn = JsonUtils.getUserInfo(jsonObject);
                    if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
                        userManager.saveUserInfo((UserInfoEntity) baseEn.getData());
                        infoEn = getUserInfoData();
                        initUserView();
                        loadUserHead();
                        AppApplication.updateUserData(false);
                    }
                    break;
            }
            handleErrorCode(baseEn);
        } catch (Exception e) {
            loadFailHandle();
            ExceptionUtil.handle(e);
        }
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

}

