package com.baza.android.bzw.events;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by LW on 2016/10/31.
 * Title : 默认处理事件订阅接口
 * Note :
 */

public abstract class IDefaultEventsSubscriber implements ISubscriber {
    /**
     * 事件分发机制
     *
     * @param targetSubscribers 目标订阅者集合
     * @param action            本次事件唯一标识符
     * @param data              本次事件可能携带的数据,可能为空
     */
    @Override
    public final void dispatchEventsToObserver(HashMap<Object, ISubscriber> targetSubscribers, String action, Object data, Object filterTag) {
        Iterator<Map.Entry<Object, ISubscriber>> iterator = targetSubscribers.entrySet().iterator();
        Map.Entry<Object, ISubscriber> entry;
        while (iterator.hasNext()) {
            entry = iterator.next();
            ISubscriber subscriber = entry.getValue();
            if (filterTag != null && subscriber.isFilterByTag(filterTag))
                continue;
            if (subscriber.killEvent(action, data))
                break;
        }
    }

    /**
     * 设置过滤标记匹配规则
     *
     * @param sendTag 本次事件所携带的过滤tag
     * @return 过滤结果
     */
    @Override
    public boolean isFilterByTag(Object sendTag) {
        return false;
    }
}
