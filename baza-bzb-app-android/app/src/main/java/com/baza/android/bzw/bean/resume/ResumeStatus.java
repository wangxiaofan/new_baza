package com.baza.android.bzw.bean.resume;

public class ResumeStatus {
    private String name;
    private int status;

    public ResumeStatus(int code, String name) {
        this.name = name;
        this.status = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
