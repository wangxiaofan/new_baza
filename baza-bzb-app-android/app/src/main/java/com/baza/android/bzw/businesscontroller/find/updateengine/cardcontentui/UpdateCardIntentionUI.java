package com.baza.android.bzw.businesscontroller.find.updateengine.cardcontentui;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.bean.resume.ResumeUpdatedContentResultBean;
import com.baza.android.bzw.bean.resumeelement.IntentionBean;
import com.baza.android.bzw.bean.resumeelement.IntentionUnion;
import com.baza.android.bzw.businesscontroller.find.updateengine.presenter.UpdateContentCardPresenter;
import com.baza.android.bzw.businesscontroller.find.updateengine.viewinterface.IUpdateContentCardView;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/8/25.
 * Title：
 * Note：
 */

public class UpdateCardIntentionUI implements CompoundButton.OnCheckedChangeListener {
    View mRootView;
    @BindView(R.id.cb_check_all)
    CheckBox checkBox_all;
    @BindView(R.id.cb_check_job)
    CheckBox checkBox_job;
    @BindView(R.id.cb_check_city)
    CheckBox checkBox_city;
    @BindView(R.id.cb_check_salary)
    CheckBox checkBox_salary;
    @BindView(R.id.tv_intention_job)
    TextView textView_job;
    @BindView(R.id.tv_intention_city)
    TextView textView_city;
    @BindView(R.id.tv_intention_salary)
    TextView textView_salary;
    @BindView(R.id.tv_no_data)
    TextView textView_noData;
    @BindView(R.id.fl_intention_job)
    View view_job;
    @BindView(R.id.fl_intention_city)
    View view_city;
    @BindView(R.id.fl_intention_salary)
    View view_salary;

    private IUpdateContentCardView mUpdateContentCardView;
    private UpdateContentCardPresenter mPresenter;
    private boolean mHasInit;

    public UpdateCardIntentionUI(IUpdateContentCardView updateContentCardView, UpdateContentCardPresenter presenter) {
        this.mUpdateContentCardView = updateContentCardView;
        this.mPresenter = presenter;
        init();
    }

    public void init() {
        mRootView = getBindActivity().getLayoutInflater().inflate(R.layout.layout_candidate_update_content_intentions_ui, null);
        ButterKnife.bind(this, mRootView);
        checkBox_all.setOnCheckedChangeListener(this);
        checkBox_job.setOnCheckedChangeListener(this);
        checkBox_city.setOnCheckedChangeListener(this);
        checkBox_salary.setOnCheckedChangeListener(this);
    }

    public View getView() {
        return mRootView;
    }

    private BaseActivity getBindActivity() {
        return mUpdateContentCardView.callGetBindActivity();
    }

    protected ResumeUpdatedContentResultBean.Data getData() {
        return mPresenter.getEnableUpdateContentData();
    }

    public void updateViews(IntentionUnion intentionUnion) {
        if (intentionUnion != null) {
            if (intentionUnion.title != null) {
                textView_job.setText(getBindActivity().callGetResources().getString(R.string.intentions_job, intentionUnion.title));
                view_job.setVisibility(View.VISIBLE);
            } else
                view_job.setVisibility(View.GONE);
            if (intentionUnion.city != null) {
                textView_city.setText(getBindActivity().callGetResources().getString(R.string.intentions_city, intentionUnion.city));
                view_city.setVisibility(View.VISIBLE);
            } else
                view_city.setVisibility(View.GONE);
            if (intentionUnion.maxSalary > 0) {
                textView_salary.setText(getBindActivity().callGetResources().getString(R.string.intentions_salary, intentionUnion.minSalary + "-" + intentionUnion.maxSalary));
                view_salary.setVisibility(View.VISIBLE);
            } else
                view_salary.setVisibility(View.GONE);
            textView_noData.setVisibility(View.GONE);
            checkBox_all.setVisibility(View.VISIBLE);
            mRootView.setVisibility(View.VISIBLE);
            if (!mHasInit) {
                mHasInit = true;
                checkBox_all.setChecked(true);
            }
            return;
        }
        if (!mPresenter.isJustShowEnableUpdateContentMode()) {
            view_job.setVisibility(View.GONE);
            view_city.setVisibility(View.GONE);
            view_salary.setVisibility(View.GONE);
            checkBox_all.setVisibility(View.GONE);
            textView_noData.setVisibility(View.VISIBLE);
            mRootView.setVisibility(View.VISIBLE);
            return;
        }
        mRootView.setVisibility(View.GONE);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == checkBox_all) {
            checkBox_job.setOnCheckedChangeListener(null);
            checkBox_city.setOnCheckedChangeListener(null);
            checkBox_salary.setOnCheckedChangeListener(null);
            checkBox_job.setChecked(isChecked);
            checkBox_city.setChecked(isChecked);
            checkBox_salary.setChecked(isChecked);
            checkBox_job.setOnCheckedChangeListener(this);
            checkBox_city.setOnCheckedChangeListener(this);
            checkBox_salary.setOnCheckedChangeListener(this);
        } else {
            checkBox_all.setOnCheckedChangeListener(null);
            checkBox_all.setChecked(isSelectedAll());
            checkBox_all.setOnCheckedChangeListener(this);
        }
    }

    private boolean isSelectedAll() {
        if (view_job.getVisibility() == View.VISIBLE && !checkBox_job.isChecked())
            return false;
        if (view_city.getVisibility() == View.VISIBLE && !checkBox_city.isChecked())
            return false;
        return view_salary.getVisibility() != View.VISIBLE || checkBox_salary.isChecked();
    }

    public List<IntentionBean> getUpdateContentParam() {
        if (!checkBox_job.isChecked() && !checkBox_city.isChecked() && !checkBox_salary.isChecked())
            return null;
        ResumeUpdatedContentResultBean.Data data = getData();
        if (data == null || data.target == null || data.target.intentions == null)
            return null;
        List<IntentionBean> list = new ArrayList<>(data.target.intentions.size());
        if (checkBox_all.isChecked()) {
            list.addAll(data.target.intentions);
            return list;
        }
        IntentionBean bean;
        IntentionBean update;
        for (int i = 0, size = data.target.intentions.size(); i < size; i++) {
            bean = data.target.intentions.get(i);
            update = new IntentionBean();
            update.id = bean.id;
            if (checkBox_job.isChecked())
                update.title = bean.title;
            if (checkBox_city.isChecked())
                update.locationId = bean.locationId;
            if (checkBox_salary.isChecked()) {
                update.minSalary = bean.minSalary;
                update.maxSalary = bean.maxSalary;
            }
            list.add(update);
        }
        return list;
    }
}
