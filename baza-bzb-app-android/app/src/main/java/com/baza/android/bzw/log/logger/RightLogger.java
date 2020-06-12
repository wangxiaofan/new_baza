package com.baza.android.bzw.log.logger;

import com.baza.android.bzw.bean.exchange.GoodListResultBean;
import com.baza.android.bzw.log.ReportAgent;

import org.json.JSONObject;

/**
 * Created by Vincent.Lei on 2018/9/14.
 * Title：
 * Note：
 */
public class RightLogger extends BaseLogger {
    public void sendExchangeLog(Object pageObj, GoodListResultBean.Good good, int exchangeTime) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("costCount", good.price * exchangeTime);
            jsonObject.put("id", good.id);
            jsonObject.put("amountCount", good.specification.limit * exchangeTime);
        } catch (Exception e) {
            jsonObject = null;
        }
        if (jsonObject != null)
            ReportAgent.sendEventLog(pageObj, "gt_ec_1", jsonObject.toString());
    }
}
