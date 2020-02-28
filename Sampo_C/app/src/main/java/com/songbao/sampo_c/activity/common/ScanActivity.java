package com.songbao.sampo_c.activity.common;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.songbao.sampo_c.R;
import com.songbao.sampo_c.activity.BaseActivity;
import com.songbao.sampo_c.utils.CommonTools;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

public class ScanActivity extends BaseActivity {

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);

		init();
	}

	private void init() {
		setTitle(R.string.qr_code_scan);

		CaptureFragment captureFragment = new CaptureFragment();
		//定制化扫描框UI
		CodeUtils.setFragmentArgs(captureFragment,R.layout.layout_qrcode_scan);
		//分析结果回调
		captureFragment.setAnalyzeCallback(analyzeCallback);
		getSupportFragmentManager().beginTransaction().replace(R.id.fl_scan,captureFragment).commit();
	}

	CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
		@Override
		public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            /*Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
            bundle.putString(CodeUtils.RESULT_STRING, result);
            resultIntent.putExtras(bundle);
            setResult(RESULT_OK, resultIntent);*/

			openGoodsOffActivity(result);
			finish();
		}

		@Override
		public void onAnalyzeFailed() {
            /*Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING, "");
            resultIntent.putExtras(bundle);
            setResult(RESULT_OK, resultIntent);*/

			CommonTools.showToast("Scan failed!");
			finish();
		}
	};
}