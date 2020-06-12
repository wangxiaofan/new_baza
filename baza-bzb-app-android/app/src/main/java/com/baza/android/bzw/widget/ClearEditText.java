package com.baza.android.bzw.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bznet.android.rcbox.R;

/**
 * Created by LW on 2016/icon_collect/icon_collect.
 * Title :
 * Note :
 */
public class ClearEditText extends AppCompatEditText implements View.OnTouchListener, TextWatcher {
    public interface IClearEditTextCheckOkStatusListener {
        boolean shouldEnterOkStatus(ClearEditText clearEditText, Editable s);
    }

    private Drawable drawableDelete;
    private Drawable drawableOk;
    private boolean checkOkStatus;
    private boolean alreadyCheckOkBefore;
    private boolean enableDelete;
    private IClearEditTextCheckOkStatusListener checkOkStatusListener;

    public void setCheckOkStatusListener(IClearEditTextCheckOkStatusListener checkOkStatusListener) {
        this.checkOkStatusListener = checkOkStatusListener;
    }


    public ClearEditText(Context context) {
        this(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ClearEditText);
            drawableDelete = ta.getDrawable(R.styleable.ClearEditText_deleteIcon);
            checkOkStatus = ta.getBoolean(R.styleable.ClearEditText_checkOkStatus, false);
            if (checkOkStatus)
                drawableOk = ta.getDrawable(R.styleable.ClearEditText_okIcon);
            ta.recycle();
        }
        init();
    }


    private void init() {
        if (drawableDelete == null)
            drawableDelete = getContext().getResources().getDrawable(R.drawable.icon_edit_delete);
        drawableDelete.setBounds(0, 0, drawableDelete.getIntrinsicWidth(), drawableDelete.getIntrinsicHeight());
        if (drawableOk != null)
            drawableOk.setBounds(0, 0, drawableOk.getIntrinsicWidth(), drawableOk.getIntrinsicHeight());
        addTextChangedListener(this);
        setOnTouchListener(this);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!enableDelete || event.getAction() != MotionEvent.ACTION_UP)
            return false;
        if (event.getX() > getWidth() - getPaddingRight() - drawableDelete.getIntrinsicWidth() * 2) {
            setText(null);
            event.setAction(MotionEvent.ACTION_CANCEL);
        }
        return false;
    }


    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (isStatusCheckOk()) {
            if (enableDelete || !alreadyCheckOkBefore)
                setCompoundDrawables(null, null, drawableOk, null);
            alreadyCheckOkBefore = true;
            enableDelete = false;
            return;
        }
        if (drawableDelete == null || s.length() == 0) {
            if (enableDelete)
                setCompoundDrawables(null, null, null, null);
            enableDelete = false;
            return;
        }
        if (enableDelete)
            return;
        enableDelete = true;
        setCompoundDrawables(null, null, drawableDelete, null);
    }

    public void setDeleteIcon(int iconId) {
        enableDelete = false;
        if (iconId <= 0) {
            drawableDelete = null;
            if (!isStatusCheckOk())
                setCompoundDrawables(null, null, null, null);
        } else {
            drawableDelete = getContext().getResources().getDrawable(iconId);
            drawableDelete.setBounds(0, 0, drawableDelete.getIntrinsicWidth(), drawableDelete.getIntrinsicHeight());
            if (isStatusCheckOk())
                return;
            if (!TextUtils.isEmpty(getText().toString())) {
                enableDelete = true;
                setCompoundDrawables(null, null, drawableDelete, null);
            }
        }
    }

    private boolean isStatusCheckOk() {
        return (checkOkStatus && checkOkStatusListener != null && drawableOk != null && checkOkStatusListener.shouldEnterOkStatus(this, getText()));
    }
}
