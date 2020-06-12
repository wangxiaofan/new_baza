package com.baza.android.bzw.businesscontroller.find.updateengine;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseFragment;
import com.baza.android.bzw.bean.resume.ResumeUpdatedContentResultBean;
import com.baza.android.bzw.bean.resumeelement.EducationUnion;
import com.baza.android.bzw.bean.resumeelement.IntentionUnion;
import com.baza.android.bzw.bean.resumeelement.ProjectUnion;
import com.baza.android.bzw.bean.resumeelement.WorkExperienceUnion;
import com.baza.android.bzw.bean.updateengine.UpdateResumeWrapperBean;
import com.baza.android.bzw.businesscontroller.find.updateengine.cardcontentui.UpdateCardContentSelfEvaluationUI;
import com.baza.android.bzw.businesscontroller.find.updateengine.cardcontentui.UpdateCardContentTopUI;
import com.baza.android.bzw.businesscontroller.find.updateengine.cardcontentui.UpdateCardEducationUI;
import com.baza.android.bzw.businesscontroller.find.updateengine.cardcontentui.UpdateCardIntentionUI;
import com.baza.android.bzw.businesscontroller.find.updateengine.cardcontentui.UpdateCardProjectExperienceUI;
import com.baza.android.bzw.businesscontroller.find.updateengine.cardcontentui.UpdateCardResumeTextUI;
import com.baza.android.bzw.businesscontroller.find.updateengine.cardcontentui.UpdateCardWorkExperienceUI;
import com.baza.android.bzw.businesscontroller.find.updateengine.presenter.UpdateContentCardPresenter;
import com.baza.android.bzw.businesscontroller.find.updateengine.viewinterface.IUpdateContentCardView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.log.LogUtil;
import com.baza.android.bzw.widget.LoadingView;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/11/7.
 * Title：
 * Note：
 */

public class UpdateContentFragment extends BaseFragment implements IUpdateContentCardView {
    LoadingView loadingView;
    LinearLayout linearLayout_container;

    private UpdateCardContentTopUI mUpdateCardContentTopUI;
    private UpdateCardContentSelfEvaluationUI mUpdateCardContentSelfEvaluationUI;
    private UpdateCardIntentionUI mUpdateCardIntentionUI;
    private UpdateCardWorkExperienceUI mUpdateCardWorkExperienceUI;
    private UpdateCardEducationUI mUpdateCardEducationUI;
    private UpdateCardProjectExperienceUI mUpdateCardProjectExperienceUI;
    private UpdateCardResumeTextUI mUpdateCardResumeTextUI;
    private UpdateContentCardPresenter mPresenter;
    private TextView textView_onUpdating;
    private int mFragmentId;

    public int getFragmentId() {
        return mFragmentId;
    }

    public void setFragmentId(int mFragmentId) {
        this.mFragmentId = mFragmentId;
    }

