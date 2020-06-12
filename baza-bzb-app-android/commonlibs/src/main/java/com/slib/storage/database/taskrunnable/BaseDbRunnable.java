package com.slib.storage.database.taskrunnable;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.slib.storage.database.core.DataBaseManager;
import com.slib.storage.database.core.TableManager;
import com.slib.storage.database.listener.IDBReplyListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Vincent.Lei on 2019/1/31.
 * Title：
 * Note：
 */
public abstract class BaseDbRunnable<T, V> implements Runnable {
    public static class Param<T> {
        public T t;
        public String[] columns;
        public String whereClause;
        public String[] whereArgs;

        public Param() {
        }

        public Param(T t, String[] columns, String whereClause, String[] whereArgs) {
            this.t = t;
            this.columns = columns;
            this.whereClause = whereClause;
            this.whereArgs = whereArgs;
        }
    }

    private static ExecutorService singleThreadExecutor;

    static {
        singleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    public static void execute(Runnable runnable) {
        singleThreadExecutor.execute(runnable);
    }

    public void submit() {
        execute(this);
    }

    protected IDBReplyListener<V> replyListener;
    protected V resultV;
    protected boolean syncResultToUIThread;

    public BaseDbRunnable(IDBReplyListener<V> replyListener, boolean syncResultToUIThread) {
        this.replyListener = replyListener;
        this.syncResultToUIThread = syncResultToUIThread;
    }

    @Override
    public final void run() {
        DataBaseManager dataBaseManager = DataBaseManager.getInstance(null);
        if (dataBaseManager == null)
            throw new IllegalArgumentException("please init DataBaseManager suggest init int Application");
        try {
            SQLiteDatabase db = dataBaseManager.getDataBase();
            Class<?>[] classZZ = getDependModeClass();
            if (classZZ != null && classZZ.length > 0) {
                for (int i = 0; i < classZZ.length; i++) {
                    TableManager.updateTable(db, classZZ[i]);
                }
            }
            resultV = dbOperate(dataBaseManager);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            try {
//                Log.d("WorkRunnable", "--------closeDataBase---------");
//                dataBaseManager.closeDataBase();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            try {
                if (syncResultToUIThread && dataBaseManager.getUIHandler() != null)
                    dataBaseManager.getUIHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callBack();
                        }
                    });
                else if (!syncResultToUIThread)
                    callBack();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    protected abstract V dbOperate(DataBaseManager dataBaseManager);

    protected abstract Class<T>[] getDependModeClass();

    private void callBack() {
        if (replyListener != null)
            replyListener.onDBReply(resultV);
    }

}
