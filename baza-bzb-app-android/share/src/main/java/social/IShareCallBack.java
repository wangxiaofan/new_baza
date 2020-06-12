package social;

/**
 * Created by Vincent.Lei on 2016/12/8.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public interface IShareCallBack extends ICallBack{
    int ERROR_CODE_CANCEL = 1;
    int ERROR_CODE_COMPLETE = 2;
    int ERROR_CODE_ERROR = 3;
    int ERROR_CODE_NO_RESULT = 4;
    int ERROR_CODE_QQ_NOT_INSTALL = 5;
    int ERROR_CODE_WECHAT_NOT_INSTALL = 6;
    int ERROR_CODE_WECHAT_LEVEL_LOW = 7;

    void onShareReply(boolean success, int errorCode, String errorMsg);
}
