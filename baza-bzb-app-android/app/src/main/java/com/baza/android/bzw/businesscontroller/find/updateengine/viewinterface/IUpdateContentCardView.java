package com.baza.android.bzw.businesscontroller.find.updateengine.viewinterface;

import com.baza.android.bzw.base.IBaseView;
import com.baza.android.bzw.bean.resumeelement.EducationUnion;
import com.baza.android.bzw.bean.resumeelement.IntentionUnion;
import com.baza.android.bzw.bean.resumeelement.ProjectUnion;
import com.baza.android.bzw.bean.resumeelement.WorkExperienceUnion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/11/7.
 * Title：
 * Note：
 */

public interface IUpdateContentCardView extends IBaseView {
    void callCancelLoadingView(boolean success, int errorCode, String errorMsg);

    void callSetTopView(ArrayList<Integer> listEnableUpdate);

    void callSetSelfEvaluationView();

    void callSetIntentionsView(IntentionUnion intentionUnion);

    void callSetProjectExperienceView(List<ProjectUnion> projectUnions);

    void callSetEducationView(List<EducationUnion> educationUnions);

    void callSetWorkExperienceView(List<WorkExperienceUnion> workExperienceUnions);

    void callSetResumeTextView();

    void callSetOnUpdatingView();

    void callSetSubmitUpdateView(boolean enable);
}
