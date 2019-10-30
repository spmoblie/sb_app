package com.songbao.sxb.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sxb.AppApplication;
import com.songbao.sxb.R;
import com.songbao.sxb.entity.OrderEntity;
import com.songbao.sxb.widgets.RoundImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.ViewHolder>{

    private Context mContext;
    private View mHeaderView;
    private AdapterCallback apCallback;
    private ArrayList<OrderEntity> mData;

    public MyOrderAdapter(Context context, ArrayList<OrderEntity> data, AdapterCallback apCallback) {
        super();
        mContext = context;
        this.apCallback = apCallback;
        if (data != null) {
            mData = data;
        } else {
            mData = new ArrayList<>();
        }
    }

    public void updateData(ArrayList<OrderEntity> data){
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_my_order, viewGroup, false);
        // 创建一个ViewHolder
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        if(getItemViewType(i) == 0) return;
        final int pos = getRealPosition(viewHolder);
        final OrderEntity data = mData.get(pos);

        if (pos == 0) {
            viewHolder.iv_top.setVisibility(View.VISIBLE);
        } else {
            viewHolder.iv_top.setVisibility(View.GONE);
        }
        // 绑定数据到ViewHolder
        Glide.with(AppApplication.getAppContext())
                .load(data.getPicUrl())
                .apply(AppApplication.getShowOptions())
                .into(viewHolder.iv_show);

        viewHolder.tv_name.setText(data.getName());
        viewHolder.tv_title.setText(data.getTitle());
        viewHolder.tv_pay_type.setText(data.getPayType());
        viewHolder.tv_cost.setText(data.getCost());
        viewHolder.tv_time.setText(data.getAddTime());

        switch (data.getStatus()) {
            case 2:
                viewHolder.tv_state.setText(mContext.getString(R.string.cancelled));
                viewHolder.tv_state.setTextColor(mContext.getResources().getColor(R.color.app_color_red_7));
                break;
            case 3:
                viewHolder.tv_state.setText(mContext.getString(R.string.expired));
                viewHolder.tv_state.setTextColor(mContext.getResources().getColor(R.color.app_color_gray_5));
                break;
            default:
                viewHolder.tv_state.setText(mContext.getString(R.string.pay_ok));
                viewHolder.tv_state.setTextColor(mContext.getResources().getColor(R.color.tv_color_status));
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

        @BindView(R.id.my_order_item_main)
        ConstraintLayout item_main;

        @BindView(R.id.my_order_item_iv_top)
        ImageView iv_top;

        @BindView(R.id.my_order_item_tv_name)
        TextView tv_name;

        @BindView(R.id.my_order_item_tv_state)
        TextView tv_state;

        @BindView(R.id.my_order_item_iv_show)
        RoundImageView iv_show;

        @BindView(R.id.my_order_item_tv_title)
        TextView tv_title;

        @BindView(R.id.my_order_item_tv_pay_type)
        TextView tv_pay_type;

        @BindView(R.id.my_order_item_tv_cost)
        TextView tv_cost;

        @BindView(R.id.my_order_item_tv_time)
        TextView tv_time;

        public ViewHolder(View itemView) {
            super(itemView);
            if(itemView == mHeaderView) return;
            ButterKnife.bind(this, itemView);
        }
    }

}
