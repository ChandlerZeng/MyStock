package com.chandler.red.mystock.util;

import android.util.Log;

import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class GsonUtil {
    public static String objectToJson(Object object){
        String json = new GsonBuilder().disableHtmlEscaping().create().toJson(object);
        Log.i("GsonUtil",json);
        return json;
    }

    public static JSONObject jsonToObject(String json){
        JSONObject returnData = null;
        try {
            returnData = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnData;
    }
}
