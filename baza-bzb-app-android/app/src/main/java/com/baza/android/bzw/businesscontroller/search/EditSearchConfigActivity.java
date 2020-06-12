package com.baza.android.bzw.businesscontroller.search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.bean.searchfilterbean.CompanySearchFilterInfoBean;
import com.baza.android.bzw.bean.searchfilterbean.SearchFilterInfoBean;
import com.baza.android.bzw.bean.searchfilterbean.TrackingSearchFilterInfoBean;
import com.baza.android.bzw.businesscontroller.search.adapter.SearchHistoryAdapter;
import com.baza.android.bzw.businesscontroller.search.presenter.EditSearchConfigPresenter;
import com.baza.android.bzw.businesscontroller.search.viewinterface.IEditSearchConfigView;
import com.baza.android.bzw.constant.CommonConst;
import com.bznet.android.rcbox.R;
import com.slib.utils.ScreenUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/5/23.
 * Title：
 * Note：
 */

public class EditSearchConfigActivity extends BaseActivity implements IEditSearchConfigView, View.OnClickListener {
    @BindView(R.id.et_keyword)
    EditText editText_input;
    @BindView(R.id.lv_history)
    ListView listView_history;
    private TextView textView_clearHistory;
    private EditSearchConfigPresenter mPresenter;
    private SearchHistoryAdapter mHistoryAdapter;
    private int SEARCH_CONFIG_TYPE;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_search_condition;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_edit_search_condition);
    }

    @Override
    protected boolean isHideInputMethodAutoWhenTouchWindow() {
        return false;
    }

    @SuppressLint("ResourceType")
    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        mPresenter = new EditSearchConfigPresenter(this, getIntent());
        SEARCH_CONFIG_TYPE = getIntent().getIntExtra("historyType", IEditSearchConfigView.MODE_RESUME_SEARCH_HISTORY);

        TextView textView = new TextView(this);
        textView.setText(R.string.search_history);
        textView.setTextColor(mResources.getColor(R.color.text_color_grey_94A1A5));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        int padding = ScreenUtil.dip2px(10);
        textView.setPadding(padding, padding, padding, padding);
        listView_history.addHeaderView(textView);
        //清空搜索历史
        FrameLayout frameLayout_clearHistory = new FrameLayout(this);
        textView_clearHistory = new TextView(this);
        textView_clearHistory.setTextColor(mResources.getColorStateList(R.color.text_color_grey_94A1A5));
        textView_clearHistory.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView_clearHistory.setGravity(Gravity.CENTER);
        textView_clearHistory.setPadding(padding, padding, padding, padding);
        textView_clearHistory.setOnClickListener(this);
        textView_clearHistory.setId(R.id.clear_history);
        SpannableString spannableString = new SpannableString(mResources.getString(R.string.clear_history));
        spannableString.setSpan(new UnderlineSpan(), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_clearHistory.setText(spannableString);
        frameLayout_clearHistory.addView(textView_clearHistory, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        listView_history.addFooterView(frameLayout_clearHistory);

        editText_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //搜索
                    String keyword = editText_input.getText().toString().trim();
                    callReBackSearchCondition(keyword, CommonConst.LIST_POSITION_NONE);
                    return true;
                }
                return false;
            }
        });
        editText_input.requestFocus();
        mPresenter.initialize();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    @Override
    public void callSetHistory() {
        if (mHistoryAdapter == null) {
            mHistoryAdapter = new SearchHistoryAdapter(this, new SearchHistoryAdapter.ISearchHistoryDataProvider() {
                @Override
                public int getCount() {
                    return mPresenter.getHistoryList().size();
                }

                @Override
                public String getItemText(int position) {
                    return mPresenter.getHistoryList().get(position).toString();
                }

                @Override
                public void onItemClick(int position) {
                    callReBackSearchCondition(editText_input.getText().toString().trim(), position);
                }

                @Override
                public void onItemDelete(int position) {
                    mPresenter.removeTargetHistory(position);
                }
            }, null);
            listView_history.setAdapter(mHistoryAdapter);
        } else
            mHistoryAdapter.notifyDataSetChanged();
        if (textView_clearHistory != null)
            textView_clearHistory.setVisibility((mPresenter.getHistoryList().isEmpty() ? View.GONE : View.VISIBLE));
    }

    @Override
    public void callUpdateKeywordHint(int keyWordHint) {
        editText_input.setHint(keyWordHint);
    }

    public void callReBackSearchCondition(String keyword, int selectedPosition) {
        hideSoftInput();
        Intent intent = new Intent();
        if (selectedPosition != CommonConst.LIST_POSITION_NONE && selectedPosition < mPresenter.getHistoryList().size()) {
            Object o = mPresenter.getHistoryList().get(selectedPosition);
            if (o instanceof SearchFilterInfoBean) {
                intent.putExtra("searchFilter", (SearchFilterInfoBean) o);
            } else if (o instanceof CompanySearchFilterInfoBean) {
                intent.putExtra("searchFilter", (CompanySearchFilterInfoBean) o);
            } else if (o instanceof TrackingSearchFilterInfoBean) {
                intent.putExtra("searchFilter", (TrackingSearchFilterInfoBean) o);
            }
        }
        if (!TextUtils.isEmpty(keyword)) {
            intent.putExtra("keyword", keyword);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void callUpdateKeyWordView(String keyword) {
        editText_input.setText(keyword);
        if (!TextUtils.isEmpty(keyword))
            editText_input.setSelection(keyword.length());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                hideSoftInput();
                finish();
                break;
            case R.id.clear_history:
                //清空搜索历史
                mPresenter.clearHistory();
                break;
        }
    }

    public static void launch(int requestCode, Activity activity, int mode, String keyword) {
        launch(requestCode, activity, IEditSearchConfigView.MODE_RESUME_SEARCH_HISTORY, mode, keyword);
    }

    public static void launch(int requestCode, Activity activity, int historyType, int mode, String keyword) {
        Intent intent = new Intent(activity, EditSearchConfigActivity.class);
        intent.putExtra("searchMode", mode);
        intent.putExtra("keyword", keyword);
        intent.putExtra("historyType", historyType);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }


}
