package com.baza.android.bzw.bean.qrcode;

/**
 * Created by Vincent.Lei on 2017/11/23.
 * Title：
 * Note：
 */

public class QRCodeScanResultBean {

    public static final int QR_CODE_ACTION_TYPE__LOGIN = 2;

    public int type;
    public String data;

    public static class QRCodeToLoginBean {
        public String id;
    }
}
