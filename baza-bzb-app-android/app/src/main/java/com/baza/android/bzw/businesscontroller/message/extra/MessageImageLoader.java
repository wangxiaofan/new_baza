package com.baza.android.bzw.businesscontroller.message.extra;

import android.content.Context;
import android.text.TextUtils;

import com.baza.android.bzw.businesscontroller.im.BZWIMMessage;
import com.baza.android.bzw.businesscontroller.im.IIMMessageStatusObserver;
import com.baza.android.bzw.manager.IMManager;
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;

/**
 * Created by Vincent.Lei on 2018/7/24.
 * Title：
 * Note：
 */
public class MessageImageLoader implements IIMMessageStatusObserver {
    public interface IMessageImageLoadListener {
        void onMessageImageLoaded(boolean success, String path);
    }

    private BZWIMMessage mBzwimMessage;
    private IMManager mImManager;
    private IMessageImageLoadListener mListener;

    public MessageImageLoader(Context context, BZWIMMessage bzwimMessage, IMessageImageLoadListener listener) {
        this.mBzwimMessage = bzwimMessage;
        this.mListener = listener;
        this.mImManager = IMManager.getInstance(context);
        registerMessageStatus(true);
    }

    @Override
    public void onIMMessageStatusChanged(BZWIMMessage bzwimMessage) {
        if (bzwimMessage.imMessage.getAttachStatus() == AttachStatusEnum.transferring)
            return;
        if (mListener == null || bzwimMessage.getUuid() == null || !bzwimMessage.getUuid().equals(mBzwimMessage.getUuid()))
            return;
        if (bzwimMessage.imMessage.getAttachStatus() == AttachStatusEnum.fail)
            mListener.onMessageImageLoaded(false, null);
        else if (bzwimMessage.imMessage.getAttachStatus() == AttachStatusEnum.transferred)
            mListener.onMessageImageLoaded(true, ((ImageAttachment) bzwimMessage.imMessage.getAttachment()).getPath());
        registerMessageStatus(false);
    }

    public String getPath() {
        return ((ImageAttachment) mBzwimMessage.imMessage.getAttachment()).getPath();
    }

    private void registerMessageStatus(boolean register) {
        mImManager.registerIMMessageStatusObserves(this, register);
    }

    public void destroy() {
        registerMessageStatus(false);
    }

    private boolean hasDownLoadImage() {
        return (mBzwimMessage.imMessage.getAttachStatus() == AttachStatusEnum.transferred && !TextUtils.isEmpty(((ImageAttachment) mBzwimMessage.imMessage.getAttachment()).getPath()));
    }

    public void download() {
        if (!hasDownLoadImage())
            mImManager.downloadAttachment(mBzwimMessage, false);
        else if (mListener != null)
            mListener.onMessageImageLoaded(true, ((ImageAttachment) mBzwimMessage.imMessage.getAttachment()).getPath());

    }
}
