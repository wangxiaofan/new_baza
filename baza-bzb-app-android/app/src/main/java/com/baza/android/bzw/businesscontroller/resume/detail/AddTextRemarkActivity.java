package com.baza.android.bzw.businesscontroller.resume.detail;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.bean.resumeelement.RemarkBean;
import com.baza.android.bzw.businesscontroller.resume.detail.adapter.remark.WuxiaoListAdapter;
import com.baza.android.bzw.businesscontroller.resume.detail.presenter.AddTextRemarkPresenter;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IAddTextRemarkView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.widget.LineBreakLayout;
import com.baza.android.bzw.widget.ListViewForScrollView;
import com.bznet.android.rcbox.R;
import com.slib.utils.ScreenUtil;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Vincent.Lei on 2019/2/11.
 * Title：
 * Note：
 */
public class AddTextRemarkActivity extends BaseActivity implements IAddTextRemarkView, View.OnClickListener, CompoundButton.OnCheckedChangeListener, BaseBZWAdapter.IAdapterEventsListener {
    @BindView(R.id.tv_title)
    TextView textView_title;
    @BindView(R.id.tv_update_title)
    TextView textView_updateTitle;
    @BindView(R.id.tv_selection_title_1)
    TextView textView_selectionTitle1;
    @BindView(R.id.tv_selection_title_2)
    TextView textView_selectionTitle2;
    @BindView(R.id.tv_selection_title_3)
    TextView textView_selectionTitle3;

    @BindView(R.id.et_text_value)
    EditText editText_textValue;
    @BindView(R.id.et_company)
    EditText editText_company;
    @BindView(R.id.et_job)
    EditText editText_job;
    @BindView(R.id.lbl_job_hopping)
    LineBreakLayout lbl_job_hopping;
    @BindView(R.id.lbl_hirer_des)
    LineBreakLayout lbl_hirer_des;
    @BindView(R.id.lbl_expect_salary)
    LineBreakLayout lbl_expect_salary;
    @BindView(R.id.sc)
    ScrollView scrollView;
    CheckBox checkBox_customerSalary;

    @BindView(R.id.iv_youxiao)
    ImageView ivYouxiao;
    @BindView(R.id.ll_youxiao)
    LinearLayout llYouxiao;
    @BindView(R.id.iv_wuxiao)
    ImageView ivWuxiao;
    @BindView(R.id.ll_wuxiao)
    LinearLayout llWuxiao;
    @BindView(R.id.ll_select_view)
    LinearLayout llSelectView;
    @BindView(R.id.ll_content_youxiao)
    LinearLayout llContentYouxiao;
    @BindView(R.id.lv_wuxiao_list)
    ListViewForScrollView lvWuxiaoList;
    @BindView(R.id.ll_content_wuxiao)
    LinearLayout llContentWuxiao;

    private AddTextRemarkPresenter mPresenter;
    private int mChildItemHeight;
    private int mChildPaddingLR;
    private int mTextSize;
    private ResumeBean mResumeBean;
    private RemarkBean mOldRemarkBean;
    private String[] mSalarySelections;

    private String[] contents;
    private boolean isCompany;
    private int wuxiaoPosition = -1;
    private WuxiaoListAdapter wuxiaoListAdapter;
    private int flag = 1;//1-有效  0-无效

