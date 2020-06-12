package com.baza.track.io.core;

import android.app.Application;
import android.os.Handler;
import android.os.HandlerThread;

import com.baza.track.io.bean.TrackEventBean;
import com.baza.track.io.constant.TrackConst;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Vincent.Lei on 2018/4/9.
 * Title：
 * Note：
 */
public class UploadService {
    private static final int UPLOAD_TYPE_NONE = 0;
    private static final int UPLOAD_TYPE_PAGE = 1;
    private static final int UPLOAD_TYPE_CLICK = 2;

    private static final int RETRY_TIME = 3;

    //    private static final String USER_NAME = "client";
//    private static final String PASSWORD = "47!1a6*44d$5f%2b";
    private static final String AUTH = "Basic Y2xpZW50OjQ3ITFhNio0NGQkNWYlMmI=";
    private static UploadService mInstance = new UploadService();
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private Application mApplication;
    private UploadTask mUploadTask;
    private static final int BATCH_COUNT = 30;
    private static final long DELAY_TIME_LONG = 60 * 1000;
    private static final long DELAY_TIME_NORMAL = 30 * 1000;
    private static final int MAX_EMPTY_COUNT = 6;
    private int mCurrentUploadType = UPLOAD_TYPE_PAGE;
    private int mEmptyReadCount;

    public static UploadService getInstance() {
        return mInstance;
    }

    private UploadService() {
        mHandlerThread = new HandlerThread("UploadService");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    public void upload(Application application) {
        mApplication = application;
        prepareToUpload();
    }

    private void prepareToUpload() {
        mCurrentUploadType = (mCurrentUploadType == UPLOAD_TYPE_NONE ? UPLOAD_TYPE_PAGE : mCurrentUploadType);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                readEvents((mCurrentUploadType == UPLOAD_TYPE_PAGE ? TrackConst.EventType.EVENT_TYPE_PAGE_REVIEW : TrackConst.EventType.EVENT_TYPE_CLICK));
            }
        });
    }

    private void onCurrentTypeAllUpload() {
        switch (mCurrentUploadType) {
            case UPLOAD_TYPE_NONE:
                mCurrentUploadType = UPLOAD_TYPE_PAGE;
                break;
            case UPLOAD_TYPE_PAGE:
                mCurrentUploadType = UPLOAD_TYPE_CLICK;
                break;
            default:
                mEmptyReadCount++;
                if (mEmptyReadCount > MAX_EMPTY_COUNT)
                    mEmptyReadCount = 1;
                mCurrentUploadType = UPLOAD_TYPE_NONE;
                TrackLog.d("all logs has upload and delay to upload in time :" + DELAY_TIME_NORMAL * mEmptyReadCount);
                break;
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                prepareToUpload();
            }
        }, mCurrentUploadType == UPLOAD_TYPE_NONE ? DELAY_TIME_NORMAL * mEmptyReadCount : 0);
    }

    private void onCurrentBatchUploadSuccess(List<TrackEventBean> mList) {
        deleteEvent(mList);
    }

    private void onCurrentBatchUploadFailed(int failedCount) {
        TrackLog.d("this batch logs upload failed count :" + failedCount);
        if (failedCount >= RETRY_TIME) {
            TrackLog.d("this batch logs upload over max retry time");
            //重新执行提交任务
            mUploadTask = null;
            mCurrentUploadType = UPLOAD_TYPE_NONE;
            TrackLog.d("this batch logs upload failed and delay to upload in time :" + DELAY_TIME_LONG);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    prepareToUpload();
                }
            }, DELAY_TIME_LONG);
            return;
        }
        if (mUploadTask == null)
            return;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mUploadTask.reTry();
            }
        }, failedCount * 3000);
    }

    private void readEvents(String types) {
        DbIO.getInstance(mApplication).readEvents(BATCH_COUNT, types, new DbIO.WorkRunnable.IDBReply<List<TrackEventBean>>() {
            @Override
            public void onDBReply(List<TrackEventBean> result) {
                if (result != null && !result.isEmpty()) {
                    mEmptyReadCount = 0;
                    if (mUploadTask == null)
                        mUploadTask = new UploadTask(result);
                    else
                        mUploadTask.refreshTask(result);
                    mHandler.post(mUploadTask);
                    return;
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onCurrentTypeAllUpload();
                    }
                });
            }
        });
    }

    private void deleteEvent(List<TrackEventBean> list) {
        DbIO.getInstance(mApplication).deleteEvents(list, new DbIO.WorkRunnable.IDBReply<Void>() {
            @Override
            public void onDBReply(Void result) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        prepareToUpload();
                    }
                });
            }
        });
    }

    private class UploadTask implements Runnable {
        private List<TrackEventBean> mList;
        private int mFailedCount;
        private boolean mSuccess;
        private Runnable mSuccessRunnable = new Runnable() {
            @Override
            public void run() {
                onCurrentBatchUploadSuccess(mList);
            }
        };

        private Runnable mFailedRunnable = new Runnable() {
            @Override
            public void run() {
                onCurrentBatchUploadFailed(mFailedCount);
            }
        };

        UploadTask(List<TrackEventBean> list) {
            this.mList = list;
        }

        public void refreshTask(List<TrackEventBean> list) {
            this.mList = list;
            this.mFailedCount = 0;
        }

        public void reTry() {
            run();
        }

        @Override
        public void run() {
            //上传到服务器
            mSuccess = false;
            HttpURLConnection conn = null;
            OutputStream os = null;
            InputStream in = null;
            try {
                String api = (TrackConst.EventType.EVENT_TYPE_CLICK.equals(mList.get(0).type) ? TrackConst.URL_CLICK : TrackConst.URL_PAGE);
                JSONArray jsonArray = new JSONArray();
                for (TrackEventBean data : mList) {
                    jsonArray.put(new JSONObject(data.value));
                }
                //init
                URL url = new URL(api);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-type", "application/json-patch+json");
                conn.setRequestProperty("Auth", AUTH);
                conn.setUseCaches(false);
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(10000);
                conn.connect();
                //push out
                os = conn.getOutputStream();
                os.write(jsonArray.toString().getBytes("UTF-8"));
                os.flush();
                os.close();
                os = null;
                //get back
                if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                    in = conn.getInputStream();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byte[] buff = new byte[1024];
                    int len;
                    while ((len = in.read(buff)) > 0)
                        byteArrayOutputStream.write(buff, 0, len);
                    in.close();
                    JSONObject jsonObject = new JSONObject(new String(byteArrayOutputStream.toByteArray(), "UTF-8"));
                    mSuccess = jsonObject.getBoolean("success");
                }


            } catch (Exception e) {
                mSuccess = false;
                e.printStackTrace();
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (conn != null)
                    conn.disconnect();
                mFailedCount = (mSuccess ? mFailedCount : mFailedCount + 1);
                mHandler.post(mSuccess ? mSuccessRunnable : mFailedRunnable);
            }


        }
    }
}
