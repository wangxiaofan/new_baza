package com.baza.android.bzw.businesscontroller.friend.presenter;

import android.text.TextUtils;

import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.friend.FriendContactRecordBean;
import com.baza.android.bzw.bean.friend.FriendListResultBean;
import com.baza.android.bzw.businesscontroller.friend.viewinterface.IFriendSelectedView;
import com.baza.android.bzw.dao.FriendDao;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.manager.UserInfoManager;
import com.slib.storage.database.listener.IDBReplyListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Vincent.Lei on 2018/1/11.
 * Title：
 * Note：
 */

public class FriendSelectedPresenter extends BasePresenter {
    private IFriendSelectedView mFriendSelectedView;
    private List<FriendListResultBean.FriendBean> mFriendListShow = new ArrayList<>();
    private List<FriendListResultBean.FriendBean> mFriendListOriginal;

    public FriendSelectedPresenter(IFriendSelectedView friendSelectedView) {
        this.mFriendSelectedView = friendSelectedView;
    }

    @Override
    public void initialize() {
        loadFriendList();
    }

    @Override
    public void onDestroy() {
    }

    public List<FriendListResultBean.FriendBean> getFriends() {
        return mFriendListShow;
    }

    public void loadFriendList() {
        FriendDao.getFriendList(new IDefaultRequestReplyListener<List<FriendListResultBean.FriendBean>>() {
            @Override
            public void onRequestReply(boolean success, List<FriendListResultBean.FriendBean> friendList, int errorCode, String errorMsg) {
                if (success) {
                    if (friendList != null && !friendList.isEmpty()) {
                        mFriendListOriginal = friendList;
                        sortByContactTime();
                    } else
                        mFriendSelectedView.callCancelLoadingView(true, errorCode, errorMsg);
                }
                mFriendSelectedView.callUpdateNoDataView((mFriendListOriginal != null && !mFriendListOriginal.isEmpty()));
                if (!success) {
                    mFriendSelectedView.callCancelLoadingView(false, errorCode, errorMsg);
                    mFriendSelectedView.callShowToastMessage(errorMsg, 0);
                }
            }
        });
    }

    private void onSortReady() {
        mFriendSelectedView.callCancelLoadingView(true, 0, null);
        mFriendListShow.addAll(mFriendListOriginal);
        mFriendSelectedView.callRefreshFriendsView();
    }

    private void sortByContactTime() {
        FriendDao.readFriendContactRecords(UserInfoManager.getInstance().getUserInfo().unionId, new IDBReplyListener<List<FriendContactRecordBean>>() {
            @Override
            public void onDBReply(List<FriendContactRecordBean> friendContactRecordBeans) {
                if (friendContactRecordBeans != null && !friendContactRecordBeans.isEmpty()) {
                    FriendListResultBean.FriendBean friendBean;
                    FriendContactRecordBean friendContactRecordBean;
                    for (int i = 0, size = friendContactRecordBeans.size(); i < size; i++) {
                        friendContactRecordBean = friendContactRecordBeans.get(i);
                        for (int j = 0, count = mFriendListOriginal.size(); j < count; j++) {
                            friendBean = mFriendListOriginal.get(j);
                            if (friendContactRecordBean.contactNeteaseId != null && friendContactRecordBean.contactNeteaseId.equals(friendBean.neteaseId)) {
                                friendBean.contactTime = friendContactRecordBean.contactTime;
                                break;
                            }
                        }
                    }
                    Collections.sort(mFriendListOriginal, new Comparator<FriendListResultBean.FriendBean>() {
                        @Override
                        public int compare(FriendListResultBean.FriendBean o1, FriendListResultBean.FriendBean o2) {
                            long time = o1.contactTime - o2.contactTime;
                            return (time == 0 ? 0 : (time > 0 ? -1 : 1));
                        }
                    });

                }
                try {
                    onSortReady();
                } catch (Exception e) {
                    //ignore
                }
            }
        });
    }

    public void matchLocalFriend(String keyWord) {
        if (mFriendListOriginal == null || mFriendListOriginal.isEmpty())
            return;
        mFriendListShow.clear();
        if (TextUtils.isEmpty(keyWord))
            mFriendListShow.addAll(mFriendListOriginal);
        else {
            keyWord = keyWord.toLowerCase();
            FriendListResultBean.FriendBean fb;
            for (int i = 0, size = mFriendListOriginal.size(); i < size; i++) {
                fb = mFriendListOriginal.get(i);
                //将本地好友的isFriend属性全部置为YES
                if ((fb.mobile != null && fb.mobile.contains(keyWord)) || (fb.nickName != null && fb.nickName.toLowerCase().contains(keyWord)) || (fb.trueName != null && fb.trueName.toLowerCase().contains(keyWord)) || (fb.title != null && fb.title.toLowerCase().contains(keyWord)) || (fb.company != null && fb.company.toLowerCase().contains(keyWord)))
                    mFriendListShow.add(fb);
            }
        }
        mFriendSelectedView.callRefreshFriendsView();
    }

    public ArrayList<FriendListResultBean.FriendBean> getSelectedList(HashSet<String> mSelectedSet) {
        if (mSelectedSet == null || mSelectedSet.isEmpty() || mFriendListShow.isEmpty())
            return null;
        ArrayList<FriendListResultBean.FriendBean> list = new ArrayList<>();
        for (int i = 0, size = mFriendListShow.size(); i < size; i++) {
            if (mSelectedSet.contains(mFriendListShow.get(i).unionId))
                list.add(mFriendListShow.get(i));
        }
        return list;
    }
}
