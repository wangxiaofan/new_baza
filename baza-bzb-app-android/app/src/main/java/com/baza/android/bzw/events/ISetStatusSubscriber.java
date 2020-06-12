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

public class ISetStatusSubscriber implements ISubscriber {
    @Override
    public void dispatchEventsToObserver(HashMap<Object, ISubscriber> targetSubscribers, String action, Object data, Object sendTag) {
        int eventType = 0;
        if (ActionConst.ACTION_EVENT_TALENT_SHARE_OPEN.equals(action))
            eventType = 1;
        else if (ActionConst.ACTION_EVENT_TALENT_SHARE_CLOSE.equals(action))
            eventType = 2;
        else if (ActionConst.ACTION_EVENT_TALENT_INTERESTED_CHANGED.equals(action))
            eventType = 3;
        else if (ActionConst.ACTION_EVENT_TRADE_OPEN.equals(action))
            eventType = 4;
        else if (ActionConst.ACTION_EVENT_TRADE_CLOSE.equals(action))
            eventType = 5;
        Iterator iterator = targetSubscribers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Object, ISubscriber> entry = (Map.Entry<Object, ISubscriber>) iterator.next();
            ISetStatusSubscriber subscriber = (ISetStatusSubscriber) entry.getValue();
            if (sendTag != null && subscriber.isFilterByTag(sendTag))
                continue;
            boolean intercept;
            switch (eventType) {
                case 1:
                    intercept = subscriber.onTalentShareOpen(data);
                    break;
                case 2:
                    intercept = subscriber.onTalentShareClose(data);
                    break;
                case 3:
                    intercept = subscriber.onTalentInterestedChanged(data);
                    break;
                case 4:
                    intercept = subscriber.onTradeOpen(data);
                    break;
                case 5:
                    intercept = subscriber.onTradeClose(data);
                    break;
                default:
                    intercept = subscriber.killEvent(action, data);
                    break;
            }
            if (intercept)
                break;
        }
    }

    public boolean onTalentShareOpen(Object extra) {
        return false;
    }

    public boolean onTalentShareClose(Object extra) {
        return false;
    }

    public boolean onTradeClose(Object extra) {
        return false;
    }

    public boolean onTradeOpen(Object extra) {
        return false;
    }

    public boolean onTalentInterestedChanged(Object extra) {
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
