package com.songbao.sampo_b.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnKeyListener;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.utils.StringUtil;

import java.lang.ref.WeakReference;


public class DialogManager {

	private Dialog mDialog;
	private ProgressBar pBar;
	private WeakReference<Context> weakContext;
	private static WeakReference<DialogManager> weakInstance;

	private DialogManager(Context context) {
		weakContext = new WeakReference<>(context);
	}

	/**
	 * 创建此对象请记得在Activity的onPause()中调用clearInstance()销毁对象
	 */
	public static DialogManager getInstance(Context context) {
		if (weakInstance == null || weakInstance.get() == null) {
			synchronized (DialogManager.class) {
				if (weakInstance == null || weakInstance.get() == null){
					weakInstance = new WeakReference<>(new DialogManager(context));
				}
			}
		}
		return weakInstance.get();
	}

	public static void clearInstance(){
		if (weakInstance != null) {
			weakInstance.clear();
			weakInstance = null;
		}
	}

	public void dismiss(){
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		pBar = null;
	}

	/**
	 * 弹出下载缓冲对话框
	 *
	 * @param length 加载进度
	 * @param width 对话框宽度
	 * @param keyListener 物理键盘监听器
	 */
	public void showLoadDialog(int length, int width, OnKeyListener keyListener){
		if (mDialog != null && mDialog.isShowing() && pBar != null) {
			pBar.setProgress(length);
		} else {
			// 销毁旧对话框
			dismiss();
			// 创建新对话框
			mDialog = new Dialog(weakContext.get(), R.style.MyDialog);
			mDialog.setCanceledOnTouchOutside(false);
			if (keyListener != null) {
				mDialog.setOnKeyListener(keyListener);
			}
			mDialog.setContentView(R.layout.dialog_download);
			// 设置对话框的坐标及宽高
			Window window = mDialog.getWindow();
			if (window != null) {
				LayoutParams lp = window.getAttributes();
				lp.width = width;
				window.setAttributes(lp);
			}
			pBar = mDialog.findViewById(R.id.dialog_progress_bar);
			pBar.setProgress(length);
			// 显示对话框
			mDialog.show();
		}
	}

