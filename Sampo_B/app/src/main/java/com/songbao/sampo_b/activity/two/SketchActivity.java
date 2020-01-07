package com.songbao.sampo_b.activity.two;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.activity.BaseActivity;
import com.songbao.sampo_b.utils.ClickUtils;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.widgets.ObservableWebView;

import butterknife.BindView;


/**
 * "设计效果图展示"Activity
 */
public class SketchActivity extends BaseActivity {

	String TAG = SketchActivity.class.getSimpleName();

	// WebView组件
	@BindView(R.id.sketch_web)
	ObservableWebView myWebView;

	@BindView(R.id.sketch_tv_click)
	TextView tv_click;

	private String titleStr, lodUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sketch);

		Bundle bundle = getIntent().getExtras();
		titleStr = bundle.getString("title");
		lodUrl = bundle.getString("lodUrl");

		initView();
	}

	private void initView() {
		setTitle(titleStr);

		tv_click.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ClickUtils.isDoubleClick()) return;
				openDesignerActivity("goodsId");
			}
		});

		initWebView();
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
			webSettings.setJavaScriptEnabled(true); //设置支持javascript脚本
			webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); //设置缓冲的模式
			webSettings.setBuiltInZoomControls(false); //设置是否支持缩放
			webSettings.setBlockNetworkImage(false); //解决图片不显示
			webSettings.setDomStorageEnabled(true); //是否允许使用Dom缓存
			webSettings.setUseWideViewPort(true);  //设置推荐使用的窗口
			webSettings.setLoadWithOverviewMode(true);  //设置加载页面的模式

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

			//加载Url
			myWebView.loadUrl(lodUrl);
		}
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

}
