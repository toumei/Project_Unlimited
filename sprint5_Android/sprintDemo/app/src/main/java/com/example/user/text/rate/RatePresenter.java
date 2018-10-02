package com.example.user.text.rate;

import com.github.mikephil.charting.data.LineData;

/**
 * Created by ASUS on 2017/10/13.
 */

public class RatePresenter {
    RateView RV;
    String access_token;
    LineData lineData;

    public RatePresenter(RateView view, String access_token){
        this.RV = view;
        this.access_token = access_token;

        try{
            lineData = new RateModel(access_token,this).execute().get();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void updateRate(){
        RV.updateRate(lineData);
    }
}
