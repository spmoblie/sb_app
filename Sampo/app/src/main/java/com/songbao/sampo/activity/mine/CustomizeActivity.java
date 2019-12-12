package com.songbao.sampo.activity.mine;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo.AppApplication;
import com.songbao.sampo.AppConfig;
import com.songbao.sampo.R;
import com.songbao.sampo.activity.BaseActivity;
import com.songbao.sampo.activity.common.ViewPagerActivity;
import com.songbao.sampo.adapter.AdapterCallback;
import com.songbao.sampo.adapter.OrderLogisticsAdapter;
import com.songbao.sampo.adapter.OrderProgressAdapter;
import com.songbao.sampo.entity.AddressEntity;
import com.songbao.sampo.entity.DesignerEntity;
import com.songbao.sampo.entity.GoodsEntity;
import com.songbao.sampo.entity.OCustomizeEntity;
import com.songbao.sampo.entity.OLogisticsEntity;
import com.songbao.sampo.entity.OProgressEntity;
import com.songbao.sampo.utils.LogUtil;
import com.songbao.sampo.utils.StringUtil;
import com.songbao.sampo.widgets.ObservableScrollView;
import com.songbao.sampo.widgets.RoundImageView;
import com.songbao.sampo.widgets.ScrollViewListView;

import java.util.ArrayList;

import butterknife.BindView;


public class CustomizeActivity extends BaseActivity implements OnClickListener {

	String TAG = CustomizeActivity.class.getSimpleName();

	@BindView(R.id.customize_sv_main)
	ObservableScrollView sv_main;

	@BindView(R.id.customize_tv_order_number)
	TextView tv_order_number;

	@BindView(R.id.customize_tv_order_status)
	TextView tv_order_status;

	@BindView(R.id.customize_tv_module_1_time)
	TextView tv_1_time;

	@BindView(R.id.customize_iv_module_1_goods_img)
	RoundImageView iv_1_goods_img;

	@BindView(R.id.customize_tv_module_1_goods_name)
	TextView tv_1_goods_name;

	@BindView(R.id.customize_tv_module_1_designer_name)
	TextView tv_1_designer_name;

	@BindView(R.id.customize_tv_module_1_designer_phone)
	TextView tv_1_designer_phone;

	@BindView(R.id.customize_tv_module_1_call_designer)
	TextView tv_1_call_designer;

	@BindView(R.id.customize_iv_module_2_title_sign)
	ImageView iv_2_sign;

	@BindView(R.id.customize_tv_module_2_time)
	TextView tv_2_time;

	@BindView(R.id.customize_tv_module_2_goods_spec)
	TextView tv_2_goods_spec;

	@BindView(R.id.customize_tv_module_2_goods_color)
	TextView tv_2_goods_color;

	@BindView(R.id.customize_tv_module_2_goods_material)
	TextView tv_2_goods_material;

	@BindView(R.id.customize_tv_module_2_goods_veneer)
	TextView tv_2_goods_veneer;

	@BindView(R.id.customize_iv_module_3_title_sign)
	ImageView iv_3_sign;

	@BindView(R.id.customize_tv_module_3_time)
	TextView tv_3_time;

	@BindView(R.id.customize_iv_module_3_show)
	RoundImageView iv_3_show;

	@BindView(R.id.customize_tv_module_3_show)
	TextView tv_3_show;

	@BindView(R.id.customize_tv_module_3_confirm)
	TextView tv_3_confirm;

	@BindView(R.id.customize_iv_module_4_title_sign)
	ImageView iv_4_sign;

	@BindView(R.id.customize_tv_module_4_time)
	TextView tv_4_time;

	@BindView(R.id.customize_tv_module_4_order_price)
	TextView tv_4_order_price;

	@BindView(R.id.customize_tv_module_4_order_day)
	TextView tv_4_order_day;

	@BindView(R.id.customize_tv_module_4_order_pay)
	TextView tv_4_order_pay;

	@BindView(R.id.customize_tv_module_4_go_pay)
	TextView tv_4_go_pay;

