package com.baza.android.bzw.keyboard;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baza.android.bzw.rangeseekbar.R;

/**
 * Created by Vincent.Lei on 2018/3/6.
 * Title：
 * Note：
 */

public class NumKeyBoardView extends LinearLayout implements View.OnClickListener {
    private static final int POSITION_ZERO = 10;
    private static final int POSITION_HIDE = 11;
    private static final int POSITION_NONE = 9;

    public interface INumKeyBoardListener {
        void onDeleteKeyClick();

        void onNumKeyClick(int num);

        void onHideKeyClick();

        void onCompleteKeyClick();
    }

    private INumKeyBoardListener mKeyBoardListener;

    public NumKeyBoardView(Context context) {
        this(context, null);
    }

    public NumKeyBoardView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumKeyBoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setNumKeyBoardListener(INumKeyBoardListener keyBoardListener) {
        this.mKeyBoardListener = keyBoardListener;
    }

    private void init(Context context) {
        setOrientation(HORIZONTAL);
        setBackgroundColor(Color.parseColor("#CCCED3"));
        int itemHeight = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.67f * 0.78f) / 4;
        View view = LayoutInflater.from(context).inflate(R.layout.num_keyboard, this);
        GridView gridView = view.findViewById(R.id.gv);
        gridView.setAdapter(new NumAdapter(context, itemHeight, this));
        view.findViewById(R.id.iv_del).setOnClickListener(this);
        view.findViewById(R.id.tv_complete).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mKeyBoardListener == null)
            return;
        int id = v.getId();
        if (id == R.id.iv_del) {
            mKeyBoardListener.onDeleteKeyClick();
            return;
        }
        if (id == R.id.tv_complete) {
            mKeyBoardListener.onCompleteKeyClick();
            return;
        }

        int position = (int) v.getTag();
        if (position == POSITION_ZERO || position < POSITION_NONE) {
            mKeyBoardListener.onNumKeyClick(position == POSITION_ZERO ? 0 : position + 1);
            return;
        }
        if (position == POSITION_HIDE)
            mKeyBoardListener.onHideKeyClick();
    }

    private static class NumAdapter extends BaseAdapter {

        private Context mContext;
        private int mItemHeight;
        private OnClickListener mOnClickListener;

        public NumAdapter(Context context, int itemHeight, OnClickListener onClickListener) {
            this.mContext = context;
            this.mItemHeight = itemHeight;
            this.mOnClickListener = onClickListener;
        }

        @Override
        public int getCount() {
            return 12;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position < POSITION_NONE || position == POSITION_ZERO) {
                TextView textView = new TextView(mContext);
                textView.setTextColor(Color.BLACK);
                textView.setBackgroundResource(R.drawable.keyboard_click_one);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                textView.setText(String.valueOf(position == POSITION_ZERO ? 0 : position + 1));
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight));
                convertView = textView;
            } else if (position == POSITION_HIDE) {
                ImageView imageView = new ImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageView.setImageResource(R.drawable.keyboard_key_hide);
                imageView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight));
                convertView = imageView;
            } else {
                convertView = new View(mContext);
                convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight));
            }
            convertView.setTag(position);
            convertView.setOnClickListener(mOnClickListener);
            return convertView;
        }
    }
}
