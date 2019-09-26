package com.sbwg.sxb.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppConfig;
import com.sbwg.sxb.R;
import com.sbwg.sxb.entity.ThemeEntity;
import com.sbwg.sxb.widgets.RoundImageView;

import java.util.List;


public class HomeListAdapter extends AppBaseAdapter {

	private static final int screenWidth = AppApplication.getSharedPreferences().getInt(AppConfig.KEY_SCREEN_WIDTH, 0);
	private Context context;
	private List<ThemeEntity> datas;
	private AdapterCallback apCallback;
	private ConstraintLayout.LayoutParams showImgLP;

	public HomeListAdapter(Context context, List<ThemeEntity> datas, AdapterCallback apCallback) {
		super(context);
		this.context = context;
		this.datas = datas;
		this.apCallback = apCallback;

		showImgLP = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		showImgLP.height = screenWidth / 2;
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
		ViewHolder holder;
		if(convertView == null){
			convertView = View.inflate(context, R.layout.item_list_home, null);
			
			holder = new ViewHolder();
			holder.cl_item = convertView.findViewById(R.id.fg_home_item_main);
			holder.iv_show = convertView.findViewById(R.id.fg_home_item_iv);
			holder.tv_title = convertView.findViewById(R.id.fg_home_item_tv_title);
			holder.iv_head = convertView.findViewById(R.id.fg_home_item_iv_head);
			holder.tv_name = convertView.findViewById(R.id.fg_home_item_tv_name);
			holder.tv_sign = convertView.findViewById(R.id.fg_home_item_tv_sign);

			holder.iv_show.setLayoutParams(showImgLP);

			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		final ThemeEntity data = datas.get(position);

		Glide.with(AppApplication.getAppContext())
				.load(data.getPicUrl())
				.apply(AppApplication.getShowOptions())
				.into(holder.iv_show);
		Glide.with(AppApplication.getAppContext())
				.load(data.getUserHead())
				.apply(AppApplication.getHeadOptions())
				.into(holder.iv_head);

		holder.tv_title.setText(data.getTitle());
		holder.tv_name.setText(data.getUserName());

		holder.tv_sign.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (apCallback != null) {
					apCallback.setOnClick(data, position, 1);
				}
			}
		});

		holder.cl_item.setOnClickListener(new OnClickListener() {
			
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

		ConstraintLayout cl_item;
		ImageView iv_show;
		RoundImageView iv_head;
		TextView tv_title, tv_name, tv_sign;

	}

}
