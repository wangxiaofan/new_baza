package com.baza.android.bzw.businesscontroller.friend.presenter;

import com.baza.android.bzw.bean.friend.DynamicListResultBean;
import com.baza.android.bzw.dao.FriendDao;
import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.businesscontroller.friend.viewinterface.IDynamicView;
import com.baza.android.bzw.events.IFriendOperateSubscriber;
import com.baza.android.bzw.events.UIEventsObservable;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/9/28.
 * Title：
 * Note：
 */

public class DynamicPresenter extends BasePresenter {
    private IDynamicView mDynamicView;
    private List<DynamicListResultBean.DynamicBean> mDynamicList = new ArrayList<>();
    private int pageNo;

    public DynamicPresenter(IDynamicView mDynamicView) {
        this.mDynamicView = mDynamicView;
    }

    @Override
    public void initialize() {
        registerObserver(true);
        getFriendDynamicList(true);
    }

    @Override
    public void onDestroy() {
        registerObserver(false);
    }

    public List<DynamicListResultBean.DynamicBean> getDynamicData() {
        return mDynamicList;
    }

    public void getFriendDynamicList(boolean refresh) {
        if (refresh)
            pageNo = 1;
        FriendDao.loadFriendDynamic(pageNo, new IDefaultRequestReplyListener<DynamicListResultBean.Data>() {
            @Override
            public void onRequestReply(boolean success, DynamicListResultBean.Data data, int errorCode, String errorMsg) {
                mDynamicView.callCancelLoadingView(success, errorCode, errorMsg);
                if (pageNo == 1)
                    mDynamicList.clear();
                if (success) {
                    if (data != null) {
                        if (data.list != null)
                            mDynamicList.addAll(data.list);
                        mDynamicView.callUpdateLoadAllDataView((mDynamicList.size() >= data.total));
                    }
                    pageNo++;
                }
                mDynamicView.callRefreshListItems();
            }
        });
    }

    private void registerObserver(boolean register) {
        if (register) {
            UIEventsObservable.getInstance().subscribeEvent(IFriendOperateSubscriber.class, this, new IFriendOperateSubscriber() {
                @Override
                public boolean onDeleteFriendDirectly(Object data) {
                    getFriendDynamicList(true);
                    return false;
                }
            });
        } else {
            UIEventsObservable.getInstance().stopSubscribeEvent(IFriendOperateSubscriber.class, this);
        }
    }
}
