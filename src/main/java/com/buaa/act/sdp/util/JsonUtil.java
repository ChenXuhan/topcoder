package com.buaa.act.sdp.util;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by yang on 2016/10/15.
 */
public class JsonUtil {
    public  static <T> T jsonToObject(String json,Class<T> target,Class source){
        Gson gson=new Gson();
        Type type=type(target,source);
        return gson.fromJson(json,type);
    }

    public static <T> T fromJson(String json,Class<T> clazz){
        Gson gson=new Gson();
        return  gson.fromJson(json,clazz);
    }

    public static String toJson(Object object){
        Gson gson=new Gson();
        return gson.toJson(object);
    }

    public static ParameterizedType type(final Class raw, final Type... args) {
        return new ParameterizedType() {
            public Type getRawType() {
                return raw;
            }

            public Type[] getActualTypeArguments() {
                return args;
            }

            public Type getOwnerType() {
                return null;
            }
        };
    }
}
