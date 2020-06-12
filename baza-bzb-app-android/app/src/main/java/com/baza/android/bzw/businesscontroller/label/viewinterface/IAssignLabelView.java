package com.baza.android.bzw.businesscontroller.label.viewinterface;

import com.baza.android.bzw.bean.label.Label;
import com.baza.android.bzw.base.IBaseView;

import java.util.List;

/**
 * Created by Vincent.Lei on 2017/5/25.
 * Title：
 * Note：
 */

public interface IAssignLabelView extends IBaseView {
    void callSetLabelLibrary(List<Label> labelList);

    void callExists();
}
