package com.baza.android.bzw.businesscontroller.friend;

import android.content.res.Resources;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baza.android.bzw.bean.friend.FriendInfoResultBean;
import com.baza.android.bzw.businesscontroller.friend.presenter.FriendHomePresenter;
import com.baza.android.bzw.businesscontroller.friend.viewinterface.IFriendHomeView;
import com.baza.android.bzw.businesscontroller.im.IMConst;
import com.baza.android.bzw.businesscontroller.message.ChatActivity;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.manager.AddressManager;
import com.baza.android.bzw.manager.FriendlyShowInfoManager;
import com.baza.android.bzw.manager.UserInfoManager;
import com.baza.android.bzw.widget.dialog.AddFriendDialog;
import com.slib.utils.LoadImageUtil;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Vincent.Lei on 2017/9/26.
 * Title：
 * Note：
 */

public class HeadViewFriendHome implements View.OnClickListener {
    private IFriendHomeView mFriendHomeView;
    private FriendHomePresenter mPresenter;
    private View rootView;
    @BindView(R.id.tv_name)
    TextView textView_name;
    @BindView(R.id.tv_company)
    TextView textView_company;
    @BindView(R.id.tv_talent_count)
    TextView textView_talentCount;
    @BindView(R.id.tv_location)
    TextView textView_location;
    @BindView(R.id.tv_title)
    TextView textView_title;
    @BindView(R.id.tv_level)
    TextView textView_level;
    @BindView(R.id.tv_pick_up)
    TextView textView_pickUp;
    @BindView(R.id.btn_menu1)
    Button button_menu1;
    @BindView(R.id.btn_menu2)
    Button button_menu2;
    @BindView(R.id.civ_avatar)
    ImageView imageView_avatar;
    @BindView(R.id.tv_avatar)
    TextView textView_avatar;

    public HeadViewFriendHome(IFriendHomeView mFriendHomeView, FriendHomePresenter mPresenter) {
        this.mFriendHomeView = mFriendHomeView;
        this.mPresenter = mPresenter;
        init();
    }

    private void init() {
        rootView = mFriendHomeView.callGetBindActivity().getLayoutInflater().inflate(R.layout.head_view_friend_main_page, null);
        ButterKnife.bind(this, rootView);
        textView_avatar.setTextSize(TypedValue.COMPLEX_UNIT_SP, ScreenUtil.px2dip(mFriendHomeView.callGetResources().getDimension(R.dimen.text_size_17)));
        textView_pickUp.setOnClickListener(this);
        button_menu1.setOnClickListener(this);
        button_menu2.setOnClickListener(this);
    }

    public View getHeadView() {
        return rootView;
    }

    public void updateFriendInfoView() {
        FriendInfoResultBean.FriendInfoBean friendInfoBean = mPresenter.getFriendInfoData();
        if (friendInfoBean == null)
            return;
        if (friendInfoBean.unionId != null && friendInfoBean.unionId.equals(UserInfoManager.getInstance().getUserInfo().unionId)) {
            //自己
            rootView.findViewById(R.id.ll_menu).setVisibility(View.GONE);
        }
        String name = (!TextUtils.isEmpty(friendInfoBean.nickName) ? friendInfoBean.nickName : (TextUtils.isEmpty(friendInfoBean.trueName) ? CommonConst.STR_DEFAULT_USER_NAME_SX : friendInfoBean.trueName));
        textView_name.setText(name);
        if (!TextUtils.isEmpty(friendInfoBean.avatar) || CommonConst.STR_DEFAULT_USER_NAME_SX.equals(name)) {
            textView_avatar.setVisibility(View.GONE);
            LoadImageUtil.loadImage(friendInfoBean.avatar, R.drawable.avatar_def, imageView_avatar);
            imageView_avatar.setVisibility(View.VISIBLE);
        } else {
            imageView_avatar.setVisibility(View.GONE);
            textView_avatar.setText(name.substring(0, 1));
            textView_avatar.setVisibility(View.VISIBLE);
        }
        Resources resources = mFriendHomeView.callGetResources();
        int color = resources.getColor(R.color.text_color_gray_a9adb3);
        SpannableString spannableString = new SpannableString(resources.getString(R.string.friend_info_company_title, (TextUtils.isEmpty(friendInfoBean.company) ? resources.getString(R.string.message_unknown) : friendInfoBean.company)));
        spannableString.setSpan(new ForegroundColorSpan(color), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_company.setText(spannableString);
        spannableString = new SpannableString(resources.getString(R.string.friend_info_talent_count_title, FriendlyShowInfoManager.getInstance().getFriendResumeCountValue(friendInfoBean.candidateCount, true, false)));
        spannableString.setSpan(new ForegroundColorSpan(color), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_talentCount.setText(spannableString);
        spannableString = new SpannableString(resources.getString(R.string.friend_info_location_title, AddressManager.getInstance().getCityNameByCode(friendInfoBean.location)));
        spannableString.setSpan(new ForegroundColorSpan(color), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_location.setText(spannableString);
        spannableString = new SpannableString(resources.getString(R.string.friend_info_title_title, (TextUtils.isEmpty(friendInfoBean.title) ? resources.getString(R.string.message_unknown) : friendInfoBean.title)));
        spannableString.setSpan(new ForegroundColorSpan(color), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_title.setText(spannableString);
        spannableString = new SpannableString(resources.getString(R.string.friend_info_level_title, String.valueOf(friendInfoBean.grade)));
        spannableString.setSpan(new ForegroundColorSpan(color), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_level.setText(spannableString);
        boolean isFriend = (friendInfoBean.isFriend == FriendInfoResultBean.FriendInfoBean.FRIEND_YES);
        button_menu1.setText(isFriend ? R.string.has_add : R.string.add_friend);
        button_menu1.setBackgroundResource((isFriend ? R.drawable.selector_background_sms_code_unenable : R.drawable.login_login_btn_bg));
        button_menu1.setTextColor(mFriendHomeView.callGetResources().getColor(isFriend ? R.color.text_color_grey_9E9E9E : android.R.color.white));
    }

    private void pickUpChanged() {
        boolean isCurrentPickUp = (textView_level.getVisibility() == View.GONE);
        textView_location.setVisibility((isCurrentPickUp ? View.VISIBLE : View.GONE));
        textView_title.setVisibility((isCurrentPickUp ? View.VISIBLE : View.GONE));
        textView_level.setVisibility((isCurrentPickUp ? View.VISIBLE : View.GONE));
        textView_pickUp.setText((isCurrentPickUp ? R.string.pick_up : R.string.show_more));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_pick_up:
                pickUpChanged();
                break;
            case R.id.btn_menu2:
                FriendInfoResultBean.FriendInfoBean friendInfoBean = mPresenter.getFriendInfoData();
                if (friendInfoBean == null || TextUtils.isEmpty(friendInfoBean.neteaseId))
                    return;
                ChatActivity.launch(mFriendHomeView.callGetBindActivity(), new ChatActivity.ChatParam(friendInfoBean.neteaseId, IMConst.SESSION_TYPE_P2P));
                break;
            case R.id.btn_menu1:
                friendInfoBean = mPresenter.getFriendInfoData();
                if (friendInfoBean == null || friendInfoBean.isFriend == FriendInfoResultBean.FriendInfoBean.FRIEND_YES)
                    return;
                //加好友
                new AddFriendDialog(mFriendHomeView.callGetBindActivity(), new AddFriendDialog.IAddFriendEditListener() {
                    @Override
                    public void onReadyAddFriend(String hello) {
                        mPresenter.addFriend(hello);
                    }
                });
                break;
        }
    }
}
