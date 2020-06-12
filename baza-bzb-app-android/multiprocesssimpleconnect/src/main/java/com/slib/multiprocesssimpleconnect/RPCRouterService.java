package com.slib.multiprocesssimpleconnect;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;

import com.slib.aidl.IAppProcessConnection;
import com.slib.aidl.IAppProcessService;

import java.util.HashMap;

/**
 * Created by Vincent.Lei on 2019/7/4.
 * Title：
 * Note：
 */
public class RPCRouterService extends Service {

    private class AppBinder extends IAppProcessService.Stub {
        private final HashMap<String, RemoteCallbackList<IAppProcessConnection>> mCallBackMap = new HashMap<>();

        @Override
        public String onRPCConnectionAction(String fromProcessId, String toProcessId, String requireId, String action, String data, boolean needReply, boolean syncWaitResult) throws RemoteException {
//            if (TextUtils.isEmpty(toProcessId)) {
//                Set<Map.Entry<String, RemoteCallbackList<IAppProcessConnection>>> set = mCallBackMap.entrySet();
//                Iterator<Map.Entry<String, RemoteCallbackList<IAppProcessConnection>>> iterator = set.iterator();
//                RemoteCallbackList<IAppProcessConnection> mCallbackList;
//                while (iterator.hasNext()) {
//                    mCallbackList = iterator.next().getValue();
//                    final int N = mCallbackList.beginBroadcast();
//                    for (int i = 0; i < N; i++) {
//                        try {
//                            mCallbackList.getBroadcastItem(i).onRPCConnectionAction(fromProcessId, requireId, action, data, false, false);
//                        } catch (RemoteException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    mCallbackList.finishBroadcast();
//                }
//                return null;
//            }
            if (TextUtils.isEmpty(toProcessId))
                return null;
            RemoteCallbackList<IAppProcessConnection> mCallbackList = mCallBackMap.get(toProcessId);
            final int N = mCallbackList.beginBroadcast();
            String result = null;
            if (N > 0)
                result = mCallbackList.getBroadcastItem(0).onRPCConnectionAction(fromProcessId, requireId, action, data, needReply, syncWaitResult);
            mCallbackList.finishBroadcast();
            return result;
        }

        @Override
        public void onRPCReplyAsync(String toProcessId, String requireId, String action, String data) throws RemoteException {
            if (TextUtils.isEmpty(toProcessId))
                return;
            RemoteCallbackList<IAppProcessConnection> mCallbackList = mCallBackMap.get(toProcessId);
            final int N = mCallbackList.beginBroadcast();
            if (N > 0)
                mCallbackList.getBroadcastItem(0).onRPCReplyAsync(requireId, action, data);
            mCallbackList.finishBroadcast();
        }

        @Override
        public void registerAppProcessConnection(String processId, IAppProcessConnection connection) throws RemoteException {
            if (TextUtils.isEmpty(processId) || connection == null)
                return;
            RPCLog.d("registerAppProcessConnection processId：" + processId);
            synchronized (mCallBackMap) {
                RemoteCallbackList<IAppProcessConnection> callBackList = mCallBackMap.get(processId);
                if (callBackList == null) {
                    callBackList = new RemoteCallbackList<>();
                    mCallBackMap.put(processId, callBackList);
                }
                callBackList.register(connection);
            }
        }

        @Override
        public void unregisterAppProcessConnection(String processId, IAppProcessConnection connection) throws RemoteException {
            if (TextUtils.isEmpty(processId) || connection == null)
                return;
            RPCLog.d("unregisterAppProcessConnection processId：" + processId);
            synchronized (mCallBackMap) {
                RemoteCallbackList<IAppProcessConnection> callBackList = mCallBackMap.get(processId);
                if (callBackList != null) {
                    callBackList.unregister(connection);
                }
            }
        }
    }

    private AppBinder mAppBinder = new AppBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mAppBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
