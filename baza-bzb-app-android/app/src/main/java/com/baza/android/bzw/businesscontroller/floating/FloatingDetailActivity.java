package com.baza.android.bzw.businesscontroller.floating;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.resume.FloatingListDetailBean;
import com.baza.android.bzw.businesscontroller.company.CompanyDetailActivity;
import com.baza.android.bzw.businesscontroller.floating.adapter.TimeLineListAdapter;
import com.baza.android.bzw.businesscontroller.floating.presenter.FloatingPresenter;
import com.baza.android.bzw.businesscontroller.floating.viewinterface.IFloatingView;
import com.baza.android.bzw.businesscontroller.resume.detail.viewinterface.IResumeDetailView;
import com.baza.android.bzw.log.logger.FloatingDetailLogger;
import com.baza.android.bzw.manager.UserInfoManager;
import com.baza.android.bzw.widget.LoadingView;
import com.baza.android.bzw.widget.dialog.FeedBackInterViewDialog;
import com.baza.android.bzw.widget.dialog.MakeInterViewDialog;
import com.baza.android.bzw.widget.dialog.MakeOfferDialog;
import com.baza.android.bzw.widget.dialog.OfferDialog;
import com.baza.android.bzw.widget.dialog.ReceiveDialog;
import com.baza.android.bzw.widget.dialog.RefuseDialog;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FloatingDetailActivity extends BaseActivity implements IFloatingView, BaseBZWAdapter.IAdapterEventsListener {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_click)
    TextView tv_right_click;
    @BindView(R.id.ibtn_left_click)
    ImageButton ibtnLeftClick;
    @BindView(R.id.view_title_bar)
    FrameLayout viewTitleBar;
    @BindView(R.id.tv_recommend_info)
    TextView tvRecommendInfo;
    @BindView(R.id.progress_bar_status)
    ProgressBar progressBarStatus;
    @BindView(R.id.iv_status_one)
    ImageView ivStatusOne;
    @BindView(R.id.iv_status_two)
    ImageView ivStatusTwo;
    @BindView(R.id.iv_status_three)
    ImageView ivStatusThree;
    @BindView(R.id.iv_status_four)
    ImageView ivStatusFour;
    @BindView(R.id.tv_status_one)
    TextView tvStatusOne;
    @BindView(R.id.tv_status_two)
    TextView tvStatusTwo;
    @BindView(R.id.tv_status_three)
    TextView tvStatusThree;
    @BindView(R.id.tv_status_four)
    TextView tvStatusFour;
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.tv_btn_one)
    TextView tvBtnOne;
    @BindView(R.id.tv_btn_two)
    TextView tvBtnTwo;
    @BindView(R.id.tv_one_key)
    TextView tv_one_key;
    @BindView(R.id.tv_btn_three)
    TextView tvBtnThree;
    @BindView(R.id.ll_action)
    LinearLayout ll_action;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    @BindView(R.id.tv_process_status)
    TextView tv_process_status;

    FloatingPresenter mPresenter;

    TimeLineListAdapter listAdapter;

    StringBuilder recommendBuilder = new StringBuilder();

    String recommendId, type;

    List<String> content = new ArrayList<>();

    String resumeId;

    int currentStatus = -1;

    boolean isStatusChange = false;

    FloatingListDetailBean floatingListDetailBean;

    boolean isOneKey;

    FloatingDetailLogger floatingDetailLogger = new FloatingDetailLogger();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_floating_detail;
    }

    @Override
    protected String getPageTitle() {
        return "floating详情";
    }

    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        tv_right_click.setText("查看简历");
        Bundle bundle = getIntent().getExtras();
        recommendId = bundle.getString("recommendId");
        type = bundle.getString("type");
        Log.e("herb", "recommendId>>" + recommendId);
        Log.e("herb", "type>>" + type);
        mPresenter = new FloatingPresenter(this);
        mPresenter.loadFloatingListDetail(recommendId);
        loadingView.setRetryListener(new LoadingView.IRetryListener() {
            @Override
            public void onRetry() {
                loadingView.startLoading(null);
                mPresenter.loadFloatingListDetail(recommendId);
            }
        });
        floatingDetailLogger.setPageCode(FloatingDetailActivity.this, "ResumeDetailRecommendRecords");
        floatingDetailLogger.RecommendDetail(FloatingDetailActivity.this, recommendId);
        mPresenter.getFloatingActionLogger().setPageCode(FloatingDetailActivity.this, "RecommendDetail");
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

