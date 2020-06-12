package com.baza.android.bzw.businesscontroller.friend.presenter;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.friend.FriendAddResultBean;
import com.baza.android.bzw.bean.friend.FriendInfoResultBean;
import com.baza.android.bzw.bean.friend.FriendListResultBean;
import com.baza.android.bzw.bean.friend.ListNearlyResultBean;
import com.baza.android.bzw.businesscontroller.friend.constant.FriendConst;
import com.baza.android.bzw.businesscontroller.friend.viewinterface.ISearchFriendView;
import com.baza.android.bzw.constant.ActionConst;
import com.baza.android.bzw.constant.CommonConst;
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

public class SearchFriendPresenter extends BasePresenter {
    private ISearchFriendView mSearchFriendView;
    private int mType;
    private List<FriendListResultBean.FriendBean> mFriendList = new ArrayList<>();
    private String mSearchKeyWord;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mCachedSearchCloudRunnable;
    private int mCloudSearchId;

    /**
     * 不要改变mLocalFriends
     */
    private List<FriendListResultBean.FriendBean> mLocalFriends;

    public SearchFriendPresenter(ISearchFriendView mSearchFriendView, Intent intent) {
        this.mSearchFriendView = mSearchFriendView;
        this.mType = intent.getIntExtra("type", FriendConst.TYPE_SEARCH_FRIEND_LOCAL);
    }

    @Override
    public void initialize() {
        subscribeEvents(true);
        boolean localSearch = (mType != FriendConst.TYPE_SEARCH_TO_ADD_FRIEND);
        if (localSearch)
            mLocalFriends = (List<FriendListResultBean.FriendBean>) BZWApplication.getApplication().getCachedTransformData(CommonConst.STR_TRANSFORM_LOACL_FRIEND_LIST);
        mSearchFriendView.callSetMode(mType);
        if (!localSearch)
            getNearlyPersonCount();
    }

    @Override
    public void onDestroy() {
        subscribeEvents(false);
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
    }

    public List<FriendListResultBean.FriendBean> getFriends() {
        return mFriendList;
    }

    public void prepareSearch(String keyWord) {
        if (mType == FriendConst.TYPE_SEARCH_FRIEND_LOCAL)
            searchLocalFriend(keyWord);
        else
            delayToSearchCloud(keyWord);
    }

    /**
     * 云端搜索是根据输入内容实时搜索，输入较快，会执行多次无效的搜索
     * 延迟减少无效的搜索
     */
    private void delayToSearchCloud(String keyWord) {
        mSearchKeyWord = keyWord;
        mCloudSearchId++;
        if (mCachedSearchCloudRunnable == null)
            mCachedSearchCloudRunnable = new Runnable() {
                @Override
                public void run() {
                    searchCloudFriend(mSearchKeyWord);
                }
            };
        mHandler.removeCallbacks(mCachedSearchCloudRunnable);
        mHandler.postDelayed(mCachedSearchCloudRunnable, 300);
    }

    private void searchLocalFriend(String keyWord) {
        if (TextUtils.isEmpty(keyWord)) {
            if (mFriendList.isEmpty())
                return;
            mFriendList.clear();
            mSearchFriendView.callRefreshList();
            return;
        }
        mFriendList.clear();
        matchLocalFriend(keyWord);
        mSearchFriendView.callRefreshList();
        if (mType == FriendConst.TYPE_SEARCH_FRIEND_LOCAL)
            delayToSearchCloud(keyWord);
    }

    private void matchLocalFriend(String keyWord) {
        if (mLocalFriends != null && !mLocalFriends.isEmpty()) {
            keyWord = keyWord.toLowerCase();
            FriendListResultBean.FriendBean fb;
            for (int i = 0, size = mLocalFriends.size(); i < size; i++) {
                fb = mLocalFriends.get(i);
                //将本地好友的isFriend属性全部置为YES
                fb.isFriend = FriendInfoResultBean.FriendInfoBean.FRIEND_YES;
                if ((fb.mobile != null && fb.mobile.contains(keyWord)) || (fb.nickName != null && fb.nickName.toLowerCase().contains(keyWord)) || (fb.trueName != null && fb.trueName.toLowerCase().contains(keyWord)) || (fb.title != null && fb.title.toLowerCase().contains(keyWord)) || (fb.company != null && fb.company.toLowerCase().contains(keyWord)))
                    mFriendList.add(fb);
            }
        }
    }


