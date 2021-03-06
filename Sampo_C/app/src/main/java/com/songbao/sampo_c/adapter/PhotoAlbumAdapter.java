package com.songbao.sampo_c.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.entity.PhotoEntity;
import com.songbao.sampo_c.utils.CommonTools;

import java.util.List;


/**
 * 选择相册适配器
 */
public class PhotoAlbumAdapter extends AppBaseAdapter<PhotoEntity> {

	private BitmapFactory.Options options;
	private LinearLayout.LayoutParams imageLP;

	public PhotoAlbumAdapter(Context context, List<PhotoEntity> data) {
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
			convertView = View.inflate(context, R.layout.item_grid_photo_album, null);
			holder = new ViewHolder();
			holder.tv =  convertView.findViewById(R.id.photo_item_tv_name);
			holder.iv = convertView.findViewById(R.id.photo_item_iv_img);
			holder.iv.setLayoutParams(imageLP);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 通过ID 获取缩略图
		PhotoEntity data = mDataList.get(position);

		Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context .getContentResolver(),
				data.getFirstId(), Thumbnails.MINI_KIND, options);
		holder.iv.setImageBitmap(bitmap);
		holder.tv.setText(context.getString(R.string.photo_show_number, data.getName(), data.getCount()));

		return convertView;
	}

}
