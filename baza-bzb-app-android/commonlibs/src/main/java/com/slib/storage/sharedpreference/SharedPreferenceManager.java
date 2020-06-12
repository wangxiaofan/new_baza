package com.slib.storage.sharedpreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by Vincent.Lei on 2016/11/7.
 * Title : SharedPreference
 * Note :
 */

public class SharedPreferenceManager {

    private static final String DEFAULT_FILE_NAME = "default_data_sp";
    private static SharedPreferences sp;

    public static void init(Context context, String fileName) {
        if (null == sp) {
            synchronized (SharedPreferenceManager.class) {
                if (null == sp) {
                    sp = context.getSharedPreferences(TextUtils.isEmpty(fileName) ? DEFAULT_FILE_NAME : fileName, Context.MODE_PRIVATE);
                }
            }
        }
    }

    public static void saveString(String key, String value) {
        if (sp != null) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, value).apply();
        }
    }

    public static String getString(String key) {
        if (sp != null) {
            return sp.getString(key, null);
        }
        return null;
    }

    public static void saveBoolean(String key, boolean value) {
        if (sp != null) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(key, value).apply();
        }
    }

    public static boolean getBoolean(String key) {
        if (sp != null) {
            return sp.getBoolean(key, false);
        }
        return false;
    }

    public static void saveLong(String key, long value) {
        if (sp != null) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putLong(key, value).apply();
        }
    }

    public static long getLong(String key) {
        if (sp != null) {
            return sp.getLong(key, 0);
        }
        return 0;
    }

    public static void saveInt(String key, int value) {
        if (sp != null) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(key, value).apply();
        }
    }

    public static int getInt(String key) {
        if (sp != null) {
            return sp.getInt(key, 0);
        }
        return 0;
    }
}
