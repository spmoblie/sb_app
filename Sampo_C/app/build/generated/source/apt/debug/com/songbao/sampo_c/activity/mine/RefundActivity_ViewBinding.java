// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_c.activity.mine;

import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.widgets.RoundImageView;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class RefundActivity_ViewBinding<T extends RefundActivity> implements Unbinder {
  protected T target;

  public RefundActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.tv_state_describe = finder.findRequiredViewAsType(source, R.id.refund_tv_state_describe, "field 'tv_state_describe'", TextView.class);
    target.tv_time_update = finder.findRequiredViewAsType(source, R.id.refund_tv_time_update, "field 'tv_time_update'", TextView.class);
    target.iv_goods = finder.findRequiredViewAsType(source, R.id.refund_iv_goods, "field 'iv_goods'", RoundImageView.class);
    target.tv_name = finder.findRequiredViewAsType(source, R.id.refund_tv_name, "field 'tv_name'", TextView.class);
    target.tv_attr = finder.findRequiredViewAsType(source, R.id.refund_tv_attr, "field 'tv_attr'", TextView.class);
    target.tv_price = finder.findRequiredViewAsType(source, R.id.refund_tv_price, "field 'tv_price'", TextView.class);
    target.iv_state_02 = finder.findRequiredViewAsType(source, R.id.refund_iv_state_02, "field 'iv_state_02'", ImageView.class);
    target.iv_state_03 = finder.findRequiredViewAsType(source, R.id.refund_iv_state_03, "field 'iv_state_03'", ImageView.class);
    target.tv_state_01_time = finder.findRequiredViewAsType(source, R.id.refund_tv_state_01_time, "field 'tv_state_01_time'", TextView.class);
    target.tv_state_02_time = finder.findRequiredViewAsType(source, R.id.refund_tv_state_02_time, "field 'tv_state_02_time'", TextView.class);
    target.tv_state_03_time = finder.findRequiredViewAsType(source, R.id.refund_tv_state_03_time, "field 'tv_state_03_time'", TextView.class);
    target.tv_refund_number = finder.findRequiredViewAsType(source, R.id.refund_tv_refund_number, "field 'tv_refund_number'", TextView.class);
    target.tv_number_copy = finder.findRequiredViewAsType(source, R.id.refund_tv_number_copy, "field 'tv_number_copy'", TextView.class);
    target.tv_time_apply = finder.findRequiredViewAsType(source, R.id.refund_tv_time_apply, "field 'tv_time_apply'", TextView.class);
    target.tv_cancel = finder.findRequiredViewAsType(source, R.id.refund_tv_cancel, "field 'tv_cancel'", TextView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.tv_state_describe = null;
    target.tv_time_update = null;
    target.iv_goods = null;
    target.tv_name = null;
    target.tv_attr = null;
    target.tv_price = null;
    target.iv_state_02 = null;
    target.iv_state_03 = null;
    target.tv_state_01_time = null;
    target.tv_state_02_time = null;
    target.tv_state_03_time = null;
    target.tv_refund_number = null;
    target.tv_number_copy = null;
    target.tv_time_apply = null;
    target.tv_cancel = null;

    this.target = null;
  }
}
