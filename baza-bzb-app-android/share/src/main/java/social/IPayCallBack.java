package social;

/**
 * Created by Vincent.Lei on 2018/2/23.
 * Title：
 * Note：
 */

public interface IPayCallBack extends ICallBack {
    int ERROR_CODE_NONE = 0;
    int ERROR_CODE_WX_INSTALL_OR_LEVEL = -1;
    int ERROR_CODE_FAILED = -2;
    int ERROR_CODE_CANCELED = -3;

    void onPayReply(boolean success, int errorCode);
}
