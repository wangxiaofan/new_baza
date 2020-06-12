package com.baza.android.bzw.bean.message;

import com.slib.storage.database.annotation.ColumnAnnotation;
import com.slib.storage.database.annotation.TableAnnotation;

/**
 * Created by Vincent.Lei on 2018/4/27.
 * Title：
 * Note：
 */
@TableAnnotation(tableName = "MessageProcessLocalTagTable")
public class MessageProcessLocalTagBean {
    @ColumnAnnotation(columnName = "id", columnType = "INTEGER", primaryKey = 1, autoIncrement = 1)
    public int id;
    @ColumnAnnotation(columnName = "unionId")
    public String unionId;
    @ColumnAnnotation(columnName = "neteasyId")
    public String neteasyId;
    @ColumnAnnotation(columnName = "messageId")
    public String messageId;

    public MessageProcessLocalTagBean() {
    }

    public MessageProcessLocalTagBean(String unionId, String neteasyId, String messageId) {
        this.unionId = unionId;
        this.neteasyId = neteasyId;
        this.messageId = messageId;
    }
}
