package com.songbao.sxb.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbao.sxb.R;
import com.songbao.sxb.entity.CouponEntity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyTicketsAdapter extends RecyclerView.Adapter<MyTicketsAdapter.ViewHolder>{

    private Context mContext;
    private View mHeaderView;
    private AdapterCallback apCallback;
    private ArrayList<CouponEntity> mData;

    public MyTicketsAdapter(Context context, ArrayList<CouponEntity> data, AdapterCallback apCallback) {
        super();
        mContext = context;
        this.apCallback = apCallback;
        if (data != null) {
            mData = data;
        } else {
            mData = new ArrayList<>();
        }
    }

    public void updateData(ArrayList<CouponEntity> data){
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_my_tickets, viewGroup, false);
        // 创建一个ViewHolder
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        if(getItemViewType(i) == 0) return;
        final int pos = getRealPosition(viewHolder);
        final CouponEntity data = mData.get(pos);

        if (pos == 0) {
            viewHolder.iv_top.setVisibility(View.VISIBLE);
        } else {
            viewHolder.iv_top.setVisibility(View.GONE);
        }
        // 绑定数据到ViewHolder
        viewHolder.tv_title.setText(data.getName());
        viewHolder.tv_time.setText(mContext.getString(R.string.coupon_expired_time, data.getTermTime()));

        switch (data.getStatus()) {
            case 2:
                viewHolder.tv_state.setText(mContext.getString(R.string.cancelled));
                viewHolder.tv_state.setBackgroundResource(R.drawable.shape_style_solid_9_18);
                break;
            case 3:
                viewHolder.tv_state.setText(mContext.getString(R.string.expired));
                viewHolder.tv_state.setBackgroundResource(R.drawable.shape_style_solid_3_18);
                break;
            default:
                viewHolder.tv_state.setText(mContext.getString(R.string.active));
                viewHolder.tv_state.setBackgroundResource(R.drawable.shape_style_solid_5_18);
                break;
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

        @BindView(R.id.my_tickets_item_main)
        ConstraintLayout item_main;

        @BindView(R.id.my_tickets_item_iv_top)
        ImageView iv_top;

        @BindView(R.id.my_tickets_item_tv_title)
        TextView tv_title;

        @BindView(R.id.my_tickets_item_tv_state)
        TextView tv_state;

        @BindView(R.id.my_tickets_item_tv_time)
        TextView tv_time;

        public ViewHolder(View itemView) {
            super(itemView);
            if(itemView == mHeaderView) return;
            ButterKnife.bind(this, itemView);
        }
    }

}
