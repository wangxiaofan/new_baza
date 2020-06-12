package com.baza.android.bzw.bean.label;

import com.slib.storage.database.annotation.ColumnAnnotation;
import com.slib.storage.database.annotation.TableAnnotation;

import java.io.Serializable;


/**
 * Created by Vincent.Lei on 2017/3/20.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */
@TableAnnotation(tableName = "labelTableV4")
public class Label implements Serializable {
    @ColumnAnnotation(columnName = "tagName")
    public String tag;
//    @ColumnAnnotation(columnName = "tagId")
//    public String id;
    @ColumnAnnotation(columnName = "count", columnType = "INTEGER")
    public int count;
    @ColumnAnnotation(columnName = "filterId")
    private String filterId;
    @ColumnAnnotation(columnName = "unionId")
    public String unionId;
    public long createTime;


}
