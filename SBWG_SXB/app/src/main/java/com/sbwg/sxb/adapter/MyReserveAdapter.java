package com.sbwg.sxb.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.R;
import com.sbwg.sxb.entity.ThemeEntity;
import com.sbwg.sxb.widgets.RoundImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyReserveAdapter extends RecyclerView.Adapter<MyReserveAdapter.ViewHolder>{

    private Context mContext;
    private View mHeaderView;
    private AdapterCallback apCallback;
    private ArrayList<ThemeEntity> mData;

    public MyReserveAdapter(Context context, ArrayList<ThemeEntity> data, AdapterCallback apCallback) {
        super();
        mContext = context;
        this.apCallback = apCallback;
        if (data != null) {
            mData = data;
        } else {
            mData = new ArrayList<>();
        }
    }

    public void updateData(ArrayList<ThemeEntity> data){
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_my_reserve, viewGroup, false);
        // 创建一个ViewHolder
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        if(getItemViewType(i) == 0) return;
        final int pos = getRealPosition(viewHolder);
        final ThemeEntity data = mData.get(pos);

        // 绑定数据到ViewHolder
        Glide.with(AppApplication.getAppContext())
                .load(data.getPicUrl())
                .apply(AppApplication.getShowOptions())
                .into(viewHolder.iv_show);

        viewHolder.item_time.setText(data.getAddTime());
        viewHolder.tv_title.setText(data.getTitle());
        viewHolder.tv_date.setText(mContext.getString(R.string.reserve_date, data.getReserveDate()));
        viewHolder.tv_time.setText(mContext.getString(R.string.reserve_time, data.getReserveTime()));
        viewHolder.tv_address.setText(mContext.getString(R.string.reserve_place, data.getAddress()));

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

        @BindView(R.id.my_reserve_item_main)
        ConstraintLayout item_main;

        @BindView(R.id.my_reserve_item_time)
        TextView item_time;

        @BindView(R.id.my_reserve_item_iv_show)
        RoundImageView iv_show;

        @BindView(R.id.my_reserve_item_tv_title)
        TextView tv_title;

        @BindView(R.id.my_reserve_item_tv_date)
        TextView tv_date;

        @BindView(R.id.my_reserve_item_tv_time)
        TextView tv_time;

        @BindView(R.id.my_reserve_item_tv_address)
        TextView tv_address;

        public ViewHolder(View itemView) {
            super(itemView);
            if(itemView == mHeaderView) return;
            ButterKnife.bind(this, itemView);
        }
    }

}
