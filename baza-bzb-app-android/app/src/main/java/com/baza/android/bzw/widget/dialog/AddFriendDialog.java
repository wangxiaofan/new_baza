package com.baza.android.bzw.widget.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.baza.android.bzw.bean.user.UserInfoBean;
import com.baza.android.bzw.manager.UserInfoManager;
import com.bznet.android.rcbox.R;

import baza.dialog.simpledialog.MaterialDialog;


/**
 * Created by LW on 2016/9/22.
 * Title : 添加或者修改备注的对话框
 * Note :
 */

public class AddFriendDialog {
    private Context mContext;
    private MaterialDialog mMaterialDialog;
    private EditText editText_content;
    private Resources mResources;
    private IAddFriendEditListener mListener;
    private String mDefaultHelloWord;

    public interface IAddFriendEditListener {
        void onReadyAddFriend(String hello);
    }

    public AddFriendDialog(Context context, IAddFriendEditListener listener) {
        this.mContext = context;
        this.mListener = listener;
        this.mResources = context.getResources();
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
                    String currentHelloWord = editText_content.getText().toString().trim();
                    mListener.onReadyAddFriend((TextUtils.isEmpty(currentHelloWord) ? mDefaultHelloWord : currentHelloWord));
                }
            }
        });
        mMaterialDialog.setCanceledOnTouchOutside(true);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_layout_add_friend, null);
        editText_content = contentView.findViewById(R.id.et_content);
        UserInfoBean userInfoBean = UserInfoManager.getInstance().getUserInfo();
        mDefaultHelloWord = mResources.getString(R.string.add_friend_default_hint, (TextUtils.isEmpty(userInfoBean.nickName) ? userInfoBean.trueName : userInfoBean.nickName));
        editText_content.setText(mDefaultHelloWord);
        editText_content.setSelection(editText_content.getText().length());
        mMaterialDialog.setMessageView(contentView);
        mMaterialDialog.show();
    }
}
