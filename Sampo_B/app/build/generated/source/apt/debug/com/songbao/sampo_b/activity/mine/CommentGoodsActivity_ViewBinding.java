// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_b.activity.mine;

import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.widgets.pullrefresh.PullToRefreshRecyclerView;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class CommentGoodsActivity_ViewBinding<T extends CommentGoodsActivity> implements Unbinder {
  protected T target;

  public CommentGoodsActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.tv_all = finder.findRequiredViewAsType(source, R.id.comment_goods_tv_all, "field 'tv_all'", TextView.class);
    target.tv_new = finder.findRequiredViewAsType(source, R.id.comment_goods_tv_new, "field 'tv_new'", TextView.class);
    target.tv_img = finder.findRequiredViewAsType(source, R.id.comment_goods_tv_img, "field 'tv_img'", TextView.class);
    target.refresh_rv = finder.findRequiredViewAsType(source, R.id.refresh_view_rv, "field 'refresh_rv'", PullToRefreshRecyclerView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.tv_all = null;
    target.tv_new = null;
    target.tv_img = null;
    target.refresh_rv = null;

    this.target = null;
  }
}
