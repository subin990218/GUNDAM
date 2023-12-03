package com.mobilesuit.clientplugin.util;

import com.google.gson.Gson;

public class JsonConverter {
    public static String objectToJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public static Object jsonToObject(String json, Class<?> classType) {
        Gson gson = new Gson();
        return gson.fromJson(json, classType);
    }
}
