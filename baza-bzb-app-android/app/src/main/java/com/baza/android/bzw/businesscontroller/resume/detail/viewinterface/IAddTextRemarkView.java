package com.baza.android.bzw.businesscontroller.resume.detail.viewinterface;

import com.baza.android.bzw.base.IBaseView;
import com.baza.android.bzw.bean.resumeelement.RemarkBean;

/**
 * Created by Vincent.Lei on 2019/2/11.
 * Title：
 * Note：
 */
public interface IAddTextRemarkView extends IBaseView {
    void callShowMoreRemarkSelectionView();

    String callGetTextRemark();

    String callGetCompany();

    String callGetJob();

    String callGetJobHoping();

    String callGetHirerDes();

    String callGetExpectSalary();

    String callGetContent();

    int callGetFlag();

    void callUpdateRemark(RemarkBean remarkBean);
}
