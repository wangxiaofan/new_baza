package com.baza.android.bzw.widget.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baza.android.bzw.bean.resume.FirmMembersResultBean;
import com.baza.android.bzw.bean.resume.SplitInfoListResultBean;
import com.baza.android.bzw.bean.resume.SubmitSplitInfoBean;
import com.baza.android.bzw.widget.HeightScrollView;
import com.bznet.android.rcbox.R;
import com.google.gson.Gson;
import com.slib.utils.DateUtil;
import com.slib.utils.ToastUtil;
import com.slib.utils.string.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import baza.dialog.simpledialog.MaterialDialog;


/**
 * Created by LW on 2016/9/22.
 * Title : 安排面试
 * Note :
 */

public class MakeOfferDialog implements View.OnClickListener {
    private Context mContext;
    private MaterialDialog mMaterialDialog;
    private IReceiveListener mListener;
    private EditText et_monthPay, et_months, et_commissionRate, et_remark, et_service_charge;
    private LinearLayout ll_date;
    private TextView tv_recruiteTime;
    private LinearLayout listView;
    private String[] mMenuItemList;
    private String interviewTime;
    private List<FirmMembersResultBean.FirmMembersBean> firmMembers = new ArrayList<>();
    private List<SplitInfoListResultBean.SplitInfoBean> splitInfoBeans = new ArrayList<>();
    private LinearLayout ll_split_content;
    private boolean isSure = false;
    private HeightScrollView hsv;

    public interface IReceiveListener {
        void onReadyReceive(String monthPay, String recruiteTime, String months, String commissionRate, String serviceCharge, String remark, String performanceSplitItems);
    }

