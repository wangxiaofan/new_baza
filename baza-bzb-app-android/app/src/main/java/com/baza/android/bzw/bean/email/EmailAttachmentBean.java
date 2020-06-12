package com.baza.android.bzw.bean.email;

import java.io.File;

/**
 * Created by Vincent.Lei on 2017/8/1.
 * Title：
 * Note：
 */

public class EmailAttachmentBean {
    public static final int STATUS_UPLOADING = 1;
    public static final int STATUS_FAILED_UPLOAD = 2;
    public static final int STATUS_SUCCESS_UPLOAD = 3;
    public String filePath;
    public String fileName;
    public String ossKey;
    public long fileSize;
    public int status;
    private File file;

    public File getFile() {
        return file;
    }

    public EmailAttachmentBean() {

    }

    public static EmailAttachmentBean createLocalAttachment(String filePath) {
        EmailAttachmentBean emailAttachmentBean = new EmailAttachmentBean();
        emailAttachmentBean.filePath = filePath;
        emailAttachmentBean.file = new File(filePath);
        if (emailAttachmentBean.file.exists()) {
            emailAttachmentBean.fileName = emailAttachmentBean.file.getName();
            emailAttachmentBean.fileSize = emailAttachmentBean.file.length();
        }
        return emailAttachmentBean;
    }

    public boolean isEmailAttachmentExist() {
        return (file.exists() && fileSize > 0);
    }

    public void updateNewInfo(EmailAttachmentBean emailAttachmentBean) {
        this.fileName = emailAttachmentBean.fileName;
        this.ossKey = emailAttachmentBean.ossKey;
    }
}
