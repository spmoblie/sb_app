package com.sbwg.sxb.widgets.area;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbwg.sxb.R;


public class AreaIndexDisplayAdapter extends IndexDisplayAdapter {

	OnIndexDisplayItemClick onIndexDisplayItemClick;

	public AreaIndexDisplayAdapter(Context mContext) {
		super(mContext);
	}

	public void setOnIndexDisplayItemClick(OnIndexDisplayItemClick onIndexDisplayItemClick) {
		this.onIndexDisplayItemClick = onIndexDisplayItemClick;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ChildHolder holder;
		if (convertView == null) {
			holder = new ChildHolder();
			convertView = ((Activity) weakContext.get()).getLayoutInflater().inflate(R.layout.item_area_index_display, null);

			holder.area_indexLayout = convertView.findViewById(R.id.item_area_indexLayout);
			holder.area_indexNameTextView = convertView.findViewById(R.id.item_area_indexNameTextView);
			holder.area_nameTextView = convertView.findViewById(R.id.item_area_nameTextView);
			holder.area_dividerImageView = convertView.findViewById(R.id.item_area_dividerImageView);

			holder.area_itemLayout = convertView.findViewById(R.id.item_area_itemLayout);
			holder.area_itemLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (onIndexDisplayItemClick != null)
						onIndexDisplayItemClick.onIndexDisplayItemClick((IndexDisplay) v.getTag());
				}
			});
			convertView.setTag(holder);
		} else {
			holder = (ChildHolder) convertView.getTag();
		}
		IndexDisplay item = getItem(position);

		if (item != null) {
			holder.obj = item;
			holder.area_itemLayout.setTag(item);
			if (isFirst(position)) {
				String indexStr = getIndexChar(position);
				holder.area_indexLayout.setVisibility(View.VISIBLE);
				holder.area_indexNameTextView.setText(indexStr);
			} else {
				holder.area_indexLayout.setVisibility(View.GONE);
			}
			/*if (isLast(position)) {
				holder.area_dividerImageView.setVisibility(View.GONE);
			} else {
				holder.area_dividerImageView.setVisibility(View.VISIBLE);
			}*/
			holder.area_nameTextView.setText(((AreaEntity) item).getName());
		}
		return convertView;
	}

	public static class ChildHolder extends SuperHolder {
		ViewGroup area_indexLayout;
		TextView area_indexNameTextView;

		ViewGroup area_itemLayout;
		TextView area_nameTextView;
		ImageView area_dividerImageView;
	}

}
