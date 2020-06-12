package com.baza.android.bzw.businesscontroller.resume.create;

import android.app.Activity;
import android.content.Intent;
import android.text.InputFilter;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.bean.common.LocalAreaBean;
import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.businesscontroller.resume.create.presenter.CreateResumePresenter;
import com.baza.android.bzw.businesscontroller.resume.create.viewinterface.ICreateResumeView;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.extra.AndroidBug5497Workaround;
import com.baza.android.bzw.manager.AddressManager;
import com.baza.android.bzw.widget.ClearEditText;
import com.baza.android.bzw.widget.dialog.PickAddressDialog;
import com.baza.android.bzw.widget.dialog.SingleWheelChoseDialog;
import com.bznet.android.rcbox.R;
import com.slib.utils.AppUtil;
import com.slib.utils.DialogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/5/31.
 * Title：创建简历
 * Note：
 */

public class CreateResumeActivity extends BaseActivity implements ICreateResumeView, View.OnClickListener {
    @BindView(R.id.tv_title)
    TextView textView_title;
    @BindView(R.id.ll_item_container)
    LinearLayout linearLayout_itemContainer;
    private ClearEditText editText_name;
    private ClearEditText editText_company;
    private ClearEditText editText_position;
    private ClearEditText editText_phone;
    private ClearEditText editText_sex;
    private ClearEditText editText_workYears;
    private ClearEditText editText_city;
    private ClearEditText editText_degree;
    private ClearEditText editText_email;
    private View.OnTouchListener mOnTouchListener;

