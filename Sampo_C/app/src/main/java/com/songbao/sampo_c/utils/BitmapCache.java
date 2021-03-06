package com.songbao.sampo_c.utils;

import android.graphics.Bitmap;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Hashtable;

/**
 * 缓存及释放图片，防止内存溢出
 */
public class BitmapCache {
	
	private static BitmapCache cache;

	/** 垃圾Reference的队列（所引用的对象已经被回收，则将该引用存入队列中） */
	private ReferenceQueue<Bitmap> q;

	/** 用于Chche内容的存储 */
	private Hashtable<String, BitmapRef> bitmapRefs;

	/**
	 * 继承SoftReference，使得每一个实例都具有可识别的标识。
	 */
	private class BitmapRef extends SoftReference<Bitmap> {
		private String _key = "";

		public BitmapRef(Bitmap bmp, ReferenceQueue<Bitmap> q, String key) {
			super(bmp, q);
			_key = key;
		}
	}

	private BitmapCache() {
		q = new ReferenceQueue<>();
		bitmapRefs = new Hashtable<>();
	}

	/**
	 * 取得缓存器实例
	 */
	public static BitmapCache getInstance() {
		if (cache == null) {
			cache = new BitmapCache();
		}
		return cache;

	}

	/**
	 * 以软引用的方式对一个Bitmap对象的实例进行引用并保存该引用
	 */
	public void addCacheBitmap(Bitmap bmp, String key) {
		if (bitmapRefs.size() >= 100) {
			clearCache(); //清除全部缓存
			cache = null; //重新创建对象
			return;
		} else {
			cleanCache(); //清除垃圾引用
		}
		BitmapRef ref = new BitmapRef(bmp, q, key);
		bitmapRefs.put(key, ref);
	}

	/**
	 * 依据所指定的文件名获取图片
	 */
	public Bitmap getBitmap(String filename) {
		if (StringUtil.isNull(filename)) {
			return null;
		}
		Bitmap bitmapImage = null;
		// 缓存中是否有该Bitmap实例的软引用，如果有，从软引用中取得。
		if (bitmapRefs != null && bitmapRefs.containsKey(filename)) {
			BitmapRef ref = bitmapRefs.get(filename);
			bitmapImage = ref.get();
		}
		return bitmapImage;
	}

	/**
	 * 清除垃圾引用
	 */
	private void cleanCache() {
		BitmapRef ref;
		while ((ref = (BitmapRef) q.poll()) != null) {
			bitmapRefs.remove(ref._key);
		}
	}

	/**
	 * 清除全部缓存
	 */
	public void clearCache() {
		cleanCache();
		bitmapRefs.clear();
		System.gc();
		System.runFinalization();
	}

}
