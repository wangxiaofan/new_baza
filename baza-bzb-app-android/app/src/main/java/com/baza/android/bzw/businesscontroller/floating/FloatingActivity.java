package com.baza.android.bzw.businesscontroller.floating;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.bean.resume.FloatingListBean;
import com.baza.android.bzw.bean.resume.FloatingListDetailBean;
import com.baza.android.bzw.businesscontroller.floating.fragment.FloatingListFragment;
import com.baza.android.bzw.businesscontroller.floating.presenter.FloatingPresenter;
import com.baza.android.bzw.businesscontroller.floating.viewinterface.IFloatingView;
import com.bznet.android.rcbox.R;
import com.flyco.tablayout.SlidingTabLayout;
import com.slib.utils.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FloatingActivity extends BaseActivity implements IFloatingView, FloatingListFragment.SelectListener {

    @BindView(R.id.stl_main)
    SlidingTabLayout stlMain;
    @BindView(R.id.tv_handle)
    TextView tvHandle;
    @BindView(R.id.view_title_bar)
    FrameLayout viewTitleBar;
    @BindView(R.id.vp_content)
    ViewPager vpContent;
    @BindView(R.id.ibtn_left_click)
    ImageButton ibtnLeftClick;

    private ArrayList<Fragment> mFragments;
    private FloatingListFragment fragmentRecommend;
    private FloatingListFragment fragmentReceive;
    private String startTime, endTime;
    private int currectStatus = -1;
    private List<FloatingListBean> selectList = new ArrayList<>();
    FloatingPresenter floatingPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_floating;
    }

    @Override
    protected String getPageTitle() {
        return "floating";
    }

    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        floatingPresenter = new FloatingPresenter(this);
        floatingPresenter.getFloatingLogger().setPageCode(FloatingActivity.this, "FloatingList");
        startTime = DateUtil.dateToString(DateUtil.getDateBefore(new Date(), 7), DateUtil.SDF_RECOMMEND_TWO) + " 00:00:00";
        endTime = DateUtil.dateToString(new Date(), DateUtil.SDF_RECOMMEND_TWO) + " 23:59:59";
        mFragments = new ArrayList<>();
        fragmentRecommend = FloatingListFragment.getInstance(startTime, endTime, "1", false);
        if (getIntent().getStringExtra("FROM") != null && getIntent().getStringExtra("FROM").equals("1000")) {
            fragmentReceive = FloatingListFragment.getInstance(startTime, endTime, "2", true);
        } else {
            fragmentReceive = FloatingListFragment.getInstance(startTime, endTime, "2", false);
        }
        mFragments.add(fragmentRecommend);
        mFragments.add(fragmentReceive);
        stlMain.setViewPager(vpContent, new String[]{"我推荐的", "我收到的"}, this, mFragments);
        if (getIntent().getStringExtra("FROM") != null && getIntent().getStringExtra("FROM").equals("1000")) {
            //（筛选出 【最近一个月】 且 【未处理】状态的简历）
            stlMain.setCurrentTab(1);
        }

        vpContent.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    floatingPresenter.getFloatingLogger().ClickMyRecommend(FloatingActivity.this);
                } else {
                    floatingPresenter.getFloatingLogger().ClickMyReceive(FloatingActivity.this);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                fragmentReceive.closeFilterPane();
                fragmentRecommend.closeFilterPane();
            }
        });
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, FloatingActivity.class);
        activity.startActivity(intent);
    }


    @Override
    public void callShowLoadingView() {

    }

    @Override
    public void callCancelLoadingView(boolean success, int errorCode, String errorMsg) {

    }

    @Override
    public void callUpdateContent(FloatingListDetailBean beans) {

    }

    @Override
    public void callUpdatePage() {

    }

    @Override
    public void callShowDialog() {

    }


    @OnClick({R.id.ibtn_left_click, R.id.tv_handle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ibtn_left_click:
                finish();
                break;
            case R.id.tv_handle:
                if (fragmentReceive.isEdit) {
                    tvHandle.setText("批量处理");
                    fragmentReceive.setEdit(false);
                } else {
                    tvHandle.setText("退出批量");
                    fragmentReceive.setEdit(true);
                    floatingPresenter.getFloatingLogger().ClickBatchHandle(FloatingActivity.this);
                }
                break;
        }
    }

    @Override
    public void setModify(boolean modify) {
        if (modify) {
            tvHandle.setVisibility(View.VISIBLE);
            tvHandle.setText("批量处理");
        } else {
            tvHandle.setVisibility(View.GONE);
            fragmentReceive.setEdit(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (fragmentReceive.isFilterPaneShow()) {
            fragmentReceive.closeFilterPane();
            return;
        }
        if (fragmentRecommend.isFilterPaneShow()) {
            fragmentRecommend.closeFilterPane();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
