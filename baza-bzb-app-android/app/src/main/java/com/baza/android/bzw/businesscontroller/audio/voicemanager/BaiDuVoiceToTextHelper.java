package com.baza.android.bzw.businesscontroller.audio.voicemanager;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;

import com.baza.android.bzw.bean.common.BaiDuAccessTokenBean;
import com.baza.android.bzw.bean.common.VoiceTextResultBean;
import com.baza.android.bzw.constant.ConfigConst;
import com.baza.android.bzw.constant.URLConst;
import com.baza.android.bzw.log.LogUtil;
import com.baza.android.bzw.manager.UserInfoManager;
import com.slib.http.HttpRequestUtil;
import com.slib.http.INetworkCallBack;
import com.slib.utils.string.MD5;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;


/**
 * Created by Vincent.Lei on 2017/2/15.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class BaiDuVoiceToTextHelper {
    //    private static final long ACCESS_TOKEN_VALID_TIME = 25 * 24 * 3600 * 1000L;
    private String mAccessToken;
    private File mSourceFile;
    private Context mContext;
    private ITextDecodeListener mTextDecodeListener;

    public interface ISourceFileBase64Listener {
        void onResult(String base64Str);
    }

    public BaiDuVoiceToTextHelper(Context mContext, File mSourceFile, ITextDecodeListener textDecodeListener) {
        this.mSourceFile = mSourceFile;
        this.mContext = mContext;
        this.mTextDecodeListener = textDecodeListener;
    }


    public void decode() {
        if (mSourceFile == null || !mSourceFile.exists()) {
            setBack(ITextDecodeListener.ERROR_CODE_VOICE_NOT_EXIST, null);
            return;
        }
        if (!checkAccessToken()) {
            getBauDuAccessToken();
            return;
        }
        base64Voice();
    }

    /**
     * 判断access_token是否有效
     * 从百度获得的access_token理论上有效期为30天，可以存储在本地并根据本地时间判断有效期
     * 为防止用户修改系统时间导致有效期错误，所以每次打开app第一次使用语音转文字时，获取一次token，并保存在内存中
     *
     * @return true有效  false无效
     */
    private boolean checkAccessToken() {
//        this.mAccessToken = SharedPreferenceManager.getString(CommonConst.BAI_DU_ACCESS_TOKEN);
//        if (!TextUtils.isEmpty(mAccessToken)) {
//            long timeSaved = SharedPreferenceManager.getLong(CommonConst.BAI_DU_ACCESS_TOKEN_SAVE_TIME);
//            long timeDepart = System.currentTimeMillis() - timeSaved;
//            if (timeDepart > 0 && timeDepart <= ACCESS_TOKEN_VALID_TIME)
//                return true;
//        }
        this.mAccessToken = UserInfoManager.getInstance().getBaiDuVoiceAccessToken();
        return (!TextUtils.isEmpty(mAccessToken));
    }

    private void getBauDuAccessToken() {
        HashMap<String, String> param = new HashMap<>();
        param.put("grant_type", "client_credentials");
        param.put("client_id", ConfigConst.BD_API_KEY);
        param.put("client_secret", ConfigConst.BD_SECRET_KEY);
        HttpRequestUtil.doHttpPost(URLConst.BAI_DU_ACCESS_TOKEN, param, new HashMap<String, String>(), BaiDuAccessTokenBean.class, new INetworkCallBack<BaiDuAccessTokenBean>() {
            @Override
            public void onSuccess(BaiDuAccessTokenBean baiduAccessTokenBean) {
                if (baiduAccessTokenBean != null && !TextUtils.isEmpty(baiduAccessTokenBean.access_token)) {
                    mAccessToken = baiduAccessTokenBean.access_token;
//                    SharedPreferenceManager.saveString(CommonConst.BAI_DU_ACCESS_TOKEN, mAccessToken);
//                    SharedPreferenceManager.saveLong(CommonConst.BAI_DU_ACCESS_TOKEN_SAVE_TIME, System.currentTimeMillis());
                    UserInfoManager.getInstance().setBaiDuVoiceAccessToken(mAccessToken);
                    LogUtil.d("access_token", mAccessToken);
                    base64Voice();
                    return;
                }
                setBack(ITextDecodeListener.ERROR_CODE_FAILED, "can not get baiDu accessToken");
            }

            @Override
            public void onFailed(Object object) {
                setBack(ITextDecodeListener.ERROR_CODE_FAILED, "can not get baiDu accessToken");
            }
        });
    }


    private void base64Voice() {
        new Base64Task(mSourceFile, new ISourceFileBase64Listener() {
            @Override
            public void onResult(String base64Str) {
                if (TextUtils.isEmpty(base64Str)) {
                    setBack(ITextDecodeListener.ERROR_CODE_BASE64_VOICE_FILE_FAILED, null);
                    return;
                }
                LogUtil.d("base64Str", base64Str);
                getVoiceText(base64Str);
            }
        }).execute();
    }

    private String getDeviceId() {
        StringBuilder sb = new StringBuilder();
        //品牌
        sb.append("BRAND = ").append(Build.BRAND)
                //整个产品的名称
                .append(";PRODUCT = ").append(Build.PRODUCT)
                //硬件序列号
                .append(";SERIAL = ").append(Build.SERIAL);
        return MD5.getStringMD5(sb.toString());
    }

    private void getVoiceText(String base64Str) {
        String jsonStr = getJsonBody(base64Str);
        HashMap<String, String> header = new HashMap<>();
        header.put("Content-length", String.valueOf(jsonStr.length()));
        HttpRequestUtil.doHttpPostJson(URLConst.BAI_DU_VOICE_TO_TEXT, jsonStr, header, VoiceTextResultBean.class, new INetworkCallBack<VoiceTextResultBean>() {
            @Override
            public void onSuccess(VoiceTextResultBean voiceTextResultBean) {
                if (voiceTextResultBean != null && voiceTextResultBean.result != null && voiceTextResultBean.result.length > 0)
                    setBack(ITextDecodeListener.ERROR_CODE_NONE, voiceTextResultBean.result[0]);
                else
                    setBack(ITextDecodeListener.ERROR_CODE_FAILED, null);
            }

            @Override
            public void onFailed(Object object) {
                setBack(ITextDecodeListener.ERROR_CODE_FAILED, null);
            }
        });

//        BaiDuVoiceRequest<VoiceTextResultBean> baiDuVoiceRequest = new BaiDuVoiceRequest<VoiceTextResultBean>(Request.Method.POST, URLConst.BAI_DU_VOICE_TO_TEXT, VoiceTextResultBean.class, new Response.Listener<VoiceTextResultBean>() {
//            @Override
//            public void onResponse(VoiceTextResultBean t) {
//                if (t != null && t.result != null && t.result.length > 0)
//                    setBack(ITextDecodeListener.ERROR_CODE_NONE, t.result[0]);
//                else
//                    setBack(ITextDecodeListener.ERROR_CODE_FAILED, null);
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                setBack(ITextDecodeListener.ERROR_CODE_FAILED, null);
//            }
//        });
//        baiDuVoiceRequest.setParameters(param);
//        baiDuVoiceRequest.setHeader(header);
//        baiDuVoiceRequest.submit();
    }

    private void setBack(int errorCode, String result) {
        if (mTextDecodeListener != null) {
            try {
                mTextDecodeListener.onDecodeToTextResult(errorCode, result);
            } catch (Exception e) {
                //
            }
        }

    }

    /**
     * 建议JSON工具 序列化对象
     */

    private String getJsonBody(String base64Str) {
        String tag1 = "\"";
        String tag2 = ":";
        String tag3 = ",";
        StringBuilder stringBuilder = new StringBuilder("{")
                .append(tag1).append("format").append(tag1).append(tag2).append(tag1).append("amr").append(tag1).append(tag3)
                .append(tag1).append("rate").append(tag1).append(tag2).append(8000).append(tag3)
                .append(tag1).append("channel").append(tag1).append(tag2).append(1).append(tag3)
                .append(tag1).append("cuid").append(tag1).append(tag2).append(tag1).append(getDeviceId()).append(tag1).append(tag3)
                .append(tag1).append("token").append(tag1).append(tag2).append(tag1).append(mAccessToken).append(tag1).append(tag3)
                .append(tag1).append("lan").append(tag1).append(tag2).append(tag1).append("zh").append(tag1).append(tag3)
                .append(tag1).append("speech").append(tag1).append(tag2).append(tag1).append(base64Str).append(tag1).append(tag3)
                .append(tag1).append("len").append(tag1).append(tag2).append(mSourceFile.length())
                .append("}");
        return stringBuilder.toString();
    }

    private static class Base64Task extends AsyncTask<Void, Void, String> {

        private File mSourceFile;
        private ISourceFileBase64Listener mListener;

        public Base64Task(File mSourceFile, ISourceFileBase64Listener mListener) {
            this.mSourceFile = mSourceFile;
            this.mListener = mListener;
        }

        @Override
        protected String doInBackground(Void... params) {
            FileInputStream fileInputStream = null;
            ByteArrayOutputStream byteArrayOutputStream;

            try {
                LogUtil.d(mSourceFile.getAbsolutePath());
                fileInputStream = new FileInputStream(mSourceFile);
                byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buff = new byte[1024];
                int len;
                while ((len = fileInputStream.read(buff)) != -1) {
                    byteArrayOutputStream.write(buff, 0, len);
                }
                return new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP), "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fileInputStream != null)
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (mListener != null)
                mListener.onResult(s);
        }
    }

}
