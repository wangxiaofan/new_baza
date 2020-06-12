package com.baza.android.bzw.bean.resumeelement;

/**
 * Created by Vincent.Lei on 2017/8/28.
 * Title：
 * Note：
 */

public class WorkExperienceUnion {
    public WorkExperienceBean current;
    public WorkExperienceBean target;

    public WorkExperienceUnion() {
    }

    public WorkExperienceUnion(WorkExperienceBean current, WorkExperienceBean target) {
        this.current = current;
        this.target = target;
    }
}
