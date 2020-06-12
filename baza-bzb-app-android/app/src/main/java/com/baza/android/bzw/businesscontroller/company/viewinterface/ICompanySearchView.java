package com.baza.android.bzw.businesscontroller.company.viewinterface;

import com.baza.android.bzw.base.IBaseView;
import com.baza.android.bzw.bean.searchfilterbean.CompanySearchFilterInfoBean;

import java.io.Serializable;

public interface ICompanySearchView extends IBaseView {
    class Param implements Serializable {
        public int searchMode;
        public String formComponentName;
        public String smartGroupId;
        public CompanySearchFilterInfoBean searchFilterInfoPrevious;

        public Param searchMode(int searchMode) {
            this.searchMode = searchMode;
            return this;
        }

        public Param formComponentName(String formComponentName) {
            this.formComponentName = formComponentName;
            return this;
        }

        public Param smartGroupId(String smartGroupId) {
            this.smartGroupId = smartGroupId;
            return this;
        }

        public Param searchFilterInfoPrevious(CompanySearchFilterInfoBean searchFilterInfoPrevious) {
            this.searchFilterInfoPrevious = searchFilterInfoPrevious;
            return this;
        }
    }


    void callUpdateSearchResultHint(int resultCount, int extraResultCount, boolean showTopView, boolean isJobFinderEntranceEnable);

    void callSearch();

    String callGetKeyWord();

    void callUpdateKeywordView(String keyWord);

    void callUpdateKeywordHint(int keyWordHint);

    void callUpdateSearchFilterView();

    void callSetLabelLibrary();

    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callShowLoadingView(String msg);

    void callRefreshListItems(int targetPosition, int selection);

    void callUpdateLoadAllDataView(boolean hasLoadAll);

    void callShowSpecialToastMsg(int type, String msg, int msgId);

    void callIsJobHunting(boolean isJobHunting);

}
