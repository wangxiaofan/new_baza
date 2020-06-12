package com.baza.android.bzw.businesscontroller.resume.detail;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.baza.android.bzw.bean.resume.ResumeDetailBean;
import com.baza.android.bzw.businesscontroller.company.CompanyDetailActivity;
import com.baza.android.bzw.businesscontroller.label.AssignLabelActivity;
import com.baza.android.bzw.businesscontroller.resume.detail.presenter.ResumeDetailPresenter;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.SharedPreferenceConst;
import com.baza.android.bzw.dao.ResumeCompareDao;
import com.baza.android.bzw.manager.UserInfoManager;
import com.bznet.android.rcbox.R;
import com.slib.storage.sharedpreference.SharedPreferenceManager;
import com.slib.utils.AppUtil;
import com.slib.utils.ScreenUtil;

/**
 * Created by Vincent.Lei on 2017/5/24.
 * Title：
 * Note：
 */

public class DetailBottomMenuUI implements View.OnClickListener {
    private View view_updateInfo;
    private View view_mimeMenu;

    private TextView textView_collection;
    private IResumeDetailView mResumeDetailView;
    private ResumeDetailPresenter mPresenter;
    private Drawable mDrawableCollectionMine, mDrawableUnCollection;
    private View view_menuAddRecommend;
    private TextView textView_recommendFloat;
    private Resources mResources;

    public DetailBottomMenuUI(IResumeDetailView mResumeDetailView, ResumeDetailPresenter mPresenter) {
        this.mResumeDetailView = mResumeDetailView;
        this.mPresenter = mPresenter;
        mResources = mResumeDetailView.callGetResources();

        mDrawableCollectionMine = AppUtil.drawableInit(R.drawable.btn_shoucang_nor, mResources);
        mDrawableUnCollection = AppUtil.drawableInit(R.drawable.btn_shoucang_sel, mResources);
        initMenu();
    }

    private void initMenu() {
        if (view_mimeMenu == null) {
            view_mimeMenu = mResumeDetailView.callGetBindActivity().findViewById(R.id.view_bottom_menu);
            view_updateInfo = view_mimeMenu.findViewById(R.id.fl_enable_update);
            textView_collection = view_mimeMenu.findViewById(R.id.tv_collection);
            textView_collection.setOnClickListener(this);
            view_mimeMenu.findViewById(R.id.tv_add_remark).setOnClickListener(this);
            view_mimeMenu.findViewById(R.id.tv_add_label).setOnClickListener(this);
            view_mimeMenu.findViewById(R.id.tv_update_to_target).setOnClickListener(this);
            view_menuAddRecommend = view_mimeMenu.findViewById(R.id.tv_add_recommend);
            view_menuAddRecommend.setOnClickListener(this);
        }

    }

//    private void showRecommendHint() {
//        if (SharedPreferenceManager.getBoolean(SharedPreferenceConst.SP_ADD_RECOMMEND_HINT + UserInfoManager.getInstance().getUserInfo().unionId))
//            return;
//        SharedPreferenceManager.saveBoolean(SharedPreferenceConst.SP_ADD_RECOMMEND_HINT + UserInfoManager.getInstance().getUserInfo().unionId, true);
//        String msg = mResources.getString(R.string.resume_add_recommend_hint);
//        Activity activity = mResumeDetailView.callGetBindActivity();
//        if (textView_recommendFloat == null) {
//            textView_recommendFloat = new TextView(activity);
//            textView_recommendFloat.setTextSize(TypedValue.COMPLEX_UNIT_DIP, ScreenUtil.px2dip(mResources.getDimension(R.dimen.text_size_10)));
//            textView_recommendFloat.setTextColor(mResources.getColor(R.color.text_color_blue_0D315C));
//            textView_recommendFloat.setGravity(Gravity.CENTER);
//            textView_recommendFloat.setBackgroundResource(R.drawable.recommend_float_bg);
//            textView_recommendFloat.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dismissRecommendFloattingView();
//                    mResumeDetailView.callShowRecommendView();
//                }
//            });
//        }
//        int[] outLocation = new int[2];
//        view_menuAddRecommend.getLocationOnScreen(outLocation);
//        textView_recommendFloat.setText(msg);
//        int width = (int) (textView_recommendFloat.getPaint().measureText(msg) + 2 * (int) mResources.getDimension(R.dimen.dp_10));
//        int height = (int) mResources.getDimension(R.dimen.dp_35);
//        FrameLayout.LayoutParams fml = new FrameLayout.LayoutParams(width, height);
//        fml.leftMargin = outLocation[0] + view_menuAddRecommend.getWidth() / 2 - width / 2;
//        fml.topMargin = outLocation[1] - height;
//
//        textView_recommendFloat.setLayoutParams(fml);
//        if (textView_recommendFloat.getParent() == null)
//            ((FrameLayout) activity.getWindow().getDecorView()).addView(textView_recommendFloat);
//
//
//    }

