package com.sbwg.sxb.activity.mine;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;

import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppConfig;
import com.sbwg.sxb.R;
import com.sbwg.sxb.activity.BaseActivity;
import com.sbwg.sxb.utils.ExceptionUtil;
import com.sbwg.sxb.utils.LogUtil;
import com.sbwg.sxb.utils.StringUtil;
import com.sbwg.sxb.utils.XmlParserHandler;
import com.sbwg.sxb.widgets.area.AreaEntity;
import com.sbwg.sxb.widgets.area.AreaIndexDisplayAdapter;
import com.sbwg.sxb.widgets.area.IndexDisplay;
import com.sbwg.sxb.widgets.area.IndexDisplayAdapter;
import com.sbwg.sxb.widgets.area.IndexDisplayFragment;
import com.sbwg.sxb.widgets.area.IndexDisplayTool;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class SelectAreaActivity extends BaseActivity {

	String TAG = SelectAreaActivity.class.getSimpleName();

	private String areaName;
	private List<AreaEntity> areaList = new ArrayList<>();
	private ArrayMap<String, Integer> am_index = new ArrayMap<>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_area);

		initData();
		initView();
	}

	private void initData() {
		InputStream input = null;
		AssetManager asset = getAssets();
		try {
			input = asset.open("area_data.xml");
			// 创建一个解析xml的工厂对象
			SAXParserFactory spf = SAXParserFactory.newInstance();
			// 解析xml
			SAXParser parser = spf.newSAXParser();
			XmlParserHandler handler = new XmlParserHandler();
			parser.parse(input, handler);
			input.close();
			// 获取解析出来的数据
			areaList = handler.getAreaDataList();
		} catch (Exception e) {
			ExceptionUtil.handle(e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					ExceptionUtil.handle(e);
				}
			}
		}
	}

	private void initView() {
		setTitle(getString(R.string.mine_change_area));
		//setRightViewText(getString(R.string.confirm));

		initIndexDisplayFragment();
	}

	public void initIndexDisplayFragment() {
		FragmentManager fm = getSupportFragmentManager();
		IndexDisplayFragment idf = (IndexDisplayFragment) fm.findFragmentById(R.id.select_area_fl_list);
		if (idf == null || idf.isRemoving()) {
			AreaIndexDisplayAdapter adapter = new AreaIndexDisplayAdapter(mContext);
			adapter.setOnIndexDisplayItemClick(new IndexDisplayAdapter.OnIndexDisplayItemClick() {

				@Override
				public void onIndexDisplayItemClick(IndexDisplay indexDisplay) {
					if (indexDisplay != null) {
						AreaEntity areaEn = (AreaEntity) indexDisplay;
						areaName = areaEn.getName();
						finish();
					}
				}

			});
			idf = IndexDisplayFragment.newInstance();
			idf.setDataList(IndexDisplayTool.buildIndexListChineseAndEng(this, areaList, am_index));
			idf.setAdapter(adapter);
			idf.setIndexHashMap(am_index);

			FragmentTransaction ft = fm.beginTransaction();
			ft.add(R.id.select_area_fl_list, idf).commitAllowingStateLoss();
		}
	}
	
	@Override
	public void OnListenerLeft() {
		super.OnListenerLeft();
		finish();
	}
	
	@Override
	public void OnListenerRight() {
		super.OnListenerRight();
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
		if (!StringUtil.isNull(areaName)) {
			Intent returnIntent = new Intent();
			returnIntent.putExtra(AppConfig.ACTIVITY_CHANGE_USER_CONTENT, areaName);
			setResult(RESULT_OK, returnIntent);
		}
		super.finish();
	}
	
}
