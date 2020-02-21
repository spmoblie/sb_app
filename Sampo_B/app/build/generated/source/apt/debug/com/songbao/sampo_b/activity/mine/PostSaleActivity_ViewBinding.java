// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_b.activity.mine;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.widgets.RoundImageView;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class PostSaleActivity_ViewBinding<T extends PostSaleActivity> implements Unbinder {
  protected T target;

  public PostSaleActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.goods_main = finder.findRequiredViewAsType(source, R.id.post_sale_goods_main, "field 'goods_main'", ConstraintLayout.class);
    target.iv_goods = finder.findRequiredViewAsType(source, R.id.post_sale_iv_goods, "field 'iv_goods'", RoundImageView.class);
    target.tv_name = finder.findRequiredViewAsType(source, R.id.post_sale_tv_name, "field 'tv_name'", TextView.class);
    target.tv_attr = finder.findRequiredViewAsType(source, R.id.post_sale_tv_attr, "field 'tv_attr'", TextView.class);
    target.tv_number = finder.findRequiredViewAsType(source, R.id.post_sale_tv_number, "field 'tv_number'", TextView.class);
    target.tv_price = finder.findRequiredViewAsType(source, R.id.post_sale_tv_price, "field 'tv_price'", TextView.class);
    target.tv_change = finder.findRequiredViewAsType(source, R.id.post_sale_tv_change, "field 'tv_change'", TextView.class);
    target.tv_return = finder.findRequiredViewAsType(source, R.id.post_sale_tv_return, "field 'tv_return'", TextView.class);
    target.et_reason = finder.findRequiredViewAsType(source, R.id.post_sale_et_reason, "field 'et_reason'", EditText.class);
    target.tv_refund_price = finder.findRequiredViewAsType(source, R.id.post_sale_tv_refund_price, "field 'tv_refund_price'", TextView.class);
    target.et_express_no = finder.findRequiredViewAsType(source, R.id.post_sale_et_express_no, "field 'et_express_no'", EditText.class);
    target.group_express_no = finder.findRequiredViewAsType(source, R.id.post_sale_group_express_no, "field 'group_express_no'", Group.class);
    target.iv_photo_01 = finder.findRequiredViewAsType(source, R.id.post_sale_iv_photo_01, "field 'iv_photo_01'", RoundImageView.class);
    target.iv_photo_02 = finder.findRequiredViewAsType(source, R.id.post_sale_iv_photo_02, "field 'iv_photo_02'", RoundImageView.class);
    target.iv_photo_03 = finder.findRequiredViewAsType(source, R.id.post_sale_iv_photo_03, "field 'iv_photo_03'", RoundImageView.class);
    target.iv_photo_01_delete = finder.findRequiredViewAsType(source, R.id.post_sale_iv_photo_01_delete, "field 'iv_photo_01_delete'", ImageView.class);
    target.iv_photo_02_delete = finder.findRequiredViewAsType(source, R.id.post_sale_iv_photo_02_delete, "field 'iv_photo_02_delete'", ImageView.class);
    target.iv_photo_03_delete = finder.findRequiredViewAsType(source, R.id.post_sale_iv_photo_03_delete, "field 'iv_photo_03_delete'", ImageView.class);
    target.tv_add_photo = finder.findRequiredViewAsType(source, R.id.post_sale_tv_add_photo, "field 'tv_add_photo'", TextView.class);
    target.tv_submit = finder.findRequiredViewAsType(source, R.id.post_sale_tv_submit, "field 'tv_submit'", TextView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.goods_main = null;
    target.iv_goods = null;
    target.tv_name = null;
    target.tv_attr = null;
    target.tv_number = null;
    target.tv_price = null;
    target.tv_change = null;
    target.tv_return = null;
    target.et_reason = null;
    target.tv_refund_price = null;
    target.et_express_no = null;
    target.group_express_no = null;
    target.iv_photo_01 = null;
    target.iv_photo_02 = null;
    target.iv_photo_03 = null;
    target.iv_photo_01_delete = null;
    target.iv_photo_02_delete = null;
    target.iv_photo_03_delete = null;
    target.tv_add_photo = null;
    target.tv_submit = null;

    this.target = null;
  }
}