    private void dismissRecommendFloattingView() {
        if (textView_recommendFloat != null && textView_recommendFloat.getParent() != null) {
            Activity activity = mResumeDetailView.callGetBindActivity();
            ((FrameLayout) activity.getWindow().getDecorView()).removeView(textView_recommendFloat);
            textView_recommendFloat = null;
        }
    }

    public void updateCollectionInfo() {
        ResumeDetailBean resumeDetailBean = mPresenter.getCurrentResumeData();
        if (resumeDetailBean == null)
            return;
        setCollectionView(resumeDetailBean.collectStatus == CommonConst.COLLECTION_YES);
    }

    private void setCollectionView(boolean collected) {
        textView_collection.setCompoundDrawables(null, (collected ? mDrawableUnCollection : mDrawableCollectionMine), null, null);
        textView_collection.setText((collected ? R.string.un_collection : R.string.collection));
    }

//    public View getView() {
//        ResumeDetailBean resumeDetailBean = mPresenter.getCurrentResumeData();
//        if (resumeDetailBean == null)
//            return null;
//        initMenu(resumeDetailBean);
//        return view_mimeMenu;
//    }

    @Override
    public void onClick(View v) {
        dismissRecommendFloattingView();
        switch (v.getId()) {
            case R.id.tv_add_remark:
                mResumeDetailView.callShowAddRemarkView();
                break;
            case R.id.tv_add_label:
                AssignLabelActivity.launch(mResumeDetailView.callGetBindActivity(), mPresenter.getCurrentResumeData());
                break;
            case R.id.tv_add_recommend:
                mResumeDetailView.callShowRecommendView();
                break;
            case R.id.tv_collection:
                mPresenter.collection();
                break;
            case R.id.tv_update_to_target:
                mResumeDetailView.updateToTargetResume();
                break;
        }
    }

    public void setData() {
        ResumeCompareDao compareDao = mPresenter.getResumeCompareDao();
        ResumeDetailBean resumeDetailBean = mPresenter.getCurrentResumeData();
        if (resumeDetailBean == null || view_mimeMenu == null)
            return;
        setCollectionView(resumeDetailBean.collectStatus == CommonConst.COLLECTION_YES);
        if (mResumeDetailView.callGetBindActivity() instanceof CompanyDetailActivity) {
            if (resumeDetailBean.waitUpdate) {
                view_updateInfo.setVisibility(View.VISIBLE);
            } else {
                view_updateInfo.setVisibility(View.GONE);
            }
        } else {
            if (resumeDetailBean.isWaitUpdate() && view_updateInfo != null && compareDao == null) {
                view_updateInfo.setVisibility(View.VISIBLE);
            } else if (view_updateInfo != null)
                view_updateInfo.setVisibility(View.GONE);
        }
//        view_menuAddRecommend.post(new Runnable() {
//            @Override
//            public void run() {
//                showRecommendHint();
//            }
//        });
    }

    void updateViewForUpdateHistory() {
        view_mimeMenu.setVisibility(View.GONE);
    }

    public void destroy() {
        AppUtil.nonCallBackDrawable(mDrawableCollectionMine);
        AppUtil.nonCallBackDrawable(mDrawableUnCollection);
    }
}
