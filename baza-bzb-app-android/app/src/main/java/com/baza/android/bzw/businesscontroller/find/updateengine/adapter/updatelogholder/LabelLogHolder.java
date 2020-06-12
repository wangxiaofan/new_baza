package com.baza.android.bzw.businesscontroller.find.updateengine.adapter.updatelogholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baza.android.bzw.widget.LineBreakLayout;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

import java.util.List;

/**
 * Created by Vincent.Lei on 2017/8/30.
 * Title：
 * Note：
 */

public class LabelLogHolder extends BaseLogHolder {
    TextView textView_title;
    LineBreakLayout lineBreakLayout_labels;

    public LabelLogHolder(Context context, View convertView, ILogAdapterListener listener) {
        super(context, convertView, listener);
    }

    @Override
    int getItemLayoutId() {
        return R.layout.layout_update_log_label;
    }

    @Override
    void initContentView() {
        textView_title = view_contentRoot.findViewById(R.id.tv_title);
        lineBreakLayout_labels = view_contentRoot.findViewById(R.id.lbl_label_container);
    }

    @Override
    void setHolderData() {
        textView_title.setText(getLogTypeMsg(mLogData.sceneId));
        List<String> tags = mLogData.getLogLabelContent();
        if (tags == null || tags.isEmpty()) {
            lineBreakLayout_labels.setVisibility(View.GONE);
            return;
        }
        int needShowCount = tags.size();
        int itemHeight = ScreenUtil.dip2px(20);
        int currentCount = lineBreakLayout_labels.getChildCount();
        boolean hasCache;
        TextView textView_label;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        for (int index = 0; index < needShowCount; index++) {
            hasCache = index < currentCount;
            textView_label = (TextView) (hasCache ? lineBreakLayout_labels.getChildAt(index) : layoutInflater.inflate(R.layout.item_label, null));
            textView_label.setText(tags.get(index));
            if (!hasCache)
                lineBreakLayout_labels.addView(textView_label, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, itemHeight));
            textView_label.setVisibility(View.VISIBLE);
        }
        if (needShowCount < currentCount) {
            for (; needShowCount < currentCount; needShowCount++) {
                lineBreakLayout_labels.getChildAt(needShowCount).setVisibility(View.GONE);
            }
        }
        lineBreakLayout_labels.setVisibility(View.VISIBLE);
    }
}
