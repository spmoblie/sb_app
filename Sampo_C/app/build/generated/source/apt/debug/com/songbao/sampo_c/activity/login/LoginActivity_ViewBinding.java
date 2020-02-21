// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_c.activity.login;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_c.R;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class LoginActivity_ViewBinding<T extends LoginActivity> implements Unbinder {
  protected T target;

  public LoginActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.login_iv_close = finder.findRequiredViewAsType(source, R.id.login_iv_close, "field 'login_iv_close'", ImageView.class);
    target.login_btn_login = finder.findRequiredViewAsType(source, R.id.login_btn_login, "field 'login_btn_login'", Button.class);
    target.login_tv_register = finder.findRequiredViewAsType(source, R.id.login_tv_register, "field 'login_tv_register'", TextView.class);
    target.login_tv_wx = finder.findRequiredViewAsType(source, R.id.login_tv_wx, "field 'login_tv_wx'", TextView.class);
    target.login_tv_qq = finder.findRequiredViewAsType(source, R.id.login_tv_qq, "field 'login_tv_qq'", TextView.class);
    target.login_tv_wb = finder.findRequiredViewAsType(source, R.id.login_tv_wb, "field 'login_tv_wb'", TextView.class);
    target.tv_agreement = finder.findRequiredViewAsType(source, R.id.login_tv_agreement, "field 'tv_agreement'", TextView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.login_iv_close = null;
    target.login_btn_login = null;
    target.login_tv_register = null;
    target.login_tv_wx = null;
    target.login_tv_qq = null;
    target.login_tv_wb = null;
    target.tv_agreement = null;

    this.target = null;
  }
}
