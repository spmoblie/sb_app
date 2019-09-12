package com.sbwg.sxb.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;

import com.sbwg.sxb.dialog.LoadDialog;
import com.sbwg.sxb.entity.BaseEntity;
import com.sbwg.sxb.utils.CommonTools;
import com.sbwg.sxb.utils.ExceptionUtil;
import com.sbwg.sxb.utils.LogUtil;
import com.sbwg.sxb.utils.StringUtil;
import com.sbwg.sxb.utils.retrofit.Fault;
import com.sbwg.sxb.utils.retrofit.HttpRequests;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Observer;


public class BaseFragment extends Fragment {

	String TAG = BaseFragment.class.getSimpleName();
	ShowErrDialogListener showErrDialogListener;
	LoadingListener loadingListener;
	SoftKeyBoardListener softKeyBoardListener;
	RequestBlockingListener requestBlockingListener;

	public interface ShowErrDialogListener {
		public void showErrDialog(String msg);
	}

	public interface LoadingListener {
		public void onShowLoading();

		public void onHideLoading();
	}

	public interface SoftKeyBoardListener {
		public void onShowSoftKeyBoard();

		public void onHideSoftKeyBoard();
	}

	public interface RequestBlockingListener {
		public void onRequestBlock();

		public void onReleaseBlock();
	}

	//
	// public interface ShowErrDialogListener{
	// public void showErrDialog(String msg);
	// }

	public void setShowErrDialogListener(
			ShowErrDialogListener showErrDialogListener) {
		this.showErrDialogListener = showErrDialogListener;
	}

	public void setLoadingListener(LoadingListener loadingListener) {
		this.loadingListener = loadingListener;
	}

	public void setSoftKeyBoardListener(
			SoftKeyBoardListener softKeyBoardListener) {
		this.softKeyBoardListener = softKeyBoardListener;
	}

	public void setRequestBlockingListener(
			RequestBlockingListener requestBlockingListener) {
		this.requestBlockingListener = requestBlockingListener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return null;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public boolean onBackPressed() {
		return false;
	}

	public void onKeyBoardShow(boolean show) {

	}

	public void enableIntercept(boolean enable, View view) {
		if (enable) {
			if (view != null) {

				// view.setOnTouchListener(new OnTouchListener(){
				//
				// @Override
				// public boolean onTouch(View arg0, MotionEvent arg1) {
				// if(arg1.getAction()==MotionEvent.ACTION_DOWN){
				// LogUtil.i(TAG, "Touched Intercept view." );
				// }
				//
				// return true;
				// }
				// });
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						LogUtil.i(TAG, "Clicked Intercept view.");
					}

				});

			}
		} else {
			if (view != null) {

				// view.setOnTouchListener(null);
				view.setOnClickListener(null);

			}
		}
	}

	public void addFragment(BaseFragment fragment, String name, boolean needBack, int containerId) {
		fragment.setShowErrDialogListener(showErrDialogListener);
		fragment.setLoadingListener(loadingListener);
		fragment.setRequestBlockingListener(requestBlockingListener);

		FragmentManager fm = getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(containerId, fragment, name);
		if (needBack)
			ft.addToBackStack(name);
		//ft.commit();
		ft.commitAllowingStateLoss(); // study later
		fm.executePendingTransactions();
		LogUtil.i(TAG, "addFragment(): fragment added. " + name);
	}

	public void removeFragment(BaseFragment fragment) {
		FragmentManager fm = getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.remove(fragment);
		//ft.commit();
		ft.commitAllowingStateLoss();
		fm.executePendingTransactions();
		LogUtil.i(TAG, "removeFragment(): fragment removed. " + fragment.getTag());
	}

	public void putViewInCenterVertical(final View v) {
		final ViewGroup parent = ((ViewGroup) v.getParent());
		parent.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@SuppressWarnings("deprecation")
					@SuppressLint("NewApi")
					@Override
					public void onGlobalLayout() {

						if (Build.VERSION.SDK_INT < 16) {
							parent.getViewTreeObserver()
									.removeGlobalOnLayoutListener(this);
						} else {
							parent.getViewTreeObserver()
									.removeOnGlobalLayoutListener(this);
						}
						// if(noResultParentHeight==0){
						// noResultParentHeight=parent.getHeight();
						// }
						int parentViewHeight = parent.getHeight();
						int tarViewHeight = v.getHeight();
						int topMargin = (parentViewHeight - tarViewHeight) / 2;
						FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) v
								.getLayoutParams();
						lp.setMargins(0, topMargin, 0, 0);
						lp.gravity = Gravity.CENTER_HORIZONTAL;
						v.setLayoutParams(lp);

						new Handler().post(new Runnable() {
							@Override
							public void run() {
								v.setVisibility(View.VISIBLE);
								parent.setVisibility(View.VISIBLE);

							}
						});

					}

				});
	}

	/**
	 * 数据刷新函数
	 */
	public static List<BaseEntity> updNewEntity(int newTotal, int oldTotal, List<? extends BaseEntity> newDatas,
												List<? extends BaseEntity> oldDatas, ArrayMap<String, Boolean> oldMap) {
		if (oldDatas == null || newDatas == null || oldMap == null) return null;
		if (oldTotal < newTotal) {
			List<BaseEntity> newLists = new ArrayList<BaseEntity>();
			BaseEntity newEn, oldEn;
			String dataId;
			int newCount = newTotal - oldTotal;
			if (newCount > newDatas.size()) {
				newCount = newDatas.size();
			}
			for (int i = 0; i < newCount; i++) {
				newEn = newDatas.get(i);
				if (newEn != null) {
					dataId = newEn.getEntityId();
					if (!StringUtil.isNull(dataId) && !oldMap.containsKey(dataId)) {
						// 添加至顶层
						newLists.add(newEn);
						oldMap.put(dataId, true);
						// 移除最底层
						if (oldDatas.size() >= 1) {
							oldEn = oldDatas.remove(oldDatas.size()-1);
							if (oldEn != null && oldMap.containsKey(oldEn.getEntityId())) {
								oldMap.remove(oldEn.getEntityId());
							}
						}
					}
				}
			}
			newLists.addAll(oldDatas);
			return newLists;
		}
		return null;
	}

	/**
	 * 数据去重函数
	 */
	public static List<BaseEntity> addNewEntity(List<? extends BaseEntity> oldDatas,
												List<? extends BaseEntity> newDatas, ArrayMap<String, Boolean> oldMap) {
		if (oldDatas == null || newDatas == null || oldMap == null) return null;
		List<BaseEntity> newLists = new ArrayList<BaseEntity>();
		newLists.addAll(oldDatas);
		BaseEntity newEn;
		String dataId;
		for (int i = 0; i < newDatas.size(); i++) {
			newEn = newDatas.get(i);
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
	public static boolean isStopLoadMore(int showCount, int countTotal, int pageSize) {
		showPageNum(showCount, countTotal, pageSize);
		return showCount > 0 && showCount == countTotal;
	}

	/**
	 * 提示当前页数
	 */
	public static void showPageNum(int showCount, int countTotal, int pageSize) {
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
						LogUtil.i("Retrofit","onError error message : " + throwable.getMessage());
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

}
