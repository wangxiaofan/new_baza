package com.baza.android.bzw.businesscontroller.account;

import android.app.Activity;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseFragment;
import com.baza.android.bzw.bean.exchange.BenefitResultBean;
import com.baza.android.bzw.bean.user.ExtraCountBean;
import com.baza.android.bzw.bean.user.GrowBean;
import com.baza.android.bzw.bean.user.UserInfoBean;
import com.baza.android.bzw.businesscontroller.account.presenter.AccountPresenter;
import com.baza.android.bzw.businesscontroller.account.viewinterface.IAccountView;
import com.baza.android.bzw.businesscontroller.browser.RemoteBrowserActivity;
import com.baza.android.bzw.businesscontroller.find.updateengine.ResumeUpdatedRecordsActivity;
import com.baza.android.bzw.businesscontroller.publish.util.QRCodeUtil;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.RequestCodeConst;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.manager.FriendlyShowInfoManager;
import com.baza.android.bzw.manager.UserInfoManager;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.slib.utils.LoadImageUtil;
import com.slib.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/5/18.
 * Title：
 * Note：
 */

public class TabAccountFragment extends BaseFragment implements IAccountView, View.OnClickListener {
    @BindView(R.id.civ_avatar)
    ImageView imageView_avatar;
    @BindView(R.id.tv_avatar)
    TextView textView_avatar;
    @BindView(R.id.tv_user_name)
    TextView textView_name;
    @BindView(R.id.tv_grade_info)
    TextView textView_gradeInfo;
    @BindView(R.id.tv_identify)
    TextView textView_identify;
    //    @BindView(R.id.tv_talent_type)
//    TextView textView_talentType;
//    @BindView(R.id.tv_rank_info)
//    TextView textView_rankInfo;
    @BindView(R.id.tv_resume_analysis)
    TextView textView_resumeAnalysis;
    @BindView(R.id.tv_grade_value)
    TextView textView_gradeItem;
    @BindView(R.id.tv_right_center_title)
    TextView textView_rightCenterTitle;
    @BindView(R.id.pull_to_refresh_scrollview)
    PullToRefreshScrollView pullToRefreshScrollView;
    @BindView(R.id.view_indicator)
    LinearLayout linearLayout_indicator;
    @BindView(R.id.iv_scan)
    ImageView ivScan;

