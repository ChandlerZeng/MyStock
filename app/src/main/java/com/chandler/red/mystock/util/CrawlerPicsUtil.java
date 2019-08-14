package com.chandler.red.mystock.util;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chandler.red.mystock.entity.baidupic.CrawerParams;

import java.util.HashMap;
import java.util.Map;

public class CrawlerPicsUtil {
    private static final String URL = "https://image.baidu.com/search/acjson";
    private static final String queryJson = "{\n" +
            "\t\"tn\": \"resultjson_com\",\n" +
            "\t\"ipn\": \"rj\",\n" +
            "\t\"ct\": \"201326592\",\n" +
            "\t\"is\": \"\",\n" +
            "\t\"fp\": \"result\",\n" +
            "\t\"queryWord\": \"大奶美女\",\n" +
            "\t\"cl\": \"2\",\n" +
            "\t\"lm\": \"-1\",\n" +
            "\t\"ie\": \"utf-8\",\n" +
            "\t\"oe\": \"utf-8\",\n" +
            "\t\"adpicid\": \"\",\n" +
            "\t\"st\": \"\",\n" +
            "\t\"z\": \"\",\n" +
            "\t\"ic\": \"\",\n" +
            "\t\"hd\": \"\",\n" +
            "\t\"latest\": \"\",\n" +
            "\t\"copyright\": \"\",\n" +
            "\t\"word\": \"大奶美女\",\n" +
            "\t\"s\": \"\",\n" +
            "\t\"se\": \"\",\n" +
            "\t\"tab\": \"\",\n" +
            "\t\"width\": \"\",\n" +
            "\t\"height\": \"\",\n" +
            "\t\"face\": \"\",\n" +
            "\t\"istype\": \"\",\n" +
            "\t\"qc\": \"\",\n" +
            "\t\"nc\": \"\",\n" +
            "\t\"fr\": \"\",\n" +
            "\t\"expermode\": \"\",\n" +
            "\t\"force\": \"\",\n" +
            "\t\"pn\": \"90\",\n" +
            "\t\"rn\": \"30\",\n" +
            "\t\"gsm\": \"5a\",\n" +
            "\t\"1564557278500\": \"\"\n" +
            "}";

    public static void getPicUrls(int offset, final CrawerParams queryParams){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("tn", queryParams.getTn());
                params.put("ipn", queryParams.getIpn());
                params.put("ct", queryParams.getCt());
                params.put("fp", queryParams.getFp());
                params.put("queryWord", queryParams.getQueryWord());
                params.put("cl", queryParams.getCl());
                params.put("lm", queryParams.getLm());
                params.put("ie", queryParams.getIe());
                params.put("oe", queryParams.getOe());
                params.put("word", queryParams.getWord());
                params.put("pn", queryParams.getPn());
                params.put("rn", queryParams.getRn());
                params.put("gsm", queryParams.getGsm());
                return params;
            }
        };
    }
}
