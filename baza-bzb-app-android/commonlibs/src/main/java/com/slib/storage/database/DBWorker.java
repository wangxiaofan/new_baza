package com.slib.storage.database;

import android.content.Context;

import com.slib.storage.database.core.DataBaseManager;
import com.slib.storage.database.handler.IDBControllerHandler;
import com.slib.storage.database.listener.IDBReplyListener;
import com.slib.storage.database.taskrunnable.BaseDbRunnable;
import com.slib.storage.database.taskrunnable.CustomerRunnable;
import com.slib.storage.database.taskrunnable.DeleteRunnable;
import com.slib.storage.database.taskrunnable.QueryRunnable;
import com.slib.storage.database.taskrunnable.SaveRunnable;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Vincent.Lei on 2016/11/8.
 * Title :
 * Note :
 */

public class DBWorker {

    public static void initDataBase(Context context, String dbName, int dbVersion) {
        DataBaseManager.getInstance(context).initDataBase(dbName, dbVersion);
    }

    public static <T, V> void saveSingle(T t, IDBReplyListener<V> replyListener) {
        saveSingle(t, null, null, null, replyListener, true);
    }

    public static <T, V> void saveSingle(T t, String[] columns, String whereClause, String[] whereArgs, IDBReplyListener<V> replyListener, boolean syncResultToUIThread) {
        new SaveRunnable<T, V>(new BaseDbRunnable.Param<T>(t, columns, whereClause, whereArgs), replyListener, syncResultToUIThread).submit();
    }

    public static <T, V> void saveList(List<T> list, IDBReplyListener<V> replyListener) {
        saveList(list, null, null, null, replyListener, true);
    }

    public static <T, V> void saveList(List<T> list, List<String[]> columnsList, List<String> whereClauseList, List<String[]> whereArgsList, IDBReplyListener<V> replyListener, boolean syncResultToUIThread) {
        List<BaseDbRunnable.Param<T>> paramList = new ArrayList<>(list.size());
        BaseDbRunnable.Param<T> param;
        boolean columnsMatch = (columnsList != null && columnsList.size() == list.size());
        boolean whereClauseMatch = (whereClauseList != null && whereClauseList.size() == list.size());
        boolean whereArgsMatch = (whereArgsList != null && whereArgsList.size() == list.size());
        for (int i = 0, size = list.size(); i < size; i++) {
            param = new BaseDbRunnable.Param<>();
            param.t = list.get(i);
            param.columns = (columnsMatch ? columnsList.get(i) : null);
            param.whereClause = (whereClauseMatch ? whereClauseList.get(i) : null);
            param.whereArgs = (whereArgsMatch ? whereArgsList.get(i) : null);
            paramList.add(param);
        }
        new SaveRunnable<T, V>(paramList, replyListener, syncResultToUIThread).submit();
    }

    public static <T, V> void query(Class<V> dependModeClass, String selection, String[] selectionArgs, IDBReplyListener<List<V>> listener) {
        query(dependModeClass, null, selection, selectionArgs, null, null, null, null, listener, true);
    }

    public static <T, V> void query(Class<V> dependModeClass, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, IDBReplyListener<List<V>> listener, boolean syncResultToUIThread) {
        QueryRunnable.QueryParam<T> queryParam = new QueryRunnable.QueryParam<>();
        queryParam.columns = columns;
        queryParam.whereClause = selection;
        queryParam.whereArgs = selectionArgs;
        queryParam.groupBy = groupBy;
        queryParam.having = having;
        queryParam.orderBy = orderBy;
        queryParam.limit = limit;
        new QueryRunnable<T, V>(queryParam, dependModeClass, listener, syncResultToUIThread).submit();
    }

    public static <T, V> void delete(Class<V> dependModeClass, String whereClause, String[] whereArgs) {
        delete(dependModeClass, whereClause, whereArgs, null, false);
    }

    public static <T, V> void delete(Class<V> dependModeClass, String whereClause, String[] whereArgs, IDBReplyListener<V> replyListener, boolean syncResultToUIThread) {
        new DeleteRunnable<T, V>(dependModeClass, new BaseDbRunnable.Param<T>(null, null, whereClause, whereArgs), replyListener, syncResultToUIThread).submit();
    }

    public static <T, V> void customerDBTask(T input, IDBControllerHandler<T, V> dbController, IDBReplyListener<V> replyListener, boolean syncResultToUIThread) {
        new CustomerRunnable<T, V>(input, dbController, replyListener, syncResultToUIThread).submit();
    }
}
