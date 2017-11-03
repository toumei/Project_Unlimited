package com.example.user.text.PriceChart;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;

/**
 * Created by user on 2017/10/20.
 */

public class PriceChartPresenter {
    PriceChartView PV;
    PriceChartModel PM;
    BarData theData;
    String[] strData;

    private String access_token;

    public PriceChartPresenter(PriceChartView view,String access_token){
        this.PV = view;
        this.access_token = access_token;

        try {
            PM = new PriceChartInteractor(PriceChartPresenter.this,access_token).execute().get();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updatePriceData(){
        PV.updatePriceData(theData,strData);
    }


}
