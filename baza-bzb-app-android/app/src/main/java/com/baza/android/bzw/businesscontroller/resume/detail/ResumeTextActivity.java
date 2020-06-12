package com.baza.android.bzw.businesscontroller.resume.detail;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.presenter.ResumeSourceFilePresenter;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeSourceFileView;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/6/28.
 * Title：
 * Note：
 */

public class ResumeTextActivity extends BaseActivity implements IResumeSourceFileView, View.OnClickListener {
    private ResumeSourceFilePresenter mPresenter;
    private TextView textView_rightClick;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_candidate_text;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_candidate_text);
    }

    @Override
    protected void initWhenCallOnCreate() {
        textView_rightClick = findViewById(R.id.tv_right_click);
        textView_rightClick.setVisibility(View.GONE);
        mPresenter = new ResumeSourceFilePresenter(this, getIntent());
        mPresenter.initialize();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_right_click:
                mPresenter.openWithOtherApplication();
                break;
            default:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityDeadForApp() {
        if (mPresenter != null)
            mPresenter.onDestroy();
    }

    public static void launch(Activity activity, String title, String fileName, String fileUrl) {
        Intent intent = new Intent(activity, ResumeTextActivity.class);
        if (title != null)
            intent.putExtra("title", title);
        if (fileName != null)
            intent.putExtra("fileName", fileName);
        if (fileUrl != null)
            intent.putExtra("fileUrl", fileUrl);
        activity.startActivity(intent);
    }

    @Override
    public void callSetTitle(String title) {
        TextView textView = findViewById(R.id.tv_title);
        textView.setText(title);
    }

    @Override
    public void callSetTextInfo(String text) {
        TextView textView = findViewById(R.id.tv_text);
        textView.setText(text);
    }

    @Override
    public void callOpenFileMode() {
        textView_rightClick.setText(R.string.open_source_file);
        textView_rightClick.setVisibility(View.VISIBLE);
    }
}
