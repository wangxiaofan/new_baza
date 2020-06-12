package com.baza.android.bzw.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.baza.android.bzw.bean.label.Label;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.resume.TrackingListBean;
import com.baza.android.bzw.bean.resume.TrackingSearchResultBean;
import com.baza.android.bzw.bean.searchfilterbean.TrackingSearchFilterInfoBean;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.slib.http.HttpRequestUtil;
import com.slib.http.INetworkCallBack;
import com.slib.storage.database.DBWorker;
import com.slib.storage.database.core.DataBaseManager;
import com.slib.storage.database.core.DbClassUtil;
import com.slib.storage.database.handler.IDBControllerHandler;
import com.slib.storage.database.listener.IDBReplyListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/6/8.
 * Title：
 * Note：
 */

public class TrackingResumeDao {

    private TrackingResumeDao() {
    }

    public static class SearchParam {
        private int offset;
        private int pageSize;
        private TrackingSearchFilterInfoBean searchFilter;
        private String searchId;

        private HashMap<String, String> mKeyMap = new HashMap<>();

        public SearchParam searchId(String searchId) {
            this.searchId = searchId;
            return this;
        }

        public SearchParam offset(int offset) {
            this.offset = offset;
            return this;
        }

        public SearchParam pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public SearchParam searchFilter(TrackingSearchFilterInfoBean searchFilter) {
            this.searchFilter = searchFilter;
            return this;
        }

        public HashMap<String, String> getKeyMap() {
            return mKeyMap;
        }

