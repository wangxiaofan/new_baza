package com.baza.android.bzw.bean;

import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.searchfilterbean.SearchFilterInfoBean;
import com.baza.android.bzw.bean.updateengine.SuggestEnableUpdateTipResultBean;

import java.util.List;

/**
 * Created by Vincent.Lei on 2017/11/7.
 * Title：
 * Note：
 */

public class ResumeUpdateTransformParamBean {
    public int position;
    public int totalCount;
    public List<ResumeBean> enableUpdateList;
    public SearchFilterInfoBean searchFilterInfoBean;
    public SuggestEnableUpdateTipResultBean.SuggestEnableUpdateTipBean suggestLabelFilter;
    public SuggestEnableUpdateTipResultBean.SuggestEnableUpdateTipBean suggestEnableUpdateTitleFilter;

    public ResumeUpdateTransformParamBean(int position, int totalCount, List<ResumeBean> enableUpdateList) {
        this.position = position;
        this.totalCount = totalCount;
        this.enableUpdateList = enableUpdateList;
    }

    public ResumeUpdateTransformParamBean setAdvanceSearchFilter(SearchFilterInfoBean searchFilterInfoBean) {
        this.searchFilterInfoBean = searchFilterInfoBean;
        return this;
    }

    public ResumeUpdateTransformParamBean setLabelFilter(SuggestEnableUpdateTipResultBean.SuggestEnableUpdateTipBean suggestLabelFilter) {
        this.suggestLabelFilter = suggestLabelFilter;
        return this;
    }

    public ResumeUpdateTransformParamBean setTitleFilter(SuggestEnableUpdateTipResultBean.SuggestEnableUpdateTipBean suggestEnableUpdateTitleFilter) {
        this.suggestEnableUpdateTitleFilter = suggestEnableUpdateTitleFilter;
        return this;
    }
}
