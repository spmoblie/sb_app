package com.songbao.sampo_c.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.view.KeyEvent;
import android.widget.Toast;

import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.AppManager;
import com.songbao.sampo_c.BuildConfig;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.utils.CommonTools;
import com.songbao.sampo_c.utils.download.DownloadListener;
import com.songbao.sampo_c.utils.FileManager;
import com.songbao.sampo_c.utils.LogUtil;
import com.songbao.sampo_c.utils.StringUtil;
import com.songbao.sampo_c.utils.download.DownloadUtil;
import com.songbao.sampo_c.utils.retrofit.HttpRequests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import okhttp3.ResponseBody;
import rx.Observer;


public class AppVersionDialog {

    private static final int DIALOG_WIDTH = AppApplication.screen_width * 2 / 3;
    private static final String APK_PATH = AppConfig.SAVE_APK_PATH;
    private Context mContext;
    private DialogManager dm;
    private String apkUrl;
    private boolean isForce = false;

    public AppVersionDialog(Context context, DialogManager dialogManager) {
        this.mContext = context;
        this.dm = dialogManager;
    }

    /**
     * 提示有新版本可以更新
     *
     * @param apkUrl     Apk下载地址
     * @param description 新版更新描述
     * @param isForce     是否强制更新
     */
    public void updateNewVersion(String apkUrl, String description, boolean isForce) {
        this.isForce = isForce;
        this.apkUrl = apkUrl;
        if (isForce) {
            showStatusDialog(AppApplication.getAppContext().getString(R.string.dialog_version_update_force), true);
        } else {
            if (StringUtil.isNull(description)) {
                description = mContext.getString(R.string.dialog_version_update);
            } else {
                description = Html.fromHtml(description).toString();
            }
            if (dm != null) {
                dm.showTwoBtnDialog(null, description, mContext.getString(R.string.ignore), mContext.getString(R.string.confirm),
                        DIALOG_WIDTH, true, true, new DialogHandler(this), AppConfig.DIALOG_CLICK_OK);
            }
        }
    }

    /**
     * 开启下载Apk任务
     */
    private void startDownLoadApk(String apkUrl) {
        showLoadingDialog();
        DownloadUtil.getInstance().downloadFile(apkUrl, APK_PATH, new DownloadListener() {
            @Override
            public void onStart() {
                LogUtil.i(LogUtil.LOG_HTTP, "DownloadListener  onStart");
            }

            @Override
            public void onProgress(int p) {
                if (dm != null) {
                    dm.showLoadDialog(p, DIALOG_WIDTH, keyListener);
                }
                //LogUtil.i(LogUtil.LOG_HTTP, "DownloadListener  onProgress");
            }

            @Override
            public void onFinish(File file) {
                startInstallApk(APK_PATH);
                if (dm != null) {
                    dm.dismiss();
                }
                LogUtil.i(LogUtil.LOG_HTTP, "DownloadListener  onFinish  fileSize = " + file.length() + " file = " + file.getPath());
            }

            @Override
            public void onError(String msg) {
                showStatusDialog(AppApplication.getAppContext().getString(R.string.toast_server_busy), isForce);
                LogUtil.i(LogUtil.LOG_HTTP, "DownloadListener  onError  " + msg);
            }
        });
    }

    /**
     * 开启安装Apk程序
     */
    private void startInstallApk(String apkFilePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(apkFilePath);
        Uri fileURI;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            fileURI = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileProvider", file);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            fileURI = Uri.fromFile(file);
        }
        intent.setDataAndType(fileURI, "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

    /**
     * 弹出下载缓冲对话框
     */
    private void showLoadingDialog() {
        if (dm != null) {
            dm.showLoadDialog(2, DIALOG_WIDTH, keyListener);
        }
    }

    /**
     * 弹出状态提示对话框
     */
    public void showStatusDialog(String msgStr, boolean isForce) {
        if (dm != null) {
            if (isForce) {
                dm.showOneBtnDialog(msgStr, DIALOG_WIDTH, true, false, new DialogHandler(AppVersionDialog.this), keyListener);
            } else {
                dm.showOneBtnDialog(msgStr, DIALOG_WIDTH, true, true, null, null);
            }
        }
    }

    /**
     * 物理键盘监听器
     */
    private OnKeyListener keyListener = new OnKeyListener() {

        private boolean exit = false;

        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (exit) {
                    AppManager.getInstance().AppExit(AppApplication.getAppContext());
                } else {
                    exit = Boolean.TRUE;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            exit = Boolean.FALSE;
                        }
                    }, 2000);
                    CommonTools.showToast(AppApplication.getAppContext().getString(R.string.toast_exit_prompt), Toast.LENGTH_SHORT);
                }
            }
            return true;
        }
    };

    static class DialogHandler extends Handler {

        WeakReference<AppVersionDialog> mDialog;

        DialogHandler(AppVersionDialog dialog) {
            mDialog = new WeakReference<>(dialog);
        }

        @Override
        public void handleMessage(Message msg) {
            AppVersionDialog theDialog = mDialog.get();
            switch (msg.what) {
                case AppConfig.DIALOG_CLICK_OK:
                    theDialog.startDownLoadApk(theDialog.apkUrl);
                    break;
            }
        }
    }

}
