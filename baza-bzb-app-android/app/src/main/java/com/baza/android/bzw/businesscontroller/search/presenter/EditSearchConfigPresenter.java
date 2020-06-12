package com.baza.android.bzw.businesscontroller.search.presenter;

import android.content.Intent;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.searchfilterbean.CompanySearchFilterInfoBean;
import com.baza.android.bzw.bean.searchfilterbean.SearchFilterInfoBean;
import com.baza.android.bzw.bean.searchfilterbean.TrackingSearchFilterInfoBean;
import com.baza.android.bzw.businesscontroller.search.viewinterface.IEditSearchConfigView;
import com.baza.android.bzw.dao.ResumeDao;
import com.baza.android.bzw.dao.TrackingResumeDao;
import com.baza.android.bzw.manager.UserInfoManager;
import com.bznet.android.rcbox.R;
import com.slib.storage.database.listener.IDBReplyListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/5/23.
 * Title：
 * Note：
 */

public class EditSearchConfigPresenter extends BasePresenter {
    private IEditSearchConfigView mEditSearchConfigView;
    private int mHistoryType;
    private List<Object> mHistoryList = new ArrayList<>();
    private String mLastKeyword;

    public EditSearchConfigPresenter(IEditSearchConfigView mEditSearchConfigView, Intent intent) {
        this.mEditSearchConfigView = mEditSearchConfigView;
        this.mHistoryType = intent.getIntExtra("historyType", IEditSearchConfigView.MODE_RESUME_SEARCH_HISTORY);
        this.mLastKeyword = intent.getStringExtra("keyword");
    }

    private int getSearchHintText() {
//        int titleId;
//        switch (mHistoryType) {
//            case IEditSearchConfigView.MODE_RESUME_SEARCH_HISTORY:
//                titleId = (isSearchCloud() ? R.string.hint_search_other_candidate : R.string.hint_search_mine_candidate);
//                break;
//            case IEditSearchConfigView.MODE_SG_COMPANY_HISTORY:
//                titleId = R.string.group_folder_search_hint_company;
//                break;
//            case IEditSearchConfigView.MODE_SG_TITLE_HISTORY:
//                titleId = R.string.group_folder_search_hint_title;
//                break;
//            case IEditSearchConfigView.MODE_NAME_LIST_SEARCH_HISTORY:
//                titleId = R.string.hint_search_name_list;
//                break;
//            default:
//                titleId = R.string.smart_group_search_default_hint;
//                break;
//        }
//        return titleId;
        return R.string.multiple_search_keyword_hint;
    }

    @Override
    public void initialize() {
        mEditSearchConfigView.callUpdateKeywordHint(getSearchHintText());
        mEditSearchConfigView.callUpdateKeyWordView(mLastKeyword);
        readSearchHistory();
    }

    public List<Object> getHistoryList() {
        return mHistoryList;
    }

    public void removeTargetHistory(int position) {
        if (mHistoryList == null || position >= mHistoryList.size())
            return;
        Object o = mHistoryList.remove(position);
        mEditSearchConfigView.callSetHistory();

        if (mHistoryType == IEditSearchConfigView.MODE_RESUME_SEARCH_HISTORY) {
            ResumeDao.deleteSearchFilterFromLocalDb(UserInfoManager.getInstance().getUserInfo().unionId, (SearchFilterInfoBean) o);
        } else if (mHistoryType == IEditSearchConfigView.MODE_COMPANY_SEARCH_HISTORY) {
            ResumeDao.deleteCompanySearchFilterFromLocalDb(UserInfoManager.getInstance().getUserInfo().unionId, (CompanySearchFilterInfoBean) o);
        } else if (mHistoryType == IEditSearchConfigView.MODE_TRACKING_LIST_HISTORY) {
            TrackingResumeDao.deleteCompanySearchFilterFromLocalDb(UserInfoManager.getInstance().getUserInfo().unionId, (TrackingSearchFilterInfoBean) o);
        }
    }

    public void clearHistory() {
        mHistoryList.clear();
        mEditSearchConfigView.callSetHistory();
        if (mHistoryType == IEditSearchConfigView.MODE_RESUME_SEARCH_HISTORY) {
            ResumeDao.deleteSearchFilterFromLocalDb(UserInfoManager.getInstance().getUserInfo().unionId, null);
        } else if (mHistoryType == IEditSearchConfigView.MODE_COMPANY_SEARCH_HISTORY) {
            ResumeDao.deleteCompanySearchFilterFromLocalDb(UserInfoManager.getInstance().getUserInfo().unionId, null);
        } else if (mHistoryType == IEditSearchConfigView.MODE_TRACKING_LIST_HISTORY) {
            TrackingResumeDao.deleteCompanySearchFilterFromLocalDb(UserInfoManager.getInstance().getUserInfo().unionId, null);
        }
    }

    private void readSearchHistory() {
        if (mHistoryType == IEditSearchConfigView.MODE_RESUME_SEARCH_HISTORY) {
            ResumeDao.readSearchFilterFromLocalDb(UserInfoManager.getInstance().getUserInfo().unionId, new IDBReplyListener<List<SearchFilterInfoBean>>() {
                @Override
                public void onDBReply(List<SearchFilterInfoBean> list) {
                    if (list != null) {
                        if (list.size() > 5) {
                            mHistoryList.addAll(list.subList(0, 5));
                        } else {
                            mHistoryList.addAll(list);
                        }
                    }
                    mEditSearchConfigView.callSetHistory();
                }
            });
        } else if (mHistoryType == IEditSearchConfigView.MODE_COMPANY_SEARCH_HISTORY) {
            ResumeDao.readCompanySearchFilterFromLocalDb(UserInfoManager.getInstance().getUserInfo().unionId, new IDBReplyListener<List<CompanySearchFilterInfoBean>>() {
                @Override
                public void onDBReply(List<CompanySearchFilterInfoBean> list) {
                    if (list != null) {
                        if (list.size() > 5) {
                            mHistoryList.addAll(list.subList(0, 5));
                        } else {
                            mHistoryList.addAll(list);
                        }
                    }
                    mEditSearchConfigView.callSetHistory();
                }
            });
        } else if (mHistoryType == IEditSearchConfigView.MODE_TRACKING_LIST_HISTORY) {
            TrackingResumeDao.readCompanySearchFilterFromLocalDb(UserInfoManager.getInstance().getUserInfo().unionId, new IDBReplyListener<List<TrackingSearchFilterInfoBean>>() {
                @Override
                public void onDBReply(List<TrackingSearchFilterInfoBean> list) {
                    if (list != null) {
                        if (list.size() > 5) {
                            mHistoryList.addAll(list.subList(0, 5));
                        } else {
                            mHistoryList.addAll(list);
                        }
                    }
                    mEditSearchConfigView.callSetHistory();
                }
            });
        }
    }
}
