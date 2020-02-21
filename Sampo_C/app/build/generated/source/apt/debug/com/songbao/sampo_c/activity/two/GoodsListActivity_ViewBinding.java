// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_c.activity.two;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.widgets.pullrefresh.PullToRefreshRecyclerView;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class GoodsListActivity_ViewBinding<T extends GoodsListActivity> implements Unbinder {
  protected T target;

  public GoodsListActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.iv_left = finder.findRequiredViewAsType(source, R.id.goods_list_iv_left, "field 'iv_left'", ImageView.class);
    target.et_search = finder.findRequiredViewAsType(source, R.id.goods_list_et_search, "field 'et_search'", EditText.class);
    target.iv_clear = finder.findRequiredViewAsType(source, R.id.goods_list_iv_search_clear, "field 'iv_clear'", ImageView.class);
    target.iv_scan = finder.findRequiredViewAsType(source, R.id.goods_list_iv_scan, "field 'iv_scan'", ImageView.class);
    target.tv_top_1 = finder.findRequiredViewAsType(source, R.id.goods_list_tv_top_item_1, "field 'tv_top_1'", TextView.class);
    target.tv_top_2 = finder.findRequiredViewAsType(source, R.id.goods_list_tv_top_item_2, "field 'tv_top_2'", TextView.class);
    target.tv_top_3 = finder.findRequiredViewAsType(source, R.id.goods_list_tv_top_item_3, "field 'tv_top_3'", TextView.class);
    target.tv_top_5 = finder.findRequiredViewAsType(source, R.id.goods_list_tv_top_item_5, "field 'tv_top_5'", TextView.class);
    target.refresh_rv = finder.findRequiredViewAsType(source, R.id.goods_list_rv_goods, "field 'refresh_rv'", PullToRefreshRecyclerView.class);
    target.iv_cart = finder.findRequiredViewAsType(source, R.id.goods_list_iv_cart, "field 'iv_cart'", ImageView.class);
    target.tv_cart_num = finder.findRequiredViewAsType(source, R.id.goods_list_tv_cart_num, "field 'tv_cart_num'", TextView.class);
    target.iv_data_null = finder.findRequiredViewAsType(source, R.id.goods_list_iv_data_null, "field 'iv_data_null'", ImageView.class);
    target.tv_data_null = finder.findRequiredViewAsType(source, R.id.goods_list_tv_data_null, "field 'tv_data_null'", TextView.class);
    target.screen_main = finder.findRequiredViewAsType(source, R.id.goods_list_screen_main, "field 'screen_main'", LinearLayout.class);
    target.screen_hide = finder.findRequiredViewAsType(source, R.id.goods_list_screen_hide, "field 'screen_hide'", RelativeLayout.class);
    target.screen_show = finder.findRequiredViewAsType(source, R.id.goods_list_screen_show, "field 'screen_show'", ConstraintLayout.class);
    target.rv_screen = finder.findRequiredViewAsType(source, R.id.screen_view_rv, "field 'rv_screen'", RecyclerView.class);
    target.tv_reset = finder.findRequiredViewAsType(source, R.id.screen_view_tv_reset, "field 'tv_reset'", TextView.class);
    target.tv_confirm = finder.findRequiredViewAsType(source, R.id.screen_view_tv_confirm, "field 'tv_confirm'", TextView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.iv_left = null;
    target.et_search = null;
    target.iv_clear = null;
    target.iv_scan = null;
    target.tv_top_1 = null;
    target.tv_top_2 = null;
    target.tv_top_3 = null;
    target.tv_top_5 = null;
    target.refresh_rv = null;
    target.iv_cart = null;
    target.tv_cart_num = null;
    target.iv_data_null = null;
    target.tv_data_null = null;
    target.screen_main = null;
    target.screen_hide = null;
    target.screen_show = null;
    target.rv_screen = null;
    target.tv_reset = null;
    target.tv_confirm = null;

    this.target = null;
  }
}
