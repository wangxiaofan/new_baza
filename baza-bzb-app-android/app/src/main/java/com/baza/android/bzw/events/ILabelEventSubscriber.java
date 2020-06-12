package com.baza.android.bzw.events;


import com.baza.android.bzw.bean.label.Label;
import com.baza.android.bzw.constant.ActionConst;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Vincent.Lei on 2017/3/24.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public abstract class ILabelEventSubscriber implements ISubscriber {
    @Override
    public final void dispatchEventsToObserver(HashMap<Object, ISubscriber> targetSubscribers, String action, Object data, Object filterTag) {
        int eventType = 0;
        if (ActionConst.ACTION_EVENT_LABEL_LIBRARY_GET.equals(action))
            eventType = 1;
        else if (ActionConst.ACTION_EVENT_LABEL_CREATED.equals(action))
            eventType = 2;
        else if (ActionConst.ACTION_EVENT_LABEL_DELETED.equals(action))
            eventType = 3;
        Iterator iterator = targetSubscribers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Object, ISubscriber> entry = (Map.Entry<Object, ISubscriber>) iterator.next();
            ILabelEventSubscriber subscriber = (ILabelEventSubscriber) entry.getValue();
            if (filterTag != null && subscriber.isFilterByTag(filterTag))
                continue;
            boolean intercept;
            switch (eventType) {
                case 1:
                    intercept = subscriber.onLabelLibraryGet();
                    break;
                case 2:
                    intercept = subscriber.onLabelCreated((Label) data);
                    break;
                case 3:
                    intercept = subscriber.onLabelDeleted((Label) data);
                    break;
                default:
                    intercept = subscriber.killEvent(action, data);
                    break;
            }
            if (intercept)
                break;
        }
    }

    public boolean onLabelLibraryGet() {
        return false;
    }


    public boolean onLabelCreated(Label label) {
        return false;
    }

    public boolean onLabelDeleted(Label label) {
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
