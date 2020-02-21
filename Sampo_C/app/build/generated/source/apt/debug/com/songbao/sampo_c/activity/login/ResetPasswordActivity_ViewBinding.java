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

public class ResetPasswordActivity_ViewBinding<T extends ResetPasswordActivity> implements Unbinder {
  protected T target;

  public ResetPasswordActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.et_phone = finder.findRequiredViewAsType(source, R.id.reset_password_et_phone, "field 'et_phone'", EditText.class);
    target.et_code = finder.findRequiredViewAsType(source, R.id.reset_password_et_code, "field 'et_code'", EditText.class);
    target.et_password = finder.findRequiredViewAsType(source, R.id.reset_password_et_password, "field 'et_password'", EditText.class);
    target.iv_phone_clear = finder.findRequiredViewAsType(source, R.id.reset_password_iv_phone_clear, "field 'iv_phone_clear'", ImageView.class);
    target.iv_password_check = finder.findRequiredViewAsType(source, R.id.reset_password_iv_password_check, "field 'iv_password_check'", ImageView.class);
    target.tv_phone_error = finder.findRequiredViewAsType(source, R.id.reset_password_tv_phone_error, "field 'tv_phone_error'", TextView.class);
    target.tv_password_error = finder.findRequiredViewAsType(source, R.id.reset_password_tv_password_error, "field 'tv_password_error'", TextView.class);
    target.tv_verify_code = finder.findRequiredViewAsType(source, R.id.reset_password_tv_verify_code, "field 'tv_verify_code'", TextView.class);
    target.btn_reset = finder.findRequiredViewAsType(source, R.id.reset_password_btn_reset, "field 'btn_reset'", Button.class);
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
    target.tv_password_error = null;
    target.tv_verify_code = null;
    target.btn_reset = null;

    this.target = null;
  }
}
