// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_b.activity.mine;

import android.widget.RadioButton;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.widgets.pullrefresh.PullToRefreshRecyclerView;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class CustomizeListActivity_ViewBinding<T extends CustomizeListActivity> implements Unbinder {
  protected T target;

  public CustomizeListActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.rb_1 = finder.findRequiredViewAsType(source, R.id.top_bar_radio_rb_1, "field 'rb_1'", RadioButton.class);
    target.rb_2 = finder.findRequiredViewAsType(source, R.id.top_bar_radio_rb_2, "field 'rb_2'", RadioButton.class);
    target.rb_3 = finder.findRequiredViewAsType(source, R.id.top_bar_radio_rb_3, "field 'rb_3'", RadioButton.class);
    target.rb_4 = finder.findRequiredViewAsType(source, R.id.top_bar_radio_rb_4, "field 'rb_4'", RadioButton.class);
    target.rb_5 = finder.findRequiredViewAsType(source, R.id.top_bar_radio_rb_5, "field 'rb_5'", RadioButton.class);
    target.refresh_rv = finder.findRequiredViewAsType(source, R.id.refresh_view_rv, "field 'refresh_rv'", PullToRefreshRecyclerView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.rb_1 = null;
    target.rb_2 = null;
    target.rb_3 = null;
    target.rb_4 = null;
    target.rb_5 = null;
    target.refresh_rv = null;

    this.target = null;
  }
}
