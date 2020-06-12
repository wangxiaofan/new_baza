package com.slib.utils;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by LW on 2016/9/9.
 * Title : 软键盘展开关闭监听
 * Note :
 */
public class KeyBoardHelper implements View.OnLayoutChangeListener {


    public interface IKeyBoardListener {
        void onKeyBoardOpen();

        void onKeyBoardClosed();
    }


    private Activity activity;
    private IKeyBoardListener iKeyBoardListener;
    private int keyboardHeight;

    public KeyBoardHelper(Activity activity, IKeyBoardListener iKeyBoardListener) {
        this.activity = activity;
        this.iKeyBoardListener = iKeyBoardListener;
        this.keyboardHeight = ScreenUtil.screenHeight / 4;
    }


    public static void addKeyBoardOpenOrClosedListener(Activity activity, IKeyBoardListener iKeyBoardListener) {
        new KeyBoardHelper(activity, iKeyBoardListener).watchChanged();
    }


    private void watchChanged() {
//        activity.findViewById((id <= 0 ? android.R.id.content : id)).addOnLayoutChangeListener(this);
        FrameLayout content = activity.findViewById(android.R.id.content);
        content.getChildAt(0).addOnLayoutChangeListener(this);
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        //现在认为只要控件将Activity向上推的高度超过了keyboardHeight高，就认为软键盘弹起
        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyboardHeight)) {
            iKeyBoardListener.onKeyBoardOpen();

        } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyboardHeight)) {
            iKeyBoardListener.onKeyBoardClosed();
        }
    }
}
