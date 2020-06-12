package com.baza.android.bzw.events;

import java.util.HashMap;

/**
 * Created by LW on 2016/10/31.
 * Title :事件订阅顶级接口
 * Note :
 */

public interface ISubscriber {
    /**
     * 事件分发机制
     *
     * @param targetSubscribers 目标订阅者集合
     * @param action            本次事件唯一标识符
     * @param data              本次事件可能携带的数据,可能为空
     */
    void dispatchEventsToObserver(HashMap<Object, ISubscriber> targetSubscribers, String action, Object data, Object sendTag);

    /**
     * 默认处理简历事件
     *
     * @param data   本次事件可能携带的数据,可能为空
     * @param action 本次事件唯一标识符
     * @return false不屏蔽事件  true屏蔽事件
     */
    boolean killEvent(String action, Object data);

    /**
     * 设置过滤标记匹配规则
     *
     * @param sendTag 本次事件所携带的过滤tag
     * @return 过滤结果
     */
    boolean isFilterByTag(Object sendTag);
}
