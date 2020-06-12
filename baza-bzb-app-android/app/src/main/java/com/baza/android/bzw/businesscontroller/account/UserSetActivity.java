package com.baza.android.bzw.businesscontroller.account;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.bean.common.LocalAreaBean;
import com.baza.android.bzw.bean.user.UserInfoBean;
import com.baza.android.bzw.bean.user.VersionBean;
import com.baza.android.bzw.businesscontroller.account.presenter.UserSetPresenter;
import com.baza.android.bzw.businesscontroller.account.viewinterface.IUserSetView;
import com.baza.android.bzw.businesscontroller.publish.CitySelectedActivity;
import com.baza.android.bzw.businesscontroller.publish.CropAvatarActivity;
import com.baza.android.bzw.businesscontroller.publish.EditValueActivity;
import com.baza.android.bzw.businesscontroller.publish.PickPhotosActivity;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.constant.RequestCodeConst;
import com.baza.android.bzw.manager.AppGlobalManager;
import com.baza.android.bzw.manager.UserInfoManager;
import com.baza.android.bzw.widget.ClipImageView;
import com.slib.utils.DialogUtil;
import com.slib.utils.LoadImageUtil;
import com.bznet.android.rcbox.BuildConfig;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;

/**
 * Created by Vincent.Lei on 2017/8/2.
 * Title：编辑用户信息
 * Note：
 */

