// IAppProcessService.aidl
package com.slib.aidl;
import com.slib.aidl.IAppProcessConnection;
// Declare any non-default types here with import statements

interface IAppProcessService {
  String onRPCConnectionAction(String fromProcessId,String toProcessId,String requireId,String action,String data,boolean needReply,boolean syncWaittingResult);
  void onRPCReplyAsync(String toProcessId,String requireId,String action,String data);
  void registerAppProcessConnection(String processId,IAppProcessConnection connection);
  void unregisterAppProcessConnection(String processId,IAppProcessConnection connection);
}
