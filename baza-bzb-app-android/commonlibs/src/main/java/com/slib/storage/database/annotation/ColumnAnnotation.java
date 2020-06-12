package com.slib.storage.database.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Vincent.Lei on 2016/11/8.
 * Title :
 * Note : 表结构
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnAnnotation {
    String columnName();

    String columnType() default "TEXT";

    int primaryKey() default 0;
    int autoIncrement() default 0;
}
