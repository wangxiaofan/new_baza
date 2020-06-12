package com.baza.android.bzw.base;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.log.ReportAgent;
import com.slib.permission.PermissionsManager;
import com.slib.permission.PermissionsResultAction;
import com.slib.progress.SelfDefineProgressDialog;
import com.slib.utils.ToastUtil;
import com.bznet.android.rcbox.R;

import java.util.List;

/**
 * Created by LW on 2016/5/19.
 * Title :Activity基类
 * Note : 所有Activity直接或者间接继承该类
 */
@SuppressLint("StringFormatMatches")
public abstract class BaseActivity extends FragmentActivity implements IBaseView {

    protected Resources mResources;
    protected BZWApplication mApplication;
    protected InputMethodManager mImm;
    protected SelfDefineProgressDialog mProgressDialog;
    protected boolean mShouldSelfDefineStatusBarChanged;
    protected static int mStatusBarHeight;
    public boolean mShowOnScreen;
    protected boolean mCalledActivityDeadForApp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            //避免后台因为资源等问题杀掉Activity，回到前台后fragment回复导致fragment遮盖问题
            savedInstanceState.putParcelable("android:support:fragments", null);
        }
        super.onCreate(savedInstanceState);
        mResources = getResources();
        mApplication = BZWApplication.getApplication();
        mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        initOverAll();
        int layoutId = getLayoutId();
        View contentView = null;
        if (layoutId > 0)
            contentView = getLayoutInflater().inflate(layoutId, null);
        //判断是否需要适配4.4状态栏 如果当前sdk版本大于等于4.4
        // contentView将被重置(如果不想使用默认的状态栏 isUseDefaultStatusBarBackground 返回false
        // 并在changedSelfDefineUIToFitSDKReachKITKAT方法里面做出相应的界面调整)
        contentView = resetStatusBarWhenSDKReachKITKAT(contentView);
        if (contentView != null)
            setContentView(contentView);
        initWhenCallOnCreate();
        ///////////////
        if (mShouldSelfDefineStatusBarChanged)
            changedSelfDefineUIToFitSDKReachKITKAT(mStatusBarHeight);
        if (shouldFitLightStatusBarWithDefaultMode())
            fitLightStatusBar();
        ReportAgent.onCreate(this, getPageTitle());
    }

    protected void initOverAll() {
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
     * Activity OnCreate 回调(初始化view信息等)
     */
    protected abstract void initWhenCallOnCreate();

    /**
     * 适配4.4以上改变状态栏
     * 暂时不用
     *
     * @param statusBarHeight
     */
    protected abstract void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight);


    /**
     * 适配4.4以上改变状态栏
     */
    private View resetStatusBarWhenSDKReachKITKAT(View contentView) {
        if (mStatusBarHeight == 0)
            mStatusBarHeight = getStatusBarHeight();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isStatusBarTintEnabledWhenSDKReachKITKAT()) {
            mShouldSelfDefineStatusBarChanged = true;
            setTranslucentStatus(true);
            if (isUseDefaultStatusBarBackground()) {
                ViewGroup viewGroup = contentView.findViewById(R.id.view_title_bar);
                if (viewGroup == null)
                    throw new RuntimeException("not find titleBar with id(R.id.view_title_bar) when useDefaultStatusBarBackground");
                View child = viewGroup.getChildAt(0);
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
                lp.topMargin += mStatusBarHeight;
                child.setLayoutParams(lp);
                return contentView;
            }
        }
        return contentView;
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

    /**
     * 适配4.4以上改变状态栏
     */
    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window window = getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0 全透明实现
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            return;
        }
        WindowManager.LayoutParams winParams = window.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        window.setAttributes(winParams);
    }

    protected void fitLightStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        else if (addCoverIfNeed && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            View view = new View(this);
//            view.setBackgroundColor(Color.parseColor("#7F000000"));
//            FrameLayout.LayoutParams vlp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mStatusBarHeight);
//            view.setLayoutParams(vlp);
//            ((FrameLayout) (getWindow().getDecorView())).addView(view);
//        }
    }

    /**
     * 是否适配4.4以上改变状态栏
     */
    protected boolean isStatusBarTintEnabledWhenSDKReachKITKAT() {
        return true;
    }

    protected boolean shouldFitLightStatusBarWithDefaultMode() {
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isHideInputMethodAutoWhenTouchWindow())
                //点击activity任何区域 隐藏输入框
                hideSoftInput();
        }
        return super.dispatchTouchEvent(ev);
    }


    protected boolean isHideInputMethodAutoWhenTouchWindow() {
        return true;
    }

    @Override
    public void finish() {
        hideSoftInput();
        super.finish();
    }

    /**
     * 隐藏输入框
     */
    public void hideSoftInput() {
        try {
            if (mImm != null && mImm.isActive() && getCurrentFocus() != null
                    && getCurrentFocus().getWindowToken() != null)
                mImm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        0);
        } catch (Exception e) {
            //ignore
        }
    }

    public void showSoftInput(View view) {
        try {
            if (mImm != null && mImm.isActive() && getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null)
                mImm.showSoftInput(view, 0);
        } catch (Exception e) {
            //ignore
        }
    }

    /**
     * 6.0及以上申请权限
     * Manifest.permission.WRITE_EXTERNAL_STORAGE 外置存储写入
     * Manifest.permission.CAMERA 拍照权限
     */
    public void requestPermission(String permission, String[] permissions, PermissionsResultAction permissionsResultAction) {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(this, (permission != null ? new String[]{permission} : permissions), permissionsResultAction);
    }

    /**
     * 6.0及以上权限申请返回
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }


    @Override
    protected void onDestroy() {
        mShowOnScreen = false;
        if (!mCalledActivityDeadForApp) {
            onActivityDeadForApp();
            mCalledActivityDeadForApp = true;
        }
        ReportAgent.onDestroy(this);
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mShowOnScreen = true;
        ReportAgent.onResume(this, getPageTitle());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mShowOnScreen = false;
        ReportAgent.onPause(this, getPageTitle());
    }

    @Override
    protected void onStop() {
        super.onStop();
        mShowOnScreen = false;
        if (isFinishing()) {
            onActivityDeadForApp();
            mCalledActivityDeadForApp = true;
            List<Fragment> list = getSupportFragmentManager().getFragments();
            if (list != null && !list.isEmpty()) {
                for (Fragment f : list) {
                    if (f instanceof BaseFragment)
                        ((BaseFragment) f).prepareToDestroyFragment();
                }
            }
        }
    }

//    public boolean isShowOnScreen() {
//        return mShowOnScreen;
//    }

    protected void onActivityDeadForApp() {
//        LogUtil.d("onActivityDeadForApp");
    }

//    protected int getSelfDefineStatusBarColor() {
//        return mResources.getColor(R.color.status_bar_default_color);
//    }

    @Override
    public BaseActivity callGetBindActivity() {
        return this;
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
            mProgressDialog = new SelfDefineProgressDialog(this);
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
}
