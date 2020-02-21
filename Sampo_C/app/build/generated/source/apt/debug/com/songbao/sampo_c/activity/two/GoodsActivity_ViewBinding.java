// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_c.activity.two;

import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.widgets.ObservableScrollView;
import com.songbao.sampo_c.widgets.ScrollViewListView;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class GoodsActivity_ViewBinding<T extends GoodsActivity> implements Unbinder {
  protected T target;

  public GoodsActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.ll_top_main = finder.findRequiredViewAsType(source, R.id.top_common_ll_main, "field 'll_top_main'", ConstraintLayout.class);
    target.ib_left = finder.findRequiredViewAsType(source, R.id.top_common_left, "field 'ib_left'", ImageButton.class);
    target.ib_right = finder.findRequiredViewAsType(source, R.id.top_common_right, "field 'ib_right'", ImageButton.class);
    target.rb_1 = finder.findRequiredViewAsType(source, R.id.top_common_rb_1, "field 'rb_1'", RadioButton.class);
    target.rb_2 = finder.findRequiredViewAsType(source, R.id.top_common_rb_2, "field 'rb_2'", RadioButton.class);
    target.rb_3 = finder.findRequiredViewAsType(source, R.id.top_common_rb_3, "field 'rb_3'", RadioButton.class);
    target.iv_left = finder.findRequiredViewAsType(source, R.id.goods_iv_left, "field 'iv_left'", ImageView.class);
    target.iv_share = finder.findRequiredViewAsType(source, R.id.goods_iv_share, "field 'iv_share'", ImageView.class);
    target.goods_sv = finder.findRequiredViewAsType(source, R.id.goods_view_sv, "field 'goods_sv'", ObservableScrollView.class);
    target.goods_vp = finder.findRequiredViewAsType(source, R.id.goods_view_vp, "field 'goods_vp'", ViewPager.class);
    target.vp_indicator = finder.findRequiredViewAsType(source, R.id.goods_vp_indicator, "field 'vp_indicator'", LinearLayout.class);
    target.tv_name = finder.findRequiredViewAsType(source, R.id.goods_tv_goods_name, "field 'tv_name'", TextView.class);
    target.tv_price = finder.findRequiredViewAsType(source, R.id.goods_tv_price, "field 'tv_price'", TextView.class);
    target.spec_main = finder.findRequiredViewAsType(source, R.id.goods_spec_choice_main, "field 'spec_main'", ConstraintLayout.class);
    target.tv_spec = finder.findRequiredViewAsType(source, R.id.goods_tv_selected_show, "field 'tv_spec'", TextView.class);
    target.comment_main = finder.findRequiredViewAsType(source, R.id.goods_good_comment_main, "field 'comment_main'", ConstraintLayout.class);
    target.tv_comment_num = finder.findRequiredViewAsType(source, R.id.goods_tv_good_comment_num, "field 'tv_comment_num'", TextView.class);
    target.tv_percentage = finder.findRequiredViewAsType(source, R.id.goods_tv_good_comment_percentage, "field 'tv_percentage'", TextView.class);
    target.lv_comment = finder.findRequiredViewAsType(source, R.id.goods_lv_comment, "field 'lv_comment'", ScrollViewListView.class);
    target.lv_detail = finder.findRequiredViewAsType(source, R.id.goods_lv_detail, "field 'lv_detail'", ScrollViewListView.class);
    target.title_detail = finder.findRequiredViewAsType(source, R.id.goods_tv_good_detail, "field 'title_detail'", TextView.class);
    target.tv_home = finder.findRequiredViewAsType(source, R.id.bottom_add_cart_tv_home, "field 'tv_home'", TextView.class);
    target.tv_cart = finder.findRequiredViewAsType(source, R.id.bottom_add_cart_tv_cart, "field 'tv_cart'", TextView.class);
    target.tv_cart_num = finder.findRequiredViewAsType(source, R.id.bottom_add_cart_tv_cart_num, "field 'tv_cart_num'", TextView.class);
    target.tv_cart_add = finder.findRequiredViewAsType(source, R.id.bottom_add_cart_tv_cart_add, "field 'tv_cart_add'", TextView.class);
    target.tv_customize = finder.findRequiredViewAsType(source, R.id.bottom_add_cart_tv_customize, "field 'tv_customize'", TextView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.ll_top_main = null;
    target.ib_left = null;
    target.ib_right = null;
    target.rb_1 = null;
    target.rb_2 = null;
    target.rb_3 = null;
    target.iv_left = null;
    target.iv_share = null;
    target.goods_sv = null;
    target.goods_vp = null;
    target.vp_indicator = null;
    target.tv_name = null;
    target.tv_price = null;
    target.spec_main = null;
    target.tv_spec = null;
    target.comment_main = null;
    target.tv_comment_num = null;
    target.tv_percentage = null;
    target.lv_comment = null;
    target.lv_detail = null;
    target.title_detail = null;
    target.tv_home = null;
    target.tv_cart = null;
    target.tv_cart_num = null;
    target.tv_cart_add = null;
    target.tv_customize = null;

    this.target = null;
  }
}
