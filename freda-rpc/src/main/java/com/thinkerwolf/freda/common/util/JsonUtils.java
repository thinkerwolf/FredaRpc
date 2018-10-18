package com.thinkerwolf.freda.common.util;

import com.alibaba.fastjson.JSON;

/**
 * Json工具
 *
 * @author wukai
 */
public class JsonUtils {

    public static String obj2Json(Object obj) {
        return JSON.toJSONString(obj);
    }

    public static <T> T json2Obj(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }
    
}
