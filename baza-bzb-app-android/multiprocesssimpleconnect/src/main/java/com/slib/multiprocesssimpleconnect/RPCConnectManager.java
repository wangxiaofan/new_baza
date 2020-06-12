package com.slib.multiprocesssimpleconnect;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;

import com.slib.aidl.IAppProcessService;

import java.util.List;

/**
 * Created by Vincent.Lei on 2019/7/4.
 * Title：
 * Note：
 */
public class RPCConnectManager {
    private static final String SERVICE_ACTION = "COM_SLIB_RPC_ROUTER_SERVICE_ACTION";

    private RPCConnectManager() {
    }

    private static final RPCConnectManager mInstance = new RPCConnectManager();

    public static RPCConnectManager getInstance() {
        return mInstance;
    }

    private RPCConnectWrapper mWrapper = new RPCConnectWrapper();
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mWrapper.onServiceConnected(IAppProcessService.Stub.asInterface(service));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (mWrapper != null) {
                mWrapper.onServiceDisconnected();
            }
        }
    };

    public void startAndConnectRPCRouterService(Context context, String processId, IRPCConnectionHandler connectionHandler) {
        context.startService(new Intent(context, RPCRouterService.class));
        connect(context, processId, connectionHandler);
    }

    public void connect(Context context, String processId, IRPCConnectionHandler connectionHandler) {
        mWrapper.setConnectionHandler(connectionHandler);
        mWrapper.setProcessId(processId);
        Intent intentService = new Intent(SERVICE_ACTION);
        intentService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentService.setPackage(context.getPackageName());
        Intent explicitIntent = createExplicitFromImplicitIntent(context, intentService);
        if (explicitIntent != null)
            context.bindService(explicitIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void disConnect(Context context) {
        mWrapper.readyToDisConnect();
        if (mServiceConnection != null)
            context.unbindService(mServiceConnection);
    }

    public String doRPCRequireSync(String toProcessId, String action, String data) {
        return doRPCRequire(toProcessId, action, data, true, true, null);
    }

    public void doRPCRequireAsync(String toProcessId, String action, String data, boolean needReply, RPCConnectWrapper.IRPCReplyListener listener) {
        doRPCRequire(toProcessId, action, data, needReply, false, listener);
    }

    public void doRPCReply(RPCConnectWrapper.RPCRequireHolder holder, String data) {
        mWrapper.doRPCReply(holder, data);
    }

    private String doRPCRequire(String toProcessId, String action, String data, boolean needReply, boolean syncWaitResult, RPCConnectWrapper.IRPCReplyListener listener) {
        return mWrapper.doRPCRequire(toProcessId, action, data, needReply, syncWaitResult, listener);
    }


    private static Intent createExplicitFromImplicitIntent(Context context, Intent
            implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }
        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);
        // Set the component to be explicit
        explicitIntent.setComponent(component);
        return explicitIntent;
    }
}
