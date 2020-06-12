package com.baza.android.bzw.businesscontroller.resume.jobfind.adapter;

import android.content.Context;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.businesscontroller.resume.mine.adapter.MineResumeListAdapter;

import java.util.List;

/**
 * Created by Vincent.Lei on 2018/1/10.
 * Title：
 * Note：
 */

public class JobFinderListAdapter extends MineResumeListAdapter {
    public JobFinderListAdapter(Context context, List<ResumeBean> candidateList, BaseBZWAdapter.IAdapterEventsListener adapterEventsListener) {
        super(context, candidateList, adapterEventsListener);
    }
}