    private void searchCloudFriend(final String keyWords) {
        if (TextUtils.isEmpty(keyWords))
            return;
        //本地搜索会额外搜索云端，但不需要显示提示用户正在搜搜
        if (mType == FriendConst.TYPE_SEARCH_TO_ADD_FRIEND)
            mSearchFriendView.callShowLoadingView();
        final int searchId = mCloudSearchId;
        FriendDao.searchFriend(keyWords, new IDefaultRequestReplyListener<List<FriendListResultBean.FriendBean>>() {
            @Override
            public void onRequestReply(boolean success, List<FriendListResultBean.FriendBean> friendBeen, int errorCode, String errorMsg) {
                if (searchId != mCloudSearchId)
                    return;
                mSearchFriendView.callCancelLoadingView(success, errorCode, errorMsg);
                if (!success) {
                    if (mType == FriendConst.TYPE_SEARCH_TO_ADD_FRIEND)
                        mSearchFriendView.callUpdateNoDataView((!mFriendList.isEmpty()));
                    return;
                }
                if (mType == FriendConst.TYPE_SEARCH_FRIEND_LOCAL) {
                    //本地搜索后添加云端的结果
                    appendCloudFriendToLocal(friendBeen);
                    mSearchFriendView.callRefreshList();
                    return;
                }

                mFriendList.clear();
                if (friendBeen != null)
                    mFriendList.addAll(friendBeen);
                mSearchFriendView.callRefreshList();
            }
        });
    }

    private void appendCloudFriendToLocal(List<FriendListResultBean.FriendBean> list) {
        if (list == null || list.isEmpty())
            return;
        if (mLocalFriends != null && !mLocalFriends.isEmpty()) {
            //剔除重复的
            FriendListResultBean.FriendBean fbLocal;
            FriendListResultBean.FriendBean fbCloud;
            int sizeCloud = list.size();
            int indexCloud;
            List<FriendListResultBean.FriendBean> temps = new ArrayList<>(mLocalFriends.size());
            for (int i = 0, size = mLocalFriends.size(); i < size; i++) {
                fbLocal = mLocalFriends.get(i);
                for (indexCloud = 0; indexCloud < sizeCloud; indexCloud++) {
                    fbCloud = list.get(indexCloud);
                    if (fbCloud != null && fbCloud.unionId != null && fbCloud.unionId.equals(fbLocal.unionId)) {
                        temps.add(fbCloud);
                        break;
                    }
                }
            }
            if (!temps.isEmpty())
                list.removeAll(temps);
        }
        if (!list.isEmpty()) {
            //null作为占位
            mFriendList.add(null);
            mFriendList.addAll(list);
        }
    }

    public void addFriend(String hello, final FriendListResultBean.FriendBean friendBean) {
        if (friendBean == null)
            return;
        mSearchFriendView.callShowProgress(null, true);
        FriendDao.addFriend(friendBean.unionId, hello, new IDefaultRequestReplyListener<FriendAddResultBean>() {
            @Override
            public void onRequestReply(boolean success, FriendAddResultBean friendAddResultBean, int errorCode, String errorMsg) {
                mSearchFriendView.callCancelProgress();
                if (success) {
                    if (friendAddResultBean.data != null) {
                        mFriendList.remove(friendBean);
                        mSearchFriendView.callRefreshList();
                        mSearchFriendView.callShowToastMessage(null, R.string.add_friend_success);
                        mSearchFriendView.callUpdateNoDataView((!mFriendList.isEmpty()));
                        UIEventsObservable.getInstance().postEvent(IFriendOperateSubscriber.class, ActionConst.ACTION_EVENT_ADD_FRIEND_DIRECTLY, friendAddResultBean.data, null);
                    } else
                        mSearchFriendView.callShowToastMessage(null, R.string.add_friend_request_send);
                    return;
                }
                mSearchFriendView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    private void subscribeEvents(boolean register) {
        if (register) {
            UIEventsObservable.getInstance().subscribeEvent(IFriendOperateSubscriber.class, this, new IFriendOperateSubscriber() {
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
                        mSearchFriendView.callUpdateNoDataView((!mFriendList.isEmpty()));
                        mSearchFriendView.callRefreshList();
                    }

                    return false;
                }
            });
        } else
            UIEventsObservable.getInstance().stopSubscribeEvent(IFriendOperateSubscriber.class, this);
    }

    private void getNearlyPersonCount() {
        FriendDao.getNearlyPersonCount(new IDefaultRequestReplyListener<ListNearlyResultBean.Data>() {
            @Override
            public void onRequestReply(boolean success, ListNearlyResultBean.Data data, int errorCode, String errorMsg) {
                if (success && data != null)
                    mSearchFriendView.callUpdateNearlyPersonCountView(data.total);
            }
        });
    }
}
