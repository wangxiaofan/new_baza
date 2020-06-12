package com.baza.android.bzw.events;

import com.baza.android.bzw.constant.ActionConst;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Vincent.Lei on 2017/6/28.
 * Title：
 * Note：
 */

public class IFriendOperateSubscriber implements ISubscriber {
    @Override
    public void dispatchEventsToObserver(HashMap<Object, ISubscriber> targetSubscribers, String action, Object data, Object sendTag) {
        int eventType = 0;
        if (ActionConst.ACTION_EVENT_ADD_FRIEND_DIRECTLY.equals(action))
            eventType = 1;
        else if (ActionConst.ACTION_EVENT_DELETE_FRIEND_DIRECTLY.equals(action))
            eventType = 2;
        else if (ActionConst.ACTION_EVENT_ADD_FRIEND_ASK.equals(action))
            eventType = 3;
        Iterator iterator = targetSubscribers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Object, IFriendOperateSubscriber> entry = (Map.Entry<Object, IFriendOperateSubscriber>) iterator.next();
            IFriendOperateSubscriber subscriber = entry.getValue();
            if (sendTag != null && subscriber.isFilterByTag(sendTag))
                continue;
            boolean intercept;
            switch (eventType) {
                case 1:
                    intercept = subscriber.onAddFriendDirectly(data);
                    break;
                case 2:
                    intercept = subscriber.onDeleteFriendDirectly(data);
                    break;
                case 3:
                    intercept = subscriber.onAddFriendAsk(data);
                    break;
                default:
                    intercept = subscriber.killEvent(action, data);
                    break;
            }
            if (intercept)
                break;
        }
    }

    public boolean onAddFriendDirectly(Object data) {
        return false;
    }

    public boolean onAddFriendAsk(Object data) {
        return false;
    }

    public boolean onDeleteFriendDirectly(Object data) {
        return false;
    }

    @Override
    public boolean killEvent(String action, Object data) {
        return false;
    }

    @Override
    public boolean isFilterByTag(Object sendTag) {
        return false;
    }
}
