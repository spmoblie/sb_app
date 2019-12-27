package com.songbao.sampo_b.activity.mine;

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
import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.activity.BaseFragment;
import com.songbao.sampo_b.activity.common.ScanActivity;
import com.songbao.sampo_b.activity.two.DesignerActivity;
import com.songbao.sampo_b.entity.BaseEntity;
import com.songbao.sampo_b.entity.UserInfoEntity;
import com.songbao.sampo_b.utils.ExceptionUtil;
import com.songbao.sampo_b.utils.JsonUtils;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.utils.UserManager;
import com.songbao.sampo_b.utils.retrofit.HttpRequests;
import com.songbao.sampo_b.widgets.RoundImageView;

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

    ConstraintLayout cl_head_main;
    RoundImageView iv_user_head;
    ImageView iv_scan, iv_message;
    TextView tv_user_nick, tv_message_num;
    RelativeLayout rl_customize, rl_address, rl_designer, rl_setting;

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
        cl_head_main = view.findViewById(R.id.fg_mine_cl_head_main);
        iv_scan = view.findViewById(R.id.fg_mine_iv_scan);
        iv_message = view.findViewById(R.id.fg_mine_iv_message);
        iv_user_head = view.findViewById(R.id.fg_mine_iv_head);
        tv_user_nick = view.findViewById(R.id.fg_mine_tv_nick);
        tv_message_num = view.findViewById(R.id.fg_mine_tv_message_num);

        rl_customize = view.findViewById(R.id.fg_mine_customize_main);
        rl_address = view.findViewById(R.id.fg_mine_address_main);
        rl_designer = view.findViewById(R.id.fg_mine_designer_main);
        rl_setting = view.findViewById(R.id.fg_mine_setting_main);
    }

    private void initView() {
        iv_scan.setOnClickListener(this);
        iv_message.setOnClickListener(this);
        cl_head_main.setOnClickListener(this);
        rl_customize.setOnClickListener(this);
        rl_address.setOnClickListener(this);
        rl_designer.setOnClickListener(this);
        rl_setting.setOnClickListener(this);
    }

    private void initUserView() {
        if (infoEn != null) {
            Bitmap headBitmap = BitmapFactory.decodeFile(AppConfig.SAVE_USER_HEAD_PATH);
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
            case R.id.fg_mine_iv_scan:
                Intent intent = new Intent(mContext, ScanActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.fg_mine_iv_message:
                if (!checkClick()) return;
                startActivity(new Intent(mContext, MessageActivity.class));
                break;
            case R.id.fg_mine_cl_head_main:
                if (!checkClick()) return;
                openPersonalActivity();
                break;
            case R.id.fg_mine_customize_main:
                if (!checkClick()) return;
                startActivity(new Intent(mContext, MyCustomizeActivity.class));
                break;
            case R.id.fg_mine_address_main:
                if (!checkClick()) return;
                startActivity(new Intent(mContext, AddressActivity.class));
                break;
            case R.id.fg_mine_designer_main:
                if (!checkClick()) return;
                startActivity(new Intent(mContext, DesignerActivity.class));
                break;
            case R.id.fg_mine_setting_main:
                startActivity(new Intent(mContext, SettingActivity.class));
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

        AppApplication.updateMineData(true);
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

    /**
     * 刷新“我的”数据
     */
    private void refreshMineData() {
        HashMap<String, String> map = new HashMap<>();
        loadSVData(AppConfig.URL_USER_DYNAMIC, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_USER_DYNAMIC);
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
                case AppConfig.REQUEST_SV_USER_DYNAMIC:
                    baseEn = JsonUtils.getUserDynamic(jsonObject);
                    if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
                        userManager.saveUserMsgNum(baseEn.getDataTotal());
                        initUserView();
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

