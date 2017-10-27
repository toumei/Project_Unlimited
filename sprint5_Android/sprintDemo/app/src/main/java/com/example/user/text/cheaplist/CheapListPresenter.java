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
    private String access_token;
    private String record;

    public CheapListPresenter(CheapListView view, String access_token){
        //this.CLModel = model;
        this.CLView = view;
        this.access_token = access_token;
        //initPresenter();
        //call api & get data

        //listAdapter = model.getListAdapter();

    }

    public void callAPI(){

        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    models = new CheapListInteractor(CheapListPresenter.this, access_token).execute().get();
                    DATA_COUNT=models.length;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        thread.start();

    }

    public void callAPI(final String query){

        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    models = new CheapListInteractor(CheapListPresenter.this, access_token, query).execute().get();
                    DATA_COUNT=models.length;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        thread.start();

    }

    //更新View的Adapter
    public void updateAdapter(ListAdapter listAdapter){
        CLView.setAdapter(listAdapter);
    }

    public void initProgressDialog(){
        CLView.initProgressDialog();
    }

    public void showProgressDialog(){
        CLView.showProgressDialog();
    }

    public void setProgressDialogMessage(int now, int DATA_COUNT){
        CLView.setProgressDialogMessage(now, DATA_COUNT);
    }

    public void dismissProgressDialog(){
        CLView.dismissProgressDialog();
    }

    public void setKeyword(String query){
        //this.record = query;
        this.callAPI(query);
    }


}
