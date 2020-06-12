package social.core;

import java.util.HashMap;

import social.ICallBack;

/**
 * Created by Vincent.Lei on 2016/12/8.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class SubscriberKeeper {
    private static HashMap<String, ICallBack> mKeeper = new HashMap<>();

    public static void addCallBack(String key, ICallBack callBack) {
        if (key != null && callBack != null)
            mKeeper.put(key, callBack);
    }

    public static ICallBack getCallBack(String key) {
        if (key != null)
            return mKeeper.remove(key);
        return null;
    }
}
