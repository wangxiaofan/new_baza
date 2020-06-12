package com.baza.android.bzw.constant;


import com.bznet.android.rcbox.BuildConfig;

/**
 * Created by Vincent.Lei on 2017/5/9.
 * Title：URL常量
 * Note：
 */

public class URLConst {
    private URLConst() {
    }

    //服务器环境
    public static String HOST_NAME = BuildConfig.HOST;
    public static String HOST_H5 = BuildConfig.HOST_H5;
    public static String VERSION_NAME = BuildConfig.VERSION_NAME;

    public static final String LINK_RIGHT_RULE = HOST_H5 + "/hybird/mobile/grade/#/taskrule";
    public static final String LINK_H5_RANK = HOST_H5 + "/hybird/mobile/rankmobile#/";
    public static final String LINK_H5_STATISTICS = HOST_H5 + "/hybird/mobile/statistics#/";
    public static final String LINK_H5_GRADE = HOST_H5 + "/hybird/mobile/grade#/";
    public static final String LINK_SHARE_UPDATE_ENGINE = HOST_H5 + "/wechat/advert/bazabox";
    public static final String LINK_EMIAL_SET = HOST_H5 + "/email_sync_help.html";
    public static final String LINK_EMIAL_POP3 = HOST_H5 + "/pop.html";
    public static final String LINK_EMIAL_HELP = "file:///android_asset/link_email_help.html";
    public static final String LINK_INVITE = HOST_H5 + "/inviteArea/desc";

    //百度
    public static final String BAI_DU_ACCESS_TOKEN = "https://openapi.baidu.com/oauth/2.0/token";
    public static final String BAI_DU_VOICE_TO_TEXT = "http://vop.baidu.com/server_api";


