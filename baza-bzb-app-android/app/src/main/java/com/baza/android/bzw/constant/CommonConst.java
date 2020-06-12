package com.baza.android.bzw.constant;

public final class CommonConst {
    private CommonConst() {
    }

    public static final int SEX_MALE = 1;
    public static final int SEX_FEMALE = 2;
    public static final int SEX_UNKNOWN = 0;

    public static final int COLLECTION_YES = 1;//已收藏
    public static final int COLLECTION_NO = 0;//未收藏

    public static final int INT_SEARCH_TYPE_MINE = 4;//搜索我的
    public static final int INT_SEARCH_TYPE_MINE_JOB_HUNTER_PREDICTION = 6;//求职预测
    public static final int INT_SEARCH_TYPE_SEEK = 7;//搜索我的寻访
    public static final int INT_SEARCH_TYPE_GROUP_ADD = 8;//搜索添加到智能分组
    public static final int INT_SEARCH_TYPE_COLLECTION = 9;
    public static final int INT_SEARCH_TYPE_COMPANY = 10;//搜索企业
    public static final int INT_SEARCH_TYPE_TRACKING = 11;//搜索tracking

    public static final int SHARE_TYPE_WEEKLY_REPORT = 1;//分享周报
    public static final int SHARE_TYPE_RESUME_SYNC = 2;//分享简历
    public static final int SHARE_TYPE_RESUME_RANK = 3;//分享排行榜

    public static final int REQUEST_SHARE_STATUS_WAIT = 0;//分享未处理
    public static final int REQUEST_SHARE_STATUS_AGREE = 1;//同意

    public static final int RESUME_SEARCH_SORT_ORDER_NONE = 0;
    public static final int RESUME_SEARCH_SORT_ORDER_COMPLETION = 6;
    public static final int RESUME_SEARCH_SORT_ORDER_UPDATE_TIME = 2;
    public static final int RESUME_SEARCH_SORT_ORDER_CREATE_TIME = 3;

    public static final int COMPANY_RESUME_SEARCH_SORT_ORDER_UPDATE_TIME = 2;
    public static final int COMPANY_RESUME_SEARCH_SORT_ORDER_NONE = 11;

    public static final int TIME_NO_GET = -1;
    public static final int TIME_UNTIL_NOW = 9999;
    public static final String TIME_NO_GET_BIRTHDAY = "-1";
    public static final long CHAT_FLAT_TIME_DISTANCE = 5 * 60 * 1000;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int WORK_YEARS_TOP_LEVEL = 20;//工作年限最高值
    public static final int MAX_VOICE_TIME_SECONDS = 3600;//秒
    public static final int MAX_REMARK_AUDIO_TIME_SECONDS = 60;//秒
    public static final int MAX_EMAIL_ATTACHMENT_SIZE = 4 * 1024 * 1024;
    public static final int MAX_EMAIL_ATTACHMENT_COUNT = 5;
    public static final int MIN_TWO_TIME_RECORD_DISTANCE = 1000;
    public static final int SMS_CODE_DEPART_TIME = 60;//两次发送验证码请求间隔
    public static final int MAX_COUNT_VALUE_LEVEL = 10000;
    public static final int COMLPETION_HIGH_LEVEL = 67;
    public static final int COMLPETION_NORMAL_LEVEL = 34;
    public static final int LIST_POSITION_NONE = -1;
    public static final int INT_ID_NONE = -1;
    public static final int INDEX_FIRST = 1;

    public static final int MAX_MESSAGE_FILE_LENGTH = 20 * 1024 * 1024;
    public static final int MAX_LIST_JOB_EMS_SHORT = 6;
    public static final int MAX_LIST_JOB_EMS_LONG = 10;


    public static class VerifyStatus {
        public static final int VERIFY_NONE = 0;
        public static final int VERIFY_SUCCESS = 1;
        public static final int VERIFY_ING = 2;
        public static final int VERIFY_FAILED = 3;
    }

    public static class JobHunterPredictionType {
        public static final int NOW = 1;
        public static final int PREVIOUS = 2;
    }

    public static class IMNoticeType {
        public static final int SCENE_TYPE_NONE = 0;
        public static final int SCENE_TYPE_FRIEND_ASK = 1;
        public static final int SCENE_TYPE_FRIEND_ACCEPT = 2;
        //        public static final int SCENE_TYPE_MEMORANDUM = 3;
        public static final int SCENE_TYPE_USER_VERIFY = 4;
    }

    public static class SmartGroupType {
        public static final int GROUP_TYPE_TIME = 1;
        public static final int GROUP_TYPE_COMPANY = 2;
        public static final int GROUP_TYPE_WORK_EXPERIENCE = 3;
        public static final int GROUP_TYPE_DEGREE = 4;
        public static final int GROUP_TYPE_TITLE = 5;
        public static final int GROUP_TYPE_CUSTOMER = 6;

    }

    public static class Marriage {
        public static final int UN_KNOW = 0;
        public static final int SINGLE = 1;
        public static final int MARRIAGE = 2;
        public static final int SECRET = 3;
    }

    public static class MerculetTaskType {
        public static final int NORMAL = 1;
        public static final int SPECIAL = 2;
    }

    public static class MerculetEventType {
        public static final int EVENT_FRIEND_REQUEST = 1;
        public static final int EVENT_RESUME_RECOMMEND = 23;
        public static final int EVENT_RESUME_UPDATE = 4;
        public static final int EVENT_RESUME_SYNC = 6;
        public static final int EVENT_ADD_REMARK = 7;
        public static final int EVENT_UPDATE_GRADE = 8;
        public static final int EVENT_INVITED = 9;
    }


    public static final String STR_BASE64_SCHEME = "Basic ";
    public static final String STR_AUTHORIZATION = "Authorization";
    public static final String STR_AGENT_INFO = "agent-info";
    public static final String STR_SEC = "agent-info2";
    public static final String STR_SEC_TAG = "BaZhuaHeZiOK-Android";
    public static final String STR_USER_AGENT = "User-Agent";
    public static final String STR_GIF_TAG = ".gif";
    public static final String STR_TRANSFORM_IM_KEY = "im_message_key";
    public static final String STR_TRANSFORM_PUSH_KEY = "push_message_key";
    public static final String STR_TRANSFORM_RESUME_PATH = "import_resume_path";
    public static final String STR_TRANSFORM_RESUME_TEXT = "resume_text_detail";
    public static final String STR_TRANSFORM_EMAIL_LIST = "email_list_cache";
    public static final String STR_TRANSFORM_LOACL_FRIEND_LIST = "local_friend_cache";
    public static final String STR_TRANSFORM_IM_IMAGE_LOAD = "im_image_load";
    public static final String STR_DEFAULT_USER_NAME_SX = "TA";
    public static final String STR_DEFAULT_USER_NAME_CONVERSATION = "TA还未起名";
    public static final String STR_TRANSFORM_RESUME_UPDATED_CONTENT_KEY = "candidate_updated_content_key";
    public static final String STR_BZW_HOME_LINK = "http://www.rchezi.com/";
    public static final String STR_PC_DOWNLOAD_URL = "http://dl.rchezi.com/win/BZBox_Setup.exe";
    public static final String STR_TRANSFORM_UPDATE_RESUME_PARAM = "update_resume_param";
}