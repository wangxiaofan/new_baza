package social;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.slib.storage.file.FileManager;
import com.slib.storage.file.ImageManager;
import com.slib.storage.file.StorageType;

import java.io.File;
import java.util.UUID;

import social.core.ShareToQQActivity;
import social.core.SubscriberKeeper;
import social.core.presenter.WeChatPresenter;
import social.data.PayData;
import social.data.ShareData;

/**
 * Created by Vincent.Lei on 2016/12/7.
 * Title : 分享
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class SocialHelper {

    private static final int MAX_DEFAULT_SHARE_DRAWABLE_KB = 50;
    private static final String DEFAULT_SHARE_DRAWABLE_FILE_NAME_TAG = "share_bzw_icon52_";
    //QQ平台
    private String mQQAppId;
    //微信平台
    private String mWeChatAppId;


    private SocialHelper() {
    }

    private static SocialHelper shareHelper = new SocialHelper();
    private int appDrawableId;
    private Application application;

    public static SocialHelper getInstance() {
        return shareHelper;
    }

    public SocialHelper init(Application application, int defaultShareDrawable) {
        this.appDrawableId = defaultShareDrawable;
        this.application = application;
        if (TextUtils.isEmpty(getShareDrawablePath(appDrawableId)))
            setDefaultShareDrawable(appDrawableId, false);
        return this;
    }

    public SocialHelper qqAppId(String id) {
        mQQAppId = id;
        return this;
    }

    public SocialHelper weChatAppId(String id) {
        mWeChatAppId = id;
        return this;
    }

    public String getQQAppId() {
        return mQQAppId;
    }

    public String getWeChatAppId() {
        return mWeChatAppId;
    }

    /**
     * @param isSync 是否用当前线程执行  推荐在应用初始化的时候作为异步任务执行 isSync置为false
     */
    public void setDefaultShareDrawable(int drawableId, boolean isSync) {
        drawableId = (drawableId <= 0 ? appDrawableId : drawableId);
        File file = FileManager.getFile(DEFAULT_SHARE_DRAWABLE_FILE_NAME_TAG + drawableId, StorageType.TYPE_CACHE, false);
        if (file != null && file.exists() && file.length() > 0)
            return;
        if (isSync)
            saveDefaultShareDrawable(application, drawableId);
        else
            new SaveTask(application, drawableId).execute();
    }

    public String getShareDrawablePath(int drawableId) {
        drawableId = (drawableId <= 0 ? appDrawableId : drawableId);
        File file = FileManager.getFile(DEFAULT_SHARE_DRAWABLE_FILE_NAME_TAG + drawableId, StorageType.TYPE_CACHE, false);
        if (file != null && file.exists())
            return file.getAbsolutePath();
        return null;
    }

    /**
     * 本地存一张默认Drawable 推荐应用图标作为分享必须带的默认图片
     */
    private static void saveDefaultShareDrawable(Application application, int appDrawableId) {
        Bitmap bitmap = BitmapFactory.decodeResource(application.getResources(), appDrawableId);
        byte[] buff = ImageManager.bitmapToByteArray(bitmap, MAX_DEFAULT_SHARE_DRAWABLE_KB, true);
        if (buff == null || buff.length == 0)
            return;
        String fileSavePath = FileManager.getDir(StorageType.TYPE_CACHE) + DEFAULT_SHARE_DRAWABLE_FILE_NAME_TAG + appDrawableId;
        ImageManager.saveBitmapByteArrayToLocal(buff, fileSavePath);
    }

    /**
     * 本地存一张默认Drawable 的异步任务
     */
    private static class SaveTask extends AsyncTask<Void, Void, Void> {
        private Application application;
        private int appDrawableId;

        public SaveTask(Application application, int appDrawableId) {
            this.application = application;
            this.appDrawableId = appDrawableId;
        }

        @Override
        protected Void doInBackground(Void... params) {
            saveDefaultShareDrawable(application, appDrawableId);
            return null;
        }
    }

    /**
     * 分享到QQ好友
     */
    public void shareToQQ(Context context, ShareData data, IShareCallBack shareCallBack) {
        if (context == null || data == null)
            return;
        int type = data.getDataType();
        if (type != ShareData.DATA_TYPE_QQ_IMAGE && type != ShareData.DATA_TYPE_QQ_LINK)
            throw new IllegalArgumentException("please build QQData");
        if (shareCallBack != null)
            SubscriberKeeper.addCallBack(data.getKeyId(), shareCallBack);
        ShareToQQActivity.launch(context, data);
    }

    /**
     * 分享到QZONE
     */
    public void shareToQZONE(Context context, ShareData data, IShareCallBack shareCallBack) {
        if (context == null || data == null)
            return;
        if (data.getDataType() != ShareData.DATA_TYPE_QZONE)
            throw new IllegalArgumentException("please build QZONEData");
        if (shareCallBack != null)
            SubscriberKeeper.addCallBack(data.getKeyId(), shareCallBack);
        ShareToQQActivity.launch(context, data);
    }

    /**
     * 分享到微信好友
     */
    public void shareToWeChatContact(Context context, ShareData data, IShareCallBack shareCallBack) {
        shareToWeChat(context, data, shareCallBack, WeChatPresenter.SHARE_TO_CONTACT);
    }

    /**
     * 分享到微信朋友圈
     */
    public void shareToWeChatFriendCircle(Context context, ShareData data, IShareCallBack shareCallBack) {
        shareToWeChat(context, data, shareCallBack, WeChatPresenter.SHARE_TO_FRIEND_CIRCLE);
    }

    private void shareToWeChat(Context context, ShareData data, IShareCallBack shareCallBack, int platformType) {
        if (context == null || data == null)
            return;
        if (data.getDataType() != ShareData.DATA_TYPE_WECHAT_TEXT && data.getDataType() != ShareData.DATA_TYPE_WECHAT_IMAGE && data.getDataType() != ShareData.DATA_TYPE_WECHAT_LINK)
            throw new IllegalArgumentException("please build WeChatData");
        if (shareCallBack != null)
            SubscriberKeeper.addCallBack(data.getKeyId(), shareCallBack);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(context.getPackageName(), context.getPackageName() + ".wxapi.WXEntryActivity");
        intent.putExtra("shareData", data);
        intent.putExtra("platformType", platformType);
        context.startActivity(intent);
    }

    public void payWithWeChat(Activity activity, PayData payData, IPayCallBack callBack) {
        String callBackId = UUID.randomUUID().toString();
        if (callBack != null)
            SubscriberKeeper.addCallBack(callBackId, callBack);
        Intent intent = new Intent();
        intent.putExtra("callBackId", callBackId);
        intent.putExtra("payData", payData);
        intent.setClassName(activity.getPackageName(), activity.getPackageName() + ".wxapi.WXPayEntryActivity");
        activity.startActivity(intent);
    }

    public void noticeShareResult(String keyId, boolean success, int shareResultCode, String shareResultMsg) {
        IShareCallBack callBack = (IShareCallBack) SubscriberKeeper.getCallBack(keyId);
        if (callBack != null) {
            try {
                callBack.onShareReply(success, shareResultCode, shareResultMsg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void noticePayResult(String keyId, int payResultCode) {
        IPayCallBack payCallBack = (IPayCallBack) SubscriberKeeper.getCallBack(keyId);
        if (payCallBack != null) {
            try {
                payCallBack.onPayReply(payResultCode == IPayCallBack.ERROR_CODE_NONE, payResultCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
