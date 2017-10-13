package com.example.user.text.rate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.text.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;

/**
 * Created by user on 2017/8/2.
 */

public class RateFragment extends Fragment  implements RateView{

    LineChart chart;
    public static final String ACCESS_TOKEN = "access_token";
    private String access_token;
    private String rateAPI="http://163.13.127.98:8088/api/v1.1/get_rmb_rate";


    //創造工廠
    public static RateFragment newInstance(String access_token)
    {
        Bundle bundle = new Bundle();
        bundle.putString(ACCESS_TOKEN, access_token);
        RateFragment rateFragment = new RateFragment();
        rateFragment.setArguments(bundle);
        return rateFragment;
    }//X

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

        RatePresenter RP = new RatePresenter(this, access_token);


    }//取得token X


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstenceState){

        // in this example, a LineChart is initialized from xml
        chart = (LineChart) getView().findViewById(R.id.Ratechart);


        return inflater.inflate(R.layout.ratefrag,container,false);
    }

    public void updateRate(LineData lineData){
        chart.setData(lineData);
        chart.invalidate(); // refresh
    }

}
