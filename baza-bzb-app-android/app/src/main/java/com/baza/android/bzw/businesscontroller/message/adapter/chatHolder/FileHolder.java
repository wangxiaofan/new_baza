package com.baza.android.bzw.businesscontroller.message.adapter.chatHolder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.businesscontroller.im.BZWIMMessage;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.manager.IMManager;
import com.slib.storage.file.FileManager;
import com.slib.utils.DialogUtil;
import com.bznet.android.rcbox.R;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vincent.Lei on 2017/6/2.
 * Title：
 * Note：
 */

public class FileHolder extends ChatViewHolder {
    ImageView imageView_fileType;
    TextView textView_name;
    TextView textView_des;
    View frame_contentContainer;

    private static final Map<String, Integer> mFileTypeIconMap = new HashMap<String, Integer>();

    static {
        mFileTypeIconMap.put("xls", R.drawable.file_ic_session_excel);
        mFileTypeIconMap.put("ppt", R.drawable.file_ic_session_ppt);
        mFileTypeIconMap.put("doc", R.drawable.file_ic_session_word);
        mFileTypeIconMap.put("xlsx", R.drawable.file_ic_session_excel);
        mFileTypeIconMap.put("pptx", R.drawable.file_ic_session_ppt);
        mFileTypeIconMap.put("docx", R.drawable.file_ic_session_word);
        mFileTypeIconMap.put("pdf", R.drawable.file_ic_session_pdf);
        mFileTypeIconMap.put("html", R.drawable.file_ic_session_html);
        mFileTypeIconMap.put("htm", R.drawable.file_ic_session_html);
        mFileTypeIconMap.put("txt", R.drawable.file_ic_session_txt);
        mFileTypeIconMap.put("rar", R.drawable.file_ic_session_rar);
        mFileTypeIconMap.put("zip", R.drawable.file_ic_session_zip);
        mFileTypeIconMap.put("7z", R.drawable.file_ic_session_zip);
        mFileTypeIconMap.put("mp4", R.drawable.file_ic_session_mp4);
        mFileTypeIconMap.put("mp3", R.drawable.file_ic_session_mp3);
        mFileTypeIconMap.put("png", R.drawable.file_ic_session_png);
        mFileTypeIconMap.put("gif", R.drawable.file_ic_session_gif);
        mFileTypeIconMap.put("jpg", R.drawable.file_ic_session_jpg);
        mFileTypeIconMap.put("jpeg", R.drawable.file_ic_session_jpg);
    }

    public FileHolder(Context context, View convertView, IChatExtraMsgProvider mChatExtraMsgProvider) {
        super(context, convertView, mChatExtraMsgProvider);
    }


    @Override
    public int getItemTypeViewId() {
        return R.layout.chat_item_file;
    }

    @Override
    public void init(View viewContentView) {
        frame_contentContainer = viewContentView;
        imageView_fileType = viewContentView.findViewById(R.id.iv_file_type);
        textView_name = viewContentView.findViewById(R.id.tv_file_name);
        textView_des = viewContentView.findViewById(R.id.tv_file_des);
        frame_contentContainer.setOnClickListener(this);
    }

    @Override
    public void refreshView(BZWIMMessage bzwimMessage, int position) {
        frame_contentContainer.setTag(bzwimMessage);
        frame_contentContainer.setTag(R.id.hold_tag_id_one, position);
        FileAttachment fileAttachment = (FileAttachment) bzwimMessage.imMessage.getAttachment();
        imageView_fileType.setImageResource(getFileTypeIcon(fileAttachment.getDisplayName()));
        textView_name.setText(fileAttachment.getDisplayName());
        String path = fileAttachment.getPath();
        if (!TextUtils.isEmpty(path)) {
            textView_des.setText(FileManager.formatFileSize(fileAttachment.getSize()));
            return;
        }
        AttachStatusEnum status = bzwimMessage.imMessage.getAttachStatus();
        switch (status) {
            case transferring:
                textView_des.setText(mResources.getString(R.string.attachment_loading_with_length, FileManager.formatFileSize(fileAttachment.getSize())));
                break;
            case def:
            case transferred:
            case fail:
                path = fileAttachment.getPathForSave();
                if (!TextUtils.isEmpty(path) && new File(path).exists()) {
                    textView_des.setText(mResources.getString(R.string.has_load_with_length, FileManager.formatFileSize(fileAttachment.getSize())));
                } else {
                    textView_des.setText(mResources.getString(R.string.not_load_with_length, FileManager.formatFileSize(fileAttachment.getSize())));
                }
                break;
        }
    }

    public static int getFileTypeIcon(String fileName) {
        String ext = FileManager.getExtensionName(fileName);
        if (ext != null) {
            Integer resId = mFileTypeIconMap.get(ext.toLowerCase());
            if (resId != null)
                return resId.intValue();
        }
        return R.drawable.file_ic_session_unknow;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.fl_file_content:
                final BZWIMMessage bzwimMessage = (BZWIMMessage) v.getTag();
                FileAttachment fileAttachment = (FileAttachment) bzwimMessage.imMessage.getAttachment();
                if (!TextUtils.isEmpty(fileAttachment.getPath())) {
                    mChatExtraMsgProvider.getAdapterEventsListener().onAdapterEventsArrival(AdapterEventIdConst.ADAPTER_EVENT_OPEN_FILE, ((int) v.getTag(R.id.hold_tag_id_one)), null, bzwimMessage);
                    return;
                }
                DialogUtil.doubleButtonShow(mContext, 0, R.string.confirm_load_attachment, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IMManager.getInstance(BZWApplication.getApplication()).downloadAttachment(bzwimMessage, false);
                    }
                }, null);
                break;
        }
    }
}
