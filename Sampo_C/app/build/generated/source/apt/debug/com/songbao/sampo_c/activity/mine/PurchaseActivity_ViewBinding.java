// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_c.activity.mine;

import android.support.constraint.ConstraintLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.widgets.ScrollViewListView;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class PurchaseActivity_ViewBinding<T extends PurchaseActivity> implements Unbinder {
  protected T target;

  public PurchaseActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.address_main = finder.findRequiredViewAsType(source, R.id.purchase_address_main, "field 'address_main'", ConstraintLayout.class);
    target.tv_address_district = finder.findRequiredViewAsType(source, R.id.purchase_tv_address_district, "field 'tv_address_district'", TextView.class);
    target.tv_address_detail = finder.findRequiredViewAsType(source, R.id.purchase_tv_address_detail, "field 'tv_address_detail'", TextView.class);
    target.tv_address_name = finder.findRequiredViewAsType(source, R.id.purchase_tv_address_name, "field 'tv_address_name'", TextView.class);
    target.iv_address_go = finder.findRequiredViewAsType(source, R.id.purchase_tv_address_go, "field 'iv_address_go'", ImageView.class);
    target.tv_time_order = finder.findRequiredViewAsType(source, R.id.purchase_tv_time_order, "field 'tv_time_order'", TextView.class);
    target.tv_status = finder.findRequiredViewAsType(source, R.id.purchase_tv_status, "field 'tv_status'", TextView.class);
    target.lv_goods = finder.findRequiredViewAsType(source, R.id.purchase_lv_goods, "field 'lv_goods'", ScrollViewListView.class);
    target.tv_price_goods = finder.findRequiredViewAsType(source, R.id.purchase_tv_goods_price_show, "field 'tv_price_goods'", TextView.class);
    target.tv_price_freight = finder.findRequiredViewAsType(source, R.id.purchase_tv_freight_price_show, "field 'tv_price_freight'", TextView.class);
    target.tv_price_discount = finder.findRequiredViewAsType(source, R.id.purchase_tv_discount_price_show, "field 'tv_price_discount'", TextView.class);
    target.tv_price_total = finder.findRequiredViewAsType(source, R.id.purchase_tv_total_price_show, "field 'tv_price_total'", TextView.class);
    target.tv_order_number = finder.findRequiredViewAsType(source, R.id.purchase_tv_order_number, "field 'tv_order_number'", TextView.class);
    target.tv_number_copy = finder.findRequiredViewAsType(source, R.id.purchase_tv_order_number_copy, "field 'tv_number_copy'", TextView.class);
    target.tv_pay_type = finder.findRequiredViewAsType(source, R.id.purchase_tv_order_pay_type, "field 'tv_pay_type'", TextView.class);
    target.tv_pay_number = finder.findRequiredViewAsType(source, R.id.purchase_tv_order_pay_number, "field 'tv_pay_number'", TextView.class);
    target.tv_time_add = finder.findRequiredViewAsType(source, R.id.purchase_tv_order_time_add, "field 'tv_time_add'", TextView.class);
    target.tv_time_pay = finder.findRequiredViewAsType(source, R.id.purchase_tv_order_time_pay, "field 'tv_time_pay'", TextView.class);
    target.tv_time_send = finder.findRequiredViewAsType(source, R.id.purchase_tv_order_time_send, "field 'tv_time_send'", TextView.class);
    target.tv_time_done = finder.findRequiredViewAsType(source, R.id.purchase_tv_order_time_done, "field 'tv_time_done'", TextView.class);
    target.tv_click_01 = finder.findRequiredViewAsType(source, R.id.purchase_tv_click_01, "field 'tv_click_01'", TextView.class);
    target.tv_click_02 = finder.findRequiredViewAsType(source, R.id.purchase_tv_click_02, "field 'tv_click_02'", TextView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.address_main = null;
    target.tv_address_district = null;
    target.tv_address_detail = null;
    target.tv_address_name = null;
    target.iv_address_go = null;
    target.tv_time_order = null;
    target.tv_status = null;
    target.lv_goods = null;
    target.tv_price_goods = null;
    target.tv_price_freight = null;
    target.tv_price_discount = null;
    target.tv_price_total = null;
    target.tv_order_number = null;
    target.tv_number_copy = null;
    target.tv_pay_type = null;
    target.tv_pay_number = null;
    target.tv_time_add = null;
    target.tv_time_pay = null;
    target.tv_time_send = null;
    target.tv_time_done = null;
    target.tv_click_01 = null;
    target.tv_click_02 = null;

    this.target = null;
  }
}
