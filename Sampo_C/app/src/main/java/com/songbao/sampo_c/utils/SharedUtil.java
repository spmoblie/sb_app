package com.songbao.sampo_c.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.dialog.DialogManager;

/**
 * 全局配置文件(偏好设置)
 */
public class SharedUtil {
	
	private static SharedPreferences shared;
	
	private SharedUtil(Context ctx){
		shared = ctx.getSharedPreferences(AppConfig.APP_SP_NAME, Context.MODE_PRIVATE);
	}

	public static SharedPreferences getInstance(Context ctx){
		if (shared == null) {
			synchronized (SharedUtil.class) {
				if (shared == null){
					new SharedUtil(ctx);
				}
			}
		}
		return shared;
	}
	
	public static void clearInstance(){
		shared.edit().clear().apply();
		shared = null;
	}
}
