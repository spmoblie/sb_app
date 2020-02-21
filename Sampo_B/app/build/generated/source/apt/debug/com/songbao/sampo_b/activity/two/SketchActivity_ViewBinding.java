// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_b.activity.two;

import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.widgets.ObservableWebView;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class SketchActivity_ViewBinding<T extends SketchActivity> implements Unbinder {
  protected T target;

  public SketchActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.myWebView = finder.findRequiredViewAsType(source, R.id.sketch_web, "field 'myWebView'", ObservableWebView.class);
    target.tv_click = finder.findRequiredViewAsType(source, R.id.sketch_tv_click, "field 'tv_click'", TextView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.myWebView = null;
    target.tv_click = null;

    this.target = null;
  }
}
