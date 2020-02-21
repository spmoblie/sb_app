package com.songbao.sampo_c.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.AppManager;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.activity.common.ViewPagerActivity;
import com.songbao.sampo_c.activity.common.clip.ClipImageCircularActivity;
import com.songbao.sampo_c.activity.common.clip.ClipImageSquareActivity;
import com.songbao.sampo_c.activity.common.clip.ClipPhotoGridActivity;
import com.songbao.sampo_c.activity.common.clip.ClipPhotoOneActivity;
import com.songbao.sampo_c.activity.login.LoginActivity;
import com.songbao.sampo_c.activity.login.LoginPhoneActivity;
import com.songbao.sampo_c.activity.login.RegisterActivity;
import com.songbao.sampo_c.activity.login.RegisterOauthActivity;
import com.songbao.sampo_c.activity.login.ResetPasswordActivity;
import com.songbao.sampo_c.activity.mine.CommentAddActivity;
import com.songbao.sampo_c.activity.mine.CommentPostActivity;
import com.songbao.sampo_c.activity.mine.DesignerListActivity;
import com.songbao.sampo_c.activity.two.GoodsActivity;
import com.songbao.sampo_c.activity.two.GoodsListActivity;
import com.songbao.sampo_c.activity.two.SketchActivity;
import com.songbao.sampo_c.adapter.GoodsAttrAdapter;
import com.songbao.sampo_c.dialog.DialogManager;
import com.songbao.sampo_c.dialog.LoadDialog;
import com.songbao.sampo_c.entity.BaseEntity;
import com.songbao.sampo_c.entity.CommentEntity;
import com.songbao.sampo_c.entity.GoodsAttrEntity;
import com.songbao.sampo_c.entity.ShareEntity;
import com.songbao.sampo_c.utils.CommonTools;
import com.songbao.sampo_c.utils.ExceptionUtil;
import com.songbao.sampo_c.utils.JsonUtils;
import com.songbao.sampo_c.utils.LogUtil;
import com.songbao.sampo_c.utils.MyCountDownTimer;
import com.songbao.sampo_c.utils.StringUtil;
import com.songbao.sampo_c.utils.UserManager;
import com.songbao.sampo_c.utils.retrofit.HttpRequests;
import com.songbao.sampo_c.widgets.RoundImageView;
import com.songbao.sampo_c.widgets.share.ShareView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Observer;


/**
 * 所有Activity的父类
 */
public class BaseActivity extends FragmentActivity {

    String TAG = BaseActivity.class.getSimpleName();

    protected Context mContext;
    protected SharedPreferences shared;
    protected DialogManager myDialog;
    protected UserManager userManager;
    protected DecimalFormat df;
    protected Boolean isInitShare = false;
    protected Boolean isTimeFinish = true;
    protected int screenWidth, screenHeight, statusHeight, dialogWidth;

    private ConstraintLayout top_main;
    private ImageView iv_left, iv_data_null;
    private TextView tv_title, tv_data_null;
    private Button bt_right;
    private ViewFlipper mLayoutBase;
    private MyCountDownTimer myTimer;

    private ShareView mShareView;
    private Animation inAnim, outAnim;

    // 商品属性浮层组件
    private boolean isShow = false;
    private int buyNumber = 1;
    private int select_id_1 = 0;
    private int select_id_2 = 0;
    private int select_id_3 = 0;
    private String select_name_1 = "";
    private String select_name_2 = "";
    private String select_name_3 = "";
    private String goodsCode = "";
    private GoodsAttrEntity attrEn, secEn;
    private GoodsAttrAdapter rv_Adapter;
    private View cartPopupView;
    private PopupWindow cartPopupWindow;
    private ConstraintLayout popupShowMain;
    private RoundImageView iv_goods_img;
    private TextView tv_popup_price, tv_popup_number, tv_popup_select;
    private Animation popupAnimShow, popupAnimGone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        // 创建动画
        overridePendingTransition(R.anim.in_from_right, R.anim.anim_no_anim);

        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onCreate");
        addActivity(this);

        mContext = this;
        shared = AppApplication.getSharedPreferences();
        userManager = UserManager.getInstance();
        screenWidth = AppApplication.screen_width;
        screenHeight = AppApplication.screen_height;
        statusHeight = AppApplication.status_height;
        dialogWidth = screenWidth * 2 / 3;
        df = new DecimalFormat("0.00");

        // 推送服务统计应用启动数据
        AppApplication.onPushAppStartData();

        findViewById();
        initView();

