package social;

/**
 * Created by Vincent.Lei on 2017/7/26.
 * Title：
 * Note：
 */

public interface ISocialLoginCallBack extends ICallBack {
    int ERROR_CODE_WECHAT_NOT_INSTALL = 6;
    int ERROR_CODE_OK = 1;
    int ERROR_CODE_AUTH_DENIED = 2;
    int ERROR_CODE_AUTH_CANCEL = 3;
    int ERROR_CODE_NO_RESULT = 4;

    void onSocialLoginResult(boolean success, int resultCode, String code);
}
