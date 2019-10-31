package com.songbao.sxb.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbao.sxb.AppApplication;
import com.songbao.sxb.R;
import com.songbao.sxb.entity.ClipPhotoEntity;
import com.songbao.sxb.utils.CommonTools;

import java.util.List;


/**
 * 选择相册适配器
 */
public class ClipPhotoGridAdapter extends AppBaseAdapter {

	private BitmapFactory.Options options;
	private LinearLayout.LayoutParams imageLP;

	public ClipPhotoGridAdapter(Context context, List<ClipPhotoEntity> data) {
		super(context);

		addData(data);

		options = new BitmapFactory.Options();
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;

		int imageSize = (AppApplication.screen_width - CommonTools.dpToPx(context, 35)) / 2;
		this.imageLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		this.imageLP.width = imageSize;
		this.imageLP.height = imageSize;
	}

	static class ViewHolder {
		ImageView iv;
		TextView tv;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.item_list_photo_select, null);
			holder = new ViewHolder();
			holder.tv =  convertView.findViewById(R.id.photo_item_name);
			holder.iv = convertView.findViewById(R.id.photo_item_image);
			holder.iv.setLayoutParams(imageLP);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		/** 通过ID 获取缩略图 */
		ClipPhotoEntity data = (ClipPhotoEntity) mDataList.get(position);

		Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context .getContentResolver(),
				data.getFirstId(), Thumbnails.MINI_KIND, options);
		holder.iv.setImageBitmap(bitmap);
		holder.tv.setText(data.getName() + " ( " + data.getCount() + " )");

		return convertView;
	}

}
