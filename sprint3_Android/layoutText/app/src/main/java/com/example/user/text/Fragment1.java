package com.example.user.text;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

/**
 * Created by user on 2017/8/2.
 */

public class Fragment1 extends Fragment {
    BarChart barChart;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstenceState){
        View v = inflater.inflate(R.layout.frag1,container,false);
        //產生chart
        barChart = (BarChart) v.findViewById(R.id.chart_1);
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, 44f));
        barEntries.add(new BarEntry(1, 100f));
        barEntries.add(new BarEntry(2, 55f));
        barEntries.add(new BarEntry(3, 22f));
        barEntries.add(new BarEntry(4, 36f));
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
}
