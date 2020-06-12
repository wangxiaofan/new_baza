package com.baza.android.bzw.events;


import com.baza.android.bzw.bean.resume.ResumeBean;
import com.baza.android.bzw.constant.ActionConst;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by LW on 2016/10/31.
 * Title : 简历事件订阅
 * Note :
 */

public abstract class IResumeEventsSubscriber implements ISubscriber {

    /**
     * 事件分发机制
     *
     * @param targetSubscribers 目标订阅者集合
     * @param action            本次事件唯一标识符
     * @param data              本次事件可能携带的数据,可能为空
     */
    @Override
    public final void dispatchEventsToObserver(HashMap<Object, ISubscriber> targetSubscribers, String action, Object data, Object filterTag) {
        int eventType = 0;
        if (ActionConst.ACTION_EVENT_COLLECTION_RESUME_CHANGED.equals(action))
            eventType = 1;
        else if (ActionConst.ACTION_EVENT_CREATED_RESUME.equals(action))
            eventType = 2;
        else if (ActionConst.ACTION_EVENT_MODIFY_RESUME.equals(action))
            eventType = 3;
        else if (ActionConst.ACTION_EVENT_DELETED_RESUME.equals(action))
            eventType = 4;
        else if (ActionConst.ACTION_EVENT_SHOULD_RESET_SCAN_HISTORY.equals(action))
            eventType = 5;
        else if (ActionConst.ACTION_EVENT_DELETE_RESUME_HISTORY.equals(action))
            eventType = 6;
        else if (ActionConst.ACTION_EVENT_RESUME_LABEL_CHANGED.equals(action))
            eventType = 7;
        else if (ActionConst.ACTION_EVENT_RESUME_IMPORT.equals(action))
            eventType = 8;
        else if (ActionConst.ACTION_EVENT_REQUEST_SHARE_RESUME.equals(action))
            eventType = 9;
        else if (ActionConst.ACTION_EVENT_RESUME_UPDATE_BY_ENGINE.equals(action))
            eventType = 10;
        else if (ActionConst.ACTION_EVENT_RESUME_TARGET_TALENT_GET.equals(action))
            eventType = 11;
        Iterator iterator = targetSubscribers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Object, ISubscriber> entry = (Map.Entry<Object, ISubscriber>) iterator.next();
            IResumeEventsSubscriber subscriber = (IResumeEventsSubscriber) entry.getValue();
            if (filterTag != null && subscriber.isFilterByTag(filterTag))
                continue;
            boolean intercept;
            switch (eventType) {
                case 1:
                    intercept = subscriber.onResumeCollectionStatusChangedObserver((ResumeBean) data, null);
                    break;
                case 2:
                    intercept = subscriber.onResumeCreatedObserver((ResumeBean) data, null);
                    break;
                case 3:
                    intercept = subscriber.onResumeModifiedObserver((ResumeBean) data, null);
                    break;
                case 4:
                    intercept = subscriber.onResumeDeletedObserver((ResumeBean) data, null);
                    break;
                case 5:
                    intercept = subscriber.onResumeBeWatchedObserver((ResumeBean) data, null);
                    break;
                case 6:
                    intercept = subscriber.onResumeHistoryBeDeleted((ResumeBean) data, null);
                    break;
                case 7:
                    intercept = subscriber.onResumeLabelsChanged((ResumeBean) data, null);
                    break;
                case 8:
                    intercept = subscriber.onResumeImport((ResumeBean) data, null);
                    break;
                case 9:
                    intercept = subscriber.onResumeRequireShare((ResumeBean) data, null);
                    break;
                case 10:
                    intercept = subscriber.onResumeUpdateByEngine((ResumeBean) data, null);
                    break;
                case 11:
                    intercept = subscriber.onResumeTargetTalentGet((ResumeBean) data, null);
                    break;
                default:
                    intercept = subscriber.killEvent(action, data);
                    break;
            }
            if (intercept)
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

    /**
     * 简历收藏状态改变
     *
     * @param data  本次事件可能携带的数据,可能为空
     * @param extra 本次事件可能携带其他的内容例如标记等
     * @return false不屏蔽事件  true屏蔽事件
     */
    public boolean onResumeCollectionStatusChangedObserver(ResumeBean data, Object extra) {
        return false;
    }

    /**
     * 新建简历事件
     *
     * @param data  本次事件可能携带的数据,可能为空
     * @param extra 本次事件可能携带其他的内容例如标记等
     * @return false不屏蔽事件  true屏蔽事件
     */
    public boolean onResumeCreatedObserver(ResumeBean data, Object extra) {
        return false;
    }

    /**
     * 修改新建的简历事件
     *
     * @param data  本次事件可能携带的数据,可能为空
     * @param extra 本次事件可能携带其他的内容例如标记等
     * @return false不屏蔽事件  true屏蔽事件
     */
    public boolean onResumeModifiedObserver(ResumeBean data, Object extra) {
        return false;
    }

    /**
     * 删除新建的简历事件
     *
     * @param data  本次事件可能携带的数据,可能为空
     * @param extra 本次事件可能携带其他的内容例如标记等
     * @return false不屏蔽事件  true屏蔽事件
     */
    public boolean onResumeDeletedObserver(ResumeBean data, Object extra) {
        return false;
    }


    /**
     * 查看了简历事件
     *
     * @param data  本次事件可能携带的数据,可能为空
     * @param extra 本次事件可能携带其他的内容例如标记等
     * @return false不屏蔽事件  true屏蔽事件
     */
    public boolean onResumeBeWatchedObserver(ResumeBean data, Object extra) {
        return false;
    }

    /**
     * 删除了简历中的某条简历历史
     *
     * @param data  本次事件可能携带的数据,可能为空
     * @param extra 本次事件可能携带其他的内容例如标记等
     * @return false不屏蔽事件  true屏蔽事件
     */
    public boolean onResumeHistoryBeDeleted(ResumeBean data, Object extra) {
        return false;
    }

    /**
     * 简历修改了标签
     *
     * @param data  本次事件可能携带的数据,可能为空
     * @param extra 本次事件可能携带其他的内容例如标记等
     * @return false不屏蔽事件  true屏蔽事件
     */
    public boolean onResumeLabelsChanged(ResumeBean data, Object extra) {
        return false;
    }

    /**
     * 简历修改了标签
     *
     * @param data  本次事件可能携带的数据,可能为空
     * @param extra 本次事件可能携带其他的内容例如标记等
     * @return false不屏蔽事件  true屏蔽事件
     */
    public boolean onResumeImport(ResumeBean data, Object extra) {
        return false;
    }

    /**
     * 请求分享简历
     *
     * @param data  本次事件可能携带的数据,可能为空
     * @param extra 本次事件可能携带其他的内容例如标记等
     * @return false不屏蔽事件  true屏蔽事件
     */
    public boolean onResumeRequireShare(ResumeBean data, Object extra) {
        return false;
    }

    /**
     * 更新引擎更新简历
     *
     * @param data  本次事件可能携带的数据,可能为空
     * @param extra 本次事件可能携带其他的内容例如标记等
     * @return false不屏蔽事件  true屏蔽事件
     */
    public boolean onResumeUpdateByEngine(ResumeBean data, Object extra) {
        return false;
    }

    /**
     * 获得该简历对应的人才
     *
     * @param data  本次事件可能携带的数据,可能为空
     * @param extra 本次事件可能携带其他的内容例如标记等
     * @return false不屏蔽事件  true屏蔽事件
     */
    public boolean onResumeTargetTalentGet(ResumeBean data, Object extra) {
        return false;
    }
   
    /**
     * 默认处理简历事件
     *
     * @param data 本次事件可能携带的数据,可能为空
     * @return false不屏蔽事件  true屏蔽事件
     */
    @Override
    public boolean killEvent(String action, Object data) {
        return false;
    }
}
