// Generated code from Butter Knife. Do not modify!
package com.songbao.sampo_b.activity.mine;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.widgets.RoundImageView;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class CommentPostActivity_ViewBinding<T extends CommentPostActivity> implements Unbinder {
  protected T target;

  public CommentPostActivity_ViewBinding(T target, Finder finder, Object source) {
    this.target = target;

    target.iv_goods = finder.findRequiredViewAsType(source, R.id.comment_post_iv_goods, "field 'iv_goods'", RoundImageView.class);
    target.tv_name = finder.findRequiredViewAsType(source, R.id.comment_post_tv_name, "field 'tv_name'", TextView.class);
    target.tv_attr = finder.findRequiredViewAsType(source, R.id.comment_post_tv_attr, "field 'tv_attr'", TextView.class);
    target.rb_star = finder.findRequiredViewAsType(source, R.id.comment_post_rb_star, "field 'rb_star'", RatingBar.class);
    target.et_comment = finder.findRequiredViewAsType(source, R.id.comment_post_et_comment, "field 'et_comment'", EditText.class);
    target.iv_photo_01 = finder.findRequiredViewAsType(source, R.id.comment_post_iv_photo_01, "field 'iv_photo_01'", RoundImageView.class);
    target.iv_photo_02 = finder.findRequiredViewAsType(source, R.id.comment_post_iv_photo_02, "field 'iv_photo_02'", RoundImageView.class);
    target.iv_photo_03 = finder.findRequiredViewAsType(source, R.id.comment_post_iv_photo_03, "field 'iv_photo_03'", RoundImageView.class);
    target.iv_photo_01_delete = finder.findRequiredViewAsType(source, R.id.comment_post_iv_photo_01_delete, "field 'iv_photo_01_delete'", ImageView.class);
    target.iv_photo_02_delete = finder.findRequiredViewAsType(source, R.id.comment_post_iv_photo_02_delete, "field 'iv_photo_02_delete'", ImageView.class);
    target.iv_photo_03_delete = finder.findRequiredViewAsType(source, R.id.comment_post_iv_photo_03_delete, "field 'iv_photo_03_delete'", ImageView.class);
    target.tv_add_photo = finder.findRequiredViewAsType(source, R.id.comment_post_tv_add_photo, "field 'tv_add_photo'", TextView.class);
    target.tv_post = finder.findRequiredViewAsType(source, R.id.comment_post_tv_post, "field 'tv_post'", TextView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.iv_goods = null;
    target.tv_name = null;
    target.tv_attr = null;
    target.rb_star = null;
    target.et_comment = null;
    target.iv_photo_01 = null;
    target.iv_photo_02 = null;
    target.iv_photo_03 = null;
    target.iv_photo_01_delete = null;
    target.iv_photo_02_delete = null;
    target.iv_photo_03_delete = null;
    target.tv_add_photo = null;
    target.tv_post = null;

    this.target = null;
  }
}
