package com.songbao.sxb.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songbao.sxb.AppApplication;
import com.songbao.sxb.AppConfig;
import com.songbao.sxb.activity.login.LoginActivity;
import com.songbao.sxb.dialog.LoadDialog;
import com.songbao.sxb.entity.BaseEntity;
import com.songbao.sxb.utils.CommonTools;
import com.songbao.sxb.utils.ExceptionUtil;
import com.songbao.sxb.utils.LogUtil;
import com.songbao.sxb.utils.StringUtil;
import com.songbao.sxb.utils.UserManager;
import com.songbao.sxb.utils.retrofit.Fault;
import com.songbao.sxb.utils.retrofit.HttpRequests;

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
	 * 数据刷新函数
	 */
	protected List<BaseEntity> updNewEntity(int newTotal, int oldTotal, List<? extends BaseEntity> newData,
												List<? extends BaseEntity> oldData, ArrayMap<String, Boolean> oldMap) {
		if (oldData == null || newData == null || oldMap == null) return null;
		if (oldTotal < newTotal) {
			List<BaseEntity> newLists = new ArrayList<BaseEntity>();
			BaseEntity newEn, oldEn;
			String dataId;
			int newCount = newTotal - oldTotal;
			if (newCount > newData.size()) {
				newCount = newData.size();
			}
			for (int i = 0; i < newCount; i++) {
				newEn = newData.get(i);
				if (newEn != null) {
					dataId = newEn.getEntityId();
					if (!StringUtil.isNull(dataId) && !oldMap.containsKey(dataId)) {
						// 添加至顶层
						newLists.add(newEn);
						oldMap.put(dataId, true);
						// 移除最底层
						if (oldData.size() >= 1) {
							oldEn = oldData.remove(oldData.size()-1);
							if (oldEn != null && oldMap.containsKey(oldEn.getEntityId())) {
								oldMap.remove(oldEn.getEntityId());
							}
						}
					}
				}
			}
			newLists.addAll(oldData);
			return newLists;
		}
		return null;
	}

	/**
	 * 数据去重函数
	 */
	protected List<BaseEntity> addNewEntity(List<? extends BaseEntity> newData,
												List<? extends BaseEntity> oldData, ArrayMap<String, Boolean> oldMap) {
		if (oldData == null || newData == null || oldMap == null) return null;
		List<BaseEntity> newLists = new ArrayList<>();
		newLists.addAll(oldData);
		BaseEntity newEn;
		String dataId;
		for (int i = 0; i < newData.size(); i++) {
			newEn = newData.get(i);
			if (newEn != null) {
				dataId = newEn.getEntityId();
				if (!StringUtil.isNull(dataId) && !oldMap.containsKey(dataId)) {
					newLists.add(newEn);
					oldMap.put(dataId, true);
				}
			}
		}
		return newLists;
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
			head = "base_1";
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
