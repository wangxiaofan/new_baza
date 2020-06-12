package com.baza.android.bzw.dao;

import android.database.Cursor;
import android.text.TextUtils;

import com.baza.android.bzw.bean.message.MessageProcessLocalTagBean;
import com.slib.storage.database.DBWorker;
import com.slib.storage.database.core.DataBaseManager;
import com.slib.storage.database.core.DbClassUtil;
import com.slib.storage.database.handler.IDBControllerHandler;
import com.slib.storage.database.listener.IDBReplyListener;

import java.util.HashSet;

/**
 * Created by Vincent.Lei on 2018/4/27.
 * Title：
 * Note：
 */
public class MessageDao {
    public static void markPassiveShareHasProcessed(MessageProcessLocalTagBean tagBean) {
        if (tagBean == null)
            return;
        DBWorker.saveSingle(tagBean, null);
    }

    public static void readPassiveShareHasProcessedTags(final String unionId, final String neteasyId, IDBReplyListener<HashSet<String>> replyListener) {
        if (TextUtils.isEmpty(unionId) || TextUtils.isEmpty(neteasyId))
            return;
        DBWorker.customerDBTask(null, new IDBControllerHandler<Void, HashSet<String>>() {
            @Override
            public HashSet<String> operateDataBaseAsync(DataBaseManager mDBManager, Void input) {
                String tableName = DbClassUtil.getTableNameByAnnotationClass(MessageProcessLocalTagBean.class);
                Cursor cursor = null;
                HashSet<String> tags = null;
                try {
                    cursor = mDBManager.query(tableName, new String[]{"messageId"}, "unionId = ? and neteasyId  =?", new String[]{unionId, neteasyId}, null, null, null);
                    if (cursor != null && cursor.getCount() > 0) {
                        tags = new HashSet<>();
                        while (cursor.moveToNext())
                            tags.add(cursor.getString(cursor.getColumnIndex("messageId")));
                    }
                } catch (Exception e) {
                    //ignore
                } finally {
                    if (cursor != null) cursor.close();
                }
                return tags;
            }

            @Override
            public Class<?>[] getDependModeClass() {
                return new Class[]{MessageProcessLocalTagBean.class};
            }
        }, replyListener, true);
    }
}
