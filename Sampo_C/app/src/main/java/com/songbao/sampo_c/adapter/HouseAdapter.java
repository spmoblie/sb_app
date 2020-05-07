package com.songbao.sampo_c.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.entity.HouseEntity;
import com.songbao.sampo_c.entity.StoreEntity;
import com.songbao.sampo_c.utils.StringUtil;

public class HouseAdapter extends BaseRecyclerAdapter<HouseEntity> {

    public HouseAdapter(Context context, int resLayout) {
        super(context, resLayout);
    }

    @Override
    public void bindData(BaseRecyclerHolder holder, final int pos) {
        // 获取View
        ConstraintLayout item_main = holder.getView(R.id.list_item_house_main);
        ImageView iv_show = holder.getView(R.id.list_item_house_iv_show);
        TextView tv_name = holder.getView(R.id.list_item_house_tv_name);
        TextView tv_info = holder.getView(R.id.list_item_house_tv_info);

        // 绑定View
        final HouseEntity data = mDataList.get(pos);
        Glide.with(AppApplication.getAppContext())
                .load(data.getImgUrl())
                .apply(AppApplication.getShowOptions())
                .into(iv_show);

        tv_name.setText(data.getName());
        tv_info.setText(StringUtil.htmlDecode(data.getInfo()));

        item_main.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (apCallback != null) {
                    apCallback.setOnClick(data, pos, 1);
                }
            }
        });
    }

}
