package com.baza.android.bzw.bean.friend;

import com.slib.storage.database.annotation.ColumnAnnotation;
import com.slib.storage.database.annotation.TableAnnotation;

/**
 * Created by Vincent.Lei on 2018/1/11.
 * Title：
 * Note：
 */
@TableAnnotation(tableName = "friendContactRecordTable")
public class FriendContactRecordBean {
    @ColumnAnnotation(columnName = "unionId")
    public String unionId;
    @ColumnAnnotation(columnName = "contactNeteaseId")
    public String contactNeteaseId;
    @ColumnAnnotation(columnName = "contactTime", columnType = "INTEGER")
    public long contactTime;

    public FriendContactRecordBean(String unionId, String contactNeteaseId, long contactTime) {
        this.unionId = unionId;
        this.contactNeteaseId = contactNeteaseId;
        this.contactTime = contactTime;
    }

    public FriendContactRecordBean() {
    }
}
