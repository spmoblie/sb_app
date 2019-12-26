package com.songbao.sampo_b.activity.common;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.activity.BaseActivity;
import com.songbao.sampo_b.entity.ShareEntity;
import com.songbao.sampo_b.utils.ExceptionUtil;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.utils.StringUtil;
import com.songbao.sampo_b.widgets.ObservableWebView;
import com.songbao.sampo_b.widgets.WebViewLoadingBar;

import butterknife.BindView;


/**
 * "Html页面展示"Activity
 */
public class MyWebViewActivity extends BaseActivity {

	String TAG = MyWebViewActivity.class.getSimpleName();

	private static final int TYPE_LOAD_URL_SUCCESS = 1001;
	private static final int TYPE_LOAD_IMG_SUCCESS = 1002;

	// WebView组件
	@BindView(R.id.my_web_view)
	ObservableWebView myWebView;

	@BindView(R.id.web_view_loading_bar)
	WebViewLoadingBar webViewLoadingBar;

	private ShareEntity shareEn;
	private String titleStr, lodUrl;
	private Bitmap shareBm;
	private int saveY = 0;
	private boolean isScroll = false;

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case TYPE_LOAD_URL_SUCCESS:
					if (myWebView != null && isScroll && saveY > 0) {
						myWebView.scrollTo(0, saveY);
						saveY = 0;
						isScroll = false;
					}
					break;
				case TYPE_LOAD_IMG_SUCCESS:
					initShareData(shareBm);
					break;
			}
		}
	};

	private void initShareData(Bitmap bm) {
		if (shareEn != null) {
			shareEn.setShareBm(bm);
			setRightViewBackground(R.mipmap.icon_share);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isInitShare = true;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_webview);

		Bundle bundle = getIntent().getExtras();
		titleStr = bundle.getString("title");
		lodUrl = bundle.getString("lodUrl");
		shareEn = (ShareEntity) bundle.getSerializable(AppConfig.PAGE_DATA);

		initView();
	}

	private void initView() {
		setTitle(titleStr);

		//loadShareImg();
		initWebView();
	}

	private void loadShareImg() {
		if (shareEn != null && !StringUtil.isNull(shareEn.getImageUrl())) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					FutureTarget<Bitmap> ft = Glide
							.with(AppApplication.getAppContext())
							.asBitmap()
							.load(shareEn.getImageUrl())
							.submit();
					try{
						shareBm = ft.get();
						if (shareBm != null) {
							mHandler.sendEmptyMessage(TYPE_LOAD_IMG_SUCCESS);
						}
					}catch (Exception e) {
						ExceptionUtil.handle(e);
					}
				}
			}).start();
		}
	}

	@SuppressWarnings("static-access")
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	private void initWebView() {
		if (myWebView != null){
			//WebView属性设置
			WebSettings webSettings = myWebView.getSettings();
			webSettings.setDefaultTextEncodingName("UTF-8");
			//String user_agent = webSettings.getUserAgentString();
			//webSettings.setUserAgentString(user_agent+"_SP"); //设置UserAgent
			//webSettings.setUserAgentString(" "); //设置UserAgent
			webSettings.setJavaScriptEnabled(true); //设置支持javascript脚本
			//webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
			webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); //设置缓冲的模式
			webSettings.setBuiltInZoomControls(false); //设置是否支持缩放
			webSettings.setBlockNetworkImage(false); //解决图片不显示
			//webSettings.setSupportZoom(true); //设置是否支持变焦
			//webSettings.setDefaultFontSize(12); //设置默认的字体大小
			//webSettings.setFixedFontFamily(""); //设置固定使用的字体
			//webSettings.setAllowFileAccess(true); //是否允许访问文件
			//webSettings.setDatabaseEnabled(true); //是否允许使用数据库api
			webSettings.setDomStorageEnabled(true); //是否允许使用Dom缓存
			//webSettings.setAppCacheEnabled(true); //有选择的缓存web浏览器中的东西
			//webSettings.setAppCachePath(""); //设置缓存路径
			//webSettings.setSavePassword(true); //是否允许保存密码
			webSettings.setUseWideViewPort(true);  //设置推荐使用的窗口
			webSettings.setLoadWithOverviewMode(true);  //设置加载页面的模式
			//webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局

			//设置可同时加载Https、Http的混合模式（解决微信链文图片不显示的问题）
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
				webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

			//开启硬件加速(华为部分手机会出现卡顿)
			myWebView.setLayerType(View.LAYER_TYPE_HARDWARE,null);

			//设置不允许外部浏览器打开
			myWebView.setWebViewClient(new WebViewClient(){

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					if (url.contains("sxb")) {
						view.loadUrl(url);
						return true; //当加载重定向URL时，物理返回按键myWebView.canGoBack()判断为true。
					}
					return false;
				}
			});

			//设置加载动画
			myWebView.setWebChromeClient(new WebChromeClient(){
				@Override
				public void onProgressChanged(WebView view, int newProgress) {
					super.onProgressChanged(view, newProgress);
					if(webViewLoadingBar != null) {
						webViewLoadingBar.setProgress(newProgress);
					}
					if (newProgress == 100) {
						mHandler.sendEmptyMessage(TYPE_LOAD_URL_SUCCESS);
					}
				}
			});

			//设置滚动监听
			myWebView.setOnScrollChangedCallback(new ObservableWebView.OnScrollChangedCallback() {
				@Override
				public void onScroll(int x, int y, int oldx, int oldy) {
					//currY = y;
				}
			});

			//加载Url
			if (!StringUtil.isNull(lodUrl)) {
				//myWebView.addJavascriptInterface(new JsToJava(), "stub");
				myLoadUrl(lodUrl);
			}
		}
	}

	private void myLoadUrl(String url) {
		/*if (isSynCookies) {
			HttpUtil.synCookies(url); //同步Cookies
		}*/
		myWebView.loadUrl(url);
	}

	@Override
	public void OnListenerRight() {
		super.OnListenerRight();
		showShareView(shareEn);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (myWebView.canGoBack()) {
				//当WebView不是处于第一页面时，返回上一个页面
				myWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
				myWebView.goBack();
				return true;
			} else {
				//当WebView处于第一页面时,直接退出
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);

		super.onResume();
	}
	
	@Override
	protected void onPause() {
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onPause");
		// 页面结束
		AppApplication.onPageEnd(this, TAG);

		super.onPause();
	}

	@Override
	protected void onDestroy() {
		//清除缓存
		if (myWebView != null) {
			myWebView.clearCache(true);
		}
		super.onDestroy();
	}

	class JsToJava {
		@JavascriptInterface
		public void jsMethod(String goodsId) {

		}

		@JavascriptInterface
		public void sendComment(final String commentStr) {
			new Handler().post(new Runnable() {
				@Override
				public void run() {
					myWebView.loadUrl("javascript: submitComment('" + commentStr + "')");
				}
			});
		}
	}

}
