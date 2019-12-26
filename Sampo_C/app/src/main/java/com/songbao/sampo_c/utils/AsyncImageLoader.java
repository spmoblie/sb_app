package com.songbao.sampo_c.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.songbao.sampo_c.AppApplication;

import java.util.ArrayList;

/**
 * 执行图片下载任务并缓存到集合
 */
public class AsyncImageLoader {

	private ArrayList<ImageLoadTask> tasks;
	private Thread workThread;
	private Handler handler;
	private boolean isLoop;
	private AsyncImageLoaderCallback ailCallback;
	private static AsyncImageLoader instance;

	/**
	 * 创建此对象请记得在Activity的onPause()中调用clearInstance()销毁对象
	 */
	public static AsyncImageLoader getInstance(AsyncImageLoaderCallback callback) {
		if (instance == null) {
			syncInit(callback);
		}
		return instance;
	}

	private static synchronized void syncInit(AsyncImageLoaderCallback callback) {
		if (instance == null) {
			instance = new AsyncImageLoader(callback);
		}
	}

	@SuppressLint("HandlerLeak")
	private AsyncImageLoader(AsyncImageLoaderCallback callback) {
		this.isLoop = true;
		this.tasks = new ArrayList<>();
		this.ailCallback = callback;

		this.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (ailCallback != null) {
					ImageLoadTask task = (ImageLoadTask) msg.obj;
					ailCallback.imageLoaded(task.oldPath, task.newPath, task.bitmap);
				}
			}
		};
		this.workThread = new Thread() {
			@Override
			public void run() {
				while (isLoop) {
					while (isLoop && !tasks.isEmpty()) {
						// 从任务队列获取任务
						ImageLoadTask task = tasks.remove(0);
						try{
							FutureTarget<Bitmap> ft = Glide
									.with(AppApplication.getAppContext())
									.asBitmap()
									.load(task.oldPath)
									.submit();
							task.bitmap = ft.get();
							if (task.bitmap != null) {
								// 缓存到集合
								BitmapCache.getInstance().addCacheBitmap(task.bitmap, task.newPath);
							}
						}catch (Exception e) {
							ExceptionUtil.handle(e);
						}finally {
							Message msg = Message.obtain();
							msg.obj = task;
							handler.sendMessage(msg);
						}
					}
					if (!isLoop) {
						break;
					}
					synchronized (workThread) {
						try {
							workThread.wait();
						} catch (InterruptedException e) {
							ExceptionUtil.handle(e);
						}
					}
				}
			}
		};
		this.workThread.start();
	}

	public void quit() {
		isLoop = false;
		synchronized (workThread) {
			try {
				workThread.notify();
			} catch (Exception e) {
				ExceptionUtil.handle(e);
			}
		}
	}
	
	public void clearInstance(){
		quit();
		instance = null;
	}

	/**
	 * 根据指定的图片路径获取图片对象
	 * 
	 * @param oldPath 图片路径
	 */
	public ImageLoadTask loadImage(String oldPath) {
		if (StringUtil.isNull(oldPath)) return null;
		ImageLoadTask task;
		String newPath;
		if (oldPath.contains(":")) {
			newPath = oldPath.toString().replace(":", "");
		} else {
			newPath = oldPath;
		}
		try {
			// 判定缓存集合中是否存在图片,如果存在则直接返回
			Bitmap bm = BitmapCache.getInstance().getBitmap(newPath);
			if (bm != null) {
				task = new ImageLoadTask(oldPath, newPath, bm);
				return task;
			}
			// 判定SD卡中是否存在图片,如果存在则直接返回
			/*File file = BitmapUtil.createPath(newPath, false);
			if (file != null) {
				bm = BitmapUtil.getBitmap(file.getAbsolutePath());
				if (bm != null) {
					task = new ImageLoadTask(oldPath, file.getAbsolutePath(), bm);
					return task;
				}
			}*/
		} catch (Exception e) {
			ExceptionUtil.handle(e);
			return  null;
		}
		// 缓存及SD卡都不存在图片则新建任务加入任务队列
		task = new ImageLoadTask(newPath, oldPath);
		if (!tasks.equals(task)) {
			tasks.add(task);
			synchronized (workThread) {
				try {
					// 唤醒工作线程
					workThread.notify();
				} catch (Exception e) {
					ExceptionUtil.handle(e);
				}
			}
		}
		return task;
	}

	public class ImageLoadTask {
		private String newPath;
		private String oldPath;
		private Bitmap bitmap;

		public ImageLoadTask(String oldPath, String newPath, Bitmap bitmap) {
			this.oldPath = oldPath;
			this.newPath = newPath;
			this.bitmap = bitmap;
		}

		public ImageLoadTask(String newPath, String oldPath) {
			this.newPath = newPath;
			this.oldPath = oldPath;
		}

		public String getNewPath() {
			return newPath;
		}

		public String getOldPath() {
			return oldPath;
		}

		public Bitmap getBitmap() {
			return bitmap;
		}

		@Override
		public boolean equals(Object o) {
			ImageLoadTask task = (ImageLoadTask) o;
			return newPath.equals(task.newPath);
		}
	}

	public interface AsyncImageLoaderCallback {
		void imageLoaded(String path, String cachePath, Bitmap bm);
	}

}
