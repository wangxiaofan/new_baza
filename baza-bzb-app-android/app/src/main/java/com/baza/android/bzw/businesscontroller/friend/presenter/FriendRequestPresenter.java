package com.baza.android.bzw.businesscontroller.friend.presenter;

import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.friend.FriendListResultBean;
import com.baza.android.bzw.businesscontroller.im.IFriendMsgObserve;
import com.baza.android.bzw.dao.FriendDao;
import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.businesscontroller.friend.viewinterface.IFriendRequestView;
import com.baza.android.bzw.constant.ActionConst;
import com.baza.android.bzw.events.IFriendOperateSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.manager.IMManager;
import com.baza.android.bzw.manager.NFManager;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/9/22.
 * Title：
 * Note：
 */

public class FriendRequestPresenter extends BasePresenter {
    private IFriendRequestView mFriendRequestView;
    private List<FriendListResultBean.FriendBean> mFriendList = new ArrayList<>();
    private IFriendMsgObserve mFriendMsgObserve;
    private String[] mListOperateMenu;

    public FriendRequestPresenter(IFriendRequestView mFriendRequestView) {
        this.mFriendRequestView = mFriendRequestView;
    }

    @Override
    public void initialize() {
        subscribeEvents(true);
        getFriendRequestList();
    }

    @Override
    public void onResume() {
        IMManager.getInstance(mFriendRequestView.callGetBindActivity()).setFriendNotifyEnable(false);
        NFManager.cancelFriendNotification();
    }

    @Override
    public void onDestroy() {
        IMManager.getInstance(mFriendRequestView.callGetBindActivity()).clearFriendMsgUnreadCount();
        subscribeEvents(false);
        IMManager.getInstance(mFriendRequestView.callGetBindActivity()).setFriendNotifyEnable(true);
    }

    public List<FriendListResultBean.FriendBean> getFriends() {
        return mFriendList;
    }

    private void subscribeEvents(boolean register) {
        if (register) {
            if (mFriendMsgObserve == null)
                mFriendMsgObserve = new IFriendMsgObserve() {
                    @Override
                    public void onFriendNoticeMsgUnreadCountChanged(int unreadCount) {
                    }

                    @Override
                    public void onFriendRequestGet(String account) {
                        getFriendRequestList();
                    }

                    @Override
                    public void onFriendAgreeGet(String account) {
                        getFriendRequestList();
                    }
                };
            IMManager.getInstance(mFriendRequestView.callGetBindActivity()).registerFriendMsgObserve(mFriendMsgObserve, true);
        } else {
            if (mFriendMsgObserve != null)
                IMManager.getInstance(mFriendRequestView.callGetBindActivity()).registerFriendMsgObserve(mFriendMsgObserve, false);

        }
    }

    public void getFriendRequestList() {
        mFriendRequestView.callShowLoadingView();
        FriendDao.getFriendRequestList(new IDefaultRequestReplyListener<List<FriendListResultBean.FriendBean>>() {
            @Override
            public void onRequestReply(boolean success, List<FriendListResultBean.FriendBean> friendBeen, int errorCode, String errorMsg) {
                mFriendRequestView.callCancelLoadingView(success, errorCode, errorMsg);
                if (success) {
                    mFriendList.clear();
                    if (friendBeen != null)
                        mFriendList.addAll(friendBeen);
                    mFriendRequestView.callRefreshFriendsView();
                }
                mFriendRequestView.callUpdateNoDataView((!mFriendList.isEmpty()));
                if (!success)
                    mFriendRequestView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public void agreeFriend(final FriendListResultBean.FriendBean friendBean) {
        mFriendRequestView.callShowProgress(null, true);
        FriendDao.replyFriendRequest(friendBean.unionId, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                mFriendRequestView.callCancelProgress();
                if (success) {
                    friendBean.requestStatus = FriendListResultBean.FriendBean.TYPE_AGREE;
                    mFriendRequestView.callRefreshFriendsView();
                    UIEventsObservable.getInstance().postEvent(IFriendOperateSubscriber.class, ActionConst.ACTION_EVENT_ADD_FRIEND_DIRECTLY, friendBean, null);
                    return;
                }
                mFriendRequestView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public void deleteRequestRecord(final FriendListResultBean.FriendBean friendBean) {
        mFriendRequestView.callShowProgress(null, true);
        FriendDao.deleteFriendRequest(friendBean.id, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                mFriendRequestView.callCancelProgress();
                if (success) {
                    mFriendList.remove(friendBean);
                    mFriendRequestView.callRefreshFriendsView();
                    mFriendRequestView.callUpdateNoDataView((!mFriendList.isEmpty()));
                    return;
                }
                mFriendRequestView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public String[] getListOperateMenu() {
        if (mListOperateMenu == null) {
            mListOperateMenu = new String[1];
            mListOperateMenu[0] = mFriendRequestView.callGetResources().getString(R.string.delete);
        }
        return mListOperateMenu;
    }
}
