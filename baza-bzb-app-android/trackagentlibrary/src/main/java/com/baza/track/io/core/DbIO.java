package com.baza.track.io.core;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.baza.track.io.bean.TrackEventBean;
import com.baza.track.io.constant.TrackConst;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DbIO {
    private static DbIO mInstance;

    public static DbIO getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DbIO.class) {
                if (mInstance == null && context != null) {
                    mInstance = new DbIO(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    private DbHelper mDbHelper;
    private ExecutorService mSingleThreadExecutor;
    private SQLiteDatabase mDb;

    private DbIO(Context context) {
        mDbHelper = new DbHelper(context);
        mSingleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    private SQLiteDatabase openDataBase() {
        if (mDb == null || !mDb.isOpen())
            mDb = mDbHelper.getWritableDatabase();
        return mDb;
    }

    private void closeDataBase() {
        if (mDb != null && mDb.isOpen())
            mDb.close();
        mDb = null;
    }

    private SQLiteDatabase getDataBase() {
        if (mDb == null || !mDb.isOpen())
            openDataBase();
        return mDb;
    }

    private Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return mDb.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    private Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return mDb.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    private Cursor query(String sql) {
        return mDb.rawQuery(sql, null);
    }

    private int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        return mDb.update(table, values, whereClause, whereArgs);
    }

    private void update(String sql) {
        mDb.execSQL(sql);
    }

    private long insert(String table, ContentValues values) {
        return mDb.insert(table, null, values);
    }

    private void insert(String sql) {
        mDb.execSQL(sql);
    }

    private int delete(String table, String whereClause, String[] whereArgs) {
        return mDb.delete(table, whereClause, whereArgs);
    }

    private void delete(String sql) {
        mDb.execSQL(sql);
    }

    private void beginTransaction() {
        mDb.beginTransaction();
    }

    private void setTransactionSuccessful() {
        mDb.setTransactionSuccessful();
    }

    private void endTransaction() {
        mDb.endTransaction();
    }

    /*******
     *
     * @param eventData
     */
    void saveEvent(TrackEventBean eventData) {
        mSingleThreadExecutor.execute(new WorkRunnable<>(new WorkRunnable.IDBControllerHandler<TrackEventBean, Void>() {
            @Override
            public Void operateDataBaseAsync(TrackEventBean input, DbIO dbIO) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(TrackConst.Db.TRACK_COLUMN_TYPE, input.type);
                contentValues.put(TrackConst.Db.TRACK_COLUMN_VALUE, input.value);
                contentValues.put(TrackConst.Db.TRACK_COLUMN_TIME, input.time);
                dbIO.insert(TrackConst.Db.TABLE_TRACK, contentValues);
                return null;
            }
        }, null, eventData));
    }

    void deleteEvents(List<TrackEventBean> list, WorkRunnable.IDBReply<Void> reply) {
        mSingleThreadExecutor.execute(new WorkRunnable<>(new WorkRunnable.IDBControllerHandler<List<TrackEventBean>, Void>() {
            @Override
            public Void operateDataBaseAsync(List<TrackEventBean> input, DbIO dbIO) {
                if (input == null || input.isEmpty())
                    return null;
                StringBuilder stringBuilder = new StringBuilder(" id in (");
                for (TrackEventBean e : input) {
                    stringBuilder.append(e.id).append(",");
                }
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                stringBuilder.append(" )");
                dbIO.delete(TrackConst.Db.TABLE_TRACK, stringBuilder.toString(), null);
                return null;
            }
        }, reply, list));
    }

    void readEvents(int count, String type, WorkRunnable.IDBReply<List<TrackEventBean>> reply) {
        QueryParam queryParam = new QueryParam(count, type);
        mSingleThreadExecutor.execute(new WorkRunnable<>(new WorkRunnable.IDBControllerHandler<QueryParam, List<TrackEventBean>>() {
            @Override
            public List<TrackEventBean> operateDataBaseAsync(QueryParam input, DbIO dbIO) {
                if (input == null)
                    return null;
                Cursor cursor = null;
                List<TrackEventBean> eventDataList = null;
                try {
                    cursor = dbIO.query(TrackConst.Db.TABLE_TRACK, null, "type = ?", new String[]{input.type}, null, null, null, String.valueOf(input.limit));
                    if (cursor != null && cursor.getCount() > 0) {
                        eventDataList = new ArrayList<>(cursor.getCount());
                        TrackEventBean eventData;
                        while (cursor.moveToNext()) {
                            eventData = new TrackEventBean();
                            eventData.id = cursor.getInt(cursor.getColumnIndex(TrackConst.Db.TRACK_COLUMN_ID));
                            eventData.time = cursor.getInt(cursor.getColumnIndex(TrackConst.Db.TRACK_COLUMN_TIME));
                            eventData.type = cursor.getString(cursor.getColumnIndex(TrackConst.Db.TRACK_COLUMN_TYPE));
                            eventData.value = cursor.getString(cursor.getColumnIndex(TrackConst.Db.TRACK_COLUMN_VALUE));
                            eventDataList.add(eventData);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
                return eventDataList;
            }
        }, reply, queryParam));
    }

    static class WorkRunnable<T, V> implements Runnable {

        public interface IDBControllerHandler<T, V> {
            V operateDataBaseAsync(T input, DbIO dbIO);
        }

        public interface IDBReply<V> {
            void onDBReply(V result);
        }

        private IDBControllerHandler<T, V> controllerHandler;
        private IDBReply<V> dbReply;
        private T t;
        private V v;

        WorkRunnable(IDBControllerHandler<T, V> controllerHandler, IDBReply<V> dbReply, T t) {
            this.controllerHandler = controllerHandler;
            this.dbReply = dbReply;
            this.t = t;
        }

        @Override
        public void run() {
            DbIO dbIO = DbIO.getInstance(null);
            if (dbIO == null)
                throw new RuntimeException("please init BaZaIO");
            try {
                dbIO.openDataBase();
                dbIO.beginTransaction();
                v = controllerHandler.operateDataBaseAsync(t, dbIO);
                dbIO.setTransactionSuccessful();
            } catch (Exception e) {
                //ignore
                e.printStackTrace();
            } finally {
                try {
                    dbIO.endTransaction();
                    dbIO.closeDataBase();
                } catch (Exception e) {
                    //ignore
                }

                try {
                    if (dbReply != null)
                        dbReply.onDBReply(v);
                } catch (Exception e) {
                    e.printStackTrace();
                    //ignore
                }
            }

        }
    }

    private static class DbHelper extends SQLiteOpenHelper {
        DbHelper(Context context) {
            this(context, TrackConst.Db.DB_PROCESS + "_" + TrackConst.Db.DB_NAME, null, TrackConst.Db.DB_VERSION);
        }

        DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "CREATE TABLE " + TrackConst.Db.TABLE_TRACK + " ("
                    + TrackConst.Db.TRACK_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                    + TrackConst.Db.TRACK_COLUMN_TYPE + " TEXT ,"
                    + TrackConst.Db.TRACK_COLUMN_VALUE + " TEXT ,"
                    + TrackConst.Db.TRACK_COLUMN_TIME + " INTEGER"
                    + " )";
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    private static class QueryParam {
        int limit;
        String type;

        QueryParam(int limit, String type) {
            this.limit = limit;
            this.type = type;
        }
    }
}
