package com.baza.android.bzw.businesscontroller.message;

import androidx.appcompat.widget.ListPopupWindow;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.base.BaseFragment;
import com.baza.android.bzw.businesscontroller.friend.FriendListActivity;
import com.baza.android.bzw.businesscontroller.im.IMConst;
import com.baza.android.bzw.businesscontroller.im.IMRecentContact;
import com.baza.android.bzw.businesscontroller.message.adapter.ConversationListAdapter;
import com.baza.android.bzw.businesscontroller.message.presenter.TabMessagePresenter;
import com.baza.android.bzw.businesscontroller.message.viewinterface.ISystemView;
import com.baza.android.bzw.businesscontroller.message.viewinterface.ITabMessageView;
import com.baza.android.bzw.businesscontroller.resume.recommend.RecommendActivity;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.manager.AppGlobalManager;
import com.baza.android.bzw.widget.LoadingView;
import com.bznet.android.rcbox.R;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.slib.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;

import baza.dialog.simpledialog.MaterialDialog;

/**
 * Created by Vincent.Lei on 2017/5/18.
 * Title：消息
 * Note：
 */

public class TabMessageFragment extends BaseFragment implements ITabMessageView, BaseBZWAdapter.IAdapterEventsListener, View.OnClickListener {
    private PullToRefreshListView pullToRefreshListView;
    private ListView listView;
    private LoadingView loadingView;
    private TextView textView_onLineStatus;
    private TextView textView_sysMsgUnreadCount;
    private TextView textView_recommendUnCompleteCount;
    //    private TextView textView_processUnreadCount;
    private View view_newFriendHint;
    private ConversationListAdapter mAdapter;
    private TabMessagePresenter mPresenter;
    private ListPopupWindow mListPopMessageDel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_message;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_tab_message);
    }

    @Override
    protected void initWhenOnCreatedViews(View mRootView) {
        mPresenter = new TabMessagePresenter(this);
        mRootView.findViewById(R.id.tv_right_click).setOnClickListener(this);
        textView_onLineStatus = mRootView.findViewById(R.id.tv_onLineStatus);
        loadingView = LoadingView.findSelf(mRootView, 0);
        pullToRefreshListView = mRootView.findViewById(R.id.pull_to_refresh_listView);
        pullToRefreshListView.setFootReboundInsteadLoad(true);
        pullToRefreshListView.setHeadReboundInsteadRefresh(true);
        listView = pullToRefreshListView.getRefreshableView();
        view_newFriendHint = mRootView.findViewById(R.id.view_new_friend_hint);
        View headView = getLayoutInflater().inflate(R.layout.tab_message_head_action, null);
        textView_sysMsgUnreadCount = headView.findViewById(R.id.tv_sys_unread_count);
        textView_recommendUnCompleteCount = headView.findViewById(R.id.tv_recommend_unread_count);
//        textView_processUnreadCount = headView.findViewById(R.id.tv_process_unread_count);
        headView.findViewById(R.id.cl_msg_sys).setOnClickListener(this);
//        headView.findViewById(R.id.cl_wait_process).setOnClickListener(this);
        headView.findViewById(R.id.cl_baza_helper).setOnClickListener(this);
        headView.findViewById(R.id.cl_msg_recommend).setOnClickListener(this);
        listView.addHeaderView(headView);
    }

    @Override
    protected void initWhenOnActivityCreated() {
        Log.e("herb","加载列表>>");
        mAdapter = new ConversationListAdapter(getActivity(), mPresenter.getRecentContactList(), this);
        listView.setAdapter(mAdapter);
        mPresenter.initialize();
    }

    @Override
    protected void changedUIToFitSDKReachKITKAT(int statusBarHeight) {

    }

    @Override
    public void callCancelInnerProgress() {
        loadingView.finishLoading();
    }

    @Override
    public void callShowInnerProgress(int msg) {
        loadingView.startLoading(msg);
    }

    @Override
    public void callRefreshTargetRecentView(final int targetPosition) {
        pullToRefreshListView.post(new Runnable() {
            @Override
            public void run() {
                if (targetPosition < 0) {
                    mAdapter.notifyDataSetChanged();
                } else {
                    View view = AppUtil.getTargetVisibleView(targetPosition, listView);
                    if (view != null)
                        mAdapter.refreshTargetView(view, targetPosition);
                }
            }
        });

    }

    @Override
    public void callOnLineStatusChanged(int statusCode) {
        switch (statusCode) {
//            case IMConst.IM_ONLINE_STATUS_NO_NET:
//                textView_onLineStatus.setText(R.string.im_status_net_broken);
//                textView_onLineStatus.setVisibility(View.VISIBLE);
//                break;
//            case IMConst.IM_ONLINE_STATUS_CONNECTING:
//                textView_onLineStatus.setText(R.string.im_status_connecting);
//                textView_onLineStatus.setVisibility(View.VISIBLE);
//                break;
//            case IMConst.IM_ONLINE_STATUS_LOGINING:
//                textView_onLineStatus.setText(R.string.im_status_logining);
//                textView_onLineStatus.setVisibility(View.VISIBLE);
//                break;
//            case IMConst.IM_ONLINE_STATUS_UNLOGIN:
//                textView_onLineStatus.setText(R.string.im_status_unlogining);
//                textView_onLineStatus.setVisibility(View.VISIBLE);
//                break;
//            case IMConst.IM_ONLINE_STATUS_KICKOUT:
//                textView_onLineStatus.setText(R.string.im_status_kickout);
//                textView_onLineStatus.setVisibility(View.VISIBLE);
//                AppGlobalManager.getInstance().forceLogoutForSinglePlatform();
//                break;
//            case IMConst.IM_ONLINE_STATUS_PWD_ERROR:
//                textView_onLineStatus.setText(R.string.im_status_account_error);
//                textView_onLineStatus.setVisibility(View.VISIBLE);
//                break;
            default:
                textView_onLineStatus.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void callUpdateFriendRequestView(int requestCount) {
//        view_newFriendHint.setVisibility(requestCount > 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void callUpdateNoticeUnreadCountView(int systemMsgCount, int processCount) {
        if (mIFragmentEventsListener != null)
            mIFragmentEventsListener.onFragmentEventsArrival(AdapterEventIdConst.EVENT_MESSAGE_COUNT, systemMsgCount + processCount);
        if (systemMsgCount > 0) {
            textView_sysMsgUnreadCount.setText(systemMsgCount > 99 ? "99+" : String.valueOf((systemMsgCount)));
            textView_sysMsgUnreadCount.setVisibility(View.VISIBLE);
        } else
            textView_sysMsgUnreadCount.setVisibility(View.GONE);
//        if (processCount > 0) {
//            textView_processUnreadCount.setText(processCount > 99 ? "99+" : String.valueOf(processCount));
//            textView_processUnreadCount.setVisibility(View.VISIBLE);
//        } else
//            textView_processUnreadCount.setVisibility(View.GONE);
    }

    @Override
    public void callUpdateRecommendCountView(int unCompleteCount) {
        if (unCompleteCount > 0) {
            textView_recommendUnCompleteCount.setText(unCompleteCount > 99 ? "99+" : String.valueOf((unCompleteCount)));
            textView_recommendUnCompleteCount.setVisibility(View.VISIBLE);
        } else
            textView_recommendUnCompleteCount.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null)
            mPresenter.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mPresenter.onHiddenChanged(hidden);
    }

    @Override
    protected void onFragmentDeadForApp() {
        super.onFragmentDeadForApp();
        if (mPresenter != null)
            mPresenter.onDestroy();
    }

    @Override
    public void onAdapterEventsArrival(int adapterEventId, int position, View v, Object input) {
        switch (adapterEventId) {
            case AdapterEventIdConst.CONVERSATION_ADAPTER_EVENT_TO_CHAT:
                IMRecentContact imRecentContact = (IMRecentContact) input;
                ChatActivity.launch(getActivity(), new ChatActivity.ChatParam(imRecentContact.nimRecentContact.getContactId(), imRecentContact.getSessionType()));
                break;
            case AdapterEventIdConst.SYSTEM_MESSAGE_ADAPTER_EVENT_OPEN:
                SystemMessageActivity.launch(getActivity(), ISystemView.MSG_BAZA_HELPER);
                break;
            case AdapterEventIdConst.CONVERSATION_ADAPTER_EVENT_ITEM_LONG_CLICK:
                final IMRecentContact imRecentContact2 = (IMRecentContact) input;
                if (mListPopMessageDel == null) {
                    List<String> list = new ArrayList<>(2);
                    list.add(mResources.getString(R.string.delete));
                    list.add(mResources.getString(R.string.cancel));
                    mListPopMessageDel = new ListPopupWindow(getActivity());
                    mListPopMessageDel.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.list_pop_defaylt_menu_item, list));
                    mListPopMessageDel.setWidth(v.getMeasuredWidth() / 3);
                    mListPopMessageDel.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                    mListPopMessageDel.setHorizontalOffset(v.getMeasuredWidth() / 3);
                    mListPopMessageDel.setModal(true);
                }
                mListPopMessageDel.setAnchorView(v);
                mListPopMessageDel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0)
                            showDeleteMsgDialog(imRecentContact2);
                        mListPopMessageDel.dismiss();
                    }
                });
                mListPopMessageDel.show();
                break;
        }
    }

    private void showDeleteMsgDialog(final IMRecentContact imRecentContact) {
        MaterialDialog materialDialog = new MaterialDialog(getActivity());
        materialDialog.buildTitle(0).buildMessage(R.string.delete_im_msg_hint).buildCancelButtonText(0).buildSureButtonText(R.string.delete).buildSureButtonColor(mResources.getColor(R.color.color_red_FF6564)).buildClickListener(null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.deleteConversation(imRecentContact);
            }
        });
        materialDialog.setCancelable(false);
        materialDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_right_click:
                FriendListActivity.launch(getActivity());
                break;
            case R.id.cl_msg_sys:
                SystemMessageActivity.launch(getActivity(), ISystemView.MSG_SYSTEM);
                break;
//            case R.id.cl_wait_process:
//                ProcessEventsActivity.launch(getActivity(), IResumeEventTypeView.WAIT_PROCESS_BY_ME);
//                break;
            case R.id.cl_baza_helper:
                SystemMessageActivity.launch(getActivity(), ISystemView.MSG_BAZA_HELPER);
                break;
            case R.id.cl_msg_recommend:
                RecommendActivity.launch(getActivity());
                break;
        }
    }
}
