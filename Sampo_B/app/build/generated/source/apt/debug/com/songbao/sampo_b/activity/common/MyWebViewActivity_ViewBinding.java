// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_b.activity.common;

import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.widgets.ObservableWebView;
import com.songbao.sampo_b.widgets.WebViewLoadingBar;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class MyWebViewActivity_ViewBinding<T extends MyWebViewActivity> implements Unbinder {
  protected T target;

  public MyWebViewActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.myWebView = finder.findRequiredViewAsType(source, R.id.my_web_view, "field 'myWebView'", ObservableWebView.class);
    target.webViewLoadingBar = finder.findRequiredViewAsType(source, R.id.web_view_loading_bar, "field 'webViewLoadingBar'", WebViewLoadingBar.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.myWebView = null;
    target.webViewLoadingBar = null;

    this.target = null;
  }
}
