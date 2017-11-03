package com.example.user.text.PriceChart;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.example.user.text.API;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by user on 2017/10/27.
 */

public class PriceChartInteractor extends AsyncTask<String, Void, PriceChartModel>{
    BarChart barChart;
    PriceChartModel PM;
    //public static final String ACCESS_TOKEN = "access_token";
    private PriceChartPresenter presenter;
    private String access_token;
    private String chartAPI = "http://163.13.127.98:8088/api/v1.2/price_data";
    private String record = "衛生紙";
    String[] intKey;
    int[] intValue;
    //String[] strData;
    int unit = 100;
    private int DATA_COUNT=0;

    public PriceChartInteractor(PriceChartPresenter presenter, String access_token){
        this.presenter = presenter;
        this.access_token = access_token;

    }
    @Override
    protected PriceChartModel doInBackground(String... params) {
        try {
            PM = new PriceChartModel();
            //傳送資料
            ArrayList<NameValuePair> sendlist = new ArrayList<NameValuePair>();
            //參數
            sendlist.add(new BasicNameValuePair("search",record));
            //sendlist.add(new BasicNameValuePair("unit",unit+""));

            JSONObject responseJSON = new JSONObject(API.CallAPI("GET",chartAPI,sendlist, access_token));
            Log.d("responseTest",responseJSON.toString());

            JSONObject dataJSON = responseJSON.getJSONObject("distribution");
            Log.d("dataTest",dataJSON.toString());

            JSONObject data2JSON = responseJSON.getJSONObject("statistic");
            Log.d("data2Test",data2JSON.toString());

            //獲取資料筆數
            final int dataCount = dataJSON.length();
            intKey = new String[dataCount];
            PM.strData = new String[3];
            intValue = new int[dataCount];
            DATA_COUNT=intValue.length;

            Log.d("dataCount", Integer.toString(dataCount));

            //尋訪JSONObject
            Iterator<String> stringIterator = dataJSON.keys();
            for (int i=0;i<dataCount;i++){
                //取得key
                String key = stringIterator.next().toString();
                intKey[i] = key;
                //取得資料
                String value = dataJSON.getString(key);
                intValue[i] = Integer.parseInt(value);
            }
            PM.strData[0] = data2JSON.getString("avg");
            PM.strData[1] = data2JSON.getString("max");
            PM.strData[2] = data2JSON.getString("min");
            Log.d("strData0",PM.strData[0]);
            Log.d("strData1",PM.strData[1]);
            Log.d("strData2",PM.strData[2]);
        }catch (Exception e){
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(PriceChartModel PCmodel) {
        super.onPostExecute(PCmodel);
        //寫入data
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        //final ArrayList<String> KeyLabel = new ArrayList<>();
        for(int i=0; i<DATA_COUNT; i++){
            barEntries.add(new BarEntry(i, (float) intValue[i]));
        }
        //設定barchart
        BarDataSet barDataSet = new BarDataSet(barEntries,"Dates");
        PM.theData = new BarData(barDataSet);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(intKey)); //設定Label

        presenter.updatePriceData();

    }
}
