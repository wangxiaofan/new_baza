package com.baza.android.bzw.businesscontroller.login.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.job.TalentTypeTreeResultBean;
import com.baza.android.bzw.widget.LineBreakLayout;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2018/8/1.
 * Title：
 * Note：
 */
public class TalentTypeAdapter extends BaseBZWAdapter implements CompoundButton.OnCheckedChangeListener {
    private Context mContext;
    private Resources mResources;
    private List<TalentTypeTreeResultBean.TalentTypeParentBean> mDataList;
    private SparseIntArray mSelectedMap;
    private int mChildItemWidth;
    private int mChildItemHeight;
    private int mChildPaddingLR;
    private int mTextSize;

    public TalentTypeAdapter(Context context, List<TalentTypeTreeResultBean.TalentTypeParentBean> dataList, SparseIntArray selectedMap, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mContext = context;
        this.mResources = context.getResources();
        this.mDataList = dataList;
        this.mSelectedMap = selectedMap;

        this.mChildItemWidth = (int) ((ScreenUtil.screenWidth - mResources.getDimension(R.dimen.dp_50)) / 3);
        this.mChildItemHeight = (int) mResources.getDimension(R.dimen.dp_30);
        this.mChildPaddingLR = (int) mResources.getDimension(R.dimen.dp_5);
        this.mTextSize = ScreenUtil.px2dip(mResources.getDimension(R.dimen.text_size_12));
    }

    @Override
    public int getCount() {
        return (mDataList == null ? 0 : mDataList.size());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.login_adapter_talent_type_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();
        TalentTypeTreeResultBean.TalentTypeParentBean talentTypeParentBean = mDataList.get(position);
        viewHolder.textView_parentTitle.setText((talentTypeParentBean.item != null ? talentTypeParentBean.item.name : null));
        if (talentTypeParentBean.children != null && !talentTypeParentBean.children.isEmpty())
            setChildTalentType(viewHolder.lineBreakLayout_child, talentTypeParentBean.children);
        else viewHolder.lineBreakLayout_child.setVerticalDepart(View.GONE);

        return convertView;
    }

    private void setChildTalentType(LineBreakLayout lineBreakLayout, ArrayList<TalentTypeTreeResultBean.TalentTypeChildBean> children) {
        int childCount = lineBreakLayout.getChildCount();
        int needShowCount = children.size();
        boolean hasCacheView;
        CheckBox checkBox;
        TalentTypeTreeResultBean.TalentTypeChildBean talentTypeChildBean;
        for (int i = 0; i < needShowCount; i++) {
            hasCacheView = (i < childCount);
            checkBox = hasCacheView ? (CheckBox) lineBreakLayout.getChildAt(i) : createChildItem();
            talentTypeChildBean = children.get(i);
            checkBox.setText(talentTypeChildBean.item != null ? talentTypeChildBean.item.name : null);
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked((mSelectedMap != null && talentTypeChildBean.item != null && mSelectedMap.indexOfKey(talentTypeChildBean.item.id) >= 0));
            checkBox.setOnCheckedChangeListener(this);
            checkBox.setTag(talentTypeChildBean);
            checkBox.setVisibility(View.VISIBLE);
            if (!hasCacheView)
                lineBreakLayout.addView(checkBox);
        }
        if (childCount > needShowCount)
            for (; needShowCount < childCount; needShowCount++)
                lineBreakLayout.getChildAt(needShowCount).setVisibility(View.GONE);

        lineBreakLayout.setVisibility(View.VISIBLE);
    }

    @SuppressLint("ResourceType")
    private CheckBox createChildItem() {
        CheckBox checkBox = new CheckBox(mContext);
        checkBox.setBackgroundResource(R.drawable.login_interested_talent_type_cb_bg);
        checkBox.setGravity(Gravity.CENTER);
        checkBox.setMaxLines(1);
        checkBox.setLayoutParams(new ViewGroup.LayoutParams(mChildItemWidth, mChildItemHeight));
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            try {
                Field field = checkBox.getClass().getSuperclass().getDeclaredField("mButtonDrawable");
                field.setAccessible(true);
                field.set(checkBox, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            checkBox.setButtonDrawable(null);
        checkBox.setEllipsize(TextUtils.TruncateAt.END);
        checkBox.setPadding(mChildPaddingLR, 0, mChildPaddingLR, 0);
        checkBox.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mTextSize);
        checkBox.setTextColor(mContext.getResources().getColorStateList(R.drawable.login_talent_type_cb_text_color));
        return checkBox;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        TalentTypeTreeResultBean.TalentTypeChildBean talentTypeChildBean = (TalentTypeTreeResultBean.TalentTypeChildBean) buttonView.getTag();
        if (talentTypeChildBean.item != null) {
            if (isChecked)
                mSelectedMap.put(talentTypeChildBean.item.id, talentTypeChildBean.item.id);
            else
                mSelectedMap.delete(talentTypeChildBean.item.id);
        }
    }

    private static class ViewHolder {
        TextView textView_parentTitle;
        LineBreakLayout lineBreakLayout_child;

        public ViewHolder(View convertView) {
            textView_parentTitle = convertView.findViewById(R.id.tv_parent_title);
            lineBreakLayout_child = convertView.findViewById(R.id.lbl_child);
        }
    }
}
