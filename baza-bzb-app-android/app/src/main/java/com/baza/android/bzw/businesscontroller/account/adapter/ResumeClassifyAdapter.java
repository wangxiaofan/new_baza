package com.baza.android.bzw.businesscontroller.account.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.baza.android.bzw.bean.resume.ResumeClassifyResultBean;
import com.baza.android.bzw.businesscontroller.account.viewinterface.IResumeClassifyView;
import com.baza.android.bzw.constant.AdapterEventIdConst;
import com.baza.android.bzw.manager.FriendlyShowInfoManager;
import com.slib.utils.DateUtil;
import com.bznet.android.rcbox.R;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Vincent.Lei on 2018/8/24.
 * Title：
 * Note：
 */
public class ResumeClassifyAdapter extends BaseBZWAdapter implements View.OnClickListener {
    private Context mContext;
    private Resources mResources;
    private int mColorHighLight;
    private int mColorHistory;
    private int mColorNormal;
    private int mType;
    private List<ResumeClassifyResultBean.ResumeClassifyBean> mList;

    public ResumeClassifyAdapter(Context context, int type, List<ResumeClassifyResultBean.ResumeClassifyBean> list, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mContext = context;
        this.mResources = context.getResources();
        this.mColorNormal = mResources.getColor(R.color.text_color_blue_0D315C);
        this.mColorHighLight = mResources.getColor(R.color.text_color_blue_53ABD5);
        this.mColorHistory = mResources.getColor(R.color.text_color_black_4E5968);
        this.mList = list;
        this.mType = type;
    }

    @Override
    public int getCount() {
        return (mList == null ? 0 : mList.size());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.account_adapter_resume_classify_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
            convertView.setOnClickListener(this);
        } else
            viewHolder = (ViewHolder) convertView.getTag();
        ResumeClassifyResultBean.ResumeClassifyBean resumeClassifyBean = mList.get(position);
        if (resumeClassifyBean.sourceType == IResumeClassifyView.HISTORY) {
            viewHolder.textView_title.setText(R.string.resume_classify_type_history);
            viewHolder.textView_title.setTextColor(mColorHistory);

        } else if (resumeClassifyBean.sourceType == IResumeClassifyView.DELETE) {
            viewHolder.textView_title.setTextColor(mColorNormal);
            viewHolder.textView_title.setText(resumeClassifyBean.insertOkCount > 1 ? R.string.batch_delete_resume : R.string.single_delete_resume);
        } else {
            viewHolder.textView_title.setTextColor(mColorNormal);
//            SpannableString spannableString = new SpannableString(mResources.getString(R.string.resume_classify_item_title, resumeClassifyBean.sourceType == IResumeClassifyView.LIST_RECEIVE ? mResources.getString(R.string.source_local_other) : (resumeClassifyBean.sourceType == IResumeClassifyView.LIST_MATCH ? mResources.getString(R.string.list_match) : resumeClassifyBean.sourcePath)));
            SpannableString spannableString = new SpannableString(mResources.getString(R.string.resume_classify_item_title, resumeClassifyBean.sourceType == IResumeClassifyView.LIST_RECEIVE ? mResources.getString(R.string.source_local_other) : resumeClassifyBean.sourcePath));
            spannableString.setSpan(new ForegroundColorSpan(mColorHighLight), 2, spannableString.length() - 4, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.textView_title.setText(spannableString);
        }
        viewHolder.textView_time.setText(DateUtil.longMillions2FormatDate(resumeClassifyBean.created, DateUtil.SDF_YMD_HM));
        viewHolder.textView_count.setText(mResources.getString(R.string.email_resume_count, FriendlyShowInfoManager.getInstance().getFriendResumeCountValue(resumeClassifyBean.insertOkCount, false, true)));

        viewHolder.textView_subInfo.setTag(null);
        JSONObject jsonObjectRemark = resumeClassifyBean.getJsonObjectRemark();
        if (jsonObjectRemark != null) {
            try {
                switch (resumeClassifyBean.sourceType) {
                    case IResumeClassifyView.OTHER_SHARE:
                        viewHolder.textView_subInfo.setText(mResources.getString(jsonObjectRemark.getInt("shareType") == 1 ? R.string.resume_classify_sub_info_initiative_share : R.string.resume_classify_sub_info_request_share, jsonObjectRemark.getString("sharer")));
                        viewHolder.textView_subInfo.setVisibility(View.VISIBLE);
                        viewHolder.textView_subInfo.setTag(position);
                        break;
                    case IResumeClassifyView.LIST_RECEIVE:
                        viewHolder.textView_subInfo.setText(mResources.getString(R.string.resume_classify_sub_info_list_receiver, jsonObjectRemark.getString("candidateName")));
                        viewHolder.textView_subInfo.setTag(position);
                        viewHolder.textView_subInfo.setVisibility(View.VISIBLE);
                        break;
//                    case IResumeClassifyView.LIST_MATCH:
//                        viewHolder.textView_subInfo.setText(jsonObjectRemark.getString("nameListFileName"));
//                        viewHolder.textView_subInfo.setVisibility(View.VISIBLE);
//                        break;
                    case IResumeClassifyView.IMPORT_FROM_OTHER_PLATFORM:
                        if (jsonObjectRemark.has("mailAccount")) {
                            viewHolder.textView_subInfo.setText(jsonObjectRemark.getString("mailAccount"));
                            viewHolder.textView_subInfo.setVisibility(View.VISIBLE);
                        } else
                            viewHolder.textView_subInfo.setVisibility(View.GONE);
                        break;
                    default:
                        viewHolder.textView_subInfo.setVisibility(View.GONE);
                        break;
                }

            } catch (Exception e) {
                //ignore
            }

        } else
            viewHolder.textView_subInfo.setVisibility(View.GONE);


        return convertView;
    }

    @Override
    public void onClick(View v) {
        ViewHolder viewHolder = (ViewHolder) v.getTag();
        if (viewHolder.textView_subInfo.getTag() == null)
            return;
        int position = (int) viewHolder.textView_subInfo.getTag();
        try {
            sendAdapterEventToHost(AdapterEventIdConst.RESUME_ADAPTER_EVENT_SEE_DETAIL, position, null, mList.get(position).getJsonObjectRemark().getString("resumeId"));
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private static class ViewHolder {
        TextView textView_title;
        TextView textView_time;
        TextView textView_count;
        TextView textView_subInfo;

        public ViewHolder(View convertView) {
            textView_title = convertView.findViewById(R.id.tv_title);
            textView_time = convertView.findViewById(R.id.tv_time);
            textView_count = convertView.findViewById(R.id.tv_count);
            textView_subInfo = convertView.findViewById(R.id.tv_sub_info);
        }
    }
}
