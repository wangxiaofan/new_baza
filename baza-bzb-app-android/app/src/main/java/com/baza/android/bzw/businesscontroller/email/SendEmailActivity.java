package com.baza.android.bzw.businesscontroller.email;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.bean.email.EmailAttachmentBean;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.businesscontroller.email.adapter.EmailAttachmentAdapter;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.businesscontroller.email.presenter.SendEmailPresenter;
import com.baza.android.bzw.businesscontroller.email.viewinterface.ISendEmailView;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.RequestCodeConst;
import com.baza.android.bzw.widget.ClearEditText;
import com.baza.android.bzw.widget.NOScrollGridView;
import com.baza.android.bzw.widget.TopTipUI;
import com.baza.android.bzw.widget.dialog.SimpleHintDialog;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;
import com.nbsp.materialfilepicker.MaterialFilePicker;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/8/1.
 * Title：发邮件
 * Note：
 */

public class SendEmailActivity extends BaseActivity implements ISendEmailView, View.OnClickListener, BaseBZWAdapter.IAdapterEventsListener {
    @BindView(R.id.tv_title)
    TextView textView_title;
    @BindView(R.id.tv_right_click)
    TextView textView_right;
    @BindView(R.id.et_email)
    ClearEditText clearEditText_assigner;
    @BindView(R.id.et_input_email_title)
    ClearEditText clearEditText_emailTitle;
    @BindView(R.id.et_input_email_content)
    EditText editText_content;
    @BindView(R.id.cb_attachment_count)
    CheckBox checkBox_attachmentCount;
    @BindView(R.id.gv_attachment)
    NOScrollGridView gridView_attachment;

    private SendEmailPresenter mPresenter;
    private EmailAttachmentAdapter mAdapter;
    private TopTipUI mTopTipUI;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_send_email;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_write_email);
    }

    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        textView_title.setText(R.string.write_email);
        textView_right.setText(R.string.send);
        mPresenter = new SendEmailPresenter(this, getIntent());
        int itemWidth = (ScreenUtil.screenWidth - ScreenUtil.dip2px(50)) / 4;
        mAdapter = new EmailAttachmentAdapter(this, mPresenter.getEmailAttachmentList(), itemWidth, this);
        gridView_attachment.setAdapter(mAdapter);
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
            case R.id.tv_right_click:
                mPresenter.doSubmit();
                break;
            case R.id.fl_add_attachment:
                if (mPresenter.isEnableToAddAttachment())
                    MaterialFilePicker.pickFileUseDefaultMode(this, mPresenter.getLastPath(), RequestCodeConst.INT_REQUEST_PICK_FILE);
                else
                    callShowToastMessage(mResources.getString(R.string.email_attachment_count_reach_max, String.valueOf(CommonConst.MAX_EMAIL_ATTACHMENT_COUNT)), 0);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mPresenter != null && mPresenter.shouldHintSendEmail()) {
            new SimpleHintDialog(this).show(mResources.getString(R.string.is_give_send_email), R.drawable.icon_warn, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            }, null);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RequestCodeConst.INT_REQUEST_PICK_FILE:
                String filePath = MaterialFilePicker.decodePickedFile(data, resultCode);
                mPresenter.addEmailAttachment(filePath);
                break;
            case RequestCodeConst.INT_REQUEST_SEND_EMAIL_SUCCESS:
                if (resultCode == RESULT_OK) {
                    finish();
                } else {
                    mPresenter.clearForAnother();
                }
                break;
        }
    }

    public static void launch(Activity activity, String assigner, String candidateId) {
        Intent intent = new Intent(activity, SendEmailActivity.class);
        if (assigner != null)
            intent.putExtra("assigner", assigner);
        if (candidateId != null)
            intent.putExtra("candidateId", candidateId);
        activity.startActivity(intent);
    }

    @Override
    public void callSetPreviousAssigner(String assigner) {
        if (assigner != null) {
            clearEditText_assigner.setDeleteIcon(0);
            clearEditText_assigner.setText(assigner);
            clearEditText_assigner.setEnabled(false);
        }
    }

    @Override
    public String callGetEmailAssigner() {
        return clearEditText_assigner.getText().toString().trim();
    }

    @Override
    public String callGetEmailTitle() {
        return clearEditText_emailTitle.getText().toString().trim();
    }

    @Override
    public String callGetEmailContent() {
        return editText_content.getText().toString().trim();
    }

    @Override
    public void callUpdateAttachmentView(int targetPosition) {
        if (targetPosition < 0) {
            mAdapter.notifyDataSetChanged();
            return;
        }
        if (targetPosition >= gridView_attachment.getFirstVisiblePosition() && targetPosition <= gridView_attachment.getLastVisiblePosition()) {
            View view = gridView_attachment.getChildAt(targetPosition - gridView_attachment.getFirstVisiblePosition());
            if (view != null)
                mAdapter.refreshTargetView(view, targetPosition);
        }
    }

    @Override
    public void callUpdateAttachmentCountView(int count) {
        checkBox_attachmentCount.setText(String.valueOf(count));
        checkBox_attachmentCount.setChecked((count > 0));
    }

    @Override
    public void callResetAllViews() {
        clearEditText_emailTitle.setText(null);
        editText_content.setText(null);
        mAdapter.notifyDataSetChanged();
        callUpdateAttachmentCountView(0);
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
        switch (adapterEventId) {
            case AdapterEventIdConst.EVENT_ID_CLICK_EMAIL_ATTACHMENT:
                showEditEmailAttachmentTopTipUI(v, position, (EmailAttachmentBean) input);
                break;
        }
    }

    private void showEditEmailAttachmentTopTipUI(View v, final int position, final EmailAttachmentBean emailAttachmentBean) {
        if (mTopTipUI == null) {
            mTopTipUI = new TopTipUI(this, null);
        }
        mTopTipUI.updateMenuClick(new TopTipUI.IMenuClickListener() {
            @Override
            public void onTipMenuClick(int menuIndex) {
                switch (menuIndex) {
                    case 0:
                        mPresenter.deleteTargetAttachment(emailAttachmentBean);
                        break;
                    case 1:
                        mPresenter.reUploadAttachment(position, emailAttachmentBean);
                        break;
                }
            }
        });
        mTopTipUI.updateMenus(mPresenter.getEditMenu(emailAttachmentBean));
        mTopTipUI.show(v);
    }
}
