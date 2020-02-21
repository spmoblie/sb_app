// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_b.activity;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_b.R;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class MainActivity_ViewBinding<T extends MainActivity> implements Unbinder {
  protected T target;

  public MainActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.fl_show = finder.findRequiredViewAsType(source, R.id.main_fragment_fl_show, "field 'fl_show'", FrameLayout.class);
    target.tab_text_1 = finder.findRequiredViewAsType(source, R.id.main_fragment_tab_tv_1, "field 'tab_text_1'", TextView.class);
    target.tab_text_2 = finder.findRequiredViewAsType(source, R.id.main_fragment_tab_tv_2, "field 'tab_text_2'", TextView.class);
    target.tab_text_3 = finder.findRequiredViewAsType(source, R.id.main_fragment_tab_tv_3, "field 'tab_text_3'", TextView.class);
    target.tab_icon_1 = finder.findRequiredViewAsType(source, R.id.main_fragment_tab_iv_1, "field 'tab_icon_1'", ImageView.class);
    target.tab_icon_2 = finder.findRequiredViewAsType(source, R.id.main_fragment_tab_iv_2, "field 'tab_icon_2'", ImageView.class);
    target.tab_icon_3 = finder.findRequiredViewAsType(source, R.id.main_fragment_tab_iv_3, "field 'tab_icon_3'", ImageView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.fl_show = null;
    target.tab_text_1 = null;
    target.tab_text_2 = null;
    target.tab_text_3 = null;
    target.tab_icon_1 = null;
    target.tab_icon_2 = null;
    target.tab_icon_3 = null;

    this.target = null;
  }
}
