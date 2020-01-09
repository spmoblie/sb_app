package com.songbao.sampo_b.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.entity.DesignerEntity;

public class DesignerAdapter extends BaseRecyclerAdapter<DesignerEntity> {

    public DesignerAdapter(Context context, int resLayout) {
        super(context, resLayout);
    }

    @Override
    public void bindData(BaseRecyclerHolder holder, final int pos) {
        // 获取View
        ConstraintLayout item_main = holder.getView(R.id.list_item_designer_main);
        ImageView iv_head = holder.getView(R.id.list_item_designer_iv_head);
        ImageView iv_select = holder.getView(R.id.list_item_designer_iv_select);
        TextView tv_name = holder.getView(R.id.list_item_designer_tv_name);
        TextView tv_phone = holder.getView(R.id.list_item_designer_tv_phone);
        TextView tv_info = holder.getView(R.id.list_item_designer_tv_info);

        // 绑定View
        final DesignerEntity data = mDataList.get(pos);
        Glide.with(AppApplication.getAppContext())
                .load(data.getImgUrl())
                .apply(AppApplication.getShowOptions())
                .into(iv_head);

        if (data.isSelect()) {
            iv_select.setSelected(true);
        } else {
            iv_select.setSelected(false);
        }

        tv_name.setText(data.getName());
        tv_phone.setText(data.getPhone());
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
