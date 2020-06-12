package com.slib.storage.database.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;


/**
 * Created by Vincent.Lei on 2016/11/7.
 * Title :
 * Note :
 */

public class DataBaseManager {

    private static DataBaseManager mDataBaseManager;
    private Context context;
    private DataBaseHelper dbHelper;
    private SQLiteDatabase db;
    private Handler mHandler;

    public void initDataBase(String dbName, int dbVersion) {
        if (TextUtils.isEmpty(dbName))
            dbName = context.getPackageName() + "_db.db";
        TableManager.DB_NAME = dbName;
        this.mHandler = new Handler(Looper.getMainLooper());
        this.dbHelper = new DataBaseHelper(this.context, dbVersion);
    }

    private DataBaseManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static DataBaseManager getInstance(Context context) {
        if (mDataBaseManager == null) {
            synchronized (DataBaseManager.class) {
                if (mDataBaseManager == null)
                    mDataBaseManager = new DataBaseManager(context);
            }
        }
        return mDataBaseManager;
    }

    private SQLiteDatabase openDataBase() {
        if (db == null || !db.isOpen())
            db = dbHelper.getWritableDatabase();
        return db;
    }

    public void closeDataBase() {
        if (db != null && db.isOpen())
            db.close();
        db = null;
    }

    public SQLiteDatabase getDataBase() {
        if (db == null || !db.isOpen())
            openDataBase();
        return db;
    }

    public Handler getUIHandler() {
        return mHandler;
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    public Cursor query(String sql) {
        return db.rawQuery(sql, null);
    }

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        return db.update(table, values, whereClause, whereArgs);
    }

    public void update(String sql) {
        db.execSQL(sql);
    }

    public long insert(String table, ContentValues values) {
        return db.insert(table, null, values);
    }

    public void insert(String sql) {
        db.execSQL(sql);
    }

    public int delete(String table, String whereClause, String[] whereArgs) {
        return db.delete(table, whereClause, whereArgs);
    }

    public void delete(String sql) {
        db.execSQL(sql);
    }

    public void checkTable(Class<?> classZZ) {
        TableManager.updateTable(db, classZZ);
    }

    public void beginTransaction() {
        db.beginTransaction();
    }

    public void setTransactionSuccessful() {
        db.setTransactionSuccessful();
    }

    public void endTransaction() {
        db.endTransaction();
    }


}
