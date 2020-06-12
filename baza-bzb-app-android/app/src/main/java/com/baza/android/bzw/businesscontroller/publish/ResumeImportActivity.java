package com.baza.android.bzw.businesscontroller.publish;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.businesscontroller.publish.presenter.ResumeImportPresenter;
import com.baza.android.bzw.businesscontroller.publish.viewinterface.IResumeImportView;
import com.slib.progress.IndeterminateProgressDrawable;
import com.bznet.android.rcbox.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/6/26.
 * Title：
 * Note：
 */

public class ResumeImportActivity extends BaseActivity implements IResumeImportView, View.OnClickListener {
    @BindView(R.id.tv_resume_attachment_name)
    TextView textView_attachmentName;
    @BindView(R.id.tv_current_account)
    TextView textView_account;
    @BindView(R.id.btn_import)
    Button button_import;
    @BindView(R.id.mpb)
    ProgressBar progressBar;
    @BindView(R.id.fl_onImporting)
    View view_onImporting;

    private ResumeImportPresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_resume_import;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_resume_import);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mPresenter != null)
            mPresenter.onNewIntent(intent);
    }

    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        TextView textView_title = findViewById(R.id.tv_title);
        textView_title.setText(R.string.import_resume);

        IndeterminateProgressDrawable indeterminateProgressDrawable = new IndeterminateProgressDrawable(mApplication);
        indeterminateProgressDrawable.setTint(mResources.getColor(R.color.text_color_grey_9E9E9E));
        progressBar.setIndeterminateDrawable(indeterminateProgressDrawable);

        mPresenter = new ResumeImportPresenter(this, getIntent());
        mPresenter.initialize();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }


    public static void launch(Activity activity, String path) {
        Intent intent = new Intent(activity, ResumeImportActivity.class);
        if (path != null)
            intent.putExtra("path", path);
        activity.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                finish();
                break;
            case R.id.btn_import:
                mPresenter.uploadResumeAttachment();
                break;
        }
    }

    @Override
    public void callUpdateImportHintView(String fileName, String accountName) {
        textView_attachmentName.setText(fileName);
        textView_account.setText(accountName);
    }

    @Override
    public void callSetOnImportView(boolean isOnImport) {
        button_import.setVisibility(isOnImport ? View.GONE : View.VISIBLE);
        view_onImporting.setVisibility(isOnImport ? View.VISIBLE : View.GONE);
    }

    @Override
    public void callFinish() {
        finish();
    }
}
