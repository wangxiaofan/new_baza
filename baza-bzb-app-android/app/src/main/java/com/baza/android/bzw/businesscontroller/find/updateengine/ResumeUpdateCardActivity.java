package com.baza.android.bzw.businesscontroller.find.updateengine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseFragment;
import com.baza.android.bzw.bean.resume.ResumeUpdatedContentResultBean;
import com.baza.android.bzw.bean.updateengine.UpdateResumeWrapperBean;
import com.baza.android.bzw.businesscontroller.browser.RemoteBrowserActivity;
import com.baza.android.bzw.businesscontroller.find.updateengine.adapter.UpdateCardListAdapter;
import com.baza.android.bzw.businesscontroller.find.updateengine.presenter.ResumeUpdateCardPresenter;
import com.baza.android.bzw.businesscontroller.find.updateengine.viewinterface.IResumeUpdateCardView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.RequestCodeConst;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.log.LogUtil;
import com.baza.android.bzw.widget.dialog.UpdateCountLimitDialog;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;

/**
 * Created by Vincent.Lei on 2017/11/7.
 * Title：
 * Note：
 */

public class ResumeUpdateCardActivity extends BaseActivity implements BaseFragment.IFragmentEventsListener, IResumeUpdateCardView, View.OnClickListener, ViewPager.OnPageChangeListener {
    Button button_ignore;
    Button button_update;
    ViewPager viewPager;
    private ResumeUpdateCardPresenter mPresenter;
    private ArrayList<UpdateContentFragment> mFragmentList = new ArrayList<>();
    private UpdateCardListAdapter mUpdateCardListAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_resume_update_card;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_resume_update_card);
    }

    @Override
    protected void initWhenCallOnCreate() {
        mPresenter = new ResumeUpdateCardPresenter(this, getIntent());
        TextView textView = findViewById(R.id.tv_right_click);
        textView.setText(R.string.show_all);
        textView.setOnClickListener(this);

        button_ignore = findViewById(R.id.btn_ignore);
        button_update = findViewById(R.id.btn_update);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(this);
        mUpdateCardListAdapter = new UpdateCardListAdapter(getSupportFragmentManager(), new UpdateCardListAdapter.IDataAndViewProvider() {
            @Override
            public int getCount() {
                return mPresenter.getAllUpdateCount();
            }

            @Override
            public Fragment instantiateItem(int position) {
                LogUtil.d("instantiateItem fragment");
                Bundle bundle = new Bundle();
                bundle.putString("candidateId", mPresenter.getTargetUpdateResumeId(position));
                bundle.putBoolean("justShowEnableContent", mPresenter.isJustShowEnableUpdateContent());
                UpdateContentFragment updateContentFragment = new UpdateContentFragment();
                updateContentFragment.setArguments(bundle);
                updateContentFragment.setFragmentId(position);
                updateContentFragment.setFragmentEventsListener(ResumeUpdateCardActivity.this);
                mFragmentList.add(updateContentFragment);
                return updateContentFragment;
            }

            @Override
            public void destroyItem(int position, Fragment outOfDate) {
                mFragmentList.remove(outOfDate);
                LogUtil.d("destroyItem fragment amount   = " + mFragmentList.size());
            }
        });
        viewPager.setAdapter(mUpdateCardListAdapter);

        viewPager.setCurrentItem(mPresenter.getInitPosition());
        viewPager.post(new Runnable() {
            @Override
            public void run() {
                int currentIndex = viewPager.getCurrentItem();
                callCurrentFragmentAlive(currentIndex);
                mPresenter.onLoadMore(currentIndex);
            }
        });
        mPresenter.initialize();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                finish();
                break;
            case R.id.btn_update:
                mPresenter.prepareToUpdate(viewPager.getCurrentItem());
                break;
            case R.id.btn_ignore:
                callScanNext();
                break;
            case R.id.tv_right_click:
                int currentItem = viewPager.getCurrentItem();
                mPresenter.setJustShowEnableUpdateContent(!mPresenter.isJustShowEnableUpdateContent());
                UpdateContentFragment updateContentFragment = findFragmentByPosition(currentItem);
                if (updateContentFragment != null)
                    updateContentFragment.setJustShowEnableContent(mPresenter.isJustShowEnableUpdateContent());
                break;
        }
    }

    @Override
    public void callScanNext() {
        int currentIndex = viewPager.getCurrentItem();
        if (mPresenter.enableScanNext(currentIndex)) {
            viewPager.setCurrentItem(currentIndex + 1);
            return;
        }
        if (mPresenter.onLoadMore(currentIndex)) {
            callShowToastMessage(null, R.string.on_get_more_enable_update_resume);
            return;
        }
        callShowToastMessage(null, R.string.no_more_enable_update_resume);
        finish();
    }

    @Override
    public void callSetUpUpdateSingleResumeView() {
        button_ignore.setVisibility(View.GONE);
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) button_update.getLayoutParams();
        mlp.leftMargin = mlp.rightMargin = ScreenUtil.dip2px(30);
        button_update.setLayoutParams(mlp);
    }

    @Override
    public UpdateResumeWrapperBean callGetSelectedUpdateContent() {
        UpdateContentFragment updateContentFragment = findFragmentByPosition(viewPager.getCurrentItem());
        if (updateContentFragment == null)
            return null;
        return updateContentFragment.callGetSelectedUpdateContent();
    }

    @Override
    public ResumeUpdatedContentResultBean.Data callGetEnableUpdateContentData() {
        UpdateContentFragment updateContentFragment = findFragmentByPosition(viewPager.getCurrentItem());
        if (updateContentFragment == null)
            return null;
        return updateContentFragment.callGetEnableUpdateContentData();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.push_bottom_out_slowly);
    }

    public static void launch(Activity activity) {
        launch(activity, null, RequestCodeConst.INT_REQUEST_NONE);
    }

    public static void launch(Activity activity, String singleUpdateResumeId, int requestCode) {
        Intent intent = new Intent(activity, ResumeUpdateCardActivity.class);
        if (singleUpdateResumeId != null) {
            intent.putExtra("singleUpdateResumeId", singleUpdateResumeId);
        }
        if (requestCode == RequestCodeConst.INT_REQUEST_NONE)
            activity.startActivity(intent);
        else
            activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.push_bottom_in_slowly, R.anim.alpha_none);
    }

    private void callCurrentFragmentAlive(int position) {
        UpdateContentFragment updateContentFragment = findFragmentByPosition(position);
        if (updateContentFragment != null && !updateContentFragment.isDetached())
            updateContentFragment.callLoadData(mPresenter.isJustShowEnableUpdateContent());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        callCurrentFragmentAlive(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private UpdateContentFragment findFragmentByPosition(int position) {
        for (int i = 0, size = mFragmentList.size(); i < size; i++) {
            if (mFragmentList.get(i).getFragmentId() == position) {
                return mFragmentList.get(i);
            }
        }
        return null;
    }

    @Override
    public void callRefreshList() {
        mUpdateCardListAdapter.notifyDataSetChanged();
    }

    @Override
    public void callReachUpdateLimitDialog() {
        new UpdateCountLimitDialog(this, new UpdateCountLimitDialog.IUpdateCountLimitListener() {
            @Override
            public void onLearnMoreClick() {
                RemoteBrowserActivity.launch(ResumeUpdateCardActivity.this, null, false, URLConst.LINK_H5_GRADE);
            }
        }).show();
    }

    @Override
    public Object onFragmentEventsArrival(int eventId, Object input) {
        if (eventId == AdapterEventIdConst.SET_SUBMIT_UPDATE_ENABLE) {
            boolean enable = (boolean) input;
            button_update.setBackgroundResource((enable ? R.drawable.resume_update_card_update_btn_bg : R.drawable.bg_city_item_normal));
            button_update.setEnabled(enable);
        }
        return null;
    }
}
