package com.baza.android.bzw.businesscontroller.publish.util;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.baza.android.bzw.base.BaseActivity;
import com.baza.android.bzw.bean.BaseHttpResultBean;
import com.baza.android.bzw.bean.qrcode.QRCodeScanResultBean;
import com.baza.android.bzw.businesscontroller.publish.QRCodeActivity;
import com.baza.android.bzw.businesscontroller.publish.QRCodeLoginConfirmPopupWindow;
import com.baza.android.bzw.dao.LoginDao;
import com.baza.android.bzw.extra.IDefaultRequestReplyListener;
import com.baza.android.bzw.log.LogUtil;
import com.slib.permission.PermissionsResultAction;
import com.bznet.android.rcbox.R;

import java.lang.ref.WeakReference;

/**
 * Created by Vincent.Lei on 2017/11/22.
 * Title：
 * Note：
 */

public class QRCodeUtil {
    public static final int INT_REQUEST_CODE_QR_CODE = 12345;
    public static final String STR_SCAN_RESULT = "qr_code_scan_result";

    public static void startToScan(final BaseActivity baseActivity) {
        baseActivity.requestPermission(Manifest.permission.CAMERA, null, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                QRCodeActivity.launch(baseActivity, INT_REQUEST_CODE_QR_CODE);
            }

            @Override
            public void onDenied(String permission) {

            }
        });
    }

    public static void parseQRCodeResult(BaseActivity activity, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null)
            return;
        String result = data.getStringExtra(STR_SCAN_RESULT);
//         result = "{\"type\":1,\"data\":{ \"id\":\"123455\" }}";
        LogUtil.d(result);
        if (TextUtils.isEmpty(result))
            return;
        try {
            QRCodeScanResultBean qrCodeScanResultBean = JSON.parseObject(result, QRCodeScanResultBean.class);
            switch (qrCodeScanResultBean.type) {
                case QRCodeScanResultBean.QR_CODE_ACTION_TYPE__LOGIN:
                    qrCodeLogin(new WeakReference<BaseActivity>(activity), JSON.parseObject(qrCodeScanResultBean.data, QRCodeScanResultBean.QRCodeToLoginBean.class));
                    break;
            }
        } catch (Exception e) {
            LogUtil.e("QRCode msg format error");
            activity.callShowToastMessage(null, R.string.un_know_qr_code);
        }

    }

    private static BaseActivity getActivityFromWeakReference(WeakReference<BaseActivity> activityRef) {
        if (activityRef != null && activityRef.get() != null && !activityRef.get().isFinishing())
            return activityRef.get();
        return null;
    }

    private static void qrCodeLogin(final WeakReference<BaseActivity> activityRef, Object actionData) {
        final QRCodeScanResultBean.QRCodeToLoginBean qrCodeToLoginBean = (QRCodeScanResultBean.QRCodeToLoginBean) actionData;
        if (qrCodeToLoginBean != null && !TextUtils.isEmpty(qrCodeToLoginBean.id)) {
            BaseActivity activity = getActivityFromWeakReference(activityRef);
            if (activity == null)
                return;
            new QRCodeLoginConfirmPopupWindow(getActivityFromWeakReference(activityRef), new QRCodeLoginConfirmPopupWindow.IQRCodeLoginConfirmListener() {
                @Override
                public void onLoginConfirm(boolean enableLogin) {
                    if (!enableLogin)
                        return;
                    getActivityFromWeakReference(activityRef).callShowProgress(null, true);
                    LoginDao.qrcodeLogin(qrCodeToLoginBean.id, new IDefaultRequestReplyListener<BaseHttpResultBean>() {
                        @Override
                        public void onRequestReply(boolean success, BaseHttpResultBean baseHttpResultBean, int errorCode, String errorMsg) {
                            BaseActivity activity2 = getActivityFromWeakReference(activityRef);
                            if (activity2 != null) {
                                activity2.callCancelProgress();
                                if (!success)
                                    activityRef.get().callShowToastMessage(errorMsg, errorCode);
                            }
                        }
                    });
                }
            }).showWithActivity(activity);

        }
    }
}
