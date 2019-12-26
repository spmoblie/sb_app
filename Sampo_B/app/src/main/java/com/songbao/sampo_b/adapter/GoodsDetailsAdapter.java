package com.songbao.sampo_b.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.R;

/**
 * 商品详情图片列表适配器
 */
public class GoodsDetailsAdapter extends AppBaseAdapter {

	public GoodsDetailsAdapter(Context context) {
		super(context);
	}

	static class ViewHolder {
		ImageView iv_show;
	}

	/**代表了ListView中的一个item对象*/
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			convertView = View.inflate(context, R.layout.item_list_goods_details, null);
			
			holder = new ViewHolder();
			holder.iv_show = convertView.findViewById(R.id.list_item_goods_details_show);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		String imgUrl = (String) mDataList.get(position);

		Glide.with(AppApplication.getAppContext())
				.load(imgUrl)
				.apply(AppApplication.getShowOptions())
				.into(holder.iv_show);

		return convertView;
	}

}