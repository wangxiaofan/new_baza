package com.baza.android.bzw.businesscontroller.login.viewinterface;

import com.baza.android.bzw.bean.user.UserInfoBean;
import com.baza.android.bzw.base.IBaseView;

import java.util.List;

/**
 * Created by Vincent.Lei on 2017/5/17.
 * Title：
 * Note：
 */

public interface IFillUserInfoView extends IBaseView {
    String callGetUserName();

    String callGetEmail();

    String callGetCompany();

    String callGetJob();

    void callSetCompanyAutoCompleteLib(List<String> companyList);

    void callSetOldData(UserInfoBean userInfoBean);
}
