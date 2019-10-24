package com.songbao.sxb.activity.common.clip;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.songbao.sxb.AppApplication;
import com.songbao.sxb.R;
import com.songbao.sxb.activity.BaseActivity;
import com.songbao.sxb.adapter.ClipPhotoGridAdapter;
import com.songbao.sxb.entity.ClipPhotoEntity;
import com.songbao.sxb.utils.CommonTools;
import com.songbao.sxb.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * "选择相片"Activity
 */
public class ClipPhotoGridActivity extends BaseActivity {

	String TAG = ClipPhotoGridActivity.class.getSimpleName();

	private GridView gv_album;
	private List<ClipPhotoEntity> albumList = new ArrayList<ClipPhotoEntity>();

	// 设置获取图片的字段信
	private static final String[] STORE_IMAGES = {
		MediaStore.Images.Media.DISPLAY_NAME, // 显示的名称
		MediaStore.Images.Media.LATITUDE, // 维度
		MediaStore.Images.Media.LONGITUDE, // 经度
		MediaStore.Images.Media._ID, // id
		MediaStore.Images.Media.BUCKET_ID, // dir id 目录
		MediaStore.Images.Media.BUCKET_DISPLAY_NAME, // dir name 目录名字
		MediaStore.Images.Media.DATA // 图片路径
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clip_photo_list);

		findViewById();
		initView();
	}
	
	private void findViewById() {
		gv_album = findViewById(R.id.clip_photo_list_gv);
	}

	private void initView() {
		setTitle(R.string.photo_select_title);
		setRightViewText(getString(R.string.next));

		albumList = getPhotoAlbum();
		if (albumList.size() < 1) {
			CommonTools.showToast(getString(R.string.photo_select_no_data));
		}
		gv_album.setAdapter(new ClipPhotoGridAdapter(albumList, this));
		gv_album.setOnItemClickListener(albumClickListener);
		gv_album.setSelector(R.color.ui_color_app_bg_01);
	}
	
	@Override
	public void OnListenerRight() {
		if (albumList.size() > 0) {
			startPhotoOneActivity(0);
		}
		super.OnListenerRight();
	}

	/**
	 * 跳转至指定相册
	 */
	private void startPhotoOneActivity(int position) {
		Intent intent = new Intent(this, ClipPhotoOneActivity.class);
		intent.putExtra("album", albumList.get(position));
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
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
	
	/**
	 * 相册点击事件
	 */
	OnItemClickListener albumClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			startPhotoOneActivity(position);
		}
	};

	/**
	 * 方法描述：按相册获取图片信息
	 */
	private List<ClipPhotoEntity> getPhotoAlbum() {
		Cursor cursor = MediaStore.Images.Media.query(getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES);
		List<ClipPhotoEntity> albumList = new ArrayList<ClipPhotoEntity>();
		Map<String, ClipPhotoEntity> countMap = new HashMap<String, ClipPhotoEntity>();
		ClipPhotoEntity pa;
		while (cursor.moveToNext()) {
			String id = cursor.getString(3);
			String dir_id = cursor.getString(4);
			String dir = cursor.getString(5);
			String url = cursor.getString(6);
			if (!countMap.containsKey(dir_id)) {
				pa = new ClipPhotoEntity();
				pa.setName(dir);
				pa.setFirstId(Integer.parseInt(id));
				pa.setCount("1");
				pa.getBitList().add(new ClipPhotoEntity(Integer.valueOf(id), url));
				countMap.put(dir_id, pa);
			} else {
				pa = countMap.get(dir_id);
				pa.setFirstId(Integer.parseInt(id));
				pa.setCount(String.valueOf(Integer.parseInt(pa.getCount()) + 1));
				pa.getBitList().add(new ClipPhotoEntity(Integer.valueOf(id), url));
			}
		}
		cursor.close();
		Iterable<String> it = countMap.keySet();
		for (String key : it) {
			albumList.add(countMap.get(key));
		}
		return albumList;
	}
	
}
