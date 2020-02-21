// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_c.activity.home;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_c.R;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class SignUpActivity_ViewBinding<T extends SignUpActivity> implements Unbinder {
  protected T target;

  public SignUpActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.iv_show = finder.findRequiredViewAsType(source, R.id.sign_iv_show, "field 'iv_show'", ImageView.class);
    target.et_name = finder.findRequiredViewAsType(source, R.id.sign_et_name, "field 'et_name'", EditText.class);
    target.tv_gender_man = finder.findRequiredViewAsType(source, R.id.sign_tv_gender_man, "field 'tv_gender_man'", TextView.class);
    target.tv_gender_woman = finder.findRequiredViewAsType(source, R.id.sign_tv_gender_woman, "field 'tv_gender_woman'", TextView.class);
    target.et_age = finder.findRequiredViewAsType(source, R.id.sign_et_age, "field 'et_age'", EditText.class);
    target.et_phone = finder.findRequiredViewAsType(source, R.id.sign_et_phone, "field 'et_phone'", EditText.class);
    target.tv_explain = finder.findRequiredViewAsType(source, R.id.sign_tv_explain, "field 'tv_explain'", TextView.class);
    target.tv_price = finder.findRequiredViewAsType(source, R.id.sign_tv_price, "field 'tv_price'", TextView.class);
    target.tv_click = finder.findRequiredViewAsType(source, R.id.sign_tv_click, "field 'tv_click'", TextView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.iv_show = null;
    target.et_name = null;
    target.tv_gender_man = null;
    target.tv_gender_woman = null;
    target.et_age = null;
    target.et_phone = null;
    target.tv_explain = null;
    target.tv_price = null;
    target.tv_click = null;

    this.target = null;
  }
}
