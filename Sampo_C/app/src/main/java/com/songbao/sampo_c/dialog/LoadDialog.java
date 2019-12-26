package com.songbao.sampo_c.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.songbao.sampo_c.R;
import com.songbao.sampo_c.utils.CommonTools;
import com.songbao.sampo_c.utils.ExceptionUtil;

public class LoadDialog extends Dialog {

	/**
	 * LoadDialog
	 */
	private static LoadDialog loadDialog;
	/**
	 * canNotCancel, the dialog dimiss or undimiss flag
	 */
	private boolean canNotCancel;
	/**
	 * if the dialog don't dimiss, what is the tips.
	 */
	private String tipMsg;

	/**
	 * the LoadDialog constructor
	 * 
	 * @param ctx
	 *            Context
	 * @param canNotCancel
	 *            boolean
	 * @param tipMsg
	 *            String
	 */
	public LoadDialog(final Context ctx, boolean canNotCancel, String tipMsg) {
		super(ctx);

		this.canNotCancel = canNotCancel;
		this.tipMsg = tipMsg;
		this.getContext().setTheme(android.R.style.Theme_Light_Panel);
		setContentView(R.layout.dailog_animation);

		ConstraintLayout rl_loading = findViewById(R.id.loading_anim_large_main);
		rl_loading.setVisibility(View.VISIBLE);

		// 设置对话框的坐标及宽高
		/*WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.y = -125;
		getWindow().setAttributes(lp);*/

		// 对话框背景设置
		/*Window window = getWindow();
		WindowManager.LayoutParams attributesParams = window.getAttributes();
		attributesParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		attributesParams.dimAmount = 0.5f;

		window.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);*/
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (canNotCancel) {
				CommonTools.showToast(tipMsg, Toast.LENGTH_SHORT);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * show the dialog
	 * 
	 * @param context
	 */
	public static void show(Context context) {
		show(context, null, false);
	}

	/**
	 * show the dialog
	 * 
	 * @param context
	 *            Context
	 * @param message
	 *            String
	 */
	public static void show(Context context, String message) {
		show(context, message, false);
	}

	/**
	 * show the dialog
	 * 
	 * @param context
	 *            Context
	 * @param message
	 *            String
	 * @param isCancel
	 *            boolean, true is can't dimiss，false is can dimiss
	 */
	private static void show(Context context, String message, boolean isCancel) {
		if (context instanceof Activity) {
			if (((Activity) context).isFinishing()) {
				return;
			}
		}
		if (loadDialog != null && loadDialog.isShowing()) {
			return;
		}
		loadDialog = new LoadDialog(context, isCancel, message);
		loadDialog.show();
	}

	/**
	 * hidden the dialog
	 */
	public static void hidden() {
		try {
			if (loadDialog != null && loadDialog.isShowing()) {
				if (loadDialog.getContext() instanceof Activity) {
					if (((Activity) loadDialog.getContext()).isFinishing()) {
						return;
					}
				}
				loadDialog.dismiss();
				loadDialog = null;
			}
		} catch (Exception e) {
			ExceptionUtil.handle(e);
		}
	}
}
