package com.baza.android.bzw.extra;


import com.baza.android.bzw.bean.resume.ResumeStatus;

/**
 * Created by Vincent.Lei on 2017/3/21.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public interface IFloatingFilterListener {

    void onSortSelected(int sort, String sortName);

    void onStatusSelected(ResumeStatus status);

    void onJobSelected(String job);
}
