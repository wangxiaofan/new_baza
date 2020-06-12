package com.baza.android.bzw.businesscontroller.floating.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.base.BaseFragment;
import com.baza.android.bzw.bean.resume.FloatingListBean;
import com.baza.android.bzw.bean.resume.ResumeStatus;
import com.baza.android.bzw.businesscontroller.floating.FloatingDetailActivity;
import com.baza.android.bzw.businesscontroller.floating.FloatingSearchFilterUI;
import com.baza.android.bzw.businesscontroller.floating.adapter.FloatingListAdapter;
import com.baza.android.bzw.businesscontroller.floating.presenter.FloatingListPresenter;
import com.baza.android.bzw.businesscontroller.floating.viewinterface.IFloatingListView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.RequestCodeConst;
import com.baza.android.bzw.widget.LoadingView;
import com.baza.android.bzw.widget.dialog.AllReceiveDialog;
import com.baza.android.bzw.widget.dialog.FeedBackInterViewDialog;
import com.baza.android.bzw.widget.dialog.MakeInterViewDialog;
import com.baza.android.bzw.widget.dialog.MakeOfferDialog;
import com.baza.android.bzw.widget.dialog.OfferDialog;
import com.baza.android.bzw.widget.dialog.ReceiveDialog;
import com.baza.android.bzw.widget.dialog.RefuseDialog;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.slib.utils.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class FloatingListFragment extends BaseFragment implements IFloatingListView, BaseBZWAdapter.IAdapterEventsListener, View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    ImageView ivAllSelect;

    TextView tvSelectNum;

    TextView tvReceive;

    LinearLayout llBottom;

    LoadingView loadingView;

    View view_empty;

    PullToRefreshListView pullToRefreshListView;

    ListView listView;

    FloatingListPresenter mPresenter;

    FloatingListAdapter mAdapter;

    boolean isOneKey;

    public static final String KEY_STARTTIME = "KEY_STARTTIME";

    public static final String KEY_ENDTIME = "KEY_ENDTIME";

    public static final String KEY_TYPE = "KEY_TYPE";

    public static final String KEY_FROM = "KEY_FROM";

    private String startTime, endTime, type;

    private boolean isFromIm = false;

    private SelectListener selectListener;

    private FloatingSearchFilterUI searchFilterUI;

    private int currectStatus = -1;

    public boolean isEdit;

    private List<FloatingListBean> selectList = new ArrayList<>();

    private boolean isAllSelect;

    private String recommendId;

    List<String> content = new ArrayList<>();

    public static FloatingListFragment getInstance(String startTime, String endTime, String type, boolean formIm) {
        FloatingListFragment fragment = new FloatingListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_STARTTIME, startTime);
        bundle.putString(KEY_ENDTIME, endTime);
        bundle.putString(KEY_TYPE, type);
        bundle.putBoolean(KEY_FROM, formIm);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_floating_list;
    }

    @Override
    protected String getPageTitle() {
        return "floating-list";
    }

    @Override
    protected void initWhenOnCreatedViews(View mRootView) {
        Bundle bundle = getArguments();
        selectListener = (SelectListener) callGetBindActivity();
        startTime = bundle.getString(KEY_STARTTIME);
        endTime = bundle.getString(KEY_ENDTIME);
        type = bundle.getString(KEY_TYPE);
        isFromIm = bundle.getBoolean(KEY_FROM);
        mPresenter = new FloatingListPresenter(this);
        mPresenter.getFloatingLogger().setPageCode(getActivity(), "FloatingList");
        loadingView = mRootView.findViewById(R.id.loading_view);
        pullToRefreshListView = mRootView.findViewById(R.id.listView);
        listView = pullToRefreshListView.getRefreshableView();
        pullToRefreshListView.setOnRefreshListener(this);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        view_empty = mRootView.findViewById(R.id.view_empty_view);
        ivAllSelect = mRootView.findViewById(R.id.iv_all_select);
        tvSelectNum = mRootView.findViewById(R.id.tv_select_num);
        tvReceive = mRootView.findViewById(R.id.tv_receive);
        llBottom = mRootView.findViewById(R.id.ll_bottom);
        ivAllSelect.setOnClickListener(this);
        tvReceive.setOnClickListener(this);
        tvSelectNum.setOnClickListener(this);

        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                mPresenter.loadFloatingList(startTime, endTime, type);
            }
        });

        mAdapter = new FloatingListAdapter(getActivity(), this, type);
        listView.setAdapter(mAdapter);

        mPresenter.getFloatingActionLogger().setPageCode(getActivity(), "FloatingList");
        searchFilterUI = new FloatingSearchFilterUI(this, mRootView.findViewById(R.id.new_search_filter_ui), type);
        searchFilterUI.init();
        if (isFromIm) {
            searchFilterUI.onSortSelected(2, "最近一个月");
            searchFilterUI.onStatusSelected(new ResumeStatus(0, "未处理"));
            isFromIm = false;
        }
    }

    @Override
    protected void initWhenOnActivityCreated() {
        mPresenter.initialize();
        mPresenter.loadFloatingList(startTime, endTime, type);
    }

    @Override
    protected void changedUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    @Override
    public void callShowLoadingView(String msg) {
        loadingView.startLoading(null);
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
    public void callRefreshListViews(int targetPosition) {
        listView.setVisibility(View.VISIBLE);
        view_empty.setVisibility(View.GONE);
        pullToRefreshListView.onRefreshComplete();
        mAdapter.setData(mPresenter.getDataList());
//        listView.setSelection(0);
        if (isEdit) {
            selectList.clear();
            mAdapter.setAllSelect(false);
            ivAllSelect.setBackgroundResource(R.drawable.btn_weixuanze);
            tvSelectNum.setText("全选" + selectList.size() + "/" + mAdapter.getCount());
        }
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {

        FloatingListBean resumeBean = (FloatingListBean) input;
        switch (adapterEventId) {
            case AdapterEventIdConst.EVENT_ID_COMPANY_ITEM_CLICK:
//                FloatingDetailActivity.launchForResult(getActivity(), resumeBean.getId());
                Intent intent = new Intent(getActivity(), FloatingDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("recommendId", resumeBean.getId());
                bundle.putString("type", type);
                intent.putExtras(bundle);
                startActivityForResult(intent, RequestCodeConst.INT_REQUEST_REFRESH_FLOATING);
                break;
            case AdapterEventIdConst.EVENT_ID_FLOATING_SELECT_CLICK:
                if (resumeBean.isSelect()) {
                    selectList.add(resumeBean);
                } else {
                    selectList.remove(resumeBean);
                }
                tvSelectNum.setText("全选" + selectList.size() + "/" + mAdapter.getCount());
                if (selectList.size() == mAdapter.getCount()) {
                    isAllSelect = true;
                    ivAllSelect.setBackgroundResource(R.drawable.btn_yigouxuan_02);
                } else {
                    isAllSelect = false;
                    ivAllSelect.setBackgroundResource(R.drawable.btn_weixuanze);
                }
                break;
            case AdapterEventIdConst.EVENT_ID_FLOATING_ACTION_REFUSE:
                delBtnOne(resumeBean);
                break;
            case AdapterEventIdConst.EVENT_ID_FLOATING_ACTION_RECEIVE:
                delBtnTwo(resumeBean);
                break;
            case AdapterEventIdConst.EVENT_ID_FLOATING_ACTION_NEXT:
                delBtnThree(resumeBean);
                break;
            case AdapterEventIdConst.EVENT_ID_FLOATING_ACTION_ONEKEY:
                oneKeyOffer(resumeBean);
                break;
        }
    }

    @Override
    protected boolean isUseDefaultStatusBarBackground() {
        return false;
    }

    @Override
    public void callShowEmpty() {
        listView.setVisibility(View.GONE);
        view_empty.setVisibility(View.VISIBLE);
    }

    @Override
    public void callRefreshData(int sort) {
        if (sort == 0) {
            startTime = null;
            endTime = null;
        } else if (sort == 1) {
            startTime = DateUtil.dateToString(DateUtil.getDateBefore(new Date(), 7), DateUtil.SDF_RECOMMEND_TWO) + " 00:00:00";
            endTime = DateUtil.dateToString(new Date(), DateUtil.SDF_RECOMMEND_TWO) + " 23:59:59";
        } else if (sort == 2) {
            startTime = DateUtil.dateToString(DateUtil.getDateBefore(new Date(), 30), DateUtil.SDF_RECOMMEND_TWO) + " 00:00:00";
            endTime = DateUtil.dateToString(new Date(), DateUtil.SDF_RECOMMEND_TWO) + " 23:59:59";
        } else if (sort == 3) {
            startTime = DateUtil.dateToString(DateUtil.getDateBefore(new Date(), 90), DateUtil.SDF_RECOMMEND_TWO) + " 00:00:00";
            endTime = DateUtil.dateToString(new Date(), DateUtil.SDF_RECOMMEND_TWO) + " 23:59:59";
        } else if (sort == 4) {
            startTime = DateUtil.dateToString(DateUtil.getDateBefore(new Date(), 180), DateUtil.SDF_RECOMMEND_TWO) + " 00:00:00";
            endTime = DateUtil.dateToString(new Date(), DateUtil.SDF_RECOMMEND_TWO) + " 23:59:59";
        } else if (sort == 5) {
            startTime = "null";
            endTime = DateUtil.dateToString(DateUtil.getDateBefore(new Date(), 180), DateUtil.SDF_RECOMMEND_TWO) + " 23:59:59";
        }
        mPresenter.loadFloatingList(startTime, endTime, type);
    }

    @Override
    public void callRefreshDataByStatus(int status) {
        if (mPresenter.getListForStatus(status).size() <= 0) {
            listView.setVisibility(View.GONE);
            view_empty.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            view_empty.setVisibility(View.GONE);
            mAdapter.setData(mPresenter.getListForStatus(status));
            listView.setSelection(0);
            currectStatus = status;
            if (type.equals("2")) {
                if (status == 0) {
                    selectListener.setModify(true);
                } else {
                    selectListener.setModify(false);
                }
            }
            if (isEdit) {
                selectList.clear();
                mAdapter.setAllSelect(false);
                ivAllSelect.setBackgroundResource(R.drawable.btn_weixuanze);
                tvSelectNum.setText("全选" + selectList.size() + "/" + mAdapter.getCount());
            }
        }
    }

    @Override
    public void callRefreshDataByJob(String job) {
        if (mPresenter.getListForJob(job).size() <= 0) {
            listView.setVisibility(View.GONE);
            view_empty.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            view_empty.setVisibility(View.GONE);
            mAdapter.setData(mPresenter.getListForJob(job));
            listView.setSelection(0);
            if (isEdit) {
                selectList.clear();
                mAdapter.setAllSelect(false);
                ivAllSelect.setBackgroundResource(R.drawable.btn_weixuanze);
                tvSelectNum.setText("全选" + selectList.size() + "/" + mAdapter.getCount());
            }
        }
    }

    @Override
    public void callRefreshDataByName(String name) {
        if (mPresenter.getListForName(name).size() <= 0) {
            listView.setVisibility(View.GONE);
            view_empty.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            view_empty.setVisibility(View.GONE);
            mAdapter.setData(mPresenter.getListForName(name));
            listView.setSelection(0);
            if (isEdit) {
                selectList.clear();
                mAdapter.setAllSelect(false);
                ivAllSelect.setBackgroundResource(R.drawable.btn_weixuanze);
                tvSelectNum.setText("全选" + selectList.size() + "/" + mAdapter.getCount());
            }
        }
    }

    @Override
    public List<String> getJobList() {
        return mPresenter.getJobs();
    }

    @Override
    public String getjob() {
        return mPresenter.getCurrentJob();
    }

    @Override
    public void callUpdateListStatus(List<String> ids) {
        mAdapter.changeStatus(ids);
        selectListener.setModify(false);
    }

    @Override
    public void callUpdatePage() {
        mPresenter.loadFloatingList(startTime, endTime, type);
    }

    @Override
    public void callShowDialog() {
        showDialog();
    }

    public void setEdit(boolean edit) {
        this.isEdit = edit;
        mAdapter.setEdit(edit);
        if (edit) {
            llBottom.setVisibility(View.VISIBLE);
            tvSelectNum.setText("全选" + selectList.size() + "/" + mAdapter.getCount());
        } else {
            selectList.clear();
            mAdapter.setAllSelect(false);
            llBottom.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_all_select:
            case R.id.tv_select_num:
                if (isAllSelect) {
                    selectList.clear();
                    isAllSelect = false;
                    ivAllSelect.setBackgroundResource(R.drawable.btn_weixuanze);
                } else {
                    selectList.clear();
                    selectList.addAll(mAdapter.getData());
                    isAllSelect = true;
                    ivAllSelect.setBackgroundResource(R.drawable.btn_yigouxuan_02);
                }
                mAdapter.setAllSelect(isAllSelect);
                break;

            case R.id.tv_receive:
                StringBuilder stringBuilder = new StringBuilder("确认批量接受");
                for (FloatingListBean bean : selectList) {
                    stringBuilder.append(bean.getCandidateName()).append(",");
                }
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                stringBuilder.append("等候选人？");

                new AllReceiveDialog(getActivity(), new AllReceiveDialog.IAllReceiveListener() {
                    @Override
                    public void onReadyAllReceive() {
                        mPresenter.accept(selectList);
                    }
                }, stringBuilder.toString());

                break;
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        callUpdatePage();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }

    public interface SelectListener {
        void setModify(boolean isEdit);
    }

    public boolean isFilterPaneShow() {
        return searchFilterUI.shouldHideFilterPane();
    }

    public void closeFilterPane() {
        searchFilterUI.closeFilterPane();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeConst.INT_REQUEST_REFRESH_FLOATING && resultCode == Activity.RESULT_OK) {
            mPresenter.loadFloatingList(startTime, endTime, type);
        }
    }

    /**
     * 处理第一个按钮操作
     */
    private void delBtnOne(FloatingListBean resumeBean) {
        switch (resumeBean.getStatus()) {
            case 0:
            case 2:
                content.clear();
                content.add("公司及行业经验不匹配");
                content.add("工作经验不足");
                content.add("跳槽频繁");
                content.add("学历不合格");
                content.add("其他原因");
                new RefuseDialog(getActivity(), new RefuseDialog.IRefuseListener() {
                    @Override
                    public void onReadyRefuse(String reason, String detail) {
                        mPresenter.acceptOrReject(resumeBean.getId(), false, reason, detail);
                    }
                }, content, "拒绝原因", true);
                break;
            case 1:
                content.clear();
                content.add("面试官不通过");
                content.add("有重复候选人");
                content.add("职位取消");
                new RefuseDialog(getActivity(), new RefuseDialog.IRefuseListener() {
                    @Override
                    public void onReadyRefuse(String reason, String detail) {
                        mPresenter.flowObsolete(resumeBean.getId(), reason, detail);
                    }
                }, content, "淘汰原因", false);
                break;

            case 4:
                content.clear();
                content.add("猎头未安排");
                content.add("候选人放弃");
                content.add("面试官放弃");
                new RefuseDialog(getActivity(), new RefuseDialog.IRefuseListener() {
                    @Override
                    public void onReadyRefuse(String reason, String detail) {
                        mPresenter.flowObsolete(resumeBean.getId(), reason, detail);
                    }
                }, content, "淘汰原因", false);
                break;

            case 5:
                // 反馈面试结果
                new FeedBackInterViewDialog(getActivity(), new FeedBackInterViewDialog.IFeedBackListener() {
                    @Override
                    public void onFeedBack(String interviewType, String result, String evaluation, String sharedResult) {
                        HashMap<String, String> param = new HashMap<>();
                        param.put("interviewId", resumeBean.getInterviewId());
                        param.put("result", result);
                        param.put("interviewType", interviewType);
                        param.put("evaluation", evaluation);
                        param.put("sharedResult", sharedResult);
                        mPresenter.interviewFeedback(param);
                    }
                });
                break;

            case 7:
                content.clear();
                content.add("候选人爽约");
                content.add("面试官不通过");
                content.add("职位取消");
                new RefuseDialog(getActivity(), new RefuseDialog.IRefuseListener() {
                    @Override
                    public void onReadyRefuse(String reason, String detail) {
                        mPresenter.flowObsolete(resumeBean.getId(), reason, detail);
                    }
                }, content, "淘汰原因", false);
                break;

            case 8:
                content.clear();
                content.add("offer没谈拢");
                content.add("候选人拒绝offer");
                content.add("候选人offer被收回");
                new RefuseDialog(getActivity(), new RefuseDialog.IRefuseListener() {
                    @Override
                    public void onReadyRefuse(String reason, String detail) {
                        mPresenter.flowObsolete(resumeBean.getId(), reason, detail);
                    }
                }, content, "淘汰原因", false);
                break;

            case 16:
                new ReceiveDialog(getActivity(), new ReceiveDialog.IReceiveListener() {
                    @Override
                    public void onReadyReceive() {
                        HashMap<String, String> param = new HashMap<>();
                        param.put("recommendId", resumeBean.getId());
                        param.put("status", "32");
                        mPresenter.flowStage(param);
                    }
                }, "确认候选人已离职吗？");
                break;
        }
    }

    /**
     * 处理第二个按钮操作
     */
    private void delBtnTwo(FloatingListBean resumeBean) {
        switch (resumeBean.getStatus()) {
            case 0:
            case 2:
                new ReceiveDialog(getActivity(), new ReceiveDialog.IReceiveListener() {
                    @Override
                    public void onReadyReceive() {
                        mPresenter.acceptOrReject(resumeBean.getId(), true, "", "");
                    }
                }, "确定接受该候选人吗？");
                break;

            case 1:
                new ReceiveDialog(getActivity(), new ReceiveDialog.IReceiveListener() {
                    @Override
                    public void onReadyReceive() {
                        HashMap<String, String> param = new HashMap<>();
                        param.put("recommendId", resumeBean.getId());
                        param.put("status", "4");
                        mPresenter.flowStage(param);
                    }
                }, "确认候选人进入面试吗？");
                break;

            case 4:
                //安排面试
                new MakeInterViewDialog(getActivity(), new MakeInterViewDialog.IReceiveListener() {
                    @Override
                    public void onReadyReceive(String interviewType, String interviewTime, String interviewAddress, String remark, String shouldSendNotification) {
                        HashMap<String, String> param = new HashMap<>();
                        param.put("recommendId", resumeBean.getId());
                        param.put("interviewTime", interviewTime);
                        param.put("interviewAddress", interviewAddress);
                        param.put("remark", remark);
                        param.put("interviewType", interviewType);
                        param.put("shouldSendNotification", shouldSendNotification);
                        mPresenter.flowInterview(param);
                    }
                }, resumeBean.getCandidateName(), resumeBean.getRecommenderRealName(), resumeBean.getMajor());
                break;

            case 7:
                recommendId = resumeBean.getId();
                mPresenter.splitInfoList(resumeBean.getId());
                isOneKey = false;
                break;

            case 8:
                // 入职
                new OfferDialog(getActivity(), new OfferDialog.IOfferListener() {
                    @Override
                    public void onReadyOffer(String date, String month) {
                        HashMap<String, String> param = new HashMap<>();
                        param.put("recommendId", resumeBean.getId());
                        param.put("status", "16");
                        param.put("onboardDate", date);
                        param.put("guaranteeDays", month);
                        mPresenter.flowStage(param);
                    }
                });
                break;

            case 16:
                //donothing
                break;
        }
    }

    /**
     * 处理第三个按钮操作
     */
    private void delBtnThree(FloatingListBean resumeBean) {
        new MakeInterViewDialog(getActivity(), new MakeInterViewDialog.IReceiveListener() {
            @Override
            public void onReadyReceive(String interviewType, String interviewTime, String interviewAddress, String remark, String shouldSendNotification) {
                HashMap<String, String> param = new HashMap<>();
                param.put("recommendId", resumeBean.getId());
                param.put("interviewTime", interviewTime);
                param.put("interviewAddress", interviewAddress);
                param.put("remark", remark);
                param.put("interviewType", interviewType);
                param.put("shouldSendNotification", shouldSendNotification);
                mPresenter.flowInterview(param);
            }
        }, resumeBean.getCandidateName(), resumeBean.getRecommenderRealName(), resumeBean.getMajor());
    }

    private void oneKeyOffer(FloatingListBean resumeBean) {
        recommendId = resumeBean.getId();
        mPresenter.splitInfoList(resumeBean.getId());
        isOneKey = true;
    }

    private void showDialog() {
        new MakeOfferDialog(getActivity(), new MakeOfferDialog.IReceiveListener() {
            @Override
            public void onReadyReceive(String monthPay, String recruiteTime, String months, String commissionRate, String serviceCharge,
                                       String remark, String performanceSplitItems) {
                HashMap<String, String> param = new HashMap<>();
                param.put("recommendId", recommendId);
                param.put("monthPay", monthPay);
                param.put("recruiteTime", recruiteTime);
                param.put("months", months);
                param.put("commissionRate", commissionRate);
                param.put("serviceCharge", serviceCharge);
                param.put("remark", remark);
                param.put("performanceSplitItems", performanceSplitItems);
                if (isOneKey) {
                    mPresenter.oneKeyOffer(param);
                } else {
                    param.put("status", "8");
                    mPresenter.flowStage(param);
                }
            }
        }, mPresenter.getFirmMembers(), mPresenter.getSplitInfoBeans());
    }
}
