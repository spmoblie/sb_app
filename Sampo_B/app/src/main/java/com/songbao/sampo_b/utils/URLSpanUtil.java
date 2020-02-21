package com.songbao.sampo_b.utils;

import android.text.TextPaint;
import android.text.style.URLSpan;

public class URLSpanUtil extends URLSpan {

    public URLSpanUtil(String url) {
        super(url);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
    }

}
