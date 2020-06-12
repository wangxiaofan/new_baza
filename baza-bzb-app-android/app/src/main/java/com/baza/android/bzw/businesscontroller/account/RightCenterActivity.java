package com.baza.android.bzw.businesscontroller.account;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.bean.exchange.BenefitResultBean;
import com.baza.android.bzw.bean.exchange.GoodListResultBean;
import com.baza.android.bzw.businesscontroller.account.presenter.RightCenterPresenter;
import com.baza.android.bzw.businesscontroller.account.viewinterface.IBenefitDetailView;
import com.baza.android.bzw.businesscontroller.account.viewinterface.IRightCenterView;
import com.baza.android.bzw.businesscontroller.browser.RemoteBrowserActivity;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.widget.LoadingView;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.List;

import baza.dialog.simpledialog.MaterialDialog;

/**
 * Created by Vincent.Lei on 2018/9/4.
 * Title：
 * Note：
 */
public class RightCenterActivity extends BaseActivity implements IRightCenterView, View.OnClickListener {

    TextView textView_allCoinCount;
    LoadingView loadingView;
    LinearLayout linearLayout_itemContainer;
    FrameLayout frameLayout_invitedCodeEntrance;
    private RightCenterPresenter mPresenter;
    private List<ExchangeView> mExchangeViews;

    @Override
    protected int getLayoutId() {
        return R.layout.account_activity_right_center;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.right_center);
    }

    @Override
    protected void initWhenCallOnCreate() {
        mPresenter = new RightCenterPresenter(this);
        TextView textView = findViewById(R.id.tv_title);
        textView.setText(R.string.right_center);
        textView_allCoinCount = findViewById(R.id.tv_coin_count);
        linearLayout_itemContainer = findViewById(R.id.ll_exchange_item_container);
        loadingView = findViewById(R.id.loading_view);
        frameLayout_invitedCodeEntrance = findViewById(R.id.fl_code_exchange);
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                loadingView.startLoading(null);
                mPresenter.loadUserBenefit();
            }
        });
        mPresenter.initialize();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                finish();
                break;
            case R.id.tv_exchange_record:
                BenefitDetailActivity.launch(this, IBenefitDetailView.TYPE_OUT);
                break;
            case R.id.tv_get_record:
                BenefitDetailActivity.launch(this, IBenefitDetailView.TYPE_IN);
                break;
            case R.id.tv_get_rule:
                RemoteBrowserActivity.launch(this, null, false, URLConst.LINK_RIGHT_RULE);
                break;
            case R.id.fl_code_exchange:
                final MaterialDialog materialDialog = new MaterialDialog(this);
                View viewDialog = getLayoutInflater().inflate(R.layout.right_center_dialog_invite_code_exchange, null);
                viewDialog.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                    }
                });
                final EditText editText = viewDialog.findViewById(R.id.et_content);
                materialDialog.setMessageView(viewDialog);
                materialDialog.buildButtonCount(1);
                materialDialog.setCancelable(true);
                materialDialog.setAutoDismissEnable(false);
                materialDialog.setMessageViewPadding(0);
                materialDialog.buildClickListener(null, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = editText.getText().toString().trim();
                        if (TextUtils.isEmpty(text)) {
                            callShowToastMessage(null, R.string.input_invite_code);
                            return;
                        }
                        materialDialog.dismiss();
                        mPresenter.inviteCodeExchange(text);
                    }
                });
                materialDialog.show();
                break;
        }
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, RightCenterActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void callCancelLoadingView(boolean success, int errorCode, String errorMsg) {
        if (!loadingView.isShownVisibility())
            return;
        if (success)
            loadingView.finishLoading();
        else
            loadingView.loadingFailed(errorCode, errorMsg);
    }

    @Override
    public void callShowLoadingView(String msg) {
        loadingView.startLoading(msg);
    }

    @Override
    public void callUpdateGoodsView() {
        List<GoodListResultBean.Good> goodList = mPresenter.getGoodList();
        ExchangeView exchangeView;
        mExchangeViews = new ArrayList<>(goodList.size());
        for (int i = 0, size = goodList.size(); i < size; i++) {
            exchangeView = new ExchangeView(this, mPresenter, goodList.get(i));
            mExchangeViews.add(exchangeView);
            linearLayout_itemContainer.addView(exchangeView.getView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    @Override
    public void callUpdateBenefitView() {
        BenefitResultBean.Data data = mPresenter.getBenefitBaseInfo();
        textView_allCoinCount.setText(String.valueOf(data.quantity));
        if (data.quantity <= 0 && mExchangeViews != null && !mExchangeViews.isEmpty()) {
            for (int i = 0, size = mExchangeViews.size(); i < size; i++) {
                mExchangeViews.get(i).unEnableToEdit();
            }
        }

    }

//    @Override
//    public void callUpdateInvitedCodeEnableView() {
//        frameLayout_invitedCodeEntrance.setVisibility(View.VISIBLE);
//    }
}