    private CreateResumePresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_candidate;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_create_candidate);
    }

    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        mOnTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    hideSoftInput();
                    showSelectedOptionsDialog(v);
                }
                return true;
            }
        };


        mPresenter = new CreateResumePresenter(this, getIntent());
        mPresenter.initialize();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {
        AndroidBug5497Workaround.assistActivity(this, mStatusBarHeight);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                onBackPressed();
                break;
            case R.id.btn_submit:
                // 保存
                hideSoftInput();
                mPresenter.save();
                break;
        }
    }

    public static void launch(Activity activity, ResumeBean candidateBeEdited, String pageCode) {
        Intent intent = new Intent(activity, CreateResumeActivity.class);
        if (candidateBeEdited != null)
            intent.putExtra("candidateBeEdited", candidateBeEdited);
        intent.putExtra("pageCode", pageCode);
        activity.startActivity(intent);
    }

    public static void launch(Activity activity, ResumeBean candidateBeEdited, boolean isFirm, String pageCode) {
        Intent intent = new Intent(activity, CreateResumeActivity.class);
        if (candidateBeEdited != null)
            intent.putExtra("candidateBeEdited", candidateBeEdited);
        intent.putExtra("isFirm", isFirm);
        intent.putExtra("pageCode", pageCode);
        activity.startActivity(intent);
    }

    @Override
    public void callSetUpItemViews() {
        String[] itemNames = mResources.getStringArray(R.array.ce_self_candidate_item_names);
        String[] itemHints = mResources.getStringArray(R.array.ce_self_candidate_item_hints);
        int[] itemIds = AppUtil.getIntegerIdList(this, R.array.resume_self_built_ids);
        LinearLayout linearLayout_item;
        for (int i = 0, length = itemNames.length; i < length; i++) {
            linearLayout_item = (LinearLayout) getLayoutInflater().inflate(R.layout.item_create_new_candidate, null);
            ((TextView) linearLayout_item.getChildAt(0)).setText(itemNames[i]);
            ClearEditText editText = (ClearEditText) linearLayout_item.getChildAt(1);
            editText.setId(itemIds[i]);
            editText.setHint(itemHints[i]);
            attachEditText(editText, itemIds[i]);
            if (mPresenter.isFirm() && itemIds[i] == R.id.resume_item_id_contact && mPresenter.getBeEditedData().mobileStatus != 3) {
                continue;
            }
            if (mPresenter.isFirm() && itemIds[i] == R.id.resume_item_id_email && mPresenter.getBeEditedData().emailStatus != 3) {
                continue;
            }
            linearLayout_itemContainer.addView(linearLayout_item);
        }
    }

    @Override
    public void callSetMode(boolean isEdited) {
        textView_title.setText((isEdited ? R.string.edit_new_candidate : R.string.create_new_candidate));
        if (!isEdited)
            return;
        ResumeBean mResumeBeEdited = mPresenter.getBeEditedData();
        if (mResumeBeEdited == null)
            return;
        editText_name.setText(mResumeBeEdited.realName);
        editText_company.setText(mResumeBeEdited.company);
        editText_position.setText(mResumeBeEdited.title);
        editText_phone.setText(mResumeBeEdited.mobile);
        editText_email.setText(mResumeBeEdited.email);
        editText_workYears.setText((mResumeBeEdited.yearExpr <= CommonConst.WORK_YEARS_TOP_LEVEL ? mResources.getString(R.string.work_years_with_tag, String.valueOf(mResumeBeEdited.yearExpr)) : mResources.getString(R.string.more_than_twenty_years)));
        //-1不详
        //1--高中及以下  senior schoolName; 2--大专  associate; 3--本科  bachelor; 4--硕士  master; 5--MBA(工商管理硕士) ; 6--博士  doctor
        int degreePosition = mResumeBeEdited.degree - 1;
        String[] options_degree = mPresenter.getOptionsDegree();
        editText_degree.setText((degreePosition >= options_degree.length || degreePosition < 0 ? mResources.getString(R.string.message_unknown) : options_degree[degreePosition]));
        editText_sex.setText((CommonConst.SEX_MALE == mResumeBeEdited.gender) ? R.string.male : ((CommonConst.SEX_FEMALE == mResumeBeEdited.gender)) ? R.string.female : R.string.message_unknown);
        //城市信息
        editText_city.setText(AddressManager.getInstance().getCityNameByCode(mResumeBeEdited.location));
    }

    @Override
    public String callGetName() {
        return editText_name.getText().toString().trim();
    }

    @Override
    public String callGetMobile() {
        return editText_phone.getText().toString().trim();
    }

    @Override
    public String callGetEmail() {
        return editText_email.getText().toString().trim();
    }

    @Override
    public String callGetCompany() {
        return editText_company.getText().toString().trim();
    }

    @Override
    public String callGetTitle() {
        return editText_position.getText().toString().trim();
    }


    private void attachEditText(ClearEditText editText, int itemId) {
        switch (itemId) {
            case R.id.resume_item_id_name:
                editText_name = editText;
                editText_name.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
                break;
            case R.id.resume_item_id_sex:
                //性别
                editText_sex = editText;
                editText_sex.setOnTouchListener(mOnTouchListener);
                editText_sex.setDeleteIcon(R.drawable.dropdown_arrow);
                editText_sex.setText(R.string.message_unknown);
                break;
            case R.id.resume_item_id_workYear:
                //工作年限
                editText_workYears = editText;
                editText_workYears.setOnTouchListener(mOnTouchListener);
                editText_workYears.setDeleteIcon(R.drawable.dropdown_arrow);
                break;
            case R.id.resume_item_id_city:
                //城市
                editText_city = editText;
                editText_city.setOnTouchListener(mOnTouchListener);
                editText_city.setDeleteIcon(R.drawable.dropdown_arrow);
                break;
            case R.id.resume_item_id_degree:
                //学历
                editText_degree = editText;
                editText_degree.setOnTouchListener(mOnTouchListener);
                editText_degree.setDeleteIcon(R.drawable.dropdown_arrow);
                break;
            case R.id.resume_item_id_company:
                //公司
                editText_company = editText;
                editText_company.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
                break;
            case R.id.resume_item_id_job:
                //职位
                editText_position = editText;
                editText_position.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
                break;
            case R.id.resume_item_id_contact:
                //手机号只能输入数字  长度11
                editText_phone = editText;
                editText_phone.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText_phone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

                break;
            case R.id.resume_item_id_email:
                //邮箱
                editText_email = editText;
                editText_email.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mPresenter.shouldRemindSave()) {
            //提醒保存
            DialogUtil.doubleButtonShow(this, 0, R.string.candidate_changed_but_not_save, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            }, null);
            return;
        }
        super.onBackPressed();
    }

    /**
     * 显示选项对话框
     */
    private void showSelectedOptionsDialog(final View v) {
        if (v.getId() == R.id.resume_item_id_city) {
            //城市
            new PickAddressDialog(this, new PickAddressDialog.IAddressSelectedListener() {
                @Override
                public void onAddressSelected(LocalAreaBean province, LocalAreaBean city) {
                    StringBuilder provinceAndCityMsg = new StringBuilder(province.name).append("·").append(city.name);
                    editText_city.setText(provinceAndCityMsg.toString());
                    try {
                        mPresenter.setCity(province.code, city.code);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).show();
            return;
        }

        new SingleWheelChoseDialog<>(this, getFiledValues(v), new SingleWheelChoseDialog.IOptionSelectedListener<String>() {
            @Override
            public void onOptionSelected(int position, String s) {
                setSelectedFiledValue(v, position, s);
            }
        }).show();
    }


    private String[] getFiledValues(View v) {
        String[] values = null;
        switch (v.getId()) {
            case R.id.resume_item_id_sex:
                values = mPresenter.getOptionsSex();
                break;
            case R.id.resume_item_id_degree:
                values = mPresenter.getOptionsDegree();
                break;
            case R.id.resume_item_id_workYear:
                values = mPresenter.getOptionsWorkYears();
                break;
        }
        return values;
    }

    private void setSelectedFiledValue(View v, int position, String s) {
        switch (v.getId()) {
            case R.id.resume_item_id_sex:
                editText_sex.setText(s);
                mPresenter.setSex((position == 0 ? CommonConst.SEX_UNKNOWN : (position == 1 ? CommonConst.SEX_MALE : CommonConst.SEX_FEMALE)));
                break;
            case R.id.resume_item_id_degree:
                mPresenter.setDegree(position + 1);
                editText_degree.setText(s);
                break;
            case R.id.resume_item_id_workYear:
                mPresenter.setWorkYears(position);
                //现在大于20年的全部归类于20年以上的选项 position= 21就是这个选项，最后一个选项
                editText_workYears.setText(((position != mPresenter.getOptionsWorkYears().length - 1) ? mResources.getString(R.string.work_years_with_tag, String.valueOf(position)) : mResources.getString(R.string.more_than_twenty_years)));
                break;
        }
    }
}
