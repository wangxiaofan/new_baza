package location;

import com.baidu.location.BDLocation;

/**
 * Created by Vincent.Lei on 2017/9/28.
 * Title：
 * Note：
 */

public class LocationInfo {
    public double mLatitude;
    public double mLongitude;

    public LocationInfo(BDLocation bdLocation) {
        mLatitude = bdLocation.getLatitude();
        mLongitude = bdLocation.getLongitude();
    }
}
