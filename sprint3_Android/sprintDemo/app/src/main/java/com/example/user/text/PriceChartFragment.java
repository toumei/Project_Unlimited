package com.example.user.text;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by user on 2017/8/2.
 */

public class PriceChartFragment extends Fragment {
    BarChart barChart;
    public static final String ACCESS_TOKEN = "access_token";
    private String access_token;
    private String chartAPI = "http://163.13.127.98:8088/api/price_data";

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

        View v = inflater.inflate(R.layout.pricechartfrag,container,false);
        //產生chart
        barChart = (BarChart) v.findViewById(R.id.chart_1);


        ArrayList<BarEntry> barEntries = new ArrayList<>();
        /*
        barEntries.add(new BarEntry(0, 44f));
        barEntries.add(new BarEntry(1, 100f));
        barEntries.add(new BarEntry(2, 55f));
        barEntries.add(new BarEntry(3, 22f));
        barEntries.add(new BarEntry(4, 36f));
        */
        BarDataSet barDataSet = new BarDataSet(barEntries,"Dates");


        /*ArrayList<String> theDates = new ArrayList<>();
        theDates.add("April");
        theDates.add("May");
        theDates.add("June");
        theDates.add("July");
        theDates.add("August");*/

        BarData theData = new BarData(barDataSet);
        barChart.setData(theData);

        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);


        return v;
    }

    /*
    //pricechart api
    class ChartTask extends AsyncTask<String,Void, Void> {

        int[] intKey;
        int[] intValue;
        int unit = 100;
        @Override
        protected Void doInBackground(String... params) {
            try {
                //傳送資料
                ArrayList<NameValuePair> sendlist = new ArrayList<NameValuePair>();
                sendlist.add(new BasicNameValuePair("kind",record));
                sendlist.add(new BasicNameValuePair("unit",unit+""));
                JSONObject responseJSON = new JSONObject(API.CallAPI("GET",params[0],sendlist, access_token));
                Log.e("responseTest",responseJSON.toString());
                //JSONObject dataJSON = responseJSON.getJSONObject("distribution");
                Log.e("dataTest",responseJSON.toString());
                final int dataCount = responseJSON.length();
                intKey = new int[dataCount];
                intValue = new int[dataCount];

                //尋訪JSONObject
                Iterator<String> stringIterator = responseJSON.keys();
                for (int i=0;i<dataCount;i++){
                    //取得key
                    String key = stringIterator.next().toString();
                    intKey[i] = Integer.parseInt(key);
                    String value = responseJSON.getString(key);
                    intValue[i] = Integer.parseInt(value);
                }

            }catch (Exception e){
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }


            return null;
        }


        @Override
        protected void onPostExecute() {
            super.onPostExecute();
        }


    }*/

    public static PriceChartFragment newInstance(String access_token)
    {
        Bundle bundle = new Bundle();
        bundle.putString(ACCESS_TOKEN, access_token);
        PriceChartFragment priceChartFragment = new PriceChartFragment();
        priceChartFragment.setArguments(bundle);
        return priceChartFragment;
    }
}
