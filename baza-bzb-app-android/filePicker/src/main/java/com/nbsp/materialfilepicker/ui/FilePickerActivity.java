package com.nbsp.materialfilepicker.ui;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.fragment.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.nbsp.materialfilepicker.R;
import com.nbsp.materialfilepicker.filter.CompositeFilter;
import com.nbsp.materialfilepicker.filter.PatternFilter;
import com.nbsp.materialfilepicker.utils.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class FilePickerActivity extends FragmentActivity implements DirectoryFragment.FileClickListener, View.OnClickListener {
    public static final String ARG_START_PATH = "arg_start_path";
    public static final String ARG_CURRENT_PATH = "arg_current_path";

    public static final String ARG_FILTER = "arg_filter";

    public static final String STATE_START_PATH = "state_start_path";
    private static final String STATE_CURRENT_PATH = "state_current_path";

    public static final String RESULT_FILE_PATH = "result_file_path";
    private static final int HANDLE_CLICK_DELAY = 150;

    private String mStartPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String mCurrentPath = mStartPath;

    private CompositeFilter mFilter;
    private TextView textView_currentPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picker);
        TextView textView = findViewById(R.id.tv_title);
        textView.setText(R.string.title_file_picker);
        textView_currentPath = findViewById(R.id.tv_current_path);
        initArguments();
        changedSelfDefineUIToFitSDKReachKITKAT();
        if (savedInstanceState != null) {
            mStartPath = savedInstanceState.getString(STATE_START_PATH);
            mCurrentPath = savedInstanceState.getString(STATE_CURRENT_PATH);
        } else {
            initFragment();
        }
        textView_currentPath.setText(mCurrentPath);
    }

    private void changedSelfDefineUIToFitSDKReachKITKAT() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            View view = findViewById(R.id.view_fit_status);
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            lp.height = getStatusBarHeight();
            view.setLayoutParams(lp);
            view.setVisibility(View.VISIBLE);
        }
    }

    protected int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
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


    @SuppressWarnings("unchecked")
    private void initArguments() {
        if (getIntent().hasExtra(ARG_FILTER)) {
            Serializable filter = getIntent().getSerializableExtra(ARG_FILTER);

            if (filter instanceof Pattern) {
                ArrayList<FileFilter> filters = new ArrayList<>();
                filters.add(new PatternFilter((Pattern) filter, false));
                mFilter = new CompositeFilter(filters);
            } else {
                mFilter = (CompositeFilter) filter;
            }
        }

        if (getIntent().hasExtra(ARG_START_PATH)) {
            mStartPath = getIntent().getStringExtra(ARG_START_PATH);
            mCurrentPath = mStartPath;
        }

        if (getIntent().hasExtra(ARG_CURRENT_PATH)) {
            String currentPath = getIntent().getStringExtra(ARG_CURRENT_PATH);
            if (currentPath.startsWith(mStartPath)) {
                mCurrentPath = currentPath;
            }
        }
    }

    private void initFragment() {
        getFragmentManager().beginTransaction()
                .add(R.id.container, DirectoryFragment.getInstance(
                        mCurrentPath, mFilter))
                .commit();
    }

    private void updateTitle() {
        textView_currentPath.setText(mCurrentPath);
    }

    private void addFragmentToBackStack(String path) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, DirectoryFragment.getInstance(
                        path, mFilter))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        mCurrentPath = FileUtils.cutLastSegmentOfPath(mCurrentPath);
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            updateTitle();
        } else if (mCurrentPath != null && mCurrentPath.startsWith(mStartPath)) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, DirectoryFragment.getInstance(
                            mCurrentPath, mFilter))
                    .commit();
            updateTitle();

        } else {
            setResult(RESULT_CANCELED);
            super.onBackPressed();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_CURRENT_PATH, mCurrentPath);
        outState.putString(STATE_START_PATH, mStartPath);
    }

    @Override
    public void onFileClicked(final File clickedFile) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                handleFileClicked(clickedFile);
            }
        }, HANDLE_CLICK_DELAY);
    }

    private void handleFileClicked(final File clickedFile) {
        if (clickedFile.isDirectory()) {
            addFragmentToBackStack(clickedFile.getPath());
            mCurrentPath = clickedFile.getPath();
            updateTitle();
        } else {
            setResultAndFinish(clickedFile.getPath());
        }
    }

    private void setResultAndFinish(String filePath) {
        Intent data = new Intent();
        data.putExtra(RESULT_FILE_PATH, filePath);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ibtn_left_click) {
            onBackPressed();
        } else if (v.getId() == R.id.tv_right_click) {
            finish();
        }
    }
}
