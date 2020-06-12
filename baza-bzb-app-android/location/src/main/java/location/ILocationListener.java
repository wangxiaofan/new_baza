package location;

/**
 * Created by Vincent.Lei on 2017/9/28.
 * Title：
 * Note：
 */

public interface ILocationListener {
    void onReceiveLastLocation(LocationInfo locationInfo);

    void onReceiveCurrentLocation(LocationInfo locationInfo);
}
