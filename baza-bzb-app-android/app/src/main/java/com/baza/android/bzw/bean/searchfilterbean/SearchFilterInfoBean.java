package com.baza.android.bzw.bean.searchfilterbean;

import android.text.TextUtils;

import com.baza.android.bzw.bean.common.LocalAreaBean;
import com.baza.android.bzw.bean.label.Label;
import com.baza.android.bzw.constant.CommonConst;
import com.baza.android.bzw.log.LogUtil;
import com.slib.storage.database.annotation.ColumnAnnotation;
import com.slib.storage.database.annotation.TableAnnotation;
import com.slib.utils.string.MD5;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by Vincent.Lei on 2017/3/28.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */
//@TableAnnotation(tableName = "searchFilterTable")
@TableAnnotation(tableName = "searchFilterTable4")
public class SearchFilterInfoBean implements Serializable {
    public static final int FILTER_INT_NONE = -1;
    @ColumnAnnotation(columnName = "id")
    private String id;
    @ColumnAnnotation(columnName = "title")
    private String title;
    private boolean mayChanged;
    @ColumnAnnotation(columnName = "keyWord")
    public String keyWord;
    @ColumnAnnotation(columnName = "cityName")
    public String cityName;
    @ColumnAnnotation(columnName = "cityCode", columnType = "INTEGER")
    public int cityCode = LocalAreaBean.CODE_NONE;
    @ColumnAnnotation(columnName = "workYearName")
    public String workYearName;
    @ColumnAnnotation(columnName = "startYearParameter", columnType = "INTEGER")
    public int startYearParameter = FILTER_INT_NONE;
    @ColumnAnnotation(columnName = "endYearParameter", columnType = "INTEGER")
    public int endYearParameter = FILTER_INT_NONE;
    @ColumnAnnotation(columnName = "degreeName")
    public String degreeName;
    @ColumnAnnotation(columnName = "degreeParameter", columnType = "INTEGER")
    public int degreeParameter = FILTER_INT_NONE;
    @ColumnAnnotation(columnName = "schoolName")
    public String schoolName;
    @ColumnAnnotation(columnName = "schoolParameter", columnType = "INTEGER")
    public int schoolParameter = FILTER_INT_NONE;
    @ColumnAnnotation(columnName = "sourceFromName")
    public String sourceFromName;
    @ColumnAnnotation(columnName = "sourceParameter")
    public String sourceParameter;
    @ColumnAnnotation(columnName = "sexName")
    public String sexName;
    @ColumnAnnotation(columnName = "sexParameter", columnType = "INTEGER")
    public int sexParameter = FILTER_INT_NONE;
    @ColumnAnnotation(columnName = "unionId")
    public String unionId;
    @ColumnAnnotation(columnName = "updateTime", columnType = "INTEGER")
    public long updateTime;
    public ArrayList<Label> labelList;
    public int mandatorySort;

//    public SearchFilterInfoBean deepCopyFromOld() {
//        SearchFilterInfoBean filterInfoBean = new SearchFilterInfoBean();
//        filterInfoBean.keyWord = keyWord;
//        filterInfoBean.cityName = cityName;
//        filterInfoBean.cityCode = cityCode;
//        filterInfoBean.workYearName = workYearName;
//        filterInfoBean.startYearParameter = startYearParameter;
//        filterInfoBean.endYearParameter = endYearParameter;
//        filterInfoBean.degreeName = degreeName;
//        filterInfoBean.degreeParameter = degreeParameter;
//        filterInfoBean.schoolName = schoolName;
//        filterInfoBean.schoolParameter = schoolParameter;
//        filterInfoBean.sourceFromName = sourceFromName;
//        filterInfoBean.sourceParameter = sourceParameter;
//        filterInfoBean.sexName = sexName;
//        filterInfoBean.sexParameter = sexParameter;
//        filterInfoBean.title = title;
//        filterInfoBean.updateTime = updateTime;
//        filterInfoBean.id = id;
//        if (labelList != null && !labelList.isEmpty()) {
//            filterInfoBean.labelList = new ArrayList<>(labelList.size());
//            filterInfoBean.labelList.addAll(labelList);
//        }
//
//        return filterInfoBean;
//    }


    public void attachKeyWord(String keyWord) {
        if (TextUtils.isEmpty(keyWord) && TextUtils.isEmpty(this.keyWord))
            return;
        if (keyWord != null && keyWord.equals(this.keyWord))
            return;
        mayChanged = true;
        this.keyWord = keyWord;
    }

