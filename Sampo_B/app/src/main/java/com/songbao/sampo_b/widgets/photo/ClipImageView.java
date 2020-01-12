package com.songbao.sampo_b.widgets.photo;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;

import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.utils.ExceptionUtil;


/**
 * 用于缩放裁剪的自定义ImageView视图
 */
public class ClipImageView extends AppCompatImageView implements View.OnTouchListener,
		ViewTreeObserver.OnGlobalLayoutListener {

	// 裁剪圆形图片宽高
	private static final int CIRCULAR_RADIUS = ClipViewCircular.CIRCULAR_RADIUS * 2;
	// 裁剪方形图片宽高
	private static final int WIDTH_HEIGHT = ClipViewSquare.WIDTH_HEIGHT;
	
	public static final float DEFAULT_MAX_SCALE = 4.0f; // 缩放的最大倍数
	public static final float DEFAULT_MID_SCALE = 2.0f; 
	public static final float DEFAULT_MIN_SCALE = 0.8f; // 缩放的最小倍数

	private SharedPreferences shared;
	private MultiGestureDetector multiGestureDetector;

	private int borderLength;
	private boolean isJust;


	private final Matrix baseMatrix = new Matrix();
	private final Matrix drawMatrix = new Matrix();
	private final Matrix suppMatrix = new Matrix();
	private final RectF displayRect = new RectF();
	private final float[] matrixValues = new float[9];

	public ClipImageView(Context context) {
		this(context, null);
	}

	public ClipImageView(Context context, AttributeSet attr) {
		this(context, attr, 0);
	}

	public ClipImageView(Context context, AttributeSet attr, int defStyle) {
		super(context, attr, defStyle);

		super.setScaleType(ScaleType.MATRIX);
		
		setOnTouchListener(this);

		shared = AppApplication.getSharedPreferences();
		multiGestureDetector = new MultiGestureDetector(context);

	}

	/**
	 * 依据图片宽高比例，设置图像初始缩放等级和位置
	 */
	private void configPosition() {
		super.setScaleType(ScaleType.MATRIX);
		Drawable d = getDrawable();
		if (d == null) {
			return;
		}
		final float viewWidth = getWidth();
		final float viewHeight = getHeight();
		final int drawableWidth = d.getIntrinsicWidth();
		final int drawableHeight = d.getIntrinsicHeight();
		// 获取剪切后图片的宽高
		switch (shared.getInt(AppConfig.KEY_CLIP_PHOTO_TYPE, AppConfig.PHOTO_TYPE_ROUND)) {
		case AppConfig.PHOTO_TYPE_ROUND: //圆形
			borderLength = CIRCULAR_RADIUS;
			break;
		case AppConfig.PHOTO_TYPE_SQUARE: //方形
			borderLength = WIDTH_HEIGHT;
			break;
		default: //圆形
			borderLength = CIRCULAR_RADIUS;
			break;
		}
		float scale = 1.0f;
		// 判断图片宽高比例，调整显示位置和缩放大小
		// 图片宽度小于等于高度
		if (drawableWidth <= drawableHeight) {
			// 判断图片宽度是否小于边框, 缩放铺满裁剪边框
			if (drawableWidth < borderLength) {
				baseMatrix.reset();
				scale = (float) borderLength / drawableWidth;
				// 缩放
				baseMatrix.postScale(scale, scale);
			}
			// 图片宽度大于高度
		} else {
			if (drawableHeight < borderLength) {
				baseMatrix.reset();
				scale = (float) borderLength / drawableHeight;
				// 缩放
				baseMatrix.postScale(scale, scale);
			}
		}
		// 移动居中
		baseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2,
				(viewHeight - drawableHeight * scale) / 2);

		resetMatrix();
		isJust = true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return multiGestureDetector.onTouchEvent(event);
	}

	private class MultiGestureDetector extends GestureDetector.SimpleOnGestureListener implements OnScaleGestureListener {

		private final ScaleGestureDetector scaleGestureDetector;
		private final GestureDetector gestureDetector;
		private final float scaledTouchSlop;

		private VelocityTracker velocityTracker;
		private boolean isDragging;

		private float lastTouchX;
		private float lastTouchY;
		private float lastPointerCount;

		MultiGestureDetector(Context context) {
			scaleGestureDetector = new ScaleGestureDetector(context, this);

			gestureDetector = new GestureDetector(context, this);
			gestureDetector.setOnDoubleTapListener(this);

			final ViewConfiguration configuration = ViewConfiguration
					.get(context);
			scaledTouchSlop = configuration.getScaledTouchSlop();
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			float scale = getScale();
			float scaleFactor = detector.getScaleFactor();
			if (getDrawable() != null
					&& ((scale < DEFAULT_MAX_SCALE && scaleFactor > 1.0f) || (scale > DEFAULT_MIN_SCALE && scaleFactor < 1.0f))) {
				if (scaleFactor * scale < DEFAULT_MIN_SCALE) {
					scaleFactor = DEFAULT_MIN_SCALE / scale;
				}
				if (scaleFactor * scale > DEFAULT_MAX_SCALE) {
					scaleFactor = DEFAULT_MAX_SCALE / scale;
				}
				suppMatrix.postScale(scaleFactor, scaleFactor, getWidth() / 2, getHeight() / 2);
				checkAndDisplayMatrix();
			}
			return true;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
		}

		boolean onTouchEvent(MotionEvent event) {
			if (gestureDetector.onTouchEvent(event)) {
				return true;
			}

			scaleGestureDetector.onTouchEvent(event);

			/*
			 * Get the center x, y of all the pointers
			 */
			float x = 0, y = 0;
			final int pointerCount = event.getPointerCount();
			for (int i = 0; i < pointerCount; i++) {
				x += event.getX(i);
				y += event.getY(i);
			}
			x = x / pointerCount;
			y = y / pointerCount;

			/*
			 * If the pointer count has changed cancel the drag
			 */
			if (pointerCount != lastPointerCount) {
				isDragging = false;
				if (velocityTracker != null) {
					velocityTracker.clear();
				}
				lastTouchX = x;
				lastTouchY = y;
			}
			lastPointerCount = pointerCount;

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (velocityTracker == null) {
					velocityTracker = VelocityTracker.obtain();
				} else {
					velocityTracker.clear();
				}
				velocityTracker.addMovement(event);

				lastTouchX = x;
				lastTouchY = y;
				isDragging = false;
				break;

			case MotionEvent.ACTION_MOVE: {
				final float dx = x - lastTouchX, dy = y - lastTouchY;

				if (!isDragging) {
					// Use Pythagoras to see if drag length is larger than
					// touch slop
					isDragging = Math.sqrt((dx * dx) + (dy * dy)) >= scaledTouchSlop;
				}

				if (isDragging) {
					if (getDrawable() != null) {
						suppMatrix.postTranslate(dx, dy);
						checkAndDisplayMatrix();
					}

					lastTouchX = x;
					lastTouchY = y;

					if (velocityTracker != null) {
						velocityTracker.addMovement(event);
					}
				}
				break;
			}
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				lastPointerCount = 0;
				if (velocityTracker != null) {
					velocityTracker.recycle();
					velocityTracker = null;
				}
				break;
			}

			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent event) {
			try {
				float scale = getScale();
				float x = getWidth() / 2;
				float y = getHeight() / 2;

				if (scale < DEFAULT_MID_SCALE) {
					post(new AnimatedZoomRunnable(scale, DEFAULT_MID_SCALE, x, y));
				} else if ((scale >= DEFAULT_MID_SCALE) && (scale < DEFAULT_MAX_SCALE)) {
					post(new AnimatedZoomRunnable(scale, DEFAULT_MAX_SCALE, x, y));
				} else {
					post(new AnimatedZoomRunnable(scale, DEFAULT_MIN_SCALE, x, y));
				}
			} catch (Exception e) {
				ExceptionUtil.handle(e);
			}

			return true;
		}
	}

	private class AnimatedZoomRunnable implements Runnable {
		// These are 'postScale' values, means they're compounded each iteration
		static final float ANIMATION_SCALE_PER_ITERATION_IN = 1.07f;
		static final float ANIMATION_SCALE_PER_ITERATION_OUT = 0.93f;

		private final float focalX, focalY;
		private final float targetZoom;
		private final float deltaScale;

		AnimatedZoomRunnable(final float currentZoom, final float targetZoom, final float focalX, final float focalY) {
			this.targetZoom = targetZoom;
			this.focalX = focalX;
			this.focalY = focalY;

			if (currentZoom < targetZoom) {
				deltaScale = ANIMATION_SCALE_PER_ITERATION_IN;
			} else {
				deltaScale = ANIMATION_SCALE_PER_ITERATION_OUT;
			}
		}

		public void run() {
			suppMatrix.postScale(deltaScale, deltaScale, focalX, focalY);
			checkAndDisplayMatrix();

			final float currentScale = getScale();

			if (((deltaScale > 1f) && (currentScale < targetZoom)) || ((deltaScale < 1f) && (targetZoom < currentScale))) {
				// We haven't hit our target scale yet, so post ourselves again
				postOnAnimation(ClipImageView.this, this);
			} else {
				// We've scaled past our target zoom, so calculate the necessary scale so we're back at target zoom
				final float delta = targetZoom / currentScale;
				suppMatrix.postScale(delta, delta, focalX, focalY);
				checkAndDisplayMatrix();
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void postOnAnimation(View view, Runnable runnable) {
		if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
			view.postOnAnimation(runnable);
		} else {
			view.postDelayed(runnable, 16);
		}
	}

	/**
	 * Returns the current scale value
	 *
	 * @return float - current scale value
	 */
	public final float getScale() {
		suppMatrix.getValues(matrixValues);
		return matrixValues[Matrix.MSCALE_X];
	}

	@Override
	public void onGlobalLayout() {
		if (isJust) {
			return;
		}
		// 调整视图位置
		configPosition();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		getViewTreeObserver().removeGlobalOnLayoutListener(this);
	}

	/**
	 * Helper method that simply checks the Matrix, and then displays the result
	 */
	private void checkAndDisplayMatrix() {
		checkMatrixBounds();
		setImageMatrix(getDisplayMatrix());
	}

	private void checkMatrixBounds() {
		final RectF rect = getDisplayRect(getDisplayMatrix());
		if (null == rect) {
			return;
		}

		float deltaX = 0, deltaY = 0;
		final float viewWidth = getWidth();
		final float viewHeight = getHeight();
		// 判断移动或缩放后，图片显示是否超出裁剪框边界
		if (rect.top > (viewHeight - borderLength) / 2) {
			deltaY = (viewHeight - borderLength) / 2 - rect.top;
		}
		if (rect.bottom < (viewHeight + borderLength) / 2) {
			deltaY = (viewHeight + borderLength) / 2 - rect.bottom;
		}
		if (rect.left > (viewWidth - borderLength) / 2) {
			deltaX = (viewWidth - borderLength) / 2 - rect.left;
		}
		if (rect.right < (viewWidth + borderLength) / 2) {
			deltaX = (viewWidth + borderLength) / 2 - rect.right;
		}
		// Finally actually translate the matrix
		suppMatrix.postTranslate(deltaX, deltaY);
	}

	/**
	 * Helper method that maps the supplied Matrix to the current Drawable
	 *
	 * @param matrix
	 *            - Matrix to map Drawable against
	 * @return RectF - Displayed Rectangle
	 */
	private RectF getDisplayRect(Matrix matrix) {
		Drawable d = getDrawable();
		if (null != d) {
			displayRect
					.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			matrix.mapRect(displayRect);
			return displayRect;
		}

		return null;
	}

	/**
	 * Resets the Matrix back to FIT_CENTER, and then displays it.s
	 */
	private void resetMatrix() {
		if (suppMatrix == null) {
			return;
		}
		suppMatrix.reset();
		setImageMatrix(getDisplayMatrix());
	}

	protected Matrix getDisplayMatrix() {
		drawMatrix.set(baseMatrix);
		drawMatrix.postConcat(suppMatrix);
		return drawMatrix;
	}

	/**
	 * 剪切图片，返回剪切后的bitmap对象
	 */
	public Bitmap clip() {

		int width = this.getWidth();
		int height = this.getHeight();

		if (width > borderLength && height > borderLength) {
			Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			draw(canvas);
			return Bitmap.createBitmap(bitmap, (width - borderLength) / 2, (height - borderLength) / 2, borderLength, borderLength);
		}else {
			return null;
		}
	}

}
