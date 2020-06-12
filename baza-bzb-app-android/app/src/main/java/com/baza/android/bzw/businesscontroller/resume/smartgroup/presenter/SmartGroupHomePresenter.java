package com.baza.android.bzw.businesscontroller.resume.smartgroup.presenter;

import android.text.TextUtils;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.smartgroup.SmartGroupFoldersResultBean;
import com.baza.android.bzw.businesscontroller.resume.smartgroup.viewinterface.ISmartGroupHomeView;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.dao.SmartGroupDao;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.log.logger.ResumeLogger;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2018/8/29.
 * Title：
 * Note：
 */
public class SmartGroupHomePresenter extends BasePresenter {
    private ISmartGroupHomeView mSmartGroupHomeView;
    private int mGroupType;
    private int mOffset;
    private List<SmartGroupFoldersResultBean.SmartGroupFolderBean> mDataList = new ArrayList<>();
    private String mLastKeyword;
    private int mHttpId;
    private ResumeLogger mSearchLogger = new ResumeLogger();

    public SmartGroupHomePresenter(ISmartGroupHomeView smartGroupHomeView, int groupType) {
        this.mSmartGroupHomeView = smartGroupHomeView;
        this.mGroupType = groupType;
    }

    @Override
    public void initialize() {
        //自定义分组在onResume中更新
        if (mGroupType != CommonConst.SmartGroupType.GROUP_TYPE_CUSTOMER)
            loadSmartGroupFolders(true, false);
    }

    public List<SmartGroupFoldersResultBean.SmartGroupFolderBean> getDataList() {
        return mDataList;
    }

    public void onKeywordChanged(String keyword) {
        mLastKeyword = keyword;
        loadSmartGroupFolders(true, true);
    }

    public String getLastKeyword() {
        return mLastKeyword;
    }

//    public void onGroupTypeChanged(int type) {
//        mGroupType = type;
//        if (mGroupType != CommonConst.SmartGroupType.GROUP_TYPE_TITLE && mGroupType != CommonConst.SmartGroupType.GROUP_TYPE_COMPANY)
//            mLastKeyword = null;
//        loadSmartGroupFolders(true, true);
//        mSearchLogger.sendSmartGroupSelectedLog(mSmartGroupHomeView, (mGroupType == CommonConst.SmartGroupType.GROUP_TYPE_CUSTOMER ? "UserDefine" : getTypeParamMap()));
//    }

    @Override
    public void onResume() {
        if (mGroupType == CommonConst.SmartGroupType.GROUP_TYPE_CUSTOMER)
            loadSmartGroupFolders(true, false);
    }

    private String getTypeParamMap() {
        String typeStr = null;
        switch (mGroupType) {
            case CommonConst.SmartGroupType.GROUP_TYPE_TIME:
                typeStr = "Year";
                break;
            case CommonConst.SmartGroupType.GROUP_TYPE_TITLE:
                typeStr = "Title";
                break;
            case CommonConst.SmartGroupType.GROUP_TYPE_COMPANY:
                typeStr = "Company";
                break;
            case CommonConst.SmartGroupType.GROUP_TYPE_DEGREE:
                typeStr = "Degree";
                break;
            case CommonConst.SmartGroupType.GROUP_TYPE_WORK_EXPERIENCE:
                typeStr = "YearOfWorkExperience";
                break;
        }
        return typeStr;
    }

    public boolean isGroupNameExist(String groupName) {
        if (TextUtils.isEmpty(groupName))
            return false;
        for (int i = 0, size = mDataList.size(); i < size; i++) {
            if (groupName.equals(mDataList.get(i).groupName))
                return true;
        }
        return false;
    }

    public void loadSmartGroupFolders(boolean refresh, boolean showLoadingView) {
        if (showLoadingView)
            mSmartGroupHomeView.callShowLoadingView(null);
        mHttpId++;
        final int httpId = mHttpId;
        mOffset = (refresh ? 0 : mDataList.size());
        SmartGroupDao.loadSmartGroups(mOffset, mGroupType == CommonConst.SmartGroupType.GROUP_TYPE_CUSTOMER, getTypeParamMap(), mLastKeyword, new IDefaultRequestReplyListener<SmartGroupFoldersResultBean.Data>() {
            @Override
            public void onRequestReply(boolean success, SmartGroupFoldersResultBean.Data data, int errorCode, String errorMsg) {
                if (httpId != mHttpId)
                    return;
                mSmartGroupHomeView.callCancelLoadingView(success, errorCode, errorMsg);
                if (mOffset == 0)
                    mDataList.clear();
                if (success && data != null) {
                    if (data.recordList != null)
                        mDataList.addAll(data.recordList);
                    mSmartGroupHomeView.callUpdateLoadAllDataView(mDataList.size() >= data.totalCount);
                    if (mOffset == 0 && !TextUtils.isEmpty(mLastKeyword))
                        mSearchLogger.sendSmartGroupFolderSearch(true, mSmartGroupHomeView, getTypeParamMap(), mLastKeyword, mDataList.size());
                }
                mSmartGroupHomeView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
            }
        });
    }

    public void createGroup(String groupName) {
        mSmartGroupHomeView.callShowProgress(null, true);
        SmartGroupDao.createSelfDefineGroup(groupName, new IDefaultRequestReplyListener<SmartGroupFoldersResultBean.SmartGroupFolderBean>() {
            @Override
            public void onRequestReply(boolean success, SmartGroupFoldersResultBean.SmartGroupFolderBean smartGroupFolderBean, int errorCode, String errorMsg) {
                mSmartGroupHomeView.callCancelProgress();
                if (success) {
                    mSmartGroupHomeView.callShowToastMessage(errorMsg, R.string.create_smart_group_success);
                    if (smartGroupFolderBean != null) {
                        mDataList.add(0, smartGroupFolderBean);
                        mSmartGroupHomeView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
                    }
                    return;
                }
                mSmartGroupHomeView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public void updateGroupName(final SmartGroupFoldersResultBean.SmartGroupFolderBean smartGroup, String newGroupName) {
        mSmartGroupHomeView.callShowProgress(null, true);
        SmartGroupDao.updateSelfDefineGroup(smartGroup.id, newGroupName, new IDefaultRequestReplyListener<SmartGroupFoldersResultBean.SmartGroupFolderBean>() {
            @Override
            public void onRequestReply(boolean success, SmartGroupFoldersResultBean.SmartGroupFolderBean smartGroupFolderBean, int errorCode, String errorMsg) {
                mSmartGroupHomeView.callCancelProgress();
                if (success) {
                    mSmartGroupHomeView.callShowToastMessage(errorMsg, R.string.update_smart_group_success);
                    if (smartGroupFolderBean != null) {
                        smartGroup.groupName = smartGroupFolderBean.groupName;
                        smartGroup.resumeCount = smartGroupFolderBean.resumeCount;
                        mSmartGroupHomeView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
                    }
                    return;
                }
                mSmartGroupHomeView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public void deleteGroup(final SmartGroupFoldersResultBean.SmartGroupFolderBean smartGroup) {
        mSmartGroupHomeView.callShowProgress(null, true);
        SmartGroupDao.deleteSelfDefineGroup(smartGroup.id, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                mSmartGroupHomeView.callCancelProgress();
                if (success) {
                    mDataList.remove(smartGroup);
                    mSmartGroupHomeView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
                    mSmartGroupHomeView.callShowToastMessage(errorMsg, R.string.delete_smart_group_success);
                    return;
                }
                mSmartGroupHomeView.callShowToastMessage(errorMsg, 0);
            }
        });
    }
}
