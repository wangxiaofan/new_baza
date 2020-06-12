package com.baza.android.bzw.businesscontroller.resume.detail;

import android.content.res.Resources;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baza.android.bzw.bean.resume.ResumeAttachment;
import com.baza.android.bzw.bean.resume.ResumeDetailBean;
import com.baza.android.bzw.businesscontroller.resume.detail.presenter.ResumeDetailPresenter;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.dao.ResumeDao;
import com.bznet.android.rcbox.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/5/24.
 * Title：
 * Note：
 */

public class FootViewExtraInfoUI implements View.OnClickListener {
    @BindView(R.id.tv_resume_source_title)
    TextView textView_resumeSourceTitle;
    @BindView(R.id.ll_resume_source_list_container)
    LinearLayout linearLayout_resumeSourceContainer;
    private View mFootView;
    private IResumeDetailView mResumeDetailView;
    private ResumeDetailPresenter mPresenter;
    private Resources mResources;

    public FootViewExtraInfoUI(IResumeDetailView mResumeDetailView, ResumeDetailPresenter mPresenter) {
        this.mResumeDetailView = mResumeDetailView;
        this.mPresenter = mPresenter;
        init();
    }

    private void init() {
        mResources = mResumeDetailView.callGetResources();
        mFootView = mResumeDetailView.callGetBindActivity().getLayoutInflater().inflate(R.layout.foot_view_candidate_extra_info, null);
        ButterKnife.bind(this, mFootView);
        setData();
    }

    public View getFootView() {
        return mFootView;
    }

    public void setData() {
        ResumeDetailBean resumeDetailBean = mPresenter.getCurrentResumeData();
        if (resumeDetailBean == null)
            return;
        setResumeHistoryData(resumeDetailBean);
    }


    private void setResumeHistoryData(ResumeDetailBean data) {
        if (data.attachments == null || data.attachments.isEmpty()) {
            textView_resumeSourceTitle.setVisibility(View.GONE);
            linearLayout_resumeSourceContainer.setVisibility(View.GONE);
            return;
        }
        int needShowCount = data.attachments.size();
        int currentCount = linearLayout_resumeSourceContainer.getChildCount();
        boolean hasCachedView;
        View view_attachment_item;
        View view_departLine;
        TextView textView;
        ResumeAttachment attachment;
        for (int i = 0; i < needShowCount; i++) {
            attachment = data.attachments.get(i);
            hasCachedView = i < currentCount;
            view_attachment_item = !hasCachedView ? mResumeDetailView.callGetBindActivity().getLayoutInflater().inflate(R.layout.layout_candidate_update_history_item, null) : linearLayout_resumeSourceContainer.getChildAt(i);
            textView = view_attachment_item.findViewById(R.id.tv_online_resume_name);
            textView.setText(attachment.fileName);

            textView = view_attachment_item.findViewById(R.id.tv_resume_create_time);
            textView.setText(mResources.getString(R.string.remark_create_time_extra, attachment.created));

            textView = view_attachment_item.findViewById(R.id.tv_scan_online);
            textView.setOnClickListener(this);
            textView.setTag(attachment);
            textView.setVisibility(ResumeDao.isAttachmentEnableScanOnLine(attachment.fileName) ? View.VISIBLE : View.GONE);

//            textView = view_attachment_item.findViewById(R.id.tv_from);
//            String source = ResumeDao.getSourceForShow(attachment.sourceChannel);
//            textView.setVisibility(source != null ? View.VISIBLE : View.GONE);
//            textView.setText(source);

            view_attachment_item.setVisibility(View.VISIBLE);
            view_departLine = view_attachment_item.findViewById(R.id.view_depart_line);
            view_departLine.setVisibility(i == needShowCount - 1 ? View.GONE : View.VISIBLE);
            if (!hasCachedView)
                linearLayout_resumeSourceContainer.addView(view_attachment_item);
        }
        if (currentCount > needShowCount) {
            for (int i = needShowCount; i < currentCount; i++) {
                linearLayout_resumeSourceContainer.getChildAt(i).setVisibility(View.GONE);
            }
        }
        textView_resumeSourceTitle.setVisibility(View.VISIBLE);
        linearLayout_resumeSourceContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_scan_online:
                mPresenter.scanResumeAttachmentOnLine((ResumeAttachment) v.getTag());
                break;
        }
    }
}
