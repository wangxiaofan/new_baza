package com.baza.android.bzw.bean.resumeelement;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by Vincent.Lei on 2017/9/1.
 * Title：
 * Note：
 */

public class ProjectExperienceBean implements Serializable {
    public String id;
    public String projectDescription;
    public String projectName;
    public String projectRole;
    public String responsibility;
    public long startDate;
    public long endDate;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof ProjectExperienceBean))
            return false;
        ProjectExperienceBean projectExperience = (ProjectExperienceBean) obj;
        if (this.startDate != projectExperience.startDate || this.endDate != projectExperience.endDate)
            return false;
        if (!TextUtils.equals(this.id, projectExperience.id))
            return false;
        if (!TextUtils.equals(this.projectDescription, projectExperience.projectDescription))
            return false;
        if (!TextUtils.equals(this.projectName, projectExperience.projectName))
            return false;
        if (!TextUtils.equals(this.projectRole, projectExperience.projectRole))
            return false;
        return TextUtils.equals(this.responsibility, projectExperience.responsibility);
    }
}
