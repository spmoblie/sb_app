// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_c.activity.home;

import android.support.constraint.ConstraintLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.widgets.ObservableWebView;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class ReserveDetailActivity_ViewBinding<T extends ReserveDetailActivity> implements Unbinder {
  protected T target;

  public ReserveDetailActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.iv_show = finder.findRequiredViewAsType(source, R.id.reserve_detail_iv_show, "field 'iv_show'", ImageView.class);
    target.tv_title = finder.findRequiredViewAsType(source, R.id.reserve_detail_tv_title, "field 'tv_title'", TextView.class);
    target.tv_name = finder.findRequiredViewAsType(source, R.id.reserve_detail_tv_name, "field 'tv_name'", TextView.class);
    target.tv_series = finder.findRequiredViewAsType(source, R.id.reserve_detail_tv_series, "field 'tv_series'", TextView.class);
    target.tv_slot = finder.findRequiredViewAsType(source, R.id.reserve_detail_tv_slot, "field 'tv_slot'", TextView.class);
    target.tv_place = finder.findRequiredViewAsType(source, R.id.reserve_detail_tv_place, "field 'tv_place'", TextView.class);
    target.tv_suit = finder.findRequiredViewAsType(source, R.id.reserve_detail_tv_suit, "field 'tv_suit'", TextView.class);
    target.date_main = finder.findRequiredViewAsType(source, R.id.reserve_detail_date_main, "field 'date_main'", ConstraintLayout.class);
    target.tv_date = finder.findRequiredViewAsType(source, R.id.reserve_detail_tv_date, "field 'tv_date'", TextView.class);
    target.iv_date_go = finder.findRequiredViewAsType(source, R.id.reserve_detail_iv_date_go, "field 'iv_date_go'", ImageView.class);
    target.time_main = finder.findRequiredViewAsType(source, R.id.reserve_detail_time_main, "field 'time_main'", ConstraintLayout.class);
    target.tv_time = finder.findRequiredViewAsType(source, R.id.reserve_detail_tv_time, "field 'tv_time'", TextView.class);
    target.iv_time_go = finder.findRequiredViewAsType(source, R.id.reserve_detail_iv_time_go, "field 'iv_time_go'", ImageView.class);
    target.code_main = finder.findRequiredViewAsType(source, R.id.reserve_detail_code_main, "field 'code_main'", ConstraintLayout.class);
    target.tv_code = finder.findRequiredViewAsType(source, R.id.reserve_detail_tv_code, "field 'tv_code'", TextView.class);
    target.iv_code = finder.findRequiredViewAsType(source, R.id.reserve_detail_iv_code, "field 'iv_code'", ImageView.class);
    target.iv_code_large = finder.findRequiredViewAsType(source, R.id.reserve_detail_iv_code_large, "field 'iv_code_large'", ImageView.class);
    target.tv_cover = finder.findRequiredViewAsType(source, R.id.reserve_detail_tv_cover, "field 'tv_cover'", TextView.class);
    target.click_main = finder.findRequiredViewAsType(source, R.id.reserve_detail_click_main, "field 'click_main'", ConstraintLayout.class);
    target.tv_price = finder.findRequiredViewAsType(source, R.id.reserve_detail_tv_price, "field 'tv_price'", TextView.class);
    target.tv_click = finder.findRequiredViewAsType(source, R.id.reserve_detail_tv_click, "field 'tv_click'", TextView.class);
    target.tv_success = finder.findRequiredViewAsType(source, R.id.reserve_detail_tv_success, "field 'tv_success'", TextView.class);
    target.myWebView = finder.findRequiredViewAsType(source, R.id.reserve_detail_web_view, "field 'myWebView'", ObservableWebView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.iv_show = null;
    target.tv_title = null;
    target.tv_name = null;
    target.tv_series = null;
    target.tv_slot = null;
    target.tv_place = null;
    target.tv_suit = null;
    target.date_main = null;
    target.tv_date = null;
    target.iv_date_go = null;
    target.time_main = null;
    target.tv_time = null;
    target.iv_time_go = null;
    target.code_main = null;
    target.tv_code = null;
    target.iv_code = null;
    target.iv_code_large = null;
    target.tv_cover = null;
    target.click_main = null;
    target.tv_price = null;
    target.tv_click = null;
    target.tv_success = null;
    target.myWebView = null;

    this.target = null;
  }
}
