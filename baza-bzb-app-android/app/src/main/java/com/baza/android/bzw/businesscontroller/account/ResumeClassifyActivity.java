package com.baza.android.bzw.businesscontroller.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.businesscontroller.account.viewinterface.IResumeClassifyView;
import com.baza.android.bzw.widget.LineBreakLayout;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2018/8/24.
 * Title：
 * Note：
 */
public class ResumeClassifyActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    LineBreakLayout lineBreakLayout_menuContainer;

    private int mSelectedIndex;
    private SparseArray<Fragment> mCachedFragment = new SparseArray<>();
    private Fragment mCurrentShowFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.account_resume_classify;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.resume_classify);
    }

    @Override
    protected void initWhenCallOnCreate() {
        lineBreakLayout_menuContainer = findViewById(R.id.lbl);
        TextView textView = findViewById(R.id.tv_title);
        textView.setText(R.string.resume_classify);
        addClassifyMenu();
        selectedFragment();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    private void addClassifyMenu() {
        int itemHeight = (int) mResources.getDimension(R.dimen.dp_24);
        String[] itemTitle = mResources.getStringArray(R.array.account_resume_classify_menu_normal);
        LayoutInflater layoutInflater = getLayoutInflater();
        CheckBox checkBox;
        for (int i = 0; i < itemTitle.length; i++) {
            checkBox = (CheckBox) layoutInflater.inflate(R.layout.account_resume_classfy_menu_item, null);
            checkBox.setText(itemTitle[i]);
            checkBox.setChecked((i == mSelectedIndex));
            lineBreakLayout_menuContainer.addView(checkBox, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, itemHeight));
            checkBox.setTag(i);
            checkBox.setOnCheckedChangeListener(this);
        }
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, ResumeClassifyActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int position = (int) buttonView.getTag();
        if (position == mSelectedIndex) {
            buttonView.setOnCheckedChangeListener(null);
            buttonView.setChecked(!isChecked);
            buttonView.setOnCheckedChangeListener(this);
            return;
        }
        mSelectedIndex = position;
        CheckBox checkBox;
        int count = lineBreakLayout_menuContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            checkBox = (CheckBox) lineBreakLayout_menuContainer.getChildAt(i);
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked((i == mSelectedIndex));
            checkBox.setOnCheckedChangeListener(this);
        }
        selectedFragment();

    }

    private void selectedFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (mCurrentShowFragment != null)
            fragmentTransaction.hide(mCurrentShowFragment);
        int type = getSourceTypeByIndex(mSelectedIndex);
        mCurrentShowFragment = mCachedFragment.get(type);
        if (mCurrentShowFragment != null) {
            fragmentTransaction.show(mCurrentShowFragment);
            fragmentTransaction.commit();
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putString("title", ((CheckBox) lineBreakLayout_menuContainer.getChildAt(mSelectedIndex)).getText().toString());
        mCurrentShowFragment = new ResumeClassifyFragment();
        mCachedFragment.put(type, mCurrentShowFragment);
        mCurrentShowFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.fl_container, mCurrentShowFragment).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                finish();
                break;
        }
    }

    private int getSourceTypeByIndex(int index) {
        int type = IResumeClassifyView.TYPE_ALL;
        switch (index) {
            case 1:
                type = IResumeClassifyView.OTHER_SHARE;
                break;
            case 2:
                type = IResumeClassifyView.LIST_RECEIVE;
                break;
//            case 3:
//                type = IResumeClassifyView.LIST_MATCH;
//                break;
            case 3:
                type = IResumeClassifyView.IMPORT_FROM_LOCAL;
                break;
            case 4:
                type = IResumeClassifyView.IMPORT_FROM_OTHER_PLATFORM;
                break;
            case 5:
                type = IResumeClassifyView.DELETE;
                break;
        }
        return type;
    }
}
