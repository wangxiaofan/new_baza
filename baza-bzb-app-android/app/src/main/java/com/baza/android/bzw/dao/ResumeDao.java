package com.baza.android.bzw.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.CustomerHttpResultBean;
import com.baza.android.bzw.bean.label.Label;
import com.baza.android.bzw.bean.resume.AddOrRemoveTrackingListResultBean;
import com.baza.android.bzw.bean.resume.CompanyLibInfoOfResumeBean;
import com.baza.android.bzw.bean.resume.ContactResultBean;
import com.baza.android.bzw.bean.resume.CreateResumeResultBean;
import com.baza.android.bzw.bean.resume.FloatingListBean;
import com.baza.android.bzw.bean.resume.FloatingListDetailBean;
import com.baza.android.bzw.bean.resume.FloatingListDetailResultBean;
import com.baza.android.bzw.bean.resume.FloatingListResultBen;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.resume.ResumeClassifyResultBean;
import com.baza.android.bzw.bean.resume.ResumeDetailResultBean;
import com.baza.android.bzw.bean.resume.ResumeOnLineResultBean;
import com.baza.android.bzw.bean.resume.ResumeSearchBean;
import com.baza.android.bzw.bean.resume.ResumeSearchResultBean;
import com.baza.android.bzw.bean.resume.TrackingSearchResultBean;
import com.baza.android.bzw.bean.resumeelement.EditRemarkResultBean;
import com.baza.android.bzw.bean.resumeelement.RemarkBean;
import com.baza.android.bzw.bean.resumeelement.ResumeImportResultBean;
import com.baza.android.bzw.bean.searchfilterbean.CompanySearchFilterInfoBean;
import com.baza.android.bzw.bean.searchfilterbean.SearchFilterInfoBean;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.extra.CustomerHttpRequestUtil;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.slib.http.HttpRequestUtil;
import com.slib.http.INetworkCallBack;
import com.slib.storage.database.DBWorker;
import com.slib.storage.database.core.DataBaseManager;
import com.slib.storage.database.core.DbClassUtil;
import com.slib.storage.database.handler.IDBControllerHandler;
import com.slib.storage.database.listener.IDBReplyListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Vincent.Lei on 2017/6/8.
 * Title：
 * Note：
 */

public class ResumeDao {
    private ResumeDao() {
    }

    public static class SearchParam {
        private int offset;
        private int pageSize;
        private int jobHuntingTimeType;
        private SearchFilterInfoBean searchFilter;
        private boolean isJobHunting;
        private boolean isReturnJobHuntingCount;
        private boolean isRecentContact;
        private boolean isRecentCreate;
        private boolean isSearchMineResume = true;
        private boolean isSetExcludeMine;
        private boolean isExcludeMine;
        private boolean isCollected;
        private String searchId;

        private HashMap<String, String> mKeyMap = new HashMap<>();

        public SearchParam isSearchMineResume(boolean isSearchMineResume) {
            this.isSearchMineResume = isSearchMineResume;
            return this;
        }

        public SearchParam searchId(String searchId) {
            this.searchId = searchId;
            return this;
        }

        public SearchParam jobHuntingTimeType(int jobHuntingTimeType) {
            this.jobHuntingTimeType = jobHuntingTimeType;
            return this;
        }

        public SearchParam isRecentContact(boolean isRecentContact) {
            this.isRecentContact = isRecentContact;
            return this;
        }

//        public SearchParam isSetExcludeMine(boolean isSetExcludeMine) {
//            this.isSetExcludeMine = isSetExcludeMine;
//            return this;
//        }

        public SearchParam isExcludeMine(boolean isExcludeMine) {
            this.isSetExcludeMine = true;
            this.isExcludeMine = isExcludeMine;
            return this;
        }

        public SearchParam isRecentCreate(boolean isRecentCreate) {
            this.isRecentCreate = isRecentCreate;
            return this;
        }

        public SearchParam isCollected(boolean isCollected) {
            this.isCollected = isCollected;
            return this;
        }

        public SearchParam isReturnJobHuntingCount(boolean isReturnJobHuntingCount) {
            this.isReturnJobHuntingCount = isReturnJobHuntingCount;
            return this;
        }

