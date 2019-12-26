package com.songbao.sampo_b.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.entity.ThemeEntity;
import com.songbao.sampo_b.utils.TimeUtil;
import com.songbao.sampo_b.widgets.RoundImageView;

public class MySignUpAdapter extends BaseRecyclerAdapter {

    public MySignUpAdapter(Context context, int resLayout) {
        super(context, resLayout);
    }

    @Override
    public void bindData(BaseRecyclerHolder holder, final int pos) {
        // 获取View
        ConstraintLayout item_main = holder.getView(R.id.my_sign_up_item_main);
        TextView item_time = holder.getView(R.id.my_sign_up_item_time);
        RoundImageView iv_show = holder.getView(R.id.my_sign_up_item_iv_show);
        TextView tv_cover = holder.getView(R.id.my_sign_up_item_tv_cover);
        TextView tv_title = holder.getView(R.id.my_sign_up_item_tv_title);
        TextView tv_time = holder.getView(R.id.my_sign_up_item_tv_time);
        TextView tv_address = holder.getView(R.id.my_sign_up_item_tv_address);

        // 绑定View
        final ThemeEntity data = (ThemeEntity) mDataList.get(pos);
        Glide.with(AppApplication.getAppContext())
                .load(data.getPicUrl())
                .apply(AppApplication.getShowOptions())
                .into(iv_show);

        if (data.getStatus() == 2) { //已过期
            tv_cover.setVisibility(View.VISIBLE);
        } else {
            tv_cover.setVisibility(View.GONE);
        }

        item_time.setText(TimeUtil.strToStrItem(data.getAddTime()));
        tv_title.setText(data.getTitle());
        tv_time.setText(context.getString(R.string.sign_up_time) + TimeUtil.strToStrYMD("yyyy-MM-dd", data.getStartTime()));
        tv_address.setText(context.getString(R.string.sign_up_address) + data.getAddress());

        item_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (apCallback != null) {
                    apCallback.setOnClick(data, pos, 0);
                }
            }
        });
    }

}
