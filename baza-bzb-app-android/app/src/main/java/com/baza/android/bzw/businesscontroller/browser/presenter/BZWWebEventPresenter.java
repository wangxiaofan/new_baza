package com.baza.android.bzw.businesscontroller.browser.presenter;

import android.content.res.Resources;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.baza.android.bzw.base.BasePresenter;
import com.baza.android.bzw.bean.common.JsCallBean;
import com.baza.android.bzw.bean.resumeelement.ResumeSyncResultBean;
import com.baza.android.bzw.bean.user.RankBean;
import com.baza.android.bzw.bean.user.WeeklyReportResultBean;
import com.baza.android.bzw.businesscontroller.browser.viewinterface.IRemoteBrowserView;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.ConfigConst;
import com.baza.android.bzw.constant.RPCConst;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.dao.AccountDao;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.log.LogUtil;
import com.baza.android.bzw.widget.dialog.ShareDialog;
import com.bznet.android.rcbox.R;
import com.slib.http.FileLoadTool;
import com.slib.http.IFileLoadObserve;
import com.slib.multiprocesssimpleconnect.RPCConnectManager;
import com.slib.multiprocesssimpleconnect.RPCConnectWrapper;
import com.slib.utils.AppUtil;

import org.json.JSONObject;

import java.io.File;

import social.data.ShareData;

/**
 * Created by Vincent.Lei on 2019/7/8.
 * Title：
 * Note：
 */
public class BZWWebEventPresenter extends BasePresenter implements IFileLoadObserve {
    private static final int JS_CALL_EVENT_SHARE = 1;
    private static final int JS_CALL_EVENT_SHARE_INVITE = 2;

    private BZWUrlPresenter mUrlPresenter;
    private IRemoteBrowserView mRemoteBrowserView;
    private RankBean mRankBean;
    private String mCacheImageUrlForShare;

    public BZWWebEventPresenter(BZWUrlPresenter mUrlPresenter, IRemoteBrowserView mRemoteBrowserView) {
        this.mUrlPresenter = mUrlPresenter;
        this.mRemoteBrowserView = mRemoteBrowserView;
    }

    @Override
    public void initialize() {

    }

    public void share(String url, int type, String data) {
        LogUtil.d("shareUrl = " + url);
        LogUtil.d("shareData = " + data);
        url = mUrlPresenter.getCompatibleForOldVersionUrl(url);
        LogUtil.d("correctUrl = " + url);
        if (TextUtils.isEmpty(url))
            return;
        switch (type) {
            case CommonConst.SHARE_TYPE_WEEKLY_REPORT:
                shareWeekReport(url, data);
                break;
            case CommonConst.SHARE_TYPE_RESUME_SYNC:
                shareResumeSync(url, data);
                break;
            case CommonConst.SHARE_TYPE_RESUME_RANK:
                shareRank();
                break;
        }
    }

    public void onJsCall(int eventId, String jsonData) {
        LogUtil.d(jsonData);
        switch (eventId) {
            case JS_CALL_EVENT_SHARE:
            case JS_CALL_EVENT_SHARE_INVITE:
                if (eventId == JS_CALL_EVENT_SHARE_INVITE) {
                    //邀请码分享
                    if (!TextUtils.isEmpty(mCacheImageUrlForShare))
                        loadInvitedCodeImg();
                    else
                        shareInviteCodeInfo();
                    return;
                }
                JsCallBean.JsShareBean jsShareBean = parseJsData(JsCallBean.JsShareBean.class, jsonData);
                if (jsShareBean == null)
                    return;
                if (TextUtils.isEmpty(jsShareBean.title) || TextUtils.isEmpty(jsShareBean.shareLink))
                    return;
                ShareData.Builder builder = new ShareData.Builder().drawableId(R.drawable.icon_bzw_share_default_b).appName(mRemoteBrowserView.callGetResources().getString(R.string.app_name)).title(jsShareBean.title).summary(jsShareBean.subTitle).shareLink(jsShareBean.shareLink);
                doShare(builder);
                break;
        }
    }

