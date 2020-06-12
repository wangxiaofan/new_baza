package com.tencent.qcloud.tim.uikit.utils.dialog;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.qcloud.tim.uikit.R;

/**
 * Created by LW on 2016/10/14.
 * Title :
 * Note :
 */

public class MaterialDialog extends Dialog implements View.OnClickListener {
    private int button_count = 2;
    private CharSequence cancel_text, sure_text, title_text, message_text;
    private View.OnClickListener onClickListener_cancel, onClickListener_sure, onClickListener_outside;
    private Resources mResources;

    private TextView textView_title;
    private TextView textView_message;
    private Button button_cancel, button_sure, button_center;
    private View view_departButtonsline;
    private View view_parent;
    private ViewGroup viewGroup_message_view_container;
    private View view_message_instead;
    //    private Effectstype mEffectstype;
//    private int gravity_title = Gravity.NO_GRAVITY;
//    private int color_title;
    private int sureButtonColor;
    private int messageViewPaddingLeft = -1, messageViewPaddingRight = -1, messageViewPaddingTop = -1, messageViewPaddingBottom = -1;
    private float widthPercentOfScreen = 0.8f;
    private boolean isAutoDismissEnable = true;
    private boolean isNotShowTitle = true;

    public void setNotShowTitle(boolean notShowTitle) {
        isNotShowTitle = notShowTitle;
    }

//    isShowLineBetweenButtonsAndMessage = true;

    public MaterialDialog(Context context) {
        this(context, R.style.materialDialog_bottom);
    }

    public MaterialDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mResources = context.getResources();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_default_dialog);

        view_parent = findViewById(R.id.fl_parent);
        view_parent.setOnClickListener(this);

        View view_main = findViewById(R.id.ll_main_view_material);
        ViewGroup.LayoutParams lp = view_main.getLayoutParams();
        lp.width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * widthPercentOfScreen);
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        view_main.setLayoutParams(lp);

        textView_title = findViewById(R.id.tv_title_material);
        textView_message = findViewById(R.id.et_message_material);
        button_cancel = findViewById(R.id.btn_left_click_material);
        button_sure = findViewById(R.id.btn_right_click_material);
        button_center = findViewById(R.id.btn_center_click_material);
        view_departButtonsline = findViewById(R.id.view_line_depart_buttons);

        viewGroup_message_view_container = findViewById(R.id.message_view_container_material);

        button_cancel.setOnClickListener(this);
        button_sure.setOnClickListener(this);
        button_center.setOnClickListener(this);

        setInfo();

//        mEffectstype = Effectstype.SlideBottom;
        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
//                if (mEffectstype != null) {
//                    BaseEffects animator = mEffectstype.getAnimator();
//                    animator.setDuration(300);
//                    animator.start(view_parent);
//                } else {
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.8f, 1f);
                valueAnimator.setInterpolator(new BounceInterpolator());
                valueAnimator.setDuration(500);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float scale = (float) animation.getAnimatedValue();
                        view_parent.setScaleX(scale);
                        view_parent.setScaleY(scale);
                    }
                });
                valueAnimator.start();
