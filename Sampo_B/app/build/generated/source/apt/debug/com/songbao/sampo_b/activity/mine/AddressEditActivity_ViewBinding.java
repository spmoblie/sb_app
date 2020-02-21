// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_b.activity.mine;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.district.selector.wheel.widget.WheelView;
import com.songbao.sampo_b.R;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class AddressEditActivity_ViewBinding<T extends AddressEditActivity> implements Unbinder {
  protected T target;

  public AddressEditActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.et_name = finder.findRequiredViewAsType(source, R.id.address_edit_et_name, "field 'et_name'", EditText.class);
    target.et_phone = finder.findRequiredViewAsType(source, R.id.address_edit_et_phone, "field 'et_phone'", EditText.class);
    target.et_detail = finder.findRequiredViewAsType(source, R.id.address_edit_et_detail, "field 'et_detail'", EditText.class);
    target.tv_area = finder.findRequiredViewAsType(source, R.id.address_edit_tv_area_show, "field 'tv_area'", TextView.class);
    target.tv_default = finder.findRequiredViewAsType(source, R.id.address_edit_tv_default, "field 'tv_default'", TextView.class);
    target.iv_default = finder.findRequiredViewAsType(source, R.id.address_edit_iv_default, "field 'iv_default'", ImageView.class);
    target.tv_save = finder.findRequiredViewAsType(source, R.id.address_edit_tv_save, "field 'tv_save'", TextView.class);
    target.wheel_main = finder.findRequiredViewAsType(source, R.id.address_edit_wheel_main, "field 'wheel_main'", LinearLayout.class);
    target.wheel_finish = finder.findRequiredView(source, R.id.address_edit_wheel_finish, "field 'wheel_finish'");
    target.wheel_dismiss = finder.findRequiredViewAsType(source, R.id.address_edit_wheel_dismiss, "field 'wheel_dismiss'", RelativeLayout.class);
    target.wheel_province = finder.findRequiredViewAsType(source, R.id.wheel_province, "field 'wheel_province'", WheelView.class);
    target.wheel_city = finder.findRequiredViewAsType(source, R.id.wheel_city, "field 'wheel_city'", WheelView.class);
    target.wheel_district = finder.findRequiredViewAsType(source, R.id.wheel_district, "field 'wheel_district'", WheelView.class);
    target.tv_confirm = finder.findRequiredViewAsType(source, R.id.address_edit_wheel_tv_confirm, "field 'tv_confirm'", TextView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.et_name = null;
    target.et_phone = null;
    target.et_detail = null;
    target.tv_area = null;
    target.tv_default = null;
    target.iv_default = null;
    target.tv_save = null;
    target.wheel_main = null;
    target.wheel_finish = null;
    target.wheel_dismiss = null;
    target.wheel_province = null;
    target.wheel_city = null;
    target.wheel_district = null;
    target.tv_confirm = null;

    this.target = null;
  }
}
