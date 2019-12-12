package com.uear.akilligaleri.ui;

import android.graphics.Bitmap;
import android.util.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {


    public static String toJSon(String base64,int width,int height)
    {
        try {
            // Here we convert Java Object to JSON
            JSONObject jsonObj = new JSONObject();
            base64 = base64.replace("\\","");
            jsonObj.put("height",height);
            jsonObj.put("width", width);
            jsonObj.put("base64",base64);
            // In this case we need a json array to hold the java list
            //JSONArray jsonArr = new JSONArray();
            String base64Json = jsonObj.getString("base64");
            String heightJson = jsonObj.getString("height");
            String widthJson = jsonObj.getString("width");
            String base64Identify = "\""+"base64"+"\""+":";
            String heightIdentify = "\""+"height"+"\""+":";
            String width64Identify = "\""+"width"+"\""+":";

            return heightIdentify+heightJson+width64Identify+widthJson+base64Identify+base64Json;
            //return jsonObj.toString();

        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
