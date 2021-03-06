package com.songbao.sampo_b.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.AppManager;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.activity.common.MyWebViewActivity;
import com.songbao.sampo_b.activity.common.ViewPagerActivity;
import com.songbao.sampo_b.activity.common.photo.ClipImageCircularActivity;
import com.songbao.sampo_b.activity.common.photo.ClipImageSquareActivity;
import com.songbao.sampo_b.activity.common.photo.PhotoAlbumActivity;
import com.songbao.sampo_b.activity.common.photo.PhotoOneActivity;
import com.songbao.sampo_b.activity.login.LoginAccountActivity;
import com.songbao.sampo_b.activity.mine.CustomizeGoodsActivity;
import com.songbao.sampo_b.activity.mine.CustomizeListActivity;
import com.songbao.sampo_b.activity.mine.CustomizeOrderActivity;
import com.songbao.sampo_b.activity.two.GoodsActivity;
import com.songbao.sampo_b.activity.two.GoodsEditActivity;
import com.songbao.sampo_b.activity.two.GoodsSortActivity;
import com.songbao.sampo_b.activity.two.SketchActivity;
import com.songbao.sampo_b.dialog.DialogManager;
import com.songbao.sampo_b.dialog.LoadDialog;
import com.songbao.sampo_b.entity.BaseEntity;
import com.songbao.sampo_b.entity.GoodsEntity;
import com.songbao.sampo_b.entity.ShareEntity;
import com.songbao.sampo_b.utils.CommonTools;
import com.songbao.sampo_b.utils.ExceptionUtil;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.utils.MyCountDownTimer;
import com.songbao.sampo_b.utils.StringUtil;
import com.songbao.sampo_b.utils.UserManager;
import com.songbao.sampo_b.utils.retrofit.HttpRequests;

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
    protected boolean isTimeFinish = true;
    protected boolean isHideCostPrice = false;
    protected double ratios;
    protected int screenWidth, screenHeight, statusHeight, dialogWidth;

    private ConstraintLayout top_main;
    private ImageView iv_left, iv_data_null;
    private TextView tv_title, tv_data_null;
    private Button bt_right;
    private ViewFlipper mLayoutBase;
    private MyCountDownTimer myTimer;

    private Animation inAnim, outAnim;

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
        ratios = userManager.getUserRatios(); //经销商销售系数
        isHideCostPrice = userManager.getUserRoleIds() == 7; //门店设计师角色登录

        // 推送服务统计应用启动数据
        AppApplication.onPushAppStartData();

        findViewById();
        initView();
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
        finish();
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
     * 设置右边按钮显示文本颜色
     */
    protected void setRightViewTextColor(int colorId) {
        bt_right.setTextColor(ContextCompat.getColor(mContext, colorId));
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
        AppManager.getInstance().finishActivity(PhotoAlbumActivity.class);
        AppManager.getInstance().finishActivity(PhotoOneActivity.class);
        AppManager.getInstance().finishActivity(ClipImageSquareActivity.class);
        AppManager.getInstance().finishActivity(ClipImageCircularActivity.class);
    }

    /**
     * 关闭登录相关Activity
     */
    protected void closeLoginActivity() {
        AppManager.getInstance().finishActivity(LoginAccountActivity.class);
        postDeviceToken();
    }

    /**
     * 关闭订制产品相关Activity
     */
    protected void closeCustomizeActivity() {
        // 关闭效果图展示页面
        AppManager.getInstance().finishActivity(SketchActivity.class);
        // 关闭产品详情页面
        AppManager.getInstance().finishActivity(GoodsActivity.class);
        // 关闭产品分类页面
        AppManager.getInstance().finishActivity(GoodsSortActivity.class);
    }

    /**
     * 关闭查看订单相关Activity
     */
    protected void closeOrderActivity() {
        // 关闭订单产品页面
        AppManager.getInstance().finishActivity(CustomizeGoodsActivity.class);
        // 关闭订单详情页面
        AppManager.getInstance().finishActivity(CustomizeOrderActivity.class);
        // 关闭订单列表页面
        AppManager.getInstance().finishActivity(CustomizeListActivity.class);
    }

    /**
     * 打开登录Activity
     */
    protected void openLoginActivity() {
        shared.edit().putBoolean(AppConfig.KEY_JUMP_PAGE, false).apply();
        Intent intent = new Intent(mContext, LoginAccountActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * 打开商品详情页
     */
    protected void openGoodsActivity(String goodsCode) {
        Intent intent = new Intent(mContext, GoodsActivity.class);
        intent.putExtra("goodsCode", goodsCode);
        startActivity(intent);
    }

    /**
     * 打开商品编辑页
     */
    protected void openGoodsEditActivity(GoodsEntity goodsEn) {
        Intent intent = new Intent(mContext, GoodsEditActivity.class);
        if (goodsEn != null) {
            intent.putExtra(AppConfig.PAGE_DATA, goodsEn);
        }
        startActivity(intent);
    }

    /**
     * 跳转至WebView
     * @param title
     * @param url
     */
    protected void openWebViewActivity(String title, String url) {
        openWebViewActivity(title, url, null);
    }

    /**
     * 跳转至WebView
     * @param title
     * @param url
     * @param shareEn
     */
    protected void openWebViewActivity(String title, String url, ShareEntity shareEn) {
        Intent intent = new Intent(mContext, MyWebViewActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("lodUrl", url);
        if (shareEn != null) {
            intent.putExtra(AppConfig.PAGE_DATA, shareEn);
        }
        startActivity(intent);
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
     * 打开系统文件管理器
     */
    protected void openSystemFile(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);//打开多个文件
        try{
            startActivityForResult(Intent.createChooser(intent,"请选择文件"),1);
        }catch (ActivityNotFoundException e){
            ExceptionUtil.handle(e);
            CommonTools.showToast("请安装文件管理器");
        }
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
     * 弹出用户同意对话框
     */
    protected void showUserAgreeDialog(String title, SpannableString spannableStr, String leftBtnStr, String rightBtnStr, Handler handler, int handlerCode) {
        leftBtnStr = (leftBtnStr == null) ? getString(R.string.cancel) : leftBtnStr;
        rightBtnStr = (rightBtnStr == null) ? getString(R.string.confirm) : rightBtnStr;
        if (myDialog == null) {
            myDialog = DialogManager.getInstance(mContext);
        }
        myDialog.showUserAgreeDialog(title, spannableStr, leftBtnStr, rightBtnStr, dialogWidth, handler, handlerCode);
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
     * 切换View背景的状态
     */
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
     * 复制文本内容到剪贴板
     */
    protected void copyString(String copyStr, String toastStr) {
        if (!StringUtil.isNull(copyStr)) {
            ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clip != null) {
                clip.setPrimaryClip(ClipData.newPlainText(null, copyStr));
                if (clip.hasPrimaryClip()){
                    clip.getPrimaryClip().getItemAt(0).getText();
                }
                CommonTools.showToast(toastStr);
            }
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
    protected void loadSVData(String path, HashMap<String, Object> map, int httpType, final int dataType) {
        loadSVData("", path, map, httpType, dataType);
    }

    /**
     * 加载网络数据
     */
    protected void loadSVData(String head, String path, HashMap<String, Object> map, int httpType, final int dataType) {
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
        HashMap<String, Object> map = new HashMap<>();
        map.put("deviceToken", UserManager.getInstance().getDeviceToken());
        loadSVData(AppConfig.URL_AUTH_DEVICE, map, HttpRequests.HTTP_POST, 0);
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