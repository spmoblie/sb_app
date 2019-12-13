package com.songbao.sampo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
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
import com.songbao.sampo.utils.CommonTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 商品属性面板RecyclerView适配器
 */
@SuppressLint({ "UseSparseArrays" })
public class GoodsAttrAdapter extends BaseRecyclerAdapter {

	private final int viewWidth = AppApplication.screen_width;

	private AttrClickCallback callback;
	private int skuNum = 99;
	private int buyNumber = 1;
	private int txtSize, pdTop, pdRight, mgTop, mgRight, mgDps, tvSpec;
	private int select_id_1, select_id_2, select_id_3;
	private View[] views_1, views_2, views_3;
	private String select_name_1, select_name_2, select_name_3;
	private HashMap<String, Integer> skuHashMap = new HashMap<>();
	private HashMap<Integer, GoodsAttrEntity> attrHashMap = new HashMap<>();

	public GoodsAttrAdapter(Context context, List<Integer> resLayout, AttrClickCallback callback) {
		super(context, resLayout);
		this.callback = callback;

		txtSize = 13;
		mgTop =  CommonTools.dpToPx(context, 15);
		mgRight =  CommonTools.dpToPx(context, 20);
		pdTop = CommonTools.dpToPx(context, 7);
		pdRight = CommonTools.dpToPx(context, 10);
		mgDps = CommonTools.dpToPx(context, 0);
		tvSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
	}

