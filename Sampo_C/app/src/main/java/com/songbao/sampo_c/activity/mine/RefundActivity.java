package com.songbao.sampo_c.activity.mine;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.activity.BaseActivity;
import com.songbao.sampo_c.entity.BaseEntity;
import com.songbao.sampo_c.entity.GoodsEntity;
import com.songbao.sampo_c.entity.GoodsSaleEntity;
import com.songbao.sampo_c.utils.ExceptionUtil;
import com.songbao.sampo_c.utils.JsonUtils;
import com.songbao.sampo_c.utils.LogUtil;
import com.songbao.sampo_c.utils.TimeUtil;
import com.songbao.sampo_c.utils.retrofit.HttpRequests;
import com.songbao.sampo_c.widgets.RoundImageView;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;


public class RefundActivity extends BaseActivity implements OnClickListener {

    String TAG = RefundActivity.class.getSimpleName();

    @BindView(R.id.refund_tv_state_describe)
    TextView tv_state_describe;

    @BindView(R.id.refund_tv_time_update)
    TextView tv_time_update;

    @BindView(R.id.refund_iv_goods)
    RoundImageView iv_goods;

    @BindView(R.id.refund_tv_name)
    TextView tv_name;

    @BindView(R.id.refund_tv_attr)
    TextView tv_attr;

    @BindView(R.id.refund_tv_price)
    TextView tv_price;

    @BindView(R.id.refund_iv_state_02)
    ImageView iv_state_02;

    @BindView(R.id.refund_iv_state_03)
    ImageView iv_state_03;

    @BindView(R.id.refund_tv_state_01_time)
    TextView tv_state_01_time;

    @BindView(R.id.refund_tv_state_02_time)
    TextView tv_state_02_time;

    @BindView(R.id.refund_tv_state_03_time)
    TextView tv_state_03_time;

    @BindView(R.id.refund_tv_refund_number)
    TextView tv_refund_number;

    @BindView(R.id.refund_tv_number_copy)
    TextView tv_number_copy;

    @BindView(R.id.refund_tv_time_apply)
    TextView tv_time_apply;

    @BindView(R.id.refund_tv_cancel)
    TextView tv_cancel;

    private GoodsEntity goodsEn;
    private GoodsSaleEntity saleEn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund);

        goodsEn = (GoodsEntity) getIntent().getSerializableExtra(AppConfig.PAGE_DATA);

        initView();
    }

    private void initView() {
        setTitle(getString(R.string.order_refund_details));

        loadRefundData();
    }

    private void initShowView() {
        if (goodsEn != null) {
            Glide.with(AppApplication.getAppContext())
                    .load(goodsEn.getPicUrl())
                    .apply(AppApplication.getShowOptions())
                    .into(iv_goods);

            tv_name.setText(goodsEn.getName());
            tv_attr.setText(goodsEn.getAttribute());
        }
        if (saleEn != null) {
            tv_price.setText(df.format(saleEn.getRefundPrice()));
            tv_refund_number.setText(getString(R.string.order_refund_no, saleEn.getRefundNo()));
            tv_time_apply.setText(getString(R.string.order_time_apply, saleEn.getAddTime()));
            tv_time_update.setText(TimeUtil.getNowString("yyyy年MM月dd日 HH时mm分ss秒"));
            tv_state_01_time.setText(TimeUtil.strToStrNode(saleEn.getAddTime()));

            switch (saleEn.getSaleStatus()) {
                case 9:
                    tv_state_02_time.setText(TimeUtil.strToStrNode(TimeUtil.getNowString("yyyy-MM-dd HH:mm:ss")));
                    tv_state_describe.setText("退款中，请耐心等待");
                    tv_state_describe.setTextSize(18);
                    iv_state_02.setImageResource(R.mipmap.icon_waiting);
                    iv_state_03.setImageResource(R.mipmap.sel_checkbox_large_no);
                    break;
                case 10:
                    tv_time_update.setText(TimeUtil.strToStr("yyyy年MM月dd日 HH时mm分ss秒", "yyyy-MM-dd HH:mm:ss", saleEn.getEndTime()));
                    tv_state_03_time.setText(TimeUtil.strToStrNode(saleEn.getEndTime()));
                    tv_state_describe.setText("退款完成，已退回原支付账户");
                    tv_state_describe.setTextSize(15);
                    iv_state_02.setImageResource(R.mipmap.icon_waiting);
                    iv_state_03.setImageResource(R.mipmap.sel_checkbox_large_ok);
                    break;
                default:
                    iv_state_02.setImageResource(R.mipmap.sel_checkbox_large_no);
                    iv_state_03.setImageResource(R.mipmap.sel_checkbox_large_no);
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refund_tv_cancel:

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

    /**
     * 加载退款详情数据
     */
    private void loadRefundData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("page", 1);
        map.put("size", AppConfig.LOAD_SIZE);
        loadSVData(AppConfig.URL_USER_MESSAGE, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_REFUND_DETAIL);
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        BaseEntity baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_REFUND_DETAIL:
                    baseEn = JsonUtils.getRefundDetailData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        saleEn = (GoodsSaleEntity) baseEn.getData();
                        initShowView();
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

}
