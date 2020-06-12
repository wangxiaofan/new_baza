package com.baza.android.bzw.businesscontroller.home;

import android.content.Context;
import androidx.appcompat.widget.ListPopupWindow;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.baza.android.bzw.bean.common.BannerResultBean;
import com.baza.android.bzw.bean.friend.FriendListResultBean;
import com.baza.android.bzw.businesscontroller.account.ImportPlatformListActivity;
import com.baza.android.bzw.businesscontroller.browser.RemoteBrowserActivity;
import com.baza.android.bzw.businesscontroller.friend.NearlyPersonActivity;
import com.baza.android.bzw.businesscontroller.home.presenter.TabHomePresenter;
import com.baza.android.bzw.businesscontroller.home.viewinterface.ITabHomeView;
import com.baza.android.bzw.businesscontroller.publish.util.QRCodeUtil;
import com.baza.android.bzw.businesscontroller.resume.smartgroup.SmartGroupIndexActivity;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.widget.circlevp.CirclePagerAdapter;
import com.baza.android.bzw.widget.circlevp.CircleViewPager;
import com.bznet.android.rcbox.R;
import com.slib.utils.LoadImageUtil;
import com.slib.utils.ScreenUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2018/2/26.
 * Title：
 * Note：
 */

public class TabHomeHeadView implements View.OnClickListener {
    private ITabHomeView mTabHomeView;
    private TabHomePresenter mPresenter;
    private View view_root;
    @BindView(R.id.vp_banner)
    CircleViewPager viewPager_banner;
    //    @BindView(R.id.tv_list_receive_entrance)
//    TextView textView_listReceive;
    @BindView(R.id.tv_smart_group)
    TextView textView_smartGroup;
    //    @BindView(R.id.tv_list_match_entrance)
//    TextView textView_listMatch;
//    @BindView(R.id.tv_hint_verify)
//    View view_hintVerify;
//    @BindView(R.id.tv_change_path)
//    TextView textView_changeBatch;
    @BindView(R.id.fl_avatar_container)
    FrameLayout frameLayout_suggestFriendAvatarContainer;
    private BannerAdapter mBannerAdapter;
    private boolean mHidden;
    private ListPopupWindow mAddMenuPopupWindow;
//    private Drawable mDrawable;

    TabHomeHeadView(ITabHomeView mTabHomeView, TabHomePresenter mPresenter) {
        this.mTabHomeView = mTabHomeView;
        this.mPresenter = mPresenter;
        init();
    }

    View getRootView() {
        return view_root;
    }

    private void init() {
        view_root = mTabHomeView.callGetBindActivity().getLayoutInflater().inflate(R.layout.head_view_tab_home, null);
        ButterKnife.bind(this, view_root);
//        view_root.findViewById(R.id.tv_change_path).setOnClickListener(this);
        view_root.findViewById(R.id.view_friend_nearly).setOnClickListener(this);
//        textView_listReceive.setOnClickListener(this);
        view_root.findViewById(R.id.tv_smart_group).setOnClickListener(this);
//        view_root.findViewById(R.id.tv_list_match_entrance).setOnClickListener(this);
//        if (!UserInfoManager.getInstance().hasCFTag()) {
//            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) textView_listReceive.getLayoutParams();
//            layoutParams.width = 1;
//            textView_listReceive.setLayoutParams(layoutParams);
//            textView_listReceive.setVisibility(View.GONE);
//
//            layoutParams = (ConstraintLayout.LayoutParams) textView_smartGroup.getLayoutParams();
//            layoutParams.rightToRight = textView_listReceive.getId();
//            layoutParams.leftToLeft = -1;
//            textView_smartGroup.setLayoutParams(layoutParams);
//
//            layoutParams = (ConstraintLayout.LayoutParams) textView_listMatch.getLayoutParams();
//            layoutParams.rightToRight = -1;
//            layoutParams.leftToLeft = textView_listReceive.getId();
//            textView_listMatch.setLayoutParams(layoutParams);
//        }

        ViewGroup.LayoutParams lp = viewPager_banner.getLayoutParams();
        lp.height = (int) (ScreenUtil.screenWidth * 0.69f);
        viewPager_banner.setLayoutParams(lp);
        viewPager_banner.setOffscreenPageLimit(5);
        mBannerAdapter = new BannerAdapter(mTabHomeView.callGetBindActivity(), mPresenter.getBanners());
        viewPager_banner.setAdapter(mBannerAdapter);

        view_root.findViewById(R.id.iv_more).setOnClickListener(this);

        View topTabView = view_root.findViewById(R.id.view_top_tab_menu);
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) topTabView.getLayoutParams();
        mlp.topMargin = (int) (-lp.height * 0.5f);
        topTabView.setLayoutParams(mlp);
//        onUserVerifyChanged();
//        SpannableString spannableString = new SpannableString(mTabHomeView.callGetResources().getString(R.string.home_friend_suggest_des));
//        spannableString.setSpan(new ForegroundColorSpan(mTabHomeView.callGetResources().getColor(R.color.text_color_black_666666)), 13, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spannableString.setSpan(new AbsoluteSizeSpan(12, true), 13, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
//        textView_FriendSuggestDes.setText(spannableString);
    }