    public void attachCityFilter(LocalAreaBean cityBean) {
        mayChanged = true;
        this.cityCode = cityBean.code;
        this.cityName = cityBean.name;
    }

    public void attachMoreFilter(WorkYearFilterBean workYearFilter, DegreeFilterBean degreeFilter, SchoolFilterBean schoolFilter, SourceFromFilterBean sourceFromFilter, SexFilterBean sexFilter) {
        mayChanged = true;
        if (workYearFilter != null) {
            this.workYearName = workYearFilter.name;
            this.startYearParameter = workYearFilter.startYearParameter;
            this.endYearParameter = workYearFilter.endYearParameter;
        } else {
            this.workYearName = null;
            this.startYearParameter = FILTER_INT_NONE;
            this.endYearParameter = FILTER_INT_NONE;
        }
        if (degreeFilter != null) {
            this.degreeName = degreeFilter.name;
            this.degreeParameter = degreeFilter.degreeParameter;
        } else {
            this.degreeName = null;
            this.degreeParameter = FILTER_INT_NONE;
        }
        if (schoolFilter != null) {
            this.schoolName = schoolFilter.name;
            this.schoolParameter = schoolFilter.schoolParameter;
        } else {
            this.schoolName = null;
            this.schoolParameter = FILTER_INT_NONE;
        }
        if (sourceFromFilter != null) {
            this.sourceFromName = sourceFromFilter.name;
            this.sourceParameter = sourceFromFilter.sourceParameter;
        } else {
            this.sourceFromName = null;
            this.sourceParameter = null;
        }
        if (sexFilter != null) {
            this.sexName = sexFilter.name;
            this.sexParameter = sexFilter.sexParameter;
        } else {
            this.sexName = null;
            this.sexParameter = FILTER_INT_NONE;
        }
    }

    public void setMandatorySort(int sortType) {
        mandatorySort = sortType;
    }

    public void attachLabelsFilter(HashMap<String, Label> mLabelSelected) {
        mayChanged = true;
        if (labelList == null)
            labelList = new ArrayList<>(mLabelSelected.size());
        else
            labelList.clear();
        Iterator iterator = mLabelSelected.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Label> entry = (Map.Entry<String, Label>) iterator.next();
            labelList.add(entry.getValue());
        }
    }

    public void clearMoreFilter() {
        mayChanged = true;
        this.workYearName = null;
        this.startYearParameter = FILTER_INT_NONE;
        this.endYearParameter = FILTER_INT_NONE;

        this.degreeName = null;
        this.degreeParameter = FILTER_INT_NONE;

        this.schoolName = null;
        this.schoolParameter = FILTER_INT_NONE;

        this.sourceFromName = null;
        this.sourceParameter = null;

        this.sexName = null;
        this.sexParameter = FILTER_INT_NONE;
    }

    public void clearSourceFromFilter() {
        mayChanged = true;
        this.sourceFromName = null;
        this.sourceParameter = null;
    }

    public void clearLabelsFilter() {
        mayChanged = true;
        if (labelList != null)
            labelList.clear();
    }

    public String getTitle() {
        shouldProcessChanged();
        return title;
    }

    public String getId() {
        shouldProcessChanged();
        return id;
    }

