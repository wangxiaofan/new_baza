package social.data;

import java.io.Serializable;

/**
 * Created by Vincent.Lei on 2018/2/23.
 * Title：
 * Note：
 */

public class PayData implements Serializable {
    public String appId;
    public String partnerId;
    public String prepayId;
    public String packageValue;
    public String nonceStr;
    public String timeStamp;
    public String sign;
}
