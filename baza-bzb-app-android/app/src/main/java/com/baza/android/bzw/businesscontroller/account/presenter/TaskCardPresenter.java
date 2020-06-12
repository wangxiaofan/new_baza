package com.baza.android.bzw.businesscontroller.account.presenter;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.taskcard.TaskBean;
import com.baza.android.bzw.businesscontroller.account.viewinterface.ITaskCardView;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.dao.AccountDao;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Vincent.Lei on 2018/11/28.
 * Title：
 * Note：
 */
public class TaskCardPresenter extends BasePresenter {
    private ITaskCardView mTaskCardView;
    private List<TaskBean> mDataList = new ArrayList<>();


    public List<TaskBean> getDataList() {
        return mDataList;
    }


    public TaskCardPresenter(ITaskCardView taskCardView) {
        this.mTaskCardView = taskCardView;
    }

    @Override
    public void initialize() {
    }

    @Override
    public void onResume() {
        loadMerculetTasks();
    }

    public void loadMerculetTasks() {
        AccountDao.loadMerculetTasks(new IDefaultRequestReplyListener<TaskBean[]>() {
            @Override
            public void onRequestReply(boolean success, TaskBean[] taskBeans, int errorCode, String errorMsg) {
                mTaskCardView.callCancelLoadingView(success, errorCode, errorMsg);
                mDataList.clear();
                if (success) {
                    Collections.addAll(mDataList, taskBeans);
                }
                mTaskCardView.callRefreshListItems(CommonConst.LIST_POSITION_NONE);
            }
        });
    }
}
