package com.baza.android.bzw.businesscontroller.friend.presenter;

import android.content.Intent;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.friend.FriendInfoResultBean;
import com.baza.android.bzw.bean.friend.FriendListResultBean;
import com.baza.android.bzw.businesscontroller.friend.viewinterface.IFriendListView;
import com.baza.android.bzw.businesscontroller.im.IFriendMsgObserve;
import com.baza.android.bzw.dao.FriendDao;
import com.baza.android.bzw.events.IFriendOperateSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.manager.IMManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/9/21.
 * Title：
 * Note：
 */

public class FriendListPresenter extends BasePresenter {
    private IFriendListView mFriendListView;
    private List<FriendListResultBean.FriendBean> mFriendList = new ArrayList<>();
    private IFriendMsgObserve mFriendMsgObserve;

    public FriendListPresenter(IFriendListView friendListView, Intent intent) {
        this.mFriendListView = friendListView;
    }

    @Override
    public void initialize() {
        subscribeEvents(true);
        loadFriendList();
    }

    public List<FriendListResultBean.FriendBean> getFriends() {
        return mFriendList;
    }

    @Override
    public void onDestroy() {
        subscribeEvents(false);
    }

    private void subscribeEvents(boolean register) {
        if (register) {
            UIEventsObservable.getInstance().subscribeEvent(IFriendOperateSubscriber.class, this, new IFriendOperateSubscriber() {
                @Override
                public boolean onAddFriendDirectly(Object data) {
                    if (data == null)
                        return false;
                    FriendListResultBean.FriendBean friendBean = (FriendListResultBean.FriendBean) data;
                    mFriendList.add(friendBean);
                    mFriendListView.callUpdateNoDataView((!mFriendList.isEmpty()));
                    mFriendListView.callRefreshFriendsView();
                    return false;
                }

                @Override
                public boolean onDeleteFriendDirectly(Object data) {
                    if (data == null)
                        return false;
                    FriendInfoResultBean.FriendInfoBean friendInfoBean = (FriendInfoResultBean.FriendInfoBean) data;
                    if (mFriendList.isEmpty())
                        return false;
                    FriendListResultBean.FriendBean friendBean;
                    boolean needRefresh = false;
                    for (int i = 0, size = mFriendList.size(); i < size; i++) {
                        friendBean = mFriendList.get(i);
                        if (friendBean.unionId != null && friendBean.unionId.equals(friendInfoBean.unionId)) {
                            mFriendList.remove(i);
                            needRefresh = true;
                            break;
                        }
                    }
                    if (needRefresh) {
                        mFriendListView.callUpdateNoDataView((!mFriendList.isEmpty()));
                        mFriendListView.callRefreshFriendsView();
                    }
                    return false;
                }
            });
            if (mFriendMsgObserve == null)
                mFriendMsgObserve = new IFriendMsgObserve() {
                    @Override
                    public void onFriendNoticeMsgUnreadCountChanged(int unreadCount) {
                        mFriendListView.callFriendNoticeUnReadCountUpdate(unreadCount);
                    }

                    @Override
                    public void onFriendRequestGet(String account) {

                    }

                    @Override
                    public void onFriendAgreeGet(String account) {
                        loadFriendList();
                    }
                };
            IMManager.getInstance(mFriendListView.callGetBindActivity()).registerFriendMsgObserve(mFriendMsgObserve, true);
        } else {
            UIEventsObservable.getInstance().stopSubscribeEvent(IFriendOperateSubscriber.class, this);
            if (mFriendMsgObserve != null)
                IMManager.getInstance(mFriendListView.callGetBindActivity()).registerFriendMsgObserve(mFriendMsgObserve, false);

        }
    }

    public void loadFriendList() {
        mFriendListView.callShowLoadingView();
        FriendDao.getFriendList(new IDefaultRequestReplyListener<List<FriendListResultBean.FriendBean>>() {
            @Override
            public void onRequestReply(boolean success, List<FriendListResultBean.FriendBean> friendBeen, int errorCode, String errorMsg) {
                mFriendListView.callCancelLoadingView(success, errorCode, errorMsg);
                if (success) {
                    mFriendList.clear();
                    if (friendBeen != null)
                        mFriendList.addAll(friendBeen);
                    mFriendListView.callRefreshFriendsView();
                }
                mFriendListView.callUpdateNoDataView((!mFriendList.isEmpty()));
                if (!success)
                    mFriendListView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

}
