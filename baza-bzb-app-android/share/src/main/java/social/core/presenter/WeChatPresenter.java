package social.core.presenter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;

import com.slib.storage.file.ImageManager;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import social.IShareCallBack;
import social.ISocialLoginCallBack;
import social.SocialHelper;
import social.SocialLoginHelper;
import social.core.viewInterface.IWeChatView;
import social.data.ShareData;

/**
 * Created by Vincent.Lei on 2016/12/9.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class WeChatPresenter implements IWXAPIEventHandler {
    public static final int SHARE_TO_CONTACT = 1;
    public static final int SHARE_TO_FRIEND_CIRCLE = 2;
    public static final int JOB_TYPE_SHARE = 0;
    public static final int JOB_TYPE_LOGIN = 1;

    private static final int MAX_THUMB_KB = 50;
    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MAX_SUMMARY_LENGTH = 100;
    private static final int MIN_WECHAT_VERSION_FOR_FRIEND_CIRCLE = 0x21020001;
    private IWeChatView mWeChatView;
    private Activity mActivity;
    private ShareData mShareData;
    private int mPlatformType;
    private int mJobType;
    private int mResultCode;
    private String mShareResultMsg;
    private Object mObjectData;
    private String mKeyId;

    private IWXAPI mIwxapi;

    public WeChatPresenter(IWeChatView mWeChatView, Activity activity, Intent intent) {
        this.mWeChatView = mWeChatView;
        this.mActivity = activity;

        mShareData = (ShareData) intent.getSerializableExtra("shareData");
        mKeyId = intent.getStringExtra("keyId");
        mPlatformType = intent.getIntExtra("platformType", SHARE_TO_CONTACT);
        mJobType = intent.getIntExtra("jobType", JOB_TYPE_SHARE);
    }

    public void init() {
        mIwxapi = WXAPIFactory.createWXAPI(mActivity, SocialHelper.getInstance().getWeChatAppId(), true);
        mIwxapi.registerApp(SocialHelper.getInstance().getWeChatAppId());
        switch (mJobType) {
            case JOB_TYPE_LOGIN:
                mResultCode = ISocialLoginCallBack.ERROR_CODE_NO_RESULT;
                jobLogin();
                break;
            case JOB_TYPE_SHARE:
                mResultCode = IShareCallBack.ERROR_CODE_NO_RESULT;
                jobShare();
                break;
        }

    }

    public void handleIntent(Intent intent) {
        mIwxapi.handleIntent(intent, this);
    }

    private void jobLogin() {
        if (!mIwxapi.isWXAppInstalled()) {
            mResultCode = ISocialLoginCallBack.ERROR_CODE_WECHAT_NOT_INSTALL;
            mWeChatView.callClosePage();
            return;
        }
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = buildTransaction("login");
        mIwxapi.sendReq(req);
    }

    private void jobShare() {
        if (!mIwxapi.isWXAppInstalled()) {
            mResultCode = IShareCallBack.ERROR_CODE_WECHAT_NOT_INSTALL;
            mWeChatView.callClosePage();
            return;
        }
        //朋友圈微信版本必须高于MIN_WECHAT_VERSION_FOR_FRIEND_CIRCLE
        if (mPlatformType == SHARE_TO_FRIEND_CIRCLE && mIwxapi.getWXAppSupportAPI() < MIN_WECHAT_VERSION_FOR_FRIEND_CIRCLE) {
            mResultCode = IShareCallBack.ERROR_CODE_WECHAT_LEVEL_LOW;
            mWeChatView.callClosePage();
            return;
        }
        if (mShareData.getTitle().length() > MAX_TITLE_LENGTH)
            mShareData.setTitle(mShareData.getTitle().substring(0, MAX_TITLE_LENGTH));
        if (mShareData.getSummary().length() > MAX_SUMMARY_LENGTH)
            mShareData.setSummary(mShareData.getSummary().substring(0, MAX_SUMMARY_LENGTH));

        switch (mShareData.getDataType()) {
            case ShareData.DATA_TYPE_WECHAT_TEXT:
                //分享文字
                shareText();
                break;
            case ShareData.DATA_TYPE_WECHAT_IMAGE:
                //分享图片
                shareImage();
                break;
            case ShareData.DATA_TYPE_WECHAT_LINK:
                //分享链接
                shareLink();
                break;
        }
    }


    public void setResultBack() {
        if (mJobType == JOB_TYPE_SHARE)
            SocialHelper.getInstance().noticeShareResult(mShareData.getKeyId(), mResultCode == IShareCallBack.ERROR_CODE_COMPLETE, mResultCode, mShareResultMsg);
        else
            SocialLoginHelper.getInstance().noticeLoginResult(mKeyId, mResultCode == ISocialLoginCallBack.ERROR_CODE_OK, mResultCode, mObjectData);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (mJobType == JOB_TYPE_SHARE)
            onJobShareResp(baseResp);
        else
            onJobLoginResp(baseResp);
    }

    private void onJobLoginResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                mResultCode = ISocialLoginCallBack.ERROR_CODE_OK;
                SendAuth.Resp resp = (SendAuth.Resp) baseResp;
                mObjectData = resp.code;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                mResultCode = ISocialLoginCallBack.ERROR_CODE_AUTH_DENIED;
                break;
            default:
                mResultCode = ISocialLoginCallBack.ERROR_CODE_AUTH_CANCEL;
                break;
        }
    }

    private void onJobShareResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                mResultCode = IShareCallBack.ERROR_CODE_COMPLETE;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                mResultCode = IShareCallBack.ERROR_CODE_CANCEL;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                mResultCode = IShareCallBack.ERROR_CODE_ERROR;
                mShareResultMsg = baseResp.errStr;
                break;
        }
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private void shareText() {
        // 初始化一个WXTextObject对象
        WXTextObject textObj = new WXTextObject();
        textObj.text = mShareData.getTitle();
        // 用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        // 发送文本类型的消息时，title字段不起作用
        msg.description = mShareData.getSummary();
        // 构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
        req.message = msg;
        doSend(req);
    }

    private void shareImage() {
        WXImageObject imgObj = new WXImageObject();
        imgObj.setImagePath(mShareData.getImageLocalPath());
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        //缩略图
        Bitmap bitmap = ImageManager.decodeBitmap(mShareData.getImageLocalPath(), 150, 150);
        msg.thumbData = ImageManager.bitmapToByteArray(bitmap, MAX_THUMB_KB, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        doSend(req);
    }

    private void shareLink() {
        WXWebpageObject wxWebpageObject = new WXWebpageObject();
        wxWebpageObject.webpageUrl = mShareData.getShareLink();
        WXMediaMessage msg = new WXMediaMessage(wxWebpageObject);
        msg.title = mShareData.getTitle();
        msg.description = mShareData.getSummary();
        //缩略图
        Bitmap bitmap = ImageManager.decodeBitmap(mShareData.getImageLocalPath(), 150, 150);
        msg.thumbData = ImageManager.bitmapToByteArray(bitmap, MAX_THUMB_KB, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        doSend(req);

    }

    private void doSend(SendMessageToWX.Req req) {
        //WXSceneTimeline 朋友圈
        req.scene = (mPlatformType == SHARE_TO_FRIEND_CIRCLE) ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        // 调用api接口发送数据到微信
        mIwxapi.sendReq(req);
    }

}
