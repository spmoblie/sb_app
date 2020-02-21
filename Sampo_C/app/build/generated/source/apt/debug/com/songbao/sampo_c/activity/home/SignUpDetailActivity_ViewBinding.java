// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_c.activity.home;

import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.widgets.ObservableWebView;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class SignUpDetailActivity_ViewBinding<T extends SignUpDetailActivity> implements Unbinder {
  protected T target;

  public SignUpDetailActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.iv_show = finder.findRequiredViewAsType(source, R.id.sign_up_detail_iv_show, "field 'iv_show'", ImageView.class);
    target.tv_title = finder.findRequiredViewAsType(source, R.id.sign_up_detail_tv_title, "field 'tv_title'", TextView.class);
    target.tv_name = finder.findRequiredViewAsType(source, R.id.sign_up_detail_tv_name, "field 'tv_name'", TextView.class);
    target.tv_series = finder.findRequiredViewAsType(source, R.id.sign_up_detail_tv_series, "field 'tv_series'", TextView.class);
    target.tv_time = finder.findRequiredViewAsType(source, R.id.sign_up_detail_tv_time, "field 'tv_time'", TextView.class);
    target.tv_place = finder.findRequiredViewAsType(source, R.id.sign_up_detail_tv_place, "field 'tv_place'", TextView.class);
    target.tv_people = finder.findRequiredViewAsType(source, R.id.sign_up_detail_tv_people, "field 'tv_people'", TextView.class);
    target.tv_suit = finder.findRequiredViewAsType(source, R.id.sign_up_detail_tv_suit, "field 'tv_suit'", TextView.class);
    target.tv_click = finder.findRequiredViewAsType(source, R.id.sign_up_detail_tv_click, "field 'tv_click'", TextView.class);
    target.myWebView = finder.findRequiredViewAsType(source, R.id.sign_up_detail_web_view, "field 'myWebView'", ObservableWebView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.iv_show = null;
    target.tv_title = null;
    target.tv_name = null;
    target.tv_series = null;
    target.tv_time = null;
    target.tv_place = null;
    target.tv_people = null;
    target.tv_suit = null;
    target.tv_click = null;
    target.myWebView = null;

    this.target = null;
  }
}
