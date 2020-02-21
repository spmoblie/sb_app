// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_c.activity.mine;

import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.widgets.ObservableScrollView;
import com.songbao.sampo_c.widgets.RoundImageView;
import com.songbao.sampo_c.widgets.ScrollViewListView;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class CustomizeActivity_ViewBinding<T extends CustomizeActivity> implements Unbinder {
  protected T target;

  public CustomizeActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.sv_main = finder.findRequiredViewAsType(source, R.id.customize_sv_main, "field 'sv_main'", ObservableScrollView.class);
    target.tv_order_number = finder.findRequiredViewAsType(source, R.id.customize_tv_order_number, "field 'tv_order_number'", TextView.class);
    target.tv_order_status = finder.findRequiredViewAsType(source, R.id.customize_tv_order_status, "field 'tv_order_status'", TextView.class);
    target.iv_1_sign = finder.findRequiredViewAsType(source, R.id.customize_iv_module_1_title_sign, "field 'iv_1_sign'", ImageView.class);
    target.tv_1_time = finder.findRequiredViewAsType(source, R.id.customize_tv_module_1_time, "field 'tv_1_time'", TextView.class);
    target.iv_1_goods_img = finder.findRequiredViewAsType(source, R.id.customize_iv_module_1_goods_img, "field 'iv_1_goods_img'", RoundImageView.class);
    target.tv_1_goods_name = finder.findRequiredViewAsType(source, R.id.customize_tv_module_1_goods_name, "field 'tv_1_goods_name'", TextView.class);
    target.tv_1_designer_name = finder.findRequiredViewAsType(source, R.id.customize_tv_module_1_designer_name, "field 'tv_1_designer_name'", TextView.class);
    target.tv_1_designer_phone = finder.findRequiredViewAsType(source, R.id.customize_tv_module_1_designer_phone, "field 'tv_1_designer_phone'", TextView.class);
    target.tv_1_check = finder.findRequiredViewAsType(source, R.id.customize_tv_module_1_check_goods, "field 'tv_1_check'", TextView.class);
    target.iv_2_sign = finder.findRequiredViewAsType(source, R.id.customize_iv_module_2_title_sign, "field 'iv_2_sign'", ImageView.class);
    target.tv_2_time = finder.findRequiredViewAsType(source, R.id.customize_tv_module_2_time, "field 'tv_2_time'", TextView.class);
    target.tv_2_goods_spec = finder.findRequiredViewAsType(source, R.id.customize_tv_module_2_goods_spec, "field 'tv_2_goods_spec'", TextView.class);
    target.tv_2_goods_color = finder.findRequiredViewAsType(source, R.id.customize_tv_module_2_goods_color, "field 'tv_2_goods_color'", TextView.class);
    target.tv_2_goods_material = finder.findRequiredViewAsType(source, R.id.customize_tv_module_2_goods_material, "field 'tv_2_goods_material'", TextView.class);
    target.tv_2_goods_veneer = finder.findRequiredViewAsType(source, R.id.customize_tv_module_2_goods_veneer, "field 'tv_2_goods_veneer'", TextView.class);
    target.iv_3_sign = finder.findRequiredViewAsType(source, R.id.customize_iv_module_3_title_sign, "field 'iv_3_sign'", ImageView.class);
    target.tv_3_time = finder.findRequiredViewAsType(source, R.id.customize_tv_module_3_time, "field 'tv_3_time'", TextView.class);
    target.iv_3_show = finder.findRequiredViewAsType(source, R.id.customize_iv_module_3_show, "field 'iv_3_show'", RoundImageView.class);
    target.tv_3_show = finder.findRequiredViewAsType(source, R.id.customize_tv_module_3_show, "field 'tv_3_show'", TextView.class);
    target.tv_3_confirm = finder.findRequiredViewAsType(source, R.id.customize_tv_module_3_confirm, "field 'tv_3_confirm'", TextView.class);
    target.iv_4_sign = finder.findRequiredViewAsType(source, R.id.customize_iv_module_4_title_sign, "field 'iv_4_sign'", ImageView.class);
    target.tv_4_time = finder.findRequiredViewAsType(source, R.id.customize_tv_module_4_time, "field 'tv_4_time'", TextView.class);
    target.tv_4_order_price = finder.findRequiredViewAsType(source, R.id.customize_tv_module_4_order_price, "field 'tv_4_order_price'", TextView.class);
    target.tv_4_order_day = finder.findRequiredViewAsType(source, R.id.customize_tv_module_4_order_day, "field 'tv_4_order_day'", TextView.class);
    target.tv_4_order_pay = finder.findRequiredViewAsType(source, R.id.customize_tv_module_4_order_pay, "field 'tv_4_order_pay'", TextView.class);
    target.tv_4_confirm = finder.findRequiredViewAsType(source, R.id.customize_tv_module_4_confirm, "field 'tv_4_confirm'", TextView.class);
    target.iv_5_sign = finder.findRequiredViewAsType(source, R.id.customize_iv_module_5_title_sign, "field 'iv_5_sign'", ImageView.class);
    target.tv_5_time = finder.findRequiredViewAsType(source, R.id.customize_tv_module_5_time, "field 'tv_5_time'", TextView.class);
    target.tv_5_addressee_name = finder.findRequiredViewAsType(source, R.id.customize_tv_module_5_addressee_name, "field 'tv_5_addressee_name'", TextView.class);
    target.tv_5_addressee_phone = finder.findRequiredViewAsType(source, R.id.customize_tv_module_5_addressee_phone, "field 'tv_5_addressee_phone'", TextView.class);
    target.tv_5_address_1 = finder.findRequiredViewAsType(source, R.id.customize_tv_module_5_address_1, "field 'tv_5_address_1'", TextView.class);
    target.tv_5_address_2 = finder.findRequiredViewAsType(source, R.id.customize_tv_module_5_address_2, "field 'tv_5_address_2'", TextView.class);
    target.tv_5_select_address = finder.findRequiredViewAsType(source, R.id.customize_tv_module_5_select_address, "field 'tv_5_select_address'", TextView.class);
    target.iv_6_sign = finder.findRequiredViewAsType(source, R.id.customize_iv_module_6_title_sign, "field 'iv_6_sign'", ImageView.class);
    target.tv_6_time = finder.findRequiredViewAsType(source, R.id.customize_tv_module_6_time, "field 'tv_6_time'", TextView.class);
    target.lv_6 = finder.findRequiredViewAsType(source, R.id.customize_module_6_lv, "field 'lv_6'", ScrollViewListView.class);
    target.tv_6_lv_open = finder.findRequiredViewAsType(source, R.id.customize_tv_module_6_lv_open, "field 'tv_6_lv_open'", TextView.class);
    target.iv_7_sign = finder.findRequiredViewAsType(source, R.id.customize_iv_module_7_title_sign, "field 'iv_7_sign'", ImageView.class);
    target.tv_7_time = finder.findRequiredViewAsType(source, R.id.customize_tv_module_7_time, "field 'tv_7_time'", TextView.class);
    target.lv_7 = finder.findRequiredViewAsType(source, R.id.customize_module_7_lv, "field 'lv_7'", ScrollViewListView.class);
    target.tv_7_confirm = finder.findRequiredViewAsType(source, R.id.customize_tv_module_7_confirm, "field 'tv_7_confirm'", TextView.class);
    target.iv_8_sign = finder.findRequiredViewAsType(source, R.id.customize_iv_module_8_title_sign, "field 'iv_8_sign'", ImageView.class);
    target.tv_8_time = finder.findRequiredViewAsType(source, R.id.customize_tv_module_8_time, "field 'tv_8_time'", TextView.class);
    target.tv_8_remind = finder.findRequiredViewAsType(source, R.id.customize_tv_module_8_remind, "field 'tv_8_remind'", TextView.class);
    target.tv_8_confirm = finder.findRequiredViewAsType(source, R.id.customize_tv_module_8_confirm, "field 'tv_8_confirm'", TextView.class);
    target.iv_9_sign = finder.findRequiredViewAsType(source, R.id.customize_iv_module_9_title_sign, "field 'iv_9_sign'", ImageView.class);
    target.tv_9_time = finder.findRequiredViewAsType(source, R.id.customize_tv_module_9_time, "field 'tv_9_time'", TextView.class);
    target.tv_9_remind = finder.findRequiredViewAsType(source, R.id.customize_tv_module_9_remind, "field 'tv_9_remind'", TextView.class);
    target.tv_9_comment = finder.findRequiredViewAsType(source, R.id.customize_tv_module_9_comment, "field 'tv_9_comment'", TextView.class);
    target.tv_9_post_sale = finder.findRequiredViewAsType(source, R.id.customize_tv_module_9_post_sale, "field 'tv_9_post_sale'", TextView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.sv_main = null;
    target.tv_order_number = null;
    target.tv_order_status = null;
    target.iv_1_sign = null;
    target.tv_1_time = null;
    target.iv_1_goods_img = null;
    target.tv_1_goods_name = null;
    target.tv_1_designer_name = null;
    target.tv_1_designer_phone = null;
    target.tv_1_check = null;
    target.iv_2_sign = null;
    target.tv_2_time = null;
    target.tv_2_goods_spec = null;
    target.tv_2_goods_color = null;
    target.tv_2_goods_material = null;
    target.tv_2_goods_veneer = null;
    target.iv_3_sign = null;
    target.tv_3_time = null;
    target.iv_3_show = null;
    target.tv_3_show = null;
    target.tv_3_confirm = null;
    target.iv_4_sign = null;
    target.tv_4_time = null;
    target.tv_4_order_price = null;
    target.tv_4_order_day = null;
    target.tv_4_order_pay = null;
    target.tv_4_confirm = null;
    target.iv_5_sign = null;
    target.tv_5_time = null;
    target.tv_5_addressee_name = null;
    target.tv_5_addressee_phone = null;
    target.tv_5_address_1 = null;
    target.tv_5_address_2 = null;
    target.tv_5_select_address = null;
    target.iv_6_sign = null;
    target.tv_6_time = null;
    target.lv_6 = null;
    target.tv_6_lv_open = null;
    target.iv_7_sign = null;
    target.tv_7_time = null;
    target.lv_7 = null;
    target.tv_7_confirm = null;
    target.iv_8_sign = null;
    target.tv_8_time = null;
    target.tv_8_remind = null;
    target.tv_8_confirm = null;
    target.iv_9_sign = null;
    target.tv_9_time = null;
    target.tv_9_remind = null;
    target.tv_9_comment = null;
    target.tv_9_post_sale = null;

    this.target = null;
  }
}
