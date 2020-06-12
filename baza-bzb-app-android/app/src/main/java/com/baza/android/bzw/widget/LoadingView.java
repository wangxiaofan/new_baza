package com.baza.android.bzw.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.slib.progress.IndeterminateProgressDrawable;
import com.bznet.android.rcbox.R;


/**
 * Created by LW on 2016/icon_collect/icon_collect.
 * Title :
 * Note :
 */
public class LoadingView extends FrameLayout {

    private ProgressBar progressBar;
    private TextView textView_msg, textView_retry_hint;
    private ImageView imageView_hint;
    private FrameLayout frameLayout_progress;
    private LinearLayout linearLayout_errorMsg;
    private View view_main;

    private boolean isRetryEnable;

    public void setRetryListener(IRetryListener retryListener) {
        this.retryListener = retryListener;
    }

    private IRetryListener retryListener;

    public interface IRetryListener {
        void onRetry();
    }

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        if (getId() == NO_ID)
            setId(R.id.define_id_loadingView);

        view_main = LayoutInflater.from(context).inflate(R.layout.layout_loading_view, this);
        frameLayout_progress = view_main.findViewById(R.id.fl_progress);
        linearLayout_errorMsg = view_main.findViewById(R.id.ll_error_msg);
        progressBar = view_main.findViewById(R.id.progressbar);
        textView_msg = view_main.findViewById(R.id.tv_msg);
        textView_retry_hint = view_main.findViewById(R.id.tv_retry_hint);
        imageView_hint = view_main.findViewById(R.id.iv_hint);


        IndeterminateProgressDrawable indeterminateProgressDrawable = new IndeterminateProgressDrawable(context);
        indeterminateProgressDrawable.setTint(context.getResources().getColor(R.color.main_progress));
        progressBar.setIndeterminateDrawable(indeterminateProgressDrawable);

        textView_retry_hint.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRetryEnable && retryListener != null)
                    retryListener.onRetry();
            }
        });

    }

    public void startLoading(int msg) {
        startLoading(getContext().getString(msg));
    }

    public void startLoading(String msg) {
        textView_retry_hint.setVisibility(View.GONE);
        imageView_hint.setVisibility(View.GONE);
        linearLayout_errorMsg.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        frameLayout_progress.setVisibility(View.VISIBLE);
//        view_main.setBackgroundResource(R.color.color_half_black);
        setVisibility(View.VISIBLE);
        isRetryEnable = false;
    }

    public void finishLoading() {
        if (getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
            setVisibility(View.GONE);
        }
    }

    public void loadingFailed(int errorCode, int msg) {
        loadingFailed(errorCode, getContext().getString(msg));
    }

    public void loadingFailed(int errorCode, String msg) {
        progressBar.setVisibility(View.GONE);
        frameLayout_progress.setVisibility(View.GONE);
        boolean isLoginError = (errorCode == CustomerRequestAssistHandler.NET_ERROR_CODE_NOT_LOGGED_IN || errorCode == CustomerRequestAssistHandler.NET_REQUEST_LOGGED_EXPIRED);
        textView_retry_hint.setVisibility((isLoginError ? View.GONE : View.VISIBLE));
        imageView_hint.setVisibility((isLoginError ? View.GONE : View.VISIBLE));
        textView_msg.setText((isLoginError ? getContext().getString(R.string.platform_changed_you_need_reLogin) : (msg == null ? getContext().getString(R.string.loading_failed) : msg)));
        linearLayout_errorMsg.setVisibility(View.VISIBLE);
//        view_main.setBackgroundResource(android.R.color.transparent);
        isRetryEnable = !isLoginError;
    }


    public boolean isShownVisibility() {
        return getVisibility() == View.VISIBLE;
    }

    public static LoadingView findSelf(Activity activity, int id) {
        id = id <= 0 ? R.id.define_id_loadingView : id;
        return (LoadingView) activity.findViewById(id);
    }

    public static LoadingView findSelf(View mRootView, int id) {
        id = id <= 0 ? R.id.define_id_loadingView : id;
        return (LoadingView) mRootView.findViewById(id);
    }
}
