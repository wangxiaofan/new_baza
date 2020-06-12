package com.baza.android.bzw.businesscontroller.resume.base.presenter;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.resume.ResumeDetailBean;
import com.baza.android.bzw.dao.ResumeCompareDao;

/**
 * Created by Vincent.Lei on 2018/11/9.
 * Title：
 * Note：
 */
public abstract class ResumeBasePresenter extends BasePresenter {
    public abstract ResumeDetailBean getCurrentResumeData();

    public abstract ResumeCompareDao getResumeCompareDao();
}
