package com.baza.android.bzw.businesscontroller.search.newfilter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.baza.android.bzw.bean.label.Label;
import com.baza.android.bzw.extra.ICompanyFilterListener;
import com.baza.android.bzw.log.LogUtil;
import com.baza.android.bzw.widget.LineBreakLayout;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Vincent.Lei on 2017/3/23.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class NewLabelFilterUI implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private Context mContext;
    private View view_root;
    //    private Resources mResources;
    private LayoutInflater mLayoutInflater;
    private ICompanyFilterListener mFilterListener;
    private ArrayList<Label> mLabelLibrary;
    private HashMap<String, Label> mLabelSelected = new HashMap<>();
    private LineBreakLayout lineBreakLayout_labelContainer;
    private boolean mMayShouldChanged;
    private TextView tvEmpty;

    public NewLabelFilterUI(Context mContext, ICompanyFilterListener mFilterListener, ArrayList<Label> mLabelLibrary) {
        this.mContext = mContext;
        this.mFilterListener = mFilterListener;
//        this.mResources = mContext.getResources();
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.mLabelLibrary = mLabelLibrary;
        init();
    }

    public View getView() {
        return view_root;
    }

    public void show(boolean mayReset, ArrayList<Label> labelsOutSide, boolean isOutSideEnable) {
        if (isOutSideEnable) {
            mMayShouldChanged = true;
            LogUtil.d("reset labels for outside");
            mLabelSelected.clear();
            if (labelsOutSide != null && !labelsOutSide.isEmpty()) {
                Label label;
                for (int i = 0; i < labelsOutSide.size(); i++) {
                    label = labelsOutSide.get(i);
                    mLabelSelected.put(label.tag, label);
                }
            }
        }

        if (mayReset || isOutSideEnable)
            mayReBackPrevious();
        view_root.setVisibility(View.VISIBLE);
    }

    public void hide() {
        view_root.setVisibility(View.GONE);
    }

    private void init() {
        view_root = mLayoutInflater.inflate(R.layout.layout_filter_label, null);
        lineBreakLayout_labelContainer = view_root.findViewById(R.id.lbl_label_container);
        tvEmpty = view_root.findViewById(R.id.tv_empty);
        view_root.findViewById(R.id.tv_clear_filter).setOnClickListener(this);
        view_root.findViewById(R.id.tv_submit_filter).setOnClickListener(this);
        if (mLabelLibrary.size() <= 0) {
            lineBreakLayout_labelContainer.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            lineBreakLayout_labelContainer.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            addLabels();
        }
    }

    public void refreshLabel(ArrayList<Label> mLabelLibrary) {
        this.mLabelLibrary = mLabelLibrary;
        if (mLabelLibrary.size() <= 0) {
            lineBreakLayout_labelContainer.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            lineBreakLayout_labelContainer.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            addLabels();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_clear_filter:
                clearAllLabels();
                mLabelSelected.clear();
                mMayShouldChanged = false;
                if (mFilterListener != null)
                    mFilterListener.clearLabelsFilter();
                break;
            case R.id.tv_submit_filter:
                mMayShouldChanged = false;
                mLabelSelected.clear();
                Label label;
                CheckBox checkBox;
                for (int i = 0, size = lineBreakLayout_labelContainer.getChildCount(); i < size; i++) {
                    checkBox = (CheckBox) lineBreakLayout_labelContainer.getChildAt(i);
                    label = (Label) checkBox.getTag(R.id.filter_id_tag);
                    if (checkBox.isChecked())
                        mLabelSelected.put(label.tag, label);
                }
                if (mFilterListener != null)
                    mFilterListener.onLabelSelected(mLabelSelected);
                break;
        }
    }

    private void mayReBackPrevious() {
        if (!mMayShouldChanged)
            return;
        CheckBox checkBox;
        Label label;
        for (int i = 0, size = lineBreakLayout_labelContainer.getChildCount(); i < size; i++) {
            checkBox = (CheckBox) lineBreakLayout_labelContainer.getChildAt(i);
            label = (Label) checkBox.getTag(R.id.filter_id_tag);
            if (label == null)
                continue;
            checkBox.setChecked(mLabelSelected.get(label.tag) != null);
        }
        mMayShouldChanged = false;
    }

    private void addLabels() {
        CheckBox checkBox;
        int itemHeight = (int) mContext.getResources().getDimension(R.dimen.dp_25);
        int currentCount = lineBreakLayout_labelContainer.getChildCount();
        int needShowCount = (mLabelLibrary == null ? 0 : mLabelLibrary.size());
        boolean hasCacheView;
        Label label;
        for (int i = 0; i < needShowCount; i++) {
            hasCacheView = i < currentCount;
            label = mLabelLibrary.get(i);
            checkBox = (hasCacheView ? (CheckBox) lineBreakLayout_labelContainer.getChildAt(i) : (CheckBox) mLayoutInflater.inflate(R.layout.layout_label_filter_item, null));
            checkBox.setText(label.tag);
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setOnCheckedChangeListener(this);
            checkBox.setTag(R.id.filter_id_tag, label);
            if (!hasCacheView)
                lineBreakLayout_labelContainer.addView(checkBox, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, itemHeight));
        }
        int shouldHideCount = currentCount - needShowCount;
        if (shouldHideCount > 0)
            for (int index = needShowCount; index < currentCount; index++) {
                lineBreakLayout_labelContainer.getChildAt(index).setVisibility(View.GONE);
            }
    }

    private void clearAllLabels() {
        CheckBox checkBox;
        for (int index = 0, count = lineBreakLayout_labelContainer.getChildCount(); index < count; index++) {
            checkBox = (CheckBox) lineBreakLayout_labelContainer.getChildAt(index);
            checkBox.setChecked(false);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mMayShouldChanged = true;
    }
}
