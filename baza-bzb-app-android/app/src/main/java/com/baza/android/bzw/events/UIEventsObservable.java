package com.baza.android.bzw.events;

import android.os.Handler;
import android.os.Looper;

import com.baza.android.bzw.log.LogUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by LW on 2016/10/31.
 * Title : 事件管理
 * Note : 目前仅限UI线程处理的事件
 */

public class UIEventsObservable {
    /**
     * 不同类型的事件订阅者集合
     */
    private HashMap<Class<?>, HashMap<Object, ISubscriber>> globalSubscriberMap = new HashMap<>(2);

    private Handler mHandler;

    private static final UIEventsObservable uiObservable = new UIEventsObservable();

    private UIEventsObservable() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static UIEventsObservable getInstance() {
        return uiObservable;
    }

    /**
     * 注册事件订阅
     *
     * @param eventType  订阅的事件类型
     * @param subscriber 订阅者
     *                   (订阅者必须与宿主绑定在一起 ,一个订阅者对应一个消耗订阅事件的宿主)
     */

    public void subscribeEvent(Class<?> eventType, Object subscriberHost, ISubscriber subscriber) {
        if (subscriber == null || subscriberHost == null)
            throw new IllegalArgumentException("subscriberHost and subscriber must not be null  !!!!");
        HashMap<Object, ISubscriber> targetSubscribers = globalSubscriberMap.get(eventType);
        if (targetSubscribers == null) {
            targetSubscribers = new HashMap<>();
            globalSubscriberMap.put(eventType, targetSubscribers);
        }
        targetSubscribers.put(subscriberHost, subscriber);
        LogUtil.d(eventType + "-SubscriberCount = " + targetSubscribers.size());
    }


    /**
     * 解除注册事件订阅
     *
     * @param eventType      订阅的事件类型
     * @param subscriberHost 订阅者所在的宿主(订阅者必须与宿主绑定在一起 一个订阅者对应一个消耗订阅事件的宿主)
     */

    public void stopSubscribeEvent(Class<?> eventType, Object subscriberHost) {
        if (subscriberHost == null)
            throw new IllegalArgumentException("subscriberHost must not be null !!!!");
        HashMap<Object, ISubscriber> targetSubscribers = globalSubscriberMap.get(eventType);
        if (targetSubscribers != null) {
            targetSubscribers.remove(subscriberHost);
            LogUtil.d(eventType + "-SubscriberCount = " + targetSubscribers.size());
        }
    }

    public void postEvent(String action, Object data) {
        postEvent(IDefaultEventsSubscriber.class, action, data, null);
    }

    public void postEvent(String action, Object data, Object sendTag) {
        postEvent(IDefaultEventsSubscriber.class, action, data, sendTag);
    }

    /**
     * 发起一个事件
     *
     * @param eventType 事件所属类型
     * @param action    事件在所属类型中的唯一标识符
     * @param data      本次事件可能携带的数据,可能为空
     * @return 本次事件的Id
     */

    public void postEvent(Class<?> eventType, String action, Object data, Object sendTag) {
        mHandler.post(new TaskRunnable(eventType, action, data, sendTag, globalSubscriberMap));
    }

    private static class TaskRunnable implements Runnable {

        private Class<?> eventType;
        private String action;
        private Object data;
        private Object sendTag;
        private HashMap<Class<?>, HashMap<Object, ISubscriber>> globalSubscriberMap;

        public TaskRunnable(Class<?> eventType, String action, Object data, Object sendTag, HashMap<Class<?>, HashMap<Object, ISubscriber>> globalSubscriberMap) {
            this.eventType = eventType;
            this.action = action;
            this.data = data;
            this.sendTag = sendTag;
            this.globalSubscriberMap = globalSubscriberMap;
        }

        @Override
        public void run() {
            HashMap<Object, ISubscriber> targetSubscribers = globalSubscriberMap.get(eventType);
            if (targetSubscribers == null || targetSubscribers.isEmpty())
                return;
            Iterator<Map.Entry<Object, ISubscriber>> iterator = targetSubscribers.entrySet().iterator();
            Map.Entry<Object, ISubscriber> entry = iterator.next();
            entry.getValue().dispatchEventsToObserver(targetSubscribers, action, data, sendTag);
        }
    }

}
