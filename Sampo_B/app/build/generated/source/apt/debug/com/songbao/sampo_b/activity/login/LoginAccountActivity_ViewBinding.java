// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_b.activity.login;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_b.R;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class LoginAccountActivity_ViewBinding<T extends LoginAccountActivity> implements Unbinder {
  protected T target;

  public LoginAccountActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.et_account = finder.findRequiredViewAsType(source, R.id.login_account_et_account, "field 'et_account'", EditText.class);
    target.et_password = finder.findRequiredViewAsType(source, R.id.login_account_et_password, "field 'et_password'", EditText.class);
    target.iv_account_clear = finder.findRequiredViewAsType(source, R.id.login_iv_account_clear, "field 'iv_account_clear'", ImageView.class);
    target.iv_password_check = finder.findRequiredViewAsType(source, R.id.login_account_iv_password_check, "field 'iv_password_check'", ImageView.class);
    target.btn_login = finder.findRequiredViewAsType(source, R.id.login_account_btn_login, "field 'btn_login'", Button.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.et_account = null;
    target.et_password = null;
    target.iv_account_clear = null;
    target.iv_password_check = null;
    target.btn_login = null;

    this.target = null;
  }
}
