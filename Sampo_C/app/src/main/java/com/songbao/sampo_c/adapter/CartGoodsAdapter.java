package com.songbao.sampo_c.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.entity.CartEntity;
import com.songbao.sampo_c.entity.GoodsAttrEntity;
import com.songbao.sampo_c.entity.GoodsEntity;
import com.songbao.sampo_c.utils.CommonTools;
import com.songbao.sampo_c.widgets.MyHorizontalScrollView;
import com.songbao.sampo_c.widgets.RoundImageView;

import java.text.DecimalFormat;
import java.util.List;

public class CartGoodsAdapter extends BaseRecyclerAdapter {

    private int scrollPos = -1;
    private DecimalFormat df;
    private LinearLayout.LayoutParams lp;

    public CartGoodsAdapter(Context context, int resLayout) {
        super(context, resLayout);

        df = new DecimalFormat("0.00");

        lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.width = AppApplication.screen_width - CommonTools.dpToPx(context, 28);
    }

    @Override
    public void updateData(List data) {
        this.scrollPos = -1;
        super.updateData(data);
    }

    public void reset(int scrollPos){
        this.scrollPos = scrollPos;
        notifyDataSetChanged();
    }

    @Override
    public void bindData(BaseRecyclerHolder holder, final int pos) {
        // 获取View
        View item_top_line = holder.getView(R.id.cart_item_top_line);
        MyHorizontalScrollView item_sv_main = holder.getView(R.id.cart_item_hsv_main);
        ConstraintLayout item_left_main = holder.getView(R.id.cart_item_left_main);
        ImageView iv_select = holder.getView(R.id.cart_item_iv_select);
        RoundImageView iv_show = holder.getView(R.id.cart_item_iv_show);
        TextView tv_name = holder.getView(R.id.cart_item_tv_name);
        TextView tv_attr = holder.getView(R.id.cart_item_tv_attr);
        TextView tv_price = holder.getView(R.id.cart_item_tv_price);
        ImageView iv_minus = holder.getView(R.id.cart_item_iv_num_minus);
        ImageView iv_add = holder.getView(R.id.cart_item_iv_num_add);
        TextView tv_number = holder.getView(R.id.cart_item_tv_number);
        TextView tv_customize = holder.getView(R.id.cart_item_tv_customize);
        TextView tv_delete = holder.getView(R.id.cart_item_tv_delete);

        // 绑定View
        final CartEntity data = (CartEntity) mDataList.get(pos);
        GoodsEntity goodsEn = data.getGoodsEn();

        if (pos == 0) {
            item_top_line.setVisibility(View.VISIBLE);
        } else {
            item_top_line.setVisibility(View.GONE);
        }

        item_left_main.setLayoutParams(lp); //适配屏幕宽度
        if (scrollPos != pos) { //对非当前滚动项进行复位
            item_sv_main.smoothScrollTo(0, item_sv_main.getScrollY());
        }
        item_sv_main.setOnScrollStateChangedListener(new MyHorizontalScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(MyHorizontalScrollView.ScrollType scrollType) {
                switch (scrollType) {
                    case TOUCH_SCROLL: //手指拖动滚动
                        break;
                    case FLING: //滚动
                        break;
                    case IDLE: //滚动停止
                        if (scrollPos != pos) { //非同一滚动项
                            apCallback.setOnClick(data, pos, 7); //滚动
                        }
                        break;
                }
            }
        });

        if (goodsEn != null) {
            Glide.with(AppApplication.getAppContext())
                    .load(goodsEn.getPicUrl())
                    .apply(AppApplication.getShowOptions())
                    .into(iv_show);
            iv_show.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (apCallback != null) {
                        apCallback.setOnClick(data, pos, 1);
                    }
                }
            });

            tv_price.setText(df.format(goodsEn.getPrice()));
            tv_name.setText(goodsEn.getName());
            tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (apCallback != null) {
                        apCallback.setOnClick(data, pos, 1);
                    }
                }
            });

            GoodsAttrEntity attrEn = goodsEn.getAttrEn();
            if (attrEn != null) {
                tv_number.setText(String.valueOf(attrEn.getBuyNum()));
                tv_attr.setText(attrEn.getAttrNameStr());
                tv_attr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (apCallback != null) {
                            apCallback.setOnClick(data, pos, 2);
                        }
                    }
                });
            }

            iv_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (apCallback != null) {
                        apCallback.setOnClick(data, pos, 3);
                    }
                }
            });
            iv_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (apCallback != null) {
                        apCallback.setOnClick(data, pos, 4);
                    }
                }
            });
        }

        iv_select.setSelected(data.isSelect());
        iv_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (apCallback != null) {
                    apCallback.setOnClick(data, pos, 0);
                }
            }
        });
        tv_customize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (apCallback != null) {
                    apCallback.setOnClick(data, pos, 5);
                }
            }
        });
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (apCallback != null) {
                    apCallback.setOnClick(data, pos, 6);
                }
            }
        });
    }

}
