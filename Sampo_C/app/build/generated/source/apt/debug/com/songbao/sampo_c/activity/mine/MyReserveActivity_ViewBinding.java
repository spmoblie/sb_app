// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_c.activity.mine;

import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.widgets.pullrefresh.PullToRefreshRecyclerView;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class MyReserveActivity_ViewBinding<T extends MyReserveActivity> implements Unbinder {
  protected T target;

  public MyReserveActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.refresh_rv = finder.findRequiredViewAsType(source, R.id.refresh_view_rv, "field 'refresh_rv'", PullToRefreshRecyclerView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.refresh_rv = null;

    this.target = null;
  }
}
