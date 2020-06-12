package com.baza.android.bzw.businesscontroller.message.adapter.chatHolder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.businesscontroller.im.BZWIMMessage;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.manager.IMManager;
import com.slib.storage.file.ImageManager;
import com.slib.utils.LoadImageUtil;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;

import java.io.File;

/**
 * Created by Vincent.Lei on 2017/6/2.
 * Title：
 * Note：
 */

public class ImageHolder extends ChatViewHolder {
    private static int DEFAULT_WH = 200;
    private static int MAX_WIDTH = 400;
    private static int MAX_HEIGHT = 400;
    private ImageView imageView;
    private int mWidth;
    private int mHeight;

    static {
        if (ScreenUtil.screenWidth > 1080) {
            DEFAULT_WH *= 1.5f;
            MAX_WIDTH *= 1.5f;
            MAX_HEIGHT *= 1.5f;
        }
    }

    public ImageHolder(Context context, View convertView, IChatExtraMsgProvider mChatExtraMsgProvider) {
        super(context, convertView, mChatExtraMsgProvider);
    }


    @Override
    public int getItemTypeViewId() {
        return R.layout.chat_item_image;
    }

    @Override
    public void init(View viewContentView) {
        imageView = viewContentView.findViewById(R.id.roundedImageView);
        imageView.setOnClickListener(this);
    }

    @Override
    public void refreshView(BZWIMMessage bzwimMessage, int position) {
        imageView.setTag(R.id.hold_tag_id_one, bzwimMessage);
        FileAttachment msgAttachment = (FileAttachment) bzwimMessage.imMessage.getAttachment();
        if (msgAttachment == null) {
            loadImage(null);
            return;
        }
        String thumbPath = msgAttachment.getThumbPath();
        if (!TextUtils.isEmpty(thumbPath)) {
            loadImage(thumbPath);
            return;
        }
        String path = msgAttachment.getPath();
        if (!TextUtils.isEmpty(path)) {
            loadImage(path);
            return;
        }
        if (bzwimMessage.imMessage.getAttachStatus() == AttachStatusEnum.transferred || bzwimMessage.imMessage.getAttachStatus() == AttachStatusEnum.def) {
            IMManager.getInstance(BZWApplication.getApplication()).downloadAttachment(bzwimMessage, true);
        }
        loadImage(null);

    }

    public void loadImage(String path) {
        mWidth = mHeight = DEFAULT_WH;
        if (!TextUtils.isEmpty(path) && new File(path).exists()) {
            int[] imageWH = ImageManager.getImageWH(path);
            if (imageWH != null) {
                mWidth = imageWH[0];
                mHeight = imageWH[1];
                float scale;
                if (mHeight > MAX_HEIGHT) {
                    scale = MAX_HEIGHT * 1.0f / mHeight;
                    mHeight = MAX_HEIGHT;
                    mWidth = (int) (mWidth * scale);
                }
                if (mWidth > MAX_WIDTH) {
                    scale = MAX_WIDTH * 1.0f / mWidth;
                    mWidth = MAX_WIDTH;
                    mHeight = (int) (mHeight * scale);
                }
            }
        }

        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        lp.width = mWidth;
        lp.height = mHeight;
        LoadImageUtil.loadImage(path, imageView);
    }

    @Override
    public boolean isUseDefaultBubble() {
        return false;
    }

    @Override
    public int getBubbleLeftDrawable() {
        return R.drawable.image_chat_bg_left_b;
    }

    @Override
    public int getBubbleRightDrawable() {
        return R.drawable.image_chat_bg_right_b;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.roundedImageView:
                BZWIMMessage bzwimMessage = (BZWIMMessage) v.getTag(R.id.hold_tag_id_one);
                mChatExtraMsgProvider.getAdapterEventsListener().onAdapterEventsArrival(AdapterEventIdConst.EVENT_ID_ATTACHMENT_SHOW_FULL_IMAGE, 0, v, bzwimMessage);
                break;
        }
    }
}
