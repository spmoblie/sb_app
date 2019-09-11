package com.sbwg.sxb.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.text.InputType;
import android.text.TextUtils;
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
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppConfig;
import com.sbwg.sxb.AppManager;
import com.sbwg.sxb.R;
import com.sbwg.sxb.activity.login.LoginActivity;
import com.sbwg.sxb.dialog.DialogManager;
import com.sbwg.sxb.dialog.LoadDialog;
import com.sbwg.sxb.entity.ShareEntity;
import com.sbwg.sxb.utils.CommonTools;
import com.sbwg.sxb.utils.ExceptionUtil;
import com.sbwg.sxb.utils.LogUtil;
import com.sbwg.sxb.utils.MyCountDownTimer;
import com.sbwg.sxb.utils.retrofit.Fault;
import com.sbwg.sxb.utils.retrofit.HttpRequests;
import com.sbwg.sxb.widgets.share.ShareView;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import rx.Observer;

/**
 * 所有Activity的父类
 */
public  class BaseActivity extends FragmentActivity implements IWeiboHandler.Response, IWXAPIEventHandler {

	public static final String TAG = BaseActivity.class.getSimpleName();
	public static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;
	public static final long SEND_TIME = 60000;

	protected Context mContext;
	protected SharedPreferences shared;
	protected Editor editor;
	protected IWXAPI api;
	protected DialogManager myDialog;
	protected Boolean isInitShare = false;
	protected Boolean isTimeFinish = true;
	protected int screenWidth, screenHeight, statusHeight, titleHeight;

	private LinearLayout ll_head;
	private ImageView iv_left;
	private TextView tv_title;
	private Button bt_right;
	private ViewFlipper mLayoutBase;
	private MyCountDownTimer mcdt;

