package com.baza.android.bzw.businesscontroller.resume.tab;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.bznet.android.rcbox.R;

public class MineResumeActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mine_resume;
    }

    @Override
    protected String getPageTitle() {
        return null;
    }

    @Override
    protected void initWhenCallOnCreate() {
        ((TextView) findViewById(R.id.tv_title)).setText(R.string.tab_talent_tab_mine);
        getSupportFragmentManager().beginTransaction().add(R.id.fl_content, new MineResumeFragment(), "mineresume").commitAllowingStateLoss();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, MineResumeActivity.class);
        activity.startActivity(intent);
    }

    public void onClick(View v) {
        finish();
    }
}
