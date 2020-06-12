package com.baza.android.bzw.businesscontroller.publish;

import android.app.Activity;
import android.content.Intent;
import androidx.viewpager.widget.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.businesscontroller.login.LoginActivity;
import com.baza.android.bzw.businesscontroller.login.adapter.FirstOpenAppGuideAdapter;
import com.baza.android.bzw.constant.ActionConst;
import com.baza.android.bzw.constant.SharedPreferenceConst;
import com.baza.android.bzw.events.IDefaultEventsSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.slib.storage.sharedpreference.SharedPreferenceManager;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;


/**
 * Created by Vincent.Lei on 2017/5/16.
 * Title： 首次打开APP 引导
 * Note：
 */

public class FirstOpenAppGuideActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    ViewPager viewPager;
    Button button_process;
    LinearLayout linearLayout_indicator;

    private IDefaultEventsSubscriber mDefaultEventsSubscriber;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_first_open_app;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_first_open_app);
    }

    @Override
    protected boolean isUseDefaultStatusBarBackground() {
        return false;
    }

    @Override
    protected void initWhenCallOnCreate() {
        viewPager = findViewById(R.id.viewPager);
        button_process = findViewById(R.id.btn_process);
        linearLayout_indicator = findViewById(R.id.ll_indicator);
        FirstOpenAppGuideAdapter appGuideAdapter = new FirstOpenAppGuideAdapter(this);
        addIndicator(appGuideAdapter.getCount(), 0);
//        viewPager.setPageTransformer(true, new FirstOpenAppGuideAdapter.OpenAppGuidePageTransformer());
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(appGuideAdapter);
        viewPager.addOnPageChangeListener(this);
        SharedPreferenceManager.saveBoolean(SharedPreferenceConst.SP_IS_NOY_FIRST_IN_APP, true);
        registerCloseSubscriber(true);
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) viewPager.getLayoutParams();
        mlp.topMargin += statusBarHeight;
        viewPager.setLayoutParams(mlp);
    }


    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, FirstOpenAppGuideActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_process:
                LoginActivity.launch(this);
                break;
        }
    }

    private void registerCloseSubscriber(boolean register) {
        if (register) {
            if (mDefaultEventsSubscriber == null)
                mDefaultEventsSubscriber = new IDefaultEventsSubscriber() {
                    @Override
                    public boolean killEvent(String action, Object data) {
                        if (ActionConst.ACTION_EVENT_LOGIN.equals(action))
                            finish();
                        return false;
                    }
                };
            UIEventsObservable.getInstance().subscribeEvent(IDefaultEventsSubscriber.class, this, mDefaultEventsSubscriber);
        } else if (mDefaultEventsSubscriber != null)
            UIEventsObservable.getInstance().stopSubscribeEvent(IDefaultEventsSubscriber.class, this);
    }

    @Override
    protected void onActivityDeadForApp() {
        super.onActivityDeadForApp();
        registerCloseSubscriber(false);
    }

    private void addIndicator(int count, int selectionPosition) {
        ImageView imageView;
        LinearLayout.MarginLayoutParams mlp;
        int wh = ScreenUtil.dip2px(6);
        for (int i = 0; i < count; i++) {
            imageView = new ImageView(this);
            mlp = new LinearLayout.LayoutParams(wh, wh);
            ((LinearLayout.LayoutParams) mlp).gravity = Gravity.LEFT;
            mlp.rightMargin = 20;
            imageView.setLayoutParams(mlp);
            imageView.setImageResource((i == selectionPosition ? R.drawable.open_guide_indicator_selected : R.drawable.open_guide_indicator_normal));
            linearLayout_indicator.addView(imageView);
        }
    }

    void updateIndicator(int selectionPosition) {
        int childCount = linearLayout_indicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ((ImageView) linearLayout_indicator.getChildAt(i)).setImageResource((i == selectionPosition ? R.drawable.open_guide_indicator_selected : R.drawable.open_guide_indicator_normal));
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        updateIndicator(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
