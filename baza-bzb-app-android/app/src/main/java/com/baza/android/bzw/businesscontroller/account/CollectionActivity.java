package com.baza.android.bzw.businesscontroller.account;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/5/26.
 * Title：我的收藏
 * Note：
 */

public class CollectionActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_collection;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_collection);
    }

    @Override
    protected void initWhenCallOnCreate() {
        TextView textView_title = findViewById(R.id.tv_title);
        textView_title.setText(R.string.mine_collection);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, new CollectionFragment()).commit();
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
        }
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, CollectionActivity.class);
        activity.startActivity(intent);
    }
}
