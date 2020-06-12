// IAppProcessService.aidl
package com.slib.aidl;

// Declare any non-default types here with import statements

interface IAppProcessConnection {
  String onRPCConnectionAction(String fromProcessId,String requireId,String action,String data,boolean needReply,boolean syncWaitResult);
  void onRPCReplyAsync(String requireId,String action,String data);
}
