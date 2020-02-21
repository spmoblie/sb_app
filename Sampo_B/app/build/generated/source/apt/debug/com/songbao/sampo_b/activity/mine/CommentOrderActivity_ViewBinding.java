// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_b.activity.mine;

import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.widgets.pullrefresh.PullToRefreshRecyclerView;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class CommentOrderActivity_ViewBinding<T extends CommentOrderActivity> implements Unbinder {
  protected T target;

  public CommentOrderActivity_ViewBinding(T target, Finder finder, Object source) {
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
