package com.baza.android.bzw.businesscontroller.resume.detail.presenter;

import android.content.Intent;
import android.text.TextUtils;

import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeSourceFileView;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.log.LogUtil;
import com.slib.http.FileLoadTool;
import com.slib.http.FileOpenHelper;
import com.slib.http.IFileLoadObserve;
import com.bznet.android.rcbox.R;

import java.io.File;

/**
 * Created by Vincent.Lei on 2017/8/14.
 * Title：
 * Note：
 */

public class ResumeSourceFilePresenter extends BasePresenter implements IFileLoadObserve {
    private IResumeSourceFileView mResumeSourceFileView;
    private String mTitle;
    private String mFileName;
    private String mFileUrl;

    public ResumeSourceFilePresenter(IResumeSourceFileView candidateSourceFileView, Intent intent) {
        this.mResumeSourceFileView = candidateSourceFileView;
        this.mTitle = intent.getStringExtra("title");
        this.mFileName = intent.getStringExtra("fileName");
        this.mFileUrl = intent.getStringExtra("fileUrl");
    }

    @Override
    public void initialize() {
        mResumeSourceFileView.callSetTitle(mTitle);
        mResumeSourceFileView.callSetTextInfo((String) BZWApplication.getApplication().getCachedTransformData(CommonConst.STR_TRANSFORM_RESUME_TEXT));
        if (!TextUtils.isEmpty(mFileName) && !TextUtils.isEmpty(mFileUrl))
            mResumeSourceFileView.callOpenFileMode();
        FileLoadTool.getInstance().registerFileLoadObserve(this);
    }

    @Override
    public void onDestroy() {
        FileLoadTool.getInstance().unRegisterFileLoadObserve(this);
    }

    public void openWithOtherApplication() {
        String extensionName = null;
        if (mFileName != null) {
            int index = mFileName.lastIndexOf(".");
            if (index >= 0) {
                extensionName = mFileName.substring(index, mFileName.length());
                LogUtil.d(extensionName);
            }
        }
        FileLoadTool.getInstance().downLoadFile(mFileUrl, extensionName);
    }

    @Override
    public void onFileStartLoading(String fileUrl, String tagForSameUrl) {
        if (mFileUrl.equals(fileUrl))
            mResumeSourceFileView.callShowToastMessage(null, R.string.on_load_candidate_file);
    }

    @Override
    public void onFileLoadProgressChanged(String fileUrl, String tagForSameUrl, int progress) {

    }

    @Override
    public void onFileLoadSuccess(String fileUrl, String tagForSameUrl, File file) {
        if (mFileUrl.equals(fileUrl))
            FileOpenHelper.openFile(mResumeSourceFileView.callGetApplication(), file);
    }

    @Override
    public void onFileLoadFailed(String fileUrl, String tagForSameUrl, int errorCode, String errorMsg) {
        if (mFileUrl.equals(fileUrl))
            mResumeSourceFileView.callShowToastMessage(null, R.string.load_candidate_file_failed);
    }
}