//    public void updateTitle(String newTitle) {
//        if (newTitle != null && !newTitle.equals(title)) {
//            this.title = newTitle;
//            LogUtil.d(title);
//            id = MD5.getStringMD5(title);
//        }
//    }

    public boolean shouldProcessChanged() {
        if (id == null || title == null || mayChanged) {
            StringBuilder stringBuilder = new StringBuilder();
            String doll = "+";
            boolean isJustDefault = true;
            if (!TextUtils.isEmpty(keyWord)) {
                stringBuilder.append(keyWord).append(doll);
                isJustDefault = false;
            }
            if (cityCode != LocalAreaBean.CODE_NONE) {
                stringBuilder.append(cityName).append(doll);
                isJustDefault = false;
            }
            if (!TextUtils.isEmpty(workYearName)) {
                stringBuilder.append(workYearName).append(doll);
                isJustDefault = false;
            }
            if (!TextUtils.isEmpty(degreeName)) {
                stringBuilder.append(degreeName).append(doll);
                isJustDefault = false;
            }
            if (!TextUtils.isEmpty(schoolName)) {
                isJustDefault = false;
                stringBuilder.append(schoolName).append(doll);
            }
            if (!TextUtils.isEmpty(sourceFromName)) {
                stringBuilder.append(sourceFromName).append(doll);
                isJustDefault = false;
            }
            if (!TextUtils.isEmpty(sexName)) {
                stringBuilder.append(sexName).append(doll);
                isJustDefault = false;
            }
            if (labelList != null && !labelList.isEmpty()) {
                for (int i = 0; i < labelList.size(); i++) {
                    stringBuilder.append(labelList.get(i).tag).append(doll);
                }
                isJustDefault = false;
            }
            mayChanged = false;
            if (isJustDefault) {
                title = null;
                id = null;
                return false;
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                title = stringBuilder.toString();
                LogUtil.d(title);
                id = MD5.getStringMD5(title);
            }

            return true;
        }
        return false;
    }

    public static void setParameterToMap(SearchFilterInfoBean mSearchFilterInfo, HashMap<String, String> mSearchParam, boolean cloudSearch) {
        if (!TextUtils.isEmpty(mSearchFilterInfo.keyWord)) {
            mSearchParam.put("keywords", mSearchFilterInfo.keyWord);
//            LogUtil.d("keyword = " + mSearchFilterInfo.keyWord);
        }
        if (mSearchFilterInfo.cityCode != LocalAreaBean.CODE_NONE) {
            mSearchParam.put("location", String.valueOf(mSearchFilterInfo.cityCode));
//            LogUtil.d("location  = " + mSearchFilterInfo.cityCode);
        }
        if (!TextUtils.isEmpty(mSearchFilterInfo.workYearName)) {
            mSearchParam.put("minExpr", String.valueOf(mSearchFilterInfo.startYearParameter));
            mSearchParam.put("maxExpr", String.valueOf(mSearchFilterInfo.endYearParameter));
//            LogUtil.d("exprStart  = " + mSearchFilterInfo.startYearParameter + " exprEnd = " + mSearchFilterInfo.endYearParameter);
        }
        if (!TextUtils.isEmpty(mSearchFilterInfo.degreeName)) {
            mSearchParam.put("degree", String.valueOf(mSearchFilterInfo.degreeParameter));
//            LogUtil.d("degree  = " + mSearchFilterInfo.degreeParameter);
        }
        if (!TextUtils.isEmpty(mSearchFilterInfo.schoolName)) {
            mSearchParam.put("schoolType", String.valueOf(mSearchFilterInfo.schoolParameter));
//            LogUtil.d("schoolType  = " + mSearchFilterInfo.schoolParameter);
        }
        if (!TextUtils.isEmpty(mSearchFilterInfo.sexName)) {
            mSearchParam.put("gender", String.valueOf(mSearchFilterInfo.sexParameter));
//            LogUtil.d("gender  = " + mSearchFilterInfo.sexParameter);
        }
        if (!TextUtils.isEmpty(mSearchFilterInfo.sourceFromName)) {
            mSearchParam.put("source", mSearchFilterInfo.sourceParameter);
//            LogUtil.d("source  = " + mSearchFilterInfo.sourceParameter);
        }
        if (!cloudSearch && mSearchFilterInfo.labelList != null && !mSearchFilterInfo.labelList.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder("[");
            for (int i = 0, size = mSearchFilterInfo.labelList.size(); i < size; i++) {
                stringBuilder.append("\"").append(mSearchFilterInfo.labelList.get(i).tag).append("\"").append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1).append("]");
            String tags = stringBuilder.toString();
            mSearchParam.put("tag", tags);
//            LogUtil.d("tagBindingList  = " + tagBindingList);
        }
        if (mSearchFilterInfo.mandatorySort != CommonConst.RESUME_SEARCH_SORT_ORDER_NONE)
            mSearchParam.put("mandatorySort", String.valueOf(mSearchFilterInfo.mandatorySort));
    }

    public static void setParameterToMap(SearchFilterInfoBean mSearchFilterInfo, HashMap<String, String> mSearchParam) {
        setParameterToMap(mSearchFilterInfo, mSearchParam, false);
    }

    private boolean isMoreFilterEnable() {
        return ((startYearParameter != FILTER_INT_NONE || endYearParameter != FILTER_INT_NONE || degreeParameter != FILTER_INT_NONE || schoolParameter != FILTER_INT_NONE || sexParameter != FILTER_INT_NONE) || !TextUtils.isEmpty(sourceParameter));
    }

    public boolean isCurrentSearchCanBeSave() {
        return !TextUtils.isEmpty(keyWord) || isAdvanceSearchEnable();
    }

    private boolean isAdvanceSearchEnable() {
        return (cityCode != LocalAreaBean.CODE_NONE) || isMoreFilterEnable() || (labelList != null && !labelList.isEmpty());
    }

    @Override
    public String toString() {
        return getTitle();
    }
}
