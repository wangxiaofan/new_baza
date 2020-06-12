package com.tencent.qcloud.tim.uikit;


import com.tencent.qcloud.tim.uikit.bean.IMCheckInnerRequestBean;
import com.tencent.qcloud.tim.uikit.bean.IMCheckInnerResponseBean;
import com.tencent.qcloud.tim.uikit.bean.IMCheckOnlineRequestBean;
import com.tencent.qcloud.tim.uikit.bean.IMCheckOnlineResponseBean;
import com.tencent.qcloud.tim.uikit.bean.IMGetExternalRequestBean;
import com.tencent.qcloud.tim.uikit.bean.IMGetExternalResponseBean;
import com.tencent.qcloud.tim.uikit.bean.IMGetMessageRequestBean;
import com.tencent.qcloud.tim.uikit.bean.IMGetMessageResponsetBean;
import com.tencent.qcloud.tim.uikit.bean.IMGetNameRequestBean;
import com.tencent.qcloud.tim.uikit.bean.IMGetNameResponseBean;
import com.tencent.qcloud.tim.uikit.bean.IMGetTeamMembersResponseBean;
import com.tencent.qcloud.tim.uikit.bean.IMGetTeamsResponseBean;
import com.tencent.qcloud.tim.uikit.bean.IMSearchBean;
import com.tencent.qcloud.tim.uikit.bean.IMSearchDetailsBean;
import com.tencent.qcloud.tim.uikit.bean.IMSearchDetailsRequestBean;
import com.tencent.qcloud.tim.uikit.bean.IMSearchRequestBean;
import com.tencent.qcloud.tim.uikit.bean.IMSendManySystemMessageRequestBean;
import com.tencent.qcloud.tim.uikit.bean.IMSendManySystemMessageResponseBean;
import com.tencent.qcloud.tim.uikit.bean.IMSendSystemMessageRequestBean;
import com.tencent.qcloud.tim.uikit.bean.IMSendSystemMessageResponseBean;
import com.tencent.qcloud.tim.uikit.modules.conversation.base.ConversationInfo;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by Vincent.Lei on 2017/5/9.
 * Title：URL常量
 * Note：
 */

public interface URLConst {

    //IM相关
    String BASE_URL = "https://api.stg.bazhua.me/api/imcenter/";
    String URL_GET_SIG = BASE_URL + "/im/create/user/sig";//获取sig
    String URL_ICON = BASE_URL + "/direct/avatar/user/";//头像
    String URL_ICON_GROUP = BASE_URL + "/direct/avatar/group/";//群组头像

    String search = "im/es/search";//搜索接口
    String searchDetails = "im/es/get/message/page";//搜索详情接口
    String searchInner = "im/check/inner";//查询是否是内部成员
    String getIMName = "im/batch/get/name";//批量获取名称
    String checkOnline = "im/query/state";//获取是否在线
    String getTeams = "im/myFirm/usersFirmTeams";//获取公司团队
    String getTeamMembers = "im/myFirm/getTeamMembers";//获取团队成员
    String getExternalMembers = "im/search/externalMembers";//获取外部成员
    String getHistoryMessage = "im/get/message/page";//获取历史消息
    String sendSystemMessage = "im/message/system/notification";//发送系统消息
    String sendManySystemMessage = "im/message/system/batch/notification";//批量发送系统消息

    @POST
    Call<IMSearchBean> search(@Url String url, @Body IMSearchRequestBean requestBody);

    @POST
    Call<IMSearchDetailsBean> searchDetails(@Url String url, @Body IMSearchDetailsRequestBean requestBody);

    @POST
    Call<IMCheckInnerResponseBean> searchInner(@Url String url, @Body IMCheckInnerRequestBean requestBody);

    @POST
    Call<IMGetNameResponseBean> getIMName(@Url String url, @Body IMGetNameRequestBean requestBody);

    @POST
    Call<IMCheckOnlineResponseBean> checkOnline(@Url String url, @Body IMCheckOnlineRequestBean requestBody);

    @POST
    Call<IMGetTeamsResponseBean> getTeams(@Url String url);

    @FormUrlEncoded
    @POST
    Call<IMGetTeamMembersResponseBean> getTeamMembers(@Url String url, @FieldMap Map<String, String> params);

    @POST
    Call<IMGetExternalResponseBean> getExternalMembers(@Url String url, @Body IMGetExternalRequestBean requestBody);

    @POST
    Call<IMGetMessageResponsetBean> getIMMessages(@Url String url, @Body IMGetMessageRequestBean requestBody);


    @POST
    Call<IMSendSystemMessageResponseBean> sendSystemMessage(@Url String url, @Body IMSendSystemMessageRequestBean requestBody);

    @POST
    Call<IMSendManySystemMessageResponseBean> sendManySystemMessage(@Url String url, @Body IMSendManySystemMessageRequestBean requestBody);
}