    public void setJustShowEnableContent(boolean justShowEnableContent) {
        if (mPresenter != null)
            mPresenter.setJustShowEnableUpdateContentMode(justShowEnableContent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_update_content_card;
    }

    @Override
    protected String getPageTitle() {
        return "";
    }

    @Override
    protected void initWhenOnCreatedViews(View mRootView) {
        mPresenter = new UpdateContentCardPresenter(this, getArguments());
        loadingView = mRootView.findViewById(R.id.loading_view);
        linearLayout_container = mRootView.findViewById(R.id.ll_container);
        textView_onUpdating = mRootView.findViewById(R.id.tv_on_updating);
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                loadingView.startLoading(null);
                mPresenter.loadResumeUpdateContent();
            }
        });
    }

    public void callLoadData(boolean justShowEnableUpdateContentMode) {
        LogUtil.d("UpdateContentFragment onShowOnScreen mFragmentId = " + mFragmentId);
        if (mPresenter != null) {
            mPresenter.setJustShowEnableUpdateContentMode(justShowEnableUpdateContentMode);
            mPresenter.initialize();
        }
    }

    @Override
    protected void initWhenOnActivityCreated() {

    }

    @Override
    protected void changedUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    @Override
    protected boolean isStatusBarTintEnabledWhenSDKReachKITKAT() {
        return false;
    }

    @Override
    public void callCancelLoadingView(boolean success, int errorCode, String errorMsg) {
        if (!loadingView.isShownVisibility())
            return;
        if (success)
            loadingView.finishLoading();
        else
            loadingView.loadingFailed(errorCode, errorMsg);
    }

    @Override
    public void callSetTopView(ArrayList<Integer> listEnableUpdate) {
        if (mUpdateCardContentTopUI == null) {
            mUpdateCardContentTopUI = new UpdateCardContentTopUI(this, mPresenter);
            linearLayout_container.addView(mUpdateCardContentTopUI.getView());
        }
        mUpdateCardContentTopUI.updateViews(listEnableUpdate);
    }

    @Override
    public void callSetSelfEvaluationView() {
        if (mUpdateCardContentSelfEvaluationUI == null) {
            mUpdateCardContentSelfEvaluationUI = new UpdateCardContentSelfEvaluationUI(this, mPresenter);
            linearLayout_container.addView(mUpdateCardContentSelfEvaluationUI.getView());
        }
        mUpdateCardContentSelfEvaluationUI.updateViews();
    }

    @Override
    public void callSetIntentionsView(IntentionUnion intentionUnion) {
        if (mUpdateCardIntentionUI == null) {
            mUpdateCardIntentionUI = new UpdateCardIntentionUI(this, mPresenter);
            linearLayout_container.addView(mUpdateCardIntentionUI.getView());
        }
        mUpdateCardIntentionUI.updateViews(intentionUnion);
    }

    @Override
    public void callSetProjectExperienceView(List<ProjectUnion> projectUnions) {
        if (mUpdateCardProjectExperienceUI == null) {
            mUpdateCardProjectExperienceUI = new UpdateCardProjectExperienceUI(this, mPresenter);
            linearLayout_container.addView(mUpdateCardProjectExperienceUI.getView());
        }
        mUpdateCardProjectExperienceUI.updateViews(projectUnions);
    }

    @Override
    public void callSetEducationView(List<EducationUnion> educationUnions) {
        if (mUpdateCardEducationUI == null) {
            mUpdateCardEducationUI = new UpdateCardEducationUI(this, mPresenter);
            linearLayout_container.addView(mUpdateCardEducationUI.getView());
        }
        mUpdateCardEducationUI.updateViews(educationUnions);
    }

    @Override
    public void callSetWorkExperienceView(List<WorkExperienceUnion> workExperienceUnions) {
        if (mUpdateCardWorkExperienceUI == null) {
            mUpdateCardWorkExperienceUI = new UpdateCardWorkExperienceUI(this, mPresenter);
            linearLayout_container.addView(mUpdateCardWorkExperienceUI.getView());
        }
        mUpdateCardWorkExperienceUI.updateViews(workExperienceUnions);
    }

    @Override
    public void callSetResumeTextView() {
        if (mUpdateCardResumeTextUI == null) {
            mUpdateCardResumeTextUI = new UpdateCardResumeTextUI(this, mPresenter);
            linearLayout_container.addView(mUpdateCardResumeTextUI.getView());
        }
        mUpdateCardResumeTextUI.updateViews();
    }

    @Override
    public void callSetOnUpdatingView() {
        mRootView.findViewById(R.id.sc).setVisibility(View.GONE);
        textView_onUpdating.setVisibility(View.VISIBLE);
    }

    @Override
    public void callSetSubmitUpdateView(boolean enable) {
        if (mIFragmentEventsListener != null) {
            mIFragmentEventsListener.onFragmentEventsArrival(AdapterEventIdConst.SET_SUBMIT_UPDATE_ENABLE, enable);
        }
    }

    public UpdateResumeWrapperBean callGetSelectedUpdateContent() {
        if (!mPresenter.isAlreadyLoadData())
            return null;
        UpdateResumeWrapperBean updateResumeWrapperBean = new UpdateResumeWrapperBean();
        if (mUpdateCardContentTopUI != null)
            updateResumeWrapperBean.mainInfo = mUpdateCardContentTopUI.getUpdateContentParam();
        if (mUpdateCardEducationUI != null)
            updateResumeWrapperBean.eduList = mUpdateCardEducationUI.getUpdateContentParam();
        if (mUpdateCardWorkExperienceUI != null)
            updateResumeWrapperBean.workList = mUpdateCardWorkExperienceUI.getUpdateContentParam();
        if (mUpdateCardContentSelfEvaluationUI != null)
            updateResumeWrapperBean.selfEvaluation = mUpdateCardContentSelfEvaluationUI.getUpdateContentParam();
        if (mUpdateCardIntentionUI != null)
            updateResumeWrapperBean.intentions = mUpdateCardIntentionUI.getUpdateContentParam();
        if (mUpdateCardProjectExperienceUI != null)
            updateResumeWrapperBean.projectExperienceList = mUpdateCardProjectExperienceUI.getUpdateContentParam();
        return updateResumeWrapperBean;
    }

    public ResumeUpdatedContentResultBean.Data callGetEnableUpdateContentData() {
        return mPresenter.getEnableUpdateContentData();
    }
}
