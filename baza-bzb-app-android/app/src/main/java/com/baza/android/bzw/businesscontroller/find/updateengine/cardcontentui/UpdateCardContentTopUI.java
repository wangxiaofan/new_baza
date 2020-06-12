package com.baza.android.bzw.businesscontroller.find.updateengine.cardcontentui;

import android.content.res.Resources;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baza.android.bzw.bean.resume.ResumeUpdatedContentResultBean;
import com.baza.android.bzw.bean.updateengine.UpdateResumeWrapperBean;
import com.baza.android.bzw.businesscontroller.find.updateengine.presenter.UpdateContentCardPresenter;
import com.baza.android.bzw.businesscontroller.find.updateengine.viewinterface.IUpdateContentCardView;
import com.baza.android.bzw.manager.AddressManager;
import com.baza.android.bzw.manager.FriendlyShowInfoManager;
import com.slib.utils.AppUtil;
import com.slib.utils.DateUtil;
import com.slib.utils.ScreenUtil;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vincent.Lei on 2017/8/25.
 * Title：
 * Note：
 */

public class UpdateCardContentTopUI implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.tv_current_complete)
    TextView textView_currentCompletion;
    @BindView(R.id.tv_updated_complete)
    TextView textView_updatedCompletion;
    @BindView(R.id.tv_name)
    TextView textView_name;
    @BindView(R.id.tv_company)
    TextView textView_company;
    @BindView(R.id.tv_city)
    TextView textView_city;
    @BindView(R.id.cb_check_all)
    CheckBox checkBox_all;
    @BindView(R.id.ll_item_container)
    LinearLayout linearLayout_itemContainer;
    @BindView(R.id.fl_main_title)
    FrameLayout frameLayout_mainTitle;

    private IUpdateContentCardView mUpdateContentCardView;
    private UpdateContentCardPresenter mPresenter;
    private View mRootView;
    private ArrayList<Integer> mListEnableUpdate;
    private ArrayList<Integer> mAllItemIdList;
    private int mAllItemCount;
    private int mSelectedCount;
    private boolean mHasInit;
    private int mColorBlack;

    public UpdateCardContentTopUI(IUpdateContentCardView updateContentCardView, UpdateContentCardPresenter presenter) {
        this.mUpdateContentCardView = updateContentCardView;
        this.mPresenter = presenter;
        init();
    }

    public View getView() {
        return mRootView;
    }

    public void init() {
        mRootView = mUpdateContentCardView.callGetBindActivity().getLayoutInflater().inflate(R.layout.layout_candidate_update_card_content_top_ui, null);
        ButterKnife.bind(this, mRootView);
        checkBox_all.setOnCheckedChangeListener(this);
        mColorBlack = mUpdateContentCardView.callGetResources().getColor(R.color.text_color_black_4E5968);
    }

    public void updateViews(ArrayList<Integer> listEnableUpdate) {
        ResumeUpdatedContentResultBean.Data data = mPresenter.getEnableUpdateContentData();
        if (data == null || data.current == null || data.target == null)
            return;
        updateShortMsgView(data);
        if (mListEnableUpdate == null)
            mListEnableUpdate = listEnableUpdate;
        mAllItemCount = mListEnableUpdate.size();
        boolean isJustShowEnableUpdateContentMode = mPresenter.isJustShowEnableUpdateContentMode();
        if (mAllItemCount == 0 && isJustShowEnableUpdateContentMode) {
            frameLayout_mainTitle.setVisibility(View.GONE);
            linearLayout_itemContainer.setVisibility(View.GONE);
            mRootView.findViewById(R.id.view_depart_line).setVisibility(View.GONE);
            return;
        }
        frameLayout_mainTitle.setVisibility(View.VISIBLE);
        linearLayout_itemContainer.setVisibility(View.VISIBLE);
        mRootView.findViewById(R.id.view_depart_line).setVisibility(View.VISIBLE);
        if (mAllItemIdList == null)
            initAllItemIds();
        for (int i = 0, size = mAllItemIdList.size() - 1; i <= size; i++)
            setUpdatedItems(mAllItemIdList.get(i), mListEnableUpdate.contains(mAllItemIdList.get(i)), isJustShowEnableUpdateContentMode, data);
        //首次初始化的时候，将checkBox_all设置为选中状态，默认选中所有选项
        checkBox_all.setVisibility((!mListEnableUpdate.isEmpty()) ? View.VISIBLE : View.GONE);
        if (!mHasInit && checkBox_all.getVisibility() == View.VISIBLE)
            checkBox_all.setChecked(true);
        mHasInit = true;
        hideLastVisibleDepartLine();

    }


    private void hideLastVisibleDepartLine() {
        View lastVisibleItem = null;
        for (int i = 0, size = linearLayout_itemContainer.getChildCount(); i < size; i++) {
            if (linearLayout_itemContainer.getChildAt(i).getVisibility() == View.VISIBLE)
                lastVisibleItem = linearLayout_itemContainer.getChildAt(i);
        }
        if (lastVisibleItem != null)
            lastVisibleItem.findViewById(R.id.view_depart_line).setVisibility(View.GONE);
    }

    private void initAllItemIds() {
        mAllItemIdList = new ArrayList<Integer>(10) {
            {
                add(R.id.set_item_id_name);
                add(R.id.set_item_id_contact);
                add(R.id.set_item_id_job);
                add(R.id.set_item_id_email);
                add(R.id.set_item_id_company);
                add(R.id.set_item_id_school);
                add(R.id.set_item_id_major);
                add(R.id.set_item_id_city);
                add(R.id.set_item_id_degree);
                add(R.id.set_item_id_workYear);
                add(R.id.set_item_id_sex);
                add(R.id.set_item_id_hukou);
                add(R.id.set_item_id_marriage);
                add(R.id.set_item_id_birthday);
            }
        };
    }

    private void updateShortMsgView(ResumeUpdatedContentResultBean.Data data) {
        Resources resources = mUpdateContentCardView.callGetResources();
        textView_name.setText(data.current.realName);
        textView_company.setText((TextUtils.isEmpty(data.current.company) ? resources.getString(R.string.message_unknown) : data.current.company) + "/" + (TextUtils.isEmpty(data.current.title) ? resources.getString(R.string.message_unknown) : data.current.title));
        textView_city.setText(AddressManager.getInstance().getCityNameByCode(data.current.location));
        //当前完整度
        SpannableString spannableString = new SpannableString(resources.getString(R.string.completion_current, AppUtil.formatFeeFloat(data.current.completion)));
        int textSizeLarge = ScreenUtil.px2dip(resources.getDimension(R.dimen.text_size_22));
        spannableString.setSpan(new ForegroundColorSpan(resources.getColor(R.color.text_color_blue_0D315C)), 0, spannableString.length() - 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(textSizeLarge, true), 0, spannableString.length() - 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_currentCompletion.setText(spannableString);
        //更新后完整度
        spannableString = new SpannableString(resources.getString(R.string.completion_updated, AppUtil.formatFeeFloat(data.target.completion)));
        spannableString.setSpan(new ForegroundColorSpan(resources.getColor(R.color.text_color_orange_FF7700)), 0, spannableString.length() - 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(textSizeLarge, true), 0, spannableString.length() - 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_updatedCompletion.setText(spannableString);
    }

    private void setUpdatedItems(int id, boolean enableUpdate, boolean isJustShowEnableUpdateContentMode, ResumeUpdatedContentResultBean.Data data) {
        LayoutInflater layoutInflater = mUpdateContentCardView.callGetBindActivity().getLayoutInflater();
        Resources resource = mUpdateContentCardView.callGetResources();
        boolean alreadyAdd = true;
        View view = linearLayout_itemContainer.findViewById(id);
        if (view == null) {
            alreadyAdd = false;
            view = layoutInflater.inflate(R.layout.layout_candidate_update_main_info_item, null);
        }
        view.setId(id);
        TextView textView_currentValue = view.findViewById(R.id.tv_current_value);
        TextView textView_UpdatedValue = view.findViewById(R.id.tv_updated_value);
        CheckBox checkBox = view.findViewById(R.id.cb_check);
        View view_departLine = view.findViewById(R.id.view_depart_line);
        checkBox.setTag(id);
        checkBox.setOnCheckedChangeListener(this);
        String currentValue = null;
        SpannableString spannableStringUpdateValue = null;
        switch (id) {
            case R.id.set_item_id_name:
                currentValue = resource.getString(R.string.update_item_name, data.current.realName);
                if (enableUpdate) {
                    spannableStringUpdateValue = new SpannableString(resource.getString(R.string.update_item_to, data.target.realName));
                    spannableStringUpdateValue.setSpan(new ForegroundColorSpan(mColorBlack), 0, spannableStringUpdateValue.length() - data.target.realName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                break;
            case R.id.set_item_id_contact:
                currentValue = resource.getString(R.string.update_item_cellphone, data.current.mobile);
                if (enableUpdate) {
                    spannableStringUpdateValue = new SpannableString(resource.getString(R.string.update_item_to, data.target.mobile));
                    spannableStringUpdateValue.setSpan(new ForegroundColorSpan(mColorBlack), 0, spannableStringUpdateValue.length() - data.target.mobile.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                break;
            case R.id.set_item_id_job:
                currentValue = resource.getString(R.string.update_item_job, TextUtils.isEmpty(data.current.title) ? resource.getString(R.string.message_unknown) : data.current.title);
                if (enableUpdate) {
                    spannableStringUpdateValue = new SpannableString(resource.getString(R.string.update_item_to, data.target.title));
                    spannableStringUpdateValue.setSpan(new ForegroundColorSpan(mColorBlack), 0, spannableStringUpdateValue.length() - data.target.title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                break;
            case R.id.set_item_id_email:
                currentValue = resource.getString(R.string.update_item_email, TextUtils.isEmpty(data.current.email) ? resource.getString(R.string.message_unknown) : data.current.email);
                if (enableUpdate) {
                    spannableStringUpdateValue = new SpannableString(resource.getString(R.string.update_item_to, data.target.email));
                    spannableStringUpdateValue.setSpan(new ForegroundColorSpan(mColorBlack), 0, spannableStringUpdateValue.length() - data.target.email.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                break;
            case R.id.set_item_id_degree:
                currentValue = resource.getString(R.string.update_item_degree, FriendlyShowInfoManager.getInstance().getDegree(data.current.degree));
                String temp;
                if (enableUpdate) {
                    temp = FriendlyShowInfoManager.getInstance().getDegree(data.target.degree);
                    spannableStringUpdateValue = new SpannableString(resource.getString(R.string.update_item_to, temp));
                    spannableStringUpdateValue.setSpan(new ForegroundColorSpan(mColorBlack), 0, spannableStringUpdateValue.length() - temp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                break;
            case R.id.set_item_id_workYear:
                currentValue = resource.getString(R.string.update_item_work_year, String.valueOf(data.current.yearExpr));
                if (enableUpdate) {
                    temp = String.valueOf(data.target.yearExpr);
                    spannableStringUpdateValue = new SpannableString(resource.getString(R.string.update_item_to, temp));
                    spannableStringUpdateValue.setSpan(new ForegroundColorSpan(mColorBlack), 0, spannableStringUpdateValue.length() - temp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                break;
            case R.id.set_item_id_company:
                currentValue = resource.getString(R.string.update_item_company, TextUtils.isEmpty(data.current.company) ? resource.getString(R.string.message_unknown) : data.current.company);
                if (enableUpdate) {
                    spannableStringUpdateValue = new SpannableString(resource.getString(R.string.update_item_to, data.target.company));
                    spannableStringUpdateValue.setSpan(new ForegroundColorSpan(mColorBlack), 0, spannableStringUpdateValue.length() - data.target.company.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                break;
            case R.id.set_item_id_city:
                currentValue = resource.getString(R.string.update_item_city, AddressManager.getInstance().getCityNameByCode(data.current.location));
                if (enableUpdate) {
                    temp = AddressManager.getInstance().getCityNameByCode(data.target.location);
                    spannableStringUpdateValue = new SpannableString(resource.getString(R.string.update_item_to, temp));
                    spannableStringUpdateValue.setSpan(new ForegroundColorSpan(mColorBlack), 0, spannableStringUpdateValue.length() - temp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                break;
            case R.id.set_item_id_school:
                currentValue = resource.getString(R.string.update_item_school, TextUtils.isEmpty(data.current.school) ? resource.getString(R.string.message_unknown) : data.current.school);
                if (enableUpdate) {
                    spannableStringUpdateValue = new SpannableString(resource.getString(R.string.update_item_to, data.target.school));
                    spannableStringUpdateValue.setSpan(new ForegroundColorSpan(mColorBlack), 0, spannableStringUpdateValue.length() - data.target.school.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                break;
            case R.id.set_item_id_major:
                currentValue = resource.getString(R.string.update_item_major, TextUtils.isEmpty(data.current.major) ? resource.getString(R.string.message_unknown) : data.current.major);
                if (enableUpdate) {
                    spannableStringUpdateValue = new SpannableString(resource.getString(R.string.update_item_to, data.target.major));
                    spannableStringUpdateValue.setSpan(new ForegroundColorSpan(mColorBlack), 0, spannableStringUpdateValue.length() - data.target.major.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                break;
            case R.id.set_item_id_sex:
                currentValue = resource.getString(R.string.update_item_sex, FriendlyShowInfoManager.getInstance().getGender(data.current.gender));
                if (enableUpdate) {
                    spannableStringUpdateValue = new SpannableString(resource.getString(R.string.update_item_to, FriendlyShowInfoManager.getInstance().getGender(data.target.gender)));
                    spannableStringUpdateValue.setSpan(new ForegroundColorSpan(mColorBlack), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                break;
            case R.id.set_item_id_hukou:
                currentValue = resource.getString(R.string.update_item_hukou, AddressManager.getInstance().getCityNameByCode(data.current.huKou));
                if (enableUpdate) {
                    spannableStringUpdateValue = new SpannableString(resource.getString(R.string.update_item_to, AddressManager.getInstance().getCityNameByCode(data.target.huKou)));
                    spannableStringUpdateValue.setSpan(new ForegroundColorSpan(mColorBlack), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                break;
            case R.id.set_item_id_marriage:
                currentValue = resource.getString(R.string.update_item_marriage, FriendlyShowInfoManager.getInstance().getMarriage(data.current.marriage));
                if (enableUpdate) {
                    spannableStringUpdateValue = new SpannableString(resource.getString(R.string.update_item_to, FriendlyShowInfoManager.getInstance().getMarriage(data.target.marriage)));
                    spannableStringUpdateValue.setSpan(new ForegroundColorSpan(mColorBlack), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                break;
            case R.id.set_item_id_birthday:
                currentValue = resource.getString(R.string.update_item_birthday, Long.parseLong(data.current.birthday) <= 0 ? resource.getString(R.string.message_unknown) : DateUtil.longMillions2FormatDate(Long.parseLong(data.current.birthday), DateUtil.SDF_YMD));
                if (enableUpdate) {
                    spannableStringUpdateValue = new SpannableString(resource.getString(R.string.update_item_to, DateUtil.longMillions2FormatDate(Long.parseLong(data.target.birthday), DateUtil.SDF_YMD)));
                    spannableStringUpdateValue.setSpan(new ForegroundColorSpan(mColorBlack), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                break;
        }
        textView_currentValue.setText(currentValue);
        textView_UpdatedValue.setText(spannableStringUpdateValue);
        checkBox.setVisibility(enableUpdate ? View.VISIBLE : View.GONE);
        textView_UpdatedValue.setVisibility(enableUpdate ? View.VISIBLE : View.GONE);
        if (isJustShowEnableUpdateContentMode && !enableUpdate) {
            view.setVisibility(View.GONE);
        } else {
            view_departLine.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);
        }
        if (!alreadyAdd)
            linearLayout_itemContainer.addView(view);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == checkBox_all) {
            mSelectedCount = (isChecked ? mAllItemCount : 0);
            int childCount = linearLayout_itemContainer.getChildCount();
            View viewItem;
            CheckBox checkBox;
            for (int i = 0; i < childCount; i++) {
                viewItem = linearLayout_itemContainer.getChildAt(i);
                checkBox = viewItem.findViewById(R.id.cb_check);
                checkBox.setOnCheckedChangeListener(null);
                checkBox.setChecked(isChecked);
                checkBox.setOnCheckedChangeListener(this);
            }
        } else {
            mSelectedCount += (isChecked ? 1 : -1);
            checkBox_all.setOnCheckedChangeListener(null);
            checkBox_all.setChecked((mSelectedCount == mAllItemCount));
            checkBox_all.setOnCheckedChangeListener(this);
        }
    }

    public UpdateResumeWrapperBean.MainInfo getUpdateContentParam() {
        if (mSelectedCount == 0)
            return null;
        ResumeUpdatedContentResultBean.Data data = mPresenter.getEnableUpdateContentData();
        if (data == null || data.target == null)
            return null;
        UpdateResumeWrapperBean.MainInfo mainInfo = new UpdateResumeWrapperBean.MainInfo();
        int childCount = linearLayout_itemContainer.getChildCount();
        View viewItem;
        CheckBox checkBox;
        int id;
        for (int i = 0; i < childCount; i++) {
            viewItem = linearLayout_itemContainer.getChildAt(i);
            checkBox = viewItem.findViewById(R.id.cb_check);
            if (checkBox.isChecked()) {
                id = (int) checkBox.getTag();
                if (!mListEnableUpdate.contains(id))
                    continue;
                switch (id) {
                    case R.id.set_item_id_name:
                        mainInfo.realName = data.target.realName;
                        break;
                    case R.id.set_item_id_contact:
                        mainInfo.mobile = data.target.mobile;
                        break;
                    case R.id.set_item_id_job:
                        mainInfo.title = data.target.title;
                        break;
                    case R.id.set_item_id_email:
                        mainInfo.email = data.target.email;
                        break;
                    case R.id.set_item_id_degree:
                        mainInfo.degree = data.target.degree;
                        break;
                    case R.id.set_item_id_workYear:
                        mainInfo.yearExpr = data.target.yearExpr;
                        break;
                    case R.id.set_item_id_company:
                        mainInfo.company = data.target.company;
                        break;
                    case R.id.set_item_id_city:
                        mainInfo.location = data.target.location;
                        break;
                    case R.id.set_item_id_school:
                        mainInfo.school = data.target.school;
                        break;
                    case R.id.set_item_id_major:
                        mainInfo.major = data.target.major;
                        break;
                    case R.id.set_item_id_marriage:
                        mainInfo.marriage = data.target.marriage;
                        break;
                    case R.id.set_item_id_birthday:
                        mainInfo.birthday = data.target.birthday;
                        break;
                    case R.id.set_item_id_sex:
                        mainInfo.gender = data.target.gender;
                        break;
                    case R.id.set_item_id_hukou:
                        mainInfo.huKou = data.target.huKou;
                        break;
                }
            }
        }
        return mainInfo;
    }
}
