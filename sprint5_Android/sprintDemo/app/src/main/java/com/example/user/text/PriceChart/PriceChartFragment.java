package com.example.user.text.PriceChart;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.text.API;
import com.example.user.text.R;
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
 * Created by user on 2017/8/2.
 */

public class PriceChartFragment extends Fragment implements PriceChartView{
    BarChart barChart;
    public static final String ACCESS_TOKEN = "access_token";
    private String access_token;
    //private String chartAPI = "http://163.13.127.98:8088/api/v1.2/price_data";
    private String record = "衛生紙";
    private TextView ave;
    private TextView max;
    private TextView min;
    PriceChartView PV;
    PriceChartModel PM;

    //創造工廠
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

        PriceChartPresenter PC = new PriceChartPresenter(PV,access_token);

    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstenceState){

        //call api
        //new ChartTask().execute(chartAPI);

        View v = inflater.inflate(R.layout.pricechartfrag,container,false);
        //產生chart
        barChart = (BarChart)v.findViewById(R.id.Barchart);
        ave = (TextView)v.findViewById(R.id.aveprice);
        max = (TextView)v.findViewById(R.id.maxprice);
        min = (TextView)v.findViewById(R.id.minprice);

        return v;
    }



    public void updatePriceData(BarData theData, String[] strData){

        barChart.setData(theData);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.invalidate();

        ave.setText("平均價格："+strData[0]);
        max.setText("最高價格："+strData[1]);
        min.setText("最低價格："+strData[2]);

    }

}