        if (isInitShare) {
            try { //初始化ShareView
                View view = getLayoutInflater().inflate(R.layout.popup_view_share, (ViewGroup) findViewById(R.id.base_fl_main));
                mShareView = new ShareView(mContext, this, view, null);
                mShareView.showShareLayer(false);
            } catch (Exception e) {
                ExceptionUtil.handle(e);
            }
        }
    }

    private void findViewById() {
        top_main = findViewById(R.id.top_base_view_main);
        iv_left = findViewById(R.id.top_base_iv_left);
        tv_title = findViewById(R.id.top_base_tv_title);
        bt_right = findViewById(R.id.top_base_bt_right);
        iv_data_null = findViewById(R.id.base_iv_data_null);
        tv_data_null = findViewById(R.id.base_tv_data_null);
        mLayoutBase = findViewById(R.id.base_ll_container);
    }

    private void initView() {
        iv_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                OnListenerLeft();
            }
        });
        bt_right.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                OnListenerRight();
            }
        });

        inAnim = AnimationUtils.loadAnimation(mContext, R.anim.in_from_right);
        outAnim = AnimationUtils.loadAnimation(mContext, R.anim.out_to_left);
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = LayoutInflater.from(this).inflate(layoutResID, null);
        setContentView(view);
    }

    @Override
    public void setContentView(View view) {
        if (mLayoutBase.getChildCount() > 1) {
            mLayoutBase.removeViewAt(1);
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1);
        mLayoutBase.addView(view, lp);
        //Butter Knife初始化
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume()");

        super.onResume();
    }

    @Override
    protected void onPause() {
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onPause()");
        // 缓存标题View高度
        if (AppApplication.title_height <= 0) {
            AppApplication.title_height = top_main.getHeight();
        }
        DialogManager.clearInstance();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onDestroy");
        finishActivity(this);

        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        // 销毁动画
        overridePendingTransition(R.anim.anim_no_anim, R.anim.out_to_right);
    }

    @Override
    public void onBackPressed() {
        if (mShareView != null && mShareView.isShowing()) {
            mShareView.showShareLayer(false);
        } else {
            super.onBackPressed();
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mShareView != null) {
            mShareView.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (mShareView != null) {
            mShareView.onNewIntent(intent);
        }
        super.onNewIntent(intent);
    }

    /**
     * 设置头部是否可见
     */
    protected void setHeadVisibility(int visibility) {
        switch (visibility) {
            case View.VISIBLE:
                if (top_main.getVisibility() == View.GONE) {
                    top_main.clearAnimation();
                    top_main.startAnimation(inAnim);
                }
                break;
            case View.GONE:
                if (top_main.getVisibility() == View.VISIBLE) {
                    top_main.clearAnimation();
                    top_main.startAnimation(outAnim);
                }
                break;
        }
        top_main.setVisibility(visibility);
    }

    /**
     * 设置内容为空是否可见
     */
    protected void setNullVisibility(int visibility) {
        iv_data_null.setVisibility(visibility);
        tv_data_null.setVisibility(visibility);
    }

    /**
     * 设置头部View背景色
     */
    protected void setHeadBackground(int color) {
        top_main.setBackgroundColor(color);
    }

    /**
     * 设置左边按钮是否可见
     */
    protected void setLeftViewGone(int visibility) {
        iv_left.setVisibility(visibility);
    }

    /**
     * 设置右边按钮是否可见
     */
    protected void setRightViewGone(int visibility) {
        bt_right.setVisibility(visibility);
    }

    /**
     * 左键键监听执行方法，让子类重写该方法
     */
    protected void OnListenerLeft() {
        if (mShareView != null && mShareView.isShowing()) {
            mShareView.showShareLayer(false);
        } else {
            finish();
        }
    }

    /**
     * 右键监听执行方法，让子类重写该方法
     */
    protected void OnListenerRight() {

    }

    /**
     * 设置标题（文本资源Id）
     */
    public void setTitle(int titleId) {
        setTitle(getString(titleId));
    }

    /**
     * 设置标题（文本对象）
     */
    public void setTitle(String title) {
        tv_title.setText(title);
    }

    /**
     * 设置右边按钮显示文本
     */
    protected void setRightViewText(String text) {
        setRightViewGone(View.VISIBLE);
        bt_right.setText(text);
    }

    /**
     * 设置右边按钮背景图片资源Id
     */
    protected void setRightViewBackground(int drawableId) {
        setRightViewGone(View.VISIBLE);
        bt_right.setBackgroundResource(drawableId);
    }

    /**
     * 设置右边按钮背景图片资源对象
     */
    @SuppressLint("NewApi")
    protected void setRightViewBackground(Drawable btnRight) {
        setRightViewGone(View.VISIBLE);
        bt_right.setBackground(btnRight);
    }

    /**
     * 添加Activity到堆栈
     *
     * @param activity
     */
    protected void addActivity(Activity activity) {
        if (activity != null) {
            LogUtil.i(LogUtil.LOG_TAG, activity.getClass().getSimpleName() + ": onCreate()");
            AppManager.getInstance().addActivity(activity);
        }
    }

    /**
     * 关闭指定的Activity
     *
     * @param activity
     */
    protected void finishActivity(Activity activity) {
        if (activity != null) {
            LogUtil.i(LogUtil.LOG_TAG, activity.getClass().getSimpleName() + ": onDestroy()");
            AppManager.getInstance().finishActivity(activity);
        }
    }

    /**
     * 关闭相册相关Activity
     */
    protected void closePhotoActivity() {
        AppManager.getInstance().finishActivity(ClipPhotoGridActivity.class);
        AppManager.getInstance().finishActivity(ClipPhotoOneActivity.class);
        AppManager.getInstance().finishActivity(ClipImageSquareActivity.class);
        AppManager.getInstance().finishActivity(ClipImageCircularActivity.class);
    }

    /**
     * 关闭登录相关Activity
     */
    protected void closeLoginActivity() {
        AppManager.getInstance().finishActivity(LoginActivity.class);
        AppManager.getInstance().finishActivity(LoginPhoneActivity.class);
        AppManager.getInstance().finishActivity(RegisterActivity.class);
        AppManager.getInstance().finishActivity(RegisterOauthActivity.class);
        AppManager.getInstance().finishActivity(ResetPasswordActivity.class);
        postDeviceToken();
    }

    /**
     * 关闭订制相关Activity
     */
    protected void closeCustomizeActivity() {
        AppManager.getInstance().finishActivity(GoodsActivity.class);
        AppManager.getInstance().finishActivity(GoodsListActivity.class);
        AppManager.getInstance().finishActivity(SketchActivity.class);
        AppManager.getInstance().finishActivity(DesignerListActivity.class);
    }

    /**
     * 回退“首页”
     */
    protected void returnHomeActivity() {
        AppApplication.jumpToHomePage(0);
        openActivity(MainActivity.class);
    }

    /**
     * 打开登录Activity
     */
    protected void openLoginActivity() {
        shared.edit().putBoolean(AppConfig.KEY_JUMP_PAGE, false).apply();
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * 打开商品详情页
     */
    protected void openGoodsActivity(String skuCode) {
        openGoodsActivity(skuCode, false);
    }

    /**
     * 打开商品详情页同时打开属性面板
     */
    protected void openGoodsActivity(String skuCode, boolean isOpenAttr) {
        Intent intent = new Intent(mContext, GoodsActivity.class);
        intent.putExtra("skuCode", skuCode);
        intent.putExtra("isOpenAttr", isOpenAttr);
        startActivity(intent);
    }

    /**
     * 打开设计师列表页
     */
    protected void openDesignerActivity(String skuCode) {
        Intent intent = new Intent(mContext, DesignerListActivity.class);
        intent.putExtra("skuCode", skuCode);
        startActivity(intent);
    }

    /**
     * 打开我要评论页
     */
    protected void openCommentPostActivity(CommentEntity commentEn) {
        if (commentEn != null) {
            Intent intent = new Intent(mContext, CommentPostActivity.class);
            intent.putExtra(AppConfig.PAGE_DATA, commentEn);
            startActivity(intent);
        }
    }

    /**
     * 打开追加评论页
     */
    protected void openCommentAddActivity(CommentEntity commentEn) {
        if (commentEn != null) {
            Intent intent = new Intent(mContext, CommentAddActivity.class);
            intent.putExtra(AppConfig.PAGE_DATA, commentEn);
            startActivity(intent);
        }
    }

    /**
     * 打开图片查看器
     *
     * @param urlLists
     * @param position
     */
    protected void openViewPagerActivity(ArrayList<String> urlLists, int position) {
        Intent intent = new Intent(mContext, ViewPagerActivity.class);
        intent.putExtra(ViewPagerActivity.EXTRA_IMAGE_URLS, urlLists);
        intent.putExtra(ViewPagerActivity.EXTRA_IMAGE_INDEX, position);
        startActivity(intent);
    }

    /**
     * 通过类名启动Activity
     */
    protected void openActivity(Class<?> pClass) {
        openActivity(pClass, null);
    }

    /**
     * 通过类名启动Activity，并且含有Bundle数据
     */
    protected void openActivity(Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(mContext, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    /**
     * 通过Action启动Activity
     */
    protected void openActivity(String pAction) {
        openActivity(pAction, null);
    }

    /**
     * 通过Action启动Activity，并且含有Bundle数据
     */
    protected void openActivity(String pAction, Bundle pBundle) {
        Intent intent = new Intent(pAction);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    /**
     * 动态检查权限
     */
    protected boolean checkPermission() {
        List<String> mPermissionList = new ArrayList<>();
        String[] permissions = AppConfig.PERMISSIONS;
        // 判断哪些权限未授予
        for (String pmsStr: permissions) {
            if (ContextCompat.checkSelfPermission(this, pmsStr) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(pmsStr);
            }
        }
        if (!mPermissionList.isEmpty()) {
            // 请求授权
            String[] permissions_new = mPermissionList.toArray(new String[mPermissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions_new, AppConfig.REQUEST_CORD_PERMISSION);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == AppConfig.REQUEST_CORD_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//允许
                LogUtil.i("Permissions", "PERMISSION_GRANTED 允许");
                permissionsResultCallback(true);
                return;
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {//拒绝
                LogUtil.i("Permissions", "PERMISSION_DENIED 拒绝");
                permissionsResultCallback(false);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 授权结果回调
     */
    protected void permissionsResultCallback(boolean result) {
    }

    /**
     * 校验登录状态
     */
    protected boolean isLogin() {
        return UserManager.getInstance().checkIsLogin();
    }

    /**
     * 处理网络请求返回状态码
     *
     * @param baseEn
     */
    protected void handleErrorCode(BaseEntity baseEn) {
        if (baseEn != null) {
            switch (baseEn.getErrNo()) {
                case AppConfig.ERROR_CODE_TIMEOUT: //登录超时
                    showTimeOutDialog();
                    break;
                default:
                    showServerBusy(baseEn.getErrMsg());
                    break;
            }
        } else {
            showServerBusy("");
        }
    }

    /**
     * 处理登入超时
     */
    protected void handleTimeOut() {
        AppApplication.AppLogout();
        openLoginActivity();
    }

    /**
     * 弹出登入超时对话框
     */
    protected void showTimeOutDialog() {
        AppApplication.AppLogout();
        showErrorDialog(getString(R.string.login_timeout), true, new TimeOutHandler(this));
    }

    /**
     * 分享参数出错提示
     */
    protected void showShareError() {
        CommonTools.showToast(getString(R.string.share_msg_entity_error));
    }

    /**
     * 数据显示出错提示
     */
    protected void showDataError() {
        CommonTools.showToast(getString(R.string.toast_error_data_page));
    }

    /**
     * 加载数据出错提示
     */
    protected void showServerBusy(String showStr) {
        if (StringUtil.isNull(showStr)) {
            showStr = getString(R.string.toast_server_busy);
        }
        //showErrorDialog(showStr);
        CommonTools.showToast(showStr);
    }

    /**
     * 单按钮提示对话框
     */
    protected void showErrorDialog(int resId) {
        showErrorDialog(getString(resId));
    }

    protected void showErrorDialog(String content) {
        showErrorDialog(content, true, null);
    }

    protected void showErrorDialog(String content, boolean isVanish, final Handler handler) {
        content = (TextUtils.isEmpty(content)) ? getString(R.string.dialog_error_msg) : content;
        if (myDialog == null) {
            myDialog = DialogManager.getInstance(mContext);
        }
        myDialog.showOneBtnDialog(content, dialogWidth, true, isVanish, handler, null);
    }

    protected void showSuccessDialog(String content, boolean isSuccess) {
        content = (TextUtils.isEmpty(content)) ? getString(R.string.dialog_error_msg) : content;
        if (myDialog == null) {
            myDialog = DialogManager.getInstance(mContext);
        }
        myDialog.showSuccessDialog(content, dialogWidth, isSuccess);
    }

    /**
     * 双按钮确认对话框
     */
    protected void showConfirmDialog(String content, Handler handler) {
        showConfirmDialog(content, handler, AppConfig.DIALOG_CLICK_OK);
    }

    protected void showConfirmDialog(String content, Handler handler, int handlerCode) {
        showConfirmDialog(content, null, null, handler, handlerCode);
    }

    protected void showConfirmDialog(String content, String leftBtnStr, String rightBtnStr, Handler handler) {
        showConfirmDialog(content, leftBtnStr, rightBtnStr, handler, AppConfig.DIALOG_CLICK_OK);
    }

    protected void showConfirmDialog(String content, String leftBtnStr, String rightBtnStr, Handler handler, int handlerCode) {
        showConfirmDialog(null, content, leftBtnStr, rightBtnStr, true, true, handler, handlerCode);
    }

    protected void showConfirmDialog(String title, String content, Handler handler) {
        showConfirmDialog(title, content, handler, AppConfig.DIALOG_CLICK_OK);
    }

    protected void showConfirmDialog(String title, String content, Handler handler, int handlerCode) {
        showConfirmDialog(title, content, null, null, handler, handlerCode);
    }

    protected void showConfirmDialog(String title, String content, String leftBtnStr, String rightBtnStr, Handler handler) {
        showConfirmDialog(title, content, leftBtnStr, rightBtnStr, handler, AppConfig.DIALOG_CLICK_OK);
    }

    protected void showConfirmDialog(String title, String content, String leftBtnStr, String rightBtnStr, Handler handler, int handlerCode) {
        showConfirmDialog(title, content, leftBtnStr, rightBtnStr, true, true, handler, handlerCode);
    }

    protected void showConfirmDialog(String title, String content, String leftBtnStr, String rightBtnStr,
                                     boolean isCenter, boolean isVanish, Handler handler, int handlerCode) {
        showConfirmDialog(title, content, leftBtnStr, rightBtnStr, dialogWidth, isCenter, isVanish, handler, handlerCode);
    }

    protected void showConfirmDialog(String title, String content, String leftBtnStr, String rightBtnStr,
                                     int width, boolean isCenter, boolean isVanish, Handler handler, int handlerCode) {
        leftBtnStr = (leftBtnStr == null) ? getString(R.string.cancel) : leftBtnStr;
        rightBtnStr = (rightBtnStr == null) ? getString(R.string.confirm) : rightBtnStr;
        if (myDialog == null) {
            myDialog = DialogManager.getInstance(mContext);
        }
        myDialog.showTwoBtnDialog(title, content, leftBtnStr, rightBtnStr, width, isCenter, isVanish, handler, handlerCode);
    }

    /**
     * 可输入的对话框
     */
    protected void showEditDialog(String title, int inputType, Handler handler) {
        showEditDialog(title, inputType, true, handler);
    }

    protected void showEditDialog(String title, int inputType, boolean isVanish, Handler handler) {
        showEditDialog(title, dialogWidth, inputType, isVanish, handler);
    }

    protected void showEditDialog(String title, int width, int inputType, boolean isVanish, Handler handler) {
        if (myDialog == null) {
            myDialog = DialogManager.getInstance(mContext);
        }
        myDialog.showEditDialog(title, width, inputType, isVanish, handler);
    }

    /**
     * 列表展示对话框
     */
    protected void showListDialog(String content, CharSequence[] items, Handler handler) {
        showListDialog(content, items, true, handler);
    }

    protected void showListDialog(String content, CharSequence[] items, boolean isCenter, Handler handler) {
        showListDialog(content, items, dialogWidth, isCenter, handler);
    }

    protected void showListDialog(String content, CharSequence[] items, int width, boolean isCenter, Handler handler) {
        if (myDialog == null) {
            myDialog = DialogManager.getInstance(mContext);
        }
        myDialog.showListItemDialog(content, items, width, isCenter, handler);
    }

    /**
     * 显示分享View
     */
    protected void showShareView(ShareEntity shareEn) {
        if (mShareView != null && shareEn != null) {
            if (mShareView.getShareEntity() == null) {
                mShareView.setShareEntity(shareEn);
            }
            if (mShareView.isShowing()) {
                mShareView.showShareLayer(false);
            } else {
                /*if (!UserManager.getInstance().checkIsLogin()) {
                    openLoginActivity();
					return;
				}*/
                mShareView.showShareLayer(true);
            }
        } else {
            showShareError();
        }
    }

    /**
     * 切换View背景的状态
     */
    @SuppressLint("NewApi")
    protected void changeViewState(View view, boolean isState) {
        if (view == null) return;
        if (isState) {
            view.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_style_solid_04_08, null));
        } else {
            view.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_style_solid_03_08, null));
        }
    }

    /**
     * 切换密码输入框密码显示的状态
     */
    protected void changeEditTextPassword(EditText et_view, boolean isShow) {
        if (et_view == null) return;
        if (isShow) { //显示密码
            et_view.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else { //隐藏密码
            et_view.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        et_view.setSelection(et_view.length()); //调整光标至最后
    }

    /**
     * 输入框获取焦点并清空内容
     */
    protected void editTextFocusAndClear(EditText et_view) {
        if (et_view == null) return;
        et_view.setText("");
        et_view.requestFocus();
    }

    /**
     * 隐藏软键盘
     */
    protected void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏
        }
    }

    /**
     * 隐藏物理键盘
     */
    protected void toggleSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 拨打电话
     *
     * @param phoneNum 电话号码
     */
    public void callPhone(String phoneNum) {
        //Intent intent = new Intent(Intent.ACTION_CALL); //直接拨打
        Intent intent = new Intent(Intent.ACTION_DIAL); //手动拨打
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }

    /**
     * 开启倒计时
     */
    protected void startTimer(TextView tv_time, long time) {
        if (tv_time == null) return;
        stopTimer();
        myTimer = new MyCountDownTimer(tv_time, time, 1000,
                new MyCountDownTimer.MyTimerCallback() {
                    @Override
                    public void onFinish() {
                        onTimerFinish();
                    }
                });
        myTimer.start(); //开始倒计时
        isTimeFinish = false;
    }

    /**
     * 取消倒计时
     */
    protected void stopTimer() {
        if (myTimer != null) {
            myTimer.cancel();
            myTimer = null;
        }
    }

    /**
     * 倒计时结束执行
     */
    protected void onTimerFinish() {
        isTimeFinish = true;
    }

    /**
     * List数据类型转换
     */
    public static <T> ArrayList<T> castList(Object obj, Class<T> clazz) {
        ArrayList<T> result = new ArrayList<>();
        if(obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }

    /**
     * 过滤数据
     */
    protected <T extends BaseEntity> List<T> filterData(List<T> newData, ArrayMap<String, Boolean> cacheMap) {
        List<T> newList = new ArrayList<>();
        if (newData == null || cacheMap == null) return newList;
        T newEn;
        for (int i = 0; i < newData.size(); i++) {
            newEn = newData.get(i);
            if (newEn != null) {
                String enId = newEn.getEntityId();
                if (!StringUtil.isNull(enId) && !cacheMap.containsKey(enId)) {
                    newList.add(newEn);
                    cacheMap.put(enId, true);
                }
            }
        }
        return newList;
    }

    /**
     * 判定是否停止加载更多
     */
    protected boolean isStopLoadMore(int showCount, int countTotal, int pageSize) {
        showPageNum(showCount, countTotal, pageSize);
        return showCount > 0 && showCount == countTotal;
    }

    /**
     * 提示当前页数
     */
    protected void showPageNum(int showCount, int countTotal, int pageSize) {
        if (pageSize <= 0) return;
        int page_num = showCount / pageSize;
        if (showCount % pageSize > 0) {
            page_num++;
        }
        int page_total = countTotal / pageSize;
        if (countTotal % pageSize > 0) {
            page_total++;
        }
        CommonTools.showPageNum(page_num + "/" + page_total, 1000);
    }

    /**
     * 提交标准Json格式数据
     */
    protected void postJsonData(String path, JSONObject jsonObj, final int dataType) {
        postJsonData("", path, jsonObj, dataType);
    }

    /**
     * 提交标准Json格式数据
     */
    protected void postJsonData(String head, String path, JSONObject jsonObj, final int dataType) {
        if (StringUtil.isNull(head)) {
            head = AppConfig.BASE_TYPE;
        }
        HttpRequests.getInstance()
                .postJsonData(head, path, jsonObj)
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onNext(ResponseBody body) {
                        try {
                            callbackData(new JSONObject(body.string()), dataType);
                        } catch (Exception e) {
                            loadFailHandle();
                            ExceptionUtil.handle(e);
                        }
                        LogUtil.i(LogUtil.LOG_HTTP, "onNext");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        loadFailHandle();
                        LogUtil.i(LogUtil.LOG_HTTP, "error message : " + throwable.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        // 结束处理
                        stopAnimation();
                        LogUtil.i(LogUtil.LOG_HTTP, "onCompleted");
                    }
                });
    }

    /**
     * 加载网络数据
     */
    protected void loadSVData(String path, HashMap<String, String> map, int httpType, final int dataType) {
        loadSVData("", path, map, httpType, dataType);
    }

    /**
     * 加载网络数据
     */
    protected void loadSVData(String head, String path, HashMap<String, String> map, int httpType, final int dataType) {
        if (StringUtil.isNull(head)) {
            head = AppConfig.BASE_TYPE;
        }
        HttpRequests.getInstance()
                .loadData(head, path, map, httpType)
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onNext(ResponseBody body) {
                        try {
                            callbackData(new JSONObject(body.string()), dataType);
                        } catch (Exception e) {
                            loadFailHandle();
                            ExceptionUtil.handle(e);
                        }
                        LogUtil.i(LogUtil.LOG_HTTP, "onNext");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        /*if (throwable instanceof Fault) {
                            Fault fault = (Fault) throwable;
                            if (fault.getErrorCode() == 404) {
                                //错误处理
                            } else if (fault.getErrorCode() == 500) {
                                //错误处理
                            }
                        } else {
                            //错误处理
                        }*/
                        loadFailHandle();
                        LogUtil.i(LogUtil.LOG_HTTP, "error message : " + throwable.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        // 结束处理
                        stopAnimation();
                        LogUtil.i(LogUtil.LOG_HTTP, "onCompleted");
                    }
                });
    }

    /**
     * 上传文件到服务器
     */
    protected void uploadPushFile(File fileName, int fileType, final int dataType) {

        //1.创建MultipartBody.Builder对象
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        //2.获取图片，创建请求体
        RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), fileName);//表单类型

        //3.调用MultipartBody.Builder的addFormDataPart()方法添加表单数据
        builder.addFormDataPart("file", fileName.getName(), body); //添加图片数据，body创建的请求体
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("type", fileType);
        } catch (JSONException e) {
            ExceptionUtil.handle(e);
        }
        builder.addFormDataPart("json", jsonObj.toString()); //传入服务器需要的key，和相应value值

        //4.创建List<MultipartBody.Part> 集合，
        List<MultipartBody.Part> parts = builder.build().parts();

        //5.最后进行HTTP请求，传入parts即可
        HttpRequests.getInstance()
                .uploadFile(AppConfig.BASE_TYPE, AppConfig.URL_UPLOAD_PUSH, parts)
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onNext(ResponseBody body) {
                        try {
                            callbackData(new JSONObject(body.string()), dataType);
                        } catch (Exception e) {
                            loadFailHandle();
                            ExceptionUtil.handle(e);
                        }
                        LogUtil.i(LogUtil.LOG_HTTP, "onNext");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        loadFailHandle();
                        LogUtil.i(LogUtil.LOG_HTTP, "error message : " + throwable.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        // 结束处理
                        stopAnimation();
                        LogUtil.i(LogUtil.LOG_HTTP, "onCompleted");
                    }
                });
    }

    /**
     * 回调网络数据
     */
    protected void callbackData(JSONObject jsonObject, int dataType) {
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_CART_COUNT: //获取购物数量
                    BaseEntity numEn = JsonUtils.getCartGoodsNum(jsonObject);
                    if (numEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        userManager.saveUserCartNum(numEn.getDataTotal());
                        updateCartGoodsNum();
                    }
                    break;
                case AppConfig.REQUEST_SV_GOODS_ATTR: //加载商品属性
                    BaseEntity<GoodsAttrEntity> baseEn = JsonUtils.getGoodsAttrData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        attrEn = baseEn.getData();
                        initAttrPopup();
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
            }
        } catch (JSONException e) {
            loadFailHandle();
            ExceptionUtil.handle(e);
        }
    }

    /**
     * 网络加载失败
     */
    protected void loadFailHandle() {
        stopAnimation();
    }

    /**
     * 显示缓冲动画
     */
    protected void startAnimation() {
        LoadDialog.show(mContext);
    }

    /**
     * 停止缓冲动画
     */
    protected void stopAnimation() {
        LoadDialog.hidden();
    }

    /**
     * 上传设备号至服务端
     */
    protected void postDeviceToken() {
        HashMap<String, String> map = new HashMap<>();
        map.put("deviceToken", UserManager.getInstance().getDeviceToken());
        loadSVData(AppConfig.URL_AUTH_DEVICE, map, HttpRequests.HTTP_POST, 0);
    }

    /**
     * 获取购物车商品数量
     */
    protected void loadCartGoodsNum() {
        HashMap<String, String> map = new HashMap<>();
        loadSVData(AppConfig.BASE_URL_3, AppConfig.URL_CART_COUNT, map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_CART_COUNT);
    }

    /**
     * 刷新购物车商品数量
     */
    protected void updateCartGoodsNum() {

    }

    /**
     * 加载商品属性数据
     */
    protected void loadGoodsAttrData(String code, GoodsAttrEntity gaEn) {
        secEn = new GoodsAttrEntity();
        if (gaEn != null) {
            secEn.setBuyNum(gaEn.getBuyNum());
            secEn.setS_id_1(gaEn.getS_id_1());
            secEn.setS_id_2(gaEn.getS_id_2());
            secEn.setS_id_3(gaEn.getS_id_3());
            secEn.setS_name_1(gaEn.getS_name_1());
            secEn.setS_name_2(gaEn.getS_name_2());
            secEn.setS_name_3(gaEn.getS_name_3());

            buyNumber = secEn.getBuyNum();
            select_id_1 = secEn.getS_id_1();
            select_id_2 = secEn.getS_id_2();
            select_id_3 = secEn.getS_id_3();
            select_name_1 = secEn.getS_name_1();
            select_name_2 = secEn.getS_name_2();
            select_name_3 = secEn.getS_name_3();
        } else {
            buyNumber = 1;
            select_id_1 = 0;
            select_id_2 = 0;
            select_id_3 = 0;
            select_name_1 = "";
            select_name_2 = "";
            select_name_3 = "";
        }
        if (goodsCode.equals(code) && attrEn != null) {
            initAttrPopup();
        } else {
            goodsCode = code;
            attrEn = null;
            cartPopupWindow = null;

            HashMap<String, String> map = new HashMap<>();
            map.put("goodsCode", goodsCode);
            loadSVData(AppConfig.URL_GOODS_ATTR, map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_GOODS_ATTR);
        }
    }

    /**
     * 弹出商品属性浮层
     */
    private void initAttrPopup() {
        if (attrEn == null) return;
        if (cartPopupWindow == null) {
            cartPopupView = View.inflate(mContext, R.layout.popup_view_attr, null);
            View view_finish = cartPopupView.findViewById(R.id.attr_view_finish);
            view_finish.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissAttrPopup();
                }
            });
            RelativeLayout rl_dismiss = cartPopupView.findViewById(R.id.attr_view_dismiss);
            rl_dismiss.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissAttrPopup();
                }
            });
            popupAnimShow = AnimationUtils.loadAnimation(mContext, R.anim.in_from_bottom);
            popupAnimShow.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    isShow = true;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            popupAnimGone = AnimationUtils.loadAnimation(mContext, R.anim.out_to_bottom);
            popupAnimGone.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    cartPopupWindow.dismiss();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            popupShowMain = cartPopupView.findViewById(R.id.attr_view_show_main);
            popupShowMain.startAnimation(popupAnimShow);

            iv_goods_img = cartPopupView.findViewById(R.id.attr_view_iv_show);
            tv_popup_price = cartPopupView.findViewById(R.id.attr_view_tv_price);
            tv_popup_number = cartPopupView.findViewById(R.id.attr_view_tv_sku_num);
            tv_popup_select = cartPopupView.findViewById(R.id.attr_view_tv_selected);

            TextView tv_popup_confirm = cartPopupView.findViewById(R.id.attr_view_tv_confirm);
            tv_popup_confirm.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rv_Adapter == null) return;
                    GoodsAttrEntity selectAttr = rv_Adapter.initSelectAttrValue(attrEn);
                    String attrsIdStr = selectAttr.getAttrIdStr();
                    String attrsNameStr = selectAttr.getAttrNameStr();
                    if (isShow) {
                        if (selectAttr.isSelect()) {
                            if (buyNumber > 0) {
                                isShow = false;
                                secEn.setAdd(true);
                                secEn.setBuyNum(buyNumber);
                                secEn.setAttrIdStr(attrsIdStr);
                                secEn.setAttrNameStr(attrsNameStr);
                                secEn.setSkuCode(rv_Adapter.getSkuCode(attrsIdStr));
                                secEn.setS_id_1(select_id_1);
                                secEn.setS_id_2(select_id_2);
                                secEn.setS_id_3(select_id_3);
                                secEn.setS_name_1(select_name_1);
                                secEn.setS_name_2(select_name_2);
                                secEn.setS_name_3(select_name_3);
                                dismissAttrPopup();
                                updateSelectAttrStr(secEn);
                            } else {
                                //提示缺货
                                CommonTools.showToast(getString(R.string.goods_attr_sku_0));
                            }
                        } else {
                            CommonTools.showToast(attrsNameStr);
                        }
                    }
                }
            });

            RecyclerView rv_attr = cartPopupView.findViewById(R.id.attr_view_rv);
            // 设置布局管理器
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            rv_attr.setLayoutManager(layoutManager);
            // 配置适配器
            GoodsAttrAdapter.AttrClickCallback apCallback = new GoodsAttrAdapter.AttrClickCallback() {

                @Override
                public void updateNumber(int number) {
                    buyNumber = number;
                }

                @Override
                public void setOnClick(GoodsAttrEntity data, int pos, int selectId, String selectName) {
                    switch (pos) {
                        case 0:
                            select_id_1 = selectId;
                            select_name_1 = selectName;
                            break;
                        case 1:
                            select_id_2 = selectId;
                            select_name_2 = selectName;
                            break;
                        case 2:
                            select_id_3 = selectId;
                            select_name_3 = selectName;
                            break;
                    }
                    updatePopupView();
                }

            };
            ArrayList<Integer> resLayout = new ArrayList<>();
            resLayout.add(R.layout.item_list_attr_item_1);
            resLayout.add(R.layout.item_list_attr_item_2);
            rv_Adapter = new GoodsAttrAdapter(mContext, resLayout, apCallback);
            rv_Adapter.updateData(attrEn, secEn);
            rv_attr.setAdapter(rv_Adapter);

            updatePopupView();

            cartPopupWindow = new PopupWindow(cartPopupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            cartPopupWindow.update();
            cartPopupWindow.setFocusable(true);
            cartPopupWindow.setOutsideTouchable(true);
            cartPopupWindow.showAtLocation(cartPopupView, Gravity.BOTTOM, 0, 0);
        } else {
            popupShowMain.startAnimation(popupAnimShow);
            cartPopupWindow.showAtLocation(cartPopupView, Gravity.BOTTOM, 0, 0);
            if (rv_Adapter != null) {
                rv_Adapter.updateData(attrEn, secEn);
                updatePopupView();
            }
        }
    }

    /**
     * 更新属性值
     */
    private void updatePopupView() {
        if (rv_Adapter == null) return;
        GoodsAttrEntity selectAttr = rv_Adapter.initSelectAttrValue(attrEn);
        String attrsIdStr = selectAttr.getAttrIdStr();
        String attrsNameStr = selectAttr.getAttrNameStr();
        if (selectAttr.isSelect()) {
            // 已选属性
            tv_popup_select.setText(getString(R.string.goods_attr_selected_1, attrsNameStr));
            // 刷新图片
            Glide.with(AppApplication.getAppContext())
                    .load(rv_Adapter.getSkuImage(attrsIdStr))
                    .apply(AppApplication.getShowOptions())
                    .into(iv_goods_img);
            // 刷新库存
            int skuNum = rv_Adapter.getSkuNum(attrsIdStr);
            if (buyNumber > skuNum) {
                buyNumber = skuNum;
                rv_Adapter.updateNumber(buyNumber);
            } else {
                if (buyNumber == 0 && skuNum > 0) {
                    buyNumber = 1;
                    rv_Adapter.updateNumber(buyNumber);
                }
            }
            tv_popup_number.setText(getString(R.string.goods_attr_sku_num, skuNum));
            // 刷新价格
            tv_popup_price.setText(df.format(rv_Adapter.getSkuPrice(attrsIdStr)));
        } else {
            // 提示选择
            tv_popup_select.setText(attrsNameStr);
        }
    }

    /**
     * 更新已选的商品属性
     */
    protected void updateSelectAttrStr(GoodsAttrEntity attrEn) {

    }

    /**
     * 关闭商品属性浮层
     */
    private void dismissAttrPopup() {
        if (popupShowMain != null) {
            popupShowMain.startAnimation(popupAnimGone);
        }
    }

    static class TimeOutHandler extends Handler {

        WeakReference<BaseActivity> mActivity;

        TimeOutHandler(BaseActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            mActivity.get().openLoginActivity();
        }
    }

}