package com.baza.android.bzw.businesscontroller.home;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.base.BaseFragment;
import com.baza.android.bzw.bean.user.VersionBean;
import com.baza.android.bzw.businesscontroller.account.TabAccountFragment;
import com.baza.android.bzw.businesscontroller.browser.RemoteBrowserActivity;
import com.baza.android.bzw.businesscontroller.home.presenter.HomePresenter;
import com.baza.android.bzw.businesscontroller.home.viewinterface.IHomeView;
import com.baza.android.bzw.businesscontroller.message.TabMessageFragment;
import com.baza.android.bzw.businesscontroller.message.TabMessageFragment02;
import com.baza.android.bzw.businesscontroller.resume.recommend.RecommendActivity;
import com.baza.android.bzw.businesscontroller.resume.tab.MineResumeFragmentNew;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.constant.CommonUtils;
import com.baza.android.bzw.dao.ActivityPrepareDao;
import com.baza.android.bzw.dao.IMDao;
import com.baza.android.bzw.log.LogUtil;
import com.baza.android.bzw.manager.AppGlobalManager;
import com.baza.android.bzw.widget.dialog.ConfigActivityDialog;
import com.bznet.android.rcbox.R;
import com.slib.utils.ScreenUtil;
import com.tencent.qcloud.tim.uikit.Configs;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.URLConst;
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack;
import com.tencent.qcloud.tim.uikit.bean.IMGetTeamMembersResponseBean;
import com.tencent.qcloud.tim.uikit.bean.IMGetTeamsResponseBean;
import com.tencent.qcloud.tim.uikit.bean.IMSigBean;
import com.tencent.qcloud.tim.uikit.component.UnreadCountTextView;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationLayout;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationListLayout;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationManagerKit;
import com.tencent.qcloud.tim.uikit.modules.conversation.base.ConversationInfo;
import com.tencent.qcloud.tim.uikit.utils.retrofit.RetrofitHelper;
import com.tencent.qcloud.tim.uikit.utils.retrofit.WaitingView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Vincent.Lei on 2017/5/16.
 * Title：
 * Note：
 */

public class HomeActivity extends BaseActivity implements IHomeView, BaseFragment.IFragmentEventsListener, View.OnClickListener {
    /*
     *tab 的索引位不要随意赋值，要与 mFragmentList遍历时下标对应
     */
    public static final int TAB_NONE = -1;
    public static final int TAB_HOME = 0;
    public static final int TAB_TALENT = 1;
    public static final int TAB_MESSAGE = 2;
    public static final int TAB_ACCOUNT = 3;
    @BindView(R.id.rb_home)
    RadioButton radioButton_home;
    @BindView(R.id.rb_candidate)
    RadioButton radioButton_candidate;
    @BindView(R.id.rb_message)
    RadioButton radioButton_message;
    @BindView(R.id.rb_account)
    RadioButton radioButton_account;
    @BindView(R.id.view_has_unread)
    View view_hasUnreadMessage;

    @BindView(R.id.bottom_view)
    View bottom_view;

    private HomePresenter mPresenter;
    private int mCurrentTabIndex;
    private TextView textView_recommendFloat;

    @BindView(R.id.message_unread)
    UnreadCountTextView message_unread;//未读消息计数

    private ArrayList<BaseFragment> mFragmentList = new ArrayList<BaseFragment>(4) {
        {
            add(null);
            add(null);
            add(null);
            add(null);
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_home_tab);
    }

    @Override
    protected boolean isUseDefaultStatusBarBackground() {
        return false;
    }

    @Override
    protected void initWhenCallOnCreate() {
        ButterKnife.bind(this);
        mPresenter = new HomePresenter(this, getIntent());
        mCurrentTabIndex = TAB_NONE;
        radioButton_account.post(new Runnable() {
            @Override
            public void run() {
                changeShowFragment(getIntent().getIntExtra("tabIndex", TAB_TALENT));
            }
        });
        mPresenter.initialize();
        AppGlobalManager.getInstance().attachMainActivity(this);

        //1.获取imcenterToken
        Log.e("herb", "获取imcenterToken>>" + Configs.imcenterToken);
        Log.e("herb", "获取unionId>>" + Configs.unionId);

        getChatList();
    }

