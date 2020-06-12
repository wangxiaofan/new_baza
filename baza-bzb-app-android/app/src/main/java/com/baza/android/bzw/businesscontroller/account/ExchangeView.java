package com.baza.android.bzw.businesscontroller.account;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baza.android.bzw.bean.exchange.GoodListResultBean;
import com.baza.android.bzw.businesscontroller.account.presenter.RightCenterPresenter;
import com.baza.android.bzw.businesscontroller.account.view.NumEditView;
import com.baza.android.bzw.businesscontroller.account.viewinterface.IRightCenterView;
import com.baza.android.bzw.manager.UserInfoManager;
import com.bznet.android.rcbox.R;
import com.slib.utils.DialogUtil;

import baza.dialog.simpledialog.MaterialDialog;

/**
 * Created by Vincent.Lei on 2018/9/4.
 * Title：
 * Note：
 */
public class ExchangeView implements View.OnClickListener, NumEditView.INumEditListener {
    private IRightCenterView mRightCenterView;
    private RightCenterPresenter mPresenter;
    private GoodListResultBean.Good mGood;
    private View view_root;
    private TextView textView_count;
    private TextView textView_exchange;
    private NumEditView numEditView;

    ExchangeView(IRightCenterView rightCenterView, RightCenterPresenter presenter, GoodListResultBean.Good good) {
        this.mRightCenterView = rightCenterView;
        this.mPresenter = presenter;
        this.mGood = good;
        if (mGood.price <= 0.00f)
            mGood.price = 1;
        init();
    }

    private void init() {
        view_root = mRightCenterView.callGetBindActivity().getLayoutInflater().inflate(R.layout.account_exchange_item, null);
        TextView textView = view_root.findViewById(R.id.tv_item_1);
        textView.setText(mGood.title);
        textView_count = view_root.findViewById(R.id.tv_item_count);
        numEditView = view_root.findViewById(R.id.num_edit_view);
        textView_exchange = view_root.findViewById(R.id.tv_exchange);
        view_root.setOnClickListener(this);
        textView_exchange.setOnClickListener(this);
        numEditView.setCurrentValue((int) mGood.price);
        numEditView.setStepCount((int) mGood.price);
        textView_count.setText("+ " + mGood.specification.limit);
        numEditView.setNumEditListener(this);
        if (mPresenter.getBenefitBaseInfo().quantity <= 0.0f)
            unEnableToEdit();
    }

    public View getView() {
        return view_root;
    }

    void unEnableToEdit() {
        numEditView.setEditEnable(false);
        textView_exchange.setEnabled(false);
        textView_exchange.setClickable(false);
        textView_exchange.setBackgroundResource(R.drawable.right_center_area_bg);
        textView_exchange.setTextColor(mRightCenterView.callGetResources().getColor(R.color.text_color_grey_94A1A5));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_exchange:
                if (!UserInfoManager.getInstance().checkIdentifyStatusAndVerifyIfNeed(mRightCenterView.callGetBindActivity()))
                    return;
                if (numEditView.getCurrentValue() <= 0)
                    return;
                if (mPresenter.getBenefitBaseInfo().quantity <= 0) {
                    showAmountNotEnoughDialog();
                    return;
                }
                final int amountCount = (mGood.specification.limit * numEditView.getCurrentValue() / (int) mGood.price);
                final int glassCount = numEditView.getCurrentValue();
                DialogUtil.doubleButtonShow(mRightCenterView.callGetBindActivity(), null, mRightCenterView.callGetResources().getString(R.string.good_exchange_hint, String.valueOf(glassCount), String.valueOf(amountCount), mGood.title), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.exchange(mGood, glassCount / (int)mGood.price, glassCount);
                    }
                }, null);
                break;
            default:
                if (mPresenter.getBenefitBaseInfo().quantity > 0)
                    return;
                showAmountNotEnoughDialog();
                break;
        }

    }

    @Override
    public boolean isNextAddEnable(NumEditView numEditView, int currentValue, int nextValue) {
        return (nextValue <= mPresenter.getBenefitBaseInfo().quantity);
    }

    @Override
    public boolean isNextMinusEnable(NumEditView numEditView, int currentValue, int nextValue) {
        return (nextValue >= 0);
    }

    @Override
    public void onNumChanged(int currentValue) {
        textView_count.setText("+ " + (mGood.specification.limit * currentValue / mGood.price));
    }

    private void showAmountNotEnoughDialog() {
        final MaterialDialog materialDialog = new MaterialDialog(mRightCenterView.callGetBindActivity());
        View messageView = LayoutInflater.from(mRightCenterView.callGetBindActivity()).inflate(R.layout.account_right_center_dialog_not_enough, null);
        messageView.findViewById(R.id.tv_get_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
                TaskCardActivity.launch(mRightCenterView.callGetBindActivity());
            }
        });
        materialDialog.buildButtonCount(1).buildSureButtonText(R.string.i_got_it);
        materialDialog.setMessageView(messageView);
        materialDialog.setCancelable(true);
        materialDialog.show();
    }
}