//    public static void launch(Activity activity, String recommendId) {
//        Intent intent = new Intent(activity, FloatingDetailActivity.class);
//        intent.putExtra("recommendId", recommendId);
//        activity.startActivity(intent);
//    }
//
//    public static void launchForResult(Activity activity, String recommendId) {
//        Intent intent = new Intent(activity, FloatingDetailActivity.class);
//        intent.putExtra("recommendId", recommendId);
//        activity.startActivityForResult(intent, RequestCodeConst.INT_REQUEST_REFRESH_FLOATING);
//    }

    @OnClick({R.id.ibtn_left_click, R.id.tv_btn_one, R.id.tv_btn_two, R.id.tv_btn_three, R.id.tv_right_click, R.id.tv_one_key})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ibtn_left_click:
                if (isStatusChange)
                    setResult(Activity.RESULT_OK);
                finish();
                break;

            case R.id.tv_right_click:
                CompanyDetailActivity.launch(FloatingDetailActivity.this, new IResumeDetailView.IntentParam(resumeId).isCompany(true));
                break;

            case R.id.tv_btn_one:
                delBtnOne();
                break;

            case R.id.tv_btn_two:
                delBtnTwo();
                break;

            case R.id.tv_btn_three:
                delBtnThree();
                break;

            case R.id.tv_one_key:
                oneKeyOffer();
                break;
        }
    }

    @Override
    public void callShowLoadingView() {
        if (loadingView.isShownVisibility())
            return;
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
    public void callUpdateContent(FloatingListDetailBean beans) {
        floatingListDetailBean = beans;
        currentStatus = beans.recommendStatus;
        resumeId = beans.resumeId;
        tvTitle.setText(beans.realName + "-推荐详情");
        tvRecommendInfo.setText(getRecommendInfo(beans));
        listAdapter = new TimeLineListAdapter(this, beans.timeLineList, this);
        listView.setAdapter(listAdapter);
        dealStep(currentStatus);
    }

    @Override
    public void callUpdatePage() {
        isStatusChange = true;
        mPresenter.loadFloatingListDetail(recommendId);
    }

    @Override
    public void callShowDialog() {
        showDialog();
    }

    /**
     * 组装推荐信息
     *
     * @param resumeBean
     * @return
     */
    private String getRecommendInfo(FloatingListDetailBean resumeBean) {
        if (recommendBuilder.length() > 0) {
            recommendBuilder.delete(0, recommendBuilder.length());
        }
        if (TextUtils.isEmpty(resumeBean.userName)) {
            recommendBuilder.append("未知");
        } else {
            recommendBuilder.append(resumeBean.userName);
        }
        recommendBuilder.append("推荐到");
        if (TextUtils.isEmpty(resumeBean.company)) {
            recommendBuilder.append("公司信息暂无");
        } else {
            recommendBuilder.append(resumeBean.company.length() > 8 ? resumeBean.company.substring(0, 8) + "..." : resumeBean.company);
        }
        recommendBuilder.append("-");
        if (TextUtils.isEmpty(resumeBean.title)) {
            recommendBuilder.append("职位信息暂无");
        } else {
            recommendBuilder.append(resumeBean.title.length() > 8 ? resumeBean.title.substring(0, 8) + "..." : resumeBean.title);
        }
        return recommendBuilder.toString();
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {

    }

    /**
     * 处理第一个按钮操作
     */
    private void delBtnOne() {
        switch (mPresenter.getStatus()) {
            case 0:
            case 2:
                content.clear();
                content.add("公司及行业经验不匹配");
                content.add("工作经验不足");
                content.add("跳槽频繁");
                content.add("学历不合格");
                content.add("其他原因");
                new RefuseDialog(FloatingDetailActivity.this, new RefuseDialog.IRefuseListener() {
                    @Override
                    public void onReadyRefuse(String reason, String detail) {
                        mPresenter.acceptOrReject(recommendId, false, reason, detail);
                    }
                }, content, "拒绝原因", true);
                break;
            case 1:
                content.clear();
                content.add("面试官不通过");
                content.add("有重复候选人");
                content.add("职位取消");
                new RefuseDialog(FloatingDetailActivity.this, new RefuseDialog.IRefuseListener() {
                    @Override
                    public void onReadyRefuse(String reason, String detail) {
                        mPresenter.flowObsolete(recommendId, reason, detail);
                    }
                }, content, "淘汰原因", false);
                break;

            case 4:
                content.clear();
                content.add("猎头未安排");
                content.add("候选人放弃");
                content.add("面试官放弃");
                new RefuseDialog(FloatingDetailActivity.this, new RefuseDialog.IRefuseListener() {
                    @Override
                    public void onReadyRefuse(String reason, String detail) {
                        mPresenter.flowObsolete(recommendId, reason, detail);
                    }
                }, content, "淘汰原因", false);
                break;

            case 5:
                // 反馈面试结果
                new FeedBackInterViewDialog(FloatingDetailActivity.this, new FeedBackInterViewDialog.IFeedBackListener() {
                    @Override
                    public void onFeedBack(String interviewType, String result, String evaluation, String sharedResult) {
                        HashMap<String, String> param = new HashMap<>();
                        param.put("interviewId", floatingListDetailBean.interviewId);
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
                new RefuseDialog(FloatingDetailActivity.this, new RefuseDialog.IRefuseListener() {
                    @Override
                    public void onReadyRefuse(String reason, String detail) {
                        mPresenter.flowObsolete(recommendId, reason, detail);
                    }
                }, content, "淘汰原因", false);
                break;

            case 8:
                content.clear();
                content.add("offer没谈拢");
                content.add("候选人拒绝offer");
                content.add("候选人offer被收回");
                new RefuseDialog(FloatingDetailActivity.this, new RefuseDialog.IRefuseListener() {
                    @Override
                    public void onReadyRefuse(String reason, String detail) {
                        mPresenter.flowObsolete(recommendId, reason, detail);
                    }
                }, content, "淘汰原因", false);
                break;

            case 16:
                new ReceiveDialog(FloatingDetailActivity.this, new ReceiveDialog.IReceiveListener() {
                    @Override
                    public void onReadyReceive() {
                        HashMap<String, String> param = new HashMap<>();
                        param.put("recommendId", recommendId);
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
    private void delBtnTwo() {
        switch (mPresenter.getStatus()) {
            case 0:
            case 2:
                new ReceiveDialog(FloatingDetailActivity.this, new ReceiveDialog.IReceiveListener() {
                    @Override
                    public void onReadyReceive() {
                        mPresenter.acceptOrReject(recommendId, true, "", "");
                    }
                }, "确定接受该候选人吗？");
                break;

            case 1:
                new ReceiveDialog(FloatingDetailActivity.this, new ReceiveDialog.IReceiveListener() {
                    @Override
                    public void onReadyReceive() {
                        HashMap<String, String> param = new HashMap<>();
                        param.put("recommendId", recommendId);
                        param.put("status", "4");
                        mPresenter.flowStage(param);
                    }
                }, "确认候选人进入面试吗？");
                break;

            case 4:
                //安排面试
                new MakeInterViewDialog(FloatingDetailActivity.this, new MakeInterViewDialog.IReceiveListener() {
                    @Override
                    public void onReadyReceive(String interviewType, String interviewTime, String interviewAddress, String remark, String shouldSendNotification) {
                        HashMap<String, String> param = new HashMap<>();
                        param.put("recommendId", recommendId);
                        param.put("interviewTime", interviewTime);
                        param.put("interviewAddress", interviewAddress);
                        param.put("remark", remark);
                        param.put("interviewType", interviewType);
                        param.put("shouldSendNotification", shouldSendNotification);
                        mPresenter.flowInterview(param);
                    }
                }, floatingListDetailBean.realName, floatingListDetailBean.userName, floatingListDetailBean.title);
                break;

            case 7:
                isOneKey = false;
                if (mPresenter.getSplitInfoBeans().size() <= 0) {
                    mPresenter.splitInfoList(recommendId);
                } else {
                    if (mPresenter.getFirmMembers().size() <= 0) {
                        mPresenter.firmMembers();
                    } else {
                        showDialog();
                    }
                }
                break;

            case 8:
                // 入职
                new OfferDialog(FloatingDetailActivity.this, new OfferDialog.IOfferListener() {
                    @Override
                    public void onReadyOffer(String date, String month) {
                        HashMap<String, String> param = new HashMap<>();
                        param.put("recommendId", recommendId);
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
    private void delBtnThree() {
        new MakeInterViewDialog(FloatingDetailActivity.this, new MakeInterViewDialog.IReceiveListener() {
            @Override
            public void onReadyReceive(String interviewType, String interviewTime, String interviewAddress, String remark, String shouldSendNotification) {
                HashMap<String, String> param = new HashMap<>();
                param.put("recommendId", recommendId);
                param.put("interviewTime", interviewTime);
                param.put("interviewAddress", interviewAddress);
                param.put("remark", remark);
                param.put("interviewType", interviewType);
                param.put("shouldSendNotification", shouldSendNotification);
                mPresenter.flowInterview(param);
            }
        }, floatingListDetailBean.realName, floatingListDetailBean.userName, floatingListDetailBean.title);
    }

    private void oneKeyOffer() {
        isOneKey = true;
        if (mPresenter.getSplitInfoBeans().size() <= 0) {
            mPresenter.splitInfoList(recommendId);
        } else {
            if (mPresenter.getFirmMembers().size() <= 0) {
                mPresenter.firmMembers();
            } else {
                showDialog();
            }
        }
    }

    private void showDialog() {
        new MakeOfferDialog(FloatingDetailActivity.this, new MakeOfferDialog.IReceiveListener() {
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

    private void dealStep(int status) {
        ll_action.setVisibility(View.VISIBLE);
        if (UserInfoManager.getInstance().getUserInfo().isCfUser == 1 && status != 0 && status != 2
                && status != 7 && status != 8 && status != 16) {
            tv_one_key.setVisibility(View.VISIBLE);
        } else {
            tv_one_key.setVisibility(View.GONE);
        }
        tvBtnThree.setVisibility(View.GONE);
        if (status == 0) {
            progressBarStatus.setProgress(13);
            tvBtnOne.setText("拒绝");
            tvBtnTwo.setText("接受");
            if (type.equals("1")) {
                tv_process_status.setText("待反馈");
            } else {
                tv_process_status.setText("未处理");
            }
            tv_process_status.setBackgroundResource(R.drawable.bg_white_all_corner_blue);
            tv_process_status.setTextColor(Color.parseColor("#49C781"));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.LEFT;
            tv_process_status.setLayoutParams(lp);
        } else if (status == 1) {
            progressBarStatus.setProgress(13);
            tvBtnOne.setText("淘汰");
            tvBtnTwo.setText("进入面试");
            ivStatusOne.setBackgroundResource(R.drawable.bg_circle_point_green);
            tvStatusOne.setTextColor(Color.parseColor("#49C781"));
            tv_process_status.setText("已接受");
            tv_process_status.setBackgroundResource(R.drawable.bg_white_all_corner_blue);
            tv_process_status.setTextColor(Color.parseColor("#49C781"));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.LEFT;
            tv_process_status.setLayoutParams(lp);
        } else if (status == 2) {
            progressBarStatus.setProgress(13);
            tvBtnOne.setVisibility(View.GONE);
            tvBtnTwo.setText("重新接受");
            tv_process_status.setText("拒绝");
            tv_process_status.setBackgroundResource(R.drawable.bg_white_all_corner_red);
            tv_process_status.setTextColor(this.getResources().getColor(R.color.color_red_900515));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.LEFT;
            tv_process_status.setLayoutParams(lp);
        } else if (status == 4) {
            progressBarStatus.setProgress(38);
            tvBtnOne.setText("淘汰");
            tvBtnTwo.setText("安排面试");
            ivStatusOne.setBackgroundResource(R.drawable.bg_circle_point_green);
            tvStatusOne.setTextColor(Color.parseColor("#49C781"));
            tv_process_status.setText("进入面试阶段");
            tv_process_status.setBackgroundResource(R.drawable.bg_white_all_corner_blue);
            tv_process_status.setTextColor(Color.parseColor("#49C781"));
        } else if (status == 5) {
            progressBarStatus.setProgress(50);
            tvBtnOne.setText("反馈面试结果");
            tvBtnTwo.setVisibility(View.GONE);
            ivStatusOne.setBackgroundResource(R.drawable.bg_circle_point_green);
            ivStatusTwo.setBackgroundResource(R.drawable.bg_circle_point_green);
            tvStatusOne.setTextColor(Color.parseColor("#49C781"));
            tvStatusTwo.setTextColor(Color.parseColor("#49C781"));
            tv_process_status.setText("进入面试阶段");
            tv_process_status.setBackgroundResource(R.drawable.bg_white_all_corner_blue);
            tv_process_status.setTextColor(Color.parseColor("#49C781"));
        } else if (status == 7) {
            progressBarStatus.setProgress(50);
            tvBtnOne.setText("淘汰");
            tvBtnTwo.setVisibility(View.VISIBLE);
            tvBtnTwo.setText("offer");
            tvBtnThree.setVisibility(View.VISIBLE);
            tvBtnThree.setText("安排下一轮");
            ivStatusOne.setBackgroundResource(R.drawable.bg_circle_point_green);
            ivStatusTwo.setBackgroundResource(R.drawable.bg_circle_point_green);
            tvStatusOne.setTextColor(Color.parseColor("#49C781"));
            tvStatusTwo.setTextColor(Color.parseColor("#49C781"));
            tv_process_status.setText("进入面试阶段");
            tv_process_status.setBackgroundResource(R.drawable.bg_white_all_corner_blue);
            tv_process_status.setTextColor(Color.parseColor("#49C781"));
        } else if (status == 8) {
            progressBarStatus.setProgress(63);
            tvBtnOne.setText("淘汰");
            tvBtnTwo.setText("入职");
            ivStatusOne.setBackgroundResource(R.drawable.bg_circle_point_green);
            ivStatusTwo.setBackgroundResource(R.drawable.bg_circle_point_green);
            ivStatusThree.setBackgroundResource(R.drawable.bg_circle_point_green);
            tvStatusOne.setTextColor(Color.parseColor("#49C781"));
            tvStatusTwo.setTextColor(Color.parseColor("#49C781"));
            tvStatusThree.setTextColor(Color.parseColor("#49C781"));
            tv_process_status.setText("已Offer");
            tv_process_status.setBackgroundResource(R.drawable.bg_white_all_corner_blue);
            tv_process_status.setTextColor(Color.parseColor("#49C781"));
            int left = (int) (this.getResources().getDisplayMetrics().widthPixels * 0.5);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(left, 0, 0, 0);
            tv_process_status.setLayoutParams(lp);
        } else if (status == 16) {
            progressBarStatus.setProgress(100);
            tvBtnOne.setText("离职");
            tvBtnTwo.setVisibility(View.GONE);
            ivStatusOne.setBackgroundResource(R.drawable.bg_circle_point_green);
            ivStatusTwo.setBackgroundResource(R.drawable.bg_circle_point_green);
            ivStatusThree.setBackgroundResource(R.drawable.bg_circle_point_green);
            ivStatusFour.setBackgroundResource(R.drawable.bg_circle_point_green);
            tvStatusOne.setTextColor(Color.parseColor("#49C781"));
            tvStatusTwo.setTextColor(Color.parseColor("#49C781"));
            tvStatusThree.setTextColor(Color.parseColor("#49C781"));
            tvStatusFour.setTextColor(Color.parseColor("#49C781"));
            tv_process_status.setText("已入职");
            tv_process_status.setBackgroundResource(R.drawable.bg_white_all_corner_blue);
            tv_process_status.setTextColor(Color.parseColor("#49C781"));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.RIGHT;
            tv_process_status.setLayoutParams(lp);
        } else {
            ll_action.setVisibility(View.GONE);
            if (status == 32) {
                tv_process_status.setText("已离职");
                tv_process_status.setBackgroundResource(R.drawable.bg_white_all_corner_red);
                tv_process_status.setTextColor(this.getResources().getColor(R.color.color_red_900515));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.RIGHT;
                tv_process_status.setLayoutParams(lp);
                progressBarStatus.setProgress(100);
                ivStatusOne.setBackgroundResource(R.drawable.bg_circle_point_green);
                ivStatusTwo.setBackgroundResource(R.drawable.bg_circle_point_green);
                ivStatusThree.setBackgroundResource(R.drawable.bg_circle_point_green);
                ivStatusFour.setBackgroundResource(R.drawable.bg_circle_point_green);
                tvStatusOne.setTextColor(Color.parseColor("#49C781"));
                tvStatusTwo.setTextColor(Color.parseColor("#49C781"));
                tvStatusThree.setTextColor(Color.parseColor("#49C781"));
                tvStatusFour.setTextColor(Color.parseColor("#49C781"));
            } else if (status == 1024) {
                tv_process_status.setText("候选人已淘汰");
                tv_process_status.setBackgroundResource(R.drawable.bg_white_all_corner_red);
                tv_process_status.setTextColor(this.getResources().getColor(R.color.color_red_900515));

                if (floatingListDetailBean.timeLineList.size() > 1) {
                    int process = floatingListDetailBean.timeLineList.get(1).getProcessType();
                    if (process == 2 || process == 4) {
                        progressBarStatus.setProgress(13);
                        ivStatusOne.setBackgroundResource(R.drawable.bg_circle_point_green);
                        tvStatusOne.setTextColor(Color.parseColor("#49C781"));
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.gravity = Gravity.LEFT;
                        tv_process_status.setLayoutParams(lp);
                    } else if (process == 5 || process == 6 || process == 7 || process == 8) {
                        progressBarStatus.setProgress(50);
                        ivStatusOne.setBackgroundResource(R.drawable.bg_circle_point_green);
                        ivStatusTwo.setBackgroundResource(R.drawable.bg_circle_point_green);
                        tvStatusOne.setTextColor(Color.parseColor("#49C781"));
                        tvStatusTwo.setTextColor(Color.parseColor("#49C781"));
                    } else if (process == 9) {
                        progressBarStatus.setProgress(63);
                        ivStatusOne.setBackgroundResource(R.drawable.bg_circle_point_green);
                        ivStatusTwo.setBackgroundResource(R.drawable.bg_circle_point_green);
                        ivStatusThree.setBackgroundResource(R.drawable.bg_circle_point_green);
                        tvStatusOne.setTextColor(Color.parseColor("#49C781"));
                        tvStatusTwo.setTextColor(Color.parseColor("#49C781"));
                        tvStatusThree.setTextColor(Color.parseColor("#49C781"));
                        int left = (int) (this.getResources().getDisplayMetrics().widthPixels * 0.5);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(left, 0, 0, 0);
                        tv_process_status.setLayoutParams(lp);
                    } else if (process == 10) {
                        progressBarStatus.setProgress(100);
                        ivStatusOne.setBackgroundResource(R.drawable.bg_circle_point_green);
                        ivStatusTwo.setBackgroundResource(R.drawable.bg_circle_point_green);
                        ivStatusThree.setBackgroundResource(R.drawable.bg_circle_point_green);
                        ivStatusFour.setBackgroundResource(R.drawable.bg_circle_point_green);
                        tvStatusOne.setTextColor(Color.parseColor("#49C781"));
                        tvStatusTwo.setTextColor(Color.parseColor("#49C781"));
                        tvStatusThree.setTextColor(Color.parseColor("#49C781"));
                        tvStatusFour.setTextColor(Color.parseColor("#49C781"));
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.gravity = Gravity.RIGHT;
                        tv_process_status.setLayoutParams(lp);
                    }
                }
            }
        }

        if (!floatingListDetailBean.isPublisher && !floatingListDetailBean.isRecommender) {
            ll_action.setVisibility(View.GONE);
        }

        if (type.equals("1") && (status == 0 || status == 2)) {
            ll_action.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isStatusChange) {
            // 在父Activity中退出了程序
            setResult(Activity.RESULT_OK);
        }
        return super.onKeyDown(keyCode, event);
    }
}
