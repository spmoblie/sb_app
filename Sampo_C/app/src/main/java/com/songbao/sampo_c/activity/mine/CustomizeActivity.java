package com.songbao.sampo_c.activity.mine;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.activity.BaseActivity;
import com.songbao.sampo_c.adapter.AdapterCallback;
import com.songbao.sampo_c.adapter.OrderLogisticsAdapter;
import com.songbao.sampo_c.adapter.OrderProgressAdapter;
import com.songbao.sampo_c.entity.AddressEntity;
import com.songbao.sampo_c.entity.BaseEntity;
import com.songbao.sampo_c.entity.DesignerEntity;
import com.songbao.sampo_c.entity.GoodsEntity;
import com.songbao.sampo_c.entity.OCustomizeEntity;
import com.songbao.sampo_c.entity.OLogisticsEntity;
import com.songbao.sampo_c.entity.OProgressEntity;
import com.songbao.sampo_c.utils.ExceptionUtil;
import com.songbao.sampo_c.utils.JsonUtils;
import com.songbao.sampo_c.utils.LogUtil;
import com.songbao.sampo_c.utils.StringUtil;
import com.songbao.sampo_c.utils.retrofit.HttpRequests;
import com.songbao.sampo_c.widgets.ObservableScrollView;
import com.songbao.sampo_c.widgets.RoundImageView;
import com.songbao.sampo_c.widgets.ScrollViewListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
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

	@BindView(R.id.customize_iv_module_1_title_sign)
	ImageView iv_1_sign;

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

	@BindView(R.id.customize_tv_module_1_check_goods)
	TextView tv_1_check;

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

	@BindView(R.id.customize_tv_module_4_confirm)
	TextView tv_4_confirm;

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

	@BindView(R.id.customize_tv_module_7_confirm)
	TextView tv_7_confirm;

	@BindView(R.id.customize_iv_module_8_title_sign)
	ImageView iv_8_sign;

	@BindView(R.id.customize_tv_module_8_time)
	TextView tv_8_time;

	@BindView(R.id.customize_tv_module_8_remind)
	TextView tv_8_remind;

	@BindView(R.id.customize_tv_module_8_confirm)
	TextView tv_8_confirm;

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

	private OrderProgressAdapter lv_6_Adapter;
	private OrderLogisticsAdapter lv_7_Adapter;

	private OCustomizeEntity ocEn;
	private Drawable open_up, open_down;

	private boolean isDesigns; //是否确认图片
	private boolean isPayment; //是否确认支付
	private boolean isReceipt; //是否确认收货
	private boolean isInstall; //是否确认安装
	private boolean isOpenAll; //是否展开进度

	private int nodeNo = 0;
	private int status = 0;
	private int addressId = 0;
	private int updateCode = 0;
	private int nodePosition = 0;
	private String orderNo; //订单编号
	private String goodsCode; //商品编号

	private ArrayList<String> al_img = new ArrayList<>();
	private ArrayList<OProgressEntity> al_6 = new ArrayList<>();
	private ArrayList<OProgressEntity> al_6_1 = new ArrayList<>();
	private ArrayList<OProgressEntity> al_6_all = new ArrayList<>();
	private ArrayList<OLogisticsEntity> al_7 = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customize);

		nodePosition = getIntent().getIntExtra("nodePosition", 0);
		ocEn = (OCustomizeEntity) getIntent().getSerializableExtra(AppConfig.PAGE_DATA);

		initView();
	}

	private void initView() {
		setTitle(getString(R.string.order_progress));

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
			status = ocEn.getStatus();
			switch (status) {
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
					tv_order_status.setBackgroundResource(R.drawable.shape_style_solid_08_04);
					break;
				case AppConfig.ORDER_STATUS_401: //待收货
					tv_order_status.setText(getString(R.string.order_wait_receive));
					tv_order_status.setBackgroundResource(R.drawable.shape_style_solid_04_04);
					break;
				case AppConfig.ORDER_STATUS_501: //已签收
				case AppConfig.ORDER_STATUS_701: //待安装
					tv_order_status.setText(getString(R.string.order_wait_install));
					tv_order_status.setBackgroundResource(R.drawable.shape_style_solid_07_04);
					break;
				case AppConfig.ORDER_STATUS_801: //已完成
					setRightViewText(getString(R.string.order_delete));
					tv_order_status.setText(getString(R.string.order_completed));
					tv_order_status.setBackgroundResource(R.drawable.shape_style_solid_03_04);
					break;
				case AppConfig.ORDER_STATUS_102: //已取消
				default:
					setRightViewText(getString(R.string.order_delete));
					tv_order_status.setText(getString(R.string.order_cancelled));
					tv_order_status.setBackgroundResource(R.drawable.shape_style_solid_03_04);
					break;
			}

			nodeNo = ocEn.getNodeNo();
			if (nodeNo > 0) { //提交预约
				iv_1_sign.setSelected(true);
				tv_1_time.setText(ocEn.getNodeTime1());
				iv_1_goods_img.setVisibility(View.VISIBLE);
				tv_1_goods_name.setVisibility(View.VISIBLE);
				tv_1_designer_name.setVisibility(View.VISIBLE);
				tv_1_designer_phone.setVisibility(View.VISIBLE);
				GoodsEntity gdEn = ocEn.getGdEn();
				if (gdEn != null) {
					Glide.with(AppApplication.getAppContext())
							.load(gdEn.getPicUrl())
							.apply(AppApplication.getShowOptions())
							.into(iv_1_goods_img);
					tv_1_goods_name.setText(gdEn.getName());
					goodsCode = gdEn.getGoodsCode();
					tv_1_check.setOnClickListener(this);
					tv_1_check.setVisibility(View.VISIBLE);
				}
				DesignerEntity dgEn = ocEn.getDgEn();
				if (dgEn != null) {
					tv_1_designer_phone.setText(dgEn.getPhone());
					tv_1_designer_name.setText(dgEn.getName());
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

					isDesigns = ocEn.isDesigns();
					if (isDesigns) {
						tv_3_confirm.setText(getString(R.string.order_confirmed));
						tv_3_confirm.setTextColor(getResources().getColor(R.color.debar_text_color));
						tv_3_confirm.setBackgroundResource(R.drawable.shape_style_empty_03_08);
					} else {
						tv_3_confirm.setOnClickListener(this);
						tv_3_confirm.setText(getString(R.string.order_confirm_designs));
						tv_3_confirm.setTextColor(getResources().getColor(R.color.app_color_white));
						tv_3_confirm.setBackgroundResource(R.drawable.shape_style_solid_04_08);
					}
					tv_3_confirm.setVisibility(View.VISIBLE);
				}
			}

			if (nodeNo > 3) { //支付信息
				iv_4_sign.setSelected(true);
				tv_4_time.setText(ocEn.getNodeTime4());
				tv_4_order_price.setVisibility(View.VISIBLE);
				tv_4_order_price.setText(getString(R.string.order_price_offer, df.format(ocEn.getPrice())));
				tv_4_order_day.setVisibility(View.VISIBLE);
				tv_4_order_day.setText(getString(R.string.order_cycle_day, ocEn.getCycle()));

				isPayment = ocEn.isPayment();
				if (isPayment) { //已支付
					tv_4_confirm.setText(getString(R.string.pay_ok));
					tv_4_confirm.setTextColor(getResources().getColor(R.color.debar_text_color));
					tv_4_confirm.setBackgroundResource(R.drawable.shape_style_empty_03_08);
				} else {
					tv_4_confirm.setOnClickListener(this);
					tv_4_confirm.setText(getString(R.string.order_confirm_payment));
					tv_4_confirm.setTextColor(getResources().getColor(R.color.app_color_white));
					tv_4_confirm.setBackgroundResource(R.drawable.shape_style_solid_04_08);
				}
				tv_4_confirm.setVisibility(View.VISIBLE);
			}

			if (nodeNo > 4) { //确认收货信息
				tv_5_addressee_name.setVisibility(View.VISIBLE);
				tv_5_addressee_phone.setVisibility(View.VISIBLE);
				tv_5_address_1.setVisibility(View.VISIBLE);
				tv_5_address_2.setVisibility(View.VISIBLE);
				tv_5_select_address.setVisibility(View.VISIBLE);
				AddressEntity adEn = ocEn.getAdEn();
				if (adEn != null) {
					addressId = adEn.getId();
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

					tv_6_time.setText(al_6_all.get(al_6_all.size() - 1).getAddTime());
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
					tv_7_confirm.setText(getString(R.string.order_confirmed));
					tv_7_confirm.setTextColor(getResources().getColor(R.color.debar_text_color));
					tv_7_confirm.setBackgroundResource(R.drawable.shape_style_empty_03_08);

					tv_8_confirm.setOnClickListener(this);
					tv_8_confirm.setVisibility(View.VISIBLE);
					tv_8_confirm.setText(getString(R.string.order_confirm_install));
					tv_8_confirm.setTextColor(getResources().getColor(R.color.app_color_white));
					tv_8_confirm.setBackgroundResource(R.drawable.shape_style_solid_04_08);
					tv_8_remind.setText(getString(R.string.order_install_hint));
				} else {
					tv_7_confirm.setOnClickListener(this);
					tv_7_confirm.setText(getString(R.string.order_confirm_receipt));
					tv_7_confirm.setTextColor(getResources().getColor(R.color.app_color_white));
					tv_7_confirm.setBackgroundResource(R.drawable.shape_style_solid_04_08);

					tv_8_remind.setText(getString(R.string.order_receipt_hint));
				}
				tv_5_select_address.setVisibility(View.GONE);
				tv_7_confirm.setVisibility(View.VISIBLE);
				tv_8_remind.setVisibility(View.VISIBLE);
			}

			if (nodeNo > 7) { //产品安装
				iv_8_sign.setSelected(true);
				tv_8_time.setText(ocEn.getNodeTime8());
				isInstall = ocEn.isInstall();
				if (isInstall) { //已确认安装
					tv_8_confirm.setText(getString(R.string.order_installed));
					tv_8_confirm.setTextColor(getResources().getColor(R.color.debar_text_color));
					tv_8_confirm.setBackgroundResource(R.drawable.shape_style_empty_03_08);
				}
			}

			if (nodeNo > 8) { //订单完成
				iv_9_sign.setSelected(true);
				tv_9_time.setText(ocEn.getNodeTime9());
				tv_9_remind.setVisibility(View.VISIBLE);
			}
		}
		initListView();

		if (nodePosition > 0) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					switch (nodePosition) {
						case 6: //查看进度
							scrollTo(iv_6_sign.getTop());
							break;
						case 7: //查看物流、确认收货
							scrollTo(iv_7_sign.getTop());
							break;
						case 8: //确认安装
							scrollTo(iv_8_sign.getTop());
							break;
					}
					nodePosition = 0;
				}
			}, 200);
		}
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
							ArrayList<String> imgList = castList(data, String.class);
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

	/**
	 * 滚动到指定位置
	 */
	private void scrollTo(int y) {
		sv_main.smoothScrollTo(0, y);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.customize_tv_module_1_check_goods:
				//商品详情
				openGoodsActivity(goodsCode);
				break;
			case R.id.customize_tv_module_3_show:
				//查看效果图
				openViewPagerActivity(al_img, 0);
				break;
			case R.id.customize_tv_module_3_confirm:
				//确认效果图
				if (!isDesigns) {
					showConfirmDialog(getString(R.string.order_confirm_designs_hint), new MyHandler(this), 3);
				}
				break;
			case R.id.customize_tv_module_4_confirm:
				//确认支付
				if (!isPayment) {
					showConfirmDialog(getString(R.string.order_confirm_payment_hint), new MyHandler(this), 4);
				}
				break;
			case R.id.customize_tv_module_5_select_address:
				//收货地址
				if (nodeNo > 0 && nodeNo < 9) {
					Intent intent = new Intent(mContext, AddressActivity.class);
					intent.putExtra("isFinish", true);
					intent.putExtra("selectId", addressId);
					startActivityForResult(intent, AppConfig.ACTIVITY_CODE_SELECT_ADDS);
				}
				break;
			case R.id.customize_tv_module_6_lv_open:
				//展开进度
				al_6.clear();
				isOpenAll = !isOpenAll;
				if (isOpenAll) {
					tv_6_lv_open.setText(getString(R.string.put_away));
					tv_6_lv_open.setCompoundDrawables(null, null, open_up, null);
					al_6.addAll(al_6_all);
				} else {
					tv_6_lv_open.setText(getString(R.string.unfold));
					tv_6_lv_open.setCompoundDrawables(null, null, open_down, null);
					al_6.addAll(al_6_1);
				}
				initListView();
				break;
			case R.id.customize_tv_module_7_confirm:
				//确认收货
				if (!isReceipt) {
					showConfirmDialog(getString(R.string.order_confirm_receipt_hint), new MyHandler(this), 7);
				}
				break;
			case R.id.customize_tv_module_8_confirm:
				//确认安装
				if (!isInstall) {
					showConfirmDialog(getString(R.string.order_confirm_install_hint), new MyHandler(this), 8);
				}
				break;
		}
	}

	@Override
	protected void OnListenerRight() {
		switch (status) {
			case AppConfig.ORDER_STATUS_101: //待付款
				// 取消订单
				showConfirmDialog(getString(R.string.order_cancel_confirm), new MyHandler(this), 101);
				break;
			case AppConfig.ORDER_STATUS_102: //已取消
			case AppConfig.ORDER_STATUS_801: //已完成
				// 删除订单
				showConfirmDialog(getString(R.string.order_delete_confirm), new MyHandler(this), 102);
				break;
		}
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

	@Override
	public void finish() {
		if (updateCode == -1
				|| updateCode == AppConfig.ORDER_STATUS_102
				|| updateCode == AppConfig.ORDER_STATUS_201
				|| updateCode == AppConfig.ORDER_STATUS_701
				|| updateCode == AppConfig.ORDER_STATUS_801) {
			Intent returnIntent = new Intent();
			returnIntent.putExtra(AppConfig.PAGE_DATA, updateCode);
			setResult(RESULT_OK, returnIntent);
		}
		super.finish();
	}

	/**
	 * 加载订单详情数据
	 */
	private void loadOrderData() {
		HashMap<String, String> map = new HashMap<>();
		map.put("bookingCode", orderNo);
		loadSVData(AppConfig.BASE_URL_3, AppConfig.URL_BOOKING_INFO, map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_BOOKING_INFO);
	}

	/**
	 * 确认效果图
	 */
	private void postConfirmDesigns() {
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("bookingCode", orderNo);
			postJsonData(AppConfig.BASE_URL_3, AppConfig.URL_BOOKING_DESIGNS, jsonObj, AppConfig.REQUEST_SV_BOOKING_DESIGNS);
		} catch (JSONException e) {
			ExceptionUtil.handle(e);
		}
	}

	/**
	 * 确认支付
	 */
	private void postConfirmPayment() {
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("bookingCode", orderNo);
			postJsonData(AppConfig.BASE_URL_3, AppConfig.URL_BOOKING_PAYMENT, jsonObj, AppConfig.REQUEST_SV_BOOKING_PAYMENT);
		} catch (JSONException e) {
			ExceptionUtil.handle(e);
		}
	}

	/**
	 * 确认收货
	 */
	private void postConfirmReceipt() {
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("bookingCode", orderNo);
			postJsonData(AppConfig.BASE_URL_3, AppConfig.URL_BOOKING_RECEIPT, jsonObj, AppConfig.REQUEST_SV_BOOKING_RECEIPT);
		} catch (JSONException e) {
			ExceptionUtil.handle(e);
		}
	}

	/**
	 * 确认安装
	 */
	private void postConfirmInstall() {
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("bookingCode", orderNo);
			postJsonData(AppConfig.BASE_URL_3, AppConfig.URL_BOOKING_INSTALL, jsonObj, AppConfig.REQUEST_SV_BOOKING_INSTALL);
		} catch (JSONException e) {
			ExceptionUtil.handle(e);
		}
	}

	/**
	 * 提交收货地址
	 */
	private void postAddressData(AddressEntity addEn) {
		if (addEn == null) return;
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("orderCode", orderNo);
			jsonObj.put("recieverId", addEn.getId());
			postJsonData(AppConfig.BASE_URL_3, AppConfig.URL_ORDER_UPDATE, jsonObj, AppConfig.REQUEST_SV_ORDER_UPDATE);
		} catch (JSONException e) {
			ExceptionUtil.handle(e);
		}
	}

	/**
	 * 取消订单
	 */
	private void postConfirmCancel() {
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("code", orderNo);
			postJsonData(AppConfig.BASE_URL_3, AppConfig.URL_BOOKING_CANCEL, jsonObj, AppConfig.REQUEST_SV_BOOKING_CANCEL);
		} catch (JSONException e) {
			ExceptionUtil.handle(e);
		}
	}

	/**
	 * 删除订单
	 */
	private void postConfirmDelete() {
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("code", orderNo);
			postJsonData(AppConfig.BASE_URL_3, AppConfig.URL_BOOKING_DELETE, jsonObj, AppConfig.REQUEST_SV_BOOKING_DELETE);
		} catch (JSONException e) {
			ExceptionUtil.handle(e);
		}
	}

	@Override
	protected void callbackData(JSONObject jsonObject, int dataType) {
		super.callbackData(jsonObject, dataType);
		BaseEntity<OCustomizeEntity> baseEn;
		try {
			switch (dataType) {
				case AppConfig.REQUEST_SV_BOOKING_INFO:
					baseEn = JsonUtils.getCustomizeDetailData(jsonObject);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						ocEn = baseEn.getData();
						initShowData();
					} else {
						handleErrorCode(baseEn);
					}
					break;
				case AppConfig.REQUEST_SV_BOOKING_DESIGNS:
					baseEn = JsonUtils.getBaseErrorData(jsonObject);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						loadOrderData();
					} else {
						handleErrorCode(baseEn);
					}
					break;
				case AppConfig.REQUEST_SV_BOOKING_PAYMENT:
					baseEn = JsonUtils.getBaseErrorData(jsonObject);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						updateCode = AppConfig.ORDER_STATUS_201;
						loadOrderData();
					} else {
						handleErrorCode(baseEn);
					}
					break;
				case AppConfig.REQUEST_SV_BOOKING_RECEIPT:
					baseEn = JsonUtils.getBaseErrorData(jsonObject);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						updateCode = AppConfig.ORDER_STATUS_701;
						loadOrderData();
					} else {
						handleErrorCode(baseEn);
					}
					break;
				case AppConfig.REQUEST_SV_BOOKING_INSTALL:
					baseEn = JsonUtils.getBaseErrorData(jsonObject);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						updateCode = AppConfig.ORDER_STATUS_801;
						loadOrderData();
					} else {
						handleErrorCode(baseEn);
					}
					break;
				case AppConfig.REQUEST_SV_ORDER_UPDATE:
					baseEn = JsonUtils.getBaseErrorData(jsonObject);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						loadOrderData();
					} else {
						handleErrorCode(baseEn);
					}
					break;
				case AppConfig.REQUEST_SV_BOOKING_CANCEL:
					baseEn = JsonUtils.getBaseErrorData(jsonObject);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						updateCode = AppConfig.ORDER_STATUS_102;
						loadOrderData();
					} else {
						handleErrorCode(baseEn);
					}
					break;
				case AppConfig.REQUEST_SV_BOOKING_DELETE:
					baseEn = JsonUtils.getBaseErrorData(jsonObject);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						updateCode = -1;
						finish();
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == AppConfig.ACTIVITY_CODE_SELECT_ADDS) {
				AddressEntity selectAddEn = (AddressEntity) data.getSerializableExtra(AppConfig.PAGE_DATA);
				if (selectAddEn != null) {
					postAddressData(selectAddEn);
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	static class MyHandler extends Handler {

		WeakReference<CustomizeActivity> mActivity;

		MyHandler(CustomizeActivity activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			CustomizeActivity theActivity = mActivity.get();
			switch (msg.what) {
				case 3: //确认效果图
					theActivity.postConfirmDesigns();
					break;
				case 4: //确认支付
					theActivity.postConfirmPayment();
					break;
				case 7: //确认收货
					theActivity.postConfirmReceipt();
					break;
				case 8: //确认安装
					theActivity.postConfirmInstall();
					break;
				case 101: //取消订单
					theActivity.postConfirmCancel();
					break;
				case 102: //删除订单
					theActivity.postConfirmDelete();
					break;
			}
		}
	}

}
