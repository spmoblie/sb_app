package com.sbwg.sxb.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppConfig;
import com.sbwg.sxb.R;
import com.sbwg.sxb.entity.DesignEntity;
import com.sbwg.sxb.utils.CommonTools;

/**
 * 课程可选场次列表适配器
 */
public class MyDesignAdapter extends AppBaseAdapter {

	public static final int screenWidth = AppApplication.getSharedPreferences().getInt(AppConfig.KEY_SCREEN_WIDTH, 0);

	private Context context;
	private ConstraintLayout.LayoutParams showImgLP;

	public MyDesignAdapter(Context context) {
		super(context);
		this.context = context;

		showImgLP = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		showImgLP.height = (screenWidth - CommonTools.dpToPx(context, 6*3)) / 2;
	}

	static class ViewHolder {

		ConstraintLayout item_main;
		ImageView iv_show;

	}

	/**代表了ListView中的一个item对象*/
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			convertView = View.inflate(context, R.layout.item_list_my_design, null);
			
			holder = new ViewHolder();
			holder.item_main = convertView.findViewById(R.id.list_item_my_design_main);
			holder.iv_show = convertView.findViewById(R.id.list_item_my_design_iv_show);
			holder.iv_show.setLayoutParams(showImgLP);

			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		final DesignEntity data = (DesignEntity) mDataList.get(position);
		Glide.with(AppApplication.getAppContext())
				.load(data.getImgUrl())
				.apply(AppApplication.getShowOptions())
				.into(holder.iv_show);

		holder.item_main.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (apCallback != null) {
					apCallback.setOnClick(data, position, 1);
				}
			}
		});
		return convertView;
	}

}