    //发送验证码
    public static String URL_SEND_SMS_CODE = HOST_NAME + "/account/getloginsmscode";
    //登录
    public static String URL_LOGIN = HOST_NAME + "/account/login";
    //上传头像
    public static String URL_UPDATE_AVATAR = HOST_NAME + "/account/setavatar";
    //获取用户信息
    public static String URL_GET_USER_PROFILE = HOST_NAME + "/account/profile";
    //检查更新
    public static String URL_CHECK_UPDATE = HOST_NAME + "/version/latest?clientType=android";
    //更新用户信息
    public static String URL_UPDATE_INFO = HOST_NAME + "/account/setprofile";
    //收藏候选人
    public static String URL_COLLECTION_RESUME = HOST_NAME + "/collection/submit";
    //取消收藏候选人
    public static String URL_UN_COLLECTION_RESUME = HOST_NAME + "/collection/cancel";
    //候选人搜索列表
    public static String URL_RESUME_SEARCH_LIST = HOST_NAME + "/candidate/querytalents";
    //新建候选人搜索
    public static String URL_CREATE_NEW_RESUME = HOST_NAME + "/candidate/create";
    //删除候选人
    public static String URL_DELETE_RESUME = HOST_NAME + "/candidate/delete";
    //候选人详情
    public static String URL_RESUME_DETAIL = HOST_NAME + "/candidate/detail";
    //更新新建的候选人
    public static String URL_UPDATE_RESUME = HOST_NAME + "/candidate/update";
    //广告
    public static String URL_ADVERTISEMENT = HOST_NAME + "/pass/adverts";
    //简历排行榜
    public static String URL_MY_RESUME_RANK = HOST_NAME + "/userextension/mytopnew";
    //新增或者修改文字备注
    public static String URL_ADD_REMARK = HOST_NAME + "/candidate/addinquiry";
    //删除文字备注
    public static String URL_DELETE_REMARK = HOST_NAME + "/candidate/deleteinquiry";
    //用户所有标签
    public static String URL_ALL_LABELS = HOST_NAME + "/tag/center/list";
    //创建标签
    public static String URL_CREATE_LABEL = HOST_NAME + "/tag/center/new";
    //删除标签
    public static String URL_DELETE_LABEL = HOST_NAME + "/tag/center/delete";
    //简历绑定标签
    public static String URL_RESUME_BING_LABELS = HOST_NAME + "/tag/center/bind";
    //简历清除标签
    public static String URL_RESUME_CLEAR_LABELS = HOST_NAME + "/tag/center/unbind";
    // 简历导入
    public static String URL_IMPORT_RESUME = HOST_NAME + "/resume/upload";
    //获取绑定的邮箱
    public static String URL_GET_BIND_EMAIL = HOST_NAME + "/account/getboundemail";
    //绑定邮箱
    public static String URL_SET_BIND_EMAIL = HOST_NAME + "/account/bindemail";
    //邮件分享
    public static String URL_EMAIL_SHARE = HOST_NAME + "/share/initiative/shareByMail";
    //获取同步邮箱的列表
    public static String URL_GET_SYNC_EMAIL_RESUME_ACCOUNT_LIST = HOST_NAME + "/email/craw/list";
    //保存同步邮箱的账号
    public static String URL_SAVE_SYNC_EMAIL_RESUME_ACCOUNT = HOST_NAME + "/email/craw/save";
    //邮箱简历立即扒取
    public static String URL_START_SYNC_EMAIL_RESUME = HOST_NAME + "/email/craw/start";
    //删除同步简历的邮箱
    public static String URL_DELETE_SYNC_EMAIL = HOST_NAME + "/email/craw/delete";
    //我的人才
    public static String URL_RESUME_MINE = HOST_NAME + "/candidate/mine";
    //全部公司获取
    public static String URL_GET_ALL_COMPANY = HOST_NAME + "/pass/company/suggest";
    //获取推送的消息列表
    public static String URL_GET_ALL_PUSH_MESSAGE = HOST_NAME + "/notification/type/list";
    //获取等级经验
    public static String URL_GET_GRADE_AND_EXPERIENCE = HOST_NAME + "/userextension/getexperiencerecordnew";
    //获取IM用户信息
    public static String URL_GET_IM_USER_INFO = HOST_NAME + "/account/netease/gets";
    //触发服务端推送
    public static String URL_TOUCH_PUSH = HOST_NAME + "/account/touchpush";
    //获取用户基本信息，包括[分享出][请求到][简历数][排名]
    public static String URL_GET_EXTENSION_INFO = HOST_NAME + "/userextension/statistics";
    //更新文字备注
    public static String URL_UPDATE_REMARK = HOST_NAME + "/candidate/updateinquiry";
    //举报
    public static String URL_REPORT_RESUME = HOST_NAME + "/candidate/problem/report";
    //上传邮件附件
    public static String URL_UPLOAD_EMAIL_ATTACHMENT = HOST_NAME + "/email/fileupload";
    //发送普通邮件
    public static String URL_SEND_NORMAL_EMAIL = HOST_NAME + "/email/send";
    //解绑邮箱
    public static String URL_UN_BIND_EMAIL = HOST_NAME + "/account/unbindemail";
    //微信登录
    public static String URL_WECHAT_LOGIN = HOST_NAME + "/wechat/user/login";
    //绑定手机
    public static String URL_BIND_MOBILE = HOST_NAME + "/wechat/user/bindmobile";
    //可更新人才数目信息
    public static String URL_GET_ENABLE_UPDATE_RESUME_INFO = HOST_NAME + "/updateengine/updatelist/baseinfo";
    //可更新人才列表
    public static String URL_GET_ENABLE_UPDATE_RESUME_LIST = HOST_NAME + "/updateengine/updatelist/search";
    //一键更新
    public static String URL_UPDATE_ONE_KEY = HOST_NAME + "/updateengine/updatelist/search/updates";
    //获取简历更新内容
    public static String URL_GET_UPDATE_CONTENT = HOST_NAME + "/updateengine/candidate/getupdatecontent";
    //更新人才
    public static String URL_UPDATE_RESUME_CONTENT = HOST_NAME + "/updateengine/candidate/doupdate";
    //更新人才反馈
    public static String URL_UPDATE_RESUME_FEED_BACK = HOST_NAME + "/updateengine/candidate/feedback";
    //获取简历更新日志
    public static String URL_GET_UPDATE_RESUME_LOG = HOST_NAME + "/operate/log/loglist";
    //上传简历更新部分类型日志
    public static String URL_SEND_UPDATE_RESUME_LOG = HOST_NAME + "/operate/log/uploadlog";
    //获取已更新简历列表
    public static String URL_GET_ALREADY_UPDATED_LIST = HOST_NAME + "/updateengine/updateloglist/page";
    //获取简历更新记录详情
    public static String URL_GET_RESUME_UPDATED_HISTORY = HOST_NAME + "/updateengine/updateloglist/detail";
    //额度为零时分享
    public static String URL_SHARE_TO_GET_DEFAULT_UPDATE_AMOUNT = HOST_NAME + "/updateengine/candidate/share/nolimit";
    //好友列表
    public static String URL_FRIEND_GET_LIST = HOST_NAME + "/social/friend/list";
    //好友请求列表
    public static String URL_FRIEND_REQUEST_LIST = HOST_NAME + "/social/friend/request/list";
    //添加好友
    public static String URL_FRIEND_ADD = HOST_NAME + "/social/friend/request/add";
    //回复好友
    public static String URL_FRIEND_REQUEST_REPLY = HOST_NAME + "/social/friend/request/reply";
    //搜索好友
    public static String URL_FRIEND_SEARCH = HOST_NAME + "/social/friend/request/search";
    //好友信息
    public static String URL_FRIEND_INFO = HOST_NAME + "/social/friend/baseinfo";
    //好友删除
    public static String URL_FRIEND_DELETE = HOST_NAME + "/social/friend/delete";
    //好友动态
    public static String URL_FRIEND_DYNAMIC = HOST_NAME + "/social/friend/dynamic";
    //指定好友动态
    public static String URL_FRIEND_DYNAMIC_TARGET = HOST_NAME + "/social/friend/dynamic/one";
    //好友推荐
    public static String URL_FRIEND_GET_SUGGEST = HOST_NAME + "/social/friend/recommend/list";
    //通知消息置为已读
    public static String URL_MARK_NOTIFY_READ = HOST_NAME + "/notification/read";
    //删除好友请求记录
    public static String URL_DELETE_FRIEND_RECORD = HOST_NAME + "/social/friend/request/delete";
    //上报位置
    public static String URL_REPORT_USER_LOCATION = HOST_NAME + "/account/location";
    //附近的人
    public static String URL_LIST_NEARLY_FRIEND = HOST_NAME + "/account/location/nearby";
    //默认更新单个人才
    public static String URL_UPDATE_SINGLE_RESUME_DEFAULT = HOST_NAME + "/updateengine/candidate/dodefaultupdate";
    //二维码登录
    public static String URL_QR_CODE_LOGIN = HOST_NAME + "/account/login/qr";
    //获取验证码
    public static String URL_SMS_CODE_NORMAL = HOST_NAME + "/account/getsmscode";
    //修改手机号
    public static String URL_UPDATE_MOBILE = HOST_NAME + "/account/changemobile";
    //用户反馈
    public static String URL_FEED_BACK = HOST_NAME + "/account/feedback";
    //运营banner
    public static String URL_BANNER = HOST_NAME + "/operation/banner/list";
    //检查活动状态
    public static String URL_CHECK_ACTIVITY_STATUS = HOST_NAME + "/operation/getactivitystatus";
    //上传文件到阿里云
    public static String URL_UPLOAD_OSS_FILE = HOST_NAME + "/pass/file/upload";
    //通知为读数
    public static String URL_NOTICE_UN_READ_COUNT = HOST_NAME + "/notification/count";
    //我的简历来源统计列表
    public static String URL_RESUME_CLASSIFY_LIST = HOST_NAME + "/statistics/candidatecount/source/list";
    //智能分组自定义列表
    public static String URL_SMART_GROUP_LIST_SELF_DEFINE = HOST_NAME + "/resume/grouping/custom/group/page";
    //智能分组默认列表
    public static String URL_SMART_GROUP_LIST_DEFAULT = HOST_NAME + "/resume/grouping/intelligent/group";
    //创建智能自定义分组
    public static String URL_CREATE_SELF_DEFINE_GROUP = HOST_NAME + "/resume/grouping/custom/create";
    //修改智能自定义分组
    public static String URL_UPDATE_SELF_DEFINE_GROUP = HOST_NAME + "/resume/grouping/custom/update/group";
    //删除智能自定义分组
    public static String URL_DELETE_SELF_DEFINE_GROUP = HOST_NAME + "/resume/grouping/custom/remove/group";
    //添加简历到智能分组
    public static String URL_ADD_RESUMES_TO_GROUP = HOST_NAME + "/resume/grouping/custom/set/resume";
    //从自定义智能分组移除简历
    public static String URL_REMOVE_RESUMES_FROM_GROUP = HOST_NAME + "/resume/grouping/custom/remove/resume";
    //时间分组时间选项
    public static String URL_TIME_SELECTOR_FROM_GROUP = HOST_NAME + "/resume/grouping/time/groupName";
    //用户权益资产
    public static String URL_USER_RIGHT_BENEFIT = HOST_NAME + "/merculet/uat/asset";
    //商品列表
    public static String URL_GOOD_LIST = HOST_NAME + "/activity/goods/list";
    //商品兑换
    public static String URL_GOOD_EXCHANGE = HOST_NAME + "/activity/order/create";
    //用户资产明细
    public static String URL_BENEFIT_RECORD = HOST_NAME + "/merculet/uat/asset/history";
    //邀请码兑换
    public static String URL_INVITE_CODE_EXCHANGE = HOST_NAME + "/activity/merculet/invitationcode";
    //获取即时消息限制信息
    public static String URL_GET_ILLEGAL_INFO = HOST_NAME + "/netease/limit";
    //汇总的智能分组接口
    public static String URL_SMART_GROUP_COLLECT_INFO = HOST_NAME + "/resume/grouping/intelligent/group/collect";
    //简历在线预览
    public static String URL_RESUME_ONLINE = HOST_NAME + "/document/preview/info";
    //任务卡
    public static String URL_MERCULET_TASK_LIST = HOST_NAME + "/merculet/user/today/event";
    //获取邀请码
    public static String URL_INVITE_CODE = HOST_NAME + "/merculet/client/invitationcode";
    //获取邀请列表
    public static String URL_INVITE_LIST = HOST_NAME + "/merculet/invitation/list/new";
    //获取邀请码图片链接
    public static String URL_INVITE_CODE_IMG_INFO = HOST_NAME + "/activity/merculet/invitationcode/img";
    //检测简历是否是公司库的
    public static String URL_CHECK_RESUME_IS_IN_COMPANY_LIB = HOST_NAME + "/candidate/inquiry/extends";
    //语音备注下载地址
    public static String URL_GET_AUDIO_SOURCE_LOAD_URL = HOST_NAME + "/candidate/inquiry/ossFileLink";
    //创建语音寻访
    public static String URL_CREATE_AUDIO_REMARK_URL = HOST_NAME + "/candidate/inquiry/create/voiceInquiry";
    //添加提醒
    public static String URL_ADD_RECOMMEND = HOST_NAME + "/reminder/add";
    //获取提醒记录
    public static String URL_GET_RECOMMEND_LIST = HOST_NAME + "/reminder/list";
    //设置提醒完成状态
    public static String URL_SET_RECOMMEND_COMPLETE = HOST_NAME + "/reminder/set";
    //删除提醒
    public static String URL_DELETE_RECOMMEND = HOST_NAME + "/reminder/remove";
    //标记电话邮箱失效
    public static String URL_RESUME_MARK_VALID = HOST_NAME + "/candidate/update/contact/notvalid";
    //新功能使用福利领取接口
    public static String URL_NEW_GIFT = HOST_NAME + "/activity/new/feature/welfare";
    //企业人才搜索列表
    public static String URL_COMPANY_RESUME_SEARCH_LIST = HOST_NAME + "/candidate/firm";
    //企业人才详情
    public static String URL_COMPANY_RESUME_DETAIL = HOST_NAME + "/candidate/firm/detail";
    //tracking搜索列表
    public static String URL_TRACKING_LIST = HOST_NAME + "/trackingList/search";
    //查看联系方式
    public static String URL_VIEW_CONTACT = HOST_NAME + "/candidate/view/contact";
    //添加到TrackingList / 从TrackingList 移除
    public static String URL_TRACKINGLIST_ADD_OR_REMOVE = HOST_NAME + "/trackingList/add/or/remove";
    //TrackingList 搜索
    public static String URL_TRACKINGLIST_SEARCH = HOST_NAME + "/trackingList/search";
    //floatingList
    public static String URL_FLOATINGLIST_SEARCH = HOST_NAME + "/floatingList/list";
    //floatingList-detail
    public static String URL_FLOATINGLIST_DETAIL = HOST_NAME + "/floatingList/detail";
    //我的人才
    public static String URL_RESUME_MINE_SEARCH = HOST_NAME + "/candidate/mine/collection";
    //批量接受
    public static String URL_FLOATINGLIST_RECOMMEND_ACCEPT = HOST_NAME + "/floatingList/batch/accept";
    //接受/拒绝
    public static String URL_ACCEPT_OR_REJECT = HOST_NAME + "/floatingList/recommend/flow/accept/or/reject";
    //获取企业用户列表
    public static String URL_FIRM_MEMBERS = HOST_NAME + "/floatingList/get/firm/members";
    //获取offer业绩分成
    public static String URL_SPLIT_INFO_LIST = HOST_NAME + "/floatingList/get/offer/performance/split/info/list";
    //进入面试/入职/离职/offer
    public static String URL_FLOW_STAGE = HOST_NAME + "/floatingList/recommend/flow/stage";
    //淘汰
    public static String URL_FLOW_OBSOLETE = HOST_NAME + "/floatingList/recommend/flow/obsolete";
    //安排面试
    public static String URL_FLOW_INTERVIEW = HOST_NAME + "/floatingList/recommend/flow/interview";
    //面试反馈
    public static String URL_FLOW_INTERVIEW_FEEDBACK = HOST_NAME + "/floatingList/recommend/flow/interview/feedback";
    //一键offer
    public static String URL_FLOW_ONEKEYOFFER = HOST_NAME + "/floatingList/recommend/flow/onekeyoffer";
}
