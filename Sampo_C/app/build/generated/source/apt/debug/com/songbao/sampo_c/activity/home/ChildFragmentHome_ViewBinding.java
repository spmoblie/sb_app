// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_c.activity.home;

import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.widgets.pullrefresh.PullToRefreshRecyclerView;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class ChildFragmentHome_ViewBinding<T extends ChildFragmentHome> implements Unbinder {
  protected T target;

  public ChildFragmentHome_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.refresh_lv = finder.findRequiredViewAsType(source, R.id.fg_home_refresh_lv, "field 'refresh_lv'", PullToRefreshRecyclerView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.refresh_lv = null;

    this.target = null;
  }
}
