package com.careerhub.go.plugin.util;

import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

public class JSONUtils {
    public static Object fromJSON(String json) {
        return new GsonBuilder().create().fromJson(json, Object.class);
    }

    public static Object fromJSON(String json, Class jsonClass ) {
        return new GsonBuilder().create().fromJson(json, jsonClass);
    }


    public static String toJSON(Object object) {
        return new GsonBuilder().create().toJson(object);
    }
}