//    void onUserVerifyChanged() {
//        if (view_hintVerify.getTag(R.id.hold_tag_id_one) != null && ((int) view_hintVerify.getTag(R.id.hold_tag_id_one)) == UserInfoManager.getInstance().getUserInfo().channelVerifyStatus)
//            return;
//        view_hintVerify.setTag(UserInfoManager.getInstance().getUserInfo().channelVerifyStatus);
//        boolean isOk = UserInfoManager.getInstance().isIdentifyStatusOk();
//        view_hintVerify.setVisibility(isOk ? View.GONE : View.VISIBLE);
//        textView_changeBatch.setText(isOk ? R.string.change_patch : R.string.go_verify);
//        textView_changeBatch.setTextColor(isOk ? mTabHomeView.callGetResources().getColor(R.color.text_color_grey_94A1A5) : Color.WHITE);
//        if (isOk && mDrawable == null)
//            mDrawable = AppUtil.drawableInit(R.drawable.tab_home_change_batch_icon, mTabHomeView.callGetResources());
//        textView_changeBatch.setCompoundDrawables(isOk ? mDrawable : null, null, null, null);
//        ViewGroup.LayoutParams lp = textView_changeBatch.getLayoutParams();
//        if (isOk) {
//            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
//            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//            textView_changeBatch.setLayoutParams(lp);
//            textView_changeBatch.setBackgroundDrawable(null);
//        } else {
//            lp.width = (int) mTabHomeView.callGetResources().getDimension(R.dimen.dp_56);
//            lp.height = (int) mTabHomeView.callGetResources().getDimension(R.dimen.dp_24);
//            textView_changeBatch.setLayoutParams(lp);
//            textView_changeBatch.setBackgroundResource(R.drawable.resume_request_share_btn_bg);
//        }
//    }

    public void changedUIToFitSDKReachKITKAT(int statusBarHeight) {
        ImageView imageView = view_root.findViewById(R.id.iv_more);
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
        mlp.topMargin += statusBarHeight;
        imageView.setLayoutParams(mlp);
    }

    public void onPause() {
        viewPager_banner.onPause();
    }

    public void onResume() {
        onHiddenChanged(mHidden);
    }

    void onHiddenChanged(boolean hidden) {
        this.mHidden = hidden;
        if (hidden)
            viewPager_banner.onPause();
        else
            viewPager_banner.onResume();
    }

    void updateSuggestFriendView() {
        List<FriendListResultBean.FriendBean> mSuggestFriendList = mPresenter.getSuggestFriends();
        if (mSuggestFriendList == null || mSuggestFriendList.isEmpty())
            return;
        FrameLayout viewAvatarItem;
        TextView textView_avatar;
        ImageView imageView_avatar;
        int currentCount = frameLayout_suggestFriendAvatarContainer.getChildCount();
        boolean hasCache;
        String nameShow;
        int wh = (int) mTabHomeView.callGetResources().getDimension(R.dimen.dp_30);
        int p = (int) mTabHomeView.callGetResources().getDimension(R.dimen.dp_10);
        FriendListResultBean.FriendBean friendBean;
        for (int i = 0, size = mSuggestFriendList.size(); i < size; i++) {
            hasCache = (i < currentCount);
            viewAvatarItem = (FrameLayout) (hasCache ? frameLayout_suggestFriendAvatarContainer.getChildAt(i) : LayoutInflater.from(mTabHomeView.callGetBindActivity()).inflate(R.layout.view_two_level_head_view, null));
            imageView_avatar = (ImageView) viewAvatarItem.getChildAt(0);
            textView_avatar = (TextView) viewAvatarItem.getChildAt(1);
            friendBean = mSuggestFriendList.get(i);
            nameShow = (!TextUtils.isEmpty(friendBean.nickName) ? friendBean.nickName : (!TextUtils.isEmpty(friendBean.trueName) ? friendBean.trueName : CommonConst.STR_DEFAULT_USER_NAME_SX));
            if (!TextUtils.isEmpty(friendBean.avatar) || nameShow.equals(CommonConst.STR_DEFAULT_USER_NAME_SX)) {
                textView_avatar.setVisibility(View.GONE);
                imageView_avatar.setVisibility(View.VISIBLE);
                LoadImageUtil.loadImage(friendBean.avatar, R.drawable.avatar_def, imageView_avatar);
            } else {
                imageView_avatar.setVisibility(View.GONE);
                textView_avatar.setText(nameShow.substring(0, 1));
                textView_avatar.setVisibility(View.VISIBLE);
            }
            viewAvatarItem.setVisibility(View.VISIBLE);
            if (!hasCache) {
                FrameLayout.LayoutParams marginLayoutParams = new FrameLayout.LayoutParams(wh, wh);
                marginLayoutParams.gravity = Gravity.RIGHT;
                marginLayoutParams.rightMargin = (wh - p) * i;
                viewAvatarItem.setLayoutParams(marginLayoutParams);
                frameLayout_suggestFriendAvatarContainer.addView(viewAvatarItem, 0);
            }
        }
    }


    void updateBannerView() {
        mBannerAdapter.updateDataSetChanged(mPresenter.getBanners());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_friend_nearly:
                NearlyPersonActivity.launch(mTabHomeView.callGetBindActivity());
                break;
//            case R.id.tv_list_receive_entrance:
//                NameListSearchActivity.launch(mTabHomeView.callGetBindActivity());
//                break;
//            case R.id.tv_list_match_entrance:
//                ListMatchActivity.launch(mTabHomeView.callGetBindActivity());
//                break;
            case R.id.tv_smart_group:
                SmartGroupIndexActivity.launch(mTabHomeView.callGetBindActivity());
                break;
            case R.id.iv_more:
                if (mAddMenuPopupWindow == null) {
                    mAddMenuPopupWindow = new ListPopupWindow(mTabHomeView.callGetBindActivity());
                    mAddMenuPopupWindow.setAdapter(new ArrayAdapter<>(mTabHomeView.callGetBindActivity(), R.layout.list_pop_defaylt_menu_item, mPresenter.getMoreEditMenu()));
                    mAddMenuPopupWindow.setWidth(ScreenUtil.screenWidth / 3);
                    mAddMenuPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                    mAddMenuPopupWindow.setHorizontalOffset((int) (-v.getMeasuredWidth() * 1.2f));
                    mAddMenuPopupWindow.setModal(true);
                    mAddMenuPopupWindow.setAnchorView(v);
                    mAddMenuPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            mAddMenuPopupWindow.dismiss();
                            switch (position) {
                                case 0:
                                    QRCodeUtil.startToScan(mTabHomeView.callGetBindActivity());
                                    break;
                                case 1:
                                    ImportPlatformListActivity.launch(mTabHomeView.callGetBindActivity());
                                    break;
                            }
                        }
                    });
                }
                mAddMenuPopupWindow.show();
                break;
        }
    }

    private class BannerAdapter extends CirclePagerAdapter<BannerResultBean.Data> implements View.OnClickListener {
        private Context mContext;
        private List<BannerResultBean.Data> mBannerList;

        private BannerAdapter(Context mContext, List<BannerResultBean.Data> bindDataList) {
            super(bindDataList);
            this.mContext = mContext;
            this.mBannerList = bindDataList;
        }

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            BannerResultBean.Data data = mBannerList.get(position);
            if (TextUtils.isEmpty(data.link))
                return;
            RemoteBrowserActivity.launch(mTabHomeView.callGetBindActivity(), null, false, data.link);
        }

        @Override
        protected View instantiateItemView(int position) {
            ImageView imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LoadImageUtil.loadImage(mBannerList.get(position).bannerUrl, 0, imageView);
            imageView.setOnClickListener(this);
            imageView.setTag(position);
            return imageView;
        }

        @Override
        protected void destroyItemView(View view) {

        }
    }
}
