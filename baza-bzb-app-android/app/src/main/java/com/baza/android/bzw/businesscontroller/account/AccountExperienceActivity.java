package com.baza.android.bzw.businesscontroller.account;

import android.app.Activity;
import android.content.Intent;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.bean.user.GrowBean;
import com.baza.android.bzw.bean.user.UserInfoBean;
import com.baza.android.bzw.businesscontroller.account.presenter.AccountExperiencePresenter;
import com.baza.android.bzw.businesscontroller.account.viewinterface.IAccountExperienceView;
import com.baza.android.bzw.businesscontroller.browser.RemoteBrowserActivity;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.manager.UserInfoManager;
import com.slib.utils.AppUtil;
import com.slib.utils.LoadImageUtil;
import com.bznet.android.rcbox.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/5/31.
 * Title：我的经验值
 * Note：
 */

public class AccountExperienceActivity extends BaseActivity implements IAccountExperienceView, View.OnClickListener {
    @BindView(R.id.tv_title)
    TextView textView_title;
    @BindView(R.id.tv_right_click)
    TextView textView_rightClick;
    @BindView(R.id.tv_grade)
    TextView textView_grade;
    //    @BindView(R.id.tv_enable_request_share_count)
//    TextView textView_enableRequestShareCount;
    @BindView(R.id.tv_enable_update_count)
    TextView textView_enableUpdateCount;
    @BindView(R.id.tv_experience_hint)
    TextView textView_nextGradeNeedValue;
    @BindView(R.id.tv_grade_resume_low)
    TextView textView_gradeLow;
    @BindView(R.id.tv_grade_resume_high)
    TextView textView_gradeHigh;
    @BindView(R.id.tv_grade_pointer)
    TextView textView_gradePointer;
    @BindView(R.id.progressBar)
    ProgressBar progressBar_gradeUpdate;
    @BindView(R.id.civ_avatar)
    ImageView imageView_avatar;
    @BindView(R.id.tv_avatar)
    TextView textView_avatar;
//    @BindView(R.id.tv_enable_list_match_count)
//    TextView textView_listMatchCount;
    private AccountExperiencePresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_account_experience;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_account_experience);
    }

    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        mPresenter = new AccountExperiencePresenter(this);
        textView_title.setText(R.string.title_my_experience);
        textView_rightClick.setText(R.string.rule_des);

        UserInfoBean userInfoBean = UserInfoManager.getInstance().getUserInfo();
        String name = (TextUtils.isEmpty(userInfoBean.nickName) ? userInfoBean.trueName : userInfoBean.nickName);
        if (TextUtils.isEmpty(userInfoBean.avatar)) {
            imageView_avatar.setVisibility(View.GONE);
            textView_avatar.setText((TextUtils.isEmpty(name) ? CommonConst.STR_DEFAULT_USER_NAME_SX : name.substring(0, 1)));
            textView_avatar.setVisibility(View.VISIBLE);
        } else {
            textView_avatar.setVisibility(View.INVISIBLE);
            LoadImageUtil.loadImage(userInfoBean.avatar, R.drawable.avatar_def, imageView_avatar);
            imageView_avatar.setVisibility(View.VISIBLE);
        }
        mPresenter.initialize();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, AccountExperienceActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                finish();
                break;
            case R.id.tv_right_click:
                RemoteBrowserActivity.launch(this, null, false, URLConst.LINK_H5_GRADE);
                break;
        }
    }

    @Override
    public void callSetMessage() {
        GrowBean growInfo = UserInfoManager.getInstance().getGrowInfo();
        if (growInfo == null)
            return;
        SpannableString spannableString = new SpannableString(mResources.getString(R.string.grade_value_b, String.valueOf(growInfo.presentGrade)));
        spannableString.setSpan(new ForegroundColorSpan(mResources.getColor(R.color.text_color_orange_FF7700)), 3, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_grade.setText(spannableString);
//        textView_enableRequestShareCount.setText(String.valueOf(growInfo.shareRequestLimit));
        textView_enableUpdateCount.setText(String.valueOf(growInfo.limit));
//        textView_listMatchCount.setText(String.valueOf(growInfo.nameListMatchLimit));

        spannableString = new SpannableString(mResources.getString(R.string.grade_update_hint, AppUtil.formatTob(growInfo.nextGradeNeedResume)));
        spannableString.setSpan(new ForegroundColorSpan(mResources.getColor(R.color.text_color_orange_FF7700)), 11, spannableString.length() - 1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_nextGradeNeedValue.setText(spannableString);

        textView_gradeLow.setText(AppUtil.formatTob(growInfo.thisGradeFloorResumeCount));
        int topLevel = growInfo.nextGradeNeedResume + growInfo.presentResumeCount;
        topLevel = (topLevel <= 0 ? Integer.MAX_VALUE : topLevel);
        textView_gradeHigh.setText(AppUtil.formatTob(topLevel));
        textView_gradePointer.setText(AppUtil.formatTob(growInfo.presentResumeCount));
        final float rate = (growInfo.presentResumeCount * 1.0f / topLevel);
        progressBar_gradeUpdate.setProgress((int) (rate * 100));
        textView_gradePointer.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) textView_gradePointer.getLayoutParams();
                lp.leftMargin = (int) (progressBar_gradeUpdate.getMeasuredWidth() * rate + mResources.getDimension(R.dimen.dp_30) - textView_gradePointer.getMeasuredWidth() / 2);
                textView_gradePointer.setLayoutParams(lp);
            }
        });
//        int levelImgIndex = growInfo.presentGrade - 1;
//        if (levelImgIndex >= 0 && levelImgIndex < mLevelImgs.length)
//            imageView_grade_logo.setImageResource(mLevelImgs[levelImgIndex]);
    }

}
