package com.songbao.sampo.widgets.photo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.songbao.sampo.AppApplication;

/**
 * 裁剪圆形边框
 */
public class ClipViewCircular extends View {

	private static int screenWidth = AppApplication.screen_width;

	/**
	 * 内圆半径
	 */
	public static final int CIRCULAR_RADIUS = screenWidth / 3;

	private Paint mPaint;

	public ClipViewCircular(Context context) {
		this(context, null);
	}

	public ClipViewCircular(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ClipViewCircular(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mPaint = new Paint();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = this.getWidth();
		int height = this.getHeight();

		int innerCircle = CIRCULAR_RADIUS; // 内圆半径
		int ringWidth = height; // 圆环宽度

		// 第一种方法绘制圆环
		// 绘制内圆
		mPaint.setColor(Color.WHITE);
		mPaint.setStrokeWidth(2);
		mPaint.setStyle(Paint.Style.STROKE);
		canvas.drawCircle(width / 2, height / 2, innerCircle, mPaint);

		// 绘制圆环
		mPaint.setColor(0xaa000000);
		mPaint.setStrokeWidth(ringWidth);
		canvas.drawCircle(width / 2, height / 2, innerCircle + 1 + ringWidth / 2, mPaint);

	}

}
