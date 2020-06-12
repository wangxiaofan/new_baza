package com.baza.android.bzw.businesscontroller.tracking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baza.android.bzw.bean.resume.BubbleBean;
import com.baza.android.bzw.businesscontroller.tracking.presenter.TrackingSearchPresenter;
import com.bznet.android.rcbox.R;

import java.util.List;

public class BubbleAdapter extends RecyclerView.Adapter<BubbleAdapter.ViewHolder> {

    private Context mContext;

    private List<BubbleBean> mData;

    private TrackingSearchPresenter mPresenter;

    private int currentPosition = -1;

    public BubbleAdapter(Context mContext, TrackingSearchPresenter presenter) {
        this.mContext = mContext;
        this.mPresenter = presenter;
        this.mData = mPresenter.getBubbleList();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_bubble_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BubbleBean bean = mData.get(position);
        holder.tvName.setText(bean.getFilterName());
        if (currentPosition == position) {
            holder.tvName.setChecked(true);
        } else {
            holder.tvName.setChecked(false);
        }
        if (mPresenter.getSearchFilterInfo() != null && mPresenter.getSearchFilterInfo().filterType != null
                && mPresenter.getSearchFilterInfo().filterType.equals(bean.getFilterType())) {
            holder.tvName.setChecked(true);
            currentPosition = position;
        }
        holder.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPosition == position) {
                    currentPosition = -1;
                    mPresenter.filterType("");
                } else {
                    currentPosition = position;
                    mPresenter.filterType(bean.getFilterType());
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }
//
//    public interface OnItemClickListener {
//        void onClick(int position);
//    }
//
//    private OnItemClickListener listener;
//
//    public void setOnItemClickListener(OnItemClickListener listener) {
//        this.listener = listener;
//    }
}
