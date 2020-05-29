package com.songbao.sampo_c.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.BuildConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileManager {

	/**
	 * 写入数据（String）
	 *
	 * @param fileName 文件名
	 * @param writeStr 写入文本对象
	 * @param isSave 是否保存
	 */
	public static void writeFileSaveString(String fileName, String writeStr, boolean isSave) {
		if (StringUtil.isNull(fileName) || StringUtil.isNull(writeStr)) return;
		FileOutputStream fos = null;
		try {
			String path;
			if (isSave) {
				path = AppConfig.PATH_TEXT_STORE + fileName;
			}else {
				path = AppConfig.PATH_TEXT_CACHE + fileName;
			}
			checkFilePath(path);
			fos = new FileOutputStream(path);
			byte[] bytes = writeStr.getBytes();
			fos.write(bytes);
		} catch (Exception e) {
			ExceptionUtil.handle(e);
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (Exception e) {
				ExceptionUtil.handle(e);
			}
		}
	}

	/**
	 * 读取数据（String）
	 *
	 * @param fileName 文件名
	 * @param isSave 是否保存
	 */
	public static String readFileSaveString(String fileName, boolean isSave) {
		FileInputStream fis = null;
		String path;
		String result = "";
		try {
			if (isSave) {
				path = AppConfig.PATH_TEXT_STORE + fileName;
			}else {
				path = AppConfig.PATH_TEXT_CACHE + fileName;
			}
			File file = new File(path);
			if (file.exists()) {
				fis = new FileInputStream(file);
				int length = fis.available();
				byte[] buffer = new byte[length];
				fis.read(buffer);
				result = new String(buffer, "UTF-8");
			}
		} catch (Exception e) {
			ExceptionUtil.handle(e);
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (Exception e) {
				ExceptionUtil.handle(e);
			}
		}
		return result;
	}

	/**
	 * 写入数据（Object）
	 *
	 * @param fileName 文件名
	 * @param obj 写入对象
	 * @param pathType 0 : AppConfig.PATH_TEXT_CACHE
	 *                 1 : AppConfig.PATH_TEXT_STORE
	 *                 2 : AppConfig.PATH_USER_DATA
	 */
	public static void writeFileSaveObject(String fileName, Object obj, int pathType) {
		if (StringUtil.isNull(fileName) || obj == null) return;
		ObjectOutputStream objOut = null;
		FileOutputStream fos = null;
		try {
			String path;
			switch (pathType) {
				case 1:
					path = AppConfig.PATH_TEXT_STORE + fileName;
					break;
				case 2:
					path = AppConfig.PATH_USER_DATA + fileName;
					break;
				default:
					path = AppConfig.PATH_TEXT_CACHE + fileName;
					break;
			}
			checkFilePath(path);
			fos = new FileOutputStream(new File(path));
			objOut = new ObjectOutputStream(fos);
			objOut.writeObject(obj);
			objOut.flush();
		} catch (IOException e) {
			ExceptionUtil.handle(e);
		}finally{
			try {
				if (fos != null) {
					fos.close();
				}
				if (objOut != null) {
					objOut.close();
				}
			} catch (IOException e) {
				ExceptionUtil.handle(e);
			}
		}
	}

	/**
	 * 写入数据（Object）
	 *
	 * @param path 写入文件名
	 * @param obj 写入对象
	 */
	public static void writeFileSaveObject(String path, Object obj) {
		if (StringUtil.isNull(path) || obj == null) return;
		ObjectOutputStream objOut = null;
		FileOutputStream fos = null;
		try {
			checkFilePath(path);
			fos = new FileOutputStream(new File(path));
			objOut = new ObjectOutputStream(fos);
			objOut.writeObject(obj);
			objOut.flush();
		} catch (IOException e) {
			ExceptionUtil.handle(e);
		}finally{
			try {
				if (fos != null) {
					fos.close();
				}
				if (objOut != null) {
					objOut.close();
				}
			} catch (IOException e) {
				ExceptionUtil.handle(e);
			}
		}
	}

	/**
	 * 读取数据（Object）
	 *
	 * @param fileName 文件名
	 * @param pathType 0 : AppConfig.PATH_TEXT_CACHE
	 *                 1 : AppConfig.PATH_TEXT_STORE
	 *                 2 : AppConfig.PATH_USER_DATA
	 * @return
	 */
	public static Object readFileSaveObject(String fileName, int pathType) {
		Object temp = null;
		FileInputStream in = null;
		ObjectInputStream objIn = null;
		try {
			String path;
			switch (pathType) {
				case 1:
					path = AppConfig.PATH_TEXT_STORE + fileName;
					break;
				case 2:
					path = AppConfig.PATH_USER_DATA + fileName;
					break;
				default:
					path = AppConfig.PATH_TEXT_CACHE + fileName;
					break;
			}
			File file = new File(path);
			if (file.exists()) {
				in = new FileInputStream(file);
				objIn = new ObjectInputStream(in);
				temp = objIn.readObject();
			}
		} catch (Exception e) {
			ExceptionUtil.handle(e);
		}finally{
			try {
				if (in != null) {
					in.close();
				}
				if (objIn != null) {
					objIn.close();
				}
			} catch (Exception e) {
				ExceptionUtil.handle(e);
			}
		}
		return temp;
	}

	/**
	 * 校验文件路径，不存在则创建
	 */
	public static void checkFilePath(String path) throws IOException {
		File file = new File(path);
		//判定文件所在的目录是否存在，不存在则创建
		File parentFile = file.getParentFile();
		if(parentFile != null && !parentFile.exists()){
			parentFile.mkdirs();
		}
		//判断文件是否存在,不存在则创建
		if(!file.exists()){
			file.createNewFile();
		}
	}

	/**
	 * 校验文件是否存在
	 */
	public static boolean checkFileExists(String path) {
		if (StringUtil.isNull(path)) return false;
		File file = new File(path);
		//判定文件所在的目录是否存在
		File parentFile = file.getParentFile();
		if(parentFile == null || !parentFile.exists()){
			return false;
		}
		//判断文件是否存在
		return file.exists();
	}

	/**
	 * 获取文件夹大小
	 *
	 * @param file File实例
	 * @return long 单位为b
	 * @throws Exception
	 */
	public static long getFolderSize(java.io.File file) throws Exception {
		long size = 0;
		if (file == null) {
			return size;
		}
		java.io.File[] fileList = file.listFiles();
		if (fileList == null) {
			return size;
		}
		for (java.io.File files: fileList) {
			if (files.isDirectory()) {
				size = size + getFolderSize(files);
			} else {
				size = size + files.length();
			}
		}
		return size;
	}

	/**
	 * 获取文件夹文件数目
	 */
	public static int getFolderNum(java.io.File file) throws Exception {
		if (file == null) {
			return 0;
		}
		java.io.File[] fileList = file.listFiles();
		if (fileList == null) {
			return 0;
		}
		return fileList.length;
	}

	/**
	 * 删除指定目录下文件及目录
	 */
	public static void deleteFolderFile(File file) throws IOException {
		if (file == null) {
			return;
		}
		if(file.isFile()){ //文件
			file.delete();
			return;
		}
		if(file.isDirectory()){ //文件夹
			File[] childFile = file.listFiles();
			if(childFile == null || childFile.length == 0){
				file.delete();
				return;
			}
			for(File f : childFile){
				deleteFolderFile(f); //递归
			}
			file.delete();
		}
	}

	/**
	 * 从给定的Uri返回文件的绝对路径
	 *
	 * @param uri
	 * @return the file path or null
	 */
	public static String getRealFilePath(final Uri uri ) {
		if ( null == uri ) return null;
		final String scheme = uri.getScheme();
		String data = null;
		if ( scheme == null )
			data = uri.getPath();
		else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
			data = uri.getPath();
		} else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
			Cursor cursor = AppApplication.getAppContext().getContentResolver()
					.query( uri, new String[] { ImageColumns.DATA }, null, null, null );
			if ( null != cursor ) {
				if ( cursor.moveToFirst() ) {
					int index = cursor.getColumnIndex( ImageColumns.DATA );
					if ( index > -1 ) {
						data = cursor.getString( index );
					}
				}
				cursor.close();
			}
		}
		return data;
	}

	/**
	 * 使用当前时间生成文件名
	 * @param fileType 文件类型(.jpg / .txt / ...)
	 */
	public static String getFileName(String fileType){
		SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss", Locale.getDefault());
		String dateTime = s.format(new Date());
		return dateTime + fileType;
	}

	/**
	 * 读取指定文件中的内容
	 */
	public static String getStringFromFile(File file) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		reader.close();
		fis.close();
		return sb.toString();
	}

	/**
	 * 读取Asset中的文件内容
	 */
	public static String loadJSONFromAsset(String filename) {
		String json;
		try {
			InputStream is = AppApplication.getAppContext().getAssets().open(filename);
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			json = new String(buffer, "UTF-8");
		} catch (Exception e) {
			ExceptionUtil.handle(e);
			return null;
		}
		return json;
	}

	/**
	 * 读取Asset中的图片
	 */
	public static Bitmap getBitmapFromAssets(String filename){
		try {
			InputStream is = AppApplication.getAppContext().getAssets().open(filename + ".png");
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			is.close();
			return bitmap;
		} catch (IOException e) {
			ExceptionUtil.handle(e);
			return null;
		}
	}

	/**
	 * 调用系统应用打开文件
	 */
	public static void openFile(Context context, File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//设置intent的Action属性
		intent.setAction(Intent.ACTION_VIEW);

		Uri uri;
		// 支持Android7.0，Android 7.0以后，用了Content Uri 替换了原本的File Uri
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
			uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// 给目标应用一个临时授权
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		} else {
			uri = Uri.fromFile(file);
		}

		//获取文件file的MIME类型
		String type = getMIMEType(file);
		//设置intent的data和Type属性。
		intent.setDataAndType(uri, type);
		//跳转
		try {
			context.startActivity(intent);
		} catch (Exception e) {
			ExceptionUtil.handle(e);
			CommonTools.showToast("找不到打开此文件的应用！");
		}
	}

	/**
	 * 转换 content:// uri
	 */
	private static Uri getImageContentUri(Context context, File imageFile) {
		String filePath = imageFile.getAbsolutePath();
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				new String[]{MediaStore.Images.Media._ID},
				MediaStore.Images.Media.DATA + "=? ",
				new String[]{filePath}, null);

		if (cursor != null && cursor.moveToFirst()) {
			int id = cursor.getInt(cursor
					.getColumnIndex(MediaStore.MediaColumns._ID));
			Uri baseUri = Uri.parse("content://media/external/images/media");
			cursor.close();
			return Uri.withAppendedPath(baseUri, "" + id);
		} else {
			if (cursor != null) {
				cursor.close();
			}
			if (imageFile.exists()) {
				ContentValues values = new ContentValues();
				values.put(MediaStore.Images.Media.DATA, filePath);
				return context.getContentResolver().insert(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			} else {
				return null;
			}
		}
	}

	/**
	 * 根据文件后缀回去MIME类型
	 */
	private static String getMIMEType(File file) {
		String type = "*/*";
		String fName = file.getName();

		//获取后缀名前的分隔符"."在fName中的位置。
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}

		//获取文件的后缀名
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if (TextUtils.isEmpty(end)) {
			return type;
		}

		//在MIME和文件类型的匹配表中找到对应的MIME类型。
		for (String[] tab : MIME_MapTable) {
			if (end.equals(tab[0])) {
				type = tab[1];
				break;
			}
		}
		return type;
	}

	private static final String[][] MIME_MapTable = {
			// {后缀名，MIME类型}
			{".3gp", "video/3gpp"},
			{".apk", "application/vnd.android.package-archive"},
			{".asf", "video/x-ms-asf"},
			{".avi", "video/x-msvideo"},
			{".bin", "application/octet-stream"},
			{".bmp", "image/bmp"},
			{".c", "text/plain"},
			{".class", "application/octet-stream"},
			{".conf", "text/plain"},
			{".cpp", "text/plain"},
			{".doc", "application/msword"},
			{".docx","application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
			{".xls", "application/vnd.ms-excel"},
			{".xlsx","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
			{".exe", "application/octet-stream"},
			{".gif", "image/gif"},
			{".gtar", "application/x-gtar"},
			{".gz", "application/x-gzip"},
			{".h", "text/plain"},
			{".htm", "text/html"},
			{".html", "text/html"},
			{".jar", "application/java-archive"},
			{".java", "text/plain"},
			{".jpeg", "image/jpeg"},
			{".jpg", "image/jpeg"},
			{".js", "application/x-javascript"},
			{".log", "text/plain"},
			{".m3u", "audio/x-mpegurl"},
			{".m4a", "audio/mp4a-latm"},
			{".m4b", "audio/mp4a-latm"},
			{".m4p", "audio/mp4a-latm"},
			{".m4u", "video/vnd.mpegurl"},
			{".m4v", "video/x-m4v"},
			{".mov", "video/quicktime"},
			{".mp2", "audio/x-mpeg"},
			{".mp3", "audio/x-mpeg"},
			{".mp4", "video/mp4"},
			{".mpc", "application/vnd.mpohun.certificate"},
			{".mpe", "video/mpeg"},
			{".mpeg", "video/mpeg"},
			{".mpg", "video/mpeg"},
			{".mpg4", "video/mp4"},
			{".mpga", "audio/mpeg"},
			{".msg", "application/vnd.ms-outlook"},
			{".ogg", "audio/ogg"},
			{".dwg", "application/pdf"},
			{".pdf", "application/pdf"},
			{".png", "image/png"},
			{".pps", "application/vnd.ms-powerpoint"},
			{".ppt", "application/vnd.ms-powerpoint"},
			{".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
			{".prop", "text/plain"},
			{".rc", "text/plain"},
			{".rmvb", "audio/x-pn-realaudio"},
			{".rtf", "application/rtf"},
			{".sh", "text/plain"},
			{".tar", "application/x-tar"},
			{".tgz", "application/x-compressed"},
			{".txt", "text/plain"},
			{".wav", "audio/x-wav"},
			{".wma", "audio/x-ms-wma"},
			{".wmv", "audio/x-ms-wmv"},
			{".wps", "application/vnd.ms-works"},
			{".xml", "text/plain"},
			{".z", "application/x-compress"},
			{".zip", "application/x-zip-compressed"},
			{"", "*/*"}
	};

}