    //获取聊天列表数据
    public void getChatList() {
        //2.获取IM sig：https://api.stg.bazhua.me/api/imcenter/
        IMDao.getSig((boolean success, IMSigBean bean, int errorCode, String errorMsg) -> {
            //IM登录返回
            Log.e("herb", "IM登录bean>>" + bean.toString());
            //3.腾讯登录
            WaitingView.getInstance().showWaitingView(this);
            TUIKit.login(Configs.unionId, bean.getData(), new IUIKitCallBack() {
                @Override
                public void onSuccess(Object data) {
                    WaitingView.getInstance().closeWaitingView();
                    Log.e("herb", "IM登录成功>>");
                    //获取未读消息数量
                    ConversationManagerKit.getInstance().loadConversation(new IUIKitCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            if (Configs.mUnreadTotal == 0) {
                                message_unread.setVisibility(View.GONE);
                            } else {
                                message_unread.setVisibility(View.VISIBLE);
                                if (Configs.mUnreadTotal > 999) {
                                    message_unread.setText("···");
                                } else {
                                    message_unread.setText(String.valueOf(Configs.mUnreadTotal));
                                }
                            }
                            CommonUtils.setNotificationBadge(Configs.mUnreadTotal, HomeActivity.this);
                            getTeams();//初始化把所有成员的昵称全存下来，后期需要的话就去查找
                        }

                        @Override
                        public void onError(String module, int errCode, String errMsg) {

                        }
                    });
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    WaitingView.getInstance().closeWaitingView();
                    Log.e("herb", "IM登录失败>>errCode>>" + errCode + ">>errMsg>>" + errMsg);
                }
            });
        });
    }


    //获取公司团队接口
    public void getTeams() {
        Log.e("herb", "开始获取公司团队>>");
        WaitingView.getInstance().showWaitingView(this);
        RetrofitHelper.getInstance().getServer().getTeams(URLConst.getTeams).enqueue(new Callback<IMGetTeamsResponseBean>() {
            @Override
            public void onResponse(Call<IMGetTeamsResponseBean> call, Response<IMGetTeamsResponseBean> response) {
                WaitingView.getInstance().closeWaitingView();
                Log.e("herb", "获取公司团队成功>>" + response.body().toString());
                if (response != null && response.body() != null && response.body().getData() != null
                        && response.body().getData().getChildTeams() != null) {
                    for (int i = 0; i < response.body().getData().getChildTeams().size(); i++) {
                        //获取子成员
                        getTeamMembers(response.body().getData().getChildTeams().get(i));
                    }
                }
            }

            @Override
            public void onFailure(Call<IMGetTeamsResponseBean> call, Throwable t) {
                WaitingView.getInstance().closeWaitingView();
                Log.e("herb", "获取公司团队失败>>" + call.toString());
            }
        });
    }

    //获取团队成员接口
    public void getTeamMembers(IMGetTeamsResponseBean.DataBean dataBean) {
        Log.e("herb", "开始获取团队成员>>");
        Map<String, String> params = new HashMap<>();
        params.put("teamId", dataBean.getTeamId());
        RetrofitHelper.getInstance().getServer().getTeamMembers(URLConst.getTeamMembers, params).enqueue(new Callback<IMGetTeamMembersResponseBean>() {
            @Override
            public void onResponse(Call<IMGetTeamMembersResponseBean> call, Response<IMGetTeamMembersResponseBean> response) {
                if (response.body() != null && response.body().getData() != null) {
                    Log.e("herb", dataBean.getTeamName() + ">>" + response.body().getData().size());
                    for (int i = 0; i < response.body().getData().size(); i++) {
                        if (Configs.nameIdsMap.get(response.body().getData().get(i).getUnionId()) == null || response.body().getData().get(i).getUnionId().equals("")) {
                            Configs.nameIdsMap.put(response.body().getData().get(i).getUnionId(), response.body().getData().get(i).getRealName() + "@" + response.body().getData().get(i).getNickname());
                            Configs.nameIdsList.add(response.body().getData().get(i).getRealName() + "@" + response.body().getData().get(i).getNickname());
                            Log.e("herb", "昵称>>" + response.body().getData().get(i).getRealName() + "@" + response.body().getData().get(i).getNickname());
                            Log.e("herb", "ID>>" + response.body().getData().get(i).getUnionId());
                            Log.e("herb", "长度>>" + Configs.nameIdsMap.size());
                        }
                    }
                    dataBean.setBean(response.body());
                    for (int i = 0; i < dataBean.getChildTeams().size(); i++) {
                        getTeamMembers(dataBean.getChildTeams().get(i));
                    }
                }
            }

            @Override
            public void onFailure(Call<IMGetTeamMembersResponseBean> call, Throwable t) {
                Log.e("herb", "获取团队成员失败>>" + call.toString());
            }
        });
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int hopeIndex = intent.getIntExtra("tabIndex", TAB_NONE);
        if (hopeIndex != TAB_NONE) {
            changeShowFragment(hopeIndex);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
//        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppGlobalManager.getInstance().onMainActivityResume();
        mPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppGlobalManager.getInstance().onMainActivityPause();
    }

    @Override
    protected void onActivityDeadForApp() {
        mPresenter.onDestroy();
        AppGlobalManager.getInstance().onMainActivityDestroy(this);
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    public static void launch(Activity activity) {
        launch(activity, TAB_TALENT);
    }

    public static void launch(Activity activity, int tabIndex) {
        Intent intent = new Intent(activity, HomeActivity.class);
        intent.putExtra("tabIndex", tabIndex);
        activity.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_tab_home:
                changeShowFragment(TAB_HOME);
                break;
            case R.id.rl_tab_candidate:
                changeShowFragment(TAB_TALENT);
                break;
            case R.id.rl_tab_message:
                changeShowFragment(TAB_MESSAGE);
                break;
            case R.id.rl_tab_account:
                changeShowFragment(TAB_ACCOUNT);
                break;
        }
    }

    private void changeShowFragment(int tabIndex) {
        if (tabIndex == mCurrentTabIndex)
            return;
        mCurrentTabIndex = tabIndex;
        radioButton_home.setChecked((mCurrentTabIndex == TAB_HOME));
        radioButton_candidate.setChecked((mCurrentTabIndex == TAB_TALENT));
        radioButton_message.setChecked((mCurrentTabIndex == TAB_MESSAGE));
        radioButton_account.setChecked((mCurrentTabIndex == TAB_ACCOUNT));
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        BaseFragment targetFragment = null;
        BaseFragment fragment;
        for (int index = 0, size = mFragmentList.size(); index < size; index++) {
            fragment = mFragmentList.get(index);
            if (index == mCurrentTabIndex)
                targetFragment = fragment;
            else if (fragment != null)
                fragmentTransaction.hide(fragment);
        }
        if (targetFragment == null) {
            targetFragment = createFragment(tabIndex);
            fragmentTransaction.add(R.id.fl_fragment_container, targetFragment);
        } else
            fragmentTransaction.show(targetFragment);
        fragmentTransaction.commitAllowingStateLoss();
        mPresenter.setIMNotifyEnable((mCurrentTabIndex != TAB_MESSAGE));
        if (mCurrentTabIndex == TAB_MESSAGE)
            callDismissRecommendFloattingView();
    }

    private BaseFragment createFragment(int tabIndex) {
        LogUtil.d("createFragment");
        BaseFragment fragment = null;
        switch (tabIndex) {
            case TAB_HOME:
                fragment = new TabHomeFragment();
                break;
            case TAB_TALENT:
                fragment = new MineResumeFragmentNew();
                break;
            case TAB_MESSAGE:
                fragment = new TabMessageFragment02(bottom_view, message_unread);
                break;
            case TAB_ACCOUNT:
                fragment = new TabAccountFragment();
                break;
        }
        mFragmentList.set(tabIndex, fragment);
        if (fragment != null)
            fragment.setFragmentEventsListener(this);
        return fragment;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("herb", "HomeActivity>>onActivityResult>>跳转回传>>");
        if (mCurrentTabIndex < mFragmentList.size() && mCurrentTabIndex >= 0)
            mFragmentList.get(mCurrentTabIndex).onFragmentResult(requestCode, resultCode, data);
    }

    @Override
    public void callShowNewVersionDialog(VersionBean data) {
        mApplication.showNewVersionDialog(this, data, dialog -> mPresenter.publishDialogShowTask(null, true));
    }

    @Override
    public void callMessageUnReadCountUpdate(int unReadCount) {
        view_hasUnreadMessage.setVisibility((unReadCount > 0 ? View.VISIBLE : View.GONE));
    }

    @Override
    public boolean callIsCurrentIsMessageView() {
        return mCurrentTabIndex == TAB_MESSAGE;
    }

    @Override
    public void callShowConfigActivityDialog(final ActivityPrepareDao.DialogConfig dialogConfig) {
        new ConfigActivityDialog(this, dialogConfig, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoteBrowserActivity.launch(HomeActivity.this, null, true, dialogConfig.mActivityUrl);
            }
        }).show();
        dialogConfig.updateTargetActivityDialogShowCount();
    }

