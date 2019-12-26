package com.songbao.sampo_c.wxapi;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.activity.BaseActivity;
import com.songbao.sampo_c.entity.BaseEntity;
import com.songbao.sampo_c.entity.PayResult;
import com.songbao.sampo_c.entity.PaymentEntity;
import com.songbao.sampo_c.utils.CommonTools;
import com.songbao.sampo_c.utils.ExceptionUtil;
import com.songbao.sampo_c.utils.JsonUtils;
import com.songbao.sampo_c.utils.LogUtil;
import com.songbao.sampo_c.utils.StringUtil;
import com.songbao.sampo_c.utils.retrofit.HttpRequests;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler, OnClickListener {

    String TAG = WXPayEntryActivity.class.getSimpleName();

    public static final int PAY_WX = 1;
    public static final int PAY_ZFB = 2;
    public static final int PAY_UNION = 3;

    public static final int PAY_SUCCESS = 1;
    public static final int PAY_CANCEL = 0;
    public static final int PAY_FAIL = -1;
    public static final int PAY_ERROR = -999;

    // 微信
    private IWXAPI api;
    // 支付宝
    private static final int SDK_ZFB_PAY_FLAG = 101;
    // 银联  mMode参数解释： "00" - 启动银联正式环境 "01" - 连接银联测试环境
    private final String mMode = AppConfig.IS_PUBLISH ? "00" : "01";

    private TextView tv_pay_amount;
    private ImageView iv_select_wx, iv_select_zfb, iv_select_union;
    private RelativeLayout rl_select_wx, rl_select_zfb, rl_select_union;
    private Button btn_confirm;

    private int payStatus = PAY_CANCEL; //支付状态
    private int payType = PAY_WX; //支付类型
    private int checkCount = 0; //查询支付结果的次数
    private int pageType = 0; //1:报名支付、预约支付
    private String orderSn, orderTotal;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_ZFB_PAY_FLAG: {
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    // 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        checkPayResult();
                    } else if (TextUtils.equals(resultStatus, "6001")) {
                        showPayResult(PAY_CANCEL);
                    } else {
                        showPayResult(PAY_FAIL);
                    }
                    break;
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxpay_entry);

        pageType = getIntent().getIntExtra(AppConfig.PAGE_TYPE, 0);
        orderSn = getIntent().getStringExtra("orderSn");
        orderTotal = getIntent().getStringExtra("orderTotal");

        api = WXAPIFactory.createWXAPI(mContext, AppConfig.WX_APP_ID);
        api.registerApp(AppConfig.WX_APP_ID);

        findViewById();
        initView();
    }

    private void findViewById() {
        tv_pay_amount = findViewById(R.id.payment_tv_pay_amount);
        iv_select_wx = findViewById(R.id.payment_iv_select_wx);
        iv_select_zfb = findViewById(R.id.payment_iv_select_zfb);
        iv_select_union = findViewById(R.id.payment_iv_select_union);
        rl_select_wx = findViewById(R.id.payment_rl_select_wx);
        rl_select_zfb = findViewById(R.id.payment_rl_select_zfb);
        rl_select_union = findViewById(R.id.payment_rl_select_union);
        btn_confirm = findViewById(R.id.payment_bt_pay_confirm);
    }

    private void initView() {
        setTitle(R.string.pay_title);  //设置标题
        tv_pay_amount.setText(orderTotal); //支付金额

        rl_select_wx.setOnClickListener(this);
        rl_select_zfb.setOnClickListener(this);
        rl_select_union.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);

        iv_select_wx.setSelected(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.payment_rl_select_wx:
                if (payType != PAY_WX) {
                    changeSelected(PAY_WX);
                }
                break;
            case R.id.payment_rl_select_zfb:
                if (payType != PAY_ZFB) {
                    changeSelected(PAY_ZFB);
                }
                break;
            case R.id.payment_rl_select_union:
                if (payType != PAY_UNION) {
                    changeSelected(PAY_UNION);
                }
                break;
            case R.id.payment_bt_pay_confirm:
                postPayment();
                break;
        }
    }

    private void changeSelected(int typeCode) {
        if (payStatus == PAY_SUCCESS) {
            showPaySuccess();
            return;
        } else if (payStatus == PAY_ERROR) {
            checkCount = 0;
            checkPayResult();
            return;
        }
        payType = typeCode;
        iv_select_wx.setSelected(false);
        iv_select_zfb.setSelected(false);
        iv_select_union.setSelected(false);
        switch (typeCode) {
            case PAY_WX:
                iv_select_wx.setSelected(true);
                break;
            case PAY_ZFB:
                iv_select_zfb.setSelected(true);
                break;
            case PAY_UNION:
                iv_select_union.setSelected(true);
                break;
        }
    }

    private void showPaySuccess() {
        CommonTools.showToast(getString(R.string.pay_result_ok));
    }

    /**
     * 提交支付请求
     */
    private void postPayment() {
        if (payStatus == PAY_SUCCESS) {
            showPaySuccess();
            return;
        } else if (payStatus == PAY_ERROR) {
            checkCount = 0;
            checkPayResult();
            return;
        }
        startAnimation();
        postPayInfo();
    }

    /**
     * 查询支付结果
     */
    private void checkPayResult() {
        startAnimation();
        checkCount++;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                postPayCheck();
            }
        }, AppConfig.LOADING_TIME);
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
    public void onBackPressed() {
        if (payStatus == PAY_CANCEL || payStatus == PAY_FAIL) {
            ask4Leave();
        } else {
            finish();
        }
    }

    @Override
    public void OnListenerLeft() {
        if (payStatus == PAY_CANCEL || payStatus == PAY_FAIL) {
            ask4Leave();
        } else {
            finish();
        }
    }

    private void ask4Leave() {
        showConfirmDialog(R.string.pay_abandon, getString(R.string.leave_confirm),
                getString(R.string.pay_continue), true, true, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case AppConfig.DIALOG_CLICK_NO:
                                finish();
                                break;
                        }
                    }
                });
    }

    @Override
    public void finish() {
        if (payStatus == PAY_SUCCESS || payStatus == PAY_ERROR) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(AppConfig.ACTIVITY_KEY_PAY_RESULT, true);
            setResult(RESULT_OK, returnIntent);
        }
        super.finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {
        LogUtil.i(TAG, "onPayFinish, errCode = " + resp.errCode);

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (resp.errCode == 0) { //(0:成功/-1:失败/-2:取消)
                checkPayResult();
            } else if (resp.errCode == -2) {
                showPayResult(PAY_CANCEL);
            } else {
                showPayResult(PAY_FAIL);
            }
        }
    }

    /**
     * 发送微信支付请求
     */
    private void sendWeiXiPayReq(PaymentEntity payEntity) {
        if (payEntity == null || StringUtil.isNull(payEntity.getPrepayid())
                || StringUtil.isNull(payEntity.getNoncestr())
                || StringUtil.isNull(payEntity.getTimestamp())
                || StringUtil.isNull(payEntity.getSign())) {
            getPayDataFail(null);
            return;
        }
        PayReq req = new PayReq();
        req.appId = AppConfig.WX_APP_ID;
        req.partnerId = AppConfig.WX_MCH_ID;
        req.prepayId = payEntity.getPrepayid();
        req.packageValue = "Sign=WXPay";
        req.nonceStr = payEntity.getNoncestr();
        req.timeStamp = payEntity.getTimestamp();
        req.sign = payEntity.getSign();
        // 发起支付
        api.sendReq(req);
        // 结束加载动画
        getPayDataSuccess();
    }

    /**
     * 发送支付宝支付请求
     */
    private void sendZFBPayReq(PaymentEntity payEntity) {
        // 获取订单数据
        final String payInfo = payEntity.getContent();
        if (StringUtil.isNull(payInfo)) {
            getPayDataFail(null);
            return;
        }
        // 创建异步任务
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
				/*PayTask alipay = new PayTask(WXPayEntryActivity.this);
				// 调用支付接口，获取支付结果
				Map<String, String> result = alipay.payV2(payInfo, true);

				Message msg = new Message();
				msg.what = SDK_ZFB_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);*/
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
        // 结束加载动画
        getPayDataSuccess();
    }

    /**
     * 发送银联支付请求
     */
    private void sendUnionPayReq(PaymentEntity payEntity) {
        // 获取银联支付订单号
        String payInfo = "201608011419051350568";
        if (!StringUtil.isNumeric(payInfo)) {
            getPayDataFail(null);
            return;
        }
        // 调用支付SDK
        //UPPayAssistEx.startPay(this, null, null, payInfo, mMode);
        // 结束加载动画
        getPayDataSuccess();
    }

    private void getPayDataFail(BaseEntity baseEn) {
        stopAnimation();
        if (baseEn != null) {
            showErrorDialog(baseEn.getErrmsg());
        } else {
            showErrorDialog(R.string.pay_info_error);
        }
    }

    private void getPayDataSuccess() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                stopAnimation();
            }
        }, AppConfig.LOADING_TIME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) return;
        int payCode = PAY_FAIL;
        // 银联手机支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
        String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {
            payCode = PAY_SUCCESS;
        } else if (str.equalsIgnoreCase("cancel")) {
            payCode = PAY_CANCEL;
        }
        showPayResult(payCode);
    }

    /**
     * 提交支付信息
     */
    private void postPayInfo() {
        HashMap<String, String> map = new HashMap<>();
        map.put("paymentType", String.valueOf(payType));
        map.put("outTradeNo", orderSn);
        map.put("sourceType", String.valueOf(pageType));
        map.put("tradeType", AppConfig.LOAD_TYPE);
        loadSVData(AppConfig.URL_PAY_PARAMETER, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_PAY_PARAMETER);
    }

    /**
     * 发起支付校验
     */
    private void postPayCheck() {
        HashMap<String, String> map = new HashMap<>();
        map.put("outTradeNo", orderSn);
        map.put("sourceType", String.valueOf(pageType));
        map.put("tradeType", AppConfig.LOAD_TYPE);
        loadSVData(AppConfig.URL_PAY_CHECK_RESULT, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_PAY_CHECK_RESULT);
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        BaseEntity baseEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_PAY_PARAMETER:
                    baseEn = JsonUtils.getPayInfo(jsonObject, payType);
                    if (baseEn != null && baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
                        PaymentEntity payEntity = (PaymentEntity) baseEn.getData();
                        switch (payType) {
                            case PAY_WX: //微信支付
                                sendWeiXiPayReq(payEntity);
                                break;
                            case PAY_ZFB: //支付宝支付
                                sendZFBPayReq(payEntity);
                                break;
                            case PAY_UNION: //银联支付
                                sendUnionPayReq(payEntity);
                                break;
                        }
                    } else {
                        getPayDataFail(baseEn);
                    }
                    break;
                case AppConfig.REQUEST_SV_PAY_CHECK_RESULT:
                    baseEn = JsonUtils.getBaseErrorData(jsonObject);
                    if (baseEn != null && baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
                        showPayResult(PAY_SUCCESS);
                    } else {
                        if (checkCount < 3) {
                            checkPayResult();
                        } else {
                            showPayResult(PAY_ERROR);
                        }
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

    private void showPayResult(int payCode) {
        stopAnimation();
        payStatus = payCode;
        switch (payCode) {
            case PAY_SUCCESS:
                CommonTools.showToast(getString(R.string.pay_success));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 500);
                break;
            case PAY_CANCEL:
                CommonTools.showToast(getString(R.string.pay_cancel));
                break;
            case PAY_FAIL:
                CommonTools.showToast(getString(R.string.pay_fail));
                break;
            case PAY_ERROR:
                showErrorDialog(getString(R.string.pay_result_error));
                break;
        }
    }

}