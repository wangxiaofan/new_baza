package social;

import android.app.Activity;
import android.content.Intent;

import java.util.UUID;

import social.core.SubscriberKeeper;
import social.core.presenter.WeChatPresenter;

/**
 * Created by Vincent.Lei on 2017/7/26.
 * Title：
 * Note：
 */

public class SocialLoginHelper {
    private SocialLoginHelper() {
    }

    public static SocialLoginHelper mInstance = new SocialLoginHelper();

    public static SocialLoginHelper getInstance() {
        return mInstance;
    }

    public void loginWithWeChat(Activity activity, ISocialLoginCallBack callBack) {
        String keyId = UUID.randomUUID().toString();
        SubscriberKeeper.addCallBack(keyId, callBack);
        Intent intent = new Intent();
        intent.setClassName(activity.getPackageName(), activity.getPackageName() + ".wxapi.WXEntryActivity");
        intent.putExtra("jobType", WeChatPresenter.JOB_TYPE_LOGIN);
        intent.putExtra("keyId", keyId);
        activity.startActivity(intent);
    }

    public void noticeLoginResult(String keyId, boolean success, int resultCode, Object resultData) {
        if (keyId == null)
            return;
        ISocialLoginCallBack socialLoginCallBack = (ISocialLoginCallBack) SubscriberKeeper.getCallBack(keyId);
        if (socialLoginCallBack == null)
            return;
        socialLoginCallBack.onSocialLoginResult(success, resultCode, (resultData == null ? null : resultData.toString()));
    }
}
