// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_c.activity.mine;

import android.widget.Button;
import android.widget.EditText;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_c.R;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class FeedBackActivity_ViewBinding<T extends FeedBackActivity> implements Unbinder {
  protected T target;

  public FeedBackActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.et_content = finder.findRequiredViewAsType(source, R.id.feed_back_et_content, "field 'et_content'", EditText.class);
    target.btn_submit = finder.findRequiredViewAsType(source, R.id.feed_back_btn_submit, "field 'btn_submit'", Button.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.et_content = null;
    target.btn_submit = null;

    this.target = null;
  }
}
