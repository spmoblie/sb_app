package com.songbao.sampo_c.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.entity.GoodsAttrEntity;
import com.songbao.sampo_c.utils.CommonTools;
import com.songbao.sampo_c.utils.StringUtil;
import com.songbao.sampo_c.widgets.PriceTextWatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 商品筛选RecyclerView适配器
 */
public class GoodsScreenAdapter extends BaseRecyclerAdapter {

	private final int viewWidth = AppApplication.screen_width * 4 / 5;

	private Context context;
	private HashMap<Integer, GoodsAttrEntity> attrHashMap = new HashMap<>();
	private ScreenClickCallback scCallback;
	private int txtSize, pdTop, pdRight, mgTop, mgRight, mgDps, tvSpec;
	private long minPrice, maxPrice;

	public GoodsScreenAdapter(Context context, List<Integer> resLayout, ScreenClickCallback callback) {
		super(context, resLayout);
		this.context = context;
		this.scCallback = callback;

		txtSize = 12;
		mgTop =  CommonTools.dpToPx(context, 10); //外边距:上下
		mgRight =  CommonTools.dpToPx(context, 10); //外边距:左右
		pdTop = CommonTools.dpToPx(context, 7); //内边距:上下
		pdRight = CommonTools.dpToPx(context, 10); //内边距:左右
		mgDps = CommonTools.dpToPx(context, 40); //父布局左右边距之和
		tvSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
	}

	/**
	 * 刷新数据
	 */
	public void updateData(GoodsAttrEntity attrEn, long minPrice, long maxPrice) {
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
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
			EditText et_price_min = holder.getView(R.id.screen_item_2_tv_attr_min);
			EditText et_price_max = holder.getView(R.id.screen_item_2_tv_attr_max);
			final TextView tv_all = holder.getView(R.id.screen_item_2_tv_attr_all);

			et_price_min.addTextChangedListener(new PriceTextWatcher(et_price_min, new PriceTextWatcher.AfterCallback() {
				@Override
				public void afterTextChanged(Editable s) {
					String priceStr = s.toString();
					if (!StringUtil.isNull(priceStr)) {
						minPrice = Integer.valueOf(priceStr);
					} else {
						minPrice = 0;
					}
					if (minPrice <= 0 && maxPrice <= 0) {
						tv_all.setSelected(true);
						tv_all.setTextColor(context.getResources().getColor(R.color.app_color_white));
					} else {
						if (tv_all.isSelected()) {
							tv_all.setSelected(false);
							tv_all.setTextColor(context.getResources().getColor(R.color.app_color_gray_5));
						}
					}
					if (scCallback != null) {
						scCallback.updatePrice(minPrice, maxPrice, -1);
					}
				}
			}));
			et_price_max.addTextChangedListener(new PriceTextWatcher(et_price_max, new PriceTextWatcher.AfterCallback() {
				@Override
				public void afterTextChanged(Editable s) {
					String priceStr = s.toString();
					if (!StringUtil.isNull(priceStr)) {
						maxPrice = Integer.valueOf(priceStr);
					} else {
						maxPrice = 0;
					}
					if (minPrice <= 0 && maxPrice <= 0) {
						tv_all.setSelected(true);
						tv_all.setTextColor(context.getResources().getColor(R.color.app_color_white));
					} else {
						if (tv_all.isSelected()) {
							tv_all.setSelected(false);
							tv_all.setTextColor(context.getResources().getColor(R.color.app_color_gray_5));
						}
					}
					if (scCallback != null) {
						scCallback.updatePrice(minPrice, maxPrice, -1);
					}
				}
			}));

			if (minPrice <= 0) {
				et_price_min.setText("");
			} else {
				et_price_min.setText(String.valueOf(minPrice));
			}
			if (maxPrice <= 0) {
				et_price_max.setText("");
			} else {
				et_price_max.setText(String.valueOf(maxPrice));
			}
			if (minPrice <= 0 && maxPrice <= 0) {
				tv_all.setSelected(true);
				tv_all.setTextColor(context.getResources().getColor(R.color.app_color_white));
			} else {
				tv_all.setSelected(false);
				tv_all.setTextColor(context.getResources().getColor(R.color.app_color_gray_5));
			}
			tv_all.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					minPrice = 0;
					maxPrice = 0;
					if (scCallback != null) {
						scCallback.updatePrice(minPrice, maxPrice, 1);
					}
				}
			});
		} else {
			// 获取View
			TextView tv_name = holder.getView(R.id.screen_item_1_tv_attr_name);
			ImageView iv_show = holder.getView(R.id.screen_item_1_iv_attr_show);
			RelativeLayout rl_main = holder.getView(R.id.screen_item_rl_attr_main);
			if (data != null) {
				tv_name.setId(data.getAttrId());
				tv_name.setText(data.getAttrName());

				rl_main.removeAllViews();
				addAttributeView(rl_main, data, pos);
			}

			if (data.isShow()) {
				rl_main.setVisibility(View.VISIBLE);
			} else {
				rl_main.setVisibility(View.GONE);
			}
			iv_show.setSelected(data.isShow());
			iv_show.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (scCallback != null) {
						scCallback.setOnClick(data, pos, pos, 1);
					}
				}
			});
			tv_name.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (scCallback != null) {
						scCallback.setOnClick(data, pos, pos, 1);
					}
				}
			});
		}
	}

	/**
	 * 动态添加View
	 */
	private void addAttributeView(RelativeLayout rl_main, final GoodsAttrEntity data, final int pos) {
		// 添加属性类别名称
		/*int attrId = data.getAttrId();
		TextView tv_name = new TextView(context);
		tv_name.setText(data.getAttrName() + ":");
		tv_name.setTextColor(context.getResources().getColor(R.color.shows_text_color));
		tv_name.setTextSize(txtSize);
		tv_name.setId(attrId);
		rl_main.addView(tv_name);*/

		ArrayList<GoodsAttrEntity> attrLists = data.getAttrLists();
		if (attrLists == null || attrLists.size() == 0) {
			return;
		}
		// 循环添加属性View
		int widthTotal = mgDps;
		int tvWidth;
		int viewId;
		int firstId = 0;
		int beforeId = 0;
		String str;

		for (int i = 0; i < attrLists.size(); i++) {
			final int index = i;
			final GoodsAttrEntity attrEn = attrLists.get(i);
			str = attrEn.getAttrName();
			viewId = attrEn.getAttrId();
			if (i > 0) {
				beforeId = attrLists.get(i-1).getAttrId();
			}
			TextView tv = new TextView(context);
			attrHashMap.put(viewId, attrEn);
			tv.setPadding(pdRight, pdTop, pdRight, pdTop);
			tv.setGravity(Gravity.CENTER);
			tv.setId(viewId);
			tv.setText(str);
			tv.setTextSize(txtSize);
			tv.setLines(1);
			tv.setEllipsize(TextUtils.TruncateAt.END);
			tv.setBackgroundResource(R.drawable.selector_item_goods_attr);
			tv.setSelected(attrEn.isSelect());
			if (tv.isSelected()) {
				tv.setTextColor(context.getResources().getColor(R.color.app_color_white));
			} else {
				tv.setTextColor(context.getResources().getColor(R.color.app_color_gray_5));
			}
			tv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (scCallback != null) {
						scCallback.setOnClick(attrEn, pos, index, 0);
					}
				}

			});

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

	public interface ScreenClickCallback {

		void updatePrice(long minPrice, long maxPrice, int typeCode);

		void setOnClick(GoodsAttrEntity data, int position, int index, int typeCode);

	}

}