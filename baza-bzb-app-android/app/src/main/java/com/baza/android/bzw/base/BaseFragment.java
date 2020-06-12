package com.baza.android.bzw.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.log.ReportAgent;
import com.slib.progress.SelfDefineProgressDialog;
import com.slib.utils.ToastUtil;
import com.bznet.android.rcbox.R;

/**
 * Created by LW on 2016/5/19.
 * Title :Fragment基类
 * Note :所有Fragment直接或者间接继承该类
 */
@SuppressLint("StringFormatMatches")
public abstract class BaseFragment extends Fragment implements IBaseView {
    public interface IFragmentEventsListener {
        Object onFragmentEventsArrival(int eventId, Object input);
    }

    protected Resources mResources;
    protected BZWApplication mApplication;
    protected SelfDefineProgressDialog mProgressDialog;
    protected View mRootView;
    protected IFragmentEventsListener mIFragmentEventsListener;
    private boolean mCalledFragmentDeadForAppSuper;
    private boolean mShouldSelfDefineStatusBarChanged;
    protected static int mStatusBarHeight;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResources = getResources();
        mApplication = BZWApplication.getApplication();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutId = getLayoutId();
        if (layoutId > 0)
            mRootView = inflater.inflate(layoutId, null);
        View contentView = resetStatusBarWhenSDKReachKITKAT(mRootView);
        initWhenOnCreatedViews(mRootView);
        if (mShouldSelfDefineStatusBarChanged)
            changedUIToFitSDKReachKITKAT(mStatusBarHeight);
        return contentView != null ? contentView : super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initWhenOnActivityCreated();
    }

    /**
     * 设置fragment时间监听
     */
    public void setFragmentEventsListener(IFragmentEventsListener mIFragmentEventsListener) {
        this.mIFragmentEventsListener = mIFragmentEventsListener;
    }

    /**
     * 提供布局layout ID
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 提供页面标题
     *
     * @return
     */
    protected abstract String getPageTitle();

    /**
     * OnCreatedViews 回调(初始化view信息等)
     */
    protected abstract void initWhenOnCreatedViews(View mRootView);

    /**
     * Activity OnCreate 回调
     */

    protected abstract void initWhenOnActivityCreated();

    /**
     * 适配4.4以上改变状态栏
     *
     * @param statusBarHeight
     */
    protected abstract void changedUIToFitSDKReachKITKAT(int statusBarHeight);


    //适配4.4以上改变状态栏
    private View resetStatusBarWhenSDKReachKITKAT(View mRootView) {
        if (mStatusBarHeight == 0)
            mStatusBarHeight = getStatusBarHeight();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isStatusBarTintEnabledWhenSDKReachKITKAT()) {
            mShouldSelfDefineStatusBarChanged = true;
            if (isUseDefaultStatusBarBackground()) {
                ViewGroup viewGroup = mRootView.findViewById(R.id.view_title_bar);
                if (viewGroup == null)
                    throw new RuntimeException("not find titleBar with id(R.id.view_title_bar) when useDefaultStatusBarBackground");
                View child = viewGroup.getChildAt(0);
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
                lp.topMargin += mStatusBarHeight;
                child.setLayoutParams(lp);
                return mRootView;
            }
        }
        return mRootView;
    }

    protected int getStatusBarHeight() {
        int resourceId = mResources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return mResources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    protected boolean isUseDefaultStatusBarBackground() {
        return true;
    }

    //适配4.4以上改变状态栏
    protected boolean isStatusBarTintEnabledWhenSDKReachKITKAT() {
        return true;
    }


    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        onActivityResult(requestCode, resultCode, data);
    }

//    protected int getSelfDefineStatusBarColor() {
//        return mResources.getColor(R.color.status_bar_default_color);
//    }


    @Override
    public BaseActivity callGetBindActivity() {
        return (BaseActivity) getActivity();
    }

    @Override
    public Resources callGetResources() {
        return mResources;
    }

    /**
     * 加载ProgressBar
     */
    @Override
    public void callShowProgress(String msg, boolean cancelEnable) {
        if (mProgressDialog == null) {
            mProgressDialog = new SelfDefineProgressDialog(getActivity());
        }
        mProgressDialog.setCancelable(cancelEnable);
        if (!mProgressDialog.isShowing())
            mProgressDialog.show(msg);
    }

    /**
     * 取消ProgressBar
     */
    @Override
    public void callCancelProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            try {
                mProgressDialog.dismiss();
            } catch (Exception e) {
            }
        }
        mProgressDialog = null;
    }

    /**
     * 显示Toast消息
     */
    @Override
    public void callShowToastMessage(String msg, int id) {
        if (id > 0)
            ToastUtil.showToast(mApplication, id);
        else if (!TextUtils.isEmpty(msg))
            ToastUtil.showToast(mApplication, msg);
    }

    @Override
    public BZWApplication callGetApplication() {
        return mApplication;
    }

    protected void replyActivityEvents(int eventId, Object data) {
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden)
            ReportAgent.onPause(this, getPageTitle());
        else
            ReportAgent.onResume(this, getPageTitle());
    }

    @Override
    public void onResume() {
        super.onResume();
        ReportAgent.onResume(this, getPageTitle());
    }

    @Override
    public void onPause() {
        super.onPause();
        ReportAgent.onPause(this, getPageTitle());
    }

    @Override
    public void onDestroy() {
        if (!mCalledFragmentDeadForAppSuper)
            prepareToDestroyFragment();
        ReportAgent.onDestroy(this);
        super.onDestroy();
    }

    void prepareToDestroyFragment() {
        onFragmentDeadForApp();
        if (!mCalledFragmentDeadForAppSuper)
            throw new RuntimeException("please call super.onFragmentDeadForApp()");
    }

    protected void onFragmentDeadForApp() {
        mCalledFragmentDeadForAppSuper = true;
    }

}