	/**
	 * 刷新数据
	 */
	public void updateData(GoodsAttrEntity attrEn, GoodsAttrEntity secEn) {
		if (secEn != null) {
			buyNumber = secEn.getBuyNum();
			select_id_1 = secEn.getS_id_1();
			select_id_2 = secEn.getS_id_2();
			select_id_3 = secEn.getS_id_3();
		}
		updateData(attrEn.getAttrLists());
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
		if (pos == getItemCount() - 1) { //价格
			// 获取View
			ImageView iv_num_minus = holder.getView(R.id.attr_item_2_iv_num_minus);
			ImageView iv_num_add = holder.getView(R.id.attr_item_2_iv_num_add);
			final TextView tv_number = holder.getView(R.id.attr_item_2_tv_number);
			tv_number.setText(String.valueOf(buyNumber));

			iv_num_minus.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (skuNum > 0) {
						buyNumber--;
						if (buyNumber < 1) {
							buyNumber = 1;
						}
					} else {
						buyNumber = 0;
					}
					if (callback != null) {
						callback.updateNumber(buyNumber);
					}
					tv_number.setText(String.valueOf(buyNumber));
				}
			});
			iv_num_add.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (skuNum > 0) {
						buyNumber++;
						if (buyNumber > skuNum) {
							buyNumber = skuNum;
						}
					} else {
						buyNumber = 0;
					}
					if (callback != null) {
						callback.updateNumber(buyNumber);
					}
					tv_number.setText(String.valueOf(buyNumber));
				}
			});
		} else {
			// 获取View
			TextView tv_name = holder.getView(R.id.attr_item_1_tv_attr_name);
			RelativeLayout rl_main = holder.getView(R.id.attr_item_1_rl_attr_main);

			if (data != null) {
				tv_name.setId(data.getAttrId());
				tv_name.setText(data.getAttrName());

				rl_main.removeAllViews();
				addAttributeView(rl_main, data, pos);
			}
		}
	}

	/**
	 * 动态添加View
	 */
	private void addAttributeView(RelativeLayout rl_main, final GoodsAttrEntity data, final int position) {
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

		int size = nameLists.size();
		switch (position) {
			case 0:
				views_1 = new View[size];
				break;
			case 1:
				views_2 = new View[size];
				break;
			case 2:
				views_3 = new View[size];
				break;
		}

		for (int i = 0; i < size; i++) {
			str = nameLists.get(i).getAttrName();
			viewId = nameLists.get(i).getAttrId();
			if (i > 0) {
				beforeId = nameLists.get(i-1).getAttrId();
			}
			TextView tv = new TextView(context);
			// 判定库存数
			int skuNum = nameLists.get(i).getSkuNum();
			if (skuNum > 0) {
				if (viewId == select_id_1 || viewId == select_id_2 || viewId == select_id_3) {
					tv.setTextColor(context.getResources().getColor(R.color.app_color_white));
					tv.setBackground(context.getResources().getDrawable(R.drawable.shape_style_solid_04_08));
				} else {
					tv.setTextColor(context.getResources().getColor(R.color.app_color_gray_5));
					tv.setBackground(context.getResources().getDrawable(R.drawable.shape_style_solid_02_08));
				}
			}else {
				tv.setTextColor(context.getResources().getColor(R.color.debar_text_color));
				tv.setBackground(context.getResources().getDrawable(R.drawable.shape_style_empty_02_08));
			}
			// 记录库存数
			skuHashMap.put(String.valueOf(viewId), skuNum);
			attrHashMap.put(viewId, nameLists.get(i));
			tv.setPadding(pdRight, pdTop, pdRight, pdTop);
			tv.setGravity(Gravity.CENTER);
			tv.setText(str);
			tv.setTextSize(txtSize);
			tv.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
			tv.setId(viewId);
			tv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (getSkuNum(String.valueOf(v.getId())) <= 0) return;
					switch (position) {
						case 0:
							defaultViewStatus(views_1);
							changeSelectStatus(v, position, select_id_1);
							if (callback != null) {
								callback.setOnClick(data, position, getAttrPrice(select_id_1), select_id_1, select_name_1, getAttrImage(select_id_1));
							}
							break;
						case 1:
							defaultViewStatus(views_2);
							changeSelectStatus(v, position, select_id_2);
							if (callback != null) {
								callback.setOnClick(data, position, getAttrPrice(select_id_2), select_id_2, select_name_2, getAttrImage(select_id_2));
							}
							break;
						case 2:
							defaultViewStatus(views_3);
							changeSelectStatus(v, position, select_id_3);
							if (callback != null) {
								callback.setOnClick(data, position, getAttrPrice(select_id_3), select_id_3, select_name_3, getAttrImage(select_id_3));
							}
							break;
					}
				}

			});
			switch (position) {
				case 0:
					views_1[i] = tv;
					break;
				case 1:
					views_2[i] = tv;
					break;
				case 2:
					views_3[i] = tv;
					break;
			}

			// 计算TextView的宽度
			tv.measure(tvSpec, tvSpec);
			tvWidth = tv.getMeasuredWidth() + 2 + mgRight; //view宽+边框+右外边距
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
			params.setMargins(0, mgTop, mgRight, 0);
			rl_main.addView(tv,params);
		}
	}

	/**
	 * 恢复默认状态
	 */
	private void defaultViewStatus(View[] views) {
		int num;
		for (int i = 0; i < views.length; i++) {
			TextView tv_item = (TextView)views[i];
			num = getSkuNum(String.valueOf(views[i].getId()));
			if (num > 0) {
				tv_item.setTextColor(context.getResources().getColor(R.color.app_color_gray_5));
				views[i].setBackground(context.getResources().getDrawable(R.drawable.shape_style_solid_02_08));
				views[i].setSelected(false);
			}else {
				tv_item.setTextColor(context.getResources().getColor(R.color.debar_text_color));
				views[i].setBackground(context.getResources().getDrawable(R.drawable.shape_style_empty_02_08));
			}
			views[i].setPadding(pdRight, pdTop, pdRight, pdTop);
		}
	}

	/**
	 * 切换选中属性项的状态
	 */
	private void changeSelectStatus(View v, int position, int selectId) {
		TextView tv_item = (TextView) v;
		String selectName;

		if (selectId != v.getId()) {
			tv_item.setTextColor(context.getResources().getColor(R.color.app_color_white));
			tv_item.setBackground(context.getResources().getDrawable(R.drawable.shape_style_solid_04_08));
			selectId = v.getId();
			selectName = tv_item.getText().toString();
		}else {
			selectId = 0;
			selectName = "";
		}

		switch (position) {
			case 0:
				select_id_1 = selectId;
				select_name_1 = selectName;
				break;
			case 1:
				select_id_2 = selectId;
				select_name_2 = selectName;
				break;
			case 2:
				select_id_3 = selectId;
				select_name_3 = selectName;
				break;
		}
	}

	/**
	 * 初始化属性组合Sku
	 */
	private void initAttrMakeSku(GoodsAttrEntity attrEn) {
		if (attrEn != null) {
			if (attrEn.getSkuLists() != null) {
				skuHashMap.clear();
				GoodsAttrEntity sku;
				for (int i = 0; i < attrEn.getSkuLists().size(); i++) {
					sku = attrEn.getSkuLists().get(i);
					if (sku != null) {
						skuHashMap.put(sku.getSku_key(), sku.getSku_value());
					}
				}
			}
		}
	}

	/**
	 * 属性Sku
	 */
	private int getSkuNum(String keyStr) {
		if (skuHashMap.containsKey(keyStr)) {
			return skuHashMap.get(keyStr);
		}
		return 99;
	}

	/**
	 * 属性价格
	 */
	private double getAttrPrice(int key) {
		if (attrHashMap.containsKey(key)) {
			return attrHashMap.get(key).getAttrPrice();
		}
		return 0;
	}

	/**
	 * 属性图片
	 */
	private String getAttrImage(int key) {
		if (attrHashMap.containsKey(key)) {
			return attrHashMap.get(key).getAttrImg();
		}
		return "";
	}

	public interface AttrClickCallback {

		void updateNumber(int number);

		void setOnClick(GoodsAttrEntity data, int pos, double attrPrice, int selectId, String selectName, String selectImg);

	}

}