    public MakeOfferDialog(Context context, IReceiveListener listener, List<FirmMembersResultBean.FirmMembersBean> firmMembers,
                           List<SplitInfoListResultBean.SplitInfoBean> splitInfoBeans) {
        this.mContext = context;
        this.mListener = listener;
        this.firmMembers.clear();
        this.firmMembers.addAll(firmMembers);
        this.splitInfoBeans.clear();
        this.splitInfoBeans.addAll(splitInfoBeans);
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
                if (mListener != null) {
                    if (StringUtil.isEmpty(et_monthPay.getText().toString())) {
                        ToastUtil.showToast(mContext, "请输入签订月薪");
                        return;
                    }
                    if (StringUtil.isEmpty(et_months.getText().toString())) {
                        ToastUtil.showToast(mContext, "请输入月薪月份");
                        return;
                    }
                    if (StringUtil.isEmpty(et_commissionRate.getText().toString())) {
                        ToastUtil.showToast(mContext, "请输入佣金比例");
                        return;
                    }
                    if (StringUtil.isEmpty(interviewTime)) {
                        ToastUtil.showToast(mContext, "请输入入职时间");
                        return;
                    }

                    if (splitInfoBeans.size() > 0) {

                        if (StringUtil.isEmpty(et_service_charge.getText().toString())) {
                            ToastUtil.showToast(mContext, "请输入服务费");
                            return;
                        }

                        if (Integer.valueOf(et_service_charge.getText().toString()) > 999999) {
                            ToastUtil.showToast(mContext, "服务费输入限制为0-999999");
                            return;
                        }

                        if (Integer.valueOf(et_service_charge.getText().toString()) <= 0 && !isSure) {
                            new ReceiveDialog(mContext, new ReceiveDialog.IReceiveListener() {
                                @Override
                                public void onReadyReceive() {
                                    isSure = true;
                                }
                            }, "当前服务费填写的是\"0\"，确定吗？");
                            return;
                        }

                        String json = getSpliteInfo();

                        if (isCheckPoint()) {
                            mListener.onReadyReceive(et_monthPay.getText().toString(), interviewTime, et_months.getText().toString(), et_commissionRate.getText().toString(),
                                    et_service_charge.getText().toString(), et_remark.getText().toString(), json);
                            mMaterialDialog.dismiss();
                        }
                    } else {
                        mListener.onReadyReceive(et_monthPay.getText().toString(), interviewTime, et_months.getText().toString(), et_commissionRate.getText().toString(),
                                "", et_remark.getText().toString(), "");
                        mMaterialDialog.dismiss();
                    }
                }
            }
        });
        mMaterialDialog.setCanceledOnTouchOutside(false);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_make_offer, null);
        et_monthPay = contentView.findViewById(R.id.et_monthPay);
        et_months = contentView.findViewById(R.id.et_months);
        et_commissionRate = contentView.findViewById(R.id.et_commissionRate);
        et_remark = contentView.findViewById(R.id.et_remark);
        et_service_charge = contentView.findViewById(R.id.et_service_charge);
        ll_date = contentView.findViewById(R.id.ll_date);
        ll_date.setOnClickListener(this);
        tv_recruiteTime = contentView.findViewById(R.id.tv_recruiteTime);
        listView = contentView.findViewById(R.id.listView);
        ll_split_content = contentView.findViewById(R.id.ll_split_content);
        hsv = contentView.findViewById(R.id.hsv);

        et_monthPay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String monthPay = editable.toString().trim();
                int month_pay = 0;
                if (!monthPay.equals("")) {
                    month_pay = Integer.valueOf(monthPay);
                    if (month_pay > 9999) {
                        ToastUtil.showToast(mContext, "签订月薪输入限制为1-9999");
                        month_pay = month_pay / 10;
                        editable.delete(3, 3);
                    }
                }
                if (!StringUtil.isEmpty(et_months.getText().toString()) && !StringUtil.isEmpty(et_commissionRate.getText().toString()) && splitInfoBeans.size() > 0) {
                    int months = Integer.valueOf(et_months.getText().toString());
                    int rate = Integer.valueOf(et_commissionRate.getText().toString());
                    et_service_charge.setText("" + 10 * months * rate * month_pay);
                }
            }
        });

        et_months.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String months = editable.toString().trim();
                int month = 0;
                if (!months.equals("")) {
                    month = Integer.valueOf(months);
                    if (month > 99) {
                        ToastUtil.showToast(mContext, "月份输入限制为0-99");
                        month = month / 10;
                        editable.delete(2, 2);
                    }
                }

                if (!StringUtil.isEmpty(et_monthPay.getText().toString()) && !StringUtil.isEmpty(et_commissionRate.getText().toString()) && splitInfoBeans.size() > 0) {
                    int monthPay = Integer.valueOf(et_monthPay.getText().toString());
                    int rate = Integer.valueOf(et_commissionRate.getText().toString());
                    et_service_charge.setText("" + 10 * monthPay * rate * month);
                }
            }
        });

        et_commissionRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String commissionRate = editable.toString().trim();
                int rate = 0;
                if (!commissionRate.equals("")) {
                    rate = Integer.valueOf(commissionRate);
                    if (rate > 100) {
                        ToastUtil.showToast(mContext, "佣金比例输入限制为0-100");
                        rate = rate / 10;
                        editable.delete(2, 2);
                    }
                }

                if (!StringUtil.isEmpty(et_months.getText().toString()) && !StringUtil.isEmpty(et_monthPay.getText().toString()) && splitInfoBeans.size() > 0) {
                    int monthPay = Integer.valueOf(et_monthPay.getText().toString());
                    int months = Integer.valueOf(et_months.getText().toString());
                    et_service_charge.setText("" + 10 * monthPay * months * rate);
                }
            }
        });

        et_service_charge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String service_charge = editable.toString().trim();
                int charge = 0;
                if (!service_charge.equals("")) {
                    charge = Integer.valueOf(service_charge);
                    if (charge > 999999) {
                        ToastUtil.showToast(mContext, "服务费输入限制为0-999999");
                    }
                }
            }
        });

        if (splitInfoBeans.size() > 0) {
            ll_split_content.setVisibility(View.VISIBLE);
            mMenuItemList = new String[firmMembers.size()];
            int i = 0;
            for (FirmMembersResultBean.FirmMembersBean bean : firmMembers) {
                mMenuItemList[i] = bean.getRealName();
                i++;
            }
            setData();
        } else {
            ll_split_content.setVisibility(View.GONE);
        }
        mMaterialDialog.setMessageView(contentView);
        mMaterialDialog.show();
    }

    private void setData() {
        listView.removeAllViews();
        for (int i = 0; i < splitInfoBeans.size(); i++) {
            View content = LayoutInflater.from(mContext).inflate(R.layout.item_list_make_offer, null);
            TextView tv_name = content.findViewById(R.id.tv_name);
            tv_name.setText(splitInfoBeans.get(i).getName() + ":");
            LinearLayout ll_content = content.findViewById(R.id.ll_content);
            int type = splitInfoBeans.get(i).getType();
            for (int j = 0; j < splitInfoBeans.get(i).getSplitItems().size(); j++) {
                SplitInfoListResultBean.SplitInfoBean.SplitItemsBean bean = splitInfoBeans.get(i).getSplitItems().get(j);
                View subView = LayoutInflater.from(mContext).inflate(R.layout.item_list_make_offer_detail, null);
                TextView tv_recommend_name = subView.findViewById(R.id.tv_recommend_name);
                EditText tv_poine = subView.findViewById(R.id.tv_poine);
                EditText tv_value = subView.findViewById(R.id.tv_value);
                ImageView iv_add = subView.findViewById(R.id.iv_add);
                LinearLayout ll_point = subView.findViewById(R.id.ll_point);
                LinearLayout ll_value = subView.findViewById(R.id.ll_value);
                TextView tv_x = subView.findViewById(R.id.tv_x);
                TextView tv_point_per = subView.findViewById(R.id.tv_point_per);
                TextView tv_value_per = subView.findViewById(R.id.tv_value_per);
                if (splitInfoBeans.get(i).getType() == 0) {
                    tv_point_per.setVisibility(View.GONE);
                    tv_value_per.setVisibility(View.GONE);
                } else {
                    tv_point_per.setVisibility(View.VISIBLE);
                    tv_value_per.setVisibility(View.VISIBLE);
                }

                tv_recommend_name.setText(bean.getRealName());

                if (!splitInfoBeans.get(i).isCanModifyDefaultUser() && j == 0) {
                    tv_recommend_name.setClickable(false);
                    tv_recommend_name.setCompoundDrawables(null, null, null, null);
                    tv_recommend_name.setTextColor(Color.parseColor("#94A1A5"));
                } else {
                    tv_recommend_name.setClickable(true);
                    Drawable drawable = mContext.getResources().getDrawable(
                            R.drawable.icon_xuanze_03);
                    // 这一步必须要做，否则不会显示。
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                            drawable.getMinimumHeight());
                    tv_recommend_name.setCompoundDrawables(null, null, drawable, null);
                    tv_recommend_name.setTag(i);
                    tv_recommend_name.setTag(R.id.tv_username_tag, j);
                    tv_recommend_name.setOnClickListener(this);
                }

                if (bean.getRecommendationProportion() > -1) {
                    tv_poine.setText(bean.getRecommendationProportion() + "");
                } else {
                    tv_poine.setText("");
                }
                if (bean.getValue() > -1) {
                    tv_value.setText(bean.getValue() + "");
                } else {
                    tv_value.setText("");
                }

                if (bean.getRecommendationProportion() > 0) {
                    ll_point.setVisibility(View.VISIBLE);
                    tv_poine.setVisibility(View.VISIBLE);
                    tv_x.setVisibility(View.VISIBLE);
                    ll_value.setVisibility(View.VISIBLE);
                    if (bean.isNewAdd()) {
                        tv_poine.setEnabled(true);
                        tv_poine.setTextColor(Color.parseColor("#4E5968"));
                    } else {
                        tv_poine.setEnabled(false);
                        tv_poine.setTextColor(Color.parseColor("#94A1A5"));
                    }
                } else {
                    ll_point.setVisibility(View.GONE);
                    tv_poine.setVisibility(View.GONE);
                    tv_x.setVisibility(View.GONE);
                    ll_value.setVisibility(View.VISIBLE);
                }

                if (!splitInfoBeans.get(i).isCanModifyPercentValue() && !bean.isNewAdd()) {
                    tv_value.setEnabled(false);
                    tv_value.setTextColor(Color.parseColor("#94A1A5"));
                } else {
                    tv_value.setEnabled(true);
                    tv_value.setTextColor(Color.parseColor("#4E5968"));
                }

                if (splitInfoBeans.get(i).isIsMulti()) {
                    iv_add.setVisibility(View.VISIBLE);
                    iv_add.setTag(i);
                    iv_add.setOnClickListener(this);
                    if (bean.isNewAdd()) {
                        iv_add.setBackgroundResource(R.drawable.btn_jian);
                        iv_add.setTag(R.id.iv_add_tag, j);
                    } else {
                        iv_add.setBackgroundResource(R.drawable.btn_tianjia);
                        iv_add.setTag(R.id.iv_add_tag, -1);
                    }
                } else {
                    iv_add.setVisibility(View.GONE);
                }


                tv_poine.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        // TODO Auto-generated method stub
                        String et_point = editable.toString().trim();
                        int point = 0;
                        if (!et_point.equals("")) {
                            point = Integer.valueOf(et_point);
                            if (type == 0) {
                                if (point > 99999999) {
                                    ToastUtil.showToast(mContext, "输入限制为0-99999999");
                                    editable.delete(7, 7);
                                }
                            } else {
                                if (point > 100) {
                                    ToastUtil.showToast(mContext, "输入限制为0-100");
                                    editable.delete(2, 2);
                                }
                            }
                        }
                    }
                });

                tv_value.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        // TODO Auto-generated method stub
                        String et_value = editable.toString().trim();
                        int value = 0;
                        if (!et_value.equals("")) {
                            value = Integer.valueOf(et_value);
                            if (type == 0) {
                                if (value > 99999999) {
                                    ToastUtil.showToast(mContext, "输入限制为0-99999999");
                                    editable.delete(7, 7);
                                }
                            } else {
                                if (value > 100) {
                                    ToastUtil.showToast(mContext, "输入限制为0-100");
                                    editable.delete(2, 2);
                                }
                            }
                        }
                    }
                });

                ll_content.addView(subView);
            }
            listView.addView(content);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_add) {
            int position = (int) v.getTag();
            int subPosition = (int) v.getTag(R.id.iv_add_tag);
            getSpliteInfo();
            if (subPosition != -1) {
                splitInfoBeans.get(position).getSplitItems().remove(subPosition);
            } else {
                SplitInfoListResultBean.SplitInfoBean.SplitItemsBean bean = new SplitInfoListResultBean.SplitInfoBean.SplitItemsBean();
                bean.setRealName("");
                bean.setNewAdd(true);
                bean.setRecommendationProportion(-1);
                bean.setValue(-1);
                splitInfoBeans.get(position).getSplitItems().add(bean);
            }
            setData();
            if (position == splitInfoBeans.size() - 1) {
                hsv.post(new Runnable() {
                    @Override
                    public void run() {
                        hsv.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        } else if (v.getId() == R.id.tv_recommend_name) {
            showUserMenu(v);
        } else if (v.getId() == R.id.ll_date) {
            new RecommendDateSetDialog(mContext, new Date(), new RecommendDateSetDialog.ITimeSelectedListener() {
                @Override
                public void onTimeSelected(int year, int month, int day, String hm) {
                    interviewTime = DateUtil.formatTimeSelected(year, month, day, "");
                    tv_recruiteTime.setText(interviewTime);
                }
            }).show();
        }
    }

    private void showUserMenu(View anchorView) {
        new SingleWheelChoseDialog<>(mContext, mMenuItemList, new SingleWheelChoseDialog.IOptionSelectedListener<String>() {
            @Override
            public void onOptionSelected(int position, String str) {
                int pos = (int) anchorView.getTag();
                int subPos = (int) anchorView.getTag(R.id.tv_username_tag);
                splitInfoBeans.get(pos).getSplitItems().get(subPos).setRealName(str);
                splitInfoBeans.get(pos).getSplitItems().get(subPos).setUserId(getUserId(str));
                getSpliteInfo();
                setData();
            }
        }).show();
    }

    private String getUserId(String name) {
        String userid = "";
        for (FirmMembersResultBean.FirmMembersBean bean : firmMembers) {
            if (bean.getRealName().equals(name)) {
                userid = bean.getUserId();
            }
        }
        return userid;
    }

    private String getSpliteInfo() {
        if (splitInfoBeans.size() <= 0) {
            return "";
        }

        List<SubmitSplitInfoBean> submitSplitInfoBeans = new ArrayList<>();
        for (int i = 0; i < splitInfoBeans.size(); i++) {
            SplitInfoListResultBean.SplitInfoBean splitInfoBean = splitInfoBeans.get(i);
            for (int j = 0; j < splitInfoBean.getSplitItems().size(); j++) {
                SplitInfoListResultBean.SplitInfoBean.SplitItemsBean itemsBean = splitInfoBean.getSplitItems().get(j);
                SubmitSplitInfoBean submitSplitInfoBean = new SubmitSplitInfoBean();
                submitSplitInfoBean.setName(splitInfoBean.getName());
                submitSplitInfoBean.setType(splitInfoBean.getType() + "");
                submitSplitInfoBean.setCalculationSettingId(splitInfoBean.getCalculationSettingId());
                submitSplitInfoBean.setUserId(itemsBean.getUserId());
                View view = listView.getChildAt(i);
                LinearLayout subView = view.findViewById(R.id.ll_content);
                View thirdView = subView.getChildAt(j);

                EditText editPoint, editValue;
                editPoint = thirdView.findViewById(R.id.tv_poine);
                editValue = thirdView.findViewById(R.id.tv_value);

                int pointTexte = 1;
                if (editPoint.getVisibility() == View.VISIBLE) {
                    String point = editPoint.getText().toString();
                    if (StringUtil.isEmpty(point)) {
                        pointTexte = -1;
                        itemsBean.setRecommendationProportion(pointTexte);
                    } else {
                        pointTexte = Integer.valueOf(point);
                        itemsBean.setRecommendationProportion(pointTexte);
                    }
                } else {
                    itemsBean.setRecommendationProportion(-999);
                }

                int valueText = 1;
                String value = editValue.getText().toString();
                if (StringUtil.isEmpty(value)) {
                    valueText = -1;
                    itemsBean.setValue(valueText);
                } else {
                    valueText = Integer.valueOf(value);
                    itemsBean.setValue(valueText);
                }

                if (editPoint.getVisibility() == View.VISIBLE) {
                    submitSplitInfoBean.setValue((float) pointTexte * valueText / 100);
                } else {
                    submitSplitInfoBean.setValue(valueText);
                }

                submitSplitInfoBeans.add(submitSplitInfoBean);
            }
        }
        return new Gson().toJson(submitSplitInfoBeans);
    }


    private boolean isCheckPoint() {

        if (splitInfoBeans.size() <= 0) {
            return true;
        }

        double percenter = 0.00;
        for (int i = 0; i < splitInfoBeans.size(); i++) {
            SplitInfoListResultBean.SplitInfoBean splitInfoBean = splitInfoBeans.get(i);
            for (int j = 0; j < splitInfoBean.getSplitItems().size(); j++) {
                SplitInfoListResultBean.SplitInfoBean.SplitItemsBean itemsBean = splitInfoBean.getSplitItems().get(j);
                if (StringUtil.isEmpty(itemsBean.getRealName())) {
                    ToastUtil.showToast(mContext, "存在未填写的业绩拆分项，请检查后再提交！");
                    return false;
                }
                if (itemsBean.getValue() < 0) {
                    ToastUtil.showToast(mContext, "存在未填写的业绩拆分项，请检查后再提交！");
                    return false;
                }
                if (itemsBean.getRecommendationProportion() == -1) {
                    ToastUtil.showToast(mContext, "存在未填写的业绩拆分项，请检查后再提交！");
                    return false;
                }


                if (splitInfoBean.getType() != 0) {
                    if (itemsBean.getRecommendationProportion() > 0) {
                        percenter += itemsBean.getRecommendationProportion() * itemsBean.getValue();
                    } else {
                        percenter += itemsBean.getValue() * 100;
                    }
                }
            }
        }

        percenter = percenter / 100;
        if (percenter > 100) {
            ToastUtil.showToast(mContext, "分成比例总和已经超过100%");
            return false;
        } else if (percenter < 100) {
            new ReceiveDialog(mContext, new ReceiveDialog.IReceiveListener() {
                @Override
                public void onReadyReceive() {
                    mListener.onReadyReceive(et_monthPay.getText().toString(), interviewTime, et_months.getText().toString(), et_commissionRate.getText().toString(),
                            et_service_charge.getText().toString(), et_remark.getText().toString(), getSpliteInfo());
                    mMaterialDialog.dismiss();
                }
            }, "分成比例相加不等于100%，是否继续？");
            return false;
        }

        return true;
    }
}
