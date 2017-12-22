package com.coolweather.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by cpl on 12/21/17.
 */

public class Getcurarea {

    public static void getcurarea(final String addr, final Context context){
        Thread x =new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;

                try {

                    URL url = new URL(addr);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    System.out.println(addr+"  "+response.toString());
                    String district = readjson(response.toString());

                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                    System.out.println("getcurarea SharedPreferences.Editor set district:"+district);
                    editor.putString("district",district);
                    editor.commit();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        });
        x.start();
        try {
            x.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
    public static String readjson(String response){
        try {
            //System.out.println("in read json "+response);
            JSONObject jsonObject = new JSONObject(response);
            //JSONObject rlts = jsonObject.getJSONObject("results");
            //JSONObject rlt1 = rlts.getJSONObject("1");

            JSONObject name = jsonObject.getJSONObject("result").getJSONObject("addressComponent");
            String district = name.getString("district");
            //System.out.println("read json district: "+district);
            return district;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String readgooglejson(String response){
        try {
            System.out.println("in read google json "+response);
            JSONObject jsonObject = new JSONObject(response);
            JSONObject rlts = jsonObject.getJSONObject("results");
            JSONArray list = jsonObject.getJSONArray("");
            JSONObject first = (JSONObject)list.get(0);
            JSONArray list1 = first.getJSONObject("address_components").getJSONArray("");
            JSONObject name = (JSONObject) list1.get(1);

            String district = name.getString("short_name");
            System.out.println("read google json district: "+district);
            return district;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }


}
