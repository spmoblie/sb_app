package com.songbao.sxb.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 全局配置文件(偏好设置)
 */
public class SharedUtil {
	
	private static SharedPreferences shared;
	
	private SharedUtil(Context ctx){
		shared = ctx.getSharedPreferences("song_bao_shared", Context.MODE_PRIVATE);
	}

	public static SharedPreferences getInstance(Context ctx){
		if (shared == null) {
			new SharedUtil(ctx);
		}
		return shared;
	}
	
	public static void clearInstance(){
		shared.edit().clear().apply();
		shared = null;
	}
}
