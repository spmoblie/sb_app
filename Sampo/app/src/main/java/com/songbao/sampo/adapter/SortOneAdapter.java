package com.songbao.sampo.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songbao.sampo.R;
import com.songbao.sampo.entity.GoodsSortEntity;

import java.util.List;

public class SortOneAdapter extends BaseRecyclerAdapter {

    private int selectPos = 0;

    public SortOneAdapter(Context context, int resLayout) {
        super(context, resLayout);
    }

    /**
     * 刷新数据
     */
    public void updateData(List<GoodsSortEntity> data, int selectPos){
        this.selectPos = selectPos;
        this.mDataList.clear();
        this.mDataList.addAll(data);
        this.notifyDataSetChanged();
    }

    @Override
    public void bindData(BaseRecyclerHolder holder, final int pos) {
        // 获取View
        ConstraintLayout item_main = holder.getView(R.id.sort_one_item_main);
        RelativeLayout rl_top = holder.getView(R.id.sort_one_item_rl_top);
        TextView tv_status = holder.getView(R.id.sort_one_item_tv_status);
        TextView tv_name = holder.getView(R.id.sort_one_item_tv_name);

        // 绑定View
        final GoodsSortEntity data = (GoodsSortEntity) mDataList.get(pos);

        if (pos == 0) {
            rl_top.setVisibility(View.VISIBLE);
        } else {
            rl_top.setVisibility(View.GONE);
        }

        tv_name.setText(data.getName());
        if (pos == selectPos) {
            tv_name.setSelected(true);
            tv_status.setVisibility(View.VISIBLE);
        } else {
            tv_name.setSelected(false);
            tv_status.setVisibility(View.GONE);
        }

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
