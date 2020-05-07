package com.songbao.sampo_b.activity.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.activity.BaseActivity;
import com.songbao.sampo_b.dialog.DialogManager;
import com.songbao.sampo_b.utils.FileManager;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.utils.StringUtil;
import com.songbao.sampo_b.utils.download.DownloadListener;
import com.songbao.sampo_b.utils.download.DownloadUtil;

import java.io.File;

import butterknife.BindView;

import static com.songbao.sampo_b.AppConfig.SAVE_PATH_DOWNLOAD;

public class FileActivity extends BaseActivity implements View.OnClickListener {

    String TAG = FileActivity.class.getSimpleName();

    @BindView(R.id.file_tv_name)
    TextView tv_name;

    @BindView(R.id.file_tv_download)
    TextView tv_download;

    private String fileUrl, filePath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        fileUrl = getIntent().getStringExtra("fileUrl");

        initView();
    }

    private void initView() {
        setTitle(R.string.file);

        tv_download.setOnClickListener(this);

        if (!StringUtil.isNull(fileUrl) && fileUrl.contains("/")) {
            String[] urls = fileUrl.split("/");
            String fileName = urls[urls.length-1];
            tv_name.setText(fileName);

            filePath = SAVE_PATH_DOWNLOAD + fileName;
        }
        changeClickText();
    }

    private void changeClickText() {
        if (FileManager.checkFileExists(filePath)) {
            tv_download.setText(getString(R.string.see));
        } else {
            tv_download.setText(getString(R.string.download));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.file_tv_download:
                if (FileManager.checkFileExists(filePath)) {
                    FileManager.openFile(mContext, new File(filePath));
                } else {
                    startDownLoadFile(fileUrl);
                }
                break;
        }
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

        DialogManager.clearInstance();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 开启下载文件任务
     */
    private void startDownLoadFile(String fileUrl) {
        showLoadingDialog();
        DownloadUtil.getInstance().downloadFile(fileUrl, filePath, new DownloadListener() {
            @Override
            public void onStart() {
                LogUtil.i(LogUtil.LOG_HTTP, "DownloadListener  onStart");
            }

            @Override
            public void onProgress(int p) {
                if (myDialog != null) {
                    myDialog.showLoadDialog(p, dialogWidth, null);
                }
            }

            @Override
            public void onFinish(File file) {
                changeClickText();
                FileManager.openFile(mContext, new File(filePath));
                if (myDialog != null) {
                    myDialog.dismiss();
                }
                LogUtil.i(LogUtil.LOG_HTTP, "DownloadListener  onFinish  fileSize = " + file.length() + " file = " + file.getPath());
            }

            @Override
            public void onError(String msg) {
                showStatusDialog(AppApplication.getAppContext().getString(R.string.toast_server_busy));
                LogUtil.i(LogUtil.LOG_HTTP, "DownloadListener  onError  " + msg);
            }
        });
    }

    /**
     * 弹出下载缓冲对话框
     */
    private void showLoadingDialog() {
        if (myDialog == null) {
            myDialog = DialogManager.getInstance(mContext);
        }
        myDialog.showLoadDialog(2, dialogWidth, null);
    }

    /**
     * 弹出状态提示对话框
     */
    public void showStatusDialog(String msgStr) {
        if (myDialog != null) {
            myDialog.showOneBtnDialog(msgStr, dialogWidth, true, true, null, null);
        }
    }
}
