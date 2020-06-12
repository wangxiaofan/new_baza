package com.slib.storage.database.taskrunnable;

import android.content.ContentValues;
import android.database.Cursor;

import com.slib.storage.database.annotation.TableAnnotation;
import com.slib.storage.database.core.DataBaseManager;
import com.slib.storage.database.core.DbClassUtil;
import com.slib.storage.database.listener.IDBReplyListener;

import java.util.List;

/**
 * Created by Vincent.Lei on 2019/1/9.
 * Title：
 * Note：
 */
public class SaveRunnable<T, V> extends BaseDbRunnable {
    private Param<T> param;
    private List<Param<T>> paramList;
    private String tableName;

    public SaveRunnable(Param<T> param, IDBReplyListener<V> replyListener, boolean syncResultToUIThread) {
        super(replyListener, syncResultToUIThread);
        this.param = param;
        if (param == null)
            throw new IllegalArgumentException("Param can not be null");

    }

    public SaveRunnable(List<Param<T>> paramList, IDBReplyListener<V> replyListener, boolean syncResultToUIThread) {
        super(replyListener, syncResultToUIThread);
        this.paramList = paramList;
        if (paramList == null || paramList.isEmpty())
            throw new IllegalArgumentException("Param can not be null");

    }

    private void saveSingle(DataBaseManager dataBaseManager, Param<T> param) {
        ContentValues contentValues = DbClassUtil.buildContentValues(param.t, param.columns);
        boolean isUpdate = false;
        if (param.whereClause != null) {
            Cursor cursor = dataBaseManager.query(tableName, null, param.whereClause, param.whereArgs, null, null, null, " 0,1 ");
            if (cursor != null) {
                isUpdate = cursor.getCount() > 0;
                cursor.close();
            }
        }
        if (isUpdate)
            dataBaseManager.update(tableName, contentValues, param.whereClause, param.whereArgs);
        else
            dataBaseManager.insert(tableName, contentValues);
    }

    private void saveList(DataBaseManager dataBaseManager) {
        dataBaseManager.beginTransaction();
        for (int i = 0, size = paramList.size(); i < size; i++) {
            saveSingle(dataBaseManager, paramList.get(i));
        }
        dataBaseManager.setTransactionSuccessful();
        dataBaseManager.endTransaction();
    }

    @Override
    protected V dbOperate(DataBaseManager dataBaseManager) {
        Class<?> tClass = getDependModeClass()[0];
        TableAnnotation tableAnnotation = tClass.getAnnotation(TableAnnotation.class);
        tableName = tableAnnotation.tableName();
        if (param != null)
            saveSingle(dataBaseManager, param);
        else
            saveList(dataBaseManager);
        return null;
    }

    @Override
    protected Class[] getDependModeClass() {
        return new Class[]{(param != null ? param.t.getClass() : (paramList.get(0).t.getClass()))};
    }
}
