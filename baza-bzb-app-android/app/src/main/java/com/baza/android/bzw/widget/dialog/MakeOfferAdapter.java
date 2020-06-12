package com.baza.android.bzw.widget.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baza.android.bzw.bean.resume.FirmMembersResultBean;
import com.baza.android.bzw.bean.resume.SplitInfoListResultBean;
import com.bznet.android.rcbox.R;

import java.util.List;

public class MakeOfferAdapter extends RecyclerView.Adapter<MakeOfferAdapter.ViewHolder> {

    private Context ctx;

    private List<SplitInfoListResultBean.SplitInfoBean> splitInfoBeans;

    private List<FirmMembersResultBean.FirmMembersBean> firmMembers;

//    public MakeOfferAdapter(Context ctx, List<SplitInfoListResultBean.SplitInfoBean> splitInfoBeans, List<FirmMembersResultBean.FirmMembersBean> firmMembers) {
//        this.splitInfoBeans = splitInfoBeans;
//        this.firmMembers = firmMembers;
//    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.item_list_make_offer, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        holder.tvName.setText(splitInfoBeans.get(position).getName());
        View content = LayoutInflater.from(ctx).inflate(R.layout.item_list_make_offer_detail, null);
        holder.llContent.addView(content);
    }

    @Override
    public int getItemCount() {
//        return splitInfoBeans.size();
        return 4;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llContent;

        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llContent=itemView.findViewById(R.id.ll_content);
            tvName=itemView.findViewById(R.id.tv_name);
        }
    }
}
