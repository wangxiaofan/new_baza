package social.core;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import social.IPayCallBack;
import social.SocialHelper;
import social.data.PayData;

/**
 * Created by Vincent.Lei on 2018/2/23.
 * Title：
 * Note：
 */

public class WeChatPayHandlerActivity extends Activity implements IWXAPIEventHandler {
    private boolean mHasShow;
    private String mCallBackId;
    private IWXAPI mIWXApi;
    private PayData mPayData;
    private int mErrorCode = IPayCallBack.ERROR_CODE_CANCELED;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        if (mPayData != null)
            pay();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mIWXApi.handleIntent(intent, this);
    }

    private void initData() {
        mIWXApi = WXAPIFactory.createWXAPI(this, SocialHelper.getInstance().getWeChatAppId());
        mIWXApi.registerApp(SocialHelper.getInstance().getWeChatAppId());
        mIWXApi.handleIntent(getIntent(), this);
        Intent dataIntent = getIntent();
        if (dataIntent != null) {
            mCallBackId = dataIntent.getStringExtra("callBackId");
            mPayData = (PayData) dataIntent.getSerializableExtra("payData");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mHasShow)
            finish();
        mHasShow = true;
    }

    private void pay() {
        if (!mIWXApi.isWXAppInstalled() || mIWXApi.getWXAppSupportAPI() < Build.PAY_SUPPORTED_SDK_INT) {
            //微信没有安装  或者版本不支持支付
            mErrorCode = IPayCallBack.ERROR_CODE_WX_INSTALL_OR_LEVEL;
            finish();
            return;
        }
        PayReq req = genPayReq();
        mIWXApi.sendReq(req);

    }

    private PayReq genPayReq() {
        PayReq req = new PayReq();
        req.appId = mPayData.appId;
        req.partnerId = mPayData.partnerId;
        req.prepayId = mPayData.prepayId;
//        req.packageValue = "Sign=WXPay";
        req.packageValue = mPayData.packageValue;
        req.nonceStr = mPayData.nonceStr;
        req.timeStamp = mPayData.timeStamp;

//        List<NameValuePair> signParams = new LinkedList<>();
//        signParams.add(new NameValuePair("appid", req.appId));
//        signParams.add(new NameValuePair("noncestr", req.nonceStr));
//        signParams.add(new NameValuePair("package", req.packageValue));
//        signParams.add(new NameValuePair("partnerid", req.partnerId));
//        signParams.add(new NameValuePair("prepayid", req.prepayId));
//        signParams.add(new NameValuePair("timestamp", req.timeStamp));

        req.sign = mPayData.sign;
        return req;
    }


    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void finish() {
        super.finish();
        SocialHelper.getInstance().noticePayResult(mCallBackId, mErrorCode);
    }

    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case 0:
                //支付成功
                mErrorCode = IPayCallBack.ERROR_CODE_NONE;
                break;
            default:
                //支付失败
                mErrorCode = IPayCallBack.ERROR_CODE_FAILED;
                break;
        }
        finish();
    }

//    private String genNonceStr() {
//        Random random = new Random();
//        return MD5.getStringMD5(String.valueOf(random.nextInt(10000)));
//    }
//
//    private long genTimeStamp() {
//        return System.currentTimeMillis() / 1000;
//    }

//    private String genAppSign(List<NameValuePair> params) {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < params.size(); i++) {
//            sb.append(params.get(i).name);
//            sb.append('=');
//            sb.append(params.get(i).value);
//            sb.append('&');
//        }
//        sb.append("key=");
//        sb.append(mWeChatApiKey);
//        return MD5.getStringMD5(sb.toString()).toUpperCase();
//    }

//    private static class NameValuePair {
//        public String name;
//        public String value;
//
//        NameValuePair(String name, String value) {
//            this.name = name;
//            this.value = value;
//        }
//    }
}
