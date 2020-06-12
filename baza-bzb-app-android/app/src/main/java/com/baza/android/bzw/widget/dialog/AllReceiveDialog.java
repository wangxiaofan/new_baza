package com.baza.android.bzw.widget.dialog;

import android.content.Context;
import android.view.View;

import com.bznet.android.rcbox.R;

import baza.dialog.simpledialog.MaterialDialog;


/**
 * Created by LW on 2016/9/22.
 * Title : 添加或者修改备注的对话框
 * Note :
 */

public class AllReceiveDialog {
    private Context mContext;
    private MaterialDialog mMaterialDialog;
    private IAllReceiveListener mListener;
    private String msg;

    public interface IAllReceiveListener {
        void onReadyAllReceive();
    }

    public AllReceiveDialog(Context context, IAllReceiveListener listener, String msg) {
        this.mContext = context;
        this.mListener = listener;
        this.msg = msg;
        makeDialog();
    }

    private void makeDialog() {
        mMaterialDialog = new MaterialDialog(mContext);
        mMaterialDialog.setCancelable(true);
        mMaterialDialog.clearTitle().buildCancelButtonText(R.string.cancel).buildSureButtonText(R.string.sure).setAutoDismissEnable(false).buildClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
                if (mListener != null) {
                    mListener.onReadyAllReceive();
                }
            }
        });
        mMaterialDialog.setCanceledOnTouchOutside(true);
//        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_layout_add_friend, null);
//        editText_content = contentView.findViewById(R.id.et_content);
//        UserInfoBean userInfoBean = UserInfoManager.getInstance().getUserInfo();
//        mDefaultHelloWord = mResources.getString(R.string.add_friend_default_hint, (TextUtils.isEmpty(userInfoBean.nickName) ? userInfoBean.trueName : userInfoBean.nickName));
//        editText_content.setText(mDefaultHelloWord);
//        editText_content.setSelection(editText_content.getText().length());
//        mMaterialDialog.setMessageView(contentView);
        mMaterialDialog.buildMessage(msg);
        mMaterialDialog.show();
    }
}
