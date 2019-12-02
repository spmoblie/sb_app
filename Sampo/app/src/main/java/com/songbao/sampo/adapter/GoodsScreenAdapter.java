package com.songbao.sampo.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.songbao.sampo.AppApplication;
import com.songbao.sampo.R;
import com.songbao.sampo.entity.GoodsAttrEntity;
import com.songbao.sampo.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 商品筛选ListView适配器
 */
public class GoodsScreenAdapter extends BaseRecyclerAdapter {

	private final int viewWidth = AppApplication.screen_width * 4 / 5;

	private Context context;
	private HashMap<Integer, GoodsAttrEntity> attrHashMap = new HashMap<>();
	private ScreenClickCallback callback;
	private int txtSize, pdWidth, pdHeight, mgWidth;
	private int mgDps, tvSpec;
	private int select_id_1, select_id_2;
	private String select_name_1, select_name_2;

	public GoodsScreenAdapter(Context context, GoodsAttrEntity attrEn,
							  List<Integer> resLayout, ScreenClickCallback callback) {
		super(context, resLayout);
		this.context = context;
		this.callback = callback;

		txtSize = 12;
		mgWidth = 15;
		pdWidth = 15;
		pdHeight = 10;
		mgDps = context.getResources().getDimensionPixelSize(R.dimen.app_margin_screen) * 2;
		tvSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

		addData(attrEn.getAttrLists());
	}

	@Override
	public int getItemViewType(int position) {
		if (position == getItemCount() - 1) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public void bindData(BaseRecyclerHolder holder, final int pos) {
		final GoodsAttrEntity data = (GoodsAttrEntity) mDataList.get(pos);
		if (pos == getItemCount() - 1) {

		} else {
			// 获取View
			TextView tv_name = holder.getView(R.id.screen_item_1_tv_attr_name);
			ImageView iv_gone = holder.getView(R.id.screen_item_1_iv_attr_gone);
			RelativeLayout rl_main = holder.getView(R.id.screen_item_rl_attr_main);
			if (data != null) {
				tv_name.setId(data.getAttrId());
				tv_name.setText(data.getAttrName());

				rl_main.removeAllViews();
				addAttributeView(rl_main, data, pos);
			}

			if (data.isGone()) {
				rl_main.setVisibility(View.GONE);
			} else {
				rl_main.setVisibility(View.VISIBLE);
			}
			iv_gone.setSelected(data.isGone());
			iv_gone.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

				}
			});
		}
	}

	/**
	 * 动态添加View
	 */
	private void addAttributeView(RelativeLayout rl_main, final GoodsAttrEntity data, final int position) {
		// 添加属性类别名称
		/*int attrId = data.getAttrId();
		TextView tv_name = new TextView(context);
		tv_name.setText(data.getAttrName() + ":");
		tv_name.setTextColor(context.getResources().getColor(R.color.shows_text_color));
		tv_name.setTextSize(txtSize);
		tv_name.setId(attrId);
		rl_main.addView(tv_name);*/

		ArrayList<GoodsAttrEntity> nameLists = data.getAttrLists();
		if (nameLists == null || nameLists.size() == 0) {
			return;
		}
		// 循环添加属性View
		int widthTotal = mgDps;
		int tvWidth;
		int viewId;
		int firstId = 0;
		int beforeId = 0;
		String str;

		for (int i = 0; i < nameLists.size(); i++) {
			str = nameLists.get(i).getAttrName();
			viewId = nameLists.get(i).getAttrId();
			if (i > 0) {
				beforeId = nameLists.get(i-1).getAttrId();
			}
			TextView tv = new TextView(context);
			attrHashMap.put(viewId, nameLists.get(i));
			tv.setPadding(pdWidth, pdHeight, pdWidth, pdHeight);
			tv.setGravity(Gravity.CENTER);
			tv.setText(str);
			tv.setTextSize(txtSize);
			tv.setId(viewId);
			tv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}

			});

			// 计算TextView的宽度
			tv.measure(tvSpec, tvSpec);
			tvWidth = tv.getMeasuredWidth() + 2 + mgWidth; //view宽+边框+右外边距
			widthTotal += tvWidth;

			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			if (i == 0) {
				params.addRule(RelativeLayout.BELOW, data.getAttrId()); //在此id控件的下边
				firstId = viewId;
			}else {
				if (widthTotal < viewWidth) {
					params.addRule(RelativeLayout.RIGHT_OF, beforeId); //在控件的右边
					params.addRule(RelativeLayout.ALIGN_BOTTOM, beforeId); //与控件底部对齐
				}else {
					params.addRule(RelativeLayout.BELOW, firstId); //在控件的下边
					firstId = viewId;
					widthTotal = mgDps + tvWidth;
				}
			}
			params.setMargins(0, mgWidth, mgWidth, 0);
			rl_main.addView(tv,params);
		}
	}

	private void updateSelectStatus(View v, TextView tv, int position, int selectId) {
		TextView tv_item = (TextView)v;
		switch (position) {
			case 0:
				if (selectId != v.getId()) {
					tv_item.setTextColor(context.getResources().getColor(R.color.app_color_white));
					v.setSelected(true);
					selectId = v.getId();
					select_name_1 = tv.getText().toString();
				}else {
					selectId = 0;
					select_name_1 = "";
				}
				select_id_1 = selectId;
				//tv_show.setText(select_name_1);
				break;
			case 1:
				if (selectId != v.getId()) {
					tv_item.setTextColor(context.getResources().getColor(R.color.app_color_white));
					v.setSelected(true);
					selectId = v.getId();
					select_name_2 = tv.getText().toString();
				}else {
					selectId = 0;
					select_name_2 = "";
				}
				select_id_2 = selectId;
				//tv_show.setText(select_name_2);
				break;
		}
	}

	private void defaultViewStatus(View[] views) {
		for (int i = 0; i < views.length; i++) {
			TextView tv_item = (TextView)views[i];
			tv_item.setTextColor(context.getResources().getColor(R.color.ui_color_status));
			views[i].setBackground(context.getResources().getDrawable(R.drawable.shape_style_empty_04_08));
			views[i].setSelected(false);
			views[i].setPadding(pdWidth, pdHeight, pdWidth, pdHeight);
		}
	}

	private String getSelectShowStr(String show1, String show2){
		StringBuilder sb = new StringBuilder();
		if (!StringUtil.isNull(show1)) {
			sb.append("“");
			sb.append(show1);
			sb.append("”");
		}
		if (!StringUtil.isNull(show2)) {
			if (!StringUtil.isNull(show1)) {
				sb.append(" ");
			}
			sb.append("“");
			sb.append(show2);
			sb.append("”");
		}
		return sb.toString();
	}

	public interface ScreenClickCallback {

		void setOnClick(Object entity, int position, int num, double attrPrice,
                        int id1, int id2, String selectName, String selectImg);

	}

}