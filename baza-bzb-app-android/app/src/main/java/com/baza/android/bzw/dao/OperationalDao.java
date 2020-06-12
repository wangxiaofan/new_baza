package com.baza.android.bzw.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.common.CountCalculateBean;
import com.baza.android.bzw.bean.operational.ActivityStatusResultBean;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.slib.http.HttpRequestUtil;
import com.slib.http.INetworkCallBack;
import com.slib.storage.database.DBWorker;
import com.slib.storage.database.core.DataBaseManager;
import com.slib.storage.database.core.DbClassUtil;
import com.slib.storage.database.handler.IDBControllerHandler;
import com.slib.storage.database.listener.IDBReplyListener;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/8/15.
 * Title：
 * Note：
 */

public class OperationalDao {
    private OperationalDao() {
    }

    public static final int ACTIVITY_ID_NEW_VERSION = 29;

    public static void loadStatusOfTargetActivity(int activityId, final IDefaultRequestReplyListener<ActivityStatusResultBean.Data> listener) {
        HashMap<String, String> param = new HashMap<>(1);
        param.put("activityId", String.valueOf(activityId));
        HttpRequestUtil.doHttpPost(URLConst.URL_CHECK_ACTIVITY_STATUS, param, ActivityStatusResultBean.class, new INetworkCallBack<ActivityStatusResultBean>() {
            @Override
            public void onSuccess(ActivityStatusResultBean activityStatusResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, activityStatusResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void queryTargetActivityDialogShowCount(final String unionId, final int activityId, final IDBReplyListener<Integer> listener) {
        DBWorker.query(CountCalculateBean.class, "type = ? and  unionId= ?", new String[]{String.valueOf(getCountCalculateTypeOfActivity(activityId)), unionId}, new IDBReplyListener<List<CountCalculateBean>>() {
            @Override
            public void onDBReply(List<CountCalculateBean> countCalculateBeans) {
                if (listener != null)
                    listener.onDBReply((countCalculateBeans != null && countCalculateBeans.size() > 0) ? countCalculateBeans.get(0).count : 0);
            }
        });
    }

    private static int getCountCalculateTypeOfActivity(int activityId) {
        int type = -1;
        switch (activityId) {
            case ACTIVITY_ID_NEW_VERSION:
                return CountCalculateBean.TYPE_ACTIVITY_NEW_VERSION;
        }
        return type;
    }

    public static void updateTargetActivityDialogShowCount(final String unionId, final int activityId) {
        DBWorker.customerDBTask(unionId, new IDBControllerHandler<String, Void>() {
            @Override
            public Void operateDataBaseAsync(DataBaseManager mDBManager, String input) {
                String tableName = DbClassUtil.getTableNameByAnnotationClass(CountCalculateBean.class);
                int type = getCountCalculateTypeOfActivity(activityId);
                int count = 0;
                boolean exist = false;
                String where = "type = ? and  unionId= ?";
                String[] whereArgs = new String[]{String.valueOf(type), unionId};
                Cursor cursor = mDBManager.query(tableName, null, where, whereArgs, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    exist = true;
                    cursor.moveToNext();
                    count = cursor.getInt(cursor.getColumnIndex("count"));
                    cursor.close();
                }
                count++;
                ContentValues contentValues = new ContentValues();
                contentValues.put("unionId", unionId);
                contentValues.put("type", type);
                contentValues.put("count", count);
                if (exist)
                    mDBManager.update(tableName, contentValues, where, whereArgs);
                else
                    mDBManager.insert(tableName, contentValues);
                return null;
            }

            @Override
            public Class<?>[] getDependModeClass() {
                return new Class[0];
            }
        }, null, false);
    }

    public static void receiveNewVersionGift(final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        HttpRequestUtil.doHttpPost(URLConst.URL_NEW_GIFT, null, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
            @Override
            public void onSuccess(BaseHttpResultBean baseHttpResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, baseHttpResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }
}
