package com.baza.android.bzw.businesscontroller.account;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.constant.CommonConst;
import com.slib.utils.AppUtil;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/5/27.
 * Title：导入渠道列表
 * Note：
 */

public class ImportPlatformListActivity extends BaseActivity implements View.OnClickListener {
    private static final int GUIDE_TYPE_PC = 1;
    private static final int GUIDE_TYPE_WEB = 2;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_candidate_import_platform_list;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_select_candidate_platform);
    }

    @Override
    protected void initWhenCallOnCreate() {
        boolean showGuide = getIntent().getBooleanExtra("showGuide", false);
        int guideType = getIntent().getIntExtra("guideType", GUIDE_TYPE_PC);
        TextView textView_title = findViewById(R.id.tv_title);
        if (showGuide) {
//            findViewById(R.id.tv_from_email).setVisibility(View.GONE);
            findViewById(R.id.tv_from_pc).setVisibility(View.GONE);
            findViewById(R.id.tv_from_web).setVisibility(View.GONE);
            findViewById(R.id.tv_sync_hint).setVisibility(View.GONE);
            textView_title.setText((guideType == GUIDE_TYPE_PC ? R.string.title_from_pc : R.string.title_from_web));
            ImageView imageView = findViewById(R.id.iv);
            imageView.setImageResource((guideType == GUIDE_TYPE_PC ? R.drawable.image_import_from_pc : R.drawable.image_import_from_web));
            imageView.setVisibility(View.VISIBLE);
            findViewById(R.id.tv_copy_link).setVisibility(View.VISIBLE);
        } else {
            textView_title.setText(R.string.candidate_import);
//            findViewById(R.id.tv_from_email).setOnClickListener(this);
            findViewById(R.id.tv_from_pc).setOnClickListener(this);
            findViewById(R.id.tv_from_web).setOnClickListener(this);
        }

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
//            case R.id.tv_from_email:
//                EmailSyncedListActivity.launch(this);
//                break;
            case R.id.tv_from_pc:
                ImportPlatformListActivity.launch(this, true, GUIDE_TYPE_PC);
                break;
            case R.id.tv_from_web:
                ImportPlatformListActivity.launch(this, true, GUIDE_TYPE_WEB);
                break;
            case R.id.tv_copy_link:
                AppUtil.copyToClipboard(mApplication, CommonConst.STR_PC_DOWNLOAD_URL);
                callShowToastMessage(null, R.string.already_copy_download_url_to_clip);
                break;
        }
    }

    public static void launch(Activity activity) {
        launch(activity, false, 0);
    }

    public static void launch(Activity activity, boolean showGuide, int guideType) {
        Intent intent = new Intent(activity, ImportPlatformListActivity.class);
        intent.putExtra("showGuide", showGuide);
        intent.putExtra("guideType", guideType);
        activity.startActivity(intent);
    }
}
