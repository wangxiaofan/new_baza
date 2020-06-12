package com.baza.android.bzw.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.CustomerHttpResultBean;
import com.baza.android.bzw.bean.common.CountCalculateBean;
import com.baza.android.bzw.bean.exchange.BenefitDetailResultBean;
import com.baza.android.bzw.bean.exchange.BenefitResultBean;
import com.baza.android.bzw.bean.exchange.GoodListResultBean;
import com.baza.android.bzw.bean.taskcard.TaskBean;
import com.baza.android.bzw.bean.user.ExtraCountBean;
import com.baza.android.bzw.bean.user.ExtraCountResultBean;
import com.baza.android.bzw.bean.user.GrowResultBean;
import com.baza.android.bzw.bean.user.IllegalBean;
import com.baza.android.bzw.bean.user.InviteCodeBean;
import com.baza.android.bzw.bean.user.InviteInfoResultBean;
import com.baza.android.bzw.bean.user.OtherUserInfoResultBean;
import com.baza.android.bzw.bean.user.RankBean;
import com.baza.android.bzw.bean.user.RankResultBean;
import com.baza.android.bzw.bean.user.UploadAvatarResultBean;
import com.baza.android.bzw.bean.user.UserInfoBean;
import com.baza.android.bzw.bean.user.UserInfoResultBean;
import com.baza.android.bzw.bean.user.VersionResultBean;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.extra.CustomerHttpRequestUtil;
import com.baza.android.bzw.extra.CustomerRequestAssistHandler;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.slib.http.HttpRequestUtil;
import com.slib.http.INetworkCallBack;
import com.slib.storage.database.DBWorker;
import com.slib.storage.database.core.DataBaseManager;
import com.slib.storage.database.core.DbClassUtil;
import com.slib.storage.database.handler.IDBControllerHandler;
import com.slib.storage.database.listener.IDBReplyListener;
import com.slib.utils.DateUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import location.LocationInfo;

/**
 * Created by Vincent.Lei on 2017/5/17.
 * Title：
 * Note：
 */

public class AccountDao {
    private AccountDao() {
    }

    public static final int ACCOUNT_EDIT_NICK_NAME = 1;
    public static final int ACCOUNT_EDIT_REAL_NAME = 2;
    public static final int ACCOUNT_EDIT_COMPANY = 3;
    public static final int ACCOUNT_EDIT_JOB = 4;
    public static final int ACCOUNT_EDIT_CITY = 5;
    public static final int ACCOUNT_BBS_USER_NAME = 6;

    public static class SmsCodeType {
        public static final int SMS_CODE_TYPE_NORMAL = 1;
        public static final int SMS_CODE_TYPE_UPDATE_MOBILE = 2;
    }

