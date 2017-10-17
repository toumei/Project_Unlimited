package com.example.user.text.cheaplist;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.user.text.R;

/**
 * Created by user on 2017/8/2.;
 * Created by user on 2017/8/2.;
 * Created by user on 2017/8/2.;
 */

public class CheapListFragment extends Fragment implements CheapListView{
    private ListView listView;
    private SearchView searchView;
    private ListAdapter listAdapter;

    public static final String ACCESS_TOKEN = "access_token";
    private String access_token;
    ProgressDialog pd;
    CheapListPresenter CLPresenter;

    //創造工廠
    public static CheapListFragment newInstance(String access_token)
    {
        Bundle bundle = new Bundle();
        bundle.putString(ACCESS_TOKEN, access_token);
        CheapListFragment cheapListFragment = new CheapListFragment();
        cheapListFragment.setArguments(bundle);
        return cheapListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get access token
        Bundle bundle = getArguments();
        Log.d("CheapAccessTest", bundle.toString());
        if (bundle != null)
            access_token = bundle.getString(ACCESS_TOKEN);
        else{
            Log.d("test", "fail");
        }

        if(CLPresenter == null){
            CLPresenter = new CheapListPresenter(this, access_token);
        }

        Log.d("access_token", access_token);
        pd = initProgressDialog();

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstenceState){

        View v = inflater.inflate(R.layout.cheaplistfrag,container,false);
        listView = (ListView)v.findViewById(R.id.listview4);
        if(listAdapter!=null){
            //CLPresenter.initPresenter();
            listView.setAdapter(listAdapter);
        }

        //searchView = (SearchView) v.findViewById(R.id.menuSearch);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(listAdapter == null){
            CLPresenter.initPresenter();
            listView.setAdapter(listAdapter);
        }
    }

    //設定listview的Adapter
    @Override
    public void setAdapter(final ListAdapter listAdapter) {
        this.listAdapter = listAdapter;
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                listView.setAdapter(listAdapter);
            }
        });
        //listView.setAdapter(listAdapter);
    }

    public ProgressDialog initProgressDialog(){
        ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setTitle("載入中");
        pd.setMessage("請稍候...");
        //    設置setCancelable(false); 表示我們不能取消這個彈出框，等下載完成之後再讓彈出框消失
        pd.setCancelable(false);
        //    設置ProgressDialog樣式為圓圈的形式
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return pd;
    }

    public void showProgressDialog(){
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                pd.show();
            }
        });
    }

    public void setProgressDialogMessage(int now, int DATA_COUNT){
        pd.setMessage(" " + now + "/" + DATA_COUNT);
    }

    public void dismissProgressDialog(){
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                pd.dismiss();
            }
        });
    }
}


