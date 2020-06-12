package com.baza.android.bzw.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.baza.android.bzw.bean.common.LocalAreaBean;
import com.baza.android.bzw.manager.AddressManager;
import com.slib.utils.ScreenUtil;
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;

/**
 * Created by LW on 2016/icon_person/14.
 * Title :
 * Note :
 */
public class PickAddressDialog extends Dialog implements View.OnClickListener, OnItemSelectedListener {

    private Button button_sure, button_cancel;

    private WheelView wheelView_province, wheelView_city;

    private ArrayList<LocalAreaBean> provinceList;
    private ArrayList<LocalAreaBean> cityList;
    private int currentProvinceIndex;

    private IAddressSelectedListener iAddressSelectedListener;


    public interface IAddressSelectedListener {
        void onAddressSelected(LocalAreaBean province, LocalAreaBean city);
    }

    public PickAddressDialog(Context context, IAddressSelectedListener iAddressSelectedListener) {
        this(context, R.style.customerDialog_bottom);
        this.iAddressSelectedListener = iAddressSelectedListener;
    }

    public PickAddressDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_pick_address);

        View mainView = findViewById(R.id.main_view);
        ViewGroup.LayoutParams lp = mainView.getLayoutParams();
        lp.width = ScreenUtil.screenWidth;
        mainView.setLayoutParams(lp);
        mainView.setOnClickListener(this);
        button_sure = findViewById(R.id.btn_sure);
        button_cancel = findViewById(R.id.btn_cancel);
        button_sure.setOnClickListener(this);
        button_cancel.setOnClickListener(this);


        wheelView_province = findViewById(R.id.options1);
        wheelView_city = findViewById(R.id.options2);

        wheelView_province.setCyclic(false);
        wheelView_city.setCyclic(false);
        wheelView_province.setCurrentItem(0);
        wheelView_city.setCurrentItem(0);

        provinceList = (ArrayList<LocalAreaBean>) AddressManager.getInstance().getProvinces();
        if (provinceList == null || provinceList.isEmpty())
            return;
        cityList = (ArrayList<LocalAreaBean>) AddressManager.getInstance().getCitiesByProvinceCode(provinceList.get(0).code);
        if (cityList == null || cityList.isEmpty())
            return;
        wheelView_province.setAdapter(new ArrayWheelAdapter<>(provinceList));
        wheelView_city.setAdapter(new ArrayWheelAdapter<>(cityList));

        wheelView_province.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sure:
                dismiss();
                setAddress();
                break;
            case R.id.btn_cancel:
            case R.id.main_view:
                dismiss();
                break;
        }
    }

    @Override
    public void onItemSelected(int index) {
        if (currentProvinceIndex == index)
            return;
        currentProvinceIndex = index;
        cityList = (ArrayList<LocalAreaBean>) AddressManager.getInstance().getCitiesByProvinceCode(provinceList.get(index).code);
        if (cityList == null || cityList.isEmpty())
            return;
//        int currentSelectedItem = wheelView_city.getCurrentItem();
        wheelView_city.setAdapter(new ArrayWheelAdapter<LocalAreaBean>(cityList));
//        if (currentSelectedItem >= 0 && currentSelectedItem < cityList.size()) {
//            wheelView_city.setCurrentItem(currentSelectedItem);
//        }
    }

    private void setAddress() {
        if (iAddressSelectedListener == null)
            return;
        if (provinceList == null || cityList == null)
            return;
        LocalAreaBean province = null;
        LocalAreaBean city = null;
        if (currentProvinceIndex >= 0 && currentProvinceIndex < provinceList.size())
            province = provinceList.get(currentProvinceIndex);
        int currentCityIndex = wheelView_city.getCurrentItem();
        if (currentCityIndex >= 0 && currentCityIndex < cityList.size())
            city = cityList.get(currentCityIndex);
        if (province != null && city != null)
            iAddressSelectedListener.onAddressSelected(province, city);
    }

}
