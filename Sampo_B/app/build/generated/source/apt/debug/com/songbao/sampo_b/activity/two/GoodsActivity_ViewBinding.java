// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_b.activity.two;

import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.widgets.ObservableScrollView;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class GoodsActivity_ViewBinding<T extends GoodsActivity> implements Unbinder {
  protected T target;

  public GoodsActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.goods_sv = finder.findRequiredViewAsType(source, R.id.goods_view_sv, "field 'goods_sv'", ObservableScrollView.class);
    target.goods_vp = finder.findRequiredViewAsType(source, R.id.goods_view_vp, "field 'goods_vp'", ViewPager.class);
    target.vp_indicator = finder.findRequiredViewAsType(source, R.id.goods_vp_indicator, "field 'vp_indicator'", LinearLayout.class);
    target.tv_name = finder.findRequiredViewAsType(source, R.id.goods_tv_goods_name, "field 'tv_name'", TextView.class);
    target.iv_code = finder.findRequiredViewAsType(source, R.id.goods_iv_code, "field 'iv_code'", ImageView.class);
    target.tv_code_show = finder.findRequiredViewAsType(source, R.id.goods_tv_code_show, "field 'tv_code_show'", TextView.class);
    target.tv_click = finder.findRequiredViewAsType(source, R.id.goods_tv_click, "field 'tv_click'", TextView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.goods_sv = null;
    target.goods_vp = null;
    target.vp_indicator = null;
    target.tv_name = null;
    target.iv_code = null;
    target.tv_code_show = null;
    target.tv_click = null;

    this.target = null;
  }
}
