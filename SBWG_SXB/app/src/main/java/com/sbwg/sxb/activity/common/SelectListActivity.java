package com.sbwg.sxb.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppConfig;
import com.sbwg.sxb.AppManager;
import com.sbwg.sxb.R;
import com.sbwg.sxb.activity.BaseActivity;
import com.sbwg.sxb.adapter.AdapterCallback;
import com.sbwg.sxb.adapter.SelectListAdapter;
import com.sbwg.sxb.entity.SelectListEntity;
import com.sbwg.sxb.utils.LogUtil;

import java.util.List;


/**
 * 选择列表Activity
 */
public class SelectListActivity extends BaseActivity {
	
	private static final String TAG = "SelectListActivity";
	private int dataType = SelectListAdapter.DATA_TYPE_2;
	private boolean isChange = false;
	
	private ListView lv;
	private AdapterCallback lv_Callback;
	private SelectListAdapter lv_Adapter;
	
	private SelectListEntity data, selectEn;
	private List<SelectListEntity> lv_lists;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_list);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
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
			public void setOnClick(Object entity, int position, int type) {
				selectEn = (SelectListEntity) entity;
				switch (dataType) {
				case SelectListAdapter.DATA_TYPE_5: //PersonalActivity --> SelectListActivity
					if (selectEn != null) {
						postChangeContent();
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

	private void postChangeContent() {
//		request(AppConfig.REQUEST_SV_POST_EDIT_USER_INFO_CODE);
		//Evan临时修改
		isChange = true;
		finish();
	}
	
	@Override
	protected void onResume() {
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		LogUtil.i(TAG, "onPause");
		// 页面结束
		AppApplication.onPageEnd(this, TAG);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		LogUtil.i(TAG, "onDestroy");
		super.onDestroy();
	}
	
	@Override
	public void finish() {
		if (isChange && selectEn != null) { 
			Intent returnIntent = new Intent();
			returnIntent.putExtra(AppConfig.ACTIVITY_SELECT_LIST_POSITION, selectEn.getChildId());
			setResult(RESULT_OK, returnIntent);
		}
		super.finish();
	}
	
}