public class UserSetActivity extends BaseActivity implements IUserSetView, View.OnClickListener {
    TextView textView_avatar;
    ImageView imageView_avatar;
    LinearLayout linearLayout_item_container;
    View view_signOut;
    private UserSetPresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.account_activity_set;
    }

    @Override
    protected String getPageTitle() {
        return mResources.getString(R.string.page_user_set);
    }

    @Override
    protected void initWhenCallOnCreate() {
        setupViews();
        mPresenter = new UserSetPresenter(this);
        mPresenter.initialize();
    }

    @Override
    protected void changedSelfDefineUIToFitSDKReachKITKAT(int statusBarHeight) {

    }


    private void setupViews() {
        TextView textView_title = findViewById(R.id.tv_title);
        textView_title.setText(R.string.title_set);
        linearLayout_item_container = findViewById(R.id.ll_item_container);
        view_signOut = findViewById(R.id.tv_sign_out);
        textView_avatar = findViewById(R.id.tv_avatar);
        imageView_avatar = findViewById(R.id.civ_avatar);
        linearLayout_item_container.removeView(view_signOut);
//        int[] itemIds = {R.id.user_set_item_nickname, R.id.user_set_item_city, R.id.user_set_item_cellphone, R.id.user_set_item_email, R.id.user_set_item_identify, R.id.user_set_item_resume_mode, R.id.user_set_item_version};
        int[] itemIds = {R.id.user_set_item_nickname, R.id.user_set_item_city, R.id.user_set_item_cellphone, R.id.user_set_item_email, R.id.user_set_item_identify, R.id.user_set_item_version};
//        int[] itemTitleIds = {R.string.user_set_title_nickname, R.string.user_set_title_city, R.string.user_set_title_cellphone, R.string.user_set_title_email, R.string.user_set_title_identify, R.string.user_set_title_resume_mode, R.string.user_set_title_version};
        int[] itemTitleIds = {R.string.user_set_title_nickname, R.string.user_set_title_city, R.string.user_set_title_cellphone, R.string.user_set_title_email, R.string.user_set_title_identify, R.string.user_set_title_version};
        ViewGroup viewItem;
        TextView textView_item_title;
        LayoutInflater layoutInflater = getLayoutInflater();
        LinearLayout.MarginLayoutParams mlp;
        for (int i = 0; i < itemIds.length; i++) {
            viewItem = (ViewGroup) layoutInflater.inflate(R.layout.account_set_item, null);
            viewItem.setId(itemIds[i]);
            textView_item_title = (TextView) viewItem.getChildAt(0);
            textView_item_title.setText(itemTitleIds[i]);
            mlp = new LinearLayout.LayoutParams(LinearLayout.MarginLayoutParams.MATCH_PARENT, LinearLayout.MarginLayoutParams.WRAP_CONTENT);
            ((LinearLayout.LayoutParams) mlp).gravity = Gravity.CENTER;
            linearLayout_item_container.addView(viewItem, mlp);
            viewItem.setOnClickListener(this);
        }
        linearLayout_item_container.addView(view_signOut);

        callUpdateVersion(null);
        callUpdateCellPhone();
        callUpdateShareResumeMode();
        updateIdentifyStatusView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left_click:
                finish();
                break;
            case R.id.cl_avatar:
                PickPhotosActivity.launch(this, RequestCodeConst.INT_REQUEST_PICK_PHOTO, 1, null);
                break;
            case R.id.user_set_item_nickname:
                //昵称4-20字符
                EditValueActivity.launch(this, RequestCodeConst.INT_REQUEST_EDIT_NICK_NAME, EditValueActivity.MSG_TYPE_NICKNAME, 1, 20, 4, EditValueActivity.INPUT_TYPE_TEXT, UserInfoManager.getInstance().getUserInfo().nickName);
                break;
            case R.id.user_set_item_email:
                mPresenter.checkEmail(true, true);
                break;
            case R.id.user_set_item_city:
                CitySelectedActivity.launch(this, mPresenter.getCityCode(), RequestCodeConst.INT_REQUEST_CITY_SELECT);
                break;
//            case R.id.user_set_item_resume_mode:
//                SetResumeModeActivity.launch(this, RequestCodeConst.INT_REQUEST_SET_RESUME_MODE);
//                break;
            case R.id.user_set_item_cellphone:
                ChangeMobileActivity.launch(this, RequestCodeConst.INT_REQUEST_BIND_MOBILE);
                break;
            case R.id.user_set_item_version:
                mPresenter.checkNewVersion(false);
                break;
            case R.id.tv_sign_out:
                DialogUtil.doubleButtonShow(this, 0, R.string.logout_confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppGlobalManager.getInstance().logOut(UserSetActivity.this);
                    }
                }, null);
                break;
            case R.id.user_set_item_identify:
                if (UserInfoManager.getInstance().checkIdentifyStatusAndVerifyIfNeed(this, true))
                    callShowToastMessage(null, R.string.has_complete_verify);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RequestCodeConst.INT_REQUEST_PICK_PHOTO:
                ArrayList<String> urls = PickPhotosActivity.parseSelectedPhotos(Activity.RESULT_OK, data);
                if (urls != null && urls.size() > 0) {
                    CropAvatarActivity.launch(this, urls.get(0), ClipImageView.TYPE_SQUARE, RequestCodeConst.INT_REQUEST_CROP_AVATAR);
                }
                break;
            case RequestCodeConst.INT_REQUEST_CROP_AVATAR:
                String avatar = CropAvatarActivity.parseCropResult(resultCode, data);
                if (avatar == null)
                    return;
                mPresenter.updateAvatar(avatar);
                break;
            case RequestCodeConst.INT_REQUEST_EDIT_NICK_NAME:
                if (resultCode == RESULT_OK && data != null) {
                    String value = EditValueActivity.parseResultValue(resultCode, data);
                    mPresenter.updateUserNickName(value);
                }
                break;
            case RequestCodeConst.INT_REQUEST_CITY_SELECT:
                if (resultCode == RESULT_OK && data != null) {
                    LocalAreaBean cityBean = (LocalAreaBean) data.getSerializableExtra("city");
                    if (cityBean != null)
                        mPresenter.updateUserCity(cityBean.code);
                }
                break;
            case RequestCodeConst.INT_REQUEST_BIND_MOBILE:
                callUpdateCellPhone();
                break;
            case RequestCodeConst.INT_REQUEST_EDIT_EMAIL:
                if (resultCode == RESULT_OK && data != null) {
                    String value = EditValueActivity.parseResultValue(resultCode, data);
                    mPresenter.bindEmail(value);
                }
                break;
            case RequestCodeConst.INT_REQUEST_SET_RESUME_MODE:
                callUpdateShareResumeMode();
                break;
        }
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, UserSetActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void updateIdentifyStatusView() {
        TextView textView = linearLayout_item_container.findViewById(R.id.user_set_item_identify).findViewById(R.id.tv_content);
        textView.setTextColor(mResources.getColor(R.color.text_color_blue_53ABD5));
        textView.setText((UserInfoManager.getInstance().isIdentifyStatusOk() ? R.string.user_verify_status_ok : (UserInfoManager.getInstance().getUserInfo().channelVerifyStatus == CommonConst.VerifyStatus.VERIFY_ING ? R.string.verify_status_on_title : R.string.user_verify_status_none)));
    }

    @Override
    public void callUpdateAvatar(UserInfoBean userInfoBean) {
        String name = (TextUtils.isEmpty(userInfoBean.nickName) ? userInfoBean.trueName : userInfoBean.nickName);
        if (TextUtils.isEmpty(userInfoBean.avatar)) {
            imageView_avatar.setVisibility(View.GONE);
            textView_avatar.setText((TextUtils.isEmpty(name) ? CommonConst.STR_DEFAULT_USER_NAME_SX : name.substring(0, 1)));
            textView_avatar.setVisibility(View.VISIBLE);
        } else {
            textView_avatar.setVisibility(View.INVISIBLE);
            LoadImageUtil.loadImage(userInfoBean.avatar, R.drawable.avatar_def, imageView_avatar);
            imageView_avatar.setVisibility(View.VISIBLE);
        }
    }

