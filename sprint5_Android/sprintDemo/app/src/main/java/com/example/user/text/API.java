package com.example.user.text;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;

/**
 * Created by toy9986619 on 2017/9/2.
 */

public class API {

    //use to call api
    public static String CallAPI(String method, String params, ArrayList sendlist, String access_token){
        String responseString = null;

        try {
            HttpClient httpClient = new DefaultHttpClient();

            if (method == "GET"){
                String stringEntity = URLEncodedUtils.format(sendlist,"UTF-8");
                params += "?"+stringEntity;
                HttpGet httpGet = new HttpGet(params);
                httpGet.setHeader("Authorization","JWT "+access_token);
                //Log.e("getAuth",access_token);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                responseString = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
            }else if(method == "POST"){
                HttpPost httpPost = new HttpPost(params);
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(sendlist);

                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                httpPost.setEntity(entity);

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                responseString = EntityUtils.toString(httpEntity,"UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error", e.getMessage());
        }
        //Log.e("APItest",responseString.toString());
        return responseString;
    }
}
