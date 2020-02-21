package com.songbao.sampo_c.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.KeyEvent;
import android.widget.Toast;

import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.AppManager;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.utils.CommonTools;
import com.songbao.sampo_c.utils.StringUtil;

import java.io.File;
import java.lang.ref.WeakReference;


public class AppVersionDialog {

	private static final int DIALOG_WIDTH = AppApplication.screen_width * 2/3;
	private static final String APK_PATH = AppConfig.SAVE_PATH_APK_DICE + "/sampo.apk";
	private DialogManager dm;
	private String apkLoadAddress;
	private boolean isForce = false;

	public AppVersionDialog(DialogManager dialogManager) {
		this.dm = dialogManager;
	}

	/**
	 * 更新状态提示
	 */
	public void showStatus(String msgStr){
		if (dm != null) {
			dm.showOneBtnDialog(msgStr, DIALOG_WIDTH, true, true, null, null);
		}
	}
	
	/**
	 * 提示有新版本可以更新
	 * 
	 * @param address Apk下载地址
	 * @param description 更新描述
	 */
	public void foundNewVersion(String address, String description){
		Context context = AppApplication.getAppContext();
		isForce = false;
		apkLoadAddress = address;
		if (StringUtil.isNull(description)) {
			description = context.getString(R.string.dialog_version_update);
		} else {
			description = Html.fromHtml(description).toString();
		}
		if (dm != null) {
			dm.showTwoBtnDialog(null, description, context.getString(R.string.ignore), context.getString(R.string.confirm),
					DIALOG_WIDTH, true, true, new DialogHandler(this), AppConfig.DIALOG_CLICK_OK);
		}
	}
	
	/**
	 * 提示有新版本需要强制更新
	 * 
	 * @param address Apk下载地址
	 * @param description 更新描述
	 */
	public void forceUpdateVersion(String address, String description) {
		isForce = true;
		apkLoadAddress = address;
		if (StringUtil.isNull(description)) {
			description = AppApplication.getAppContext().getString(R.string.dialog_version_update_force);
		} else {
			description = Html.fromHtml(description).toString();
		}
		if (dm != null) {
			dm.showOneBtnDialog(description, DIALOG_WIDTH, true, false, new DialogHandler(this), keyListener);
		}
	}

	/**
	 * 开始下载apk程序
	 */
	private void startLoadApk(final String address) {
		loadApkDialog();
		//new UpdateAppHttpTask().execute(address);
	}

	/**
	 * 弹出下载缓冲对话框
	 */
	private void loadApkDialog() {
		if (dm != null) {
			dm.showLoadDialog(DIALOG_WIDTH, keyListener);
		}
	}
	
	/**
	 * 开始安装apk程序
	 */
	private void startInstallApk() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(APK_PATH)), "application/vnd.android.package-archive");
		AppApplication.getAppContext().startActivity(intent);
		if (dm != null) {
			dm.dismiss();
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
	
	/**
	 * 下载apk安装包的异步任务
	 */
	/*class UpdateAppHttpTask extends AsyncTask<String, Void, String> {

		protected String doInBackground(String... url) {
			List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
			try {
				HttpEntity entity = HttpUtil.getEntity(url[0], params, HttpUtil.METHOD_POST);
				return FileManager.writeFileSaveHttpEntity(APK_PATH, entity);
			} catch (Exception e) {
				ExceptionUtil.handle(e);
			}
			return null;
		}

		protected void onPostExecute(String result) {
			if (result != null) {
				startInstallApk();
			} else {
				if (isForce) {
					dm.showOneBtnDialog(mContext.getString(R.string.toast_server_busy),
							DIALOG_WIDTH, true, false, new DialogHandler(AppVersionDialog.this), keyListener);
				} else {
					showStatus(mContext.getString(R.string.toast_server_busy));
				}
			}
		}

	}*/

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
					theDialog.startLoadApk(theDialog.apkLoadAddress);
					break;
			}
		}
	}

}
