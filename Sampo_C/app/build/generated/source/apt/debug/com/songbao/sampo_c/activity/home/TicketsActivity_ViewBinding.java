// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_c.activity.home;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_c.R;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class TicketsActivity_ViewBinding<T extends TicketsActivity> implements Unbinder {
  protected T target;

  public TicketsActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.iv_discount_1 = finder.findRequiredViewAsType(source, R.id.tickets_set_meal_1_iv_discount, "field 'iv_discount_1'", ImageView.class);
    target.iv_discount_2 = finder.findRequiredViewAsType(source, R.id.tickets_set_meal_2_iv_discount, "field 'iv_discount_2'", ImageView.class);
    target.iv_discount_3 = finder.findRequiredViewAsType(source, R.id.tickets_set_meal_3_iv_discount, "field 'iv_discount_3'", ImageView.class);
    target.set_meal_main_1 = finder.findRequiredViewAsType(source, R.id.tickets_set_meal_1_main, "field 'set_meal_main_1'", LinearLayout.class);
    target.set_meal_main_2 = finder.findRequiredViewAsType(source, R.id.tickets_set_meal_2_main, "field 'set_meal_main_2'", LinearLayout.class);
    target.set_meal_main_3 = finder.findRequiredViewAsType(source, R.id.tickets_set_meal_3_main, "field 'set_meal_main_3'", LinearLayout.class);
    target.tv_title_1 = finder.findRequiredViewAsType(source, R.id.tickets_set_meal_1_tv_name, "field 'tv_title_1'", TextView.class);
    target.tv_title_2 = finder.findRequiredViewAsType(source, R.id.tickets_set_meal_2_tv_name, "field 'tv_title_2'", TextView.class);
    target.tv_title_3 = finder.findRequiredViewAsType(source, R.id.tickets_set_meal_3_tv_name, "field 'tv_title_3'", TextView.class);
    target.tv_price_new_1 = finder.findRequiredViewAsType(source, R.id.tickets_set_meal_1_tv_price_new, "field 'tv_price_new_1'", TextView.class);
    target.tv_price_new_2 = finder.findRequiredViewAsType(source, R.id.tickets_set_meal_2_tv_price_new, "field 'tv_price_new_2'", TextView.class);
    target.tv_price_new_3 = finder.findRequiredViewAsType(source, R.id.tickets_set_meal_3_tv_price_new, "field 'tv_price_new_3'", TextView.class);
    target.tv_price_old_1 = finder.findRequiredViewAsType(source, R.id.tickets_set_meal_1_tv_price_old, "field 'tv_price_old_1'", TextView.class);
    target.tv_price_old_2 = finder.findRequiredViewAsType(source, R.id.tickets_set_meal_2_tv_price_old, "field 'tv_price_old_2'", TextView.class);
    target.tv_price_old_3 = finder.findRequiredViewAsType(source, R.id.tickets_set_meal_3_tv_price_old, "field 'tv_price_old_3'", TextView.class);
    target.tv_discount_1 = finder.findRequiredViewAsType(source, R.id.tickets_set_meal_1_tv_discount, "field 'tv_discount_1'", TextView.class);
    target.tv_discount_2 = finder.findRequiredViewAsType(source, R.id.tickets_set_meal_2_tv_discount, "field 'tv_discount_2'", TextView.class);
    target.tv_discount_3 = finder.findRequiredViewAsType(source, R.id.tickets_set_meal_3_tv_discount, "field 'tv_discount_3'", TextView.class);
    target.bt_buy_now = finder.findRequiredViewAsType(source, R.id.tickets_bt_buy_now, "field 'bt_buy_now'", Button.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.iv_discount_1 = null;
    target.iv_discount_2 = null;
    target.iv_discount_3 = null;
    target.set_meal_main_1 = null;
    target.set_meal_main_2 = null;
    target.set_meal_main_3 = null;
    target.tv_title_1 = null;
    target.tv_title_2 = null;
    target.tv_title_3 = null;
    target.tv_price_new_1 = null;
    target.tv_price_new_2 = null;
    target.tv_price_new_3 = null;
    target.tv_price_old_1 = null;
    target.tv_price_old_2 = null;
    target.tv_price_old_3 = null;
    target.tv_discount_1 = null;
    target.tv_discount_2 = null;
    target.tv_discount_3 = null;
    target.bt_buy_now = null;

    this.target = null;
  }
}
