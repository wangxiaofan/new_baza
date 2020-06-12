package com.baza.android.bzw.businesscontroller.floating.viewinterface;

import com.baza.android.bzw.base.IBaseView;

import java.util.List;

public interface IFloatingListView extends IBaseView {

    void callShowLoadingView(String msg);

    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callRefreshListViews(int targetPosition);

    void callShowEmpty();

    void callRefreshData(int sort);

    void callRefreshDataByStatus(int status);

    void callRefreshDataByJob(String job);

    void callRefreshDataByName(String name);

    List<String> getJobList();

    String getjob();

    void callUpdateListStatus(List<String> ids);

    void callUpdatePage();

    void callShowDialog();
}
