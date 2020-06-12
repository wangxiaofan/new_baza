package com.baza.android.bzw.bean.resumeelement;

import com.slib.storage.database.annotation.ColumnAnnotation;
import com.slib.storage.database.annotation.TableAnnotation;

import java.io.Serializable;


/**
 * Created by Vincent.Lei on 2017/1/4.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */
@TableAnnotation(tableName = "remark_attachment2")
public class LocalRemarkAttachment implements Serializable{
    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_AUDIO = 2;
    @ColumnAnnotation(columnName = "attachmentId", columnType = "INTEGER", primaryKey = 1, autoIncrement = 1)
    public int attachmentId;
    @ColumnAnnotation(columnName = "filePath")
    public String filePath;
    @ColumnAnnotation(columnName = "remarkId")
    public String remarkId;
    @ColumnAnnotation(columnName = "type", columnType = "INTEGER")
    public int type;
    @ColumnAnnotation(columnName = "sourceUpdateTime", columnType = "INTEGER")
    public long updateTime;
    @ColumnAnnotation(columnName = "timeLength", columnType = "INTEGER")
    public long timeLength;

    public LocalRemarkAttachment() {
    }

    public LocalRemarkAttachment(String filePath, String remarkId, int type) {
        this.filePath = filePath;
        this.remarkId = remarkId;
        this.type = type;
    }

    public LocalRemarkAttachment(String filePath, String remarkId, long updateTime, long timeLength, int type) {
        this.filePath = filePath;
        this.remarkId = remarkId;
        this.updateTime = updateTime;
        this.timeLength = timeLength;
        this.type = type;
    }
}
