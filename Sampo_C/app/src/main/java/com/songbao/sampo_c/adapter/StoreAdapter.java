package com.songbao.sampo_c.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.entity.DesignerEntity;
import com.songbao.sampo_c.entity.StoreEntity;

public class StoreAdapter extends BaseRecyclerAdapter<StoreEntity> {

    public StoreAdapter(Context context, int resLayout) {
        super(context, resLayout);
    }

    @Override
    public void bindData(BaseRecyclerHolder holder, final int pos) {
        // 获取View
        ConstraintLayout item_main = holder.getView(R.id.list_item_store_main);
        ImageView iv_show = holder.getView(R.id.list_item_store_iv_show);
        TextView tv_name = holder.getView(R.id.list_item_store_tv_name);
        TextView tv_area = holder.getView(R.id.list_item_store_tv_area);
        TextView tv_info = holder.getView(R.id.list_item_store_tv_info);

        // 绑定View
        final StoreEntity data = mDataList.get(pos);
        Glide.with(AppApplication.getAppContext())
                .load(data.getImgUrl())
                .apply(AppApplication.getShowOptions())
                .into(iv_show);

        tv_name.setText(data.getName());
        tv_area.setText(data.getArea());
        tv_info.setText(data.getInfo());

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
