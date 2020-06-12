package location;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.ArrayList;

/**
 * Created by Vincent.Lei on 2017/9/28.
 * Title：
 * Note：
 */

public class LocationManager {
    private static LocationManager mManager;
    private Context mContext;
    private LocationClient mLocationClient;
    private LocationInfo mLocationInfo;
    private ArrayList<ILocationListener> mLocationListeners = new ArrayList<>(1);

    private LocationManager(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static LocationManager getInstance(Context context) {
        if (mManager == null) {
            synchronized (LocationManager.class) {
                if (mManager == null)
                    mManager = new LocationManager(context);
            }
        }
        return mManager;
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系
        int span = 30 * 1000;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps
        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    public void startLocation() {
        if (mLocationClient == null) {
            //声明LocationClient类
            mLocationClient = new LocationClient(mContext);
            initLocation();
            //注册监听函数
            mLocationClient.registerLocationListener(new BDLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    if (bdLocation != null) {
                        mLocationInfo = new LocationInfo(bdLocation);
                        if (!mLocationListeners.isEmpty()) {
                            for (int i = 0, size = mLocationListeners.size(); i < size; i++) {
                                mLocationListeners.get(i).onReceiveCurrentLocation(mLocationInfo);
                            }
                        }
                    }
                }
            });
        }
        mLocationClient.start();
    }

    public void stopLocation() {
        if (mLocationClient != null)
            mLocationClient.stop();
        mLocationClient = null;
    }

    public void registerLocationListener(ILocationListener listener, boolean register) {
        if (listener == null)
            return;
        if (register) {
            mLocationListeners.add(listener);
            if (mLocationInfo != null)
                listener.onReceiveLastLocation(mLocationInfo);
        } else
            mLocationListeners.remove(listener);
    }
}
