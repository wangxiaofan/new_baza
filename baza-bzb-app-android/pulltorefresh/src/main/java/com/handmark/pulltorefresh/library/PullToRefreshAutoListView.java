/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.handmark.pulltorefresh.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.handmark.pulltorefresh.library.internal.EmptyViewMethodAccessor;

public class PullToRefreshAutoListView extends PullToRefreshAdapterViewBase<AutoListView> {

    public interface IAutoLimitConditionProvider {
        boolean isAutoStateEnable();
    }


    private AutoListView autoListView;
    private IAutoLimitConditionProvider iAutoLimitConditionProvider;
    private boolean hasLoadAllData;
    private boolean isOnAutoLoading;
    private int minAutoCount = 10;

    public void setMinAutoCount(int minAutoCount) {
        this.minAutoCount = minAutoCount;
    }


    public void setAutoLimitConditionProvider(IAutoLimitConditionProvider iAutoLimitConditionProvider) {
        this.iAutoLimitConditionProvider = iAutoLimitConditionProvider;
    }


    public PullToRefreshAutoListView(Context context) {
        super(context);
    }

    public PullToRefreshAutoListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshAutoListView(Context context, Mode mode) {
        super(context, mode);
    }

    public PullToRefreshAutoListView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
    }

    @Override
    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected final AutoListView createRefreshableView(Context context, AttributeSet attrs) {
        if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
            autoListView = new InternalAutoListViewSDK9(context, attrs);
        } else {
            autoListView = new InternalAutoListView(context, attrs);
        }

        // Use Generated ID (from res/values/ids.xml)
        autoListView.setId(R.id.autolistview);
        return autoListView;
    }

    @Override
    protected void onLastItemVisibleEx() {
        Log.d("AutoListView", "onLastItemVisibleEx*****");

        int currentItemCount = (autoListView != null && autoListView.getAdapter() != null) ? autoListView.getAdapter().getCount() - (autoListView.getFooterViewsCount() + autoListView.getHeaderViewsCount()) : 0;
//        Log.d("AutoListView", "count = " + currentItemCount);

        if (currentItemCount < minAutoCount)
            hasLoadAllData = true;

        if (isOnAutoLoading || hasLoadAllData) {

            if (hasLoadAllData && autoListView != null) {
                autoListView.reset(AutoListView.COMPLETE_MODE_NO_MORE_DATA, currentItemCount > 0);
            }
            return;
        }


        if (autoListView != null) {
            isOnAutoLoading = true;
            autoListView.onLastItemVisible();

        }
        if (mOnRefreshListener2 != null) {
            isOnAutoLoading = true;
            mOnRefreshListener2.onPullUpToRefresh(this);
        }
    }

    @Override
    protected void footReboundRelease() {
        onLastItemVisibleEx();
    }

    @Override
    public void onRefreshComplete() {
        onRefreshComplete(AutoListView.COMPLETE_MODE_NO_MORE_DATA);
    }

    public void onRefreshComplete(int status) {
        super.onRefreshComplete();
        hasLoadAllData = status == AutoListView.COMPLETE_MODE_NO_MORE_DATA;
        isOnAutoLoading = false;
        if (autoListView != null) {
            int currentItemCount = autoListView.getAdapter() != null ? autoListView.getAdapter().getCount() - (autoListView.getHeaderViewsCount() + autoListView.getFooterViewsCount()) : 0;
//            Log.d("AutoListView", "count = " + autoListView.getAdapter().getCount());
            autoListView.reset(status, currentItemCount > 0);
        }
    }

    class InternalAutoListView extends AutoListView implements EmptyViewMethodAccessor {

        public InternalAutoListView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public void setEmptyView(View emptyView) {
            PullToRefreshAutoListView.this.setEmptyView(emptyView);
        }

        @Override
        public void setEmptyViewInternal(View emptyView) {
            super.setEmptyView(emptyView);
        }
    }

    @TargetApi(9)
    final class InternalAutoListViewSDK9 extends InternalAutoListView {

        public InternalAutoListViewSDK9(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
                                       int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

            final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
                    scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

            // Does all of the hard work...
            OverscrollHelper.overScrollBy(PullToRefreshAutoListView.this, deltaX, scrollX, deltaY, scrollY, isTouchEvent);

            return returnValue;
        }
    }
}
