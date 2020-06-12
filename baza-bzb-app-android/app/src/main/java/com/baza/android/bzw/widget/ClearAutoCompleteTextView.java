package com.baza.android.bzw.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/5/17.
 * Title：
 * Note：
 */

public class ClearAutoCompleteTextView extends AutoCompleteTextView implements View.OnTouchListener, TextWatcher {
    private Drawable drawableDelete;

    private boolean isDeleteIconShown;

    public ClearAutoCompleteTextView(Context context) {
        this(context, null);
    }

    public ClearAutoCompleteTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.autoCompleteTextViewStyle);
    }

    public ClearAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        drawableDelete = getContext().getResources().getDrawable(R.drawable.icon_edit_delete);
        drawableDelete.setBounds(0, 0, drawableDelete.getIntrinsicWidth(), drawableDelete.getIntrinsicHeight());
        addTextChangedListener(this);
        setOnTouchListener(this);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!isDeleteIconShown)
            return false;
        if (event.getAction() != MotionEvent.ACTION_UP)
            return false;
        if (event.getX() > getWidth() - getPaddingRight() - drawableDelete.getIntrinsicWidth() * 2) {
            setText(null);
//            return isNotSpecialDevice();
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
        showOrHideDeleteIcon(s.length() > 0);
    }


    private void showOrHideDeleteIcon(boolean isShow) {
        if (isShow && !isDeleteIconShown && drawableDelete != null) {
            isDeleteIconShown = true;
            Drawable[] drawables = getCompoundDrawables();
            setCompoundDrawables(drawables[0], drawables[1], drawableDelete, drawables[3]);
        } else if (!isShow && isDeleteIconShown && drawableDelete != null) {
            isDeleteIconShown = false;
            Drawable[] drawables = getCompoundDrawables();
            setCompoundDrawables(drawables[0], drawables[1], null, drawables[3]);
        }
    }

    public void setDeleteIcon(int iconId) {
        if (iconId <= 0) {
            drawableDelete = null;
        } else {
            addTextChangedListener(this);
            setOnTouchListener(this);
            drawableDelete = getContext().getResources().getDrawable(iconId);
            drawableDelete.setBounds(0, 0, drawableDelete.getIntrinsicWidth(), drawableDelete.getIntrinsicHeight());
        }
        invalidate();
    }


}
