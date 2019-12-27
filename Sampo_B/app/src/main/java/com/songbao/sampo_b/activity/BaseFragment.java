package com.songbao.sampo_b.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.activity.common.MyWebViewActivity;
import com.songbao.sampo_b.activity.login.LoginActivity;
import com.songbao.sampo_b.activity.two.GoodsActivity;
import com.songbao.sampo_b.dialog.LoadDialog;
import com.songbao.sampo_b.entity.BaseEntity;
import com.songbao.sampo_b.utils.CommonTools;
import com.songbao.sampo_b.utils.ExceptionUtil;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.utils.StringUtil;
import com.songbao.sampo_b.utils.UserManager;
import com.songbao.sampo_b.utils.retrofit.Fault;
import com.songbao.sampo_b.utils.retrofit.HttpRequests;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Observer;


public class BaseFragment extends Fragment {

	String TAG = BaseFragment.class.getSimpleName();

	protected SharedPreferences shared;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onCreate");
		shared = AppApplication.getSharedPreferences();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return null;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onResume() {
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
		super.onResume();
	}

	@Override
	public void onPause() {
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onPause");
		super.onPause();
	}

	@Override
	public void onDestroy() {
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onDestroy");
		super.onDestroy();
	}

	/**
	 * 校验登录状态
	 */
	protected boolean isLogin() {
		return UserManager.getInstance().checkIsLogin();
	}

	/**
	 * 打开登录Activity
	 */
	protected void openLoginActivity(){
		shared.edit().putBoolean(AppConfig.KEY_JUMP_PAGE, true).apply();
		Intent intent = new Intent(getActivity(), LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	/**
	 * 打开商品详情页
	 */
	protected void openGoodsActivity(String skuCode) {
		Intent intent = new Intent(getActivity(), GoodsActivity.class);
		intent.putExtra("skuCode", skuCode);
		startActivity(intent);
	}

	/**
	 * 跳转至WebView
	 * @param title
	 * @param url
	 */
	protected void openWebViewActivity(String title, String url) {
		Intent intent = new Intent(getActivity(), MyWebViewActivity.class);
		intent.putExtra("title", title);
		intent.putExtra("lodUrl", url);
		startActivity(intent);
	}

	/**
	 * 刷新数据
	 */
	protected <T extends BaseEntity> void refreshData(int newTotal, int oldTotal, List<T> newData,
													  List<T> oldData, ArrayMap<String, Boolean> cacheMap) {
		if (oldData == null || newData == null) return;
		int newCount = newTotal - oldTotal;
		if (newCount > 0) {
			if (newCount > newData.size()) {
				newCount = newData.size();
			}
			List<T> newLists = new ArrayList<>();
			T newEn, oldEn;
			for (int i = 0; i < newCount; i++) {
				newEn = newData.get(i);
				if (newEn != null) {
					// 添加至顶层
					newLists.add(newEn);
					cacheMap.put(newEn.getEntityId(), true);
					// 移除最底层
					if (oldData.size() > 0) {
						oldEn = oldData.remove(oldData.size() - 1);
						if (oldEn != null && cacheMap.containsKey(oldEn.getEntityId())) {
							cacheMap.remove(oldEn.getEntityId());
						}
					}
				}
			}
			newLists.addAll(oldData);
			oldData.clear();
			oldData.addAll(newLists);
		}
	}

	/**
	 * 过滤数据
	 */
	protected <T extends BaseEntity>List<T> filterData(List<T> newData, ArrayMap<String, Boolean> cacheMap) {
		if (newData == null || cacheMap == null) return null;
		List<T> newList = new ArrayList<>();
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
	protected void postJsonData(String path, HashMap<String, String> map, int httpType, final int dataType) {
		loadSVData("", path, map, httpType, dataType);
	}

	/**
	 * 提交标准Json格式数据
	 */
	protected void postJsonData(String head, String path, HashMap<String, String> map, int httpType, final int dataType) {
		loadSVData(head, path, map, httpType, dataType);
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
						LogUtil.i(LogUtil.LOG_HTTP,"onNext");
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
						LogUtil.i(LogUtil.LOG_HTTP,"onError error message : " + throwable.getMessage());
					}

					@Override
					public void onCompleted() {
						// 结束处理
						stopAnimation();
						LogUtil.i(LogUtil.LOG_HTTP,"onCompleted");
					}
				});
	}

	/**
	 * 回调网络数据
	 */
	protected void callbackData(JSONObject jsonObject, int dataType) {}

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
		LoadDialog.show(getActivity());
	}

	/**
	 * 停止缓冲动画
	 */
	protected void stopAnimation() {
		LoadDialog.hidden();
	}

	/**
	 * 处理网络请求返回状态码
	 * @param baseEn
	 */
	protected void handleErrorCode(BaseEntity baseEn) {
		if (baseEn != null) {
			switch (baseEn.getErrno()) {
				case AppConfig.ERROR_CODE_TIMEOUT: //登录超时
					AppApplication.AppLogout();
					openLoginActivity();
					break;
			}
		}
	}

}