//    @Override
//    public void callUpdateRealName(String realName) {
//        textView_editRealName.setText(realName);
//    }

    @Override
    public void callUpdateNickName(String nickName) {
        ((TextView) linearLayout_item_container.findViewById(R.id.user_set_item_nickname).findViewById(R.id.tv_content)).setText(nickName);
    }

    @Override
    public void callUpdateEmail(String email, boolean valid) {
        TextView textView_editEmail = linearLayout_item_container.findViewById(R.id.user_set_item_email).findViewById(R.id.tv_content);
        if (TextUtils.isEmpty(email)) {
            textView_editEmail.setText(null);
            return;
        }
        if (valid)
            textView_editEmail.setText(email);
        else
            textView_editEmail.setText(mResources.getString(R.string.email_not_valid_with_value, email));
    }

//    @Override
//    public void callUpdateCompany(String company) {
//        textView_editCompany.setText(company);
//    }

//    @Override
//    public void callUpdateJob(String job) {
//        textView_editJob.setText(job);
//    }

    @Override
    public void callUpdateCity(String cityName) {
        ((TextView) linearLayout_item_container.findViewById(R.id.user_set_item_city).findViewById(R.id.tv_content)).setText(cityName);
    }

    @Override
    public void callSetNewEmail(boolean valid) {
        if (valid) {
            //邮箱已验证 需要先解绑
            DialogUtil.doubleButtonShow(this, 0, R.string.email_has_valid_is_unbind, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.unBindEmail();
                }
            }, null);
            return;
        }
        EditValueActivity.launch(this, RequestCodeConst.INT_REQUEST_EDIT_EMAIL, EditValueActivity.MSG_TYPE_EMAIL, 1, 50, 0, EditValueActivity.INPUT_TYPE_TEXT, mPresenter.getEmail());
    }

    @Override
    public void callUpdateVersion(VersionBean versionBean) {
        TextView textView = linearLayout_item_container.findViewById(R.id.user_set_item_version).findViewById(R.id.tv_content);
        if (versionBean == null)
            textView.setText("v" + BuildConfig.VERSION_NAME);
        else {
            SpannableString spannableString = new SpannableString(mResources.getString(R.string.user_set_new_version_hint, versionBean.versionNo));
            spannableString.setSpan(new ForegroundColorSpan(Color.RED), 4, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(spannableString);
        }
    }

    @Override
    public void callUpdateCellPhone() {
        String mobile = UserInfoManager.getInstance().getUserInfo().mobile;
        TextView textView = linearLayout_item_container.findViewById(R.id.user_set_item_cellphone).findViewById(R.id.tv_content);
        textView.setText((mobile != null && mobile.length() == 11 ? (mobile.substring(0, 3) + "****" + mobile.substring(7)) : mobile));
    }

    @Override
    public void callUpdateShareResumeMode() {
//        TextView textView = linearLayout_item_container.findViewById(R.id.user_set_item_resume_mode).findViewById(R.id.tv_content);
//        textView.setText((UserInfoManager.getInstance().getUserExtraInfo().resumeSetStatus == CommonConst.RESUME_SET_STATUS_NONE ? R.string.user_set_text_resume_none_mode : R.string.user_set_text_resume_share_mode));
    }

    @Override
    public void callShowNewVersionDialog(VersionBean versionBean) {
        BZWApplication.getApplication().showNewVersionDialog(this, versionBean);
    }

    @Override
    protected void onActivityDeadForApp() {
        mPresenter.onDestroy();
        super.onActivityDeadForApp();
    }
}
