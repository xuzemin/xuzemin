package com.android.jdrd.headcontrol.util;

import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/10 0010.
 * Description:·â×°JasonÊý¾Ý
 */

public class JsonPackage {
    private static Gson gson = new Gson();
    private static Map map = new LinkedHashMap();

    /**
     * @param type
     * @param function
     * @param date
     * @return
     */
    public static String stringToJson(String type, String function, String date) {
        map.put("type", type);
        map.put("function", function);
        map.put("data", date);
        String str = gson.toJson(map);
        return str;
    }

    public static String stringToJson(String type, String function, Map map1) {
        map.put("type", type);
        map.put("function", function);
        map.put("data", map1);
        String str = gson.toJson(map);
        return str;
    }


}
