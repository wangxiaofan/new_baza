package com.baza.android.bzw.businesscontroller.email.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baza.android.bzw.bean.email.EmailAttachmentBean;
import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.slib.storage.file.FileManager;
import com.bznet.android.rcbox.R;

import java.util.List;

/**
 * Created by Vincent.Lei on 2017/8/1.
 * Title：
 * Note：
 */

public class EmailAttachmentAdapter extends BaseBZWAdapter implements View.OnClickListener {
    private List<EmailAttachmentBean> mEmailAttachmentList;
    private Context mContext;
    private int mItemWidth;

    public EmailAttachmentAdapter(Context context, List<EmailAttachmentBean> emailAttachmentList, int itemWidth, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mEmailAttachmentList = emailAttachmentList;
        this.mContext = context;
        this.mItemWidth = itemWidth;
    }

    @Override
    public int getCount() {
        return (mEmailAttachmentList != null ? mEmailAttachmentList.size() : 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_for_email_attachment, null);
            ViewHolder viewHolder = new ViewHolder(convertView, mItemWidth);
            convertView.setTag(viewHolder);
            convertView.setOnClickListener(this);
        }
        refreshTargetView(convertView, position);
        return convertView;
    }


    public void refreshTargetView(View convertView, int position) {
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        EmailAttachmentBean emailAttachmentBean = mEmailAttachmentList.get(position);
        viewHolder.textView_attachmentName.setText(emailAttachmentBean.fileName);
        viewHolder.textView_attachmentSize.setText(FileManager.formatFileSize(emailAttachmentBean.fileSize));
        switch (emailAttachmentBean.status) {
            case EmailAttachmentBean.STATUS_UPLOADING:
                viewHolder.textView_attachmentStatus.setText(R.string.uploading);
                viewHolder.textView_attachmentStatus.setTextColor(Color.WHITE);
                viewHolder.textView_attachmentStatus.setVisibility(View.VISIBLE);
                break;
            case EmailAttachmentBean.STATUS_FAILED_UPLOAD:
                viewHolder.textView_attachmentStatus.setText(R.string.uploading_failed);
                viewHolder.textView_attachmentStatus.setTextColor(Color.RED);
                viewHolder.textView_attachmentStatus.setVisibility(View.VISIBLE);
                break;
            case EmailAttachmentBean.STATUS_SUCCESS_UPLOAD:
                viewHolder.textView_attachmentStatus.setVisibility(View.GONE);
                break;
        }

        viewHolder.textView_attachmentName.setTag(position);
    }

    @Override
    public void onClick(View v) {
        ViewHolder viewHolder = (ViewHolder) v.getTag();
        int position = (int) viewHolder.textView_attachmentName.getTag();
        sendAdapterEventToHost(AdapterEventIdConst.EVENT_ID_CLICK_EMAIL_ATTACHMENT, position, v, mEmailAttachmentList.get(position));
    }

    static class ViewHolder {
        TextView textView_attachmentName;
        TextView textView_attachmentSize;
        TextView textView_attachmentStatus;

        public ViewHolder(View convertView, int itemWidth) {
            textView_attachmentName = convertView.findViewById(R.id.tv_attachment_name);
            textView_attachmentSize = convertView.findViewById(R.id.tv_attachment_size);
            textView_attachmentStatus = convertView.findViewById(R.id.tv_attachment_uploading_status);

            ViewGroup.LayoutParams lp = textView_attachmentName.getLayoutParams();
            lp.width = itemWidth;
            textView_attachmentName.setLayoutParams(lp);
        }
    }
}
