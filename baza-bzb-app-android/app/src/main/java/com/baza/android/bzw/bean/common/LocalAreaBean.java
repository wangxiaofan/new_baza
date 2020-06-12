package com.baza.android.bzw.bean.common;

import java.io.Serializable;

/**
 * Created by Vincent.Lei on 2018/12/27.
 * Title：
 * Note：
 */
public class LocalAreaBean implements Serializable {
    public static final int CODE_NONE = -1;
    public int code;
    public String name;
    public String enName;
    public String shortName;
    public int level;
    public int parentCode;

    @Override
    public String toString() {
        return name;
    }
}
