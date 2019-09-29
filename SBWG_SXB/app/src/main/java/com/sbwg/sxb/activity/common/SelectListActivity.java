package com.sbwg.sxb.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppConfig;
import com.sbwg.sxb.R;
import com.sbwg.sxb.activity.BaseActivity;
import com.sbwg.sxb.adapter.AdapterCallback;
import com.sbwg.sxb.adapter.SelectListAdapter;
import com.sbwg.sxb.entity.BaseEntity;
import com.sbwg.sxb.entity.SelectListEntity;
import com.sbwg.sxb.utils.ExceptionUtil;
import com.sbwg.sxb.utils.JsonUtils;
import com.sbwg.sxb.utils.LogUtil;
import com.sbwg.sxb.utils.retrofit.HttpRequests;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


/**
 * 选择列表Activity
 */
public class SelectListActivity extends BaseActivity {

	String TAG = SelectListActivity.class.getSimpleName();

	private boolean isChange = false;
	private int dataType = SelectListAdapter.DATA_TYPE_2;

	private ListView lv;
	private AdapterCallback lv_Callback;
	private SelectListAdapter lv_Adapter;
	
	private SelectListEntity data, selectEn;
	private List<SelectListEntity> lv_lists;
	private String userKey, userValue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_list);

		data = (SelectListEntity) getIntent().getExtras().get("data");
		dataType = getIntent().getExtras().getInt("dataType", SelectListAdapter.DATA_TYPE_2);
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		lv = findViewById(R.id.select_list_lv);
	}

	private void initView() {
		if (data != null) {
			setTitle(data.getTypeName()); //标题
			selectEn = data.getSelectEn();
			if (dataType == SelectListAdapter.DATA_TYPE_4
					| dataType == SelectListAdapter.DATA_TYPE_7) {
				setRightViewText(getString(R.string.clean)); //右边按钮
				if (selectEn == null) {
					setRightViewGone(View.GONE);
				}
			}
			lv_lists = data.getChildLists();
			if (lv_lists != null) {
				setAdapter();
			}
		}
	}
	
	@Override
	public void OnListenerRight() {
		super.OnListenerRight();
	}

	/**
	 * 设置适配器
	 */
	private void setAdapter() {
		lv_Callback = new AdapterCallback() {
			
			@Override
			public void setOnClick(Object data, int position, int type) {
				selectEn = (SelectListEntity) data;
				switch (dataType) {
				case SelectListAdapter.DATA_TYPE_5: //PersonalActivity --> SelectListActivity
					if (selectEn != null) {
						userKey = "gender";
						userValue = String.valueOf(selectEn.getChildId());
						saveUserInfo();
					}else {
						finish();
					}
					break;
				case SelectListAdapter.DATA_TYPE_8: //AddressEditActivity --> SelectListActivity
					isChange = true;
					finish();
					break;
				}
			}
		};
		lv_Adapter = new SelectListAdapter(mContext, selectEn, lv_lists, lv_Callback, dataType);
		lv.setAdapter(lv_Adapter);
		lv.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
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
		super.onDestroy();
	}
	
	@Override
	public void finish() {
		if (isChange && selectEn != null) { 
			Intent returnIntent = new Intent();
			returnIntent.putExtra(AppConfig.ACTIVITY_KEY_SELECT_LIST, selectEn.getChildId());
			setResult(RESULT_OK, returnIntent);
		}
		super.finish();
	}

	/**
	 * 修改用户资料
	 */
	private void saveUserInfo() {
		HashMap<String, String> map = new HashMap<>();
		map.put(userKey, userValue);
		loadSVData(AppConfig.URL_USER_SAVE, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_POST_USER_SAVE);
	}

	@Override
	protected void callbackData(JSONObject jsonObject, int dataType) {
		BaseEntity baseEn;
		try {
			switch (dataType) {
				case AppConfig.REQUEST_SV_POST_USER_SAVE:
					baseEn = JsonUtils.getUploadResult(jsonObject);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						AppApplication.updateUserData(true);
						isChange = true;
						finish();
					} else {
						handleErrorCode(baseEn);
					}
					break;
			}
		} catch (Exception e) {
			loadFailHandle();
			ExceptionUtil.handle(e);
		}
	}

	@Override
	protected void loadFailHandle() {
		super.loadFailHandle();
		handleErrorCode(null);
	}
	
}
