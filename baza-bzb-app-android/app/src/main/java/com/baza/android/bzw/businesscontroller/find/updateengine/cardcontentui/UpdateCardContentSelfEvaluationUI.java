package com.baza.android.bzw.businesscontroller.find.updateengine.cardcontentui;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.baza.android.bzw.bean.resume.ResumeUpdatedContentResultBean;
import com.baza.android.bzw.businesscontroller.find.updateengine.presenter.UpdateContentCardPresenter;
import com.baza.android.bzw.businesscontroller.find.updateengine.viewinterface.IUpdateContentCardView;
import com.bznet.android.rcbox.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/8/25.
 * Title：
 * Note：
 */

public class UpdateCardContentSelfEvaluationUI {
    private IUpdateContentCardView mUpdateContentCardView;
    private UpdateContentCardPresenter mPresenter;
    private View mRootView;
    @BindView(R.id.tv_self_evaluation_current)
    TextView textView_current;
    @BindView(R.id.tv_self_evaluation_updated)
    TextView textView_AddOrUpdate;
    @BindView(R.id.tv_add_or_update)
    TextView textView_enableUpdateFlag;
    @BindView(R.id.cb_check_all)
    CheckBox checkBox;

    public UpdateCardContentSelfEvaluationUI(IUpdateContentCardView updateContentCardView, UpdateContentCardPresenter presenter) {
        this.mUpdateContentCardView = updateContentCardView;
        this.mPresenter = presenter;
        init();
    }

    public View getView() {
        return mRootView;
    }

    public void init() {
        mRootView = mUpdateContentCardView.callGetBindActivity().getLayoutInflater().inflate(R.layout.layout_candidate_update_content_self_evaluation_ui, null);
        ButterKnife.bind(this, mRootView);
        checkBox.setChecked(true);
    }

    public void updateViews() {
        ResumeUpdatedContentResultBean.Data data = mPresenter.getEnableUpdateContentData();
        boolean enableUpdate = (!TextUtils.isEmpty(data.target.selfEvaluation) && !data.target.selfEvaluation.equals(data.current.selfEvaluation));
        if (mPresenter.isJustShowEnableUpdateContentMode() && !enableUpdate) {
            mRootView.setVisibility(View.GONE);
            return;
        }
        textView_current.setText((!TextUtils.isEmpty(data.current.selfEvaluation) ? data.current.selfEvaluation : mUpdateContentCardView.callGetResources().getString(R.string.message_unknown)));
        if (enableUpdate) {
            textView_AddOrUpdate.setText(data.target.selfEvaluation);
            textView_AddOrUpdate.setVisibility(View.VISIBLE);
            textView_enableUpdateFlag.setVisibility(View.VISIBLE);
            checkBox.setVisibility(View.VISIBLE);
        } else {
            textView_AddOrUpdate.setVisibility(View.GONE);
            textView_enableUpdateFlag.setVisibility(View.GONE);
            checkBox.setVisibility(View.GONE);
        }
        mRootView.setVisibility(View.VISIBLE);
    }

    public String getUpdateContentParam() {
        if (!checkBox.isChecked())
            return null;
        ResumeUpdatedContentResultBean.Data data = mPresenter.getEnableUpdateContentData();
        if (data == null)
            return null;
        return data.target.selfEvaluation;
    }

}
