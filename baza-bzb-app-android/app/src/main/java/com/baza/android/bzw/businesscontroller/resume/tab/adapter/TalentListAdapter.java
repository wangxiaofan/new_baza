package com.baza.android.bzw.businesscontroller.resume.tab.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baza.android.bzw.bean.resume.TalentListBean;
import com.baza.android.bzw.businesscontroller.account.CollectionActivity;
import com.baza.android.bzw.businesscontroller.company.CompanyTalentActivity;
import com.baza.android.bzw.businesscontroller.floating.FloatingActivity;
import com.baza.android.bzw.businesscontroller.resume.tab.MineResumeActivity;
import com.baza.android.bzw.businesscontroller.tracking.TrackingActivity;
import com.baza.android.bzw.log.logger.TalentLogger;
import com.bznet.android.rcbox.R;

import java.util.List;

public class TalentListAdapter extends RecyclerView.Adapter<TalentListAdapter.TalentHolder> {

    private Context mContext;

    private List<TalentListBean> mData;

    private TalentLogger mTalentLogger;

    public TalentListAdapter(Context mContext, List<TalentListBean> mData, TalentLogger talentLogger) {
        this.mContext = mContext;
        this.mData = mData;
        this.mTalentLogger = talentLogger;
    }

    @NonNull
    @Override
    public TalentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_talent_list, parent, false);
        return new TalentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TalentHolder holder, int position) {
        TalentListBean bean = mData.get(position);
        holder.tvName.setText(bean.getName());
        holder.ivIcon.setImageResource(bean.getRes());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventCode = "";
                if (bean.getId() == TalentListBean.COMPANY_TALENT) {
                    CompanyTalentActivity.launch((Activity) mContext);
                    eventCode = "OpenFirmTalent";
                } else if (bean.getId() == TalentListBean.MY_TALENT) {
                    MineResumeActivity.launch((Activity) mContext);
                    eventCode = "OpenMyTalent";
                } else if (bean.getId() == TalentListBean.TRACKING_LIST) {
                    TrackingActivity.launch((Activity) mContext);
                    eventCode = "OpenTrackingList";
                } else if (bean.getId() == TalentListBean.FLOATING_LIST) {
                    FloatingActivity.launch((Activity) mContext);
                    eventCode = "OpenFloatingList";
                } else if (bean.getId() == TalentListBean.MY_COLLECTION) {
                    CollectionActivity.launch((Activity) mContext);
                    eventCode = "OpenMyCollection";
                }
                mTalentLogger.sendClickLog(mContext, eventCode);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class TalentHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView ivIcon;

        public TalentHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            ivIcon = itemView.findViewById(R.id.iv_icon);
        }
    }
}
