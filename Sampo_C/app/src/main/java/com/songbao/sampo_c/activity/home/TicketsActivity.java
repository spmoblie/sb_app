package com.songbao.sampo_c.activity.home;


import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.activity.BaseActivity;
import com.songbao.sampo_c.entity.BaseEntity;
import com.songbao.sampo_c.entity.CouponEntity;
import com.songbao.sampo_c.utils.CommonTools;
import com.songbao.sampo_c.utils.ExceptionUtil;
import com.songbao.sampo_c.utils.JsonUtils;
import com.songbao.sampo_c.utils.LogUtil;
import com.songbao.sampo_c.utils.retrofit.HttpRequests;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

public class TicketsActivity extends BaseActivity implements View.OnClickListener {

    String TAG = TicketsActivity.class.getSimpleName();

    @BindView(R.id.tickets_set_meal_1_iv_discount)
    ImageView iv_discount_1;

    @BindView(R.id.tickets_set_meal_2_iv_discount)
    ImageView iv_discount_2;

    @BindView(R.id.tickets_set_meal_3_iv_discount)
    ImageView iv_discount_3;

    @BindView(R.id.tickets_set_meal_1_main)
    LinearLayout set_meal_main_1;

    @BindView(R.id.tickets_set_meal_2_main)
    LinearLayout set_meal_main_2;

    @BindView(R.id.tickets_set_meal_3_main)
    LinearLayout set_meal_main_3;

    @BindView(R.id.tickets_set_meal_1_tv_name)
    TextView tv_title_1;

    @BindView(R.id.tickets_set_meal_2_tv_name)
    TextView tv_title_2;

    @BindView(R.id.tickets_set_meal_3_tv_name)
    TextView tv_title_3;

    @BindView(R.id.tickets_set_meal_1_tv_price_new)
    TextView tv_price_new_1;

    @BindView(R.id.tickets_set_meal_2_tv_price_new)
    TextView tv_price_new_2;

    @BindView(R.id.tickets_set_meal_3_tv_price_new)
    TextView tv_price_new_3;

    @BindView(R.id.tickets_set_meal_1_tv_price_old)
    TextView tv_price_old_1;

    @BindView(R.id.tickets_set_meal_2_tv_price_old)
    TextView tv_price_old_2;

    @BindView(R.id.tickets_set_meal_3_tv_price_old)
    TextView tv_price_old_3;

    @BindView(R.id.tickets_set_meal_1_tv_discount)
    TextView tv_discount_1;

    @BindView(R.id.tickets_set_meal_2_tv_discount)
    TextView tv_discount_2;

    @BindView(R.id.tickets_set_meal_3_tv_discount)
    TextView tv_discount_3;

    @BindView(R.id.tickets_bt_buy_now)
    Button bt_buy_now;