//    @Override
//    public void callShowActivityDialog(ActivityStatusResultBean.Data data) {
//        if (data.id == OperationalDao.ACTIVITY_ID_NEW_VERSION) {
//            DefaultActivityDialog.Param param = new DefaultActivityDialog.Param();
//            param.backgroundImgId = R.drawable.activity_img_invited;
//            param.buttonImgId = R.drawable.activity_btn_bg_process;
//            param.link = data.link;
//            param.id = data.id;
//            param.hideCloseBtn = true;
//            new DefaultActivityDialog(this, param, new DefaultActivityDialog.IConfirmListener() {
//                @Override
//                public void onClose(DefaultActivityDialog.Param param) {
//                    mPresenter.publishDialogShowTask(null, true);
//                    OperationalDao.updateTargetActivityDialogShowCount(UserInfoManager.getInstance().getUserInfo().unionId, param.id);
//                }
//
//                @Override
//                public void onConfirm(DefaultActivityDialog.Param param) {
//                    mPresenter.receiveNewVersionGift();
//                    OperationalDao.updateTargetActivityDialogShowCount(UserInfoManager.getInstance().getUserInfo().unionId, param.id);
//                }
//            }).show();
//        }
//    }

    @Override
    public void callUpdateRecommendFloattingView(String msg) {
        if (textView_recommendFloat == null) {
            textView_recommendFloat = new TextView(HomeActivity.this);
            textView_recommendFloat.setTextSize(TypedValue.COMPLEX_UNIT_DIP, ScreenUtil.px2dip(mResources.getDimension(R.dimen.text_size_10)));
            textView_recommendFloat.setTextColor(mResources.getColor(R.color.text_color_blue_0D315C));
            textView_recommendFloat.setGravity(Gravity.CENTER);
            textView_recommendFloat.setBackgroundResource(R.drawable.recommend_float_bg);
            textView_recommendFloat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callDismissRecommendFloattingView();
                    RecommendActivity.launch(HomeActivity.this);
                }
            });
        }
        int[] outLocation = new int[2];
        radioButton_message.getLocationOnScreen(outLocation);
        textView_recommendFloat.setText(msg);
        int width = (int) (textView_recommendFloat.getPaint().measureText(msg) + 2 * (int) mResources.getDimension(R.dimen.dp_10));
        int height = (int) mResources.getDimension(R.dimen.dp_35);
        FrameLayout.LayoutParams fml = new FrameLayout.LayoutParams(width, height);
        fml.leftMargin = outLocation[0] + radioButton_message.getWidth() / 2 - width / 2;
        fml.topMargin = outLocation[1] - height;

        textView_recommendFloat.setLayoutParams(fml);
        if (textView_recommendFloat.getParent() == null)
            ((FrameLayout) getWindow().getDecorView()).addView(textView_recommendFloat);
    }

    @Override
    public void callDismissRecommendFloattingView() {
        if (callIsRecommendFloattingViewShown()) {
            ((FrameLayout) getWindow().getDecorView()).removeView(textView_recommendFloat);
            textView_recommendFloat = null;
        }
    }

    @Override
    public boolean callIsRecommendFloattingViewShown() {
        return textView_recommendFloat != null && textView_recommendFloat.getParent() != null;
    }

    @Override
    public void onBackPressed() {
        mPresenter.setIMNotifyEnable(true);
        moveTaskToBack(false);
    }

    @Override
    public Object onFragmentEventsArrival(int eventId, Object input) {
        if (eventId == AdapterEventIdConst.EVENT_ID_IS_HOME_TARGET_INDEX_FRAGMENT_SHOWN) {
            int requireType = (int) input;
            return requireType == mCurrentTabIndex;
        } else if (eventId == AdapterEventIdConst.EVENT_MESSAGE_COUNT) {
            int unreadCount = (int) input;
            mPresenter.setWaitProcessUnreadCount(unreadCount);
        }
        return null;
    }
}
