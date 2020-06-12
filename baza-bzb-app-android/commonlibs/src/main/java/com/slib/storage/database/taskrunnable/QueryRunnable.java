package com.slib.storage.database.taskrunnable;

import android.database.Cursor;

import com.slib.storage.database.core.DataBaseManager;
import com.slib.storage.database.core.DbClassUtil;
import com.slib.storage.database.listener.IDBReplyListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2019/2/1.
 * Title：
 * Note：
 */
public class QueryRunnable<T, V> extends BaseDbRunnable {

    public static class QueryParam<T> extends Param {
        public String groupBy;
        public String having;
        public String orderBy;
        public String limit;
    }

    private QueryParam<T> queryParam;
    private Class<V> dependModeClass;

    public QueryRunnable(QueryParam<T> queryParam, Class<V> dependModeClass, IDBReplyListener<List<V>> replyListener, boolean syncResultToUIThread) {
        super(replyListener, syncResultToUIThread);
        this.queryParam = queryParam;
        this.dependModeClass = dependModeClass;
        if (queryParam == null)
            throw new IllegalArgumentException("queryParam can not be null");
    }

    @Override
    protected List<V> dbOperate(DataBaseManager dataBaseManager) {
        String tableName = DbClassUtil.getTableNameByAnnotationClass(dependModeClass);
        Cursor cursor = dataBaseManager.query(tableName, queryParam != null ? queryParam.columns : null, queryParam != null ? queryParam.whereClause : null, queryParam != null ? queryParam.whereArgs : null, queryParam != null ? queryParam.groupBy : null, queryParam != null ? queryParam.having : null, queryParam != null ? queryParam.orderBy : null, queryParam != null ? queryParam.limit : null);
        List<V> resultList = null;
        if (cursor != null && cursor.getCount() > 0) {
            resultList = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext()) {
                resultList.add(DbClassUtil.buildObject(dependModeClass, cursor, queryParam.columns));
            }
        }
        return resultList;
    }

    @Override
    protected Class[] getDependModeClass() {
        return new Class[]{dependModeClass};
    }
}
