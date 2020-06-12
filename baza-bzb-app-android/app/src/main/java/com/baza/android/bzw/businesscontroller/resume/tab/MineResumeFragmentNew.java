package com.baza.android.bzw.businesscontroller.resume.tab;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baza.android.bzw.base.BaseFragment;
import com.baza.android.bzw.bean.resume.TalentListBean;
import com.baza.android.bzw.bean.user.UserInfoBean;
import com.baza.android.bzw.businesscontroller.resume.tab.adapter.TalentListAdapter;
import com.baza.android.bzw.log.logger.TalentLogger;
import com.baza.android.bzw.manager.UserInfoManager;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 人才tab
 */
public class MineResumeFragmentNew extends BaseFragment {

    private RecyclerView rvList;

    private TalentListAdapter listAdapter;

    private List<TalentListBean> listBeans = new ArrayList<>();

    private TalentLogger talentLogger = new TalentLogger();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_candidate_new;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_tab_talent);
    }

    @Override
    protected void initWhenOnCreatedViews(View mRootView) {
        talentLogger.setPageCode(getActivity(), "Talent");
        mRootView.findViewById(R.id.ibtn_left_click).setVisibility(View.GONE);
        ((TextView) mRootView.findViewById(R.id.tv_title)).setText(R.string.tab_talent);
        rvList = mRootView.findViewById(R.id.rv_list);

        UserInfoBean userInfoBean = UserInfoManager.getInstance().getUserInfo();
        if (userInfoBean.isFirmUser == 1) {
            listBeans.add(new TalentListBean(R.string.talent_company_talent, TalentListBean.COMPANY_TALENT, R.drawable.icon_qiyerencaiku));
            listBeans.add(new TalentListBean(R.string.talent_my_talent, TalentListBean.MY_TALENT, R.drawable.icon_woderencaiku));
            listBeans.add(new TalentListBean(R.string.talent_tracking_list, TalentListBean.TRACKING_LIST, R.drawable.icon_rencaigenjin));
            listBeans.add(new TalentListBean(R.string.talent_floating_list, TalentListBean.FLOATING_LIST, R.drawable.icon_liuchengguanli));
            listBeans.add(new TalentListBean(R.string.talent_my_collection, TalentListBean.MY_COLLECTION, R.drawable.icon_wodeshoucang));
        } else {
            listBeans.add(new TalentListBean(R.string.talent_my_talent, TalentListBean.MY_TALENT, R.drawable.icon_woderencaiku));
            listBeans.add(new TalentListBean(R.string.talent_my_collection, TalentListBean.MY_COLLECTION, R.drawable.icon_wodeshoucang));
        }
        listAdapter = new TalentListAdapter(getActivity(), listBeans, talentLogger);
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvList.setAdapter(listAdapter);
    }

    @Override
    protected void initWhenOnActivityCreated() {

    }

    @Override
    protected void changedUIToFitSDKReachKITKAT(int statusBarHeight) {

    }
}
