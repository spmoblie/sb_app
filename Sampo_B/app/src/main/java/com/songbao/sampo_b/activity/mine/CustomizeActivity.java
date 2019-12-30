package com.songbao.sampo_b.activity.mine;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.activity.BaseActivity;
import com.songbao.sampo_b.adapter.AdapterCallback;
import com.songbao.sampo_b.adapter.OrderLogisticsAdapter;
import com.songbao.sampo_b.adapter.OrderProgressAdapter;
import com.songbao.sampo_b.entity.AddressEntity;
import com.songbao.sampo_b.entity.BaseEntity;
import com.songbao.sampo_b.entity.DesignerEntity;
import com.songbao.sampo_b.entity.GoodsEntity;
import com.songbao.sampo_b.entity.OCustomizeEntity;
import com.songbao.sampo_b.entity.OLogisticsEntity;
import com.songbao.sampo_b.entity.OProgressEntity;
import com.songbao.sampo_b.utils.ExceptionUtil;
import com.songbao.sampo_b.utils.JsonUtils;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.utils.StringUtil;
import com.songbao.sampo_b.utils.retrofit.HttpRequests;
import com.songbao.sampo_b.widgets.ObservableScrollView;
import com.songbao.sampo_b.widgets.RoundImageView;
import com.songbao.sampo_b.widgets.ScrollViewListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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
	private String orderNo; //订单编号

	private ArrayList<String> al_img = new ArrayList<>();
	private ArrayList<OProgressEntity> al_6 = new ArrayList<>();
	private ArrayList<OProgressEntity> al_6_1 = new ArrayList<>();
	private ArrayList<OProgressEntity> al_6_all = new ArrayList<>();
	private ArrayList<OLogisticsEntity> al_7 = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customize);

		ocEn = (OCustomizeEntity) getIntent().getSerializableExtra(AppConfig.PAGE_DATA);

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

		if (ocEn != null) {
			orderNo = ocEn.getOrderNo();
			loadOrderData();
		}
	}

	private void initShowData() {
		if (ocEn != null) {
			tv_order_number.setText(getString(R.string.order_booking_no, ocEn.getOrderNo()));
			switch (ocEn.getStatus()) {
				case AppConfig.ORDER_STATUS_101: //待付款
					setRightViewText(getString(R.string.order_cancel));
					tv_order_status.setText(getString(R.string.order_wait_pay));
					tv_order_status.setBackgroundResource(R.drawable.shape_style_solid_09_04);
					break;
				case AppConfig.ORDER_STATUS_201: //生产中
					tv_order_status.setText(getString(R.string.order_producing));
					tv_order_status.setBackgroundResource(R.drawable.shape_style_solid_10_04);
					break;
				case AppConfig.ORDER_STATUS_301: //待发货
					tv_order_status.setText(getString(R.string.order_wait_send));
					tv_order_status.setBackgroundResource(R.drawable.shape_style_solid_07_04);
					break;
				case AppConfig.ORDER_STATUS_401: //待收货
					tv_order_status.setText(getString(R.string.order_wait_receive));
					tv_order_status.setBackgroundResource(R.drawable.shape_style_solid_04_04);
					break;
				case AppConfig.ORDER_STATUS_501: //已签收
				case AppConfig.ORDER_STATUS_701: //待安装
					tv_order_status.setText(getString(R.string.order_wait_install));
					tv_order_status.setBackgroundResource(R.drawable.shape_style_solid_08_04);
					break;
				case AppConfig.ORDER_STATUS_801: //已完成
					setRightViewText(getString(R.string.order_delete));
					tv_order_status.setText(getString(R.string.order_completed));
					tv_order_status.setBackgroundResource(R.drawable.shape_style_solid_03_04);
					break;
				case AppConfig.ORDER_STATUS_102: //已取消
					setRightViewText(getString(R.string.order_delete));
					tv_order_status.setText(getString(R.string.order_cancelled));
					tv_order_status.setBackgroundResource(R.drawable.shape_style_solid_03_04);
					break;
			}

			int nodeNo = ocEn.getNodeNo();
			if (nodeNo > 0) { //提交预约
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
			}

			if (nodeNo > 1) { //上门量尺
				iv_2_sign.setSelected(true);
				tv_2_time.setText(ocEn.getNodeTime2());
				GoodsEntity gdEn = ocEn.getGdEn();
				if (gdEn != null) {
					if (!StringUtil.isNull(gdEn.getAttribute())) {
						tv_2_goods_spec.setVisibility(View.VISIBLE);
						tv_2_goods_spec.setText(getString(R.string.order_attr_show, gdEn.getAttribute()));
					}
					if (!StringUtil.isNull(gdEn.getColor())) {
						tv_2_goods_color.setVisibility(View.VISIBLE);
						tv_2_goods_color.setText(getString(R.string.order_color_show, gdEn.getColor()));
					}
					if (!StringUtil.isNull(gdEn.getMaterial())) {
						tv_2_goods_material.setVisibility(View.VISIBLE);
						tv_2_goods_material.setText(getString(R.string.order_material_show, gdEn.getMaterial()));
					}
					if (!StringUtil.isNull(gdEn.getVeneer())) {
						tv_2_goods_veneer.setVisibility(View.VISIBLE);
						tv_2_goods_veneer.setText(getString(R.string.order_veneer_show, gdEn.getVeneer()));
					}
				}
			}

			if (nodeNo > 2) { //设计效果图
				iv_3_sign.setSelected(true);
				tv_3_time.setText(ocEn.getNodeTime3());
				al_img.clear();
				if (ocEn.getImgList() != null) {
					al_img.addAll(ocEn.getImgList());
				}
				if (al_img.size() > 0) {
					iv_3_show.setVisibility(View.VISIBLE);
					Glide.with(AppApplication.getAppContext())
							.load(al_img.get(0))
							.apply(AppApplication.getShowOptions())
							.into(iv_3_show);

					tv_3_show.setVisibility(View.VISIBLE);
					tv_3_show.setText(getString(R.string.order_image_number, al_img.size()));
					tv_3_show.setOnClickListener(this);

					isConfirm = ocEn.isConfirm();
					if (isConfirm) {
						tv_3_confirm.setText(getString(R.string.order_confirmed));
					} else {
						tv_3_confirm.setText(getString(R.string.order_confirm_image));
					}
					tv_3_confirm.setOnClickListener(this);
					tv_3_confirm.setVisibility(View.VISIBLE);
				}
			}

			if (nodeNo > 3) { //支付信息
				iv_4_sign.setSelected(true);
				tv_4_time.setText(ocEn.getNodeTime4());
				if (ocEn.getPrice() > 0) {
					tv_4_order_price.setVisibility(View.VISIBLE);
					tv_4_order_price.setText(getString(R.string.order_price_offer, df.format(ocEn.getPrice())));

					isPay = ocEn.isPay();
					if (isPay) { //已支付
						tv_4_go_pay.setText(getString(R.string.pay_ok));
					} else {
						tv_4_go_pay.setText(getString(R.string.pay_confirm));
					}
					tv_4_go_pay.setOnClickListener(this);
					tv_4_go_pay.setVisibility(View.VISIBLE);
				}
				if (ocEn.getCycle() > 0) {
					tv_4_order_day.setVisibility(View.VISIBLE);
					tv_4_order_day.setText(getString(R.string.order_cycle_day, ocEn.getCycle()));
				}
			}

			if (nodeNo > 4) { //确认收货信息
				tv_5_addressee_name.setVisibility(View.VISIBLE);
				tv_5_addressee_phone.setVisibility(View.VISIBLE);
				tv_5_address_1.setVisibility(View.VISIBLE);
				tv_5_address_2.setVisibility(View.VISIBLE);
				tv_5_select_address.setVisibility(View.VISIBLE);
				AddressEntity adEn = ocEn.getAdEn();
				if (adEn != null) {
					tv_5_time.setText(ocEn.getNodeTime5());
					tv_5_addressee_name.setText(getString(R.string.address_contacts, adEn.getName()));
					tv_5_addressee_phone.setText(getString(R.string.address_phone, adEn.getPhone()));
					tv_5_address_2.setText(adEn.getAddress());
					if (StringUtil.isNull(adEn.getName())
							|| StringUtil.isNull(adEn.getPhone())
							|| StringUtil.isNull(adEn.getAddress())) {
						iv_5_sign.setSelected(false);
						tv_5_select_address.setText(getString(R.string.address_select));
					} else {
						iv_5_sign.setSelected(true);
						tv_5_select_address.setText(getString(R.string.address_change));
					}
					tv_5_select_address.setOnClickListener(this);
				}
			}

			if (nodeNo > 5) { //生产进度跟踪
				iv_6_sign.setSelected(true);
				al_6.clear();
				al_6_1.clear();
				al_6_all.clear();
				if (ocEn.getOpList() != null) {
					al_6_all.addAll(ocEn.getOpList());
				}
				if (al_6_all.size() > 0) {
					al_6_1.add(al_6_all.get(0));
					al_6.addAll(al_6_1); //默认显示1条数据

					tv_6_time.setText(al_6_all.get(al_6_all.size()-1).getAddTime());
					if (al_6_all.size() > 1) {
						tv_6_lv_open.setOnClickListener(this);
						tv_6_lv_open.setVisibility(View.VISIBLE);
					}
					lv_6.setVisibility(View.VISIBLE);
				}
			}

			if (nodeNo > 6) { //产品发货
				iv_7_sign.setSelected(true);
				tv_7_time.setText(ocEn.getNodeTime7());
				al_7.clear();
				if (ocEn.getOlList() != null) {
					al_7.addAll(ocEn.getOlList());
				}
				if (al_7.size() > 0) {
					lv_7.setVisibility(View.VISIBLE);
				}
				isReceipt = ocEn.isReceipt();
				if (isReceipt) { //已确认安装
					tv_7_call_install.setText(getString(R.string.order_confirmed));
					tv_8_install_confirm.setOnClickListener(this);
					tv_8_install_confirm.setVisibility(View.VISIBLE);
					tv_8_remind.setText(getString(R.string.order_install_hint));
				} else {
					tv_7_call_install.setOnClickListener(this);
					tv_7_call_install.setText(getString(R.string.order_confirm_receipt));
					tv_8_remind.setText(getString(R.string.order_receipt_hint));
				}
				tv_7_call_install.setVisibility(View.VISIBLE);
				tv_8_remind.setVisibility(View.VISIBLE);
			}

			if (nodeNo > 7) { //产品安装
				iv_8_sign.setSelected(true);
				tv_8_time.setText(ocEn.getNodeTime8());
				isFinish = ocEn.isFinish();
				if (isFinish) { //已确认安装
					tv_8_install_confirm.setText("已安装");
				} else {
					tv_8_install_confirm.setText("确认安装完成");
					tv_8_install_confirm.setOnClickListener(this);
				}
			}

			if (nodeNo > 8) { //订单完成
				iv_9_sign.setSelected(true);
				tv_9_time.setText(ocEn.getNodeTime9());
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

	/**
	 * 加载订单详情数据
	 */
	private void loadOrderData() {
		HashMap<String, String> map = new HashMap<>();
		map.put("bookingCode", orderNo);
		loadSVData(AppConfig.BASE_URL_3, AppConfig.URL_BOOKING_INFO, map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_BOOKING_INFO);
	}

	@Override
	protected void callbackData(JSONObject jsonObject, int dataType) {
		super.callbackData(jsonObject, dataType);
		BaseEntity baseEn;
		try {
			switch (dataType) {
				case AppConfig.REQUEST_SV_BOOKING_INFO:
					baseEn = JsonUtils.getCustomizeDetailData(jsonObject);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						ocEn = (OCustomizeEntity) baseEn.getData();
						initShowData();
					} else {
						handleErrorCode(baseEn);
					}
					break;
			}
		} catch (Exception e) {
			loadFailHandle();
			ExceptionUtil.handle(e);
		}
	}

	@Override
	protected void loadFailHandle() {
		super.loadFailHandle();
		handleErrorCode(null);
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
		ocEn.setNodeTime1("2019-12-14 10:18");
		if (code > 1) {
			ocEn.setNodeTime2("2019-12-14 10:18");
		}
		ocEn.setNodeTime3("2019-12-14 10:18");
		if (code > 3) {
			ocEn.setNodeTime4("2019-12-14 10:18");
		}
		if (code > 4) {
			ocEn.setNodeTime5("2019-12-14 10:18");
		}
		ocEn.setNodeTime6("2019-12-14 10:18");
		ocEn.setNodeTime7("2019-12-14 10:18");
		ocEn.setNodeTime9("2019-12-14 10:18");
		ocEn.setNodeTime8("2019-12-14 10:18");

		//商品信息
		GoodsEntity gdEn = new GoodsEntity();
		gdEn.setName("松堡王国运动男孩双层床");
		gdEn.setPicUrl(AppConfig.IMAGE_URL + "design_001.png");
		if (code > 1) {
			gdEn.setAttribute("2030*1372*1870mm");
			gdEn.setColor("深蓝色");
			gdEn.setMaterial("北欧松木");
			gdEn.setVeneer("环保水性漆涂层");
		}
		ocEn.setGdEn(gdEn);

		//设计师
		DesignerEntity dgEn = new DesignerEntity();
		dgEn.setName("设计师C");
		dgEn.setPhone("13686436466");
		ocEn.setDgEn(dgEn);

		//收货地址
		AddressEntity adEn = new AddressEntity();
		adEn.setName("李女士");
		adEn.setPhone("16999666699");
		adEn.setAddress("广东省深圳市南山区粤海街道科发路大冲城市花园5栋16B");
		if (code > 4) {
			ocEn.setAdEn(adEn);
		}

		//效果图集
		ArrayList<String> imgList = new ArrayList<>();
		imgList.add(AppConfig.IMAGE_URL + "design_001.png");
		imgList.add(AppConfig.IMAGE_URL + "design_001.png");
		imgList.add(AppConfig.IMAGE_URL + "design_001.png");
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
					imgLs.add(AppConfig.IMAGE_URL + "design_001.png");
					imgLs.add(AppConfig.IMAGE_URL + "design_001.png");
					imgLs.add(AppConfig.IMAGE_URL + "design_001.png");
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
