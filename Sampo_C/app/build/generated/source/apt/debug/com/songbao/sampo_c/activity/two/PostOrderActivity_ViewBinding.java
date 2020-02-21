// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_c.activity.two;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.widgets.ScrollViewListView;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class PostOrderActivity_ViewBinding<T extends PostOrderActivity> implements Unbinder {
  protected T target;

  public PostOrderActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.address_main = finder.findRequiredViewAsType(source, R.id.post_order_address_main, "field 'address_main'", ConstraintLayout.class);
    target.tv_address_add = finder.findRequiredViewAsType(source, R.id.post_order_tv_address_add, "field 'tv_address_add'", TextView.class);
    target.tv_address_district = finder.findRequiredViewAsType(source, R.id.post_order_tv_address_district, "field 'tv_address_district'", TextView.class);
    target.tv_address_detail = finder.findRequiredViewAsType(source, R.id.post_order_tv_address_detail, "field 'tv_address_detail'", TextView.class);
    target.tv_address_name = finder.findRequiredViewAsType(source, R.id.post_order_tv_address_name, "field 'tv_address_name'", TextView.class);
    target.tv_address_status = finder.findRequiredViewAsType(source, R.id.post_order_tv_address_status, "field 'tv_address_status'", TextView.class);
    target.tv_address_group = finder.findRequiredViewAsType(source, R.id.post_order_tv_address_group, "field 'tv_address_group'", Group.class);
    target.lv_goods = finder.findRequiredViewAsType(source, R.id.post_order_view_lv, "field 'lv_goods'", ScrollViewListView.class);
    target.tv_price_goods = finder.findRequiredViewAsType(source, R.id.post_order_tv_goods_price_show, "field 'tv_price_goods'", TextView.class);
    target.tv_price_freight = finder.findRequiredViewAsType(source, R.id.post_order_tv_freight_price_show, "field 'tv_price_freight'", TextView.class);
    target.tv_price_discount = finder.findRequiredViewAsType(source, R.id.post_order_tv_discount_price_show, "field 'tv_price_discount'", TextView.class);
    target.tv_price_total = finder.findRequiredViewAsType(source, R.id.post_order_tv_price, "field 'tv_price_total'", TextView.class);
    target.tv_confirm = finder.findRequiredViewAsType(source, R.id.post_order_tv_confirm, "field 'tv_confirm'", TextView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.address_main = null;
    target.tv_address_add = null;
    target.tv_address_district = null;
    target.tv_address_detail = null;
    target.tv_address_name = null;
    target.tv_address_status = null;
    target.tv_address_group = null;
    target.lv_goods = null;
    target.tv_price_goods = null;
    target.tv_price_freight = null;
    target.tv_price_discount = null;
    target.tv_price_total = null;
    target.tv_confirm = null;

    this.target = null;
  }
}