	/**
	 * 弹出提示成功对话框
	 *
	 * @param content
	 * @param width 对话框宽度
	 */
	public void showSuccessDialog(String content, int width, boolean isSuccess){
		// 销毁旧对话框
		dismiss();
		// 创建新对话框
		mDialog = new Dialog(weakContext.get(), R.style.MyDialog);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.setContentView(R.layout.dialog_success);
		// 设置对话框的坐标及宽高
		Window window = mDialog.getWindow();
		if (window != null) {
			LayoutParams lp = window.getAttributes();
			lp.width = width;
			window.setAttributes(lp);
		}
		// 初始化对话框中的子控件
		ImageView iv_logo = mDialog.findViewById(R.id.dialog_logo);
		if (!isSuccess) {
			iv_logo.setImageResource(R.mipmap.icon_dialog_fail);
		}

		TextView tv_content = mDialog.findViewById(R.id.dialog_content);
		tv_content.setText(content);

		ImageView iv_close = mDialog.findViewById(R.id.dialog_close);
		iv_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});

		Button ok = mDialog.findViewById(R.id.dialog_button_ok);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		// 显示对话框
		mDialog.show();
	}

	/**
	 * 弹出一个按钮的通用对话框
	 *
	 * @param content 提示内容
	 * @param width 对话框宽度
	 * @param isCenter 提示内容是否居中
	 * @param isVanish 点击框以外是否消失
	 * @param keyListener 物理键盘监听器
	 */
	public void showOneBtnDialog(String content, int width, boolean isCenter, boolean isVanish, final Handler handler, OnKeyListener keyListener){
		// 销毁旧对话框
		dismiss();
		// 创建新对话框
		mDialog =  new Dialog(weakContext.get(), R.style.MyDialog);
		mDialog.setCanceledOnTouchOutside(isVanish);
		if (keyListener != null) {
			mDialog.setOnKeyListener(keyListener);
		}
		mDialog.setContentView(R.layout.dialog_btn_one);
		// 设置对话框的坐标及宽高
		Window window = mDialog.getWindow();
		if (window != null) {
			LayoutParams lp = window.getAttributes();
			lp.width = width;
			window.setAttributes(lp);
		}
		// 初始化对话框中的子控件
		TextView tv_content = mDialog.findViewById(R.id.dialog_content);
		tv_content.setText(content);
		if (!isCenter) { //不居中
			tv_content.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
		}
		Button ok = mDialog.findViewById(R.id.dialog_button_ok);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (handler != null) { //确定
					handler.sendEmptyMessage(AppConfig.DIALOG_CLICK_OK);
				}
				mDialog.dismiss();
			}
		});
		// 显示对话框
		mDialog.show();
	}

	/**
	 * 弹出两个按钮的通用对话框
	 *
	 * @param title 对话框标题
	 * @param content 对话框内容
	 * @param leftStr 对话框左边按钮文本
	 * @param rightStr 对话框右边按钮文本
	 * @param width 对话框宽度
	 * @param isCenter 提示内容是否居中
	 * @param isVanish 点击框以外是否消失
	 */
	public void showTwoBtnDialog(String title, String content, String leftStr, String rightStr,int width,
								 boolean isCenter, boolean isVanish, final Handler handler, final int handlerCode){
		// 销毁旧对话框
		dismiss();
		// 创建新对话框
		mDialog =  new Dialog(weakContext.get(), R.style.MyDialog);
		mDialog.setCanceledOnTouchOutside(isVanish);
		mDialog.setContentView(R.layout.dialog_btn_two);
		// 设置对话框的坐标及宽高
		Window window = mDialog.getWindow();
		if (window != null) {
			LayoutParams lp = window.getAttributes();
			lp.width = width;
			window.setAttributes(lp);
		}
		// 初始化对话框中的子控件
		TextView tv_title = mDialog.findViewById(R.id.dialog_title);
		if (!StringUtil.isNull(title)) {
			tv_title.setText(title);
			tv_title.setVisibility(View.VISIBLE);
		}
		TextView tv_content = mDialog.findViewById(R.id.dialog_content);
		tv_content.setText(content);
		if (!isCenter) { //不居中
			tv_content.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
		}
		Button left = mDialog.findViewById(R.id.dialog_button_cancel);
		if (!StringUtil.isNull(leftStr)) {
			left.setText(leftStr);
		}
		left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (handler != null) { //取消
					handler.sendEmptyMessage(AppConfig.DIALOG_CLICK_NO);
				}
				mDialog.dismiss();
			}
		});
		Button right = mDialog.findViewById(R.id.dialog_button_confirm);
		if (!StringUtil.isNull(rightStr)) {
			right.setText(rightStr);
		}
		right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (handler != null) { //确定
					handler.sendEmptyMessage(handlerCode);
				}
				mDialog.dismiss();
			}
		});
		// 显示对话框
		mDialog.show();
	}

	/**
	 * 弹出带输入框的对话框
	 */
	public void showEditDialog(String title, int width, int inputType, boolean isVanish, final Handler handler){
		// 销毁旧对话框
		dismiss();
		// 创建新对话框
		mDialog =  new Dialog(weakContext.get(), R.style.MyDialog);
		mDialog.setCanceledOnTouchOutside(isVanish);
		mDialog.setContentView(R.layout.dialog_edit);
		// 设置对话框的坐标及宽高
		Window window = mDialog.getWindow();
		if (window != null) {
			LayoutParams lp = window.getAttributes();
			lp.width = width;
			window.setAttributes(lp);
		}
		// 初始化对话框中的子控件
		TextView tv_title = mDialog.findViewById(R.id.dialog_title);
		if (!StringUtil.isNull(title)) {
			tv_title.setText(title);
			tv_title.setVisibility(View.VISIBLE);
		}
		final EditText et_content = mDialog.findViewById(R.id.dialog_et_content);
		et_content.setInputType(inputType);

		Button left = mDialog.findViewById(R.id.dialog_button_cancel);
		left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (handler != null) { //取消
					handler.sendEmptyMessage(AppConfig.DIALOG_CLICK_NO);
				}
				mDialog.dismiss();
			}
		});
		Button right = mDialog.findViewById(R.id.dialog_button_confirm);
		right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String contStr = et_content.getText().toString();
				if (StringUtil.isNull(contStr)) return;
				if (handler != null) { //确定
					Message msg = Message.obtain();
					msg.obj = contStr;
					handler.sendMessage(msg);
				}
				mDialog.dismiss();
			}
		});
		// 显示对话框
		mDialog.show();
	}

	/**
	 * 弹出列表形式的对话框
	 */
	public void showListItemDialog(String title, CharSequence[] items, int width, final boolean isCenter, final Handler handler){
		// 销毁旧对话框
		dismiss();
		// 创建新对话框
		mDialog =  new Dialog(weakContext.get(), R.style.MyDialog);
		mDialog.setContentView(R.layout.dialog_listview);
		// 设置对话框的坐标及宽高
		Window window = mDialog.getWindow();
		if (window != null) {
			LayoutParams lp = window.getAttributes();
			lp.width = width;
			if (items != null && items.length > 3) {
				lp.height = width * 3 / 2;
			}
			window.setAttributes(lp);
		}
		// 初始化对话框中的子控件
		TextView tv_title = mDialog.findViewById(R.id.dialog_list_title);
		tv_title.setText(title);
		if (!isCenter) { //不居中
			tv_title.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
		}
		ListView lv = mDialog.findViewById(R.id.dialog_list_lv);
		lv.setSelector(R.color.app_color_white);
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(weakContext.get(), android.R.layout.simple_list_item_1, items) {

			@Override
			public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView tv_item = view.findViewById(android.R.id.text1);
				tv_item.setPadding(30, 0, 30, 0);
				tv_item.setTextSize(18);
				tv_item.setTextColor(weakContext.get().getResources().getColor(R.color.shows_text_color));
				if (!isCenter) { //不居中
					tv_item.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
				} else {
					tv_item.setGravity(Gravity.CENTER);
				}
				return view;
			}

		};
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (handler != null) {
					handler.sendEmptyMessage(position);
				}
				mDialog.dismiss();
			}
		});
		// 显示对话框
		mDialog.show();
	}

}
