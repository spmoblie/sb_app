package com.songbao.sampo_b.activity.common.clip;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.activity.BaseActivity;
import com.songbao.sampo_b.adapter.ClipPhotoAllAdapter;
import com.songbao.sampo_b.entity.ClipPhotoEntity;
import com.songbao.sampo_b.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * "选择多张相片"Activity
 */
public class ClipPhotoAllActivity extends BaseActivity {

    String TAG = ClipPhotoAllActivity.class.getSimpleName();

    @BindView(R.id.clip_photo_one_gv)
    GridView gv;

    private int sizeNub;
    private ClipPhotoEntity photoItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip_photo_grid);

        initView();
    }

    private void initView() {
        setTitle(R.string.photo_select_one_title);

        gv.setAdapter(new ClipPhotoAllAdapter(mContext, getAllImgInThePhone()));
        gv.setSelector(R.color.ui_color_app_bg_01);
        gv.setOnItemClickListener(gvItemClickListener);
    }

    @Override
    public void OnListenerRight() {
        selectDone();
        super.OnListenerRight();
    }

    private void selectDone() {

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

            }
        }
    };

    /**
     * 查询手机里所有的图片
     */
    public List<String> getAllImgInThePhone() {
        List<String> dateList = new ArrayList<>();//存放图片的路径
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
            while (cursor.moveToNext()) {
                String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));//获取图片名.
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));//获取文件大小.
                byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));//图片在手机里的路径
                String dataStr = new String(data, 0, data.length - 1);//路径转换成字符串
                dateList.add(dataStr);
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
