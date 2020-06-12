package com.baza.android.bzw.businesscontroller.label.presenter;

import android.text.TextUtils;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.label.Label;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.businesscontroller.label.viewinterface.ILabelLibraryView;
import com.baza.android.bzw.constant.ActionConst;
import com.baza.android.bzw.dao.LabelDao;
import com.baza.android.bzw.events.ILabelEventSubscriber;
import com.baza.android.bzw.events.IResumeEventsSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.manager.LabelCacheManager;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/5/25.
 * Title：
 * Note：
 */

public class LabelLibraryPresenter extends BasePresenter {
    private ILabelLibraryView mLabelLibraryView;
    private ArrayList<Label> mLabelList = new ArrayList<>();
    private Object mFilter = new Object();
    private boolean mShouldResetLabel;

    public LabelLibraryPresenter(ILabelLibraryView mLabelLibraryView) {
        this.mLabelLibraryView = mLabelLibraryView;
    }

    @Override
    public void initialize() {
        loadLabelLibrary();
        subscribeEvents(true);
    }

    public ArrayList<Label> getLabelList() {
        return mLabelList;
    }

    @Override
    public void onResume() {
        if (mShouldResetLabel) {
            mShouldResetLabel = false;
            loadLabelLibrary();
        }
    }

    @Override
    public void onDestroy() {
        subscribeEvents(false);
    }

    public void loadLabelLibrary() {
        mLabelLibraryView.callShowLoadingView();
        LabelDao.loadLabelLibrary(new IDefaultRequestReplyListener<List<Label>>() {
            @Override
            public void onRequestReply(boolean success, List<Label> labels, int errorCode, String errorMsg) {
                mLabelLibraryView.callCancelLoadingView(success, errorCode, errorMsg);
                mLabelList.clear();
                if (labels != null && !labels.isEmpty()) {
                    mLabelList.addAll(labels);
                    LabelCacheManager.getInstance().setLabels(mLabelList);
                }
                mLabelLibraryView.callRefreshLabelsView();
                mLabelLibraryView.callShowNoDataView(mLabelList.isEmpty());
            }
        });
    }

    public void deleteLabel(final Label label) {
        mLabelLibraryView.callShowProgress(null, true);
        LabelDao.deleteTag(label.tag, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                mLabelLibraryView.callCancelProgress();
                mLabelLibraryView.callShowToastMessage(null, R.string.has_delete);
                LabelCacheManager.getInstance().deleteLabel(label);
                mLabelList.remove(label);
                mLabelLibraryView.callRefreshLabelsView();
                mLabelLibraryView.callShowNoDataView(mLabelList.isEmpty());
                UIEventsObservable.getInstance().postEvent(ILabelEventSubscriber.class, ActionConst.ACTION_EVENT_LABEL_DELETED, label, mFilter);
            }
        });
    }

    private void subscribeEvents(boolean isSubscribe) {
        if (isSubscribe) {
            UIEventsObservable.getInstance().subscribeEvent(ILabelEventSubscriber.class, this, new ILabelEventSubscriber() {
                @Override
                public boolean isFilterByTag(Object sendTag) {
                    return sendTag == mFilter;
                }

                @Override
                public boolean onLabelDeleted(Label label) {
                    mLabelList.clear();
                    mLabelList.addAll(LabelCacheManager.getInstance().getAllLabels());
                    mLabelLibraryView.callRefreshLabelsView();
                    return false;
                }

                @Override
                public boolean onLabelCreated(Label label) {
                    mLabelList.clear();
                    mLabelList.addAll(LabelCacheManager.getInstance().getAllLabels());
                    mLabelLibraryView.callRefreshLabelsView();
                    return false;
                }
            });
            UIEventsObservable.getInstance().subscribeEvent(IResumeEventsSubscriber.class, this, new IResumeEventsSubscriber() {
                @Override
                public boolean onResumeDeletedObserver(ResumeBean data, Object extra) {
                    //删除简历  标签对应的简历数目可能发生变化 需要重新拉取一次
                    mShouldResetLabel = true;
                    return false;
                }

                @Override
                public boolean onResumeLabelsChanged(ResumeBean data, Object extra) {
                    mShouldResetLabel = true;
                    return false;
                }
            });
        } else {
            UIEventsObservable.getInstance().stopSubscribeEvent(IResumeEventsSubscriber.class, this);
            UIEventsObservable.getInstance().stopSubscribeEvent(ILabelEventSubscriber.class, this);
        }
    }

    public boolean checkEnableToCreateNewTag(String tag) {
        if (TextUtils.isEmpty(tag)) {
            mLabelLibraryView.callShowToastMessage(null, R.string.input_label_msg);
            return false;
        }
        if (mLabelList.isEmpty())
            return true;
        for (Label label : mLabelList) {
            if (tag.equals(label.tag)) {
                mLabelLibraryView.callShowToastMessage(null, R.string.create_label_not_same_hint);
                return false;
            }
        }
        return true;
    }

    public void createNewTag(String tag) {
        mLabelLibraryView.callShowProgress(null, true);
        LabelDao.createNewTag(tag, new IDefaultRequestReplyListener<Label>() {
            @Override
            public void onRequestReply(boolean success, Label label, int errorCode, String errorMsg) {
                mLabelLibraryView.callCancelProgress();
                if (success) {
                    mLabelList.add(0, label);
                    mLabelLibraryView.callRefreshLabelsView();
                    mLabelLibraryView.callShowNoDataView(mLabelList.isEmpty());
                    mLabelLibraryView.callShowToastMessage(null, R.string.create_label_success);
                    LabelCacheManager.getInstance().addLabel(label);
                    UIEventsObservable.getInstance().postEvent(ILabelEventSubscriber.class, ActionConst.ACTION_EVENT_LABEL_CREATED, label, mFilter);
                    return;
                }
                mLabelLibraryView.callShowToastMessage(errorMsg, 0);
            }
        });
    }
}
