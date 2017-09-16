package com.example.user.text;

import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2017/8/2.
 */

public class Fragment2 extends Fragment {


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstenceState){

        View v = inflater.inflate(R.layout.frag2,container,false);



        return v;
    }


}