	private int dialogWidth;
	private ShareView mShareView;
	private Animation inAnim, outAnim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_base);
		// 创建动画
		overridePendingTransition(R.anim.in_from_right, R.anim.anim_no_anim);

		LogUtil.i(TAG, "onCreate()");
		AppManager.getInstance().addActivity(this);

		mContext = this;
		shared = AppApplication.getSharedPreferences();
		editor = shared.edit();
		editor.apply();

		titleHeight = shared.getInt(AppConfig.KEY_TITLE_HEIGHT, 0);
		statusHeight = shared.getInt(AppConfig.KEY_STATUS_HEIGHT, 0);
		screenHeight = shared.getInt(AppConfig.KEY_SCREEN_HEIGHT, 0);
		screenWidth = shared.getInt(AppConfig.KEY_SCREEN_WIDTH, 0);
		dialogWidth = screenWidth * 2/3;
		myDialog = DialogManager.getInstance(mContext);

		api = WXAPIFactory.createWXAPI(mContext, AppConfig.WX_APP_ID);
		api.registerApp(AppConfig.WX_APP_ID);

		// 设置App字体不随系统字体变化
		AppApplication.initDisplayMetrics();

		// 推送服务统计应用启动数据
		AppApplication.onPushAppStartData();

		findViewById();
		initView();

		if (isInitShare) {
			try { //初始化ShareView
				View view = getLayoutInflater().inflate(R.layout.popup_share_view, (ViewGroup) findViewById(R.id.base_fl_main));
				mShareView = new ShareView(mContext, this, view, null);
				mShareView.showShareLayer(false);
			} catch (Exception e) {
				ExceptionUtil.handle(e);
			}
		}
	}

	private void findViewById() {
		ll_head = findViewById(R.id.top_bar_head_ll_main);
		iv_left = findViewById(R.id.top_bar_left);
		tv_title = findViewById(R.id.top_bar_title);
		bt_right = findViewById(R.id.top_bar_right);
		mLayoutBase = findViewById(R.id.base_ll_container);
	}

	@SuppressWarnings("static-access")
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

		inAnim = new AnimationUtils().loadAnimation(mContext, R.anim.in_from_right);
		outAnim = new AnimationUtils().loadAnimation(mContext, R.anim.out_to_left);
	}

	/**
	 * 设置头部是否可见
	 */
	protected void setHeadVisibility(int visibility) {
		switch (visibility) {
			case View.VISIBLE:
				if (ll_head.getVisibility() == View.GONE) {
					ll_head.clearAnimation();
					ll_head.startAnimation(inAnim);
				}
				break;
			case View.GONE:
				if (ll_head.getVisibility() == View.VISIBLE) {
					ll_head.clearAnimation();
					ll_head.startAnimation(outAnim);
				}
				break;
		}
		ll_head.setVisibility(visibility);
	}

	/**
	 * 设置头部View背景色
	 */
	protected void setHeadBackground(int color){
		ll_head.setBackgroundColor(color);
	}

	/**
	 * 设置左边按钮是否可见
	 */
	protected void setBtnLeftGone(int visibility){
		iv_left.setVisibility(visibility);
	}

	/**
	 * 设置右边按钮是否可见
	 */
	protected void setRightViewGone(int visibility){
		bt_right.setVisibility(visibility);
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
	 * 设置右边按钮背景图片资源对象
	 */
	@SuppressLint("NewApi")
	protected void setRightViewBackground(Drawable btnRight) {
		setRightViewGone(View.VISIBLE);
		bt_right.setBackground(btnRight);
	}

	/**
	 * 设置右边按钮背景图片资源Id
	 */
	protected void setRightViewBackground(int drawableId){
		setRightViewGone(View.VISIBLE);
		bt_right.setBackgroundResource(drawableId);
	}

	/**
	 * 设置右边按钮显示文本
	 */
	protected void setRightViewText(String text){
		setRightViewGone(View.VISIBLE);
		bt_right.setText(text);
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

	/**
	 * 左键键监听执行方法，让子类重写该方法
	 */
	protected void OnListenerLeft(){
		if (mShareView != null && mShareView.isShowing()) {
			mShareView.showShareLayer(false);
		} else {
			finish();
		}
		finish();
	}

	/**
	 * 右键监听执行方法，让子类重写该方法
	 */
	protected void OnListenerRight(){

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

	protected void openLoginActivity(){
		openLoginActivity(TAG);
	}

	protected void openLoginActivity(String rootPage){
		Intent intent = new Intent(mContext, LoginActivity.class);
		intent.putExtra("rootPage", rootPage);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
		Intent intent = new Intent(this, pClass);
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
			mShareView.onNewIntent(intent, this, this);
		}
		super.onNewIntent(intent);
	}

	@Override
	public void onReq(BaseReq arg0) {

	}

	@Override
	public void onResp(BaseResp arg0) {

	}

	@Override
	public void onResponse(BaseResponse arg0) {

	}

	@Override
	protected void onResume() {
		LogUtil.i(TAG, "onResume()");
		// 设置App字体不随系统字体变化
		AppApplication.initDisplayMetrics();
		super.onResume();
	}

	@Override
	protected void onPause() {
		LogUtil.i(TAG, "onPause()");
		if (myDialog != null) {
			myDialog.clearInstance();
		}
		// 缓存标题View高度
		if (titleHeight <= 0) {
			editor.putInt(AppConfig.KEY_TITLE_HEIGHT, ll_head.getHeight()).apply();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		LogUtil.i(TAG, "onDestroy()");
		super.onDestroy();
	}

	@Override
	public void finish() {
		super.finish();
		// 销毁动画
		overridePendingTransition(R.anim.anim_no_anim, R.anim.out_to_right);
	}

	/**
	 * 登入超时对话框
	 */
	protected void showTimeOutDialog() {
//		openLoginActivity(TAG);
	}

	protected void showTimeOutDialog(final String rootPage) {
		AppApplication.AppLogout(true);
		showErrorDialog(getString(R.string.login_timeout), true, new Handler(){
			@Override
			public void handleMessage(Message msg) {
//				openLoginActivity(rootPage);
			}
		});
	}

	/**
	 * 分享参数出错提示
	 */
	protected void showShareError() {
		CommonTools.showToast(getString(R.string.share_msg_entity_error));
	}

	/**
	 * 加载数据出错提示
	 */
	protected void showServerBusy() {
		showErrorDialog(R.string.toast_server_busy);
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
		myDialog.showOneBtnDialog(content, dialogWidth, true, isVanish, handler, null);
	}

	/**
	 * 双按钮确认对话框
	 */
	protected void showConfirmDialog(int contentResId, String positiveBtnStr, String negativeBtnStr,
									 boolean isCenter, boolean isVanish, final Handler handler) {
		showConfirmDialog(getString(contentResId), positiveBtnStr, negativeBtnStr, isCenter, isVanish, handler);
	}

	protected void showConfirmDialog(String content, String positiveBtnStr, String negativeBtnStr,
									 boolean isCenter, boolean isVanish, final Handler handler) {
		showConfirmDialog(null, content, positiveBtnStr, negativeBtnStr, isCenter, isVanish, handler);
	}

	protected void showConfirmDialog(String title, String content, String positiveBtnStr, String negativeBtnStr,
									 boolean isCenter, boolean isVanish, final Handler handler) {
		showConfirmDialog(title, content, positiveBtnStr, negativeBtnStr, dialogWidth, isCenter, isVanish, handler);
	}

	protected void showConfirmDialog(String title, String content, String positiveBtnStr, String negativeBtnStr,
									 int width, boolean isCenter, boolean isVanish, final Handler handler) {
		positiveBtnStr = (positiveBtnStr == null) ? getString(R.string.confirm) : positiveBtnStr;
		negativeBtnStr = (negativeBtnStr == null) ? getString(R.string.cancel) : negativeBtnStr;
		myDialog.showTwoBtnDialog(title, content, positiveBtnStr, negativeBtnStr, width, isCenter, isVanish, handler);
	}

	/**
	 * 可输入的对话框
	 */
	protected void showEditDialog(String title, int inputType, boolean isVanish, final Handler handler) {
		myDialog.showEditDialog(title, dialogWidth, inputType, isVanish, handler);
	}

	/**
	 * 列表展示对话框
	 */
	protected void showListDialog(int contentResId, CharSequence[] items, boolean isCenter, final Handler handler) {
		showListDialog(contentResId, items, dialogWidth, isCenter, handler);
	}

	protected void showListDialog(int contentResId, CharSequence[] items, int width, boolean isCenter, final Handler handler) {
		showListDialog(getString(contentResId), items, width, isCenter, handler);
	}

	protected void showListDialog(String content, CharSequence[] items, boolean isCenter, final Handler handler) {
		showListDialog(content, items, dialogWidth, isCenter, handler);
	}

	protected void showListDialog(String content, CharSequence[] items, int width, boolean isCenter, final Handler handler) {
		myDialog.showListItemDialog(content, items, width, isCenter, handler);
	}

	/**
	 * 显示分享View
	 */
	protected void showShareView(ShareEntity shareEn){
		if (mShareView != null && shareEn != null) {
			if (mShareView.getShareEntity() == null) {
				mShareView.setShareEntity(shareEn);
			}
			if (mShareView.isShowing()) {
				mShareView.showShareLayer(false);
			} else {
//				if (!UserManager.getInstance().checkIsLogined()) {
//					openLoginActivity();
//					return;
//				}
				mShareView.showShareLayer(true);
			}
		}else {
			showShareError();
		}
	}

	/**
	 * 动态检查权限
	 */
	protected boolean checkPermission() {
		List<String> mPermissionList = new ArrayList<>();
		String[] permissions = AppConfig.PERMISSIONS;
		// 判断哪些权限未授予
		for (int i = 0; i < permissions.length; i++) {
			if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
				mPermissionList.add(permissions[i]);
			}
		}
		if (!mPermissionList.isEmpty()) {
			// 请求授权
			String[] permissions_new = mPermissionList.toArray(new String[mPermissionList.size()]);
			ActivityCompat.requestPermissions(this, permissions_new, 1);
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 切换View背景的状态
	 */
	protected void changeViewState(View view, boolean isState) {
		if (view == null) return;
		if (isState) {
			view.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_button_style_2_20, null));
		} else {
			view.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_button_style_3_20, null));
		}
	}

	/**
	 * 切换密码输入框密码显示的状态
	 */
	protected void changeEditTextPassword(EditText et_view, boolean isShow) {
		if (et_view == null) return;
		if (isShow) { //显示密码
			et_view.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		}else { //隐藏密码
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
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏
	}

	/**
	 * 隐藏物理键盘
	 */
	protected void toggleSoftInput() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 开启倒计时
	 */
	protected void startTimer(TextView tv_time, long time) {
		if (tv_time == null) return;
		stopTimer();
		mcdt = new MyCountDownTimer(tv_time, time, 1000,
				new MyCountDownTimer.MyTimerCallback() {
					@Override
					public void onFinish() {
						onTimerFinish();
					}
				});
		mcdt.start(); //开始倒计时
		isTimeFinish = false;
	}

	/**
	 * 取消倒计时
	 */
	protected void stopTimer() {
		if (mcdt != null) {
			mcdt.cancel();
			mcdt = null;
		}
	}

	/**
	 * 倒计时结束执行
	 */
	protected void onTimerFinish() {
		isTimeFinish = true;
	}

	/**
	 * 加载网络数据
	 * @param path
	 * @param map
	 * @param httpType
	 * @param dataType
	 */
	protected void loadSVData(String path, HashMap<String, String> map, int httpType, final int dataType) {
		HttpRequests.getInstance()
				.loadDatas(path, map, httpType)
				.subscribe(new Observer<ResponseBody>() {
					@Override
					public void onNext(ResponseBody body) {
						try {
							callbackData(new JSONObject(body.string()), dataType);
						} catch (Exception e) {
							ExceptionUtil.handle(e);
							loadFailHandle();
						}
						LogUtil.i("Retrofit","onNext");
					}

					@Override
					public void onError(Throwable throwable) {
						if (throwable instanceof Fault) {
							Fault fault = (Fault) throwable;
							if (fault.getErrorCode() == 404) {
								//错误处理
							} else
							if (fault.getErrorCode() == 500) {
								//错误处理
							}
						} else {
							//错误处理
						}
						loadFailHandle();
						LogUtil.i("Retrofit","error message : " + throwable.getMessage());
					}

					@Override
					public void onCompleted() {
						// 结束处理
						stopAnimation();
						LogUtil.i("Retrofit","onCompleted");
					}
				});
	}

	/**
	 * 回调网络数据
	 */
	protected void callbackData(JSONObject jsonObject, int dataType) {}

	/**
	 * 显示缓冲动画
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

}