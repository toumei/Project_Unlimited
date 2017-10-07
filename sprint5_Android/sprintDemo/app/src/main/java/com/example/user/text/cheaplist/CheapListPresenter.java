package com.example.user.text.cheaplist;

import android.widget.ListAdapter;

/**
 * Created by toy9986619 on 2017/10/7.
 */

public class CheapListPresenter {

    private int DATA_COUNT=0;
    private CheapListView CLView;
    private CheapListModel[] models;
    //private ListAdapter listAdapter;


    public CheapListPresenter(CheapListView view, String access_token){
        //this.CLModel = model;
        this.CLView = view;

        try {
            models = new CheapListInteractor(this, access_token).execute().get();
            DATA_COUNT=models.length;
        }catch (Exception e){
            e.printStackTrace();
        }


        //listAdapter = model.getListAdapter();

    }

    public void updateAdapter(ListAdapter listAdapter){

        CLView.setAdapter(listAdapter);

    }



}
