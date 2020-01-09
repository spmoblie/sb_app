package com.songbao.sampo_c.activity.common.clip;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.activity.BaseActivity;
import com.songbao.sampo_c.adapter.ClipPhotoOneAdapter;
import com.songbao.sampo_c.entity.ClipPhotoEntity;
import com.songbao.sampo_c.utils.LogUtil;

/**
 * "选择一张相片"Activity
 */
public class ClipPhotoOneActivity extends BaseActivity {

    String TAG = ClipPhotoOneActivity.class.getSimpleName();

    private GridView gv;
    private int sizeNub;
    private boolean isClip;
    private ClipPhotoEntity album, photoItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip_photo_one);

        isClip = getIntent().getBooleanExtra("isClip", false);
        album = (ClipPhotoEntity) getIntent().getSerializableExtra("album");

        findViewById();
        initView();
    }

    private void findViewById() {
        gv = findViewById(R.id.clip_photo_one_gv);
    }

    private void initView() {
        setTitle(R.string.photo_select_one_title);

        sizeNub = album.getBitList().size();
        gv.setAdapter(new ClipPhotoOneAdapter(mContext, album));
        gv.setSelector(R.color.ui_color_app_bg_01);
        gv.setOnItemClickListener(gvItemClickListener);
    }

    @Override
    public void OnListenerRight() {
        photoItem = album.getBitList().get(sizeNub - 1);
        selectDone();
        super.OnListenerRight();
    }

    private void selectDone() {
        if (isClip) {
            startClipImageActivity();
        } else {
            userManager.savePostPhotoUrl(photoItem.getPhotoUrl());
            closePhotoActivity();
        }
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
            if (sizeNub - 1 - position >= 0) {
                photoItem = album.getBitList().get(sizeNub - 1 - position);
                selectDone();
            }
        }
    };

}
