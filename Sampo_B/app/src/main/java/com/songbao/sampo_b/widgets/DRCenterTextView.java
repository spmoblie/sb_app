package com.songbao.sampo_b.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * 自定义控件，使用drawableRight与text水平居中显示
 */
public class DRCenterTextView extends AppCompatTextView {

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
        Drawable drawableRight = drawables[2];
        if(drawableRight != null){
            float textWidth = getPaint().measureText(getText().toString());
            int drawablePadding = getCompoundDrawablePadding();
            int drawableWidth;
            drawableWidth = drawableRight.getIntrinsicWidth();
            float bodyWidth = textWidth + drawableWidth + drawablePadding;
            setPadding(getPaddingLeft(), getPaddingTop(), (int)(getWidth() - bodyWidth), getPaddingBottom());
            canvas.translate((getWidth() - bodyWidth) / 2, 0);
        }
        super.onDraw(canvas);
    }

}
