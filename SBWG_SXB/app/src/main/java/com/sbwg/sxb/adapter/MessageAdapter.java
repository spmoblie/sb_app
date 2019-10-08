package com.sbwg.sxb.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbwg.sxb.R;
import com.sbwg.sxb.entity.MessageEntity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    private Context mContext;
    private View mHeaderView;
    private AdapterCallback apCallback;
    private ArrayList<MessageEntity> mData;

    public MessageAdapter(Context context, ArrayList<MessageEntity> data, AdapterCallback apCallback) {
        super();
        mContext = context;
        this.apCallback = apCallback;
        if (data != null) {
            mData = data;
        } else {
            mData = new ArrayList<>();
        }
    }

    public void updateData(ArrayList<MessageEntity> data){
        if (data != null) {
            mData = data;
        } else {
            mData = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    @Override
    public int getItemViewType(int position) {
        if(mHeaderView == null) return 1;
        if(position == 0) return 0;
        return 1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // 创建头部View
        if(mHeaderView != null && i == 0) return new ViewHolder(mHeaderView);
        // 创建一个View
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_message, viewGroup, false);
        // 创建一个ViewHolder
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        if(getItemViewType(i) == 0) return;
        final int pos = getRealPosition(viewHolder);
        final MessageEntity data = mData.get(pos);

        // 绑定数据到ViewHolder
        viewHolder.item_time.setText(data.getTime());
        viewHolder.item_title.setText(data.getTitle());
        viewHolder.item_content.setText(data.getContent());

        if (data.isRead()) {
            viewHolder.item_warn_red.setVisibility(View.GONE);
        } else {
            viewHolder.item_warn_red.setVisibility(View.VISIBLE);
        }

        viewHolder.item_main.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (apCallback != null) {
                    apCallback.setOnClick(data, pos, 0);
                }
            }
        });

    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? mData.size() : mData.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.message_item_main)
        ConstraintLayout item_main;

        @BindView(R.id.message_item_time)
        TextView item_time;

        @BindView(R.id.message_item_title)
        TextView item_title;

        @BindView(R.id.message_item_content)
        TextView item_content;

        @BindView(R.id.message_item_warn_red)
        ImageView item_warn_red;

        public ViewHolder(View itemView) {
            super(itemView);
            if(itemView == mHeaderView) return;
            ButterKnife.bind(this, itemView);
        }
    }

}
