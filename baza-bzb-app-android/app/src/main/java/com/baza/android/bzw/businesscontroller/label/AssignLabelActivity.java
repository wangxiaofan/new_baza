package com.baza.android.bzw.businesscontroller.label;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.bean.label.Label;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.businesscontroller.label.presenter.AssignLabelPresenter;
import com.baza.android.bzw.businesscontroller.label.viewinterface.IAssignLabelView;
import com.baza.android.bzw.widget.LineBreakLayout;
import com.baza.android.bzw.widget.dialog.AddLabelDialog;
import com.baza.android.bzw.widget.dialog.SimpleHintDialog;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

import java.lang.reflect.Field;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/5/25.
 * Title：
 * Note：
 */

public class AssignLabelActivity extends BaseActivity implements IAssignLabelView, View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    @BindView(R.id.tv_title)
    TextView textView_title;
    @BindView(R.id.lbl_all_container)
    LineBreakLayout lineBreakLayout_libraryContainer;

    private AssignLabelPresenter mPresenter;
    private int mChildItemHeight;
    private int mChildPaddingLR;
    private int mTextSize;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_assign_label;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_assign_label);
    }

    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        this.mChildItemHeight = (int) mResources.getDimension(R.dimen.dp_30);
        this.mChildPaddingLR = (int) mResources.getDimension(R.dimen.dp_5);
        this.mTextSize = ScreenUtil.px2dip(mResources.getDimension(R.dimen.text_size_12));
        textView_title.setText(R.string.label_library);
        mPresenter = new AssignLabelPresenter(this, getIntent());
        mPresenter.initialize();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                onBackPressed();
                break;
            case R.id.btn_submit:
                mPresenter.saveLabelForResume();
                break;
            case R.id.btn_add:
                new AddLabelDialog(this, new AddLabelDialog.IAddLabelListener() {
                    @Override
                    public void onSubmit(String labelText) {
                        mPresenter.createNewTag(labelText);
                    }

                    @Override
                    public boolean onCheck(String labelText) {
                        return (!TextUtils.isEmpty(labelText) && mPresenter.checkEnableToCreateNewTag(labelText));
                    }
                });
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mPresenter.shouldHintSave()) {
            new SimpleHintDialog(this).show(mResources.getString(R.string.label_change_is_save), R.drawable.icon_arrow_save, R.string.save, R.string.not_save, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.saveLabelForResume();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            return;
        }
        super.onBackPressed();
    }

    public static void launch(Activity activity, ResumeBean resumeBean) {
        Intent intent = new Intent(activity, AssignLabelActivity.class);
        intent.putExtra("resumeBean", resumeBean);
        activity.startActivity(intent);
    }

    @Override
    public void callSetLabelLibrary(List<Label> labelList) {
        int currentCount = lineBreakLayout_libraryContainer.getChildCount();
        int needShowCount = labelList.size();
        CheckBox checkBox;
        boolean hasCacheView;
        Label label;
        for (int position = 0; position < needShowCount; position++) {
            label = labelList.get(position);
            hasCacheView = (position < currentCount);
            checkBox = (hasCacheView ? (CheckBox) lineBreakLayout_libraryContainer.getChildAt(position) : createChildItem());
            checkBox.setText(label.tag);
            checkBox.setTag(label);
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(mPresenter.isLabelInAssignedList(label.tag));
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setOnCheckedChangeListener(this);
            if (!hasCacheView)
                lineBreakLayout_libraryContainer.addView(checkBox);
        }
        if (currentCount > needShowCount)
            for (; needShowCount < currentCount; needShowCount++) {
                lineBreakLayout_libraryContainer.getChildAt(needShowCount).setVisibility(View.GONE);
            }
    }


    @SuppressLint("ResourceType")
    private CheckBox createChildItem() {
        CheckBox checkBox = new CheckBox(this);
        checkBox.setBackgroundResource(R.drawable.login_interested_talent_type_cb_bg);
        checkBox.setGravity(Gravity.CENTER);
        checkBox.setMaxLines(1);
        checkBox.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, mChildItemHeight));
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            try {
                Field field = checkBox.getClass().getSuperclass().getDeclaredField("mButtonDrawable");
                field.setAccessible(true);
                field.set(checkBox, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            checkBox.setButtonDrawable(null);
        checkBox.setEllipsize(TextUtils.TruncateAt.END);
        checkBox.setPadding(mChildPaddingLR, 0, mChildPaddingLR, 0);
        checkBox.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mTextSize);
        checkBox.setTextColor(mResources.getColorStateList(R.drawable.login_talent_type_cb_text_color));
        return checkBox;
    }

    @Override
    public void callExists() {
        finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        buttonView.setOnCheckedChangeListener(null);
        if (isChecked)
            buttonView.setChecked(mPresenter.tryToAddLabel((Label) buttonView.getTag()));
        else {
            mPresenter.tryToDeleteLabel((Label) buttonView.getTag());
            buttonView.setChecked(false);
        }

        buttonView.setOnCheckedChangeListener(this);
    }
}
