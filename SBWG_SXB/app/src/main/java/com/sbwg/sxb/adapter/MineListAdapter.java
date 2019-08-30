package com.sbwg.sxb.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppConfig;
import com.sbwg.sxb.R;
import com.sbwg.sxb.entity.ThemeEntity;
import com.sbwg.sxb.widgets.RoundImageView;

import java.util.List;


public class MineListAdapter extends AppBaseAdapter {

	private static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;
	private Context context;
	private List<ThemeEntity> datas;
	private AdapterCallback apCallback;

	public MineListAdapter(Context context, List<ThemeEntity> datas, AdapterCallback apCallback) {
		super(context);
		this.context = context;
		this.datas = datas;
		this.apCallback = apCallback;
	}

	public void updateAdapter(List<ThemeEntity> datas){
		if (datas != null) {
			this.datas = datas;
			notifyDataSetChanged();
		}
	}

	/**获得总共有多少条数据*/
	@Override
	public int getCount() {
		return datas.size();
	}

	/**在ListView中显示的每个item内容*/
	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	/**返回集合中个某个元素的位置*/
	@Override
	public long getItemId(int position) {
		return position;
	}

	/**代表了ListView中的一个item对象*/
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = View.inflate(context, R.layout.item_list_mine, null);
			
			holder = new ViewHolder();
			holder.rl_item = convertView.findViewById(R.id.fg_mine_item_main);
			holder.iv_show = convertView.findViewById(R.id.fg_mine_item_iv_show);
			holder.tv_title = convertView.findViewById(R.id.fg_mine_item_tv_title);
			holder.tv_time = convertView.findViewById(R.id.fg_mine_item_tv_time);
			holder.tv_number = convertView.findViewById(R.id.fg_mine_item_tv_number);

			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		final ThemeEntity data = datas.get(position);

		Glide.with(AppApplication.getAppContext())
				.load(IMAGE_URL_HTTP + data.getImgUrl())
				.apply(AppApplication.getShowOpeions())
				.into(holder.iv_show);

		holder.tv_title.setText(data.getTitle());
		holder.tv_time.setText(data.getExplain());
		holder.tv_number.setText(data.getNumber());

		holder.rl_item.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (apCallback != null) {
					apCallback.setOnClick(data, position, 0);
				}
			}
		});
		return convertView;
	}

	static class ViewHolder{

		RelativeLayout rl_item;
		RoundImageView iv_show;
		TextView tv_title, tv_time, tv_number;

	}

}
