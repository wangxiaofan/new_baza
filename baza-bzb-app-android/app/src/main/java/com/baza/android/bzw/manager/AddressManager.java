package com.baza.android.bzw.manager;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.SparseArray;

import com.baza.android.bzw.application.BZWApplication;
import com.baza.android.bzw.bean.common.LocalAreaBean;
import com.baza.android.bzw.log.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent.Lei on 2016/12/15.
 * Title : 地址管理器
 * Note :
 */

public class AddressManager {
    private static final int CN_COUNT_CODE = 41;
    private SparseArray<String> mSparseCodeNamePair;
    private SparseArray<List<LocalAreaBean>> mSparseProvinceAndCityPair;
    private List<LocalAreaBean> mProvinceAreaList;
//    private List<LocalAreaBean> mLocalAreaBeanList;

    private static AddressManager mInstance = new AddressManager();

    private AddressManager() {
    }

    public static AddressManager getInstance() {
        return mInstance;
    }

    public String getCityNameByStringCode(String codeStr) {
        if (TextUtils.isEmpty(codeStr))
            return getCityNameByCode(0);
        int code;
        try {
            code = Integer.parseInt(codeStr);
        } catch (Exception e) {
            //ignore
            code = 0;
        }
        return getCityNameByCode(code);
    }

    public String getCityNameByCode(int code) {
        if (mSparseCodeNamePair == null)
            init(BZWApplication.getApplication());
        String city = mSparseCodeNamePair.get(code);
        return city == null ? "未知" : city;
    }

    public boolean isCityCodeEnable(int code) {
        return (mSparseCodeNamePair != null && mSparseCodeNamePair.indexOfKey(code) >= 0);
    }

    public List<LocalAreaBean> getCitiesByProvinceCode(int provinceCode) {
        if (provinceCode <= 0 || mSparseProvinceAndCityPair == null)
            return null;
        return mSparseProvinceAndCityPair.get(provinceCode);
    }

    public List<LocalAreaBean> getProvinces() {
        if (mProvinceAreaList == null)
            init(BZWApplication.getApplication());
        return mProvinceAreaList;
    }

    public SparseArray<List<LocalAreaBean>> getAllChineseCities() {
        if (mSparseProvinceAndCityPair == null)
            init(BZWApplication.getApplication());
        return mSparseProvinceAndCityPair;
    }


    public synchronized void init(Application application) {
        checkAndCopyAddressDbFile(application);
        readAddress(application);
    }

    private void checkAndCopyAddressDbFile(Context context) {
        String dbPath = getDbPath(context);
        File dbFile = new File(dbPath);
        if (dbFile.exists())
            return;
        LogUtil.d("write address db");
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        byte[] buff = new byte[1024];
        int len;
        try {
            inputStream = context.getAssets().open("baza_address.db");
            fileOutputStream = new FileOutputStream(dbFile);
            while ((len = inputStream.read(buff)) > 0) {
                fileOutputStream.write(buff, 0, len);
            }
            fileOutputStream.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private String getDbPath(Context context) {
        return context.getApplicationInfo().dataDir + "/baza_address.db";
    }

    private void readAddress(Context context) {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(getDbPath(context), null);
        if (sqLiteDatabase != null) {
            String tableName = "address";
            List<LocalAreaBean> mLocalAreaBeanList;
            Cursor cursor = sqLiteDatabase.query(tableName, null, null, null, null, null, null);
            if (cursor != null) {
                mProvinceAreaList = new ArrayList<>(36);
                mSparseCodeNamePair = new SparseArray<>();
                mLocalAreaBeanList = new ArrayList<>(cursor.getCount());
                LocalAreaBean areaBean;
                while (cursor.moveToNext()) {
                    areaBean = new LocalAreaBean();
                    areaBean.code = cursor.getInt(cursor.getColumnIndex("code"));
                    areaBean.level = cursor.getInt(cursor.getColumnIndex("level"));
                    areaBean.parentCode = cursor.getInt(cursor.getColumnIndex("parentCode"));
                    areaBean.name = cursor.getString(cursor.getColumnIndex("name"));
                    areaBean.enName = cursor.getString(cursor.getColumnIndex("enName"));
                    areaBean.shortName = cursor.getString(cursor.getColumnIndex("shortName"));
                    mLocalAreaBeanList.add(areaBean);
                    mSparseCodeNamePair.put(areaBean.code, areaBean.name);
                    if (areaBean.level == 2 && areaBean.parentCode == CN_COUNT_CODE)
                        mProvinceAreaList.add(areaBean);
                }
                cursor.close();

                mSparseProvinceAndCityPair = new SparseArray<>();
                LocalAreaBean province;
                LocalAreaBean city;
                List<LocalAreaBean> citys;
                for (int i = 0, size = mProvinceAreaList.size(); i < size; i++) {
                    province = mProvinceAreaList.get(i);
                    citys = new ArrayList<>(16);
                    for (int j = 0, size2 = mLocalAreaBeanList.size(); j < size2; j++) {
                        city = mLocalAreaBeanList.get(j);
                        if (city.parentCode == province.code)
                            citys.add(city);
                    }
                    mSparseProvinceAndCityPair.put(province.code, citys);
                }

            }
        }

    }
}
