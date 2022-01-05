package com.zarszz.userservice.utility;

import java.lang.reflect.Method;

import com.zarszz.userservice.requests.v1.Params;

import org.springframework.util.StringUtils;

public class Global {
    public static Object generateCacheKey(Object target, Method method, Object... params) {

        var tagetClass = target.getClass().getSimpleName();
        var methodGet = method.getName();
        var key = tagetClass + "_" + methodGet;
        var isFound = false;

        // Define key search from params
        for (Object param : params) {
            if (Params.isInstanceOfMe(param)) {
                isFound = true;
                key += "_" + Params.generateOfKey((Params) param);
            }
        }

        if (!isFound) {
            key += "_" + StringUtils.arrayToDelimitedString(params, "_");
        }

        return key;
    }
}
