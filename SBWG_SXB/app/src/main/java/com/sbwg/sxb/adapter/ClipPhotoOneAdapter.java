package com.sbwg.sxb.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;

import com.sbwg.sxb.entity.ClipPhotoEntity;
import com.sbwg.sxb.widgets.photo.PhotoGridItem;


/**
 * 选择相片适配器
 */
public class ClipPhotoOneAdapter extends AppBaseAdapter {

	private Context context;
	private ClipPhotoEntity album;

	public ClipPhotoOneAdapter(Context context, ClipPhotoEntity album) {
		super(context);
		this.context = context;
		this.album = album;
	}

	@Override
	public int getCount() {
		return album.getBitList().size();
	}

	@Override
	public Object getItem(int position) {
		return album.getBitList().get(getCount() - 1 - position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PhotoGridItem item;
		if (convertView == null) {
			item = new PhotoGridItem(context);
			item.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		} else {
			item = (PhotoGridItem) convertView;
		}

		// 通过ID 加载缩略图
		Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(),
				album.getBitList().get(getCount() - 1 - position).getPhotoId(), Thumbnails.MINI_KIND, null);
		item.SetBitmap(bitmap);
		boolean flag = album.getBitList().get(getCount() - 1 - position).isSelect();
		item.setChecked(flag);
		return item;
	}

}
