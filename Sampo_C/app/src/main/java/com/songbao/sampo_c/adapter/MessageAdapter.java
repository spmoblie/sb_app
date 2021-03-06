package com.songbao.sampo_c.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbao.sampo_c.R;
import com.songbao.sampo_c.entity.MessageEntity;

import java.util.List;

public class MessageAdapter extends BaseRecyclerAdapter {

    private int newNum = 0;

    public MessageAdapter(Context context, int resLayout) {
        super(context, resLayout);
    }

    /**
     * 添加数据
     */
    public void addData(List<MessageEntity> data, int newNum){
        this.newNum = newNum;
        this.mDataList.addAll(data);
        this.notifyDataSetChanged();
    }

    /**
     * 刷新数据
     */
    public void updateData(List<MessageEntity> data, int newNum){
        this.newNum = newNum;
        this.mDataList.clear();
        this.mDataList.addAll(data);
        this.notifyDataSetChanged();
    }

    @Override
    public void bindData(BaseRecyclerHolder holder, final int pos) {
        // 获取View
        ConstraintLayout item_main = holder.getView(R.id.message_item_main);
        LinearLayout item_history = holder.getView(R.id.message_item_line_main);
        TextView item_time = holder.getView(R.id.message_item_time);
        TextView item_title = holder.getView(R.id.message_item_title);
        TextView item_content = holder.getView(R.id.message_item_content);
        ImageView item_warn_red = holder.getView(R.id.message_item_warn_red);

        // 绑定View
        final MessageEntity data = (MessageEntity) mDataList.get(pos);
        item_time.setText(data.getAddTime());
        item_title.setText(data.getTitle());
        item_content.setText(data.getContent());

        if (data.isRead()) {
            item_warn_red.setVisibility(View.GONE);
        } else {
            item_warn_red.setVisibility(View.VISIBLE);
        }

        // 历史消息提示
        if (newNum > 0 && pos == newNum) {
            item_history.setVisibility(View.VISIBLE);
        } else {
            item_history.setVisibility(View.GONE);
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
