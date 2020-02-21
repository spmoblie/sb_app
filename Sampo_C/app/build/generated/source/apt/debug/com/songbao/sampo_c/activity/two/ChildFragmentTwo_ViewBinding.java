// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_c.activity.two;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.widgets.pullrefresh.PullToRefreshRecyclerView;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class ChildFragmentTwo_ViewBinding<T extends ChildFragmentTwo> implements Unbinder {
  protected T target;

  public ChildFragmentTwo_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.et_search = finder.findRequiredViewAsType(source, R.id.fg_two_et_search, "field 'et_search'", EditText.class);
    target.tv_scan = finder.findRequiredViewAsType(source, R.id.fg_two_tv_scan, "field 'tv_scan'", TextView.class);
    target.rv_left = finder.findRequiredViewAsType(source, R.id.fg_two_rv_left, "field 'rv_left'", PullToRefreshRecyclerView.class);
    target.rv_right = finder.findRequiredViewAsType(source, R.id.fg_two_rv_right, "field 'rv_right'", PullToRefreshRecyclerView.class);
    target.iv_cart = finder.findRequiredViewAsType(source, R.id.fg_two_iv_cart, "field 'iv_cart'", ImageView.class);
    target.tv_cart_num = finder.findRequiredViewAsType(source, R.id.fg_two_tv_cart_num, "field 'tv_cart_num'", TextView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.et_search = null;
    target.tv_scan = null;
    target.rv_left = null;
    target.rv_right = null;
    target.iv_cart = null;
    target.tv_cart_num = null;

    this.target = null;
  }
}
