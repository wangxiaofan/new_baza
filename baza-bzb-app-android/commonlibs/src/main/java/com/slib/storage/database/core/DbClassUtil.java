package com.slib.storage.database.core;

import android.content.ContentValues;
import android.database.Cursor;

import com.slib.storage.database.annotation.ColumnAnnotation;
import com.slib.storage.database.annotation.TableAnnotation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Vincent.Lei on 2019/1/31.
 * Title：
 * Note：
 */
public class DbClassUtil {
    public static List<Field> getFieldList(Class<?> classZZ) {
        List<Field> fieldList = new ArrayList<>();
        while (classZZ != null && !classZZ.getName().toLowerCase().equals("java.lang.object")) {
            //当父类为null的时候说明到达了最上层的父类(Object类).
            fieldList.addAll(Arrays.asList(classZZ.getDeclaredFields()));
            classZZ = classZZ.getSuperclass();
        }
        return fieldList;
    }

    public static <T> ContentValues buildContentValues(T t, String[] columns) {
        ContentValues contentValues = new ContentValues();
        Class<?> classZZ = t.getClass();
        List<Field> fieldList = getFieldList(classZZ);
        ColumnAnnotation columnAnnotation;
        for (int i = 0, size = fieldList.size(); i < size; i++) {
            Field f = fieldList.get(i);
            columnAnnotation = getColumnAnnotationByField(f, columns);
            if (columnAnnotation != null) {
                f.setAccessible(true);
                if (columnAnnotation.autoIncrement() > 0)
                    continue;
                try {
                    Object o = f.get(t);
                    contentValues.put(columnAnnotation.columnName(), o != null ? o.toString() : "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return contentValues;
    }

    private static ColumnAnnotation getColumnAnnotationByField(Field f, String[] columnsLimit) {
        if (!f.isAnnotationPresent(ColumnAnnotation.class))
            return null;
        ColumnAnnotation columnAnnotation = f.getAnnotation(ColumnAnnotation.class);
        if (columnsLimit == null || columnsLimit.length == 0)
            return columnAnnotation;
        for (int i = 0; i < columnsLimit.length; i++) {
            if (columnsLimit[i].equals(columnAnnotation.columnName()))
                return columnAnnotation;
        }
        return null;
    }

    public static <T> String getTableNameByAnnotationClass(Class<T> classZZ) {
        if (!classZZ.isAnnotationPresent(TableAnnotation.class))
            return null;
        TableAnnotation tableAnnotation = classZZ.getAnnotation(TableAnnotation.class);
        return tableAnnotation.tableName();
    }


    public static <T> T buildObject(Class<T> classZZ, Cursor cursor, String[] columns) {
        try {
            T t = classZZ.newInstance();
            List<Field> fieldList = getFieldList(classZZ);
            ColumnAnnotation columnAnnotation;
            for (int i = 0, size = fieldList.size(); i < size; i++) {
                Field f = fieldList.get(i);
                columnAnnotation = getColumnAnnotationByField(f, columns);
                if (columnAnnotation == null)
                    continue;
                f.setAccessible(true);
                Class<?> c = f.getType();
                if (c == String.class)
                    f.set(t, cursor.getString(cursor.getColumnIndex(columnAnnotation.columnName())));
                else if (c == int.class)
                    f.set(t, cursor.getInt(cursor.getColumnIndex(columnAnnotation.columnName())));
                else if (c == float.class)
                    f.set(t, cursor.getFloat(cursor.getColumnIndex(columnAnnotation.columnName())));
                else if (c == double.class)
                    f.set(t, cursor.getDouble(cursor.getColumnIndex(columnAnnotation.columnName())));
                else if (c == long.class)
                    f.set(t, cursor.getLong(cursor.getColumnIndex(columnAnnotation.columnName())));
                else if (c == short.class)
                    f.set(t, cursor.getShort(cursor.getColumnIndex(columnAnnotation.columnName())));
            }
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
