package com.baza.android.bzw.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import com.bznet.android.rcbox.R;


/**
 * Created by Vincent.Lei on 2018/11/9.
 * Title：
 * Note：
 */
public class FoldTextView extends androidx.appcompat.widget.AppCompatTextView implements ViewTreeObserver.OnGlobalLayoutListener {
    private CharSequence mSource;
    private BufferType mType;
    private int mToggleTextColor = Color.BLACK;
    private ClickableSpan mClickableSpan;
    private String mFoldTextHint;
    private String mExpandTextHint;
    private int mFoldLine;
    private int mSourceLineCount;
    private boolean mIsFold = true;
    private boolean mHasProcessChanged;
    private SpannableString mEditFoldSpan;

    public FoldTextView(Context context) {
        this(context, null);
    }

    public FoldTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FoldTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setHighlightColor(getResources().getColor(android.R.color.transparent));
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FoldTextView);
            if (typedArray != null) {
                mFoldLine = typedArray.getInt(R.styleable.FoldTextView_foldLine, 0);
                mToggleTextColor = typedArray.getColor(R.styleable.FoldTextView_toggleTextColor, Color.BLACK);
                mFoldTextHint = typedArray.getString(R.styleable.FoldTextView_foldToggleText);
                mExpandTextHint = typedArray.getString(R.styleable.FoldTextView_expandToggleText);
                typedArray.recycle();
            }
        }

        if (TextUtils.isEmpty(mFoldTextHint))
            mFoldTextHint = "Close>>";
        if (TextUtils.isEmpty(mExpandTextHint))
            mExpandTextHint = "...Open>>";
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (mEditFoldSpan == null || !(mEditFoldSpan.equals(text))) {
            mSource = text;
            mSourceLineCount = 0;
            mHasProcessChanged = false;
        }
        mType = type;
        super.setText(text, type);
        foldText();
    }


    public void setFoldLine(int foldLine) {
        if (this.mFoldLine != foldLine) {
            this.mFoldLine = foldLine;
            mHasProcessChanged = false;
            requestLayout();
        }
    }

    public void toggleFold(boolean isFold) {
        if (this.mIsFold != isFold) {
            this.mIsFold = isFold;
            mHasProcessChanged = false;
            requestLayout();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    private void foldText() {
        if (TextUtils.isEmpty(mSource) || mHasProcessChanged)
            return;
        mHasProcessChanged = true;
        int lineCountCount = getLineCount();
        if (mSourceLineCount == 0)
            mSourceLineCount = lineCountCount;
        Paint paint = getPaint();
        if (mFoldLine <= 0 || lineCountCount < mFoldLine || mSourceLineCount == mFoldLine) {
            super.setText(mSource, mType);
            return;
        }
        String toggleText = (mIsFold ? mExpandTextHint : mFoldTextHint);
        int expandedTextWidth = (int) (2 * paint.measureText(toggleText));
        if (!mIsFold) {
            mEditFoldSpan = new SpannableString(mSource + toggleText);
        } else {
            CharSequence lastLineText;
            int start = getLayout().getLineStart(mFoldLine - 1);
            int end = getLayout().getLineEnd(mFoldLine - 1);
            lastLineText = mSource.subSequence(start, end);
            int measureEnd = lastLineText.length();
            while (measureEnd > 0 && paint.measureText(lastLineText, 0, measureEnd) + expandedTextWidth > getMeasuredWidth()) {
                measureEnd -= 2;
            }
            if (measureEnd > 1)
                lastLineText = lastLineText.subSequence(0, measureEnd);
            if (mFoldLine == 1)
                mEditFoldSpan = new SpannableString(lastLineText + toggleText);
            else
                mEditFoldSpan = new SpannableString(mSource.subSequence(0, start).toString() + lastLineText + toggleText);
        }
        if (mClickableSpan == null)
            mClickableSpan = new ClickableSpan();
        mEditFoldSpan.setSpan(mClickableSpan, mEditFoldSpan.length() - toggleText.length(), mEditFoldSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        super.setText(mEditFoldSpan, mType);
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onGlobalLayout() {
        foldText();
    }

    private class ClickableSpan extends android.text.style.ClickableSpan {
        @Override
        public void onClick(@NonNull View widget) {
            toggleFold(!mIsFold);
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            ds.setColor(mToggleTextColor);
            ds.setUnderlineText(false);
            ds.clearShadowLayer();
        }
    }
}