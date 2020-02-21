// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_c.activity.login;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_c.R;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class LoginPhoneActivity_ViewBinding<T extends LoginPhoneActivity> implements Unbinder {
  protected T target;

  public LoginPhoneActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.et_phone = finder.findRequiredViewAsType(source, R.id.login_account_et_phone, "field 'et_phone'", EditText.class);
    target.et_code = finder.findRequiredViewAsType(source, R.id.login_account_et_code, "field 'et_code'", EditText.class);
    target.et_password = finder.findRequiredViewAsType(source, R.id.login_account_et_password, "field 'et_password'", EditText.class);
    target.iv_phone_clear = finder.findRequiredViewAsType(source, R.id.login_iv_phone_clear, "field 'iv_phone_clear'", ImageView.class);
    target.iv_password_check = finder.findRequiredViewAsType(source, R.id.login_account_iv_password_check, "field 'iv_password_check'", ImageView.class);
    target.tv_phone_error = finder.findRequiredViewAsType(source, R.id.login_account_tv_phone_error, "field 'tv_phone_error'", TextView.class);
    target.tv_verify_code = finder.findRequiredViewAsType(source, R.id.login_account_tv_verify_code, "field 'tv_verify_code'", TextView.class);
    target.tv_password_change = finder.findRequiredViewAsType(source, R.id.login_account_tv_password_change, "field 'tv_password_change'", TextView.class);
    target.tv_password_reset = finder.findRequiredViewAsType(source, R.id.login_account_tv_password_reset, "field 'tv_password_reset'", TextView.class);
    target.tv_register = finder.findRequiredViewAsType(source, R.id.login_account_tv_register, "field 'tv_register'", TextView.class);
    target.btn_login = finder.findRequiredViewAsType(source, R.id.login_account_btn_login, "field 'btn_login'", Button.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.et_phone = null;
    target.et_code = null;
    target.et_password = null;
    target.iv_phone_clear = null;
    target.iv_password_check = null;
    target.tv_phone_error = null;
    target.tv_verify_code = null;
    target.tv_password_change = null;
    target.tv_password_reset = null;
    target.tv_register = null;
    target.btn_login = null;

    this.target = null;
  }
}
