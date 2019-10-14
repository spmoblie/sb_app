package com.songbao.sxb.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;

import com.songbao.sxb.entity.ClipPhotoEntity;
import com.songbao.sxb.widgets.photo.PhotoGridItem;


/**
 * 选择相片适配器
 */
public class ClipPhotoOneAdapter extends AppBaseAdapter {

	private Context context;
	private ClipPhotoEntity album;
	private BitmapFactory.Options options;

	public ClipPhotoOneAdapter(Context context, ClipPhotoEntity album) {
		super(context);
		this.context = context;
		this.album = album;

		options = new BitmapFactory.Options();
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
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
				album.getBitList().get(getCount() - 1 - position).getPhotoId(), Thumbnails.MINI_KIND, options);
		item.SetBitmap(bitmap);
		boolean flag = album.getBitList().get(getCount() - 1 - position).isSelect();
		item.setChecked(flag);
		return item;
	}

}
