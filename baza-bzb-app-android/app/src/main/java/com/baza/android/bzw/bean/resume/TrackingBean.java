package com.baza.android.bzw.bean.resume;

import java.util.List;

public class TrackingBean {
    private int actualPageSize;
    private int count;
    private int currentPage;
    private boolean hasNext;
    private boolean hasPrev;
    private int offset;
    private int pageCount;
    private int pageSize;
    private int topCount;
    private List<DataBean> data;

    public int getActualPageSize() {
        return actualPageSize;
    }

    public void setActualPageSize(int actualPageSize) {
        this.actualPageSize = actualPageSize;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean isHasPrev() {
        return hasPrev;
    }

    public void setHasPrev(boolean hasPrev) {
        this.hasPrev = hasPrev;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTopCount() {
        return topCount;
    }

    public void setTopCount(int topCount) {
        this.topCount = topCount;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private List<BubbleBean> bubbleData;
        private List<TrackingListBean> trackingListData;

        public List<BubbleBean> getBubbleData() {
            return bubbleData;
        }

        public void setBubbleData(List<BubbleBean> bubbleData) {
            this.bubbleData = bubbleData;
        }

        public List<TrackingListBean> getTrackingListData() {
            return trackingListData;
        }

        public void setTrackingListData(List<TrackingListBean> trackingListData) {
            this.trackingListData = trackingListData;
        }
    }
}