	@BindView(R.id.customize_iv_module_5_title_sign)
	ImageView iv_5_sign;

	@BindView(R.id.customize_tv_module_5_time)
	TextView tv_5_time;

	@BindView(R.id.customize_tv_module_5_addressee_name)
	TextView tv_5_addressee_name;

	@BindView(R.id.customize_tv_module_5_addressee_phone)
	TextView tv_5_addressee_phone;

	@BindView(R.id.customize_tv_module_5_address_1)
	TextView tv_5_address_1;

	@BindView(R.id.customize_tv_module_5_address_2)
	TextView tv_5_address_2;

	@BindView(R.id.customize_tv_module_5_select_address)
	TextView tv_5_select_address;

	@BindView(R.id.customize_iv_module_6_title_sign)
	ImageView iv_6_sign;

	@BindView(R.id.customize_tv_module_6_time)
	TextView tv_6_time;

	@BindView(R.id.customize_module_6_lv)
	ScrollViewListView lv_6;

	@BindView(R.id.customize_tv_module_6_lv_open)
	TextView tv_6_lv_open;

	@BindView(R.id.customize_iv_module_7_title_sign)
	ImageView iv_7_sign;

	@BindView(R.id.customize_tv_module_7_time)
	TextView tv_7_time;

	@BindView(R.id.customize_module_7_lv)
	ScrollViewListView lv_7;

	@BindView(R.id.customize_tv_module_7_call_install)
	TextView tv_7_call_install;

	@BindView(R.id.customize_iv_module_8_title_sign)
	ImageView iv_8_sign;

	@BindView(R.id.customize_tv_module_8_time)
	TextView tv_8_time;

	@BindView(R.id.customize_tv_module_8_remind)
	TextView tv_8_remind;

	@BindView(R.id.customize_tv_module_8_install_confirm)
	TextView tv_8_install_confirm;

	@BindView(R.id.customize_iv_module_9_title_sign)
	ImageView iv_9_sign;

	@BindView(R.id.customize_tv_module_9_time)
	TextView tv_9_time;

	@BindView(R.id.customize_tv_module_9_remind)
	TextView tv_9_remind;

	@BindView(R.id.customize_tv_module_9_comment)
	TextView tv_9_comment;

	@BindView(R.id.customize_tv_module_9_post_sale)
	TextView tv_9_post_sale;

	@BindView(R.id.customize_tv_module_2_title)
	TextView tv_2_title;
	@BindView(R.id.customize_tv_module_3_title)
	TextView tv_3_title;
	@BindView(R.id.customize_tv_module_4_title)
	TextView tv_4_title;
	@BindView(R.id.customize_tv_module_5_title)
	TextView tv_5_title;
	@BindView(R.id.customize_tv_module_6_title)
	TextView tv_6_title;
	@BindView(R.id.customize_tv_module_7_title)
	TextView tv_7_title;
	@BindView(R.id.customize_tv_module_8_title)
	TextView tv_8_title;

	private OrderProgressAdapter lv_6_Adapter;
	private OrderLogisticsAdapter lv_7_Adapter;

	private OCustomizeEntity ocEn;
	private Drawable open_up, open_down;

	private boolean isPay; //是否支付
	private boolean isFinish; //是否安装完成
	private boolean isReceipt; //是否确认收货
	private boolean isConfirm; //是否确认效果图
	private boolean isOpenAll; //是否展开生产进度

	private String phone; //设计师联系电话

