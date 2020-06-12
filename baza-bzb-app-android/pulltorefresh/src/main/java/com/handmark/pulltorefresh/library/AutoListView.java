package com.handmark.pulltorefresh.library;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


/**
 * Created by LW on 2016/8/23.
 * Title :
 * Note :
 */
public class AutoListView extends ListView {


    public static final int COMPLETE_MODE_DEFAULT = 1, COMPLETE_MODE_NO_MORE_DATA = 2;
    private static final int COMPLETE_MODE_NO_DATA = 3;
    private int status;

    public interface IAutoStatusListener {
        void onAutoLoadMoreChanged(int status);
    }

    private View footLoadView = null;

    private TextView textView_subtext;

    private ImageView imageView_progress;

    private IAutoStatusListener iAutoStatusListener;


    public AutoListView(Context context) {
        this(context, null);
    }

    public AutoListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setAutoFootView(View v, IAutoStatusListener iAutoStatusListener) {
        this.iAutoStatusListener = iAutoStatusListener;
        if (v == null) {
            footLoadView = LayoutInflater.from(getContext()).inflate(R.layout.auto_load_layout, null);
            textView_subtext = footLoadView.findViewById(R.id.pull_to_refresh_sub_text);
            imageView_progress = footLoadView.findViewById(R.id.pull_to_refresh_progress);
            footLoadView.setVisibility(View.INVISIBLE);
        }

        LinearLayout ll = new LinearLayout(getContext());
        ll.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ll.setGravity(Gravity.CENTER);
        ll.addView(v == null ? footLoadView : v, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addFooterView(ll);
    }

    void onLastItemVisible() {
        if (footLoadView != null) {
            footLoadView.setVisibility(View.VISIBLE);
            imageView_progress.setVisibility(View.VISIBLE);
            textView_subtext.setText(R.string.pull_to_refresh_refreshing_label);
        } else if (iAutoStatusListener != null)
            iAutoStatusListener.onAutoLoadMoreChanged(COMPLETE_MODE_DEFAULT);
    }

    void reset(int status, boolean hasData) {

        if (!hasData)
            status = COMPLETE_MODE_NO_DATA;
        if (this.status == status)
            return;
        this.status = status;
        if (footLoadView != null) {

            if (!hasData) {
                footLoadView.setVisibility(View.INVISIBLE);
                return;
            }

            footLoadView.setVisibility(View.VISIBLE);
            imageView_progress.setVisibility(View.GONE);
            textView_subtext.setText(status == COMPLETE_MODE_NO_MORE_DATA ? R.string.no_more_data : R.string.pull_to_refresh_pull_label);
        } else if (iAutoStatusListener != null)
            iAutoStatusListener.onAutoLoadMoreChanged(status);
    }
}
