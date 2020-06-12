package com.slib.storage.database.taskrunnable;

import com.slib.storage.database.core.DataBaseManager;
import com.slib.storage.database.handler.IDBControllerHandler;
import com.slib.storage.database.listener.IDBReplyListener;

/**
 * Created by Vincent.Lei on 2019/1/31.
 * Title：
 * Note：
 */
public class CustomerRunnable<T, V> extends BaseDbRunnable {
    private T t;
    private IDBControllerHandler<T, V> controllerHandler;

    public CustomerRunnable(T t, IDBControllerHandler<T, V> controllerHandler, IDBReplyListener replyListener, boolean syncResultToUIThread) {
        super(replyListener, syncResultToUIThread);
        this.t = t;
        this.controllerHandler = controllerHandler;
        if (controllerHandler == null)
            throw new IllegalArgumentException("IDBControllerHandler can not be null");
    }

    @Override
    protected V dbOperate(DataBaseManager dataBaseManager) {
        return controllerHandler.operateDataBaseAsync(dataBaseManager, t);
    }

    @Override
    protected Class[] getDependModeClass() {
        return controllerHandler.getDependModeClass();
    }
}
