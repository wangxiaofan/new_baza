package com.baza.android.bzw.bean.common;

import com.slib.storage.database.annotation.ColumnAnnotation;
import com.slib.storage.database.annotation.TableAnnotation;

/**
 * Created by Vincent.Lei on 2018/10/9.
 * Title：
 * Note：
 */
@TableAnnotation(tableName = "CountCalculateTable")
public class CountCalculateBean {
    public static final int TYPE_IM_STRANGER_MSG = 1;
    public static final int TYPE_ACTIVITY_NEW_VERSION = 6;

    @ColumnAnnotation(columnName = "count", columnType = "INTEGER")
    public int count;
    @ColumnAnnotation(columnName = "type", columnType = "INTEGER")
    public int type;
    @ColumnAnnotation(columnName = "unionId")
    public String unionId;
    @ColumnAnnotation(columnName = "timeYMD")
    public String timeYMD;

}
