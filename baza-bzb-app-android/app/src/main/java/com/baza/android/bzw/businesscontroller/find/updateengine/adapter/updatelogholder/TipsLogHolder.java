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

public class TipsLogHolder extends BaseLogHolder {
    TextView textView_tips;

    public TipsLogHolder(Context context, View convertView, ILogAdapterListener listener) {
        super(context, convertView, listener);
    }

    @Override
    int getItemLayoutId() {
        return R.layout.layout_update_log_tips;
    }

    @Override
    void initContentView() {
        textView_tips = view_contentRoot.findViewById(R.id.tv_tips);
    }

    @Override
    void setHolderData() {
        textView_tips.setText(getLogTypeMsg(mLogData.sceneId));
    }
}
