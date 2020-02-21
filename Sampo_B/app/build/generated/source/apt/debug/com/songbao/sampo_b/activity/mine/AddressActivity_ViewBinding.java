// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_b.activity.mine;

import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.widgets.pullrefresh.PullToRefreshRecyclerView;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class AddressActivity_ViewBinding<T extends AddressActivity> implements Unbinder {
  protected T target;

  public AddressActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.refresh_rv = finder.findRequiredViewAsType(source, R.id.address_rv_address, "field 'refresh_rv'", PullToRefreshRecyclerView.class);
    target.tv_address_add = finder.findRequiredViewAsType(source, R.id.address_view_tv_address_add, "field 'tv_address_add'", TextView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.refresh_rv = null;
    target.tv_address_add = null;

    this.target = null;
  }
}
