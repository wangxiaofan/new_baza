package com.baza.android.bzw.businesscontroller.friend.presenter;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.friend.FriendAddResultBean;
import com.baza.android.bzw.bean.friend.FriendListResultBean;
import com.baza.android.bzw.bean.friend.ListNearlyResultBean;
import com.baza.android.bzw.businesscontroller.friend.viewinterface.INearlyPersonView;
import com.baza.android.bzw.constant.ActionConst;
import com.baza.android.bzw.dao.FriendDao;
import com.baza.android.bzw.events.IFriendOperateSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/9/21.
 * Title：
 * Note：
 */

public class NearlyPersonPresenter extends BasePresenter {
    private INearlyPersonView mNearlyPersonView;
    private List<FriendListResultBean.FriendBean> mFriendList = new ArrayList<>();
    private int mPageNo;

    public NearlyPersonPresenter(INearlyPersonView mNearlyPersonView) {
        this.mNearlyPersonView = mNearlyPersonView;
    }

    @Override
    public void initialize() {
        listNearlyPerson(true);
    }

    public List<FriendListResultBean.FriendBean> getFriends() {
        return mFriendList;
    }

    public void listNearlyPerson(final boolean refresh) {
        FriendDao.listNearlyPerson((refresh ? 1 : mPageNo), new IDefaultRequestReplyListener<ListNearlyResultBean.Data>() {
            @Override
            public void onRequestReply(boolean success, ListNearlyResultBean.Data data, int errorCode, String errorMsg) {
                mNearlyPersonView.callCancelLoadingView(success, errorCode, errorMsg);
                if (!success)
                    mNearlyPersonView.callShowToastMessage(errorMsg, 0);
                if (success && data != null) {
                    if (refresh) {
                        mPageNo = 1;
                        mFriendList.clear();
                    }
                    if (data.list != null && !data.list.isEmpty())
                        mFriendList.addAll(data.list);
                    mNearlyPersonView.callRefreshList();
                    mNearlyPersonView.callSetLoadDataStatus((mFriendList.size() >= data.total));
                    mPageNo++;
                    mNearlyPersonView.callUpdateNoDataView((!mFriendList.isEmpty()));
                }
            }
        });
    }

    public void addFriend(String hello, final FriendListResultBean.FriendBean friendBean) {
        if (friendBean == null)
            return;
        mNearlyPersonView.callShowProgress(null, true);
        FriendDao.addFriend(friendBean.unionId, hello, new IDefaultRequestReplyListener<FriendAddResultBean>() {
            @Override
            public void onRequestReply(boolean success, FriendAddResultBean friendAddResultBean, int errorCode, String errorMsg) {
                mNearlyPersonView.callCancelProgress();
                if (success) {
                    mFriendList.remove(friendBean);
                    mNearlyPersonView.callRefreshList();
                    if (friendAddResultBean.data != null) {
                        mNearlyPersonView.callShowToastMessage(null, R.string.add_friend_success);
                        mNearlyPersonView.callUpdateNoDataView((!mFriendList.isEmpty()));
                        UIEventsObservable.getInstance().postEvent(IFriendOperateSubscriber.class, ActionConst.ACTION_EVENT_ADD_FRIEND_DIRECTLY, friendAddResultBean.data, null);
                    } else
                        mNearlyPersonView.callShowToastMessage(null, R.string.add_friend_request_send);
                    return;
                }
                mNearlyPersonView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

}