    TextView textView_resumeCount;
    TextView textView_updateCount;
    //    TextView textView_receiveCount;
    //    TextView textView_publishCount;
//    TextView textView_requestCount;
//    TextView textView_shareCount;
    private AccountPresenter mPresenter;
    private int colorNeedIdentify;
    private int colorIdentifyOk;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_account;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_tab_account);
    }

    @Override
    protected void initWhenOnCreatedViews(View mRootView) {
        colorNeedIdentify = mResources.getColor(R.color.text_color_orange_FF7700);
        colorIdentifyOk = mResources.getColor(R.color.main_them_color);
        ButterKnife.bind(this, mRootView);
        pullToRefreshScrollView.setFootReboundInsteadLoad(true);
        pullToRefreshScrollView.setHeadReboundInsteadRefresh(true);
        mRootView.findViewById(R.id.tv_identify).setOnClickListener(this);
        mRootView.findViewById(R.id.view_item_analysis).setOnClickListener(this);
//        mRootView.findViewById(R.id.view_item_talent_type_interested).setOnClickListener(this);
//        mRootView.findViewById(R.id.view_item_rank).setOnClickListener(this);
        mRootView.findViewById(R.id.view_item_grade).setOnClickListener(this);
        mRootView.findViewById(R.id.view_item_feedback).setOnClickListener(this);
        mRootView.findViewById(R.id.view_item_right_center).setOnClickListener(this);
        mRootView.findViewById(R.id.view_item_set).setOnClickListener(this);
        mRootView.findViewById(R.id.view_head_view).setOnClickListener(this);
        mRootView.findViewById(R.id.tv_task_card).setOnClickListener(this);
//        mRootView.findViewById(R.id.view_item_invited).setOnClickListener(this);
        textView_name.setOnClickListener(this);
        textView_gradeInfo.setOnClickListener(this);
        ivScan.setOnClickListener(this);

        addCountInfoView();
        callUpdateGrowInfo();
        callUpdateBenefitView(null);
        mPresenter = new AccountPresenter(this);
    }

    @Override
    protected boolean isUseDefaultStatusBarBackground() {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    protected void onFragmentDeadForApp() {
        super.onFragmentDeadForApp();
        mPresenter.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        mPresenter.onHiddenChanged(hidden);
    }

    @Override
    protected void initWhenOnActivityCreated() {
        mPresenter.initialize();
    }

    @Override
    protected void changedUIToFitSDKReachKITKAT(int statusBarHeight) {
        View view = mRootView.findViewById(R.id.cl);
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        lp.topMargin += statusBarHeight;
        view.setLayoutParams(lp);
    }

    @Override
    public void callUpdateMainInfo() {
        UserInfoBean userInfoBean = UserInfoManager.getInstance().getUserInfo();
        String name = (TextUtils.isEmpty(userInfoBean.nickName) ? userInfoBean.trueName : userInfoBean.nickName);
        textView_name.setText(name);
        if (TextUtils.isEmpty(userInfoBean.avatar)) {
            imageView_avatar.setVisibility(View.GONE);
            textView_avatar.setText((TextUtils.isEmpty(name) ? CommonConst.STR_DEFAULT_USER_NAME_SX : name.substring(0, 1)));
            textView_avatar.setVisibility(View.VISIBLE);
        } else {
            textView_avatar.setVisibility(View.INVISIBLE);
            LoadImageUtil.loadImage(userInfoBean.avatar, R.drawable.avatar_def, imageView_avatar);
            imageView_avatar.setVisibility(View.VISIBLE);
        }
        textView_identify.setText(UserInfoManager.getInstance().isIdentifyStatusOk() ? R.string.user_verify_status_ok : (UserInfoManager.getInstance().getUserInfo().channelVerifyStatus == CommonConst.VerifyStatus.VERIFY_ING ? R.string.verify_status_on_title : R.string.account_identify_now));
        textView_identify.setTextColor(UserInfoManager.getInstance().isIdentifyStatusOk() ? colorIdentifyOk : colorNeedIdentify);
        textView_identify.setBackgroundResource(UserInfoManager.getInstance().isIdentifyStatusOk() ? R.drawable.account_btn_identify_ok_bg : R.drawable.account_btn_identify_bg);
    }

    private void wrapperCountText(SpannableString spannableString) {
        String sourceText = spannableString.toString();
        if (TextUtils.isEmpty(sourceText))
            return;
        int index = sourceText.indexOf(mResources.getString(R.string.wan_zh));
        if (index > 0)
            spannableString.setSpan(new AbsoluteSizeSpan(ScreenUtil.px2dip(mResources.getDimension(R.dimen.text_size_10)), true), index, index + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void callUpdateGrowInfo() {
        GrowBean growInfo = UserInfoManager.getInstance().getGrowInfo();
        SpannableString spannableString = new SpannableString(mResources.getString(R.string.grade_value, String.valueOf(growInfo == null ? 0 : growInfo.presentGrade)));
        spannableString.setSpan(new ForegroundColorSpan(mResources.getColor(R.color.text_color_black_4E5968)), 2, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_gradeInfo.setText(spannableString);
        textView_gradeItem.setText("LV." + (growInfo == null ? 0 : growInfo.presentGrade));
    }

    @Override
    public void callUpdateBenefitView(BenefitResultBean.Data data) {
        SpannableString spannableString = new SpannableString(mResources.getString(R.string.account_item_text_rights_center_value, String.valueOf(data == null ? 0 : (int) data.quantity)));
        spannableString.setSpan(new ForegroundColorSpan(mResources.getColor(R.color.text_color_grey_94A1A5)), 5, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(ScreenUtil.px2dip(mResources.getDimension(R.dimen.text_size_10)), true), 5, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_rightCenterTitle.setText(spannableString);
    }

    @Override
    public void callUpdateInterestedTalentView(String typeName) {
//        textView_talentType.setText(typeName);
    }

    @Override
    public void callUpdateCountViews(ExtraCountBean extraCountBean) {
        textView_resumeAnalysis.setText(FriendlyShowInfoManager.getInstance().getFriendResumeCountValue(extraCountBean.candidateCount, false, true));
        SpannableString spannableString = new SpannableString(FriendlyShowInfoManager.getInstance().getFriendResumeCountValue(extraCountBean.candidateCount, true, true));
        wrapperCountText(spannableString);
        textView_resumeCount.setText(spannableString);

        spannableString = new SpannableString(FriendlyShowInfoManager.getInstance().getFriendResumeCountValue(extraCountBean.updatedCount, true, true));
        wrapperCountText(spannableString);
        textView_updateCount.setText(spannableString);

//        spannableString = new SpannableString(FriendlyShowInfoManager.getInstance().getFriendResumeCountValue(extraCountBean.receivedCount, true, true));
//        wrapperCountText(spannableString);
//        textView_receiveCount.setText(spannableString);
    }

    private void addCountInfoView() {
        ViewPager viewPager = mRootView.findViewById(R.id.view_pager);
        final List<View> pages = new ArrayList<>(2);
        LinearLayout linearLayout = null;
        int pageSize = 4;
        int itemWidth = ScreenUtil.screenWidth / pageSize;
        int[] itemIds = new int[]{R.id.account_count_view_resume, R.id.account_count_view_update};
        String[] itemTitles = mResources.getStringArray(R.array.account_count_view_titles_normal);
        ViewGroup viewGroup;
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        for (int i = 0; i < itemIds.length; i++) {
            viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.account_count_info_item, null);
            viewGroup.setOnClickListener(this);
            viewGroup.setBackgroundResource(R.drawable.selector_background_click_default);
            viewGroup.setLayoutParams(new LinearLayout.LayoutParams(itemWidth, LinearLayout.LayoutParams.MATCH_PARENT));
            viewGroup.setId(itemIds[i]);
            attachCountTextView(itemIds[i], viewGroup);
            ((TextView) viewGroup.getChildAt(0)).setText("0");
            ((TextView) viewGroup.getChildAt(1)).setText(itemTitles[i]);
            if (i % pageSize == 0) {
                linearLayout = new LinearLayout(getActivity());
                pages.add(linearLayout);
            }
            if (linearLayout != null)
                linearLayout.addView(viewGroup);
        }


        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return pages.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                container.addView(pages.get(position));
                return pages.get(position);
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }
        });
        addIndicator(pages.size());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int count = linearLayout_indicator.getChildCount();
                for (int i = 0; i < count; i++) {
                    ((ImageView) linearLayout_indicator.getChildAt(i)).setImageResource((i == position ? R.drawable.indicator_selected : R.drawable.indicator_normal));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void attachCountTextView(int id, ViewGroup viewGroup) {
        switch (id) {
            case R.id.account_count_view_resume:
                textView_resumeCount = (TextView) viewGroup.getChildAt(0);
                break;
            case R.id.account_count_view_update:
                textView_updateCount = (TextView) viewGroup.getChildAt(0);
                break;
//            case R.id.account_count_view_receive:
//                textView_receiveCount = (TextView) viewGroup.getChildAt(0);
//                break;
//            case R.id.account_count_view_publish:
//                textView_publishCount = (TextView) viewGroup.getChildAt(0);
//                break;
//            case R.id.account_count_view_share_request:
//                textView_requestCount = (TextView) viewGroup.getChildAt(0);
//                break;
//            case R.id.account_count_view_share:
//                textView_shareCount = (TextView) viewGroup.getChildAt(0);
//                break;
        }
    }

    private void addIndicator(int count) {
        ImageView imageView;
        ViewGroup.MarginLayoutParams mlp;
        for (int i = 0; i < count; i++) {
            imageView = new ImageView(getActivity());
            imageView.setImageResource((i == 0 ? R.drawable.indicator_selected : R.drawable.indicator_normal));
            mlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ((LinearLayout.LayoutParams) mlp).gravity = Gravity.CENTER;
            mlp.rightMargin = 15;
            linearLayout_indicator.addView(imageView, mlp);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_task_card:
                TaskCardActivity.launch(getActivity());
                break;
            case R.id.view_item_right_center:
                RightCenterActivity.launch(getActivity());
                break;
            case R.id.view_item_analysis:
                RemoteBrowserActivity.launch(getActivity(), null, false, URLConst.LINK_H5_STATISTICS);
                break;
            case R.id.tv_identify:
                if (UserInfoManager.getInstance().checkIdentifyStatusAndVerifyIfNeed(getActivity(), true))
                    callShowToastMessage(null, R.string.has_complete_verify);
                break;
            case R.id.view_item_set:
            case R.id.view_head_view:
            case R.id.tv_user_name:
                UserSetActivity.launch(getActivity());
                break;
//            case R.id.view_item_talent_type_interested:
//                InterestedTalentTypeActivity.launch(getActivity(), IInterestedTalentTypeView.TYPE_RESET, RequestCodeConst.INT_REQUEST_TALENT_TYPE);
//                break;
            case R.id.view_item_grade:
            case R.id.tv_grade_info:
                if (UserInfoManager.getInstance().getGrowInfo() == null) {
                    mPresenter.loadGradeAndExperience(true);
                    return;
                }
                AccountExperienceActivity.launch(getActivity());
                break;
//            case R.id.view_item_rank:
//                RemoteBrowserActivity.launch(getActivity(), mResources.getString(R.string.rank), true, URLConst.LINK_H5_RANK);
//                break;
            case R.id.view_item_feedback:
                FeedBackActivity.launch(getActivity());
                break;
            case R.id.account_count_view_resume:
                ResumeClassifyActivity.launch(getActivity());
                break;
            case R.id.account_count_view_update:
                ResumeUpdatedRecordsActivity.launch(getActivity());
                break;
            case R.id.account_count_view_receive:
                break;
            case R.id.iv_scan:
                QRCodeUtil.startToScan(callGetBindActivity());
                break;
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RequestCodeConst.INT_REQUEST_TALENT_TYPE:
                if (resultCode == Activity.RESULT_OK)
                    callUpdateInterestedTalentView(data == null ? null : data.getStringExtra("type"));
                break;
            case QRCodeUtil.INT_REQUEST_CODE_QR_CODE:
                QRCodeUtil.parseQRCodeResult((BaseActivity) getActivity(), resultCode, data);
                break;
        }
    }
}
