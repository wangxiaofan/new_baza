package com.baza.track.io.bean;

/**
 * Created by Vincent.Lei on 2018/4/17.
 * Title：
 * Note：
 */
public class TrackEventBean {
    public int id;
    public String type;
    public String value;
    public long time;

    public TrackEventBean() {
    }

    public TrackEventBean(String type, String value) {
        this.type = type;
        this.value = value;
        this.time = System.currentTimeMillis();
    }
}
