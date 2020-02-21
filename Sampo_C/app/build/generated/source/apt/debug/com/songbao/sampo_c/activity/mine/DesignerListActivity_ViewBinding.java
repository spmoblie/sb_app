// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_c.activity.mine;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_c.R;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class DesignerListActivity_ViewBinding<T extends DesignerListActivity> implements Unbinder {
  protected T target;

  public DesignerListActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.refresh_rv = finder.findRequiredViewAsType(source, R.id.refresh_view_rv, "field 'refresh_rv'", RecyclerView.class);
    target.tv_click = finder.findRequiredViewAsType(source, R.id.designer_tv_click, "field 'tv_click'", TextView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.refresh_rv = null;
    target.tv_click = null;

    this.target = null;
  }
}
