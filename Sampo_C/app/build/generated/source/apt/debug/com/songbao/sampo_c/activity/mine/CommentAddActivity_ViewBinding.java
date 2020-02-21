// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_c.activity.mine;

import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.widgets.RoundImageView;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class CommentAddActivity_ViewBinding<T extends CommentAddActivity> implements Unbinder {
  protected T target;

  public CommentAddActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.iv_goods = finder.findRequiredViewAsType(source, R.id.comment_add_iv_goods, "field 'iv_goods'", RoundImageView.class);
    target.tv_name = finder.findRequiredViewAsType(source, R.id.comment_add_tv_name, "field 'tv_name'", TextView.class);
    target.tv_attr = finder.findRequiredViewAsType(source, R.id.comment_add_tv_attr, "field 'tv_attr'", TextView.class);
    target.rb_star = finder.findRequiredViewAsType(source, R.id.comment_add_rb_star, "field 'rb_star'", RatingBar.class);
    target.tv_time = finder.findRequiredViewAsType(source, R.id.comment_add_tv_time, "field 'tv_time'", TextView.class);
    target.tv_content = finder.findRequiredViewAsType(source, R.id.comment_add_tv_content, "field 'tv_content'", TextView.class);
    target.sv_main = finder.findRequiredViewAsType(source, R.id.comment_add_view_hsv, "field 'sv_main'", HorizontalScrollView.class);
    target.ll_main = finder.findRequiredViewAsType(source, R.id.comment_add_hsv_ll_main, "field 'll_main'", LinearLayout.class);
    target.et_add_comment = finder.findRequiredViewAsType(source, R.id.comment_add_et_add_comment, "field 'et_add_comment'", EditText.class);
    target.tv_post = finder.findRequiredViewAsType(source, R.id.comment_add_tv_post, "field 'tv_post'", TextView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.iv_goods = null;
    target.tv_name = null;
    target.tv_attr = null;
    target.rb_star = null;
    target.tv_time = null;
    target.tv_content = null;
    target.sv_main = null;
    target.ll_main = null;
    target.et_add_comment = null;
    target.tv_post = null;

    this.target = null;
  }
}
