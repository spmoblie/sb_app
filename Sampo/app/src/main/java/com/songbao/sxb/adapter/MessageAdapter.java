package com.songbao.sxb.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbao.sxb.R;
import com.songbao.sxb.entity.MessageEntity;

public class MessageAdapter extends BaseRecyclerAdapter {

    public MessageAdapter(Context context, int resLayout) {
        super(context, resLayout);
    }

    @Override
    public void bindData(BaseRecyclerHolder holder, final int pos) {
        // 获取View
        ConstraintLayout item_main = holder.getView(R.id.message_item_main);
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
