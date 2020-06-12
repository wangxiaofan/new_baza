package com.baza.android.bzw.businesscontroller.appprocessservice;

import android.content.Context;

import com.baza.android.bzw.constant.RPCConst;
import com.slib.multiprocesssimpleconnect.IRPCConnectionHandler;
import com.slib.multiprocesssimpleconnect.RPCConnectManager;
import com.slib.multiprocesssimpleconnect.RPCConnectWrapper;

/**
 * Created by Vincent.Lei on 2018/4/20.
 * Title：
 * Note：
 */
public class AppProcessService {
//    private class AppProcessBinder extends IAppProcessService.Stub {
//
//        @Override
//        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
//            if (!checkPermission(getCallingUid()))
//                return false;
//            return super.onTransact(code, data, reply, flags);
//        }

//    private boolean checkPermission(int callingUid) {
//        String[] packages = getPackageManager().getPackagesForUid(callingUid);
//        if (packages != null && packages.length > 0) {
//            String packageName = packages[0];
//            return packageName != null && packageName.startsWith("com.bznet.android");
//        }
//        return false;
//    }
//


    public static void start(Context context) {
        RPCConnectManager rpcConnectManager = RPCConnectManager.getInstance();
        final AppProcessHandler appProcessHandler = new AppProcessHandler(rpcConnectManager);
        rpcConnectManager.startAndConnectRPCRouterService(context, RPCConst.RPCProcessId.ID_MAIN, new IRPCConnectionHandler() {
            @Override
            public void onRPCRouterServiceConnected() {

            }

            @Override
            public void onRPCRouterServiceDisConnected() {

            }

            @Override
            public boolean shouldRPCEventCostByUIThread(String requireId, String action) {
                return appProcessHandler.shouldRPCEventCostByUIThread(requireId, action);
            }

            @Override
            public String onRPCConnectionAction(RPCConnectWrapper.RPCRequireHolder holder) {
                return appProcessHandler.onRPCConnectionAction(holder);
            }
        });
    }
}