        public SearchParam isJobHunting(boolean isJobHunting) {
            this.isJobHunting = isJobHunting;
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

        public SearchParam searchFilter(SearchFilterInfoBean searchFilter) {
            this.searchFilter = searchFilter;
            return this;
        }

        public HashMap<String, String> getKeyMap() {
            return mKeyMap;
        }

        public SearchParam build() {
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
                if (isSearchMineResume && searchFilter.labelList != null && !searchFilter.labelList.isEmpty()) {
                    StringBuilder stringBuilder = new StringBuilder("[");
                    for (int i = 0, size = searchFilter.labelList.size(); i < size; i++) {
                        stringBuilder.append("\"").append(searchFilter.labelList.get(i).tag).append("\"").append(",");
                    }
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1).append("]");
                    String tags = stringBuilder.toString();
                    mKeyMap.put("tag", tags);
                }
                if (searchFilter.mandatorySort != CommonConst.RESUME_SEARCH_SORT_ORDER_NONE)
                    mKeyMap.put("mandatorySort", String.valueOf(searchFilter.mandatorySort));
            }
            if (!TextUtils.isEmpty(searchId))
                mKeyMap.put("searchId", searchId);
            mKeyMap.put("offset", String.valueOf(offset));
            mKeyMap.put("pageSize", String.valueOf(pageSize > 0 ? pageSize : CommonConst.DEFAULT_PAGE_SIZE));
            if (isCollected)
                mKeyMap.put("collected", "true");
            if (isJobHunting)
                mKeyMap.put("isJobHunting", "true");
            if (isReturnJobHuntingCount)
                mKeyMap.put("returnJobHuntingCount", "true");
            if (isRecentContact)
                mKeyMap.put("recentContact", "1");
            if (isRecentCreate)
                mKeyMap.put("recentCreate", "1");
            if (isSetExcludeMine)
                mKeyMap.put("excludeMine", isExcludeMine ? "true" : "false");
            if (jobHuntingTimeType == CommonConst.JobHunterPredictionType.NOW || jobHuntingTimeType == CommonConst.JobHunterPredictionType.PREVIOUS)
                mKeyMap.put("jobHuntingTimeType", String.valueOf(jobHuntingTimeType));
            return this;
        }
    }

    public static class CompanySearchParam {
        private int offset;
        private int pageSize;
        private int jobHuntingTimeType;
        private CompanySearchFilterInfoBean searchFilter;
        private boolean isJobHunting;
        private boolean isReturnJobHuntingCount;
        private boolean isRecentContact;
        private boolean isRecentCreate;
        private boolean isSearchMineResume = true;
        private boolean isSetExcludeMine;
        private boolean isExcludeMine;
        private boolean isCollected;
        private String searchId;
        public boolean isExcludeEmptyMobile;
        public boolean notScan;
        public int searchType;//0-企业 1-我的收藏 2-tracking

        private HashMap<String, String> mKeyMap = new HashMap<>();

        public CompanySearchParam isSearchMineResume(boolean isSearchMineResume) {
            this.isSearchMineResume = isSearchMineResume;
            return this;
        }

        public CompanySearchParam searchId(String searchId) {
            this.searchId = searchId;
            return this;
        }

        public CompanySearchParam searchType(int searchType) {
            this.searchType = searchType;
            return this;
        }

        public CompanySearchParam jobHuntingTimeType(int jobHuntingTimeType) {
            this.jobHuntingTimeType = jobHuntingTimeType;
            return this;
        }

        public CompanySearchParam isRecentContact(boolean isRecentContact) {
            this.isRecentContact = isRecentContact;
            return this;
        }

//        public SearchParam isSetExcludeMine(boolean isSetExcludeMine) {
//            this.isSetExcludeMine = isSetExcludeMine;
//            return this;
//        }

        public CompanySearchParam isExcludeMine(boolean isExcludeMine) {
            this.isSetExcludeMine = true;
            this.isExcludeMine = isExcludeMine;
            return this;
        }

        public CompanySearchParam isRecentCreate(boolean isRecentCreate) {
            this.isRecentCreate = isRecentCreate;
            return this;
        }

        public CompanySearchParam isCollected(boolean isCollected) {
            this.isCollected = isCollected;
            return this;
        }

        public CompanySearchParam isReturnJobHuntingCount(boolean isReturnJobHuntingCount) {
            this.isReturnJobHuntingCount = isReturnJobHuntingCount;
            return this;
        }

        public CompanySearchParam isJobHunting(boolean isJobHunting) {
            this.isJobHunting = isJobHunting;
            return this;
        }

        public CompanySearchParam isExcludeEmptyMobile(boolean isExcludeEmptyMobile) {
            this.isExcludeEmptyMobile = isExcludeEmptyMobile;
            return this;
        }

        public CompanySearchParam notScan(boolean notScan) {
            this.notScan = notScan;
            return this;
        }

        public CompanySearchParam offset(int offset) {
            this.offset = offset;
            return this;
        }

        public CompanySearchParam pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public CompanySearchParam searchFilter(CompanySearchFilterInfoBean searchFilter) {
            this.searchFilter = searchFilter;
            return this;
        }

        public HashMap<String, String> getKeyMap() {
            return mKeyMap;
        }

        public CompanySearchParam build() {
            mKeyMap.clear();
            if (searchFilter != null) {
                if (!TextUtils.isEmpty(searchFilter.keyWord)) {
                    mKeyMap.put("keywords", searchFilter.keyWord);
                }
                if (!TextUtils.isEmpty(searchFilter.title)) {
                    mKeyMap.put("title", searchFilter.title);
                }
                if (!TextUtils.isEmpty(searchFilter.company)) {
                    mKeyMap.put("company", searchFilter.company);
                }
                if (searchFilter.yearOfExperienceRegion > 0) {
                    mKeyMap.put("yearOfExperienceRegion", String.valueOf(searchFilter.yearOfExperienceRegion));
                }
                if (searchFilter.cityCode > 0) {
                    mKeyMap.put("location", String.valueOf(searchFilter.cityCode));
                }
//                if (!TextUtils.isEmpty(searchFilter.workYearName)) {
//                    mKeyMap.put("minExpr", String.valueOf(searchFilter.startYearParameter));
//                    mKeyMap.put("maxExpr", String.valueOf(searchFilter.endYearParameter));
//                }
                if (searchFilter.degreeParameter > 0) {
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
                if (isSearchMineResume && searchFilter.labelList != null && !searchFilter.labelList.isEmpty()) {
                    StringBuilder stringBuilder = new StringBuilder("[");
                    for (int i = 0, size = searchFilter.labelList.size(); i < size; i++) {
                        stringBuilder.append("\"").append(searchFilter.labelList.get(i).tag).append("\"").append(",");
                    }
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1).append("]");
                    String tags = stringBuilder.toString();
                    mKeyMap.put("tag", tags);
                }
                if (searchFilter.mandatorySort != CommonConst.RESUME_SEARCH_SORT_ORDER_NONE)
                    mKeyMap.put("mandatorySort", String.valueOf(searchFilter.mandatorySort));
            }
            if (!TextUtils.isEmpty(searchId))
                mKeyMap.put("searchId", searchId);
            mKeyMap.put("offset", String.valueOf(offset));
            mKeyMap.put("pageSize", String.valueOf(pageSize > 0 ? pageSize : CommonConst.DEFAULT_PAGE_SIZE));
            if (isCollected)
                mKeyMap.put("collected", "true");
            if (isSetExcludeMine)
                mKeyMap.put("excludeMine", isExcludeMine ? "true" : "false");
            if (isJobHunting)
                mKeyMap.put("isJobHunting", "true");
            if (isReturnJobHuntingCount)
                mKeyMap.put("returnJobHuntingCount", "true");
            if (isRecentContact)
                mKeyMap.put("recentContact", "1");
            if (isRecentCreate)
                mKeyMap.put("recentCreate", "1");
            if (jobHuntingTimeType == CommonConst.JobHunterPredictionType.NOW || jobHuntingTimeType == CommonConst.JobHunterPredictionType.PREVIOUS)
                mKeyMap.put("jobHuntingTimeType", String.valueOf(jobHuntingTimeType));
            if (isExcludeEmptyMobile)
                mKeyMap.put("isExcludeEmptyMobile", "true");
            if (notScan)
                mKeyMap.put("notScan", "true");
            return this;
        }
    }

    private static final String ENABLE_SCAN_ON_LINE_PATTERN = "\\S+(.pdf|.doc|.docx|.htm|.html|.mht|.txt|.rtf|.xls|.xlsx)";

    public static boolean isAttachmentEnableScanOnLine(String fileFullName) {
        return fileFullName != null && fileFullName.matches(ENABLE_SCAN_ON_LINE_PATTERN);
    }

    //    private static final long ONE_YEAR_TIME_MILLIONS = 365L * 24 * 60 * 60 * 1000;
    private static final HashMap<String, String> mResumeSourceFromMap = new HashMap<>();

    static {
        mResumeSourceFromMap.put("namelistmatch", "本地其他");
        mResumeSourceFromMap.put("namelist", "本地其他");
        mResumeSourceFromMap.put("localexcel", "本地其他");
        mResumeSourceFromMap.put("zhilianhp", "智联卓聘");
        mResumeSourceFromMap.put("xls", "小猎手");
        mResumeSourceFromMap.put("lagou", "拉勾");
        mResumeSourceFromMap.put("yilie", "易猎");
        mResumeSourceFromMap.put("trade", "有偿推荐");
        mResumeSourceFromMap.put("import", "导入");
        mResumeSourceFromMap.put("gllue", "谷露");
        mResumeSourceFromMap.put("share", "分享");
        mResumeSourceFromMap.put("passiveshare", "分享");
        mResumeSourceFromMap.put("liepin", "猎聘");
        mResumeSourceFromMap.put("zhilian", "智联");
        mResumeSourceFromMap.put("email", "邮箱");
        mResumeSourceFromMap.put("51job", "51Job");
        mResumeSourceFromMap.put("appadd", "本地");
        mResumeSourceFromMap.put("local", "本地");
    }

    public static String getSourceForShow(ResumeBean resumeBean) {
        return getSourceForShow(resumeBean.source);
    }

    public static String getSourceForShow(String source) {
        if (TextUtils.isEmpty(source))
            return null;
        return mResumeSourceFromMap.get(source.toLowerCase());
    }

    public static int findTargetResumePosition(ResumeBean resumeBean, List<ResumeBean> list) {
        if (resumeBean == null || list == null || resumeBean.candidateId == null)
            return CommonConst.LIST_POSITION_NONE;
        for (int i = 0, size = list.size(); i < size; i++) {
            if (list.get(i) == null)
                continue;
            if (resumeBean.candidateId.equals(list.get(i).candidateId))
                return i;
        }
        return CommonConst.LIST_POSITION_NONE;
    }

    public static int findTargetResumePositionByTalentId(String talentId, List<ResumeBean> list) {
        if (TextUtils.isEmpty(talentId) || list == null)
            return CommonConst.LIST_POSITION_NONE;
        for (int i = 0, size = list.size(); i < size; i++) {
            if (list.get(i) == null)
                continue;
            if (talentId.equals(list.get(i).talentId))
                return i;
        }
        return CommonConst.LIST_POSITION_NONE;
    }

    public static void doSearch(ResumeDao.SearchParam searchParam, final IDefaultRequestReplyListener<ResumeSearchBean> listener) {
        HttpRequestUtil.doHttpPost((searchParam.isSearchMineResume ? (searchParam.isCollected ? URLConst.URL_RESUME_MINE_SEARCH : URLConst.URL_RESUME_MINE) : URLConst.URL_RESUME_SEARCH_LIST), searchParam.getKeyMap(), ResumeSearchResultBean.class, new INetworkCallBack<ResumeSearchResultBean>() {
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

    public static void createOrUpdateResume(HashMap<String, String> param, boolean isUpdate, final IDefaultRequestReplyListener<CreateResumeResultBean> listener) {
        HttpRequestUtil.doHttpPost((isUpdate ? URLConst.URL_UPDATE_RESUME : URLConst.URL_CREATE_NEW_RESUME), param, CreateResumeResultBean.class, new INetworkCallBack<CreateResumeResultBean>() {
            @Override
            public void onSuccess(CreateResumeResultBean createResumeResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, createResumeResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }


    public static void addAudioAttachmentRemark(String candidateId, String ossKey, int audioLength, final IDefaultRequestReplyListener<RemarkBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("resumeId", candidateId);
        param.put("voiceInquiryKey", ossKey);
        param.put("voiceDuration", String.valueOf(audioLength));
        HttpRequestUtil.doHttpPost(URLConst.URL_CREATE_AUDIO_REMARK_URL, param, EditRemarkResultBean.class, new INetworkCallBack<EditRemarkResultBean>() {
            @Override
            public void onSuccess(EditRemarkResultBean editRemarkResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, editRemarkResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void addOrEditRemark(String candidateId, String inquiryId, String content, String jobHoppingOccasion, String employerInfo, String expectSalary, String company, String title, int flag, final IDefaultRequestReplyListener<RemarkBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        boolean update = !TextUtils.isEmpty(inquiryId);
        if (flag == 1) {
            if (update)
                param.put("id", inquiryId);
            param.put("candidateId", candidateId);
            param.put("content", content);
            if (!TextUtils.isEmpty(jobHoppingOccasion))
                param.put("jobHoppingOccasion", jobHoppingOccasion);
            if (!TextUtils.isEmpty(employerInfo))
                param.put("employerInfo", employerInfo);
            if (!TextUtils.isEmpty(expectSalary))
                param.put("expectSalary", expectSalary);
            if (!TextUtils.isEmpty(company))
                param.put("company", company);
            if (!TextUtils.isEmpty(title))
                param.put("title", title);
            param.put("isValidInquiry", String.valueOf(flag));
        } else {
            if (update)
                param.put("id", inquiryId);
            param.put("candidateId", candidateId);
            param.put("content", content);
            param.put("isValidInquiry", String.valueOf(flag));
        }
        HttpRequestUtil.doHttpPost(update ? URLConst.URL_UPDATE_REMARK : URLConst.URL_ADD_REMARK, param, EditRemarkResultBean.class, new INetworkCallBack<EditRemarkResultBean>() {
            @Override
            public void onSuccess(EditRemarkResultBean editRemarkResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, editRemarkResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void getResumeDetail(String candidateId, final IDefaultRequestReplyListener<ResumeDetailResultBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("candidateId", candidateId);
        HttpRequestUtil.doHttpPost(URLConst.URL_RESUME_DETAIL, param, ResumeDetailResultBean.class, new INetworkCallBack<ResumeDetailResultBean>() {
            @Override
            public void onSuccess(ResumeDetailResultBean candidateDetailResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, candidateDetailResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void deleteResume(String candidateId, final IDefaultRequestReplyListener<ResumeDetailResultBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("candidateId", candidateId);
        HttpRequestUtil.doHttpPost(URLConst.URL_DELETE_RESUME, param, ResumeDetailResultBean.class, new INetworkCallBack<ResumeDetailResultBean>() {
            @Override
            public void onSuccess(ResumeDetailResultBean candidateDetailResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, candidateDetailResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void saveSearchFilterToLocalDb(SearchFilterInfoBean searchFilterInfoBean, final String unionId) {
        searchFilterInfoBean.shouldProcessChanged();
        DBWorker.customerDBTask(searchFilterInfoBean, new IDBControllerHandler<SearchFilterInfoBean, Void>() {
            @Override
            public Void operateDataBaseAsync(DataBaseManager mDBManager, SearchFilterInfoBean input) {
                if (input == null || input.getId() == null)
                    return null;
                input.updateTime = System.currentTimeMillis();
                Cursor cursor = null;
                try {
                    boolean isInsert = true;
                    String table = DbClassUtil.getTableNameByAnnotationClass(SearchFilterInfoBean.class);
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
                return new Class[]{SearchFilterInfoBean.class, Label.class};
            }
        }, null, false);
    }

    public static void readSearchFilterFromLocalDb(final String unionId, final IDBReplyListener<List<SearchFilterInfoBean>> listener) {

        DBWorker.customerDBTask(unionId, new IDBControllerHandler<String, List<SearchFilterInfoBean>>() {
            @Override
            public List<SearchFilterInfoBean> operateDataBaseAsync(DataBaseManager mDBManager, String unionId) {
                Cursor cursor = null;
                Cursor cursor1Labels = null;
                List<SearchFilterInfoBean> list = null;
                try {
                    cursor = mDBManager.query(DbClassUtil.getTableNameByAnnotationClass(SearchFilterInfoBean.class), null, "unionId = '" + unionId + "'", null, null, null, " updateTime desc limit 20");
                    if (cursor != null && cursor.getCount() > 0) {
                        list = new ArrayList<>(cursor.getCount());
                        SearchFilterInfoBean searchFilterInfoBean;
                        while (cursor.moveToNext()) {
                            searchFilterInfoBean = DbClassUtil.buildObject(SearchFilterInfoBean.class, cursor, null);
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
                return new Class[]{SearchFilterInfoBean.class, Label.class};
            }
        }, listener, true);
    }

    public static void deleteSearchFilterFromLocalDb(final String unionId, SearchFilterInfoBean searchFilterInfoBean) {

        DBWorker.customerDBTask(searchFilterInfoBean, new IDBControllerHandler<SearchFilterInfoBean, Void>() {
            @Override
            public Void operateDataBaseAsync(DataBaseManager mDBManager, SearchFilterInfoBean input) {
                if (input == null) {
                    // 删除全部
                    mDBManager.delete(DbClassUtil.getTableNameByAnnotationClass(SearchFilterInfoBean.class), "unionId = '" + unionId + "'", null);
                    mDBManager.delete(DbClassUtil.getTableNameByAnnotationClass(Label.class), "unionId = '" + unionId + "'", null);
                    return null;
                }
                mDBManager.delete(DbClassUtil.getTableNameByAnnotationClass(SearchFilterInfoBean.class), "unionId = '" + unionId + "'" + " and id = '" + input.getId() + "'", null);
                mDBManager.delete(DbClassUtil.getTableNameByAnnotationClass(Label.class), "unionId = '" + unionId + "'" + " and filterId = '" + input.getId() + "'", null);
                return null;
            }

            @Override
            public Class<?>[] getDependModeClass() {
                return new Class[]{SearchFilterInfoBean.class, Label.class};
            }
        }, null, false);
    }

    public static void deleteRemark(String candidateId, String remarkId, final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("candidateId", candidateId);
        param.put("id", remarkId);
        HttpRequestUtil.doHttpPost(URLConst.URL_DELETE_REMARK, param, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
            @Override
            public void onSuccess(BaseHttpResultBean baseHttpResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, baseHttpResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void reportResume(String candidateId, String description, String type, final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("candidateId", candidateId);
        if (!TextUtils.isEmpty(description))
            param.put("description", description);
        if (type != null)
            param.put("type", type);
        HttpRequestUtil.doHttpPost(URLConst.URL_REPORT_RESUME, param, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
            @Override
            public void onSuccess(BaseHttpResultBean baseHttpResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, baseHttpResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void doImportResume(final File file, final IDefaultRequestReplyListener<ResumeImportResultBean> listener) {
        List<File> fileList = new ArrayList<>(1);
        fileList.add(file);
        HttpRequestUtil.doHttpWithFiles(URLConst.URL_IMPORT_RESUME, fileList, null, null, ResumeImportResultBean.class, new INetworkCallBack<ResumeImportResultBean>() {
            @Override
            public void onSuccess(ResumeImportResultBean resumeImportResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, resumeImportResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }


    public static String lineFeedResumeText(String text) {
//        Pattern p = Pattern.compile("\t|\r|\n", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern p = Pattern.compile("[\r\n]", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(text);
        String linePart = "\n";
        StringBuilder stringBuilder = new StringBuilder();
        int lastEnd = 0, start;
        try {
            while (m.find()) {
                start = m.start();
                if (start > lastEnd)
                    stringBuilder.append(text.substring(lastEnd, start).replaceAll("[\\u00A0]+", "").trim()).append(linePart);
                lastEnd = m.end();
            }
            if (lastEnd < text.length() - 1)
                stringBuilder.append(text.substring(lastEnd, text.length()).trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static void loadResumeClassifyList(int offset, int pageSize, int sourceType, String sourcePath, final IDefaultRequestReplyListener<ResumeClassifyResultBean.Data> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("offset", String.valueOf(offset));
        param.put("pageSize", String.valueOf(pageSize));
        if (sourceType > 0)
            param.put("sourceType", String.valueOf(sourceType));
        if (sourcePath != null)
            param.put("sourcePath", sourcePath);
        HttpRequestUtil.doHttpPost(URLConst.URL_RESUME_CLASSIFY_LIST, param, ResumeClassifyResultBean.class, new INetworkCallBack<ResumeClassifyResultBean>() {
            @Override
            public void onSuccess(ResumeClassifyResultBean resumeClassifyResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, resumeClassifyResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void loadOnLineAttachmentInfo(String resumeId, String fileId, String sourceChannel, final IDefaultRequestReplyListener<ResumeOnLineResultBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("candidateId", resumeId);
        if (fileId != null)
            param.put("fileId", fileId);
        if (sourceChannel != null)
            param.put("sourceChannel", sourceChannel);
        CustomerHttpRequestUtil.doHttpPost(URLConst.URL_RESUME_ONLINE, param, ResumeOnLineResultBean.class, new INetworkCallBack<CustomerHttpResultBean<ResumeOnLineResultBean>>() {
            @Override
            public void onSuccess(CustomerHttpResultBean<ResumeOnLineResultBean> resumeOnLineResultBeanCustomerHttpResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, resumeOnLineResultBeanCustomerHttpResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));

            }
        });
    }

    public static void loadCompanyLibInfoOfResume(String resumeId, final IDefaultRequestReplyListener<CompanyLibInfoOfResumeBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("candidateId", resumeId);
        CustomerHttpRequestUtil.doHttpPost(URLConst.URL_CHECK_RESUME_IS_IN_COMPANY_LIB, param, CompanyLibInfoOfResumeBean.class, new INetworkCallBack<CustomerHttpResultBean<CompanyLibInfoOfResumeBean>>() {
            @Override
            public void onSuccess(CustomerHttpResultBean<CompanyLibInfoOfResumeBean> companyLibInfoOfResumeBeanCustomerHttpResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, companyLibInfoOfResumeBeanCustomerHttpResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));

            }
        });
    }

    public static final int VALID_TYPE_MOBILE = 0;
    public static final int VALID_TYPE_EMAIL = 1;

    public static void markValid(String resumeId, int type, final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("candidateId", resumeId);
        param.put("validType", String.valueOf(type));
        HttpRequestUtil.doHttpPost(URLConst.URL_RESUME_MARK_VALID, param, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
            @Override
            public void onSuccess(BaseHttpResultBean baseHttpResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, baseHttpResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    //企业人才搜索
    public static void doCompanySearch(ResumeDao.CompanySearchParam searchParam, final IDefaultRequestReplyListener<ResumeSearchBean> listener) {
        String requestUrl = "";
        if (searchParam.searchType == CommonConst.INT_SEARCH_TYPE_COMPANY) {
            requestUrl = URLConst.URL_COMPANY_RESUME_SEARCH_LIST;
        } else if (searchParam.searchType == CommonConst.INT_SEARCH_TYPE_COLLECTION) {
            requestUrl = URLConst.URL_RESUME_MINE_SEARCH;
        } else if (searchParam.searchType == CommonConst.INT_SEARCH_TYPE_TRACKING) {
            requestUrl = URLConst.URL_TRACKINGLIST_SEARCH;
        }
        HttpRequestUtil.doHttpPost(requestUrl, searchParam.getKeyMap(), ResumeSearchResultBean.class, new INetworkCallBack<ResumeSearchResultBean>() {
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

    //企业人才详情
    public static void getCompanyResumeDetail(String candidateId, final IDefaultRequestReplyListener<ResumeDetailResultBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("candidateId", candidateId);
        HttpRequestUtil.doHttpPost(URLConst.URL_COMPANY_RESUME_DETAIL, param, ResumeDetailResultBean.class, new INetworkCallBack<ResumeDetailResultBean>() {
            @Override
            public void onSuccess(ResumeDetailResultBean candidateDetailResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, candidateDetailResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    //查看联系方式
    public static void checkContact(int type, String id, final IDefaultRequestReplyListener<ContactResultBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("candidateId", id);
        param.put("viewType", type + "");
        HttpRequestUtil.doHttpPost(URLConst.URL_VIEW_CONTACT, param, ContactResultBean.class, new INetworkCallBack<ContactResultBean>() {
            @Override
            public void onSuccess(ContactResultBean bean) {
                if (listener != null)
                    listener.onRequestReply(true, bean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    //添加到TrackingList / 从TrackingList 移除
    public static void addOrRemoveTrackingList(int type, String id, final IDefaultRequestReplyListener<AddOrRemoveTrackingListResultBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("resumeId", id);
        param.put("operateType", type + "");
        HttpRequestUtil.doHttpPost(URLConst.URL_TRACKINGLIST_ADD_OR_REMOVE, param, AddOrRemoveTrackingListResultBean.class, new INetworkCallBack<AddOrRemoveTrackingListResultBean>() {
            @Override
            public void onSuccess(AddOrRemoveTrackingListResultBean bean) {
                if (listener != null)
                    listener.onRequestReply(true, bean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }


    //tracking人才搜索
    public static void doTrackingSearch(ResumeDao.SearchParam searchParam, final IDefaultRequestReplyListener<TrackingSearchResultBean> listener) {
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
    public static void saveCompanySearchFilterToLocalDb(CompanySearchFilterInfoBean searchFilterInfoBean, final String unionId) {
        searchFilterInfoBean.shouldProcessChanged();
        DBWorker.customerDBTask(searchFilterInfoBean, new IDBControllerHandler<CompanySearchFilterInfoBean, Void>() {
            @Override
            public Void operateDataBaseAsync(DataBaseManager mDBManager, CompanySearchFilterInfoBean input) {
                if (input == null || input.getId() == null)
                    return null;
                input.updateTime = System.currentTimeMillis();
                Cursor cursor = null;
                try {
                    boolean isInsert = true;
                    String table = DbClassUtil.getTableNameByAnnotationClass(CompanySearchFilterInfoBean.class);
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
                return new Class[]{CompanySearchFilterInfoBean.class, Label.class};
            }
        }, null, false);
    }

    /**
     * 查询企业搜索记录
     *
     * @param unionId
     * @param listener
     */
    public static void readCompanySearchFilterFromLocalDb(final String unionId, final IDBReplyListener<List<CompanySearchFilterInfoBean>> listener) {

        DBWorker.customerDBTask(unionId, new IDBControllerHandler<String, List<CompanySearchFilterInfoBean>>() {
            @Override
            public List<CompanySearchFilterInfoBean> operateDataBaseAsync(DataBaseManager mDBManager, String unionId) {
                Cursor cursor = null;
                Cursor cursor1Labels = null;
                List<CompanySearchFilterInfoBean> list = null;
                try {
                    cursor = mDBManager.query(DbClassUtil.getTableNameByAnnotationClass(CompanySearchFilterInfoBean.class), null, "unionId = '" + unionId + "'", null, null, null, " updateTime desc limit 20");
                    if (cursor != null && cursor.getCount() > 0) {
                        list = new ArrayList<>(cursor.getCount());
                        CompanySearchFilterInfoBean searchFilterInfoBean;
                        while (cursor.moveToNext()) {
                            searchFilterInfoBean = DbClassUtil.buildObject(CompanySearchFilterInfoBean.class, cursor, null);
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
                return new Class[]{CompanySearchFilterInfoBean.class, Label.class};
            }
        }, listener, true);
    }

    /**
     * 清除企业搜索历史
     *
     * @param unionId
     * @param searchFilterInfoBean
     */
    public static void deleteCompanySearchFilterFromLocalDb(final String unionId, CompanySearchFilterInfoBean searchFilterInfoBean) {

        DBWorker.customerDBTask(searchFilterInfoBean, new IDBControllerHandler<CompanySearchFilterInfoBean, Void>() {
            @Override
            public Void operateDataBaseAsync(DataBaseManager mDBManager, CompanySearchFilterInfoBean input) {
                if (input == null) {
                    // 删除全部
                    mDBManager.delete(DbClassUtil.getTableNameByAnnotationClass(CompanySearchFilterInfoBean.class), "unionId = '" + unionId + "'", null);
                    mDBManager.delete(DbClassUtil.getTableNameByAnnotationClass(Label.class), "unionId = '" + unionId + "'", null);
                    return null;
                }
                mDBManager.delete(DbClassUtil.getTableNameByAnnotationClass(CompanySearchFilterInfoBean.class), "unionId = '" + unionId + "'" + " and id = '" + input.getId() + "'", null);
                mDBManager.delete(DbClassUtil.getTableNameByAnnotationClass(Label.class), "unionId = '" + unionId + "'" + " and filterId = '" + input.getId() + "'", null);
                return null;
            }

            @Override
            public Class<?>[] getDependModeClass() {
                return new Class[]{CompanySearchFilterInfoBean.class, Label.class};
            }
        }, null, false);
    }
}
