package com.songbao.sxb.utils;


import com.songbao.sxb.AppConfig;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {
	
	public static void handle(Exception e)
	{
		if (AppConfig.IS_PUBLISH) {
			//App正式发布之后将异常详细信息上传至错误统计平台
			//MobclickAgent.reportError(AppApplication.getAppContext(), e);
		}
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);
		LogUtil.i("ExceptionUtil", stringWriter.toString());
	}

}