    private void loadInvitedCodeImg() {
        FileLoadTool.getInstance().registerFileLoadObserve(this);
        FileLoadTool.getInstance().downLoadFile(mCacheImageUrlForShare);
    }

    private void shareInviteCodeInfo() {
        mRemoteBrowserView.callShowProgress(null, true);
        AccountDao.loadInviteCodeImageInfo(new IDefaultRequestReplyListener<String>() {
            @Override
            public void onRequestReply(boolean success, String url, int errorCode, String errorMsg) {
                mRemoteBrowserView.callCancelProgress();
                if (success && !TextUtils.isEmpty(url)) {
                    mCacheImageUrlForShare = url;
                    loadInvitedCodeImg();
                    return;
                }
                mRemoteBrowserView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    private void shareWeekReport(final String url, String data) {
        WeeklyReportResultBean weeklyReportResultBean = null;
        try {
            weeklyReportResultBean = JSON.parseObject(data, WeeklyReportResultBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (weeklyReportResultBean == null)
            return;
        Resources resources = mRemoteBrowserView.callGetResources();
        ShareData.Builder builder = new ShareData.Builder().drawableId(R.drawable.icon_bzw_share_default_b).appName(resources.getString(R.string.app_name)).title(resources.getString(R.string.share_weekly_title, String.valueOf(weeklyReportResultBean.week))).summary(" ").shareLink(url);
        doShare(builder);
    }


    private void shareResumeSync(final String url, String data) {
        ResumeSyncResultBean resumeSyncResultBean = null;
        try {
            resumeSyncResultBean = JSON.parseObject(data, ResumeSyncResultBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (resumeSyncResultBean == null)
            return;
        Resources resources = mRemoteBrowserView.callGetResources();
        ShareData.Builder builder = new ShareData.Builder().drawableId(R.drawable.icon_bzw_share_default_b).appName(resources.getString(R.string.app_name)).title(resources.getString(R.string.share_weekly_sync_title, String.valueOf(resumeSyncResultBean.insertOkCount))).summary(" ").shareLink(url);
        doShare(builder);
    }

    /**
     * 分享排行榜
     */

    public void shareRank() {
        if (mRankBean != null) {
            doRankShare();
            return;
        }
        String rsaUnionId = mRemoteBrowserView.getRsaUnionId();
        if (TextUtils.isEmpty(rsaUnionId))
            return;
        mRemoteBrowserView.callShowProgress(null, true);
        AccountDao.getResumeRank(rsaUnionId, new IDefaultRequestReplyListener<RankBean>() {
            @Override
            public void onRequestReply(boolean success, RankBean rankBean, int errorCode, String errorMsg) {
                mRemoteBrowserView.callCancelProgress();
                if (success) {
                    mRankBean = rankBean;
                    doRankShare();
                    return;
                }
                mRemoteBrowserView.callShowToastMessage(errorMsg, 0);
            }
        });
    }

    private void doRankShare() {
        Resources resources = mRemoteBrowserView.callGetResources();
        String shareTitle = resources.getString(R.string.rank_share_title, mRankBean.defeat + "%");
        String shareSummary = resources.getString(R.string.rank_share_summary);
        ShareData.Builder builder = new ShareData.Builder().appName(resources.getString(R.string.app_name)).title(shareTitle).summary(shareSummary).shareLink(mUrlPresenter.getWrappedUrl(false, URLConst.LINK_H5_RANK));
        doShare(builder);
    }

    private void doShare(final ShareData.Builder builder) {
        new ShareDialog(mRemoteBrowserView.callGetBindActivity()).addWeChatContactMenu().addQQContactMenu().addQZoneMenu().addWeChatFriendCircleMenu().addShareMenuSelectedListener(new ShareDialog.IShareMenuSelectedListener() {
            @Override
            public void onSharePlatformSelected(int platformType) {
                switch (platformType) {
                    case ShareDialog.SHARE_TYPE_WE_CHAT_CONTACT:
                        shareToWeChat(platformType, builder);
                        break;
                    case ShareDialog.SHARE_TYPE_WE_CHAT_FRIEND_CIRCLE:
                        shareToWeChat(platformType, builder);
                        break;
                    case ShareDialog.SHARE_TYPE_QQ_CONTACT:
                        shareToQQContact(platformType, builder);
                        break;
                    case ShareDialog.SHARE_TYPE_QZONE:
                        shareToQQContact(platformType, builder);
                        break;

                }
            }
        }).show();
    }

    /**
     * 分享到微信
     */
    private void shareToWeChat(final int type, ShareData.Builder builder) {
        ShareData shareData = builder.isShareImage() ? builder.buildWeChatImageData() : builder.buildWeChatLinkData();
        if (type == ShareDialog.SHARE_TYPE_WE_CHAT_CONTACT)
            realShare(RPCConst.ACTION_NAME_SHARE_TO_WECHAT_CONTACT, shareData);
        else
            realShare(RPCConst.ACTION_NAME_SHARE_TO_WECHAT_FC, shareData);

    }

    /**
     * 分享到qq
     */

    private void shareToQQContact(int platformType, ShareData.Builder builder) {
        if (platformType == ShareDialog.SHARE_TYPE_QZONE && builder.isShareImage()) {
            builder.shareLink(ConfigConst.USER_RC_INDEX);
        }
        ShareData shareData = ((platformType == ShareDialog.SHARE_TYPE_QQ_CONTACT) ? (builder.isShareImage() ? builder.buildQQImageData() : builder.buildQQLinkData()) : builder.buildQZoneData());
        if (platformType == ShareDialog.SHARE_TYPE_QQ_CONTACT)
            realShare(RPCConst.ACTION_NAME_SHARE_TO_QQ, shareData);
        else
            realShare(RPCConst.ACTION_NAME_SHARE_TO_QZONE, shareData);

    }


    private void realShare(String action, ShareData shareData) {
        RPCConnectManager.getInstance().doRPCRequireAsync(RPCConst.RPCProcessId.ID_MAIN, action, AppUtil.objectToJson(shareData), true, new RPCConnectWrapper.IRPCReplyListener() {
            @Override
            public void onRPCReply(String requireId, String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
//                        boolean success = jsonObject.getBoolean("success");
                    String errorMsg = jsonObject.getString("errorMsg");
                    if (!TextUtils.isEmpty(errorMsg)) {
                        mRemoteBrowserView.callShowToastMessage(errorMsg, 0);
                    }

                } catch (Exception e) {
                    //ignore
                }
            }

            @Override
            public boolean syncToUIThread() {
                return true;
            }
        });
    }

    private static <T> T parseJsData(Class<T> classZZ, String jsonData) {
        if (TextUtils.isEmpty(jsonData))
            return null;
        T t = null;
        try {
            t = JSON.parseObject(jsonData, classZZ);
        } catch (Exception e) {
            //ignore
        }
        return t;
    }

    @Override
    public void onFileStartLoading(String fileUrl, String tagForSameUrl) {
        mRemoteBrowserView.callShowProgress(null, true);
    }

    @Override
    public void onFileLoadProgressChanged(String fileUrl, String tagForSameUrl, int progress) {

    }

    @Override
    public void onFileLoadSuccess(String fileUrl, String tagForSameUrl, File file) {
        if (fileUrl.equals(mCacheImageUrlForShare)) {
            mRemoteBrowserView.callCancelProgress();
            ShareData.Builder builder = new ShareData.Builder().appName(mRemoteBrowserView.callGetResources().getString(R.string.app_name)).imageLocalPath(file.getAbsolutePath()).isImageMode(true);
            doShare(builder);
        }

    }

    @Override
    public void onFileLoadFailed(String fileUrl, String tagForSameUrl, int errorCode, String errorMsg) {
        if (fileUrl.equals(mCacheImageUrlForShare))
            mRemoteBrowserView.callCancelProgress();
    }

    public void onDestroy() {
        FileLoadTool.getInstance().unRegisterFileLoadObserve(this);
    }
}
