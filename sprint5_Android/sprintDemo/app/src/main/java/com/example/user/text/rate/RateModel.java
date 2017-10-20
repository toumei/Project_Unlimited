package com.example.user.text.rate;

import android.os.AsyncTask;
import android.util.Log;

import com.example.user.text.API;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ASUS on 2017/10/13.
 */

class RateModel extends AsyncTask<String, Void, LineData> {


    Double rateData[];
    String timeData[];
    private int DATA_COUNT = 0;
    LineData lineData;

    private String access_token;
    private String from_time = "2017-10-01";
    private String to_time = "2017-10-20";
    private String rateAPI = "http://163.13.127.98:8088/api/v1.2/get_rmb_rate";

    RatePresenter presenter;

    public RateModel(String access_token,RatePresenter presenter) {
        this.access_token = access_token;
        this.presenter = presenter;
    }

    @Override
    protected LineData doInBackground(String... params) {
        try {
            //傳送資料
            ArrayList<NameValuePair> sendlist = new ArrayList<NameValuePair>();

            //參數
            sendlist.add(new BasicNameValuePair("from", from_time));
            sendlist.add(new BasicNameValuePair("to", to_time));

            //json處理
            JSONArray responseJSON = new JSONArray(API.CallAPI("GET", rateAPI, sendlist, access_token));
            Log.d("RateDataJSON", responseJSON.toString());

            //取得資料筆數
            DATA_COUNT = responseJSON.length();
            rateData = new Double[DATA_COUNT];
            timeData = new String[DATA_COUNT];

            Log.d("DATA_COUNT", Integer.toString(DATA_COUNT));

            //尋訪JSONObject
            for (int i = 0; i < DATA_COUNT; i++) {
                //取得資料
                JSONObject data = responseJSON.getJSONObject(i);
                rateData[i] = data.getDouble("rate");
                timeData[i] = data.getString("update_time");
            }

            Log.d("DATA_CHECK", timeData[3]);

            //data add
            ArrayList<Entry> entries = new ArrayList<>();
            setChartData(entries);

            //add entries to set
            LineDataSet dataSet = new LineDataSet(entries, "Lable");

            //set LineChart
            lineData = new LineData(dataSet);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return lineData;
    }
    @Override
    protected void onPostExecute(LineData lineData) {
        super.onPostExecute(lineData);
        presenter.updateRate();


    }

    //設定Chart Data //presenter
    private void setChartData(ArrayList<Entry> entries) {
        for (int i = 0; i < DATA_COUNT; i++) {
            entries.add(new Entry((float) i, rateData[i].floatValue()));
        }
    }
}


