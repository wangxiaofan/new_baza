package social.data;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.UUID;

import social.SocialHelper;

/**
 * Created by Vincent.Lei on 2016/12/7.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class ShareData implements Serializable {

    public static final int DATA_TYPE_QQ_IMAGE = 2;
    public static final int DATA_TYPE_QQ_LINK = 3;
    public static final int DATA_TYPE_QZONE = 1;

    public static final int DATA_TYPE_WECHAT_TEXT = 4;
    public static final int DATA_TYPE_WECHAT_IMAGE = 5;
    public static final int DATA_TYPE_WECHAT_LINK = 6;

    private int dataType;
    private String title;
    private String summary;
    private String imageLink;
    private String imageLocalPath;
    private String shareLink;
    private String appName;
    private int drawableId;
    private String keyId;

    public ShareData() {
    }

    private ShareData(int dataType, String title) {
        this.dataType = dataType;
        this.title = title;
        this.keyId = UUID.randomUUID().toString();
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public void setImageLocalPath(String imageLocalPath) {
        this.imageLocalPath = imageLocalPath;
    }

    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public int getDataType() {
        return dataType;
    }

    public String getKeyId() {
        return keyId;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String getImageLocalPath() {
        return imageLocalPath;
    }

    public String getShareLink() {
        return shareLink;
    }

    public String getAppName() {
        return appName;
    }

    private void checkEmpty() {
        if (TextUtils.isEmpty(appName))
            throw new IllegalArgumentException("appName  can not be null");
        if ((dataType == DATA_TYPE_QQ_LINK || dataType == DATA_TYPE_QZONE || dataType == DATA_TYPE_WECHAT_LINK) && TextUtils.isEmpty(imageLink) && TextUtils.isEmpty(imageLocalPath))
            formatImage();
        if ((dataType == DATA_TYPE_QQ_IMAGE || dataType == DATA_TYPE_WECHAT_IMAGE) && TextUtils.isEmpty(imageLocalPath))
            throw new IllegalArgumentException("imageLocalPath can not  be null");
        if ((dataType == DATA_TYPE_QQ_LINK || dataType == DATA_TYPE_QZONE || dataType == DATA_TYPE_WECHAT_LINK) && TextUtils.isEmpty(shareLink))
            throw new IllegalArgumentException("shareLink can not all be null");
        if (TextUtils.isEmpty(title))
            title = appName;
        if (summary == null)
            summary = "";

    }

    private void formatImage() {
        imageLocalPath = SocialHelper.getInstance().getShareDrawablePath(drawableId);
        if (imageLocalPath == null)
            SocialHelper.getInstance().setDefaultShareDrawable(drawableId, true);
        imageLocalPath = SocialHelper.getInstance().getShareDrawablePath(drawableId);
    }

    public static class Builder {
        private String title;
        private String summary;
        private String imageLink;
        private String imageLocalPath;
        private String shareLink;
        private String appName;
        private int drawableId;
        private boolean isImageMode;

        public boolean isShareImage() {
            return isImageMode;
        }

        public Builder isImageMode(boolean isImageMode) {
            this.isImageMode = isImageMode;
            return this;
        }

        public Builder drawableId(int drawableId) {
            this.drawableId = drawableId;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder summary(String summary) {
            this.summary = summary;
            return this;
        }

        public Builder imageLink(String imageLink) {
            this.imageLink = imageLink;
            return this;
        }

        public Builder imageLocalPath(String imageLocalPath) {
            this.imageLocalPath = imageLocalPath;
            return this;
        }

        public Builder shareLink(String shareLink) {
            this.shareLink = shareLink;
            return this;
        }

        public Builder appName(String appName) {
            this.appName = appName;
            return this;
        }

        public ShareData buildQQImageData() {
            ShareData data = new ShareData(DATA_TYPE_QQ_IMAGE, title);
            data.summary = summary;
            data.appName = appName;
            data.imageLink = imageLink;
            data.drawableId = drawableId;
            data.imageLocalPath = imageLocalPath;
            data.checkEmpty();
            return data;
        }

        public ShareData buildQQLinkData() {
            ShareData data = new ShareData(DATA_TYPE_QQ_LINK, title);
            data.summary = summary;
            data.appName = appName;
            data.imageLink = imageLink;
            data.drawableId = drawableId;
            data.imageLocalPath = imageLocalPath;
            data.shareLink = shareLink;
            data.checkEmpty();
            return data;
        }

        public ShareData buildQZoneData() {
            ShareData data = new ShareData(DATA_TYPE_QZONE, title);
            data.summary = summary;
            data.appName = appName;
            data.imageLink = imageLink;
            data.imageLocalPath = imageLocalPath;
            data.shareLink = shareLink;
            data.drawableId = drawableId;
            data.checkEmpty();
            return data;
        }

        public ShareData buildWeChatTextData() {
            ShareData data = new ShareData(DATA_TYPE_WECHAT_TEXT, title);
            data.summary = summary;
            data.appName = appName;
            data.drawableId = drawableId;
            data.checkEmpty();
            return data;
        }

        public ShareData buildWeChatImageData() {
            ShareData data = new ShareData(DATA_TYPE_WECHAT_IMAGE, title);
            data.summary = summary;
            data.appName = appName;
            data.drawableId = drawableId;
            data.imageLocalPath = imageLocalPath;
            data.checkEmpty();
            return data;
        }

        public ShareData buildWeChatLinkData() {
            ShareData data = new ShareData(DATA_TYPE_WECHAT_LINK, title);
            data.summary = summary;
            data.drawableId = drawableId;
            data.appName = appName;
            data.imageLocalPath = imageLocalPath;
            data.shareLink = shareLink;
            data.checkEmpty();
            return data;
        }
    }
}
