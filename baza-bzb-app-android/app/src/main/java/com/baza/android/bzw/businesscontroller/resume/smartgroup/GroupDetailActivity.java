package com.baza.android.bzw.businesscontroller.resume.smartgroup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseFragment;
import com.baza.android.bzw.bean.smartgroup.SmartGroupFoldersResultBean;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.RequestCodeConst;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2018/8/31.
 * Title：
 * Note：
 */
public class GroupDetailActivity extends BaseActivity {
    private int mType;
    private SmartGroupFoldersResultBean.SmartGroupFolderBean mSmartGroupFolderBean;
    private BaseFragment mBaseFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.smart_group_activity_detail;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.smart_group_detail);
    }

    @Override
    protected void initWhenCallOnCreate() {
        Intent intent = getIntent();
        mType = intent.getIntExtra("type", CommonConst.SmartGroupType.GROUP_TYPE_CUSTOMER);
        mSmartGroupFolderBean = (SmartGroupFoldersResultBean.SmartGroupFolderBean) intent.getSerializableExtra("smartGroupFolder");
        mBaseFragment = createFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, mBaseFragment).commit();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    @Override
    protected boolean isUseDefaultStatusBarBackground() {
        return false;
    }

    private BaseFragment createFragment() {
        BaseFragment fragment;
        Bundle bundle = new Bundle();
        bundle.putInt("type", mType);
        if (mSmartGroupFolderBean != null) {
            bundle.putSerializable("groupFolder", mSmartGroupFolderBean);
        }
        switch (mType) {
            case CommonConst.SmartGroupType.GROUP_TYPE_CUSTOMER:
                fragment = new SFGroupDetailFragment();
                fragment.setArguments(bundle);
                break;
            default:
                fragment = new CommonGroupDetailFragment();
                fragment.setArguments(bundle);
                break;
        }
        return fragment;
    }


    public static void launch(Activity activity, SmartGroupFoldersResultBean.SmartGroupFolderBean smartGroupFolderBean, int type, int requestCode) {
        Intent intent = new Intent(activity, GroupDetailActivity.class);
        intent.putExtra("type", type);
        if (smartGroupFolderBean != null)
            intent.putExtra("smartGroupFolder", smartGroupFolderBean);
        if (requestCode != RequestCodeConst.INT_REQUEST_NONE)
            activity.startActivityForResult(intent, requestCode);
        else
            activity.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mBaseFragment != null)
            mBaseFragment.onFragmentResult(requestCode, resultCode, data);
    }
}
