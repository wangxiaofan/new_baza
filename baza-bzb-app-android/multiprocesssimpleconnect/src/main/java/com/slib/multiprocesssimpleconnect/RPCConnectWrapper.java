package com.slib.multiprocesssimpleconnect;

import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;

import com.slib.aidl.IAppProcessConnection;
import com.slib.aidl.IAppProcessService;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Vincent.Lei on 2019/7/4.
 * Title：
 * Note：
 */
public class RPCConnectWrapper extends IAppProcessConnection.Stub {
    private IAppProcessService mProcessService;
    private String mProcessId;
    private Random mRandom = new Random();
    private IRPCConnectionHandler mConnectionHandler;
    private final HashMap<String, IRPCReplyListener> mWaitReplyMap = new HashMap<>();
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    //    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int CORE_POOL_SIZE = 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 5;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(8);
    private ThreadPoolExecutor mThreadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS, sPoolWorkQueue);

    void setProcessId(String processId) {
        this.mProcessId = processId;
    }

    void onServiceConnected(IAppProcessService processService) {
        RPCLog.d("onServiceConnected ：" + mProcessId);
        this.mProcessService = processService;
        try {
            this.mProcessService.registerAppProcessConnection(mProcessId, this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (mConnectionHandler != null)
            mConnectionHandler.onRPCRouterServiceConnected();

    }

    void onServiceDisconnected() {
        RPCLog.d("onServiceDisconnected ：" + mProcessId);
        mProcessService = null;
        mHandler.removeCallbacksAndMessages(null);
        if (mConnectionHandler != null)
            mConnectionHandler.onRPCRouterServiceDisConnected();
    }

    void readyToDisConnect() {
        try {
            this.mProcessService.unregisterAppProcessConnection(mProcessId, this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    void setConnectionHandler(IRPCConnectionHandler connectionHandler) {
        this.mConnectionHandler = connectionHandler;
    }

    private boolean isConnected() {
        return mProcessService != null;
    }

    void doRPCReply(RPCRequireHolder holder, String data) {
        if (!isConnected())
            return;
        if (holder != null && holder.needReplyAsync)
            try {
                mProcessService.onRPCReplyAsync(holder.fromProcessId, holder.requireId, holder.action, data);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
    }

    String doRPCRequire(String toProcessId, String action, String data, boolean needReply, boolean syncWaitResult, IRPCReplyListener listener) {
        if (!isConnected())
            return null;
        String requireId = UUID.randomUUID().toString().replaceAll("-", "") + mRandom.nextInt(10000);
        if (listener != null && !syncWaitResult && needReply) {
            mWaitReplyMap.put(requireId, listener);
        }
        try {
            return mProcessService.onRPCConnectionAction(mProcessId, toProcessId, requireId, action, data, needReply, syncWaitResult);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String onRPCConnectionAction(String fromProcessId, String requireId, String action, String data, boolean needReply, boolean syncWaitResult) throws RemoteException {
        RPCLog.d("onRPCConnectionAction requireId：" + requireId + "  action：" + action + "  data：" + data);
        if (mConnectionHandler == null)
            return null;
        final RPCRequireHolder holder = new RPCRequireHolder(fromProcessId, requireId, action, data, needReply);
        if (!syncWaitResult) {
            if (mConnectionHandler.shouldRPCEventCostByUIThread(requireId, action)) {
                RPCLog.d("onRPCConnectionAction sync to uiThread requireId：" + requireId);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mConnectionHandler.onRPCConnectionAction(holder);
                    }
                });
                return null;
            }
            RPCLog.d("onRPCConnectionAction sync to ThreadPool requireId：" + requireId);
            mThreadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    mConnectionHandler.onRPCConnectionAction(holder);
                }
            });
            return null;
        }
        holder.needReplyAsync = false;
        RPCLog.d("onRPCConnectionAction wait until result back requireId：" + requireId);
        return mConnectionHandler.onRPCConnectionAction(holder);
    }

    @Override
    public void onRPCReplyAsync(final String requireId, String action, final String data) throws RemoteException {
        RPCLog.d("onRPCReplyAsync requireId：" + requireId + "  action：" + action + "  data：" + data);
        if (TextUtils.isEmpty(requireId))
            return;
        synchronized (mWaitReplyMap) {
            final IRPCReplyListener listener = mWaitReplyMap.remove(requireId);
            if (listener == null)
                return;
            if (!listener.syncToUIThread()) {
                listener.onRPCReply(requireId, data);
                return;
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onRPCReply(requireId, data);
                }
            });
        }
    }

    public static class RPCRequireHolder {
        private String fromProcessId;
        public String requireId;
        public String action;
        public String data;
        public boolean needReplyAsync;

        private RPCRequireHolder(String fromProcessId, String requireId, String action, String data, boolean needReply) {
            this.fromProcessId = fromProcessId;
            this.requireId = requireId;
            this.action = action;
            this.data = data;
            this.needReplyAsync = needReply;
        }

    }

    public interface IRPCReplyListener {
        void onRPCReply(String requireId, String data);

        boolean syncToUIThread();
    }
}
