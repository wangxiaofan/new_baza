package com.baza.android.bzw.widget.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

import baza.dialog.simpledialog.MaterialDialog;


/**
 * Created by LW on 2016/5/30.
 * Title :
 * Note :
 */
public class ListMenuDialog implements View.OnClickListener {

    private String[] mItems;
    private String mTitle;
    private Context mContext;
    private IOnChoseItemClickListener mListener;

    private MaterialDialog mMaterialDialog;

    private boolean mIsShowTitle;
    private boolean mIsDismissWhenTouchOutside;

    public interface IOnChoseItemClickListener {
        void onChoseItemClick(int position);
    }

    private ListMenuDialog(Context context, String title, String[] items, boolean mIsDismissWhenTouchOutside, IOnChoseItemClickListener listener) {
        this.mItems = items;
        this.mContext = context;
        this.mListener = listener;
        this.mTitle = title;
        this.mIsDismissWhenTouchOutside = mIsDismissWhenTouchOutside;
        if (!TextUtils.isEmpty(title))
            mIsShowTitle = true;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        if (position == mItems.length) {
            mMaterialDialog.dismiss();
            return;
        }

        if (mListener != null)
            mListener.onChoseItemClick(position);
        mMaterialDialog.dismiss();
    }

    public static void showNewInstance(Context context, String[] items, IOnChoseItemClickListener listener) {
        showNewInstance(context, null, items, listener);
    }

    public static void showNewInstance(Context context, String title, String[] items, IOnChoseItemClickListener listener) {
        showNewInstance(context, title, items, false, listener);
    }

    public static void showNewInstance(Context context, String title, String[] items, boolean mIsDismissWhenTouchOutside, IOnChoseItemClickListener listener) {
        new ListMenuDialog(context, title, items, mIsDismissWhenTouchOutside, listener).makeNewDialog();
    }


    private void makeNewDialog() {
        mMaterialDialog = new MaterialDialog(mContext);
        mMaterialDialog.setCancelable(true);
        mMaterialDialog.buildTitle(mTitle == null ? mContext.getResources().getString(R.string.please_chose) : mTitle).clearLineBetweenButtonsAndMessage();
        mMaterialDialog.buildButtonCount(0);
        mMaterialDialog.setWidthPercentOfScreen(0.7f);
        if (!mIsShowTitle)
            mMaterialDialog.clearTitle();
        mMaterialDialog.setCanceledOnTouchOutside(mIsDismissWhenTouchOutside);
        LinearLayout linearLayout = new LinearLayout(mContext);
        mMaterialDialog.setMessageViewPadding(0);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        int viewHeight = (int) mContext.getResources().getDimension(R.dimen.dp_40);
        int margin = (int) mContext.getResources().getDimension(R.dimen.dp_20);
        int textSize = ScreenUtil.px2dip(mContext.getResources().getDimension(R.dimen.text_size_14));
        int textColor = mContext.getResources().getColor(R.color.text_color_blue_0D315C);
        int end = mItems.length - 1;
        LinearLayout.MarginLayoutParams mlp;
        if (mItems.length > 0) {
            TextView textView;
            for (int i = 0, length = mItems.length; i < length; i++) {
                textView = new TextView(mContext);
                textView.setTextColor(textColor);
                textView.setTextSize(textSize);
                textView.setGravity(Gravity.CENTER);
                textView.setText(mItems[i]);
                textView.setOnClickListener(this);
                textView.setTag(i);
                if (i != end)
                    textView.setBackgroundResource(R.drawable.background_default_with_bottom_line);
                else
                    textView.setBackgroundColor(mContext.getResources().getColor(android.R.color.white));
                mlp = new LinearLayout.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, viewHeight);
                mlp.leftMargin = margin;
                mlp.rightMargin = margin;
                linearLayout.addView(textView, mlp);

            }

        }
        mMaterialDialog.setMessageView(linearLayout);
        try {
            mMaterialDialog.show();
        } catch (Exception e) {
            //ignore
        }
    }
}
