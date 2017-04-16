package com.haizhi.bqd.common;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by chenbo on 17/4/6.
 */
public class DeviceUtil {

    public static final String Device_ID = "DeviceID";

    public static final String User_Agent = "User-Agent";

    public static int getChannel(HttpServletRequest request) {
        String ua = request.getHeader(User_Agent);
        if (ua == null) {
            return 0;
        } else {
            ua = ua.toLowerCase();
        }
        if (ua.contains("android") || ua.contains("ios") || ua.contains("iphone")) {
            return 1;
        }
        return 0;
    }

    public static String getDeviceId(HttpServletRequest request) {
        return request.getHeader(Device_ID);
    }

}
