package com.baza.android.bzw.businesscontroller.find.updateengine.cardcontentui;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.baza.android.bzw.bean.resume.ResumeUpdatedContentResultBean;
import com.baza.android.bzw.businesscontroller.find.updateengine.presenter.UpdateContentCardPresenter;
import com.baza.android.bzw.businesscontroller.find.updateengine.viewinterface.IUpdateContentCardView;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/11/30.
 * Title：
 * Note：
 */

public class UpdateCardResumeTextUI implements View.OnClickListener {
    private IUpdateContentCardView mUpdateContentCardView;
    private UpdateContentCardPresenter mPresenter;
    private View mRootView;
    private TextView textView_resumeName;

    public UpdateCardResumeTextUI(IUpdateContentCardView mUpdateContentCardView, UpdateContentCardPresenter mPresenter) {
        this.mUpdateContentCardView = mUpdateContentCardView;
        this.mPresenter = mPresenter;
        init();
    }

    public View getView() {
        return mRootView;
    }

    private void init() {
        mRootView = mUpdateContentCardView.callGetBindActivity().getLayoutInflater().inflate(R.layout.layout_resume_update_card_text_ui, null);
        textView_resumeName = mRootView.findViewById(R.id.tv_online_resume_name);
        textView_resumeName.setOnClickListener(this);
    }

    public void updateViews() {
        ResumeUpdatedContentResultBean.Data data = mPresenter.getEnableUpdateContentData();
        if (data == null || data.current == null)
            return;
        if (mPresenter.isJustShowEnableUpdateContentMode() || TextUtils.isEmpty(data.content)) {
            mRootView.setVisibility(View.GONE);
            return;
        }
        textView_resumeName.setText(data.current.realName);
        mRootView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        mPresenter.watchResumeText();
    }
}
