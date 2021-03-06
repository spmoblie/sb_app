package com.songbao.sampo_c.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
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
import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.activity.BaseFragment;
import com.songbao.sampo_c.dialog.DialogManager;
import com.songbao.sampo_c.entity.BaseEntity;
import com.songbao.sampo_c.entity.UserDataEntity;
import com.songbao.sampo_c.entity.UserInfoEntity;
import com.songbao.sampo_c.utils.ExceptionUtil;
import com.songbao.sampo_c.utils.JsonUtils;
import com.songbao.sampo_c.utils.LogUtil;
import com.songbao.sampo_c.utils.UserManager;
import com.songbao.sampo_c.utils.retrofit.HttpRequests;
import com.songbao.sampo_c.widgets.RoundImageView;

import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
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
    TextView tv_user_nick, tv_message_num;
    RelativeLayout rl_coupon, rl_purchase, rl_customize, rl_sign_up, rl_reserve, rl_help;

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
    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_layout_mine, null);
        //Butter Knife初始化
        ButterKnife.bind(this, view);

        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onCreate");
        mContext = getActivity();
        userManager = UserManager.getInstance();
        AppApplication.updateMineData(true);

        findViewById(view);
        initView();
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
        tv_message_num = view.findViewById(R.id.fg_mine_tv_message_num);

        rl_purchase = view.findViewById(R.id.fg_mine_purchase_main);
        rl_customize = view.findViewById(R.id.fg_mine_customize_main);
        rl_coupon = view.findViewById(R.id.fg_mine_coupon_main);
        rl_sign_up = view.findViewById(R.id.fg_mine_sign_up_main);
        rl_reserve = view.findViewById(R.id.fg_mine_reserve_main);
        rl_help = view.findViewById(R.id.fg_mine_help_main);
    }

    private void initView() {
        iv_setting.setOnClickListener(this);
        iv_message.setOnClickListener(this);
        cl_head_main.setOnClickListener(this);
        rl_purchase.setOnClickListener(this);
        rl_customize.setOnClickListener(this);
        rl_coupon.setOnClickListener(this);
        rl_sign_up.setOnClickListener(this);
        rl_reserve.setOnClickListener(this);
        rl_help.setOnClickListener(this);

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
            Bitmap headBitmap = BitmapFactory.decodeFile(AppConfig.PATH_USER_HEAD);
            if (headBitmap != null) {
                iv_user_head.setImageBitmap(headBitmap);
            } else {
                loadUserHead();
                iv_user_head.setImageResource(R.mipmap.icon_default_head);
            }
            tv_user_nick.setText(infoEn.getUserNick());

            int newNum = userManager.getUserMsgNum();
            if (newNum > 0) {
                tv_message_num.setVisibility(View.VISIBLE);
            } else {
                tv_message_num.setVisibility(View.GONE);
            }
            if (newNum > 99) {
                tv_message_num.setText("⋅⋅⋅");
            } else {
                tv_message_num.setText(String.valueOf(newNum));
            }
        } else {
            iv_user_head.setImageResource(R.mipmap.icon_default_head);
            tv_user_nick.setText(getString(R.string.mine_login));
            tv_message_num.setVisibility(View.GONE);
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
            case R.id.fg_mine_cl_head_main:
                if (!checkClick()) return;
                openPersonalActivity();
                break;
            case R.id.fg_mine_purchase_main:
                if (!checkClick()) return;
                startActivity(new Intent(mContext, MyPurchaseActivity.class));
                break;
            case R.id.fg_mine_customize_main:
                if (!checkClick()) return;
                startActivity(new Intent(mContext, CustomizeListActivity.class));
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
            case R.id.fg_mine_help_main:
                DialogManager.getInstance(mContext).showTwoBtnDialog(null, getString(R.string.mine_call_sale),
                        getString(R.string.cancel), getString(R.string.call), AppApplication.screen_width * 2 / 3,
                        true, true, new MyHandler(ChildFragmentMine.this), 101);
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
                refreshMineData();
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
        // 销毁对话框对象
        DialogManager.clearInstance();

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
        HashMap<String, Object> map = new HashMap<>();
        loadSVData(AppConfig.URL_USER_GET, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_USER_GET);
    }

    /**
     * 刷新“我的”数据
     */
    private void refreshMineData() {
        HashMap<String, Object> map = new HashMap<>();
        loadSVData(AppConfig.URL_USER_DYNAMIC, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_USER_DYNAMIC);
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_USER_GET:
                    BaseEntity<UserInfoEntity> baseEn = JsonUtils.getUserInfo(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        userManager.saveUserInfo(baseEn.getData());
                        infoEn = getUserInfoData();
                        initUserView();
                        loadUserHead();
                        AppApplication.updateUserData(false);
                    }
                    handleErrorCode(baseEn);
                    break;
                case AppConfig.REQUEST_SV_USER_DYNAMIC:
                    BaseEntity<UserDataEntity> dataEn = JsonUtils.getUserDynamic(jsonObject);
                    if (dataEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        UserDataEntity data = dataEn.getData();
                        userManager.saveUserMsgNum(data.getMessageNum());
                        if (data.getCartGoodsNum() > 0) {
                            userManager.saveUserCartNum(data.getCartGoodsNum());
                        }
                        initUserView();
                    }
                    handleErrorCode(dataEn);
                    break;
            }
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
                            AppApplication.saveBitmapFile(headBitmap, new File(AppConfig.PATH_USER_HEAD), 100);
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

    static class MyHandler extends Handler {

        WeakReference<ChildFragmentMine> mActivity;

        MyHandler(ChildFragmentMine activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ChildFragmentMine theActivity = mActivity.get();
            if (theActivity != null) {
                switch (msg.what) {
                    case 101: //联系客服
                        theActivity.callPhone(AppConfig.SALE_PHONE);
                        break;
                }
            }
        }
    }

}

