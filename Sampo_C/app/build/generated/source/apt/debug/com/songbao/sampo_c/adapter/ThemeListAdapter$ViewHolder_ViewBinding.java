// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_c.adapter;

import android.support.constraint.ConstraintLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_c.R;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class ThemeListAdapter$ViewHolder_ViewBinding<T extends ThemeListAdapter.ViewHolder> implements Unbinder {
  protected T target;

  public ThemeListAdapter$ViewHolder_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.item_main = finder.findRequiredViewAsType(source, R.id.fg_home_item_main, "field 'item_main'", ConstraintLayout.class);
    target.iv_show = finder.findRequiredViewAsType(source, R.id.fg_home_item_iv, "field 'iv_show'", ImageView.class);
    target.tv_title = finder.findRequiredViewAsType(source, R.id.fg_home_item_tv_title, "field 'tv_title'", TextView.class);
    target.tv_name = finder.findRequiredViewAsType(source, R.id.fg_home_item_tv_name, "field 'tv_name'", TextView.class);
    target.tv_series = finder.findRequiredViewAsType(source, R.id.fg_home_item_tv_series, "field 'tv_series'", TextView.class);
    target.tv_time = finder.findRequiredViewAsType(source, R.id.fg_home_item_tv_time, "field 'tv_time'", TextView.class);
    target.tv_sign = finder.findRequiredViewAsType(source, R.id.fg_home_item_tv_sign, "field 'tv_sign'", TextView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.item_main = null;
    target.iv_show = null;
    target.tv_title = null;
    target.tv_name = null;
    target.tv_series = null;
    target.tv_time = null;
    target.tv_sign = null;

    this.target = null;
  }
}
