package com.songbao.sampo_c.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.R;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 系统通用工具类
 */
public class CommonTools {

	private static int screenHeight = AppApplication.screen_height;
	private static Toast toast;

	/**
	 * 隐藏当前Toast
	 */
	public static void cancelToast() {
		if (toast != null) {
			toast.cancel();
		}
	}

    /**
     * 显示Toast消息
     *
     * @param message 消息文本
     */
    public static void showToast(String message) {
		showToast(message, Toast.LENGTH_LONG);
    }

    /**
     * 显示Toast消息
     *
     * @param message 消息文本
     * @param time 消息显示的时长 Toast.LENGTH_LONG / Toast.LENGTH_SHORT
     */
    public static void showToast(String message, long time) {
		// 隐藏Toast
		cancelToast();

		// 自定义View
		Context ctx = AppApplication.getAppContext();
    	View view = View.inflate(ctx, R.layout.layout_toast, null);
    	TextView text = view.findViewById(R.id.toast_message);
    	text.setText(message);

		// 创建Toast
		toast = new Toast(ctx);
		toast.setView(view);
		toast.setGravity(Gravity.CENTER, 0, screenHeight / 3);
		// 设置显示时长
		if (time == Toast.LENGTH_LONG) {
			toast.setDuration(Toast.LENGTH_LONG);
		} else {
			toast.setDuration(Toast.LENGTH_SHORT);
		}
		// 显示Toast
		toast.show();
    }

	/**
	 * 显示翻页数量
	 *
	 * @param message 页数
	 * @param time 显示的时长
	 */
	public static void showPageNum(String message, long time) {
		// 隐藏Toast
		cancelToast();

		Context ctx = AppApplication.getAppContext();
		View view = View.inflate(ctx, R.layout.layout_toast_page_num, null);
		TextView text = view.findViewById(R.id.toast_message);
		text.setText(message);

		// 创建Toast
		toast = new Toast(ctx);
		toast.setView(view);
		toast.setGravity(Gravity.CENTER, 0, screenHeight / 3);
		// 设置显示时长
		if (time == Toast.LENGTH_LONG) {
			toast.setDuration(Toast.LENGTH_LONG);
		} else {
			toast.setDuration(Toast.LENGTH_SHORT);
		}
		// 显示Toast
		toast.show();
	}

	/**
	 * 根据手机分辨率从dp转成px
	 * 
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	public static float dpToPx(Context context, float dp) {
	    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
	    return dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);
	}
	
	public static int dpToPx(Context context, int dp) {
	    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
	    return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
	}

	public static float convertDpToPixel(Context context, float dp) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
	}
	
	public static double getHypotenuseByPath(PointF pt1, PointF pt2) {
        int w = (int) (Math.max(pt1.x, pt2.x) - Math.min(pt1.x, pt2.x));
        int h = (int) (Math.max(pt1.y, pt2.y) - Math.min(pt1.y, pt2.y));
        return Math.sqrt(w * w + h * h);
    }
	
	public static double getAngle(float x, float y, float x2, float y2) {
        float dx = x2 - x;
        float dy = y2 - y;
        return Math.atan2(dy, dx) / Math.PI * 180;
    }
	
	public static float getScaleFactor(double oldDist, double newDist) {
        return (float) (newDist / oldDist);
    }
	
	public static float adjustAngle(float degrees) {
        if (degrees > 180.0f) {
            degrees -= 360.0f;
        } else if (degrees < -180.0f) {
            degrees += 360.0f;
        }
        return degrees;
    }
	
	public static void deleteFileInCache(Context context, String fileName) {
	    File file = new File(context.getExternalCacheDir(), fileName);
	    file.delete();
	}
	
	public static void adjustTranslation(View view, float deltaX, float deltaY) {
        float[] deltaVector = { deltaX, deltaY };
        view.getMatrix().mapVectors(deltaVector);
        view.setTranslationX(view.getTranslationX() + deltaVector[0]);
        view.setTranslationY(view.getTranslationY() + deltaVector[1]);
    }
	
	public static  void enableViews(View view, boolean enabled) {
		
	    view.setEnabled(enabled);

	    if ( view instanceof ViewGroup ) {
	        ViewGroup group = (ViewGroup)view;

	        for ( int idx = 0 ; idx < group.getChildCount() ; idx++ ) {
	        	enableViews(group.getChildAt(idx), enabled);
	        }
	    }
	}

	/**
	 * 获取存储屏幕信息的Point
	 */
	public static Point getScreenSize(Activity activity){
	    Display display = activity.getWindowManager().getDefaultDisplay();
	    Point size = new Point();
	    display.getSize(size);
	    return size;
	}

	/**
	 * 正则匹配手机号码
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileOR(String mobiles){
		Pattern pattern = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");  
		Matcher matcher = pattern.matcher(mobiles);  
		return matcher.matches();
	}
	
	/**
	 * 动态设置布局的宽高
	 */
	public static void setLayoutParams(View view, int width, int height){
		// 获取布局参数
		LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) view.getLayoutParams();  
		linearParams.width = width;   
		linearParams.height = height;
		// 应用布局参数
		view.setLayoutParams(linearParams);
	}

}
