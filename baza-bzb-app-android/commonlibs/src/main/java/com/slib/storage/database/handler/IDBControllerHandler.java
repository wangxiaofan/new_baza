package com.slib.storage.database.handler;

import com.slib.storage.database.core.DataBaseManager;

/**
 * Created by Vincent.Lei on 2016/11/8.
 * Title :
 * Note :
 */

public interface IDBControllerHandler<T, V> {
    V operateDataBaseAsync(DataBaseManager mDBManager, T input);

    Class<?> []getDependModeClass();
}
