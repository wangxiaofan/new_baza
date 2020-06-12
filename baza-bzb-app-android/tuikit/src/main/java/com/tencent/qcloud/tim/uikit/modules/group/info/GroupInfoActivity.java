package com.tencent.qcloud.tim.uikit.modules.group.info;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.tencent.qcloud.tim.uikit.R;


public class GroupInfoActivity extends Activity {

    public boolean isAll = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_info_activity);
        isAll = getIntent().getBooleanExtra("IS_ALL", false);
        GroupInfoFragment fragment = new GroupInfoFragment(isAll);
        fragment.setArguments(getIntent().getExtras());
        getFragmentManager().beginTransaction().replace(R.id.group_manager_base, fragment).commitAllowingStateLoss();
    }

    @Override
    public void finish() {
        super.finish();
        setResult(1001);
    }
}
