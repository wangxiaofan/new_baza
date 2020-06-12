package com.baza.android.bzw.businesscontroller.find.updateengine;

import android.app.Activity;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.businesscontroller.browser.RemoteBrowserActivity;
import com.baza.android.bzw.businesscontroller.find.updateengine.adapter.UpdateEngineQAAdapter;
import com.baza.android.bzw.constant.URLConst;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/8/24.
 * Title：简历更新常见问题
 * Note：
 */

public class ResumeUpdateQAActivity extends BaseActivity implements View.OnClickListener {
    private ListView listView;
    private UpdateEngineQAAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_resume_update_qa;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_candidate_update_qa);
    }

    @Override
    protected void initWhenCallOnCreate() {
        TextView textView = findViewById(R.id.tv_title);
        textView.setText(R.string.title_qa);

        textView = new TextView(this);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setPadding(ScreenUtil.dip2px(35), 0, 0, ScreenUtil.dip2px(20));
        textView.setTextColor(mResources.getColor(R.color.text_color_blue_53ABD5));
        SpannableString spannableString = new SpannableString(mResources.getString(R.string.how_to_update_grade));
        spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
        textView.setOnClickListener(this);
        listView = findViewById(R.id.list);
        listView.addFooterView(textView);

        mAdapter = new UpdateEngineQAAdapter(this, null);
        listView.setAdapter(mAdapter);
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, ResumeUpdateQAActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                finish();
                break;
            default:
                RemoteBrowserActivity.launch(this, null, false, URLConst.LINK_H5_GRADE);
                break;
        }
    }
}
