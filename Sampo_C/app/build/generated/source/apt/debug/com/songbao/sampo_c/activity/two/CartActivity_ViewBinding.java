// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_c.activity.two;

import android.support.constraint.Group;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.widgets.pullrefresh.PullToRefreshRecyclerView;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class CartActivity_ViewBinding<T extends CartActivity> implements Unbinder {
  protected T target;

  public CartActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.refresh_rv = finder.findRequiredViewAsType(source, R.id.cart_rv_goods, "field 'refresh_rv'", PullToRefreshRecyclerView.class);
    target.iv_select_all = finder.findRequiredViewAsType(source, R.id.cart_view_iv_select_all, "field 'iv_select_all'", ImageView.class);
    target.tv_select_all = finder.findRequiredViewAsType(source, R.id.cart_view_tv_select_all, "field 'tv_select_all'", TextView.class);
    target.tv_price = finder.findRequiredViewAsType(source, R.id.cart_view_tv_price, "field 'tv_price'", TextView.class);
    target.tv_price_group = finder.findRequiredViewAsType(source, R.id.cart_view_tv_price_group, "field 'tv_price_group'", Group.class);
    target.tv_confirm = finder.findRequiredViewAsType(source, R.id.cart_view_tv_confirm, "field 'tv_confirm'", TextView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.refresh_rv = null;
    target.iv_select_all = null;
    target.tv_select_all = null;
    target.tv_price = null;
    target.tv_price_group = null;
    target.tv_confirm = null;

    this.target = null;
  }
}
