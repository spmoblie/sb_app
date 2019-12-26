package com.songbao.sampo_b.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 自定义控件，使用drawableRight与text水平居中显示
 */
@SuppressLint("AppCompatCustomView")
public class DRCenterTextView extends TextView {

    public DRCenterTextView(Context context) {
        super(context);
    }

    public DRCenterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DRCenterTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();//left,top,right,bottom
        if(drawables != null){
            Drawable drawableRight = drawables[2];
            if(drawableRight != null){
                float textWidth = getPaint().measureText(getText().toString());
                int drawablePadding = getCompoundDrawablePadding();
                int drawableWidth = 0;
                drawableWidth = drawableRight.getIntrinsicWidth();
                float bodyWidth = textWidth + drawableWidth + drawablePadding;
                setPadding(getPaddingLeft(), getPaddingTop(), (int)(getWidth() - bodyWidth), getPaddingBottom());
                canvas.translate((getWidth() - bodyWidth) / 2, 0);
            }
        }
        super.onDraw(canvas);
    }

}
