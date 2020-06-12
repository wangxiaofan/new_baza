package com.baza.android.bzw.widget.dialog;

import android.content.Context;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.bznet.android.rcbox.R;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by LW on 2016/6/14.
 * Title :
 * Note :
 */
public class SingleWheelChoseDialog<T> implements View.OnClickListener, OnItemSelectedListener {
    private int currentProvinceIndex;
    private int previousPosition;
    private IOptionSelectedListener<T> iOptionSelectedListener;
    private ArrayList<T> data;
    private BottomSheetDialog bottomSheetDialog;
    private WheelView wheelView_selectors;

    public interface IOptionSelectedListener<T> {
        void onOptionSelected(int position, T t);
    }

    public SingleWheelChoseDialog(Context context, T[] data, int selectedPosition, IOptionSelectedListener<T> iOptionSelectedListener) {
        this.data = new ArrayList<>(data.length);
        this.previousPosition = selectedPosition;
        Collections.addAll(this.data, data);
        this.iOptionSelectedListener = iOptionSelectedListener;
        createRealDialog(context);
    }

    public SingleWheelChoseDialog(Context context, T[] data, IOptionSelectedListener<T> iOptionSelectedListener) {
        this(context, data, 0, iOptionSelectedListener);
    }

    private void createRealDialog(Context context) {
        bottomSheetDialog = new BottomSheetDialog(context);
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_single_wheel_choise, null);
        Button button_sure = viewGroup.findViewById(R.id.btn_sure);
        Button button_cancel = viewGroup.findViewById(R.id.btn_cancel);
        button_sure.setOnClickListener(this);
        button_cancel.setOnClickListener(this);
        wheelView_selectors = viewGroup.findViewById(R.id.options);
        wheelView_selectors.setCyclic(false);
        if (previousPosition >= 0 && previousPosition < data.size())
            wheelView_selectors.setCurrentItem(previousPosition);
        wheelView_selectors.setAdapter(new ArrayWheelAdapter<T>(data));
        wheelView_selectors.setOnItemSelectedListener(this);
        bottomSheetDialog.setContentView(viewGroup);
        bottomSheetDialog.setCancelable(false);
//        try {
//            Field mBehaviorField = bottomSheetDialog.getClass().getDeclaredField("mBehavior");
//            mBehaviorField.setAccessible(true);
//            final BottomSheetBehavior behavior = (BottomSheetBehavior) mBehaviorField.get(bottomSheetDialog);
//            behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//                @Override
//                public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
//                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                    }
//                }
//
//                @Override
//                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                }
//            });
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }

//        bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(R.color.line_D3DFEF);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sure:
                dismiss();
                if (iOptionSelectedListener != null)
                    iOptionSelectedListener.onOptionSelected(wheelView_selectors.getCurrentItem(), data.get(currentProvinceIndex));
                break;
            case R.id.btn_cancel:
            case R.id.main_view:
                dismiss();
                break;
        }
    }

    @Override
    public void onItemSelected(int index) {
        currentProvinceIndex = index;
    }

    public void show() {
        try {
            bottomSheetDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            //ignore
        }
    }

    public void dismiss() {
        try {
            bottomSheetDialog.dismiss();
        } catch (Exception e) {
            //ignore
        }
    }

}
