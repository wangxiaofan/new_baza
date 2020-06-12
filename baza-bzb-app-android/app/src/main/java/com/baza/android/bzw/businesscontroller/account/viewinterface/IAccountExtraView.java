package com.baza.android.bzw.businesscontroller.account.viewinterface;

import com.baza.android.bzw.bean.user.VersionBean;
import com.baza.android.bzw.base.IBaseView;

/**
 * Created by Vincent.Lei on 2017/5/26.
 * Title：
 * Note：
 */

public interface IAccountExtraView extends IBaseView {
    void callUpdateNewVersionView(VersionBean data);

    void callShowNewVersionDialog(VersionBean data);
}