    public static void checkNewVersion(final IDefaultRequestReplyListener<VersionResultBean> listener) {
        HttpRequestUtil.doHttpPost(URLConst.URL_CHECK_UPDATE, null, VersionResultBean.class, new INetworkCallBack<VersionResultBean>() {
            @Override
            public void onSuccess(final VersionResultBean versionResultBean) {
                if (listener != null) {
                    boolean success = versionResultBean != null && versionResultBean.data != null;
                    listener.onRequestReply(success, versionResultBean, (success ? CustomerRequestAssistHandler.NET_REQUEST_OK : CustomerRequestAssistHandler.getErrorCode(versionResultBean)), (success ? null : CustomerRequestAssistHandler.getErrorMsg(versionResultBean)));
                }
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null) {
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
                }
            }
        });
    }

    public static void updateUserInfo(int type, String value, final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        switch (type) {
            case ACCOUNT_EDIT_NICK_NAME:
                param.put("nickName", value);
                break;
            case ACCOUNT_EDIT_REAL_NAME:
                param.put("trueName", value);
                break;
            case ACCOUNT_EDIT_COMPANY:
                param.put("company", value);
                break;
            case ACCOUNT_EDIT_JOB:
                param.put("title", value);
                break;
            case ACCOUNT_EDIT_CITY:
                param.put("location", value);
                break;
            case ACCOUNT_BBS_USER_NAME:
                param.put("bbsUserName", value);
                break;
        }

        HttpRequestUtil.doHttpPost(URLConst.URL_UPDATE_INFO, param, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
            @Override
            public void onSuccess(BaseHttpResultBean baseHttpResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, baseHttpResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void updateAvatar(final File avatar, final IDefaultRequestReplyListener<UploadAvatarResultBean> listener) {
        List<File> fileList = new ArrayList<File>(1) {{
            add(avatar);
        }};
        List<String> nameList = new ArrayList<String>(1) {
            {
                add("image");
            }
        };

        HttpRequestUtil.doHttpWithFiles(URLConst.URL_UPDATE_AVATAR, fileList, nameList, null, UploadAvatarResultBean.class, new INetworkCallBack<UploadAvatarResultBean>() {
            @Override
            public void onSuccess(UploadAvatarResultBean uploadAvatarResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, uploadAvatarResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void getUserInfo(final IDefaultRequestReplyListener<UserInfoResultBean> listener) {
        HttpRequestUtil.doHttpPost(URLConst.URL_GET_USER_PROFILE, null, UserInfoResultBean.class, new INetworkCallBack<UserInfoResultBean>() {
            @Override
            public void onSuccess(UserInfoResultBean userInfoResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, userInfoResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void getImUserInfo(String id, String[] ids, final IDefaultRequestReplyListener<OtherUserInfoResultBean> listener) {
        StringBuilder stringBuilder = new StringBuilder("[");
        if (ids != null && ids.length > 0) {
            for (int i = 0; i < ids.length; i++) {
                stringBuilder.append("\"").append(ids[i]).append("\"").append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        } else {

            stringBuilder.append("\"");
            stringBuilder.append(id);
            stringBuilder.append("\"");
        }
        stringBuilder.append("]");
//        LogUtil.d(stringBuilder.toString());
        HashMap<String, String> param = new HashMap<>();
        param.put("neteases", stringBuilder.toString());
        HttpRequestUtil.doHttpPost(URLConst.URL_GET_IM_USER_INFO, param, OtherUserInfoResultBean.class, new INetworkCallBack<OtherUserInfoResultBean>() {
            @Override
            public void onSuccess(OtherUserInfoResultBean otherUserInfoResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, otherUserInfoResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void readLocalOtherUserInfo(final IDBReplyListener<List<UserInfoBean>> listener) {
        DBWorker.query(UserInfoBean.class, null, null, listener);
    }

    public static void saveOtherUserInfo(final UserInfoBean userInfoBean) {
        if (userInfoBean == null)
            return;
        DBWorker.saveSingle(userInfoBean, null, "userId = ?", new String[]{String.valueOf(userInfoBean.userId)}, null, false);
    }

    public static void bindPushId(String pushId) {
        HashMap<String, String> param = new HashMap<>(1);
        param.put("jpushId", pushId);
        HttpRequestUtil.doHttpPost(URLConst.URL_UPDATE_INFO, param, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
            @Override
            public void onSuccess(BaseHttpResultBean baseHttpResultBean) {
                HttpRequestUtil.doHttpPost(URLConst.URL_TOUCH_PUSH, null, BaseHttpResultBean.class, null);
            }

            @Override
            public void onFailed(Object object) {

            }
        });

    }


    public static void getGradeAndExperience(String signUnionId, final IDefaultRequestReplyListener<GrowResultBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("currentPage", "1");
        param.put("unionId", signUnionId);
        HttpRequestUtil.doHttpPost(URLConst.URL_GET_GRADE_AND_EXPERIENCE, param, GrowResultBean.class, new INetworkCallBack<GrowResultBean>() {
            @Override
            public void onSuccess(GrowResultBean growResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, growResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void getExtensionCountInfo(final IDefaultRequestReplyListener<ExtraCountBean> listener) {
        HttpRequestUtil.doHttpPost(URLConst.URL_GET_EXTENSION_INFO, null, ExtraCountResultBean.class, new INetworkCallBack<ExtraCountResultBean>() {
            @Override
            public void onSuccess(ExtraCountResultBean extraCountResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, extraCountResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void getResumeRank(String signUnionId, final IDefaultRequestReplyListener<RankBean> listener) {
        if (TextUtils.isEmpty(signUnionId))
            return;
        HashMap<String, String> mParam = new HashMap<>(1);
        mParam.put("unionId", signUnionId);
        HttpRequestUtil.doHttpPost(URLConst.URL_MY_RESUME_RANK, mParam, RankResultBean.class, new INetworkCallBack<RankResultBean>() {
            @Override
            public void onSuccess(RankResultBean rankRequestBean) {
                boolean success = rankRequestBean.data != null;
                if (listener != null)
                    listener.onRequestReply(success, rankRequestBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null) {
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
                }
            }
        });
    }

    public static void reportUserLocation(LocationInfo locationInfo) {
        if (locationInfo == null)
            return;
//        LogUtil.d("mLatitude = " + locationInfo.mLatitude);
//        LogUtil.d("mLongitude = " + locationInfo.mLongitude);
        if (locationInfo.mLatitude < 0.01 || locationInfo.mLongitude < 0.01)
            return;
        HashMap<String, String> map = new HashMap<>();
        map.put("lat", String.valueOf(locationInfo.mLatitude));
        map.put("lng", String.valueOf(locationInfo.mLongitude));
        HttpRequestUtil.doHttpPost(URLConst.URL_REPORT_USER_LOCATION, map, BaseHttpResultBean.class, null);
    }

    public static void getSmSCode(String phone, int smsType, final IDefaultRequestReplyListener<String> listener) {
        HashMap<String, String> param = new HashMap<>(2);
        param.put("mobile", phone);
        param.put("smsType", String.valueOf(smsType));
        CustomerRequestAssistHandler.wrapperSmsCodeParam(param);
        HttpRequestUtil.doHttpPost(URLConst.URL_SMS_CODE_NORMAL, param, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
            @Override
            public void onSuccess(BaseHttpResultBean baseHttpResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, null, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void updateMobile(String phone, String code, final IDefaultRequestReplyListener<String> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("mobile", phone);
        param.put("code", code);
        HttpRequestUtil.doHttpPost(URLConst.URL_UPDATE_MOBILE, param, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
            @Override
            public void onSuccess(BaseHttpResultBean baseHttpResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, null, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void feedBack(String content, final IDefaultRequestReplyListener<String> listener) {
        HashMap<String, String> param = new HashMap<>(1);
        param.put("content", content);
        HttpRequestUtil.doHttpPost(URLConst.URL_FEED_BACK, param, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
            @Override
            public void onSuccess(BaseHttpResultBean baseHttpResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, null, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void loadUserBenefit(final IDefaultRequestReplyListener<BenefitResultBean.Data> listener) {
        HttpRequestUtil.doHttpPost(URLConst.URL_USER_RIGHT_BENEFIT, null, BenefitResultBean.class, new INetworkCallBack<BenefitResultBean>() {
            @Override
            public void onSuccess(BenefitResultBean benefitResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, benefitResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void loadGoodList(final IDefaultRequestReplyListener<GoodListResultBean> listener) {
        HttpRequestUtil.doHttpPost(URLConst.URL_GOOD_LIST, null, GoodListResultBean.class, new INetworkCallBack<GoodListResultBean>() {
            @Override
            public void onSuccess(GoodListResultBean goodListResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, goodListResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }


    public static void exchangeGoodList(GoodListResultBean.Good good, int exchangeTime, final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("goodsId", good.id);
        param.put("quantity", String.valueOf(exchangeTime));
        HttpRequestUtil.doHttpPost(URLConst.URL_GOOD_EXCHANGE, param, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
            @Override
            public void onSuccess(BaseHttpResultBean baseHttpResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, baseHttpResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void loadBenefitRecords(int type, int pageNo, int pageSize, final IDefaultRequestReplyListener<BenefitDetailResultBean.Data> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("opType", String.valueOf(type));
        param.put("pageNo", String.valueOf(pageNo));
        param.put("pageSize", String.valueOf(pageSize));
        HttpRequestUtil.doHttpPost(URLConst.URL_BENEFIT_RECORD, param, BenefitDetailResultBean.class, new INetworkCallBack<BenefitDetailResultBean>() {
            @Override
            public void onSuccess(BenefitDetailResultBean benefitDetailResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, benefitDetailResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void inviteCodeExchange(String code, final IDefaultRequestReplyListener<BaseHttpResultBean> listener) {
        HashMap<String, String> param = new HashMap<>();
        param.put("invitationCode", code);
        HttpRequestUtil.doHttpPost(URLConst.URL_INVITE_CODE_EXCHANGE, param, BaseHttpResultBean.class, new INetworkCallBack<BaseHttpResultBean>() {
            @Override
            public void onSuccess(BaseHttpResultBean baseHttpResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, baseHttpResultBean, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void loadIllegalWord(final IDefaultRequestReplyListener<IllegalBean> listener) {
        CustomerHttpRequestUtil.doHttpPost(URLConst.URL_GET_ILLEGAL_INFO, null, IllegalBean.class, new INetworkCallBack<CustomerHttpResultBean<IllegalBean>>() {
            @Override
            public void onSuccess(CustomerHttpResultBean<IllegalBean> dataCustomerHttpResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, dataCustomerHttpResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void readImMessageCountOfStanger(String unionId, final IDBReplyListener<Integer> replyListener) {
        DBWorker.query(CountCalculateBean.class, "type = ? and unionId = ? and timeYMD = ?", new String[]{String.valueOf(CountCalculateBean.TYPE_IM_STRANGER_MSG), unionId, DateUtil.longMillions2FormatDate(System.currentTimeMillis(), DateUtil.SDF_YMD)}, new IDBReplyListener<List<CountCalculateBean>>() {
            @Override
            public void onDBReply(List<CountCalculateBean> countCalculateBeans) {
                if (replyListener != null)
                    replyListener.onDBReply((countCalculateBeans != null && countCalculateBeans.size() > 0) ? countCalculateBeans.get(0).count : 0);
            }
        });
    }

    public static void updateImMessageCountOfStanger(String unionId, final int countAdd) {
        DBWorker.customerDBTask(unionId, new IDBControllerHandler<String, Void>() {
            @Override
            public Void operateDataBaseAsync(DataBaseManager mDBManager, String input) {
                String tableName = DbClassUtil.getTableNameByAnnotationClass(CountCalculateBean.class);
                int count = 0;
                String timeYMD = DateUtil.longMillions2FormatDate(System.currentTimeMillis(), DateUtil.SDF_YMD);
                Cursor cursor = mDBManager.query(tableName, null, "type = ? and unionId = ? and timeYMD = ?", new String[]{String.valueOf(CountCalculateBean.TYPE_IM_STRANGER_MSG), input, timeYMD}, null, null, null);
                boolean exist = false;
                if (cursor != null && cursor.getCount() > 0) {
                    exist = true;
                    cursor.moveToNext();
                    count = cursor.getInt(cursor.getColumnIndex("count"));
                    cursor.close();
                }
                count += countAdd;
                ContentValues contentValues = new ContentValues();
                contentValues.put("type", CountCalculateBean.TYPE_IM_STRANGER_MSG);
                contentValues.put("unionId", input);
                contentValues.put("count", count);
                contentValues.put("timeYMD", timeYMD);
                if (exist)
                    mDBManager.update(tableName, contentValues, "type = ? and unionId = ? and timeYMD = ?", new String[]{String.valueOf(CountCalculateBean.TYPE_IM_STRANGER_MSG), input, timeYMD});
                else
                    mDBManager.insert(tableName, contentValues);
                return null;
            }

            @Override
            public Class<?>[] getDependModeClass() {
                return new Class[]{CountCalculateBean.class};
            }
        }, null, false);
    }

    public static void loadMerculetTasks(final IDefaultRequestReplyListener<TaskBean[]> listener) {
        CustomerHttpRequestUtil.doHttpPost(URLConst.URL_MERCULET_TASK_LIST, null, TaskBean[].class, new INetworkCallBack<CustomerHttpResultBean<TaskBean[]>>() {
            @Override
            public void onSuccess(CustomerHttpResultBean<TaskBean[]> baseHttpResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, baseHttpResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void loadInviteCode(final IDefaultRequestReplyListener<InviteCodeBean> listener) {
        CustomerHttpRequestUtil.doHttpPost(URLConst.URL_INVITE_CODE, null, InviteCodeBean.class, new INetworkCallBack<CustomerHttpResultBean<InviteCodeBean>>() {
            @Override
            public void onSuccess(CustomerHttpResultBean<InviteCodeBean> baseHttpResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, baseHttpResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void loadInviteInfos(final IDefaultRequestReplyListener<InviteInfoResultBean> listener) {
        CustomerHttpRequestUtil.doHttpPost(URLConst.URL_INVITE_LIST, null, InviteInfoResultBean.class, new INetworkCallBack<CustomerHttpResultBean<InviteInfoResultBean>>() {
            @Override
            public void onSuccess(CustomerHttpResultBean<InviteInfoResultBean> baseHttpResultBean) {
                if (listener != null)
                    listener.onRequestReply(true, baseHttpResultBean.data, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }

    public static void loadInviteCodeImageInfo(final IDefaultRequestReplyListener<String> listener) {
        CustomerHttpRequestUtil.doHttpPost(URLConst.URL_INVITE_CODE_IMG_INFO, null, String.class, new INetworkCallBack<CustomerHttpResultBean<String>>() {
            @Override
            public void onSuccess(CustomerHttpResultBean<String> baseHttpResultBean) {
                if (listener != null) {
                    String code = null;
                    try {
                        JSONObject jsonObject = new JSONObject(baseHttpResultBean.data);
                        if (jsonObject.has("imgUrl")) {
                            code = jsonObject.getString("imgUrl");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    listener.onRequestReply(true, code, CustomerRequestAssistHandler.NET_REQUEST_OK, null);
                }
            }

            @Override
            public void onFailed(Object object) {
                if (listener != null)
                    listener.onRequestReply(false, null, CustomerRequestAssistHandler.getErrorCode(object), CustomerRequestAssistHandler.getErrorMsg(object));
            }
        });
    }
}


