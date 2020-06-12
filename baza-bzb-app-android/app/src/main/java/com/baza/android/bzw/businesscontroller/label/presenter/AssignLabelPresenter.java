package com.baza.android.bzw.businesscontroller.label.presenter;

import android.content.Intent;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.label.Label;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.businesscontroller.label.viewinterface.IAssignLabelView;
import com.baza.android.bzw.constant.ActionConst;
import com.baza.android.bzw.dao.LabelDao;
import com.baza.android.bzw.events.ILabelEventSubscriber;
import com.baza.android.bzw.events.IResumeEventsSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.manager.LabelCacheManager;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/5/25.
 * Title：
 * Note：
 */

public class AssignLabelPresenter extends BasePresenter {
//    private static final int MAX_ASSIGN_LABEL_COUNT = 5;
    private IAssignLabelView mAssignLabelView;

    private ArrayList<Label> mLabelLibrary = new ArrayList<>();
    private HashMap<String, Label> mOriginalLabelMap;
    private ArrayList<Label> mLabelAssigned = new ArrayList<>();
    private Object mFilter = new Object();
    private ResumeBean mResumeBean;

    public AssignLabelPresenter(IAssignLabelView mAssignLabelView, Intent intent) {
        this.mAssignLabelView = mAssignLabelView;
        mResumeBean = (ResumeBean) intent.getSerializableExtra("resumeBean");
        if (mResumeBean != null && mResumeBean.tagBindingList != null && !mResumeBean.tagBindingList.isEmpty()) {
            mLabelAssigned.addAll(mResumeBean.tagBindingList);
            mOriginalLabelMap = new HashMap<>(mLabelAssigned.size());
            Label label;
            for (int i = 0, size = mLabelAssigned.size(); i < size; i++) {
                label = mLabelAssigned.get(i);
                mOriginalLabelMap.put(label.tag, label);
            }
        }
    }

    @Override
    public void initialize() {
        mLabelLibrary.addAll(LabelCacheManager.getInstance().getAllLabels());
        if (!mLabelLibrary.isEmpty())
            mAssignLabelView.callSetLabelLibrary(mLabelLibrary);
        else
            loadLabelLibrary();
    }

    private void loadLabelLibrary() {
        LabelDao.loadLabelLibrary(new IDefaultRequestReplyListener<List<Label>>() {
            @Override
            public void onRequestReply(boolean success, List<Label> list, int errorCode, String errorMsg) {
                if (success) {
                    if (list != null && !list.isEmpty()) {
                        mLabelLibrary.clear();
                        mLabelLibrary.addAll(list);
                        mAssignLabelView.callSetLabelLibrary(mLabelLibrary);
                        LabelCacheManager.getInstance().setLabels(mLabelLibrary);
                        UIEventsObservable.getInstance().postEvent(ILabelEventSubscriber.class, ActionConst.ACTION_EVENT_LABEL_LIBRARY_GET, null, mFilter);
                    }
                    return;
                }
                mAssignLabelView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public boolean isLabelInAssignedList(String tagName) {
        if (mLabelAssigned.isEmpty())
            return false;
        for (int i = 0, size = mLabelAssigned.size(); i < size; i++) {
            if (tagName.equals(mLabelAssigned.get(i).tag))
                return true;
        }
        return false;
    }

    public boolean tryToAddLabel(Label labelNeedAdded) {
//        if (mLabelAssigned.size() >= MAX_ASSIGN_LABEL_COUNT) {
//            mAssignLabelView.callShowToastMessage(null, R.string.assign_label_count_limit);
//            return false;
//        }
        mLabelAssigned.add(labelNeedAdded);
        return true;
    }

    public void tryToDeleteLabel(Label labelNeedAdded) {
        for (int i = 0, size = mLabelAssigned.size(); i < size; i++) {
            if (labelNeedAdded.tag.equals(mLabelAssigned.get(i).tag)) {
                mLabelAssigned.remove(i);
                break;
            }
        }
    }

    public void saveLabelForResume() {
        mAssignLabelView.callShowProgress(null, true);
        LabelDao.saveLabelForResume(mResumeBean.candidateId, mLabelAssigned, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                mAssignLabelView.callCancelProgress();
                if (success) {
                    mResumeBean.tagBindingList = mLabelAssigned;
                    mAssignLabelView.callExists();
                    UIEventsObservable.getInstance().postEvent(IResumeEventsSubscriber.class, ActionConst.ACTION_EVENT_RESUME_LABEL_CHANGED, mResumeBean, mFilter);
                    return;
                }
                mAssignLabelView.callShowToastMessage(errorMsg, 0);
            }
        });
    }


    public void createNewTag(String tag) {
        mAssignLabelView.callShowProgress(null, true);
        LabelDao.createNewTag(tag, new IDefaultRequestReplyListener<Label>() {
            @Override
            public void onRequestReply(boolean success, Label label, int errorCode, String errorMsg) {
                mAssignLabelView.callCancelProgress();
                if (success) {
                    LabelCacheManager.getInstance().addLabel(label);
                    UIEventsObservable.getInstance().postEvent(ILabelEventSubscriber.class, ActionConst.ACTION_EVENT_LABEL_CREATED, label, mFilter);
                    mLabelAssigned.add(label);
                    mLabelLibrary.add(0, label);
                    mAssignLabelView.callSetLabelLibrary(mLabelLibrary);
                    mAssignLabelView.callShowToastMessage(null, R.string.create_label_success);
                    return;
                }
                mAssignLabelView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    /**
     * 判断是否需要提醒用户保存
     *
     * @return true 提醒 false不提醒
     */
    public boolean shouldHintSave() {
        boolean notHasOriginalData = mOriginalLabelMap == null || mOriginalLabelMap.isEmpty();
        //原始的没有 现在的有
        if (notHasOriginalData && !mLabelAssigned.isEmpty())
            return true;
        //原始的有  现在的没有
        if (!notHasOriginalData && mLabelAssigned.isEmpty())
            return true;
        // 原始的和现在的都有 但数量不同
        if (!notHasOriginalData && mLabelAssigned.size() != mOriginalLabelMap.size())
            return true;
        // 原始的和现在的都有 数量想同 判断是否有差别
        for (int indexAssigned = 0, sizeAssigned = mLabelAssigned.size(); indexAssigned < sizeAssigned; indexAssigned++) {
            if (mOriginalLabelMap.get(mLabelAssigned.get(indexAssigned).tag) == null)
                return true;
        }
        return false;
    }

    public boolean checkEnableToCreateNewTag(String tag) {
        if (mLabelLibrary.isEmpty())
            return true;
        for (int i = 0, size = mLabelLibrary.size(); i < size; i++) {
            if (tag.equals(mLabelLibrary.get(i).tag)) {
                mAssignLabelView.callShowToastMessage(null, R.string.create_label_not_same_hint);
                return false;
            }
        }
        return true;
    }
}
