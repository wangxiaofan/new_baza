package com.baza.android.bzw.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.IdRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;


/**
 * Created by LW on 2016/9/22.
 * Title : 更新反馈的对话框
 * Note :
 */

public class UpdateByEngineFeedBackDialog extends Dialog implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private static final int COMMENT_TYPE_USEFUL = 1;
    private static final int COMMENT_TYPE_BUG = 2;
    private static final int COMMENT_TYPE_UN_LIKE = 3;
    private static final int COMMENT_TYPE_NO_FEEL = 4;

    public interface IUpdateByEngineFeedBackListener {
        void onFeedBack(int commentType, String msg);

        void onNotHint();

        void onNormalClosed();
    }

    private IUpdateByEngineFeedBackListener mListener;
    private EditText editText_content;
    private int mCommentType = COMMENT_TYPE_USEFUL;

    public UpdateByEngineFeedBackDialog(Context context, IUpdateByEngineFeedBackListener listener) {
        super(context, R.style.customerDialog);
        this.mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_layout_update_engine_feed_back);
        editText_content = findViewById(R.id.et_content);
        findViewById(R.id.iv_close).setOnClickListener(this);
        findViewById(R.id.btn_left_click_material).setOnClickListener(this);
        findViewById(R.id.btn_right_click_material).setOnClickListener(this);
        RadioGroup radioGroup = findViewById(R.id.rg);
        radioGroup.setOnCheckedChangeListener(this);
        View mainView = findViewById(R.id.main_view);
        ViewGroup.LayoutParams lp = mainView.getLayoutParams();
        lp.width = (int) (ScreenUtil.screenWidth * 0.8f);
        mainView.setLayoutParams(lp);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.rb_a:
                mCommentType = COMMENT_TYPE_USEFUL;
                break;
            case R.id.rb_b:
                mCommentType = COMMENT_TYPE_BUG;
                break;
            case R.id.rb_c:
                mCommentType = COMMENT_TYPE_UN_LIKE;
                break;
            case R.id.rb_d:
                mCommentType = COMMENT_TYPE_NO_FEEL;
                break;
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.btn_left_click_material:
                if (mListener != null)
                    mListener.onNotHint();
                break;
            case R.id.btn_right_click_material:
                if (mListener != null)
                    mListener.onFeedBack(mCommentType, editText_content.getText().toString().trim());
                break;
            case R.id.iv_close:
                if (mListener != null) mListener.onNormalClosed();
                break;
        }
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {
            //ignore
        }
    }
}
