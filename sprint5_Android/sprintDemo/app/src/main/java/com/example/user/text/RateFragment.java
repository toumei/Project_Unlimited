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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

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

    //創造工廠
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


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstenceState){

        //call api
        new RateTask().execute(rateAPI);

        return inflater.inflate(R.layout.ratefrag,container,false);
    }

    class RateTask extends AsyncTask<String, Void, Void> {
        Double rateData[];
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

                //json處理
                JSONArray responseJSON = new JSONArray(API.CallAPI("GET", params[0], sendlist, access_token));
                Log.d("RateDataJSON", responseJSON.toString());

                //取得資料筆數
                DATA_COUNT = responseJSON.length();
                rateData = new Double[DATA_COUNT];
                timeData = new String[DATA_COUNT];

                Log.d("DATA_COUNT", Integer.toString(DATA_COUNT));

                //尋訪JSONObject
                for (int i=0;i<DATA_COUNT;i++){
                    //取得資料
                    JSONObject data = responseJSON.getJSONObject(i);
                    rateData[i] = data.getDouble("rate");
                    timeData[i] = data.getString("update_time");
                }

                Log.d("DATA_CHECK", timeData[3]);


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // in this example, a LineChart is initialized from xml
            chart = (LineChart) getView().findViewById(R.id.Ratechart);

            //data add
            ArrayList<Entry> entries = new ArrayList<>();
            setChartData(entries);

            //add entries to set
            LineDataSet dataSet = new LineDataSet(entries, "Lable");

            //set LineChart
            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);
            chart.invalidate(); // refresh
        }

        //設定Chart Data
        private void setChartData(ArrayList<Entry> entries){
            for(int i=0; i<DATA_COUNT; i++){
                entries.add(new Entry((float) i, rateData[i].floatValue() ));
            }
        }
    }
}
