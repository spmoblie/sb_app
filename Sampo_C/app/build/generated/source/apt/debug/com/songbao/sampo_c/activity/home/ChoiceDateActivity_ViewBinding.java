// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_c.activity.home;

import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.widgets.CustomCalendar;
import com.songbao.sampo_c.widgets.MyScrollView;
import com.songbao.sampo_c.widgets.ScrollViewListView;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class ChoiceDateActivity_ViewBinding<T extends ChoiceDateActivity> implements Unbinder {
  protected T target;

  public ChoiceDateActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.myScrollView = finder.findRequiredViewAsType(source, R.id.choice_date_sv, "field 'myScrollView'", MyScrollView.class);
    target.calendar = finder.findRequiredViewAsType(source, R.id.choice_date_cal, "field 'calendar'", CustomCalendar.class);
    target.listView = finder.findRequiredViewAsType(source, R.id.choice_date_listView, "field 'listView'", ScrollViewListView.class);
    target.tv_confirm = finder.findRequiredViewAsType(source, R.id.choice_date_tv_confirm, "field 'tv_confirm'", TextView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.myScrollView = null;
    target.calendar = null;
    target.listView = null;
    target.tv_confirm = null;

    this.target = null;
  }
}
