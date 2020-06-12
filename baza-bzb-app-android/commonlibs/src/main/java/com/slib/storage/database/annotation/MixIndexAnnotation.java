package com.slib.storage.database.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Vincent.Lei on 2016/11/8.
 * Title : 表注解
 * Note :
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MixIndexAnnotation {
    String[] indexNames();

    String[] columnMixNames();

    boolean[] isUnique();
}
