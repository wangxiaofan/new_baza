package com.slib.storage.database.taskrunnable;

import com.slib.storage.database.core.DataBaseManager;
import com.slib.storage.database.core.DbClassUtil;
import com.slib.storage.database.listener.IDBReplyListener;

/**
 * Created by Vincent.Lei on 2019/2/1.
 * Title：
 * Note：
 */
public class DeleteRunnable<T, V> extends BaseDbRunnable {
    private Class<V> dependModeClass;
    private Param<T> param;

    public DeleteRunnable(Class<V> dependModeClass, Param<T> param, IDBReplyListener replyListener, boolean syncResultToUIThread) {
        super(replyListener, syncResultToUIThread);
        this.dependModeClass = dependModeClass;
        this.param = param;
        if (param == null)
            throw new IllegalArgumentException("Param can not be null");
    }

    @Override
    protected Object dbOperate(DataBaseManager dataBaseManager) {
        return dataBaseManager.delete(DbClassUtil.getTableNameByAnnotationClass(dependModeClass), param.whereClause, param.whereArgs);
    }

    @Override
    protected Class[] getDependModeClass() {
        return new Class[]{dependModeClass};
    }
}
