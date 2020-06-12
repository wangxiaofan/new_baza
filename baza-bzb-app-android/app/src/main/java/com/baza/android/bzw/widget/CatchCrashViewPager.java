package com.baza.android.bzw.widget;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CatchCrashViewPager extends ViewPager {


	public CatchCrashViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CatchCrashViewPager(Context context) {
		super(context);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		try {
			return super.onInterceptTouchEvent(arg0);
		} catch (Exception e) {
			return false;
		}

	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		try {
			return super.onTouchEvent(arg0);
		} catch (Exception ex) {
			return false;
		}

	}
	
	
}
