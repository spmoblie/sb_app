package com.sbwg.sxb.adapter;

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

import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppConfig;
import com.sbwg.sxb.R;
import com.sbwg.sxb.entity.ClipPhotoEntity;
import com.sbwg.sxb.utils.CommonTools;

import java.util.List;


/**
 * 选择相册适配器
 */
public class ClipPhotoGridAdapter extends AppBaseAdapter {

	private List<ClipPhotoEntity> albumList;
	private Context context;
	private BitmapFactory.Options options;
	private LinearLayout.LayoutParams imageLP;

	public ClipPhotoGridAdapter(List<ClipPhotoEntity> list, Context context) {
		super(context);
		this.albumList = list;
		this.context = context;

		options = new BitmapFactory.Options();
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;

		int imageSize = (AppApplication.getSharedPreferences().getInt(AppConfig.KEY_SCREEN_WIDTH, 0) - CommonTools.dpToPx(context, 35)) / 2;
		this.imageLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		this.imageLP.width = imageSize;
		this.imageLP.height = imageSize;
	}

	@Override
	public int getCount() {
		return albumList.size();
	}

	@Override
	public Object getItem(int position) {
		return albumList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
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
		Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context .getContentResolver(),
				albumList.get(position).getFirstId(), Thumbnails.MINI_KIND, options);
		holder.iv.setImageBitmap(bitmap);
		holder.tv.setText(albumList.get(position).getName() + " ( " + albumList.get(position).getCount() + " )");

		return convertView;
	}

}
