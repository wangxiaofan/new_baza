package com.baza.android.bzw.businesscontroller.floating.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.resume.FloatingListBean;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.manager.UserInfoManager;
import com.bznet.android.rcbox.R;
import com.slib.utils.DateUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class FloatingListAdapter extends BaseBZWAdapter implements View.OnClickListener {

    private Context mContext;

    private List<FloatingListBean> mFloatingList = new ArrayList<>();

    private StringBuilder companyBuilder = new StringBuilder();

    private StringBuilder recommendBuilder = new StringBuilder();

    private String type;

    private boolean isEdit = false;

    public FloatingListAdapter(Context context, IAdapterEventsListener adapterEventsListener, String type) {
        super(adapterEventsListener);
        this.mContext = context;
        this.type = type;
    }

    public void setData(List<FloatingListBean> floatingList) {
        mFloatingList.clear();
        mFloatingList.addAll(floatingList);
        notifyDataSetChanged();
    }

    public List<FloatingListBean> getData() {
        return mFloatingList;
    }

    public void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
        notifyDataSetChanged();
    }

    public void setAllSelect(boolean select) {
        for (FloatingListBean bean : mFloatingList) {
            bean.setSelect(select);
        }
        notifyDataSetChanged();
    }

    public void refreshItem(View convertView, int position) {

    }

    public void changeStatus(List<String> ids) {
        this.isEdit = false;
        for (String id : ids) {
            for (FloatingListBean bean : mFloatingList) {
                if (bean.getId().equals(id)) {
                    bean.setStatus(1);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mFloatingList == null ? 0 : mFloatingList.size();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_floating_list, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        FloatingListBean bean = mFloatingList.get(position);
        holder.tv_name.setText(bean.getCandidateName());
        holder.tv_integrity.setText(bean.getCompletion());
        try {
            holder.tv_update_time.setText(DateUtil.dateToString(DateUtil.parse(bean.getRecommendDate(), DateUtil.DEFAULT_API_FORMAT), DateUtil.SDF_YMD) + "推荐");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setStatus(bean, holder.tv_feedback_status);
        holder.tv_company_info.setText(getCompanyInfo(bean));
        holder.tv_recommend_info.setText(getRecommendInfo(bean));
        holder.tv_receive_name.setText(bean.getLatestOperation());
        holder.resume_layout.setTag(position);
        holder.resume_layout.setOnClickListener(this);

        if (isEdit) {
            holder.iv_select.setVisibility(View.VISIBLE);
            holder.iv_select.setTag(position);
            holder.iv_select.setOnClickListener(this);
            if (bean.isSelect()) {
                holder.iv_select.setBackgroundResource(R.drawable.btn_yigouxuan_02);
            } else {
                holder.iv_select.setBackgroundResource(R.drawable.btn_weixuanze);
            }
        } else {
            holder.iv_select.setVisibility(View.GONE);
        }
        dealStep(holder, bean);

        holder.tv_receive.setTag(position);
        holder.tv_refuse.setTag(position);
        holder.tv_onekey.setTag(position);
        holder.tv_next.setTag(position);
        holder.tv_refuse.setOnClickListener(this);
        holder.tv_receive.setOnClickListener(this);
        holder.tv_onekey.setOnClickListener(this);
        holder.tv_next.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        switch (v.getId()) {
            case R.id.resume_layout:
                if (isEdit) {
                    mFloatingList.get(position).setSelect(!mFloatingList.get(position).isSelect());
                    notifyDataSetChanged();
                    sendAdapterEventToHost(AdapterEventIdConst.EVENT_ID_FLOATING_SELECT_CLICK, position, null, mFloatingList.get(position));
                } else {
                    sendAdapterEventToHost(AdapterEventIdConst.EVENT_ID_COMPANY_ITEM_CLICK, position, null, mFloatingList.get(position));
                }
                break;
            case R.id.iv_select:
                mFloatingList.get(position).setSelect(!mFloatingList.get(position).isSelect());
                notifyDataSetChanged();
                sendAdapterEventToHost(AdapterEventIdConst.EVENT_ID_FLOATING_SELECT_CLICK, position, null, mFloatingList.get(position));
                break;
            case R.id.tv_receive:
                sendAdapterEventToHost(AdapterEventIdConst.EVENT_ID_FLOATING_ACTION_RECEIVE, position, null, mFloatingList.get(position));
                break;
            case R.id.tv_refuse:
                sendAdapterEventToHost(AdapterEventIdConst.EVENT_ID_FLOATING_ACTION_REFUSE, position, null, mFloatingList.get(position));
                break;
            case R.id.tv_next:
                sendAdapterEventToHost(AdapterEventIdConst.EVENT_ID_FLOATING_ACTION_NEXT, position, null, mFloatingList.get(position));
                break;
            case R.id.tv_one_key:
                sendAdapterEventToHost(AdapterEventIdConst.EVENT_ID_FLOATING_ACTION_ONEKEY, position, null, mFloatingList.get(position));
                break;
        }
    }

    class ViewHolder {
        LinearLayout resume_layout;
        TextView tv_name;
        TextView tv_integrity;
        TextView tv_update_time;
        TextView tv_feedback_status;
        TextView tv_company_info;
        TextView tv_recommend_info;
        TextView tv_receive_name;
        LinearLayout ll_action;
        TextView tv_refuse;
        TextView tv_receive;
        ImageView iv_select;
        TextView tv_onekey;
        TextView tv_next;

        public ViewHolder(View itemView) {
            resume_layout = itemView.findViewById(R.id.resume_layout);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_integrity = itemView.findViewById(R.id.tv_integrity);
            tv_update_time = itemView.findViewById(R.id.tv_update_time);
            tv_feedback_status = itemView.findViewById(R.id.tv_feedback_status);
            tv_company_info = itemView.findViewById(R.id.tv_company_info);
            tv_recommend_info = itemView.findViewById(R.id.tv_recommend_info);
            tv_receive_name = itemView.findViewById(R.id.tv_receive_name);
            ll_action = itemView.findViewById(R.id.ll_action);
            tv_refuse = itemView.findViewById(R.id.tv_refuse);
            tv_receive = itemView.findViewById(R.id.tv_receive);
            iv_select = itemView.findViewById(R.id.iv_select);
            tv_onekey = itemView.findViewById(R.id.tv_one_key);
            tv_next = itemView.findViewById(R.id.tv_next);
        }
    }

    /**
     * 组装公司信息
     *
     * @param resumeBean
     * @return
     */
    private String getCompanyInfo(FloatingListBean resumeBean) {
        if (companyBuilder.length() > 0) {
            companyBuilder.delete(0, companyBuilder.length());
        }
        if (TextUtils.isEmpty(resumeBean.getCompany())) {
            companyBuilder.append("公司信息暂无");
        } else {
            companyBuilder.append(resumeBean.getCompany().length() > 8 ? resumeBean.getCompany().substring(0, 8) + "..." : resumeBean.getCompany());
        }
        companyBuilder.append("/");
        if (TextUtils.isEmpty(resumeBean.getTitle())) {
            companyBuilder.append("职位信息暂无");
        } else {
            companyBuilder.append(resumeBean.getTitle().length() > 8 ? resumeBean.getTitle().substring(0, 8) + "..." : resumeBean.getTitle());
        }
        return companyBuilder.toString();
    }

    /**
     * 组装推荐信息
     *
     * @param resumeBean
     * @return
     */
    private String getRecommendInfo(FloatingListBean resumeBean) {
        if (recommendBuilder.length() > 0) {
            recommendBuilder.delete(0, recommendBuilder.length());
        }
        if (TextUtils.isEmpty(resumeBean.getRecommenderRealName())) {
            recommendBuilder.append("未知");
        } else {
            recommendBuilder.append(resumeBean.getRecommenderRealName());
        }
        recommendBuilder.append(" 推荐到");
        if (TextUtils.isEmpty(resumeBean.getCustomer())) {
            recommendBuilder.append("公司信息暂无");
        } else {
            recommendBuilder.append(resumeBean.getCustomer().length() > 8 ? resumeBean.getCustomer().substring(0, 8) + "..." : resumeBean.getCustomer());
        }
        recommendBuilder.append("-");
        if (TextUtils.isEmpty(resumeBean.getJob())) {
            recommendBuilder.append("职位信息暂无");
        } else {
            recommendBuilder.append(resumeBean.getJob().length() > 8 ? resumeBean.getJob().substring(0, 8) + "..." : resumeBean.getJob());
        }
        return recommendBuilder.toString();
    }

    private void setStatus(FloatingListBean bean, TextView textView) {
        switch (bean.getStatus()) {
            case 0:
                if (type.equals("2")) {
                    textView.setText("未处理");
                } else {
                    textView.setText("待反馈");
                }
                break;
            case 1:
                textView.setText("已接受");
                break;
            case 2:
                textView.setText("已拒绝");
                break;
            case 4:
                textView.setText("面试待安排");
                break;
            case 5:
                textView.setText("已安排");
                break;
            case 6:
                textView.setText("待评估");
                break;
            case 7:
                textView.setText("已反馈");
                break;
            case 8:
                textView.setText("offer");
                break;
            case 16:
                textView.setText("已入职");
                break;
            case 32:
                textView.setText("已离职");
                break;
            case 1024:
                textView.setText("已淘汰");
                break;
        }
    }

    private void dealStep(ViewHolder holder, FloatingListBean bean) {
        if (!bean.isIsPublisher() && !bean.isIsRecommender()) {
            holder.ll_action.setVisibility(View.GONE);
        } else {
            int status = bean.getStatus();
            if (type.equals("1") && (status == 0 || status == 2)) {
                holder.ll_action.setVisibility(View.GONE);
            } else {
                holder.ll_action.setVisibility(View.VISIBLE);
                holder.tv_refuse.setVisibility(View.VISIBLE);
                holder.tv_receive.setVisibility(View.VISIBLE);
                holder.tv_next.setVisibility(View.GONE);
                if (UserInfoManager.getInstance().getUserInfo().isCfUser == 1 && status != 0 && status != 2
                        && status != 7 && status != 8 && status != 16) {
                    holder.tv_onekey.setVisibility(View.VISIBLE);
                } else {
                    holder.tv_onekey.setVisibility(View.GONE);
                }
                if (status == 0) {
                    holder.tv_refuse.setText("拒绝");
                    holder.tv_receive.setText("接受");
                } else if (status == 1) {
                    holder.tv_refuse.setText("淘汰");
                    holder.tv_receive.setText("进入面试");
                } else if (status == 2) {
                    holder.tv_refuse.setVisibility(View.GONE);
                    holder.tv_receive.setText("重新接受");
                } else if (status == 4) {
                    holder.tv_refuse.setText("淘汰");
                    holder.tv_receive.setText("安排面试");
                } else if (status == 5) {
                    holder.tv_refuse.setText("反馈面试结果");
                    holder.tv_receive.setVisibility(View.GONE);
                } else if (status == 7) {
                    holder.tv_refuse.setText("淘汰");
                    holder.tv_receive.setText("Offer");
                    holder.tv_next.setVisibility(View.VISIBLE);
                    holder.tv_next.setText("安排下一轮");
                } else if (status == 8) {
                    holder.tv_refuse.setText("淘汰");
                    holder.tv_receive.setText("入职");
                } else if (status == 16) {
                    holder.tv_refuse.setText("离职");
                    holder.tv_receive.setVisibility(View.GONE);
                } else {
                    holder.ll_action.setVisibility(View.GONE);
                }
            }
        }
    }
}
