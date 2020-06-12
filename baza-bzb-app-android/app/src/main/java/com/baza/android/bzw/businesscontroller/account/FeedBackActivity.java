package com.baza.android.bzw.businesscontroller.account;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.businesscontroller.account.presenter.FeedBackPresenter;
import com.baza.android.bzw.businesscontroller.account.viewinterface.IFeedBackView;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2018/1/12.
 * Title：
 * Note：
 */

public class FeedBackActivity extends BaseActivity implements IFeedBackView, View.OnClickListener {
    private FeedBackPresenter mPresenter;
    private EditText editText;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feed_back;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_feed_back);
    }

    @Override
    protected void initWhenCallOnCreate() {
        mPresenter = new FeedBackPresenter(this);
        TextView textView = findViewById(R.id.tv_title);
        textView.setText(R.string.title_feed_back);
        editText = findViewById(R.id.et_input);
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
            case R.id.btn_submit:
                mPresenter.submit();
                break;
        }
    }

    @Override
    public String callGetContent() {
        return editText.getText().toString().trim();
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, FeedBackActivity.class);
        activity.startActivity(intent);
    }
}
