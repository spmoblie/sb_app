// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_c.activity.mine;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_c.R;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class EditUserInfoActivity_ViewBinding<T extends EditUserInfoActivity> implements Unbinder {
  protected T target;

  public EditUserInfoActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.et_content = finder.findRequiredViewAsType(source, R.id.edit_info_et_content, "field 'et_content'", EditText.class);
    target.iv_clear = finder.findRequiredViewAsType(source, R.id.edit_info_iv_clear, "field 'iv_clear'", ImageView.class);
    target.tv_reminder = finder.findRequiredViewAsType(source, R.id.edit_info_tv_reminder, "field 'tv_reminder'", TextView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.et_content = null;
    target.iv_clear = null;
    target.tv_reminder = null;

    this.target = null;
  }
}
