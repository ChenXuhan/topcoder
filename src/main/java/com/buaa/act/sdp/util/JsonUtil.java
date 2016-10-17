package com.buaa.act.sdp.util;

import com.google.gson.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by yang on 2016/10/15.
 */
public class JsonUtil {

    public static <T> T fromJson(String json,Class<T> clazz){
        Gson gson=new Gson();
        return  gson.fromJson(json,clazz);
    }

    public static <T> T fromJson(JsonElement jsonElement,Class<T> clazz){
        Gson gson=new Gson();
        return  gson.fromJson(jsonElement,clazz);
    }

    public static String toJson(Object object){
        Gson gson=new Gson();
        return gson.toJson(object);
    }

    public static JsonElement getJsonElement(String json, String param) {
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(json);
        if(jsonElement!=null){
            return jsonElement.getAsJsonObject().get(param);
        }
        return null;
    }

    public static <T> Map<String,T> jsonToMap(JsonObject jsonObject, Class<T>clazz ){
        Map<String,T>map=new HashMap<>();
        Gson gson=new Gson();
        Set<Map.Entry<String,JsonElement>>set=jsonObject.entrySet();
        Iterator<Map.Entry<String,JsonElement>>iterator=set.iterator();
        Map.Entry<String,JsonElement>entry;
        while(iterator.hasNext()){
            entry=iterator.next();
            map.put(entry.getKey(),gson.fromJson(entry.getValue(),clazz));
        }
        return map;
    }

    public  static <T> T jsonToObject(String json,Class<T> target,Class source){
        Gson gson=new Gson();
        Type type=type(target,source);
        return gson.fromJson(json,type);
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
