package com.songbao.sampo.activity.mine;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.songbao.sampo.AppApplication;
import com.songbao.sampo.R;
import com.songbao.sampo.activity.BaseActivity;
import com.songbao.sampo.utils.LogUtil;
import com.songbao.sampo.widgets.ObservableScrollView;
import com.songbao.sampo.widgets.RoundImageView;
import com.songbao.sampo.widgets.ScrollViewListView;

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

	@BindView(R.id.customize_tv_module_3_time)
	TextView tv_3_time;

	@BindView(R.id.customize_iv_module_3_show)
	RoundImageView iv_3_show;

	@BindView(R.id.customize_tv_module_3_show)
	TextView tv_3_show;

	@BindView(R.id.customize_tv_module_3_confirm)
	TextView tv_3_confirm;

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

	@BindView(R.id.customize_tv_module_5_time)
	TextView tv_5_time;

	@BindView(R.id.customize_tv_module_5_addressee_name)
	TextView tv_5_addressee_name;

	@BindView(R.id.customize_tv_module_5_addressee_phone)
	TextView tv_5_addressee_phone;

	@BindView(R.id.customize_tv_module_5_address_2)
	TextView tv_5_address_2;

	@BindView(R.id.customize_tv_module_5_select_address)
	TextView tv_5_select_address;

	@BindView(R.id.customize_tv_module_6_time)
	TextView tv_6_time;

	@BindView(R.id.customize_module_6_lv)
	ScrollViewListView lv_6;

	@BindView(R.id.customize_tv_module_6_lv_open)
	TextView tv_6_lv_open;

	@BindView(R.id.customize_tv_module_7_time)
	TextView tv_7_time;

	@BindView(R.id.customize_module_7_lv)
	ScrollViewListView lv_7;

	@BindView(R.id.customize_tv_module_7_call_install)
	TextView tv_7_call_install;

	@BindView(R.id.customize_tv_module_8_time)
	TextView tv_8_time;

	@BindView(R.id.customize_tv_module_8_remind)
	TextView tv_8_remind;

	@BindView(R.id.customize_tv_module_8_install_confirm)
	TextView tv_8_install_confirm;

	@BindView(R.id.customize_tv_module_9_time)
	TextView tv_9_time;

	@BindView(R.id.customize_tv_module_9_remind)
	TextView tv_9_remind;

	@BindView(R.id.customize_tv_module_9_comment)
	TextView tv_9_comment;

	@BindView(R.id.customize_tv_module_9_post_sale)
	TextView tv_9_post_sale;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customize);

		initView();
	}

	private void initView() {
		setTitle("定制进度");
		setRightViewText("取消订单");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
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

}
