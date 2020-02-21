// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_b.activity.two;

import android.support.constraint.ConstraintLayout;
import android.widget.ImageView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.widgets.pullrefresh.PullToRefreshGridView;
import com.songbao.sampo_b.widgets.pullrefresh.PullToRefreshRecyclerView;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class ChildFragmentTwo_ViewBinding<T extends ChildFragmentTwo> implements Unbinder {
  protected T target;

  public ChildFragmentTwo_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.rv_left = finder.findRequiredViewAsType(source, R.id.fg_two_rv_left, "field 'rv_left'", PullToRefreshRecyclerView.class);
    target.gv_right = finder.findRequiredViewAsType(source, R.id.fg_two_gv_right, "field 'gv_right'", PullToRefreshGridView.class);
    target.view_null = finder.findRequiredViewAsType(source, R.id.fg_two_data_null_main, "field 'view_null'", ConstraintLayout.class);
    target.iv_scan = finder.findRequiredViewAsType(source, R.id.fg_two_iv_scan, "field 'iv_scan'", ImageView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.rv_left = null;
    target.gv_right = null;
    target.view_null = null;
    target.iv_scan = null;

    this.target = null;
  }
}
