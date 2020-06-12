package com.baza.android.bzw.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.businesscontroller.audio.voicemanager.ITextDecodeListener;
import com.baza.android.bzw.businesscontroller.audio.voicemanager.VoiceDecode;
import com.slib.progress.IndeterminateProgressDrawable;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

import java.io.File;


/**
 * Created by Vincent.Lei on 2017/2/16.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class VoiceToTextDialog extends Dialog implements View.OnClickListener {


    public interface IChangeResultListener {
        void onTextResult(String result);
    }

    private TextView textView_error;
    private EditText editText_result;
    private LinearLayout linearLayout_onLoading;
    private Button button_cancel, button_save;

    private File mSourceFile;
    private IChangeResultListener mListener;
    private boolean mCanceled;
    private String mAlreadyDecodeText;

    public VoiceToTextDialog(Context context, String mAlreadyDecodeText, File mSourceFile, IChangeResultListener mListener) {
        this(context, 0);
        this.mSourceFile = mSourceFile;
        this.mListener = mListener;
        this.mAlreadyDecodeText = mAlreadyDecodeText;
    }

    public VoiceToTextDialog(Context context, int themeResId) {
        super(context, R.style.customerDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_voice_to_text);
        setCancelable(false);

        View view_main = findViewById(R.id.fl_main_view);
        ViewGroup.LayoutParams lp = view_main.getLayoutParams();
        lp.width = ScreenUtil.screenWidth;
        lp.height = ScreenUtil.screenHeight;
        view_main.setLayoutParams(lp);

        editText_result = findViewById(R.id.et_result);
        textView_error = findViewById(R.id.tv_error);
        linearLayout_onLoading = findViewById(R.id.ll_on_changing);
        button_cancel = findViewById(R.id.btn_cancel);
        button_save = findViewById(R.id.btn_save);
        button_cancel.setOnClickListener(this);
        button_save.setOnClickListener(this);

        if (!TextUtils.isEmpty(mAlreadyDecodeText) || mSourceFile == null) {
            //已经转为文字了
            linearLayout_onLoading.setVisibility(View.GONE);
            button_save.setVisibility(View.VISIBLE);
            editText_result.setText(mAlreadyDecodeText);
            editText_result.setVisibility(View.VISIBLE);
            try {
                editText_result.setSelection(mAlreadyDecodeText.length());
            } catch (Exception e) {
                //ignore
            }
            return;
        }

        ProgressBar mProgressBar = findViewById(R.id.mpb);
        IndeterminateProgressDrawable indeterminateProgressDrawable = new IndeterminateProgressDrawable(getContext());
        indeterminateProgressDrawable.setTint(getContext().getResources().getColor(R.color.text_color_black_666666));
        mProgressBar.setIndeterminateDrawable(indeterminateProgressDrawable);


        button_save.postDelayed(new Runnable() {
            @Override
            public void run() {
                doTextDecode();
            }
        }, 800);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                mCanceled = true;
                dismiss();
                break;
            case R.id.btn_save:
                dismiss();
                if (mListener != null)
                    mListener.onTextResult(editText_result.getText().toString());
                break;
        }
    }


    private void doTextDecode() {
        VoiceDecode.decodeToText(BZWApplication.getApplication(), mSourceFile, new ITextDecodeListener() {
            @Override
            public void onDecodeToTextResult(int errorCode, String result) {
                if (mCanceled)
                    return;
                linearLayout_onLoading.setVisibility(View.GONE);
                if (errorCode == ITextDecodeListener.ERROR_CODE_NONE) {
                    button_save.setVisibility(View.VISIBLE);
                    editText_result.setText(result);
                    editText_result.setVisibility(View.VISIBLE);
                    return;
                }
                textView_error.setVisibility(View.VISIBLE);
            }
        });
    }
}
