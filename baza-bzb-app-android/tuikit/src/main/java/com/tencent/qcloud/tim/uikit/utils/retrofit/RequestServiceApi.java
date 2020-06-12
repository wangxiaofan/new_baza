//package com.tencent.qcloud.tim.uikit.utils.retrofit;
//
//import com.pro.recovery.entity.AddNoteEntity;
//import com.pro.recovery.entity.CancelOrdersEntity;
//import com.pro.recovery.entity.ChangePasswordEntity;
//import com.pro.recovery.entity.CompanyEntity;
//import com.pro.recovery.entity.ConfirmOrderEntity;
//import com.pro.recovery.entity.EvaluateDetailsEntity;
//import com.pro.recovery.entity.EvaluateEntity;
//import com.pro.recovery.entity.ForwardOrderEntity;
//import com.pro.recovery.entity.GetOrderNumEntity;
//import com.pro.recovery.entity.GetTencentTokenEntity;
//import com.pro.recovery.entity.InCompanyEntity;
//import com.pro.recovery.entity.LoginEntity;
//import com.pro.recovery.entity.MyManagerEntity;
//import com.pro.recovery.entity.MySubordinatesEntity;
//import com.pro.recovery.entity.OrderDetailsEntity;
//import com.pro.recovery.entity.OrderListEntity;
//import com.pro.recovery.entity.RecoveryDetailsEntity;
//import com.pro.recovery.entity.SmsEntity;
//import com.pro.recovery.entity.UploadImaEntity;
//import com.pro.recovery.entity.UserInformationEntity;
//
//import java.util.Map;
//
//import io.reactivex.Observable;
//import okhttp3.ResponseBody;
//import retrofit2.http.FieldMap;
//import retrofit2.http.FormUrlEncoded;
//import retrofit2.http.GET;
//import retrofit2.http.POST;
//import retrofit2.http.Path;
//import retrofit2.http.Query;
//import retrofit2.http.Streaming;
//
//public interface RequestServiceApi {
//
//    //公共参数
//    String TestCode = "test_pass_s8d7ft9o827t3rp92q";
//    String ContentType = "application/x-www-form-urlencoded";
//    String client_id = "webApp";
//    String client_secret = "webApp";
//
//    //根路径
//    String baseUrl = "http://120.55.240.60:9200/";
//    //获取验证码
//    String getCode = "api-auth/sms/send";
//    //短信登录
//    String smsLogin = "api-auth/authentication/sms";
//    //密码登录
//    String passwordLogin = "api-auth/oauth/user/token";
//    //用户信息
//    String getUserInformation = "api-auth/oauth/userinfo";
//    //订单列表
//    String getOrderList = "api-personal/stafforderinfo/staffInitial";
//    //获取token
//    String getToken = "api-thirdparty/tencentcloudapi";
//    //我的公司
//    String myCompany = "api-facilitatorEmployeeManager/staffinfo/showEmployeeCompany";
//    //认证完成信息导入
//    String sendInfo = "api-thirdparty/identityentry";
//    //服务评分
//    String getEvaluate = "api-recyclebusiness/staffinfoevaluation/servicerating";
//    //好评
//    String getGoodEvaluate = "api-recyclebusiness/staffinfoevaluation/praise";
//    //中评
//    String getMiddleEvaluate = "api-recyclebusiness/staffinfoevaluation/average";
//    //差评
//    String getBadEvaluate = "api-recyclebusiness/staffinfoevaluation/badreview";
//    //获取待确认订单详情
//    String getOrderDetails = "/api-personal/stafforderinfo/";
//    //修改密码
//    String changePassword = "api-user/users/resetPassword";
//    //上传头像
//    String uploadIcon = "api-user/users/updateheadImg";
//    //对订单添加备注
//    String orderAddNote = "api-recyclebusiness/staffinfoevaluation/addnotes";
//    //确认接单
//    String confirmOrder = "api-recyclebusiness/recyclebusiness/confirmtheorder";
//    //取消任务
//    String cancelOrder = "api-recyclebusiness/recyclebusiness/canceltask";
//    //转发订单
//    String forwardOrder = "api-recyclebusiness/recyclebusiness/orderrouting";
//    //获取已转派订单列表
//    String getForwardList = "api-personal/stafforderinfo/redeployInitial";
//    //获取已取消订单列表
//    String getCancelOrder = "api-personal/stafforderinfo/ordercancelled";
//    //获取已取消任务列表
//    String getCancelTask = "api-personal/stafforderinfo/taskcancelled";
//    //已取消订单详情
//    String cancelOrderDetails = "api-personal/stafforderinfo/staffAccept";
//    //我的下属人员
//    String getMySubordinates = "api-recyclebusiness/staffinfoevaluation/chakansubordinate";
//    //查看生活垃圾
//    String getRecoveryDetails = "api-personal/recoverygoodsclass/selectLifeGarbage";
//    //确认接单
//    String confirmReceipt = "api-recyclebusiness/recyclebusiness/appordercompleted";
//    //获取下属人员
//    String getSubordinate = "api-recyclebusiness/staffinfoevaluation/chakansubordinate";
//    //获取预付款订单号
//    String getPayOrderNum = "api-recyclebusiness/getwxcode";
//    //绑定
//    String bindWx = "api-recyclebusiness/bindwxappopenid";
//    //删除备注
//    String delNote = "api-recyclebusiness/staffinfoevaluation/deletenotes";
//    //我的业务经理
//    String MyManager = "api-recyclebusiness/staffinfoevaluation/chakanboss";
//    //已取消订单&已取消任务
//    String getCancelDetails = "api-recyclebusiness/staffinfoevaluation/";
//    //回收人员入住服务商
//    String inCompany = "api-facilitatorEmployeeManager/staffinfo/enterFacilitator";
//    //扫码回收获取订单详情
//    String getOrderDetailsFromScan = "api-personal/wdinfo/appSelectLifeGarbage";
//    //扫码回收完成订单
//    String scanFinishOrder = "api-personal/wdinfo/appWdOrder";
//    //获取小程序ID
//    String getMiniID = "api-personal/wdinfo/getSmallId";
//
//    @FormUrlEncoded
//    @POST(getMiniID)
//    Observable<ChangePasswordEntity> getMiniID(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(scanFinishOrder)
//    Observable<ConfirmOrderEntity> scanFinishOrder(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(getOrderDetailsFromScan)
//    Observable<RecoveryDetailsEntity> getOrderDetailsFromScan(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(inCompany)
//    Observable<InCompanyEntity> inCompany(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(getCancelDetails + "{type}")
//    Observable<OrderDetailsEntity> getCancelDetails(@Path("type") String type, @Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(MyManager)
//    Observable<MyManagerEntity> MyManager(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(delNote)
//    Observable<ChangePasswordEntity> delNote(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(bindWx)
//    Observable<GetOrderNumEntity> bindWx(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(getPayOrderNum)
//    Observable<GetOrderNumEntity> getPayOrderNum(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(getSubordinate)
//    Observable<RecoveryDetailsEntity> getSubordinate(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(confirmReceipt)
//    Observable<ConfirmOrderEntity> confirmReceipt(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(getRecoveryDetails)
//    Observable<RecoveryDetailsEntity> getRecoveryDetails(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(getMySubordinates)
//    Observable<MySubordinatesEntity> getMySubordinates(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(cancelOrderDetails)
//    Observable<CancelOrdersEntity> cancelOrderDetails(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(getCancelTask)
//    Observable<OrderListEntity> getCancelTask(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(getCancelOrder)
//    Observable<OrderListEntity> getCancelOrder(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(getForwardList)
//    Observable<ForwardOrderEntity> getForwardList(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(forwardOrder)
//    Observable<ChangePasswordEntity> forwardOrder(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(cancelOrder)
//    Observable<ChangePasswordEntity> cancelOrder(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(confirmOrder)
//    Observable<ConfirmOrderEntity> confirmOrder(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(orderAddNote)
//    Observable<AddNoteEntity> orderAddNote(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(uploadIcon)
//    Observable<UploadImaEntity> uploadIcon(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(changePassword)
//    Observable<ChangePasswordEntity> changePassword(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(getOrderDetails + "{type}")
//    Observable<OrderDetailsEntity> getOrderDetails(@Path("type") String type, @Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @POST(getCode)
//    Observable<SmsEntity> getCode(@Query("mobile") String mobile);
//
//    @FormUrlEncoded
//    @POST(smsLogin)
//    Observable<LoginEntity> smsLogin(@FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(passwordLogin)
//    Observable<LoginEntity> passwordLogin(@FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(getUserInformation)
//    Observable<UserInformationEntity> getUserInformation(@FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(getOrderList)
//    Observable<OrderListEntity> getOrderList(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @POST(getToken)
//    Observable<GetTencentTokenEntity> getTencentToken(@Query("access_token") String access_token);
//
//    @FormUrlEncoded
//    @POST(myCompany)
//    Observable<CompanyEntity> getMyCompany(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(sendInfo)
//    Observable<SendInfoEntity> sendInfo(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(getEvaluate)
//    Observable<EvaluateEntity> getEvaluate(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(getGoodEvaluate)
//    Observable<EvaluateDetailsEntity> getGoodEvaluate(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(getMiddleEvaluate)
//    Observable<EvaluateDetailsEntity> getMiddleEvaluate(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST(getBadEvaluate)
//    Observable<EvaluateDetailsEntity> getBadEvaluate(@Query("access_token") String access_token, @FieldMap Map<String, String> params);
//
//    @Streaming
//    @GET("{name}")
//    Observable<ResponseBody> downLoadIcon(@Path("name") String name);
//}
