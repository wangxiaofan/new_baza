package com.baza.android.bzw.businesscontroller.publish.presenter;


import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by LW on 2016/icon_collect/icon_collect.
 * Title : 到计时帮助类
 * Note :
 */
public class CutDownPresenter {
    private int totalSecond;
    private int amountSecond;
    private Timer timer;
    private ICallBack callBack;


    public interface ICallBack {
        void onTimeCut(int amountSecond);
    }


    public CutDownPresenter(int totalSecond, ICallBack callBack) {
        this.totalSecond = totalSecond;
        this.callBack = callBack;
    }

    public void start() {

        if (callBack == null)
            return;
        if (timer == null)
            timer = new Timer();
        this.amountSecond = totalSecond;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    callBack.onTimeCut(amountSecond);
                } catch (Exception e) {
                }

                amountSecond--;
                if (amountSecond < -1) {
                    timer.cancel();
                    timer = null;
                }

            }
        }, 0, 1000);
    }


    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

}
