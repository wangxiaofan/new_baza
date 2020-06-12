package com.baza.android.bzw.dao;

import android.text.TextUtils;

import com.baza.android.bzw.bean.resume.ResumeSearchBean;
import com.baza.android.bzw.bean.resume.ResumeSearchResultBean;
import com.baza.android.bzw.bean.searchfilterbean.SearchFilterInfoBean;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.slib.http.HttpRequestUtil;
import com.slib.http.INetworkCallBack;

import java.util.HashMap;

public class TrackingDao {

    public TrackingDao() {
    }

    public static class SearchParam {
        private int offset;
        private int pageSize;
        private String sort;
        private int sortOrder;//排序方式，0-升序，1-降序
        private String operationTimeStart;
        private String operationTimeEnd;
        private boolean isJobHunting;
        private String filterType;
        private SearchFilterInfoBean searchFilter;

        private HashMap<String, String> mKeyMap = new HashMap<>();

        public TrackingDao.SearchParam offset(int offset) {
            this.offset = offset;
            return this;
        }

        public TrackingDao.SearchParam pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public TrackingDao.SearchParam sort(String sort){
            this.sort=sort;
            return this;
        }

        public TrackingDao.SearchParam sortOrder(int sortOrder){
            this.sortOrder=sortOrder;
            return this;
        }

        public TrackingDao.SearchParam operationTimeStart(String operationTimeStart){
            this.operationTimeStart=operationTimeStart;
            return this;
        }

        public TrackingDao.SearchParam operationTimeEnd(String operationTimeEnd){
            this.operationTimeEnd=operationTimeEnd;
            return this;
        }

        public TrackingDao.SearchParam isJobHunting(boolean isJobHunting) {
            this.isJobHunting = isJobHunting;
            return this;
        }

        public TrackingDao.SearchParam filterType(String filterType){
            this.filterType=filterType;
            return this;
        }

        public TrackingDao.SearchParam searchFilter(SearchFilterInfoBean searchFilter) {
            this.searchFilter = searchFilter;
            return this;
        }

        public HashMap<String, String> getKeyMap() {
            return mKeyMap;
        }

        public TrackingDao.SearchParam build() {
            mKeyMap.clear();
            if (searchFilter != null) {
                if (!TextUtils.isEmpty(searchFilter.keyWord)) {
                    mKeyMap.put("keywords", searchFilter.keyWord);
                }
                if (searchFilter.cityCode > 0) {
                    mKeyMap.put("location", String.valueOf(searchFilter.cityCode));
                }
                if (!TextUtils.isEmpty(searchFilter.workYearName)) {
                    mKeyMap.put("minExpr", String.valueOf(searchFilter.startYearParameter));
                    mKeyMap.put("maxExpr", String.valueOf(searchFilter.endYearParameter));
                }
                if (!TextUtils.isEmpty(searchFilter.degreeName)) {
                    mKeyMap.put("degree", String.valueOf(searchFilter.degreeParameter));
                }
                if (!TextUtils.isEmpty(searchFilter.schoolName)) {
                    mKeyMap.put("schoolType", String.valueOf(searchFilter.schoolParameter));
                }
                if (!TextUtils.isEmpty(searchFilter.sexName)) {
                    mKeyMap.put("gender", String.valueOf(searchFilter.sexParameter));
                }
                if (!TextUtils.isEmpty(searchFilter.sourceFromName)) {
                    mKeyMap.put("source", searchFilter.sourceParameter);
                }
//                if (isSearchMineResume && searchFilter.labelList != null && !searchFilter.labelList.isEmpty()) {
//                    StringBuilder stringBuilder = new StringBuilder("[");
//                    for (int i = 0, size = searchFilter.labelList.size(); i < size; i++) {
//                        stringBuilder.append("\"").append(searchFilter.labelList.get(i).tag).append("\"").append(",");
//                    }
//                    stringBuilder.deleteCharAt(stringBuilder.length() - 1).append("]");
//                    String tags = stringBuilder.toString();
//                    mKeyMap.put("tag", tags);
//                }
                if (searchFilter.mandatorySort != CommonConst.RESUME_SEARCH_SORT_ORDER_NONE)
                    mKeyMap.put("mandatorySort", String.valueOf(searchFilter.mandatorySort));
            }
//            if (!TextUtils.isEmpty(searchId))
//                mKeyMap.put("searchId", searchId);
//            mKeyMap.put("offset", String.valueOf(offset));
//            mKeyMap.put("pageSize", String.valueOf(pageSize > 0 ? pageSize : CommonConst.DEFAULT_PAGE_SIZE));
//            if (isCollected)
//                mKeyMap.put("collected", "true");
//            if (isJobHunting)
//                mKeyMap.put("isJobHunting", "true");
//            if (isReturnJobHuntingCount)
//                mKeyMap.put("returnJobHuntingCount", "true");
//            if (isRecentContact)
//                mKeyMap.put("recentContact", "1");
//            if (isRecentCreate)
//                mKeyMap.put("recentCreate", "1");
//            if (isSetExcludeMine)
//                mKeyMap.put("excludeMine", isExcludeMine ? "true" : "false");
//            if (jobHuntingTimeType == CommonConst.JobHunterPredictionType.NOW || jobHuntingTimeType == CommonConst.JobHunterPredictionType.PREVIOUS)
//                mKeyMap.put("jobHuntingTimeType", String.valueOf(jobHuntingTimeType));
            return this;
        }
    }


    public static void doSearch(TrackingDao.SearchParam searchParam, final IDefaultRequestReplyListener<ResumeSearchBean> listener) {
        HttpRequestUtil.doHttpPost(URLConst.URL_TRACKING_LIST, searchParam.getKeyMap(), ResumeSearchResultBean.class, new INetworkCallBack<ResumeSearchResultBean>() {
            @Override
            public void onSuccess(ResumeSearchResultBean candidateSearchResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, candidateSearchResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }
}
