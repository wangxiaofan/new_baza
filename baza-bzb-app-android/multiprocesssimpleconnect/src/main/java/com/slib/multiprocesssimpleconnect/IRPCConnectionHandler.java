package com.slib.multiprocesssimpleconnect;

/**
 * Created by Vincent.Lei on 2019/7/4.
 * Title：
 * Note：
 */
public interface IRPCConnectionHandler {
    void onRPCRouterServiceConnected();

    void onRPCRouterServiceDisConnected();

    boolean shouldRPCEventCostByUIThread(String requireId, String action);

    String onRPCConnectionAction(RPCConnectWrapper.RPCRequireHolder holder);
}
