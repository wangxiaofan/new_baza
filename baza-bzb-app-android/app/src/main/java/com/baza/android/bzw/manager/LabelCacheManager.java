package com.baza.android.bzw.manager;

import com.baza.android.bzw.bean.label.Label;
import com.baza.android.bzw.dao.LabelDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2017/5/25.
 * Title：
 * Note：
 */

public class LabelCacheManager {
    private LabelCacheManager() {
    }

    private ArrayList<Label> mAllLabels = new ArrayList<>();
//    private Comparator<Label> mComparator = new Comparator<Label>() {
//        @Override
//        public int compare(Label lhs, Label rhs) {
//            return rhs.count - lhs.count;
//        }
//    };
    private static LabelCacheManager mManager = new LabelCacheManager();

    public static LabelCacheManager getInstance() {
        return mManager;
    }

    public ArrayList<Label> getAllLabels() {
        return mAllLabels;
    }

    public void setLabels(List<Label> labels) {
        if (labels != null) {
            mAllLabels.clear();
            mAllLabels.addAll(labels);
        }

    }

    public void addLabel(Label label) {
        if (label == null)
            return;
        mAllLabels.add(0, label);
    }

    public void deleteLabel(Label label) {
        if (label == null)
            return;
        int targetIndex = LabelDao.findTargetLabelPosition(label, mAllLabels);
        if (targetIndex == -1)
            return;
        mAllLabels.remove(targetIndex);
    }

    public void clear() {
        mAllLabels.clear();
    }

//    public void sortByCount(ArrayList<Label> list) {
//        if (list != null)
//            Collections.sort(list, mComparator);
//    }
}
