package com.example.user.text;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by user on 2017/8/2.
 */

public class PriceChartFragment extends Fragment {
    BarChart barChart;
    public static final String ACCESS_TOKEN = "access_token";
    private String access_token;
    private String chartAPI = "http://163.13.127.98:8088/api/v1.1/price_data";
    private String record = "衛生紙";

    public static PriceChartFragment newInstance(String access_token)
    {
        Bundle bundle = new Bundle();
        bundle.putString(ACCESS_TOKEN, access_token);
        PriceChartFragment priceChartFragment = new PriceChartFragment();
        priceChartFragment.setArguments(bundle);
        return priceChartFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstenceState){

        //call api
        new ChartTask().execute(chartAPI);

        View v = inflater.inflate(R.layout.pricechartfrag,container,false);
        
        return v;
    }


    //pricechart api

    class ChartTask extends AsyncTask<String,Void, Void> {

        String[] intKey;
        int[] intValue;
        int unit = 100;
        private int DATA_COUNT=0;
        @Override
        protected Void doInBackground(String... params) {
            try {
                //傳送資料
                ArrayList<NameValuePair> sendlist = new ArrayList<NameValuePair>();
                //參數
                sendlist.add(new BasicNameValuePair("search",record));
                //sendlist.add(new BasicNameValuePair("unit",unit+""));

                JSONObject responseJSON = new JSONObject(API.CallAPI("GET",params[0],sendlist, access_token));
                Log.d("responseTest",responseJSON.toString());

                JSONObject dataJSON = responseJSON.getJSONObject("distribution");
                Log.d("dataTest",responseJSON.toString());

                final int dataCount = dataJSON.length();
                intKey = new String[dataCount];
                intValue = new int[dataCount];
                DATA_COUNT=intValue.length;

                Log.d("dataCount", Integer.toString(dataCount));

                //尋訪JSONObject
                Iterator<String> stringIterator = dataJSON.keys();
                for (int i=0;i<dataCount;i++){
                    //取得key
                    String key = stringIterator.next().toString();
                    //Log.d("get key", key);
                    intKey[i] = key;
                    String value = dataJSON.getString(key);
                    intValue[i] = Integer.parseInt(value);
                }

                //Log.d("key", Integer.toString(intKey[0]));
                //Log.d("Value", Integer.toString(intValue[0]));

            }catch (Exception e){
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Log.d("post test","in");

            //產生chart
            barChart = (BarChart) getView().findViewById(R.id.chart_1);

            //寫入data
            ArrayList<BarEntry> barEntries = new ArrayList<>();
            final ArrayList<String> KeyLabel = new ArrayList<>();
            for(int i=0; i<DATA_COUNT; i++){
                barEntries.add(new BarEntry(i, (float) intValue[i]));

            }

            //設定barchart
            BarDataSet barDataSet = new BarDataSet(barEntries,"Dates");
            BarData theData = new BarData(barDataSet);
            barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(intKey)); //設定Label
            barChart.setData(theData);

            barChart.setTouchEnabled(true);
            barChart.setDragEnabled(true);
            barChart.setScaleEnabled(true);

        }



    }




}
