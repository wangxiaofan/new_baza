package com.baza.android.bzw.bean.resumeelement;

/**
 * Created by Vincent.Lei on 2017/8/28.
 * Title：
 * Note：
 */

public class EducationUnion {
    public EducationBean current;
    public EducationBean target;

    public EducationUnion() {
    }

    public EducationUnion(EducationBean current, EducationBean target) {
        this.current = current;
        this.target = target;
    }
}
