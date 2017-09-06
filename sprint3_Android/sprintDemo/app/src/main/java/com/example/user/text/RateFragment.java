package com.example.user.text;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by user on 2017/8/2.
 */

public class RateFragment extends Fragment {

    LineChart chart;
    public static final String ACCESS_TOKEN = "access_token";
    private String access_token;
    private String rateAPI="http://163.13.127.98:8088/api/v1.1/get_rmb_rate";
    private String from_time="2017-08-13";
    private String to_time="2017-8-20";

    public static RateFragment newInstance(String access_token)
    {
        Bundle bundle = new Bundle();
        bundle.putString(ACCESS_TOKEN, access_token);
        RateFragment rateFragment = new RateFragment();
        rateFragment.setArguments(bundle);
        return rateFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get access token
        Bundle bundle = getArguments();
        Log.d("test", bundle.toString());
        if (bundle != null)
            access_token = bundle.getString(ACCESS_TOKEN);
        else{
            Log.d("test", "fail");
        }

        Log.d("access_token", access_token);

        // in this example, a LineChart is initialized from xml
        //chart = (LineChart) findViewById(R.id.chart);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstenceState){

        //call api
        new RateTask().execute(rateAPI);

        return inflater.inflate(R.layout.ratefrag,container,false);
    }

    class RateTask extends AsyncTask<String, Void, Void> {
        double rateData[];
        String timeData[];
        private int DATA_COUNT=0;

        @Override
        protected Void doInBackground(String... params) {
            try {
                //傳送資料
                ArrayList<NameValuePair> sendlist = new ArrayList<NameValuePair>();
                //參數
                sendlist.add(new BasicNameValuePair("from", from_time));
                sendlist.add(new BasicNameValuePair("to", to_time));

                JSONArray dataJSON = new JSONArray(API.CallAPI("GET", params[0], sendlist, access_token));
                Log.d("RateDataJSON", dataJSON.toString());

                DATA_COUNT = dataJSON.length();
                rateData = new double[DATA_COUNT];
                timeData = new String[DATA_COUNT];

                Log.d("DATA_COUNT", Integer.toString(DATA_COUNT));

                //尋訪JSONObject
                /*Iterator<String> stringIterator = dataJSON.keys();
                for (int i=0;i<DATA_COUNT;i++){
                    //取得key
                    String key = stringIterator.next().toString();

                    intKey[i] = key;
                    String value = dataJSON.getString(key);
                    intValue[i] = Integer.parseInt(value);
                }*/


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