    @Override
    protected int getLayoutId() {
        return R.layout.resume_detail_activity_add_text_remark;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.add_a_new_remark);
    }

    @Override
    protected void initOverAll() {
        mResumeBean = (ResumeBean) getIntent().getSerializableExtra("resumeBean");
        mOldRemarkBean = (RemarkBean) getIntent().getSerializableExtra("oldRemarkBean");
        isCompany = getIntent().getBooleanExtra("isCompany", false);
    }

    @Override
    protected void initWhenCallOnCreate() {
        mPresenter = new AddTextRemarkPresenter(this, mResumeBean, mOldRemarkBean);
        this.mChildItemHeight = (int) mResources.getDimension(R.dimen.dp_30);
        this.mChildPaddingLR = (int) mResources.getDimension(R.dimen.dp_5);
        this.mTextSize = ScreenUtil.px2dip(mResources.getDimension(R.dimen.text_size_12));
        ButterKnife.bind(this);
        textView_title.setText(mOldRemarkBean != null ? R.string.modify_remark_title : R.string.add_remark_title);

        String[] values = mResources.getStringArray(R.array.remark_text_label_job_hopping);
        addSelectionLabels(lbl_job_hopping, values, (mOldRemarkBean != null ? mOldRemarkBean.jobHoppingOccasion : null));
        values = mResources.getStringArray(R.array.remark_text_label_hirer_des);
        addSelectionLabels(lbl_hirer_des, values, (mOldRemarkBean != null ? mOldRemarkBean.employerInfo : null));
        mSalarySelections = mResources.getStringArray(R.array.remark_text_label_expect_salary);
        addSelectionLabels(lbl_expect_salary, mSalarySelections, (mOldRemarkBean != null ? mOldRemarkBean.expectSalary : null));
        checkBox_customerSalary = (CheckBox) lbl_expect_salary.getChildAt(lbl_expect_salary.getChildCount() - 1);
        if (mOldRemarkBean != null && !TextUtils.isEmpty(mOldRemarkBean.expectSalary) && !isStandSalary(mOldRemarkBean.expectSalary)) {
            checkBox_customerSalary.setOnCheckedChangeListener(null);
            checkBox_customerSalary.setText(mOldRemarkBean.expectSalary);
            checkBox_customerSalary.setChecked(true);
            checkBox_customerSalary.setOnCheckedChangeListener(this);
        }

        if (mOldRemarkBean != null)
            editText_textValue.setText(mOldRemarkBean.content);
        mPresenter.initialize();

        //处理企业寻访
        if (isCompany) {
            llSelectView.setVisibility(View.VISIBLE);
            contents = mResources.getStringArray(R.array.wuxiao_remark);
            wuxiaoListAdapter = new WuxiaoListAdapter(this, contents, this);
            lvWuxiaoList.setAdapter(wuxiaoListAdapter);
        } else {
            llSelectView.setVisibility(View.GONE);
            llContentWuxiao.setVisibility(View.GONE);
        }
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {
//        AndroidBug5497Workaround.assistActivity(this, statusBarHeight);
    }

    private boolean isStandSalary(String salary) {
        for (int i = 0; i < mSalarySelections.length - 1; i++) {
            if (mSalarySelections[i].equals(salary)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_submit:
                mPresenter.submit();
                break;
        }
    }

    private void addSelectionLabels(LineBreakLayout lineBreakLayout, String[] values, String oldValue) {
        CheckBox checkBox;
        for (int i = 0; i < values.length; i++) {
            checkBox = createChildItem();
            checkBox.setText(values[i]);
            checkBox.setChecked(values[i].equals(oldValue));
            checkBox.setOnCheckedChangeListener(this);
            lineBreakLayout.addView(checkBox);
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
        checkBox.setTextColor(mResources.getColorStateList(R.drawable.text_remark_label_cb_text_color));
        return checkBox;
    }

    public static void launch(Activity activity, ResumeBean resumeBean, RemarkBean oldRemarkBean, int requestCode, boolean isCompany) {
        Intent intent = new Intent(activity, AddTextRemarkActivity.class);
        ResumeBean resume = new ResumeBean();
        resume.copyFromOld(resumeBean);
        intent.putExtra("resumeBean", resume);
        intent.putExtra("isCompany", isCompany);
        if (oldRemarkBean != null) {
            intent.putExtra("oldRemarkBean", oldRemarkBean);
        }
        if (requestCode <= 0)
            activity.startActivity(intent);
        else
            activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isChecked)
            return;
        ViewGroup viewGroup = (ViewGroup) buttonView.getParent();
        int childCount = viewGroup.getChildCount();
        CheckBox checkBox;
        for (int i = 0; i < childCount; i++) {
            checkBox = (CheckBox) viewGroup.getChildAt(i);
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(buttonView == checkBox);
            checkBox.setOnCheckedChangeListener(this);
        }

        if (buttonView == checkBox_customerSalary) {
            //自定义薪资输入
            showCustomerSalaryDialog();
        }
    }

    private void showCustomerSalaryDialog() {
        String customerSalary = checkBox_customerSalary.getText().toString();
        CustomerSalaryDialog customerSalaryDialog = new CustomerSalaryDialog(this, (mResources.getString(R.string.customer_salary).equals(customerSalary) ? null : customerSalary),
                new CustomerSalaryDialog.ICustomerSalaryListener() {
                    @Override
                    public void onSubmit(String salary) {
                        checkBox_customerSalary.setText(salary);
                    }

                    @Override
                    public boolean onCheck(String salary) {
                        if (TextUtils.isEmpty(salary) || (salary.equals(mResources.getString(R.string.customer_salary)))) {
                            callShowToastMessage(null, R.string.input_customer_expert_salary);
                            return false;
                        }
                        if (isStandSalary(salary)) {
                            callShowToastMessage(null, R.string.expert_salary_selection_exit);
                            return false;
                        }
                        return true;
                    }

                    @Override
                    public void onDismiss() {
                        if (checkBox_customerSalary.getText().toString().equals(mResources.getString(R.string.customer_salary))) {
                            checkBox_customerSalary.setOnCheckedChangeListener(null);
                            checkBox_customerSalary.setChecked(false);
                            checkBox_customerSalary.setOnCheckedChangeListener(AddTextRemarkActivity.this);
                        }
                        editText_textValue.clearFocus();
                        editText_company.clearFocus();
                        editText_job.clearFocus();
                        scrollView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        }, 200);
                    }
                });
        customerSalaryDialog.show();
    }

    @Override
    public void callShowMoreRemarkSelectionView() {
        editText_company.setText(mResumeBean.company);
        editText_job.setText(mResumeBean.title);


        textView_updateTitle.setVisibility(View.VISIBLE);
        editText_company.setVisibility(View.VISIBLE);
        editText_job.setVisibility(View.VISIBLE);
//        textView_selectionTitle1.setVisibility(View.VISIBLE);
//        textView_selectionTitle2.setVisibility(View.VISIBLE);
//        textView_selectionTitle3.setVisibility(View.VISIBLE);
//        lbl_job_hopping.setVisibility(View.VISIBLE);
//        lbl_hirer_des.setVisibility(View.VISIBLE);
//        lbl_expect_salary.setVisibility(View.VISIBLE);
    }

    @Override
    public String callGetTextRemark() {
        return editText_textValue.getText().toString().trim();
    }

    @Override
    public String callGetCompany() {
        return editText_company.getText().toString().trim();
    }

    @Override
    public String callGetJob() {
        return editText_job.getText().toString().trim();
    }

    @Override
    public String callGetJobHoping() {
        int childCount = lbl_job_hopping.getChildCount();
        CheckBox checkBox;
        for (int i = 0; i < childCount; i++) {
            checkBox = (CheckBox) lbl_job_hopping.getChildAt(i);
            if (checkBox.isChecked())
                return checkBox.getText().toString();
        }
        return null;
    }

    @Override
    public String callGetHirerDes() {
        int childCount = lbl_hirer_des.getChildCount();
        CheckBox checkBox;
        for (int i = 0; i < childCount; i++) {
            checkBox = (CheckBox) lbl_hirer_des.getChildAt(i);
            if (checkBox.isChecked())
                return checkBox.getText().toString();
        }
        return null;
    }

    @Override
    public String callGetExpectSalary() {
        int childCount = lbl_expect_salary.getChildCount();
        CheckBox checkBox;
        for (int i = 0; i < childCount; i++) {
            checkBox = (CheckBox) lbl_expect_salary.getChildAt(i);
            if (checkBox.isChecked())
                return checkBox.getText().toString();
        }
        return null;
    }

    @Override
    public String callGetContent() {
        if (wuxiaoPosition < 0) return "";
        return contents[wuxiaoPosition];
    }

    @Override
    public int callGetFlag() {
        return flag;
    }

    @Override
    public void callUpdateRemark(RemarkBean remarkBean) {

    }

    @OnClick({R.id.ll_youxiao, R.id.ll_wuxiao})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_youxiao:
                flag = 1;
                llContentYouxiao.setVisibility(View.VISIBLE);
                llContentWuxiao.setVisibility(View.GONE);
                ivYouxiao.setBackgroundResource(R.drawable.btn_yixuanze);
                ivWuxiao.setBackgroundResource(R.drawable.btn_weixuanze);
                break;
            case R.id.ll_wuxiao:
                flag = 0;
                llContentYouxiao.setVisibility(View.GONE);
                llContentWuxiao.setVisibility(View.VISIBLE);
                ivYouxiao.setBackgroundResource(R.drawable.btn_weixuanze);
                ivWuxiao.setBackgroundResource(R.drawable.btn_yixuanze);
                break;
        }
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
        switch (adapterEventId) {
            case AdapterEventIdConst.WUXIAO_REMARK_CLICK:
                wuxiaoPosition = position;
                wuxiaoListAdapter.setPosition(position);
                break;
        }
    }
}
