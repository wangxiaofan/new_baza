package com.baza.android.bzw.bean.resume;

import com.slib.storage.database.annotation.ColumnAnnotation;
import com.slib.storage.database.annotation.TableAnnotation;

/**
 * Created by Vincent.Lei on 2019/8/6.
 * Title：
 * Note：
 */
@TableAnnotation(tableName = "remarkAudioAttachmentMapTable")
public class RemarkAudioAttachmentMapBean {
    @ColumnAnnotation(columnName = "indexId", columnType = "INTEGER", primaryKey = 1, autoIncrement = 1)
    public int indexId;
    @ColumnAnnotation(columnName = "inquiryId")
    public String inquiryId;
    @ColumnAnnotation(columnName = "unionId")
    public String unionId;
    @ColumnAnnotation(columnName = "filePath")
    public String filePath;

    @Override
    public String toString() {
        return "inquiryId  = " + inquiryId + "   filePath = " + filePath;
    }
}
