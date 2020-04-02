package com.songbao.sampo_c.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.entity.ThemeEntity;
import com.songbao.sampo_c.utils.TimeUtil;
import com.songbao.sampo_c.widgets.RoundImageView;

public class MyReserveAdapter extends BaseRecyclerAdapter<ThemeEntity> {

    public MyReserveAdapter(Context context, int resLayout) {
        super(context, resLayout);
    }

    @Override
    public void bindData(BaseRecyclerHolder holder, final int pos) {
        // 获取View
        ConstraintLayout item_main = holder.getView(R.id.my_reserve_item_main);
        RoundImageView iv_show = holder.getView(R.id.my_reserve_item_iv_show);
        TextView item_time = holder.getView(R.id.my_reserve_item_time);
        TextView tv_cover = holder.getView(R.id.my_reserve_item_tv_cover);
        TextView tv_title = holder.getView(R.id.my_reserve_item_tv_title);
        TextView tv_date = holder.getView(R.id.my_reserve_item_tv_date);
        TextView tv_time = holder.getView(R.id.my_reserve_item_tv_time);

        // 绑定View
        final ThemeEntity data = mDataList.get(pos);
        Glide.with(AppApplication.getAppContext())
                .load(data.getPicUrl())
                .apply(AppApplication.getShowOptions())
                .into(iv_show);

        switch (data.getWriteOffStatus()) {
            case 3: //已核销
                tv_cover.setText(context.getString(R.string.cancelled));
                tv_cover.setVisibility(View.VISIBLE);
                break;
            case 10: //已过期
                tv_cover.setText(context.getString(R.string.expired));
                tv_cover.setVisibility(View.VISIBLE);
                break;
            default:
                tv_cover.setVisibility(View.GONE);
                break;
        }

        item_time.setText(TimeUtil.strToStrItem(data.getAddTime()));
        tv_title.setText(data.getTitle());
        tv_date.setText(context.getString(R.string.reserve_date_item, TimeUtil.strToStrYMD("yyyy-MM-dd", data.getStartTime())));
        tv_time.setText(context.getString(R.string.reserve_time_item, data.getReserveTime()));

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
