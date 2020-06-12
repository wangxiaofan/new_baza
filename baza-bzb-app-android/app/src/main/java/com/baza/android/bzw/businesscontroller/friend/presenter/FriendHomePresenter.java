package com.baza.android.bzw.businesscontroller.friend.presenter;

import android.content.Intent;

import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.friend.DynamicListResultBean;
import com.baza.android.bzw.bean.friend.FriendAddResultBean;
import com.baza.android.bzw.bean.friend.FriendInfoResultBean;
import com.baza.android.bzw.dao.FriendDao;
import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.businesscontroller.friend.viewinterface.IFriendHomeView;
import com.baza.android.bzw.constant.ActionConst;
import com.baza.android.bzw.events.IFriendOperateSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/9/26.
 * Title：
 * Note：
 */

public class FriendHomePresenter extends BasePresenter {
    private IFriendHomeView mFriendHomeView;
    private String mUnionId;
    private FriendInfoResultBean.FriendInfoBean mFriendInfoBean;
    private List<DynamicListResultBean.DynamicBean> mDynamicList = new ArrayList<>();
    private String[] mMoreMenu;
    private int pageNo;

    public FriendHomePresenter(IFriendHomeView mFriendHomeView, Intent intent) {
        this.mFriendHomeView = mFriendHomeView;
        this.mUnionId = intent.getStringExtra("unionId");
    }

    @Override
    public void initialize() {
        getFriendInfo();
        getTargetDynamicList(true);
    }

    public String[] getMoreEditMenu() {
        if (mMoreMenu == null) {
            mMoreMenu = new String[]{mFriendHomeView.callGetResources().getString(R.string.delete_friend)};
        }
        return mMoreMenu;
    }

    public FriendInfoResultBean.FriendInfoBean getFriendInfoData() {
        return mFriendInfoBean;
    }

    public void getFriendInfo() {
        FriendDao.getFriendInfo(mUnionId, new IDefaultRequestReplyListener<FriendInfoResultBean.FriendInfoBean>() {
            @Override
            public void onRequestReply(boolean success, FriendInfoResultBean.FriendInfoBean friendInfoBean, int errorCode, String errorMsg) {
                mFriendHomeView.callCancelLoadingView(success, errorCode, errorMsg);
                if (success && friendInfoBean != null) {
                    mFriendInfoBean = friendInfoBean;
                    mFriendHomeView.callUpdateFriendInfoView();
                }
            }
        });
    }

    public List<DynamicListResultBean.DynamicBean> getDynamicData() {
        return mDynamicList;
    }

    public void getTargetDynamicList(boolean refresh) {
        if (refresh)
            pageNo = 1;
        FriendDao.getTargetFriendDynamic(mUnionId, pageNo, new IDefaultRequestReplyListener<DynamicListResultBean.Data>() {
            @Override
            public void onRequestReply(boolean success, DynamicListResultBean.Data data, int errorCode, String errorMsg) {
                if (pageNo == 1)
                    mDynamicList.clear();
                if (success) {
                    if (data != null) {
                        if (data.list != null)
                            mDynamicList.addAll(data.list);
                        mFriendHomeView.callSetLoadMoreEnable((mDynamicList.size() < data.total));
                    }
                    mFriendHomeView.callUpdateNoDataView(mDynamicList.size() > 0);
                    pageNo++;
                }
                mFriendHomeView.callRefreshDynamicViews();
            }
        });
    }

    public void deleteFriend() {
        if (mFriendInfoBean == null)
            return;
        mFriendHomeView.callShowProgress(null, true);
        FriendDao.deleteFriend(mFriendInfoBean.unionId, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
            @Override
            public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                mFriendHomeView.callCancelProgress();
                if (success) {
                    UIEventsObservable.getInstance().postEvent(IFriendOperateSubscriber.class, ActionConst.ACTION_EVENT_DELETE_FRIEND_DIRECTLY, mFriendInfoBean, null);
                    mFriendHomeView.callGetBindActivity().finish();
                    return;
                }
                mFriendHomeView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    public void addFriend(String hello) {
        if (mFriendInfoBean == null)
            return;
        mFriendHomeView.callShowProgress(null, true);
        FriendDao.addFriend(mFriendInfoBean.unionId, hello, new IDefaultRequestReplyListener<FriendAddResultBean>() {
            @Override
            public void onRequestReply(boolean success, FriendAddResultBean friendAddResultBean, int errorCode, String errorMsg) {
                mFriendHomeView.callCancelProgress();
                if (success) {
                    if (friendAddResultBean.data != null) {
                        mFriendInfoBean.isFriend = FriendInfoResultBean.FriendInfoBean.FRIEND_YES;
                        mFriendHomeView.callUpdateFriendInfoView();
                        mFriendHomeView.callShowToastMessage(null, R.string.add_friend_success);
                        UIEventsObservable.getInstance().postEvent(IFriendOperateSubscriber.class, ActionConst.ACTION_EVENT_ADD_FRIEND_DIRECTLY, friendAddResultBean.data, null);
                    } else {
                        UIEventsObservable.getInstance().postEvent(IFriendOperateSubscriber.class, ActionConst.ACTION_EVENT_ADD_FRIEND_ASK, mFriendInfoBean.unionId, null);
                        mFriendHomeView.callShowToastMessage(null, R.string.add_friend_request_send);
                    }
                    return;
                }
                mFriendHomeView.callShowToastMessage(errorMsg, 0);
            }
        });
    }
}