    private CouponEntity data, coupon_1, coupon_2, coupon_3, select_coupon;
    private int set_meal_type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);

        initView();
        //loadServerData();
        data = getDemoData();
        setView();
    }

    private void initView() {
        setTitle(getString(R.string.main_head_tickets));

        set_meal_main_1.setOnClickListener(this);
        set_meal_main_2.setOnClickListener(this);
        set_meal_main_3.setOnClickListener(this);
        bt_buy_now.setOnClickListener(this);

        tv_price_old_1.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
        tv_price_old_2.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
        tv_price_old_3.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
    }

    private void setView() {
        if (data != null) {
            List<CouponEntity> coupons = data.getMainLists();
            if (coupons != null) {
                for (int i = 0; i < coupons.size(); i++) {
                    switch (i) {
                        case 0:
                            coupon_1 = coupons.get(i);
                            if (coupon_1 != null) {
                                tv_title_1.setText(coupon_1.getName());
                                tv_price_new_1.setText(df.format(coupon_1.getPriceNew()));
                                tv_price_old_1.setText(getString(R.string.pay_rmb, df.format(coupon_1.getPriceOld())));
                                tv_discount_1.setText(getString(R.string.coupon_discount, df.format(coupon_1.getDiscount())));

                                changeViewStatus(0);
                            }
                            break;
                        case 1:
                            coupon_2 = coupons.get(i);
                            if (coupon_2 != null) {
                                tv_title_2.setText(coupon_2.getName());
                                tv_price_new_2.setText(df.format(coupon_2.getPriceNew()));
                                tv_price_old_2.setText(getString(R.string.pay_rmb, df.format(coupon_2.getPriceOld())));
                                tv_discount_2.setText(getString(R.string.coupon_discount, df.format(coupon_2.getDiscount())));
                            }
                            break;
                        case 2:
                            coupon_3 = coupons.get(i);
                            if (coupon_3 != null) {
                                tv_title_3.setText(coupon_3.getName());
                                tv_price_new_3.setText(df.format(coupon_3.getPriceNew()));
                                tv_price_old_3.setText(getString(R.string.pay_rmb, df.format(coupon_3.getPriceOld())));
                                tv_discount_3.setText(getString(R.string.coupon_discount, df.format(coupon_3.getDiscount())));
                            }
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tickets_set_meal_1_main:
                if (set_meal_type == 0) return;
                changeViewStatus(0);
                break;
            case R.id.tickets_set_meal_2_main:
                if (set_meal_type == 1) return;
                changeViewStatus(1);
                break;
            case R.id.tickets_set_meal_3_main:
                if (set_meal_type == 2) return;
                changeViewStatus(2);
                break;
            case R.id.tickets_bt_buy_now:
                if (select_coupon != null) {
                    CommonTools.showToast("购买" + select_coupon.getName());
                } else {
                    dataErrorHandle();
                }
                break;
        }
    }

    /**
     * 切换View状态
     */
    private void changeViewStatus(int type) {
        set_meal_main_1.setSelected(false);
        set_meal_main_2.setSelected(false);
        set_meal_main_3.setSelected(false);
        iv_discount_1.setVisibility(View.GONE);
        iv_discount_2.setVisibility(View.GONE);
        iv_discount_3.setVisibility(View.GONE);

        switch (type) {
            case 0:
                if (coupon_1 == null) return;
                select_coupon = coupon_1;
                set_meal_main_1.setSelected(true);
                iv_discount_1.setVisibility(View.VISIBLE);
                break;
            case 1:
                if (coupon_2 == null) return;
                select_coupon = coupon_2;
                set_meal_main_2.setSelected(true);
                iv_discount_2.setVisibility(View.VISIBLE);
                break;
            case 2:
                if (coupon_3 == null) return;
                select_coupon = coupon_3;
                set_meal_main_3.setSelected(true);
                iv_discount_3.setVisibility(View.VISIBLE);
                break;
        }
        set_meal_type = type;
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
     * 数据报错处理
     */
    private void dataErrorHandle() {
        showDataError();
        loadServerData();
    }

    /**
     * 加载数据
     */
    private void loadServerData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("activityId", "");
        loadSVData(AppConfig.URL_ACTIVITY_DETAIL, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_ACTIVITY_DETAIL);
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        BaseEntity baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_ACTIVITY_DETAIL:
                    baseEn = JsonUtils.getThemeDetail(jsonObject);
                    if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {

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

    /**
     * 构建Demo数据
     */
    private CouponEntity getDemoData() {
        CouponEntity baseEn = new CouponEntity();

        List<CouponEntity> mainLists = new ArrayList<>();
        CouponEntity chEn_1 = new CouponEntity();
        CouponEntity chEn_2 = new CouponEntity();
        CouponEntity chEn_3 = new CouponEntity();

        chEn_1.setId(1);
        chEn_1.setName("半年卡");
        chEn_1.setPriceNew(6999);
        chEn_1.setPriceOld(7499);
        chEn_1.setDiscount(500);
        mainLists.add(chEn_1);

        chEn_2.setId(2);
        chEn_2.setName("10次卡");
        chEn_2.setPriceNew(2999);
        chEn_2.setPriceOld(3199);
        chEn_2.setDiscount(200);
        mainLists.add(chEn_2);

        chEn_3.setId(3);
        chEn_3.setName("1次卡");
        chEn_3.setPriceNew(299);
        chEn_3.setPriceOld(349);
        chEn_3.setDiscount(50);
        mainLists.add(chEn_3);

        baseEn.setMainLists(mainLists);

        return baseEn;
    }

}
