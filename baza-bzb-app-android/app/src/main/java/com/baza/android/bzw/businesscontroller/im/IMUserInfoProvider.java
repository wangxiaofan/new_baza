package com.baza.android.bzw.businesscontroller.im;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.baza.android.bzw.bean.user.OtherUserInfoResultBean;
import com.baza.android.bzw.bean.user.UserInfoBean;
import com.baza.android.bzw.dao.AccountDao;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.manager.UserInfoManager;
import com.slib.memorycache.MemoryCache;
import com.slib.memorycache.MemoryCacheUtil;
import com.slib.storage.database.listener.IDBReplyListener;
import com.slib.utils.LoadImageUtil;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/6/1.
 * Title：
 * Note：
 */

public class IMUserInfoProvider implements UserInfoProvider {
    //    private Context mContext;
    private static final int MAX_SMALL_AVATAR_CACHE_SIZE = 1024 * 1024 * 8;
    private static final int SMALL_AVATAR_WH = 60;
    private HashMap<String, String> mUserInfoOnLoad;
    private HashMap<String, UserInfoBean> mUserInfoMap;
    private HashMap<String, BZWNimUserInfo> mNimUserInfoMap;
    private ArrayList<IUserInfoObserve> mObserves;
    private static IMUserInfoProvider userInfoProvider;
    private MemoryCache mSmallAvatarCache;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private IMUserInfoProvider(Context mContext) {
//        this.mContext = mContext;
        this.mUserInfoOnLoad = new HashMap<>();
        this.mUserInfoMap = new HashMap<>();
        this.mNimUserInfoMap = new HashMap<>();
        this.mObserves = new ArrayList<>(3);
        this.mSmallAvatarCache = MemoryCacheUtil.createMemoryCache(mContext, MAX_SMALL_AVATAR_CACHE_SIZE);
    }

    public static IMUserInfoProvider getInstance(Context mContext) {
        if (userInfoProvider == null) {
            synchronized (IMUserInfoProvider.class) {
                if (userInfoProvider == null)
                    userInfoProvider = new IMUserInfoProvider(mContext.getApplicationContext());
            }
        }
        return userInfoProvider;
    }

    @Override
    public UserInfo getUserInfo(String account) {
        return mNimUserInfoMap.get(account);
    }

    private Bitmap getSmallAvatarBitmap(final String avatar) {
        if (TextUtils.isEmpty(avatar))
            return null;
        final String cacheKey = (avatar + "_" + SMALL_AVATAR_WH + "*" + SMALL_AVATAR_WH);
        Bitmap bitmap = mSmallAvatarCache.get(cacheKey);
        if (bitmap == null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    LoadImageUtil.loadImage(avatar, SMALL_AVATAR_WH, SMALL_AVATAR_WH, new LoadImageUtil.ILoadBitmapListener() {
                        @Override
                        public void onResourceReady(Bitmap resource) {
                            if (resource != null)
                                mSmallAvatarCache.put(cacheKey, resource);
                        }
                    });
                }
            });

        }
        return bitmap;
    }

    @Override
    public String getDisplayNameForMessageNotifier(String account, String sessionId, SessionTypeEnum sessionType) {
        BZWNimUserInfo bzwNimUserInfo = mNimUserInfoMap.get(account);
        if (bzwNimUserInfo != null)
            return bzwNimUserInfo.getName();
        UserInfoBean userInfoBean = getBZWUserInfo(account);
        if (userInfoBean == null)
            return null;
        bzwNimUserInfo = new BZWNimUserInfo(userInfoBean, account);
        mNimUserInfoMap.put(account, bzwNimUserInfo);
        return bzwNimUserInfo.getName();
    }

    @Override
    public Bitmap getAvatarForMessageNotifier(SessionTypeEnum sessionType, String sessionId) {
        if (sessionType == SessionTypeEnum.P2P) {
            BZWNimUserInfo bzwNimUserInfo = mNimUserInfoMap.get(sessionId);
            if (bzwNimUserInfo != null) {
                return getSmallAvatarBitmap(bzwNimUserInfo.getAvatar());
            }
        }
        return null;
    }

    public UserInfoBean getBZWUserInfo(String id) {
        if (id == null)
            return null;
        UserInfoBean userInfoBean = mUserInfoMap.get(id);
        if (userInfoBean != null)
            return userInfoBean;
        if (mUserInfoOnLoad.get(id) == null) {
            mUserInfoOnLoad.put(id, id);
            loadUserInfo(id);
        }
        return null;
    }

    public void refreshUserInfoByAccount(String account) {
        if (!TextUtils.isEmpty(account) && mUserInfoOnLoad.get(account) == null) {
            mUserInfoOnLoad.put(account, account);
            loadUserInfo(account);
        }
    }

    private void loadUserInfo(final String idStr) {
        AccountDao.getImUserInfo(idStr, null, new IDefaultRequestReplyListener<OtherUserInfoResultBean>() {
            @Override
            public void onRequestReply(boolean success, OtherUserInfoResultBean otherUserInfoResultBean, int errorCode, String errorMsg) {
                mUserInfoOnLoad.remove(idStr);
                if (success && otherUserInfoResultBean.data != null && !otherUserInfoResultBean.data.isEmpty()) {
                    UserInfoBean user = otherUserInfoResultBean.data.get(0);
                    mUserInfoMap.put(idStr, user);
                    notifyUserInfoChanged(user);
                    AccountDao.saveOtherUserInfo(user);
                }
            }
        });
    }

    public void registerUserInfoObserve(IUserInfoObserve observe, boolean register) {
        if (observe == null)
            return;
        if (register)
            mObserves.add(observe);
        else
            mObserves.remove(observe);
    }

    private void notifyUserInfoChanged(UserInfoBean user) {
        if (!mObserves.isEmpty()) {
            for (int i = 0, size = mObserves.size(); i < size; i++) {
                mObserves.get(i).onUserInfoGet(user);
            }
        }
    }

    public void updateMySelfInfo(UserInfoBean userInfoBean) {
        if (!TextUtils.isEmpty(userInfoBean.neteaseId)) {
            mUserInfoMap.put(userInfoBean.neteaseId, userInfoBean);
            notifyUserInfoChanged(userInfoBean);
        }
    }

    public void initLocalInfo() {
        //读取数据库存储的信息
        AccountDao.readLocalOtherUserInfo(new IDBReplyListener<List<UserInfoBean>>() {
            @Override
            public void onDBReply(List<UserInfoBean> list) {
                if (list != null && !list.isEmpty()) {
                    UserInfoBean ui;
                    for (int i = 0, size = list.size(); i < size; i++) {
                        ui = list.get(i);
                        mUserInfoMap.put(ui.neteaseId, ui);
                    }
                }
                //添加自己
                UserInfoBean userInfoBean = UserInfoManager.getInstance().getUserInfo();
                if (userInfoBean.userId > 0)
                    mUserInfoMap.put(userInfoBean.neteaseId, userInfoBean);
            }
        });
    }
}
