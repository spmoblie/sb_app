package com.songbao.sampo.activity.mine;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.songbao.sampo.AppApplication;
import com.songbao.sampo.AppConfig;
import com.songbao.sampo.R;
import com.songbao.sampo.activity.BaseActivity;
import com.songbao.sampo.entity.BaseEntity;
import com.songbao.sampo.entity.GoodsEntity;
import com.songbao.sampo.utils.ExceptionUtil;
import com.songbao.sampo.utils.JsonUtils;
import com.songbao.sampo.utils.LogUtil;
import com.songbao.sampo.widgets.RoundImageView;

import org.json.JSONObject;

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

    private GoodsEntity data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund);

        data = (GoodsEntity) getIntent().getSerializableExtra(AppConfig.PAGE_DATA);

        initView();
    }

    private void initView() {
        setTitle("退款详情");

        initShowView();
        loadRefundData();
    }

    private void initShowView() {
        if (data != null) {
            Glide.with(AppApplication.getAppContext())
                    .load(data.getPicUrl())
                    .apply(AppApplication.getShowOptions())
                    .into(iv_goods);

            tv_name.setText(data.getName());
            tv_attr.setText(data.getAttribute());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comment_post_tv_post:

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
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        BaseEntity baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_UPLOAD_COMMENT_PHOTO:
                    baseEn = JsonUtils.getUploadResult(jsonObject);
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

    @Override
    protected void loadFailHandle() {
        super.loadFailHandle();
        handleErrorCode(null);
    }

}