//                }
            }
        });
    }


    private void setInfo() {
//        textView_title.setGravity(gravity_title != Gravity.NO_GRAVITY ? gravity_title : Gravity.CENTER);
        if (isNotShowTitle)
            textView_title.setVisibility(View.GONE);
        else if (!TextUtils.isEmpty(title_text)) {
            textView_title.setText(title_text);
            textView_title.setVisibility(View.VISIBLE);
        }

//        if (color_title != 0)
//            textView_title.setTextColor(color_title);

//        if (!isShowLineBetweenButtonsAndMessage)
//            findViewById(R.id.view_line_depart_button_and_message).setVisibility(View.GONE);

        if (view_message_instead != null) {
            viewGroup_message_view_container.removeAllViews();
            if (messageViewPaddingLeft != -1)
                viewGroup_message_view_container.setPadding(messageViewPaddingLeft, messageViewPaddingTop, messageViewPaddingRight, messageViewPaddingBottom);
            viewGroup_message_view_container.addView(view_message_instead, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        } else
            textView_message.setText(message_text);
        if (button_count == 1) {
            button_center.setVisibility(View.VISIBLE);
            button_sure.setVisibility(View.GONE);
            button_cancel.setVisibility(View.GONE);
            view_departButtonsline.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(sure_text))
                button_center.setText(sure_text);
            if (sureButtonColor != 0)
                button_center.setTextColor(sureButtonColor);

        } else if (button_count == 2) {
            button_center.setVisibility(View.GONE);
            button_sure.setVisibility(View.VISIBLE);
            button_cancel.setVisibility(View.VISIBLE);
            view_departButtonsline.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(sure_text))
                button_sure.setText(sure_text);
            if (!TextUtils.isEmpty(cancel_text))
                button_cancel.setText(cancel_text);
            if (sureButtonColor != 0)
                button_sure.setTextColor(sureButtonColor);
        } else {
            button_center.setVisibility(View.GONE);
            button_sure.setVisibility(View.GONE);
            button_cancel.setVisibility(View.GONE);
            view_departButtonsline.setVisibility(View.GONE);
            findViewById(R.id.view_line_depart_button_and_message).setVisibility(View.GONE);
        }
    }


    public MaterialDialog buildButtonCount(int button_count) {
        this.button_count = button_count > 2 ? 2 : (button_count < 0 ? 0 : button_count);
        return this;
    }

    public MaterialDialog buildTitle(CharSequence title_text) {
        this.title_text = title_text;
        return this;
    }

    public MaterialDialog clearTitle() {
        this.isNotShowTitle = true;
        return this;
    }

    public MaterialDialog clearLineBetweenButtonsAndMessage() {
//        this.isShowLineBetweenButtonsAndMessage = false;
        return this;
    }

    public MaterialDialog buildTitle(int title_text_id) {
        if (title_text_id > 0)
            this.title_text = mResources.getString(title_text_id);
        return this;
    }

    public MaterialDialog buildMessage(CharSequence message_text) {
        this.message_text = message_text;
        return this;
    }

    public MaterialDialog buildMessage(int message_text_id) {
        if (message_text_id > 0)
            this.message_text = mResources.getString(message_text_id);
        return this;
    }

    public MaterialDialog buildSureButtonText(String sure_text) {
        this.sure_text = sure_text;
        return this;
    }

    public MaterialDialog buildSureButtonText(int sure_text_id) {
        if (sure_text_id > 0)
            this.sure_text = mResources.getString(sure_text_id);
        return this;
    }

    public MaterialDialog buildSureButtonColor(int colorId) {
        sureButtonColor = colorId;
        return this;
    }

    public MaterialDialog buildCancelButtonText(String cancel_text) {
        this.cancel_text = cancel_text;
        return this;
    }

    public MaterialDialog buildCancelButtonText(int cancel_text_id) {
        if (cancel_text_id > 0)
            this.cancel_text = mResources.getString(cancel_text_id);
        return this;
    }

    public MaterialDialog buildClickListener(View.OnClickListener onClickListener_cancel, View.OnClickListener onClickListener_sure) {
        this.onClickListener_cancel = onClickListener_cancel;
        this.onClickListener_sure = onClickListener_sure;
        return this;
    }

    public MaterialDialog setMessageView(View view) {
        this.view_message_instead = view;
        return this;
    }

//    public MaterialDialog setTitleGravity(int gravity_title) {
//        this.gravity_title = gravity_title;
//        return this;
//    }
//
//    public MaterialDialog setTitleColor(int color) {
//        this.color_title = color;
//        return this;
//    }

    public MaterialDialog setWidthPercentOfScreen(float widthPercentOfScreen) {
        this.widthPercentOfScreen = widthPercentOfScreen;
        return this;
    }

    public MaterialDialog setMessageViewPadding(int messageViewPadding) {
        messageViewPaddingLeft = messageViewPaddingRight = messageViewPaddingTop = messageViewPaddingBottom = messageViewPadding;
        return this;
    }

    public MaterialDialog setAutoDismissEnable(boolean autoDismissEnable) {
        this.isAutoDismissEnable = autoDismissEnable;
        return this;
    }

    public MaterialDialog setOutSideClickListener(View.OnClickListener onClickListener_outside) {
        this.onClickListener_outside = onClickListener_outside;
        return this;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.fl_parent) {
            if (onClickListener_outside != null)
                onClickListener_outside.onClick(view);
            return;
        }
        if (isAutoDismissEnable)
            dismiss();
        if (id == R.id.btn_right_click_material || id == R.id.btn_center_click_material) {
            if (onClickListener_sure != null)
                onClickListener_sure.onClick(view);
        } else if (id == R.id.btn_left_click_material) {
            if (onClickListener_cancel != null)
                onClickListener_cancel.onClick(view);
        }
    }
}
