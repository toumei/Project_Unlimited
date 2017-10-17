package com.example.user.text.cheaplist;

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

    public static final String ACCESS_TOKEN = "access_token";
    private String access_token;

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

        Log.d("access_token", access_token);



    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstenceState){
        CheapListPresenter CLPresenter = new CheapListPresenter(this, access_token);
        View v = inflater.inflate(R.layout.cheaplistfrag,container,false);

        listView = (ListView)v.findViewById(R.id.listview4);
        //searchView = (SearchView) v.findViewById(R.id.menuSearch);

        return v;
    }


    //設定listview的Adapter
    @Override
    public void setAdapter(ListAdapter listAdapter) {
        listView.setAdapter(listAdapter);
    }
}


