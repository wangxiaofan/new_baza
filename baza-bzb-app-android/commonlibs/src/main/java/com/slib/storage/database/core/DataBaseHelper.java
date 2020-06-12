package com.slib.storage.database.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Vincent.Lei on 2016/11/8.
 * Title :
 * Note :
 */

class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(Context context, int dbVersion) {
        this(context, TableManager.DB_NAME, null, dbVersion);
    }

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TableManager.onDataBaseCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        TableManager.onDataBaseUpgrade(db, oldVersion, newVersion);
    }
}
