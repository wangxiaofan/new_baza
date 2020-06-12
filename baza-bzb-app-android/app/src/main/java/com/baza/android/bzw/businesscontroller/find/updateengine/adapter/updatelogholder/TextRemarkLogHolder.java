package com.baza.android.bzw.businesscontroller.find.updateengine.adapter.updatelogholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/8/30.
 * Title：
 * Note：
 */

public class TextRemarkLogHolder extends BaseLogHolder {
    TextView textView_title;
    TextView textView_content;

    public TextRemarkLogHolder(Context context, View convertView, ILogAdapterListener listener) {
        super(context, convertView, listener);
    }

    @Override
    int getItemLayoutId() {
        return R.layout.layout_update_log_text_remark;
    }

    @Override
    void initContentView() {
        textView_title = view_contentRoot.findViewById(R.id.tv_title);
        textView_content = view_contentRoot.findViewById(R.id.tv_content);
    }

    @Override
    void setHolderData() {
        textView_title.setText(getLogTypeMsg(mLogData.sceneId));
        textView_content.setText(mLogData.content);
    }
}
