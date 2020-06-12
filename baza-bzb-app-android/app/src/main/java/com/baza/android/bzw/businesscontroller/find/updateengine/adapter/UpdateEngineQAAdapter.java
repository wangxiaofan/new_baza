package com.baza.android.bzw.businesscontroller.find.updateengine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baza.android.bzw.base.BaseBZWAdapter;
import com.bznet.android.rcbox.R;

/**
 * Created by Vincent.Lei on 2017/10/16.
 * Title：
 * Note：
 */

public class UpdateEngineQAAdapter extends BaseBZWAdapter {
    private String[] mQuestions;
    private String[] mAnswers;
    private Context mContext;

    public UpdateEngineQAAdapter(Context context, IAdapterEventsListener adapterEventsListener) {
        super(adapterEventsListener);
        this.mContext = context;
        mQuestions = mContext.getResources().getStringArray(R.array.update_engine_question);
        mAnswers = mContext.getResources().getStringArray(R.array.update_engine_answer);
    }

    @Override
    public int getCount() {
        return mQuestions.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_update_engine_qa_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else
            viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.textView_question.setText(mQuestions[position]);
        viewHolder.textView_answer.setText(mAnswers[position]);
        return convertView;
    }

    private static class ViewHolder {
        public ViewHolder(View convertView) {
            textView_question = convertView.findViewById(R.id.tv_question);
            textView_answer = convertView.findViewById(R.id.tv_answer);
        }

        TextView textView_question;
        TextView textView_answer;
    }
}
