package com.baza.android.bzw.bean.email;


import com.slib.storage.database.annotation.ColumnAnnotation;
import com.slib.storage.database.annotation.TableAnnotation;

/**
 * Created by Vincent.Lei on 2017/4/14.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */
@TableAnnotation(tableName = "addressee")
public class AddresseeBean {
    public static final int VALID_OK = 1;
    @ColumnAnnotation(columnName = "email")
    public String email;
    @ColumnAnnotation(columnName = "createTime", columnType = "INTEGER")
    public long createTime;
    @ColumnAnnotation(columnName = "uid", columnType = "INTEGER")
    public long uid;
    public int validStatus;//1-已验证 其他未验证
    public boolean isSyncing;
}
