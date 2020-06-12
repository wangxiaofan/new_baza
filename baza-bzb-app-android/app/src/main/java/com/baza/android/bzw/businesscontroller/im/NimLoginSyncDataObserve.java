package com.baza.android.bzw.businesscontroller.im;

import com.baza.android.bzw.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.constant.LoginSyncStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/6/1.
 * Title：
 * Note：
 */

public class NimLoginSyncDataObserve {
    //登陆后数据同步状态
    private LoginSyncStatus mSyncStatus = LoginSyncStatus.NO_BEGIN;
    //监听登陆后数据同步状态观察者集合
    private List<Observer<Void>> mLoginSyncStatusObservers = new ArrayList<>();
    private Observer<LoginSyncStatus> mNimLoginSyncStatusObserver = new Observer<LoginSyncStatus>() {
        @Override
        public void onEvent(LoginSyncStatus status) {
            mSyncStatus = status;
            if (status == LoginSyncStatus.BEGIN_SYNC) {
                LogUtil.d("start sync nim data after nim login");
            } else if (status == LoginSyncStatus.SYNC_COMPLETED) {
                onLoginSyncDataCompleted();
            }
        }
    };


    /**
     * 在App启动时向SDK注册登录后同步数据过程状态的通知
     * 调用时机：主进程Application onCreate中
     */
    public void registerLoginSyncDataStatus(boolean register) {
        LogUtil.d(register ? "register nim sync data observer" : "unregister nim sync data observer");
        NIMClient.getService(AuthServiceObserver.class).observeLoginSyncDataStatus(mNimLoginSyncStatusObserver, register);
    }

    /**
     * 监听登录后同步数据完成事件，缓存构建完成后自动取消监听
     * 调用时机：登录成功后
     *
     * @param observer 观察者
     * @return 返回true表示数据同步已经完成或者不进行同步，返回false表示正在同步数据
     */
    public boolean observeSyncDataCompletedEvent(Observer<Void> observer) {
        if (mSyncStatus == LoginSyncStatus.NO_BEGIN || mSyncStatus == LoginSyncStatus.SYNC_COMPLETED) {
            /*
            * NO_BEGIN 如果登录后未开始同步数据，那么可能是自动登录的情况:
            * PUSH进程已经登录同步数据完成了，此时UI进程启动后并不知道，这里直接视为同步完成
            */
            return true;
        }
        // 正在同步
        if (!mLoginSyncStatusObservers.contains(observer)) {
            mLoginSyncStatusObservers.add(observer);
        }
        return false;
    }

    public void unObserveSyncDataCompletedEvent(Observer<Void> observer) {
        mLoginSyncStatusObservers.remove(observer);
    }

    /**
     * 登录同步数据完成处理
     */
    private void onLoginSyncDataCompleted() {
        for (int i = 0, size = mLoginSyncStatusObservers.size(); i < size; i++) {
            mLoginSyncStatusObservers.get(i).onEvent(null);
        }
        // 重置状态
        reset();
    }

    /**
     * 注销时清除状态&监听
     */
    public void reset() {
        mSyncStatus = LoginSyncStatus.NO_BEGIN;
        mLoginSyncStatusObservers.clear();
    }
}
