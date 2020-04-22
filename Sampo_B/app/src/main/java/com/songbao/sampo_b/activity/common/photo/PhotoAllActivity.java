package com.songbao.sampo_b.activity.common.photo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
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

import butterknife.BindView;

/**
 * "选择多张相片"Activity
 */
public class PhotoAllActivity extends BaseActivity {

    String TAG = PhotoAllActivity.class.getSimpleName();

    @BindView(R.id.photo_grid_gv)
    GridView gv;

    private PhotoItemAdapter gv_adapter;
    private int sizeNub, selectNum, totalNum;
    private PhotoEntity photoItem;
    private ArrayList<String> al_select_path = new ArrayList<>();
    private ArrayList<PhotoEntity> al_photos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_grid);

        totalNum = getIntent().getIntExtra("totalNum", 0);

        initView();
    }

    private void initView() {
        setRightViewTextColor(R.color.app_color_white);
        setRightViewBackground(R.drawable.shape_style_solid_04_08);

        initData();
        initGridView();
        updateSelectNum();
    }

    private void initData() {
        selectNum = 0;
        al_photos = getAllImgInThePhone();
    }

    private void initGridView() {
        gv_adapter = new PhotoItemAdapter(mContext, al_photos);
        gv_adapter.setShowSelect(true);
        gv.setAdapter(gv_adapter);
        gv.setSelector(R.color.ui_color_app_bg_01);
        gv.setOnItemClickListener(gvItemClickListener);
    }

    private void updateGridView() {
        if (gv_adapter != null) {
            gv_adapter.updateData(al_photos);
        }
    }

    private void updateSelectNum() {
        setRightViewText(getString(R.string.photo_show_number_2, selectNum, totalNum));
    }

    @Override
    public void OnListenerRight() {
        finish();
    }

    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        returnIntent.putStringArrayListExtra(AppConfig.PAGE_DATA, al_select_path);
        setResult(RESULT_OK, returnIntent);
        super.finish();
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
                boolean isSelect = !al_photos.get(position).isSelect();
                if (isSelect) {
                    if (selectNum < totalNum) {
                        selectNum++;
                        al_photos.get(position).setSelect(true);
                        al_select_path.add(al_photos.get(position).getPhotoUrl());
                        updateGridView();
                    }
                } else {
                    if (selectNum > 0) {
                        selectNum--;
                        al_photos.get(position).setSelect(false);
                        al_select_path.remove(al_photos.get(position).getPhotoUrl());
                        updateGridView();
                    }
                }
                updateSelectNum();
            }
        }
    };

    /**
     * 查询手机里所有的图片
     */
    public ArrayList<PhotoEntity> getAllImgInThePhone() {
        ArrayList<PhotoEntity> dateList = new ArrayList<>();//存放图片的路径
        String[] projection = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE
        };
        //全部图片
        String where = MediaStore.Images.Media.MIME_TYPE + "=? or "
                + MediaStore.Images.Media.MIME_TYPE + "=? or "
                + MediaStore.Images.Media.MIME_TYPE + "=?";
        //指定格式
        String[] whereArgs = {"image/jpeg", "image/png", "image/jpg"};
        //查询命令
        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, where, whereArgs, MediaStore.Images.Media.DATE_MODIFIED + " desc ");

        if (cursor != null) {
            PhotoEntity photoEn;
            while (cursor.moveToNext()) {
                String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));//获取图片名.
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));//获取文件大小.
                byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));//图片在手机里的路径
                String dataStr = new String(data, 0, data.length - 1);//路径转换成字符串

                photoEn = new PhotoEntity();
                photoEn.setName(fileName);
                photoEn.setPhotoUrl(dataStr);
                dateList.add(photoEn);
            /*File file = new File(dataStr);
            long time = file.lastModified(); //记录此图片的上次修改时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = sdf.format(time); // 2019-04-10*/
            }
            cursor.close();
        }

        return dateList;
    }

}
