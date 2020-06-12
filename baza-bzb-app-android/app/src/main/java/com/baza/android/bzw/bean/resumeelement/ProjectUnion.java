package com.baza.android.bzw.bean.resumeelement;

/**
 * Created by Vincent.Lei on 2018/11/13.
 * Title：
 * Note：
 */
public class ProjectUnion {
    public ProjectExperienceBean current;
    public ProjectExperienceBean target;

    public ProjectUnion() {
    }

    public ProjectUnion(ProjectExperienceBean current, ProjectExperienceBean target) {
        this.current = current;
        this.target = target;
    }
}
