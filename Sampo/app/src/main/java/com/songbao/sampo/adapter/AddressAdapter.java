package com.songbao.sampo.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbao.sampo.AppApplication;
import com.songbao.sampo.R;
import com.songbao.sampo.entity.AddressEntity;
import com.songbao.sampo.utils.CommonTools;
import com.songbao.sampo.utils.UserManager;
import com.songbao.sampo.widgets.MyHorizontalScrollView;

import java.util.List;

public class AddressAdapter extends BaseRecyclerAdapter {

    private int scrollPos = -1;
    private LinearLayout.LayoutParams lp;
    private UserManager userManager;

    public AddressAdapter(Context context, int resLayout) {
        super(context, resLayout);

        userManager = UserManager.getInstance();

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
        LinearLayout item_main = holder.getView(R.id.address_item_view_main);
        View item_top_line = holder.getView(R.id.address_item_top_line);
        MyHorizontalScrollView item_sv_main = holder.getView(R.id.address_item_hsv_main);
        ConstraintLayout item_left_main = holder.getView(R.id.address_item_left_main);
        ImageView iv_select = holder.getView(R.id.address_item_iv_select);
        TextView tv_name = holder.getView(R.id.address_item_tv_name);
        TextView tv_status = holder.getView(R.id.address_item_tv_status);
        TextView tv_address = holder.getView(R.id.address_item_tv_address);
        ImageView iv_edit = holder.getView(R.id.address_item_iv_edit);
        TextView tv_default = holder.getView(R.id.address_item_tv_default);
        TextView tv_delete = holder.getView(R.id.address_item_tv_delete);

        // 绑定View
        final AddressEntity data = (AddressEntity) mDataList.get(pos);

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

        if (userManager.getDefaultAddressId() == data.getId()) { //默认
            tv_status.setVisibility(View.VISIBLE);
        } else {
            tv_status.setVisibility(View.GONE);
        }
        tv_name.setText(context.getString(R.string.address_name_phone, data.getName(), data.getPhone()));
        tv_address.setText(data.getDistrict() + data.getAddress());

        iv_select.setSelected(data.isSelect());
        item_left_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (apCallback != null) {
                    apCallback.setOnClick(data, pos, 0);
                }
            }
        });
        iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (apCallback != null) {
                    apCallback.setOnClick(data, pos, 1);
                }
            }
        });
        tv_default.setOnClickListener(new View.OnClickListener() {
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
