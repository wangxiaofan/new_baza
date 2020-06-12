package com.baza.android.bzw.bean.user;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LW on 2016/icon_collect/5.
 * Title :
 * Note :
 */
public class LoginBean implements Serializable {
    /**
     * {"code":0,
     * "data":
     * {"account":{"avatar":"http://192.168.1.191/noauth/account/avatar?avatar=5026_1471585586378.jpg",
     * "location":530,"email":"vincent.lei@hirede.com",
     * "gender":"M","userId":5026,"mobile":"15602977091","nickName":"你你呵呵","province":192,
     * "registTime":1467710783000,"trueName":"猎大宝SD蜜","userName":"15602977091"
     * }
     * ,"agentType":9,
     * "candidateCount":0,"cid":"1e0578fbc44c0ddcf87e7a83d46269bf",
     * "token":"79cc22945808c37d158e5bf6bc451e4f","userId":5026},"msg":"成功"}
     */
    public UserInfoBean user;
    public String cid;
    public String token;
    public List<String> openIds;
    public String unionid;
    public String imcenterToken;//"Basic aW1jZW50ZXI6aW1jZW50ZXJAIyQyMDIw", //IM中心token
    public String lbdOpenApiToken;//"Basic bGJkb3BlbmFwaTpsYmRvcGVuYXBpQCMkMjAyMA==" //猎必得Open Api token
    //{"code":0,"msg":"成功",
    // "data":{"userId":5026,"userName":"15602977091","cid":"75db3a6ab90f7cfdbe084d938d0e8103","token":"9c3d48e87a516894730b9235a134a173"}}
}