        public SearchParam build() {
            mKeyMap.clear();
            if (searchFilter != null) {
                if (!TextUtils.isEmpty(searchFilter.sort)) {
                    mKeyMap.put("sort", searchFilter.sort);
                }
                if (!TextUtils.isEmpty(searchFilter.keyWord)) {
                    mKeyMap.put("keywords", searchFilter.keyWord);
                }
                if (searchFilter.yearOfExperienceRegion > 0) {
                    mKeyMap.put("yearOfExperienceRegions", String.valueOf(searchFilter.yearOfExperienceRegion));
                }
                if (searchFilter.cityCode > 0) {
                    mKeyMap.put("locations", String.valueOf(searchFilter.cityCode));
                }
                if (searchFilter.degreeParameter > 0) {
                    mKeyMap.put("degrees", String.valueOf(searchFilter.degreeParameter));
                }
                if (!TextUtils.isEmpty(searchFilter.company)) {
                    mKeyMap.put("companies", searchFilter.company);
                }
                if (!TextUtils.isEmpty(searchFilter.title)) {
                    mKeyMap.put("titles", searchFilter.title);
                }
                if (searchFilter.labelList != null && !searchFilter.labelList.isEmpty()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0, size = searchFilter.labelList.size(); i < size; i++) {
                        stringBuilder.append(searchFilter.labelList.get(i).tag).append(",");
                    }
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                    String tags = stringBuilder.toString();
                    mKeyMap.put("tagNames", tags);
                }
                if (!TextUtils.isEmpty(searchFilter.filterType)) {
                    mKeyMap.put("filterType", searchFilter.filterType);
                }
            }
            if (!TextUtils.isEmpty(searchId))
                mKeyMap.put("searchId", searchId);
            mKeyMap.put("offset", String.valueOf(offset));
            mKeyMap.put("pageSize", String.valueOf(pageSize > 0 ? pageSize : CommonConst.DEFAULT_PAGE_SIZE));
            return this;
        }
    }

    //tracking人才搜索
    public static void doTrackingSearch(TrackingResumeDao.SearchParam searchParam, final IDefaultRequestReplyListener<TrackingSearchResultBean> listener) {
        HttpRequestUtil.doHttpPost(URLConst.URL_TRACKINGLIST_SEARCH, searchParam.getKeyMap(), TrackingSearchResultBean.class, new INetworkCallBack<TrackingSearchResultBean>() {
            @Override
            public void onSuccess(TrackingSearchResultBean candidateSearchResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, candidateSearchResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    /**
     * 保存企业搜索条件
     *
     * @param searchFilterInfoBean
     * @param unionId
     */
    public static void saveCompanySearchFilterToLocalDb(TrackingSearchFilterInfoBean searchFilterInfoBean, final String unionId) {
        searchFilterInfoBean.shouldProcessChanged();
        DBWorker.customerDBTask(searchFilterInfoBean, new IDBControllerHandler<TrackingSearchFilterInfoBean, Void>() {
            @Override
            public Void operateDataBaseAsync(DataBaseManager mDBManager, TrackingSearchFilterInfoBean input) {
                if (input == null || input.getId() == null)
                    return null;
                input.updateTime = System.currentTimeMillis();
                Cursor cursor = null;
                try {
                    boolean isInsert = true;
                    String table = DbClassUtil.getTableNameByAnnotationClass(TrackingSearchFilterInfoBean.class);
                    cursor = mDBManager.query(table, null, "unionId = '" + unionId + "' and id = '" + input.getId() + "'", null, null, null, null);
                    if (cursor != null) {
                        isInsert = cursor.getCount() <= 0;
                        cursor.close();
                    }
                    ContentValues contentValues = DbClassUtil.buildContentValues(input, null);
                    contentValues.put("unionId", unionId);
                    if (isInsert)
                        mDBManager.insert(table, contentValues);
                    else
                        mDBManager.update(table, contentValues, "unionId = '" + unionId + "' and id = '" + input.getId() + "'", null);
                    table = DbClassUtil.getTableNameByAnnotationClass(Label.class);
                    mDBManager.delete(table, "unionId = '" + unionId + "' and filterId = '" + input.getId() + "'", null);
                    if (input.labelList != null && !input.labelList.isEmpty()) {
                        ContentValues labelValues;
                        for (int i = 0; i < input.labelList.size(); i++) {
                            labelValues = DbClassUtil.buildContentValues(input.labelList.get(i), null);
                            labelValues.put("filterId", input.getId());
                            labelValues.put("unionId", unionId);
                            mDBManager.insert(table, labelValues);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null && !cursor.isClosed())
                        cursor.close();
                }
                return null;
            }

            @Override
            public Class<?>[] getDependModeClass() {
                return new Class[]{TrackingSearchFilterInfoBean.class, Label.class};
            }
        }, null, false);
    }

    /**
     * 查询企业搜索记录
     *
     * @param unionId
     * @param listener
     */
    public static void readCompanySearchFilterFromLocalDb(final String unionId, final IDBReplyListener<List<TrackingSearchFilterInfoBean>> listener) {

        DBWorker.customerDBTask(unionId, new IDBControllerHandler<String, List<TrackingSearchFilterInfoBean>>() {
            @Override
            public List<TrackingSearchFilterInfoBean> operateDataBaseAsync(DataBaseManager mDBManager, String unionId) {
                Cursor cursor = null;
                Cursor cursor1Labels = null;
                List<TrackingSearchFilterInfoBean> list = null;
                try {
                    cursor = mDBManager.query(DbClassUtil.getTableNameByAnnotationClass(TrackingSearchFilterInfoBean.class), null, "unionId = '" + unionId + "'", null, null, null, " updateTime desc limit 20");
                    if (cursor != null && cursor.getCount() > 0) {
                        list = new ArrayList<>(cursor.getCount());
                        TrackingSearchFilterInfoBean searchFilterInfoBean;
                        while (cursor.moveToNext()) {
                            searchFilterInfoBean = DbClassUtil.buildObject(TrackingSearchFilterInfoBean.class, cursor, null);
                            list.add(searchFilterInfoBean);
                            cursor1Labels = mDBManager.query(DbClassUtil.getTableNameByAnnotationClass(Label.class), null, "unionId = '" + unionId + "' and filterId = '" + searchFilterInfoBean.getId() + "'", null, null, null, null);
                            if (cursor1Labels != null && cursor1Labels.getCount() > 0) {
                                searchFilterInfoBean.labelList = new ArrayList<>(cursor1Labels.getCount());
                                while (cursor1Labels.moveToNext()) {
                                    searchFilterInfoBean.labelList.add(DbClassUtil.buildObject(Label.class, cursor1Labels, null));
                                }
                                cursor1Labels.close();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null && !cursor.isClosed())
                        cursor.close();
                    if (cursor1Labels != null && !cursor1Labels.isClosed())
                        cursor1Labels.close();
                }
                return list;
            }

            @Override
            public Class<?>[] getDependModeClass() {
                return new Class[]{TrackingSearchFilterInfoBean.class, Label.class};
            }
        }, listener, true);
    }

    /**
     * 清除企业搜索历史
     *
     * @param unionId
     * @param searchFilterInfoBean
     */
    public static void deleteCompanySearchFilterFromLocalDb(final String unionId, TrackingSearchFilterInfoBean searchFilterInfoBean) {

        DBWorker.customerDBTask(searchFilterInfoBean, new IDBControllerHandler<TrackingSearchFilterInfoBean, Void>() {
            @Override
            public Void operateDataBaseAsync(DataBaseManager mDBManager, TrackingSearchFilterInfoBean input) {
                if (input == null) {
                    // 删除全部
                    mDBManager.delete(DbClassUtil.getTableNameByAnnotationClass(TrackingSearchFilterInfoBean.class), "unionId = '" + unionId + "'", null);
                    mDBManager.delete(DbClassUtil.getTableNameByAnnotationClass(Label.class), "unionId = '" + unionId + "'", null);
                    return null;
                }
                mDBManager.delete(DbClassUtil.getTableNameByAnnotationClass(TrackingSearchFilterInfoBean.class), "unionId = '" + unionId + "'" + " and id = '" + input.getId() + "'", null);
                mDBManager.delete(DbClassUtil.getTableNameByAnnotationClass(Label.class), "unionId = '" + unionId + "'" + " and filterId = '" + input.getId() + "'", null);
                return null;
            }

            @Override
            public Class<?>[] getDependModeClass() {
                return new Class[]{TrackingSearchFilterInfoBean.class, Label.class};
            }
        }, null, false);
    }

    public static int findTargetResumePosition(ResumeBean resumeBean, List<TrackingListBean> list) {
        if (resumeBean == null || list == null || resumeBean.candidateId == null)
            return CommonConst.LIST_POSITION_NONE;
        for (int i = 0, size = list.size(); i < size; i++) {
            if (list.get(i) == null)
                continue;
            if (resumeBean.candidateId.equals(list.get(i).getResumeId()))
                return i;
        }
        return CommonConst.LIST_POSITION_NONE;
    }
}
