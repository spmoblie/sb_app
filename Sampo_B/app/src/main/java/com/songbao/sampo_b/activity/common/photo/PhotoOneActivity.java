package com.songbao.sampo_b.activity.common.photo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.activity.BaseActivity;
import com.songbao.sampo_b.adapter.PhotoItemAdapter;
import com.songbao.sampo_b.entity.PhotoEntity;
import com.songbao.sampo_b.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * "选择一张相片"Activity
 */
public class PhotoOneActivity extends BaseActivity {

    String TAG = PhotoOneActivity.class.getSimpleName();

    @BindView(R.id.photo_grid_gv)
    GridView gv;

    private boolean isClip;
    private PhotoEntity albumItem, photoItem;
    private List<PhotoEntity> al_photos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_grid);

        isClip = getIntent().getBooleanExtra("isClip", false);
        albumItem = (PhotoEntity) getIntent().getSerializableExtra("album");

        initView();
    }

    private void initView() {
        setTitle(R.string.photo_select_one_title);

        initData();
        initGridView();
    }

    private void initData() {
        if (albumItem != null) {
            int size = albumItem.getBitList().size();
            for (int i = size - 1; i >= 0; i--) {
                al_photos.add(albumItem.getBitList().get(i));
            }
        }
    }

    private void initGridView() {
        gv.setAdapter(new PhotoItemAdapter(mContext, al_photos));
        gv.setSelector(R.color.ui_color_app_bg_01);
        gv.setOnItemClickListener(gvItemClickListener);
    }

    /**
     * 跳转至相片编辑器
     */
    private void startClipImageActivity() {
        Intent intent;
        switch (shared.getInt(AppConfig.KEY_CLIP_PHOTO_TYPE, AppConfig.PHOTO_TYPE_ROUND)) {
            default:
            case AppConfig.PHOTO_TYPE_ROUND: //圆形
                intent = new Intent(this, ClipImageCircularActivity.class);
                break;
            case AppConfig.PHOTO_TYPE_SQUARE: //方形
                intent = new Intent(this, ClipImageSquareActivity.class);
                break;
        }
        intent.putExtra(AppConfig.ACTIVITY_KEY_PHOTO_PATH, photoItem.getPhotoUrl());
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

    private OnItemClickListener gvItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position >= 0 && position < al_photos.size()) {
                photoItem = al_photos.get(position);
                selectDone();
            }
        }
    };

    private void selectDone() {
        if (photoItem == null) return;
        if (isClip) {
            startClipImageActivity();
        } else {
            userManager.savePostPhotoUrl(photoItem.getPhotoUrl());
            closePhotoActivity();
        }
    }

}
