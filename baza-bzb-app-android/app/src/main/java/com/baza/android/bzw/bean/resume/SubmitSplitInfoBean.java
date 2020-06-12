package com.baza.android.bzw.bean.resume;

import java.io.Serializable;

public class SubmitSplitInfoBean implements Serializable {
    private String name;
    private String calculationSettingId;
    private String userId;
    private String type;
    private double value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCalculationSettingId() {
        return calculationSettingId;
    }

    public void setCalculationSettingId(String calculationSettingId) {
        this.calculationSettingId = calculationSettingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
