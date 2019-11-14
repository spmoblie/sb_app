package com.songbao.sampo.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;

import com.songbao.sampo.R;
import com.songbao.sampo.entity.CustomizeEntity;

public class MyCustomizeAdapter extends BaseRecyclerAdapter {

    public MyCustomizeAdapter(Context context, int resLayout) {
        super(context, resLayout);
    }

    @Override
    public void bindData(BaseRecyclerHolder holder, final int pos) {
        // 获取View
        ConstraintLayout item_main = holder.getView(R.id.my_customize_item_main);

        // 绑定View
        final CustomizeEntity data = (CustomizeEntity) mDataList.get(pos);

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