	private ArrayList<String> al_img = new ArrayList<>();
	private ArrayList<OProgressEntity> al_6 = new ArrayList<>();
	private ArrayList<OProgressEntity> al_6_1 = new ArrayList<>();
	private ArrayList<OProgressEntity> al_6_all = new ArrayList<>();
	private ArrayList<OLogisticsEntity> al_7 = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customize);

		initView();
	}

	private void initView() {
		setTitle(getString(R.string.order_progress));

		tv_2_title.setOnClickListener(this);
		tv_3_title.setOnClickListener(this);
		tv_4_title.setOnClickListener(this);
		tv_5_title.setOnClickListener(this);
		tv_6_title.setOnClickListener(this);
		tv_7_title.setOnClickListener(this);
		tv_8_title.setOnClickListener(this);

		open_up = getResources().getDrawable(R.mipmap.icon_go_up);
		open_down = getResources().getDrawable(R.mipmap.icon_go_down);
		open_up.setBounds(0, 0, open_up.getMinimumWidth(), open_up.getMinimumHeight());
		open_down.setBounds(0, 0, open_down.getMinimumWidth(), open_down.getMinimumHeight());

		initDemoData(1);
	}

	private void initShowData() {
		setRightViewText(getString(R.string.order_cancel));
		if (ocEn != null) {
			tv_order_number.setText("定制编号：" + ocEn.getOrderNo());
			switch (ocEn.getStatus()) {
				case 1: //待付款
					tv_order_status.setText(getString(R.string.order_wait_pay));
					tv_order_status.setBackgroundResource(R.drawable.shape_style_solid_09_04);
					break;
				case 2: //生产中
					tv_order_status.setText(getString(R.string.order_producing));
					tv_order_status.setBackgroundResource(R.drawable.shape_style_solid_10_04);
					break;
				case 3: //待收货
					tv_order_status.setText(getString(R.string.order_wait_receive));
					tv_order_status.setBackgroundResource(R.drawable.shape_style_solid_04_04);
					break;
				case 4: //待安装
					tv_order_status.setText(getString(R.string.order_wait_install));
					tv_order_status.setBackgroundResource(R.drawable.shape_style_solid_06_04);
					break;
				case 5: //待评价
					tv_order_status.setText(getString(R.string.order_completed));
					tv_order_status.setBackgroundResource(R.drawable.shape_style_solid_03_04);
					break;
				case 6: //已完成
					tv_order_status.setText(getString(R.string.order_completed));
					tv_order_status.setBackgroundResource(R.drawable.shape_style_solid_03_04);
					break;
				case 7: //退换货
					tv_order_status.setText(getString(R.string.order_repair_return));
					tv_order_status.setBackgroundResource(R.drawable.shape_style_solid_05_04);
					break;
				case 8: //已取消
					setRightViewText(getString(R.string.order_delete));
					tv_order_status.setText(getString(R.string.order_cancelled));
					tv_order_status.setBackgroundResource(R.drawable.shape_style_solid_03_04);
					break;
			}

			// 提交预约
			tv_1_time.setText(ocEn.getNodeTime1());
			GoodsEntity gdEn = ocEn.getGdEn();
			if (gdEn != null) {
				Glide.with(AppApplication.getAppContext())
						.load(gdEn.getPicUrl())
						.apply(AppApplication.getShowOptions())
						.into(iv_1_goods_img);
				tv_1_goods_name.setText(gdEn.getName());
			}
			DesignerEntity dgEn = ocEn.getDgEn();
			if (dgEn != null) {
				phone = dgEn.getPhone();
				tv_1_designer_phone.setText(phone);
				tv_1_designer_name.setText(dgEn.getName());
				tv_1_call_designer.setOnClickListener(this);
			}

			// 上门量尺
			if (!StringUtil.isNull(ocEn.getNodeTime2())) {
				iv_2_sign.setSelected(true);
				tv_2_time.setText(ocEn.getNodeTime2());
			}
			if (gdEn != null) {
				if (!StringUtil.isNull(gdEn.getAttribute())) {
					tv_2_goods_spec.setVisibility(View.VISIBLE);
					tv_2_goods_spec.setText("产品规格：" + gdEn.getAttribute());
				}
				if (!StringUtil.isNull(gdEn.getColor())) {
					tv_2_goods_color.setVisibility(View.VISIBLE);
					tv_2_goods_color.setText("产品颜色：" + gdEn.getColor());
				}
				if (!StringUtil.isNull(gdEn.getMaterial())) {
					tv_2_goods_material.setVisibility(View.VISIBLE);
					tv_2_goods_material.setText("产品用料：" + gdEn.getMaterial());
				}
				if (!StringUtil.isNull(gdEn.getVeneer())) {
					tv_2_goods_veneer.setVisibility(View.VISIBLE);
					tv_2_goods_veneer.setText("产品饰面：" + gdEn.getVeneer());
				}
			}

			// 设计效果图
			al_img.clear();
			if (ocEn.getImgList() != null) {
				al_img.addAll(ocEn.getImgList());
			}
			if (al_img.size() > 0) {
				iv_3_sign.setSelected(true);
				tv_3_time.setText(ocEn.getNodeTime3());

				iv_3_show.setVisibility(View.VISIBLE);
				Glide.with(AppApplication.getAppContext())
						.load(al_img.get(0))
						.apply(AppApplication.getShowOptions())
						.into(iv_3_show);

				tv_3_show.setVisibility(View.VISIBLE);
				tv_3_show.setText("点击查看" + al_img.size() + "张效果图");
				tv_3_show.setOnClickListener(this);

				isConfirm = ocEn.isConfirm();
				if (isConfirm) { //已确认
					tv_3_confirm.setText("已确认");
					tv_3_confirm.setTextColor(getResources().getColor(R.color.app_color_white));
					tv_3_confirm.setBackgroundResource(R.drawable.shape_style_solid_03_08);
				} else {
					tv_3_confirm.setText("确认效果图");
				}
				tv_3_confirm.setOnClickListener(this);
				tv_3_confirm.setVisibility(View.VISIBLE);
			}

			// 支付订单
			if (!StringUtil.isNull(ocEn.getNodeTime4())) {
				iv_4_sign.setSelected(true);
				tv_4_time.setText(ocEn.getNodeTime4());

			}
			if (ocEn.getPrice() > 0) {
				tv_4_order_price.setVisibility(View.VISIBLE);
				tv_4_order_price.setText("定制产品报价：￥" + ocEn.getPrice());

				isPay = ocEn.isPay();
				if (isPay) { //已支付
					tv_4_go_pay.setText("已支付");
					tv_4_go_pay.setTextColor(getResources().getColor(R.color.app_color_white));
					tv_4_go_pay.setBackgroundResource(R.drawable.shape_style_solid_03_08);

					tv_5_addressee_name.setVisibility(View.VISIBLE);
					tv_5_addressee_phone.setVisibility(View.VISIBLE);
					tv_5_address_1.setVisibility(View.VISIBLE);
					tv_5_address_2.setVisibility(View.VISIBLE);
					tv_5_select_address.setVisibility(View.VISIBLE);
				} else {
					tv_4_go_pay.setText("在线支付");
				}
				tv_4_go_pay.setOnClickListener(this);
				tv_4_go_pay.setVisibility(View.VISIBLE);
			}
			if (ocEn.getCycle() > 0) {
				tv_4_order_day.setVisibility(View.VISIBLE);
				tv_4_order_day.setText("计划生产周期：" + ocEn.getCycle() + "天");
			}
			if (!StringUtil.isNull(ocEn.getPayType())) {
				tv_4_order_pay.setVisibility(View.VISIBLE);
				tv_4_order_pay.setText("订单支付方式：" + ocEn.getPayType());
			}

			// 确认收货信息
			AddressEntity adEn = ocEn.getAdEn();
			if (adEn != null) {
				tv_5_time.setText(ocEn.getNodeTime5());
				tv_5_addressee_name.setText("联系人：" + adEn.getName());
				tv_5_addressee_phone.setText("联系电话：" + adEn.getPhone());
				tv_5_address_2.setText(adEn.getAddress());
				if (StringUtil.isNull(adEn.getName())
						|| StringUtil.isNull(adEn.getPhone())
						|| StringUtil.isNull(adEn.getAddress())) {
					iv_5_sign.setSelected(false);
					tv_5_select_address.setText("选择收货地址");
				} else {
					iv_5_sign.setSelected(true);
					tv_5_select_address.setText("修改收货地址");
				}
				tv_5_select_address.setOnClickListener(this);
			}

			// 生产进度跟踪
			al_6.clear();
			al_6_1.clear();
			al_6_all.clear();
			if (ocEn.getOpList() != null) {
				al_6_all.addAll(ocEn.getOpList());
			}
			if (al_6_all.size() > 0) {
				al_6_1.add(al_6_all.get(0));
				al_6.addAll(al_6_1); //默认显示1条数据

				iv_6_sign.setSelected(true);
				tv_6_time.setText(ocEn.getNodeTime6());
				if (al_6_all.size() > 1) {
					tv_6_lv_open.setOnClickListener(this);
					tv_6_lv_open.setVisibility(View.VISIBLE);
				}
				lv_6.setVisibility(View.VISIBLE);
			}

			// 产品发货
			al_7.clear();
			if (ocEn.getOlList() != null) {
				al_7.addAll(ocEn.getOlList());
			}
			if (al_7.size() > 0) {
				iv_7_sign.setSelected(true);
				tv_7_time.setText(ocEn.getNodeTime7());

				lv_7.setVisibility(View.VISIBLE);
				tv_7_call_install.setOnClickListener(this);
				tv_7_call_install.setVisibility(View.VISIBLE);
				tv_8_remind.setVisibility(View.VISIBLE);
			}

			// 产品安装
			isReceipt = ocEn.isReceipt();
			if (isReceipt) { //已确认收货
				iv_8_sign.setSelected(true);
				tv_8_time.setText(ocEn.getNodeTime8());
				tv_8_remind.setText("安装完成后请确认安装完成，如未确认系统会自签收日起7个自然日后自动确认安装完成。");
				tv_7_call_install.setText("联系安装");
				tv_8_install_confirm.setOnClickListener(this);
				tv_8_install_confirm.setVisibility(View.VISIBLE);
			} else {
				tv_7_call_install.setText("确认收货");
				tv_8_remind.setText("产品签收后请确认收货，确认收货后方可联系上门安装服务。");
			}

			// 订单完成
			isFinish = ocEn.isFinish();
			if (isFinish) {
				iv_9_sign.setSelected(true);
				tv_9_time.setText(ocEn.getNodeTime9());
				tv_8_install_confirm.setText("已安装");
				tv_8_install_confirm.setTextColor(getResources().getColor(R.color.app_color_white));
				tv_8_install_confirm.setBackgroundResource(R.drawable.shape_style_solid_03_08);
				tv_9_remind.setVisibility(View.VISIBLE);
				tv_9_comment.setVisibility(View.VISIBLE);
				tv_9_post_sale.setVisibility(View.VISIBLE);
			} else {
				tv_8_install_confirm.setText("确认安装完成");
				tv_9_remind.setVisibility(View.GONE);
				tv_9_comment.setVisibility(View.GONE);
				tv_9_post_sale.setVisibility(View.GONE);
			}
		}
		initListView();
	}

	private void initListView() {
		//生产进度
		if (lv_6_Adapter == null) {
			lv_6_Adapter = new OrderProgressAdapter(mContext);
			lv_6_Adapter.addCallback(new AdapterCallback() {
				@Override
				public void setOnClick(Object data, int position, int type) {
					if (data != null) {
						if (type == 1) {
							ArrayList<String> imgList = (ArrayList<String>) data;
							openViewPagerActivity(imgList, position);
						}
					}
				}
			});
		}
		lv_6_Adapter.updateData(al_6);
		lv_6.setAdapter(lv_6_Adapter);
		lv_6.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

		//物流单号
		if (lv_7_Adapter == null) {
			lv_7_Adapter = new OrderLogisticsAdapter(mContext);
			lv_7_Adapter.addCallback(new AdapterCallback() {
				@Override
				public void setOnClick(Object data, int position, int type) {

				}
			});
		}
		lv_7_Adapter.updateData(al_7);
		lv_7.setAdapter(lv_7_Adapter);
		lv_7.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.customize_tv_module_1_call_designer:
				//联系Ta
				if (!StringUtil.isNull(phone)) {
					callPhone(phone);
				}
				break;
			case R.id.customize_tv_module_3_show:
				//查看效果图
				openViewPagerActivity(al_img, 0);
				break;
			case R.id.customize_tv_module_3_confirm:
				//确认效果图
				if (!isConfirm) {
					initDemoData(4);
				}
				break;
			case R.id.customize_tv_module_4_go_pay:
				//去支付
				if (!isPay) {
					initDemoData(5);
				}
				break;
			case R.id.customize_tv_module_5_select_address:
				//收货地址
				break;
			case R.id.customize_tv_module_6_lv_open:
				//展开进度
				al_6.clear();
				isOpenAll = !isOpenAll;
				if (isOpenAll) {
					tv_6_lv_open.setText("收起");
					tv_6_lv_open.setCompoundDrawables(null, null, open_up, null);
					al_6.addAll(al_6_all);
				} else {
					tv_6_lv_open.setText("展开");
					tv_6_lv_open.setCompoundDrawables(null, null, open_down, null);
					al_6.addAll(al_6_1);
				}
				initListView();
				break;
			case R.id.customize_tv_module_7_call_install:
				//确认收货or联系安装
				if (!isReceipt) {
					initDemoData(8);
				} else {
					if (!StringUtil.isNull(phone)) {
						callPhone(phone);
					}
				}
				break;
			case R.id.customize_tv_module_8_install_confirm:
				//确认安装完成
				if (!isFinish) {
					initDemoData(9);
				}
				break;
			case R.id.customize_tv_module_2_title:
				initDemoData(2);
				break;
			case R.id.customize_tv_module_3_title:
				initDemoData(3);
				break;
			case R.id.customize_tv_module_4_title:
				initDemoData(4);
				break;
			case R.id.customize_tv_module_5_title:
				initDemoData(5);
				break;
			case R.id.customize_tv_module_6_title:
				initDemoData(6);
				break;
			case R.id.customize_tv_module_7_title:
				initDemoData(7);
				break;
		}
	}

	/**
	 * 打开图片查看器
	 * @param urlLists
	 * @param position
	 */
	private void openViewPagerActivity(ArrayList<String> urlLists, int position) {
		Intent intent = new Intent(mContext, ViewPagerActivity.class);
		intent.putExtra(ViewPagerActivity.EXTRA_IMAGE_URLS, urlLists);
		intent.putExtra(ViewPagerActivity.EXTRA_IMAGE_INDEX, position);
		startActivity(intent);
	}

	@Override
	protected void OnListenerRight() {

	}

	@Override
	protected void onResume() {
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);

		super.onResume();
	}
	
	@Override
	protected void onPause() {
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onPause");
		// 页面结束
		AppApplication.onPageEnd(this, TAG);

		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void initDemoData(int code) {

		ocEn = new OCustomizeEntity();

		ocEn.setId(6);
		ocEn.setOrderNo("35461211011241");
		if (code < 5) {
			ocEn.setStatus(1);
		} else if (code == 5 || code == 6){
			ocEn.setStatus(2);
		} else if (code == 7){
			ocEn.setStatus(3);
		} else if (code == 8){
			ocEn.setStatus(4);
		} else if (code == 9){
			ocEn.setStatus(6);
		}
		if (code > 3) {
			ocEn.setCycle(30);
			ocEn.setPrice(1000);
			if (code > 4) {
				ocEn.setPay(true);
				ocEn.setPayType("微信支付");
			}
			ocEn.setConfirm(true);
		}
		if (code > 7) {
			ocEn.setReceipt(true);
		}
		if (code == 9) {
			ocEn.setFinish(true);
		}
		ocEn.setNodeTime1("2019/11/11  19：25");
		if (code > 1) {
			ocEn.setNodeTime2("2019/11/12  12：11");
		}
		ocEn.setNodeTime3("2019/11/13  08：55");
		if (code > 3) {
			ocEn.setNodeTime4("2019/11/13  16：25");
		}
		if (code > 4) {
			ocEn.setNodeTime5("2019/11/25  11：13");
		}
		ocEn.setNodeTime6("2019/11/13  16：26");
		ocEn.setNodeTime7("2019/11/25  18：22");
		ocEn.setNodeTime9("2019/11/30  14：22");
		ocEn.setNodeTime8("2019/11/28  12：05");

		//商品信息
		GoodsEntity gdEn = new GoodsEntity();
		gdEn.setName("松堡王国运动女孩双层床");
		gdEn.setPicUrl("");
		if (code > 1) {
			gdEn.setAttribute("2030*1372*1870mm");
			gdEn.setColor("水洗白+芭比粉");
			gdEn.setMaterial("北欧松木");
			gdEn.setVeneer("环保水性漆涂层");
		}
		ocEn.setGdEn(gdEn);

		//设计师
		DesignerEntity dgEn = new DesignerEntity();
		dgEn.setName("史蒂芬");
		dgEn.setPhone("18888880088");
		ocEn.setDgEn(dgEn);

		//收货地址
		AddressEntity adEn = new AddressEntity();
		adEn.setName("张先生");
		adEn.setPhone("16999666699");
		adEn.setAddress("广东省深圳市南山区粤海街道科发路大冲城市花园5栋16B");
		if (code > 4) {
			ocEn.setAdEn(adEn);
		}

		//效果图集
		ArrayList<String> imgList = new ArrayList<>();
		imgList.add(AppConfig.IMAGE_URL + "banner_001.png");
		imgList.add(AppConfig.IMAGE_URL + "banner_002.png");
		imgList.add(AppConfig.IMAGE_URL + "banner_003.png");
		imgList.add(AppConfig.IMAGE_URL + "banner_004.png");
		imgList.add(AppConfig.IMAGE_URL + "banner_005.png");
		if (code > 2) {
			ocEn.setImgList(imgList);
		}

		//生产进度
		OProgressEntity opEn;
		ArrayList<OProgressEntity> opList = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			opEn = new OProgressEntity();
			opEn.setId(i+1);
			opEn.setAddTime("2019/11/14  8：0" + i);

			switch (i) {
				case 0:
					opEn.setContent("木材备料，即将送往生产线");
					break;
				case 1:
					opEn.setType(1);
					ArrayList<String> imgLs = new ArrayList<>();
					imgLs.add(AppConfig.IMAGE_URL + "banner_001.png");
					imgLs.add(AppConfig.IMAGE_URL + "banner_002.png");
					imgLs.add(AppConfig.IMAGE_URL + "banner_003.png");
					imgLs.add(AppConfig.IMAGE_URL + "banner_004.png");
					imgLs.add(AppConfig.IMAGE_URL + "banner_005.png");
					opEn.setImgList(imgLs);
					break;
				case 2:
					opEn.setContent("产品生产完成，即将打包入库");
					break;
				case 3:
					opEn.setContent("产品已入库");
					break;
				case 4:
					opEn.setContent("产品已出库");
					break;
			}
			opList.add(opEn);
		}
		if (code > 5) {
			ocEn.setOpList(opList);
		}

		//物流单号
		OLogisticsEntity olEn;
		ArrayList<OLogisticsEntity> olList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			olEn = new OLogisticsEntity();
			olEn.setId(i+1);
			switch (i) {
				case 0:
					olEn.setName("德邦物流");
					olEn.setNumber("DB5412210021456");
					break;
				case 1:
					olEn.setName("顺丰物流");
					olEn.setNumber("SF5412210021456");
					break;
				case 2:
					olEn.setName("京东物流");
					olEn.setNumber("JD5412210021456");
					break;
			}
			olList.add(olEn);
		}
		if (code > 6) {
			ocEn.setOlList(olList);
		}

		initShowData();
	}

}
