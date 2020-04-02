package com.songbao.sampo_b.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.entity.ThemeEntity;
import com.songbao.sampo_b.utils.ClickUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ThemeListAdapter extends RecyclerView.Adapter<ThemeListAdapter.ViewHolder>{

    private Context mContext;
    private View mHeaderView;
    private AdapterCallback apCallback;
    private ArrayList<ThemeEntity> mData;

    public ThemeListAdapter(Context context, ArrayList<ThemeEntity> data, AdapterCallback apCallback) {
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // 创建头部View
        if(mHeaderView != null && i == 0) return new ViewHolder(mHeaderView);
        // 创建一个View
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_home, viewGroup, false);
        // 创建一个ViewHolder
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if(getItemViewType(i) == 0) return;
        final int pos = getRealPosition(viewHolder);
        final ThemeEntity data = mData.get(pos);

        // 绑定数据到ViewHolder
        String imgUrl = "";
        if (data.getPicUrls() != null && data.getPicUrls().size() > 0) {
            imgUrl = data.getPicUrls().get(0);
        }
        Glide.with(AppApplication.getAppContext())
                .load(imgUrl)
                .apply(AppApplication.getShowOptions())
                .into(viewHolder.iv_show);

        viewHolder.tv_title.setText(data.getTitle());
        viewHolder.tv_name.setText(data.getUserName());
        viewHolder.tv_series.setText(data.getSeries());
        viewHolder.tv_time.setText(data.getAddTime());

        if (data.getThemeType() == 1) {
            viewHolder.tv_sign.setText("立即预约");
            viewHolder.tv_sign.setBackgroundResource(R.drawable.shape_style_solid_04_08);
            viewHolder.tv_series.setBackgroundResource(R.drawable.shape_style_empty_06_08);
            viewHolder.tv_series.setTextColor(mContext.getResources().getColor(R.color.app_color_yellow));
        } else {
            viewHolder.tv_sign.setText("我要报名");
            viewHolder.tv_sign.setBackgroundResource(R.drawable.shape_style_solid_06_08);
            viewHolder.tv_series.setBackgroundResource(R.drawable.shape_style_empty_09_08);
            viewHolder.tv_series.setTextColor(mContext.getResources().getColor(R.color.app_color_blue));
        }
        viewHolder.tv_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtils.isDoubleClick(v.getId())) return;
                if (apCallback != null) {
                    apCallback.setOnClick(data, pos, 1);
                }
            }
        });

        viewHolder.item_main.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ClickUtils.isDoubleClick(v.getId())) return;
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

        @BindView(R.id.fg_home_item_main)
        ConstraintLayout item_main;

        @BindView(R.id.fg_home_item_iv)
        ImageView iv_show;

        @BindView(R.id.fg_home_item_tv_title)
        TextView tv_title;

        @BindView(R.id.fg_home_item_tv_name)
        TextView tv_name;

        @BindView(R.id.fg_home_item_tv_series)
        TextView tv_series;

        @BindView(R.id.fg_home_item_tv_time)
        TextView tv_time;

        @BindView(R.id.fg_home_item_tv_sign)
        TextView tv_sign;

        public ViewHolder(View itemView) {
            super(itemView);
            if(itemView == mHeaderView) return;
            ButterKnife.bind(this, itemView);
        }
    }

}
